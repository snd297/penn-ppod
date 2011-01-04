/*
 * Copyright (C) 2010 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.imodel;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.Function;

import edu.upenn.cis.ppod.model.StandardState;

@XmlJavaTypeAdapter(StandardState.Adapter.class)
public interface IStandardState
		extends IChild<IStandardCharacter>, IPPodEntity, ILabeled, IHasDocId {

	/**
	 * {@link Function} wrapper of {@link #getStateNumber()}.
	 */
	public static final Function<IStandardState, Integer> getStateNumber = new Function<IStandardState, Integer>() {

		public Integer apply(final IStandardState from) {
			return from.getStateNumber();
		}

	};

	/**
	 * Get the integer value of this character stateNumber. The integer value is
	 * the heart of the class.
	 * <p>
	 * {@code null} when the object is created. Never {@code null} for
	 * persistent objects.
	 * 
	 * @return get the integer value of this character stateNumber
	 */
	@XmlAttribute
	@Nullable
	Integer getStateNumber();

	void setLabel(final String label);

}