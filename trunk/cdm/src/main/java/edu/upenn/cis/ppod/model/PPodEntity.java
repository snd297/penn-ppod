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
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.CheckForNull;
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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;

import com.google.common.collect.Iterables;

import edu.upenn.cis.ppod.modelinterfaces.IAttachee;
import edu.upenn.cis.ppod.modelinterfaces.IPPodVersioned;
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
public abstract class PPodEntity extends PersistentObject implements IAttachee,
			IPPodVersioned {

	static final String TABLE = "PPOD_ENTITY";

	/**
	 * The pPod-version of this object. Similar in concept to Hibernate's
	 * version, but tweaked for our purposes.
	 * 
	 * @see PPodVersionInfo
	 * @see PPodVersionInfoInterceptor
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = PPodVersionInfo.ID_COLUMN, nullable = false)
	@CheckForNull
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
	 * version info needs to be set at time of creation, so this value is will
	 * be unset anyway in {@link #setPPodVersionInfo} . Subsequent calls to the
	 * setters will then turn it to {@code true} needlessly.
	 * </ul>
	 */
	@Transient
	private boolean inNeedOfNewPPodVersionInfo = false;

	@Transient
	@CheckForNull
	private Long pPodVersion;

	@ManyToMany
	@JoinTable(joinColumns = @JoinColumn(name = ID_COLUMN), inverseJoinColumns = @JoinColumn(name = Attachment.JOIN_COLUMN))
	@CheckForNull
	private Set<Attachment> attachments;

	@Column(name = "HAS_ATTACHMENTS", nullable = false)
	private Boolean hasAttachments = false;

	@Transient
	@CheckForNull
	private Set<Attachment> attachmentsXml;

	PPodEntity() {}

	@Override
	@OverridingMethodsMustInvokeSuper
	public void accept(final IVisitor visitor) {
		for (final Attachment attachment : getAttachments()) {
			attachment.accept(visitor);
		}
	}

	public PPodEntity addAttachment(final Attachment attachment) {
		if (attachments == null) {
			attachments = newHashSet();
		}
		if (attachments.add(attachment)) {
			setInNeedOfNewPPodVersionInfo();
		}
		attachment.addAttachee(this);
		hasAttachments = true;

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
	public boolean beforeMarshal(@CheckForNull final Marshaller marshaler) {
		// Write out the version number for the client of the xml
		if (pPodVersionInfo != null) {
			pPodVersion = pPodVersionInfo.getPPodVersion();
		}
		getAttachmentsXml().addAll(getAttachments());
		return true;
	}

	private Set<Attachment> getAttachments() {
		if (hasAttachments) {
			if (attachments == null) {
				throw new AssertionError(
						"programming errors: attachments == null and hasAttachments == true");
			}
			return attachments;
		}
		return Collections.emptySet();
	}

	public Set<Attachment> getAttachmentsByNamespace(
			final String namespace) {
		return newHashSet(Iterables
				.filter(getAttachments(),
						new Attachment.IsOfNamespace(namespace)));
	}

	public Set<Attachment> getAttachmentsByNamespaceAndType(
			final String namespace, final String type) {
		return newHashSet(Iterables
				.filter(getAttachments(),
						new Attachment.IsOfNamespaceAndType(namespace, type)));
	}

	/**
	 * Get an iterator over the attachments. There will be no duplicates.
	 * 
	 * @return an iterator over the attachments
	 */
	public Iterator<Attachment> getAttachmentsIterator() {
		return Collections.unmodifiableSet(getAttachments()).iterator();
	}

	/**
	 * Get the number of attachments that this {@code PPodEntity} has.
	 * 
	 * @return the number of attachments that this {@code PPodEntity} has
	 */
	public int getAttachmentsSize() {
		return getAttachments().size();
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
	final Boolean getHasAttachments() {
		return hasAttachments;
	}

	@XmlAttribute
	@Nullable
	public Long getPPodVersion() {
		if (pPodVersionInfo != null) {
			return pPodVersionInfo.getPPodVersion();
		}
		return pPodVersion;
	}

	public PPodVersionInfo getPPodVersionInfo() {
		checkState(
				!getMarshalled(),
						"can't access a PPodVersionInfo through a marshalled PPodEntity");
		return pPodVersionInfo;
	}

	public boolean isInNeedOfNewPPodVersionInfo() {
		return inNeedOfNewPPodVersionInfo;
	}

	public boolean removeAttachment(final Attachment attachment) {
		checkNotNull(attachment);
		Boolean attachmentRemoved;
		if (!hasAttachments) {
			attachmentRemoved = false;
		} else {
			attachmentRemoved = getAttachments().remove(attachment);
			if (attachmentRemoved) {
				setInNeedOfNewPPodVersionInfo();
			}
			if (getAttachments().size() == 0) {
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
	public PPodEntity setInNeedOfNewPPodVersionInfo() {
		inNeedOfNewPPodVersionInfo = true;
		return this;
	}

	/**
	 * Set the pPOD version number.
	 * 
	 * @param pPodVersion the pPOD version number
	 * 
	 * @return this
	 */
	public PPodEntity setPPodVersion(final Long pPodVersion) {
		checkNotNull(pPodVersion);
		this.pPodVersion = pPodVersion;
		return this;
	}

	/**
	 * Set the pPod version info.
	 * 
	 * @param pPodVersionInfo new pPOD version
	 * 
	 * @return this
	 */
	public PPodEntity setPPodVersionInfo(
			final PPodVersionInfo pPodVersionInfo) {
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

	PPodEntity unsetInNeedOfNewPPodVersionInfo() {
		inNeedOfNewPPodVersionInfo = false;
		return this;
	}

}
