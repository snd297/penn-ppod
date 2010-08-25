package edu.upenn.cis.ppod.imodel;

public interface IProteinMatrix
		extends IMatrix<IProteinRow, IProteinCell> {
	void afterUnmarshal();

}
