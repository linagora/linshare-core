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

import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linShare.core.domain.constants.Policies;
import org.linagora.linShare.core.domain.entities.AbstractDomain;
import org.linagora.linShare.core.domain.entities.Functionality;
import org.linagora.linShare.core.domain.entities.StringValueFunctionality;
import org.linagora.linShare.core.domain.entities.UnitValueFunctionality;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;
import org.linagora.linShare.core.repository.AbstractDomainRepository;
import org.linagora.linShare.core.repository.DomainPolicyRepository;
import org.linagora.linShare.core.repository.FunctionalityRepository;
import org.linagora.linShare.core.service.FunctionalityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
public class FunctionalityServiceImplTest extends AbstractJUnit4SpringContextTests{
	
	
	private LoadingServiceTestDatas datas;
	
	private int TOTAL_COUNT_FUNC=LoadingServiceTestDatas.TOTAL_COUNT_FUNC;
	
	@Autowired
	private FunctionalityService functionalityService;
	
	@Autowired
	private FunctionalityRepository functionalityRepository;
	
	@Autowired
	private AbstractDomainRepository abstractDomainRepository;
	
	@Autowired
	private DomainPolicyRepository domainPolicyRepository;
	
	@Before
	public void setUp() throws Exception {
		logger.debug("Begin setUp");
		datas = new LoadingServiceTestDatas(functionalityRepository,abstractDomainRepository,domainPolicyRepository,null, null);
		datas.loadDatas();
		logger.debug("End setUp");
	}

