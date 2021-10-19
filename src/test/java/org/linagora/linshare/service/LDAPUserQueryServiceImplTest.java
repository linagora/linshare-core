/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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
package org.linagora.linshare.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserLdapPattern;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.LDAPUserQueryService;
import org.linagora.linshare.server.embedded.ldap.LdapServerRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ExtendWith(LdapServerRule.class)
@Transactional
@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml" })
public class LDAPUserQueryServiceImplTest {

	protected Logger logger = LoggerFactory.getLogger(LDAPUserQueryServiceImplTest.class);

	@Autowired
	private LDAPUserQueryService ldapQueryService;

	private LdapConnection ldapConn;

	private UserLdapPattern domainPattern;

	private Map<String, LdapAttribute> attributes;

	private static final String BASE_DN = "ou=People,dc=linshare,dc=org";
	private static final String TOP_DOMAIN2_BASE_DN = "ou=People2,dc=linshare,dc=org";
	private static final String USER1_MAIL = LinShareTestConstants.JOHN_ACCOUNT;
	private static final String AMY_TOP_DOMAIN2_MAIL = LinShareTestConstants.AMY_WOLSH_ACCOUNT;
	private static final String PASSWORD_USER1 = "password1";
	private static final String PASSWORD_AMY_TOP_DOMAIN2 = "secret";
	private static final String STR_PATTERN = "linpki";

	private void logUser(User user) {
		logger.debug(user.getAccountRepresentation());
		// logger.debug(user.getMail());
		// logger.debug(user.getFirstName());
		// logger.debug(user.getLastName());
		// logger.debug(user.getLdapUid());
	}

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		
		ldapConn = new LdapConnection("testldap", "ldap://localhost:33389", "anonymous");
		// auto complete command using first name, last name or mail attributes
		String auto_complete_command_on_all_attributes = "ldap.search(domain, \"(&(objectClass=*)(mail=*)(givenName=*)(sn=*)(|(mail=\" + pattern + \")(sn=\" + pattern + \")(givenName=\" + pattern + \")))\");";
		
		// auto complete command using first name and last name attributes (association)
		String auto_complete_command_on_first_and_last_name = "ldap.search(domain, \"(&(objectClass=*)(mail=*)(givenName=*)(sn=*)(|(&(sn=\" + first_name + \")(givenName=\" + last_name + \"))(&(sn=\" + last_name + \")(givenName=\" + first_name + \"))))\");";
		
		String search_command = "ldap.search(domain, \"(&(objectClass=*)(mail=\" + mail + \")(sn=\" + first_name + \")(givenName=\" + last_name + \"))\");";

		String auth_command = "ldap.search(domain, \"(&(objectClass=*)(givenName=*)(sn=*)(|(mail=\" + login + \")(uid=\" + login + \")))\");";
		
		initDefault(auto_complete_command_on_all_attributes, auto_complete_command_on_first_and_last_name, search_command, auth_command, 0, 0);

