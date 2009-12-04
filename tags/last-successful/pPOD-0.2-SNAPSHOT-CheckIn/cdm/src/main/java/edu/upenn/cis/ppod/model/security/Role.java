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

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collections;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Index;

import edu.upenn.cis.ppod.model.PersistentObject;

/**
 * Model object that represents a security role.
 */
@Entity
@Table(name = Role.TABLE)
// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public final class Role extends PersistentObject {

	static final String TABLE = "ROLE";

	static final String ID_COLUMN = TABLE + "_ID";

	@Column(name = "NAME", length = 100, nullable = false)
	@Index(name = "IDX_PPOD_ROLE_NAME")
	private String name;

	@Column(name = "DESCRIPTION")
	private String description;

	/**
	 * Get the description.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Get the name.
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the description.
	 * 
	 * @param description the description
	 * @return this role
	 */
	public Role setDescription(final String description) {
		this.description = description;
		return this;
	}

	/**
	 * The the name of this role.
	 * 
	 * @param name the name
	 * @return this role
	 */
	public Role setName(final String name) {
		this.name = name;
		return this;
	}

	@OneToMany
	@Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = ID_COLUMN)
	// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private final Set<PPodPermission> pPodPermissions = newHashSet();

	/**
	 * Get an unmodifiable view of this role's permissions.
	 * 
	 * @return an unmodifiable view of this role's permissions
	 */
	public final Set<PPodPermission> getPermissions() {
		return Collections.unmodifiableSet(pPodPermissions);
	}

	/**
	 * Set this role's permissions.
	 * 
	 * @param pPodPermissions the new permissions
	 * @return this role
	 */
	public final Role setPermissions(final Set<PPodPermission> pPodPermissions) {
		this.pPodPermissions.clear();
		this.pPodPermissions.addAll(pPodPermissions);
		return this;
	}

}
