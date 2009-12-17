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

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collections;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;

import org.hibernate.annotations.Cascade;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * A {@code PersistentObject} with pPOD version information and to which we can
 * add/remove attachments.
 * 
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = PPodEntity.TABLE)
@Inheritance(strategy = InheritanceType.JOINED)
abstract class PPodEntity extends PersistentObject implements IAttachee,
		IPPodVersioned {

	static final String TABLE = "PPOD_ENTITY";

	@XmlElement(name = "attachmentDocId")
	@XmlIDREF
	@ManyToMany
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@JoinTable(name = TABLE + "_" + Attachment.TABLE, joinColumns = { @JoinColumn(name = ID_COLUMN) }, inverseJoinColumns = { @JoinColumn(name = Attachment.ID_COLUMN) })
	private Set<Attachment> attachments;

	/**
	 * The pPod-version of this object. Similar in concept to Hibernate's
	 * version, but tweaked for our purposes.
	 * <p>
	 * Will be saved in {@link PPodVersionInfoInterceptor} and it's because its
	 * manipulated in there that we need to leave it eagerly fetched.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@JoinColumn(name = PPodVersionInfo.ID_COLUMN, nullable = false)
	private PPodVersionInfo pPodVersionInfo;

	@XmlAttribute
	@Transient
	private Long pPodVersion;

	@Transient
	private boolean suppressResetPPodVersionInfo = false;

	PPodEntity() {}

	public PPodEntity addAttachment(final Attachment attachment) {
		if (attachments == null) {
			attachments = newHashSet();
		}
		attachments.add(attachment);
		attachment.addAttachee(this);
		return this;
	}

	/**
	 * See {@link Marshaller}.
	 * 
	 * @param marshaler see {@code Marshaller}
	 * @return see {@code Marshaller}
	 */
	public boolean beforeMarshal(final Marshaller marshaler) {
		// Write out the version number for the client of the xml
		if (pPodVersionInfo != null) {
			pPodVersion = pPodVersionInfo.getPPodVersion();
		}
		return true;
	}

	public Set<Attachment> getAttachments() {
		if (attachments == null) {
			return Collections.emptySet();
		} else {
			return Collections.unmodifiableSet(attachments);
		}
	}

	public Set<Attachment> getAttachmentsByNamespace(final String namespace) {
		final Set<Attachment> attachmentsByNamespace = newHashSet();
		for (final Attachment attachment : getAttachments()) {
			if (namespace
					.equals(attachment.getType().getNamespace().getLabel())) {
				attachmentsByNamespace.add(attachment);
			}
		}
		return attachmentsByNamespace;
	}

	public Set<Attachment> getAttachmentsByNamespaceAndType(
			final String namespace, final String type) {
		return Sets.newHashSet(Iterables.filter(getAttachments(),
				new Predicate<Attachment>() {
					public boolean apply(final Attachment input) {
						return input.getType().getNamespace().getLabel()
								.equals(namespace)
								&& input.getType().getLabel().equals(type);
					}
				}));
	}

	public Long getPPodVersion() {
		if (pPodVersionInfo != null) {
			return pPodVersionInfo.getPPodVersion();
		}
		if (pPodVersion != null) {
			return pPodVersion;
		}
		return null;
	}

	public PPodVersionInfo getPPodVersionInfo() {
		return pPodVersionInfo;
	}

	public boolean getSuppressResetPPodVersionInfo() {
		return suppressResetPPodVersionInfo;
	}

	public boolean removeAttachment(final Attachment attachment) {
		return attachments.remove(attachment);
	}

	/**
	 * Mark this object's {@link PPodVersionInfo} for update to the next version
	 * number on save or update. This is done by setting it to {@code null}.
	 * <p>
	 * Implementors should also, if desired, call {@code resetPPodVersionInfo()}
	 * on any owning objects.
	 * 
	 * @return this {@code PPodEntity}
	 */
	protected PPodEntity resetPPodVersionInfo() {
		if (suppressResetPPodVersionInfo) {

		} else {
			pPodVersionInfo = null;
			pPodVersion = null;
		}
		return this;
	}

	PPodEntity setPPodVersionInfo(final PPodVersionInfo pPodVersionInfo) {
		this.pPodVersionInfo = pPodVersionInfo;
		return this;
	}

	public PPodEntity suppressResetPPodVersionInfo(final boolean suppress) {
		suppressResetPPodVersionInfo = suppress;
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

		retValue.append("PPodEntity(").append(super.toString()).append(TAB)
				.append("attachments=").append(this.attachments).append(TAB)
				.append("pPodVersionInfo=").append(this.pPodVersionInfo)
				.append(TAB).append("pPodVersion=").append(this.pPodVersion)
				.append(TAB).append(")");

		return retValue.toString();
	}

}
