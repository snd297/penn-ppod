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
package edu.upenn.cis.ppod.util;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;

import edu.upenn.cis.ppod.imodel.IOTU;

/**
 * @author Sam Donnelly
 * 
 */
public class OTUSomethingPair<U> extends Pair<IOTU, U> {

	@XmlElement
	@XmlIDREF
	@Override
	public IOTU getFirst() {
		return super.getFirst();
	}

	/**
	 * This seemingly redundant setter method added for the sake of JAXB.
	 */
	@Override
	public OTUSomethingPair<U> setFirst(final IOTU otu) {
		return (OTUSomethingPair<U>) super.setFirst(otu);
	}

}
