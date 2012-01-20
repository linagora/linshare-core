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
package org.linagora.linShare.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linShare.core.domain.constants.LinShareConstants;
import org.linagora.linShare.core.domain.entities.AbstractDomain;
import org.linagora.linShare.core.domain.entities.DomainPattern;
import org.linagora.linShare.core.domain.entities.DomainPolicy;
import org.linagora.linShare.core.domain.entities.LDAPConnection;
import org.linagora.linShare.core.domain.entities.TopDomain;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.DomainPolicyRepository;
import org.linagora.linShare.core.service.AbstractDomainService;
import org.linagora.linShare.core.service.UserProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = { 
		"classpath:springContext-test.xml",
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-service-test.xml"		
		})
public class AbstractDomainServiceImplTest extends AbstractJUnit4SpringContextTests{
	
	public static String topDomaineName = "TEST_ADST_Domain-0-1";
	
	private static String baseDn = "dc=nodomain,dc=com";
	private static String identifier= "ID_ADST_LDAP_DE_TEST";
	private static String identifierP= "ID_ADST_PARAM_DE_TEST";
	private static String providerUrl= "ldap://10.75.113.53:389";
	private static String securityAuth= "simple";
	
	
	@Autowired
	private AbstractDomainService abstractDomainService;
	
	@Autowired
	private UserProviderService userProviderService;
	
	@Autowired
	private DomainPolicyRepository domainPolicyRepository;
	
	
	
	private LDAPConnection ldapconnexion;
	private DomainPattern domainPattern;
	
	@Before
	@Transactional (propagation=Propagation.REQUIRED)
	public void setUp() throws Exception {
		logger.debug("Begin setUp");
		ldapconnexion  = new LDAPConnection(identifier, providerUrl, securityAuth);
		try {
			userProviderService.createLDAPConnection(ldapconnexion);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		logger.debug("Current ldapconnexion object: " + ldapconnexion.toString());
		
		domainPattern = new DomainPattern(identifierP, "blabla", "getUserCommand", "getAllDomainUsersCommand", "authCommand", "searchUserCommand", "mail","firstname","lastname");
		try {
			userProviderService.createDomainPattern(domainPattern);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		logger.debug("Current pattern object: " + domainPattern.toString());
		
		logger.debug("End setUp");
	}

	@After
	@Transactional (propagation=Propagation.REQUIRED)
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");
		userProviderService.deleteConnection(ldapconnexion.getIdentifier());
		userProviderService.deletePattern(domainPattern.getIdentifier());
		logger.debug("End tearDown");
	}
	
	

	@Test
	public void testCreateTopDomain() {
		TopDomain topDomain = new TopDomain(topDomaineName,"label",ldapconnexion,domainPattern,baseDn);
		DomainPolicy policy = domainPolicyRepository.findById(LinShareConstants.defaultDomainPolicyIdentifier);
		topDomain.setPolicy(policy);
		
		try {
			AbstractDomain domain = abstractDomainService.createTopDomain(topDomain);
			
			abstractDomainService.deleteDomain(topDomain.getIdentifier());
					
			
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.fail("Can't create top domain.");
		}
	}
	
	
	@Test
	public void testCreateTopDomain2() {
		
		LDAPConnection myldapconnexion = null;
		DomainPattern mydomainPattern = null;
		
		try {
			myldapconnexion = userProviderService.retrieveLDAPConnection("baseLDAP");
			mydomainPattern = userProviderService.retrieveDomainPattern("basePattern");
		} catch (BusinessException e1) {
			e1.printStackTrace();
			Assert.fail("Can't get initial data (domain pattern and ldap connection");
		}
		
		TopDomain topDomain = new TopDomain(topDomaineName+"1","label",myldapconnexion,mydomainPattern,baseDn);
		DomainPolicy policy = domainPolicyRepository.findById(LinShareConstants.defaultDomainPolicyIdentifier);
		topDomain.setPolicy(policy);
		
		try {
			abstractDomainService.createTopDomain(topDomain);
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.fail("Can't create domain.");
		}
		
		try {
			abstractDomainService.deleteDomain(topDomain.getIdentifier());
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.fail("Can't delete top domain.");
		}
		
	}
		
}
