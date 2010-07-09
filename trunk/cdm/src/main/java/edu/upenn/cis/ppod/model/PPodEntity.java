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
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collections;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.google.common.collect.Iterables;

import edu.upenn.cis.ppod.modelinterfaces.IAttachee;
import edu.upenn.cis.ppod.modelinterfaces.IVersioned;
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
public abstract class PPodEntity
		extends PersistentObject
		implements IAttachee, IVersioned {

	public static final String TABLE = "PPOD_ENTITY";

	public static final String JOIN_COLUMN = TABLE + "_ID";

	@OneToMany(mappedBy = "attachee", cascade = CascadeType.ALL,
			orphanRemoval = true)
	@CheckForNull
	private Set<Attachment> attachments;

	@Transient
	@CheckForNull
	private Set<Attachment> attachmentsXml;

	@Column(name = "HAS_ATTACHMENTS", nullable = false)
	private Boolean hasAttachments = false;

	/**
	 * Does this object need to be assigned a new pPOD version at next save or
	 * update? This flag is really only needed for entities that were obtained
	 * from the database and not for transient objects - read on for the why.
	 * <p>
	 * We start with {@code false} because
	 * <ul>
	 * <li>that's appropriate for DB entities: when an object is modified, this
	 * value will be set to {@code true} and it will be set to the newest
	 * version number by whatever mechanism is in place</li>
	 * <li>for transient entities this flag is functionally irrelevant: the pPOD
	 * version info needs to be set at time of creation, so this value is will
	 * be unset anyway in {@link #setVersionInfo} . Subsequent calls to the
	 * setters will then turn it to {@code true} needlessly.
	 * </ul>
	 */
	@Transient
	private boolean inNeedOfNewVersion = false;

	@Transient
	@CheckForNull
	private Long version;

	/**
	 * The pPod version of this object. Similar in concept to Hibernate's
	 * version, but tweaked for our purposes.
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = VersionInfo.JOIN_COLUMN)
	@CheckForNull
	private VersionInfo versionInfo;

	protected PPodEntity() {}

	@Override
	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		for (final Attachment attachment : getAttachments()) {
			attachment.accept(visitor);
		}
	}

	public PPodEntity addAttachment(final Attachment attachment) {
		if (attachments == null) {
			attachments = newHashSet();
		}
		if (attachments.add(attachment)) {
			setInNeedOfNewVersion();
		}
		attachment.setAttachee(this);
		hasAttachments = true;

		return this;
	}

	/**
	 * {@link Unmarshaller} callback.
	 * 
	 * @param u see {@code Unmarshaller}
	 * @param parent see {@code Unmarshaller}
	 */
	public void afterUnmarshal(
			@CheckForNull final Unmarshaller u,
			final Object parent) {
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
		if (versionInfo != null) {
			version = versionInfo.getVersion();
		}
		getAttachmentsXml().addAll(getAttachmentsModifiable());
		return true;
	}

	public Set<Attachment> getAttachments() {
		return Collections.unmodifiableSet(getAttachmentsModifiable());
	}

	public Set<Attachment> getAttachmentsByNamespace(
			final String namespace) {
		return newHashSet(Iterables
				.filter(getAttachmentsModifiable(),
						new Attachment.IsOfNamespace(namespace)));
	}

	public Set<Attachment> getAttachmentsByNamespaceAndType(
			final String namespace, final String type) {
		return newHashSet(filter(
						getAttachments(),
						new Attachment.IsOfNamespaceAndType(namespace, type)));
	}

	private Set<Attachment> getAttachmentsModifiable() {
		if (hasAttachments) {
			if (attachments == null) {
				throw new AssertionError(
						"programming errors: attachments == null and hasAttachments == true");
			}
			return attachments;
		}
		return Collections.emptySet();
	}

	@XmlElement(name = "attachment")
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
	public Long getVersion() {
		if (versionInfo != null) {
			return versionInfo.getVersion();
		}
		return version;
	}

	public VersionInfo getVersionInfo() {
		checkState(
				!isUnmarshalled(),
						"can't access a VersionInfo through an unmarshalled PPodEntity");
		return versionInfo;
	}

	public boolean isInNeedOfNewVersion() {
		return inNeedOfNewVersion;
	}

	public boolean removeAttachment(final Attachment attachment) {
		checkNotNull(attachment);
		Boolean attachmentRemoved;
		if (!hasAttachments) {
			attachmentRemoved = false;
		} else {
			attachmentRemoved = getAttachmentsModifiable().remove(attachment);
			if (attachmentRemoved) {
				setInNeedOfNewVersion();
			}
			if (getAttachmentsModifiable().size() == 0) {
				hasAttachments = false;
			}
		}
		return attachmentRemoved;
	}

	/**
	 * Set the {@code Attachments}s of this {@code PPodEntity}.
	 * <p>
	 * This {@code PPodEntity} makes a shallow copy of {@code attachments}.
	 * <p>
	 * If this method is effectively removing any of this sets's original
	 * attachments, then the {@code PPodEntity->Attachments} relationship is
	 * severed.
	 * 
	 * @param attachments the otus to assign to this {@code PPodEntity}
	 * 
	 * @return any attachments that were removed as a result of this operation
	 */
	public Set<Attachment> setAttachments(final Set<Attachment> attachments) {
		checkNotNull(attachments);
		if (attachments.equals(getAttachments())) {
			return Collections.emptySet();
		}

		final Set<Attachment> removedAttachments = newHashSet(getAttachments());
		removedAttachments.removeAll(attachments);

		for (final Attachment removedAttachment : removedAttachments) {
			removedAttachment.setAttachee(null);
		}

		getAttachmentsModifiable().clear();

		for (final Attachment attachment : attachments) {
			addAttachment(attachment);
		}

		setInNeedOfNewVersion();
		return removedAttachments;
	}

	/**
	 * Mark this object's {@link VersionInfo} for update to the next version
	 * number on save or update. This is done by setting it to {@code null}.
	 * <p>
	 * Implementors should also, if desired, call {@code resetVersionInfo()} on
	 * any owning objects.
	 * 
	 * @return this {@code PPodEntity}
	 */
	@OverridingMethodsMustInvokeSuper
	public PPodEntity setInNeedOfNewVersion() {
		inNeedOfNewVersion = true;
		return this;
	}

	/**
	 * Set the pPOD version number.
	 * 
	 * @param pPodVersion the pPOD version number
	 * 
	 * @return this
	 */
	public PPodEntity setVersion(final Long version) {
		checkNotNull(version);
		this.version = version;
		return this;
	}

	/**
	 * Set the pPod version info.
	 * 
	 * @param versionInfo new pPOD version
	 * 
	 * @return this
	 */
	public PPodEntity setVersionInfo(
			final VersionInfo versionInfo) {
		checkNotNull(versionInfo);
		unsetInNeedOfNewVersion();
		this.versionInfo = versionInfo;
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
				.append("versionInfo=").append(this.versionInfo)
				.append(TAB).append("version=").append(this.version)
				.append(TAB).append(")");

		return retValue.toString();
	}

	protected PPodEntity unsetInNeedOfNewVersion() {
		inNeedOfNewVersion = false;
		return this;
	}

}
