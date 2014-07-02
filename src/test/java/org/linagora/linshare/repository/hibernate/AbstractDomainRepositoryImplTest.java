/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainAccessPolicy;
import org.linagora.linshare.core.domain.entities.DomainPattern;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.domain.entities.LDAPConnection;
import org.linagora.linshare.core.domain.entities.LdapUserProvider;
import org.linagora.linshare.core.domain.entities.RootDomain;
import org.linagora.linshare.core.domain.entities.SubDomain;
import org.linagora.linshare.core.domain.entities.TopDomain;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DomainAccessPolicyRepository;
import org.linagora.linshare.core.repository.DomainPatternRepository;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.linagora.linshare.core.repository.LDAPConnectionRepository;
import org.linagora.linshare.core.repository.MessagesRepository;
import org.linagora.linshare.core.repository.UserProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations={"classpath:springContext-test.xml", 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class AbstractDomainRepositoryImplTest extends AbstractJUnit4SpringContextTests {

	private static String rootDomainName = "Domain0";
	private static String topDomainName = "Domain0.1";
	private static String subDomainName = "Domain0.1.1";
	private static String domainePolicyName0 = "TestAccessPolicy0";

	//private static String baseDn = "dc=nodomain,dc=com";
	private static String identifier= "ID_LDAP_DE_TEST";
	private static String identifierP= "ID_PARAM_DE_TEST";
	private static String providerUrl= "ldap://10.75.113.53:389";
	private static String securityAuth= "simple";


	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	@Autowired
	private UserProviderRepository userProviderRepository;

	@Autowired
	private DomainPolicyRepository domainPolicyRepository;

	@Autowired
	private DomainAccessPolicyRepository domainAccessRepository;

	@Autowired
	private MessagesRepository messagesRepository;

	@Autowired
	private LDAPConnectionRepository ldapConnectionRepository;

	@Autowired
	private DomainPatternRepository domainPatternRepository;

	private DomainPolicy defaultPolicy;



	@Before
	public void setUp() throws Exception {
		logger.debug("Begin setUp");
		DomainAccessPolicy domainAccessPolicy = new DomainAccessPolicy();
		domainAccessRepository.create(domainAccessPolicy);
		defaultPolicy = new DomainPolicy(domainePolicyName0, domainAccessPolicy );
		domainPolicyRepository.create(defaultPolicy);
		logger.debug("Current DomainPolicy : " + defaultPolicy.toString());
		logger.debug("End setUp");
	}

	@After
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");
		domainPolicyRepository.delete(defaultPolicy);
		logger.debug("End tearDown");
	}




	private AbstractDomain createATestRootDomain() throws BusinessException {
		AbstractDomain currentDomain= new RootDomain(rootDomainName,"My root domain");

		// Set default messages (welcome messages, mails, ...)
		currentDomain.setMessagesConfiguration(messagesRepository.loadDefault());

		currentDomain.setPolicy(defaultPolicy);

		abstractDomainRepository.create(currentDomain);
		logger.debug("Current AbstractDomain object: " + currentDomain.toString());
		return currentDomain;
	}

	private AbstractDomain createATestTopDomain(AbstractDomain rootDomain) throws BusinessException {
		AbstractDomain currentTopDomain = new TopDomain(topDomainName,"My top domain",(RootDomain)rootDomain);

		currentTopDomain.setPolicy(defaultPolicy);

		abstractDomainRepository.create(currentTopDomain);
		logger.debug("Current TopDomain object: " + currentTopDomain.toString());
		return currentTopDomain;
	}

	@Test
	public void testRootDomainCreation() throws BusinessException{
		logger.debug("Begin testRootDomainCreation");

		AbstractDomain currentDomain = createATestRootDomain();

		Assert.assertNotNull(abstractDomainRepository.findAll());
		AbstractDomain entityDomain = abstractDomainRepository.findById(rootDomainName);
		Assert.assertNotNull(entityDomain);
		Assert.assertNull(entityDomain.getParentDomain());
		Assert.assertTrue(entityDomain instanceof RootDomain);
		Assert.assertFalse(entityDomain instanceof TopDomain);
		Assert.assertFalse(entityDomain instanceof SubDomain);
		Assert.assertTrue(entityDomain.isEnable());
		Assert.assertFalse(entityDomain.isTemplate());
		Assert.assertTrue(entityDomain.getMessagesConfiguration().getId() >= 1);

		abstractDomainRepository.delete(currentDomain);
		logger.debug("End testRootDomainCreation");
	}

	@Test
	public void testTopDomainCreation() throws BusinessException{
		logger.debug("Begin testTopDomainCreation");
		AbstractDomain rootDomain = createATestRootDomain();
		AbstractDomain currentTopDomain = createATestTopDomain(rootDomain);

		rootDomain.addSubdomain(currentTopDomain);
		abstractDomainRepository.update(rootDomain);
		logger.debug("my parent is  : " + currentTopDomain.getParentDomain().toString());

		Assert.assertNotNull(abstractDomainRepository.findAll());
		logger.debug("abstractDomainRepository.findAll().size():"+abstractDomainRepository.findAll().size());
		Assert.assertNotNull(abstractDomainRepository.findById(rootDomainName));
		AbstractDomain entityRootDomain = abstractDomainRepository.findById(rootDomainName);

		List<AbstractDomain> subDomainList = new ArrayList<AbstractDomain>();
		subDomainList.addAll(entityRootDomain.getSubdomain());
		logger.debug(entityRootDomain.getIdentifier() + " : my son is : " + subDomainList.get(0).getIdentifier());
		Assert.assertEquals(topDomainName, subDomainList.get(0).getIdentifier());

		abstractDomainRepository.delete(rootDomain);
		logger.debug("End testTopDomainCreation");
	}

	@Test
	public void testSubDomainCreation() throws BusinessException{
		logger.debug(" testSubDomainCreation ");

		AbstractDomain rootDomain = createATestRootDomain();
		AbstractDomain currentTopDomain = createATestTopDomain(rootDomain);

		rootDomain.addSubdomain(currentTopDomain);
		abstractDomainRepository.update(rootDomain);

		LDAPConnection ldapconnexion  = new LDAPConnection(identifier, providerUrl, securityAuth);
		ldapConnectionRepository.create(ldapconnexion);
		logger.debug("Current ldapconnexion object: " + ldapconnexion.toString());


		DomainPattern domainPattern = new DomainPattern(identifierP, "blabla", "getUserCommand", "getAllDomainUsersCommand", "authCommand", "searchUserCommand", null);
		domainPatternRepository.create(domainPattern);
		logger.debug("Current pattern object: " + domainPattern.toString());

		LdapUserProvider provider = new LdapUserProvider("",ldapconnexion,domainPattern);
		userProviderRepository.create(provider);

		SubDomain subDomain= new SubDomain(subDomainName,"My root domain",(TopDomain)currentTopDomain);
		currentTopDomain.addSubdomain(subDomain);
		subDomain.setUserProvider(provider);
		subDomain.setPolicy(defaultPolicy);
		abstractDomainRepository.create(subDomain);
		logger.debug("Current SubDomain object: " + subDomain.toString());


		AbstractDomain entityTopDomain = abstractDomainRepository.findById(topDomainName);
		logger.debug("entityTopDomain.getSubdomain().size():"+entityTopDomain.getSubdomain().size());
		Assert.assertEquals(1, entityTopDomain.getSubdomain().size());

		Assert.assertEquals(rootDomainName,subDomain.getParentDomain().getParentDomain().getIdentifier());
		Assert.assertNull(subDomain.getParentDomain().getParentDomain().getParentDomain());

		subDomain.setUserProvider(null);
		abstractDomainRepository.update(subDomain);
		userProviderRepository.delete(provider);
		abstractDomainRepository.delete(rootDomain);

	}

	@Test
	public void testFindAllTopDomain() throws BusinessException{
		List<AbstractDomain> list = abstractDomainRepository.findAllTopDomain();
		// one by default + 2 topdomain for tests.
		Assert.assertEquals(3, list.size());
	}

	@Test
	public void testFindAllSubDomain() throws BusinessException{
		List<AbstractDomain> list = abstractDomainRepository.findAllSubDomain();
		Assert.assertNotNull(list.size());
	}
}
