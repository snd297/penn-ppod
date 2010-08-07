package edu.upenn.cis.ppod.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.upenn.cis.ppod.model.Attachment.IIsOfNamepspaceTypeLabelAndStringValue;
import edu.upenn.cis.ppod.modelinterfaces.IPPodEntity;
import edu.upenn.cis.ppod.modelinterfaces.IUUPPodEntity;

@XmlJavaTypeAdapter(Attachment.Adapter.class)
public interface IAttachment extends IUUPPodEntity {

	/**
	 * Is an attachment of a particular {@link AttachmentNamespace}?
	 */
	@ImplementedBy(IsOfNamespace.class)
	public static interface IIsOfNamespace extends Predicate<IAttachment> {

		/**
		 * Makes {@code IIsOfNamespace}s.
		 */
		public static interface IFactory {

			/**
			 * Create a {@code IIsOfNamespace} that will return {@code true} if
			 * and only if the attachment's namespaceLabel has the given
			 * attachmentLabel.
			 * 
			 * @param namespaceLabel the attachmentLabel of the namespaceLabel
			 *            we're interested in
			 * 
			 * @return a new {@code IIsOfNamespace}
			 */
			IIsOfNamespace create(String namespaceLabel);
		}
	}

	final static class IsOfNamespace implements IIsOfNamespace {

		private final String namespaceLabel;

		/**
		 * @param namespaceLabel is the typeLabel of this namespaceLabel?
		 */
		@Inject
		IsOfNamespace(@Assisted final String namespaceLabel) {
			checkNotNull(namespaceLabel);
			this.namespaceLabel = namespaceLabel;
		}

		public boolean apply(final IAttachment input) {
			checkNotNull(input);
			return namespaceLabel.equals(input.getType().getNamespace()
						.getLabel());
		}

	}

	/**
	 * Is an attachment of a particular {@link AttachmentNamespace} and
	 * {@link AttachmentType}?
	 */
	public final static class IsOfNamespaceAndType
			implements Predicate<IAttachment> {

		private final String namespaceLabel;

		private final String typeLabel;

		/**
		 * @param typeLabel is the attachment of this typeLabel?
		 * @param namespaceLabel is the typeLabel of this namespaceLabel?
		 */
		IsOfNamespaceAndType(final String namespaceLabel, final String typeLabel) {
			checkNotNull(namespaceLabel);
			checkNotNull(typeLabel);
			this.typeLabel = typeLabel;
			this.namespaceLabel = namespaceLabel;
		}

		public boolean apply(final IAttachment input) {
			checkNotNull(input);
			return namespaceLabel.equals(input.getType().getNamespace()
					.getLabel())
					&& typeLabel.equals(input.getType().getLabel());
		}

	}

	final static class IsOfNamespaceTypeLabelAndStringValue
				implements IIsOfNamepspaceTypeLabelAndStringValue {

		private final String attachmentLabel;
		private final String attachmentStringValue;
		private final String namespaceLabel;
		private final String typeLabel;

		IsOfNamespaceTypeLabelAndStringValue(final IAttachment attachment) {
			checkNotNull(attachment);
			checkArgument(attachment.getType() != null,
						"attachment.getType() == null");

			checkArgument(attachment.getType().getNamespace() != null,
						"attachment.getType().getNamespace() == null");

			checkArgument(
						attachment.getType().getNamespace().getLabel() != null,
						"attachment's typeLabel's namespaceLabel has null attachmentLabel");

			checkArgument(attachment.getType().getLabel() != null,
						"attachment's typeLabel has null attachmentLabel");

			final String attachmentLabel = attachment.getLabel();
			checkArgument(attachmentLabel != null,
						"attachment.getLabel() == null");

			final String attachmentStringValue = attachment.getStringValue();

			checkArgument(attachmentStringValue != null,
						"attachment.getStringValue() == null");

			this.namespaceLabel = attachment.getType().getNamespace()
						.getLabel();
			this.typeLabel = attachment.getType().getLabel();
			this.attachmentLabel = attachmentLabel;
			this.attachmentStringValue = attachmentStringValue;
		}

		public boolean apply(final IAttachment input) {
			return namespaceLabel.equals(input.getType().getNamespace()
						.getLabel())
						&& typeLabel.equals(input.getType().getLabel())
						&& attachmentLabel.equals(input.getLabel())
						&& attachmentStringValue.equals(input.getStringValue());
		}
	}

	/**
	 * {@link Function} wrapper of {@link #getStringValue()}.
	 */
	public static final Function<IAttachment, String> getStringValue =
			new Function<IAttachment, String>() {

				public String apply(final IAttachment from) {
					return from.getStringValue();
				}
			};

	/**
	 * Get the entities that have this has an attachment.
	 * <p>
	 * Will be {@code null} for newly create attachments, will never be
	 * {@code null} for persistent attachments.
	 * 
	 * @return the entities that have this has an attachment
	 */
	@Nullable
	IPPodEntity getAttachee();

	/**
	 * Get a copy of the byteArrayValue.
	 * 
	 * @return a copy of the byteArrayValue
	 */
	@XmlElement
	@CheckForNull
	byte[] getBytesValue();

	/**
	 * Get the attachmentLabel.
	 * 
	 * @return the attachmentLabel
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
	 * Get the typeLabel of this attachment.
	 * 
	 * @return the typeLabel of this attachment
	 */
	@XmlAttribute(name = "attachmentTypeDocId")
	@XmlIDREF
	@Nullable
	AttachmentType getType();

	/**
	 * Set the item to which this is attached, {@code null} to sever the
	 * relationship.
	 * 
	 * @param attachee to which this is attached
	 * 
	 * @return this
	 */
	IPPodEntity setAttachee(@CheckForNull final IPPodEntity attachee);

	IAttachment setBytesValue(@CheckForNull final byte[] bytesValue);

	IAttachment setLabel(@CheckForNull final String label);

	IAttachment setStringValue(@CheckForNull final String stringValue);

	IAttachment setType(final AttachmentType type);

}