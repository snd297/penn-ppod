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

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import com.google.inject.Inject;

import edu.upenn.cis.dbgroup.common.messages.IMessage.Level;

/**
 * @author Sam Donnelly
 */
final class Messages implements IMessages {

	private List<IMessage> messages = newArrayList();

	private IMessage.IFactory messageFactory;

	private MessagesException.Factory messagesExceptionFactory;

	@Inject
	Messages(IMessage.IFactory messageFactory,
			MessagesException.Factory messagesExceptionFactory) {
		this.messageFactory = messageFactory;
		this.messagesExceptionFactory = messagesExceptionFactory;
	}

	public List<IMessage> getMessages() {
		return messages;
	}

	public List<IMessage> getMessages(final Level level) {
		final List<IMessage> messagesAtLevel = newArrayList();
		for (final IMessage message : messages) {
			if (message.getLevel().equals(level)) {
				messagesAtLevel.add(message);
			}
		}
		return messagesAtLevel;
	}

	public void newCriticalErrorMessage(String code, String... params)
			throws MessagesException {
		messages.add(messageFactory.create(code, IMessage.Level.CRITICAL_ERROR,
				params));
		throw messagesExceptionFactory.create(this);
	}

	public void newCriticalErrorMessage(String code,
			MessagesException messagesException) throws MessagesException {
		messages
				.add(messageFactory.create(code, IMessage.Level.CRITICAL_ERROR));
		throw messagesException;
	}
}
