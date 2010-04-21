package edu.upenn.cis.ppod.modelinterfaces;

import javax.annotation.Nullable;


/**
 * @author Sam Donnelly
 * 
 */
public interface IUUPPodEntityWithXmlID extends IWithXmlID {

	/**
	 * Get the {@link XmlID} attribute.
	 * <p>
	 * Will be {@code null} until one of the {@code setDocId(...)}s are called
	 * by the client.
	 * 
	 * @return the {@code XmlID} attribute
	 */
	@Nullable
	String getDocId();

}