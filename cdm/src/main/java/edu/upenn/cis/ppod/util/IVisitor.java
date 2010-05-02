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
package edu.upenn.cis.ppod.util;

import edu.upenn.cis.ppod.model.Attachment;
import edu.upenn.cis.ppod.model.AttachmentNamespace;
import edu.upenn.cis.ppod.model.AttachmentType;
import edu.upenn.cis.ppod.model.CategoricalMatrix;
import edu.upenn.cis.ppod.model.Character;
import edu.upenn.cis.ppod.model.CategoricalState;
import edu.upenn.cis.ppod.model.CategoricalCell;
import edu.upenn.cis.ppod.model.CategoricalRow;
import edu.upenn.cis.ppod.model.DNASequence;
import edu.upenn.cis.ppod.model.DNASequenceSet;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUKeyedMap;
import edu.upenn.cis.ppod.model.OTUSet;
import edu.upenn.cis.ppod.model.Study;
import edu.upenn.cis.ppod.model.Tree;
import edu.upenn.cis.ppod.model.TreeSet;

/**
 * The visitor in the visitor pattern.
 * <p>
 * <a href="http://en.wikipedia.org/wiki/Visitor_pattern">wikipedia visitor
 * entry</a>
 * 
 * @author Sam Donnelly
 */
public interface IVisitor {

	/**
	 * Visit an attachment
	 * 
	 * @param attachment visitee
	 */
	void visit(Attachment attachment);

	/**
	 * Visit an attachment namespace.
	 * 
	 * @param the attachmentNamespace visitee
	 */
	void visit(AttachmentNamespace attachemntNamespace);

	/**
	 * Visit an attachment type.
	 * 
	 * @param the attachment type visitee
	 */
	void visit(AttachmentType attachmentType);

	/**
	 * Visit a character
	 * 
	 * @param the character visitee
	 */
	void visit(Character character);

	/**
	 * Visit the character state.
	 * 
	 * @param characterState the character state visitee
	 */
	void visit(CategoricalState categoricalState);

	void visit(CategoricalCell cell);

	void visit(CategoricalMatrix matrix);

	void visit(CategoricalRow row);

	void visit(OTU otu);

	void visit(OTUSet otuset);

	void visit(OTUKeyedMap<?> otuKeyedBimap);

	void visit(Study study);

	void visit(TreeSet treeSet);

	void visit(Tree tree);

	void visit(DNASequenceSet dnaSequenceSet);

	void visit(DNASequence dnaSequence);

}
