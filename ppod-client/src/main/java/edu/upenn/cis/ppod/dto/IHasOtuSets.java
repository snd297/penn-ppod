package edu.upenn.cis.ppod.dto;

import java.util.List;

public interface IHasOtuSets extends ILabeled {

	List<PPodOtuSet> getOtuSets();

	void setOtuSets(List<PPodOtuSet> otuSets);

}