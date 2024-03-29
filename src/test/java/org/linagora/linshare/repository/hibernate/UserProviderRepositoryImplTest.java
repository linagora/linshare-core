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
package org.linagora.linshare.repository.hibernate;

import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.LdapUserProvider;
import org.linagora.linshare.core.domain.entities.UserLdapPattern;
import org.linagora.linshare.core.domain.entities.UserProvider;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DomainPatternRepository;
import org.linagora.linshare.core.repository.UserProviderRepository;
import org.linagora.linshare.core.repository.hibernate.LdapConnectionRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations={
		"classpath:springContext-test.xml", 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class UserProviderRepositoryImplTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private static String baseDn = "dc=nodomain,dc=com";
	private static String identifier= "ID_LDAP_DE_TEST3";
	private static String identifierP= "ID_PARAM_DE_TEST3";
	private static String providerUrl= "ldap://localhost:33389";
	private static String securityAuth= "simple";
	
	// Repositories
	@Autowired
	private UserProviderRepository ldapUserProviderRepository;
	
	@Autowired
	private LdapConnectionRepositoryImpl ldapConnectionRepository;
	
	@Autowired
	private DomainPatternRepository domainPatternRepository;
	
	//Objects
	private LdapConnection ldapconnexion;
	private UserLdapPattern pattern;
	
	
	@BeforeEach
	public void setUp() throws Exception {
		logger.debug("Begin setUp");
		
		ldapconnexion  = new LdapConnection(identifier, providerUrl, securityAuth);
		ldapConnectionRepository.create(ldapconnexion);
		logger.debug("Current ldapconnexion object: " + ldapconnexion.toString());
		
		
		pattern = new UserLdapPattern(identifierP, "blabla", "getUserCommand", "getAllDomainUsersCommand", "authCommand", "searchUserCommand", null);
		domainPatternRepository.create(pattern);
		logger.debug("Current pattern object: " + pattern.toString());
		
		logger.debug("End setUp");
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");
		ldapConnectionRepository.delete(ldapconnexion);
		domainPatternRepository.delete(pattern);
		logger.debug("End tearDown");
	}
	
	// TODO : toto?
	@Test
	public void test1() throws BusinessException {
		Map<String, LdapAttribute> a = new HashMap<String, LdapAttribute>();
		a.put("ldapUid", new LdapAttribute("ldapUid" , "uid", false));
		a.put("toto", new LdapAttribute("ldapUid" , "uid", false));
		UserLdapPattern p = new UserLdapPattern("identifierP2", "blabla", "getUserCommand", "getAllDomainUsersCommand", "authCommand", "searchUserCommand", a);
		
		domainPatternRepository.create(p);
		
		UserLdapPattern aa = domainPatternRepository.findByUuid(p.getUuid());
		logger.debug("aa size : " + aa.getAttributes().size());
		Assertions.assertEquals(2, aa.getAttributes().size());

		
		aa.getAttributes().put("nom", new LdapAttribute("nom" , "sn", true));
		domainPatternRepository.update(aa);
		
		aa = domainPatternRepository.findByUuid(p.getUuid());
		logger.debug("aa size : " + aa.getAttributes().size());
		Assertions.assertEquals(3, aa.getAttributes().size());

		LdapAttribute ldapAttribute = aa.getAttributes().get("ldapUid");
		
		logger.debug("ldapUid field: " + ldapAttribute.getField());
		logger.debug("ldapUid attribute : " + ldapAttribute.getAttribute());
		Assertions.assertEquals("ldapUid", ldapAttribute.getField());
		Assertions.assertEquals("uid", ldapAttribute.getAttribute());
	}
	
	@Test
	public void testCreateLdapUserProvider() throws BusinessException{
		logger.debug("Begin testCreateLdapUserProvider");
	
		int actualCount = ldapUserProviderRepository.findAll().size();
		
		
		LdapUserProvider provider = new LdapUserProvider(null, baseDn,ldapconnexion,pattern);
		ldapUserProviderRepository.create(provider);
		Assertions.assertNotNull(provider.getId());
		
		logger.debug("Current provider object: " + provider.toString());
		
		Assertions.assertTrue(ldapUserProviderRepository.findAll() != null);
		Assertions.assertTrue(ldapUserProviderRepository.findAll().size() == actualCount + 1);
		
		UserProvider newProvider = ldapUserProviderRepository.findByUuid(provider.getUuid());
		Assertions.assertTrue(newProvider != null);
		
		LdapUserProvider ldapProvider = (LdapUserProvider) newProvider;
		
		Assertions.assertTrue(ldapProvider.getBaseDn().equals(baseDn));
		Assertions.assertTrue(ldapProvider.getPattern().getLabel().equals(identifierP));
		Assertions.assertTrue(ldapProvider.getLdapConnection().getLabel().equals(identifier));
		
		ldapUserProviderRepository.delete(newProvider);
		logger.debug("End testCreateLdapUserProvider");
	}
}
