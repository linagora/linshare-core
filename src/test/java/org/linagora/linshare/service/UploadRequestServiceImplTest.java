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

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestHistory;
import org.linagora.linshare.core.domain.entities.UploadRequestTemplate;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.ContactRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.UploadRequestService;
import org.linagora.linshare.utils.LinShareWiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml", })
public class UploadRequestServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests {
	private static Logger logger = LoggerFactory.getLogger(UploadRequestServiceImplTest.class);

	private LinShareWiser wiser;

	public UploadRequestServiceImplTest() {
		super();
		wiser = new LinShareWiser(2525);
	}

	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;

	@Autowired
	private ContactRepository repository;

	@Autowired
	private UploadRequestService service;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	private UploadRequest ure = new UploadRequest();

	private UploadRequestTemplate template = new UploadRequestTemplate();

	private LoadingServiceTestDatas datas;

	private UploadRequest e;

	private UploadRequestTemplate temp;

	private User john;

	private User jane;

	private Contact yoda;

	@Before
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		this.executeSqlScript("import-tests-upload-request.sql", false);
		this.executeSqlScript("import-mails-hibernate3.sql", false);
		wiser.start();
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		john = datas.getUser1();
		jane = datas.getUser2();
		AbstractDomain subDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlSubDomain);
		yoda = repository.findByMail("yoda@linshare.org");
		john.setDomain(subDomain);
//		UPLOAD REQUEST CREATE
		ure.setAbstractDomain(john.getDomain());
		ure.setCanClose(true);
		ure.setMaxDepositSize((long) 100);
		ure.setMaxFileCount(new Integer(3));
		ure.setMaxFileSize((long) 50);
		ure.setStatus(UploadRequestStatus.STATUS_CREATED);
		ure.setExpiryDate(new Date());
		ure.setSecured(false);
		ure.setCanEditExpiryDate(true);
		ure.setCanDelete(true);
		ure.setLocale("en");
		List<UploadRequest> eList = Lists.newArrayList();
		eList = service.createRequest(john, john, ure, yoda, "This is a subject", "This is a body", false);
		e = eList.get(0);
