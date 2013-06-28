/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.DomainPattern;
import org.linagora.linshare.core.domain.entities.LDAPConnection;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.LDAPQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


@ContextConfiguration(locations = { 
		"classpath:springContext-test.xml",
		"classpath:springContext-ldap.xml"
//		"classpath:springContext-startopends.xml"
		})
public class LDAPQueryServiceImplTest extends AbstractJUnit4SpringContextTests {
	
	protected Logger logger = LoggerFactory.getLogger(LDAPQueryServiceImplTest.class);

	
	@Autowired
	private LDAPQueryService ldapQueryService;
	
	private LDAPConnection ldapConn;
	
	private DomainPattern pattern;
	
	private Map<String, LdapAttribute> attributes;
	
	private String baseDn ;
	
	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		
		ldapConn = new LDAPConnection("testldap", "ldap://10.75.128.208:389", "anonymous");
//		ldapConn = new LDAPConnection("testldap", "ldap://linshare-obm.linshare.team.services.par.lng:389", "anonymous");
		attributes = new HashMap<String, LdapAttribute>();
		attributes.put(DomainPattern.USER_MAIL, new LdapAttribute(DomainPattern.USER_MAIL, "mail", true));
		attributes.put(DomainPattern.USER_FIRST_NAME, new LdapAttribute(DomainPattern.USER_FIRST_NAME, "sn", true));
//		attributes.put(DomainPattern.USER_FIRST_NAME, new LdapAttribute(DomainPattern.USER_FIRST_NAME, "givenName", true));
		attributes.put(DomainPattern.USER_LAST_NAME, new LdapAttribute(DomainPattern.USER_LAST_NAME, "cn", true));
//		attributes.put(DomainPattern.USER_LAST_NAME, new LdapAttribute(DomainPattern.USER_LAST_NAME, "sn", true));
		attributes.put(DomainPattern.USER_UID, new LdapAttribute(DomainPattern.USER_UID, "uid", false));
		
		String autocomplete1 = "ldap.search(domain, \"(&(objectClass=inetOrgPerson)(mail=*)(cn=*)(sn=*)" + "(|" + "(mail=\" + pattern + \")" + "(sn=\" + pattern + \")" + "(cn=\" + pattern + \")" + "))\")";
		
		String 	autocomplete2 = "ldap.search(domain, \"(&(objectClass=inetOrgPerson)(mail=*)(cn=*)(sn=*)" + "(|" + "(&(sn=\" + first_name + \")(cn=\" + last_name + \"))" + // "first_name last_name"
				"(&(sn=\" + last_name + \")(cn=\" + first_name + \"))" + // "last_name first_name"
				"))\")";
		
		this.pattern = new DomainPattern("testPattern", "testPattern", " ", " ", 
				"ldap.search(domain, \"(&(objectClass=inetOrgPerson)(|(mail=\" + login + \")(uid=\" + login + \")))\");", // auth
				// command
				"ldap.search(domain, \"(&(objectClass=inetOrgPerson)(mail=\" + mail + \")(sn=\" + first_name + \")(cn=\" + last_name + \"))\");", // search
				// command
				500, 2000, attributes, 
				autocomplete1, // auto complete command using first name, last name or mail attributes
				autocomplete2, // auto complete command using first name and last name attributes (association)
				20, 20, false);
		
		
//		pattern= new DomainPattern("testPattern", "testPattern", 
//				" ", 
//				" ", 
//				"ldap.search(domain, \"(&(objectClass=inetOrgPerson)(cn=*)(sn=*)(mail=\"+login+\"))\");", // auth command
//				"ldap.search(domain, \"(&(objectClass=inetOrgPerson)(mail=\"+mail+\")(cn=*)(sn=*))\");", // search command 
//				attributes,
//				"ldap.search(domain, \"(&(objectClass=obmUser)(|(mail=\"+pattern+\")(givenName=\"+pattern+\")(sn=\"+pattern+\"))(mail=*)(givenName=*)(sn=*))\");", // auto complete command
//				false);
//		baseDn = "ou=users,dc=int1.linshare.dev,dc=local";
		baseDn = "ou=People,o=insee,c=fr";
//		baseDn = "dc=int1.linshare.dev,dc=local";
//		"ldap.search(domain, \"(&(objectClass=obmUser)(mail=\"+mail+\")(givenName=\"+firstName+\")(sn=\"+lastName+\"))\");", // search command 
		
		logger.debug(LinShareTestConstants.END_SETUP);
	}
	
	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}
	
//	@Ignore
	@Test
	public void testAuth() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
//		User user = ldapQueryService.auth(ldapConn, baseDn, pattern, "bart.simpson@int1.linshare.dev", "password1");
		User user = ldapQueryService.auth(ldapConn, baseDn, pattern, "uafieyee@linagora.com", "secret");
		
		
		Assert.assertNotNull(user);
//		user = ldapQueryService.auth(ldapConn, baseDn, pattern, "user1", "bla");
//		Assert.assertNull(user);
//		user = ldapQueryService.auth(ldapConn, baseDn, pattern, "user1@linpki.org", "password1");
//		Assert.assertNotNull(user);
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testFred() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		String mail = "homer";
		String firstName= "homer";
		String lastName = "";
		List<User> searchUser = ldapQueryService.completeUser(ldapConn, baseDn, pattern, mail);
		searchUser = ldapQueryService.completeUser(ldapConn, baseDn, pattern, firstName, lastName);
		for (User user : searchUser) {
			logger.debug(user.getAccountReprentation());
//			logger.debug(user.getMail());
//			logger.debug(user.getFirstName());
//			logger.debug(user.getLastName());
//			logger.debug(user.getLdapUid());
		}
		logger.debug("searchUser size : " + searchUser.size());
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	
	@Ignore
	@Test
	public void testSearchUser() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<User> users = ldapQueryService.searchUser(ldapConn, baseDn, pattern, "er1", null, null);
		Assert.assertEquals(1, users.size());
		logger.debug(LinShareTestConstants.END_TEST);
	}

}
