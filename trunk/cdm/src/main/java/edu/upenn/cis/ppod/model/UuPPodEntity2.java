package edu.upenn.cis.ppod.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import edu.upenn.cis.ppod.imodel.IUuPPodEntity;

@MappedSuperclass
public abstract class UuPPodEntity2 implements IUuPPodEntity {

	public final static String PPOD_ID_COLUMN = "PPOD_ID";
	public final static int PPOD_ID_COLUMN_LENGTH = 36;

	/**
	 * {@code updatable = false} makes this property immutable
	 */
	private String pPodId = UUID.randomUUID().toString();

	/**
	 * Weird looking so that the hibernate property is "pPodId".
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	@Column(name = PPOD_ID_COLUMN,
			unique = true,
			nullable = false,
			length = PPOD_ID_COLUMN_LENGTH,
			updatable = false)
	private String getpPodId() {
		return getPPodId();
	}

	@Transient
	public String getPPodId() {
		return pPodId;
	}

	/**
	 * Weird looking so that the hibernate property is "pPodId".
	 */
	@SuppressWarnings("unused")
	private void setpPodId(final String pPodId) {
		setPPodId(pPodId);
	}

	private void setPPodId(final String pPodId) {
		this.pPodId = pPodId;
	}
}
