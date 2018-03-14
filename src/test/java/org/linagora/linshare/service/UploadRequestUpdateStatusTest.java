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
import java.util.Date;
import org.apache.cxf.helpers.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UploadRequestUrlRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.UploadRequestGroupService;
import org.linagora.linshare.core.service.UploadRequestService;
import org.linagora.linshare.core.service.UploadRequestUrlService;
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
public class UploadRequestUpdateStatusTest extends AbstractTransactionalJUnit4SpringContextTests {

	private static Logger logger = LoggerFactory.getLogger(UploadRequestServiceImplTestV2.class);

	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;

	@Autowired
	private UploadRequestService uploadRequestService;
	
	@Autowired
	private UploadRequestGroupService uploadRequestGroupService;

	@Autowired
	UploadRequestUrlRepository uploadRequestUrlRepository;

	@Autowired
	private UploadRequestUrlService uploadRequestUrlService;

	@Qualifier("jcloudFileDataStore")
	@Autowired
	private FileDataStore fileDataStore;

	private UploadRequest referenceUploadRequest = new UploadRequest();


	private LinShareWiser wiser;

	public UploadRequestUpdateStatusTest() {
		super();
		wiser = new LinShareWiser(2525);
	}

	private final InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");

	private final String fileName = "linshare-default.properties";


