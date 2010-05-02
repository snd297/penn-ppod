package edu.upenn.cis.ppod.modelinterfaces;

import com.google.common.base.Function;

/**
 * An object with a label.
 * 
 * @author Sam Donnelly
 */
public interface ILabeled {

	/**
	 * {@link Function} wrapper of {@link #getLabel()}.
	 */
	public static final Function<ILabeled, String> getLabel = new Function<ILabeled, String>() {

		/**
		 * Return {@code labeled.getLabel()}.
		 * 
		 * @param labeled on which to call {@code getLabel()}.
		 * 
		 * @return {@code labeled.getLabel()}.
		 */
		public String apply(final ILabeled labeled) {
			return labeled.getLabel();
		}
	};

	/**
	 * Get the label.
	 * 
	 * @return the label
	 */
	String getLabel();
}
