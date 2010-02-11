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
import edu.upenn.cis.ppod.model.Character;
import edu.upenn.cis.ppod.model.CharacterState;
import edu.upenn.cis.ppod.model.OTU;
import edu.upenn.cis.ppod.model.OTUSet;

/**
 * @author Sam Donnelly
 * 
 */
public class SetDocIdVisitor extends EmptyVisitor {

	/**
	 * Call {@code otuSet.setDocId()}.
	 * 
	 * @param otuSet target
	 */
	@Override
	public void visit(final OTUSet otuSet) {
		// We'd rather not do this but it is required by
		// the Mesquite modules
		if (otuSet.getDocId() == null) {
			otuSet.setDocId();
		}
	}

	/**
	 * Call {@code otu.setDocId()}.
	 * 
	 * @param otu target
	 */
	@Override
	public void visit(final OTU otu) {
		// We'd rather not do this but it is required by
		// the Mesquite modules
		if (otu.getDocId() == null) {
			otu.setDocId();
		}
	}

	/**
	 * Call {@code character.setDocId()}.
	 * 
	 * @param character target
	 */
	@Override
	public void visit(final Character character) {
		character.setDocId();
	}

	/**
	 * Call {@code characterState.setDocId()}.
	 * 
	 * @param characterState target
	 */
	@Override
	public void visit(final CharacterState characterState) {
		characterState.setDocId();
	}

	/**
	 * Call {@code attachmentNamespace.setDocId()}.
	 * 
	 * @param attachmentNamespace target
	 */
	@Override
	public void visit(final AttachmentNamespace attachmentNamespace) {
		// Since this will be visited once for every attachment that is out
		// there,
		// we need to check it first.
		if (attachmentNamespace.getDocId() == null) {
			attachmentNamespace.setDocId();
		}
	}

	/**
	 * Call {@code attachmentType.setDocId()}.
	 * 
	 * @param attachmentType target
	 */
	@Override
	public void visit(final AttachmentType attachmentType) {
		// Since this will be visited once for every attachment that is out
		// there,
		// we need to check it first.
		if (attachmentType.getDocId() == null) {
			attachmentType.setDocId();
		}
	}

	/**
	 * Call {@code attachmnent.setDocId()}.
	 * 
	 * @param attachment target
	 */
	@Override
	public void visit(final Attachment attachment) {
		attachment.setDocId();
	}
}
