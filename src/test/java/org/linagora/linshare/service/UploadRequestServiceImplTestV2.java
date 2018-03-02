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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.cxf.helpers.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.ContactRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.UploadRequestEntryRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.UploadRequestEntryService;
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

	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;

	@Autowired
	private ContactRepository repository;

	@Autowired
	private UploadRequestService uplaodRequestService;
	
	@Autowired
	private UploadRequestGroupService uploadRequestGroupService;
	
	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	@Autowired
	private UploadRequestEntryService uploadRequestEntryService;

	@Autowired
	private UploadRequestEntryRepository uploadRequestEntryRepository;

	@Qualifier("jcloudFileDataStore")
	@Autowired
	private FileDataStore fileDataStore;

	@Autowired
	private DocumentRepository documentRepository;

	private UploadRequest ure = new UploadRequest();

	private DocumentEntry documentEntry;

	private LoadingServiceTestDatas datas;

	private UploadRequest uploadRequest;

	private UploadRequestEntry uploadRequestEntry;

	private User john;

	private Contact yoda;

	private LinShareWiser wiser;

	public UploadRequestServiceImplTestV2() {
		super();
		wiser = new LinShareWiser(2525);
	}
	private User jane;

	private final InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");

	private final String fileName = "linshare-default.properties";

	private final String comment = "file description";

	private UploadRequest ur;

	@Before
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		this.executeSqlScript("import-tests-default-domain-quotas.sql", false);
		this.executeSqlScript("import-tests-quota-other.sql", false);
		this.executeSqlScript("import-tests-upload-request.sql", false);
		wiser.start();
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		john = datas.getUser1();
		jane = datas.getUser2();
		AbstractDomain subDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlSubDomain);
		yoda = repository.findByMail("yoda@linshare.org");
		john.setDomain(subDomain);
		// UPLOAD REQUEST CREATE
		ure.setCanClose(true);
		ure.setMaxDepositSize((long) 100);
		ure.setMaxFileCount(new Integer(3));
		ure.setMaxFileSize((long) 50);
		ure.setStatus(UploadRequestStatus.CREATED);
		ure.setExpiryDate(new Date());
		ure.setSecured(false);
		ure.setCanEditExpiryDate(true);
		ure.setCanDelete(true);
		ure.setLocale("en");
		ure.setActivationDate(new Date());
		uploadRequest = uploadRequestGroupService.createRequest(john, john, ure, Lists.newArrayList(yoda), "This is a subject", "This is a body", false).get(0);
		List<UploadRequest> eList = Lists.newArrayList();
		eList = uploadRequestGroupService.createRequest(john, john, ure, Lists.newArrayList(yoda), "This is a subject",
				"This is a body", false);
		ur = eList.get(0);
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
		int initSize = uplaodRequestService.findAllRequest(john, john, null).size();
		uploadRequestGroupService.createRequest(john, john, ure, Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		uploadRequestGroupService.createRequest(john, john, ure, Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		int finalSize = uplaodRequestService.findAllRequest(john, john, null).size();
		Assert.assertEquals(initSize+2, finalSize);
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void findByGroup() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<UploadRequest> list = uploadRequestGroupService.createRequest(john, john, ure, Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		uploadRequestGroupService.createRequest(john, john, ure, Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		Assert.assertNotNull(list.get(0));
		int size = uplaodRequestService.findAllRequestsByGroup(john, john, list.get(0).getUploadRequestGroup().getUuid(), null).size();
		Assert.assertEquals(1, size);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void findFiltredUploadRequests() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		int initSize = uplaodRequestService.findAllRequest(john, john, null).size();
		uploadRequestGroupService.createRequest(john, john, ure, Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date tomorrow = calendar.getTime();
		ure.setActivationDate(tomorrow);
		uploadRequestGroupService.createRequest(john, john, ure, Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		int finalSize = uplaodRequestService.findAllRequest(john, john, Lists.newArrayList(UploadRequestStatus.ENABLED)).size();
		Assert.assertEquals(initSize+1, finalSize);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void updateStatus() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<UploadRequest> uploadRequests = uploadRequestGroupService.createRequest(john, john, ure,
				Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		Assert.assertEquals(UploadRequestStatus.ENABLED, ure.getUploadRequestGroup().getStatus());
		// Update upload request status
		uplaodRequestService.updateStatus(john, john, uploadRequests.get(0).getUuid(), UploadRequestStatus.CLOSED, false);
		Assert.assertEquals(UploadRequestStatus.CLOSED, uploadRequests.get(0).getStatus());
		uplaodRequestService.updateStatus(john, john, uploadRequests.get(0).getUuid(), UploadRequestStatus.ARCHIVED, true);
		Assert.assertEquals(UploadRequestStatus.ARCHIVED, uploadRequests.get(0).getStatus());
		uplaodRequestService.updateStatus(john, john, uploadRequests.get(0).getUuid(), UploadRequestStatus.DELETED, false);
		Assert.assertEquals(UploadRequestStatus.DELETED, uploadRequests.get(0).getStatus());
	}

	@Test
	public void updateUploadRequest() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequest uRequest = uplaodRequestService.findRequestByUuid(john, john, uploadRequest.getUuid());
		uRequest.setCanClose(false);
		uRequest.setMaxDepositSize((long)150);
		uRequest.setEnableNotification(true);
		uRequest = uplaodRequestService.update(john, john, uRequest.getUuid(), uRequest);
		Assert.assertEquals(uRequest.isCanClose(), false);
		Assert.assertEquals(uRequest.getMaxDepositSize(), new Long(150));
		Assert.assertEquals(uRequest.getEnableNotification(), true);
		Assert.assertEquals(uRequest.getDirty(), true);
		wiser.checkGeneratedMessages();
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateNewUploadRequestActivatedNow() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// UPLOAD REQUEST CREATE
		List<UploadRequest> eListActivated = Lists.newArrayList();
		UploadRequest ureActivated = createSimpleUploadRequest(new Date());
		eListActivated = uploadRequestGroupService.createRequest(john, john, ureActivated, Lists.newArrayList(yoda),
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
		uploadRequestGroupService.createRequest(john, john, ureActivatedLater, Lists.newArrayList(yoda),
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
		uploadRequest.setStatus(UploadRequestStatus.CREATED);
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
		uplaodRequestService.updateStatus(actor, actor, ure.getUuid(), UploadRequestStatus.CLOSED, false);
		uplaodRequestService.updateStatus(actor, actor, ure.getUuid(), UploadRequestStatus.ARCHIVED, false);
		uplaodRequestService.deleteRequest(actor, actor, ure.getUuid());
	}

	@Test
	public void testUploadRequestCopyUploadRequestEntry() throws BusinessException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Account actor = jane;
		Assert.assertNotNull(actor);
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		UploadRequestUrl requestUrl= ur.getUploadRequestURLs().iterator().next();
		Assert.assertNotNull(requestUrl);
		UploadRequest uploadRequest = requestUrl.getUploadRequest();
		Assert.assertNotNull(uploadRequest);
		uploadRequest.setStatus(UploadRequestStatus.CLOSED);
		uploadRequestEntry = uploadRequestEntryService.create(actor, actor, tempFile, fileName, comment, false, null,
				requestUrl);
		Assert.assertTrue(uploadRequestEntryRepository.findByUuid(uploadRequestEntry.getUuid()) != null);

		uplaodRequestService.updateStatus(actor, actor, ur.getUuid(), UploadRequestStatus.CLOSED, false);
		documentEntry = uploadRequestEntryService.copy(actor, actor, uploadRequestEntry);
		Assert.assertNotNull(documentEntry);

		Document aDocument = uploadRequestEntry.getDocument();
		uploadRequestEntryRepository.delete(uploadRequestEntry);
		john.getEntries().clear();
		userRepository.update(jane);
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.THUMBNAIL_SMALL, aDocument, "image/png");
		metadata.setUuid(aDocument.getUuid());
		fileDataStore.remove(metadata);
		documentRepository.delete(aDocument);
		logger.debug(LinShareTestConstants.END_TEST);
	}
}