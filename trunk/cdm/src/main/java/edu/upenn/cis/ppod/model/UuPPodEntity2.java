package edu.upenn.cis.ppod.model;

import java.util.UUID;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import edu.upenn.cis.ppod.imodel.IUuPPodEntity;

@MappedSuperclass
abstract class UuPPodEntity2 implements IUuPPodEntity {

	public final static String PPOD_ID_COLUMN = "PPOD_ID";
	public final static int PPOD_ID_COLUMN_LENGTH = 36;

	/**
	 * {@code updatable = false} makes this property immutable
	 */
	@Access(AccessType.FIELD)
	@Column(name = PPOD_ID_COLUMN,
			unique = true,
			nullable = false,
			length = PPOD_ID_COLUMN_LENGTH,
			updatable = false)
	private String pPodId = UUID.randomUUID().toString();

	@Transient
	public String getPPodId() {
		return pPodId;
	}

	@SuppressWarnings("unused")
	private void setPPodId(String pPodId) {
		this.pPodId = pPodId;
	}
}
