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
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.hibernate.annotations.Index;

import edu.upenn.cis.ppod.imodel.IAttachmentNamespace;
import edu.upenn.cis.ppod.imodel.IAttachmentType;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * Says what kind of attachment we have. for example, you might have an author
 * attachment.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = AttachmentType.TABLE)
public class AttachmentType
		extends PersistentObjectWithDocId
		implements IAttachmentType {

	public static class Adapter extends
			XmlAdapter<AttachmentType, IAttachmentType> {

		@Override
		public AttachmentType marshal(final IAttachmentType type) {
			return (AttachmentType) type;
		}

		@Override
		public IAttachmentType unmarshal(final AttachmentType type) {
			return type;
		}
	}

	public static final String TABLE = "ATTACHMENT_TYPE";

	public static final String JOIN_COLUMN = TABLE + "_ID";

	public static final String LABEL_COLUMN = "LABEL";

	public static final int LABEL_COLUMN_LENGTH = 64;

	@ManyToOne(
			fetch = FetchType.LAZY,
			optional = false,
			targetEntity = AttachmentNamespace.class)
	@JoinColumn(name = AttachmentNamespace.JOIN_COLUMN)
	@CheckForNull
	private IAttachmentNamespace namespace;

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

	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitAttachmentType(this);
		getNamespace().accept(visitor);
	}

	/** {@inheritDoc} */
	@XmlAttribute
	@Nullable
	public String getLabel() {
		return label;
	}

	/** {@inheritDoc} */
	@XmlAttribute(name = "attachmentNamespaceDocId")
	@XmlIDREF
	@Nullable
	public IAttachmentNamespace getNamespace() {
		return namespace;
	}

	/** {@inheritDoc} */
	public void setLabel(final String label) {
		checkNotNull(label);
		this.label = label;
	}

	/** {@inheritDoc} */
	public void setNamespace(final IAttachmentNamespace namespace) {
		checkNotNull(namespace);
		this.namespace = namespace;
	}
}
