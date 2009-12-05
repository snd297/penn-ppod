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
package edu.upenn.cis.ppod.thirdparty.model.security;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Index;

import edu.upenn.cis.ppod.model.security.Party;

/**
 * Simple class that represents any User domain entity in any application.
 * 
 * <p>
 * Because this class performs its own Realm and PPodPermission checks, and
 * these can happen frequently enough in a production application, it is highly
 * recommended that the internal {@link User#getRoles()} collection be cached in
 * a 2nd-level cache when using JPA and/or Hibernate. The hibernate xml
 * configuration for this sample application does in fact do this for your
 * reference (see PPodUser.hbm.xml - the 'roles' declaration).
 * </p>
 */
@Entity
@Table(name = User.TABLE)
// @Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
public final class User extends Party {

	static final String TABLE = "USER";

	static final String ID_COLUMN = TABLE + "_ID";

	@Column(name = "EMAIL")
	@Index(name = "IDX_USERS_EMAIL")
	private String email;

	@Column(name = "PASSWORD", length = 255, nullable = false)
	private String password;

	@ManyToMany
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@JoinTable(name = TABLE + "_" + Role.TABLE, joinColumns = { @JoinColumn(name = ID_COLUMN) }, inverseJoinColumns = { @JoinColumn(name = Role.ID_COLUMN) })
	// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private final Set<Role> roles = newHashSet();

	/**
	 * Get this user's email.
	 * 
	 * @return this user's email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Returns the password for this user.
	 * 
	 * @return this user's password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Get this user's roles.
	 * 
	 * @return this user's roles
	 */
	public Set<Role> getRoles() {
		return roles;
	}

	/**
	 * Set this user's email.
	 * 
	 * @param email the user's email
	 * @return this user's email
	 */
	public User setEmail(final String email) {
		this.email = email;
		return this;
	}

	/**
	 * Set this user's password.
	 * 
	 * @param password the password
	 * @return this user
	 */
	public User setPassword(final String password) {
		this.password = password;
		return this;
	}

	/**
	 * Set this user's roles.
	 * 
	 * @param roles the roles
	 * @return this user
	 */
	public User setRoles(final Set<Role> roles) {
		this.roles.clear();
		this.roles.addAll(roles);
		return this;
	}
}
