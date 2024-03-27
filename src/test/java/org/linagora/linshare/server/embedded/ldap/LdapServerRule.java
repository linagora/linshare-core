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
package org.linagora.linshare.server.embedded.ldap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.LDAPException;

public class LdapServerRule implements AfterAllCallback, BeforeAllCallback {
	private static final Logger LOG = LogManager.getLogger(LdapServerRule.class);

	public static final String DefaultDn = "cn=Directory Manager";
	public static final String DefaultPassword = "password";
	public static final int DefaultPort = 33389;

	private String baseDn;

	private String dn;

	private String password;

	private String lDiffPath;

	private int port;

	private InMemoryDirectoryServer server;

	public LdapServerRule() {
		this("dc=linshare,dc=org", "src/test/resources/test.ldif", DefaultPort);
	}

	public LdapServerRule(String baseDn, String lDiffPath, int port) {
		this.lDiffPath = lDiffPath;
		this.baseDn = baseDn;
		this.dn = DefaultDn;
		this.password = DefaultPassword;
		this.port = port;
	}

	@Override
	public void beforeAll(ExtensionContext context) throws Exception {
		start();
	}

	@Override
	public void afterAll(ExtensionContext context) throws Exception {
		stop();
	}

	public int getRunningPort() {
		return server.getListenPort();
	}

	private void start() {
		InMemoryDirectoryServerConfig config;
		try {
			config = new InMemoryDirectoryServerConfig(getBaseDn());
			config.addAdditionalBindCredentials(getDn(), getPassword());
			config.setSchema(null);
			config.setListenerConfigs(InMemoryListenerConfig.createLDAPConfig("LDAP", port));
			setServer(new InMemoryDirectoryServer(config));
			server.importFromLDIF(true, lDiffPath);
			server.startListening();
		} catch (LDAPException e) {
			throw new RuntimeException(e);
		}
	}

	private void stop() {
		server.shutDown(true);
		LOG.info("LDAP server " + toString() + " stopped");
	}

	public String getBaseDn() {
		return baseDn;
	}

	public String getDn() {
		return dn;
	}

	public String getPassword() {
		return password;
	}

	public InMemoryDirectoryServer getServer() {
		return server;
	}

	public void setServer(InMemoryDirectoryServer server) {
		this.server = server;
	}

	public String getLDiffPath() {
		return lDiffPath;
	}

	public int getListenPort() {
		return port;
	}

	public String getlDiffPath() {
		return lDiffPath;
	}

	public void setlDiffPath(String lDiffPath) {
		this.lDiffPath = lDiffPath;
	}

	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}
}
