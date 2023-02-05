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
package org.linagora.linshare.service;

import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.LdapWorkSpaceFilter;
import org.linagora.linshare.core.domain.entities.LdapWorkSpaceProvider;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.WorkSpaceProviderService;
import org.linagora.linshare.core.service.LdapWorkSpaceFilterService;
import org.linagora.linshare.core.service.impl.LdapConnectionServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-test.xml" })
public class WorkSpaceProviderServiceImplTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private WorkSpaceProviderService workSpaceProviderService;

	@Autowired
	private LdapWorkSpaceFilterService workSpaceLdapPatternService;

	@Autowired
	private LdapConnectionServiceImpl ldapConnectionService;

	@Autowired
	private AccountService accountService;

	private LdapWorkSpaceFilter workSpaceFilter;

	private LdapConnection ldapconnexion;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		ldapconnexion  = new LdapConnection("label", "ldap://10.75.113.53:389", "simple");
		ldapconnexion = ldapConnectionService.create(ldapconnexion);
		LdapAttribute attribute = new LdapAttribute("field", "attribute", false);
		Map<String, LdapAttribute> attributeList = new HashMap<>();
		attributeList.put("first", attribute);
		workSpaceFilter = new LdapWorkSpaceFilter("lable", "description", "searchAllGroupsQuery",
				"searchGroupQuery", "workSpacePrefix", false);
		Account actor = accountService.findByLsUuid("root@localhost.localdomain");
		workSpaceFilter = workSpaceLdapPatternService.create(actor, workSpaceFilter);
		Assertions.assertNotNull(workSpaceFilter);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testCreateWorkSpaceProvider() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		LdapWorkSpaceProvider workSpaceProvider = new LdapWorkSpaceProvider(workSpaceFilter, "dc=nodomain,dc=com", ldapconnexion,
				false);
		workSpaceProvider = workSpaceProviderService.create(workSpaceProvider);
		Assertions.assertNotNull(workSpaceProvider);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateDeleteWorkSpaceProvider() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			LdapWorkSpaceProvider workSpaceProvider = new LdapWorkSpaceProvider(workSpaceFilter, "dc=nodomain,dc=com", ldapconnexion,
					false);
			workSpaceProvider = workSpaceProviderService.create(workSpaceProvider);
			Assertions.assertNotNull(workSpaceProvider);
			workSpaceProviderService.delete(workSpaceProvider);
			workSpaceProviderService.find(workSpaceProvider.getUuid());
		});
		Assertions.assertEquals(BusinessErrorCode.WORKSPACE_PROVIDER_NOT_FOUND, exception.getErrorCode());
		logger.debug(LinShareTestConstants.END_TEST);
	}

}
