package edu.upenn.cis.ppod.saveorupdate.hibernate;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static edu.upenn.cis.ppod.util.PPodIterables.findEach;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.model.Attachment;
import edu.upenn.cis.ppod.model.Character;
import edu.upenn.cis.ppod.model.CharacterState;
import edu.upenn.cis.ppod.model.CharacterStateCell;
import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.CharacterStateRow;
import edu.upenn.cis.ppod.model.DNACharacter;
import edu.upenn.cis.ppod.model.DNAStateMatrix;
import edu.upenn.cis.ppod.model.MolecularCharacter;
import edu.upenn.cis.ppod.model.MolecularState;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.saveorupdate.IMergeAttachment;
import edu.upenn.cis.ppod.thirdparty.injectslf4j.InjectLogger;

/**
 * @author Sam Donnelly
 */
public class SaveCharacterStateMatrixHibernate {

	private final Provider<CharacterStateMatrix> standardMatrixProvider;
	private final Provider<DNAStateMatrix> dnaMatrixProvider;
	private final Provider<Character> characterProvider;
	private final Provider<CharacterStateRow> rowProvider;
	private final Provider<CharacterStateCell> cellProvider;
	private final CharacterState.IFactory stateFactory;
	private final Provider<Attachment> attachmentProvider;
	private final IMergeAttachment mergeAttachment;
	private final Session session;

	@InjectLogger
	private Logger logger;

	@Inject
	SaveCharacterStateMatrixHibernate(
			final Provider<CharacterStateMatrix> standardMatrixProvider,
			final Provider<DNAStateMatrix> dnaMatrixProvider,
			final Provider<Character> characterProvider,
			final Provider<CharacterStateRow> rowProvider,
			final Provider<CharacterStateCell> cellProvider,
			final CharacterState.IFactory stateFactory,
			final Provider<Attachment> attachmentProvider,
			@Assisted final IMergeAttachment mergeAttachment,
			@Assisted final Session session) {
		this.standardMatrixProvider = standardMatrixProvider;
		this.dnaMatrixProvider = dnaMatrixProvider;
		this.characterProvider = characterProvider;
		this.rowProvider = rowProvider;
		this.cellProvider = cellProvider;
		this.stateFactory = stateFactory;
		this.attachmentProvider = attachmentProvider;
		this.mergeAttachment = mergeAttachment;
		this.session = session;
	}

