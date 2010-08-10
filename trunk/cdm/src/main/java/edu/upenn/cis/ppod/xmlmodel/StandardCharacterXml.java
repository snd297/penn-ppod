package edu.upenn.cis.ppod.xmlmodel;

import java.util.Set;

import com.google.inject.Inject;

import edu.upenn.cis.ppod.imodel.IAttachment;
import edu.upenn.cis.ppod.imodel.IStandardCharacter;
import edu.upenn.cis.ppod.imodel.IStandardMatrix;
import edu.upenn.cis.ppod.imodel.IVersioned;
import edu.upenn.cis.ppod.imodel.IWithPPodId;
import edu.upenn.cis.ppod.model.StandardCharacter;
import edu.upenn.cis.ppod.model.StandardState;
import edu.upenn.cis.ppod.model.VersionInfo;
import edu.upenn.cis.ppod.util.IVisitor;

public class StandardCharacterXml implements IStandardCharacter {

	private StandardCharacter character;

	@Inject
	StandardCharacterXml(final StandardCharacter character) {
		this.character = character;
	}

	public IStandardMatrix getParent() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void setParent(IStandardMatrix parent) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void setInNeedOfNewVersion() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public IAttachment addAttachment(IAttachment attachment) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Set<IAttachment> getAttachments() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Set<IAttachment> getAttachmentsByNamespace(String namespace) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Set<IAttachment> getAttachmentsByNamespaceAndType(String namespace,
			String type) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean removeAttachment(IAttachment attachment) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Long getId() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Long getVersion() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public VersionInfo getVersionInfo() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean isInNeedOfNewVersion() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public IVersioned setVersionInfo(VersionInfo versionInfo) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public IVersioned unsetInNeedOfNewVersion() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void accept(IVisitor visitor) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String getPPodId() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public IWithPPodId setPPodId() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public IWithPPodId setPPodId(String pPodId) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String getDocId() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void setDocId() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void setDocId(String xmlId) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String getLabel() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public StandardState addState(StandardState state) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public StandardState getState(Integer stateNumber) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Set<StandardState> getStates() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public IStandardCharacter setLabel(String label) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
