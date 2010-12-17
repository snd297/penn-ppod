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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

import edu.upenn.cis.ppod.TestGroupDefs;
import edu.upenn.cis.ppod.imodel.IOTUSet;
import edu.upenn.cis.ppod.imodel.IStandardCharacter;
import edu.upenn.cis.ppod.imodel.IVersionInfo;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * Test {@link PPodEntity}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST })
public class PPodEntityTest {

	@Test
	public void addAttachment() {
		final OTUSet otuSet = new OTUSet();
		otuSet.unsetInNeedOfNewVersion();

		final Attachment attachment = new Attachment();
		assertFalse(otuSet.getHasAttachments());
		otuSet.addAttachment(attachment);
		assertEquals(
				getOnlyElement(
						otuSet.getAttachments()), attachment);
		assertTrue(otuSet.getHasAttachments());
		assertTrue(otuSet.isInNeedOfNewVersion());

	}

	@Test
	public void getElementsXmlWNullAttachments() {
		final OTUSet otuSet = new OTUSet();
		assertNull(otuSet.getAttachmentsXml());

		otuSet.setHasAttachments(true);

		assertNotNull(otuSet.getAttachmentsXml());
		assertEquals(otuSet.getAttachmentsXml().size(), 0);

	}

	@Test
	public void getElementsXmlWAttachments() {
		final Attachment attachment0 = new Attachment();
		final Attachment attachment1 = new Attachment();
		final Attachment attachment2 = new Attachment();

		final Set<Attachment> attachments =
				ImmutableSet.of(attachment0,
						attachment1,
						attachment2);

		final OTUSet otuSet = new OTUSet();

		otuSet.addAttachment(attachment0);
		otuSet.addAttachment(attachment1);
		otuSet.addAttachment(attachment2);

		assertEquals(otuSet.getAttachmentsXml(), attachments);
	}

	@Test
	public void beforeMarahal() {
		final OTUSet otuSet = new OTUSet();
		final IVersionInfo versionInfo = new VersionInfo();
		otuSet.setVersionInfo(new VersionInfo());
		otuSet.beforeMarshal(null);
		assertEquals(otuSet.getVersion(), versionInfo.getVersion());
	}

	/**
	 * Make sure {@code PPodEntity.getPPodVersionInfo()} throws an exception for
	 * marshalled objects.
	 */
	@Test(expectedExceptions = IllegalStateException.class)
	public void getMarshalled() {
		final IOTUSet otuSet = new OTUSet();
		((PersistentObject) otuSet).setUnmarshalled(true);
		otuSet.getVersionInfo();
	}

	/**
	 * This is a pretty thorough test of
	 * {@code PPodEntity.removeAttachment(...)}. A refactoring wouldn't hurt.
	 */
	@Test
	public void removeAttachment() {
		final OTUSet otuSet = new OTUSet();
		final Attachment attachment1 = new Attachment();
		final Attachment attachment2 = new Attachment();
		final Attachment attachment3 = new Attachment();
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
		final IStandardCharacter character = new StandardCharacter();

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
		final IOTUSet otuSet = new OTUSet();
		assertNull(otuSet.getVersion());

		final VersionInfo versionInfo = new VersionInfo();

		final Long versionNo = 454L;

		versionInfo.setVersion(versionNo);
		otuSet.setVersionInfo(versionInfo);

		assertEquals(otuSet.getVersion(), versionNo);
	}

	@Test
	public void getVersionInfo() {
		final IOTUSet otuSet = new OTUSet();
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
		final IOTUSet otuSet = new OTUSet();
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

		final IVisitor testVisitor = mock(IVisitor.class);

		otuSet.accept(testVisitor);

		verify(testVisitor, times(expectedAttachments.size())).visitAttachment(
				any(Attachment.class));
		verify(testVisitor).visitAttachment(attachment0);
		verify(testVisitor).visitAttachment(attachment1);
		verify(testVisitor).visitAttachment(attachment2);
	}
}
