package edu.upenn.cis.ppod.saveorupdate;

import static com.google.common.collect.Maps.newHashMap;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import edu.upenn.cis.ppod.dao.IAttachmentTypeDAO;
import edu.upenn.cis.ppod.model.AttachmentType;

/**
 * @author Sam Donnelly
 */
public class TestAttachmentTypeDAO implements IAttachmentTypeDAO {

	private Map<String, Map<String, AttachmentType>> typesByNamespaceLabelAndTypeLabel = newHashMap();

	public AttachmentType delete(final AttachmentType entity) {
		throw new UnsupportedOperationException();
	}

	public List<AttachmentType> findAll() {
		throw new UnsupportedOperationException();
	}

	public List<AttachmentType> findByExample(
			final AttachmentType exampleInstance,
			final String... excludeProperty) {
		throw new UnsupportedOperationException();
	}

	public AttachmentType get(final Long id, final boolean lock) {
		throw new UnsupportedOperationException();
	}

	public AttachmentType getAttachmentTypeByNamespaceAndType(
			final String namespaceLabel, final String typeLabel) {
		final Map<String, AttachmentType> typesByLabel = typesByNamespaceLabelAndTypeLabel
				.get(namespaceLabel);
		if (typesByLabel == null) {
			return null;
		}
		return typesByLabel.get(typeLabel);
	}

	public Serializable getIdentifier(final Object o) {
		throw new UnsupportedOperationException();
	}

	public AttachmentType saveOrUpdate(final AttachmentType entity) {
		throw new UnsupportedOperationException();
	}

	public TestAttachmentTypeDAO setTypesByNamespaceLabelAndTypeLabel(
			final Map<String, Map<String, AttachmentType>> typesByNamespaceLabelAndTypeLabel) {
		this.typesByNamespaceLabelAndTypeLabel = typesByNamespaceLabelAndTypeLabel;
		return this;
	}

	public AttachmentType evict(AttachmentType entity) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void flush() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
