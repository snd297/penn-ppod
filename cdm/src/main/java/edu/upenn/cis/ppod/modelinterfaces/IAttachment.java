package edu.upenn.cis.ppod.modelinterfaces;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import edu.upenn.cis.ppod.model.Attachment;
import edu.upenn.cis.ppod.model.AttachmentType;
import edu.upenn.cis.ppod.util.IVisitor;

/**
 * A flexible container for data that can be attached to other pPOD attachees.
 * 
 * @author Sam Donnelly
 */
public interface IAttachment extends IUUPPodEntityWithXmlID {

	/**
	 * Is an attachment of a particular {@link AttachmentNamespace}.
	 */
	public final static class IsOfNamespace implements
			Predicate<IAttachment> {

		private final String namespace;

		/**
		 * @param namespace is the type of this namespace?
		 */
		public IsOfNamespace(final String namespace) {
			this.namespace = namespace;
		}

		public boolean apply(final IAttachment input) {
			return input.getType().getNamespace().getLabel().equals(namespace);
		}

	}

	/**
	 * Is an attachment of a particular {@link AttachmentNamespace} and
	 * {@link AttachmentType}?
	 */
	final static class IsOfNamespaceAndType implements
			Predicate<IAttachment> {

		private final String namespace;

		private final String type;

		/**
		 * @param type is the attachment of this type?
		 * @param namespace is the type of this namespace?
		 */
		public IsOfNamespaceAndType(final String namespace, final String type) {
			this.type = type;
			this.namespace = namespace;
		}

		public boolean apply(final IAttachment input) {
			return input.getType().getNamespace().getLabel().equals(namespace)
					&& input.getType().getLabel().equals(type);
		}

	}

	/**
	 * {@link Function} wrapper of {@link #getStringValue()}.
	 */
	static final Function<IAttachment, String> getStringValue = new Function<IAttachment, String>() {

		public String apply(final IAttachment from) {
			return from.getStringValue();
		}
	};

	void accept(final IVisitor visitor);

	/**
	 * Get the byteArrayValue.
	 * 
	 * @return the byteArrayValue
	 */
	@XmlElement
	@CheckForNull
	byte[] getBytesValue();

	/**
	 * Get the label.
	 * 
	 * @return the label
	 */
	@XmlAttribute
	@CheckForNull
	String getLabel();

	/**
	 * Get the value.
	 * 
	 * @return the value.
	 */
	@XmlAttribute
	@CheckForNull
	String getStringValue();

	/**
	 * Get the type of this attachment.
	 * 
	 * @return the type of this attachment
	 */
	@XmlAttribute(name = "attachmentTypeDocId")
	@XmlIDREF
	@Nullable
	AttachmentType getType();

	Attachment setByteArrayValue(@Nullable final byte[] bytesValue);

	Attachment setInNeedOfNewPPodVersionInfo();

	Attachment setLabel(@Nullable final String label);

	Attachment setStringValue(@Nullable final String stringValue);

	Attachment setType(final AttachmentType type);

}