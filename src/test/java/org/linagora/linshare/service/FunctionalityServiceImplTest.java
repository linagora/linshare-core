/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.service;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.linagora.linshare.core.repository.RootUserRepository;
import org.linagora.linshare.core.service.FunctionalityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.collect.ImmutableList;

@ExtendWith(SpringExtension.class)
@Sql({
	"/import-tests-fake-domains.sql",
	"/import-tests-domain-quota-updates.sql"})
@Transactional
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-mongo-java-server.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-test.xml" })
@DirtiesContext
public class FunctionalityServiceImplTest {

	private static Logger logger = LoggerFactory.getLogger(FunctionalityServiceImplTest.class);

	private static int TOTAL_COUNT_FUNC = 10;

	private static String rootDomainName = "TEST_Domain-0";
	private static String topDomainName = "TEST_Domain-0-1";
//	private static String topDomainName2 = "TEST_Domain-0-2";
	private static String subDomainName1 = "TEST_Domain-0-1-1";
	private static String subDomainName2 = "TEST_Domain-0-1-2";

	public static String TEST_TIME_STAMPING="TEST_TIME_STAMPING";
	public static String FILESIZE_MAX="TEST_FILESIZE_MAX";
	public static String QUOTA_USER="TEST_QUOTA_USER";
	public static String QUOTA_GLOBAL="TEST_QUOTA_GLOBAL";
	public static String GUEST="GUEST";
	public static String FUNC1="TEST_FUNC1";
	public static String FUNC2="TEST_FUNC2";
	public static String FUNC3="TEST_FUNC3";
	public static String FUNC4="TEST_FUNC4";
	public static String FUNC5="TEST_FUNC5";


	@Autowired
	private FunctionalityService functionalityService;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	@Autowired
	private RootUserRepository rootUserRepository;

	private User actor;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		actor = rootUserRepository.findByLsUuid(LinShareTestConstants.ROOT_ACCOUNT);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	@DirtiesContext
	public void testGetAllFunctionalityForRootDomain() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		AbstractDomain domain = abstractDomainRepository.findById(rootDomainName);
		Assertions.assertNotNull(domain);

		Assertions.assertEquals(TOTAL_COUNT_FUNC,domain.getFunctionalities().size());
		Iterable<Functionality> list = functionalityService.findAll(actor, domain.getUuid());

		Assertions.assertNotNull(list);
		Assertions.assertEquals(TOTAL_COUNT_FUNC, ImmutableList.copyOf(list).size());
		logger.debug(LinShareTestConstants.END_TEST);

	}

	@Test
	@DirtiesContext
	public void testGetAllFunctionalityForTopDomain() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain domain = abstractDomainRepository.findById(topDomainName);
		Assertions.assertNotNull(domain);

		Assertions.assertEquals(2, domain.getFunctionalities().size());
		Iterable<Functionality> list = functionalityService.findAll(actor, domain.getUuid());

		Assertions.assertNotNull(list);
		Assertions.assertEquals(TOTAL_COUNT_FUNC - 2, ImmutableList.copyOf(list).size());
		logger.debug(LinShareTestConstants.END_TEST);

	}

	@Test
	@DirtiesContext
	public void testGetAllFunctionalityForSubDomain() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain domain = abstractDomainRepository.findById(subDomainName1);
		Assertions.assertNotNull(domain);
		Assertions.assertEquals(1,domain.getFunctionalities().size());
		Iterable<Functionality> list = functionalityService.findAll(actor, domain.getUuid());
		Assertions.assertNotNull(list);
		// There is two functionalities that can not be display
		Assertions.assertEquals(TOTAL_COUNT_FUNC -2, ImmutableList.copyOf(list).size());
		logger.debug(LinShareTestConstants.END_TEST);

	}

	@Test
	@DirtiesContext
	public void testgetFunctionality() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
