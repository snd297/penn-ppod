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
	@XmlAttribute(name = "attachmentTypeDocId")
	@XmlIDREF
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = AttachmentType.ID_COLUMN, nullable = false)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	private AttachmentType type;

	/** Like a variable name. */
	@XmlAttribute
	@Column(name = "LABEL", nullable = true)
	private String label;

	// TODO: this is set to unique only to allow lookups of Character's mesquite
	// id's. See bug 128
	@XmlAttribute
	@Column(name = STRING_VALUE_COLUMN, nullable = true, unique = true)
	private String stringValue;

	@XmlElement
	@Lob
	@Column(name = BYTES_VALUE_COLUMN, nullable = true)
	private byte[] bytesValue;

	/** Objects to which this {@code Attachment} is attached. */
	@ManyToMany(mappedBy = "attachments")
	private final Set<PPodEntity> attachees = newHashSet();

	@XmlAttribute
	@XmlID
	@Transient
	@SuppressWarnings("unused")
	private final String docId = UUID.randomUUID().toString();

	/** Default constructor for (at least) Hibernate. */
	Attachment() {}

	@Override
	public Attachment accept(final IVisitor visitor) {
		visitor.visit(this);
		for (final Attachment attachment : getAttachments()) {
			attachment.accept(visitor);
		}
		return this;
	}

	PPodEntity addAttachee(final PPodEntity attachee) {
		attachees.add(attachee);
		return attachee;
	}

	/**
	 * Get the byteArrayValue.
	 * 
	 * @return the byteArrayValue
	 */
	public byte[] getBytesValue() {
		return bytesValue;
	}

	/**
	 * Get the label.
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Get the value.
	 * 
	 * @return the value.
	 */
	public String getStringValue() {
		return stringValue;
	}

	/**
	 * Get the type of this attachment.
	 * 
	 * @return the type of this attachment
	 */
	public AttachmentType getType() {
		return type;
	}

	@Override
	protected Attachment resetPPodVersionInfo() {
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
	 * @param byteArrayValue the byteArrayValue to set
	 * 
	 * @return this
	 */
	public Attachment setByteArrayValue(final byte[] byteArrayValue) {
		checkNotNull(byteArrayValue);
		this.bytesValue = byteArrayValue;
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
	 * @param stringValue the value
	 * @return this {@code Attachment}
	 */
	public Attachment setStringValue(final String stringValue) {
		checkNotNull(stringValue);
		if (stringValue.equals(this.stringValue)) {

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

}
