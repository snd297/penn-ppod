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

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;

/**
 * Group permissions.
 * <p>
 * A group is a set of {@link Party}s.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = Group.TABLE)
public final class Group extends Party {

	static final String TABLE = "PPOD_GROUP";

	static final String ID_COLUMN = TABLE + "_ID";

	@ManyToMany
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@JoinTable(name = TABLE + "_" + Party.TABLE, joinColumns = { @JoinColumn(
			name = ID_COLUMN) }, inverseJoinColumns = { @JoinColumn(
			name = Party.JOIN_COLUMN) })
	private final Set<User> users = newHashSet();

	/**
	 * Set the parties.
	 * 
	 * @param parties the parties to set
	 * 
	 * @return this
	 */
	public Group setParties(final Set<User> parties) {
		this.users.clear();
		this.users.addAll(parties);
		return this;
	}

	public Set<User> getUsers() {
		return users;
	}

}
