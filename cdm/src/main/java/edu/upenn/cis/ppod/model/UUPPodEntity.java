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

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAttribute;

import edu.upenn.cis.ppod.modelinterfaces.IWithPPodId;

/**
 * A universally unique pPOD entity.
 * <p>
 * Made public for Hibernate. Otherwise we get:
 * 
 * <pre>
 * Caused by: java.lang.IllegalAccessException: Class org.hibernate.proxy.pojo.javassist.JavassistLazyInitializer can not access a member of class edu.upenn.cis.ppod.model.PPodEntity with modifiers "public"
 *  	at sun.reflect.Reflection.ensureMemberAccess(Reflection.java:65)
 *  	at java.lang.reflect.Method.invoke(Method.java:588)
 *  	at org.hibernate.proxy.pojo.javassist.JavassistLazyInitializer.invoke(JavassistLazyInitializer.java:197)
 *  	at edu.upenn.cis.ppod.model.CharacterState_$$_javassist_0.beforeMarshal(CharacterState_$$_javassist_0.java)
 * </pre>
 * 
 * @author Sam Donnelly
 */
@MappedSuperclass
public abstract class UUPPodEntity extends PPodEntity implements IWithPPodId {

	final static String PPOD_ID_COLUMN = "PPOD_ID";
	final static int PPOD_ID_COLUMN_LENGTH = 36;

	@Column(name = PPOD_ID_COLUMN, unique = true, nullable = false, length = PPOD_ID_COLUMN_LENGTH)
	@Nullable
	private String pPodId;

	@XmlAttribute
	@Nullable
	public String getPPodId() {
		return pPodId;
	}

	public UUPPodEntity setPPodId() {
		return setPPodId(UUID.randomUUID().toString());
	}

	public UUPPodEntity setPPodId(@Nullable final String pPodId) {
		checkState(getPPodId() == null, "pPodId already set");

		this.pPodId = pPodId;
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

		retValue.append("UUPPodEntity(").append(super.toString()).append(TAB)
				.append("pPodId=").append(this.pPodId).append(TAB).append(")");

		return retValue.toString();
	}

}
