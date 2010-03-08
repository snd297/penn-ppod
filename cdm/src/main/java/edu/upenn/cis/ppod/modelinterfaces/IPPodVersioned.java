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
package edu.upenn.cis.ppod.modelinterfaces;

import javax.annotation.Nullable;

import edu.upenn.cis.ppod.model.PPodVersionInfo;

/**
 * An object with a {@link PPodVersionInfo}.
 * 
 * @author Sam Donnelly
 */
public interface IPPodVersioned extends IPersistentObject {

	/**
	 * Used when we serialize so that we don't have to serialize the
	 * {@link PPodVersionInfo} too. Will be {@code null} when the object is
	 * first constructed.
	 * 
	 * @return {@code getPPodVersionInfo().getPPodVersion()} if available,
	 *         otherwise use the value that was serialized
	 * 
	 * @throws IllegalStateException if there is no pPOD version number
	 *             available
	 */
	@Nullable
	Long getPPodVersion();

	/**
	 * Get the version info of this {@code IPPodVersioned}.
	 * <p>
	 * NOTE: the weird name is on purpose so that Hibernate can identity it as
	 * the getter.
	 * 
	 * @return the version info of this {@code IPPodVersioned}
	 */
	@Nullable
	PPodVersionInfo getpPodVersionInfo();

	/**
	 * NOTE: the weird name is on purpose so that Hibernate can identity it as
	 * the getter.
	 */
	IPPodVersioned setPPodVersionInfo(final PPodVersionInfo pPodVersionInfo);

	IPPodVersioned setAllowResetPPodVersionInfo(
			boolean allowResetPPodVersionInfo);

	IPPodVersioned unsetInNeedOfNewPPodVersionInfo();

	IPPodVersioned setInNeedOfNewPPodVersionInfo();

	boolean isInNeedOfNewPPodVersionInfo();

}
