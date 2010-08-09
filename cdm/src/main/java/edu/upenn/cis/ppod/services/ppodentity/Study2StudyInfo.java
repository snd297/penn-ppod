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

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.imodel.IDNACell;
import edu.upenn.cis.ppod.imodel.IDNAMatrix;
import edu.upenn.cis.ppod.imodel.IDNARow;
import edu.upenn.cis.ppod.imodel.IDNASequenceSet;
import edu.upenn.cis.ppod.imodel.IOTU;
import edu.upenn.cis.ppod.imodel.IOTUSet;
import edu.upenn.cis.ppod.imodel.IStandardCharacter;
import edu.upenn.cis.ppod.imodel.IStandardMatrix;
import edu.upenn.cis.ppod.imodel.IStudy;
import edu.upenn.cis.ppod.imodel.ITree;
import edu.upenn.cis.ppod.imodel.ITreeSet;
import edu.upenn.cis.ppod.model.DNASequence;
import edu.upenn.cis.ppod.model.IStandardRow;
import edu.upenn.cis.ppod.model.StandardCell;
import edu.upenn.cis.ppod.model.VersionInfo;

/**
 * @author Sam Donnelly
 */
final class Study2StudyInfo implements IStudy2StudyInfo {

	private final Provider<MolecularSequenceSetInfo> molecularSequenceSetInfoProvider;
	private final Provider<TreeSetInfo> treeSetInfoProvider;
	private final Provider<PPodEntityInfo> pPodEntityInfoProvider;
	private final Provider<PPodEntityInfoWDocId> pPodEntityInfoWDocIdProvider;
	private final Provider<MatrixInfo> matrixInfoProvider;
	private final Provider<StudyInfo> studyInfoProvider;
	private final Provider<OTUSetInfo> otuSetInfoProvider;

	@Inject
	Study2StudyInfo(
			final Provider<OTUSetInfo> otuSetPPodIdAndVersionProvider,
			final Provider<MatrixInfo> matrixPPodIdAndVersionProvider,
			final Provider<MolecularSequenceSetInfo> molecularSequenceSetInfoProvider,
			final Provider<TreeSetInfo> treeSetInfoProvider,
			final Provider<PPodEntityInfo> pPodEntityInfoProvider,
			final Provider<PPodEntityInfoWDocId> pPodEntityInfoWDocIdProvider,
			final Provider<MatrixInfo> matrixInfoProvider,
			final Provider<StudyInfo> studyInfoProvider,
			final Provider<OTUSetInfo> otuSetInfoProvider) {
		this.molecularSequenceSetInfoProvider = molecularSequenceSetInfoProvider;
		this.treeSetInfoProvider = treeSetInfoProvider;
		this.pPodEntityInfoProvider = pPodEntityInfoProvider;
		this.pPodEntityInfoWDocIdProvider = pPodEntityInfoWDocIdProvider;
		this.matrixInfoProvider = matrixInfoProvider;
		this.studyInfoProvider = studyInfoProvider;
		this.otuSetInfoProvider = otuSetInfoProvider;
	}

