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
	 * 
	 * @return the {@code XmlID} attribute
	 */
	@Nullable
	String getDocId();

}
