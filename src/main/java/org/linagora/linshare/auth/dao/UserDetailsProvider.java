/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.auth.dao;

import org.linagora.linshare.auth.exceptions.BadDomainException;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.auth.AuthentificationFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class UserDetailsProvider {

	private static final Logger logger = LoggerFactory.getLogger(UserDetailsProvider.class);

	protected AuthentificationFacade authentificationFacade;

	public UserDetailsProvider(AuthentificationFacade logEntryService) {
		super();
		this.authentificationFacade = logEntryService;
	}

	public abstract User retrieveUser(String domainIdentifier, String login);

	public void logAuthError(String login, String domainIdentifier,
			String message) {
		try {
			authentificationFacade.logAuthError(login, domainIdentifier, message);
		} catch (IllegalArgumentException e) {
			logger.error("Couldn't log an authentication failure : " + message);
			logger.debug(e.getMessage());
		} catch (BusinessException e1) {
			logger.error("Couldn't log an authentication failure : " + message);
			logger.debug(e1.getMessage());
		}
	}

	public void logAuthError(User user, String domainIdentifier, String message) {
		try {
			authentificationFacade.logAuthError(user, domainIdentifier, message);
		} catch (IllegalArgumentException e) {
			logger.error("Couldn't log an authentication failure : " + message);
			logger.debug(e.getMessage());
		} catch (BusinessException e1) {
			logger.error("Couldn't log an authentication failure : " + message);
			logger.debug(e1.getMessage());
		}
	}

	public void logAuthError(User user, String message) {
		try {
			authentificationFacade.logAuthError(user, message);
		} catch (IllegalArgumentException e) {
			logger.error("Couldn't log an authentication failure : " + message);
			logger.debug(e.getMessage());
		} catch (BusinessException e1) {
			logger.error("Couldn't log an authentication failure : " + message);
			logger.debug(e1.getMessage());
		}
	}

	public void logAuthSuccess(User user) {
		try {
			authentificationFacade.logAuthSuccess(user);
		} catch (IllegalArgumentException e) {
			logger.error("Error while trying to log user successfull auth", e);
		} catch (BusinessException e) {
			logger.error("Error while trying to log user successfull auth", e);
		}
	}

	public void logAuthSuccess(String userUuid) {
		try {
			User user = authentificationFacade.loadUserDetails(userUuid);
			authentificationFacade.logAuthSuccess(user);
		} catch (IllegalArgumentException e) {
			logger.error("Error while trying to log user successfull auth", e);
		} catch (BusinessException e) {
			logger.error("Error while trying to log user successfull auth", e);
		}
	}

	protected AbstractDomain retrieveDomain(String login, String domainIdentifier) {
		AbstractDomain domain = authentificationFacade.retrieveDomain(domainIdentifier);
		if (domain == null) {
			logger.error("Can't find the specified domain : "
					+ domainIdentifier);
			logAuthError(login, domainIdentifier, "Bad domain.");
			throw new BadDomainException("Domain '" + domainIdentifier
					+ "' not found");
		}
		return domain;
	}
}
