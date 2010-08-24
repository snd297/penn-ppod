/*
s * Copyright (C) 2010 Trustees of the University of Pennsylvania
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

import javax.annotation.Nullable;

/**
 * An object that holds a write-once-read-many {@link XmlID}.
 * <p>
 * One may ask: why require that the client explicitly set the doc id? And why
 * only allow it to be sent once? Why not just have it assigned automatically at
 * construction and have it freely reset if required. The reason is that
 * sometimes a client needs to set it to a certain value using
 * {@link #setDocId(String)} and since the {@link #getDocId()} is an identifier,
 * it seems dangerous to allow it to be reset since as soon as it's assigned,
 * something may be depending on that value.
 * 
 * @author Sam Donnelly
 */
public interface IWithDocId {

	/**
	 * Get the {@link javax.xml.bind.annotation.XmlID} attribute.
	 * <p>
	 * Will be {@code null} until one of the {@code setDocId(...)}s are called
	 * by the client, but never {@code null} after that.
	 * 
	 * @return the {@code XmlID} attribute
	 */
	@Nullable
	String getDocId();

	/**
	 * Create and set this {@code IWithXmlID}'s doc id.
	 * 
	 * @throws IllegalStateException if {@link #getXmlId()}{@code != null} when
	 *             this method is called
	 */
	void setDocId();

	/**
	 * Set this {@code IWithDocID}'s xml id.
	 * 
	 * @param docId the xml id
	 * 
	 * @throws IllegalStateException if {@link #getDocId()}{@code != null} when
	 *             this method is called
	 */
	void setDocId(final String docId);

}