	@Before
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		this.executeSqlScript("import-tests-default-domain-quotas.sql", false);
		this.executeSqlScript("import-tests-quota-other.sql", false);
		this.executeSqlScript("import-tests-upload-request.sql", false);
		wiser.start();
		// UPLOAD REQUEST CREATE
		referenceUploadRequest.setCanClose(true);
		referenceUploadRequest.setMaxDepositSize((long) 100);
		referenceUploadRequest.setMaxFileCount(new Integer(3));
		referenceUploadRequest.setMaxFileSize((long) 5000000);
		referenceUploadRequest.setStatus(UploadRequestStatus.CREATED);
		referenceUploadRequest.setExpiryDate(new Date());
		referenceUploadRequest.setSecured(false);
		referenceUploadRequest.setCanEditExpiryDate(true);
		referenceUploadRequest.setCanDelete(true);
		referenceUploadRequest.setLocale("en");
		referenceUploadRequest.setActivationDate(new Date());
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		wiser.stop();
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testPurgeUploadRequest() throws BusinessException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		Account actor = userRepository.findByMail("user1@linshare.org");
		//CreateUploadRequest : STATUS ENABLED
		UploadRequest uploadRequest = uploadRequestGroupService.createRequest(actor, (User)actor, referenceUploadRequest, Lists.newArrayList(new Contact("yoda@int4.linshare.dev")), "This is a subject", "This is a body", false).get(0);
		Assert.assertNotNull("This initial upload request is null", uploadRequest);
		Assert.assertEquals("Wrong upload Request status", UploadRequestStatus.ENABLED, uploadRequest.getStatus());
		//UploadFileIntoUploadRequest : STATUS ENABLED
		UploadRequestUrl uploadRequestUrl = uploadRequest.getUploadRequestURLs().iterator().next();
		uploadRequestUrlService.createUploadRequestEntry(uploadRequestUrl.getUuid(), tempFile, fileName, "");
		//CloseUploadRequest : STATUS CLOSED
		checkUpdateStatus(actor, uploadRequest, UploadRequestStatus.CLOSED, false);
		//ArchiveUploadRequest : STATUS ARCHIVED
		checkUpdateStatus(actor, uploadRequest, UploadRequestStatus.ARCHIVED, false);
		//PurgeUploadRequest : STATUS PURGED
		checkUpdateStatus(actor, uploadRequest, UploadRequestStatus.PURGED, false);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testPurgeUploadRequestAndCopyDocuments() throws BusinessException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		Account actor = userRepository.findByMail("user1@linshare.org");
		//CreateUploadRequest : STATUS ENABLED
		UploadRequest uploadRequest = uploadRequestGroupService.createRequest(actor, (User)actor, referenceUploadRequest, Lists.newArrayList(new Contact("yoda@int4.linshare.dev")), "This is a subject", "This is a body", false).get(0);
		Assert.assertNotNull("This initial upload request is null", uploadRequest);
		Assert.assertEquals("Wrong upload Request status", UploadRequestStatus.ENABLED, uploadRequest.getStatus());
		//UploadFileIntoUploadRequest : STATUS ENABLED
		UploadRequestUrl uploadRequestUrl = uploadRequest.getUploadRequestURLs().iterator().next();
		uploadRequestUrlService.createUploadRequestEntry(uploadRequestUrl.getUuid(), tempFile, fileName, "");
		//CloseUploadRequest : STATUS CLOSED
		checkUpdateStatus(actor, uploadRequest, UploadRequestStatus.CLOSED, false);
		//ArchiveUploadRequest : STATUS ARCHIVED
		checkUpdateStatus(actor, uploadRequest, UploadRequestStatus.ARCHIVED, false);
		//PurgeUploadRequest : STATUS PURGED
		checkUpdateStatus(actor, uploadRequest, UploadRequestStatus.PURGED, true);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCloseAndPurgeUploadRequest() throws BusinessException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		Account actor = userRepository.findByMail("user1@linshare.org");
		//CreateUploadRequest : STATUS ENABLED
		UploadRequest uploadRequest = uploadRequestGroupService.createRequest(actor, (User)actor, referenceUploadRequest, Lists.newArrayList(new Contact("yoda@int4.linshare.dev")), "This is a subject", "This is a body", false).get(0);
		Assert.assertNotNull("This initial upload request is null", uploadRequest);
		Assert.assertEquals("Wrong upload Request status", UploadRequestStatus.ENABLED, uploadRequest.getStatus());
		//UploadFileIntoUploadRequest : STATUS ENABLED
		UploadRequestUrl uploadRequestUrl = uploadRequest.getUploadRequestURLs().iterator().next();
		uploadRequestUrlService.createUploadRequestEntry(uploadRequestUrl.getUuid(), tempFile, fileName, "");
		//CloseUploadRequest : STATUS CLOSED
		checkUpdateStatus(actor, uploadRequest, UploadRequestStatus.CLOSED, false);
		//PurgeUploadRequest : STATUS PURGED
		checkUpdateStatus(actor, uploadRequest, UploadRequestStatus.PURGED, false);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCloseCopyAndPurgeUploadRequest() throws BusinessException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		Account actor = userRepository.findByMail("user1@linshare.org");
		//CreateUploadRequest : STATUS ENABLED
		UploadRequest uploadRequest = uploadRequestGroupService.createRequest(actor, (User)actor, referenceUploadRequest, Lists.newArrayList(new Contact("yoda@int4.linshare.dev")), "This is a subject", "This is a body", false).get(0);
		Assert.assertNotNull("This initial upload request is null", uploadRequest);
		Assert.assertEquals("Wrong upload Request status", UploadRequestStatus.ENABLED, uploadRequest.getStatus());
		//UploadFileIntoUploadRequest : STATUS ENABLED
		UploadRequestUrl uploadRequestUrl = uploadRequest.getUploadRequestURLs().iterator().next();
		uploadRequestUrlService.createUploadRequestEntry(uploadRequestUrl.getUuid(), tempFile, fileName, "");
		//CloseUploadRequest : STATUS CLOSED
		checkUpdateStatus(actor, uploadRequest, UploadRequestStatus.CLOSED, true);
		//PurgeUploadRequest : STATUS PURGED
		checkUpdateStatus(actor, uploadRequest, UploadRequestStatus.PURGED, false);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCloseCopyAndPurgeCopyUploadRequest() throws BusinessException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		Account actor = userRepository.findByMail("user1@linshare.org");
		//CreateUploadRequest : STATUS ENABLED
		UploadRequest uploadRequest = uploadRequestGroupService.createRequest(actor, (User)actor, referenceUploadRequest, Lists.newArrayList(new Contact("yoda@int4.linshare.dev")), "This is a subject", "This is a body", false).get(0);
		Assert.assertNotNull("This initial upload request is null", uploadRequest);
		Assert.assertEquals("Wrong upload Request status", UploadRequestStatus.ENABLED, uploadRequest.getStatus());
		//UploadFileIntoUploadRequest : STATUS ENABLED
		UploadRequestUrl uploadRequestUrl = uploadRequest.getUploadRequestURLs().iterator().next();
		uploadRequestUrlService.createUploadRequestEntry(uploadRequestUrl.getUuid(), tempFile, fileName, "");
		//CloseUploadRequest : STATUS CLOSED
		checkUpdateStatus(actor, uploadRequest, UploadRequestStatus.CLOSED, true);
		//PurgeUploadRequest : STATUS PURGED
		checkUpdateStatus(actor, uploadRequest, UploadRequestStatus.PURGED, true);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	private void checkUpdateStatus(Account actor, UploadRequest uploadReq, UploadRequestStatus requestStatus, boolean copy) {
		uploadRequestService.updateStatus(actor, actor, uploadReq.getUuid(), requestStatus, copy);
		Assert.assertEquals("Wrong upload Request status", requestStatus, uploadReq.getStatus());
	}

}
