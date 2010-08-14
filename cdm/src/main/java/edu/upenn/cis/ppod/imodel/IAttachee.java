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
package edu.upenn.cis.ppod.imodel;

import java.util.Set;


/**
 * An object to which we can add and remove attachments.
 * 
 * @author Sam Donnelly
 */
public interface IAttachee {

	/**
	 * Add an attachment to this {@code IAttachee}.
	 * 
	 * @param attachment to be attached
	 * 
	 * @return {@code attachment}.
	 */
	IAttachment addAttachment(IAttachment attachment);

	/**
	 * Get the attachments of this {@code IAttachee}.
	 * 
	 * @return the attachments of this {@code IAttachee}
	 */
	Set<IAttachment> getAttachments();

	/**
	 * Get all attachments within the given namespace.
	 * 
	 * @param namespace the namespace
	 * 
	 * @return all attachments within the given namespace
	 */
	Set<IAttachment> getAttachmentsByNamespace(String namespace);

	/**
	 * Get all attachments within the given namespace and of the given type.
	 * 
	 * @param namespace the namespace
	 * @param type the attachment type
	 * 
	 * @return all attachments within the given namespace and of the given type.
	 */
	Set<IAttachment> getAttachmentsByNamespaceAndType(
			String namespace,
			String type);
	
	/**
	 * Remove {@code attachment} from this {@code IAttachee}'s attachments.
	 * 
	 * @param attachment to be removed
	 * 
	 * @return {@code true} if the set contained the specified element
	 */
	boolean removeAttachment(IAttachment attachment);
}
