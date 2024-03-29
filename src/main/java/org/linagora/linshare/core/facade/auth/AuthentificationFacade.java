/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.facade.auth;

import java.util.List;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.OIDCUserProviderDto;

public interface AuthentificationFacade {

	User loadUserDetails(String uuid) throws BusinessException;

	User findOrCreateUser(String domainIdentifier, String mail) throws BusinessException;

	User  findByLogin(String login);

	User findByLoginAndDomain(String domain, String login);

	boolean userExist(String lsUuid);

	public void logAuthError(String login, String domainIdentifier, String message) throws BusinessException;

	void logAuthError(User user, String domainIdentifier, String message) throws BusinessException;

	void logAuthError(String userUuid, String message) throws BusinessException;

	public void logAuthSuccess(String userUuid) throws BusinessException;

	public AbstractDomain retrieveDomain(String domainIdentifier);

	public boolean isExist(String domainUuid);

	List<String> getAllSubDomainIdentifiers(String domainIdentifier);

	public List<String> getAllDomains();

	public User ldapAuth(String domainIdentifier,	String login, String userPasswd) throws BusinessException;

	public User userProviderSearchForAuth(String domainIdentifier, String login) throws BusinessException;

	public User checkStillInLdap(User user, String login) throws BusinessException;

	public boolean isJwtLongTimeFunctionalityEnabled(String domainUuid);

	OIDCUserProviderDto findOidcProvider(List<String> domainDiscriminators);
}