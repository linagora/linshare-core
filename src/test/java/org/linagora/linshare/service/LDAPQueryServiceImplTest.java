/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserLdapPattern;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.LDAPQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = { "classpath:springContext-test.xml",
		"classpath:springContext-ldap.xml", 
		"classpath:springContext-start-embedded-ldap.xml"
})
public class LDAPQueryServiceImplTest extends AbstractJUnit4SpringContextTests {

	protected Logger logger = LoggerFactory.getLogger(LDAPQueryServiceImplTest.class);

	private String userMail1;

	private String userPassword1;
	
	private String strPattern;

	@Autowired
	private LDAPQueryService ldapQueryService;

	private LdapConnection ldapConn;

	private UserLdapPattern domainPattern;

	private Map<String, LdapAttribute> attributes;

	private String baseDn;

	private void logUser(User user) {
		logger.debug(user.getAccountRepresentation());
		// logger.debug(user.getMail());
		// logger.debug(user.getFirstName());
		// logger.debug(user.getLastName());
		// logger.debug(user.getLdapUid());
	}

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		
		ldapConn = new LdapConnection("testldap", "ldap://localhost:33389", "anonymous");
		baseDn = "ou=People,dc=linshare,dc=org";
		baseDn = "dc=linshare,dc=org";
		
		// auto complete command using first name, last name or mail attributes
		String auto_complete_command_on_all_attributes = "ldap.search(domain, \"(&(objectClass=*)(mail=*)(givenName=*)(sn=*)(|(mail=\" + pattern + \")(sn=\" + pattern + \")(givenName=\" + pattern + \")))\");";
		
		// auto complete command using first name and last name attributes (association)
		String auto_complete_command_on_first_and_last_name = "ldap.search(domain, \"(&(objectClass=*)(mail=*)(givenName=*)(sn=*)(|(&(sn=\" + first_name + \")(givenName=\" + last_name + \"))(&(sn=\" + last_name + \")(givenName=\" + first_name + \"))))\");";
		
		String search_command = "ldap.search(domain, \"(&(objectClass=*)(mail=\" + mail + \")(sn=\" + first_name + \")(givenName=\" + last_name + \"))\");";

		String auth_command = "ldap.search(domain, \"(&(objectClass=*)(givenName=*)(sn=*)(|(mail=\" + login + \")(uid=\" + login + \")))\");";
		
		initDefault(auto_complete_command_on_all_attributes, auto_complete_command_on_first_and_last_name, search_command, auth_command, 0, 0);

		this.userMail1 = "user1@linshare.org";
		this.userPassword1 = "password1";
		this.strPattern = "linpki";
		
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

//	@Before
	public void setUp2() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		
		baseDn = "ou=users,dc=int5.linshare.dev,dc=local";
		baseDn = "dc=int5.linshare.dev,dc=local";
		ldapConn = new LdapConnection("testldap", "	ldap://linshare-obm2.linagora.dc1:389", "anonymous");

		// auto complete command using first name, last name or mail attributes
		String auto_complete_command_on_all_attributes = "ldap.search(domain, \"(&(objectClass=obmUser)(mail=*)(givenName=*)(sn=*)(|(mail=\" + pattern + \")(sn=\" + pattern + \")(givenName=\" + pattern + \")))\");";
		
		// auto complete command using first name and last name attributes (association)
		String auto_complete_command_on_first_and_last_name = "ldap.search(domain, \"(&(objectClass=obmUser)(mail=*)(givenName=*)(sn=*)(|(&(sn=\" + first_name + \")(givenName=\" + last_name + \"))(&(sn=\" + last_name + \")(givenName=\" + first_name + \"))))\");";
		
		String search_command = "ldap.search(domain, \"(&(objectClass=obmUser)(mail=\" + mail + \")(sn=\" + first_name + \")(givenName=\" + last_name + \"))\");";

		String auth_command = "ldap.search(domain, \"(&(objectClass=obmUser)(givenName=*)(sn=*)(|(mail=\" + login + \")(uid=\" + login + \")))\");";
		
		initDefault(auto_complete_command_on_all_attributes, auto_complete_command_on_first_and_last_name, search_command, auth_command, 500, 5);

		this.userMail1 = "aaliyah.alvarez@int5.linshare.dev";
		this.userPassword1 = "secret";
		this.strPattern = "abdel";
		
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testAuth() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date date_before = new Date();
		User user = ldapQueryService.auth(ldapConn, baseDn, domainPattern, userMail1, userPassword1);
		Date date_after = new Date();
		Assert.assertNotNull(user);
		logUser(user);
		logger.info("fin test : " + String.valueOf(date_after.getTime() - date_before.getTime()));
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testAuthWrongPassword() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date date_before = new Date();
		User user = null;
		try {
			user = ldapQueryService.auth(ldapConn, baseDn, domainPattern, userMail1, "eeee");
		} catch (BadCredentialsException e) {

		}
		Date date_after = new Date();
		Assert.assertNull(user);
		logger.info("fin test : " + String.valueOf(date_after.getTime() - date_before.getTime()));
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testAuthUserNotFound() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date date_before = new Date();
		try {
			ldapQueryService.auth(ldapConn, baseDn, domainPattern, userMail1 + "undefined", userPassword1);
			Assert.assertTrue(false);
		} catch (NameNotFoundException e) {
			// spring exception when user is not found
			Assert.assertTrue(true);
		}
		Date date_after = new Date();
		logger.info("fin test : " + String.valueOf(date_after.getTime() - date_before.getTime()));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testUserExist() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date date_before = new Date();
		Boolean exist = ldapQueryService.isUserExist(ldapConn, baseDn, domainPattern, userMail1);
		Date date_after = new Date();
		logger.info("fin test : " + String.valueOf(date_after.getTime() - date_before.getTime()));
		Assert.assertEquals(exist, true);

		exist = ldapQueryService.isUserExist(ldapConn, baseDn, domainPattern, userMail1 + "undefined");
		Assert.assertEquals(exist, false);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testSearchUser() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date date_before = new Date();
		List<User> user = ldapQueryService.searchUser(ldapConn, baseDn, domainPattern, userMail1, null, null);
		Date date_after = new Date();
		logger.info("fin test : " + String.valueOf(date_after.getTime() - date_before.getTime()) + " milliseconds.");
		logger.info("Result count : " + String.valueOf(user.size()));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCompleteUser() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date date_before = new Date();
		List<User> users = ldapQueryService.completeUser(ldapConn, baseDn, domainPattern, strPattern);
		Date date_after = new Date();
		logger.info("fin test : " + String.valueOf(date_after.getTime() - date_before.getTime()) + " milliseconds.");
		logger.info("Result count : " + String.valueOf(users.size()));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testGetUser() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date date_before = new Date();
		User user = ldapQueryService.getUser(ldapConn, baseDn, domainPattern, userMail1);
		Date date_after = new Date();
		logger.info("fin test : " + String.valueOf(date_after.getTime() - date_before.getTime()) + " milliseconds.");
		Assert.assertNotNull(user);
		logUser(user);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testGetUser2() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date date_before = new Date();
		domainPattern.getAttributes().put(UserLdapPattern.USER_FIRST_NAME, new LdapAttribute(UserLdapPattern.USER_FIRST_NAME, "plopName", true));
		User user = ldapQueryService.getUser(ldapConn, baseDn, domainPattern, userMail1);
		Date date_after = new Date();
		logger.info("fin test : " + String.valueOf(date_after.getTime() - date_before.getTime()) + " milliseconds.");
		Assert.assertNull(user);
		logger.debug(LinShareTestConstants.END_TEST);
	}

}
