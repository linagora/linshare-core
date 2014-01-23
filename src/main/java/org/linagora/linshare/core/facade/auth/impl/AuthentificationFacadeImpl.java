/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.facade.auth.impl;

import java.util.List;

import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserLogEntry;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.auth.AuthentificationFacade;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.UserService;

public class AuthentificationFacadeImpl implements AuthentificationFacade {

	private final UserService userService;

	private final LogEntryService logEntryService;

	private final AbstractDomainService abstractDomainService;

	public AuthentificationFacadeImpl(UserService userService, LogEntryService logEntryService,
			AbstractDomainService abstractDomainService) {
		super();
		this.userService = userService;
		this.logEntryService = logEntryService;
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	public User loadUserDetails(String uuid) throws BusinessException {
		return userService.findByLsUuid(uuid);
	}

	@Override
	public User findOrCreateUser(String domainIdentifier, String mail)
			throws BusinessException {
		return userService.findOrCreateUser(mail, domainIdentifier);
	}

	@Override
	public void logAuthError(String login, String domainIdentifier,
			String message) throws BusinessException {
		logEntryService.create(new UserLogEntry(login, domainIdentifier,
				LogAction.USER_AUTH_FAILED, message));
	}

	@Override
	public void logAuthError(User user, String domainIdentifier, String message)
			throws BusinessException {
		logEntryService.create(new UserLogEntry(user,
				LogAction.USER_AUTH_FAILED, message, user));
	}

	@Override
	public void logAuthSuccess(User user) throws BusinessException {
		logEntryService.create(new UserLogEntry(user,
				LogAction.USER_AUTH, "Successfull authentification"));
	}

	@Override
	public AbstractDomain retrieveDomain(String domainIdentifier) {
		return abstractDomainService.retrieveDomain(domainIdentifier);
	}

	@Override
	public List<AbstractDomain> getAllDomains() {
		return abstractDomainService.getAllDomains();
	}
}
