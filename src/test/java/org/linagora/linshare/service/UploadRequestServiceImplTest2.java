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

@ContextConfiguration(locations = { "classpath:springContext-datasource.xml", "classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml", "classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml", "classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml", "classpath:springContext-rac.xml", "classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml", "classpath:springContext-test.xml", })
public class UploadRequestServiceImplTest2 extends AbstractTransactionalJUnit4SpringContextTests {
	private static Logger logger = LoggerFactory.getLogger(UploadRequestServiceImplTest2.class);

	private LinShareWiser wiser;

	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;

	@Autowired
	private ContactRepository repository;

	@Autowired
	private UploadRequestService service;

	@Autowired
	private UploadRequestGroupService groupService;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	private LoadingServiceTestDatas datas;

	private User john;

	private Contact yoda;

	public UploadRequestServiceImplTest2() {
		super();
		wiser = new LinShareWiser(2525);
	}

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		this.executeSqlScript("import-tests-upload-request.sql", false);
		wiser.start();
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		john = datas.getUser1();
		AbstractDomain subDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlSubDomain);
		yoda = repository.findByMail("yoda@linshare.org");
		john.setDomain(subDomain);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		wiser.stop();
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testCreateNewUploadRequestActivatedNow() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// UPLOAD REQUEST CREATE
		List<UploadRequest> eListActivated = Lists.newArrayList();
		UploadRequest ureActivated = createSimpleUploadRequest(new Date());
		eListActivated = groupService.createRequest(john, john, ureActivated, Lists.newArrayList(yoda),
				"This is the subject of a new Upload Request",
				"This is a body sent after the creation of the Upload Request", false);
		// Test the creation notification
		wiser.checkGeneratedMessages();
		UploadRequest eActivated = eListActivated.get(0);
		// Test the creation notification
		// END OF UPLOAD REQUEST CREATE
		finishUploadRequest(eActivated, john);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateNewUploadRequestActivatedLater() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// UPLOAD REQUEST CREATE
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, 2);
		Date ulteriorActivationDate = calendar.getTime();
		UploadRequest ureActivatedLater = createSimpleUploadRequest(ulteriorActivationDate);
		// Test the creation notification
		groupService.createRequest(john, john, ureActivatedLater, Lists.newArrayList(yoda),
				"This is the subject of a new Upload Request",
				"This is a body sent after the creation of the Upload Request", false);
		wiser.checkGeneratedMessages();
		// END OF UPLOAD REQUEST CREATE
		logger.info(LinShareTestConstants.END_TEST);
	}

	private UploadRequest createSimpleUploadRequest(Date activationDate) {
		UploadRequest uploadRequest = new UploadRequest();
		uploadRequest.setCanClose(true);
		uploadRequest.setMaxDepositSize((long) 100);
		uploadRequest.setMaxFileCount(new Integer(3));
		uploadRequest.setMaxFileSize((long) 50);
		uploadRequest.setStatus(UploadRequestStatus.STATUS_CREATED);
		uploadRequest.setExpiryDate(new Date());
		uploadRequest.setSecured(false);
		uploadRequest.setCanEditExpiryDate(true);
		uploadRequest.setCanDelete(true);
		uploadRequest.setLocale("fr");
		uploadRequest.setActivationDate(activationDate);
		uploadRequest.setCreationDate(new Date());
		uploadRequest.setModificationDate(new Date());
		return uploadRequest;
	}

	private void finishUploadRequest(UploadRequest ure, User actor) {
		service.updateStatus(actor, actor, ure.getUuid(), UploadRequestStatus.STATUS_CLOSED);
		service.updateStatus(actor, actor, ure.getUuid(), UploadRequestStatus.STATUS_ARCHIVED);
		service.deleteRequest(actor, actor, ure.getUuid());
	}

}
