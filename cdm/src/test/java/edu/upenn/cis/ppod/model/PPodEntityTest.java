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

import java.util.Set;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.util.TestVisitor;

/**
 * Test {@link PPodEntity}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST, TestGroupDefs.SINGLE },
		dependsOnGroups = TestGroupDefs.INIT)
public class PPodEntityTest {

	@Inject
	private Provider<OTUSet> otuSetProvider;

	@Inject
	private Provider<Attachment> attachmentProvider;

	@Inject
	private Provider<VersionInfo> pPodVersionInfoProvider;

	@Inject
	private Provider<TestVisitor> testVisitorProvider;

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

	@Test
	public void getAttachmentsNoAttachments() {
		final StandardCharacter character = new StandardCharacter();
		assertFalse(character.getHasAttachments());
		assertTrue(isEmpty(character.getAttachments()));
	}

	@Test
	public void getAttachmentsHasAttachments() {
		final StandardCharacter character = new StandardCharacter();

		final Attachment attachment0 = new Attachment();
		final Attachment attachment1 = new Attachment();
		final Attachment attachment2 = new Attachment();
		final Set<Attachment> expectedAttachments =
				ImmutableSet.of(attachment0,
								attachment1,
								attachment2);

		character.addAttachment(attachment0);
		character.addAttachment(attachment1);
		character.addAttachment(attachment2);
		assertEquals(character.getAttachments(), expectedAttachments);

	}

	@Test
	public void getVersion() {
		final OTUSet otuSet = new OTUSet();
		assertNull(otuSet.getVersion());

		final VersionInfo versionInfo = new VersionInfo();

		final Long versionNo = 454L;

		versionInfo.setVersion(versionNo);
		otuSet.setVersionInfo(versionInfo);

		assertEquals(otuSet.getVersion(), versionNo);
	}

	@Test
	public void getVersionInfo() {
		final OTUSet otuSet = new OTUSet();
		final VersionInfo versionInfo = new VersionInfo();
		otuSet.setVersionInfo(versionInfo);
		assertSame(otuSet.getVersionInfo(), versionInfo);
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void getVersionInfoNotUnmarshalled() {
		final OTUSet otuSet = new OTUSet();
		otuSet.setUnmarshalled(true);
		otuSet.getVersionInfo();
	}

	@Test
	public void getAttachmentsXmlWAttachments() {
		final OTUSet otuSet = new OTUSet();
		final Attachment attachment0 = new Attachment();
		final Attachment attachment1 = new Attachment();
		final Attachment attachment2 = new Attachment();
		final Set<Attachment> expectedAttachments =
				ImmutableSet.of(attachment0,
								attachment1,
								attachment2);
		otuSet.addAttachment(attachment0);
		otuSet.addAttachment(attachment1);
		otuSet.addAttachment(attachment2);

		assertEquals(otuSet.getAttachmentsXml(), expectedAttachments);

	}

	@Test
	public void getAttachmensXmlWNoAttachments() {
		final OTUSet otuSet = new OTUSet();
		assertNull(otuSet.getAttachmentsXml());
	}

	@Test
	public void accept() {
		final OTUSet otuSet = new OTUSet();
		final Attachment attachment0 = new Attachment();
		final Attachment attachment1 = new Attachment();
		final Attachment attachment2 = new Attachment();
		final Set<Attachment> expectedAttachments =
				ImmutableSet.of(attachment0,
								attachment1,
								attachment2);

		otuSet.addAttachment(attachment0);
		otuSet.addAttachment(attachment1);
		otuSet.addAttachment(attachment2);

		final TestVisitor testVisitor = testVisitorProvider.get();

		otuSet.accept(testVisitor);

		assertEquals(testVisitor.getVisited().size(),
				1 + otuSet.getAttachments().size());

		assertTrue(testVisitor.getVisited().containsAll(expectedAttachments));

	}
}
