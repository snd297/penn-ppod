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
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collections;
import java.util.Set;

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

import com.google.common.annotations.VisibleForTesting;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.upenn.cis.ppod.imodel.IPPodEntity;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A {@code PersistentObject} with pPOD version information and to which we can
 * add/remove attachments.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = PPodEntity.TABLE)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class PPodEntity
		extends PersistentObject
		implements IPPodEntity {

	public static final String TABLE = "PPOD_ENTITY";

	public static final String JOIN_COLUMN = TABLE + "_ID";

	@OneToMany(mappedBy = "attachee", cascade = CascadeType.ALL,
			orphanRemoval = true)
	@CheckForNull
	private Set<Attachment> attachments;

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

	/**
	 * The pPod version of this object. Similar in concept to Hibernate's
	 * version, but tweaked for our purposes.
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = VersionInfo.JOIN_COLUMN)
	@CheckForNull
	private VersionInfo versionInfo;

	protected PPodEntity() {}

	public void accept(final IVisitor visitor) {
		checkNotNull(visitor);
		for (final Attachment attachment : getAttachments()) {
			attachment.accept(visitor);
		}
	}

	public Attachment addAttachment(final Attachment attachment) {
		if (attachments == null) {
			attachments = newHashSet();
		}
		if (attachments.add(attachment)) {
			setInNeedOfNewVersion();
		}
		attachment.setAttachee(this);
		hasAttachments = true;

		return attachment;
	}

	/** {@inheritDoc} */
	public Set<Attachment> getAttachments() {
		if (hasAttachments) {
			if (attachments == null) {
				throw new AssertionError(
						"programming errors: attachments == null and hasAttachments == true");
			}
			return Collections.unmodifiableSet(attachments);
		}
		return Collections.emptySet();
	}

	/** {@inheritDoc} */
	public Set<Attachment> getAttachmentsByNamespace(
			final String namespace) {
		return newHashSet(filter(
				getAttachments(),
						new Attachment.IsOfNamespace(namespace)));
	}

	/** {@inheritDoc} */
	public Set<Attachment> getAttachmentsByNamespaceAndType(
			final String namespace, final String type) {
		return newHashSet(filter(
						getAttachments(),
						new Attachment.IsOfNamespaceAndType(namespace, type)));
	}

	/**
	 * So that we don't need to hit the attachments if not necessary. It's an
	 * issue for matrices which have tons of cells.
	 */
	protected Boolean getHasAttachments() {
		return hasAttachments;
	}

	public VersionInfo getVersionInfo() {
		return versionInfo;
	}

	public boolean isInNeedOfNewVersion() {
		return inNeedOfNewVersion;
	}

	/** {@inheritDoc} */
	public boolean removeAttachment(final Attachment attachment) {
		checkNotNull(attachment);
		Boolean attachmentRemoved;
		if (!hasAttachments) {
			attachmentRemoved = false;
		} else {
			final Set<Attachment> thisAttachments = attachments;
			if (thisAttachments == null) {
				throw new AssertionError(
						"hasAttachments is true but attachments == null");
			}

			attachmentRemoved = thisAttachments.remove(attachment);
			if (attachmentRemoved) {
				setInNeedOfNewVersion();
			}
			if (thisAttachments.size() == 0) {
				hasAttachments = false;
			}
		}
		return attachmentRemoved;
	}

	/** {@inheritDoc} */
	@OverridingMethodsMustInvokeSuper
	public void setInNeedOfNewVersion() {
		inNeedOfNewVersion = true;
	}

	/** {@inheritDoc} */
	public void setVersionInfo(
			final VersionInfo versionInfo) {
		checkNotNull(versionInfo);
		unsetInNeedOfNewVersion();
		this.versionInfo = versionInfo;
	}

	@VisibleForTesting
	public void unsetInNeedOfNewVersion() {
		inNeedOfNewVersion = false;
	}
}
