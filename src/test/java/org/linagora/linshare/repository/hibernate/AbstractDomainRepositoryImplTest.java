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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.DomainPurgeStepEnum;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainAccessPolicy;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.domain.entities.RootDomain;
import org.linagora.linshare.core.domain.entities.SubDomain;
import org.linagora.linshare.core.domain.entities.TopDomain;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DomainAccessPolicyRepository;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@Sql({ "/import-tests-fake-domains.sql" })
@ContextConfiguration(locations={
		"classpath:springContext-test.xml", 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
@Transactional
@DirtiesContext
public class AbstractDomainRepositoryImplTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private static String rootDomainName = "Domain0";
	private static String topDomainName = "Domain0.1";
	private static String domainePolicyName0 = "TestAccessPolicy0";
	private static String existingSubDomain = "MySubDomain";
	private static String existingTopDomain = "MyDomain";
	
	@Autowired
	private AbstractDomainRepository abstractDomainRepository;


	@Autowired
	private DomainPolicyRepository domainPolicyRepository;

	@Autowired
	private DomainAccessPolicyRepository domainAccessRepository;

	private DomainPolicy defaultPolicy;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug("Begin setUp");
		DomainAccessPolicy domainAccessPolicy = new DomainAccessPolicy();
		domainAccessRepository.create(domainAccessPolicy);
		defaultPolicy = new DomainPolicy(domainePolicyName0, domainAccessPolicy );
		domainPolicyRepository.create(defaultPolicy);
		logger.debug("Current DomainPolicy : " + defaultPolicy.toString());
		logger.debug("End setUp");
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");
		domainPolicyRepository.delete(defaultPolicy);
		logger.debug("End tearDown");
	}

	private AbstractDomain createATestRootDomain() throws BusinessException {
		AbstractDomain currentDomain= new RootDomain("My root domain");
		currentDomain.setPolicy(defaultPolicy);
		abstractDomainRepository.create(currentDomain);
		logger.debug("Current AbstractDomain object: " + currentDomain.toString());
		return currentDomain;
	}

	private AbstractDomain createATestTopDomain(AbstractDomain rootDomain) throws BusinessException {
		AbstractDomain currentTopDomain = new TopDomain("My top domain",(RootDomain)rootDomain);

		currentTopDomain.setPolicy(defaultPolicy);

		abstractDomainRepository.create(currentTopDomain);
		logger.debug("Current TopDomain object: " + currentTopDomain.toString());
		return currentTopDomain;
	}

	@Disabled // FIXME Domains use uuid now, not identifier.
	@Test
	public void testRootDomainCreation() throws BusinessException{
		logger.debug("Begin testRootDomainCreation");

		AbstractDomain currentDomain = createATestRootDomain();

		Assertions.assertNotNull(abstractDomainRepository.findAll());
		AbstractDomain entityDomain = abstractDomainRepository.findById(rootDomainName);
		Assertions.assertNotNull(entityDomain);
		Assertions.assertNull(entityDomain.getParentDomain());
		Assertions.assertTrue(entityDomain instanceof RootDomain);
		Assertions.assertFalse(entityDomain instanceof TopDomain);
		Assertions.assertFalse(entityDomain instanceof SubDomain);
		Assertions.assertTrue(entityDomain.isEnable());
		Assertions.assertFalse(entityDomain.isTemplate());

		abstractDomainRepository.delete(currentDomain);
		logger.debug("End testRootDomainCreation");
	}

	@Disabled // FIXME Domains use uuid now, not identifier.
	@Test
	public void testTopDomainCreation() throws BusinessException{
		logger.debug("Begin testTopDomainCreation");
		AbstractDomain rootDomain = createATestRootDomain();
		AbstractDomain currentTopDomain = createATestTopDomain(rootDomain);

		rootDomain.addSubdomain(currentTopDomain);
		abstractDomainRepository.update(rootDomain);
		logger.debug("my parent is  : " + currentTopDomain.getParentDomain().toString());

		Assertions.assertNotNull(abstractDomainRepository.findAll());
		logger.debug("abstractDomainRepository.findAll().size():" + abstractDomainRepository.findAll().size());
		Assertions.assertNotNull(abstractDomainRepository.findById(rootDomainName));
		AbstractDomain entityRootDomain = abstractDomainRepository.findById(rootDomainName);

		List<AbstractDomain> subDomainList = new ArrayList<AbstractDomain>();
		subDomainList.addAll(abstractDomainRepository.getSubDomainsByDomain(entityRootDomain.getUuid()));
		logger.debug(entityRootDomain.getUuid() + " : my son is : " + subDomainList.get(0).getUuid());
		Assertions.assertEquals(topDomainName, subDomainList.get(0).getUuid());

		logger.debug("End testTopDomainCreation");
	}

	@Test
	public void testFindAllTopDomain() throws BusinessException{
		List<AbstractDomain> list = abstractDomainRepository.findAllTopDomain();
		// one by default + 2 topdomain for tests.
		Assertions.assertEquals(4, list.size());
	}

	@Test
	public void testFindAllSubDomain() throws BusinessException{
		List<AbstractDomain> list = abstractDomainRepository.findAllSubDomain();
		Assertions.assertNotNull(list.size());
	}

	@Test
	public void testFindSubDomain() throws BusinessException{
		List<String> list = abstractDomainRepository.getAllSubDomainIdentifiers("LinShareRootDomain");
		Assertions.assertEquals(2, list.size());
	}
	
	@Test
	public void testFindSubDomainsByDomain() throws BusinessException{
		AbstractDomain subDomain = abstractDomainRepository.findById(existingSubDomain);
		subDomain.setPurgeStep(DomainPurgeStepEnum.WAIT_FOR_PURGE);
		abstractDomainRepository.update(subDomain);
		List<AbstractDomain> list = abstractDomainRepository.getSubDomainsByDomain(existingTopDomain);
		Assertions.assertEquals(1, list.size());
	}
	
	@Test
	public void testFindGuestDomainByDomain() throws BusinessException {
		AbstractDomain topDomain = abstractDomainRepository.findById(existingTopDomain);
		AbstractDomain guestDomain = abstractDomainRepository.getGuestSubDomainByDomain(topDomain.getUuid());
		Assertions.assertNotNull(guestDomain);
	}
}
