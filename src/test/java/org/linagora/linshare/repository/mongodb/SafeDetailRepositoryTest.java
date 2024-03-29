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
package org.linagora.linshare.repository.mongodb;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.domain.entities.GuestDomain;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.linagora.linshare.core.repository.InternalRepository;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.linagora.linshare.mongo.entities.SafeDetail;
import org.linagora.linshare.mongo.repository.SafeDetailMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml" })
public class SafeDetailRepositoryTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private final static String THREAD_NAME = "threadName";
	private final static String EMAIL1 = "anakin.skywalker@int4.linshare.dev";
	private final static String CMIS_LOCALE = "cmis";
	private final static String DESCRIPTION = "This is an optional description";
	private final static String DOMAIN_POLICY_UUID = "DefaultDomainPolicy";

	@Autowired
	private InternalRepository internalRepository;

	@Autowired
	private SafeDetailMongoRepository safeDetailMongoRepository;
	
	@Autowired
	private ThreadRepository threadRepository;
	
	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	@Autowired
	private DomainPolicyRepository domainPolicyRepository;
			
	private Internal internal;
	
	private WorkGroup workGroup;
	
	private AbstractDomain testDomain;

	private DomainPolicy defaultPolicy;
	
	@BeforeEach
	public void setUp() throws Exception {
		logger.debug("Begin setUp");
		defaultPolicy = domainPolicyRepository.findById(DOMAIN_POLICY_UUID);
		testDomain = createATestDomain();
		
		internal = new Internal();
		internal.setMail(EMAIL1);
		internal.setCmisLocale(CMIS_LOCALE);
		internal.setDomain(testDomain);
		internal = internalRepository.create(internal);
		

		workGroup = new WorkGroup();
		workGroup.setMailLocale(Language.ENGLISH);
		workGroup.setExternalMailLocale(Language.ENGLISH);
		workGroup.setCmisLocale(CMIS_LOCALE);
		workGroup.setDomain(testDomain);
		workGroup.setName(THREAD_NAME);
		workGroup = threadRepository.create(workGroup);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");
		internalRepository.delete(internal);
		abstractDomainRepository.delete(testDomain);
		logger.debug("End tearDown");
	}
	
	private AbstractDomain createATestDomain() throws BusinessException {
		AbstractDomain currentTopDomain = new GuestDomain("My Guest domain");
		currentTopDomain.setPolicy(defaultPolicy);
		abstractDomainRepository.create(currentTopDomain);
		logger.debug("Current TopDomain object: " + currentTopDomain.toString());
		return currentTopDomain;
	}

	@Test
	public void testCreateSafeDetail() {
		SafeDetail safeDetail = new SafeDetail(internal.getLsUuid(), workGroup.getLsUuid(), DESCRIPTION);
		safeDetail = safeDetailMongoRepository.insert(safeDetail);
		Assertions.assertEquals(safeDetail.getAccountUuid(), internal.getLsUuid());
		safeDetailMongoRepository.delete(safeDetail);
	}

	@Test
	public void testDeleteSafeDetail() {
		SafeDetail safeDetail = new SafeDetail(internal.getLsUuid(), workGroup.getLsUuid(), DESCRIPTION);
		safeDetail = safeDetailMongoRepository.insert(safeDetail);
		Assertions.assertEquals(safeDetail.getAccountUuid(), internal.getLsUuid());
		safeDetailMongoRepository.delete(safeDetail);
		Assertions.assertTrue(safeDetailMongoRepository.findByAccountUuid(internal.getLsUuid()).isEmpty());
	}
	
	@Test
	public void testfindAll() {
		SafeDetail safeDetail = new SafeDetail(internal.getLsUuid(), workGroup.getLsUuid(), DESCRIPTION);
		safeDetailMongoRepository.insert(safeDetail);
		List<SafeDetail> exist = safeDetailMongoRepository.findByAccountUuid(internal.getLsUuid());
		Assertions.assertEquals(exist.iterator().next().getAccountUuid() , internal.getLsUuid());
		safeDetailMongoRepository.delete(safeDetail);
	}
}
