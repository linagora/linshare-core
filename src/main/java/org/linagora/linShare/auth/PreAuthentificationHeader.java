/*
 *    This file is part of Linshare. Initial work has been done by
 *    C. Oudot on LinID Directory Manager project
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
 *   (c) 2010 Groupe Linagora - http://linagora.org
 *
 */
package org.linagora.linShare.auth;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.ui.preauth.header.RequestHeaderPreAuthenticatedProcessingFilter;
import org.springframework.util.Assert;

/**
 * This Spring Security filter is designed to filter authentication
 * against a LemonLDAP::NG Web Single Sign On
 * @author Clement Oudot &lt;coudot@linagora.com&gt;
 */
public class PreAuthentificationHeader extends
		RequestHeaderPreAuthenticatedProcessingFilter {

	private String principalRequestHeader;

	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		// Do not throw exception if header is not set
		String obj = request.getHeader(principalRequestHeader);
		return obj;
	}

	public void setPrincipalRequestHeader(String principalRequestHeader) {
		Assert.hasText(principalRequestHeader, "principalRequestHeader must not be empty or null");
		this.principalRequestHeader = principalRequestHeader;
	}
}
