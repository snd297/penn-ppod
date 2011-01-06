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
package edu.upenn.cis.ppod.util;

import java.io.FileOutputStream;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;

/**
 * From an existing set of tables, create DbUnit testing data.
 * 
 * @author Sam Donnelly
 */
public class CreateDataSet {
	public static void main(final String[] args) throws Exception {
		final IDatabaseTester tester = new JdbcDatabaseTester(
				"com.mysql.jdbc.Driver", "jdbc:mysql://localhost/PPOD", "root",
				"");
		final IDatabaseConnection c = tester.getConnection();
		c.getConfig().setProperty(
				"http://www.dbunit.org/properties/datatypeFactory",
				new MySqlDataTypeFactory());

		final ITableFilter filter = new DatabaseSequenceFilter(c);
		final IDataSet output = new FilteredDataSet(filter, c.createDataSet());

		final FileOutputStream fos = new FileOutputStream("ppod-data.xml");
		// / FlatXmlDataSet xmlDataSet = new FlatXmlDataSet(
		FlatXmlDataSet.write(output, fos);
	}
}
