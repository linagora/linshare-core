/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2020-2022 LINAGORA
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