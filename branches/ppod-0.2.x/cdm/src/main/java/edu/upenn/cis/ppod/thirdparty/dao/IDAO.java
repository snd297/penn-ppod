package edu.upenn.cis.ppod.thirdparty.dao;

import java.io.Serializable;
import java.util.List;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A DAO interface.
 * <p>
 * From <a
 * href="http://www.hibernate.org/328.html">http://www.hibernate.org/328.
 * html</a>.
 * 
 * @param <T> type of the transfer object.
 * @param <ID> the type of the persistence id.
 */
public interface IDAO<T, ID extends Serializable> {

	void evict(final T entity);

	/**
	 * Retrieve all <code>T</code>s.
	 * 
	 * @return all persisted <code>T</code>s
	 */
	List<T> findAll();

	/**
	 * Do a find by example with the given example instance and properties to
	 * exclude.
	 * 
	 * @param exampleInstance the example instance
	 * @param excludeProperty properties to exclude in the find by example
	 * @return the results of the search
	 */
	List<T> findByExample(T exampleInstance, String... excludeProperty);

	void flush();

	/**
	 * Given a persistence id <code>id</code>, retrieve the corresponding
	 * <code>T</code>. If <code>id</code> is <code>null</code>, returns
	 * <code>null</code>.
	 * 
	 * @param id see description
	 * @param lock use an upgrade lock. Objects loaded in this lock mode are
	 *            materialized using an SQL <tt>select ... for update</tt>.
	 * @return the retrieved object, or <code>null</code> if there is no such
	 *         object or if <code>id</code> is <code>null</code>
	 */
	@Nullable
	T findById(ID id, boolean lock);

	/**
	 * Return the identifier value of the given entity as associated with this
	 * <code>IDAO</code>'s session. An exception is thrown if the given entity
	 * instance is transient or detached in relation to the session.
	 * 
	 * @param o a persistent instance
	 * @return the identifier
	 */
	Serializable getIdentifier(Object o);

	void initialize(T entity);

	/**
	 * Save or update <code>entity</code>.
	 * 
	 * @param entity entity object
	 */
	void makePersistent(T entity);

	/**
	 * Make the given entity transient. That is, delete <code>entity</code>.
	 * 
	 * @param entity to be made transient
	 */
	void makeTransient(T entity);
}