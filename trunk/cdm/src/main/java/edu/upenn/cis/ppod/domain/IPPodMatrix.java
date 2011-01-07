package edu.upenn.cis.ppod.domain;

import java.util.List;

import edu.upenn.cis.ppod.imodel.ILabeled;

public interface IPPodMatrix<R extends IPPodRow> extends ILabeled {

	String getDocId();

	List<R> getRows();
}
