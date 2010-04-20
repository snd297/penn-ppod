package edu.upenn.cis.ppod.dao;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static edu.upenn.cis.ppod.util.CollectionsUtil.nullFillAndSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import edu.upenn.cis.ppod.dao.hibernate.IObjectWithLongIdDAOHibernate;
import edu.upenn.cis.ppod.model.CharacterStateCell;
import edu.upenn.cis.ppod.model.CharacterStateRow;

/**
 * A {@code IObjectWithLongIdDAO} that stores the entities that operations were
 * called on. So you can, for example, get a list of all of the entities that
 * {@code IObjectWithLongIdDAOHibernate.delete(...)} was called on.
 * <p>
 * This is only implemented for {@code delete(...)} so far.
 * 
 * @author Sam Donnelly
 */
public class TestObjectWithLongIdDAO implements IObjectWithLongIdDAOHibernate {

	private List<Object> deletedEntities = newArrayList();

	public List<Object> getDeletedEntities() {
		return deletedEntities;
	}

	/**
	 * Hangs onto all entities that this method was called with and they can be
	 * retrieved, in order, with {@link #getDeletedEntities()}.
	 * 
	 * @param entity recorded for later retrieval
	 */
	public void delete(final Object entity) {
		deletedEntities.add(entity);
	}

	/**
	 * Does nothing.
	 * 
	 * @param entity ignored
	 */
	public void evict(final Object entity) {
		return;
	}

	/**
	 * Does nothing.
	 * 
	 * @param entities ignored
	 */
	public void evictEntities(final Collection<? extends Object> entities) {}

	public List<Object> findAll() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public List<Object> findByExample(final Object exampleInstance,
			final String... excludeProperty) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	/**
	 * Does nothing.
	 */
	public void flush() {}

	public Object get(final Long id, final boolean lock) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String getEntityName(final Class<? extends Object> entityClass) {
		return entityClass.getName();
	}

	public String getEntityName(final Object entity) {
		return entity.getClass().getName();
	}

	public Serializable getIdentifier(final Object o) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void initialize(final Object entity) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	private Map<CharacterStateRow, List<CharacterStateCell>> rowsToCells = newHashMap();

	/**
	 * Does nothing.
	 * 
	 * @entity ignored
	 */
	public void saveOrUpdate(final Object entity) {
		if (entity instanceof CharacterStateCell) {
			final CharacterStateCell cell = (CharacterStateCell) entity;
			final CharacterStateRow row = cell.getRow();
			if (rowsToCells.containsKey(cell.getRow())) {

			} else {
				rowsToCells.put(row, new ArrayList<CharacterStateCell>());
			}
			nullFillAndSet(rowsToCells.get(row), row.getCellPosition(cell),
						cell);
		}
	}

	public Map<CharacterStateRow, List<CharacterStateCell>> getRowsToCells() {
		return rowsToCells;
	}

	/**
	 * Does nothing and returns.
	 * 
	 * @param s ignored
	 * 
	 * @return this
	 */
	public TestObjectWithLongIdDAO setSession(final Session s) {
		return this;
	}

}
