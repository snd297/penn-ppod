package edu.upenn.cis.ppod.util;

import com.google.inject.ImplementedBy;

/**
 * Set the doc id on {@code Attachment}s, {@code AttachmentNamespace}s, {@code
 * AttachmentType}s, {@code AbstractCharacter}s, {@code CharacterState}s, {@code OTU}s,
 * {@code OTUSet}s.
 * 
 * @author Sam Donnelly
 */
@ImplementedBy(SetDocIdVisitor.class)
public interface ISetDocIdVisitor extends IVisitor {

}
