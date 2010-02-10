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
import static com.google.common.collect.Sets.newHashSet;
import static edu.upenn.cis.ppod.util.UPennCisPPodUtil.nullSafeEquals;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

import org.hibernate.annotations.Cascade;

import com.google.common.base.Function;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A flexible container for data that can be attached to other pPOD attachees.
 * <p>
 * This class has a non-accessible {@code @XmlId} (only used by the marshaller
 * and unmarshaller) and so doesn't extend from {@code UUPPodEntityWXmlId}.
 * 
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = Attachment.TABLE)
public final class Attachment extends UUPPodEntity {

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

	/**
	 * Return the attachments in this attachment set that are of type {@code
	 * type}.
	 * 
	 * @param attachments in which to look for the attachments
	 * @param type the type of attachment we want
	 * @return the attachments in this attachment set that are of type {@code
	 *         type}
	 */
	public static Set<Attachment> getAttachmentsByType(
			final Set<Attachment> attachments, final String type) {
		final Set<Attachment> returnAttachments = newHashSet();
		for (final Attachment attachment : attachments) {
			if (type.equals(attachment.getType().getLabel())) {
				returnAttachments.add(attachment);
			}
		}
		return returnAttachments;
	}

	/** Like a variable type. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = AttachmentType.ID_COLUMN, nullable = false)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	private AttachmentType type;

	/** Like a variable name. */
	@Column(name = "LABEL", nullable = true)
	private String label;

	// TODO: this is set to unique only to allow lookups of Character's mesquite
	// id's. See bug Bugzilla 128
	@Column(name = STRING_VALUE_COLUMN, nullable = true, unique = true)
	private String stringValue;

	@Lob
	@Column(name = BYTES_VALUE_COLUMN, nullable = true)
	private byte[] bytesValue;

	/** Objects to which this {@code Attachment} is attached. */
	@ManyToMany(mappedBy = "attachments")
	private final Set<PPodEntity> attachees = newHashSet();

	@XmlAttribute
	@XmlID
	@Transient
	private final String docId = UUID.randomUUID().toString();

	/** Default constructor for (at least) Hibernate. */
	Attachment() {}

	@Override
	public Attachment accept(final IVisitor visitor) {
		visitor.visit(this);
		super.accept(visitor);
		return this;
	}

	IPPodEntity addAttachee(final PPodEntity attachee) {
		attachees.add(attachee);
		return attachee;
	}

	/**
	 * Get the byteArrayValue.
	 * 
	 * @return the byteArrayValue
	 */
	@XmlElement
	public byte[] getBytesValue() {
		return bytesValue;
	}

	/**
	 * Get the label.
	 * 
	 * @return the label
	 */
	@XmlAttribute
	public String getLabel() {
		return label;
	}

	/**
	 * Get the value.
	 * 
	 * @return the value.
	 */
	@XmlAttribute
	public String getStringValue() {
		return stringValue;
	}

	/**
	 * Get the type of this attachment.
	 * 
	 * @return the type of this attachment
	 */
	@XmlAttribute(name = "attachmentTypeDocId")
	@XmlIDREF
	public AttachmentType getType() {
		return type;
	}

	@Override
	public Attachment resetPPodVersionInfo() {
		if (getPPodVersionInfo() == null) {
			// Then it's already been reset
		} else {
			for (final PPodEntity attachee : attachees) {
				attachee.resetPPodVersionInfo();
			}
			super.resetPPodVersionInfo();
		}
		return this;
	}

	/**
	 * Set the byteArrayValue.
	 * 
	 * @param bytesValue the byteArrayValue to set. Nullable.
	 * 
	 * @return this
	 */
	public Attachment setByteArrayValue(final byte[] bytesValue) {
		if (Arrays.equals(bytesValue, this.bytesValue)) {
			return this;
		}
		if (this.bytesValue == null
				|| this.bytesValue.length != bytesValue.length) {
			this.bytesValue = new byte[bytesValue.length];
		}
		System.arraycopy(this.bytesValue, 0, bytesValue, 0,
				this.bytesValue.length);
		resetPPodVersionInfo();
		return this;
	}

	/**
	 * Set the label.
	 * 
	 * @param label the label to set
	 * 
	 * @return this
	 */
	public Attachment setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(this.label)) {

		} else {
			this.label = label;
			resetPPodVersionInfo();
		}
		return this;
	}

	/**
	 * Set the value.
	 * 
	 * @param stringValue the value. Nullable.
	 * @return this {@code Attachment}
	 */
	public Attachment setStringValue(final String stringValue) {
		if (nullSafeEquals(stringValue, this.stringValue)) {

		} else {
			this.stringValue = stringValue;
			resetPPodVersionInfo();
		}
		return this;
	}

	/**
	 * Set the type of this attachment.
	 * 
	 * @param type the type
	 * @return this attachment
	 */
	public Attachment setType(final AttachmentType type) {
		checkNotNull(type);
		if (type.equals(getType())) {

		} else {
			this.type = type;
			resetPPodVersionInfo();
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
	public String toString() {
		final String TAB = "";

		final StringBuilder retValue = new StringBuilder();

		retValue.append("Attachment(").append(super.toString()).append(TAB)
				.append("type=").append(this.type).append(TAB).append("label=")
				.append(this.label).append(TAB).append("stringValue=").append(
						this.stringValue).append(TAB).append("bytesValue=")
				.append(this.bytesValue).append(TAB).append("docId=").append(
						this.docId).append(TAB).append(")");

		return retValue.toString();
	}

}
