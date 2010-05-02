package edu.upenn.cis.ppod.util;

import com.google.inject.ImplementedBy;

/**
 * For straightening up or filling data structures after we've unmarshalled.
 * 
 * @author Sam Donnelly
 */
@ImplementedBy(AfterUnmarshalVisitor.class)
public interface IAfterUnmarshalVisitor extends IVisitor {

}
