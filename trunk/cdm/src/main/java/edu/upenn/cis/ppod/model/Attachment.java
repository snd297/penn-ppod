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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.common.base.Predicate;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.imodel.IPPodEntity;

/**
 * A flexible container for data that can be attached to other pPOD attachees.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = Attachment.TABLE)
public class Attachment extends UuPPodEntity {

	final static class IsOfNamespace implements Predicate<Attachment> {

		private final String namespaceLabel;

		/**
		 * @param namespaceLabel is the typeLabel of this namespaceLabel?
		 */
		public IsOfNamespace(final String namespaceLabel) {
			checkNotNull(namespaceLabel);
			this.namespaceLabel = namespaceLabel;
		}

		public boolean apply(final Attachment input) {
			checkNotNull(input);
			return namespaceLabel.equals(input.getType().getNamespace()
						.getLabel());
		}

	}

	/**
	 * Is an attachment of a particular {@link AttachmentNamespace} and
	 * {@link AttachmentType}?
	 */
	public final static class IsOfNamespaceAndType
			implements Predicate<Attachment> {

		private final String namespaceLabel;

		private final String typeLabel;

		/**
		 * @param typeLabel is the attachment of this typeLabel?
		 * @param namespaceLabel is the typeLabel of this namespaceLabel?
		 */
		public IsOfNamespaceAndType(final String namespaceLabel,
				final String typeLabel) {
			checkNotNull(namespaceLabel);
			checkNotNull(typeLabel);
			this.typeLabel = typeLabel;
			this.namespaceLabel = namespaceLabel;
		}

		public boolean apply(final Attachment input) {
			checkNotNull(input);
			return namespaceLabel.equals(input.getType().getNamespace()
					.getLabel())
					&& typeLabel.equals(input.getType().getLabel());
		}

	}

	public final static class IsOfNamespaceTypeLabelAndStringValue implements
			Predicate<Attachment> {

		private final String attachmentLabel;
		private final String attachmentStringValue;
		private final String namespaceLabel;
		private final String typeLabel;

		public IsOfNamespaceTypeLabelAndStringValue(final Attachment attachment) {
			checkNotNull(attachment);
			checkArgument(attachment.getType() != null,
						"attachment.getType() == null");

			checkArgument(attachment.getType().getNamespace() != null,
						"attachment.getType().getNamespace() == null");

			checkArgument(
						attachment.getType().getNamespace().getLabel() != null,
						"attachment's typeLabel's namespaceLabel has null attachmentLabel");

			checkArgument(attachment.getType().getLabel() != null,
						"attachment's typeLabel has null attachmentLabel");

			final String attachmentLabel = attachment.getLabel();
			checkArgument(attachmentLabel != null,
						"attachment.getLabel() == null");

			final String attachmentStringValue = attachment.getStringValue();

			checkArgument(attachmentStringValue != null,
						"attachment.getStringValue() == null");

			this.namespaceLabel = attachment.getType().getNamespace()
						.getLabel();
			this.typeLabel = attachment.getType().getLabel();
			this.attachmentLabel = attachmentLabel;
			this.attachmentStringValue = attachmentStringValue;
		}

		public boolean apply(final Attachment input) {
			return namespaceLabel.equals(input.getType().getNamespace()
						.getLabel())
						&& typeLabel.equals(input.getType().getLabel())
						&& attachmentLabel.equals(input.getLabel())
						&& attachmentStringValue.equals(input.getStringValue());
		}
	}

	static final String BYTES_VALUE_COLUMN = "BYTES_VALUE";

	public static final String TABLE = "ATTACHMENT";

	public static final String JOIN_COLUMN = TABLE + "_ID";

	public static final String STRING_VALUE_COLUMN = "STRING_VALUE";

	public static final String TYPE_COLUMN = "TYPE";

	/** Object to which this attachment is attached. */
	@CheckForNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = PPodEntity.JOIN_COLUMN)
	private PPodEntity attachee;

	@CheckForNull
	@Lob
	@Column(name = BYTES_VALUE_COLUMN, nullable = true)
	private byte[] bytesValue;

	/** Like a variable name. */
	@Column(name = "LABEL", nullable = true)
	@CheckForNull
	private String label;

	@Column(name = STRING_VALUE_COLUMN, nullable = true)
	@CheckForNull
	private String stringValue;

	/** Like a variable typeLabel. */
	@ManyToOne(
			fetch = FetchType.LAZY,
			optional = false)
	@JoinColumn(name = AttachmentType.JOIN_COLUMN)
	@CheckForNull
	private AttachmentType type;

	/** Default constructor for (at least) Hibernate. */
	public Attachment() {}

	/**
	 * Get the entities that have this has an attachment.
	 * <p>
	 * Will be {@code null} for newly create attachments, will never be
	 * {@code null} for persistent attachments.
	 * 
	 * @return the entities that have this has an attachment
	 */
	@Nullable
	public IPPodEntity getAttachee() {
		return attachee;
	}

	/**
	 * Get a copy of the byteArrayValue.
	 * 
	 * @return a copy of the byteArrayValue
	 */
	@Nullable
	public byte[] getBytesValue() {
		if (bytesValue == null) {
			return null;
		}
		final byte[] bytesValueCopy = new byte[bytesValue.length];
		System.arraycopy(bytesValue, 0, bytesValueCopy, 0, bytesValue.length);
		return bytesValueCopy;
	}

	/**
	 * Get the attachmentLabel.
	 * 
	 * @return the attachmentLabel
	 */
	@Nullable
	public String getLabel() {
		return label;
	}

	/**
	 * Get the value.
	 * 
	 * @return the value.
	 */
	@Nullable
	public String getStringValue() {
		return stringValue;
	}

	/**
	 * Get the typeLabel of this attachment.
	 * 
	 * @return the typeLabel of this attachment
	 */
	@Nullable
	public AttachmentType getType() {
		return type;
	}

	/** {@inheritDoc} */
	public void setAttachee(@CheckForNull final PPodEntity attachee) {
		this.attachee = attachee;
	}

	/**
	 * Set the byteValue.
	 * 
	 * @param bytesValue the byteValue to set
	 */
	public void setBytesValue(@CheckForNull final byte[] bytesValue) {
		this.bytesValue = bytesValue;
	}

	/**
	 * Set the label.
	 * 
	 * @param label the label
	 */
	public void setLabel(@CheckForNull final String label) {
		this.label = label;
	}

	/**
	 * Set the string value. Use {@code null} to indicate there is no string
	 * value.
	 * 
	 * @param stringValue the string value
	 */
	public void setStringValue(@CheckForNull final String stringValue) {
		this.stringValue = stringValue;
	}

	/**
	 * Set the type of this attachment.
	 * 
	 * @param type the type
	 */
	public void setType(final AttachmentType type) {
		this.type = checkNotNull(type);
	}

}
