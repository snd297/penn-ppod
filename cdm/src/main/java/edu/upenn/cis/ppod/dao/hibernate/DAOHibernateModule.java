/*
 * Copyright (C) 2010 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.dao.hibernate;

import org.hibernate.Session;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import edu.upenn.cis.ppod.dao.IAttachmentNamespaceDAO;
import edu.upenn.cis.ppod.dao.IAttachmentTypeDAO;
import edu.upenn.cis.ppod.dao.IObjectWithLongIdDAO;
import edu.upenn.cis.ppod.thirdparty.util.HibernateUtil;

/**
 * The Class DAOHibernateModule.
 * 
 * @author Sam Donnelly
 */
public class DAOHibernateModule extends AbstractModule {

	@Override
	protected void configure() {

		bind(IAttachmentNamespaceDAO.class)
				.to(AttachmentNamespaceDAOHibernate.class);
		bind(IAttachmentTypeDAO.class)
				.to(AttachmentTypeDAOHibernate.class);
		bind(IObjectWithLongIdDAO.class)
				.to(ObjectWithLongIdDAOHibernate.class);

	}

	// TODO: load session factory into a guice singelton in this modeule
	// also make dao's request scoped?
	@Provides
	Session provideSession() {
		return HibernateUtil.getSessionFactory().getCurrentSession();
	}
}
