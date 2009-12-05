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
package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * To prevent name clashes among {@link AttachmentType}s.
 * 
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = AttachmentNamespace.TABLE)
public final class AttachmentNamespace extends PersistentObjectWXmlId {

	static final String TABLE = "ATTACHMENT_NAMESPACE";

	static final String ID_COLUMN = TABLE + "_ID";

	static final String LABEL_COLUMN = "LABEL";

	static final int LABEL_COLUMN_LENGTH = 64;

	@XmlAttribute
	@Column(name = LABEL_COLUMN, unique = true, nullable = false, length = LABEL_COLUMN_LENGTH)
	private String label;

	AttachmentNamespace() {}

	/**
	 * Get the label.
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Set the label.
	 * 
	 * @param label the label
	 * @return this
	 */
	public AttachmentNamespace setLabel(final String label) {
		checkNotNull(label);
		this.label = label;
		return this;
	}
}
