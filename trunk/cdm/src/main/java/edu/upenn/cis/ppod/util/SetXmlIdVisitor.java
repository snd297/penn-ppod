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

import edu.upenn.cis.ppod.imodel.IOTU;
import edu.upenn.cis.ppod.imodel.IOTUSet;
import edu.upenn.cis.ppod.model.AttachmentNamespace;
import edu.upenn.cis.ppod.model.AttachmentType;
import edu.upenn.cis.ppod.model.StandardCharacter;
import edu.upenn.cis.ppod.model.StandardState;

/**
 * Set the doc id on {@code Attachment}s, {@code AttachmentNamespace}s,
 * {@code AttachmentType}s, {@code Character}s, {@code CharacterState}s,
 * {@code OTU}s, {@code OTUSet}s.
 * 
 * @author Sam Donnelly
 */
final class SetXmlIdVisitor extends EmptyVisitor implements ISetXmlIdVisitor {

	/**
	 * Call {@link IOTUSet#setXmlId()}.
	 * 
	 * @param otuSet target
	 */
	@Override
	public void visitOTUSet(final IOTUSet otuSet) {
		if (otuSet.getXmlId() == null) {
			otuSet.setXmlId();
		}
	}

	/**
	 * Call {@link IOTU#setXmlId()}
	 * 
	 * @param otu target
	 */
	@Override
	public void visitOTU(final IOTU otu) {
		if (otu.getXmlId() == null) {
			otu.setXmlId();
		}
	}

	/**
	 * Call {@link StandardCharacter#setXmlId()}
	 * 
	 * @param character target
	 */
	@Override
	public void visitStandardCharacter(final StandardCharacter character) {
		if (character.getXmlId() == null) {
			character.setXmlId();
		}
	}

	/**
	 * Call {@code characterState.setDocId()}.
	 * 
	 * @param characterState target
	 */
	@Override
	public void visitStandardState(final StandardState standardState) {
		if (standardState.getXmlId() == null) {
			standardState.setXmlId();
		}
	}

	/**
	 * Call {@code attachmentNamespace.setDocId()}.
	 * 
	 * @param attachmentNamespace target
	 */
	@Override
	public void visitAttachmentNamespace(
			final AttachmentNamespace attachmentNamespace) {
		// Since this will be visited once for every attachment that is out
		// there,
		// we need to check it first.
		if (attachmentNamespace.getXmlId() == null) {
			attachmentNamespace.setXmlId();
		}
	}

	/**
	 * Call {@code attachmentType.setDocId()}.
	 * 
	 * @param attachmentType target
	 */
	@Override
	public void visitAttachmentType(final AttachmentType attachmentType) {
		// Since this will be visited once for every attachment that is out
		// there,
		// we need to check it first.
		if (attachmentType.getXmlId() == null) {
			attachmentType.setXmlId();
		}
	}
}
