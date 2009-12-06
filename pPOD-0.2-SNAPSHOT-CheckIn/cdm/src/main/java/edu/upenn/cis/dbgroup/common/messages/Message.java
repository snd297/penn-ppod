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

import org.hibernate.cfg.AnnotationConfiguration;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * Safe for subclassing.
 * 
 * @author Sam Donnelly
 */
class Message implements IMessage {

	private final Level level;

	private final String code;

	private final IMessagesConfig messagesConfig;

	private String[] params;
	
	@Inject
	Message(@Assisted final String code, @Assisted final Level level,
			final IMessagesConfig messagesConfig,
			@Assisted final String... params) {
		new AnnotationConfiguration();
		this.level = level;
		this.code = code;
		this.messagesConfig = messagesConfig;
		this.params = params;
	}

	public Level getLevel() {
		return level;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return messagesConfig.getMessageText(code, params);
	}

}
