package edu.upenn.cis.ppod.dao;

import com.google.inject.ImplementedBy;

import edu.upenn.cis.ppod.model.DnaMatrix;
import edu.upenn.cis.ppod.thirdparty.dao.IDAO;

@ImplementedBy(DnaMatrixDAOHibernate.class)
public interface IDnaMatrixDAO extends IDAO<DnaMatrix, Long> {}
