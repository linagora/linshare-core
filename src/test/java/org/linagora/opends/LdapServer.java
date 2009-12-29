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
 *   (c) 2009-2010 Groupe Linagora - http://linagora.org
 * 
 *   This file as been copied from the LSC project under BSD license
 */
package org.linagora.opends;

import org.opends.server.api.Backend;
import org.opends.server.core.DirectoryServer;
import org.opends.server.types.DN;
import org.opends.server.util.StaticUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to manage directory state (start/stop/status/...)
 * @author Sebastien Bahloul <sbahloul@linagora.com>
 */
public class LdapServer {

	/** The local logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(LdapServer.class);
	
	private static String DIRECTORY_REAL_ROOT = "dc=linpki,dc=org";
	
	public void start() throws Exception {
		LOGGER.info("Starting embedded OpenDS directory");
		EmbeddedOpenDS.startServer();
		EmbeddedOpenDS.initializeTestBackend(false, DIRECTORY_REAL_ROOT );
		Backend backend = DirectoryServer.getBackend(DN.decode(DIRECTORY_REAL_ROOT));
		backend.addEntry(StaticUtils.createEntry(DN.decode(DIRECTORY_REAL_ROOT)), null);
		if(EmbeddedOpenDS.class.getResource("test.ldif") == null || EmbeddedOpenDS.class.getResource("test.ldif").toURI().getPath() == null) {
			LOGGER.error("Unable to load LDIF sample content !");
		} else {
			EmbeddedOpenDS.importLdif(EmbeddedOpenDS.class.getResource("test.ldif").toURI().getPath());
			LOGGER.info("LDIF sample content loaded successfully");
		}
		LOGGER.info("Embedded OpenDS directory started");
	}
	
	public void stop() {
		EmbeddedOpenDS.shutdownServer("Normal stop process");
	}

}
