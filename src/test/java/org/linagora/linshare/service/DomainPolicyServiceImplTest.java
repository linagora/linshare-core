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
package org.linagora.linshare.service;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AllowAllDomain;
import org.linagora.linshare.core.domain.entities.AllowDomain;
import org.linagora.linshare.core.domain.entities.DenyAllDomain;
import org.linagora.linshare.core.domain.entities.DomainAccessPolicy;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.domain.entities.GuestDomain;
import org.linagora.linshare.core.domain.entities.RootDomain;
import org.linagora.linshare.core.domain.entities.SubDomain;
import org.linagora.linshare.core.domain.entities.TopDomain;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DomainAccessRuleRepository;
import org.linagora.linshare.core.service.DomainPolicyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-test.xml" })
public class DomainPolicyServiceImplTest {
	
	private static Logger logger = LoggerFactory.getLogger(DomainPolicyServiceImplTest.class);
	
	@Autowired
	private DomainPolicyService domainPolicyService;
	@Autowired
	AbstractDomainRepository abstractDomainRepository;
	@Autowired
	DomainAccessRuleRepository domainAccessRuleRepository;
	
	private AbstractDomain rootDomain;
	
	private TopDomain t1 ;
	private TopDomain t2 ;
	private SubDomain s1 ;
	private SubDomain s4 ;
	
	// Default domains + Tests Domains
	private int CST_TOTAL_DOMAIN=14;
	
	
	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		
		DomainPolicy domainePolicy1 = new DomainPolicy("TestAccessPolicy0", new DomainAccessPolicy());
		domainePolicy1.getDomainAccessPolicy().addRule(new AllowAllDomain());
		domainPolicyService.create(domainePolicy1);
		
		
		
		rootDomain = new RootDomain("root domain");
		rootDomain.setPolicy(domainePolicy1);
		abstractDomainRepository.create(rootDomain);
		
		t1 = new TopDomain("top1", (RootDomain)rootDomain);
		rootDomain.addSubdomain(t1);
		t1.setPolicy(domainePolicy1);
		t1.setParentDomain(rootDomain);
		abstractDomainRepository.create(t1);
		
		s1 = new SubDomain("id_sub1",t1);
		t1.addSubdomain(s1);
		s1.setPolicy(domainePolicy1);
		s1.setParentDomain(t1);
		abstractDomainRepository.create(s1);
		
		SubDomain s2 = new SubDomain("id_sub2",t1);
		t1.addSubdomain(s2);
		s2.setPolicy(domainePolicy1);
		s2.setParentDomain(t1);
		abstractDomainRepository.create(s2);
		
		GuestDomain s3 = new GuestDomain("id_sub3");
		s3.setParentDomain(t1);
		t1.addSubdomain(s3);
		s3.setPolicy(domainePolicy1);
		s3.setParentDomain(t1);
		abstractDomainRepository.create(s3);
		
		
		t2 = new TopDomain("top2", (RootDomain)rootDomain);
		rootDomain.addSubdomain(t2);
		t2.setParentDomain(rootDomain);
		abstractDomainRepository.create(t2);
		
		DomainPolicy domainePolicy2 = new DomainPolicy("TestAccessPolicy1", new DomainAccessPolicy());
		domainePolicy2.getDomainAccessPolicy().addRule(new AllowDomain(t2));
		domainePolicy2.getDomainAccessPolicy().addRule(new DenyAllDomain());
		domainPolicyService.create(domainePolicy2);
		
		t2.setPolicy(domainePolicy2);
		abstractDomainRepository.update(t2);
		
		s4 = new SubDomain("id_sub4",t2);
		t2.addSubdomain(s4);
		s4.setPolicy(domainePolicy2);
		s4.setParentDomain(t2);
		abstractDomainRepository.create(s4);
		
		SubDomain s5 = new SubDomain("id_sub5",t2);
		t2.addSubdomain(s5);
		s5.setPolicy(domainePolicy2);
		s5.setParentDomain(t2);
		abstractDomainRepository.create(s5);
		
