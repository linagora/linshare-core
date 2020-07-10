/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2019-2020 LINAGORA
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

package org.linagora.linshare.server.embedded.ldap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.LDAPException;

public class LdapServerRule implements AfterEachCallback, BeforeEachCallback {
	private static final Log LOG = LogFactory.getLog(LdapServerRule.class);

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
	public void beforeEach(ExtensionContext context) throws Exception {
		start();
	}

	@Override
	public void afterEach(ExtensionContext context) throws Exception {
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
