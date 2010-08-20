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

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A child that is in an ordered list in its parent.
 * 
 * @author Sam Donnelly
 * 
 * @param <P>
 */
public interface IOrderedChild<P> extends IChild<P> {

	@Nullable
	Integer getPosition();

	/**
	 * Set the position of this child.
	 * <p>
	 * Use a {@code null} when removing a child from its parent
	 * <p>
	 * There is no reason for client code to call this method as the value will
	 * always be set by the parent object.
	 * 
	 * @param position the position of this child
	 */
	void setPosition(@CheckForNull final Integer position);

}
