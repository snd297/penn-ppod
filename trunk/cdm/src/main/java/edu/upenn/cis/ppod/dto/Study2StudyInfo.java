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
package edu.upenn.cis.ppod.dto;

import static com.google.common.base.Preconditions.checkNotNull;
import edu.upenn.cis.ppod.model.DnaCell;
import edu.upenn.cis.ppod.model.DnaMatrix;
import edu.upenn.cis.ppod.model.DnaRow;
import edu.upenn.cis.ppod.model.DnaSequence;
import edu.upenn.cis.ppod.model.DnaSequenceSet;
import edu.upenn.cis.ppod.model.Otu;
import edu.upenn.cis.ppod.model.OtuSet;
import edu.upenn.cis.ppod.model.StandardCell;
import edu.upenn.cis.ppod.model.StandardCharacter;
import edu.upenn.cis.ppod.model.StandardMatrix;
import edu.upenn.cis.ppod.model.StandardRow;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.model.Tree;
import edu.upenn.cis.ppod.model.TreeSet;
import edu.upenn.cis.ppod.model.VersionInfo;

/**
 * @author Sam Donnelly
 */
public final class Study2StudyInfo {

	private Study2StudyInfo() {}

	public static StudyInfo toStudyInfo(final Study study) {
		checkNotNull(study);
		final StudyInfo studyInfo = new StudyInfo();
		studyInfo.setEntityId(study.getId());
		studyInfo.setPPodId(study.getPPodId());
		studyInfo.setVersion(study.getVersionInfo().getVersion());

		for (final OtuSet otuSet : study.getOtuSets()) {
			final OtuSetInfo otuSetInfo = new OtuSetInfo();

			studyInfo.getOtuSetInfos().add(otuSetInfo);
			otuSetInfo.setEntityId(otuSet.getId());
			otuSetInfo.setPPodId(otuSet.getPPodId());
			otuSetInfo.setVersion(otuSet.getVersionInfo()
					.getVersion());
			for (final Otu otu : otuSet.getOtus()) {
				final PPodEntityInfo otuInfo =
						new PPodEntityInfo();
				otuSetInfo.getOtuInfos().add(otuInfo);
				otuInfo.setEntityId(otu.getId());
				otuInfo.setPPodId(otu.getPPodId());
				otuInfo.setVersion(otu.getVersionInfo()
						.getVersion());
			}

			for (final StandardMatrix matrix : otuSet
					.getStandardMatrices()) {
				final StandardMatrixInfo matrixInfo = new StandardMatrixInfo();
				otuSetInfo.getStandardMatrixInfos().add(matrixInfo);
				matrixInfo.setPPodId(matrix.getPPodId());
				matrixInfo.setEntityId(matrix.getId());
				matrixInfo.setVersion(matrix.getVersionInfo()
						.getVersion());

				int characterIdx = -1;
				for (final StandardCharacter standardCharacter : matrix
						.getCharacters()) {
					characterIdx++;
					final PPodEntityInfo characterInfo =
							new PPodEntityInfo();
					characterInfo.setPPodId(standardCharacter.getPPodId());
					characterInfo.setEntityId(standardCharacter.getId());
					characterInfo.setVersion(
							standardCharacter.getVersionInfo().getVersion());
					matrixInfo.getCharacterInfosByIdx()
							.put(characterIdx, characterInfo);
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

				for (final Otu otu : matrix.getParent().getOtus()) {
					final StandardRow row = matrix.getRows().get(otu);
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
			for (final DnaMatrix matrix : otuSet
					.getDnaMatrices()) {
				final MatrixInfo matrixInfo = new MatrixInfo();
				otuSetInfo.getDnaMatrixInfos().add(matrixInfo);
				matrixInfo.setPPodId(matrix.getPPodId());
				matrixInfo.setEntityId(matrix.getId());
				matrixInfo.setVersion(matrix.getVersionInfo()
						.getVersion());

				int rowIdx = -1;

				for (final Otu otu : matrix.getParent().getOtus()) {
					final DnaRow row = matrix.getRows().get(otu);
					rowIdx++;
					final Long rowVersion =
							row.getVersionInfo().getVersion();
					matrixInfo.getRowHeaderVersionsByIdx()
							.put(rowIdx, rowVersion);

					int cellIdx = -1;
					for (final DnaCell cell : row.getCells()) {
						cellIdx++;
						matrixInfo
								.setCellPPodIdAndVersion(rowIdx, cellIdx,
										cell.getVersionInfo().getVersion());
					}
				}
			}

			// TODO: this should be genericized when we support other kinds of
			// MolecularSequenceSets
			for (final DnaSequenceSet dnaSequenceSet : otuSet
					.getDnaSequenceSets()) {
				final SequenceSetInfo sequenceSetInfo =
						new SequenceSetInfo();
				otuSetInfo.getSequenceSetInfos().add(sequenceSetInfo);
				sequenceSetInfo.setPPodId(dnaSequenceSet.getPPodId());
				sequenceSetInfo.setVersion(dnaSequenceSet
						.getVersionInfo().getVersion());
				sequenceSetInfo.setEntityId(dnaSequenceSet.getId());
				int otuPos = -1;
				for (final Otu otu : otuSet.getOtus()) {
					otuPos++;
					final DnaSequence dnaSequence =
							dnaSequenceSet.getSequence(otu);
					sequenceSetInfo
							.getSequenceVersions().put(otuPos,
									dnaSequence.getVersionInfo().getVersion());
				}
			}

			for (final TreeSet treeSet : otuSet.getTreeSets()) {
				final TreeSetInfo treeSetInfo = new TreeSetInfo();
				otuSetInfo.getTreeSetInfos().add(treeSetInfo);
				treeSetInfo.setEntityId(treeSet.getId());
				treeSetInfo.setPPodId(treeSet.getPPodId());
				treeSetInfo.setVersion(treeSet.getVersionInfo()
						.getVersion());

				for (final Tree tree : treeSet.getTrees()) {
					final PPodEntityInfo treeInfo = new PPodEntityInfo();
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
