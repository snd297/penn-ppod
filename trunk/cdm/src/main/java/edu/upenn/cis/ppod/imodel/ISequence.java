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
package edu.upenn.cis.ppod.imodel;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public interface ISequence<SS extends ISequenceSet<?>>
		extends IChild<SS>, IPPodEntity {

	/**
	 * Get the accession.
	 * 
	 * @return the accession
	 */
	@XmlAttribute
	@CheckForNull
	String getAccession();

	/**
	 * Get the description.
	 * 
	 * @return the description
	 */
	@XmlAttribute
	@CheckForNull
	String getDescription();

	/**
	 * Get the name.
	 * 
	 * @return the name
	 */
	@XmlAttribute
	@CheckForNull
	String getName();

	/**
	 * Get the sequence string.
	 * <p>
	 * This will only be {@code null} for newly created objects until
	 * {@link #setSequence(String)} is called. 
	 * 
	 * @return the sequence string
	 */
	@XmlElement
	@Nullable
	String getSequence();

	ISequence<SS> setAccession(
			@CheckForNull final String accession);

	ISequence<SS> setDescription(
			@CheckForNull final String newDescription);

	ISequence<SS> setName(@CheckForNull final String name);

	ISequence<SS> setSequence(final String sequence);

}