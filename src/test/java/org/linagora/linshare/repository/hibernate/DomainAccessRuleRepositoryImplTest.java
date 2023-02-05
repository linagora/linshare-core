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

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AllowAllDomain;
import org.linagora.linshare.core.domain.entities.AllowDomain;
import org.linagora.linshare.core.domain.entities.DenyAllDomain;
import org.linagora.linshare.core.domain.entities.DenyDomain;
import org.linagora.linshare.core.domain.entities.DomainAccessPolicy;
import org.linagora.linshare.core.domain.entities.DomainAccessRule;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.domain.entities.RootDomain;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DomainAccessPolicyRepository;
import org.linagora.linshare.core.repository.DomainAccessRuleRepository;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations={
		"classpath:springContext-test.xml", 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class DomainAccessRuleRepositoryImplTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private DomainAccessRuleRepository domainAccessRuleRepository;

	@Autowired
	private DomainAccessPolicyRepository domainAccessPolicyRepository;

	@Autowired
	private DomainPolicyRepository domainPolicyRepository;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	@Autowired
	private DomainAccessPolicyRepository domainAccessRepository;

	private static String rootDomaineName = "Domain0";
	private static String domainePolicyName0 = "TestAccessPolicy0";


	private DomainAccessPolicy policy;


	@BeforeEach
	public void setUp() throws Exception {
		logger.debug("Begin setUp");
		policy = new DomainAccessPolicy();
		logger.debug("Current policy : " + policy.toString());
		domainAccessPolicyRepository.create(policy);
		logger.debug("End setUp");
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");
		domainAccessPolicyRepository.delete(policy);
		logger.debug("End tearDown");
	}

	private AbstractDomain createATestRootDomain(Integer var) throws BusinessException {
		// Default policy creation 
		DomainAccessPolicy domainAccessPolicy = new DomainAccessPolicy();
		domainAccessRepository.create(domainAccessPolicy);
		DomainPolicy policy = new DomainPolicy(domainePolicyName0, domainAccessPolicy);

		domainPolicyRepository.create(policy);

		AbstractDomain currentDomain= new RootDomain("My root domain");
		currentDomain.setPolicy(policy);
		abstractDomainRepository.create(currentDomain);

		// FIXME : override uuid with default root domain name
		currentDomain.setUuid(rootDomaineName + "-" + var);
		currentDomain = abstractDomainRepository.update(currentDomain);
		logger.debug("Current AbstractDomain object: " + currentDomain.toString());
		return currentDomain;
	}
	
	@Test
	private void deleteTestRootDomain(AbstractDomain currentDomain) throws BusinessException {
		DomainPolicy p = currentDomain.getPolicy();
//		currentDomain.setPolicy(null);
		domainPolicyRepository.delete(p);
		currentDomain = abstractDomainRepository.update(currentDomain);
		abstractDomainRepository.delete(currentDomain);
	}

	@Test
	public void testCreateAllowAllDomainRule() throws BusinessException{
		logger.debug("Begin testCreateAllowAllDomainRule");

		DomainAccessRule rule = new AllowAllDomain();
		policy.addRule(rule);
		domainAccessPolicyRepository.update(policy);
		Assertions.assertNotNull(rule.getPersistenceId());

		logger.debug("Current object: " + rule.toString());

		DomainAccessRule entityRule = domainAccessRuleRepository.findById(rule.getPersistenceId());

		Assertions.assertTrue(entityRule != null );
		Assertions.assertTrue(entityRule instanceof AllowAllDomain );

		logger.debug("End testCreateAllowAllDomainRule");
	}

	@Test
	public void testCreateDenyAllDomainRule() throws BusinessException{
		logger.debug("Begin testCreateDenyAllDomainRule");

		DomainAccessRule rule = new DenyAllDomain();
		policy.addRule(rule);
		domainAccessPolicyRepository.update(policy);
		Assertions.assertNotNull(rule.getPersistenceId());

		logger.debug("Current object: " + rule.toString());

		DomainAccessRule entityRule = domainAccessRuleRepository.findById(rule.getPersistenceId());

		Assertions.assertTrue(entityRule != null );
		Assertions.assertFalse(entityRule instanceof AllowAllDomain );
		Assertions.assertTrue(entityRule instanceof DenyAllDomain );
		logger.debug("End testCreateDenyAllDomainRule");
	}

	@DirtiesContext
	@Test
	public void testCreateDenyDomainRule() throws BusinessException{
		logger.debug("Begin testCreateDenyDomainRule");

		AbstractDomain currentDomain = createATestRootDomain(1);
		DomainAccessRule rule = new DenyDomain(currentDomain);
		policy.addRule(rule);
		currentDomain.getDomainAccessRules().add(rule);

		domainAccessPolicyRepository.update(policy);
		currentDomain = abstractDomainRepository.update(currentDomain);
		Assertions.assertNotNull(rule.getPersistenceId());

		logger.debug("Current object: " + rule.toString());

		DomainAccessRule entityRule = domainAccessRuleRepository.findById(rule.getPersistenceId());
		logger.debug("Current ENTITY : " + entityRule.toString());

		Assertions.assertTrue(entityRule instanceof DenyDomain );

		logger.debug("End testCreateDenyDomainRule");
	}

	@DirtiesContext
	@Test
	public void testCreateAllowDomainRule() throws BusinessException{
		logger.debug("Begin testCreateAllowDomainRule");

		AbstractDomain currentDomain = createATestRootDomain(2);

		AllowDomain rule = new AllowDomain(currentDomain);
		policy.addRule(rule);
		currentDomain.getDomainAccessRules().add(rule);

		domainAccessPolicyRepository.update(policy);
		currentDomain = abstractDomainRepository.update(currentDomain);
		Assertions.assertNotNull(rule.getPersistenceId());

		logger.debug("Current object: " + rule.toString());

		DomainAccessRule entityRule = domainAccessRuleRepository.findById(rule.getPersistenceId());
		logger.debug("Current ENTITY : " + entityRule.toString());

		Assertions.assertTrue(entityRule instanceof AllowDomain );

		logger.debug("currentDomain.getDomainAccessRules().size() : " + currentDomain.getDomainAccessRules().size());

		logger.debug("End testCreateAllowDomainRule");
	}

	@DirtiesContext
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
		currentDomain = abstractDomainRepository.update(currentDomain);
		Assertions.assertNotNull(rule.getPersistenceId());

		logger.debug("Current object: " + rule.toString());

		DomainAccessRule entityRule = domainAccessRuleRepository.findById(rule.getPersistenceId());
		logger.debug("Current ENTITY : " + entityRule.toString());

		Assertions.assertTrue(entityRule instanceof AllowDomain );

		logger.debug("currentDomain.getDomainAccessRules().size() : " + currentDomain.getDomainAccessRules().size());
		Assertions.assertEquals(2, currentDomain.getDomainAccessRules().size());

		logger.debug("End testCreateDomainWhiteListRule");
	}

}
