package edu.upenn.cis.ppod.model;

import java.util.List;

import edu.upenn.cis.ppod.imodel.IOrderedChild;

public class ModelUtil {

	public static void adjustPositions(
			List<? extends IOrderedChild<?>> orderedChildren) {
		int pos = -1;
		for (final IOrderedChild<?> orderedChild : orderedChildren) {
			pos++;
			orderedChild.setPosition(pos);
		}
	}

	private ModelUtil() {
		throw new AssertionError("can't instantiate a ModelUtil");
	}
}
