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
package edu.upenn.cis.ppod.imodel;

import javax.annotation.Nullable;

import com.google.common.base.Function;

/**
 * Has a PPod ID.
 * 
 * @author Sam Donnelly
 */
public interface IWithPPodId {

	/**
	 * {@link Function} wrapper of {@link #getPPodId()}.
	 */
	public static Function<IWithPPodId, String> getPPodId = new Function<IWithPPodId, String>() {

		public String apply(final IWithPPodId from) {
			return from.getPPodId();
		}

	};

	/**
	 * Get the pPOD id of this {@link IWithPPodId}.
	 * 
	 * @return the pPOD id of this {@link IWithPPodId}
	 */
	@Nullable
	String getPPodId();

	/**
	 * Create the pPOD ID for this {@link IWithPPodId}.
	 * 
	 * @return this {@link IWithPPodId}
	 * 
	 * @throws IllegalStateException if {@link #getPPodId()}{@code != null} when
	 *             this method is called. That is, it throws an exception if the
	 *             pPOD id has already been set.
	 */
	IWithPPodId setPPodId();

	/**
	 * Set the pPOD id.
	 * <p>
	 * It is legal to call this with a {@code null} {@code pPodId}.
	 * 
	 * @param pPodId
	 * 
	 * @return this {@link IWithPPodId}
	 * 
	 * @throws IllegalStateException if {@link #getPPodId()}{@code != null} when
	 *             this method is called. That is, it throws an exception if the
	 *             pPOD id has already been set.
	 */
	IWithPPodId setPPodId(@Nullable String pPodId);

}
