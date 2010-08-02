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
package edu.upenn.cis.ppod.security;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.upenn.cis.ppod.dao.IDAOFactory;
import edu.upenn.cis.ppod.dao.IUserDAO;
import edu.upenn.cis.ppod.model.security.Role;
import edu.upenn.cis.ppod.model.security.StudyPermission;
import edu.upenn.cis.ppod.model.security.User;

/**
 * The Spring/Hibernate sample application's one and only configured Apache
 * Shiro Realm.
 * 
 * <p>
 * Because a Realm is really just a security-specific DAO, we could have just
 * made Hibernate calls directly in the implementation and named it a
 * 'HibernateRealm' or something similar.
 * </p>
 * 
 * <p>
 * But we've decided to make the calls to the database using a UserDAO, since a
 * DAO would be used in other areas of a 'real' application in addition to here.
 * We felt it better to use that same DAO to show code re-use.
 * </p>
 */
public class PPodRealm extends AuthorizingRealm {

	private IDAOFactory daoFactory;

	private final ISimpleAuthenticationInfoFactory simpleAuthenticationInfoFactory;

	@Inject
	public PPodRealm(
			final ISimpleAuthenticationInfoFactory simpleAutehnciationInfoFactory,
			final Provider<User> userProvider,
			final Provider<Role> roleProvider,
			final Provider<StudyPermission> permissionProvider) {
		setName("iniRealm"); // This name must match the name in the Subject
		// class's getPrincipals() method
		// setCredentialsMatcher(new Sha256CredentialsMatcher());
		setCredentialsMatcher(new AllowAllCredentialsMatcher());
		this.simpleAuthenticationInfoFactory = simpleAutehnciationInfoFactory;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			final AuthenticationToken authcToken)
			throws AuthenticationException {
		final UsernamePasswordToken token = (UsernamePasswordToken) authcToken;

		IUserDAO pPodUserDAO = daoFactory.getPPodUserDAO();

		User pPodUser = pPodUserDAO.getUserByName(token.getUsername());
		if (pPodUser == null) {
			throw new IllegalArgumentException(
					"unknown user " + token.getUsername());
		}
		return simpleAuthenticationInfoFactory.create(
				pPodUser.getId(),
				pPodUser.getPassword(),
				getName());
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
			final PrincipalCollection principals) {
		final Long userId =
				(Long) principals.fromRealm(getName()).iterator().next();
		final User pPodUser = daoFactory.getPPodUserDAO().get(userId, false);
		if (pPodUser != null) {
			final SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
			for (final Permission permission : pPodUser.getPermissions()) {
				info.addObjectPermission(permission);
			}
			for (final Role pPodRole : pPodUser.getRoles()) {
				info.addRole(pPodRole.getName());
				for (final Permission permission : pPodRole.getPermissions()) {
					info.addObjectPermission(permission);
				}
			}

			return info;
		} else {
			return null;
		}
	}

	public PPodRealm setDAOFactory(final IDAOFactory daoFactory) {
		this.daoFactory = daoFactory;
		return this;
	}

}
