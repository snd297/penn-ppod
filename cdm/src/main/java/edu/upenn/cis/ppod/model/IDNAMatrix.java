package edu.upenn.cis.ppod.model;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlJavaTypeAdapter(DNAMatrix.Adapter.class)
public interface IDNAMatrix extends IMatrix<DNARow> {
	void afterUnmarshal();
}