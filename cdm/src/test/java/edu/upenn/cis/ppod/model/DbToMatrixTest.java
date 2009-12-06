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
package edu.upenn.cis.ppod.model;

import static com.google.common.collect.Lists.newArrayList;
import static org.testng.Assert.assertEquals;

import java.util.List;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.operation.DatabaseOperation;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.dao.ICharacterStateMatrixDAO;
import edu.upenn.cis.ppod.util.DbUnitUtil;

@Test(groups = { TestGroupDefs.SLOW, TestGroupDefs.BROKEN })
public class DbToMatrixTest extends DbTestBase {

	public void dbToMatrix() throws Throwable {
		final String M = "dbToMatrix()";
		try {
			final ICharacterStateMatrixDAO phyloCharMatrixDAO = daoFactory
					.getCharacterStateMatrixDAO();
// ManagedSessionContextUtil.openAndBindSession();
// HibernateUtil.beginTransactionInCurrentSession();

			final SAXReader saxReader = new SAXReader();
			final Document pPodDataSmall = saxReader.read(getClass()
					.getResourceAsStream(PPOD_DATA_XML));

			@SuppressWarnings("unchecked")
			final List<Element> matrixElements = pPodDataSmall
					.selectNodes("/dataset/"
							+ CharacterStateMatrix.TABLE.toLowerCase());

			Assert.assertTrue(matrixElements.size() > 0);

			for (final Element matrixElement : matrixElements) {
				final List<CharacterStateMatrix> matrices = phyloCharMatrixDAO
						.getByLabel(matrixElement
								.attributeValue(CharacterStateMatrix.LABEL_COLUMN));
				Assert.assertEquals(matrices.size(), 1);
				final CharacterStateMatrix m = matrices.get(0);
				Assert.assertEquals(m.getLabel(), matrixElement
						.attributeValue(CharacterStateMatrix.LABEL_COLUMN));

				checkOTUs(pPodDataSmall, m);

				@SuppressWarnings("unchecked")
				final List<Element> rowElements = pPodDataSmall
						.selectNodes("/dataset/"
								+ CharacterStateRow.TABLE.toLowerCase()
								+ "[@"
								+ CharacterStateMatrix.ID_COLUMN
								+ "='"
								+ matrixElement
										.attributeValue(CharacterStateMatrix.ID_COLUMN)
								+ "']");

				Assert.assertTrue(rowElements.size() > 0);
				for (final Element rowElement : rowElements) {
					final CharacterStateRow phyloCharMatrixRow = m
							.getRow(Integer
									.parseInt(rowElement
											.attributeValue(CharacterStateMatrix.ROW_INDEX_COLUMN)));
					// Assert.assertEquals(row.getId(), new Long(rowElement
					// .attributeValue(PhyloCharMatrixRow.ID_COLUMN)));

					@SuppressWarnings("unchecked")
					final List<Element> cellElements = pPodDataSmall
							.selectNodes("/dataset/"
									+ CharacterStateCell.TABLE.toLowerCase()
									+ "[@"
									+ CharacterStateRow.ID_COLUMN
									+ "='"
									+ rowElement
											.attributeValue(CharacterStateRow.ID_COLUMN)
									+ "']");
					Assert.assertTrue(cellElements.size() > 0);
					for (final Element cellElement : cellElements) {
						final CharacterStateCell phyloCharMatrixCell = phyloCharMatrixRow
								.getCells()
								.get(
										Integer
												.parseInt(cellElement
														.attributeValue(CharacterStateRow.CELL_INDEX_COLUMN)));
						Assert
								.assertEquals(
										phyloCharMatrixCell.getType()
												.toString(),
										cellElement
												.attributeValue(CharacterStateCell.TYPE_COLUMN));

						final String cellPhyloCharStateXPath = "/dataset/"
								+ CharacterStateCell.CELL_CHARACTER_STATE_JOIN_TABLE
										.toLowerCase()
								+ "[@"
								+ CharacterStateCell.ID_COLUMN
								+ "='"
								+ cellElement
										.attributeValue(CharacterStateCell.ID_COLUMN)
								+ "']";
						logger.debug("{}:Looking for nodes with {}", M,
								cellPhyloCharStateXPath);

						@SuppressWarnings("unchecked")
						final List<Element> cellPhyloCharStateElements = pPodDataSmall
								.selectNodes(cellPhyloCharStateXPath);

						final List<Element> phyloCharStateElements = newArrayList();

						for (final Element cellPhyloCharStateElement : cellPhyloCharStateElements) {
							final String phyloCharStateXPath = "/dataset/"
									+ CharacterState.TABLE.toLowerCase()
									+ "[@"
									+ CharacterState.ID_COLUMN
									+ "='"
									+ cellPhyloCharStateElement
											.attributeValue(CharacterState.ID_COLUMN)
									+ "']";

							logger.debug(
									"{}:Looking for PhyloCharState's with {}",
									M, phyloCharStateXPath);

							@SuppressWarnings("unchecked")
							final List<Element> phyloCharStateObjectsTemp = pPodDataSmall
									.selectNodes(phyloCharStateXPath);

							Assert.assertTrue(
									phyloCharStateObjectsTemp.size() > 0,
									"phyloCharStateObjects.size() should have been > 0, but was "
											+ phyloCharStateElements.size());

							phyloCharStateElements
									.addAll(phyloCharStateObjectsTemp);
						}

						for (final Element phyloCharStateElement : phyloCharStateElements) {
							final CharacterState phyloCharState = phyloCharMatrixCell
									.getStateByNumber(new Integer(
											phyloCharStateElement
													.attributeValue(CharacterState.STATE_COLUMN)));
							logger
									.debug(
											"{}:new Long(\r\n"
													+ "															phyloCharStateElement\r\n"
													+ "																	.attributeValue(PhyloCharState.ID_COLUMN)))):{}",
											M,
											new Long(
													phyloCharStateElement
															.attributeValue(CharacterState.ID_COLUMN)));

							logger.debug("{}:phyloCharState: {}", M,
									phyloCharState);
							logger
									.debug(
											"{}:phyloCharStateElement.attributeValue(\"State\"):{}",
											M,
											new Integer(
													phyloCharStateElement
															.attributeValue(CharacterState.STATE_COLUMN)));
							Assert
									.assertEquals(
											phyloCharState.getStateNumber(),
											new Integer(
													phyloCharStateElement
															.attributeValue(CharacterState.STATE_COLUMN)));
							Assert
									.assertEquals(
											phyloCharState.getLabel(),
											phyloCharStateElement
													.attributeValue(CharacterState.LABEL_COLUMN));
						}
					}
				}
			}

// HibernateUtil.commitTransactionInCurrentSession();

		} catch (final Throwable t) {
			try {
// HibernateUtil.rollbackTransactionInCurrentSession();
			} catch (final Throwable rbEx) {
				logger.error("Could not rollback transaction after exception!",
						rbEx);
			}
			throw t;
		}
	}

