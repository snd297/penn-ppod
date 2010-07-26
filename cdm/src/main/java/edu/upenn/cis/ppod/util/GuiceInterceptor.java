package edu.upenn.cis.ppod.util;

import java.io.Serializable;

import org.hibernate.EmptyInterceptor;
import org.hibernate.EntityMode;

public class GuiceInterceptor extends EmptyInterceptor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Object instantiate(
			final String entityName,
			final EntityMode entityMode,
			final Serializable id) {
		return null;
	}

}
