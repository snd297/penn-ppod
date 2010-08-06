package edu.upenn.cis.ppod.model;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;

import com.google.common.annotations.VisibleForTesting;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.modelinterfaces.IOTU;
import edu.upenn.cis.ppod.modelinterfaces.IOTUSet;
import edu.upenn.cis.ppod.modelinterfaces.IOTUSetChild;
import edu.upenn.cis.ppod.modelinterfaces.IUUPPodEntity;
import edu.upenn.cis.ppod.modelinterfaces.IWithXmlID;

public interface IMatrix<R extends Row<?, ?>>
		extends IOTUSetChild, IUUPPodEntity, IWithXmlID {

	Integer getColumnsSize();

	List<VersionInfo> getColumnVersionInfos();

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
	@XmlAttribute
	@Nullable
	String getLabel();

	/**
	 * Getter. Will be {@code null} when object is first created, but never
	 * {@code null} for persistent objects.
	 * 
	 * @return this matrix's {@code OTUSet}
	 */
	@Nullable
	IOTUSet getParent();

	/**
	 * Get the row indexed by an OTU, or {@code null} if no such row has been
	 * inserted yet.
	 * <p>
	 * The return value won't be {@code null} for matrices straight out of the
	 * database.
	 * <p>
	 * {@code null} return values occur when {@link #setOTUSet(OTUSet)} contains
	 * OTUs newly introduced to this matrix.
	 * 
	 * @param otu the key
	 * 
	 * @return the row, or {@code null} of no such row has been inserted yet
	 * 
	 * @throws IllegalArgumentException if {@code otu} does not belong to this
	 *             matrix's {@code OTUSet}
	 */
	@Nullable
	R getRow(final IOTU otu);

	Map<IOTU, R> getRows();

	/**
	 * Set row at <code>otu</code> to <code>row</code>.
	 * <p>
	 * Assumes {@code row} does not belong to another matrix.
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
	R putRow(final IOTU otu, final R row);

	IMatrix<R> setColumnsSize(final int columnsSize);

	IMatrix<R> setColumnVersionInfo(
			final int pos,
			final VersionInfo versionInfo);

	IMatrix<R> setColumnVersionInfos(
			final VersionInfo versionInfo);

	/**
	 * Set the values of the column version numbers for marshalled matrices.
	 * <p>
	 * <strong>Outside of testing, clients should never call this
	 * method.</strong> In normal usage, these values are populated when an
	 * object in marshalled.
	 * 
	 * @param columnVersions values
	 */
	@VisibleForTesting
	void setColumnVersions(final List<Integer> columnVersions);

	IMatrix<R> setDescription(
			@CheckForNull final String description);

	IMatrix<R> setInNeedOfNewColumnVersion(final int position);

	IMatrix<R> setInNeedOfNewVersion();

	IMatrix<R> setLabel(final String label);

	IMatrix<?> setParent(
			@CheckForNull final IOTUSet otuSet);

}