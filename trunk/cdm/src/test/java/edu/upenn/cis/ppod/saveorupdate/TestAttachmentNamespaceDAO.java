package edu.upenn.cis.ppod.saveorupdate;

import java.io.Serializable;
import java.util.List;

import edu.upenn.cis.ppod.dao.IAttachmentNamespaceDAO;
import edu.upenn.cis.ppod.model.AttachmentNamespace;

/**
 * @author Sam Donnelly
 * 
 */
public class TestAttachmentNamespaceDAO implements IAttachmentNamespaceDAO {

	public AttachmentNamespace delete(final AttachmentNamespace entity) {
		throw new UnsupportedOperationException();
	}

	public List<AttachmentNamespace> findAll() {
		throw new UnsupportedOperationException();
	}

	public List<AttachmentNamespace> findByExample(
			final AttachmentNamespace exampleInstance,
			final String... excludeProperty) {
		throw new UnsupportedOperationException();
	}

	public AttachmentNamespace get(final Long id, final boolean lock) {
		throw new UnsupportedOperationException();
	}

	public Serializable getIdentifier(final Object o) {
		throw new UnsupportedOperationException();
	}

	public AttachmentNamespace getNamespaceByLabel(final String label) {
		throw new UnsupportedOperationException();
	}

	public AttachmentNamespace saveOrUpdate(final AttachmentNamespace entity) {
		throw new UnsupportedOperationException();
	}

}
