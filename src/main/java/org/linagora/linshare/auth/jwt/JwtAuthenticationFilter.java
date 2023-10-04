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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.trim;

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
		Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
		if (authenticationIsRequired(token, currentAuthentication)) {
			AbstractAuthenticationToken authentication = null;
			// FIXME: it is a very dirty workaround to differentiate Opaque Token for JWT token. 
			if (useOIDC) {
				String appNAme = request.getHeader(CLIENT_APP_HEADER);
				if (token.length() <= opaqueTokenThreshold) {
					authentication = new OidcOpaqueAuthenticationToken(token);
				} else if (LINSHARE_WEB_APP_NAME.equalsIgnoreCase(trim(appNAme))){
					String idToken = request.getHeader(ID_TOKEN_HEADER);
					if(StringUtils.isNotEmpty(idToken)) {
						authentication = new OidcJwtAuthenticationToken(token, idToken);
					}
				}
			} else {
				authentication = new JwtAuthenticationToken(token);
			}
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		filterChain.doFilter(request, response);
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
