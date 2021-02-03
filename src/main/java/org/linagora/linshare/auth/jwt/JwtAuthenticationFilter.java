/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2020 LINAGORA
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
package org.linagora.linshare.auth.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

	// Do not remove trailer space.
	private static final String AUTH_METHOD = "Bearer ";

	private Integer opaqueTokenThreshold;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String header = request.getHeader(AUTH_HEADER);
		if (header == null || !header.startsWith(AUTH_METHOD)) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = header.substring(AUTH_METHOD.length());
		if (token == null || token.isEmpty()) {
			logger.warn("Bearer header without token: ", header);
			filterChain.doFilter(request, response);
			return;
		}
		Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
		if (authenticationIsRequired(token, currentAuthentication)) {
			AbstractAuthenticationToken authentication = null;
			// FIXME: it is a very dirty workaround to differentiate Opaque Token for JWT token. 
			if (token.length() > opaqueTokenThreshold) {
				authentication = new JwtAuthenticationToken(token);
			} else {
				authentication = new OidcOpaqueAuthenticationToken(token);
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

}
