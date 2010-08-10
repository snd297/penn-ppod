package edu.upenn.cis.ppod.model;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.Function;

import edu.upenn.cis.ppod.imodel.IChild;
import edu.upenn.cis.ppod.imodel.ILabeled;
import edu.upenn.cis.ppod.imodel.IPPodEntity;
import edu.upenn.cis.ppod.imodel.IStandardCharacter;
import edu.upenn.cis.ppod.imodel.IWithDocId;

@XmlJavaTypeAdapter(StandardState.Adapter.class)
public interface IStandardState
		extends IChild<IStandardCharacter>, IPPodEntity, ILabeled, IWithDocId {

	/**
	 * For assisted injections.
	 */
	public static interface IFactory {

		/**
		 * Create a character state with the given state number.
		 * 
		 * @param stateNumber the state number for the new state
		 * @return the new state
		 */
		StandardState create(Integer stateNumber);
	}

	/**
	 * {@link Function} wrapper of {@link #getStateNumber()}.
	 */
	public static final Function<IStandardState, Integer> getStateNumber = new Function<IStandardState, Integer>() {

		public Integer apply(final IStandardState from) {
			return from.getStateNumber();
		}

	};

	/**
	 * Get the integer value of this character stateNumber. The integer value is
	 * the heart of the class.
	 * <p>
	 * {@code null} when the object is created. Never {@code null} for
	 * persistent objects.
	 * 
	 * @return get the integer value of this character stateNumber
	 */
	@XmlAttribute
	@Nullable
	Integer getStateNumber();

	IStandardState setLabel(final String label);

}