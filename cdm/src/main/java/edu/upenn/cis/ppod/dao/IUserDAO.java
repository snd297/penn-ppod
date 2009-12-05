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
package edu.upenn.cis.ppod.dao;

import edu.upenn.cis.ppod.thirdparty.model.security.User;

/**
 * Data Access Object for PPodUser related operations.
 * 
 * @author Sam Donnelly
 */
public interface IUserDAO extends IDAO<User, Long> {

	/**
	 * Get the user with the given name, or {@code null} if there is no such
	 * user.
	 * 
	 * @param userName the user name
	 * @return the user with the given name, or {@code null} if there is no such
	 *         user
	 */
	User getUserByName(String userName);

}
