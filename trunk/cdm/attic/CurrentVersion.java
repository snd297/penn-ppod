package edu.upenn.cis.ppod.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

/**
 * The biggest version number in the db. There will be only one row in this
 * table.
 * 
 * @author Sam Donnelly
 */
@Entity
public class CurrentVersion {

	public static final Long ID = 1L;

	/**
	 * Assign this here and keep setter private to guarantee only one row.
	 */
	private Long id = ID;

	private Integer objVersion;

	private Long version;

	CurrentVersion() {}

	public CurrentVersion(final Long version) {
		this.version = version;
	}

	@Id
	@Column(name = "CURRENT_VERSION_ID")
	public Long getId() {
		return id;
	}

	@Version
	@Column(name = "OBJ_VERSION")
	@SuppressWarnings("unused")
	private Integer getObjVersion() {
		return objVersion;
	}

	/**
	 * Get the biggest pPOD version number in the db.
	 * 
	 * Just made it unique because it is, but since there will only be one
	 * entry, it's not necessary.
	 * 
	 * @return the biggest pPOD version number in the db
	 */
	@Column(unique = true)
	public Long getVersion() {
		return version;
	}

	@SuppressWarnings("unused")
	private void setId(final Long id) {
		this.id = id;
	}

	@SuppressWarnings("unused")
	private void setObjVersion(final Integer objVersion) {
		this.objVersion = objVersion;
	}

	public void setVersion(final Long version) {
		this.version = version;
	}
}
