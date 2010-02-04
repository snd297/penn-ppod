package edu.upenn.cis.ppod.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.Type;



/**
 * @author samd
 *
 */
public class StubQuery implements Query{

	public int executeUpdate() throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String[] getNamedParameters() throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String getQueryString() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String[] getReturnAliases() throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Type[] getReturnTypes() throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Iterator iterate() throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public List list() throws HibernateException {
		return Collections.EMPTY_LIST;
	}

	public ScrollableResults scroll() throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public ScrollableResults scroll(ScrollMode scrollMode)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setBigDecimal(int position, BigDecimal number) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setBigDecimal(String name, BigDecimal number) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setBigInteger(int position, BigInteger number) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setBigInteger(String name, BigInteger number) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setBinary(int position, byte[] val) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setBinary(String name, byte[] val) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setBoolean(int position, boolean val) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setBoolean(String name, boolean val) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setByte(int position, byte val) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setByte(String name, byte val) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setCacheMode(CacheMode cacheMode) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setCacheRegion(String cacheRegion) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setCacheable(boolean cacheable) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setCalendar(int position, Calendar calendar) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setCalendar(String name, Calendar calendar) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setCalendarDate(int position, Calendar calendar) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setCalendarDate(String name, Calendar calendar) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setCharacter(int position, char val) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setCharacter(String name, char val) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setComment(String comment) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setDate(int position, Date date) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setDate(String name, Date date) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setDouble(int position, double val) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setDouble(String name, double val) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setEntity(int position, Object val) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setEntity(String name, Object val) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setFetchSize(int fetchSize) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setFirstResult(int firstResult) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setFloat(int position, float val) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setFloat(String name, float val) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setFlushMode(FlushMode flushMode) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setInteger(int position, int val) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setInteger(String name, int val) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setLocale(int position, Locale locale) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setLocale(String name, Locale locale) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setLockMode(String alias, LockMode lockMode) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setLong(int position, long val) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setLong(String name, long val) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setMaxResults(int maxResults) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setParameter(int position, Object val)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setParameter(String name, Object val)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setParameter(int position, Object val, Type type) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setParameter(String name, Object val, Type type) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setParameterList(String name, Collection vals)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setParameterList(String name, Object[] vals)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setParameterList(String name, Collection vals, Type type)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setParameterList(String name, Object[] vals, Type type)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setParameters(Object[] values, Type[] types)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setProperties(Object bean) throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setProperties(Map bean) throws HibernateException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setReadOnly(boolean readOnly) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setResultTransformer(ResultTransformer transformer) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setSerializable(int position, Serializable val) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setSerializable(String name, Serializable val) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setShort(int position, short val) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setShort(String name, short val) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setString(int position, String val) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setString(String name, String val) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setText(int position, String val) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setText(String name, String val) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setTime(int position, Date date) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setTime(String name, Date date) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setTimeout(int timeout) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setTimestamp(int position, Date date) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Query setTimestamp(String name, Date date) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Object uniqueResult() throws HibernateException {
		return null;
	}

}
