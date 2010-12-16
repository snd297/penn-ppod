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
import edu.upenn.cis.ppod.util.Pair;

/**
 * Serializing a {@code Pair<String, String>} didn't work right, so we made this
 * class.
 * 
 * @author Sam Donnelly
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class StringPair extends Pair<String, String> {

	public static final Function<StringPair, IPair<String, String>> castToIPair = new Function<StringPair, IPair<String, String>>() {

		public IPair<String, String> apply(final StringPair from) {
			return from;
		}

	};

	/**
	 * Created for JAXB.
	 */
	@SuppressWarnings("unused")
	private StringPair() {}

	public StringPair(final String first,
			final String second) {
		super(first, second);
	}

	@Override
	public String getFirst() {
		return super.getFirst();
	}

	@Override
	public String getSecond() {
		return super.getSecond();
	}

	/**
	 * This seemingly redundant setter method added for the sake of JAXB.
	 */
	@Override
	protected void setFirst(final String first) {
		super.setFirst(first);
	}

	/**
	 * This seemingly redundant setter method added for the sake of JAXB.
	 */
	@Override
	protected void setSecond(final String second) {
		super.setSecond(second);
	}
}
