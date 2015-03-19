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
package org.linagora.linshare.view.tapestry.services.impl;

import org.apache.tapestry5.services.ApplicationStateManager;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.auth.AuthentificationFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *  Populates the UserVo ASO if user is authentified via Spring Security.
 *  Doesn't do much more
 */
public class UserAccessAuthentity  {

	private final AuthentificationFacade authentificationFacade;

	private final ApplicationStateManager applicationStateManager;

	private static final Logger logger = LoggerFactory.getLogger(UserAccessAuthentity.class);

	public UserAccessAuthentity(AuthentificationFacade accountFacade, ApplicationStateManager applicationStateManager) {
		this.authentificationFacade = accountFacade;
		this.applicationStateManager = applicationStateManager;
	}

	public void processAuth() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null) {
			// If we are logged

			if (applicationStateManager.getIfExists(UserVo.class) == null) {
				// fetch user if not existing
				UserDetails userDetails = (UserDetails) authentication.getPrincipal();
				String username = userDetails.getUsername();
				logger.debug("processAuth with " + username);
				UserVo userVo = null;
				try {
					userVo = authentificationFacade.loadUserVoDetails(username.toLowerCase());
					applicationStateManager.set(UserVo.class, userVo);
				} catch (BusinessException e) {
					logger.error("Error while trying to find user details", e);
				}
			} else {
				// if the login doesn't match the session user email, change the user
				String lsUuid = applicationStateManager.getIfExists(UserVo.class).getLsUuid();
				UserDetails userDetails = (UserDetails) authentication.getPrincipal();
				String username = userDetails.getUsername();
				if (!lsUuid.equalsIgnoreCase(username)) {
					// fetch user 
					UserVo userVo = null;
					try {
						userVo = new UserVo(authentificationFacade.loadUserDetails(username.toLowerCase()));
						applicationStateManager.set(UserVo.class, userVo);
					} catch (BusinessException e) {
						logger.error("Error while trying to find user details", e);
					}
				}
			}
		}
	}
}
