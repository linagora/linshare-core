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
