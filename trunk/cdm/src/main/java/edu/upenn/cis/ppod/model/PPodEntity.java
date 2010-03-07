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

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.persistence.Column;
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
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

import edu.upenn.cis.ppod.modelinterfaces.IPPodEntity;
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

	static final String ID_COLUMN = TABLE + "_ID";

	/**
	 * The pPod-version of this object. Similar in concept to Hibernate's
	 * version, but tweaked for our purposes.
	 * 
	 * @see PPodVersionInfo
	 * @see PPodVersionInfoInterceptor
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = PPodVersionInfo.ID_COLUMN, nullable = false)
	@Nullable
	private PPodVersionInfo pPodVersionInfo;

	/**
	 * Does this object need to be assigned a new pPOD version at next save or
	 * update? This flag is really only needed for entities that were obtained
	 * from the database and not for transient objects - read on for the why.
	 * <p>
	 * We start with {@code false} because
	 * <ul>
	 * <li>that's appropriate for Db entities: when an object is modified, this
	 * value will be set to {@code true} and it will be set to the newest
	 * version number by whatever mechanism is in place</li>
	 * <li>for transient entities this flag is functionally irrelevant: the pPOD
	 * version info needs to set at time of creation, so this value is will be
	 * unset anyway in {@link #setPPodVersion(Long)} . Subsequent calls to the
	 * setters will then turn it to {@code true} needlessly.
	 * </ul>
	 */
	@Transient
	private boolean inNeedOfNewPPodVersionInfo = false;

	@Transient
	@Nullable
	private Long pPodVersion;

	@Transient
	boolean allowResetPPodVersionInfo = true;

	@ManyToMany
	@JoinTable(joinColumns = @JoinColumn(name = ID_COLUMN), inverseJoinColumns = @JoinColumn(name = Attachment.ID_COLUMN))
	@Nullable
	private Set<Attachment> attachments;

	@Column(name = "HAS_ATTACHMENTS", nullable = false)
	private Boolean hasAttachments = false;

	@Transient
	@Nullable
	private Set<Attachment> attachmentsXml;

	PPodEntity() {}

	@Inject
	PPodEntity(final PPodVersionInfo pPodVersionInfo) {
		// This will have a version of negative one and should block saves since
		// it is not saved.
		// It MUST be replaced for this object to be saved.
		setPPodVersion(pPodVersion);
	}

	@Override
	@OverridingMethodsMustInvokeSuper
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
	 * Take actions after unmarshalling that need to occur after
	 * {@link #afterUnmarshal(Unmarshaller, Object)} is called, specifically
	 * after {@code @XmlIDRef} elements are resolved.
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
	 * See {@link Marshaller}.
	 * 
	 * @param marshaler see {@code Marshaller}
	 * @return see {@code Marshaller}
	 */
	@OverridingMethodsMustInvokeSuper
	public boolean beforeMarshal(@Nullable final Marshaller marshaler) {
		// Write out the version number for the client of the xml
		if (pPodVersionInfo != null) {
			pPodVersion = pPodVersionInfo.getPPodVersion();
		}
		getAttachmentsXml().addAll(getAttachments());
		return true;
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

	/**
	 * Created for testing.
	 */
	Boolean getHasAttachments() {
		return hasAttachments;
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

	public boolean isInNeedOfNewPPodVersionInfo() {
		return inNeedOfNewPPodVersionInfo;
	}

	public boolean removeAttachment(final Attachment attachment) {
		Boolean attachmentRemoved;
		if (!hasAttachments) {
			attachmentRemoved = false;
		} else {
			attachmentRemoved = attachments.remove(attachment);
			if (attachmentRemoved) {
				resetPPodVersionInfo();
			}
			if (attachments.size() == 0) {
				hasAttachments = false;
			}
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
	@OverridingMethodsMustInvokeSuper
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
	 * Set the inNeedOfNewPPodVersionInfo. Intentionally package-private.
	 * 
	 * @param inNeedOfNewPPodVersionInfo the inNeedOfNewPPodVersionInfo to set
	 * 
	 * @return this
	 */
	PPodEntity setInNeedOfNewPPodVersionInfo() {
		this.inNeedOfNewPPodVersionInfo = true;
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
	 * Created for testing and Hibernate because this has access type
	 * "property".
	 * <p>
	 * NOTE: the weird name is on purpose so that Hibernate can identity it as
	 * the setter.
	 * 
	 * @param pPodVersionInfo new pPOD version
	 * 
	 * @return this
	 */
	public PPodEntity setPPodVersionInfo(final PPodVersionInfo pPodVersionInfo) {
		checkNotNull(pPodVersionInfo);
		unsetInNeedOfNewPPodVersionInfo();
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
		unsetAllowPersist();
		allowResetPPodVersionInfo = false;
		return this;
	}

	public PPodEntity unsetInNeedOfNewPPodVersionInfo() {
		inNeedOfNewPPodVersionInfo = false;
		return this;
	}

}
