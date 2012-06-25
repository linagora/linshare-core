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
package org.linagora.linshare.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainPattern;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.domain.entities.LDAPConnection;
import org.linagora.linshare.core.domain.entities.TopDomain;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.UserProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class AbstractDomainServiceImplTest extends AbstractJUnit4SpringContextTests{
	
	private static Logger logger = LoggerFactory.getLogger(AbstractDomainServiceImplTest.class);

	
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
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		ldapconnexion  = new LDAPConnection(identifier, providerUrl, securityAuth);
		try {
			userProviderService.createLDAPConnection(ldapconnexion);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		logger.debug("Current ldapconnexion object: " + ldapconnexion.toString());
		
		domainPattern = new DomainPattern(identifierP, "blabla", "getUserCommand", "getAllDomainUsersCommand", "authCommand", "searchUserCommand", "mail","firstname","lastname", "firstname.lastname");
		try {
			userProviderService.createDomainPattern(domainPattern);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		logger.debug("Current pattern object: " + domainPattern.toString());
		
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		userProviderService.deleteConnection(ldapconnexion.getIdentifier());
		userProviderService.deletePattern(domainPattern.getIdentifier());
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}
	
	

	@Test
	public void testCreateTopDomain() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
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
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	
	@Test
	public void testCreateTopDomain2() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
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
		logger.debug(LinShareTestConstants.END_TEST);
	}
		
}