	@BeforeClass
	public void loadDb() throws Exception {
		final String M = "loadDb()";
		logger.debug("{}: entering", M);
		dbTester = DbUnitUtil.getDatabaseTester();
		initialDataSet = DbUnitUtil.getDataSet(PPOD_DATA_XML);
		final IDatabaseConnection databaseConnection = getDatabaseConnection();
		try {
			// Leave all of our tables empty
			DatabaseOperation.CLEAN_INSERT.execute(databaseConnection,
					initialDataSet);
		} finally {
			databaseConnection.close();
		}
	}

	void checkOTUs(final Document pPodDocument, final CharacterStateMatrix m) {

		for (final OTU otu : m.getOTUs()) {
			final Element otuElement = (Element) pPodDocument
					.selectSingleNode("/dataset/" + OTU.TABLE.toLowerCase()
							+ "[@" + OTU.ID_COLUMN + "='" + otu.getId() + "']");
			Assert.assertEquals(otu.getLabel(), otuElement
					.attributeValue(OTU.LABEL_COLUMN));

			for (final OTUSet otuSet : otu.getOTUSets()) {
				final Element otuSetElement = (Element) pPodDocument
						.selectSingleNode("/dataset/"
								+ OTUSet.TABLE.toLowerCase() + "[@"
								+ OTUSet.ID_COLUMN + "='" + otuSet.getId()
								+ "']");
				assertEquals(otuSet.getLabel(), otuSetElement
						.attributeValue(OTUSet.LABEL_COLUMN));
				assertEquals(m.getOTUSet(), otuSet);
			}
		}
	}
}
