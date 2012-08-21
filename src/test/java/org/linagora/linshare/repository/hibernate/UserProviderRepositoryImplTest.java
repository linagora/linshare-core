/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linshare.repository.hibernate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.linagora.linshare.core.domain.entities.DomainPattern;
import org.linagora.linshare.core.domain.entities.LDAPConnection;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.LdapUserProvider;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DomainPatternRepository;
import org.linagora.linshare.core.repository.LDAPConnectionRepository;
import org.linagora.linshare.core.repository.UserProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations={"classpath:springContext-test.xml", 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class UserProviderRepositoryImplTest extends AbstractTransactionalJUnit4SpringContextTests {

	private static String baseDn = "dc=nodomain,dc=com";
	private static String identifier= "ID_LDAP_DE_TEST3";
	private static String identifierP= "ID_PARAM_DE_TEST3";
	private static String providerUrl= "ldap://10.75.113.53:389";
	private static String securityAuth= "simple";
	
	// Repositories
	@Autowired
	private UserProviderRepository ldapUserProviderRepository;
	
	@Autowired
	private LDAPConnectionRepository ldapConnectionRepository;
	
	@Autowired
	private DomainPatternRepository domainPatternRepository;
	
	//Objects
	private LDAPConnection ldapconnexion;
	private DomainPattern pattern;
	
	
	@Before
	public void setUp() throws Exception {
		logger.debug("Begin setUp");
		
		ldapconnexion  = new LDAPConnection(identifier, providerUrl, securityAuth);
		ldapConnectionRepository.create(ldapconnexion);
		logger.debug("Current ldapconnexion object: " + ldapconnexion.toString());
		
		
		pattern = new DomainPattern(identifierP, "blabla", "getUserCommand", "getAllDomainUsersCommand", "authCommand", "searchUserCommand", null);
		domainPatternRepository.create(pattern);
		logger.debug("Current pattern object: " + pattern.toString());
		
		logger.debug("End setUp");
	}

	@After
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");
		ldapConnectionRepository.delete(ldapconnexion);
		domainPatternRepository.delete(pattern);
		logger.debug("End tearDown");
	}
	
	@Test
	public void toto() throws BusinessException {
		Map<String, LdapAttribute> a = new HashMap<String, LdapAttribute>();
		a.put("ldapUid", new LdapAttribute("ldapUid" , "uid"));
		a.put("toto", new LdapAttribute("ldapUid" , "uid"));
		DomainPattern p = new DomainPattern("identifierP2", "blabla", "getUserCommand", "getAllDomainUsersCommand", "authCommand", "searchUserCommand", a);
		
		domainPatternRepository.create(p);
		
		DomainPattern aa = domainPatternRepository.findById("identifierP2");
		logger.debug("aa size : " + aa.getAttributes().size());
		Assert.assertEquals(2, aa.getAttributes().size());

		
		aa.getAttributes().put("nom", new LdapAttribute("nom" , "sn"));
		domainPatternRepository.update(aa);
		
		aa = domainPatternRepository.findById("identifierP2");
		logger.debug("aa size : " + aa.getAttributes().size());
		Assert.assertEquals(3, aa.getAttributes().size());

		LdapAttribute ldapAttribute = aa.getAttributes().get("ldapUid");
		
		logger.debug("ldapUid field: " + ldapAttribute.getField());
		logger.debug("ldapUid attribute : " + ldapAttribute.getAttribute());
		Assert.assertEquals("ldapUid", ldapAttribute.getField());
		Assert.assertEquals("uid", ldapAttribute.getAttribute());
	}
	
	@Ignore
	@Test
	public void testCreateLdapUserProvider() throws BusinessException{
		logger.debug("Begin testCreateLdapUserProvider");
	
		int actualCount = ldapUserProviderRepository.findAll().size();
		
		
		LdapUserProvider provider = new LdapUserProvider(baseDn,ldapconnexion,pattern);
		ldapUserProviderRepository.create(provider);
		Assert.assertNotNull(provider.getPersistenceId());
		
		logger.debug("Current provider object: " + provider.toString());
		
		Assert.assertTrue(ldapUserProviderRepository.findAll() != null);
		Assert.assertTrue(ldapUserProviderRepository.findAll().size() == actualCount + 1);
		
		LdapUserProvider newProvider = ldapUserProviderRepository.findById(provider.getPersistenceId());
		Assert.assertTrue(newProvider != null);
		
		Assert.assertTrue(newProvider instanceof LdapUserProvider);
		LdapUserProvider ldapProvider = (LdapUserProvider)newProvider; 
		
		Assert.assertTrue(ldapProvider.getBaseDn().equals(baseDn));
		Assert.assertTrue(ldapProvider.getPattern().getIdentifier().equals(identifierP));
		Assert.assertTrue(ldapProvider.getLdapconnexion().getIdentifier().equals(identifier));
		
		ldapUserProviderRepository.delete(newProvider);
		logger.debug("End testCreateLdapUserProvider");
	}
}
