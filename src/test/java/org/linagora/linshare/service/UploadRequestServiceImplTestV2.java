/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.ContactRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.UploadRequestGroupService;
import org.linagora.linshare.core.service.UploadRequestService;
import org.linagora.linshare.utils.LinShareWiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.google.common.collect.Lists;

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
public class UploadRequestServiceImplTestV2 extends AbstractTransactionalJUnit4SpringContextTests {
	private static Logger logger = LoggerFactory.getLogger(UploadRequestServiceImplTestV2.class);

	private LinShareWiser wiser;

	public UploadRequestServiceImplTestV2() {
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
	private UploadRequestGroupService uploadRequestGroupService;
	
	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	private UploadRequest ure = new UploadRequest();

	private LoadingServiceTestDatas datas;

	private UploadRequest uploadRequest;

	private User john;

	private Contact yoda;

	@Before
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		this.executeSqlScript("import-tests-upload-request.sql", false);
		wiser.start();
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		john = datas.getUser1();
		AbstractDomain subDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlSubDomain);
		yoda = repository.findByMail("yoda@linshare.org");
		john.setDomain(subDomain);
		//UPLOAD REQUEST CREATE
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
		ure.setActivationDate(new Date());
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		wiser.stop();
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void createUploadRequest() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<UploadRequest> eList = Lists.newArrayList();
		eList = uploadRequestGroupService.createRequest(john, john, ure, Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		uploadRequest = eList.get(0);
		Assert.assertNotNull(uploadRequest);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void findAll() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		int initSize = service.findAllRequest(john, john, null).size();
		uploadRequestGroupService.createRequest(john, john, ure, Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		uploadRequestGroupService.createRequest(john, john, ure, Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		int finalSize = service.findAllRequest(john, john, null).size();
		Assert.assertEquals(initSize+2, finalSize);
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void findByGroup() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<UploadRequest> list = uploadRequestGroupService.createRequest(john, john, ure, Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		uploadRequestGroupService.createRequest(john, john, ure, Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		Assert.assertNotNull(list.get(0));
		int size = service.findAllRequestsByGroup(john, john, list.get(0).getUploadRequestGroup().getUuid(), null).size();
		Assert.assertEquals(1, size);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void findFiltredUploadRequests() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		int initSize = service.findAllRequest(john, john, null).size();
		uploadRequestGroupService.createRequest(john, john, ure, Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date tomorrow = calendar.getTime();
		ure.setActivationDate(tomorrow);
		uploadRequestGroupService.createRequest(john, john, ure, Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		int finalSize = service.findAllRequest(john, john, Lists.newArrayList(UploadRequestStatus.STATUS_ENABLED)).size();
		Assert.assertEquals(initSize+1, finalSize);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void updateStatus() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<UploadRequest> uploadRequests = uploadRequestGroupService.createRequest(john, john, ure, Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		Assert.assertEquals(UploadRequestStatus.STATUS_ENABLED, ure.getUploadRequestGroup().getStatus());

		// Update upload request status
		service.updateStatus(john, john, uploadRequests.get(0).getUuid(), UploadRequestStatus.STATUS_CLOSED);
		Assert.assertEquals(UploadRequestStatus.STATUS_CLOSED, uploadRequests.get(0).getStatus());
		service.updateStatus(john, john, uploadRequests.get(0).getUuid(), UploadRequestStatus.STATUS_ARCHIVED);
		Assert.assertEquals(UploadRequestStatus.STATUS_ARCHIVED, uploadRequests.get(0).getStatus());
		service.updateStatus(john, john, uploadRequests.get(0).getUuid(), UploadRequestStatus.STATUS_DELETED);
		Assert.assertEquals(UploadRequestStatus.STATUS_DELETED, uploadRequests.get(0).getStatus());
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
