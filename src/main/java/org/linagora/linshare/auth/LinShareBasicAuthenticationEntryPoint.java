/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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

package org.linagora.linshare.auth;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.linagora.linshare.auth.exceptions.LinShareAuthenticationException;
import org.linagora.linshare.auth.exceptions.LinShareAuthenticationExceptionCode;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

public class LinShareBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {
		String authenticationHeader = request.getHeader("WWW-No-Authenticate");
		LinShareAuthenticationExceptionCode errorCode = LinShareAuthenticationExceptionCode.DEFAULT_AUTHENTICATION_ERROR;
		if (authException instanceof LinShareAuthenticationException) {
			errorCode = ((LinShareAuthenticationException) authException).getErrorCode();
		} else if (authException instanceof LockedException) {
			errorCode = LinShareAuthenticationExceptionCode.ACCOUNT_LOCKED;
		} else if (authException instanceof BadCredentialsException) {
			errorCode = LinShareAuthenticationExceptionCode.BAD_CREDENTIAL;
		}
		if (authenticationHeader == null) {
			/**
			 *  1002 and 1003 error codes are related to the 2FA.
			 *  It means login/password authentication succeeded but TOTP was invalid or not provided.
			 *  So we inform the client of the supported authentication process.
			 */
			if (errorCode.equals(LinShareAuthenticationExceptionCode.TOTP_BAD_VALUE)
					|| errorCode.equals(LinShareAuthenticationExceptionCode.TOTP_BAD_FORMAT)) {
				response.addHeader("WWW-Authenticate-", "Basic+TOTP realm=\"" + this.getRealmName() + "\"");
			} else {
				response.addHeader("WWW-Authenticate", "Basic realm=\"" + this.getRealmName() + "\"");
			}
		}
		response.addIntHeader("X-LinShare-Auth-error-code", errorCode.toInt());
		response.addHeader("X-LinShare-Auth-error-msg", authException.getMessage());
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
	}
}
