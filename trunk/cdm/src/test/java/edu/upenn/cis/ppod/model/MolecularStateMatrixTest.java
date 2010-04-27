package edu.upenn.cis.ppod.model;

import static com.google.common.collect.Lists.newArrayList;
import static org.testng.Assert.assertSame;

import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.TestGroupDefs;

/**
 * Test {@link MolecularStateMatrix}.
 * 
 * @author Sam Donnelly
 */
@Test(groups = TestGroupDefs.FAST, dependsOnGroups = TestGroupDefs.INIT)
public class MolecularStateMatrixTest {

	@Inject
	private Provider<DNAMatrix> dnaMatrixProvider;

	@Inject
	private Provider<DNACharacter> dnaCharacterProvider;

	/**
	 * Set the characters on a molecular matrix and make sure that it makes all
	 * of them the same value.
	 */
	public void setCharacters() {
		final MolecularStateMatrix molecularMatrix = dnaMatrixProvider.get();
		final MolecularCharacter molecularCharacter = dnaCharacterProvider
				.get();
		molecularMatrix.setCharacters(newArrayList(molecularCharacter));

		for (int i = 0; i < molecularMatrix.getCharactersSize(); i++) {
			assertSame(molecularMatrix.getCharacter(i), molecularCharacter);
		}
	}
}
