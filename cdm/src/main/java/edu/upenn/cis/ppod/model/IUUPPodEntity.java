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

import com.google.common.base.Function;

/**
 * A universally-unique pPOD entity.
 * 
 * @author Sam Donnelly
 */
public interface IUUPPodEntity {

	/**
	 * {@link Function} wrapper of {@link #getPPodId()}.
	 */
	public static Function<IUUPPodEntity, String> getPPodId = new Function<IUUPPodEntity, String>() {

		public String apply(final IUUPPodEntity from) {
			return from.getPPodId();
		}

	};

	/**
	 * Get the pPOD id of this {@link IUUPPodEntity}.
	 * 
	 * @return the pPOD id of this {@link IUUPPodEntity}
	 */
	String getPPodId();

	/**
	 * Create the pPOD ID for this {@link IUUPPodEntity}.
	 * 
	 * @return this {@link IUUPPodEntity}
	 * 
	 * @throws IllegalStateException if {@link #getPPodId()}{@code != null} when
	 *             this method is called. That is, it throws an exception if the
	 *             pPOD id has already been set.
	 */
	IUUPPodEntity setPPodId();

	/**
	 * Set the pPOD id.
	 * 
	 * @param pPodId
	 * 
	 * @return this {@link IUUPPodEntity}
	 * 
	 * @throws IllegalStateException if {@link #getPPodId()}{@code != null} when
	 *             this method is called. That is, it throws an exception if the
	 *             pPOD id has already been set.
	 */
	IUUPPodEntity setPPodId(String pPodId);

}
