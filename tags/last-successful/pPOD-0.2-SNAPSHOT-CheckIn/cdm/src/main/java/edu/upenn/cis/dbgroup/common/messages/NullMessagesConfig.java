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
package edu.upenn.cis.dbgroup.common.messages;

import edu.upenn.cis.dbgroup.common.messages.IMessage.Level;

/**
 * @author Sam Donnelly
 */
public class NullMessagesConfig implements IMessagesConfig {

	/**
	 * Does nothing.
	 */
	public Level getMessageLevel(String messageCode) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMessageText(String messageCode, String... params) {
		// TODO Auto-generated method stub
		return null;
	}

}
