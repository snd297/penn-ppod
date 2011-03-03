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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.upenn.cis.ppod.imodel.IPPodEntity;

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

	protected PPodEntity() {}

	public Attachment addAttachment(final Attachment attachment) {
		checkNotNull(attachment);
		if (attachments == null) {
			attachments = newHashSet();
		}
		attachments.add(attachment);
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
		checkNotNull(namespace);
		return newHashSet(filter(
				getAttachments(),
						new Attachment.IsOfNamespace(namespace)));
	}

	/** {@inheritDoc} */
	public Set<Attachment> getAttachmentsByNamespaceAndType(
			final String namespace, final String type) {
		checkNotNull(namespace);
		checkNotNull(type);
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

			if (thisAttachments.size() == 0) {
				hasAttachments = false;
			}
		}
		return attachmentRemoved;
	}

}
