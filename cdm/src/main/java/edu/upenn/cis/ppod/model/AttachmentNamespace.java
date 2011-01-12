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

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * To prevent name clashes among {@link AttachmentType}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = AttachmentNamespace.TABLE)
public class AttachmentNamespace extends PersistentObject {

	public static final String TABLE = "ATTACHMENT_NAMESPACE";

	public static final String JOIN_COLUMN = TABLE + "_ID";

	public static final String LABEL_COLUMN = "LABEL";

	public static final int LABEL_COLUMN_LENGTH = 64;

	@Column(name = LABEL_COLUMN,
			unique = true,
			nullable = false,
			length = LABEL_COLUMN_LENGTH)
	@CheckForNull
	private String label;

	/**
	 * For Hibernate.
	 */
	public AttachmentNamespace() {}

	public void accept(final IVisitor visitor) {
		visitor.visitAttachmentNamespace(this);
	}

	/**
	 * Get the label. Will be {@code null} for newly created objects until
	 * {@link #setLabel(String)} is called. Once set, it will never be
	 * {@code null}
	 * 
	 * @return the label
	 */
	@XmlAttribute
	@Nullable
	public String getLabel() {
		return label;
	}

	/**
	 * Set the label.
	 * 
	 * @param label the label
	 */
	public void setLabel(final String label) {
		checkNotNull(label);
		this.label = label;
	}
}
