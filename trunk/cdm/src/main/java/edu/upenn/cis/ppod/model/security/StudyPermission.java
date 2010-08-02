/*
 * Copyright (C) 2009 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS of ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.ppod.model.security;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.EnumSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.hibernate.annotations.IndexColumn;

import edu.upenn.cis.ppod.model.Study;

/**
 * Persistent {@link DomainPermission}.
 * 
 * @author Sam Donnelly
 */
@Entity
@Table(name = StudyPermission.TABLE)
@edu.umd.cs.findbugs.annotations.SuppressWarnings
public final class StudyPermission extends WildcardPermission {

	public enum Action {
		READ,
		UPDATE,
		DELETE
	}

	static final String TABLE = "STUDY_PERMISSION";

	private static final long serialVersionUID = 1L;

	static final String ID_COLUMN = "ID";

	private Long id;

	private Integer version;

	@Column(name = "USER_NAME")
	@IndexColumn(name = "USER_NAME_IDX")
	private String username;

	@ManyToOne
	private Study parent;

	private Set<Action> actions = EnumSet.noneOf(Action.class);

	public StudyPermission() {
		super();
	}

	public StudyPermission(final Set<Action> actions, final Study study) {
		setParts(toWildcardString(actions, study));
	}

	private static String toWildcardString(
			final Set<Action> actions,
			final Study study) {
		checkNotNull(actions);
		checkNotNull(study);
		final StringBuilder sb = new StringBuilder();
		int actionPos = -1;
		for (final Action action : actions) {
			actionPos++;
			sb.append(action.toString());
			if (actionPos < actions.size() - 1) {
				sb.append(SUBPART_DIVIDER_TOKEN);
			}
		}
		sb.append(PART_DIVIDER_TOKEN);
		sb.append(study.getId()); // what if no id yet?
		return sb.toString();
	}

	@Override
	public boolean implies(final Permission p) {
		toWildcardString(getActions(), parent);
		return super.implies(p);
	}

	@ElementCollection
	public Set<Action> getActions() {
		return actions;
	}

	@Id
	@GeneratedValue
	@Column(name = ID_COLUMN)
	public Long getId() {
		return id;
	}

	@Version
	@Column(name = "OBJ_VERSION")
	@SuppressWarnings("unused")
	private Integer getVersion() {
		return version;
	}

	@SuppressWarnings("unused")
	private StudyPermission setId(final Long id) {
		this.id = id;
		return this;
	}

	@SuppressWarnings("unused")
	private void setVersion(final Integer version) {
		this.version = version;
	}
}
