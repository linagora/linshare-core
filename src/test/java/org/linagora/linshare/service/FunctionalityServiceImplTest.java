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
package org.linagora.linshare.service;

import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Policy;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.UnitValueFunctionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.linagora.linshare.core.repository.FunctionalityRepository;
import org.linagora.linshare.core.repository.RootUserRepository;
import org.linagora.linshare.core.service.FunctionalityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.google.common.collect.ImmutableList;

@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-test.xml" })
public class FunctionalityServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests{

	private static Logger logger = LoggerFactory.getLogger(FunctionalityServiceImplTest.class);

	private int TOTAL_COUNT_FUNC = LoadingServiceTestDatas.TOTAL_COUNT_FUNC;

	@Autowired
	private FunctionalityService functionalityService;

	@Autowired
	private FunctionalityRepository functionalityRepository;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	@Autowired
	private DomainPolicyRepository domainPolicyRepository;

	@Autowired
	private RootUserRepository rootUserRepository;

	private User actor;

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		actor = rootUserRepository.findByLsUuid("root@localhost.localdomain@test");
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	@DirtiesContext
	public void testGetAllFunctionalityForRootDomain() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.rootDomainName);
		Assert.assertNotNull(domain);

		Assert.assertEquals(TOTAL_COUNT_FUNC,domain.getFunctionalities().size());
		Iterable<Functionality> list = functionalityService.findAll(actor, domain.getUuid());

		Assert.assertNotNull(list);
		Assert.assertEquals(TOTAL_COUNT_FUNC, ImmutableList.copyOf(list).size());
		logger.debug(LinShareTestConstants.END_TEST);

	}

	@Test
	@DirtiesContext
	public void testGetAllFunctionalityForTopDomain() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.topDomainName);
		Assert.assertNotNull(domain);

		Assert.assertEquals(2, domain.getFunctionalities().size());
		Iterable<Functionality> list = functionalityService.findAll(actor, domain.getUuid());

		Assert.assertNotNull(list);
		Assert.assertEquals(TOTAL_COUNT_FUNC - 2, ImmutableList.copyOf(list).size());
		logger.debug(LinShareTestConstants.END_TEST);

	}

	@Test
	@DirtiesContext
	public void testGetAllFunctionalityForSubDomain() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.subDomainName1);
		Assert.assertNotNull(domain);
		Assert.assertEquals(1,domain.getFunctionalities().size());
		Iterable<Functionality> list = functionalityService.findAll(actor, domain.getUuid());
		Assert.assertNotNull(list);
		// There is two functionalities that can not be display
		Assert.assertEquals(TOTAL_COUNT_FUNC -2, ImmutableList.copyOf(list).size());
		logger.debug(LinShareTestConstants.END_TEST);

	}

	@Test
	@DirtiesContext
	public void testgetFunctionality() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
