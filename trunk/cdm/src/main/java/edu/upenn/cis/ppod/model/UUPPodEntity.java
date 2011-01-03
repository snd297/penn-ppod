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

import static com.google.common.base.Preconditions.checkState;

import java.util.UUID;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAttribute;

import edu.upenn.cis.ppod.imodel.IUUPPodEntity;

/**
 * A universally unique pPOD entity.
 * 
 * @author Sam Donnelly
 */
@MappedSuperclass
public abstract class UUPPodEntity extends PPodEntity implements IUUPPodEntity {

	final static String PPOD_ID_COLUMN = "PPOD_ID";
	final static int PPOD_ID_COLUMN_LENGTH = 36;

	/**
	 * {@code updatable = false} makes this property immutable
	 */
	@Column(name = PPOD_ID_COLUMN, unique = true, nullable = false,
			length = PPOD_ID_COLUMN_LENGTH, updatable = false)
	@CheckForNull
	private String pPodId;

	@XmlAttribute
	@Nullable
	public String getPPodId() {
		return pPodId;
	}

	public void setPPodId() {
		setPPodId(UUID.randomUUID().toString());
	}

	public void setPPodId(@CheckForNull final String pPodId) {
		checkState(getPPodId() == null, "pPodId already set");

		this.pPodId = pPodId;
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

		retValue.append("UUPPodEntity(").append(super.toString()).append(TAB)
				.append("pPodId=").append(this.pPodId).append(TAB).append(")");

		return retValue.toString();
	}

}
