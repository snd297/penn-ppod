package edu.upenn.cis.ppod.model;

import javax.annotation.Nullable;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAttribute;

import edu.upenn.cis.ppod.modelinterfaces.ILabeled;

/**
 * @author Sam Donnelly
 */
@MappedSuperclass
public abstract class CharacterState extends PPodEntityWXmlId implements
		ILabeled {

	/**
	 * The column where the label is stored.
	 */
	protected final static String LABEL_COLUMN = "LABEL";

	public abstract String getLabel();
}
