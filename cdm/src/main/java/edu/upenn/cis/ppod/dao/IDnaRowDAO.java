package edu.upenn.cis.ppod.dao;

import com.google.inject.ImplementedBy;

import edu.upenn.cis.ppod.model.DnaRow;
import edu.upenn.cis.ppod.thirdparty.dao.IDAO;

@ImplementedBy(DnaRowDAOHibernate.class)
public interface IDnaRowDAO extends IDAO<DnaRow, Long> {

}
