package org.linagora.linshare.webservice.external.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.server.embedded.ldap.LdapServerRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@ExtendWith(LdapServerRule.class)
@Transactional
@Sql({ "/import-tests-anonymousurl.sql" })
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml", "classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml", "classpath:springContext-repository.xml",
		"classpath:springContext-mongo.xml", "classpath:springContext-service.xml",
		"classpath:springContext-service-miscellaneous.xml", "classpath:springContext-rac.xml",
		"classpath:springContext-mongo-init.xml", "classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-business-service.xml", "classpath:springContext-webservice-admin.xml",
		"classpath:springContext-facade-ws-admin.xml", "classpath:springContext-facade-ws-user.xml",
		"classpath:springContext-webservice.xml", "classpath:springContext-upgrade-v2-0.xml",
		"classpath:springContext-facade-ws-async.xml", "classpath:springContext-task-executor.xml",
		"classpath:springContext-batches.xml", "classpath:springContext-test.xml",
		"classpath:springContext-webservice-anonymousurl.xml" })
public class AnonymousUrlRestServiceImplTest {

	@Autowired
	private AnonymousUrlRestServiceImpl testee;

	@Test
	public void getWithNoPasswordShouldReturnCookieWithDefaultValue() {
		HttpHeaders headers = mock(HttpHeaders.class);
		when(headers.getHeaderString(anyString())).thenReturn(null);
		Response response = testee.getAnonymousUrl("1", headers);
		assertEquals(200, response.getStatus());
		// assert the cookie is here
		assertEquals("_", response.getCookies().get("1").getValue());
	}

	@Test
	public void getWithPasswordInHeadersShouldReturnCookieWithPassword() {
		HttpHeaders headers = mock(HttpHeaders.class);
		when(headers.getHeaderString(anyString())).thenReturn("secret");
		Response response = testee.getAnonymousUrl("2", headers);
		assertEquals(200, response.getStatus());
		// assert the cookie is here
		assertEquals("secret", response.getCookies().get("2").getValue());
	}

	@Test
	public void getWithPasswordInCookieShouldReturnCookieWithPassword() {
		HttpHeaders headers = mock(HttpHeaders.class);
		when(headers.getHeaderString(anyString())).thenReturn(null);
		when(headers.getCookies()).thenReturn(new HashMap<>() {
			{
				put("2", new Cookie("2", "secret"));
			}
		});
		Response response = testee.getAnonymousUrl("2", headers);
		assertEquals(200, response.getStatus());
		// assert the cookie is here
		assertEquals("secret", response.getCookies().get("2").getValue());
	}

	@ParameterizedTest
	@ArgumentsSource(WrongPasswordArgumentsProvider.class)
	void getWithWrongPasswordShouldThrowBusinessException(String headerValue, Map<String, Cookie> cookies) {
		System.out.println("headerValue: " + headerValue);
		System.out.println("cookies: " + cookies);
		HttpHeaders headers = mock(HttpHeaders.class);
		when(headers.getHeaderString(anyString())).thenReturn(headerValue);
		when(headers.getCookies()).thenReturn(cookies);
		Exception exception = assertThrows(BusinessException.class, () -> testee.getAnonymousUrl("2", headers));
		assertEquals("You do not have the right to get this anonymous url : 2", exception.getMessage());
	}
}

class WrongPasswordArgumentsProvider implements ArgumentsProvider {

	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
		return Stream.of(
				Arguments.of("wrong", new HashMap<>()),
				Arguments.of(null, new HashMap<>()),
				Arguments.of(null, new HashMap<>() {
					{
						put("2", new Cookie("2", "wrong"));
					}
				}));
	}
}