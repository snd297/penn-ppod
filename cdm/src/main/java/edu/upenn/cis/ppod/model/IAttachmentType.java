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
package edu.upenn.cis.ppod.model;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import edu.upenn.cis.ppod.imodel.IVisitable;
import edu.upenn.cis.ppod.imodel.IWithDocId;

@XmlJavaTypeAdapter(AttachmentType.Adapter.class)
public interface IAttachmentType extends IVisitable, IWithDocId {

	/**
	 * Get the label. Will be {@code null} for newly created objects until
	 * {@link #setLabel(String)} is called and then will never be {@code null}
	 * 
	 * @return the label
	 */
	@Nullable
	String getLabel();

	/**
	 * Set the label.
	 * 
	 * @param label the label
	 */
	void setLabel(String label);

	/**
	 * Set the namespace.
	 * 
	 * @param namespace the namespace to set
	 */
	void setNamespace(IAttachmentNamespace namespace);

	/**
	 * Get the namespace. Will be {@code null} for newly constructed objects,
	 * once the namespace is set it will never be {@code null}.
	 * 
	 * @return the namespace
	 */
	public IAttachmentNamespace getNamespace();

}