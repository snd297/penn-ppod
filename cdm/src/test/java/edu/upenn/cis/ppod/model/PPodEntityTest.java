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

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Sets.newHashSet;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.Collections;
import java.util.Set;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST }, dependsOnGroups = TestGroupDefs.INIT)
public class PPodEntityTest {

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<Attachment> attachmentProvider;

	@Inject
	private Provider<VersionInfo> pPodVersionInfoProvider;

	@Test
	public void addAttachment() {
		final OTUSet otuSet = otuSetProvider.get();
		otuSet.unsetInNeedOfNewVersion();

		final Attachment attachment = attachmentProvider.get();
		assertFalse(otuSet.getHasAttachments());
		otuSet.addAttachment(attachment);
		assertEquals(
				getOnlyElement(
						otuSet.getAttachments()), attachment);
		assertTrue(otuSet.getHasAttachments());
		assertTrue(otuSet.isInNeedOfNewVersion());

	}

	@Test
	public void beforeMarahal() {
		final OTUSet otuSet = otuSetProvider.get();
		final VersionInfo versionInfo = pPodVersionInfoProvider.get();
		otuSet.setVersionInfo(pPodVersionInfoProvider.get());
		otuSet.beforeMarshal(null);
		assertEquals(otuSet.getVersion(), versionInfo.getVersion());
	}

	/**
	 * Make sure {@code PPodEntity.getPPodVersionInfo()} throws an exception for
	 * marshalled objects.
	 */
	@Test(expectedExceptions = IllegalStateException.class)
	public void getMarshalled() {
		final OTUSet otuSet = otuSetProvider.get();
		((PersistentObject) otuSet).setUnmarshalled(true);
		otuSet.getVersionInfo();
	}

	/**
	 * This is a pretty thorough test of
	 * {@code PPodEntity.removeAttachment(...)}. A refactoring wouldn't hurt.
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
		assertEquals(otuSet.getAttachments(),
				newHashSet(
						attachment1,
						attachment2, attachment3));
		final boolean returnBoolean = otuSet.removeAttachment(attachment2);
		assertTrue(returnBoolean);
		assertEquals(newHashSet(otuSet.getAttachments()),
				newHashSet(
						attachment1,
						attachment3));
		assertTrue(otuSet.getHasAttachments());

		otuSet.removeAttachment(attachment1);
		otuSet.removeAttachment(attachment3);
		assertTrue(isEmpty(otuSet.getAttachments()));
		assertFalse(otuSet.getHasAttachments());

		final boolean returnBoolean2 = otuSet.removeAttachment(attachment3);
		assertFalse(returnBoolean2);
	}

	@Test(groups = TestGroupDefs.SINGLE)
	public void setAttachments() {

		final Attachment attachment0 = attachmentProvider.get();
		final Attachment attachment1 = attachmentProvider.get();
		final Attachment attachment2 = attachmentProvider.get();
		final Set<Attachment> attachments =
				ImmutableSet.of(attachment0,
						attachment1,
						attachment2);

		final OTUSet otuSet = otuSetProvider.get();
		otuSet.unsetInNeedOfNewVersion();

		final Set<Attachment> returnedAttachments =
				otuSet.setAttachments(attachments);

		assertTrue(otuSet.isInNeedOfNewVersion());
		assertTrue(isEmpty(returnedAttachments));
		assertEquals(otuSet.getAttachments(),
				attachments);

		for (final Attachment attachment : otuSet.getAttachments()) {
			assertSame(attachment.getAttachee(), otuSet);
		}

		otuSet.unsetInNeedOfNewVersion();

		final Set<Attachment> attachments02 =
				ImmutableSet.of(attachment0, attachment2);

		final Set<Attachment> attachments1 =
				ImmutableSet.of(attachment1);

		final Set<Attachment> returnedAttachments2 =
				otuSet.setAttachments(attachments02);

		assertTrue(otuSet.isInNeedOfNewVersion());
		assertEquals(returnedAttachments2, attachments1);
		assertEquals(otuSet.getAttachments(), attachments02);
		assertNull(attachment1.getAttachee());

		for (final Attachment attachment : otuSet.getAttachments()) {
			assertSame(attachment.getAttachee(), otuSet);
		}

		otuSet.unsetInNeedOfNewVersion();

		final Set<Attachment> returnedAttachments4 =
				otuSet.setAttachments(attachments02);
		assertFalse(otuSet.isInNeedOfNewVersion());
		assertTrue(isEmpty(returnedAttachments4));
		assertEquals(otuSet.getAttachments(), attachments02);

		for (final Attachment attachment : otuSet.getAttachments()) {
			assertSame(attachment.getAttachee(), otuSet);
		}

		final Set<Attachment> noAttachments = Collections.emptySet();

		otuSet.unsetInNeedOfNewVersion();

		final Set<Attachment> returnedAttachments3 =
				otuSet.setAttachments(noAttachments);
		assertTrue(otuSet.isInNeedOfNewVersion());
		assertTrue(isEmpty(otuSet.getAttachments()));
		assertEquals(returnedAttachments3, attachments02);

		for (final Attachment attachment : attachments) {
			assertNull(attachment.getAttachee());
		}
	}
}
