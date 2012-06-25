package org.linagora.linshare.service;

import java.io.IOException;
import java.util.List;

import javax.naming.NamingException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.DomainPattern;
import org.linagora.linshare.core.domain.entities.LDAPConnection;
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
		"classpath:springContext-startopends.xml"
		})
public class LDAPQueryServiceImplTest extends AbstractJUnit4SpringContextTests {
	
	protected Logger logger = LoggerFactory.getLogger(LDAPQueryServiceImplTest.class);

	
	@Autowired
	private LDAPQueryService ldapQueryService;
	
	private LDAPConnection ldapConn;
	
	private DomainPattern pattern;
	
	private String baseDn ;
	
	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		ldapConn = new LDAPConnection("testldap", "ldap://localhost:33389", "anonymous");
		pattern= new DomainPattern("testPattern", "testPattern", 
				"ldap.entry(\"uid=\" + userId + \",ou=People,\" + domain, \"objectClass=*\");", 
				"ldap.list(\"ou=People,\" + domain, \"(&(objectClass=*)(mail=*)(givenName=*)(sn=*))\");", 
				"ldap.list(\"ou=People,\" + domain, \"(&(objectClass=*)(givenName=*)(sn=*)(|(mail=\"+login+\")(uid=\"+login+\")))\");", 
				"ldap.list(\"ou=People,\" + domain, \"(&(objectClass=*)(mail=\"+mail+\")(givenName=\"+firstName+\")(sn=\"+lastName+\"))\");", 
				"mail", "givenName", "sn", "givenName.sn");
		baseDn = "dc=linpki,dc=org";
		logger.debug(LinShareTestConstants.END_SETUP);
	}
	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Ignore
	@Test
	public void testGetUser() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		User user = ldapQueryService.getUser(ldapConn, baseDn, pattern, "user1");
		Assert.assertEquals("user1@linpki.org", user.getMail());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Ignore
	@Test
	public void testGetAllDomainUsers() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<User> users = ldapQueryService.getAllDomainUsers(ldapConn, baseDn, pattern);
		Assert.assertEquals(2, users.size());
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Ignore
	@Test
	public void testAuth() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		User user = ldapQueryService.auth(ldapConn, baseDn, pattern, "user1", "password1");
		Assert.assertNotNull(user);
		user = ldapQueryService.auth(ldapConn, baseDn, pattern, "user1", "bla");
		Assert.assertNull(user);
		user = ldapQueryService.auth(ldapConn, baseDn, pattern, "user1@linpki.org", "password1");
		Assert.assertNotNull(user);
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
