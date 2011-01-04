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

import com.google.common.annotations.Beta;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface IMatrix<R extends IRow<C, ?>, C extends ICell<?, ?>>
		extends IOtuSetChild, IUuPPodEntity, IHasDocId {

	/**
	 * The number of columns which any newly introduced rows must have.
	 * <p>
	 * Will return {@code 0} for newly constructed matrices.
	 * 
	 * @param columnsSize the number of columns in this matrix
	 */
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
	List<IVersionInfo> getColumnVersionInfos();

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
	 * Rows will only be {@code null} for OTUs newly introduced to this matrix
	 * by {@link #setOTUs}.
	 * 
	 * @return the rows that make up this matrix
	 */
	Map<IOtu, R> getRows();

	/**
	 * Set row at <code>otu</code> to <code>row</code>.
	 * <p>
	 * Assumes {@code row} does not belong to another matrix.
	 * <p>
	 * {@code otu} must be a member of {@link #getParent()}.
	 * <p>
	 * Assumes {@code row} is not detached.
	 * 
	 * @param otu index of the row we are adding
	 * @param row the row we're adding
	 * 
	 * @return the row that was previously there, or {@code null} if there was
	 *         no row previously there
	 * 
	 * @throws IllegalArgumentException if {@code otu} does not belong to this
	 *             matrix's {@code OTUSet}
	 * @throws IllegalArgumentException if this matrix already contains a row
	 *             {@code .equals} to {@code row}
	 */
	@CheckForNull
	R putRow(IOtu otu, R row);

	/**
	 * Remove the cells the make up the given column number.
	 * 
	 * @param columnNo the column to remove
	 * 
	 * @return the cells in the column
	 */
	@Beta
	public List<C> removeColumn(int columnNo);

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

	/**
	 * Set all of the columns' pPOD version infos.
	 * 
	 * @param versionInfo the pPOD version info
	 * 
	 * @return this
	 */
	void setColumnVersionInfos(IVersionInfo versionInfo);

	/**
	 * Setter.
	 * 
	 * @param description the description value, {@code null} is allowed
	 */
	void setDescription(@CheckForNull String description);

	/**
	 * Set the column at {@code position} as in need of a new
	 * {@link VersionInfo}. Which means to set {@link #getColumnVersionInfos()}
	 * {@code .get(position)} to {@code null}.
	 * 
	 * @param position the column that needs the new {@code VersionInfo}
	 */
	void setInNeedOfNewColumnVersion(int position);

	/**
	 * Set the label of this matrix.
	 * 
	 * @param label the value for the label
	 */
	void setLabel(String label);

}