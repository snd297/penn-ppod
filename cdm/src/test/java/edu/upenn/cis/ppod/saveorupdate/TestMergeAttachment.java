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
package edu.upenn.cis.ppod.saveorupdate;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import edu.upenn.cis.ppod.model.Attachment;

/**
 * Stub {@link IMergeAttachments}.
 * 
 * @author Sam Donnelly
 */
public class TestMergeAttachment implements IMergeAttachments {

	/**
	 * Does nothing but return {@code targetAttachment}.
	 */
	public void merge(final Attachment targetAttachment,
			final Attachment sourceAttachment) {
		checkNotNull(targetAttachment);
		checkNotNull(sourceAttachment);
		checkArgument(sourceAttachment.getType() != null,
				"sourceAttachment.getType() == null");
		targetAttachment.setLabel(sourceAttachment.getLabel());
		targetAttachment.setStringValue(sourceAttachment.getStringValue());
		targetAttachment.setType(sourceAttachment.getType());
	}
}
