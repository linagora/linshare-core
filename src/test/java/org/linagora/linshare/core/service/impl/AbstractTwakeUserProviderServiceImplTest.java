/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 *
 * Copyright (C) 2021-2022 LINAGORA
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
package org.linagora.linshare.core.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.linagora.linshare.core.domain.constants.ServerType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.TwakeConnection;
import org.linagora.linshare.core.domain.entities.TwakeUserProvider;
import org.linagora.linshare.core.domain.entities.User;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public abstract class AbstractTwakeUserProviderServiceImplTest {

	@Test
	public void twakeUserProviderShouldNotFailWhenUnknownEntriesInJson() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(unknownEntryUserResponseFileName()));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.searchUser(domain, userProvider, "", "", "");
		assertThat(users).hasSize(1);
	}

	protected abstract String unknownEntryUserResponseFileName();

	protected abstract TwakeUserProviderService implementation(HttpUrl httpUrl);

	protected abstract AbstractDomain mockDomain();

	@Test
	public void searchUserShouldReturnTheUsersList() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.searchUser(domain, userProvider, "", "", "");
		assertThat(users).hasSize(4);
	}

	protected abstract String usersResponseFileName();

	@Test
	public void searchUserShouldReturnEmptyListWhenAllBlocked() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(allBlockedUsersResponseFileName()));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.searchUser(domain, userProvider, "", "", "");
		assertThat(users).isEmpty();
	}

	protected abstract String allBlockedUsersResponseFileName();

	@Test
	public void searchUserShouldReturnEmptyListWhenAllWrongDomainKind() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(allWrongDomainKindUsersResponseFileName()));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.searchUser(domain, userProvider, "", "", "");
		assertThat(users).isEmpty();
	}

	protected abstract String allWrongDomainKindUsersResponseFileName();

	@Test
	public void searchUserShouldParseResponse() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(oneUserResponseFileName()));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.searchUser(domain, userProvider, "", "", "");
		assertThat(users).hasSize(1);

		User user = users.get(0);
		assertThat(user.getFirstName()).isEqualTo("antoine");
		assertThat(user.getLastName()).isEqualTo("toine");
		assertThat(user.getMail()).isEqualTo("antoine@linshare.org");
	}

	protected abstract String oneUserResponseFileName();

	@Test
	public void searchUserShouldFilterByMail() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.searchUser(domain, userProvider, "ric@lins", "", "");
		assertThat(users).hasSize(1);
	}

	@Test
	public void searchUserShouldFilterByFirstName() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.searchUser(domain, userProvider, "", "anto", "");
		assertThat(users).hasSize(1);
	}

	@Test
	public void searchUserShouldFilterByLastName() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.searchUser(domain, userProvider, "", "", "toin");
		assertThat(users).hasSize(1);
	}

	@Test
	public void findUserShouldReturnTheUserWhenExists() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(oneUserResponseFileName()));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		User user = testee.findUser(domain, userProvider, "antoine@linshare.org");
		assertThat(user.getFirstName()).isEqualTo("antoine");
		assertThat(user.getLastName()).isEqualTo("toine");
		assertThat(user.getMail()).isEqualTo("antoine@linshare.org");
	}

	@Test
	public void findUserShouldReturnNullWhenDoesNotExist() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(oneUserResponseFileName()));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		User user = testee.findUser(domain, userProvider, "wrong@bad.org");
		assertThat(user).isNull();
	}

	@Test
	public void autoCompleteUserShouldReturnTheUsersListWhenEmptyPattern() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.autoCompleteUser(domain, userProvider, "");
		assertThat(users).hasSize(4);
	}

	@Test
	public void autoCompleteUserShouldReturnEmptyListWhenNoneMatch() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.autoCompleteUser(domain, userProvider, "wrong");
		assertThat(users).isEmpty();
	}

	@Test
	public void autoCompleteUserShouldReturnTheUsersListWhenSomeMatch() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.autoCompleteUser(domain, userProvider, "rede");
		assertThat(users).hasSize(1);
	}

	@Test
	public void autoCompleteUserByNamesShouldReturnTheUsersListWhenEmptyPattern() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.autoCompleteUser(domain, userProvider, "", "");
		assertThat(users).hasSize(4);
	}

	@Test
	public void autoCompleteUserByNamesShouldReturnTheUsersListWhenSomeMatchFirstName() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.autoCompleteUser(domain, userProvider, "ant", "");
		assertThat(users).hasSize(1);
	}

	@Test
	public void autoCompleteUserByNamesShouldReturnTheUsersListWhenSomeMatchLastName() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.autoCompleteUser(domain, userProvider, "", "toin");
		assertThat(users).hasSize(1);
	}

	@Test
	public void autoCompleteUserByNamesShouldReturnTheUsersListWhenSomeMatchFirstNameAndLastName() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.autoCompleteUser(domain, userProvider, "oine", "toin");
		assertThat(users).hasSize(1);
	}

	@Test
	public void autoCompleteUserByNamesShouldReturnEmptyListWhenNoneMatchFirstNameAndLastName() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.autoCompleteUser(domain, userProvider, "rede", "toin");
		assertThat(users).isEmpty();
	}

	@Test
	public void isUserExistShouldReturnTrueWhenExists() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(oneUserResponseFileName()));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		assertThat(testee.isUserExist(domain, userProvider, "antoine@linshare.org")).isTrue();
	}

	@Test
	public void isUserExistShouldReturnFalseWhenDoesNotExist() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(oneUserResponseFileName()));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		assertThat(testee.isUserExist(domain, userProvider, "wrong@bad.org")).isFalse();
	}

	@Test
	public void searchForAuthShouldReturnNullWhenUsersDoesntExist() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		User user = testee.searchForAuth(domain, userProvider, "ric@lins");
		assertThat(user).isNull();
	}

	@Test
	public void searchForAuthShouldReturnUserWhenExists() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		User user = testee.searchForAuth(domain, userProvider, "frederic@linshare.org");
		assertThat(user).isNotNull();
	}
}
