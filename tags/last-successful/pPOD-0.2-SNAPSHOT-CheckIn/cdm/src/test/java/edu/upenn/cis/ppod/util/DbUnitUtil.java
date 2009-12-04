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

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.dbunit.DatabaseUnitException;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.hibernate.cfg.Environment;

/**
 * Utilities for working with DbUnit
 * 
 * @author Sam Donnelly
 */
public class DbUnitUtil {

	/** Logger. */
	// private static Logger logger = LoggerFactory.getLogger(DbUnitUtil.class);

	/** Prevent inheritance and instantiation. */
	private DbUnitUtil() {}

	/**
	 * Using Hibernate's {@code Environment}, get a {@code IDatabaseConnection}.
	 * 
	 * @return see description
	 */
	public static IDatabaseConnection getConnection() {
		return getConnection(null);
	}

	/**
	 * Using Hibernate's {@code Environment}, get a {@code IDatabaseConnection}.
	 * 
	 * @param dataTypeFactory value of the {@code IDatabaseConnetion}'s {@code
	 *            http://www.dbunit.org/properties/datatypeFactory} property -
	 *            that is, it's the the {@code IDataTypeFactory} that the
	 *            returned connection should use. If {@code null}, then the
	 *            property won't be set.
	 * 
	 * @return see description
	 */
	public static IDatabaseConnection getConnection(
			final IDataTypeFactory dataTypeFactory) {
		final Properties hibernateProperties = null;// HibernateUtil.getConfiguration()
		// .getProperties();

		// Load up the driver
		try {
			Class.forName(hibernateProperties.getProperty(Environment.DRIVER));
		} catch (final ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
		Connection jdbcConnection;
		try {
			jdbcConnection = DriverManager.getConnection(hibernateProperties
					.getProperty(Environment.URL), hibernateProperties
					.getProperty(Environment.USER), hibernateProperties
					.getProperty(Environment.PASS));
		} catch (final SQLException e) {
			throw new IllegalStateException(e);
		}

		try {
			final IDatabaseConnection c = new DatabaseConnection(jdbcConnection);
			if (dataTypeFactory != null) {
				c.getConfig().setProperty(
						"http://www.dbunit.org/properties/datatypeFactory",
						dataTypeFactory);
			}
			return c;

		} catch (final DatabaseUnitException e) {
			throw new IllegalStateException(e);
		}
	}

	public static IDatabaseTester getDatabaseTester() {
		return null;
//		final Properties hibernateProperties = HibernateUtil.getConfiguration()
//				.getProperties();
//
//		try {
//			return new JdbcDatabaseTester(hibernateProperties
//					.getProperty(Environment.DRIVER), hibernateProperties
//					.getProperty(Environment.URL), hibernateProperties
//					.getProperty(Environment.USER), hibernateProperties
//					.getProperty(Environment.PASS));
//		} catch (final ClassNotFoundException e) {
//			throw new IllegalStateException(e);
//		}
	}

	/**
	 * Given a resource name, load it up (via {@code
	 * ClassLoader.getResourceAsStream(String)} into a {@code IDataSet}.
	 * 
	 * @param xmlDataset resource name
	 * @return the {@code IDataSet}
	 */
	public static IDataSet getDataSet(final String xmlDataset) {
		IDataSet loadedDataSet;
		try {
			loadedDataSet = new FlatXmlDataSet(DbUnitUtil.class
					.getResourceAsStream(xmlDataset));
		} catch (final DataSetException e) {
			throw new IllegalStateException(e);
		} catch (final IOException e) {
			throw new IllegalStateException(e);
		}
		return loadedDataSet;
	}

	public static String getDialect() {
		return null;// HibernateUtil.getConfiguration().getProperties().getProperty(
		// Environment.DIALECT);
	}
}
