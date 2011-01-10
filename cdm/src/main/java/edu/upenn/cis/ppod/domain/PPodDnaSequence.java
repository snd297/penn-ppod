package edu.upenn.cis.ppod.domain;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

final public class PPodDnaSequence extends PPodDomainObject {

	private final String sequence;

	@CheckForNull
	private final String name;

	@CheckForNull
	private final String description;

	@CheckForNull
	private final String accession;

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

	public String getSequence() {
		return sequence;
	}

	@Nullable
	public String getName() {
		return name;
	}

	@Nullable
	public String getDescription() {
		return description;
	}

	@Nullable
	public String getAccession() {
		return accession;
	}
}
