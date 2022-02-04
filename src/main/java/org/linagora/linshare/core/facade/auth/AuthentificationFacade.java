/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
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

	public User ldapSearchForAuth(String domainIdentifier, String login) throws BusinessException;

	public User checkStillInLdap(User user, String login) throws BusinessException;

	public boolean isJwtLongTimeFunctionalityEnabled(String domainUuid);

	OIDCUserProviderDto findOidcProvider(String oidcDomainIdentifier);
}