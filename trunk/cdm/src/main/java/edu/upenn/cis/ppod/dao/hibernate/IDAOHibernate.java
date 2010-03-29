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

import java.io.Serializable;

import org.hibernate.Session;

import edu.upenn.cis.ppod.dao.IDAO;

/**
 * An {@link IDAO} that needs to have a {@link Session} set to function.
 * 
 * @author Sam Donnelly
 */
public interface IDAOHibernate<T, ID extends Serializable> extends IDAO<T, ID> {

	/**
	 * Set the {@code session}.
	 * 
	 * @param s the session
	 * 
	 * @return this
	 */
	IDAOHibernate<T, ID> setSession(Session s);
}
