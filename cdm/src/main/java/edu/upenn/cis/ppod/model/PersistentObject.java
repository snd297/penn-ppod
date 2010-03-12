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

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.hibernate.annotations.AccessType;

import edu.upenn.cis.ppod.modelinterfaces.IPersistentObject;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * Base class for {@code edu.upenn.cis.ppod.model} (and subpackages) entities.
 * 
 * @author Sam Donnelly
 */
@XmlAccessorType(XmlAccessType.NONE)
@MappedSuperclass
public abstract class PersistentObject implements IPersistentObject {

	static final String ID_COLUMN = "ID";

	@AccessType("property")
	@Id
	@GeneratedValue
	@Column(name = ID_COLUMN)
	private Long id;

	@Version
	@Column(name = "OBJ_VERSION")
	private Integer version;

	@Transient
	private boolean allowPersist = true;

	@Transient
	protected boolean marshalled = false;

	/** Default constructor. */
	protected PersistentObject() {}

	public void accept(final IVisitor visitor) {
		throw new UnsupportedOperationException();
	}

	public boolean getAllowPersist() {
		return allowPersist;
	}

	@XmlAttribute
	@Nullable
	public Long getId() {
		return id;
	}

	/** Created for Jaxb. */
	@SuppressWarnings("unused")
	private final PersistentObject setId(final Long id) {
		this.id = id;
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

		retValue.append("PersistentObject(").append(super.toString()).append(
				TAB).append("id=").append(this.id).append(TAB).append(
				"version=").append(this.version).append(TAB).append(")");

		return retValue.toString();
	}

	/**
	 * Can be used to block persisting an object, for example an in interceptor.
	 * 
	 * @return this
	 */
	protected PersistentObject unsetAllowPersist() {
		allowPersist = false;
		return this;
	}

	protected boolean getMarshalled() {
		return marshalled;
	}
}
