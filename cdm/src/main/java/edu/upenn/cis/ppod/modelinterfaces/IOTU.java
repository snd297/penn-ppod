package edu.upenn.cis.ppod.modelinterfaces;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.util.IVisitor;

@XmlJavaTypeAdapter(OTU.Adapter.class)
public interface IOTU
		extends IAttachee, ILabeled, IOTUSetChild, IVersioned, IWithXmlID, IWithPPodId {

	void accept(final IVisitor visitor);

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

	IOTU setInNeedOfNewVersion();

	/**
	 * Set this OTU's label.
	 * 
	 * @param label the label
	 * 
	 * @return this
	 */
	IOTU setLabel(final String label);

}