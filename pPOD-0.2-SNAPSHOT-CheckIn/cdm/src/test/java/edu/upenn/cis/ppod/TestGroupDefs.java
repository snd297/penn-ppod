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
package edu.upenn.cis.ppod;

/**
 * Test groups definitions.
 * 
 * @author Sam Donnelly
 */
public class TestGroupDefs {
	/**
	 * Handler group for tests that take a long amount of time, generally >=.1s.
	 * Such tests would not be suitable to run on every check-in, but instead on
	 * a nightly basis.
	 */
	public static final String SLOW = "slow";

	/**
	 * Handler group for tests that take a short amount of time, generally <.1s.
	 * Such tests should be suitable to be run on every check-in.
	 */
	public static final String FAST = "fast";

	/**
	 * Group for tests that are broken and should be skipped.
	 */
	public static final String BROKEN = "broken";

	/**
	 * Group for initialization tests.
	 */
	public static final String INIT = "init";

	/**
	 * Group for tests that are in development and should not be run as part of
	 * a normal test suite.
	 */
	public static final String IN_DEVELOPMENT = "in-development";

	/**
	 * Prevent inheritance and instantiation.
	 * 
	 * @throws UnsupportedOperationException always
	 */
	private TestGroupDefs() {
		throw new AssertionError("Can't instantiate a TestGroupDefs");
	}
}
