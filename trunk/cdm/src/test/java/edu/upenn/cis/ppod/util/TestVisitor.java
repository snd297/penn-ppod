package edu.upenn.cis.ppod.util;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import edu.upenn.cis.ppod.model.DNASequence;
import edu.upenn.cis.ppod.model.DNASequenceSet;

/**
 * Made so we can test the various {@code accept(IVisitor)} methods.
 * <p>
 * Collects, in order, the all of the objects that are visited.
 * 
 * @author Sam Donnelly
 */
public class TestVisitor extends EmptyVisitor {

	final List<Object> visited = newArrayList();

	@Override
	public void visit(final DNASequenceSet sequenceSet) {
		visited.add(sequenceSet);
	}

	@Override
	public void visit(final DNASequence sequence) {
		visited.add(sequence);
	}

	public List<Object> getVisited() {
		return visited;
	}
}
