/*
 * Copyright (C) 2009 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS of ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.services.ppodentity;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Iterables.find;

import java.util.Iterator;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.model.Character;
import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.CharacterStateRow;
import edu.upenn.cis.ppod.model.DNASequence;
import edu.upenn.cis.ppod.model.DNASequenceSet;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.PPodVersionInfo;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.model.Tree;
import edu.upenn.cis.ppod.model.TreeSet;
import edu.upenn.cis.ppod.modelinterfaces.IWithPPodId;

/**
 * @author Sam Donnelly
 */
final class Study2StudyInfo implements IStudy2StudyInfo {

	private final Provider<MolecularSequenceSetInfo> molecularSequenceSetInfoProvider;
	private final Provider<TreeSetInfo> treeSetInfoProvider;
	private final Provider<PPodEntityInfo> pPodEntityInfoProvider;
	private final Provider<PPodEntityInfoWDocId> pPodEntityInfoWDocIdProvider;

	@Inject
	Study2StudyInfo(
			final Provider<OTUSetInfo> otuSetPPodIdAndVersionProvider,
			final Provider<CharacterStateMatrixInfo> matrixPPodIdAndVersionProvider,
			final Provider<MolecularSequenceSetInfo> molecularSequenceSetInfoProvider,
			final Provider<TreeSetInfo> treeSetInfoProvider,
			final Provider<PPodEntityInfo> pPodEntityInfoProvider,
			final Provider<PPodEntityInfoWDocId> pPodEntityInfoWDocIdProvider) {
		this.molecularSequenceSetInfoProvider = molecularSequenceSetInfoProvider;
		this.treeSetInfoProvider = treeSetInfoProvider;
		this.pPodEntityInfoProvider = pPodEntityInfoProvider;
		this.pPodEntityInfoWDocIdProvider = pPodEntityInfoWDocIdProvider;
	}

