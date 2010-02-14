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

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.persistence.Column;
import javax.persistence.Entity;
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

import org.hibernate.annotations.AccessType;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import edu.upenn.cis.ppod.util.IVisitor;

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

	/**
	 * The pPod-version of this object. Similar in concept to Hibernate's
	 * version, but tweaked for our purposes.
	 * 
	 * @see PPodVersionInfo
	 * @see PPodVersionInfoInterceptor
	 */
	@AccessType("property")
	@ManyToOne
	@JoinColumn(name = PPodVersionInfo.ID_COLUMN, nullable = false)
	@Nullable
	private PPodVersionInfo pPodVersionInfo;

	/** Does this object need to be assigned a new pPOD version? */
	@Transient
	private boolean inNeedOfNewPPodVersionInfo = true;

	@Transient
	@Nullable
	private Long pPodVersion;

	@Transient
	boolean allowResetPPodVersionInfo = true;

	@ManyToMany
	@JoinTable(inverseJoinColumns = { @JoinColumn(name = Attachment.ID_COLUMN) })
	@Nullable
	private Set<Attachment> attachments;

	@Column(name = "HAS_ATTACHMENTS", nullable = false)
	private Boolean hasAttachments = false;

	/**
	 * Created for testing.
	 */
	Boolean getHasAttachments() {
		return hasAttachments;
	}

	@Transient
	@Nullable
	private Set<Attachment> attachmentsXml;

	PPodEntity() {}

	@Override
	public PPodEntity accept(final IVisitor visitor) {
		for (final Attachment attachment : getAttachments()) {
			attachment.accept(visitor);
		}
		return this;
	}

	public IPPodEntity addAttachment(final Attachment attachment) {
		if (attachments == null) {
			attachments = newHashSet();
		}
		if (attachments.add(attachment)) {
			resetPPodVersionInfo();
		}
		hasAttachments = true;
		attachment.addAttachee(this);
		return this;
	}

	/**
	 * Take care of any after-unmarshal work that needs to be done after
	 * xmlidref's are resolved.
	 */
	@OverridingMethodsMustInvokeSuper
	public void afterUnmarshal() {
		if (attachmentsXml != null) {
			for (final Attachment attachment : getAttachmentsXml()) {
				addAttachment(attachment);
			}
			attachmentsXml = null;
		}
	}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	@OverridingMethodsMustInvokeSuper
	public void beforeUnmarshal(final Unmarshaller u, final Object parent) {
		unsetAllowPersistAndResetPPodVersionInfo();
	}

	/**
	 * See {@link Marshaller}.
	 * 
	 * @param marshaler see {@code Marshaller}
	 * @return see {@code Marshaller}
	 */
	@OverridingMethodsMustInvokeSuper
	public boolean beforeMarshal(final Marshaller marshaler) {
		// Write out the version number for the client of the xml
		if (pPodVersionInfo != null) {
			pPodVersion = pPodVersionInfo.getPPodVersion();
		}
		getAttachmentsXml().addAll(getAttachments());
		return true;
	}

	public boolean getAllowResetPPodVersionInfo() {
		return allowResetPPodVersionInfo;
	}

	public Set<Attachment> getAttachments() {
		if (hasAttachments) {
			return Collections.unmodifiableSet(attachments);
		}
		return Collections.emptySet();
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
				new Attachment.IsOfNamespaceAndType(type, namespace)));
	}

	@XmlElement(name = "attachmentDocId")
	@XmlIDREF
	private Set<Attachment> getAttachmentsXml() {
		if (attachmentsXml == null) {
			attachmentsXml = newHashSet();
		}
		return attachmentsXml;
	}

	@XmlAttribute
	public Long getPPodVersion() {
		if (pPodVersionInfo != null) {
			return pPodVersionInfo.getPPodVersion();
		}
		return pPodVersion;
	}

	public PPodVersionInfo getpPodVersionInfo() {
		return pPodVersionInfo;
	}

	public boolean removeAttachment(final Attachment attachment) {
		final boolean attachmentRemoved = attachments.remove(attachment);
		if (attachmentRemoved) {
			resetPPodVersionInfo();
		}
		if (attachments.size() == 0) {
			hasAttachments = false;
		}
		return attachmentRemoved;
	}

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
			inNeedOfNewPPodVersionInfo = true;
		}
		return this;
	}

	public PPodEntity setAllowResetPPodVersionInfo(
			final boolean allowResetPPodVersionInfo) {
		this.allowResetPPodVersionInfo = allowResetPPodVersionInfo;
		return this;
	}

	/**
	 * Set the pPOD version number.
	 * 
	 * @param pPodVersion the pPOD version number
	 * 
	 * @return this
	 */
	public IPPodEntity setPPodVersion(final Long pPodVersion) {
		this.pPodVersion = pPodVersion;
		return this;
	}

	/**
	 * Created for testing an Hibernate because this has access type "property".
	 * <p>
	 * NOTE: the weird name is on purpose so that Hibernate can identity it as
	 * the setter.
	 * 
	 * @param pPodVersionInfo new pPOD version
	 * 
	 * @return this
	 */
	PersistentObject setpPodVersionInfo(final PPodVersionInfo pPodVersionInfo) {
		this.pPodVersionInfo = pPodVersionInfo;
		unsetInNeedOfNewPPodVersionInfo();
		return this;
	}

	public PPodEntity unsetInNeedOfNewPPodVersionInfo() {
		inNeedOfNewPPodVersionInfo = false;
		return this;
	}

	public boolean isInNeedOfNewPPodVersionInfo() {
		return inNeedOfNewPPodVersionInfo;
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

		retValue.append("PPodEntity(").append(super.toString())/*
																 * .append(TAB)
																 * .append(
																 * "attachments="
																 * )
																 * .append(this.
																 * attachments)
																 */.append(TAB)
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
