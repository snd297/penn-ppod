package edu.upenn.cis.ppod.model;

import static edu.upenn.cis.ppod.util.CollectionsUtil.newHashMap;
import static edu.upenn.cis.ppod.util.CollectionsUtil.newHashSet;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;

import edu.upenn.cis.ppod.util.UPennCisPPodUtil;

/**
 * A standard character. For example, "length_of_infraorb_canal".
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = PhyloChar.TABLE)
final public class PhyloChar implements IPPodVersionInfoable {

	/**
	 * Default constructor.
	 */
	public PhyloChar() {}

	/** This entity's table. Intentionally package-private. */
	final static String TABLE = "PHYLO_CHAR";

	/**
	 * The column where a {@link Matrix}'s {@link javax.persistence.Id} gets
	 * stored. Intentionally package-private.
	 */
	final static String ID_COLUMN = TABLE + "_ID";

	/** The label's column. Intentionally package-private. */
	final static String LABEL_COLUMN = "LABEL";

	/** Surrogate key. */
	private Long id;

	/**
	 * Get the identifier property.
	 * 
	 * @return the the identifier property.
	 */
	@Id
	@GeneratedValue
	@Column(name = ID_COLUMN)
	@SuppressWarnings("unused")
	private Long getId() {
		return id;
	}

	@SuppressWarnings("unused")
	private PhyloChar setId(Long id) {
		this.id = id;
		return this;
	}

	/** For optimistic locking. */
	private Integer version;

	@Version
	@Column(name = "OBJ_VERSION")
	@SuppressWarnings("unused")
	private Integer getVersion() {
		return version;
	}

	@SuppressWarnings("unused")
	private PhyloChar setVersion(final Integer version) {
		this.version = version;
		return this;
	}

	/**
	 * The states that this character can have. For example, 0->"absent",
	 * 1->"present", 2->"one", or 3->"two". NOTE: it is legal to have
	 * non-contiguous integers in the keys.
	 */

	private final Map<Integer, PhyloCharState> phyloCharStates = newHashMap();

	@SuppressWarnings("unused")
	private PhyloChar setPhyloCharStates(
			Map<Integer, PhyloCharState> phyloCharStates) {
		this.phyloCharStates.clear();
		this.phyloCharStates.putAll(phyloCharStates);
		return this;
	}

	@OneToMany(mappedBy = "phyloChar")
	@Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@MapKey(name = "state")
	@SuppressWarnings("unused")
	private Map<Integer, PhyloCharState> getPhyloCharStates() {
		return phyloCharStates;
	}

	/**
	 * The pPod-version of this object. Similar in concept to Hibernate's
	 * version, but tweaked to match our semantics.
	 */
	private PPodVersionInfo pPodVersionInfo;

	/** The matrices that hold a reference to this {@link PhyloChar}. */
	private final Set<Matrix> matrices = newHashSet();

	@ManyToMany(mappedBy = "phyloChars")
	@SuppressWarnings("unused")
	private Set<Matrix> getMatrices() {
		return matrices;
	}

	@SuppressWarnings("unused")
	private PhyloChar setMatrices(final Set<Matrix> matrices) {
		matrices.clear();
		matrices.addAll(matrices);
		return this;
	}

	private final Attachments attachments = new Attachments();

	@OneToMany
	@JoinTable(name = TABLE + "_" + Attachment.TABLE, joinColumns = @JoinColumn(name = ID_COLUMN), inverseJoinColumns = @JoinColumn(name = Attachment.ID_COLUMN))
	@MapKey(name = "attachmentKey")
	//@Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@SuppressWarnings("unused")
	private Map<String, Attachment> getAttachments() {
		return attachments.getAttachments();
	}

	@SuppressWarnings("unused")
	private PhyloChar setAttachments(final Map<String, Attachment> attachments) {
		this.attachments.setAttachments(attachments);
		return this;
	}

	/**
	 * The non-unique label of this {@link PhyloChar}.
	 */
	private String label;

	private String pPodId = PPodUtil.createPPodId();

	/**
	 * Get the label of this <code>StdChar</code>.
	 * 
	 * @return the label of this <code>StdChar</code>.
	 */
	@Column(name = LABEL_COLUMN, nullable = false)
	public String getLabel() {
		return label;
	}

	/**
	 * Return an unmodifiable view of this <code>PhyloChar</code>'s states.
	 * 
	 * @return an unmodifiable view of this <code>PhyloChar</code>'s states.
	 */
	@Transient
	public Map<Integer, PhyloCharState> getPhyloCharStatesImmutable() {
		return Collections.unmodifiableMap(phyloCharStates);
	}

	/**
	 * Get the pPOD id.
	 * 
	 * @return the pPOD id
	 */
	@Column(name = "PPOD_ID", unique = true, nullable = false, length = 36)
	@org.hibernate.annotations.Index(name = "IDX_PPOD_ID")
	public String getpPodId() {
		return pPodId;
	}

	@SuppressWarnings("unused")
	private PhyloChar setpPodId(final String pPodId) {
		this.pPodId = pPodId;
		return this;
	}

	/**
	 * Getter.
	 * 
	 * @return the {@code PPodVersionInfo}
	 */
	@ManyToOne
	@JoinColumn(name = PPodVersionInfo.ID_COLUMN, nullable = false)
	public PPodVersionInfo getpPodVersionInfo() {
		return pPodVersionInfo;
	}

	@SuppressWarnings("unused")
	private PhyloChar setpPodVersionInfo(final PPodVersionInfo pPodVersionInfo) {
		this.pPodVersionInfo = pPodVersionInfo;
		return this;
	}

	/**
	 * Get property with key <code>key</code>.
	 * 
	 * @param key the key.
	 * @return the value.
	 */
	public Attachment getAttachment(final String key) {
		return attachments.get(key);
	}

	/**
	 * Put <code>phyloCharState</code> into this <code>PhyloChar</code>.
	 * <p>
	 * Calling this handles both sides of the <code>PhyloChar</code><->
	 * <code>PhyloCharState</code>s. relationship.
	 * 
	 * @param phyloCharState what we're adding.
	 * @return <code>phyloCharState</code>.
	 */
	public PhyloCharState put(final PhyloCharState phyloCharState) {
		phyloCharStates.put(phyloCharState.getState(), phyloCharState);
		phyloCharState.setPhyloChar(this);
		resetPPodVersionInfo();
		return phyloCharState;
	}

	/**
	 * Set the label of this <code>StdChar</code>.
	 * 
	 * @param label the value for the label.
	 * @return this <code>PhyloChar</code>.
	 */
	public PhyloChar setLabel(final String label) {
		if (label.equals(getLabel())) {
			// they're the same, nothing to do.
		} else {
			this.label = label;
			resetPPodVersionInfo();
		}
		return this;
	}

	/**
	 * Constructs a <code>String</code> with all attributes in name = value
	 * format.
	 * 
	 * @return a <code>String</code> representation of this object.
	 */
	@Override
	public String toString() {
		final String TAB = " ";

		final StringBuilder retValue = new StringBuilder();

		retValue.append("PhyloChar ( ").append(super.toString()).append(TAB)
				.append("id = ").append(this.id).append(TAB).append(
						"version = ").append(TAB).append("label = ").append(
						this.label).append(TAB).append(")");

		return retValue.toString();
	}

	/**
	 * Setter.
	 * 
	 * @return this <code>Cell</code>
	 */
	PhyloChar resetPPodVersionInfo() {
		if (pPodVersionInfo == null) {} else {
			pPodVersionInfo = null;
			for (final Matrix matrix : matrices) {
				matrix.resetPPodVersionInfo();
			}
		}
		return this;
	}

	/**
	 * Add {@code matrix} to this {@link PhyloChar}'s matrices.
	 * <p>
	 * Intentionally package-protected.
	 * 
	 * @param matrix to be added.
	 * @return <code>true</code> if <code>matrix</code> was not there before,
	 *         <code>false</code> otherwise.
	 */
	boolean addMatrix(final Matrix matrix) {
		return matrices.add(matrix);
	}

	/**
	 * Remove <code>matrix</code> from this <code>PhyloChar</code>s matrices.
	 * <p>
	 * Intentionally package-protected.
	 * 
	 * @param matrix to be removed.
	 * @return <code>true</code> if <code>matrix</code> was there to be removed,
	 *         <code>false</code> otherwise.
	 */
	boolean removeMatrix(final Matrix matrix) {
		return matrices.remove(matrix);
	}

	public Attachment putAttachment(String key, Attachment attachment) {
		attachments.put(key, attachment);
		resetPPodVersionInfo();
		return attachment;
	}

	public Attachment removeAttachment(String key) {
		resetPPodVersionInfo();
		return attachments.remove(key);
	}
}
