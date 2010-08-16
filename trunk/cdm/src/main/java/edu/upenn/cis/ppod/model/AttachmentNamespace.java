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
import javax.xml.bind.annotation.adapters.XmlAdapter;

import edu.upenn.cis.ppod.imodel.IAttachmentNamespace;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * To prevent name clashes among {@link AttachmentType}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = AttachmentNamespace.TABLE)
public class AttachmentNamespace
		extends PersistentObjectWithDocId
		implements IAttachmentNamespace {

	public static class Adapter extends
			XmlAdapter<AttachmentNamespace, IAttachmentNamespace> {

		@Override
		public AttachmentNamespace marshal(
				final IAttachmentNamespace namespace) {
			return (AttachmentNamespace) namespace;
		}

		@Override
		public IAttachmentNamespace unmarshal(
				final AttachmentNamespace namespace) {
			return namespace;
		}
	}

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

	AttachmentNamespace() {}

	public void accept(final IVisitor visitor) {
		visitor.visitAttachmentNamespace(this);
	}

	/** {@inheritDoc} */
	@XmlAttribute
	@Nullable
	public String getLabel() {
		return label;
	}

	/** {@inheritDoc} */
	public void setLabel(final String label) {
		checkNotNull(label);
		this.label = label;
	}
}
