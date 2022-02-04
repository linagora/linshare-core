/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
package org.linagora.linshare.repository.hibernate;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.FileSizeUnit;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.constants.TimeUnit;
import org.linagora.linshare.core.domain.constants.UnitType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainAccessPolicy;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.domain.entities.FileSizeUnitClass;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.IntegerValueFunctionality;
import org.linagora.linshare.core.domain.entities.Policy;
import org.linagora.linshare.core.domain.entities.RootDomain;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.TimeUnitClass;
import org.linagora.linshare.core.domain.entities.UnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DomainAccessPolicyRepository;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.linagora.linshare.core.repository.FunctionalityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations={
		"classpath:springContext-test.xml", 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class FunctionalityRepositoryImplTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FunctionalityRepository functionalityRepository;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	@Autowired
	private DomainPolicyRepository domainPolicyRepository;

	@Autowired
	private DomainAccessPolicyRepository domainAccessRepository;

	static private String ID_FONC_1="TEST_TIME_STAMPING";
	static private String ID_FONC_2="TEST_QUOTA_U";
	static private String ID_FONC_3="TEST_EXPIRATION";
	private static String rootDomaineName = "Domain0";
	private static String domainePolicyName0 = "TestAccessPolicy0";
	private static String domainePolicyName1 = "TestAccessPolicy1";

	private AbstractDomain currentDomain;
	private DomainPolicy defaultPolicy;



	@BeforeEach
	public void setUp() throws Exception {
		logger.debug("Begin setUp");
		DomainAccessPolicy domainAccessPolicy = new DomainAccessPolicy();
		domainAccessRepository.create(domainAccessPolicy);
		defaultPolicy = new DomainPolicy(domainePolicyName0, domainAccessPolicy );
		logger.debug("Current DomainPolicy : " + defaultPolicy.toString());
		domainPolicyRepository.create(defaultPolicy);

		currentDomain= new RootDomain("My root domain");
		currentDomain.setPolicy(defaultPolicy);
		abstractDomainRepository.create(currentDomain);
		// FIXME : override uuid with default root domain previous identifier
		currentDomain.setUuid(rootDomaineName);
		currentDomain = abstractDomainRepository.update(currentDomain);
		logger.debug("Current AbstractDomain object: " + currentDomain.toString());

		logger.debug("End setUp");
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");
		abstractDomainRepository.delete(currentDomain);
		domainPolicyRepository.delete(defaultPolicy);
		logger.debug("End tearDown");
	}

	@Test
	public void testCreateStringValueFunctionality() throws BusinessException{

		String value = "http://server/service";
		Functionality fonc = new StringValueFunctionality(ID_FONC_1,
						false,
						new Policy(Policies.ALLOWED, true),
						new Policy(Policies.ALLOWED, true),
						currentDomain,
						value);

		functionalityRepository.create(fonc);

		logger.debug("Current object: " + fonc.toString());

		StringValueFunctionality entityFonc = (StringValueFunctionality)functionalityRepository.findByDomain(currentDomain,ID_FONC_1);
		Assertions.assertTrue(entityFonc.getActivationPolicy().getStatus());
		Assertions.assertTrue(entityFonc.getConfigurationPolicy().getStatus());
		Assertions.assertEquals(rootDomaineName,entityFonc.getDomain().getUuid());
		Assertions.assertEquals(value,entityFonc.getValue());
		functionalityRepository.delete(fonc);
	}

	@Test
	public void testCreateFileSizeValueFunctionality() throws BusinessException{

		Integer value = 1024;
		Functionality fonc = new UnitValueFunctionality(ID_FONC_2,
				false,
				new Policy(Policies.ALLOWED, false),
				new Policy(Policies.ALLOWED, false),
				currentDomain,
				value,
				new FileSizeUnitClass(FileSizeUnit.GIGA)
				);

		functionalityRepository.create(fonc);

		logger.debug("Current object: " + fonc.toString());


		List<Functionality> a =functionalityRepository.findAll();
		logger.debug("a.size() : " + a.size());

		UnitValueFunctionality entityFonc = (UnitValueFunctionality)functionalityRepository.findByDomain(currentDomain,ID_FONC_2);
		Assertions.assertFalse(entityFonc.getActivationPolicy().getStatus());
		Assertions.assertFalse(entityFonc.getConfigurationPolicy().getStatus());
		Assertions.assertEquals(rootDomaineName,entityFonc.getDomain().getUuid());
		Assertions.assertEquals(value,entityFonc.getValue());
		Assertions.assertEquals(UnitType.SIZE, entityFonc.getUnit().getUnitType());
		Assertions.assertEquals(FileSizeUnit.GIGA, entityFonc.getUnit().getUnitValue());
	}

	@Test
	public void testCreateTimeValueFunctionality() throws BusinessException{

		Integer value = 1024;
		Functionality fonc = new UnitValueFunctionality(ID_FONC_2,
				false,
				new Policy(Policies.FORBIDDEN, true),
				new Policy(Policies.MANDATORY, false),
				currentDomain,
				value,
				new TimeUnitClass(TimeUnit.WEEK)
				);

		functionalityRepository.create(fonc);

		logger.debug("Current object: " + fonc.toString());

		UnitValueFunctionality entityFonc = (UnitValueFunctionality)functionalityRepository.findByDomain(currentDomain,ID_FONC_2);
		Assertions.assertFalse(entityFonc.getActivationPolicy().getStatus());
		Assertions.assertTrue(entityFonc.getConfigurationPolicy().getStatus());
		Assertions.assertEquals(rootDomaineName,entityFonc.getDomain().getUuid());
		Assertions.assertEquals(value,entityFonc.getValue());
		Assertions.assertEquals(UnitType.TIME, entityFonc.getUnit().getUnitType());
		Assertions.assertEquals(TimeUnit.WEEK, entityFonc.getUnit().getUnitValue());
	}



	@Test
	public void testCreateTwoUnitValueFunctionality() throws BusinessException{

		Integer value = 1024;
		Functionality fonc = new UnitValueFunctionality(ID_FONC_2,
				false,
				new Policy(Policies.ALLOWED, false),
				new Policy(Policies.ALLOWED, false),
				currentDomain,
				value,
				new FileSizeUnitClass(FileSizeUnit.GIGA)
				);

		functionalityRepository.create(fonc);

		logger.debug("Current object: " + fonc.toString());

		UnitValueFunctionality entityFonc = (UnitValueFunctionality)functionalityRepository.findByDomain(currentDomain,ID_FONC_2);
		Assertions.assertFalse(entityFonc.getActivationPolicy().getStatus());
		Assertions.assertFalse(entityFonc.getConfigurationPolicy().getStatus());
		Assertions.assertEquals(rootDomaineName,entityFonc.getDomain().getUuid());
		Assertions.assertEquals(value,entityFonc.getValue());
		Assertions.assertEquals(UnitType.SIZE, entityFonc.getUnit().getUnitType());
		Assertions.assertEquals(FileSizeUnit.GIGA, entityFonc.getUnit().getUnitValue());



		Integer value2 = 256;
		Functionality fonc2 = new UnitValueFunctionality(ID_FONC_3,
				false,
				new Policy(Policies.ALLOWED, false),
				new Policy(Policies.ALLOWED, false),
				currentDomain,
				value2,
				new TimeUnitClass(TimeUnit.WEEK)
				);

		functionalityRepository.create(fonc2);

		logger.debug("Current object: " + fonc2.toString());

		UnitValueFunctionality entityFonc2 = (UnitValueFunctionality)functionalityRepository.findByDomain(currentDomain,ID_FONC_3);
		Assertions.assertFalse(entityFonc2.getActivationPolicy().getStatus());
		Assertions.assertFalse(entityFonc2.getConfigurationPolicy().getStatus());
		Assertions.assertEquals(rootDomaineName,entityFonc2.getDomain().getUuid());
		Assertions.assertEquals(value2,entityFonc2.getValue());
		Assertions.assertEquals(UnitType.TIME, entityFonc2.getUnit().getUnitType());
		Assertions.assertEquals(TimeUnit.WEEK, entityFonc2.getUnit().getUnitValue());
	}





	@Test
	public void testCreateTwoStringValueFunctionality() throws BusinessException{

		String value = "http://server/service";
		Functionality fonc = new StringValueFunctionality(ID_FONC_1,
						false,
						new Policy(Policies.ALLOWED, true),
						new Policy(Policies.ALLOWED, true),
						currentDomain,
						value);

		functionalityRepository.create(fonc);

		logger.debug("Current object: " + fonc.toString());

		StringValueFunctionality entityFonc = (StringValueFunctionality)functionalityRepository.findByDomain(currentDomain, ID_FONC_1);
		Assertions.assertTrue(entityFonc.getActivationPolicy().getStatus());
		Assertions.assertTrue(entityFonc.getConfigurationPolicy().getStatus());
		Assertions.assertEquals(rootDomaineName,entityFonc.getDomain().getUuid());
		Assertions.assertEquals(value,entityFonc.getValue());




		String rootDomaineName2=rootDomaineName+"-0";
		AbstractDomain currentDomain2= new RootDomain("My root domain");

		DomainAccessPolicy domainAccessPolicy = new DomainAccessPolicy();
		domainAccessRepository.create(domainAccessPolicy);
		DomainPolicy policy = new DomainPolicy(domainePolicyName1, domainAccessPolicy);
		domainPolicyRepository.create(policy);
		currentDomain2.setPolicy(policy);


		abstractDomainRepository.create(currentDomain2);
		// FIXME : override uuid with domain previous identifier
		currentDomain2.setUuid("Domain0-0");
		currentDomain2 = abstractDomainRepository.update(currentDomain2);

		logger.debug("Current AbstractDomain object: " + currentDomain2.toString());

		String value2 = "http://server/service";
		Functionality fonc2 = new StringValueFunctionality(ID_FONC_1,
						false,
						new Policy(Policies.ALLOWED, true),
						new Policy(Policies.ALLOWED, true),
						currentDomain2,
						value2);

		functionalityRepository.create(fonc2);

		logger.debug("Current object: " + fonc2.toString());

		StringValueFunctionality entityFonc2 = (StringValueFunctionality)functionalityRepository.findByDomain(currentDomain2, ID_FONC_1);
		Assertions.assertTrue(entityFonc2.getActivationPolicy().getStatus());
		Assertions.assertTrue(entityFonc2.getConfigurationPolicy().getStatus());
		Assertions.assertEquals(rootDomaineName2,entityFonc2.getDomain().getUuid());
		Assertions.assertEquals(value2,entityFonc2.getValue());

		functionalityRepository.delete(fonc);
		functionalityRepository.delete(fonc2);
		abstractDomainRepository.delete(currentDomain2);
		domainPolicyRepository.delete(policy);
	}


	@Disabled //FIXME Should assert throw a DataIntegrityViolationException
	@Test
	public void testUnicityOfFunctionality() throws BusinessException{

		String value = "http://server/service";
		Functionality fonc = new StringValueFunctionality(ID_FONC_1,
						false,
						new Policy(Policies.ALLOWED, true),
						new Policy(Policies.ALLOWED, true),
						currentDomain,
						value);

		functionalityRepository.create(fonc);

		logger.debug("Current object: " + fonc.toString());

		StringValueFunctionality entityFonc = (StringValueFunctionality)functionalityRepository.findByDomain(currentDomain, ID_FONC_1);
		Assertions.assertTrue(entityFonc.getActivationPolicy().getStatus());
		Assertions.assertTrue(entityFonc.getConfigurationPolicy().getStatus());
		Assertions.assertEquals(rootDomaineName,entityFonc.getDomain().getUuid());
		Assertions.assertEquals(value,entityFonc.getValue());




		String value2 = "http://server/service";
		Functionality fonc2 = new StringValueFunctionality(ID_FONC_1,
						false,
						new Policy(Policies.ALLOWED, true),
						new Policy(Policies.ALLOWED, true),
						currentDomain,
						value2);

		// it should throw a DataIntegrityViolationException
		functionalityRepository.create(fonc2);
		functionalityRepository.delete(fonc);
	}/**/

	@Test
	public void testEqualFunctionality() throws BusinessException{

		Functionality fonc = new Functionality(ID_FONC_1,
						false,
						new Policy(Policies.ALLOWED, true),
						new Policy(Policies.ALLOWED, true),
						currentDomain);


		Functionality fonc2 = new Functionality(ID_FONC_1,
						false,
						new Policy(Policies.ALLOWED, true),
						new Policy(Policies.ALLOWED, true),
						currentDomain);

		Assertions.assertTrue(fonc.businessEquals(fonc2, true));
		fonc2.setSystem(true);
		Assertions.assertFalse(fonc.businessEquals(fonc2, true));

		fonc.setSystem(true);
		fonc2.setIdentifier(ID_FONC_2);
		Assertions.assertFalse(fonc.businessEquals(fonc2, true));
	}

	@Test
	public void testEqualStringValueFunctionality() throws BusinessException{

		String value = "http://server/service";
		StringValueFunctionality fonc = new StringValueFunctionality(ID_FONC_1,
				false,
				new Policy(Policies.ALLOWED, true),
				new Policy(Policies.ALLOWED, true),
				currentDomain,
				value);


		StringValueFunctionality fonc2 = new StringValueFunctionality(ID_FONC_1,
				false,
				new Policy(Policies.ALLOWED, true),
				new Policy(Policies.ALLOWED, true),
				currentDomain,
				value);

		Assertions.assertTrue(fonc.businessEquals(fonc2, true));
		String value2 = "http://server/service2";
		fonc2.setValue(value2);
		Assertions.assertFalse(fonc.businessEquals(fonc2, true));
	}

	@Test
	public void testEqualIntegerValueFunctionality() throws BusinessException{

		Integer value=8;
		Integer maxValue=0;
		Boolean valueUsed = true;
		Boolean maxValueUsed = true;
		IntegerValueFunctionality fonc = new IntegerValueFunctionality(ID_FONC_1,
				false,
				new Policy(Policies.ALLOWED, true),
				new Policy(Policies.ALLOWED, true),
				currentDomain,
				value,
				maxValue,
				valueUsed,
				maxValueUsed);

		IntegerValueFunctionality fonc2 = new IntegerValueFunctionality(ID_FONC_1,
				false,
				new Policy(Policies.ALLOWED, true),
				new Policy(Policies.ALLOWED, true),
				currentDomain,
				value,
				maxValue,
				valueUsed,
				maxValueUsed);
		Assertions.assertTrue(fonc.businessEquals(fonc2, true));
		fonc2.setMaxValue(2);
		Assertions.assertFalse(fonc.businessEquals(fonc2, true));
	}
	
	/**
	 * For API Admin v1 maxValue is null
	 * @throws BusinessException
	 */
	@Test
	public void testEqualIntegerValueFunctionalityNullMaxValue() throws BusinessException{
		Integer value = 8;
		Boolean valueUsed = true;
		Boolean maxValueUsed = true;
		IntegerValueFunctionality integerValueFunctionality = new IntegerValueFunctionality(ID_FONC_1,
				false,
				new Policy(Policies.ALLOWED, true),
				new Policy(Policies.ALLOWED, true),
				currentDomain,
				value,
				null,
				valueUsed,
				maxValueUsed);

		IntegerValueFunctionality integerValueFunctionality_2 = new IntegerValueFunctionality(ID_FONC_1,
				false,
				new Policy(Policies.ALLOWED, true),
				new Policy(Policies.ALLOWED, true),
				currentDomain,
				value,
				null,
				valueUsed,
				maxValueUsed);
		Assertions.assertTrue(integerValueFunctionality.businessEquals(integerValueFunctionality_2, true));
		integerValueFunctionality_2.setValue(2);
		Assertions.assertFalse(integerValueFunctionality.businessEquals(integerValueFunctionality_2, true), "Expected to be not Equal");
	}

	@Test
	public void testEqualIntegerValueFunctionalityNullMaxValueAndObjectMaxValueNotNull() throws BusinessException{
		Integer value = 8;
		Integer maxValue=10;
		Boolean valueUsed = true;
		Boolean maxValueUsed = true;
		IntegerValueFunctionality integerValueFunctionality = new IntegerValueFunctionality(ID_FONC_1,
				false,
				new Policy(Policies.ALLOWED, true),
				new Policy(Policies.ALLOWED, true),
				currentDomain,
				value,
				null,
				valueUsed,
				maxValueUsed);

		IntegerValueFunctionality integerValueFunctionality_2 = new IntegerValueFunctionality(ID_FONC_1,
				false,
				new Policy(Policies.ALLOWED, true),
				new Policy(Policies.ALLOWED, true),
				currentDomain,
				value,
				null,
				valueUsed,
				maxValueUsed);
		Assertions.assertTrue(integerValueFunctionality.businessEquals(integerValueFunctionality_2, true));
		integerValueFunctionality_2.setMaxValue(maxValue);
		Assertions.assertFalse(integerValueFunctionality.businessEquals(integerValueFunctionality_2, true), "Expected to be not Equal");
	}

	@Test
	public void testEqualUnitValueFunctionality1() throws BusinessException{
		
		Integer value = 1024;
		Integer maxValue = 2048;
		UnitValueFunctionality fonc = new UnitValueFunctionality(ID_FONC_2,
				false,
				new Policy(Policies.ALLOWED, false),
				new Policy(Policies.ALLOWED, false),
				currentDomain,
				value,
				new FileSizeUnitClass(FileSizeUnit.GIGA),
				new FileSizeUnitClass(FileSizeUnit.GIGA),
				maxValue,
				true,
				true);
		
		UnitValueFunctionality fonc2 = new UnitValueFunctionality(ID_FONC_2,
				false,
				new Policy(Policies.ALLOWED, false),
				new Policy(Policies.ALLOWED, false),
				currentDomain,
				value,
				new FileSizeUnitClass(FileSizeUnit.GIGA),
				new FileSizeUnitClass(FileSizeUnit.GIGA),
				maxValue,
				true,
				true);
		
		Assertions.assertTrue(fonc.businessEquals(fonc2, true));
		fonc2.setMaxValue(8);
		Assertions.assertFalse(fonc.businessEquals(fonc2, true));
	}
	/**
	 * For API Admin v1 maxValue and maxUnit are null
	 * @throws BusinessException
	 */
	@Test
	public void testEqualUnitValueFunctionality1NullMaxValueMaxUnit() throws BusinessException{
		Integer value = 1024;
		UnitValueFunctionality unitValueFunctionality = new UnitValueFunctionality(ID_FONC_2,
				false,
				new Policy(Policies.ALLOWED, false),
				new Policy(Policies.ALLOWED, false),
				currentDomain,
				value,
				new FileSizeUnitClass(FileSizeUnit.GIGA),
				null,
				null,
				true,
				true);

		UnitValueFunctionality unitValueFunctionality_2 = new UnitValueFunctionality(ID_FONC_2,
				false,
				new Policy(Policies.ALLOWED, false),
				new Policy(Policies.ALLOWED, false),
				currentDomain,
				value,
				new FileSizeUnitClass(FileSizeUnit.GIGA),
				null,
				null,
				true,
				true);

		Assertions.assertTrue(unitValueFunctionality.businessEquals(unitValueFunctionality_2, true));
		unitValueFunctionality_2.setValue(8);
		Assertions.assertFalse(unitValueFunctionality.businessEquals(unitValueFunctionality_2, true));
	}

	@Test
	public void testEqualUnitValueFunctionality2() throws BusinessException{

		Integer value = 1024;
		Integer maxValue = 2048;
		UnitValueFunctionality fonc = new UnitValueFunctionality(ID_FONC_2,
				false,
				new Policy(Policies.ALLOWED, false),
				new Policy(Policies.ALLOWED, false),
				currentDomain,
				value,
				new FileSizeUnitClass(FileSizeUnit.GIGA),
				new FileSizeUnitClass(FileSizeUnit.GIGA),
				maxValue,
				true,
				true);

		UnitValueFunctionality fonc2 = new UnitValueFunctionality(ID_FONC_2,
				false,
				new Policy(Policies.ALLOWED, false),
				new Policy(Policies.ALLOWED, false),
				currentDomain,
				value,
				new TimeUnitClass(TimeUnit.WEEK),
				new TimeUnitClass(TimeUnit.WEEK),
				maxValue,
				true,
				true);

		UnitValueFunctionality fonc3 = new UnitValueFunctionality(ID_FONC_2,
				false,
				new Policy(Policies.ALLOWED, false),
				new Policy(Policies.ALLOWED, false),
				currentDomain,
				value,
				new TimeUnitClass(TimeUnit.DAY),
				new TimeUnitClass(TimeUnit.DAY),
				maxValue,
				true,
				true);

		Assertions.assertFalse(fonc.businessEquals(fonc2, true));
		Assertions.assertFalse(fonc2.businessEquals(fonc3, true));
	}


	@Test
	public void testCloneFunctionality() throws BusinessException{

		Functionality func = new Functionality(ID_FONC_1,
				false,
				new Policy(Policies.ALLOWED, true),
				new Policy(Policies.ALLOWED, true),
				currentDomain);

		Functionality newFunc = (Functionality) func.clone();
		Assertions.assertTrue(newFunc.businessEquals(func, true));

		func.setSystem(true);
		Assertions.assertFalse(newFunc.businessEquals(func, true));
	}

	// FIXME : Now we need LoggerParent because
	// we made some mapping modifications (enable lasy loading)
	@Disabled
	@Test
	public void testCloneStringFunctionality() throws BusinessException{
		AbstractDomain otherDomain= new RootDomain("My root domain");
		DomainAccessPolicy domainAccessPolicy = new DomainAccessPolicy();
		domainAccessRepository.create(domainAccessPolicy);
		DomainPolicy policy = new DomainPolicy(domainePolicyName1, domainAccessPolicy);
		domainPolicyRepository.create(policy);
		otherDomain.setPolicy(policy);

		abstractDomainRepository.create(otherDomain);
		logger.debug("otherDomain object: " + otherDomain.toString());


		String value = "http://server/service";
		StringValueFunctionality func = new StringValueFunctionality(ID_FONC_1,
				false,
				new Policy(Policies.ALLOWED, true),
				new Policy(Policies.ALLOWED, true),
				currentDomain,
				value);

		functionalityRepository.create(func);
		logger.debug("Current object func : " + func.toString());


		Functionality newFunc = (Functionality) func.clone();
		newFunc.setDomain(otherDomain);

		functionalityRepository.create(newFunc);
		logger.debug("Current object newFunc : " + newFunc.toString());


		Assertions.assertTrue(newFunc.businessEquals(func, true));
		Assertions.assertNotNull(newFunc.getDomain());

		func.setValue("plop");
		Assertions.assertFalse(newFunc.businessEquals(func, true));

		abstractDomainRepository.delete(otherDomain);
		domainPolicyRepository.delete(policy);
	}

	// we made some mapping modifications (enable lasy loading)
	@Disabled
	@Test
	public void testCloneUnitValueFunctionality() throws BusinessException{
		AbstractDomain otherDomain= new RootDomain("My root domain");
		DomainAccessPolicy domainAccessPolicy = new DomainAccessPolicy();
		domainAccessRepository.create(domainAccessPolicy);
		DomainPolicy policy = new DomainPolicy(domainePolicyName1, domainAccessPolicy);
		domainPolicyRepository.create(policy);
		otherDomain.setPolicy(policy);

		abstractDomainRepository.create(otherDomain);
		logger.debug("otherDomain object: " + otherDomain.toString());


		Integer value = 1024;
		UnitValueFunctionality func = new UnitValueFunctionality(ID_FONC_2,
				false,
				new Policy(Policies.ALLOWED, false),
				new Policy(Policies.ALLOWED, false),
				currentDomain,
				value,
				new FileSizeUnitClass(FileSizeUnit.GIGA)
				);

		functionalityRepository.create(func);
		logger.debug("Current object func : " + func.toString());


		Functionality newFunc = (Functionality) func.clone();
		newFunc.setDomain(otherDomain);

		functionalityRepository.create(newFunc);
		logger.debug("Current object newFunc : " + newFunc.toString());


		Assertions.assertTrue(newFunc.businessEquals(func, true));
		Assertions.assertNotNull(newFunc.getDomain());

		func.setMaxValue(256);
		Assertions.assertFalse(newFunc.businessEquals(func, true));
		Assertions.assertTrue(newFunc instanceof UnitValueFunctionality);


		abstractDomainRepository.delete(otherDomain);
		domainPolicyRepository.delete(policy);
	}
}
