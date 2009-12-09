package edu.upenn.cis.ppod.saveorupdate;

import static com.google.common.collect.Maps.newHashMap;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.upenn.cis.ppod.dao.IAttachmentNamespaceDAO;
import edu.upenn.cis.ppod.model.AttachmentNamespace;

/**
 * @author Sam Donnelly
 */
public class TestAttachmentNamespaceDAO implements IAttachmentNamespaceDAO {

	/**
	 * Makes {@code TestAttachmentNamespaceDAO}s
	 */
	public interface IFactory {
		TestAttachmentNamespaceDAO create(
				Map<String, AttachmentNamespace> namespacesByLabel);
	}

	@Inject
	TestAttachmentNamespaceDAO(
			@Assisted Map<String, AttachmentNamespace> namespacesByLabel) {
		this.namespacesByLabel.putAll(namespacesByLabel);
	}

	private final Map<String, AttachmentNamespace> namespacesByLabel = newHashMap();

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
		return namespacesByLabel.get(label);
	}

	public AttachmentNamespace saveOrUpdate(final AttachmentNamespace entity) {
		throw new UnsupportedOperationException();
	}

}
