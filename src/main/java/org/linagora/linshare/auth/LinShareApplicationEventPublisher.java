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
package org.linagora.linshare.auth;

import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.facade.auth.AuthentificationFacade;
import org.linagora.linshare.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

public class LinShareApplicationEventPublisher implements AuthenticationEventPublisher {

	final private static Logger logger = LoggerFactory.getLogger(LinShareApplicationEventPublisher.class);

	protected AuthentificationFacade authentificationFacade;

	protected UserRepository<User> userRepository;

	public LinShareApplicationEventPublisher(AuthentificationFacade authentificationFacade,
			UserRepository<User> userRepository) {
		super();
		this.authentificationFacade = authentificationFacade;
		this.userRepository = userRepository;
	}

	@Override
	public void publishAuthenticationSuccess(Authentication authentication) {
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		logger.info(userDetails.toString());
		authentificationFacade.logAuthSuccess(userDetails.getUsername());
	}

	@Override
	public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
		if (authentication.getPrincipal() instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			logger.info(userDetails.toString());
			authentificationFacade.logAuthError(userDetails.getUsername(), exception.getMessage());
		} else {
			String login = authentication.getPrincipal().toString();
			// No matter the provider, it should be store in the database if it is a valid user
			User user = userRepository.findByLogin(login);
			if (user != null) {
				logger.info("Authentication failure for:" + authentication.toString());
				authentificationFacade.logAuthError(user.getLsUuid(), exception.getMessage());
			} else {
				logger.debug("Unknown account:" + authentication.toString(), exception);
			}
		}
	}


}
