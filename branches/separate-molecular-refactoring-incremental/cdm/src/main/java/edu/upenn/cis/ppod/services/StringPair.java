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
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.util.IPair;

/**
 * Serializing a {@code Pair<String, String>} didn't work right, so we made this
 * class.
 * 
 * @author Sam Donnelly
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class StringPair implements IPair<String, String> {

	private String first;
	private String second;

	/**
	 * Created for JAXB.
	 */
	@SuppressWarnings("unused")
	private StringPair() {}

	@Inject
	StringPair(@Assisted("first") final String first,
			@Assisted("second") final String second) {
		this.first = first;
		this.second = second;
	}

	public String getFirst() {
		return first;
	}

	public String getSecond() {
		return second;
	}

	public static final Function<StringPair, IPair<String, String>> castToIPair = new Function<StringPair, IPair<String, String>>() {

		public IPair<String, String> apply(final StringPair from) {
			return (IPair<String, String>) from;
		}

	};

	public static interface IFactory {
		StringPair create(@Assisted("first") String first,
				@Assisted("second") String second);
	}
}
