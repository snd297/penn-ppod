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

import org.dbunit.IDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.slf4j.Logger;
import org.testng.annotations.AfterClass;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.dao.IDAOFactory;
import edu.upenn.cis.ppod.util.DbUnitUtil;

/**
 * @author Sam Donnelly
 */
public class DbTestBase {
	protected static final String PPOD_DATA_XML = "/ppod-data-M1328.xml";
	protected IDatabaseTester dbTester = DbUnitUtil.getDatabaseTester();;
	protected IDataSet initialDataSet = DbUnitUtil.getDataSet(PPOD_DATA_XML);;

	@edu.umd.cs.findbugs.annotations.SuppressWarnings
	protected IDAOFactory daoFactory = null; // new PPodFactory();
// .create(IDAOFactory.class);

	@Inject
	protected static Logger logger;

	@AfterClass
	public void cleanDb() throws Exception {
		final String METHOD = "cleanDb()";
		logger.debug("{}: entering", METHOD);
		final IDatabaseConnection databaseConnection = getDatabaseConnection();
		try {
			DatabaseOperation.DELETE_ALL.execute(databaseConnection,
					initialDataSet);
		} finally {
			databaseConnection.close();
		}
	}

	protected IDatabaseConnection getDatabaseConnection() throws Exception {
		final IDatabaseConnection databaseConnection = dbTester.getConnection();
		if ("org.hibernate.dialect.MySQLDialect"
				.equals(DbUnitUtil.getDialect())) {
			databaseConnection.getConfig().setProperty(
					"http://www.dbunit.org/properties/datatypeFactory",
					new MySqlDataTypeFactory());
		} else {

		}
		return databaseConnection;
	}
}
