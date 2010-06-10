package edu.upenn.cis.ppod.dao.hibernate;

import com.google.inject.ImplementedBy;

import edu.upenn.cis.ppod.dao.IVersionInfoDAO;
import edu.upenn.cis.ppod.model.VersionInfo;

@ImplementedBy(VersionInfoDAOHibernate.class)
public interface IVersionInfoDAOHibernate
		extends IVersionInfoDAO, IDAOHibernate<VersionInfo, Long> {

}