//		AbstractDomain domain = abstractDomainRepository.findById(subDomainName1);
		String subdomain1 = subDomainName1;

		// The first functionality should be override by the current subdomain
		Functionality func1 = functionalityService.find(actor, subdomain1, FILESIZE_MAX);
		Assertions.assertNotNull(func1);
		Assertions.assertEquals(subdomain1, func1.getDomain().getUuid());

		// The second functionality should be override by the top domain
		Assertions.assertEquals(3, countFunctionality(rootDomainName, QUOTA_USER));
		Functionality func2_parent = functionalityService.find(actor, rootDomainName, QUOTA_USER);
		Functionality func2_child = functionalityService.find(actor, subdomain1, QUOTA_USER);
		Assertions.assertNotNull(func2_child);
		Assertions.assertNotSame(func2_parent, func2_child);
		Assertions.assertFalse(func2_parent.businessEquals(func2_child, true));

		// It should the same functionality for each domain.
		Functionality func3_root = functionalityService.find(actor, rootDomainName, TEST_TIME_STAMPING);
		Functionality func3_top = functionalityService.find(actor, topDomainName, TEST_TIME_STAMPING);
		Functionality func3_sub = functionalityService.find(actor, subdomain1, TEST_TIME_STAMPING);
		Assertions.assertNotNull(func3_root);
		Assertions.assertNotNull(func3_top);
		Assertions.assertNotNull(func3_top);
		Assertions.assertNotSame(func3_root, func3_top);
		Assertions.assertNotSame(func3_sub, func3_top);
		Assertions.assertNotSame(func3_root, func3_sub);
		Assertions.assertTrue(func3_sub.businessEquals(func3_root, true));
		Assertions.assertTrue(func3_sub.businessEquals(func3_top, true));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@DirtiesContext
	public void testUpdateUnitValueFunctionality1() throws BusinessException {
		// This test is designed to check a simple functionality modification.
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain domain = abstractDomainRepository.findById(subDomainName1);
		Assertions.assertNotNull(domain);

		UnitValueFunctionality func = (UnitValueFunctionality)functionalityService.find(actor, domain.getUuid(), FILESIZE_MAX);

		Assertions.assertEquals(50,func.getMaxValue().intValue());
		func.setMaxValue(25);
		functionalityService.update(actor, domain.getUuid(), func);

		domain = abstractDomainRepository.findById(subDomainName1);
		Assertions.assertEquals(1, domain.getFunctionalities().size());
		for (Functionality it : domain.getFunctionalities()) {
			Assertions.assertEquals(25, ((UnitValueFunctionality)it).getMaxValue().intValue());
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@DirtiesContext
	public void testUpdateUnitValueFunctionality2() throws BusinessException {
		// This test is designed to check a simple update without modification.
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain domain = abstractDomainRepository.findById(subDomainName1);
		Assertions.assertNotNull(domain);

		UnitValueFunctionality func = (UnitValueFunctionality)functionalityService.find(actor, domain.getUuid(), FILESIZE_MAX);

		Assertions.assertEquals(50,func.getMaxValue().intValue());
		functionalityService.update(actor, domain.getUuid(), func);

		domain = abstractDomainRepository.findById(subDomainName1);
		Assertions.assertEquals(1, domain.getFunctionalities().size());
		for (Functionality it : domain.getFunctionalities()) {
			Assertions.assertEquals(50, ((UnitValueFunctionality)it).getMaxValue().intValue());
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@DirtiesContext
	public void testUpdateUnitValueFunctionality2bis() throws BusinessException {
		// This test is designed to check a simple update without modification.
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain domain = abstractDomainRepository.findById(subDomainName1);
		Assertions.assertNotNull(domain);

		StringValueFunctionality func = (StringValueFunctionality)functionalityService.find(actor, domain.getUuid(), TEST_TIME_STAMPING);

		Assertions.assertTrue(func.getMaxValue().equals(LoadingServiceTestDatas.timeStampingUrl));
		functionalityService.update(actor, domain.getUuid(), func);

		domain = abstractDomainRepository.findById(subDomainName1);
		Assertions.assertEquals(1, domain.getFunctionalities().size());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@DirtiesContext
	public void testUpdateActivationPolicyFunctionality1() {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		String subDomain = subDomainName2;

		StringValueFunctionality func_child = (StringValueFunctionality)functionalityService.find(actor, subDomain, TEST_TIME_STAMPING);

		// modification of a functionality which belong to the root domain. This will lead to the creation of a new functionality
		func_child.setMaxValue("plop");
		functionalityService.update(actor, subDomain, func_child);

		String topDomain = rootDomainName;
		StringValueFunctionality func_parent = (StringValueFunctionality)functionalityService.find(actor, topDomain, TEST_TIME_STAMPING);
		// sub functionality will be reset.
		func_parent.getActivationPolicy().setPolicy(Policies.FORBIDDEN);
		functionalityService.update(actor, topDomain, func_parent);

		try {
			func_child = (StringValueFunctionality)functionalityService.find(actor, subDomain, TEST_TIME_STAMPING);
			Assertions.fail("should not be display");
		} catch (BusinessException e) {
			Assertions.assertTrue(true, "should be display");
		}

		// we should not be able to update a functionality in subdomain where parent do not allowed it.
		func_child = (StringValueFunctionality) func_parent.clone();
		func_child.setMaxValue("plop2");
		try {
			functionalityService.update(actor, subDomain, func_child);
			Assertions.fail("This message should not be seen. An BusinessException was expected.");
		} catch (BusinessException e) {
			Assertions.assertTrue(true);
		} catch (TechnicalException e) {
			if(!e.getErrorCode().equals(TechnicalErrorCode.FUNCTIONALITY_ENTITY_MODIFICATION_NOT_ALLOW)) {
				Assertions.fail("unkown error : " + e.getMessage());
				throw(e);
			}
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@DirtiesContext
	public void testUpdateActivationPolicyFunctionality4() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		AbstractDomain domain = abstractDomainRepository.findById(subDomainName2);
		Assertions.assertNotNull(domain);

		StringValueFunctionality func = (StringValueFunctionality)functionalityService.find(actor, domain.getUuid(), TEST_TIME_STAMPING);

		// modification of a functionality which belong to the root domain. This will lead to the creation of a new functionality
		func.setMaxValue("plop");
		functionalityService.update(actor, domain.getUuid(), func);

		StringValueFunctionality parent = (StringValueFunctionality)functionalityService.find(actor, topDomainName, TEST_TIME_STAMPING);
		parent.getActivationPolicy().setPolicy(Policies.FORBIDDEN);

		try {
			functionalityService.update(actor, domain.getUuid(), parent);
			Assertions.assertTrue(false);
		} catch (BusinessException e) {
			Assertions.assertTrue(true);
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@DirtiesContext
	public void testUpdateRootDomainFunctionality() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		AbstractDomain domain = abstractDomainRepository.findById(rootDomainName);
		Assertions.assertNotNull(domain);

		StringValueFunctionality func = (StringValueFunctionality)functionalityService.find(actor, domain.getUuid(), TEST_TIME_STAMPING);

		func.setMaxValue("plop");
		func.getActivationPolicy().setPolicy(Policies.FORBIDDEN);
		functionalityService.update(actor, domain.getUuid(), func);

		logger.debug(LinShareTestConstants.END_TEST);
	}

	private int countFunctionality(String domainId, String funcIdentifier) {
		// reload each time 
		AbstractDomain domain = abstractDomainRepository.findById(domainId);
		Assertions.assertNotNull(domain);
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

		List<AbstractDomain> subdomain = abstractDomainRepository.getSubDomainsByDomain(domain.getUuid());
		for (AbstractDomain abstractDomain : subdomain) {
			count += countFunctionality(abstractDomain, funcIdentifier);
		}
		return count;
	}

	// FIXME : Now we need LoggerParent because
	// we made some mapping modifications (enable lasy loading)
	// countFunctionality won't work now. Need another method to test test results.
	@Disabled
	@Test
	@DirtiesContext
	public void testUpdateActivationPolicyFunctionality3() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		String rootDomainId = rootDomainName;

		Assertions.assertEquals(1, countFunctionality(rootDomainId, TEST_TIME_STAMPING));

		// Step 1
		StringValueFunctionality func = (StringValueFunctionality)functionalityService.find(actor, topDomainName, TEST_TIME_STAMPING);

		func.getActivationPolicy().setStatus(false);
		// modification of a functionality which belong to the root domain. This will lead to the creation of a new functionality for top domain
		functionalityService.update(actor, topDomainName, func);
		Assertions.assertEquals(2, countFunctionality(rootDomainId, TEST_TIME_STAMPING));

		// Step 2
		StringValueFunctionality func2 = (StringValueFunctionality)functionalityService.find(actor, subDomainName1, TEST_TIME_STAMPING);
		func2.getActivationPolicy().setPolicy(Policies.FORBIDDEN);
		// modification of a functionality which belong to the top domain. This will lead to the creation of a new functionality for sub domain
		functionalityService.update(actor, subDomainName1, func2);
		Assertions.assertEquals(3, countFunctionality(rootDomainId, TEST_TIME_STAMPING));

		// Step 3
		StringValueFunctionality func3 = (StringValueFunctionality)functionalityService.find(actor, rootDomainId, TEST_TIME_STAMPING);
		func3.getActivationPolicy().setPolicy(Policies.FORBIDDEN);
		// root domain functionality is set with a forbidden policy, this should lead to the suppression of all functionalities above rootDomain
		functionalityService.update(actor, rootDomainId, func3);
		Assertions.assertEquals(1, countFunctionality(rootDomainId, TEST_TIME_STAMPING));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	// FIXME : Now we need LoggerParent because
	// we made some mapping modifications (enable lasy loading)
	// countFunctionality won't work now. Need another method to test test results.
	@Disabled
	@Test
	@DirtiesContext
	public void testUpdateConfigurationPolicyFunctionality1() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		String rootDomainId = rootDomainName;

		Assertions.assertEquals(1, countFunctionality(rootDomainId, TEST_TIME_STAMPING));

		// Step 1
		StringValueFunctionality func = (StringValueFunctionality)functionalityService.find(actor, topDomainName, TEST_TIME_STAMPING);

		func.getConfigurationPolicy().setStatus(false);
		// modification of a functionality which belong to the root domain. This will lead to the creation of a new functionality for top domain
		functionalityService.update(actor, topDomainName, func);
		Assertions.assertEquals(2, countFunctionality(rootDomainId, TEST_TIME_STAMPING));


		// Step 2
		StringValueFunctionality func2 = (StringValueFunctionality)functionalityService.find(actor, subDomainName1, TEST_TIME_STAMPING);
		func2.getConfigurationPolicy().setPolicy(Policies.FORBIDDEN);
		// modification of a functionality which belong to the top domain. This will lead to the creation of a new functionality for sub domain
		functionalityService.update(actor, subDomainName1, func2);
		Assertions.assertEquals(3, countFunctionality(rootDomainId, TEST_TIME_STAMPING));


		// Step 3
		StringValueFunctionality func3 = (StringValueFunctionality)functionalityService.find(actor, rootDomainId, TEST_TIME_STAMPING);
		func3.getConfigurationPolicy().setPolicy(Policies.MANDATORY);
		// root domain functionality is set with a mandatory policy, this should lead to the modification of all functionalities above rootDomain
		// func 2 : configuration policy will be updated form FORBIDDEN to MANDATORY
		functionalityService.update(actor, rootDomainId, func3);

		// first check
		Assertions.assertEquals(3, countFunctionality(rootDomainId, TEST_TIME_STAMPING));
		// second check : func 4 (equal to func 2) : configuration policy will be updated form FORBIDDEN to MANDATORY
		StringValueFunctionality func4 = (StringValueFunctionality)functionalityService.find(actor, subDomainName1, TEST_TIME_STAMPING);
		logger.debug("func4.getConfigurationPolicy().getPolicy() : " + func4.getConfigurationPolicy().getPolicy());
		Assertions.assertTrue(func4.getConfigurationPolicy().getPolicy().equals(Policies.MANDATORY));

		logger.debug(LinShareTestConstants.END_TEST);
	}

	// FIXME : Now we need LoggerParent because
	// we made some mapping modifications (enable lasy loading)
	// countFunctionality won't work now. Need another method to test test results.
	@Disabled
	@Test
	@DirtiesContext
	public void testUpdateConfigurationPolicyFunctionality2() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		String rootDomainId = rootDomainName;
		String valueFromTop="value from top";
		String valueFromSub="value from sub";

		Assertions.assertEquals(1, countFunctionality(rootDomainId, TEST_TIME_STAMPING));


		// Step 1
		StringValueFunctionality func = (StringValueFunctionality)functionalityService.find(actor, topDomainName, TEST_TIME_STAMPING);

		func.getConfigurationPolicy().setStatus(false);
		func.setMaxValue(valueFromTop);
		// modification of a functionality which belong to the root domain. This will lead to the creation of a new functionality for top domain
		functionalityService.update(actor, topDomainName, func);
		Assertions.assertEquals(2, countFunctionality(rootDomainId, TEST_TIME_STAMPING));


		// Step 2
		StringValueFunctionality func2 = (StringValueFunctionality)functionalityService.find(actor, subDomainName1, TEST_TIME_STAMPING);
		func2.getConfigurationPolicy().setPolicy(Policies.MANDATORY);
		func2.setMaxValue(valueFromSub);
		// modification of a functionality which belong to the top domain. This will lead to the creation of a new functionality for sub domain
		functionalityService.update(actor, subDomainName1, func2);
		Assertions.assertEquals(3, countFunctionality(rootDomainId, TEST_TIME_STAMPING));


		// Step 3
		StringValueFunctionality func3 = (StringValueFunctionality)functionalityService.find(actor, rootDomainId, TEST_TIME_STAMPING);
		Assertions.assertEquals(LoadingServiceTestDatas.timeStampingUrl, func3.getMaxValue());
		func3.getConfigurationPolicy().setPolicy(Policies.FORBIDDEN);

		// root domain functionality is set with a forbidden policy, this should lead to the modification of all functionalities above rootDomain
		functionalityService.update(actor, rootDomainId, func3);
		Assertions.assertEquals(3, countFunctionality(rootDomainId, TEST_TIME_STAMPING));


		// Step 4
		StringValueFunctionality func4 = (StringValueFunctionality)functionalityService.find(actor, subDomainName1, TEST_TIME_STAMPING);
		logger.debug("func4.getConfigurationPolicy().getPolicy() : " + func4.getConfigurationPolicy().getPolicy());
		Assertions.assertTrue(func4.getConfigurationPolicy().getPolicy().equals(Policies.FORBIDDEN));
		logger.debug("func4.getValue()" + func4.getMaxValue());
		Assertions.assertEquals(LoadingServiceTestDatas.timeStampingUrl, func4.getMaxValue());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	// FIXME : Now we need LoggerParent because
	// we made some mapping modifications (enable lasy loading)
	// countFunctionality won't work now. Need another method to test test results.
	@Disabled
	@Test
	@DirtiesContext
	public void testUpdateConfigurationPolicyFunctionality3() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		String rootDomainId = rootDomainName;

		Assertions.assertEquals(1, countFunctionality(rootDomainId, TEST_TIME_STAMPING));


		// Step 1
		StringValueFunctionality func = (StringValueFunctionality)functionalityService.find(actor, topDomainName, TEST_TIME_STAMPING);

		func.getConfigurationPolicy().setStatus(false);
		// modification of a functionality which belong to the root domain. This will lead to the creation of a new functionality for top domain
		functionalityService.update(actor, topDomainName, func);
		Assertions.assertEquals(2, countFunctionality(rootDomainId, TEST_TIME_STAMPING));


		// Step 2
		StringValueFunctionality func2 = (StringValueFunctionality)functionalityService.find(actor, subDomainName1, TEST_TIME_STAMPING);
		func2.getConfigurationPolicy().setPolicy(Policies.FORBIDDEN);
		// modification of a functionality which belong to the top domain. This will lead to the creation of a new functionality for sub domain
		functionalityService.update(actor, subDomainName1, func2);
		Assertions.assertEquals(3, countFunctionality(rootDomainId, TEST_TIME_STAMPING));


		// Step 3
		StringValueFunctionality func3 = (StringValueFunctionality)functionalityService.find(actor, rootDomainId, TEST_TIME_STAMPING);
		func3.getConfigurationPolicy().setPolicy(Policies.MANDATORY);
		// root domain functionality is set with a forbidden policy, this should lead to the suppression of all functionalities above rootDomain
		functionalityService.update(actor, rootDomainId, func3);
		Assertions.assertEquals(3, countFunctionality(rootDomainId, TEST_TIME_STAMPING));

		StringValueFunctionality func4 = (StringValueFunctionality)functionalityService.find(actor, subDomainName1, TEST_TIME_STAMPING);
		Assertions.assertTrue(func4.getConfigurationPolicy().getPolicy().equals(Policies.MANDATORY));

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
			StringValueFunctionality funcAncestor = (StringValueFunctionality)functionalityService.find(actor, rootDomainName, TEST_TIME_STAMPING);

			activationPolicy = funcAncestor.getActivationPolicy();
			activationPolicy.setStatus(true);
			activationPolicy.setSystem(false);
			activationPolicy.setPolicy(Policies.ALLOWED);

			configurationPolicy = funcAncestor.getConfigurationPolicy();
			configurationPolicy.setStatus(false);
			configurationPolicy.setSystem(true);
			configurationPolicy.setPolicy(Policies.FORBIDDEN);

			functionalityService.update(actor,rootDomainName, funcAncestor);

			StringValueFunctionality funcEntity = (StringValueFunctionality)functionalityService.find(actor, topDomainName, TEST_TIME_STAMPING);
			activationPolicy = funcEntity.getActivationPolicy();
			activationPolicy.setStatus(false);
			activationPolicy.setSystem(false);
			activationPolicy.setPolicy(Policies.FORBIDDEN);
			functionalityService.update(actor,topDomainName, funcEntity);

			funcEntity = (StringValueFunctionality)functionalityService.find(actor, topDomainName, TEST_TIME_STAMPING);
			configurationPolicy = funcEntity.getConfigurationPolicy();
			configurationPolicy.setStatus(false);
			configurationPolicy.setSystem(true);
			configurationPolicy.setPolicy(Policies.FORBIDDEN);
			try {
				// Should fail : you can not modify CP, your ancestor does not allowed it.
				functionalityService.update(actor,topDomainName, funcEntity);
				Assertions.assertFalse(true);
			} catch (BusinessException e) {
				Assertions.assertFalse(false);
			}

			StringValueFunctionality funcDto = (StringValueFunctionality)functionalityService.find(actor, topDomainName, TEST_TIME_STAMPING);
			activationPolicy = funcDto.getActivationPolicy();
			activationPolicy.setStatus(true);
			activationPolicy.setSystem(false);
			activationPolicy.setPolicy(Policies.MANDATORY);

			functionalityService.update(actor, topDomainName, funcDto);
		} catch (TechnicalException e){
			logger.error(e.toString());
			Assertions.fail();
		}

		logger.info(LinShareTestConstants.END_TEST);
	}
}
