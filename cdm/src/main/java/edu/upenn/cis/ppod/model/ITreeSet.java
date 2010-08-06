package edu.upenn.cis.ppod.model;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import edu.upenn.cis.ppod.modelinterfaces.ILabeled;
import edu.upenn.cis.ppod.modelinterfaces.IOTUSet;
import edu.upenn.cis.ppod.modelinterfaces.IOTUSetChild;
import edu.upenn.cis.ppod.modelinterfaces.IUUPPodEntity;
import edu.upenn.cis.ppod.modelinterfaces.IVisitable;
import edu.upenn.cis.ppod.modelinterfaces.IWithXmlID;

@XmlJavaTypeAdapter(TreeSet.Adapter.class)
public interface ITreeSet
		extends ILabeled, IUUPPodEntity, IOTUSetChild, IVisitable, IWithXmlID {

	/**
	 * Add {@code tree} to this {@code TreeSet}.
	 * <p>
	 * It is illegal to add the same tree more than once.
	 * 
	 * @param tree to be added
	 * 
	 * @return {@code tree}
	 */
	Tree addTree(final Tree tree);

	/**
	 * Get the parent OTU set.
	 * 
	 * @return the value
	 */
	@Nullable
	IOTUSet getParent();

	List<Tree> getTrees();

	ITreeSet setLabel(final String label);

	ITreeSet setParent(@CheckForNull final IOTUSet parent);

	List<Tree> setTrees(final List<? extends Tree> trees);

}