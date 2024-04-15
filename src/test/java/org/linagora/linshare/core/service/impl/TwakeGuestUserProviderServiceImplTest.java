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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.linagora.linshare.core.domain.constants.ServerType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AbstractTwakeUserProvider;
import org.linagora.linshare.core.domain.entities.TwakeConnection;
import org.linagora.linshare.core.domain.entities.TwakeUserProvider;
import org.linagora.linshare.core.domain.entities.User;

import mockwebserver3.MockResponse;
import okhttp3.HttpUrl;

public class TwakeGuestUserProviderServiceImplTest extends AbstractTwakeUserProviderServiceImplTest {

	private static class MyTwakeUserProviderServiceImpl extends TwakeGuestUserProviderServiceImpl {

		private final HttpUrl httpUrl;

		public MyTwakeUserProviderServiceImpl(HttpUrl httpUrl) {
			this.httpUrl = httpUrl;
		}

		@Override
		protected HttpUrl httpUrlFrom(AbstractTwakeUserProvider userProvider, Optional<String> extraPath) {
			return httpUrl;
		}
	}

	@Override
	protected TwakeUserProviderService implementation(HttpUrl httpUrl) {
		return new MyTwakeUserProviderServiceImpl(httpUrl);
	}

	@Override
	protected AbstractDomain mockDomain() {
		AbstractDomain domain = mock(AbstractDomain.class);
		when(domain.isGuestDomain())
			.thenReturn(true);
		return domain;
	}

	@Override
	protected String unknownEntryUserResponseFileName() {
		return "twake/twakeConsole-guest-users-response-unknown-entries.json";
	}

	@Override
	protected  String usersResponseFileName() {
		return "twake/twakeConsole-guest-users-response.json";
	}

	@Override
	protected String allBlockedUsersResponseFileName() {
		return "twake/twakeConsole-guest-users-response-all-blocked.json";
	}

	@Override
	protected String allWrongDomainKindUsersResponseFileName() {
		return "twake/twakeConsole-guest-users-response-all-members.json";
	}

	@Override
	protected String oneUserResponseFileName() {
		return "twake/twakeConsole-guest-users-response-one-user.json";
	}

	@Test
	public void searchUserShouldReturnEmptyListWhenNotGuestDomain() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream("twake/twakeConsole-users-response.json"));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mock(AbstractDomain.class);
		when(domain.isGuestDomain())
			.thenReturn(false);
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.searchUser(domain, userProvider, "", "", "");
		assertThat(users).isEmpty();
	}

	@Test
	public void findUserShouldReturnNullWhenNotGuestDomain() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream("twake/twakeConsole-users-response-one-user.json"));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mock(AbstractDomain.class);
		when(domain.isGuestDomain())
			.thenReturn(false);
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		User user = testee.findUser(domain, userProvider, "antoine@linshare.org");
		assertThat(user).isNull();
	}

	@Test
	public void autoCompleteUserShouldReturnEmptyListWhenNotGuestDomain() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream("twake/twakeConsole-users-response.json"));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mock(AbstractDomain.class);
		when(domain.isGuestDomain())
			.thenReturn(false);
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.autoCompleteUser(domain, userProvider, "");
		assertThat(users).isEmpty();
	}

	@Test
	public void autoCompleteUserByNamesShouldReturnEmptyListWhenNotGuestDomain() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream("twake/twakeConsole-users-response.json"));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mock(AbstractDomain.class);
		when(domain.isGuestDomain())
			.thenReturn(false);
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.autoCompleteUser(domain, userProvider, "", "");
		assertThat(users).isEmpty();
	}

	@Test
	public void isUserExistShouldReturnFalseWhenNotGuestDomain() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream("twake/twakeConsole-users-response-one-user.json"));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mock(AbstractDomain.class);
		when(domain.isGuestDomain())
			.thenReturn(false);
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		assertThat(testee.isUserExist(domain, userProvider, "antoine@linshare.org")).isFalse();
	}

	@Test
	public void searchForAuthShouldReturnNullWhenNotGuestDomain() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		TwakeUserProviderService testee = implementation(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream("twake/twakeConsole-users-response.json"));
		this.mockWebServer.enqueue(new MockResponse.Builder().body(responseBody).build());

		AbstractDomain domain = mock(AbstractDomain.class);
		when(domain.isGuestDomain())
			.thenReturn(false);
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		User user = testee.searchForAuth(domain, userProvider, "ric@lins");
		assertThat(user).isNull();
	}
}
