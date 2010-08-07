/*
 * Copyright (C) 2010 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.modelinterfaces;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import edu.upenn.cis.ppod.model.TreeSet;

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
	ITree addTree(final ITree tree);

	/**
	 * Get the parent OTU set.
	 * 
	 * @return the value
	 */
	@Nullable
	IOTUSet getParent();

	List<ITree> getTrees();

	ITreeSet setLabel(final String label);

	ITreeSet setParent(@CheckForNull final IOTUSet parent);

	List<ITree> setTrees(final List<? extends ITree> trees);

}