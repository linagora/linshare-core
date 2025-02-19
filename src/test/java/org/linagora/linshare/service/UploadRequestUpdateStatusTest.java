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
package org.linagora.linshare.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import javax.transaction.Transactional;

import org.apache.cxf.helpers.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UploadRequestUrlRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.UploadRequestGroupService;
import org.linagora.linshare.core.service.UploadRequestService;
import org.linagora.linshare.core.service.UploadRequestUrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.collect.Lists;

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@ExtendWith(SpringExtension.class)
@Sql({
	"/import-tests-upload-request.sql" })
@Transactional
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml", })
public class UploadRequestUpdateStatusTest {

	private static Logger logger = LoggerFactory.getLogger(UploadRequestUpdateStatusTest.class);

	@TempDir
	private File tempDir;

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

	private UploadRequest referenceUploadRequest = new UploadRequest();

	private final InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");

	private final String fileName = "linshare-default.properties";

	public UploadRequestUpdateStatusTest() {
		super();
	}

	@BeforeEach
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		// UPLOAD REQUEST CREATE
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 10);
		Date expiryDate = calendar.getTime();
		referenceUploadRequest.setCanClose(true);
		referenceUploadRequest.setMaxDepositSize((long) 5000000);
		referenceUploadRequest.setMaxFileCount(Integer.valueOf(3));
		referenceUploadRequest.setMaxFileSize((long) 5000000);
		referenceUploadRequest.setExpiryDate(expiryDate);
		referenceUploadRequest.setProtectedByPassword(false);
		referenceUploadRequest.setCanEditExpiryDate(true);
		referenceUploadRequest.setCanDelete(true);
		referenceUploadRequest.setLocale(Language.FRENCH);
		referenceUploadRequest.setActivationDate(null);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testPurgeUploadRequest() throws BusinessException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		File tempFile = File.createTempFile("linshare-test-", ".tmp", this.tempDir);
		IOUtils.transferTo(stream, tempFile);
		Account actor = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		//CreateUploadRequest : STATUS ENABLED
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(actor, (User)actor, referenceUploadRequest, Lists.newArrayList(new Contact("yoda@int4.linshare.dev")), "This is a subject", "This is a body", false);
		UploadRequest uploadRequest = uploadRequestGroup.getUploadRequests().iterator().next();
		Assertions.assertNotNull(uploadRequest, "This initial upload request is null");
		Assertions.assertEquals(UploadRequestStatus.ENABLED, uploadRequest.getStatus(), "Wrong upload Request status");
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
		File tempFile = File.createTempFile("linshare-test-", ".tmp", this.tempDir);
		IOUtils.transferTo(stream, tempFile);
		Account actor = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		//CreateUploadRequest : STATUS ENABLED
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(actor, (User)actor, referenceUploadRequest, Lists.newArrayList(new Contact("yoda@int4.linshare.dev")), "This is a subject", "This is a body", false);
		UploadRequest uploadRequest = uploadRequestGroup.getUploadRequests().iterator().next();
		Assertions.assertNotNull(uploadRequest, "This initial upload request is null");
		Assertions.assertEquals(UploadRequestStatus.ENABLED, uploadRequest.getStatus(), "Wrong upload Request status");
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
		File tempFile = File.createTempFile("linshare-test-", ".tmp", this.tempDir);
		IOUtils.transferTo(stream, tempFile);
		Account actor = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		//CreateUploadRequest : STATUS ENABLED
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(actor, (User)actor, referenceUploadRequest, Lists.newArrayList(new Contact("yoda@int4.linshare.dev")), "This is a subject", "This is a body", false);
		UploadRequest uploadRequest = uploadRequestGroup.getUploadRequests().iterator().next();
		Assertions.assertNotNull(uploadRequest, "This initial upload request is null");
		Assertions.assertEquals(UploadRequestStatus.ENABLED, uploadRequest.getStatus(), "Wrong upload Request status");
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
		File tempFile = File.createTempFile("linshare-test-", ".tmp", this.tempDir);
		IOUtils.transferTo(stream, tempFile);
		Account actor = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		//CreateUploadRequest : STATUS ENABLED
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(actor, (User)actor, referenceUploadRequest, Lists.newArrayList(new Contact("yoda@int4.linshare.dev")), "This is a subject", "This is a body", false);
		UploadRequest uploadRequest = uploadRequestGroup.getUploadRequests().iterator().next();
		Assertions.assertNotNull(uploadRequest, "This initial upload request is null");
		Assertions.assertEquals(UploadRequestStatus.ENABLED, uploadRequest.getStatus(), "Wrong upload Request status");
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
		File tempFile = File.createTempFile("linshare-test-", ".tmp", this.tempDir);
		IOUtils.transferTo(stream, tempFile);
		Account actor = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		//CreateUploadRequest : STATUS ENABLED
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(actor, (User)actor, referenceUploadRequest, Lists.newArrayList(new Contact("yoda@int4.linshare.dev")), "This is a subject", "This is a body", false);
		UploadRequest uploadRequest = uploadRequestGroup.getUploadRequests().iterator().next();
		Assertions.assertNotNull(uploadRequest, "This initial upload request is null");
		Assertions.assertEquals(UploadRequestStatus.ENABLED, uploadRequest.getStatus(), "Wrong upload Request status");
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
		Assertions.assertEquals(requestStatus, uploadReq.getStatus(), "Wrong upload Request status");
	}
}
