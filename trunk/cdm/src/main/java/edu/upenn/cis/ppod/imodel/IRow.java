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

import java.util.List;

public interface IRow<C extends ICell<?, ?>, M extends IMatrix<?>>
		extends IOTUKeyedMapValue<M>, IPPodEntity, IVisitable {
	/**
	 * Get the cells that make up this row.
	 * 
	 * @return the cells that make up this row
	 */
	List<C> getCells();

	/**
	 * Set the cells of this row.
	 * <p>
	 * This handles both sides of the {@code Row<->Cell} relationship.
	 * 
	 * @param cells the cells
	 * 
	 * @return any cells which were removed as a result of this operation
	 * 
	 */
	List<C> setCells(final List<? extends C> cells);

}