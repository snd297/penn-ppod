package edu.upenn.cis.ppod.dao;

import com.google.inject.ImplementedBy;

import edu.upenn.cis.ppod.model.CurrentVersion;
import edu.upenn.cis.ppod.thirdparty.dao.IDAO;

@ImplementedBy(CurrentVersionDAOHibernate.class)
public interface ICurrentVersionDAO extends IDAO<CurrentVersion, Long> {}
