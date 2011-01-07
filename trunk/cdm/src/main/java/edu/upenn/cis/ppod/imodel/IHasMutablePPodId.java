package edu.upenn.cis.ppod.imodel;

import javax.annotation.Nullable;

import edu.upenn.cis.ppod.domain.IHasPPodId;

public interface IHasMutablePPodId extends IHasPPodId {
	/**
	 * Create the pPOD ID for this {@link IWithPPodId}.
	 * 
	 * 
	 * @throws IllegalStateException if {@link #getPPodId()}{@code != null} when
	 *             this method is called. That is, it throws an exception if the
	 *             pPOD id has already been set.
	 */
	void setPPodId();

	/**
	 * Set the pPOD id.
	 * <p>
	 * It is legal to call this with a {@code null} {@code pPodId}.
	 * 
	 * @param pPodId
	 * 
	 * @throws IllegalStateException if {@link #getPPodId()}{@code != null} when
	 *             this method is called. That is, it throws an exception if the
	 *             pPOD id has already been set.
	 */
	void setPPodId(@Nullable String pPodId);
}
