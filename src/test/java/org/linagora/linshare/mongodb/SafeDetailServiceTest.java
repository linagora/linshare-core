/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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
package org.linagora.linshare.mongodb;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AccountPermission;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.domain.entities.GuestDomain;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.TechnicalAccount;
import org.linagora.linshare.core.domain.entities.TechnicalAccountPermission;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.linagora.linshare.core.repository.InternalRepository;
import org.linagora.linshare.core.repository.TechnicalAccountPermissionRepository;
import org.linagora.linshare.core.repository.TechnicalAccountRepository;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.linagora.linshare.core.service.SafeDetailService;
import org.linagora.linshare.mongo.entities.SafeDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

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
		"classpath:springContext-mongo-java-server.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml" })
public class SafeDetailServiceTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private final static String THREAD_NAME = "threadName";
	private final static String EMAIL = "anakin.skywalker@int4.linshare.dev";
	private final static String CMIS_LOCALE = "cmis";
	private final static String DOMAIN_POLICY_UUID = "DefaultDomainPolicy";

	@Autowired
	private InternalRepository internalRepository;

	@Autowired
	private TechnicalAccountRepository technicalAccountRepository;

	@Autowired
	private SafeDetailService safeDetailService;

	@Autowired
	private ThreadRepository threadRepository;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	@Autowired
	private DomainPolicyRepository domainPolicyRepository;

	@Autowired
	private TechnicalAccountPermissionRepository technicalAccountPermissionRepository;

	private TechnicalAccountPermission technicalAccountPermission;

	private TechnicalAccount technicalAccount;

	private Internal actor;

	private WorkGroup workGroup;

	private AbstractDomain testDomain;

	private DomainPolicy defaultPolicy;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug("Begin setUp");
		defaultPolicy = domainPolicyRepository.findById(DOMAIN_POLICY_UUID);
		testDomain = createATestDomain();
		technicalAccountPermission = new TechnicalAccountPermission();
		AccountPermission ap1 = new AccountPermission(TechnicalAccountPermissionType.SAFE_DETAIL_CREATE);
		AccountPermission ap2 = new AccountPermission(TechnicalAccountPermissionType.SAFE_DETAIL_DELETE);
		AccountPermission ap3 = new AccountPermission(TechnicalAccountPermissionType.SAFE_DETAIL_LIST);
		AccountPermission ap4 = new AccountPermission(TechnicalAccountPermissionType.SAFE_DETAIL_GET);
		technicalAccountPermission.addPermission(ap1);
		technicalAccountPermission.addPermission(ap2);
		technicalAccountPermission.addPermission(ap3);
		technicalAccountPermission.addPermission(ap4);
		technicalAccountPermission = technicalAccountPermissionRepository.create(technicalAccountPermission);

		technicalAccount = new TechnicalAccount();
		technicalAccount.setRole(Role.DELEGATION);
		technicalAccount.setLocale(SupportedLanguage.ENGLISH);
		technicalAccount.setCmisLocale(CMIS_LOCALE);
		technicalAccount.setDomain(testDomain);
		technicalAccount.setPermission(technicalAccountPermission);
		technicalAccount = technicalAccountRepository.create(technicalAccount);

		actor = new Internal();
		actor.setMail(EMAIL);
		actor.setLocale(SupportedLanguage.ENGLISH);
		actor.setCmisLocale(CMIS_LOCALE);
		actor.setDomain(testDomain);
		actor = internalRepository.create(actor);

		workGroup = new WorkGroup();
		workGroup.setLocale(SupportedLanguage.ENGLISH);
		workGroup.setExternalMailLocale(Language.ENGLISH);
		workGroup.setCmisLocale(CMIS_LOCALE);
		workGroup.setDomain(testDomain);
		workGroup.setName(THREAD_NAME);
		workGroup = threadRepository.create(workGroup);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");
		technicalAccountRepository.delete(technicalAccount);
		internalRepository.delete(actor);
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
		SafeDetail safeDetail = new SafeDetail();
		safeDetail.setContainerUuid(workGroup.getLsUuid());
		SafeDetail exist = safeDetailService.create(technicalAccount, actor, safeDetail);
		Assertions.assertEquals(exist.getAccountUuid(), actor.getLsUuid());
		safeDetailService.delete(technicalAccount, actor, exist.getUuid());
	}
	
	@Test
	public void testDeleteSafeDetail() {
		SafeDetail safeDetail = new SafeDetail();
		safeDetail.setContainerUuid(workGroup.getLsUuid());
		SafeDetail exist = safeDetailService.create(technicalAccount, actor, safeDetail);
		safeDetailService.delete(technicalAccount, actor, exist.getUuid());
		Assertions.assertTrue(safeDetailService.findAll(technicalAccount, actor).isEmpty());
	}
	
	@Test
	public void testfindAll() {
		SafeDetail safeDetail = new SafeDetail();
		safeDetail.setContainerUuid(workGroup.getLsUuid());
		safeDetailService.create(technicalAccount, actor, safeDetail);
		List<SafeDetail> exist = safeDetailService.findAll(technicalAccount, actor);
		Assertions.assertEquals(exist.iterator().next().getAccountUuid(), actor.getLsUuid());
		safeDetailService.delete(technicalAccount, actor, exist.iterator().next().getUuid());
	}
}
