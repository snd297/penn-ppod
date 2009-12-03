package edu.upenn.cis.ppod.model;

import static edu.upenn.cis.ppod.util.CollectionsUtil.newHashSet;

import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;

/**
 * @author Sam Donnelly
 */
@Entity
@Table(name = Attachment.TABLE)
final public class Attachment {

	static final String TABLE = "ATTACHMENT";

	static final String ID_COLUMN = TABLE + "_ID";

	static final String ATTACHMENT_PHYLO_CHAR_JOIN_TABLE = TABLE + "_"
			+ PhyloChar.TABLE;

	private Long id;

	@Id
	@GeneratedValue
	@Column(name = ID_COLUMN)
	@SuppressWarnings("unused")
	private Long getId() {
		return id;
	}

	@SuppressWarnings("unused")
	private Attachment setId(final Long id) {
		this.id = id;
		return this;
	}

	private String attachmentKey;

	private String value;

	private Attachments attachments = new Attachments();

	@OneToMany
	@JoinTable(name = TABLE + "_" + TABLE, joinColumns = @JoinColumn(name = ID_COLUMN
			+ "_SOURCE"), inverseJoinColumns = @JoinColumn(name = ID_COLUMN
			+ "_TARGET"))
	// @org.hibernate.annotations.MapKey(columns = @Column(name =
	// "ATTACHMENT_KEY"))
	@MapKey(name = "attachmentKey")
	@Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@SuppressWarnings("unused")
	private Map<String, Attachment> getAttachments() {
		return attachments.getAttachments();
	}

	@SuppressWarnings("unused")
	private Attachment setAttachments(Map<String, Attachment> attachments) {
		this.attachments.setAttachments(attachments);
		return this;
	}

	/**
	 * Get the attachmentKey.
	 * <p>
	 * Intentionally package-private.
	 * 
	 * @return the attachmentKey
	 */
	@Column(name = "ATTACHEMENT_KEY", nullable = false)
	String getAttachmentKey() {
		return attachmentKey;
	}

	/**
	 * Get the value.
	 * 
	 * @return the value
	 */
	@Column(name = "VALUE")
	public String getValue() {
		return value;
	}

	/**
	 * Set the attachmentKey.
	 * <p>
	 * Intentionally package-private.
	 * 
	 * @param attachmentKey the attachmentKey to set
	 * 
	 * @return this
	 */
	Attachment setAttachmentKey(final String attachmentKey) {
		this.attachmentKey = attachmentKey;
		return this;
	}

	/**
	 * Set the value.
	 * 
	 * @param value the value to set
	 * 
	 * @return this
	 */
	public Attachment setValue(final String attachmentValue) {
		this.value = attachmentValue;
		return this;
	}

	/**
	 * Constructs a string with attributes in name=value format.
	 * 
	 * @return a string representation of this object.
	 */
	public String toString() {
		final String TAB = " ";

		StringBuilder retValue = new StringBuilder();

		retValue.append("Attachment(").append(super.toString()).append(TAB)
				.append("id=").append(this.id).append(TAB).append(
						"attachmentKey=").append(this.attachmentKey)
				.append(TAB).append("value=").append(this.value).append(TAB)
				.append("attachments=").append(this.attachments).append(TAB)
				.append(")");

		return retValue.toString();
	}

}
