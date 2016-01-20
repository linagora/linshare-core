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
package org.linagora.linshare.repository.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.LdapUserProvider;
import org.linagora.linshare.core.domain.entities.UserLdapPattern;
import org.linagora.linshare.core.domain.entities.UserProvider;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DomainPatternRepository;
import org.linagora.linshare.core.repository.LdapConnectionRepository;
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
	private static String providerUrl= "ldap://localhost:33389";
	private static String securityAuth= "simple";
	
	// Repositories
	@Autowired
	private UserProviderRepository ldapUserProviderRepository;
	
	@Autowired
	private LdapConnectionRepository ldapConnectionRepository;
	
	@Autowired
	private DomainPatternRepository domainPatternRepository;
	
	//Objects
	private LdapConnection ldapconnexion;
	private UserLdapPattern pattern;
	
	
	@Before
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

	@After
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
		Assert.assertEquals(2, aa.getAttributes().size());

		
		aa.getAttributes().put("nom", new LdapAttribute("nom" , "sn", true));
		domainPatternRepository.update(aa);
		
		aa = domainPatternRepository.findByUuid(p.getUuid());
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
		Assert.assertNotNull(provider.getId());
		
		logger.debug("Current provider object: " + provider.toString());
		
		Assert.assertTrue(ldapUserProviderRepository.findAll() != null);
		Assert.assertTrue(ldapUserProviderRepository.findAll().size() == actualCount + 1);
		
		UserProvider newProvider = ldapUserProviderRepository.findByUuid(provider.getUuid());
		Assert.assertTrue(newProvider != null);
		
		LdapUserProvider ldapProvider = (LdapUserProvider) newProvider;
		
		Assert.assertTrue(ldapProvider.getBaseDn().equals(baseDn));
		Assert.assertTrue(ldapProvider.getPattern().getLabel().equals(identifierP));
		Assert.assertTrue(ldapProvider.getLdapConnection().getLabel().equals(identifier));
		
		ldapUserProviderRepository.delete(newProvider);
		logger.debug("End testCreateLdapUserProvider");
	}
}
