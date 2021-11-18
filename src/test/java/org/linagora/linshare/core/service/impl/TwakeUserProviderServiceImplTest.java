/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 *
 * Copyright (C) 2021 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Optional;

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

public class TwakeUserProviderServiceImplTest {

	private static class MyTwakeUserProviderServiceImpl extends TwakeUserProviderServiceImpl {

		private final HttpUrl httpUrl;

		public MyTwakeUserProviderServiceImpl(HttpUrl httpUrl) {
			this.httpUrl = httpUrl;
		}

		@Override
		protected HttpUrl httpUrlFrom(TwakeUserProvider userProvider, Optional<String> extraPath) {
			return httpUrl;
		}
	}

	@Test
	public void searchUserShouldReturnTheUsersList() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderServiceImpl testee = new MyTwakeUserProviderServiceImpl(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream("twake/twakeConsole-users-response.json"));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mock(AbstractDomain.class);
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.searchUser(domain, userProvider, "mail", "firstName", "lastName");
		assertThat(users).hasSize(4);
	}

	@Test
	public void searchUserShouldReturnEmptyListWhenNoneVerified() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderServiceImpl testee = new MyTwakeUserProviderServiceImpl(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream("twake/twakeConsole-users-response-non-verified.json"));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mock(AbstractDomain.class);
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.searchUser(domain, userProvider, "mail", "firstName", "lastName");
		assertThat(users).isEmpty();
	}

	@Test
	public void searchUserShouldReturnEmptyListWhenAllBlocked() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderServiceImpl testee = new MyTwakeUserProviderServiceImpl(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream("twake/twakeConsole-users-response-all-blocked.json"));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mock(AbstractDomain.class);
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.searchUser(domain, userProvider, "mail", "firstName", "lastName");
		assertThat(users).isEmpty();
	}

	@Test
	public void searchUserShouldReturnEmptyListWhenAllGuests() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderServiceImpl testee = new MyTwakeUserProviderServiceImpl(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream("twake/twakeConsole-users-response-all-guests.json"));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mock(AbstractDomain.class);
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.searchUser(domain, userProvider, "mail", "firstName", "lastName");
		assertThat(users).isEmpty();
	}

	@Test
	public void searchUserShouldParseResponse() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderServiceImpl testee = new MyTwakeUserProviderServiceImpl(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream("twake/twakeConsole-users-response-one-user.json"));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mock(AbstractDomain.class);
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		List<User> users = testee.searchUser(domain, userProvider, "mail", "firstName", "lastName");
		assertThat(users).hasSize(1);

		User user = users.get(0);
		assertThat(user.getFirstName()).isEqualTo("antoine");
		assertThat(user.getLastName()).isEqualTo("toine");
		assertThat(user.getMail()).isEqualTo("antoine@linshare.org");
	}

	@Test
	public void findUserShouldReturnTheUserWhenExists() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderServiceImpl testee = new MyTwakeUserProviderServiceImpl(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream("twake/twakeConsole-users-response-one-user.json"));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mock(AbstractDomain.class);
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
	public void findUserShouldReturnTheNullWhenDoesNotExist() throws Exception {
		MockWebServer server = new MockWebServer();
		String url = "/twakeconsole.dev";
		HttpUrl httpUrl = server.url(url);
		TwakeUserProviderServiceImpl testee = new MyTwakeUserProviderServiceImpl(httpUrl);

		String responseBody = IOUtils.toString(ClassLoader.getSystemResourceAsStream("twake/twakeConsole-users-response-one-user.json"));
		server.enqueue(new MockResponse().setBody(responseBody));

		AbstractDomain domain = mock(AbstractDomain.class);
		TwakeConnection twakeConnection = new TwakeConnection();
		twakeConnection.setServerType(ServerType.TWAKE);
		twakeConnection.setProviderUrl(url);
		twakeConnection.setClientId("clientId");
		twakeConnection.setClientSecret("clientSecret");
		TwakeUserProvider userProvider = new TwakeUserProvider(domain, twakeConnection, "twakeCompanyId");
		User user = testee.findUser(domain, userProvider, "wrong@bad.org");
		assertThat(user).isNull();
	}
}
