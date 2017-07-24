/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.InternalRepository;
import org.linagora.linshare.core.repository.MailingListRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.InconsistentUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-startopendj.xml",
		"classpath:springContext-jackRabbit-mock.xml",
		"classpath:springContext-test.xml" })
public class InconsistentUserServiceImplTest extends
		AbstractTransactionalJUnit4SpringContextTests {

	private static Logger logger = LoggerFactory
			.getLogger(InconsistentUserServiceImplTest.class);

	@Qualifier("userRepository")
	@Autowired
	private UserRepository userRepository;
	
	@Autowired	
	private	MailingListRepository mailingListBusinessService;
	
	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	@Autowired
	private InconsistentUserService inconsistentUserService;
	
	private LoadingServiceTestDatas datas;
	
	public InconsistentUserServiceImplTest() {
		super();
	}
	
	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();		
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testChecklListMigrationDomain() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		AbstractDomain subDomain1 = abstractDomainRepository
				.findById(LoadingServiceTestDatas.subDomainName1);
			
		Account userRoot = datas.getRoot(); 				
		Internal user1 = (Internal)datas.getUser1();

		// Before update		
		Assert.assertEquals(mailingListBusinessService.findAll().size(), 3);
		Assert.assertEquals(mailingListBusinessService.findAllListWhereOwner(user1).size(), 2);
		Assert.assertEquals(mailingListBusinessService.findAllListWhereOwner(user1).get(0).getDomain().getPersistenceId(), 2);				
		
		// Update
		inconsistentUserService.updateDomain(userRoot, user1.getLsUuid(), subDomain1.getIdentifier());
				
		// After update
		Assert.assertEquals(mailingListBusinessService.findAllListWhereOwner(user1).get(0).getDomain().getIdentifier(), "TEST_Domain-0-1-1");
		Assert.assertEquals(mailingListBusinessService.findAllListWhereOwner(user1).get(0).getDomain().getPersistenceId(), 100004);
		Assert.assertEquals(mailingListBusinessService.findAllListWhereOwner(user1).get(1).getDomain().getPersistenceId(), 100004);
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
}

