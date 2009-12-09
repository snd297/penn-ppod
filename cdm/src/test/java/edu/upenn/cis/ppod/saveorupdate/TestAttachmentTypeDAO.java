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

	private final Map<String, Map<String, AttachmentType>> typesByNamespaceLabelAndTypeLabel = newHashMap();

	public AttachmentType getAttachmentTypeByNamespaceAndType(
			String namespaceLabel, String typeLabel) {
		final Map<String, AttachmentType> typesByLabel = typesByNamespaceLabelAndTypeLabel
				.get(namespaceLabel);
		if (typesByLabel != null) { 
			return typesByLabel.get(typeLabel);
		}
		return null;
	}

	public AttachmentType delete(AttachmentType entity) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public List<AttachmentType> findAll() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public List<AttachmentType> findByExample(AttachmentType exampleInstance,
			String... excludeProperty) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public AttachmentType get(Long id, boolean lock) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Serializable getIdentifier(Object o) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public AttachmentType saveOrUpdate(AttachmentType entity) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
