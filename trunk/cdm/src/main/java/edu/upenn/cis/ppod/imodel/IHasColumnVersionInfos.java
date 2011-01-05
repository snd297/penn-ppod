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

import java.util.List;

public interface IHasColumnVersionInfos extends IVersioned {

	/**
	 * Get the column pPOD version infos. These are equal to the largest pPOD
	 * version in the columns, where largest list determined determined by
	 * {@link VersionInfo#getVersion()} .
	 * <p>
	 * The behavior of this method is undefined for unmarshalled matrices.
	 * 
	 * @return get the column pPOD version infos
	 */
	List<IVersionInfo> getColumnVersionInfos();

	/**
	 * Set a particular column to a version.
	 * 
	 * @param pos position of the column
	 * @param versionInfo the version
	 * 
	 * @throw IllegalArgumentException if {@code pos >=
	 *        getColumnVersionInfos().size()}
	 */
	void setColumnVersionInfo(int pos, IVersionInfo versionInfo);

}