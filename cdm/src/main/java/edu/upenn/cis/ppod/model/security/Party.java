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

import java.util.Collections;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
	private final Set<PPodPermission> permissions = newHashSet();

	@ManyToMany
	@JoinTable(name = TABLE + "_" + PPodGroup.TABLE, joinColumns = { @JoinColumn(name = JOIN_COLUMN) }, inverseJoinColumns = { @JoinColumn(name = PPodGroup.ID_COLUMN) })
	private final Set<PPodGroup> groups = newHashSet();

	@Column(name = "NAME", length = 100, nullable = false)
	@Index(name = "IDX_PPOD_PARTY_NAME")
	private String name;

	/**
	 * Get an unmodifiable view of the the groups to which this party belongs.
	 * 
	 * @return an unmodifiable view of the the groups to which this party
	 *         belongs
	 */
	public Set<PPodGroup> getGroups() {
		return Collections.unmodifiableSet(groups);
	}

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
	public final Set<PPodPermission> getPermissions() {
		return permissions;
	}

	/**
	 * Set the groups.
	 * 
	 * @param groups the groups to set
	 * 
	 * @return this
	 */
	public Party setGroups(final Set<PPodGroup> groups) {
		this.groups.clear();
		this.groups.addAll(groups);
		return this;
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
	public final Party setPermissions(final Set<PPodPermission> pPodPermissions) {
		this.permissions.clear();
		this.permissions.addAll(pPodPermissions);
		return this;
	}
}
