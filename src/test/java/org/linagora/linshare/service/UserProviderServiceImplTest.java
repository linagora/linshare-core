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
import java.util.List;
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
import org.linagora.linshare.core.domain.entities.UserLdapPattern;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.RemoteServerService;
import org.linagora.linshare.core.service.UserProviderService;
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
public class UserProviderServiceImplTest {

	private static final Logger logger = LoggerFactory.getLogger(UserProviderServiceImplTest.class);

	@SuppressWarnings("unused")
	private static String baseDn = "dc=nodomain,dc=com";
	private static String identifier= "ID_LDAP_DE_TEST2";
	private static String identifierP= "ID_PARAM_DE_TEST2";
	private static String providerUrl= "ldap://10.75.113.53:389";
	private static String securityAuth= "simple";

	@Autowired
	private RemoteServerService ldapConnectionService;

	@Autowired
	private UserProviderService userProviderService;
	
	@Autowired
	private AccountService accountService;

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
	public void testCreateLDAPConnection() {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		
		LdapConnection ldapconnexion  = new LdapConnection(identifier, providerUrl, securityAuth);
		try {
			ldapConnectionService.create(ldapconnexion);
		} catch (BusinessException e) {
			e.printStackTrace();
			Assertions.fail("Can't create connection.");
		}
		logger.debug("Current ldapconnexion object: " + ldapconnexion.toString());
		
		logger.debug(LinShareTestConstants.END_TEST);

	}
	
	
	@Test
	public void testCreateDomainPattern() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		LdapAttribute attribute = new LdapAttribute("field", "attribute", false);
		Map<String, LdapAttribute> attributeList = new HashMap<>();
			attributeList.put("first", attribute);
		UserLdapPattern domainPattern = new UserLdapPattern(identifierP, "blabla", "getUserCommand", "getAllDomainUsersCommand", "authCommand", "searchUserCommand", attributeList);
		domainPattern.setAutoCompleteCommandOnAllAttributes("auto complete command 1");
		domainPattern.setAutoCompleteCommandOnFirstAndLastName("auto complete command 2");
		try {
			Account actor = accountService.findByLsUuid(LinShareTestConstants.ROOT_ACCOUNT);
			userProviderService.createDomainPattern(actor, domainPattern);
		} catch (BusinessException e) {
			e.printStackTrace();
			Assertions.fail("Can't create domain pattern.");
		}
		logger.debug("Current pattern object: " + domainPattern.toString());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateDomainPatternSpecialChar() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		LdapAttribute attribute = new LdapAttribute("field", "attribute", false);
		Map<String, LdapAttribute> attributeList = new HashMap<>();
		attributeList.put("first", attribute);
		UserLdapPattern domainPattern = new UserLdapPattern("EP_TEST_v233<script>alert(document.cookie)</script>",
				"EP_TEST_v233<script>alert(document.cookie)</script>", "getUserCommand", "getAllDomainUsersCommand",
				"authCommand", "searchUserCommand", attributeList);
		domainPattern.setAutoCompleteCommandOnAllAttributes("auto complete command 1");
		domainPattern.setAutoCompleteCommandOnFirstAndLastName("auto complete command 2");
		Account actor = accountService.findByLsUuid(LinShareTestConstants.ROOT_ACCOUNT);
		userProviderService.createDomainPattern(actor, domainPattern);
		Assertions.assertNotNull(domainPattern);
		Assertions.assertEquals(domainPattern.getLabel(), "EP_TEST_v233");
		Assertions.assertEquals(domainPattern.getDescription(), "EP_TEST_v233");
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateDeleteLDAPConnection() {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		LdapConnection ldapconnexion  = new LdapConnection(identifier +"2", providerUrl, securityAuth);
		try {
			ldapConnectionService.create(ldapconnexion);
		} catch (BusinessException e) {
			e.printStackTrace();
			Assertions.fail("Can't create connection.");
		}
		logger.debug("Current ldapconnexion object: " + ldapconnexion.toString());
		
		
		try {
			ldapConnectionService.delete(ldapconnexion.getUuid());
		} catch (BusinessException e) {
			e.printStackTrace();
			Assertions.fail("Can't delete connection.");
		}
		logger.debug(LinShareTestConstants.END_TEST);

	}
	
	@Test
	public void testCreateDeleteDomainPattern() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		LdapAttribute attribute = new LdapAttribute("field", "attribute", false);
		Map<String, LdapAttribute> attributeList = new HashMap<>();
			attributeList.put("first", attribute);
		UserLdapPattern domainPattern = new UserLdapPattern(identifierP +"2", "blabla", "getUserCommand", "getAllDomainUsersCommand", "authCommand", "searchUserCommand", attributeList);
		domainPattern.setAutoCompleteCommandOnAllAttributes("auto complete command 1");
		domainPattern.setAutoCompleteCommandOnFirstAndLastName("auto complete command 2");
		Account actor = accountService.findByLsUuid(LinShareTestConstants.ROOT_ACCOUNT);
		try {
			domainPattern = userProviderService.createDomainPattern(actor, domainPattern);
		} catch (BusinessException e) {
			e.printStackTrace();
			Assertions.fail("Can't create pattern.");
		}
		logger.debug("Current pattern object: " + domainPattern.toString());

		try {
			userProviderService.deletePattern(actor, domainPattern.getUuid());
		} catch (BusinessException e) {
			logger.error(e.toString());
			e.printStackTrace();
			Assertions.fail("Can't delete pattern.");
		}
		logger.debug(LinShareTestConstants.END_TEST);

	}

	@Test
	public void testUpdateDomainPattern() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		UserLdapPattern domainPattern = null;
		
		try {
			List<UserLdapPattern> listD = userProviderService.findAllUserDomainPattern();
			domainPattern = listD.get(0);
		} catch (BusinessException e) {
			e.printStackTrace();
			Assertions.fail("Can't retrieve pattern.");
		}
		Account actor = accountService.findByLsUuid(LinShareTestConstants.ROOT_ACCOUNT);
		Map<String, LdapAttribute> attributes = domainPattern.getAttributes();
		attributes.get(UserLdapPattern.USER_FIRST_NAME).setAttribute("foo");
		try {
			userProviderService.updateDomainPattern(actor, domainPattern);
		} catch (BusinessException e) {
			e.printStackTrace();
			Assertions.fail("Can't update pattern.");
		}

		logger.debug(LinShareTestConstants.END_TEST);
	}
}
