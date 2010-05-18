package edu.upenn.cis.ppod.model;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static edu.upenn.cis.ppod.util.CollectionsUtil.nullFillAndSet;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OrderColumn;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import edu.upenn.cis.ppod.modelinterfaces.IMatrix;

/**
 * A matrix is a set of OTU-keyed rows with column header pPOD versions which
 * are each equal to the largest version in the column.
 * 
 * @author Sam Donnelly
 */
@MappedSuperclass
public abstract class Matrix<R extends Row<?>> extends
		UUPPodEntityWXmlId
		implements IMatrix {

	/** Description column. */
	public static final String DESCRIPTION_COLUMN = "DESCRIPTION";

	public static final String LABEL_COLUMN = "LABEL";

	/** The pPod versions of the columns. */
	@ManyToMany
	@JoinTable(inverseJoinColumns = { @JoinColumn(name = PPodVersionInfo.ID_COLUMN) })
	@OrderColumn(name = PPodVersionInfo.TABLE + "_POSITION")
	private final List<PPodVersionInfo> columnPPodVersionInfos = newArrayList();

	@Transient
	private final List<Long> columnPPodVersions = newArrayList();

	/** Free-form description. */
	@Column(name = DESCRIPTION_COLUMN, nullable = true)
	@CheckForNull
	private String description;

	/** The label for this {@code CharacterStateMatrix}. */
	@Column(name = LABEL_COLUMN, nullable = false)
	@CheckForNull
	private String label;

	/**
	 * These are the <code>OTU</code>s whose data comprises this {@code
	 * CharacterStateMatrix}.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = OTUSet.JOIN_COLUMN, nullable = false)
	@CheckForNull
	private OTUSet otuSet;

	protected Matrix() {}

	/**
	 * Get the column pPOD version infos. These are equal to the largest pPOD
	 * version in the columns.
	 * 
	 * 
	 * @return get the column pPOD version infos
	 */
	public List<PPodVersionInfo> getColumnPPodVersionInfos() {
		return Collections.unmodifiableList(columnPPodVersionInfos);
	}

	/**
	 * A modifiable reference to the column pPOD version infos.
	 * 
	 * @return a modifiable reference to the column pPOD version infos
	 */
	protected List<PPodVersionInfo> getColumnPPodVersionInfosModifiable() {
		return columnPPodVersionInfos;
	}

	public List<Long> getColumnPPodVersions() {
		return Collections.unmodifiableList(columnPPodVersions);
	}

	@XmlElement(name = "columnPPodVersion")
	protected List<Long> getColumnPPodVersionsModifiable() {
		return columnPPodVersions;
	}

	public abstract Integer getColumnsSize();

	/**
	 * Getter.
	 * <p>
	 * {@code null} is a legal value.
	 * 
	 * @return the description
	 */
	@XmlAttribute
	@CheckForNull
	public String getDescription() {
		return description;
	}

	/**
	 * Getter. {@code null} when the object is constructed, but never {@code
	 * null} for persistent objects.
	 * 
	 * @return the label
	 */
	@XmlAttribute
	@Nullable
	public String getLabel() {
		return label;
	}

	/**
	 * Getter. Will be {@code null} when object is first created, but never
	 * {@code null} for persistent objects.
	 * 
	 * @return this matrix's {@code OTUSet}
	 */
	@Nullable
	public OTUSet getOTUSet() {
		return otuSet;
	}

	/**
	 * Get the otusToRows.
	 * <p>
	 * In perfect world, this would live in a subclass since it does impose a
	 * certain implementation detail - storing rows in an OTU-to-row map.
	 * 
	 * @return the otusToRows
	 */
	protected abstract OTUKeyedMap<R> getOTUsToRows();

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
	public R getRow(final OTU otu) {
		checkNotNull(otu);
		return getOTUsToRows().get(otu);
	}

	/**
	 * Get the rows that make up this matrix.
	 * 
	 * @return the rows that make up this matrix
	 */
	public Map<OTU, R> getRows() {
		return Collections.unmodifiableMap(getOTUsToRows().getOTUsToValues());
	}

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
	public R putRow(final OTU otu, final R row) {
		checkNotNull(otu);
		checkNotNull(row);
		return getOTUsToRows().put(otu, row);
	}

	/**
	 * Set the {@link PPodVersionInfo} at {@code idx} to {@code null}. Fills
	 * with <code>null</code>s if necessary.
	 * 
	 * @param position see description
	 * 
	 * @return this
	 */
	public Matrix<R> resetColumnPPodVersion(
			@Nonnegative final int position) {
		checkArgument(position >= 0, "position is negative");
		nullFillAndSet(getColumnPPodVersionInfosModifiable(), position, null);
		return this;
	}

	/**
	 * Set a particular column to a version
	 * 
	 * @param pos position of the column
	 * @param pPodVersionInfo the version
	 * 
	 * @return this
	 * 
	 * @throw IllegalArgumentException if {@code pos >=
	 *        getColumnPPodVersionInfos().size()}
	 */
	public Matrix<R> setColumnPPodVersionInfo(final int pos,
			final PPodVersionInfo pPodVersionInfo) {
		checkNotNull(pPodVersionInfo);
		checkArgument(pos < getColumnPPodVersionInfos().size(),
				"pos is bigger than getColumnPPodVersionInfos().size()");
		getColumnPPodVersionInfosModifiable().set(pos, pPodVersionInfo);
		return this;
	}

	/**
	 * Set all of the columns' pPOD version infos.
	 * 
	 * @param pPodVersionInfo version
	 * 
	 * @return this
	 */
	public Matrix<R> setColumnPPodVersionInfos(
			final PPodVersionInfo pPodVersionInfo) {
		for (int pos = 0; pos < getColumnPPodVersionInfos().size(); pos++) {
			setColumnPPodVersionInfo(pos, pPodVersionInfo);
		}
		return this;
	}

	/**
	 * Setter.
	 * 
	 * @param description the description value, {@code null} is allowed
	 * 
	 * @return this matrix
	 */
	public Matrix<R> setDescription(
			@CheckForNull final String description) {
		if (equal(description, getDescription())) {
			// nothing to do
		} else {
			this.description = description;
			setInNeedOfNewPPodVersionInfo();
		}
		return this;
	}

	/**
	 * {@code null} out {@code pPodVersionInfo} and the {@link PPodVersionInfo}
	 * of the owning study.
	 * 
	 * @return this {@code CharacterStateMatrix}
	 */
	@Override
	public Matrix<R> setInNeedOfNewPPodVersionInfo() {
		if (getOTUSet() != null) {
			getOTUSet().setInNeedOfNewPPodVersionInfo();
		}
		super.setInNeedOfNewPPodVersionInfo();
		return this;
	}

	/**
	 * Set the label of this matrix.
	 * <p>
	 * Will {@code null} only until {@code setLabel()} is called for newly
	 * created objects. Will never be {@code null} for persistent objects.
	 * 
	 * @param label the value for the label
	 * 
	 * @return this matrix
	 */
	public Matrix<R> setLabel(final String label) {
		checkNotNull(label);
		if (label.equals(getLabel())) {
			// they're the same, nothing to do
		} else {
			this.label = label;
			setInNeedOfNewPPodVersionInfo();
		}
		return this;
	}

	/**
	 * Setter.
	 * <p>
	 * Meant to be called only from objects responsible for managing the {@code
	 * OTUSET<->CharacterStateMatrix} relationship.
	 * <p>
	 * This method will remove otusToRows from this matrix as necessary.
	 * <p>
	 * If there are any new {@code OTU}s in {@code newOTUSet}, then {@code
	 * getRow(theNewOTU) == null}. That is, it adss {@code null} rows for new
	 * {@code OTU}s.
	 * 
	 * @param newOTUSet new {@code OTUSet} for this matrix, or {@code null} if
	 *            we're destroying the association
	 * 
	 * @return this
	 */
	public Matrix<R> setOTUSet(
			@CheckForNull final OTUSet newOTUSet) {
		otuSet = newOTUSet;
		getOTUsToRows().setOTUs();
		return this;
	}

}
