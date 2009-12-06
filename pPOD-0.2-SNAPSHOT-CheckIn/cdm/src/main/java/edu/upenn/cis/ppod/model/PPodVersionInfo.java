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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import edu.upenn.cis.ppod.thirdparty.HibernateUtil;

/**
 * Stores data about a particular version of the whole pPOD instance - for
 * example the pPOD version number.
 * <p>
 * The constructors and mutators of this class are intentionally package-private
 * because we don't want for these to be manipulated outside of this package due
 * to their trickiness.
 * 
 * @see PPodVersionInfoInterceptor
 * @author Sam Donnelly
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = PPodVersionInfo.TABLE)
public final class PPodVersionInfo extends PersistentObjectWXmlId {

	final static String TABLE = "PPOD_VERSION_INFO";

	final static String ID_COLUMN = TABLE + "_ID";

	final static String PPOD_VERSION_INFO_FIELD = "pPodVersionInfo";

	/** Global pPOD version. */
	@XmlAttribute
	@Column(name = "PPOD_VERSION", unique = true, nullable = false)
	@org.hibernate.annotations.Index(name = "IDX_PPOD_VERSION")
	private Long pPodVersion;

	/** Record the creation time of this record. */
	@XmlAttribute
	@Column(name = "CREATED", nullable = false)
	private Date created;

	/**
	 * Intentionally package-private.
	 */
	PPodVersionInfo() {}

	/**
	 * Get a copy of the creation date.
	 * 
	 * @return a copy of the creation date
	 */
	public Date getCreated() {
		return (Date) created.clone();
	}

	/**
	 * Getter.
	 * 
	 * @return the pPOD version number
	 */
	public Long getPPodVersion() {
		return pPodVersion;
	}

	/**
	 * Setter. Intentionally package-private.
	 * 
	 * @param created the value
	 * 
	 * @return this {@link PPodVersionInfo}
	 */
	PPodVersionInfo setCreated(final Date created) {
		checkNotNull(created);
		this.created = (Date) created.clone();
		return this;
	}

	/**
	 * Set the value for the pPOD version number.
	 * <p>
	 * Intentionally package-private: we don't want for these to be manipulated
	 * outside of the package: use {@link HibernateUtil#openSession()} to start
	 * session that will create these.
	 * 
	 * @param pPodVersion the pPOD version number
	 * 
	 * @return this {@link PPodVersionInfo}
	 */
	PPodVersionInfo setPPodVersion(final Long pPodVersion) {
		checkNotNull(pPodVersion);
		this.pPodVersion = pPodVersion;
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

		retValue.append("PPodVersionInfo(").append(super.toString())
				.append(TAB).append("pPodVersion=").append(this.pPodVersion)
				.append(TAB).append("created=").append(this.created)
				.append(TAB).append(")");

		return retValue.toString();
	}

}
