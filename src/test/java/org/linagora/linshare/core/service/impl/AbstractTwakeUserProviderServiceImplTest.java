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

import mockwebserver3.MockResponse;
import okhttp3.HttpUrl;

public abstract class AbstractTwakeUserProviderServiceImplTest extends AbstractTwakeUserProviderServiceImplEnv {

	@Test
	public void twakeUserProviderShouldNotFailWhenUnknownEntriesInJson() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(unknownEntryUserResponseFileName()));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
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
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.searchUser(domain, userProvider, "", "", "");
		assertThat(users).hasSize(4);
	}

	protected abstract String usersResponseFileName();

	@Test
	public void searchUserShouldReturnEmptyListWhenAllBlocked() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(allBlockedUsersResponseFileName()));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.searchUser(domain, userProvider, "", "", "");
		assertThat(users).isEmpty();
	}

	protected abstract String allBlockedUsersResponseFileName();

	@Test
	public void searchUserShouldReturnEmptyListWhenAllWrongDomainKind() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(allWrongDomainKindUsersResponseFileName()));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.searchUser(domain, userProvider, "", "", "");
		assertThat(users).isEmpty();
	}

	protected abstract String allWrongDomainKindUsersResponseFileName();

	@Test
	public void searchUserShouldParseResponse() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(oneUserResponseFileName()));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
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
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.searchUser(domain, userProvider, "ric@lins", "", "");
		assertThat(users).hasSize(1);
	}

	@Test
	public void searchUserShouldFilterByFirstName() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.searchUser(domain, userProvider, "", "anto", "");
		assertThat(users).hasSize(1);
	}

	@Test
	public void searchUserShouldFilterByLastName() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.searchUser(domain, userProvider, "", "", "toin");
		assertThat(users).hasSize(1);
	}

	@Test
	public void findUserShouldReturnTheUserWhenExists() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(oneUserResponseFileName()));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
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
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(oneUserResponseFileName()));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		User user = testee.findUser(domain, userProvider, "wrong@bad.org");
		assertThat(user).isNull();
	}

	@Test
	public void autoCompleteUserShouldReturnTheUsersListWhenEmptyPattern() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.autoCompleteUser(domain, userProvider, "");
		assertThat(users).hasSize(4);
	}

	@Test
	public void autoCompleteUserShouldReturnEmptyListWhenNoneMatch() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.autoCompleteUser(domain, userProvider, "wrong");
		assertThat(users).isEmpty();
	}

	@Test
	public void autoCompleteUserShouldReturnTheUsersListWhenSomeMatch() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.autoCompleteUser(domain, userProvider, "rede");
		assertThat(users).hasSize(1);
	}

	@Test
	public void autoCompleteUserByNamesShouldReturnTheUsersListWhenEmptyPattern() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.autoCompleteUser(domain, userProvider, "", "");
		assertThat(users).hasSize(4);
	}

	@Test
	public void autoCompleteUserByNamesShouldReturnTheUsersListWhenSomeMatchFirstName() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.autoCompleteUser(domain, userProvider, "ant", "");
		assertThat(users).hasSize(1);
	}

	@Test
	public void autoCompleteUserByNamesShouldReturnTheUsersListWhenSomeMatchLastName() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.autoCompleteUser(domain, userProvider, "", "toin");
		assertThat(users).hasSize(1);
	}

	@Test
	public void autoCompleteUserByNamesShouldReturnTheUsersListWhenSomeMatchFirstNameAndLastName() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.autoCompleteUser(domain, userProvider, "oine", "toin");
		assertThat(users).hasSize(1);
	}

	@Test
	public void autoCompleteUserByNamesShouldReturnEmptyListWhenNoneMatchFirstNameAndLastName() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.autoCompleteUser(domain, userProvider, "rede", "toin");
		assertThat(users).isEmpty();
	}

	@Test
	public void isUserExistShouldReturnTrueWhenExists() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(oneUserResponseFileName()));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		assertThat(testee.isUserExist(domain, userProvider, "antoine@linshare.org")).isTrue();
	}

	@Test
	public void isUserExistShouldReturnFalseWhenDoesNotExist() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(oneUserResponseFileName()));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		assertThat(testee.isUserExist(domain, userProvider, "wrong@bad.org")).isFalse();
	}

	@Test
	public void searchForAuthShouldReturnNullWhenUsersDoesntExist() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		User user = testee.searchForAuth(domain, userProvider, "ric@lins");
		assertThat(user).isNull();
	}

	@Test
	public void searchForAuthShouldReturnUserWhenExists() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream(usersResponseFileName()));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mockDomain();
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		User user = testee.searchForAuth(domain, userProvider, "frederic@linshare.org");
		assertThat(user).isNotNull();
	}
}
