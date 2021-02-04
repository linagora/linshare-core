/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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
package org.linagora.linshare.auth.oidc;

import java.util.List;

import org.linagora.linshare.auth.AuthRole;
import org.linagora.linshare.auth.RoleProvider;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.auth.AuthentificationFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;

import com.google.common.collect.Lists;

public class OidcOpaqueAuthenticationProvider implements AuthenticationProvider {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	private AuthentificationFacade authentificationFacade;

	private OpaqueTokenAuthenticationProvider opaqueTokenAuthenticationProvider;

	public void setAuthentificationFacade(AuthentificationFacade authentificationFacade) {
		this.authentificationFacade = authentificationFacade;
	}

	public void setOpaqueTokenAuthenticationProvider(OpaqueTokenAuthenticationProvider opaqueTokenAuthenticationProvider) {
		this.opaqueTokenAuthenticationProvider = opaqueTokenAuthenticationProvider;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		// Quick workaround: Not very nice code. :(
		OidcOpaqueAuthenticationToken jwtAuthentication = (OidcOpaqueAuthenticationToken) authentication;
		final String token = jwtAuthentication.getToken();
		BearerTokenAuthenticationToken authToken = new BearerTokenAuthenticationToken(token);
		BearerTokenAuthentication authenticate = (BearerTokenAuthentication) opaqueTokenAuthenticationProvider
				.authenticate(authToken);
		logger.debug("OIDC opaque access token seems to be good. Processing authentication...");
		OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) authenticate.getPrincipal();

		User foundUser = null;
		String email = principal.getName();
		String domainUuid = principal.getAttribute("domain");
		if (domainUuid == null) {
			foundUser = authentificationFacade.findByLogin(email);
		} else {
			foundUser = authentificationFacade.findByLoginAndDomain(domainUuid, email);
		}
		if (foundUser == null) {
			List<String> domains = Lists.newArrayList();
			if (domainUuid == null) {
				domains = authentificationFacade.getAllDomains();
			} else {
				domains = authentificationFacade.getAllSubDomainIdentifiers(domainUuid);
			}
			for (String domain : domains) {
				foundUser = authentificationFacade.ldapSearchForAuth(domain, email);
				if (foundUser != null) {
					break;
				}
			}
		}
		if (foundUser == null) {
			logger.error("User not found: " + token);
			throw new UsernameNotFoundException("Could not find user account : " + authenticate.getTokenAttributes().toString());
		}
		try {
			// loading/creating the real entity
			foundUser = authentificationFacade.findOrCreateUser(foundUser.getDomainId(), foundUser.getMail());
		} catch (BusinessException e) {
			logger.error(e.getMessage(), e);
			throw new AuthenticationServiceException(
					"Could not create user account : "
					+ foundUser.getDomainId() + " : "
					+ foundUser.getMail(), e);
		}

		logger.info(String.format("Successful authentication of  %1$s with OIDC opaque token : %2$s", foundUser.getLsUuid(), authenticate.getTokenAttributes().toString()));
		List<GrantedAuthority> grantedAuthorities = RoleProvider.getRoles(foundUser);
		grantedAuthorities.add(new SimpleGrantedAuthority(AuthRole.ROLE_AUTH_OIDC));
		UserDetails userDetail = new org.springframework.security.core.userdetails.User(foundUser.getLsUuid(), "", true,
				true, true, true, grantedAuthorities);

		return new UsernamePasswordAuthenticationToken(userDetail, jwtAuthentication.getCredentials(), grantedAuthorities);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (OidcOpaqueAuthenticationToken.class).isAssignableFrom(authentication);
	}

}
