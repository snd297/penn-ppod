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

import java.util.Set;

import javax.annotation.CheckForNull;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import edu.upenn.cis.ppod.model.StandardCharacter;

@XmlJavaTypeAdapter(StandardCharacter.Adapter.class)
public interface IStandardCharacter
		extends IChild<IStandardMatrix>, IUUPPodEntity, IWithDocId, ILabeled {

	/**
	 * Add <code>state</code> into this <code>Character</code>.
	 * <p>
	 * Calling this handles both sides of the <code>Character</code><->
	 * <code>CharacterState</code>s. relationship.
	 * 
	 * @param state what we're adding
	 * 
	 * @return the previous state that was associated with
	 *         {@code state.getStateNumber()} or {@code null} if there was no
	 *         such state.
	 */
	@CheckForNull
	IStandardState addState(IStandardState state);

	/**
	 * Get the state with the given state number, or {@code null} if there is no
	 * such state.
	 * 
	 * @param stateNumber the state number of the state we want to retrieve
	 * 
	 * @return the state with the given state number, or {@code null} if there
	 *         is no such state.
	 */
	@CheckForNull
	IStandardState getState(Integer stateNumber);

	/**
	 * Get the states of this character.
	 * 
	 * @return the states of this character.
	 */
	Set<IStandardState> getStates();

	/**
	 * Set the label of this character
	 * 
	 * @param label the value for the label
	 * 
	 * @return this
	 */
	IStandardCharacter setLabel(String label);

}