	public StudyInfo toStudyInfo(final Study study, final StudyInfo studyInfo) {
		checkNotNull(study);
		studyInfo.setEntityId(study.getId());
		studyInfo.setPPodId(study.getPPodId());
		studyInfo.setPPodVersion(study.getPPodVersionInfo().getPPodVersion());

		for (final Iterator<OTUSet> otuSetsItr = study.getOTUSetsIterator(); otuSetsItr
				.hasNext();) {
			final OTUSet otuSet = otuSetsItr.next();
			final OTUSetInfo otuSetInfo = find(studyInfo.getOTUSetInfos(),
					compose(equalTo(otuSet.getPPodId()),
							IWithPPodId.getPPodId));

			studyInfo.getOTUSetInfos().add(otuSetInfo);
			otuSetInfo.setEntityId(otuSet.getId());
			otuSetInfo.setPPodId(otuSet.getPPodId());
			otuSetInfo.setPPodVersion(otuSet.getPPodVersionInfo()
					.getPPodVersion());
			otuSetInfo.setDocId(otuSet.getDocId());
			for (final OTU otu : otuSet) {
				final PPodEntityInfoWDocId otuInfo = pPodEntityInfoWDocIdProvider
						.get();
				otuSetInfo.getOTUInfos().add(otuInfo);
				otuInfo.setEntityId(otu.getId());
				otuInfo.setPPodId(otu.getPPodId());
				otuInfo.setPPodVersion(otu.getPPodVersionInfo()
						.getPPodVersion());
				otuInfo.setDocId(otu.getDocId());
			}

			for (final Iterator<CharacterStateMatrix> matrixItr = otuSet
					.characterStateMatricesIterator(); matrixItr.hasNext();) {
				final CharacterStateMatrix matrix = matrixItr.next();
				final CharacterStateMatrixInfo matrixInfo =
						find(otuSetInfo.getMatrixInfos(),
								compose(equalTo(matrix.getPPodId()),
										IWithPPodId.getPPodId));
				matrixInfo.setEntityId(matrix.getId());
				matrixInfo.setPPodVersion(matrix.getPPodVersionInfo()
						.getPPodVersion());
				matrixInfo.setDocId(matrix.getDocId());

				int characterIdx = -1;
				for (final Iterator<Character> charactersItr = matrix
						.getCharactersIterator(); charactersItr.hasNext();) {
					characterIdx++;
					final Character character = charactersItr.next();
					PPodEntityInfo characterInfo = pPodEntityInfoProvider.get();
					characterInfo.setPPodId(character.getPPodId());
					characterInfo.setEntityId(character.getId());
					characterInfo.setPPodVersion(character.getPPodVersionInfo()
							.getPPodVersion());
					matrixInfo.getCharacterInfosByIdx().put(characterIdx,
							characterInfo);
				}

				int columnIdx = -1;
				for (final Iterator<PPodVersionInfo> columnPPodVersionInfosItr = matrix
						.getColumnPPodVersionInfosIterator(); columnPPodVersionInfosItr
						.hasNext();) {
					columnIdx++;
					final PPodVersionInfo columnPPodVersionInfo = columnPPodVersionInfosItr
							.next();
					matrixInfo.getColumnHeaderVersionsByIdx().put(columnIdx,
							columnPPodVersionInfo.getPPodVersion());
				}

				int rowIdx = -1;
				for (final CharacterStateRow row : matrix) {
					rowIdx++;
					final Long rowPPodVersion = row.getPPodVersionInfo()
							.getPPodVersion();
					matrixInfo.getRowHeaderVersionsByIdx().put(rowIdx,
							rowPPodVersion);

					// This bit is not handled in
					// SaveOrUpdateCharacterStateMatrix

					// int cellIdx = -1;
					// for (final CharacterStateCell cell : row) {
					// cellIdx++;
					// matrixInfo.setCellPPodIdAndVersion(rowIdx, cellIdx,
					// cell.getPPodVersionInfo().getPPodVersion());
					// }
				}
			}
			// TODO: this should be genericized when we support other kinds of
			// MolecularSequenceSets
			for (final Iterator<DNASequenceSet> dnaSequenceSetItr = otuSet
					.dnaSequenceSetsIterator(); dnaSequenceSetItr.hasNext();) {
				final DNASequenceSet dnaSequenceSet = dnaSequenceSetItr.next();
				final MolecularSequenceSetInfo sequenceSetInfo = molecularSequenceSetInfoProvider
						.get();
				otuSetInfo.getSequenceSetInfos().add(sequenceSetInfo);
				sequenceSetInfo.setPPodId(dnaSequenceSet.getPPodId());
				sequenceSetInfo.setPPodVersion(dnaSequenceSet
						.getPPodVersionInfo().getPPodVersion());
				sequenceSetInfo.setEntityId(dnaSequenceSet.getId());
				for (final OTU otu : otuSet) {
					final DNASequence dnaSequence = dnaSequenceSet
							.getSequence(otu);
					sequenceSetInfo.getSequenceVersionsByOTUDocId().put(
							otu.getDocId(),
							dnaSequence.getPPodVersionInfo().getPPodVersion());
				}
			}

			for (final Iterator<TreeSet> treeSetItr = otuSet
					.treeSetsIterator(); treeSetItr.hasNext();) {
				final TreeSet treeSet = treeSetItr.next();
				final TreeSetInfo treeSetInfo = treeSetInfoProvider.get();
				otuSetInfo.getTreeSetInfos().add(treeSetInfo);
				treeSetInfo.setEntityId(treeSet.getId());
				treeSetInfo.setPPodId(treeSet.getPPodId());
				treeSetInfo.setPPodVersion(treeSet.getPPodVersionInfo()
						.getPPodVersion());
				treeSetInfo.setDocId(treeSet.getDocId());

				for (final Tree tree : treeSet) {
					final PPodEntityInfo treeInfo = pPodEntityInfoProvider
							.get();
					treeSetInfo.getTreeInfos().add(treeInfo);
					treeInfo.setEntityId(tree.getId());
					treeInfo.setPPodId(tree.getPPodId());
					treeInfo.setPPodVersion(tree.getPPodVersionInfo()
							.getPPodVersion());
				}
			}
		}
		return studyInfo;
	}
}
