package edu.upenn.cis.ppod.domain;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * 
 * Has a {@link javax.xml.bind.annotation.XmlID} attribute.
 * 
 * @author Sam Donnelly
 */
public interface IHasDocId {

	/**
	 * Get the {@link javax.xml.bind.annotation.XmlID} attribute.
	 * <p>
	 * Will be {@code null} until one of the {@code setDocId(...)}s are called
	 * by the client, but never {@code null} after that.
	 * 
	 * @return the {@code XmlID} attribute
	 */
	@Nullable
	String getDocId();

}
