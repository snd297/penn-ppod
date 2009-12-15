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
package edu.upenn.cis.ppod.util;

import static com.google.common.collect.Iterables.getOnlyElement;

import javax.xml.bind.JAXBContext;

import org.testng.annotations.DataProvider;

import edu.upenn.cis.ppod.model.CharacterStateMatrix;
import edu.upenn.cis.ppod.model.Study;

/**
 * A TestNG {@link DataProvider} that reads xml-serialized {@link Study}s from
 * the root of the class path and delivers them as {@link CharacterStateMatrix}
 * s.
 * 
 * @author Sam Donnelly
 */
public class MatrixProvider {

	public static final String SMALL_SIMPLE_MATRIX_PROVIDER = "MX54O";

	@DataProvider(name = SMALL_SIMPLE_MATRIX_PROVIDER)
	public static Object[][] createMatrix() throws Exception {

		final JAXBContext ctx = JAXBContext.newInstance(Study.class);
		final Study study = (Study) ctx.createUnmarshaller().unmarshal(
				MatrixProvider.class.getResourceAsStream("/MX540.xml"));
		final CharacterStateMatrix smallSimpleMatrix = getOnlyElement(getOnlyElement(
				study.getOTUSets()).getMatrices());
		return new Object[][] { new Object[] { smallSimpleMatrix } };
	}
}
