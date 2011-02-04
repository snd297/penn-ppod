package edu.upenn.cis.ppod.dao;

import com.google.inject.ImplementedBy;

import edu.upenn.cis.ppod.model.ProteinRow;
import edu.upenn.cis.ppod.thirdparty.dao.IDAO;

@ImplementedBy(ProteinRowDAOHibernate.class)
public interface IProteinRowDAO extends IDAO<ProteinRow, Long> {}
