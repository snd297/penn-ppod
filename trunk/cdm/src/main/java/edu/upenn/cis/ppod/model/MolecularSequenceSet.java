package edu.upenn.cis.ppod.model;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author Sam Donnelly
 */
@MappedSuperclass
public class MolecularSequenceSet {
	public final static String TABLE = "MOLECULAR_SEQUENCE_SET";

	@OneToMany(mappedBy = "molecularSequenceSet")
	private Set<MolecularSequence> molecularSequences = newHashSet();
}
