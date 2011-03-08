package edu.upenn.cis.ppod.dto;

import java.util.List;

import edu.upenn.cis.ppod.imodel.ILabeled;

public interface IHasOtuSets extends ILabeled {

	List<PPodOtuSet> getOtuSets();

	void setOtuSets(List<PPodOtuSet> otuSets);

}