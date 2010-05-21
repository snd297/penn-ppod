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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * Says what kind of attachment we have. for example, you might have an author
 * attachment.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = AttachmentType.TABLE)
public class AttachmentType extends PersistentObjectWithXmlId {

	static final String TABLE = "ATTACHMENT_TYPE";

	static final String ID_COLUMN = TABLE + "_ID";

	static final String LABEL_COLUMN = "LABEL";

	static final int LABEL_COLUMN_LENGTH = 64;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = AttachmentNamespace.ID_COLUMN, nullable = false)
	@CheckForNull
	private AttachmentNamespace namespace;

	@Column(name = LABEL_COLUMN, unique = true, nullable = false, length = LABEL_COLUMN_LENGTH)
	@CheckForNull
	private String label;

	AttachmentType() {}

	@Override
	public void accept(final IVisitor visitor) {
		visitor.visit(this);
		getNamespace().accept(visitor);
	}

	/**
	 * Get the label. Will be {@code null} for newly created objects until
	 * {@link #setLabel(String)} is called. Will never be {@code null} for
	 * persistent objects.
	 * 
	 * @return the label
	 */
	@XmlAttribute
	@Nullable
	public String getLabel() {
		return label;
	}

	/**
	 * Get the namespace. Will be {@code null} for newly constructed objects.
	 * 
	 * @return the namespace
	 */
	@XmlAttribute(name = "attachmentNamespaceDocId")
	@XmlIDREF
	@Nullable
	public AttachmentNamespace getNamespace() {
		return namespace;
	}

	/**
	 * Set the label.
	 * 
	 * @param label the label
	 * @return this
	 */
	public AttachmentType setLabel(final String label) {
		checkNotNull(label);
		this.label = label;
		return this;
	}

	/**
	 * Set the namespace.
	 * 
	 * @param namespace the namespace to set
	 * 
	 * @return this
	 */
	public AttachmentType setNamespace(final AttachmentNamespace namespace) {
		this.namespace = namespace;
		return this;
	}
}
