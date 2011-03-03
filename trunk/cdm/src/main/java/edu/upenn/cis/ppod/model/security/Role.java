/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package edu.upenn.cis.ppod.model.security;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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

	static final String JOIN_COLUMN = TABLE + "_ID";

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
	 * 
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

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = JOIN_COLUMN)
	// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private final Set<StudyPermission> studyPermissions = newHashSet();

	public final Set<StudyPermission> getPermissions() {
		return studyPermissions;
	}

	/**
	 * Set this role's permissions.
	 * 
	 * @param pPodPermissions the new permissions
	 * @return this role
	 */
	public final Role setPermissions(final Set<StudyPermission> studyPermissions) {
		this.studyPermissions.clear();
		this.studyPermissions.addAll(studyPermissions);
		return this;
	}

}
