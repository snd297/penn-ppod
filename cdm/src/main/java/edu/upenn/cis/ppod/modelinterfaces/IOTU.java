package edu.upenn.cis.ppod.modelinterfaces;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.model.OTU;

@XmlJavaTypeAdapter(OTU.Adapter.class)
public interface IOTU
		extends IUUPPodEntity, ILabeled, IOTUSetChild, IWithXmlID {

	/**
	 * Return the label of this {@code OTU}.
	 * <p>
	 * Until the label is set, it is {@code null}. Once the label is set, it
	 * will never be {@code null}.
	 * 
	 * @return the label
	 */
	@Nullable
	String getLabel();

	/**
	 * Set this OTU's label.
	 * 
	 * @param label the label
	 * 
	 * @return this
	 */
	IOTU setLabel(final String label);

}