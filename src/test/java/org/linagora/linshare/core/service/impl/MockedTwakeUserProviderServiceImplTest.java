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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
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
import okhttp3.mockwebserver.MockWebServer;

public class MockedTwakeUserProviderServiceImplTest {

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
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
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
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		testee.findUser(domain, userProvider, "");
	}

	@Test
	public void searchUserShouldNotBreakOnClientException() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
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
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		testee.searchUser(domain, userProvider, "", "", "");
	}

	@Test
	public void autoCompleteUserShouldNotBreakOnClientException() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
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
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		testee.autoCompleteUser(domain, userProvider, "");
	}

	@Test
	public void autoCompleteUserByNamesShouldNotBreakOnClientException() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
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
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		testee.autoCompleteUser(domain, userProvider, "", "");
	}

	@Test
	public void isUserExistShouldNotBreakOnClientException() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
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
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		testee.isUserExist(domain, userProvider, "");
	}

	@Test
	public void authShouldAlwaysFail() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
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
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		assertThatThrownBy(() -> testee.auth(userProvider, "", ""))
			.isInstanceOf(BusinessException.class)
			.hasMessage("Not implemented");
	}

	@Test
	public void searchForAuthShouldNotBreakOnClientException() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
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
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		testee.searchForAuth(domain, userProvider, "");
	}
}
