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
package edu.upenn.cis.ppod.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Serializing a {@code Pair<String, String>} didn't work right, so we made this
 * class.
 * 
 * @author Sam Donnelly
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class StringPair {

	private String first;
	private String second;

	/**
	 * Created for JAXB.
	 */
	StringPair() {}

	public StringPair(final String first,
			final String second) {
		this.first = first;
		this.second = second;
	}

	public String getFirst() {
		return first;
	}

	public String getSecond() {
		return second;
	}

	/**
	 * This seemingly redundant setter method added for the sake of JAXB.
	 */
	public void setFirst(final String first) {
		this.first = first;
	}

	/**
	 * This seemingly redundant setter method added for the sake of JAXB.
	 */
	public void setSecond(final String second) {
		this.second = second;
	}
}
