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

import edu.upenn.cis.ppod.model.Character;
import edu.upenn.cis.ppod.model.CharacterStateCell;
import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.CharacterStateRow;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.PPodVersionInfo;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.model.Tree;
import edu.upenn.cis.ppod.model.TreeSet;

/**
 * @author Sam Donnelly
 */
public final class Study2StudyInfo implements IStudy2StudyInfo {
	private final Provider<StudyInfo> studyInfoProvider;
	private final Provider<OTUSetInfo> otuSetInfoProvider;
	private final Provider<CharacterStateMatrixInfo> matrixInfoProvider;
	private final Provider<TreeSetInfo> treeSetInfoProvider;
	private final Provider<PPodEntityInfo> pPodEntityInfoProvider;
	private final Provider<PPodEntityInfoWDocId> pPodEntityInfoWDocIdProvider;

	@Inject
	Study2StudyInfo(
			final Provider<StudyInfo> studyPPodIdAndVersionProvider,
			final Provider<OTUSetInfo> otuSetPPodIdAndVersionProvider,
			final Provider<CharacterStateMatrixInfo> matrixPPodIdAndVersionProvider,
			final Provider<TreeSetInfo> treeSetInfoProvider,
			final Provider<PPodEntityInfo> pPodEntityInfoProvider,
			final Provider<PPodEntityInfoWDocId> pPodEntityInfoWDocIdProvider) {
		this.studyInfoProvider = studyPPodIdAndVersionProvider;
		this.otuSetInfoProvider = otuSetPPodIdAndVersionProvider;
		this.matrixInfoProvider = matrixPPodIdAndVersionProvider;
		this.treeSetInfoProvider = treeSetInfoProvider;
		this.pPodEntityInfoProvider = pPodEntityInfoProvider;
		this.pPodEntityInfoWDocIdProvider = pPodEntityInfoWDocIdProvider;
	}

	public StudyInfo go(final Study study) {
		checkNotNull(study);
		final StudyInfo studyInfo = studyInfoProvider.get();
		studyInfo.setEntityId(study.getId());
		studyInfo.setPPodId(study.getPPodId());
		studyInfo.setPPodVersion(study.getpPodVersionInfo().getPPodVersion());
		for (final OTUSet otuSet : study.getOTUSets()) {
			final OTUSetInfo otuSetInfo = otuSetInfoProvider.get();
			studyInfo.getOTUSetInfos().add(otuSetInfo);
			otuSetInfo.setEntityId(otuSet.getId());
			otuSetInfo.setPPodId(otuSet.getPPodId());
			otuSetInfo.setPPodVersion(otuSet.getpPodVersionInfo()
					.getPPodVersion());
			otuSetInfo.setDocId(otuSet.getDocId());
			for (final OTU otu : otuSet.getOTUs()) {
				final PPodEntityInfoWDocId otuInfo = pPodEntityInfoWDocIdProvider
						.get();
				otuSetInfo.getOTUInfos().add(otuInfo);
				otuInfo.setEntityId(otu.getId());
				otuInfo.setPPodId(otu.getPPodId());
				otuInfo.setPPodVersion(otu.getpPodVersionInfo()
						.getPPodVersion());
				otuInfo.setDocId(otu.getDocId());
			}

			for (final CharacterStateMatrix matrix : otuSet.getMatrices()) {
				final CharacterStateMatrixInfo matrixInfo = matrixInfoProvider
						.get();
				otuSetInfo.getMatrixInfos().add(matrixInfo);
				matrixInfo.setEntityId(matrix.getId());
				matrixInfo.setPPodId(matrix.getPPodId());
				matrixInfo.setPPodVersion(matrix.getpPodVersionInfo()
						.getPPodVersion());
				matrixInfo.setDocId(matrix.getDocId());

				int characterIdx = 0;
				for (final Character character : matrix.getCharacters()) {
					PPodEntityInfo characterInfo = pPodEntityInfoProvider.get();
					characterInfo.setPPodId(character.getPPodId());
					characterInfo.setEntityId(character.getId());
					characterInfo.setPPodVersion(character.getPPodVersion());
					matrixInfo.getCharacterInfosByIdx().put(characterIdx++,
							characterInfo);
				}

				int columnIdx = 0;
				for (final PPodVersionInfo columnPPodVersionInfo : matrix
						.getColumnPPodVersionInfos()) {
					matrixInfo.getColumnHeaderVersionsByIdx().put(columnIdx++,
					// TODO: what are we going to do about column header version
							matrix.getpPodVersionInfo().getPPodVersion());
					// columnPPodVersionInfo.getPPodVersion());
				}

				int rowIdx = 0;
				for (final CharacterStateRow row : matrix.getRows()) {
					matrixInfo.getRowHeaderVersionsByIdx().put(rowIdx++,
							row.getpPodVersionInfo().getPPodVersion());
					int cellIdx = 0;
					for (final CharacterStateCell cell : row.getCells()) {
						matrixInfo.setCellPPodIdAndVersion(rowIdx, cellIdx++,
								cell.getpPodVersionInfo().getPPodVersion());
					}
				}
			}

			for (final TreeSet treeSet : otuSet.getTreeSets()) {
				final TreeSetInfo treeSetInfo = treeSetInfoProvider.get();
				otuSetInfo.getTreeSetInfos().add(treeSetInfo);
				treeSetInfo.setEntityId(treeSet.getId());
				treeSetInfo.setPPodId(treeSet.getPPodId());
				treeSetInfo.setPPodVersion(treeSet.getpPodVersionInfo()
						.getPPodVersion());
				treeSetInfo.setDocId(treeSet.getDocId());

				for (final Tree tree : treeSet.getTrees()) {
					final PPodEntityInfo treeInfo = pPodEntityInfoProvider
							.get();
					treeSetInfo.getTreeInfos().add(treeInfo);
					treeInfo.setEntityId(tree.getId());
					treeInfo.setPPodId(tree.getPPodId());
					treeInfo.setPPodVersion(tree.getPPodVersion());
				}
			}
		}
		return studyInfo;
	}
}
