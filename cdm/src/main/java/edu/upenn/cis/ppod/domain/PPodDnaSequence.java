package edu.upenn.cis.ppod.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
final public class PPodDnaSequence extends PPodDomainObject {

	PPodDnaSequence() {}

	private String sequence;

	@CheckForNull
	private String name;

	@CheckForNull
	private String description;

	@CheckForNull
	private String accession;

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

	public void setSequence(final String sequence) {
		checkNotNull(sequence);
		this.sequence = sequence;
	}
}
