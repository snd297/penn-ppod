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
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * A {@code PersistentObject} with pPOD version information and to which we can
 * add/remove attachments.
 * 
 * <pre>
 * Caused by: java.lang.IllegalAccessException: Class org.hibernate.proxy.pojo.javassist.JavassistLazyInitializer can not access a member of class edu.upenn.cis.ppod.model.PPodEntity with modifiers "public"
 *  	at sun.reflect.Reflection.ensureMemberAccess(Reflection.java:65)
 *  	at java.lang.reflect.Method.invoke(Method.java:588)
 *  	at org.hibernate.proxy.pojo.javassist.JavassistLazyInitializer.invoke(JavassistLazyInitializer.java:197)
 *  	at edu.upenn.cis.ppod.model.CharacterState_$$_javassist_0.beforeMarshal(CharacterState_$$_javassist_0.java)
 * </pre>
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = PPodEntity.TABLE)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class PPodEntity extends PersistentObject implements
		IPPodEntity {

	static final String TABLE = "PPOD_ENTITY";

	// @Transient
	// @ManyToMany
	// @JoinTable(inverseJoinColumns = { @JoinColumn(name =
	// Attachment.ID_COLUMN) })
// private Set<Attachment> attachments = newHashSet();
	/**
	 * The pPod-version of this object. Similar in concept to Hibernate's
	 * version, but tweaked for our purposes.
	 * <p>
	 * Will be saved in {@link PPodVersionInfoInterceptor} and it's because its
	 * manipulated in there that we need to leave it eagerly fetched.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = PPodVersionInfo.ID_COLUMN, nullable = false)
	private PPodVersionInfo pPodVersionInfo;

	@Transient
	private Long pPodVersion;

	@Transient
	boolean allowPersist = true;

	@Transient
	boolean allowResetPPodVersionInfo = true;

	public PPodEntity setAllowResetPPodVersionInfo(
			final boolean allowResetPPodVersionInfo) {
		this.allowResetPPodVersionInfo = allowResetPPodVersionInfo;
		return this;
	}

	public boolean getAllowResetPPodVersionInfo() {
		return allowResetPPodVersionInfo;
	}

	PPodEntity() {}

// public IPPodEntity addAttachment(final Attachment attachment) {
// if (attachments == null) {
// attachments = newHashSet();
// }
// attachments.add(attachment);
// attachment.addAttachee(this);
// return this;
// }

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	public void afterUnmarshal(final Unmarshaller u, final Object parent) {
		unsetAllowPersistAndResetPPodVersionInfo();
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

	public boolean getAllowPersist() {
		return allowPersist;
	}

// public Set<Attachment> getAttachments() {
// if (attachments == null) {
// return Collections.emptySet();
// } else {
// return Collections.unmodifiableSet(attachments);
// }
// }

// public Set<Attachment> getAttachmentsByNamespace(final String namespace) {
// final Set<Attachment> attachmentsByNamespace = newHashSet();
// for (final Attachment attachment : getAttachments()) {
// if (namespace
// .equals(attachment.getType().getNamespace().getLabel())) {
// attachmentsByNamespace.add(attachment);
// }
// }
// return attachmentsByNamespace;
// }
//
// public Set<Attachment> getAttachmentsByNamespaceAndType(
// final String namespace, final String type) {
// return Sets.newHashSet(Iterables.filter(getAttachments(),
// new Predicate<Attachment>() {
// public boolean apply(final Attachment input) {
// return input.getType().getNamespace().getLabel()
// .equals(namespace)
// && input.getType().getLabel().equals(type);
// }
// }));
// }

	/**
	 * Created for JAXB.
	 */
// @XmlElement(name = "attachmentDocId")
// @XmlIDREF
// @SuppressWarnings("unused")
// private Set<Attachment> getAttachmentsMutable() {
// if (attachments == null) {
// attachments = newHashSet();
// }
// return attachments;
// }

	@XmlAttribute
	public Long getPPodVersion() {
		if (pPodVersionInfo != null) {
			return pPodVersionInfo.getPPodVersion();
		}
		return pPodVersion;
	}

	public PPodVersionInfo getPPodVersionInfo() {
		return pPodVersionInfo;
	}

	// public boolean removeAttachment(final Attachment attachment) {
	// return attachments.remove(attachment);
	// }

	/**
	 * Mark this object's {@link PPodVersionInfo} for update to the next version
	 * number on save or update. This is done by setting it to {@code null}.
	 * <p>
	 * Implementors should also, if desired, call {@code resetPPodVersionInfo()}
	 * on any owning objects.
	 * <p>
	 * If {@code setSuppressResetPPodVersion(true)} has been called, and not
	 * undone, this method returns without doing anything.
	 * 
	 * @return this {@code PPodEntity}
	 */
	public PPodEntity resetPPodVersionInfo() {
		if (getAllowResetPPodVersionInfo()) {
			pPodVersionInfo = null;
			pPodVersion = null;
		}
		return this;
	}

	/**
	 * Created for JAXB.
	 * 
	 * @param pPodVersion the pPOD version number
	 * 
	 * @return this
	 */
	public IPPodEntity setPPodVersion(final Long pPodVersion) {
		this.pPodVersion = pPodVersion;
		return this;
	}

	IPPodEntity setPPodVersionInfo(final PPodVersionInfo pPodVersionInfo) {
		this.pPodVersionInfo = pPodVersionInfo;
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
				.append("pPodVersionInfo=").append(this.pPodVersionInfo)
				.append(TAB).append("pPodVersion=").append(this.pPodVersion)
				.append(TAB).append(")");

		return retValue.toString();
	}

	public PersistentObject unsetAllowPersistAndResetPPodVersionInfo() {
		allowPersist = false;
		allowResetPPodVersionInfo = false;
		return this;
	}

}