//		END OF UPLOAD REQUEST CREATE
//		Set upload request template
		template.setDayBeforeNotification(new Long(10));
		template.setDepositMode(true);
		template.setDescription("This is a tempale");
		template.setName("templateName");
		template.setDurationBeforeActivation(new Long(10));
		template.setDurationBeforeExpiry(new Long(10));
		template.setUnitBeforeActivation(new Long(2));
		template.setUnitBeforeExpiry(new Long(3));
		template.setGroupMode(false);
		template.setMaxDepositSize(new Long(10));
		template.setMaxFile(new Long(3));
		template.setMaxFileSize(new Long(10));
		template.setLocale("fr");
		temp = service.createTemplate(john, john, template);
		Assert.assertEquals(john, (User) e.getOwner());
		Assert.assertEquals(john, (User) temp.getOwner());
		Assert.assertEquals(new Long(3), temp.getMaxFile());
		Assert.assertEquals(new Long(10), temp.getMaxFileSize());
		Assert.assertEquals(new Long(3), temp.getUnitBeforeExpiry());
		Assert.assertEquals(false, temp.getGroupMode());
		Assert.assertEquals("templateName", temp.getName());
		Assert.assertEquals(e.getStatus(), UploadRequestStatus.STATUS_ENABLED);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		wiser.stop();
		service.deleteTemplate(john, john, temp.getUuid());
		service.updateStatus(john, john, e.getUuid(), UploadRequestStatus.STATUS_CLOSED);
		service.updateStatus(john, john, e.getUuid(), UploadRequestStatus.STATUS_ARCHIVED);
		service.deleteRequest(john, john, e.getUuid());
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void findRequest() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequest tmp = service.findRequestByUuid(john, john, e.getUuid());
		Assert.assertEquals(tmp.getStatus(), e.getStatus());
		Assert.assertEquals(tmp.getOwner(), e.getOwner());
		Assert.assertEquals(tmp.getStatus(), e.getStatus());
		Assert.assertEquals(tmp.getMaxDepositSize(), e.getMaxDepositSize());
		Assert.assertEquals(tmp.getMaxFileCount(), e.getMaxFileCount());
		Assert.assertEquals(tmp.getMaxFileSize(), e.getMaxFileSize());
		Assert.assertEquals(tmp.isSecured(), e.isSecured());
		Assert.assertEquals(tmp.isCanClose(), e.isCanClose());
		Assert.assertEquals(tmp.isCanDelete(), e.isCanDelete());
		Assert.assertEquals(tmp.getAbstractDomain(), e.getAbstractDomain());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void update() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequest tmp = service.findRequestByUuid(john, john, e.getUuid());
		tmp.setCanClose(false);
		tmp.setCanDelete(false);
		tmp.setCanEditExpiryDate(false);
		tmp.setMaxFileCount(new Integer(2));
		tmp = service.update(john, john, tmp.getUuid(), tmp);
		Assert.assertEquals(tmp.isCanClose(), false);
		Assert.assertEquals(tmp.isCanDelete(), false);
		Assert.assertEquals(tmp.isCanEditExpiryDate(), false);
		Assert.assertEquals(tmp.getMaxFileCount(), new Integer(2));
		wiser.checkGeneratedMessages();
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void updateTemplate() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		template.setDayBeforeNotification(new Long(5));
		template.setDepositMode(false);
		template.setDescription("This is a tempale");
		template.setName("templateName");
		template.setDurationBeforeActivation(new Long(100));
		template.setDurationBeforeExpiry(new Long(100));
		template.setUnitBeforeActivation(new Long(2));
		template.setUnitBeforeExpiry(new Long(2));
		template.setGroupMode(true);
		template.setMaxDepositSize(new Long(100));
		template.setMaxFile(new Long(30));
		template.setMaxFileSize(new Long(100));
		template.setLocale("en");
		UploadRequestTemplate tmp = service.updateTemplate(john, john, temp.getUuid(), template);
		Assert.assertEquals(new Long(30), tmp.getMaxFile());
		Assert.assertEquals(new Long(100), tmp.getMaxFileSize());
		Assert.assertEquals(new Long(2), tmp.getUnitBeforeExpiry());
		Assert.assertEquals(true, tmp.getGroupMode());
		Assert.assertEquals("templateName", tmp.getName());
		wiser.checkGeneratedMessages();
	}

	@Test
	public void UpdateUploadRequestStatus() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequest tmp = e.clone();
		tmp = service.updateStatus(john, john, tmp.getUuid(), UploadRequestStatus.STATUS_CLOSED);
		Assert.assertEquals(tmp.getStatus(), UploadRequestStatus.STATUS_CLOSED);
		Assert.assertEquals(john, (User) e.getOwner());
		// Status ARCHIVED
		tmp = service.updateStatus(john, john, tmp.getUuid(), UploadRequestStatus.STATUS_ARCHIVED);
		Assert.assertEquals(tmp.getStatus(), UploadRequestStatus.STATUS_ARCHIVED);
		Assert.assertEquals(john, (User) e.getOwner());
		// STATUS DELETED
		tmp = service.updateStatus(john, john, tmp.getUuid(), UploadRequestStatus.STATUS_DELETED);
		Assert.assertEquals(tmp.getStatus(), UploadRequestStatus.STATUS_DELETED);
		Assert.assertEquals(john, (User) e.getOwner());
		wiser.checkGeneratedMessages();
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void closeRequestByRecipient() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequest tmp = e.clone();
		tmp = service.closeRequestByRecipient(e.getUploadRequestURLs().iterator().next());
		Assert.assertEquals(tmp.getStatus(), UploadRequestStatus.STATUS_CLOSED);
		Assert.assertEquals(john, (User) e.getOwner());
		wiser.checkGeneratedMessages();
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void updateStatusWithError() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		try {
			e.updateStatus(UploadRequestStatus.STATUS_CLOSED);
			UploadRequest tmp = service.updateRequest(john, john, e);
			tmp = service.findRequestByUuid(john, john, e.getUuid());
			tmp.updateStatus(UploadRequestStatus.STATUS_ENABLED);
			tmp = service.updateRequest(john, john, tmp);
		} catch (BusinessException ex) {
			Assert.assertEquals("Cannot transition from STATUS_CLOSED to STATUS_ENABLED.", ex.getMessage());
		}
		wiser.checkGeneratedMessages();
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void findAllRequestHistory() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Set<UploadRequestHistory> history = Sets.newHashSet();
		try {
			history = service.findAllRequestHistory(jane, john, e.getUuid());
		} catch (BusinessException ex) {
			Assert.assertEquals("You are not authorized to get this entry.", ex.getMessage());
		}
		history = service.findAllRequestHistory(john, john, e.getUuid());
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
