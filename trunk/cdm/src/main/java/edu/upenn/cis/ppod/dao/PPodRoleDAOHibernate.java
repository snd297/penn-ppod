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
package edu.upenn.cis.ppod.dao;

import edu.upenn.cis.ppod.model.security.Role;
import edu.upenn.cis.ppod.thirdparty.dao.hibernate.GenericHibernateDAO;

/**
 * The Class PPodRoleDAOHibernate.
 */
public class PPodRoleDAOHibernate extends
		GenericHibernateDAO<Role, Long> implements IPPodRoleDAO {

	public Role getByName(String name) {
		return (Role) getSession().createQuery(
				"select role from Role role where role.name=:name")
				.setParameter("name", name).uniqueResult();
	}
}