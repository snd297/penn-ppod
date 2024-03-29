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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

/**
 * Says what kind of attachment we have. for example, you might have an author
 * attachment.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = AttachmentType.TABLE)
public class AttachmentType extends PersistentObject {

	public static final String TABLE = "ATTACHMENT_TYPE";

	public static final String JOIN_COLUMN = TABLE + "_ID";

	public static final String LABEL_COLUMN = "LABEL";

	public static final int LABEL_COLUMN_LENGTH = 64;

	@ManyToOne(
			fetch = FetchType.LAZY,
			optional = false)
	@JoinColumn(name = AttachmentNamespace.JOIN_COLUMN)
	@CheckForNull
	private AttachmentNamespace namespace;

	/**
	 * Label's are unique in a given attachment namespace, but not unique
	 * globally.
	 */
	@Column(name = LABEL_COLUMN, nullable = false, length = LABEL_COLUMN_LENGTH)
	@Index(name = LABEL_COLUMN + "_IDX")
	@CheckForNull
	private String label;

	/**
	 * For Hibernate.
	 */
	public AttachmentType() {}

	/**
	 * Get the label. Will be {@code null} for newly created objects until
	 * {@link #setLabel(String)} is called and then will never be {@code null}
	 * 
	 * @return the label
	 */
	@Nullable
	public String getLabel() {
		return label;
	}

	/**
	 * Get the namespace. Will be {@code null} for newly constructed objects,
	 * once the namespace is set it will never be {@code null}.
	 * 
	 * @return the namespace
	 */
	@Nullable
	public AttachmentNamespace getNamespace() {
		return namespace;
	}

	/** {@inheritDoc} */
	public void setLabel(final String label) {
		checkNotNull(label);
		this.label = label;
	}

	/**
	 * Set the namespace.
	 * 
	 * @param namespace the namespace to set
	 */
	public void setNamespace(final AttachmentNamespace namespace) {
		checkNotNull(namespace);
		this.namespace = namespace;
	}
}
