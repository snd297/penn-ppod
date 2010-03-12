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

	public static final String SMALL_MATRICES_PROVIDER = "SMALL_MATRICES_PROVIDER";

	private static AfterUnmarshalVisitor afterUnmarshalVisitor = new AfterUnmarshalVisitor();

	@DataProvider(name = SMALL_MATRICES_PROVIDER)
	public static Object[][] createMatrix() throws Exception {

		final JAXBContext ctx = JAXBContext.newInstance(Study.class);
		final Study studyMX540 = (Study) ctx.createUnmarshaller().unmarshal(
				MatrixProvider.class.getResourceAsStream("/MX540.xml"));
		studyMX540.accept(afterUnmarshalVisitor);
	
		final CharacterStateMatrix smallSimpleMatrix = getOnlyElement(getOnlyElement(
				studyMX540.getOTUSets()).getMatrices());

		final Study studyM1808 = (Study) ctx.createUnmarshaller().unmarshal(
				MatrixProvider.class.getResourceAsStream("/M1808.nex.xml"));

		studyM1808.accept(afterUnmarshalVisitor);
		final CharacterStateMatrix smallDNAMatrix = getOnlyElement(getOnlyElement(
				studyM1808.getOTUSets()).getMatrices());

		return new Object[][] { new Object[] { smallSimpleMatrix },
				new Object[] { smallDNAMatrix } };

	}
}
