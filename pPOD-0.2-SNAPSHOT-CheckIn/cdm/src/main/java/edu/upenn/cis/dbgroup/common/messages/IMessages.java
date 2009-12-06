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

import java.util.List;

/**
 * PPodUser messages.
 * 
 * @author Sam Donnelly
 */
public interface IMessages {

	/**
	 * Get the messages
	 * 
	 * @return the messages
	 */
	List<IMessage> getMessages();

	/**
	 * Get the messages at a particular level.
	 * 
	 * @param level the level of the messages we want
	 * @return the messages at a particular level
	 */
	List<IMessage> getMessages(IMessage.Level level);

	void newCriticalErrorMessage(String code, String... params)
			throws MessagesException;

	/**
	 * 
	 * @param code
	 * @param messagesException
	 * @throws MessagesException always throws {@code messageException}
	 */
	void newCriticalErrorMessage(String code,
			MessagesException messagesException) throws MessagesException;
}
