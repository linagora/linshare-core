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

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class LinShareWebAuthenticationDetails extends WebAuthenticationDetails {

	private static final long serialVersionUID = -4056430945615709737L;

	private final String secondFactorAuthCode;

	public LinShareWebAuthenticationDetails(HttpServletRequest request) {
		super(request);
		this.secondFactorAuthCode = request.getHeader("X-LinShare-2fa-pin");
	}

	public String getSecondFactorAuthCode() {
		return secondFactorAuthCode;
	}
}
