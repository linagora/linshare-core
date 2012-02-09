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
package org.linagora.linShare.repository.hibernate;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linShare.core.domain.entities.AbstractDomain;
import org.linagora.linShare.core.domain.entities.AllowAllDomain;
import org.linagora.linShare.core.domain.entities.AllowDomain;
import org.linagora.linShare.core.domain.entities.DenyAllDomain;
import org.linagora.linShare.core.domain.entities.DenyDomain;
import org.linagora.linShare.core.domain.entities.DomainAccessPolicy;
import org.linagora.linShare.core.domain.entities.DomainAccessRule;
import org.linagora.linShare.core.domain.entities.DomainPolicy;
import org.linagora.linShare.core.domain.entities.Policy;
import org.linagora.linShare.core.domain.entities.RootDomain;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.AbstractDomainRepository;
import org.linagora.linShare.core.repository.DomainAccessPolicyRepository;
import org.linagora.linShare.core.repository.DomainAccessRuleRepository;
import org.linagora.linShare.core.repository.DomainPolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations={"classpath:springContext-test.xml", 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class DomainAccessRuleRepositoryImplTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private DomainAccessRuleRepository domainAccessRuleRepository;

	@Autowired
	private DomainAccessPolicyRepository domainAccessPolicyRepository;
	
	@Autowired
	private DomainPolicyRepository domainPolicyRepository;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	
	private static String rootDomaineName = "Domain0";
	private static String domainePolicyName0 = "TestAccessPolicy0";
	
	
	private DomainAccessPolicy policy;
	
	
	@Before
	public void setUp() throws Exception {
		logger.debug("Begin setUp");
		policy = new DomainAccessPolicy();
		logger.debug("Current policy : " + policy.toString());
		domainAccessPolicyRepository.create(policy);
		logger.debug("End setUp");
	}

	@After
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");
		domainAccessPolicyRepository.delete(policy);
		logger.debug("End tearDown");
	}
	
	private AbstractDomain createATestRootDomain(Integer var) throws BusinessException {
		// Default policy creation 
		DomainPolicy policy = new DomainPolicy(domainePolicyName0, new DomainAccessPolicy());
		domainPolicyRepository.create(policy);
				
		AbstractDomain currentDomain= new RootDomain(rootDomaineName + "-" + var,"My root domain");
		currentDomain.setPolicy(policy);
		abstractDomainRepository.create(currentDomain);
		logger.debug("Current AbstractDomain object: " + currentDomain.toString());
		return currentDomain;
	}
	
	private void deleteTestRootDomain(AbstractDomain currentDomain) throws BusinessException {
		DomainPolicy p = currentDomain.getPolicy();
		abstractDomainRepository.delete(currentDomain);
		domainPolicyRepository.delete(p);
	}
	
	@Test
	public void testCreateAllowAllDomainRule() throws BusinessException{
		logger.debug("Begin testCreateAllowAllDomainRule");
		
		DomainAccessRule rule = new AllowAllDomain();
		policy.addRule(rule);
		domainAccessPolicyRepository.update(policy);
		Assert.assertNotNull(rule.getPersistenceId());
		
		logger.debug("Current object: " + rule.toString());

		DomainAccessRule entityRule = domainAccessRuleRepository.findById(rule.getPersistenceId());
		
		Assert.assertTrue(entityRule != null );
		Assert.assertTrue(entityRule instanceof AllowAllDomain );
		
		logger.debug("End testCreateAllowAllDomainRule");
	}
	
	@Test
	public void testCreateDenyAllDomainRule() throws BusinessException{
		logger.debug("Begin testCreateDenyAllDomainRule");
		
		DomainAccessRule rule = new DenyAllDomain();
		policy.addRule(rule);
		domainAccessPolicyRepository.update(policy);
		Assert.assertNotNull(rule.getPersistenceId());
		
		logger.debug("Current object: " + rule.toString());
		
		DomainAccessRule entityRule = domainAccessRuleRepository.findById(rule.getPersistenceId());
		
		Assert.assertTrue(entityRule != null );
		Assert.assertFalse(entityRule instanceof AllowAllDomain );
		Assert.assertTrue(entityRule instanceof DenyAllDomain );
		logger.debug("End testCreateDenyAllDomainRule");
	}
	
	@Test
	public void testCreateDenyDomainRule() throws BusinessException{
		logger.debug("Begin testCreateDenyDomainRule");
		
		AbstractDomain currentDomain = createATestRootDomain(1);
		DomainAccessRule rule = new DenyDomain(currentDomain);
		policy.addRule(rule);
		currentDomain.getDomainAccessRules().add(rule);
		
		domainAccessPolicyRepository.update(policy);
		abstractDomainRepository.update(currentDomain);
		Assert.assertNotNull(rule.getPersistenceId());
		
		logger.debug("Current object: " + rule.toString());
		
		DomainAccessRule entityRule = domainAccessRuleRepository.findById(rule.getPersistenceId());
		logger.debug("Current ENTITY : " + entityRule.toString());

		Assert.assertTrue(entityRule instanceof DenyDomain );
		
		
		logger.debug("policy.getRules().size() : " + policy.getRules().size());
		deleteTestRootDomain(currentDomain);
		
		DomainAccessPolicy p = domainAccessPolicyRepository.findById(policy.getPersistenceId());
		Assert.assertEquals(0, p.getRules().size());
		
		logger.debug("p.getRules().size() : " + p.getRules().size());
		logger.debug("End testCreateDenyDomainRule");
	}
	
	@Test
	public void testCreateAllowDomainRule() throws BusinessException{
		logger.debug("Begin testCreateAllowDomainRule");
		
		AbstractDomain currentDomain = createATestRootDomain(2);

		AllowDomain rule = new AllowDomain(currentDomain);
		policy.addRule(rule);
		currentDomain.getDomainAccessRules().add(rule);
		
		domainAccessPolicyRepository.update(policy);
		abstractDomainRepository.update(currentDomain);
		Assert.assertNotNull(rule.getPersistenceId());
		
		logger.debug("Current object: " + rule.toString());
		
		DomainAccessRule entityRule = domainAccessRuleRepository.findById(rule.getPersistenceId());
		logger.debug("Current ENTITY : " + entityRule.toString());

		Assert.assertTrue(entityRule instanceof AllowDomain );
		
		logger.debug("currentDomain.getDomainAccessRules().size() : " + currentDomain.getDomainAccessRules().size());
		
		
		logger.debug("policy.getRules().size() : " + policy.getRules().size());
		deleteTestRootDomain(currentDomain);
		
		DomainAccessPolicy p = domainAccessPolicyRepository.findById(policy.getPersistenceId());
		Assert.assertEquals(0, p.getRules().size());
		
		logger.debug("p.getRules().size() : " + p.getRules().size());
		logger.debug("End testCreateAllowDomainRule");
	}
	
	@Test
	public void testCreateDomainWhiteListRule() throws BusinessException{
		logger.debug("Begin testCreateDomainWhiteListRule");
		
		AbstractDomain currentDomain = createATestRootDomain(3);
		
		AllowDomain rule = new AllowDomain(currentDomain);
		policy.addRule(rule);
		currentDomain.getDomainAccessRules().add(rule);
		
		DomainAccessRule endRule = new DenyAllDomain();
		policy.addRule(endRule);
		currentDomain.getDomainAccessRules().add(endRule);
		
		domainAccessPolicyRepository.update(policy);
		abstractDomainRepository.update(currentDomain);
		Assert.assertNotNull(rule.getPersistenceId());
		
		logger.debug("Current object: " + rule.toString());
		
		DomainAccessRule entityRule = domainAccessRuleRepository.findById(rule.getPersistenceId());
		logger.debug("Current ENTITY : " + entityRule.toString());
		
		Assert.assertTrue(entityRule instanceof AllowDomain );
		
		logger.debug("currentDomain.getDomainAccessRules().size() : " + currentDomain.getDomainAccessRules().size());
		Assert.assertEquals(2, currentDomain.getDomainAccessRules().size());
		
		logger.debug("policy.getRules().size() : " + policy.getRules().size());
		deleteTestRootDomain(currentDomain);
		
		logger.debug("End testCreateDomainWhiteListRule");
	}
	
}
