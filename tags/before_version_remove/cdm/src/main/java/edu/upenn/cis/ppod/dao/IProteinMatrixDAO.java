package edu.upenn.cis.ppod.dao;

import com.google.inject.ImplementedBy;

import edu.upenn.cis.ppod.model.ProteinMatrix;
import edu.upenn.cis.ppod.thirdparty.dao.IDAO;

@ImplementedBy(ProteinMatrixDAOHibernate.class)
public interface IProteinMatrixDAO extends IDAO<ProteinMatrix, Long> {}