	public StudyInfo toStudyInfo(final IStudy study) {
		checkNotNull(study);
		final StudyInfo studyInfo = studyInfoProvider.get();
		studyInfo.setEntityId(study.getId());
		studyInfo.setPPodId(study.getPPodId());
		studyInfo.setVersion(study.getVersionInfo().getVersion());

		for (final IOTUSet otuSet : study.getOTUSets()) {
			final OTUSetInfo otuSetInfo = otuSetInfoProvider.get();

			studyInfo.getOTUSetInfos().add(otuSetInfo);
			otuSetInfo.setEntityId(otuSet.getId());
			otuSetInfo.setPPodId(otuSet.getPPodId());
			otuSetInfo.setVersion(otuSet.getVersionInfo()
					.getVersion());
			otuSetInfo.setDocId(otuSet.getDocId());
			for (final IOTU otu : otuSet.getOTUs()) {
				final PPodEntityInfoWDocId otuInfo =
						pPodEntityInfoWDocIdProvider.get();
				otuSetInfo.getOTUInfos().add(otuInfo);
				otuInfo.setEntityId(otu.getId());
				otuInfo.setPPodId(otu.getPPodId());
				otuInfo.setVersion(otu.getVersionInfo()
						.getVersion());
				otuInfo.setDocId(otu.getDocId());
			}

			for (final IStandardMatrix matrix : otuSet
					.getStandardMatrices()) {
				final MatrixInfo matrixInfo = matrixInfoProvider.get();
				otuSetInfo.getMatrixInfos().add(matrixInfo);
				matrixInfo.setPPodId(matrix.getPPodId());
				matrixInfo.setEntityId(matrix.getId());
				matrixInfo.setVersion(matrix.getVersionInfo()
						.getVersion());
				matrixInfo.setDocId(matrix.getDocId());

				int characterIdx = -1;
				for (final IStandardCharacter standardCharacter : matrix
						.getCharacters()) {
					characterIdx++;
					final PPodEntityInfo characterInfo =
							pPodEntityInfoProvider.get();
					characterInfo.setPPodId(standardCharacter.getPPodId());
					characterInfo.setEntityId(standardCharacter.getId());
					characterInfo.setVersion(standardCharacter.getVersionInfo()
							.getVersion());
					matrixInfo.getCharacterInfosByIdx().put(characterIdx,
							characterInfo);
				}

				for (int columnPosition = 0; columnPosition < matrix
						.getColumnVersionInfos().size(); columnPosition++) {
					final VersionInfo columnVersionInfo =
							matrix.getColumnVersionInfos()
									.get(columnPosition);
					matrixInfo.getColumnHeaderVersionsByIdx()
								.put(columnPosition,
										columnVersionInfo.getVersion());
				}

				int rowIdx = -1;

				for (final IOTU otu : matrix.getParent().getOTUs()) {
					final IStandardRow row = matrix.getRows().get(otu);
					rowIdx++;
					final Long rowVersion = row.getVersionInfo()
							.getVersion();
					matrixInfo.getRowHeaderVersionsByIdx().put(rowIdx,
							rowVersion);

					int cellIdx = -1;
					for (final StandardCell cell : row.getCells()) {
						cellIdx++;
						matrixInfo.setCellPPodIdAndVersion(
								rowIdx, cellIdx,
								cell
										.getVersionInfo()
										.getVersion());
					}
				}
			}

			// TODO: refactor so CharacterStateMatrix and DNAMatrix don't have
			// duplicate code.
			for (final IDNAMatrix matrix : otuSet
					.getDNAMatrices()) {
				final MatrixInfo matrixInfo = matrixInfoProvider.get();
				otuSetInfo.getMatrixInfos().add(matrixInfo);
				matrixInfo.setPPodId(matrix.getPPodId());
				matrixInfo.setEntityId(matrix.getId());
				matrixInfo.setVersion(matrix.getVersionInfo()
						.getVersion());
				matrixInfo.setDocId(matrix.getDocId());

				for (int columnPosition = 0; columnPosition < matrix
						.getColumnVersionInfos().size(); columnPosition++) {
					final VersionInfo columnVersionInfo =
							matrix.getColumnVersionInfos()
									.get(columnPosition);
					matrixInfo.getColumnHeaderVersionsByIdx()
							.put(columnPosition,
									columnVersionInfo.getVersion());
				}

				int rowIdx = -1;

				for (final IOTU otu : matrix.getParent().getOTUs()) {
					final IDNARow row = matrix.getRows().get(otu);
					rowIdx++;
					final Long rowVersion =
							row.getVersionInfo().getVersion();
					matrixInfo.getRowHeaderVersionsByIdx()
							.put(rowIdx, rowVersion);

					int cellIdx = -1;
					for (final IDNACell cell : row.getCells()) {
						cellIdx++;
						matrixInfo
								.setCellPPodIdAndVersion(rowIdx, cellIdx,
										cell.getVersionInfo().getVersion());
					}
				}
			}

			// TODO: this should be genericized when we support other kinds of
			// MolecularSequenceSets
			for (final IDNASequenceSet dnaSequenceSet : otuSet
					.getDNASequenceSets()) {
				final MolecularSequenceSetInfo sequenceSetInfo =
						molecularSequenceSetInfoProvider.get();
				otuSetInfo.getSequenceSetInfos().add(sequenceSetInfo);
				sequenceSetInfo.setPPodId(dnaSequenceSet.getPPodId());
				sequenceSetInfo.setVersion(dnaSequenceSet
						.getVersionInfo().getVersion());
				sequenceSetInfo.setEntityId(dnaSequenceSet.getId());
				for (final IOTU otu : otuSet.getOTUs()) {
					final DNASequence dnaSequence =
							dnaSequenceSet.getSequence(otu);
					sequenceSetInfo
							.getSequenceVersionsByOTUDocId()
							.put(
									otu.getDocId(),
									dnaSequence.getVersionInfo().getVersion());
				}
			}

			for (final ITreeSet treeSet : otuSet.getTreeSets()) {
				final TreeSetInfo treeSetInfo = treeSetInfoProvider.get();
				otuSetInfo.getTreeSetInfos().add(treeSetInfo);
				treeSetInfo.setEntityId(treeSet.getId());
				treeSetInfo.setPPodId(treeSet.getPPodId());
				treeSetInfo.setVersion(treeSet.getVersionInfo()
						.getVersion());
				treeSetInfo.setDocId(treeSet.getDocId());

				for (final ITree tree : treeSet.getTrees()) {
					final PPodEntityInfo treeInfo =
							pPodEntityInfoProvider.get();
					treeSetInfo.getTreeInfos().add(treeInfo);
					treeInfo.setEntityId(tree.getId());
					treeInfo.setPPodId(tree.getPPodId());
					treeInfo.setVersion(
							tree.getVersionInfo()
									.getVersion());
				}
			}
		}
		return studyInfo;
	}
}
