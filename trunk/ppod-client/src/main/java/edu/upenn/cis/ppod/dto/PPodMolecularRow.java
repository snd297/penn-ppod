package edu.upenn.cis.ppod.dto;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.google.common.collect.ImmutableSet;

@XmlAccessorType(XmlAccessType.FIELD)
abstract class PPodMolecularRow {

	public static final Set<Character> DELIMITERS = ImmutableSet.of(
						'(',
						')',
						'{',
						'}');

	private String sequence;

	PPodMolecularRow() {}

	PPodMolecularRow(final String sequence) {
		this.sequence = checkNotNull(sequence);
	}

	abstract Set<Character> getLegalChars();

	public final String getSequence() {
		return sequence;
	}

	public final void setSequence(final String sequence) {
		for (int i = 0; i < sequence.length(); i++) {
			checkArgument(
					getLegalChars().contains(sequence.charAt(i))
							|| DELIMITERS.contains(sequence.charAt(i)),
					"position " + i + " is " + sequence.charAt(i)
							+ " which is illegal");
		}
		this.sequence = sequence;
	}
}
