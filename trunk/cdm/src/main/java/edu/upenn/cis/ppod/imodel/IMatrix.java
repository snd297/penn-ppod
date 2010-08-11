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
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.model.VersionInfo;

public interface IMatrix<R extends IRow<?, ?>>
		extends IChild<IOTUSet>, IUUPPodEntity, IWithDocId {

	Integer getColumnsSize();

	/**
	 * Get the column pPOD version infos. These are equal to the largest pPOD
	 * version in the columns, where largest list determined determined by
	 * {@link VersionInfo#getVersion()} .
	 * <p>
	 * The behavior of this method is undefined for unmarshalled matrices.
	 * 
	 * @return get the column pPOD version infos
	 */
	List<VersionInfo> getColumnVersionInfos();

	/**
	 * Get the pPOD version numbers of each column. The version number is the
	 * value of the largest cell version in the column.
	 * <p>
	 * This method was created for getting at the version number in unmarshalled
	 * matrices, but it is fine to call for any matrix. When
	 * {@link #getColumnVersionInfos()} is defined the following calls are
	 * equivalent:
	 * <ul>
	 * <li>
	 * {@code getColumnVersionInfos().get(n).getVersion()}</li>
	 * <li>
	 * {@code getColumnVersions().get(n)}</li>
	 * </ul>
	 * 
	 * @return the pPOD version number of each column
	 */
	List<Long> getColumnVersions();

	/**
	 * Getter.
	 * <p>
	 * {@code null} is a legal value.
	 * 
	 * @return the description
	 */
	@XmlAttribute
	@CheckForNull
	String getDescription();

	/**
	 * Getter. {@code null} when the object is constructed, but never
	 * {@code null} for persistent objects.
	 * <p>
	 * Will {@code null} only until {@code setLabel()} is called for newly
	 * created objects. Will never be {@code null} for persistent objects.
	 * 
	 * @return the label
	 */
	@Nullable
	String getLabel();

	/**
	 * Get the rows that make up this matrix.
	 * <p>
	 * The values won't be {@code null} for matrices straight out of the
	 * database.
	 * <p>
	 * {@code null} values occur only when {@link #setOTUSet(OTUSet)} contains
	 * OTUs newly introduced to this matrix.
	 * 
	 * @param otu the key
	 * 
	 * @return the row, or {@code null} of no such row has been inserted yet
	 */
	Map<IOTU, R> getRows();

	/**
	 * Set row at <code>otu</code> to <code>row</code>.
	 * <p>
	 * Assumes {@code row} does not belong to another matrix.
	 * <p>
	 * {@code otu} must be a member of {@link #getParent()}.
	 * 
	 * @param otu index of the row we are adding
	 * @param row the row we're adding
	 * 
	 * @return the row that was previously there, or {@code null} if there was
	 *         no row previously there
	 */
	@CheckForNull
	R putRow(final IOTU otu, final R row);

	void setColumnsSize(final int columnsSize);

	/**
	 * Set a particular column to a version.
	 * 
	 * @param pos position of the column
	 * @param versionInfo the version
	 */
	void setColumnVersionInfo(
			final int pos,
			final VersionInfo versionInfo);

	/**
	 * Set all of the columns' pPOD version infos.
	 * 
	 * @param versionInfo the pPOD version info
	 * 
	 * @return this
	 */
	void setColumnVersionInfos(
			final VersionInfo versionInfo);

	/**
	 * Setter.
	 * 
	 * @param description the description value, {@code null} is allowed
	 */
	void setDescription(
			@CheckForNull final String description);

	/**
	 * Set the column at {@code position} as in need of a new
	 * {@link VersionInfo}. Which means to set {@link #getColumnVersionInfos()}
	 * {@code .get(position)} to {@code null}.
	 * 
	 * @param position the column that needs the new {@code VersionInfo}
	 */
	void setInNeedOfNewColumnVersion(final int position);

	/**
	 * Set the label of this matrix.
	 * 
	 * @param label the value for the label
	 */
	void setLabel(final String label);

}