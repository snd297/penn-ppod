package edu.upenn.cis.ppod.dto;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import edu.umd.cs.findbugs.annotations.CheckForNull;

@XmlAccessorType(XmlAccessType.NONE)
abstract class PPodMatrix<R extends PPodDomainObject> extends
		UuPPodDomainObjectWithLabel {

	private List<R> rows = newArrayList();

	/** For Jaxb. */
	PPodMatrix() {}

	PPodMatrix(final String pPodId, final Long version,
			final String label) {
		super(pPodId, version, label);
	}

	PPodMatrix(@CheckForNull final String pPodId, final String label) {
		super(pPodId, label);
	}

	public List<R> getRows() {
		return rows;
	}

	public final void setRows(final List<R> rows) {
		checkNotNull(rows);
		this.rows = rows;
	}
}
