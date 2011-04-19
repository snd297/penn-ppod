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
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newTreeMap;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.dbunit.Assertion;
import org.dbunit.dataset.IDataSet;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.testng.Assert;
import org.testng.annotations.Test;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.dao.ICharacterDAO;
import edu.upenn.cis.ppod.dao.ICharacterStateDAO;
import edu.upenn.cis.ppod.dao.IStandardMatrixDAO;
import edu.upenn.cis.ppod.dao.IOTUDAO;
import edu.upenn.cis.ppod.dao.IOTUSetDAO;

@Test(groups = { TestGroupDefs.SLOW, TestGroupDefs.BROKEN })
@edu.umd.cs.findbugs.annotations.SuppressWarnings
public class MatrixToDbTest extends DbTestBase {

	@Test
	public void matrixToDb() throws Throwable {
		final String METHOD = "matrixToDb()";
		try {
			final SAXReader saxReader = new SAXReader();
			final Document pPodData = saxReader.read(getClass()
					.getResourceAsStream(PPOD_DATA_XML));

// ManagedSessionContextUtil.openAndBindSession();
			final ICharacterDAO phyloCharDAO = daoFactory.getCharacterDAO();

			final Map<Long, Character> initialDataSetIdToPhyloCarMap = newHashMap();
			final Map<Long, CharacterState> initialDataSetIdToPhyloCharStateMap = newHashMap();

// HibernateUtil.beginTransactionInCurrentSession();

			@SuppressWarnings("unchecked")
			final List<Element> phyloCharElements = pPodData
					.selectNodes("/dataset/" + Character.TABLE.toLowerCase());

			for (final Element phyloCharElement : phyloCharElements) {
				final Character phyloChar = new Character();

				initialDataSetIdToPhyloCarMap.put(new Long(phyloCharElement
						.attributeValue(Character.ID_COLUMN)), phyloChar);

				phyloChar.setLabel(phyloCharElement
						.attributeValue(Character.LABEL_COLUMN));

				phyloCharDAO.saveOrUpdate(phyloChar);
			}

			final ICharacterStateDAO phyloCharStateDAO = daoFactory
					.getCharacterStateDAO();

			@SuppressWarnings("unchecked")
			final List<Element> phyloCharStateElements = pPodData
					.selectNodes("/dataset/"
							+ CharacterState.TABLE.toLowerCase());

			for (final Element phyloCharStateElement : phyloCharStateElements) {
// final CharacterState phyloCharState = initialDataSetIdToPhyloCarMap
// .get(
// new Long(phyloCharStateElement
// .attributeValue(Character.ID_COLUMN)))
// .put(new CharacterState());
// phyloCharState.setState(new Integer(phyloCharStateElement
// .attributeValue(CharacterState.STATE_NUMBER_COLUMN)));
// phyloCharState.setLabel(phyloCharStateElement
// .attributeValue(CharacterState.LABEL_COLUMN));
// initialDataSetIdToPhyloCharStateMap.put(new Long(
// phyloCharStateElement
// .attributeValue(CharacterState.ID_COLUMN)),
// phyloCharState);
// phyloCharDAO.saveOrUpdate(initialDataSetIdToPhyloCarMap
// .get(new Long(phyloCharStateElement
// .attributeValue(Character.ID_COLUMN))));
// phyloCharStateDAO.saveOrUpdate(phyloCharState);
			}

			final Map<Long, OTU> initialDataSetToOTUMap = newHashMap();

			@SuppressWarnings("unchecked")
			final List<Element> otuElements = pPodData.selectNodes("/dataset/"
					+ OTU.TABLE.toLowerCase());
			for (final Element otuElement : otuElements) {
				final OTU otu = new OTU().setLabel(otuElement
						.attributeValue(OTU.LABEL_COLUMN));
				initialDataSetToOTUMap.put(new Long(otuElement
						.attributeValue(OTU.ID_COLUMN)), otu);
				final IOTUDAO otuDAO = daoFactory.getOTUDAO();
				otuDAO.saveOrUpdate(otu);
			}

			final IOTUSetDAO otuSetDAO = daoFactory.getOTUSetDAO();

			final Map<Long, OTUSet> initialDataSetToOTUSetMap = newHashMap();

			@SuppressWarnings("unchecked")
			final List<Element> otuSetElements = pPodData
					.selectNodes("/dataset/" + OTUSet.TABLE.toLowerCase());

			for (final Element otuSetElement : otuSetElements) {
				final OTUSet otuSet = new OTUSet();
				initialDataSetToOTUSetMap.put(new Long(otuSetElement
						.attributeValue(OTUSet.ID_COLUMN)), otuSet);
				otuSet.setLabel(otuSetElement
						.attributeValue(OTUSet.LABEL_COLUMN));
				otuSet.setDescription(otuSetElement
						.attributeValue(OTUSet.DESCRIPTION_COLUMN));

				@SuppressWarnings("unchecked")
				final List<Element> otuSetOTUElements = pPodData
						.selectNodes("/dataset/"
								+ OTUSet.OTU_SET_OTU_JOIN_TABLE.toLowerCase()
								+ "[@"
								+ OTUSet.ID_COLUMN
								+ "='"
								+ otuSetElement
										.attributeValue(OTUSet.ID_COLUMN)
								+ "']");

				for (final Element otuSetOTUElement : otuSetOTUElements) {
					// otuSet.addOTU(initialDataSetToOTUMap.get(new Long(
					// otuSetOTUElement.attributeValue(OTU.ID_COLUMN))));
				}
				otuSetDAO.saveOrUpdate(otuSet); // this'll save the OTU's too
			}

			final ICharacterStateMatrixDAO phyloCharMatrixDAO = daoFactory
					.getCharacterStateMatrixDAO();

			@SuppressWarnings("unchecked")
			final List<Element> matrixElements = pPodData
					.selectNodes("/dataset/"
							+ CharacterStateMatrix.TABLE.toLowerCase());

			for (final Element matrixElement : matrixElements) {
				final CharacterStateMatrix phyloCharMatrix = new CharacterStateMatrix();
				phyloCharMatrix.setLabel(matrixElement
						.attributeValue(CharacterStateMatrix.LABEL_COLUMN));
				phyloCharMatrix
						.setDescription(matrixElement
								.attributeValue(CharacterStateMatrix.DESCRIPTION_COLUMN));

				phyloCharMatrix.setOTUSet(initialDataSetToOTUSetMap
						.get(new Long(matrixElement
								.attributeValue(OTUSet.ID_COLUMN))));

				@SuppressWarnings("unchecked")
				List<Element> matrixOTUElements = pPodData
						.selectNodes("/dataset/"
								+ (CharacterStateMatrix.TABLE + OTU.TABLE)
										.toLowerCase()
								+ "[@"
								+ CharacterStateMatrix.ID_COLUMN
								+ "='"
								+ matrixElement
										.attributeValue(CharacterStateMatrix.ID_COLUMN)
								+ "']");

				final SortedMap<Integer, OTU> sortedOTUs = newTreeMap();

				for (final Element matrixOTUElement : matrixOTUElements) {
					sortedOTUs.put(new Integer(matrixOTUElement
							.attributeValue(OTU.TABLE + "_POSITION")),
							initialDataSetToOTUMap.get(new Long(
									matrixOTUElement
											.attributeValue(OTU.ID_COLUMN))));
				}
				matrixOTUElements = null; // free up the memory

				@SuppressWarnings("unchecked")
				final List<Element> matrixPhyloCharElements = pPodData
						.selectNodes("/dataset/"
								+ (CharacterStateMatrix.TABLE + Character.TABLE)
										.toLowerCase()
								+ "[@"
								+ CharacterStateMatrix.ID_COLUMN
								+ "='"
								+ matrixElement
										.attributeValue(CharacterStateMatrix.ID_COLUMN)
								+ "']");

				for (final Element matrixPhyloCharElement : matrixPhyloCharElements) {
					// logger.debug("{}: new Integer(numberValueOf.intValue): {}",
					// METHOD, new Integer(matrixPhyloCharElement
					// .attributeValue(Character.TABLE
					// + "_POSITION")));
// phyloCharMatrix
// .setCharacter(
// new Integer(
// matrixPhyloCharElement
// .attributeValue(CharacterStateMatrix.CHARACTERS_POSITION_COLUMN)),
// initialDataSetIdToPhyloCarMap
// .get(new Long(
// matrixPhyloCharElement
// .attributeValue(Character.ID_COLUMN))));
				}

				@SuppressWarnings("unchecked")
				List<Element> rowElements = pPodData.selectNodes("/dataset/"
						+ CharacterStateRow.TABLE.toLowerCase()
						+ "[@"
						+ CharacterStateMatrix.ID_COLUMN
						+ "='"
						+ matrixElement
								.attributeValue(CharacterStateMatrix.ID_COLUMN)
						+ "']");

				final SortedMap<Integer, Element> sortedRowElements = newTreeMap(); // couldn't
				// get
				// pPodSelectNodes
				// to
				// sort
				// numerically
				// on
				// position
				for (final Element rowElement : rowElements) {}
				rowElements = null; // free up the memory

				for (final Element sortedRowElement : sortedRowElements
						.values()) {

					final CharacterStateRow phyloCharMatrixRow = null;// phyloCharMatrix
					// .setRow(
					// new Integer(
					// sortedRowElement
					// .attributeValue(CharacterStateMatrix.ROWS_POSITION_COLUMN)),
					// new CharacterStateRow());

					@SuppressWarnings("unchecked")
					List<Element> cellElements = pPodData
							.selectNodes("/dataset/"
									+ CharacterStateCell.TABLE.toLowerCase()
									+ "[@"
									+ CharacterStateRow.ID_COLUMN
									+ "='"
									+ sortedRowElement
											.attributeValue(CharacterStateRow.ID_COLUMN)
									+ "']");

					final SortedMap<Integer, Element> sortedCellElements = newTreeMap(); // couldn't
					// get pPodSelectNodes to sort numerically on position
					for (final Element cellElement : cellElements) {
						// logger.debug("cellElement: " + cellElement);
						// logger
						// .debug("cellElement: "
						// + cellElement
						// .attributeValue(PhyloCharMatrixRow.CELL_INDEX_COLUMN));
						sortedCellElements
								.put(
										new Integer(
												cellElement
														.attributeValue(CharacterStateRow.CELLS_INDEX_COLUMN)),
										cellElement);
					}
					cellElements = null; // free up the memory

					Assert.assertTrue(sortedCellElements.size() > 0);

					final List<CharacterStateCell> cells = newArrayList();
					for (final Element sortedCellElement : sortedCellElements
							.values()) {
						// logger.debug("cellElement: {}", sortedCellElement);
// final CharacterStateCell phyloCharMatrixCell = phyloCharMatrixRow
// .addCell(new CharacterStateCell());

// phyloCharMatrixCell
// .setType(CharacterStateCell.Type
// .of(sortedCellElement
// .attributeValue(CharacterStateCell.TYPE_COLUMN)));
						final String cellPhyloCharStateXPath = "/dataset/"
								+ CharacterStateCell.CELL_CHARACTER_STATE_JOIN_TABLE
										.toLowerCase()
								+ "[@"
								+ CharacterStateCell.ID_COLUMN
								+ "='"
								+ sortedCellElement
										.attributeValue(CharacterStateCell.ID_COLUMN)
								+ "']";
						// logger.debug("Looking for nodes with {}",
						// cellPhyloCharStateXPath);

						@SuppressWarnings("unchecked")
						final List<Element> cellPhyloCharStateElements = pPodData
								.selectNodes(cellPhyloCharStateXPath);

						// logger.debug("cellPhyloCharStates.size(): {}",
						// cellPhyloCharStateElements.size());
						for (final Element cellPhyloCharStateElement : cellPhyloCharStateElements) {
// phyloCharMatrixCell
// .addState(initialDataSetIdToPhyloCharStateMap
// .get(new Long(
// cellPhyloCharStateElement
// .attributeValue(CharacterState.ID_COLUMN))));
						}
					}
				}
				phyloCharMatrixDAO.saveOrUpdate(phyloCharMatrix);
			}

// HibernateUtil.commitTransactionInCurrentSession();
// ManagedSessionContextUtil.unbindAndCloseCurrentSession();

			// Fetch database data after executing your code
			final IDataSet actualDataSet = getDatabaseConnection()
					.createDataSet(
							new String[] { CharacterStateMatrix.TABLE,
									OTU.TABLE, OTUSet.TABLE,
									CharacterStateRow.TABLE,
									CharacterStateCell.TABLE, Character.TABLE,
									CharacterState.TABLE });

			for (final String tableName : actualDataSet.getTableNames()) {
				if ((CharacterStateMatrix.TABLE + OTU.TABLE).equals(tableName)) {
					continue;
				}

				// We're only ignoring the DESCRIPTION because DbUnit doesn't
				// like
				// it and we need to write a DTD for the dataset for it to
				// handle null'd columns right.
				Assertion.assertEqualsIgnoreCols(initialDataSet, actualDataSet,
						tableName, new String[] { "PHYLO_CHAR_ID_COLUMN",
								"*_ID", "OBJ_VERSION", "DESCRIPTION" });
			}

		} catch (final Throwable t) {
			try {
// HibernateUtil.rollbackTransactionInCurrentSession();
			} catch (final Throwable rbEx) {
				logger.error("Could not rollback transaction after exception!",
						rbEx);
			}
			throw t;
		} finally {
// HibernateUtil.closeSessionFactory();
		}
	}
}
