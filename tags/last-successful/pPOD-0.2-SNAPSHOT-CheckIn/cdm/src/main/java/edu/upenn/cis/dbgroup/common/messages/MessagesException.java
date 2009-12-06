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

import com.google.inject.Inject;

/**
 * An exception that contains {@code IMessages}.
 * <p>
 * Safe to subclass.
 * 
 * @author Sam Donnelly
 */
public class MessagesException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final IMessages messages;

	MessagesException(final IMessages messages) {
		super();
		this.messages = messages;
	}

	MessagesException(final IMessages messages, final Throwable cause) {
		super(cause);
		this.messages = messages;
	}

	public IMessages getMessages() {
		return messages;
	}

	public static class Factory {
		@Inject
		public Factory() {}

		public MessagesException create(IMessages messages) {
			return new MessagesException(messages);
		}

		public MessagesException create(IMessages messages, Throwable cause) {
			return new MessagesException(messages, cause);
		}
	}
}
