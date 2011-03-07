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
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * Test {@link PPodEntity}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = { TestGroupDefs.FAST })
public class PPodEntityTest {

	@Test
	public void addAttachment() {
		final OtuSet otuSet = new OtuSet();

		final Attachment attachment = new Attachment();
		assertFalse(otuSet.getHasAttachments());
		otuSet.addAttachment(attachment);
		assertEquals(
				getOnlyElement(
						otuSet.getAttachments()), attachment);
		assertTrue(otuSet.getHasAttachments());

	}

	/**
	 * This is a pretty thorough test of
	 * {@code PPodEntity.removeAttachment(...)}. A refactoring wouldn't hurt.
	 */
	@Test
	public void removeAttachment() {
		final OtuSet otuSet = new OtuSet();
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

}
