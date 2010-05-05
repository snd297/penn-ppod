package edu.upenn.cis.ppod.model;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.CheckForNull;
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

import edu.umd.cs.findbugs.annotations.OverrideMustInvoke;
import edu.upenn.cis.ppod.modelinterfaces.IPPodVersionedWithOTUSet;

/**
 * @author Sam Donnelly
 */
@MappedSuperclass
public abstract class Matrix<R extends Row<?>> extends UUPPodEntityWXmlId
		implements IPPodVersionedWithOTUSet, Iterable<R> {

	/** Description column. */
	public static final String DESCRIPTION_COLUMN = "DESCRIPTION";

	public static final String LABEL_COLUMN = "LABEL";

	/** The pPod versions of the columns. */
	@ManyToMany
	@JoinTable(inverseJoinColumns = { @JoinColumn(name = PPodVersionInfo.ID_COLUMN) })
	@OrderColumn(name = PPodVersionInfo.TABLE + "_POSITION")
	private final List<PPodVersionInfo> columnPPodVersionInfos = newArrayList();

	@XmlElement(name = "columnPPodVersion")
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
	@JoinColumn(name = OTUSet.ID_COLUMN, nullable = false)
	@CheckForNull
	private OTUSet otuSet;

	Matrix() {}

	/**
	 * Get a reference of the {@code PPodVersionInfo}s for each for the columns
	 * of the matrix.
	 * <p>
	 * Intentionally package-private.
	 * 
	 * @return a mutable view of the {@code PPodVersionInfo}s for each for the
	 *         columns of the matrix
	 */
	protected List<PPodVersionInfo> getColumnPPodVersionInfos() {
		return columnPPodVersionInfos;
	}

	public Iterator<PPodVersionInfo> getColumnPPodVersionInfosIterator() {
		return Collections.unmodifiableList(getColumnPPodVersionInfos())
				.iterator();
	}

	/**
	 * Created for testing.
	 * 
	 * @return
	 */
	List<Long> getColumnPPodVersions() {
		return columnPPodVersions;
	}

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
	protected abstract OTUsToRows<R> getOTUsToRows();

	/**
	 * Get the row indexed by an OTU or {@code null} if {@code otu} has not had
	 * a row assigned to it.
	 * 
	 * @param otu the index
	 * 
	 * @return the row, or {@code null} if {@code otu} has not had a row
	 *         assigned to it
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
	@OverrideMustInvoke
	protected Matrix<R> setOTUSet(
			@CheckForNull final OTUSet newOTUSet) {
		otuSet = newOTUSet;
		return this;
	}

}
