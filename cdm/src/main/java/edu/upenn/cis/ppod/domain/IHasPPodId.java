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
package edu.upenn.cis.ppod.domain;

import javax.annotation.Nullable;

import com.google.common.base.Function;

/**
 * Has a PPod ID.
 * 
 * @author Sam Donnelly
 */
public interface IHasPPodId {

	/**
	 * {@link Function} wrapper of {@link #getPPodId()}.
	 */
	public static Function<IHasPPodId, String> getPPodId = new Function<IHasPPodId, String>() {

		public String apply(final IHasPPodId from) {
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

}
