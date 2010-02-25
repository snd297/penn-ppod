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
package edu.upenn.cis.ppod.saveorupdate;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.dao.hibernate.ObjectWLongIdDAOHibernate;
import edu.upenn.cis.ppod.model.Character;
import edu.upenn.cis.ppod.model.CharacterStateCell;
import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.CharacterStateRow;
import edu.upenn.cis.ppod.model.DNACharacter;
import edu.upenn.cis.ppod.model.ICharacterStateMatrixFactory;
import edu.upenn.cis.ppod.model.ModelAssert;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.PPodVersionInfo;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;
import edu.upenn.cis.ppod.util.MatrixProvider;

/**
 * Tests of {@link ISaveOrUpdateMatrix}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST })
public class SaveOrUpdateCharacterStateMatrixTest {

	@Inject
	private ISaveOrUpdateMatrixFactory mergeMatrixFactory;

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<OTU> otuProvider;

	@Inject
	private ICharacterStateMatrixFactory matrixFactory;

	@Inject
	private Session session;

	@Inject
	private ObjectWLongIdDAOHibernate dao;

	@Inject
	private TestMergeAttachment mergeAttachment;

	@Inject
	private Provider<PPodVersionInfo> pPodVersionInfoProvider;

	@Inject
	private DNACharacter dnaCharacter;

	@Inject
	private INewPPodVersionInfo newPPodVersionInfo;

// @BeforeMethod
// public void beforeMethod() {
// final org.hibernate.classic.Session session = HibernateUtil
// .getSessionFactory().openSession();
// ManagedSessionContext.bind(session);
// this.session = session;
// }
//
// @AfterMethod
// public void afterMethod() {
// Session s = ManagedSessionContext.unbind(HibernateUtil
// .getSessionFactory());
// s.close();
// }

	@Test(dataProvider = MatrixProvider.SMALL_MATRICES_PROVIDER, dataProviderClass = MatrixProvider.class)
	public void save(final CharacterStateMatrix sourceMatrix) {

		final ISaveOrUpdateMatrix saveOrUpdateMatrix = mergeMatrixFactory
				.create(mergeAttachment, dao.setSession(session),
						newPPodVersionInfo);
		final OTUSet fakeDbOTUSet = sourceMatrix.getOTUSet();
		final Map<OTU, OTU> fakeOTUsByIncomingOTU = newHashMap();
		for (final OTU sourceOTU : sourceMatrix.getOTUs()) {
			fakeOTUsByIncomingOTU.put(sourceOTU, sourceOTU);
		}

		final CharacterStateMatrix targetMatrix = matrixFactory
				.create(sourceMatrix);

		final Set<CharacterStateMatrix> sourceAndTargetMatrices = newHashSet(sourceMatrix);
		sourceAndTargetMatrices.add(targetMatrix);

		fakeDbOTUSet.setMatrices(sourceAndTargetMatrices);
		saveOrUpdateMatrix.saveOrUpdate(targetMatrix, sourceMatrix,
				fakeOTUsByIncomingOTU, dnaCharacter);
		ModelAssert.assertEqualsCharacterStateMatrices(targetMatrix,
				sourceMatrix);
	}

	@Test(dataProvider = MatrixProvider.SMALL_MATRICES_PROVIDER, dataProviderClass = MatrixProvider.class)
	public void moveRows(final CharacterStateMatrix sourceMatrix) {
		final ISaveOrUpdateMatrix saveOrUpdateMatrix = mergeMatrixFactory
				.create(mergeAttachment, dao.setSession(session),
						newPPodVersionInfo);
		final OTUSet fakeTargetOTUSet = sourceMatrix.getOTUSet();
		final Map<OTU, OTU> fakeOTUsByIncomingOTU = newHashMap();
		for (final OTU sourceOTU : sourceMatrix.getOTUs()) {
			fakeOTUsByIncomingOTU.put(sourceOTU, sourceOTU);
		}

		final CharacterStateMatrix targetMatrix = matrixFactory
				.create(sourceMatrix);

		final Set<CharacterStateMatrix> sourceAndTargetMatrices = newHashSet(sourceMatrix);
		sourceAndTargetMatrices.add(targetMatrix);

		fakeTargetOTUSet.setMatrices(sourceAndTargetMatrices);
		saveOrUpdateMatrix.saveOrUpdate(targetMatrix, sourceMatrix,
				fakeOTUsByIncomingOTU, dnaCharacter);

		// Simulate passing back in the persisted characters: so we need to
		// assign the proper pPOD ID's.
		for (int i = 0; i < sourceMatrix.getCharacters().size(); i++) {
			sourceMatrix.getCharacters().get(i).setPPodId(
					targetMatrix.getCharacters().get(i).getPPodId());
		}

		final List<OTU> shuffledSourceOTUs = newArrayList(sourceMatrix
				.getOTUs());
		Collections.shuffle(shuffledSourceOTUs);

		sourceMatrix.setOTUs(shuffledSourceOTUs);

		for (final CharacterStateRow targetRow : targetMatrix.getRows()) {
			targetRow.setPPodVersion(1L);
		}
		for (final CharacterStateRow sourceRow : sourceMatrix.getRows()) {
			sourceRow.setPPodVersion(1L);
		}
		saveOrUpdateMatrix.saveOrUpdate(targetMatrix, sourceMatrix,
				fakeOTUsByIncomingOTU, dnaCharacter);
		ModelAssert.assertEqualsCharacterStateMatrices(targetMatrix,
				sourceMatrix);
	}

	@Test(dataProvider = MatrixProvider.SMALL_MATRICES_PROVIDER, dataProviderClass = MatrixProvider.class)
	public void moveCharacters(final CharacterStateMatrix sourceMatrix) {
		// It only makes sense to move characters in a standard matrix
		if (sourceMatrix.getClass().equals(CharacterStateMatrix.class)) {
			final ISaveOrUpdateMatrix saveOrUpdateMatrix = mergeMatrixFactory
					.create(mergeAttachment, dao.setSession(session),
							newPPodVersionInfo);
			final OTUSet fakeTargetOTUSet = sourceMatrix.getOTUSet();
			final Map<OTU, OTU> fakeOTUsByIncomingOTU = newHashMap();
			for (final OTU sourceOTU : sourceMatrix.getOTUs()) {
				fakeOTUsByIncomingOTU.put(sourceOTU, sourceOTU);
			}

			final CharacterStateMatrix targetMatrix = matrixFactory
					.create(sourceMatrix);
			final Set<CharacterStateMatrix> sourceAndTargetMatrices = newHashSet(sourceMatrix);
			sourceAndTargetMatrices.add(targetMatrix);

			fakeTargetOTUSet.setMatrices(sourceAndTargetMatrices);
			saveOrUpdateMatrix.saveOrUpdate(targetMatrix, sourceMatrix,
					fakeOTUsByIncomingOTU, dnaCharacter);

			// Simulate passing back in the persisted characters: so we need to
			// assign the proper pPOD ID's.
			for (int i = 0; i < sourceMatrix.getCharacters().size(); i++) {
				sourceMatrix.getCharacters().get(i).setPPodId(
						targetMatrix.getCharacters().get(i).getPPodId());
			}

			// Swap 2 and 0
			final List<Character> newSourceMatrixCharacters = newArrayList(sourceMatrix
					.getCharacters());

			newSourceMatrixCharacters.set(0, sourceMatrix.getCharacters()
					.get(2));
			newSourceMatrixCharacters.set(2, sourceMatrix.getCharacters()
					.get(0));
			sourceMatrix.setCharacters(newSourceMatrixCharacters);

			for (final CharacterStateRow sourceRow : sourceMatrix.getRows()) {
				final List<CharacterStateCell> newSourceCells = newArrayList(sourceRow
						.getCells());
				newSourceCells.set(0, sourceRow.getCells().get(2));
				newSourceCells.set(2, sourceRow.getCells().get(0));
				sourceRow.setCells(newSourceCells);
			}
			saveOrUpdateMatrix.saveOrUpdate(targetMatrix, sourceMatrix,
					fakeOTUsByIncomingOTU, dnaCharacter);

			ModelAssert.assertEqualsCharacterStateMatrices(targetMatrix,
					sourceMatrix);
		}
	}
}
