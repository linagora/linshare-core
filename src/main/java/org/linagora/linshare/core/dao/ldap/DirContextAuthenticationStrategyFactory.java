/*
 *    This file is part of Linshare.
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
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linshare.core.dao.ldap;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.support.DefaultTlsDirContextAuthenticationStrategy;
import org.springframework.ldap.core.support.DirContextAuthenticationStrategy;
import org.springframework.ldap.core.support.ExternalTlsDirContextAuthenticationStrategy;
import org.springframework.ldap.core.support.SimpleDirContextAuthenticationStrategy;

/**
 * This factory returns an instance of DirContextAuthenticationStrategy
 * @deprecated not used anymore since domain implementation
 */
public class DirContextAuthenticationStrategyFactory {

	private static final String SIMPLE = "SIMPLE";
	private static final String DEFAULT_TLS = "DEFAULT_TLS";
	private static final String EXTERNAL_TLS = "EXTERNAL_TLS";

	private final String strategy;
	
	private final boolean useMyTruststore;
	private final String pathToTruststore;
	private final String password;
	
	
    Logger logger = LoggerFactory.getLogger(DirContextAuthenticationStrategyFactory.class);
	

	public DirContextAuthenticationStrategyFactory(String strategy,
			String pathToTruststore, String password) {
		this.strategy = strategy;

		if (pathToTruststore == null || password == null
				|| pathToTruststore.equals("") || password.equals("")) {
			this.pathToTruststore = null;
			this.password = null;
			useMyTruststore = false;
			if (logger.isDebugEnabled())
				logger.debug("no configured truststore is used, use default jvm setting");

		} else {
			File trustoreFile = new File(pathToTruststore);

			if (trustoreFile.exists() && trustoreFile.canRead()) {
				if (logger.isInfoEnabled())
					logger.info("ldap auth use truststore:" + pathToTruststore);
				this.pathToTruststore = pathToTruststore;
				this.password = password;
				useMyTruststore = true;
			} else {
				logger.error("ldap auth bad truststore file:" + pathToTruststore);
				logger.info("the configured truststore is not used, use default jvm setting");
				
				this.pathToTruststore = null;
				this.password = null;
				useMyTruststore = false;
			}
		}

	}

	public DirContextAuthenticationStrategy getInstance() {
        if(useMyTruststore) {
            //override jvm setting else use default settings
            System.setProperty("javax.net.ssl.trustStore",pathToTruststore);
            System.setProperty("javax.net.ssl.trustStorePassword",password);
        }
		if (DEFAULT_TLS.equals(strategy)) {
			return new DefaultTlsDirContextAuthenticationStrategy();
		} else if (EXTERNAL_TLS.equals(strategy)) {
			return new ExternalTlsDirContextAuthenticationStrategy();
		} else {
			return new SimpleDirContextAuthenticationStrategy();
		}
	}
}
