package edu.upenn.cis.ppod.model;

import javax.xml.bind.annotation.XmlID;

/**
 * One may ask, why require that the client explicitly set the doc id? And why
 * only allow it to be sent once? Why not just have it assigned automatically at
 * construction and have it freely reset if required. The reason is that it has
 * been useful to notice when a doc id has accidentally not been set, or when we
 * try to set it twice. These things have meant that something is wrong.
 * 
 * @author Sam Donnelly
 */
public interface IWXmlID {

	/**
	 * Get the {@link XmlID} attribute.
	 * 
	 * @return the {@code XmlID} attribute
	 */
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
