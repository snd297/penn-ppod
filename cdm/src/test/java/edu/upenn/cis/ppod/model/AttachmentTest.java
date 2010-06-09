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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * Tests for {@link Attachment}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.FAST, dependsOnGroups = TestGroupDefs.INIT)
public class AttachmentTest {

	@Inject
	private Provider<StandardCharacter> standardCharacterProvider;

	@Inject
	private Provider<Attachment> attachmentProvider;

	/**
	 * Run {@link Attachment#setBytesValue(byte[])} through its paces:
	 * <ol>
	 * <li>straight set and verify in need of new pPOD version</li>
	 * <li>set w/ already-value and make sure its not in need of a new pPOD
	 * version</li>
	 * <li>set w/ a new byte array that has the same length as the one it
	 * already has. Because the attachment will reuse the same byte array in
	 * this case and its another branch</li>
	 * <li>set w/ a null value</li>
	 * </ol>
	 */
	@Test
	public void setBytesValue() {

		final Attachment attachment = attachmentProvider.get();

		assertNull(attachment.getBytesValue());

		final byte[] bytesValue = new byte[] { 1, 3, 5 };
		final Attachment attachmentReturned = attachment
				.setBytesValue(bytesValue);

		assertSame(attachmentReturned, attachment);
		assertEquals(attachment.getBytesValue(), bytesValue);

		assertTrue(attachment.isInNeedOfNewVersion());

		attachment.unsetInNeedOfNewVersion();

		attachment.setBytesValue(bytesValue);

		assertFalse(attachment.isInNeedOfNewVersion());

		attachment.unsetInNeedOfNewVersion();

		final byte[] bytesValue2 = new byte[] { 3, 5, 3 };

		attachment.setBytesValue(bytesValue2);

		assertEquals(attachment.getBytesValue(), bytesValue2);

		attachment.unsetInNeedOfNewVersion();

		attachment.setBytesValue(null);

		assertNull(attachment.getBytesValue());

	}

	@Test
	public void setLabel() {
		final Attachment attachment = attachmentProvider.get();
		assertNull(attachment.getLabel());

		final String label = "LABEL";

		assertFalse(attachment.isInNeedOfNewVersion());

		final Attachment attachmentReturned = attachment.setLabel(label);
		assertSame(attachmentReturned, attachment);
		assertEquals(attachment.getLabel(), label);

		assertTrue(attachment.isInNeedOfNewVersion());

		attachment.unsetInNeedOfNewVersion();

		attachment.setLabel(label);

		assertEquals(attachment.getLabel(), label);

		assertFalse(attachment.isInNeedOfNewVersion());

		final String label2 = "LABEL2";

		attachment.unsetInNeedOfNewVersion();

		attachment.setLabel(label2);

		assertEquals(attachment.getLabel(), label2);

		assertTrue(attachment.isInNeedOfNewVersion());

		attachment.unsetInNeedOfNewVersion();

		attachment.setLabel(null);

		assertTrue(attachment.isInNeedOfNewVersion());

		assertNull(attachment.getLabel());
	}

	@Test
	public void setStringValue() {
		final Attachment attachment = attachmentProvider.get();
		assertNull(attachment.getStringValue());

		final String stringVal = "STRING-VALUE";

		assertFalse(attachment.isInNeedOfNewVersion());

		final Attachment attachmentReturned =
				attachment.setStringValue(stringVal);
		assertSame(attachmentReturned, attachment);
		assertEquals(attachment.getStringValue(), stringVal);

		assertTrue(attachment.isInNeedOfNewVersion());

		attachment.unsetInNeedOfNewVersion();

		attachment.setStringValue(stringVal);

		assertEquals(attachment.getStringValue(), stringVal);

		assertFalse(attachment.isInNeedOfNewVersion());

		final String stringVal2 = "STRING-VALUE2";

		attachment.unsetInNeedOfNewVersion();

		attachment.setStringValue(stringVal2);

		assertEquals(attachment.getStringValue(), stringVal2);

		assertTrue(attachment.isInNeedOfNewVersion());

		attachment.unsetInNeedOfNewVersion();

		attachment.setStringValue(null);

		assertTrue(attachment.isInNeedOfNewVersion());

		assertNull(attachment.getStringValue());
	}

	/**
	 * Verify that when {@link Attachment#setInNeedOfNewVersion()} is called,
	 * it's owner's {@code setInNeedOfNewVersion()} is also called.ax
	 */
	@Test
	public void setInNeedOfNewVersion() {
		final Attachment attachment = attachmentProvider.get();
		final StandardCharacter character = standardCharacterProvider.get();
		character.addAttachment(attachment);
		attachment.unsetInNeedOfNewVersion();
		character.unsetInNeedOfNewVersion();

		attachment.setStringValue("arbitraray string");

		assertTrue(attachment.isInNeedOfNewVersion());
		assertTrue(character.isInNeedOfNewVersion());

	}
}