	@After
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");
		datas.deleteDatas();
		logger.debug("End tearDown");
	}
	
	

	@Test
	public void testGetAllFunctionalityForRootDomain() {
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.rootDomainName);
		Assert.assertNotNull(domain);
		
		Assert.assertEquals(TOTAL_COUNT_FUNC,domain.getFunctionalities().size());
		List<Functionality> list = functionalityService.getAllFunctionalities(domain);
		
		Assert.assertNotNull(list);
		Assert.assertEquals(TOTAL_COUNT_FUNC, list.size());
	}
	
	@Test
	public void testGetAllFunctionalityForTopDomain() {
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.topDomainName);
		Assert.assertNotNull(domain);
		
		Assert.assertEquals(2,domain.getFunctionalities().size());
		List<Functionality> list = functionalityService.getAllFunctionalities(domain);
		
		Assert.assertNotNull(list);
		Assert.assertEquals(TOTAL_COUNT_FUNC, list.size());
	}
	
	@Test
	public void testGetAllFunctionalityForSubDomain() {
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.subDomainName1);
		Assert.assertNotNull(domain);
		
		Assert.assertEquals(1,domain.getFunctionalities().size());
		
		List<Functionality> list = functionalityService.getAllFunctionalities(domain);
		
		Assert.assertNotNull(list);
		Assert.assertEquals(TOTAL_COUNT_FUNC, list.size());
	}
	
	@Test
	public void testGetFunctionalityByIdentifiers() {
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.subDomainName1);
		Assert.assertNotNull(domain);
		
		// The first functionality should be override by the current subdomain
		Functionality fonc1 = functionalityService.getFunctionalityByIdentifiers(domain.getIdentifier(), LoadingServiceTestDatas.FILESIZE_MAX);
		Assert.assertNotNull(fonc1);
		Assert.assertEquals(domain.getIdentifier(), fonc1.getDomain().getIdentifier());
		
		// The second functionality should be override by the top domain
		Functionality fonc2 = functionalityService.getFunctionalityByIdentifiers(domain.getIdentifier(), LoadingServiceTestDatas.QUOTA_USER);
		Assert.assertNotNull(fonc2);
		Assert.assertNotSame(domain.getIdentifier(), fonc2.getDomain().getIdentifier());
		Assert.assertEquals(domain.getParentDomain().getIdentifier() , fonc2.getDomain().getIdentifier());
		
		// The third functionality should be override by the root domain
		Functionality fonc3 = functionalityService.getFunctionalityByIdentifiers(domain.getIdentifier(), LoadingServiceTestDatas.TEST_TIME_STAMPING);
		Assert.assertNotNull(fonc3);
		Assert.assertNotSame(domain.getIdentifier(), fonc3.getDomain().getIdentifier());
		Assert.assertNotSame(domain.getParentDomain().getIdentifier() , fonc3.getDomain().getIdentifier());
		Assert.assertEquals(domain.getParentDomain().getParentDomain().getIdentifier() , fonc3.getDomain().getIdentifier());
		
	}
	
	@Test
	public void testGetAllAvailableFunctionalities() {
		
		logger.debug("Begin testGetAllAvailableFunctionalities : ");
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.topDomainName2);
		Assert.assertNotNull(domain);
		
		List<Functionality> list  = functionalityService.getAllAvailableFunctionalities(domain);
		Assert.assertNotNull(list);
		logger.debug("Nb func : " + list.size());
		for (Functionality functionality : list) {
			logger.debug("func : " + functionality.toString());
		}
		
		Assert.assertEquals(datas.getAvailableFunctionalitiesForTopDomain2(), list.size());
		logger.debug("End testGetAllAvailableFunctionalities : ");
	}
	
	@Test
	public void testGetAllAlterableFunctionalities() {
		
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.topDomainName2);
		Assert.assertNotNull(domain);
		
		List<Functionality> list  = functionalityService.getAllAlterableFunctionalities(domain);
		Assert.assertNotNull(list);
		logger.debug("Nb func : " + list.size());
		Assert.assertEquals(datas.getAlterableFunctionalitiesForTopDomain2(), list.size());
	}
	
	@Test
	public void testGetAllEditableFunctionalities() {
		
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.topDomainName2);
		Assert.assertNotNull(domain);
		
		List<Functionality> list  = functionalityService.getAllEditableFunctionalities(domain);
		Assert.assertNotNull(list);
		logger.debug("Nb func : " + list.size());
		Assert.assertEquals(datas.getEditableFunctionalitiesForTopDomain2(), list.size());
	}
	
	
	
	@Test
	public void testUpdateUnitValueFunctionality1() throws BusinessException {
		// This test is designed to check a simple functionality modification.
		
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.subDomainName1);
		Assert.assertNotNull(domain);
		
		UnitValueFunctionality func = (UnitValueFunctionality)functionalityService.getFunctionalityByIdentifiers(domain.getIdentifier(), LoadingServiceTestDatas.FILESIZE_MAX);
		
		Assert.assertEquals(50,func.getValue().intValue());
		func.setValue(25);
		functionalityService.update(domain, func);
		
		domain = abstractDomainRepository.findById(LoadingServiceTestDatas.subDomainName1);
		Assert.assertEquals(1, domain.getFunctionalities().size());
		for (Functionality it : domain.getFunctionalities()) {
			Assert.assertEquals(25, ((UnitValueFunctionality)it).getValue().intValue());
		}
	}
	
	
	@Test
	public void testUpdateUnitValueFunctionality2() throws BusinessException {
		// This test is designed to check a simple update without modification.
		
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.subDomainName1);
		Assert.assertNotNull(domain);
		
		UnitValueFunctionality func = (UnitValueFunctionality)functionalityService.getFunctionalityByIdentifiers(domain.getIdentifier(), LoadingServiceTestDatas.FILESIZE_MAX);
		
		Assert.assertEquals(50,func.getValue().intValue());
		functionalityService.update(domain, func);
		
		domain = abstractDomainRepository.findById(LoadingServiceTestDatas.subDomainName1);
		Assert.assertEquals(1, domain.getFunctionalities().size());
		for (Functionality it : domain.getFunctionalities()) {
			Assert.assertEquals(50, ((UnitValueFunctionality)it).getValue().intValue());
		}
	}
	
	@Test
	public void testUpdateUnitValueFunctionality2bis() throws BusinessException {
		// This test is designed to check a simple update without modification.
		
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.subDomainName1);
		Assert.assertNotNull(domain);
		
		StringValueFunctionality func = (StringValueFunctionality)functionalityService.getFunctionalityByIdentifiers(domain.getIdentifier(), LoadingServiceTestDatas.TEST_TIME_STAMPING);
		
		Assert.assertTrue(func.getValue().equals(LoadingServiceTestDatas.timeStampingUrl));
		functionalityService.update(domain, func);
		
		domain = abstractDomainRepository.findById(LoadingServiceTestDatas.subDomainName1);
		Assert.assertEquals(1, domain.getFunctionalities().size());
	
	}
	
	
	
	@Test
	public void testUpdateUnitValueFunctionality3() throws BusinessException {
		// This test is designed to check when a functionality is set up with the same parameters as its ancestor, it should be deleted.
		
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.subDomainName2);
		Assert.assertNotNull(domain);
		Assert.assertEquals(1, domain.getFunctionalities().size());
		
		
		List<Functionality> list  = functionalityService.getAllEditableFunctionalities(domain);
		Assert.assertNotNull(list);
		Assert.assertEquals(datas.getEditableFunctionalitiesForTopDomain2(), list.size());
		
		
		UnitValueFunctionality func = (UnitValueFunctionality)functionalityService.getFunctionalityByIdentifiers(domain.getIdentifier(), LoadingServiceTestDatas.QUOTA_USER);
		
		Assert.assertFalse(func.getDomain().getIdentifier().equals(domain.getParentDomain().getIdentifier()));
		Assert.assertEquals(125,func.getValue().intValue());
		func.setValue(250);
		functionalityService.update(domain, func);
		
		list  = functionalityService.getAllEditableFunctionalities(domain);
		Assert.assertNotNull(list);
		Assert.assertEquals(datas.getEditableFunctionalitiesForTopDomain2(), list.size());
		
		domain = abstractDomainRepository.findById(LoadingServiceTestDatas.subDomainName2);
		Assert.assertNotNull(domain);
		
		Assert.assertEquals(0, domain.getFunctionalities().size());
		
		func = (UnitValueFunctionality)functionalityService.getFunctionalityByIdentifiers(domain.getIdentifier(), LoadingServiceTestDatas.QUOTA_USER);
		Assert.assertEquals(250,func.getValue().intValue());
		
		Assert.assertTrue(func.getDomain().getIdentifier().equals(domain.getParentDomain().getIdentifier()));
		
	}
	
	@Test
	public void testUpdateUnitValueFunctionality4() throws BusinessException {
		// This test is designed to check when we modify a functionality from its ancestor, it should create a new entity.
		
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.subDomainName2);
		Assert.assertNotNull(domain);
		
		
		List<Functionality> list  = functionalityService.getAllEditableFunctionalities(domain);
		Assert.assertNotNull(list);
		Assert.assertEquals(datas.getEditableFunctionalitiesForTopDomain2(), list.size());
		
		
		
		StringValueFunctionality func = (StringValueFunctionality)functionalityService.getFunctionalityByIdentifiers(domain.getIdentifier(), LoadingServiceTestDatas.TEST_TIME_STAMPING);
		
		Assert.assertFalse(func.getDomain().getIdentifier().equals(domain.getIdentifier()));
		Assert.assertTrue(func.getDomain().getIdentifier().equals(domain.getParentDomain().getParentDomain().getIdentifier()));
		Assert.assertTrue(func.getValue().equals(LoadingServiceTestDatas.timeStampingUrl));
		func.setValue("plop");
		functionalityService.update(domain, func);
		
		
		domain = abstractDomainRepository.findById(LoadingServiceTestDatas.subDomainName2);
		Assert.assertNotNull(domain);
		
		Assert.assertEquals(2, domain.getFunctionalities().size());
		
		StringValueFunctionality func2 = (StringValueFunctionality)functionalityService.getFunctionalityByIdentifiers(domain.getIdentifier(), LoadingServiceTestDatas.TEST_TIME_STAMPING);
		Assert.assertTrue(func2.getDomain().getIdentifier().equals(domain.getIdentifier()));
		Assert.assertTrue(func.getId() !=func2.getId());
	}
	
	
	
	@Test
	public void testUpdateActivationPolicyFunctionality1() throws BusinessException {
		logger.debug("Begin testUpdateActivationPolicyFunctionality1");
		
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.subDomainName2);
		Assert.assertNotNull(domain);
		
		StringValueFunctionality func = (StringValueFunctionality)functionalityService.getFunctionalityByIdentifiers(domain.getIdentifier(), LoadingServiceTestDatas.TEST_TIME_STAMPING);
		
		
		// modification of a functionality which belong to the root domain. This will lead to the creation of a new functionality
		func.setValue("plop");
		functionalityService.update(domain, func);
		
		// updating this new functionality without reloading it: should failed
		func.setValue("plop2");
		func.getActivationPolicy().setPolicy(Policies.FORBIDDEN);
		
		try {
			
			functionalityService.update(domain, func);
			Assert.fail("This message should not be seen. An BusinessException was expected.");
		} catch (TechnicalException e) {
			if(!e.getErrorCode().equals(TechnicalErrorCode.FUNCTIONALITY_ENTITY_MODIFICATION_NOT_ALLOW)) {
				Assert.fail("unkown error : " + e.getMessage());
				throw(e);
			}
		}
		
		logger.debug("End testUpdateActivationPolicyFunctionality1");
	}
	
	@Test
	public void testUpdateActivationPolicyFunctionality2() throws BusinessException {
		logger.debug("Begin testUpdateActivationPolicyFunctionality2");
		
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.subDomainName2);
		Assert.assertNotNull(domain);
		
		StringValueFunctionality func = (StringValueFunctionality)functionalityService.getFunctionalityByIdentifiers(domain.getIdentifier(), LoadingServiceTestDatas.TEST_TIME_STAMPING);
		
		// modification of a functionality which belong to the root domain. This will lead to the creation of a new functionality
		func.setValue("plop");
		functionalityService.update(domain, func);
		
		func = (StringValueFunctionality)functionalityService.getFunctionalityByIdentifiers(domain.getIdentifier(), LoadingServiceTestDatas.TEST_TIME_STAMPING);
		func.setValue("plop2");
		func.getActivationPolicy().setPolicy(Policies.FORBIDDEN);
		
		try {
			functionalityService.update(domain, func);
			Assert.fail("This message should not be seen. An technicalException was expected.");
		} catch (TechnicalException e) {
//			e.printStackTrace();
		}
		
		logger.debug("End testUpdateActivationPolicyFunctionality2");
	}
	
	@Test
	public void testUpdateRootDomainFunctionality() throws BusinessException {
		logger.debug("Begin testUpdateRootDomainFunctionality");
		
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.rootDomainName);
		Assert.assertNotNull(domain);
		
		StringValueFunctionality func = (StringValueFunctionality)functionalityService.getFunctionalityByIdentifiers(domain.getIdentifier(), LoadingServiceTestDatas.TEST_TIME_STAMPING);
		
		func.setValue("plop");
		func.getActivationPolicy().setPolicy(Policies.FORBIDDEN);
		functionalityService.update(domain, func);
		
		logger.debug("End testUpdateRootDomainFunctionality");
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
				logger.debug("countFunctionality:" + domain.getIdentifier());
				count++;
			}
			
		}
		
		Set<AbstractDomain> subdomain = domain.getSubdomain();
		for (AbstractDomain abstractDomain : subdomain) {
			count += countFunctionality(abstractDomain, funcIdentifier);
		}
		return count;
	}
	
	
	@Test
	public void testUpdateActivationPolicyFunctionality3() throws BusinessException {
		logger.debug("Begin testUpdateActivationPolicyFunctionality3");
		String rootDomainId = LoadingServiceTestDatas.rootDomainName;
		
		Assert.assertEquals(1, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));
		
		
		// Step 1
		StringValueFunctionality func = (StringValueFunctionality)functionalityService.getFunctionalityByIdentifiers(LoadingServiceTestDatas.topDomainName, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		
		func.getActivationPolicy().setStatus(false);
		// modification of a functionality which belong to the root domain. This will lead to the creation of a new functionality for top domain
		functionalityService.update(LoadingServiceTestDatas.topDomainName, func);
		Assert.assertEquals(2, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));
		
		
		// Step 2
		StringValueFunctionality func2 = (StringValueFunctionality)functionalityService.getFunctionalityByIdentifiers(LoadingServiceTestDatas.subDomainName1, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		func2.getActivationPolicy().setPolicy(Policies.FORBIDDEN);
		// modification of a functionality which belong to the top domain. This will lead to the creation of a new functionality for sub domain
		functionalityService.update(LoadingServiceTestDatas.subDomainName1, func2);
		Assert.assertEquals(3, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));
		
		
		// Step 3
		StringValueFunctionality func3 = (StringValueFunctionality)functionalityService.getFunctionalityByIdentifiers(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		func3.getActivationPolicy().setPolicy(Policies.FORBIDDEN);
		// root domain functionality is set with a forbidden policy, this should lead to the suppression of all functionalities above rootDomain
		functionalityService.update(rootDomainId, func3);
		Assert.assertEquals(1, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));
	
		
		logger.debug("End testUpdateActivationPolicyFunctionality3");
	}
	
	@Test
	public void testUpdateConfigurationPolicyFunctionality1() throws BusinessException {
		logger.debug("Begin testUpdateConfigurationPolicyFunctionality1");
		String rootDomainId = LoadingServiceTestDatas.rootDomainName;
		
		Assert.assertEquals(1, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));
		
		
		// Step 1
		StringValueFunctionality func = (StringValueFunctionality)functionalityService.getFunctionalityByIdentifiers(LoadingServiceTestDatas.topDomainName, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		
		func.getConfigurationPolicy().setStatus(false);
		// modification of a functionality which belong to the root domain. This will lead to the creation of a new functionality for top domain
		functionalityService.update(LoadingServiceTestDatas.topDomainName, func);
		Assert.assertEquals(2, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));
		
		
		// Step 2
		StringValueFunctionality func2 = (StringValueFunctionality)functionalityService.getFunctionalityByIdentifiers(LoadingServiceTestDatas.subDomainName1, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		func2.getConfigurationPolicy().setPolicy(Policies.FORBIDDEN);
		// modification of a functionality which belong to the top domain. This will lead to the creation of a new functionality for sub domain
		functionalityService.update(LoadingServiceTestDatas.subDomainName1, func2);
		Assert.assertEquals(3, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));
		
		
		// Step 3
		StringValueFunctionality func3 = (StringValueFunctionality)functionalityService.getFunctionalityByIdentifiers(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		func3.getConfigurationPolicy().setPolicy(Policies.MANDATORY);
		// root domain functionality is set with a mandatory policy, this should lead to the modification of all functionalities above rootDomain
		functionalityService.update(rootDomainId, func3);
		Assert.assertEquals(3, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));
		
	
		StringValueFunctionality func4 = (StringValueFunctionality)functionalityService.getFunctionalityByIdentifiers(LoadingServiceTestDatas.subDomainName1, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		logger.debug("func4.getConfigurationPolicy().getPolicy() : " + func4.getConfigurationPolicy().getPolicy());
		Assert.assertTrue(func4.getConfigurationPolicy().getPolicy().equals(Policies.MANDATORY));
		
		logger.debug("End testUpdateConfigurationPolicyFunctionality1");
	}
	
	
	@Test
	public void testUpdateConfigurationPolicyFunctionality2() throws BusinessException {
		logger.debug("Begin testUpdateConfigurationPolicyFunctionality2");
		String rootDomainId = LoadingServiceTestDatas.rootDomainName;
		String valueFromTop="value from top";
		String valueFromSub="value from sub";
		
		Assert.assertEquals(1, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));
		
		
		// Step 1
		StringValueFunctionality func = (StringValueFunctionality)functionalityService.getFunctionalityByIdentifiers(LoadingServiceTestDatas.topDomainName, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		
		func.getConfigurationPolicy().setStatus(false);
		func.setValue(valueFromTop);
		// modification of a functionality which belong to the root domain. This will lead to the creation of a new functionality for top domain
		functionalityService.update(LoadingServiceTestDatas.topDomainName, func);
		Assert.assertEquals(2, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));
		
		
		// Step 2
		StringValueFunctionality func2 = (StringValueFunctionality)functionalityService.getFunctionalityByIdentifiers(LoadingServiceTestDatas.subDomainName1, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		func2.getConfigurationPolicy().setPolicy(Policies.MANDATORY);
		func2.setValue(valueFromSub);
		// modification of a functionality which belong to the top domain. This will lead to the creation of a new functionality for sub domain
		functionalityService.update(LoadingServiceTestDatas.subDomainName1, func2);
		Assert.assertEquals(3, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));
		
		
		// Step 3
		StringValueFunctionality func3 = (StringValueFunctionality)functionalityService.getFunctionalityByIdentifiers(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		Assert.assertEquals(LoadingServiceTestDatas.timeStampingUrl, func3.getValue());
		func3.getConfigurationPolicy().setPolicy(Policies.FORBIDDEN);

		// root domain functionality is set with a forbidden policy, this should lead to the modification of all functionalities above rootDomain
		functionalityService.update(rootDomainId, func3);
		Assert.assertEquals(3, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));
	
		
		// Step 4
		StringValueFunctionality func4 = (StringValueFunctionality)functionalityService.getFunctionalityByIdentifiers(LoadingServiceTestDatas.subDomainName1, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		logger.debug("func4.getConfigurationPolicy().getPolicy() : " + func4.getConfigurationPolicy().getPolicy());
		Assert.assertTrue(func4.getConfigurationPolicy().getPolicy().equals(Policies.FORBIDDEN));
		logger.debug("func4.getValue()" + func4.getValue());
		Assert.assertEquals(LoadingServiceTestDatas.timeStampingUrl, func4.getValue());
		
		logger.debug("End testUpdateConfigurationPolicyFunctionality2");
	}
	
	@Test
	public void testUpdateConfigurationPolicyFunctionality3() throws BusinessException {
		logger.debug("Begin testUpdateConfigurationPolicyFunctionality3");
		String rootDomainId = LoadingServiceTestDatas.rootDomainName;
		
		Assert.assertEquals(1, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));
		
		
		// Step 1
		StringValueFunctionality func = (StringValueFunctionality)functionalityService.getFunctionalityByIdentifiers(LoadingServiceTestDatas.topDomainName, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		
		func.getConfigurationPolicy().setStatus(false);
		// modification of a functionality which belong to the root domain. This will lead to the creation of a new functionality for top domain
		functionalityService.update(LoadingServiceTestDatas.topDomainName, func);
		Assert.assertEquals(2, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));
		
		
		// Step 2
		StringValueFunctionality func2 = (StringValueFunctionality)functionalityService.getFunctionalityByIdentifiers(LoadingServiceTestDatas.subDomainName1, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		func2.getConfigurationPolicy().setPolicy(Policies.FORBIDDEN);
		// modification of a functionality which belong to the top domain. This will lead to the creation of a new functionality for sub domain
		functionalityService.update(LoadingServiceTestDatas.subDomainName1, func2);
		Assert.assertEquals(3, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));
		
		
		// Step 3
		StringValueFunctionality func3 = (StringValueFunctionality)functionalityService.getFunctionalityByIdentifiers(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		func3.getConfigurationPolicy().setPolicy(Policies.MANDATORY);
		// root domain functionality is set with a forbidden policy, this should lead to the suppression of all functionalities above rootDomain
		functionalityService.update(rootDomainId, func3);
		Assert.assertEquals(3, countFunctionality(rootDomainId, LoadingServiceTestDatas.TEST_TIME_STAMPING));
		
	
		StringValueFunctionality func4 = (StringValueFunctionality)functionalityService.getFunctionalityByIdentifiers(LoadingServiceTestDatas.subDomainName1, LoadingServiceTestDatas.TEST_TIME_STAMPING);
		Assert.assertTrue(func4.getConfigurationPolicy().getPolicy().equals(Policies.MANDATORY));
		
		logger.debug("End testUpdateConfigurationPolicyFunctionality3");
	}
	
}
