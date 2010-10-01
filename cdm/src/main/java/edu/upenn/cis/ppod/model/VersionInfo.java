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

import java.util.Date;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Stores data about a particular version of the whole pPOD instance - for
 * example the pPOD version number.
 * <p>
 * The constructors and mutators of this class are intentionally package-private
 * because we don't want for these to be manipulated outside of this package due
 * to their trickiness.
 * <p>
 * We don't make the entity immutable since it's not a root entity (or is it? -
 * not sure if PersistentObject counts since it is not an entity).
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = VersionInfo.TABLE)
@NamedQueries({ @NamedQuery(name = "VersionInfo-getMaxVersionInfo",
		query = "select max(vi.version) from VersionInfo vi") })
public class VersionInfo extends PersistentObject {

	public final static String TABLE = "VERSION_INFO";

	public final static String JOIN_COLUMN = TABLE + "_ID";

	/** The pPOD version number. Immutable. */
	@Column(name = "PPOD_VERSION", unique = true, nullable = false,
			updatable = false)
	private Long version = -1L;

	/** Record the creation time of this record. Immutable */
	@Column(name = "CREATED", nullable = false, updatable = false)
	@Nullable
	private Date created;

	/**
	 * Intentionally package-private, to block subclassing outside of this
	 * package.
	 */
	VersionInfo() {}

	/**
	 * Get a copy of the creation date.
	 * 
	 * @return a copy of the creation date
	 */
	@XmlAttribute
	@Nullable
	public Date getCreated() {
		if (created == null) {
			return null;
		}
		return (Date) created.clone();
	}

	/**
	 * Getter.
	 * 
	 * @return the pPOD version number
	 */
	@XmlAttribute
	public Long getVersion() {
		return version;
	}

	/**
	 * Setter.
	 * <p>
	 * {@code protected} for JAXB.
	 * 
	 * @param created the value
	 * 
	 * @return this {@link VersionInfo}
	 */
	protected VersionInfo setCreated(final Date created) {
		checkNotNull(created);
		this.created = (Date) created.clone();
		return this;
	}

	/**
	 * Set the value for the pPOD version number.
	 * <p>
	 * Intentionally non-public (protected for JAXB): we don't want for these to
	 * be manipulated outside of the package: use
	 * 
	 * @param version the pPOD version number
	 * 
	 * @return this {@link VersionInfo}
	 */
	protected VersionInfo setVersion(final Long version) {
		checkNotNull(version);
		this.version = version;
		return this;
	}

	/**
	 * Constructs a <code>String</code> with attributes in name=value format.
	 * 
	 * @return a <code>String</code> representation of this object.
	 */
	@Override
	public String toString() {
		final String TAB = " ";

		final StringBuilder retValue = new StringBuilder();

		retValue.append("VersionInfo(").append(super.toString())
				.append(TAB).append("version=").append(this.version)
				.append(TAB).append("created=").append(this.created)
				.append(TAB).append(")");

		return retValue.toString();
	}

}
