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
package org.linagora.linshare.auth.jwt;

import static org.apache.commons.lang3.StringUtils.trim;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.linagora.linshare.auth.oidc.OidcJwtAuthenticationToken;
import org.linagora.linshare.auth.oidc.OidcOpaqueAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	private static final String AUTH_HEADER = "Authorization";
	private static final String CLIENT_APP_HEADER = "X-LinShare-Client-App";
	private static final String ID_TOKEN_HEADER = "X-LinShare-ID-Token";
	private static final String LINSHARE_WEB_APP_NAME = "Linshare-Web";

	// Do not remove trailer space.
	private static final String AUTH_METHOD = "Bearer ";

	private Integer opaqueTokenThreshold;

	private Boolean useOIDC;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String header = request.getHeader(AUTH_HEADER);
		if (header == null || !header.startsWith(AUTH_METHOD)) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = header.substring(AUTH_METHOD.length());
		if (token.isEmpty()) {
			logger.warn("Bearer header without token: {}", header);
			filterChain.doFilter(request, response);
			return;
		}

		if (authenticationIsRequired(token, SecurityContextHolder.getContext().getAuthentication())) {
			AbstractAuthenticationToken authentication = selectAuthentication(request, token);
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		filterChain.doFilter(request, response);
	}

	// Workaround for mobile auth: first call to "/jwt" to create a permanent token is made with oidc opaque token,
	// then all following calls are made with legacy jwt authentication (not oidc)
	private AbstractAuthenticationToken selectAuthentication(HttpServletRequest request, String token) {
		String idToken = request.getHeader(ID_TOKEN_HEADER);

		// FIXME: it is a very dirty workaround to differentiate Opaque Token for JWT token.
		if (useOIDC && token.length() <= opaqueTokenThreshold) {
			return new OidcOpaqueAuthenticationToken(token);

			//If we don't receive specifically the web app header, we assume the request come from mobile
		} else if (useOIDC && isLinshareWebApp(request) && StringUtils.isNotEmpty(idToken)) {
			return new OidcJwtAuthenticationToken(token, idToken);

		} else {
			return new JwtAuthenticationToken(token);
		}
	}

	private static boolean isLinshareWebApp(HttpServletRequest request) {
		return LINSHARE_WEB_APP_NAME.equalsIgnoreCase(trim(request.getHeader(CLIENT_APP_HEADER)));
	}

	private boolean authenticationIsRequired(String username, Authentication currentAuthentication) {
		if (currentAuthentication != null) {
			if (currentAuthentication.isAuthenticated()) {
				return false;
			}
			if (currentAuthentication.getName().equals(username)) {
				return false;
			}
		}
		return true;
	}

	public void setOpaqueTokenThreshold(Integer opaqueTokenThreshold) {
		this.opaqueTokenThreshold = opaqueTokenThreshold;
	}

	public void setUseOIDC(Boolean useOIDC) {
		this.useOIDC = useOIDC;
	}

}
