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

import static com.google.common.collect.Sets.newHashSet;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Collections;

import org.testng.annotations.Test;

import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.FAST, dependsOnGroups = TestGroupDefs.INIT)
public class PPodEntityTest {

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<Attachment> attachmentProvider;

	@Inject
	private Provider<PPodVersionInfo> pPodVersionInfoProvider;

	@Test
	public void addAttachment() {
		final OTUSet otuSet = otuSetProvider.get();
		final Attachment attachment = attachmentProvider.get();
		assertFalse(otuSet.getHasAttachments());
		otuSet.addAttachment(attachment);
		assertEquals(Iterators.getOnlyElement(otuSet.getAttachmentsIterator()),
				attachment);
		assertTrue(otuSet.getHasAttachments());
	}

	/**
	 * This is a pretty thorough test of {@code
	 * PPodEntity.removeAttachment(...)}. A refactoring wouldn't hurt.
	 */
	@Test
	public void removeAttachment() {
		final OTUSet otuSet = otuSetProvider.get();
		final Attachment attachment1 = attachmentProvider.get();
		final Attachment attachment2 = attachmentProvider.get();
		final Attachment attachment3 = attachmentProvider.get();
		otuSet.addAttachment(attachment1);
		otuSet.addAttachment(attachment2);
		otuSet.addAttachment(attachment3);
		assertEquals((Object) newHashSet(otuSet.getAttachmentsIterator()),
				(Object) newHashSet(
						attachment1,
						attachment2, attachment3));
		final boolean returnBoolean = otuSet.removeAttachment(attachment2);
		assertTrue(returnBoolean);
		assertEquals((Object) newHashSet(otuSet.getAttachmentsIterator()),
				(Object) newHashSet(
						attachment1,
						attachment3));
		assertTrue(otuSet.getHasAttachments());

		otuSet.removeAttachment(attachment1);
		otuSet.removeAttachment(attachment3);
		assertEquals((Object) newHashSet(otuSet.getAttachmentsIterator()),
				(Object) Collections
						.emptySet());
		assertFalse(otuSet.getHasAttachments());

		final boolean returnBoolean2 = otuSet.removeAttachment(attachment3);
		assertFalse(returnBoolean2);
	}

	/**
	 * Make sure {@code PPodEntity.getPPodVersionInfo()} throws an exception for
	 * marshalled objects.
	 */
	@Test(expectedExceptions = IllegalStateException.class)
	public void getMarshalled() {
		final OTUSet otuSet = otuSetProvider.get();
		((PersistentObject) otuSet).setMarshalled(true);
		otuSet.getPPodVersionInfo();
	}

	@Test
	public void beforeMarahal() {
		final OTUSet otuSet = otuSetProvider.get();
		final PPodVersionInfo pPodVersionInfo = pPodVersionInfoProvider.get();
		otuSet.setPPodVersionInfo(pPodVersionInfoProvider.get());
		otuSet.beforeMarshal(null);
		assertEquals(otuSet.getPPodVersion(), pPodVersionInfo.getPPodVersion());
	}
}
