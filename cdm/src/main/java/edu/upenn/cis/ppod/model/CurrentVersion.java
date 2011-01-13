package edu.upenn.cis.ppod.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class CurrentVersion {

	public static final Long ID = 1L;

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