//		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.subDomainName1);
		String subdomain1 = LoadingServiceTestDatas.subDomainName1;

		// The first functionality should be override by the current subdomain
		Functionality func1 = functionalityService.find(actor, subdomain1, LoadingServiceTestDatas.FILESIZE_MAX);
		Assert.assertNotNull(func1);
		Assert.assertEquals(subdomain1, func1.getDomain().getUuid());

		// The second functionality should be override by the top domain
		Assert.assertEquals(3, countFunctionality(LoadingServiceTestDatas.rootDomainName, LoadingServiceTestDatas.QUOTA_USER));
		Functionality func2_parent = functionalityService.find(actor, LoadingServiceTestDatas.rootDomainName, LoadingServiceTestDatas.QUOTA_USER);
		Functionality func2_child = functionalityService.find(actor, subdomain1, LoadingServiceTestDatas.QUOTA_USER);
		Assert.assertNotNull(func2_child);
		Assert.assertNotSame(func2_parent, func2_child);
		Assert.assertFalse(func2_parent.businessEquals(func2_child, true));

		// It should the same functionality for each domain.
		Functionality func3_root = functionalityService.find(actor, LoadingServiceTestDatas.rootDomainName, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		Functionality func3_top = functionalityService.find(actor, LoadingServiceTestDatas.topDomainName, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		Functionality func3_sub = functionalityService.find(actor, subdomain1, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		Assert.assertNotNull(func3_root);
		Assert.assertNotNull(func3_top);
		Assert.assertNotNull(func3_top);
		Assert.assertNotSame(func3_root, func3_top);
		Assert.assertNotSame(func3_sub, func3_top);
		Assert.assertNotSame(func3_root, func3_sub);
		Assert.assertTrue(func3_sub.businessEquals(func3_root, true));
		Assert.assertTrue(func3_sub.businessEquals(func3_top, true));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@DirtiesContext
	public void testUpdateUnitValueFunctionality1() throws BusinessException {
		// This test is designed to check a simple functionality modification.
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.subDomainName1);
		Assert.assertNotNull(domain);

		UnitValueFunctionality func = (UnitValueFunctionality)functionalityService.find(actor, domain.getUuid(), LoadingServiceTestDatas.FILESIZE_MAX);

		Assert.assertEquals(50,func.getValue().intValue());
		func.setValue(25);
		functionalityService.update(actor, domain.getUuid(), func);

		domain = abstractDomainRepository.findById(LoadingServiceTestDatas.subDomainName1);
		Assert.assertEquals(1, domain.getFunctionalities().size());
		for (Functionality it : domain.getFunctionalities()) {
			Assert.assertEquals(25, ((UnitValueFunctionality)it).getValue().intValue());
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@DirtiesContext
	public void testUpdateUnitValueFunctionality2() throws BusinessException {
		// This test is designed to check a simple update without modification.
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.subDomainName1);
		Assert.assertNotNull(domain);

		UnitValueFunctionality func = (UnitValueFunctionality)functionalityService.find(actor, domain.getUuid(), LoadingServiceTestDatas.FILESIZE_MAX);

		Assert.assertEquals(50,func.getValue().intValue());
		functionalityService.update(actor, domain.getUuid(), func);

		domain = abstractDomainRepository.findById(LoadingServiceTestDatas.subDomainName1);
		Assert.assertEquals(1, domain.getFunctionalities().size());
		for (Functionality it : domain.getFunctionalities()) {
			Assert.assertEquals(50, ((UnitValueFunctionality)it).getValue().intValue());
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@DirtiesContext
	public void testUpdateUnitValueFunctionality2bis() throws BusinessException {
		// This test is designed to check a simple update without modification.
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.subDomainName1);
		Assert.assertNotNull(domain);

		StringValueFunctionality func = (StringValueFunctionality)functionalityService.find(actor, domain.getUuid(), LoadingServiceTestDatas.TEST_TIME_STAMPING);

		Assert.assertTrue(func.getValue().equals(LoadingServiceTestDatas.timeStampingUrl));
		functionalityService.update(actor, domain.getUuid(), func);

		domain = abstractDomainRepository.findById(LoadingServiceTestDatas.subDomainName1);
		Assert.assertEquals(1, domain.getFunctionalities().size());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@DirtiesContext
	public void testUpdateActivationPolicyFunctionality1() {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		String subDomain = LoadingServiceTestDatas.subDomainName2;

		StringValueFunctionality func_child = (StringValueFunctionality)functionalityService.find(actor, subDomain, LoadingServiceTestDatas.TEST_TIME_STAMPING);

		// modification of a functionality which belong to the root domain. This will lead to the creation of a new functionality
		func_child.setValue("plop");
		functionalityService.update(actor, subDomain, func_child);

		String topDomain = LoadingServiceTestDatas.rootDomainName;
		StringValueFunctionality func_parent = (StringValueFunctionality)functionalityService.find(actor, topDomain, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		// sub functionality will be reset.
		func_parent.getActivationPolicy().setPolicy(Policies.FORBIDDEN);
		functionalityService.update(actor, topDomain, func_parent);

		try {
			func_child = (StringValueFunctionality)functionalityService.find(actor, subDomain, LoadingServiceTestDatas.TEST_TIME_STAMPING);
			Assert.fail("should not be display");
		} catch (BusinessException e) {
			Assert.assertTrue("should be display", true);
		}

		// we should not be able to update a functionality in subdomain where parent do not allowed it.
		func_child = (StringValueFunctionality) func_parent.clone();
		func_child.setValue("plop2");
		try {
			functionalityService.update(actor, subDomain, func_child);
			Assert.fail("This message should not be seen. An BusinessException was expected.");
		} catch (BusinessException e) {
			Assert.assertTrue(true);
		} catch (TechnicalException e) {
			if(!e.getErrorCode().equals(TechnicalErrorCode.FUNCTIONALITY_ENTITY_MODIFICATION_NOT_ALLOW)) {
				Assert.fail("unkown error : " + e.getMessage());
				throw(e);
			}
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@DirtiesContext
	public void testUpdateActivationPolicyFunctionality4() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.subDomainName2);
		Assert.assertNotNull(domain);

		StringValueFunctionality func = (StringValueFunctionality)functionalityService.find(actor, domain.getUuid(), LoadingServiceTestDatas.TEST_TIME_STAMPING);

		// modification of a functionality which belong to the root domain. This will lead to the creation of a new functionality
		func.setValue("plop");
		functionalityService.update(actor, domain.getUuid(), func);

		StringValueFunctionality parent = (StringValueFunctionality)functionalityService.find(actor, LoadingServiceTestDatas.topDomainName, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		parent.getActivationPolicy().setPolicy(Policies.FORBIDDEN);

		try {
			functionalityService.update(actor, domain.getUuid(), parent);
			Assert.assertTrue(false);
		} catch (BusinessException e) {
			Assert.assertTrue(true);
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@DirtiesContext
	public void testUpdateRootDomainFunctionality() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.rootDomainName);
		Assert.assertNotNull(domain);

		StringValueFunctionality func = (StringValueFunctionality)functionalityService.find(actor, domain.getUuid(), LoadingServiceTestDatas.TEST_TIME_STAMPING);

		func.setValue("plop");
		func.getActivationPolicy().setPolicy(Policies.FORBIDDEN);
		functionalityService.update(actor, domain.getUuid(), func);

		logger.debug(LinShareTestConstants.END_TEST);
	}

	private int countFunctionality(String domainId, String funcIdentifier) {
		// reload each time 
		AbstractDomain domain = abstractDomainRepository.findById(domainId);
		Assert.assertNotNull(domain);
		return countFunctionality(domain, funcIdentifier);
	}

	private int countFunctionality(AbstractDomain domain , String funcIdentifier) {
		int count = 0;

		Set<Functionality> functionalities = domain.getFunctionalities();
		for (Functionality f : functionalities) {
			if(f.getIdentifier().equals(funcIdentifier)) {
				logger.debug("countFunctionality:" + domain.getUuid());
				count++;
			}

		}

		Set<AbstractDomain> subdomain = domain.getSubdomain();
		for (AbstractDomain abstractDomain : subdomain) {
			count += countFunctionality(abstractDomain, funcIdentifier);
		}
		return count;
	}

	// FIXME : Now we need AbstractTransactionalJUnit4SpringContextTests because
	// we made some mapping modifications (enable lasy loading)
	// countFunctionality won't work now. Need another method to test test results.
	@Ignore
	@Test
	@DirtiesContext
	public void testUpdateActivationPolicyFunctionality3() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		String rootDomainId = LoadingServiceTestDatas.rootDomainName;

		Assert.assertEquals(1, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));

		// Step 1
		StringValueFunctionality func = (StringValueFunctionality)functionalityService.find(actor, LoadingServiceTestDatas.topDomainName, LoadingServiceTestDatas.TEST_TIME_STAMPING);

		func.getActivationPolicy().setStatus(false);
		// modification of a functionality which belong to the root domain. This will lead to the creation of a new functionality for top domain
		functionalityService.update(actor, LoadingServiceTestDatas.topDomainName, func);
		Assert.assertEquals(2, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));

		// Step 2
		StringValueFunctionality func2 = (StringValueFunctionality)functionalityService.find(actor, LoadingServiceTestDatas.subDomainName1, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		func2.getActivationPolicy().setPolicy(Policies.FORBIDDEN);
		// modification of a functionality which belong to the top domain. This will lead to the creation of a new functionality for sub domain
		functionalityService.update(actor, LoadingServiceTestDatas.subDomainName1, func2);
		Assert.assertEquals(3, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));

		// Step 3
		StringValueFunctionality func3 = (StringValueFunctionality)functionalityService.find(actor, rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		func3.getActivationPolicy().setPolicy(Policies.FORBIDDEN);
		// root domain functionality is set with a forbidden policy, this should lead to the suppression of all functionalities above rootDomain
		functionalityService.update(actor, rootDomainId, func3);
		Assert.assertEquals(1, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	// FIXME : Now we need AbstractTransactionalJUnit4SpringContextTests because
	// we made some mapping modifications (enable lasy loading)
	// countFunctionality won't work now. Need another method to test test results.
	@Ignore
	@Test
	@DirtiesContext
	public void testUpdateConfigurationPolicyFunctionality1() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		String rootDomainId = LoadingServiceTestDatas.rootDomainName;

		Assert.assertEquals(1, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));

		// Step 1
		StringValueFunctionality func = (StringValueFunctionality)functionalityService.find(actor, LoadingServiceTestDatas.topDomainName, LoadingServiceTestDatas.TEST_TIME_STAMPING);

		func.getConfigurationPolicy().setStatus(false);
		// modification of a functionality which belong to the root domain. This will lead to the creation of a new functionality for top domain
		functionalityService.update(actor, LoadingServiceTestDatas.topDomainName, func);
		Assert.assertEquals(2, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));


		// Step 2
		StringValueFunctionality func2 = (StringValueFunctionality)functionalityService.find(actor, LoadingServiceTestDatas.subDomainName1, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		func2.getConfigurationPolicy().setPolicy(Policies.FORBIDDEN);
		// modification of a functionality which belong to the top domain. This will lead to the creation of a new functionality for sub domain
		functionalityService.update(actor, LoadingServiceTestDatas.subDomainName1, func2);
		Assert.assertEquals(3, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));


		// Step 3
		StringValueFunctionality func3 = (StringValueFunctionality)functionalityService.find(actor, rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		func3.getConfigurationPolicy().setPolicy(Policies.MANDATORY);
		// root domain functionality is set with a mandatory policy, this should lead to the modification of all functionalities above rootDomain
		// func 2 : configuration policy will be updated form FORBIDDEN to MANDATORY
		functionalityService.update(actor, rootDomainId, func3);

		// first check
		Assert.assertEquals(3, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));
		// second check : func 4 (equal to func 2) : configuration policy will be updated form FORBIDDEN to MANDATORY
		StringValueFunctionality func4 = (StringValueFunctionality)functionalityService.find(actor, LoadingServiceTestDatas.subDomainName1, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		logger.debug("func4.getConfigurationPolicy().getPolicy() : " + func4.getConfigurationPolicy().getPolicy());
		Assert.assertTrue(func4.getConfigurationPolicy().getPolicy().equals(Policies.MANDATORY));

		logger.debug(LinShareTestConstants.END_TEST);
	}

	// FIXME : Now we need AbstractTransactionalJUnit4SpringContextTests because
	// we made some mapping modifications (enable lasy loading)
	// countFunctionality won't work now. Need another method to test test results.
	@Ignore
	@Test
	@DirtiesContext
	public void testUpdateConfigurationPolicyFunctionality2() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		String rootDomainId = LoadingServiceTestDatas.rootDomainName;
		String valueFromTop="value from top";
		String valueFromSub="value from sub";

		Assert.assertEquals(1, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));


		// Step 1
		StringValueFunctionality func = (StringValueFunctionality)functionalityService.find(actor, LoadingServiceTestDatas.topDomainName, LoadingServiceTestDatas.TEST_TIME_STAMPING);

		func.getConfigurationPolicy().setStatus(false);
		func.setValue(valueFromTop);
		// modification of a functionality which belong to the root domain. This will lead to the creation of a new functionality for top domain
		functionalityService.update(actor, LoadingServiceTestDatas.topDomainName, func);
		Assert.assertEquals(2, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));


		// Step 2
		StringValueFunctionality func2 = (StringValueFunctionality)functionalityService.find(actor, LoadingServiceTestDatas.subDomainName1, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		func2.getConfigurationPolicy().setPolicy(Policies.MANDATORY);
		func2.setValue(valueFromSub);
		// modification of a functionality which belong to the top domain. This will lead to the creation of a new functionality for sub domain
		functionalityService.update(actor, LoadingServiceTestDatas.subDomainName1, func2);
		Assert.assertEquals(3, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));


		// Step 3
		StringValueFunctionality func3 = (StringValueFunctionality)functionalityService.find(actor, rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		Assert.assertEquals(LoadingServiceTestDatas.timeStampingUrl, func3.getValue());
		func3.getConfigurationPolicy().setPolicy(Policies.FORBIDDEN);

		// root domain functionality is set with a forbidden policy, this should lead to the modification of all functionalities above rootDomain
		functionalityService.update(actor, rootDomainId, func3);
		Assert.assertEquals(3, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));


		// Step 4
		StringValueFunctionality func4 = (StringValueFunctionality)functionalityService.find(actor, LoadingServiceTestDatas.subDomainName1, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		logger.debug("func4.getConfigurationPolicy().getPolicy() : " + func4.getConfigurationPolicy().getPolicy());
		Assert.assertTrue(func4.getConfigurationPolicy().getPolicy().equals(Policies.FORBIDDEN));
		logger.debug("func4.getValue()" + func4.getValue());
		Assert.assertEquals(LoadingServiceTestDatas.timeStampingUrl, func4.getValue());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	// FIXME : Now we need AbstractTransactionalJUnit4SpringContextTests because
	// we made some mapping modifications (enable lasy loading)
	// countFunctionality won't work now. Need another method to test test results.
	@Ignore
	@Test
	@DirtiesContext
	public void testUpdateConfigurationPolicyFunctionality3() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		String rootDomainId = LoadingServiceTestDatas.rootDomainName;

		Assert.assertEquals(1, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));


		// Step 1
		StringValueFunctionality func = (StringValueFunctionality)functionalityService.find(actor, LoadingServiceTestDatas.topDomainName, LoadingServiceTestDatas.TEST_TIME_STAMPING);

		func.getConfigurationPolicy().setStatus(false);
		// modification of a functionality which belong to the root domain. This will lead to the creation of a new functionality for top domain
		functionalityService.update(actor, LoadingServiceTestDatas.topDomainName, func);
		Assert.assertEquals(2, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));


		// Step 2
		StringValueFunctionality func2 = (StringValueFunctionality)functionalityService.find(actor, LoadingServiceTestDatas.subDomainName1, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		func2.getConfigurationPolicy().setPolicy(Policies.FORBIDDEN);
		// modification of a functionality which belong to the top domain. This will lead to the creation of a new functionality for sub domain
		functionalityService.update(actor, LoadingServiceTestDatas.subDomainName1, func2);
		Assert.assertEquals(3, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));


		// Step 3
		StringValueFunctionality func3 = (StringValueFunctionality)functionalityService.find(actor, rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		func3.getConfigurationPolicy().setPolicy(Policies.MANDATORY);
		// root domain functionality is set with a forbidden policy, this should lead to the suppression of all functionalities above rootDomain
		functionalityService.update(actor, rootDomainId, func3);
		Assert.assertEquals(3, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));

		StringValueFunctionality func4 = (StringValueFunctionality)functionalityService.find(actor, LoadingServiceTestDatas.subDomainName1, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		Assert.assertTrue(func4.getConfigurationPolicy().getPolicy().equals(Policies.MANDATORY));

		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@DirtiesContext
	public void testCheckAndUpdate() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		Policy activationPolicy = new Policy();
		Policy configurationPolicy = new Policy();

		// Try to modify functionalities with a specific configuration. Fail in Linshare 1.0.0-rc1
		try {
			// Set the functionality entity
			StringValueFunctionality funcAncestor = (StringValueFunctionality)functionalityService.find(actor, LoadingServiceTestDatas.rootDomainName, LoadingServiceTestDatas.TEST_TIME_STAMPING);

			activationPolicy = funcAncestor.getActivationPolicy();
			activationPolicy.setStatus(true);
			activationPolicy.setSystem(false);
			activationPolicy.setPolicy(Policies.ALLOWED);

			configurationPolicy = funcAncestor.getConfigurationPolicy();
			configurationPolicy.setStatus(false);
			configurationPolicy.setSystem(true);
			configurationPolicy.setPolicy(Policies.FORBIDDEN);

			functionalityService.update(actor,LoadingServiceTestDatas.rootDomainName, funcAncestor);

			StringValueFunctionality funcEntity = (StringValueFunctionality)functionalityService.find(actor, LoadingServiceTestDatas.topDomainName, LoadingServiceTestDatas.TEST_TIME_STAMPING);
			activationPolicy = funcEntity.getActivationPolicy();
			activationPolicy.setStatus(false);
			activationPolicy.setSystem(false);
			activationPolicy.setPolicy(Policies.FORBIDDEN);
			functionalityService.update(actor,LoadingServiceTestDatas.topDomainName, funcEntity);

			funcEntity = (StringValueFunctionality)functionalityService.find(actor, LoadingServiceTestDatas.topDomainName, LoadingServiceTestDatas.TEST_TIME_STAMPING);
			configurationPolicy = funcEntity.getConfigurationPolicy();
			configurationPolicy.setStatus(false);
			configurationPolicy.setSystem(true);
			configurationPolicy.setPolicy(Policies.FORBIDDEN);
			try {
				// Should fail : you can not modify CP, your ancestor does not allowed it.
				functionalityService.update(actor,LoadingServiceTestDatas.topDomainName, funcEntity);
				Assert.assertFalse(true);
			} catch (BusinessException e) {
				Assert.assertFalse(false);
			}

			StringValueFunctionality funcDto = (StringValueFunctionality)functionalityService.find(actor, LoadingServiceTestDatas.topDomainName, LoadingServiceTestDatas.TEST_TIME_STAMPING);
			activationPolicy = funcDto.getActivationPolicy();
			activationPolicy.setStatus(true);
			activationPolicy.setSystem(false);
			activationPolicy.setPolicy(Policies.MANDATORY);

			functionalityService.update(actor, LoadingServiceTestDatas.topDomainName, funcDto);
		} catch (TechnicalException e){
			logger.error(e.toString());
			Assert.fail();
		}

		logger.info(LinShareTestConstants.END_TEST);
	}
}
