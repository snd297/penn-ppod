package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkNotNull;

abstract class MolecularRow<C extends Cell<?, ? extends MolecularRow<?, ?>>, M extends Matrix<?, ?>>
		extends Row<C, M> {

}
