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
package edu.upenn.cis.ppod.saveorupdate;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryProvider;

import edu.upenn.cis.ppod.model.DNASequence;
import edu.upenn.cis.ppod.model.DNASequenceSet;

/**
 * @author Sam Donnelly
 * 
 */
public class SaveOrUpdateModule extends AbstractModule {

	private final static class IMergeMolecularSequenceSetsIFactoryTypeLiteral
			extends
			TypeLiteral<IMergeMolecularSequenceSets.IFactory<DNASequenceSet, DNASequence>> {}

	private final static class MergeMolecularSequenceSetsTypeLiteral
			extends
			TypeLiteral<MergeMolecularSequenceSets<DNASequenceSet, DNASequence>> {}

	@Override
	protected void configure() {
		final TypeLiteral<MergeMolecularSequenceSets<DNASequenceSet, DNASequence>> mergeDNASequenceSetTypeLiteral =
				new MergeMolecularSequenceSetsTypeLiteral();
		final TypeLiteral<IMergeMolecularSequenceSets.IFactory<DNASequenceSet, DNASequence>> mergeDNASequencesFactoryTypeLiteral =
				new IMergeMolecularSequenceSetsIFactoryTypeLiteral();

		bind(mergeDNASequencesFactoryTypeLiteral).toProvider(
				FactoryProvider.newFactory(mergeDNASequencesFactoryTypeLiteral,
						mergeDNASequenceSetTypeLiteral));

	}

}
