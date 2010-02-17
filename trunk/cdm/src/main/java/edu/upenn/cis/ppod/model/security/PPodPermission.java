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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.shiro.authz.permission.DomainPermission;

import edu.upenn.cis.ppod.util.IVisitor;

/**
 * Persistent {@link DomainPermission}.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = PPodPermission.TABLE)
@edu.umd.cs.findbugs.annotations.SuppressWarnings
public final class PPodPermission extends DomainPermission {
		

	static final String TABLE = "PPOD_PERMISSION";

	private static final long serialVersionUID = 1L;

	static final String ID_COLUMN = "ID";

	private Long id;

	private Integer version;

	@Column(name = "ACTIONS")
	@Override
	public String getActions() {
		return super.getActions();
	}

	@Column(name = "DOMAIN")
	@Override
	public String getDomain() {
		return super.getDomain();
	}

	@Id
	@GeneratedValue
	@Column(name = ID_COLUMN)
	public Long getId() {
		return id;
	}

	@Column(name = "TARGETS")
	@Override
	public String getTargets() {
		return super.getTargets();
	}

	@Version
	@Column(name = "OBJ_VERSION")
	@SuppressWarnings("unused")
	private Integer getVersion() {
		return version;
	}

	@Override
	public void setActions(final String actions) {
		super.setActions(actions);
		super.setParts(null == getDomain() ? "*" : getDomain()
				+ PART_DIVIDER_TOKEN + getActions() + PART_DIVIDER_TOKEN
				+ getTargets());
	}

	@Override
	public void setDomain(final String domain) {
		super.setDomain(domain);
		super.setParts(null == getDomain() ? "*" : getDomain()
				+ PART_DIVIDER_TOKEN + getActions() + PART_DIVIDER_TOKEN
				+ getTargets());
	}

	@SuppressWarnings("unused")
	private PPodPermission setId(final Long id) {
		this.id = id;
		return this;
	}

	@Override
	public void setTargets(final String targets) {
		super.setTargets(targets);
		super.setParts(null == getDomain() ? "*" : getDomain()
				+ PART_DIVIDER_TOKEN + getActions() + PART_DIVIDER_TOKEN
				+ getTargets());
	}

	@SuppressWarnings("unused")
	private PPodPermission setVersion(final Integer version) {
		this.version = version;
		return this;
	}

	public PPodPermission accept(IVisitor visitor) {
		throw new UnsupportedOperationException();
	}

	public boolean getAllowPersist() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public PPodPermission setAllowPersist(final Boolean allowPersist) {
		throw new UnsupportedOperationException();
	}
}
