package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Parent;

import edu.upenn.cis.ppod.imodel.IOtuKeyedMap;
import edu.upenn.cis.ppod.util.IVisitor;

@Embeddable
@Access(AccessType.PROPERTY)
public class ProteinRows implements IOtuKeyedMap<ProteinRow> {

	private final OtuKeyedMapPlus<ProteinRow, ProteinMatrix> rows = new OtuKeyedMapPlus<ProteinRow, ProteinMatrix>();

	ProteinRows() {}

	ProteinRows(final ProteinMatrix parent) {
		checkNotNull(parent);
		rows.setParent(parent);
	}

	/** {@inheritDoc} */
	public void accept(final IVisitor visitor) {
		rows.accept(visitor);
	}

	public void clear() {
		rows.clear();
	}

	/** {@inheritDoc} */
	public ProteinRow get(final Otu key) {
		return rows.get(key);
	}

	@Parent
	public ProteinMatrix getParent() {
		return rows.getParent();
	}

	/**
	 * We want everything but SAVE_UPDATE (which ALL will give us) - once it's
	 * evicted out of the persistence context, we don't want it back in via
	 * cascading UPDATE. So that we can run leaner for large matrices.
	 */
	@OneToMany(cascade = {
			CascadeType.PERSIST,
			CascadeType.MERGE,
			CascadeType.REMOVE,
			CascadeType.DETACH,
			CascadeType.REFRESH },
			orphanRemoval = true)
	@JoinTable(inverseJoinColumns = @JoinColumn(name = ProteinRow.JOIN_COLUMN))
	@MapKeyJoinColumn(name = Otu.JOIN_COLUMN)
	public Map<Otu, ProteinRow> getValues() {
		return rows.getValues();
	}

	/** {@inheritDoc} */
	public ProteinRow put(final Otu key, final ProteinRow value) {
		return rows.put(key, value);
	}

	/**
	 * Set the owner of this object.
	 * 
	 * @param parent the owner
	 */
	public void setParent(final ProteinMatrix parent) {
		rows.setParent(parent);
	}

	/** {@inheritDoc} */
	public void setValues(final Map<Otu, ProteinRow> values) {
		rows.setValues(values);
	}

	/** {@inheritDoc} */
	public void updateOtus() {
		rows.updateOtus();
	}

}
