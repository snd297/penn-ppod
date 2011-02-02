package edu.upenn.cis.ppod.dao;

import com.google.inject.ImplementedBy;

import edu.upenn.cis.ppod.model.TreeSet;
import edu.upenn.cis.ppod.thirdparty.dao.IDAO;

@ImplementedBy(TreeSetDAOHibernate.class)
public interface ITreeSetDAO extends IDAO<TreeSet, Long> {}
