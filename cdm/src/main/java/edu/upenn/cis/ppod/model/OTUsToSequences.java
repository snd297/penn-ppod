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
package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An {@code OTUKeyedBiMap} which maps to {@code Sequence}s.
 * 
 * @author Sam Donnelly
 */
public abstract class OTUsToSequences<T extends Sequence, P extends SequenceSet<?>>
		extends OTUKeyedMap<T, P> {

	@Override
	public OTUsToSequences<T, P> clear(final P sequenceSet) {
		for (final Sequence sequence : getOTUsToValues().values()) {
			sequence.unsetSequenceSet();
		}
		super.clear(sequenceSet);
		return this;
	}

	@Override
	protected T putHelper(final OTU key,
			final T value,
			final P parent) {
		checkNotNull(key);
		checkNotNull(value);
		checkNotNull(parent);
		final T originalSequence = super.putHelper(key, value,
				parent);

		// If we are replacing an OTU's sequence, we need to sever the previous
		// sequence's sequence->sequenceSet pointer.
		if (originalSequence != null && !originalSequence.equals(value)) {
			originalSequence.unsetSequenceSet();
		}
		return originalSequence;
	}

}
