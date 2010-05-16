package edu.upenn.cis.ppod.util;

/**
 * @author Sam Donnelly
 */
public interface ICellElementFactory<E> {
	E get(E cellElement);
}
