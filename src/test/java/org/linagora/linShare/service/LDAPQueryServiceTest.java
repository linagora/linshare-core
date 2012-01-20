package org.linagora.linShare.service;

import java.io.IOException;
import java.util.List;

import javax.naming.NamingException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.linagora.linShare.core.domain.entities.DomainPattern;
import org.linagora.linShare.core.domain.entities.LDAPConnection;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.service.LDAPQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


@ContextConfiguration(locations = { 
		"classpath:springContext-test.xml",
		"classpath:springContext-startopends.xml"
		})
public class LDAPQueryServiceTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	private LDAPQueryService ldapQueryService;
	
	private LDAPConnection ldapConn;
	
	private DomainPattern pattern;
	
	private String baseDn ;
	
	@Before
	public void setUp() throws Exception {
		ldapConn = new LDAPConnection("testldap", "ldap://localhost:33389", "anonymous");
		pattern= new DomainPattern("testPattern", "testPattern", 
				"ldap.entry(\"uid=\" + userId + \",ou=People,\" + domain, \"objectClass=*\");", 
				"ldap.list(\"ou=People,\" + domain, \"(&(objectClass=*)(mail=*)(givenName=*)(sn=*))\");", 
				"ldap.list(\"ou=People,\" + domain, \"(&(objectClass=*)(givenName=*)(sn=*)(|(mail=\"+login+\")(uid=\"+login+\")))\");", 
				"ldap.list(\"ou=People,\" + domain, \"(&(objectClass=*)(mail=\"+mail+\")(givenName=\"+firstName+\")(sn=\"+lastName+\"))\");", 
				"mail", "givenName", "sn");
		baseDn = "dc=linpki,dc=org";
	}
	@After
	public void tearDown() throws Exception {
	}

	@Ignore
	@Test
	public void testGetUser() throws BusinessException {
		User user = ldapQueryService.getUser(ldapConn, baseDn, pattern, "user1");
		Assert.assertEquals("user1@linpki.org", user.getMail());
	}

	@Ignore
	@Test
	public void testGetAllDomainUsers() throws BusinessException {
		List<User> users = ldapQueryService.getAllDomainUsers(ldapConn, baseDn, pattern);
		Assert.assertEquals(2, users.size());
	}
	
	@Ignore
	@Test
	public void testAuth() throws BusinessException, NamingException, IOException {
		User user = ldapQueryService.auth(ldapConn, baseDn, pattern, "user1", "password1");
		Assert.assertNotNull(user);
		user = ldapQueryService.auth(ldapConn, baseDn, pattern, "user1", "bla");
		Assert.assertNull(user);
		user = ldapQueryService.auth(ldapConn, baseDn, pattern, "user1@linpki.org", "password1");
		Assert.assertNotNull(user);
	}

	@Ignore
	@Test
	public void testSearchUser() throws BusinessException {
		List<User> users = ldapQueryService.searchUser(ldapConn, baseDn, pattern, "er1", null, null);
		Assert.assertEquals(1, users.size());
	}

}
