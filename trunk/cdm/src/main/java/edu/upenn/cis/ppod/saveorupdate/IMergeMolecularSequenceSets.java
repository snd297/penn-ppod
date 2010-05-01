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

import edu.upenn.cis.ppod.dao.IDAO;
import edu.upenn.cis.ppod.model.Sequence;
import edu.upenn.cis.ppod.model.SequenceSet;
import edu.upenn.cis.ppod.modelinterfaces.INewPPodVersionInfo;

/**
 * @author Sam Donnelly
 * 
 * @param <SS> the kind of {@link SequenceSet} we're operating on
 * @param <S> the kind of {@link Sequence} that belongs in the sequence
 *            set
 */
public interface IMergeMolecularSequenceSets<SS extends SequenceSet<S>, S extends Sequence<SS>> {

	
	public void merge(final SS targetSequenceSet, final SS sourceSequenceSet);

	static interface IFactory<SS extends SequenceSet<S>, S extends Sequence<SS>> {


		IMergeMolecularSequenceSets<SS, S> create(IDAO<Object, Long> dao,
				INewPPodVersionInfo newPPodVersionInfo);
	}
}