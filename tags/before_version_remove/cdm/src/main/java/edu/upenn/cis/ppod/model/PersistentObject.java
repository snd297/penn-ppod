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
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.upenn.cis.ppod.imodel.IHasLongId;

/**
 * Base class for {@code edu.upenn.cis.ppod.model} (and subpackages) entities.
 * 
 * @author Sam Donnelly
 */
@MappedSuperclass
public abstract class PersistentObject implements IHasLongId {

	public static final String ID_COLUMN = "ID";

	/**
	 * We changed this to PROPERTY because on page 564 of
	 * <em>Java persistence with Hibernate</em>, Fourth, corrected printing, by
	 * Christian Bauer, Gavin King. Copyright 2007 Manning Publications Co.,
	 * 1-932394-88-5 it says:
	 * <p>
	 * As long as you access only the database identifier property, no
	 * initialization of the proxy is necessary. (Note that this isn't true if
	 * you map the identifier property with direct field access; Hibernate then
	 * doesn't even know that the {@code getId()} method exists. If you call it,
	 * the proxy has to be initialized.)
	 */
	@Access(AccessType.PROPERTY)
	@Id
	@GeneratedValue
	@Column(name = ID_COLUMN)
	@CheckForNull
	private Long id;

	@SuppressWarnings("unused")
	@Version
	@Column(name = "OBJ_VERSION")
	@CheckForNull
	private Integer objVersion;

	/** Default constructor. */
	protected PersistentObject() {}

	@Nullable
	public Long getId() {
		return id;
	}

	@SuppressWarnings("unused")
	private PersistentObject setId(final Long id) {
		this.id = id;
		return this;
	}

}
