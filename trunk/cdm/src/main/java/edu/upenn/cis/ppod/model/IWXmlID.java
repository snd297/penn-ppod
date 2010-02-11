package edu.upenn.cis.ppod.model;

import javax.xml.bind.annotation.XmlID;

/**
 * One may ask, why require that the client explicitly set the doc id - why not
 * just have it assigned automatically at construction. The reason is that it
 * has been useful to notice when a doc id has accidentally not been set,
 * indicating that something is wrong.
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