	public CharacterStateMatrix merge(final CharacterStateMatrix sourceMatrix,
			final OTUSet newTargetMatrixOTUSet,
			final Map<OTU, OTU> mergedOTUsBySourceOTU,
			final DNACharacter dnaCharacter, boolean save) {
		final String METHOD = "merge(...)";
		logger.debug("{}: entering", METHOD);
		checkNotNull(sourceMatrix);
		checkNotNull(newTargetMatrixOTUSet);

		CharacterStateMatrix targetMatrix;
		if (sourceMatrix.getType() == CharacterStateMatrix.Type.STANDARD) {
			targetMatrix = standardMatrixProvider.get();
		} else {
			targetMatrix = dnaMatrixProvider.get();
		}

		newTargetMatrixOTUSet.addMatrix(targetMatrix);

		targetMatrix.setLabel(sourceMatrix.getLabel());
		targetMatrix.setDescription(sourceMatrix.getDescription());

		// We need this for the response: it's less than ideal to do this here,
		// but easy
		targetMatrix.setDocId(sourceMatrix.getDocId());

		final List<OTU> newTargetOTUs = newArrayList();
		for (final OTU sourceOTU : sourceMatrix.getOTUs()) {
			final OTU newTargetOTU = mergedOTUsBySourceOTU.get(sourceOTU);
			if (newTargetOTU == null) {
				throw new AssertionError(
						"couldn't find incomingOTU in persistentOTUsByIncomingOTU");
			}
			newTargetOTUs.add(newTargetOTU);
		}
		targetMatrix.setOTUs(newTargetOTUs);

		for (final Character sourceCharacter : sourceMatrix.getCharacters()) {
			Character newTargetCharacter;
			if (sourceMatrix.getType() == CharacterStateMatrix.Type.DNA) { 
				newTargetCharacter = dnaCharacter;
			} else {
				newTargetCharacter = characterProvider.get();
				newTargetCharacter.setPPodId();
			}
			targetMatrix.addCharacter(newTargetCharacter);
			if (!(newTargetCharacter instanceof MolecularCharacter)) {
				newTargetCharacter.setLabel(sourceCharacter.getLabel());
			}

			for (final CharacterState sourceState : sourceCharacter.getStates()
					.values()) {
				CharacterState targetState;
				if (null == (targetState = newTargetCharacter.getStates().get(
						sourceState.getStateNumber()))) {
					targetState = newTargetCharacter.addState(stateFactory
							.create(sourceState.getStateNumber()));
				}
				if (!(targetState instanceof MolecularState)) {
					targetState.setLabel(sourceState.getLabel());
				}
			}

			for (final Attachment sourceAttachment : sourceCharacter
					.getAttachments()) {
				final Set<Attachment> targetAttachments = findEach(
						newTargetCharacter.getAttachments(), compose(
								equalTo(sourceAttachment.getStringValue()),
								Attachment.getStringValue));

				Attachment targetAttachment = getOnlyElement(targetAttachments,
						null);
				if (targetAttachment == null) {
					targetAttachment = attachmentProvider.get();
					targetAttachment.setPPodId();
				}
				newTargetCharacter.addAttachment(targetAttachment);
				mergeAttachment.merge(targetAttachment, sourceAttachment);
				if (save) {
					session.save(newTargetCharacter);
					session.save(targetAttachment);
				}
			}
		}

		int sourceRowIdx = 0;
		int cellCounter = -1;
		final List<CharacterStateRow> targetRows = newArrayList();

		for (@SuppressWarnings("unused")
		final OTU targetOTU : targetMatrix.getOTUs()) {
			final CharacterStateRow targetRow = rowProvider.get();
			targetRows.add(targetRow);

			targetRow.addCell(cellProvider.get());

			session.save(targetRow);

			final CharacterStateRow sourceRow = sourceMatrix
					.getRow(sourceMatrix.getOTUs().get(sourceRowIdx++));
			for (final ListIterator<CharacterStateCell> sourceCellItr = sourceRow
					.getCells().listIterator(), targetCellItr = targetRow
					.getCells().listIterator(); sourceCellItr.hasNext();) {
				final CharacterStateCell sourceCell = sourceCellItr.next();
				final CharacterStateCell targetCell = targetCellItr.next();
				final Set<CharacterState> newTargetStates = newHashSet();
				for (final CharacterState sourceState : sourceCell.getStates()) {
					newTargetStates.add(targetMatrix.getCharacters().get(
							targetCellItr.previousIndex()).getStates().get(
							sourceState.getStateNumber()));
				}
				switch (sourceCell.getType()) {
					case INAPPLICABLE:
						targetCell.setInapplicable();
						break;
					case POLYMORPHIC:
						targetCell.setPolymorphicStates(newTargetStates);
						break;
					case SINGLE:
						targetCell.setSingleState(get(newTargetStates, 0));
						break;
					case UNASSIGNED:
						targetCell.setUnassigned();
						break;
					case UNCERTAIN:
						targetCell.setUncertainStates(newTargetStates);
						break;
					default:
						throw new AssertionError("unknown type");
				}
				session.save(targetCell);
				if (save && cellCounter++ % 20 == 0) {
					logger.debug("{}: flushing rows, cellCounter: {}", METHOD,
							cellCounter);
					session.flush();
					session.clear();
				}
			}
		}
		for (int i = 0; i < targetRows.size(); i++) {
			targetMatrix.setRow(targetMatrix.getOTUs().get(i), targetRows
					.get(i));
		}
		return targetMatrix;
	}
}
