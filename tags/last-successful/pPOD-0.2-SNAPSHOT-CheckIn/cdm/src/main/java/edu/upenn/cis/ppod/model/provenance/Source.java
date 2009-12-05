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
package edu.upenn.cis.ppod.model.provenance;

import java.util.List;

/**
 * @author Wayne Maddison
 */
public class Source {
	/**
	 * human-readable name of type of source, e.g. Specimen, Image,
	 * Chromatogram, etc. for use in GUI
	 * 
	 */
	private String type;

	/**
	 * Optional use, human readable name of particular source.
	 */
	private String label;

	/**
	 * internal bookkeeping, though would be good if it were GUID to permit
	 * checking against blessed repository of original to see if updated
	 */
	private String uniqueID;

	List<Author> authors;

	/** nouns */
	List<Source> material;

	/** verbs */
	List<Method> methods;

	/**
	 * comments, as needed only. Could include assessment of certainty?
	 */
	List<Note> notes;

}
