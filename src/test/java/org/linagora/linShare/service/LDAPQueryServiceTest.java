package org.linagora.linShare.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.naming.NamingException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linShare.core.domain.constants.TimeUnit;
import org.linagora.linShare.core.domain.entities.Domain;
import org.linagora.linShare.core.domain.entities.DomainPattern;
import org.linagora.linShare.core.domain.entities.LDAPConnection;
import org.linagora.linShare.core.domain.entities.MailSubject;
import org.linagora.linShare.core.domain.entities.MailTemplate;
import org.linagora.linShare.core.domain.entities.Parameter;
import org.linagora.linShare.core.domain.entities.ShareExpiryRule;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.entities.WelcomeText;
import org.linagora.linShare.core.domain.vo.DomainPatternVo;
import org.linagora.linShare.core.domain.vo.LDAPConnectionVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.DomainRepository;
import org.linagora.linShare.core.service.DomainService;
import org.linagora.linShare.core.service.LDAPQueryService;
import org.linagora.linShare.core.service.ParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-startopends.xml",
		"classpath:springContext-jackRabbit.xml",
		"classpath:springContext-test.xml"
		})
public class LDAPQueryServiceTest extends AbstractJUnit4SpringContextTests {
	
	private static final String DOMAIN_IDENTIFIER = "testDomain";

	@Autowired
	private DomainService domainService;
	
	@Autowired
	private DomainRepository domainRepository;
	
	@Autowired
	private ParameterService parameterService;
	
	@Autowired
	private LDAPQueryService ldapQueryService;
	
	private static boolean initialized = false;
	
	@Before
	public void setUp() throws Exception {
		if (!initialized) {
			Parameter param = new Parameter("testParam", new Long(100L), new Long(100L), new Long(1000L), new Long(0L), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, 
					Boolean.FALSE, new Integer(100), TimeUnit.DAY, "", TimeUnit.DAY, new Integer(100), TimeUnit.DAY, new Integer(100), 
					new ArrayList<ShareExpiryRule>(), Boolean.FALSE, new HashSet<WelcomeText>(), 
					new HashSet<MailTemplate>(), new HashSet<MailSubject>(), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
			LDAPConnectionVo ldapConnVo = new LDAPConnectionVo("testldap", "ldap://localhost:33389", "anonymous");
			DomainPatternVo patternVo = new DomainPatternVo("testPattern", "testPattern", 
					"ldap.entry(\"uid=\" + userId + \",ou=People,\" + domain, \"objectClass=*\");", 
					"ldap.list(\"ou=People,\" + domain, \"(&(objectClass=*)(mail=*)(givenName=*)(sn=*))\");", 
					"", 
					"ldap.list(\"ou=People,\" + domain, \"(&(objectClass=*)(givenName=*)(sn=*)(|(mail=\"+login+\")(uid=\"+login+\")))\");", 
					"ldap.list(\"ou=People,\" + domain, \"(&(objectClass=*)(mail=\"+mail+\")(givenName=\"+firstName+\")(sn=\"+lastName+\"))\");", 
					"mail givenName sn");
			LDAPConnection ldapConn = domainService.createLDAPConnection(ldapConnVo);
			DomainPattern pattern = domainService.createDomainPattern(patternVo);
			param = parameterService.saveOrUpdate(param);
			
			Domain domain = new Domain(DOMAIN_IDENTIFIER, "dc=linpki,dc=org", pattern, ldapConn, param);
			
			domainRepository.create(domain);
			initialized = true;
		}
	}

	@Test
	public void testGetUser() throws BusinessException {
		Domain domain = domainService.retrieveDomain(DOMAIN_IDENTIFIER);
		User user = ldapQueryService.getUser("user1", domain, null);
		Assert.assertEquals("user1@linpki.org", user.getMail());
	}

	@Test
	public void testGetAllDomainUsers() throws BusinessException {
		Domain domain = domainService.retrieveDomain(DOMAIN_IDENTIFIER);
		List<User> users = ldapQueryService.getAllDomainUsers(domain, null);
		Assert.assertEquals(2, users.size());
	}
	
	@Test
	public void testAuth() throws BusinessException, NamingException, IOException {
		Domain domain = domainService.retrieveDomain(DOMAIN_IDENTIFIER);
		User user = ldapQueryService.auth("user1", "password1", domain);
		Assert.assertNotNull(user);
		user = ldapQueryService.auth("user1", "bla", domain);
		Assert.assertNull(user);
		user = ldapQueryService.auth("user1@linpki.org", "password1", domain);
		Assert.assertNotNull(user);
	}

	@Test
	public void testSearchUser() throws BusinessException {
		Domain domain = domainService.retrieveDomain(DOMAIN_IDENTIFIER);
		List<User> users = ldapQueryService.searchUser("er1", null, null, domain, null);
		Assert.assertEquals(1, users.size());
	}

}
