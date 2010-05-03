package edu.upenn.cis.ppod.model;

import javax.persistence.MappedSuperclass;

import edu.upenn.cis.ppod.modelinterfaces.IPPodVersionedWithOTUSet;

/**
 * @author Sam Donnelly
 */
@MappedSuperclass
public abstract class Matrix<R extends Row<?>> extends UUPPodEntityWXmlId
		implements IPPodVersionedWithOTUSet, Iterable<R> {

}
