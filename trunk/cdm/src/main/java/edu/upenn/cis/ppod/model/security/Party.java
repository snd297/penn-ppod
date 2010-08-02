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
package edu.upenn.cis.ppod.model.security;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

import edu.upenn.cis.ppod.model.PersistentObject;

/**
 * An object with a name, wholly owned permissions, and that can belong to a
 * group.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = Party.TABLE)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Party extends PersistentObject {

	public static final String TABLE = "PARTY";
	public static final String JOIN_COLUMN = TABLE + "_ID";

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = JOIN_COLUMN)
	// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private final Set<StudyPermission> permissions = newHashSet();

	@Column(name = "NAME", length = 100, nullable = false)
	@Index(name = "IDX_PPOD_PARTY_NAME")
	private String name;

	/**
	 * Get the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get an this party's permissions.
	 * 
	 * @return this party's permissions
	 */
	public final Set<StudyPermission> getPermissions() {
		return permissions;
	}

	/**
	 * Set the name.
	 * 
	 * @param name the name to set
	 * 
	 * @return this
	 */
	public Party setName(final String name) {
		checkNotNull(name);
		this.name = name;
		return this;
	}

	/**
	 * Set this party's permissions.
	 * 
	 * @param pPodPermissions new permissions.
	 * @return this party
	 */
	public final Party setPermissions(
			final Set<StudyPermission> studyPermissions) {
		this.permissions.clear();
		this.permissions.addAll(studyPermissions);
		return this;
	}
}
