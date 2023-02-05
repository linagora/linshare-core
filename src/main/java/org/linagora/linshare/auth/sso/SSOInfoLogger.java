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
package org.linagora.linshare.auth.sso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSOInfoLogger {

	private static Logger logger = LoggerFactory.getLogger(SSOInfoLogger.class);

	protected PreAuthenticationHeader authFilter;

	public SSOInfoLogger(PreAuthenticationHeader authFilter) {
		super();
		this.authFilter = authFilter;
	}

	public SSOInfoLogger() {
		super();
	}

	public void afterPropertiesSet() throws Exception {
		if (this.authFilter == null) {
			logger.info("SSO: SSO mode is disabled.");
		} else {
			logger.info("SSO:SSO mode is enabled.");
			logger.info("SSO:User request header : {}", authFilter.getPrincipalRequestHeader());
			logger.info("SSO:Domain request header : {}", authFilter.getDomainRequestHeader());
			logger.info("SSO:Authorized addresses : {}", authFilter.getAuthorizedAddresses().toString());
		}
	}

}
