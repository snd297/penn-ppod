/*
 * Copyright (C) 2009 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS of ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.services.ppodentity;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.List;
import java.util.Set;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.hibernate.annotations.Cascade;

import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.model.TreeSet;

/**
 * @author Sam Donnelly
 * 
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings
public class XmlOTUSet {
	/**
	 * Globally non-unique label.
	 * <p>
	 * OTU set labels are unique within a particular <code>Study</code>.
	 */
	@XmlAttribute
	private String label;

	/** The set of {@code OTU}s that this {@code OTUSet} contains. */
	@XmlElement(name = "otu")
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	private List<OTU> otus = newArrayList();

	/** The matrices which reference this OTU set. */
	@XmlElement(name = "matrix")
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	private List<CharacterStateMatrix> matrices = newArrayList();

	/** The tree sets that reference this OTU set. */
	@XmlElement(name = "treeSet")
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	private List<TreeSet> treeSets = newArrayList();

	/** Free-form description. */
	@XmlAttribute
	private String description;

	@ManyToOne
	@JoinColumn(name = Study.ID_COLUMN, updatable = false, insertable = false)
	private Study study;
}
