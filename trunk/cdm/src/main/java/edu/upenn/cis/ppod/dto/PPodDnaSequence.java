package edu.upenn.cis.ppod.dto;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;

import com.google.common.collect.ImmutableSet;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

final public class PPodDnaSequence extends PPodDomainObject {

	private String sequence;

	@XmlAttribute
	@CheckForNull
	private String name;

	@XmlAttribute
	@CheckForNull
	private String description;

	@XmlAttribute
	@CheckForNull
	private String accession;

	public final static Set<java.lang.Character> LEGAL_CHARS =
	ImmutableSet.of(
			'A', 'a',
			'C', 'c',
			'G', 'g',
			'T', 't',
			'R',
			'Y',
			'K',
			'M',
			'S',
			'W',
			'B',
			'D',
			'H',
			'V',
			'N',
			'-');

	PPodDnaSequence() {}

	public PPodDnaSequence(final Long version, final String sequence,
			@CheckForNull final String name,
			@CheckForNull final String description,
			@CheckForNull final String accession) {
		super(version);
		this.sequence = sequence;
		this.name = name;
		this.description = description;
		this.accession = accession;
	}

	public PPodDnaSequence(
			final String sequence,
			@CheckForNull final String name,
			@CheckForNull final String description,
			@CheckForNull final String accession) {
		this.sequence = sequence;
		this.name = name;
		this.description = description;
		this.accession = accession;
	}

	@Nullable
	public String getAccession() {
		return accession;
	}

	@Nullable
	public String getDescription() {
		return description;
	}

	@Nullable
	public String getName() {
		return name;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(final String sequence) {
		checkNotNull(sequence);
		this.sequence = sequence;
	}
}
