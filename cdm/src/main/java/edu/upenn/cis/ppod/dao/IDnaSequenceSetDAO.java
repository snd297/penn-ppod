package edu.upenn.cis.ppod.dao;

import com.google.inject.ImplementedBy;

import edu.upenn.cis.ppod.model.DnaSequenceSet;
import edu.upenn.cis.ppod.thirdparty.dao.IDAO;

@ImplementedBy(DnaSequenceSetDAOHibernate.class)
public interface IDnaSequenceSetDAO extends IDAO<DnaSequenceSet, Long> {

}
