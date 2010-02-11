package edu.upenn.cis.ppod.model;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlID;

/**
 * One may ask, why require that the client explicitly set the doc id? And why
 * only allow it to be sent once? Why not just have it assigned automatically at
 * construction and have it freely reset if required. The reason is that
 * sometimes a client needs to set it to a certain value using
 * {@link #setDocId(String)} and since the {@link #getDocId()} is an identifier,
 * it seems dangerous to allow it to be reset since as soon as it's assigned,
 * something may be depending on that value.
 * 
 * @author Sam Donnelly
 */
public interface IWXmlID {

	/**
	 * Get the {@link XmlID} attribute.
	 * 
	 * @return the {@code XmlID} attribute
	 */
	@Nullable
	String getDocId();

	/**
	 * Create and set this {@code UUPPodEntityWXmlId}'s doc id.
	 * 
	 * @return this {@code UUPPodEntityWXmlId}
	 * 
	 * @throws IllegalStateException if {@code getDocId() != null} when this
	 *             method is called
	 */
	IWXmlID setDocId();

	/**
	 * Set this {@code UUPPodEntityWXmlId}'s doc id.
	 * 
	 * @param docId the doc id
	 * 
	 * @return this {@code UUPPodEntityWXmlId}
	 * 
	 * @throws IllegalStateException if {@code getDocId() != null} when this
	 *             method is called
	 */
	IWXmlID setDocId(final String docId);

}
