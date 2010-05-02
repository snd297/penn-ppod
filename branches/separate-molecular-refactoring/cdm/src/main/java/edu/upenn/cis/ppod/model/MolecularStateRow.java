package edu.upenn.cis.ppod.model;

import javax.persistence.MappedSuperclass;

/**
 * @author Sam Donnelly
 */
@MappedSuperclass
public abstract class MolecularStateRow<R extends MolecularCell<?>>
		extends Row<R> {

}
