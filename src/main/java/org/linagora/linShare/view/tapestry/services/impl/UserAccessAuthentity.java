/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.view.tapestry.services.impl;

import java.util.GregorianCalendar;

import org.apache.tapestry5.services.ApplicationStateManager;
import org.linagora.linShare.core.Facade.AccountFacade;
import org.linagora.linShare.core.domain.constants.LogAction;
import org.linagora.linShare.core.domain.entities.UserLogEntry;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.service.LogEntryService;
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

    private final AccountFacade accountFacade;
    private final ApplicationStateManager applicationStateManager;
    private final LogEntryService logEntryService;
    
	private static final Logger logger = LoggerFactory.getLogger(UserAccessAuthentity.class);

    public UserAccessAuthentity(AccountFacade accountFacade, ApplicationStateManager applicationStateManager,
    		LogEntryService logEntryService) {
        this.accountFacade = accountFacade;
        this.applicationStateManager = applicationStateManager;
        this.logEntryService = logEntryService;
    }

    public void processAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null) {
        	// If we are logged
        	if (applicationStateManager.getIfExists(UserVo.class) == null) {
        		// fetch user if not existing
	            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	            logger.debug("processAuth with " + userDetails.getUsername());
	            UserVo userVo = null;
				try {
					userVo = accountFacade.loadUserDetails(userDetails.getUsername().toLowerCase());
				} catch (BusinessException e) {
					logger.error("Error while trying to find user details", e);
				}
				generateAuthLogEntry(userVo);
				applicationStateManager.set(UserVo.class, userVo);
        	} else {
        		// if the login doesn't match the session user email, change the user
        		if (!applicationStateManager.getIfExists(UserVo.class).getMail().equalsIgnoreCase(
        				((UserDetails)(authentication.getPrincipal())).getUsername())) {
        			// fetch user 
    	            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    	            UserVo userVo = null;
					try {
						userVo = accountFacade.loadUserDetails(userDetails.getUsername().toLowerCase());
					} catch (BusinessException e) {
						logger.error("Error while trying to find user details", e);
					}
					generateAuthLogEntry(userVo);
    	            applicationStateManager.set(UserVo.class, userVo);
        		}
        	}
        }
    }

	private void generateAuthLogEntry(UserVo userVo) {
		UserLogEntry logEntry = new UserLogEntry(new GregorianCalendar(), userVo.getLogin(), 
				userVo.getFirstName(), userVo.getLastName(), userVo.getDomainIdentifier(),
				LogAction.USER_AUTH, "Successfull authentification", null, null, null, null, null);
		try {
			logEntryService.create(logEntry);
		} catch (IllegalArgumentException e) {
			logger.error("Error while trying to log user successfull auth", e);
		} catch (BusinessException e) {
			logger.error("Error while trying to log user successfull auth", e);
		}
	}

}
