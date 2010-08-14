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
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.google.common.base.Predicate;
import com.google.inject.assistedinject.Assisted;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.imodel.IAttachment;
import edu.upenn.cis.ppod.imodel.IPPodEntity;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A flexible container for data that can be attached to other pPOD attachees.
 * 
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = Attachment.TABLE)
public class Attachment extends UUPPodEntity implements IAttachment {

	public static class Adapter extends XmlAdapter<Attachment, IAttachment> {

		@Override
		public Attachment marshal(final IAttachment attachment) {
			return (Attachment) attachment;
		}

		@Override
		public IAttachment unmarshal(final Attachment attachment) {
			return attachment;
		}
	}

	/**
	 * Is an attachment of a the given's attachments namespace and type? And
	 * does it have the given attachment's label and string value?
	 */
	public static interface IIsOfNamepspaceTypeLabelAndStringValue
			extends Predicate<IAttachment> {

		/**
		 * Creates {@code IIsOfNamepspaceTypeLabelAndStringValue}s. For Guice.
		 */
		public static interface IFactory {

			/**
			 * Create an {@code IIsOfNamepspaceTypeLabelAndStringValue} that
			 * compares to the given attachment's label, string value, type
			 * label, and namespace label.
			 * 
			 * @param attachment the attachment to match against
			 * 
			 * @return a new {@code IIsOfNamepspaceTypeLabelAndStringValue}
			 */
			IIsOfNamepspaceTypeLabelAndStringValue create(
					@Assisted IAttachment attachment);
		}
	}

	static final String BYTES_VALUE_COLUMN = "BYTES_VALUE";

	public static final String TABLE = "ATTACHMENT";

	public static final String JOIN_COLUMN = TABLE + "_ID";

	public static final String STRING_VALUE_COLUMN = "STRING_VALUE";

	public static final String TYPE_COLUMN = "TYPE";

	/** Object to which this attachment is attached. */
	@CheckForNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false,
			targetEntity = PPodEntity.class)
	@JoinColumn(name = PPodEntity.JOIN_COLUMN)
	private IPPodEntity attachee;

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
			optional = false,
			targetEntity = AttachmentType.class)
	@JoinColumn(name = AttachmentType.JOIN_COLUMN)
	@CheckForNull
	private IAttachmentType type;

	/** Default constructor for (at least) Hibernate. */
	Attachment() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		visitor.visitAttachment(this);
		if (getType() != null) {
			getType().accept(visitor);
		}
		super.accept(visitor);
	}

	/**
	 * See {@link Unmarshaller}.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	public void afterUnmarshal(
			@CheckForNull final Unmarshaller u,
			final Object parent) {
		attachee = (IPPodEntity) parent;
	}

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
	 * Get the attachmentLabel.
	 * 
	 * @return the attachmentLabel
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
	public IAttachmentType getType() {
		return type;
	}

	/** {@inheritDoc} */
	public void setAttachee(@CheckForNull final IPPodEntity attachee) {
		this.attachee = attachee;
	}

	/**
	 * Set the byteValue.
	 * 
	 * @param bytesValue the byteValue to set
	 */
	public void setBytesValue(@CheckForNull final byte[] bytesValue) {
		if (Arrays.equals(bytesValue, this.bytesValue)) {

		} else {

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
			setInNeedOfNewVersion();
		}
	}

	@Override
	public void setInNeedOfNewVersion() {
		if (attachee != null) {
			attachee.setInNeedOfNewVersion();
		}
		super.setInNeedOfNewVersion();
	}

	/**
	 * Set the label.
	 * 
	 * @param label the label
	 */
	public void setLabel(@CheckForNull final String label) {
		if (equal(label, getLabel())) {

		} else {
			this.label = label;
			setInNeedOfNewVersion();
		}
	}

	/**
	 * Set the string value. Use {@code null} to indicate there is no string
	 * value.
	 * 
	 * @param stringValue the string value
	 */
	public void setStringValue(@CheckForNull final String stringValue) {
		if (equal(stringValue, getStringValue())) {

		} else {
			this.stringValue = stringValue;
			setInNeedOfNewVersion();
		}
	}

	/**
	 * Set the type of this attachment.
	 * 
	 * @param type the type
	 */
	public void setType(final IAttachmentType type) {
		checkNotNull(type);
		if (type.equals(getType())) {

		} else {
			this.type = type;
			setInNeedOfNewVersion();
		}
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
						"attachmentLabel=")
				.append(this.label).append(TAB)
				.append("attachmentStringValue=").append(
						this.stringValue).append(TAB).append("bytesValue=")
				.append(this.bytesValue).append(TAB).append(")");

		return retValue.toString();
	}

}
