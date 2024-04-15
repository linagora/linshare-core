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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.linagora.linshare.core.domain.constants.ServerType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AbstractTwakeUserProvider;
import org.linagora.linshare.core.domain.entities.TwakeConnection;
import org.linagora.linshare.core.domain.entities.TwakeUserProvider;
import org.linagora.linshare.core.exception.BusinessException;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class MockedTwakeUserProviderServiceImplTest extends AbstractTwakeUserProviderServiceImplEnv {

	private static class MockedTwakeUserProviderServiceImpl extends TwakeUserProviderServiceImpl {

		private final HttpUrl httpUrl;
		private final OkHttpClient mockedClient;

		public MockedTwakeUserProviderServiceImpl(HttpUrl httpUrl, OkHttpClient mockedClient) {
			this.httpUrl = httpUrl;
			this.mockedClient = mockedClient;
		}

		@Override
		protected OkHttpClient client() {
			return mockedClient;
		}

		@Override
		protected HttpUrl httpUrlFrom(AbstractTwakeUserProvider userProvider, Optional<String> extraPath) {
			return httpUrl;
		}
	}

	@Test
	public void findUserShouldNotBreakOnClientException() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		OkHttpClient client = mock(OkHttpClient.class);
		TwakeUserProviderServiceImpl testee = new MockedTwakeUserProviderServiceImpl(httpUrl, client);

		Call call = mock(Call.class);
		when(client.newCall(any()))
			.thenReturn(call);

		when(call.execute())
			.thenThrow(IOException.class);

		AbstractDomain domain = mock(AbstractDomain.class);
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		testee.findUser(domain, userProvider, "");
	}

	@Test
	public void searchUserShouldNotBreakOnClientException() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		OkHttpClient client = mock(OkHttpClient.class);
		TwakeUserProviderServiceImpl testee = new MockedTwakeUserProviderServiceImpl(httpUrl, client);

		Call call = mock(Call.class);
		when(client.newCall(any()))
			.thenReturn(call);

		when(call.execute())
			.thenThrow(IOException.class);

		AbstractDomain domain = mock(AbstractDomain.class);
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		testee.searchUser(domain, userProvider, "", "", "");
	}

	@Test
	public void autoCompleteUserShouldNotBreakOnClientException() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		OkHttpClient client = mock(OkHttpClient.class);
		TwakeUserProviderServiceImpl testee = new MockedTwakeUserProviderServiceImpl(httpUrl, client);

		Call call = mock(Call.class);
		when(client.newCall(any()))
			.thenReturn(call);

		when(call.execute())
			.thenThrow(IOException.class);

		AbstractDomain domain = mock(AbstractDomain.class);
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		testee.autoCompleteUser(domain, userProvider, "");
	}

	@Test
	public void autoCompleteUserByNamesShouldNotBreakOnClientException() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		OkHttpClient client = mock(OkHttpClient.class);
		TwakeUserProviderServiceImpl testee = new MockedTwakeUserProviderServiceImpl(httpUrl, client);

		Call call = mock(Call.class);
		when(client.newCall(any()))
			.thenReturn(call);

		when(call.execute())
			.thenThrow(IOException.class);

		AbstractDomain domain = mock(AbstractDomain.class);
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		testee.autoCompleteUser(domain, userProvider, "", "");
	}

	@Test
	public void isUserExistShouldNotBreakOnClientException() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		OkHttpClient client = mock(OkHttpClient.class);
		TwakeUserProviderServiceImpl testee = new MockedTwakeUserProviderServiceImpl(httpUrl, client);

		Call call = mock(Call.class);
		when(client.newCall(any()))
			.thenReturn(call);

		when(call.execute())
			.thenThrow(IOException.class);

		AbstractDomain domain = mock(AbstractDomain.class);
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		testee.isUserExist(domain, userProvider, "");
	}

	@Test
	public void authShouldAlwaysFail() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		OkHttpClient client = mock(OkHttpClient.class);
		TwakeUserProviderServiceImpl testee = new MockedTwakeUserProviderServiceImpl(httpUrl, client);

		Call call = mock(Call.class);
		when(client.newCall(any()))
			.thenReturn(call);

		when(call.execute())
			.thenThrow(IOException.class);

		AbstractDomain domain = mock(AbstractDomain.class);
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		assertThatThrownBy(() -> testee.auth(userProvider, "", ""))
			.isInstanceOf(BusinessException.class)
			.hasMessage("Not implemented");
	}

	@Test
	public void searchForAuthShouldNotBreakOnClientException() throws Exception {
		HttpUrl httpUrl = this.mockWebServer.url(TWAKE_CONSOLE_URL);
		OkHttpClient client = mock(OkHttpClient.class);
		TwakeUserProviderServiceImpl testee = new MockedTwakeUserProviderServiceImpl(httpUrl, client);

		Call call = mock(Call.class);
		when(client.newCall(any()))
			.thenReturn(call);

		when(call.execute())
			.thenThrow(IOException.class);

		AbstractDomain domain = mock(AbstractDomain.class);
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(TWAKE_CONSOLE_URL);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		testee.searchForAuth(domain, userProvider, "");
	}
}