		SubDomain s6 = new SubDomain("id_sub6",t2);
		t2.addSubdomain(s6);
		s6.setPolicy(domainePolicy2);
		s6.setParentDomain(t2);
		abstractDomainRepository.create(s6);
		
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}
	
	
	private void printAuthorizedDomain(List<AbstractDomain> authorizedSubDomain, AbstractDomain src) {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		logger.debug("Begin print : " + src);
		for (AbstractDomain abstractDomain : authorizedSubDomain) {
			logger.debug("domain : " + abstractDomain);
		}
		logger.debug("End print : " + src);
		logger.debug(LinShareTestConstants.END_TEST);

	}

	@Test
	public void testAuthorizedSubDomain() {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		List<AbstractDomain> authorizedSubDomain = domainPolicyService.getAuthorizedSubDomain(rootDomain);
		printAuthorizedDomain(authorizedSubDomain,rootDomain);
		
		authorizedSubDomain = domainPolicyService.getAuthorizedSubDomain(t1);
		printAuthorizedDomain(authorizedSubDomain,t1);
		// 3 sub domains.
		Assertions.assertEquals(3, authorizedSubDomain.size());
		
		authorizedSubDomain = domainPolicyService.getAuthorizedSubDomain(s1);
		printAuthorizedDomain(authorizedSubDomain,s1);
		// no sub domains
		Assertions.assertEquals(0, authorizedSubDomain.size());

		authorizedSubDomain = domainPolicyService.getAuthorizedSubDomain(t2);
		printAuthorizedDomain(authorizedSubDomain,t2);
		// sub domain communication not allowed : 0
		Assertions.assertEquals(0, authorizedSubDomain.size());
		
		authorizedSubDomain = domainPolicyService.getAuthorizedSubDomain(s4);
		printAuthorizedDomain(authorizedSubDomain,s4);
		// no sub domains
		Assertions.assertEquals(0, authorizedSubDomain.size());
		logger.debug(LinShareTestConstants.END_TEST);

	}
	
	@Test
	public void testIsAuthorizedToCommunicateWithItSelf() {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		Assertions.assertTrue(domainPolicyService.isAuthorizedToCommunicateWithItSelf(t1));
		Assertions.assertTrue(domainPolicyService.isAuthorizedToCommunicateWithItSelf(s1));
		Assertions.assertTrue(domainPolicyService.isAuthorizedToCommunicateWithItSelf(t2));
		Assertions.assertFalse(domainPolicyService.isAuthorizedToCommunicateWithItSelf(s4));
		logger.debug(LinShareTestConstants.END_TEST);

	}
	
	@Test
	public void testIsAuthorizedToCommunicateWithItsParent() {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		Assertions.assertTrue(domainPolicyService.isAuthorizedToCommunicateWithItsParent(t1));
		Assertions.assertTrue(domainPolicyService.isAuthorizedToCommunicateWithItsParent(s1));
		Assertions.assertFalse(domainPolicyService.isAuthorizedToCommunicateWithItsParent(t2));
		Assertions.assertTrue(domainPolicyService.isAuthorizedToCommunicateWithItsParent(s4));
		logger.debug(LinShareTestConstants.END_TEST);

	}
	
	
	@Test
	public void testAuthorizedSiblingDomain1() {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		List<AbstractDomain> authorizedSubDomain = domainPolicyService.getAuthorizedSibblingDomain(rootDomain);
		printAuthorizedDomain(authorizedSubDomain,rootDomain);
		
		authorizedSubDomain = domainPolicyService.getAuthorizedSibblingDomain(t1);
		printAuthorizedDomain(authorizedSubDomain,t1);
		// 2 siblings.
		Assertions.assertEquals(2, authorizedSubDomain.size());
		
		authorizedSubDomain = domainPolicyService.getAuthorizedSibblingDomain(s1);
		printAuthorizedDomain(authorizedSubDomain,s1);
		// 3 siblings
		Assertions.assertEquals(3, authorizedSubDomain.size());
		logger.debug(LinShareTestConstants.END_TEST);


	}
	
	@Test
	public void testAuthorizedSiblingDomain2() {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		List<AbstractDomain> authorizedSubDomain;

		authorizedSubDomain = domainPolicyService.getAuthorizedSibblingDomain(t2);
		printAuthorizedDomain(authorizedSubDomain,t2);
		// 2 siblings, but communication with itself only : 1
		Assertions.assertEquals(1, authorizedSubDomain.size());
		
		authorizedSubDomain = domainPolicyService.getAuthorizedSibblingDomain(s4);
		printAuthorizedDomain(authorizedSubDomain,s4);
		// 3 siblings, but communication with only top domain t2: 0
		Assertions.assertEquals(0, authorizedSubDomain.size());
		logger.debug(LinShareTestConstants.END_TEST);

	}

	@Disabled
	@Test
	public void testGetAllAuthorizedDomain () {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		List<AbstractDomain> authorizedSubDomain;
		// Default domains + Tests Domains
		authorizedSubDomain = domainPolicyService.getAllAuthorizedDomain(rootDomain);
		Assertions.assertEquals(CST_TOTAL_DOMAIN, authorizedSubDomain.size());
		
		authorizedSubDomain = domainPolicyService.getAllAuthorizedDomain(t1);
		Assertions.assertEquals(CST_TOTAL_DOMAIN, authorizedSubDomain.size());
		

		authorizedSubDomain = domainPolicyService.getAllAuthorizedDomain(s1);
		Assertions.assertEquals(CST_TOTAL_DOMAIN, authorizedSubDomain.size());
		
		authorizedSubDomain = domainPolicyService.getAllAuthorizedDomain(t2);
		Assertions.assertEquals(1, authorizedSubDomain.size());
		
		authorizedSubDomain = domainPolicyService.getAllAuthorizedDomain(s4);
		Assertions.assertEquals(1, authorizedSubDomain.size());
		logger.debug(LinShareTestConstants.END_TEST);

	}
}
