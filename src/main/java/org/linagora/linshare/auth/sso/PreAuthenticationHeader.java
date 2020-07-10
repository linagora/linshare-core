/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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
package org.linagora.linshare.auth.sso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.auth.AuthentificationFacade;
import org.linagora.linshare.core.repository.RootUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;

/**
 * This Spring Security filter is designed to filter authentication against a
 * LemonLDAP::NG Web Single Sign On
 * 
 * @author Clement Oudot &lt;coudot@linagora.com&gt;
 */
public class PreAuthenticationHeader extends RequestHeaderAuthenticationFilter {

	private static Logger logger = LoggerFactory
			.getLogger(PreAuthenticationHeader.class);

	private RootUserRepository rootUserRepository;

	private String principalRequestHeader;

	private String domainRequestHeader;

	/** List of IP / DNS hostname */
	private List<String> authorizedAddresses;

	private Boolean authorizedAddressesEnable;

	protected AuthentificationFacade authentificationFacade;

	public PreAuthenticationHeader(Boolean authorizedAddressesEnable, String authorizedAddressesList) {
		super();
		this.authorizedAddressesEnable = authorizedAddressesEnable;
		if (authorizedAddressesList != null) {
			List<String> asList = Arrays.asList(authorizedAddressesList
					.split(","));
			this.authorizedAddresses = asList;
		} else {
			this.authorizedAddresses = new ArrayList<String>();
		}
	}

	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		// Do not throw exception if header is not set
		String authenticationHeader = request.getHeader(principalRequestHeader);
		String domainIdentifier = request.getParameter("domain");
		if (domainIdentifier == null)	domainIdentifier = request.getHeader(domainRequestHeader);

		if (authenticationHeader != null) {
			if (authorizedAddressesEnable) {
				if (!authorizedAddresses.contains(request.getRemoteAddr())) {
					logger.error("SECURITY ALERT: Unauthorized header value '"
							+ authenticationHeader + "' from IP: "
							+ request.getRemoteAddr() + ":"
							+ request.getRemotePort());
					return null;
				}
			} else {
				logger.debug("Injected header value '"
						+ authenticationHeader + "' from IP: "
						+ request.getRemoteAddr() + ":"
						+ request.getRemotePort());
			}
			User foundUser = getPreAuthenticatedUser(authenticationHeader, domainIdentifier);
			if (foundUser == null) {
				logger.debug("No user was found with : " + authenticationHeader);
			logger.warn("PreAuthenticationHeader (SSO) is looking for someone who does not belong to the ldap domain anymore.");
				return null;
			}
			authenticationHeader = foundUser.getLsUuid();
		}
		return authenticationHeader;
	}

	private User getPreAuthenticatedUser(String authenticationHeader,
			String domainIdentifier) {
		// Looking for a root user no matter the domain.
		User foundUser = rootUserRepository.findByLogin(authenticationHeader);
		if (foundUser == null) {
			logger.debug("looking into ldap or db");
			try {
				// Workaround. everything need to be revamped.
				List<String> domains = Lists.newArrayList();
				if (domainIdentifier == null) {
					foundUser = authentificationFacade.findByLogin(authenticationHeader);
					domains = authentificationFacade.getAllDomains();
				} else {
					foundUser = authentificationFacade.findByLoginAndDomain(domainIdentifier, authenticationHeader);
					domains = authentificationFacade.getAllSubDomainIdentifiers(domainIdentifier);
				}
				if (foundUser == null) {
					for (String domainUuid : domains) {
						foundUser = authentificationFacade.ldapSearchForAuth(domainUuid, authenticationHeader);
						if (foundUser != null) {
							break;
						}
					}
				}
			} catch (UsernameNotFoundException e) {
				logger.error(e.getMessage());
				foundUser = null;
			}
		}
		if (foundUser != null) {
			try {
				foundUser = authentificationFacade.findOrCreateUser(foundUser.getDomainId(), foundUser.getMail());
			} catch (BusinessException e) {
				logger.error(e.getMessage());
				throw new AuthenticationServiceException(
						"Could not create user account : "
								+ foundUser.getDomainId() + " : "
								+ foundUser.getMail(), e);
			}
		}
		return foundUser;
	}

	public void setPrincipalRequestHeader(String principalRequestHeader) {
		Assert.hasText(principalRequestHeader,
				"principalRequestHeader must not be empty or null");
		this.principalRequestHeader = principalRequestHeader;
	}

	public void setDomainRequestHeader(String domainRequestHeader) {
		Assert.hasText(domainRequestHeader,
				"domainRequestHeader must not be empty or null");
		this.domainRequestHeader = domainRequestHeader;
	}

	public void setRootUserRepository(RootUserRepository rootUserRepository) {
		this.rootUserRepository = rootUserRepository;
	}

	public String getPrincipalRequestHeader() {
		return principalRequestHeader;
	}

	public String getDomainRequestHeader() {
		return domainRequestHeader;
	}

	public List<String> getAuthorizedAddresses() {
		return authorizedAddresses;
	}

	public void setAuthentificationFacade(AuthentificationFacade authentificationFacade) {
		this.authentificationFacade = authentificationFacade;
	}
}
