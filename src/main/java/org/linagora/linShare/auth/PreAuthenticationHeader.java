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

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.ui.preauth.header.RequestHeaderPreAuthenticatedProcessingFilter;
import org.springframework.util.Assert;

/**
 * This Spring Security filter is designed to filter authentication
 * against a LemonLDAP::NG Web Single Sign On
 * @author Clement Oudot &lt;coudot@linagora.com&gt;
 */
public class PreAuthenticationHeader extends
		RequestHeaderPreAuthenticatedProcessingFilter {

	/** */
	private String principalRequestHeader;
	
	/** List of IP / DNS hostname */
	private List<String> authorizedAddresses;
	
	private static Logger logger = LoggerFactory.getLogger(PreAuthenticationHeader.class);

	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		// Do not throw exception if header is not set
		String authenticationHeader = request.getHeader(principalRequestHeader);
		if(authenticationHeader != null) {
			if(!authorizedAddresses.contains(request.getRemoteAddr())) {
				logger.error("SECURITY ALERT: Unauthorized header value '" + authenticationHeader 
						+ "' from IP: " + request.getRemoteAddr() + ":" + request.getRemotePort());
				return null;
			}
		}
		return authenticationHeader;
	}

	public void setPrincipalRequestHeader(String principalRequestHeader) {
		Assert.hasText(principalRequestHeader, "principalRequestHeader must not be empty or null");
		this.principalRequestHeader = principalRequestHeader;
	}

	public void setAuthorizedAddresses(List<String> authorizedAddresses) {
		Assert.hasText(authorizedAddresses.toString(), "authorizedAddresses must not be empty or null");
		this.authorizedAddresses = authorizedAddresses;
	}
}
