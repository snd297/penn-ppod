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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Arrays;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;

import org.hibernate.annotations.Cascade;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A flexible container for data that can be attached to other pPOD attachees.
 * 
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = Attachment.TABLE)
public class Attachment extends UUPPodEntityWXmlId {

	/**
	 * Is an attachment of a particular {@link AttachmentNamespace}?
	 */
	@ImplementedBy(IsOfNamespace.class)
	public static interface IIsOfNamespace extends Predicate<Attachment> {
		static interface IFactory {
			IIsOfNamespace create(String namespace);
		}
	}

	final static class IsOfNamespace implements IIsOfNamespace {

		private final String namespaceLabel;

		/**
		 * @param namespaceLabel is the typeLabel of this namespaceLabel?
		 */
		@Inject
		IsOfNamespace(@Assisted final String namespaceLabel) {
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
	public final static class IsOfNamespaceAndType implements
			Predicate<Attachment> {

		private final String namespaceLabel;

		private final String typeLabel;

		/**
		 * @param typeLabel is the attachment of this typeLabel?
		 * @param namespaceLabel is the typeLabel of this namespaceLabel?
		 */
		IsOfNamespaceAndType(final String namespaceLabel, final String typeLabel) {
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

	public static interface IIsOfNamepspaceTypeLabelAndStringValue extends
			Predicate<Attachment> {

	}

	public final static class IsOfNamespaceTypeLabelAndStringValue implements
			IIsOfNamepspaceTypeLabelAndStringValue {

		private final String namespace;
		private final String type;
		private final String label;
		private final String stringValue;

		IsOfNamespaceTypeLabelAndStringValue(final Attachment attachment) {
			checkNotNull(attachment);

			checkArgument(attachment.getType() != null,
					"attachment.getType() == null");

			checkArgument(attachment.getType().getNamespace() != null,
					"attachment.getType().getNamespace() == null");

			checkArgument(
					attachment.getType().getNamespace().getLabel() != null,
					"attachment's typeLabel's namespaceLabel has null label");

			checkArgument(attachment.getType().getLabel() != null,
					"attachment's typeLabel has null label");

			final String attachmentLabel = attachment.getLabel();
			checkArgument(attachmentLabel != null,
					"attachment.getLabel() == null");

			final String attachmentStringValue = attachment.getStringValue();

			checkArgument(attachmentStringValue != null,
					"attachment.getStringValue() == null");

			this.namespace = attachment.getType().getNamespace().getLabel();
			this.type = attachment.getType().getLabel();
			this.label = attachmentLabel;
			this.stringValue = attachmentStringValue;
		}

		public boolean apply(final Attachment input) {
			return namespace.equals(input.getType().getNamespace().getLabel())
					&& type.equals(input.getType().getLabel())
					&& label.equals(input.getLabel())
					&& stringValue.equals(input.getStringValue());
		}
	}

	static final String TABLE = "ATTACHMENT";

	static final String ID_COLUMN = TABLE + "_ID";

	static final String TYPE_COLUMN = "TYPE";

	static final String STRING_VALUE_COLUMN = "STRING_VALUE";

	static final String BYTES_VALUE_COLUMN = "BYTES_VALUE";

	/**
	 * {@link Function} wrapper of {@link #getStringValue()}.
	 */
	public static final Function<Attachment, String> getStringValue = new Function<Attachment, String>() {

		public String apply(final Attachment from) {
			return from.getStringValue();
		}
	};

	/** Like a variable typeLabel. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = AttachmentType.ID_COLUMN, nullable = false)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@CheckForNull
	private AttachmentType type;

	/** Like a variable name. */
	@Column(name = "LABEL", nullable = true)
	@CheckForNull
	private String label;

	// TODO: this is set to unique only to allow lookups of Character's mesquite
	// id's. See bug Bugzilla 128
	@Column(name = STRING_VALUE_COLUMN, nullable = true, unique = true)
	@CheckForNull
	private String stringValue;

	@Lob
	@Column(name = BYTES_VALUE_COLUMN, nullable = true)
	@CheckForNull
	private byte[] bytesValue;

	/** Objects to which this {@code Attachment} is attached. */
	@ManyToMany(mappedBy = "attachments")
	private final Set<PPodEntity> attachees = newHashSet();

	/** Default constructor for (at least) Hibernate. */
	Attachment() {}

	@Override
	public void accept(final IVisitor visitor) {
		visitor.visit(this);
		getType().accept(visitor);
		super.accept(visitor);
	}

	/**
	 * Add an item to which this is attached.
	 * 
	 * @param attachee to which this is attached
	 * 
	 * @return {@code attachee}
	 */
	protected PPodEntity addAttachee(final PPodEntity attachee) {
		checkNotNull(attachee);
		attachees.add(attachee);
		return attachee;
	}

	/**
	 * Get a copy of the byteArrayValue.
	 * 
	 * @return a copy of the byteArrayValue
	 */
	@XmlElement
	@CheckForNull
	public byte[] getBytesValue() {
		if (bytesValue == null) {
			return null;
		}
		final byte[] bytesValueCopy = new byte[bytesValue.length];
		System.arraycopy(bytesValue, 0, bytesValueCopy, 0, bytesValue.length);
		return bytesValueCopy;
	}

	/**
	 * Get the label.
	 * 
	 * @return the label
	 */
	@XmlAttribute
	@CheckForNull
	public String getLabel() {
		return label;
	}

	/**
	 * Get the value.
	 * 
	 * @return the value.
	 */
	@XmlAttribute
	@CheckForNull
	public String getStringValue() {
		return stringValue;
	}

	/**
	 * Get the typeLabel of this attachment.
	 * 
	 * @return the typeLabel of this attachment
	 */
	@XmlAttribute(name = "attachmentTypeDocId")
	@XmlIDREF
	@Nullable
	public AttachmentType getType() {
		return type;
	}

	/**
	 * Set the byteValue.
	 * 
	 * @param bytesValue the byteValue to set
	 * 
	 * @return this
	 */
	public Attachment setBytesValue(@CheckForNull final byte[] bytesValue) {
		if (Arrays.equals(bytesValue, this.bytesValue)) {
			return this;
		}

		if (bytesValue == null) {
			this.bytesValue = null;
		} else {
			if (this.bytesValue == null
					|| this.bytesValue.length != bytesValue.length) {
				this.bytesValue = new byte[bytesValue.length];
			}
			System.arraycopy(this.bytesValue, 0, bytesValue, 0,
					this.bytesValue.length);
		}
		setInNeedOfNewPPodVersionInfo();
		return this;
	}

	@Override
	public Attachment setInNeedOfNewPPodVersionInfo() {
		for (final PPodEntity attachee : attachees) {
			attachee.setInNeedOfNewPPodVersionInfo();
		}
		super.setInNeedOfNewPPodVersionInfo();

		return this;
	}

	/**
	 * Set the label.
	 * 
	 * @param label the label to set
	 * 
	 * @return this
	 */
	public Attachment setLabel(@CheckForNull final String label) {
		if (equal(label, getLabel())) {

		} else {
			this.label = label;
			setInNeedOfNewPPodVersionInfo();
		}
		return this;
	}

	/**
	 * Set the value.
	 * 
	 * @param stringValue the value
	 * 
	 * @return this {@code Attachment}
	 */
	public Attachment setStringValue(@CheckForNull final String stringValue) {
		if (equal(stringValue, getStringValue())) {

		} else {
			this.stringValue = stringValue;
			setInNeedOfNewPPodVersionInfo();
		}
		return this;
	}

	/**
	 * Set the typeLabel of this attachment.
	 * 
	 * @param typeLabel the typeLabel
	 * @return this attachment
	 */
	public Attachment setType(final AttachmentType type) {
		checkNotNull(type);
		if (type.equals(getType())) {

		} else {
			this.type = type;
			setInNeedOfNewPPodVersionInfo();
		}
		return this;
	}

	/**
	 * Constructs a <code>String</code> with all attributes in name = value
	 * format.
	 * 
	 * @return a <code>String</code> representation of this object.
	 */
	@Override
	@edu.umd.cs.findbugs.annotations.SuppressWarnings
	public String toString() {
		final String TAB = "";

		final StringBuilder retValue = new StringBuilder();

		retValue.append("Attachment(").append(super.toString()).append(TAB)
				.append("typeLabel=").append(this.type).append(TAB).append(
						"label=")
				.append(this.label).append(TAB).append("stringValue=").append(
						this.stringValue).append(TAB).append("bytesValue=")
				.append(this.bytesValue).append(TAB).append(")");

		return retValue.toString();
	}

}
