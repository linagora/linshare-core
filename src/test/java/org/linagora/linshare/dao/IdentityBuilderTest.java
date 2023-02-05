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
package org.linagora.linshare.dao;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.dao.utils.IdentityBuilder;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = { "classpath:springContext-test.xml" })
public class IdentityBuilderTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void test1() {
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		IdentityBuilder ib = IdentityBuilder.New()
				.identity("domain:user");
		Assertions.assertEquals("domain:user", ib.build());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void test2() {
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		IdentityBuilder ib = IdentityBuilder.New()
			.tenantName("tenant")
			.userName("user");
		Assertions.assertEquals("tenant:user", ib.build());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void test3() {
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		IdentityBuilder ib = IdentityBuilder.New()
				.userDomainName("domain")
				.userName("user");
		Assertions.assertEquals("domain:user", ib.build());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void test4() {
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		IdentityBuilder ib = IdentityBuilder.New()
				.userName("user");
		Assertions.assertEquals("user", ib.build());
		logger.debug(LinShareTestConstants.END_TEST);
	}
	@Test
	public void test4b() {
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		IdentityBuilder ib = IdentityBuilder.New()
			.userDomainName("")
			.userName("user");
		Assertions.assertEquals("user", ib.build());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void test5() {
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		IdentityBuilder ib = IdentityBuilder.New()
				.tenantName("tenant")
				.userDomainName("domain")
				.userName("user");
		Assertions.assertEquals("domain:user", ib.build());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void test6() {
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		IdentityBuilder ib = IdentityBuilder.New()
			.identity("domain1:user1")
			.tenantName("tenant")
			.userDomainName("domain")
			.userName("user");
		Assertions.assertEquals("domain1:user1", ib.build());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void test7() {
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		IdentityBuilder ib = IdentityBuilder.New()
			.identity(null)
			.tenantName("tenant")
			.userDomainName("domain")
			.userName("user");
		Assertions.assertEquals("domain:user", ib.build());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void test8() {
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		IdentityBuilder ib = IdentityBuilder.New()
				.identity("")
				.tenantName("")
				.userDomainName("domain")
				.userName("user");
		Assertions.assertEquals("domain:user", ib.build());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void test9() {
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		IdentityBuilder ib = IdentityBuilder.New()
				.identity("")
				.tenantName("")
				.userDomainName("domain")
				.userName("");
		Assertions.assertThrows(NoSuchElementException.class, () -> {ib.build();});
		logger.debug(ib.toString());
		logger.debug(LinShareTestConstants.END_TEST);
	}

}