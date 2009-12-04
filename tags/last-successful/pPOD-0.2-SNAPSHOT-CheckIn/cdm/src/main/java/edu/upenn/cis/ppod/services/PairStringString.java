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

import edu.upenn.cis.ppod.util.IPair;

/**
 * Serializing a {@code Pair<String, String>} didn't work right, so we made this
 * class.
 * 
 * @author Sam Donnelly
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class PairStringString implements IPair<String, String> {

	private String first;
	private String second;

	private PairStringString() {}

	public PairStringString(final String first, final String second) {
		this.first = first;
		this.second = second;
	}

	public String getFirst() {
		return first;
	}

	public String getSecond() {
		return second;
	}

	public static PairStringString of(IPair<String, String> pair) {
		return new PairStringString(pair.getFirst(), pair.getSecond());
	}

	public static final Function<PairStringString, IPair<String, String>> castToIPair = new Function<PairStringString, IPair<String, String>>() {

		public IPair<String, String> apply(final PairStringString from) {
			return (IPair<String, String>) from;
		}

	};

	public static final Function<IPair<String, String>, PairStringString> of = new Function<IPair<String, String>, PairStringString>() {

		public PairStringString apply(final IPair<String, String> from) {
			return of(from);
		}

	};

}
