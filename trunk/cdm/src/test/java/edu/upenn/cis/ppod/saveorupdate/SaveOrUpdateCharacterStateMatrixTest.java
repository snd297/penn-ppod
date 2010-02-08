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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.dao.hibernate.ObjectWLongIdDAOHibernate;
import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.CharacterStateRow;
import edu.upenn.cis.ppod.model.DNACharacter;
import edu.upenn.cis.ppod.model.ICharacterStateMatrixFactory;
import edu.upenn.cis.ppod.model.ModelAssert;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.PPodVersionInfo;
import edu.upenn.cis.ppod.util.MatrixProvider;

/**
 * Tests of {@link ISaveOrUpdateCharacterStateMatrix}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST })
public class SaveOrUpdateCharacterStateMatrixTest {

	@Inject
	private ISaveOrUpdateCharacterStateMatrix.IFactory mergeMatrixFactory;

	@Inject
	private ICharacterStateMatrixFactory characterStateMatrixFactory;

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<OTU> otuProvider;

	@Inject
	private Provider<DNACharacter> dnaCharacterProvider;

	@Inject
	private Session session;

	@Inject
	private ObjectWLongIdDAOHibernate dao;

	@Inject
	private TestMergeAttachment mergeAttachment;

	@Inject
	private Provider<PPodVersionInfo> pPodVersionInfoProvider;

	@Test(dataProvider = MatrixProvider.SMALL_MATRICES_PROVIDER, dataProviderClass = MatrixProvider.class)
	public void save(final CharacterStateMatrix sourceMatrix) {

		final ISaveOrUpdateCharacterStateMatrix saveOrUpdateCharacterStateMatrix = mergeMatrixFactory
				.create(mergeAttachment, dao.setSession(session));
		final OTUSet fakeDbOTUSet = sourceMatrix.getOTUSet();
		final Map<OTU, OTU> fakeOTUsByIncomingOTU = newHashMap();
		for (final OTU sourceOTU : sourceMatrix.getOTUs()) {
			fakeOTUsByIncomingOTU.put(sourceOTU, sourceOTU);
		}

		final CharacterStateMatrix targetMatrix = characterStateMatrixFactory
				.create(sourceMatrix.getType());
		final CharacterStateMatrix dbMatrix = saveOrUpdateCharacterStateMatrix
				.saveOrUpdate(targetMatrix, sourceMatrix, fakeDbOTUSet,
						fakeOTUsByIncomingOTU, dnaCharacterProvider.get());
		ModelAssert.assertEqualsCharacterStateMatrices(dbMatrix, sourceMatrix);
	}

	@Test(dataProvider = MatrixProvider.SMALL_MATRICES_PROVIDER, dataProviderClass = MatrixProvider.class)
	public void moveRows(final CharacterStateMatrix sourceMatrix) {
		final ISaveOrUpdateCharacterStateMatrix saveOrUpdateCharacterStateMatrix = mergeMatrixFactory
				.create(mergeAttachment, dao.setSession(session));
		final OTUSet fakeTargetOTUSet = sourceMatrix.getOTUSet();
		final Map<OTU, OTU> fakeOTUsByIncomingOTU = newHashMap();
		for (final OTU sourceOTU : sourceMatrix.getOTUs()) {
			fakeOTUsByIncomingOTU.put(sourceOTU, sourceOTU);
		}

		final CharacterStateMatrix targetMatrix = characterStateMatrixFactory
				.create(sourceMatrix.getType());

		saveOrUpdateCharacterStateMatrix.saveOrUpdate(targetMatrix,
				sourceMatrix, fakeTargetOTUSet, fakeOTUsByIncomingOTU,
				dnaCharacterProvider.get());
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
		saveOrUpdateCharacterStateMatrix.saveOrUpdate(targetMatrix,
				sourceMatrix, fakeTargetOTUSet, fakeOTUsByIncomingOTU,
				dnaCharacterProvider.get());
		ModelAssert.assertEqualsCharacterStateMatrices(targetMatrix,
				sourceMatrix);
	}

	@Test(dataProvider = MatrixProvider.SMALL_MATRICES_PROVIDER, dataProviderClass = MatrixProvider.class, groups = TestGroupDefs.IN_DEVELOPMENT)
	public void moveCharacters(final CharacterStateMatrix sourceMatrix) {
		final ISaveOrUpdateCharacterStateMatrix saveOrUpdateCharacterStateMatrix = mergeMatrixFactory
				.create(mergeAttachment, dao.setSession(session));
		final OTUSet fakeTargetOTUSet = sourceMatrix.getOTUSet();
		final Map<OTU, OTU> fakeOTUsByIncomingOTU = newHashMap();
		for (final OTU sourceOTU : sourceMatrix.getOTUs()) {
			fakeOTUsByIncomingOTU.put(sourceOTU, sourceOTU);
		}

		final CharacterStateMatrix targetMatrix = characterStateMatrixFactory
				.create(sourceMatrix.getType());

		saveOrUpdateCharacterStateMatrix.saveOrUpdate(targetMatrix,
				sourceMatrix, fakeTargetOTUSet, fakeOTUsByIncomingOTU,
				dnaCharacterProvider.get());

		// Swap 2 and 0
		sourceMatrix.setCharacter(0, sourceMatrix.setCharacter(2, sourceMatrix
				.getCharacters().get(0)));

		saveOrUpdateCharacterStateMatrix.saveOrUpdate(targetMatrix,
				sourceMatrix, fakeTargetOTUSet, fakeOTUsByIncomingOTU,
				dnaCharacterProvider.get());

		ModelAssert.assertEqualsCharacterStateMatrices(targetMatrix,
				sourceMatrix);
	}
}
