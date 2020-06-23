/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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
		// TODO Auto-generated method stub
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		logger.info(userDetails.toString());
		User user = userRepository.findByLsUuid(userDetails.getUsername());
		authentificationFacade.logAuthSuccess(user);
	}

	@Override
	public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
		if (authentication.getPrincipal() instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			logger.info(userDetails.toString());
			User user = userRepository.findByLsUuid(userDetails.getUsername());
			authentificationFacade.logAuthError(user, exception.getMessage());
		} else {
			User user = userRepository.findByLogin(authentication.getPrincipal().toString());
			if (user != null) {
				logger.info("Authentication failure for:" + authentication.toString());
				authentificationFacade.logAuthError(user, exception.getMessage());
			} else {
				logger.debug("Unknown account:" + authentication.toString(), exception);
			}
		}
	}


}