		logger.debug(LinShareTestConstants.END_SETUP);
	}

	private void initDefault(String auto_complete_command_on_all_attributes,
			String auto_complete_command_on_first_and_last_name,
			String search_command,
			String auth_command, int searchPagination, int completePagination) {
		
		attributes = new HashMap<String, LdapAttribute>();
		attributes.put(UserLdapPattern.USER_MAIL, new LdapAttribute(UserLdapPattern.USER_MAIL, "mail", true));
		attributes.put(UserLdapPattern.USER_FIRST_NAME, new LdapAttribute(UserLdapPattern.USER_FIRST_NAME, "givenName", true));
		attributes.put(UserLdapPattern.USER_LAST_NAME, new LdapAttribute(UserLdapPattern.USER_LAST_NAME, "sn", true));
		attributes.put(UserLdapPattern.USER_UID, new LdapAttribute(UserLdapPattern.USER_UID, "uid", false));

		this.domainPattern = new UserLdapPattern("testPattern", "testPattern", 
				auth_command, 
				search_command, 
				searchPagination,
				2000,
				attributes,
				auto_complete_command_on_all_attributes,
				auto_complete_command_on_first_and_last_name,
				completePagination, 
				5, 
				false);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testAuth() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date date_before = new Date();
		User user = ldapQueryService.auth(ldapConn, BASE_DN, domainPattern, USER1_MAIL, PASSWORD_USER1);
		Date date_after = new Date();
		Assertions.assertNotNull(user);
		logUser(user);
		logger.info("fin test : " + String.valueOf(date_after.getTime() - date_before.getTime()));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testAuthAmyWolshTopDomain2() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		User amy = ldapQueryService.auth(ldapConn, TOP_DOMAIN2_BASE_DN, domainPattern,
				AMY_TOP_DOMAIN2_MAIL, PASSWORD_AMY_TOP_DOMAIN2);
		Assertions.assertNotNull(amy);
		logUser(amy);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testAuthWrongPassword() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date date_before = new Date();
		User user = null;
		try {
			user = ldapQueryService.auth(ldapConn, BASE_DN, domainPattern, USER1_MAIL, "eeee");
		} catch (BadCredentialsException e) {

		}
		Date date_after = new Date();
		Assertions.assertNull(user);
		logger.info("fin test : " + String.valueOf(date_after.getTime() - date_before.getTime()));
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testAuthUserNotFound() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date date_before = new Date();
		try {
			ldapQueryService.auth(ldapConn, BASE_DN, domainPattern, USER1_MAIL + "undefined", PASSWORD_USER1);
			Assertions.assertTrue(false);
		} catch (NameNotFoundException e) {
			// spring exception when user is not found
			Assertions.assertTrue(true);
		}
		Date date_after = new Date();
		logger.info("fin test : " + String.valueOf(date_after.getTime() - date_before.getTime()));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testUserExist() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date date_before = new Date();
		Boolean exist = ldapQueryService.isUserExist(ldapConn, BASE_DN, domainPattern, USER1_MAIL);
		Date date_after = new Date();
		logger.info("fin test : " + String.valueOf(date_after.getTime() - date_before.getTime()));
		Assertions.assertEquals(exist, true);

		exist = ldapQueryService.isUserExist(ldapConn, BASE_DN, domainPattern, USER1_MAIL + "undefined");
		Assertions.assertEquals(exist, false);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testSearchUser() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date date_before = new Date();
		List<User> user = ldapQueryService.searchUser(ldapConn, BASE_DN, domainPattern, USER1_MAIL, null, null);
		Date date_after = new Date();
		logger.info("fin test : " + String.valueOf(date_after.getTime() - date_before.getTime()) + " milliseconds.");
		logger.info("Result count : " + String.valueOf(user.size()));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCompleteUser() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date date_before = new Date();
		List<User> users = ldapQueryService.completeUser(ldapConn, BASE_DN, domainPattern, STR_PATTERN);
		Date date_after = new Date();
		logger.info("fin test : " + String.valueOf(date_after.getTime() - date_before.getTime()) + " milliseconds.");
		logger.info("Result count : " + String.valueOf(users.size()));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testGetUser() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date date_before = new Date();
		User user = ldapQueryService.getUser(ldapConn, BASE_DN, domainPattern, USER1_MAIL);
		Date date_after = new Date();
		logger.info("fin test : " + String.valueOf(date_after.getTime() - date_before.getTime()) + " milliseconds.");
		Assertions.assertNotNull(user);
		logUser(user);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testGetUser2() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date date_before = new Date();
		domainPattern.getAttributes().put(UserLdapPattern.USER_FIRST_NAME, new LdapAttribute(UserLdapPattern.USER_FIRST_NAME, "plopName", true));
		User user = ldapQueryService.getUser(ldapConn, BASE_DN, domainPattern, USER1_MAIL);
		Date date_after = new Date();
		logger.info("fin test : " + String.valueOf(date_after.getTime() - date_before.getTime()) + " milliseconds.");
		Assertions.assertNull(user);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testGetUserFromTopDomain2() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		User amy = ldapQueryService.getUser(ldapConn, TOP_DOMAIN2_BASE_DN, domainPattern,
				AMY_TOP_DOMAIN2_MAIL);
		Assertions.assertNotNull(amy);
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
