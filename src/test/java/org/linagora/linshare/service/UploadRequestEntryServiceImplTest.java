/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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

package org.linagora.linshare.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import javax.transaction.Transactional;

import org.apache.cxf.helpers.IOUtils;
import org.joda.time.DateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.UploadRequestBusinessService;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.ContactRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.UploadRequestEntryRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.QuotaService;
import org.linagora.linshare.core.service.UploadRequestEntryService;
import org.linagora.linshare.core.service.UploadRequestGroupService;
import org.linagora.linshare.core.service.UploadRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.collect.Lists;

@ExtendWith(SpringExtension.class)
@Sql({
	
	"/import-tests-upload-request.sql" })
@Transactional
@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
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
public class UploadRequestEntryServiceImplTest {
	private static Logger logger = LoggerFactory.getLogger(UploadRequestEntryServiceImplTest.class);

	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;

	@Autowired
	private ContactRepository repository;

	@Autowired
	private UploadRequestGroupService uploadRequestGroupService;

	@Autowired
	private UploadRequestEntryService uploadRequestEntryService;

	@Autowired
	private UploadRequestEntryRepository uploadRequestEntryRepository;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	@Autowired
	private UploadRequestService uploadRequestService;

	@Autowired
	private UploadRequestBusinessService uploadRequestBusinessService;

	@Qualifier("jcloudFileDataStore")
	@Autowired
	private FileDataStore fileDataStore;

	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private QuotaService quotaService;

	private UploadRequest ure = new UploadRequest();

	private User john;

	private User jane;

	private Contact yoda;

	private InputStream stream;

	private InputStream stream2;

	private final String fileName = "linshare-default.properties";

	private final String comment = "file description";

	private UploadRequestEntry uploadRequestEntry;

	public UploadRequestEntryServiceImplTest() {
		super();
	}

	@BeforeEach
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		john = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		jane = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
		AbstractDomain subDomain = abstractDomainRepository.findById(LinShareTestConstants.SUB_DOMAIN);
		yoda = repository.findByMail("yoda@linshare.org");
		john.setDomain(subDomain);
		stream = getStream("linshare-default.properties");
		stream2 = getStream("linshare.properties.sample");
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	private InputStream getStream(String resourceName) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
	}

	private UploadRequest createdUploadRequest() throws ParseException {
		// UPLOAD REQUEST CREATED
		ure.setCanClose(true);
		ure.setMaxDepositSize((long) 100000);
		ure.setMaxFileCount(Integer.valueOf(3));
		ure.setMaxFileSize((long) 100000);
		ure.setActivationDate(new DateTime().plusMonths(3).toDate());
		ure.setExpiryDate(new DateTime().plusMonths(4).toDate());
		ure.setProtectedByPassword(false);
		ure.setCanEditExpiryDate(true);
		ure.setCanDelete(true);
		ure.setLocale(Language.ENGLISH);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, ure, Lists.newArrayList(yoda),
				"This is a subject", "This is a body", false);
		return uploadRequestGroup.getUploadRequests().iterator().next();
	}

	private UploadRequest enabledUploadRequest() throws ParseException {
		// UPLOAD REQUEST ENABLED
		ure.setCanClose(true);
		ure.setMaxDepositSize((long) 100000);
		ure.setMaxFileCount(Integer.valueOf(3));
		ure.setMaxFileSize((long) 100000);
		ure.setProtectedByPassword(false);
		ure.setCanEditExpiryDate(true);
		ure.setCanDelete(true);
		ure.setLocale(Language.ENGLISH);
		ure.setActivationDate(null);
		UploadRequestGroup enabledUploadRequestGroup = uploadRequestGroupService.create(john, john, ure,
				Lists.newArrayList(new Contact(LinShareTestConstants.JANE_ACCOUNT)), "This is a subject",
				"This is a body", false);
		return enabledUploadRequestGroup.getUploadRequests().iterator().next();
	}

	@Test
	public void testUploadRequestCreateDocumentEntry() throws BusinessException, IOException, ParseException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		UploadRequest enabledUploadRequest = enabledUploadRequest();
		uploadRequestEntry = uploadRequestEntryService.create(jane, jane, tempFile, fileName, comment, false, null,
				enabledUploadRequest.getUploadRequestURLs().iterator().next());
		Assertions.assertTrue(uploadRequestEntryRepository.findByUuid(uploadRequestEntry.getUuid()) != null);
		Document aDocument = uploadRequestEntry.getDocument();
		uploadRequestEntryRepository.delete(uploadRequestEntry);
		jane.getEntries().clear();
		userRepository.update(jane);
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.THUMBNAIL_SMALL, aDocument, "image/png");
		metadata.setUuid(aDocument.getUuid());
		fileDataStore.remove(metadata);
		documentRepository.delete(aDocument);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testforbidEntryCreation() throws BusinessException, IOException, ParseException {
		// In this test we will forbid the entry creation if the UR not yet enabled
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequest request = createdUploadRequest();
		Assertions.assertNotNull(request);
		UploadRequestUrl url = request.getUploadRequestURLs().iterator().next();
		Assertions.assertNotNull(url);
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		Assertions.assertEquals(UploadRequestStatus.CREATED, request.getStatus());
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			uploadRequestEntryService.create(jane, jane, tempFile, fileName, comment, false, null, url);
		});
		Assertions.assertEquals(BusinessErrorCode.UPLOAD_REQUEST_READONLY_MODE, exception.getErrorCode());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@Disabled
	public void deleteUploadRequestEntryFail() throws BusinessException, IOException, ParseException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		UploadRequest enabledUploadRequest = enabledUploadRequest();
		uploadRequestEntry = uploadRequestEntryService.create(jane, jane, tempFile, fileName, comment, false, null,
				enabledUploadRequest.getUploadRequestURLs().iterator().next());
		UploadRequestEntry entry = uploadRequestEntryService.find(jane, jane, uploadRequestEntry.getUuid());
		Assertions.assertNotNull(entry);
		Assertions.assertEquals(UploadRequestStatus.ENABLED,
				uploadRequestEntry.getUploadRequestUrl().getUploadRequest().getStatus());
		uploadRequestService.updateStatus(john, john, enabledUploadRequest.getUuid(), UploadRequestStatus.CLOSED,
				false);
		uploadRequestService.updateStatus(john, john, enabledUploadRequest.getUuid(), UploadRequestStatus.PURGED,
				false);
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			uploadRequestEntryService.delete(jane, jane, uploadRequestEntry.getUuid());
		});
		Assertions.assertEquals(
				"You Cannot cannot perform the requested action if upload request's status is not enabled, closed or archived",
				exception.getMessage());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void deleteUploadRequestEntryClosedUR() throws BusinessException, IOException, ParseException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		UploadRequest enabledUploadRequest = enabledUploadRequest();
		uploadRequestEntry = uploadRequestEntryService.create(jane, jane, tempFile, fileName, comment, false, null,
				enabledUploadRequest.getUploadRequestURLs().iterator().next());
		UploadRequestEntry entry = uploadRequestEntryService.find(jane, jane, uploadRequestEntry.getUuid());
		Assertions.assertNotNull(entry);
		uploadRequestService.updateStatus(john, john, enabledUploadRequest.getUuid(), UploadRequestStatus.CLOSED,
				false);
		uploadRequestEntryService.delete(jane, jane, uploadRequestEntry.getUuid());
		BusinessException e = Assertions.assertThrows(BusinessException.class, () -> {
			uploadRequestEntryService.find(jane, jane, uploadRequestEntry.getUuid());
		});
		Assertions.assertEquals(BusinessErrorCode.UPLOAD_REQUEST_ENTRY_NOT_FOUND, e.getErrorCode());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void deleteUploadRequestEntryEnabledUR() throws BusinessException, IOException, ParseException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		UploadRequest enabledUploadRequest = enabledUploadRequest();
		uploadRequestEntry = uploadRequestEntryService.create(jane, jane, tempFile, fileName, comment, false, null,
				enabledUploadRequest.getUploadRequestURLs().iterator().next());
		UploadRequestEntry entry = uploadRequestEntryService.find(jane, jane, uploadRequestEntry.getUuid());
		Assertions.assertNotNull(entry);
		Assertions.assertEquals(UploadRequestStatus.ENABLED,
				uploadRequestEntry.getUploadRequestUrl().getUploadRequest().getStatus());
		uploadRequestEntryService.delete(jane, jane, uploadRequestEntry.getUuid());
		BusinessException e = Assertions.assertThrows(BusinessException.class, () -> {
			uploadRequestEntryService.find(jane, jane, uploadRequestEntry.getUuid());
		});
		Assertions.assertEquals(BusinessErrorCode.UPLOAD_REQUEST_ENTRY_NOT_FOUND, e.getErrorCode());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testForbidDeleteUploadRequestEntryByRecipient() throws BusinessException, IOException, ParseException {
		// In this test the deletion of URE is forbidden if the deletion right is
		// disabled
		logger.info(LinShareTestConstants.BEGIN_TEST);
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		UploadRequest enabledUploadRequest = enabledUploadRequest();
		uploadRequestEntry = uploadRequestEntryService.create(jane, jane, tempFile, fileName, comment, false, null,
				enabledUploadRequest.getUploadRequestURLs().iterator().next());
		UploadRequestEntry entry = uploadRequestEntryService.find(jane, jane, uploadRequestEntry.getUuid());
		Assertions.assertNotNull(entry);
		entry.getUploadRequestUrl().getUploadRequest().setCanDelete(false);
		uploadRequestService.update(john, john, entry.getUploadRequestUrl().getUploadRequest().getUuid(),
				entry.getUploadRequestUrl().getUploadRequest(), false);
		Assertions.assertFalse(enabledUploadRequest.isCanDelete());
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			uploadRequestEntryService.deleteEntryByRecipients(
					enabledUploadRequest.getUploadRequestURLs().iterator().next(), uploadRequestEntry.getUuid());
		});
		Assertions.assertEquals(BusinessErrorCode.UPLOAD_REQUEST_ENTRY_FILE_CANNOT_DELETED, exception.getErrorCode());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCheckAccountQuotaAfterURArchiving() throws BusinessException, IOException, ParseException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Account owner = john;
		Account recipient = jane;
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		UploadRequest enabledUploadRequest = enabledUploadRequest();
		AccountQuota johnQuota = quotaService.findByRelatedAccount(owner);
		Long quota = quotaService.getRealTimeUsedSpace(owner, owner, johnQuota.getUuid());
		UploadRequestUrl uploadRequestUrl = enabledUploadRequest.getUploadRequestURLs().iterator().next();
		uploadRequestEntry = uploadRequestEntryService.create(recipient, owner, tempFile, fileName, comment, false,
				null, uploadRequestUrl);
		Assertions.assertEquals(1, uploadRequestEntryService.findAllEntries(owner, owner, enabledUploadRequest).size());
		Long quotaAfterUpload = quotaService.getRealTimeUsedSpace(owner, owner, johnQuota.getUuid());
		Assertions.assertEquals(quota + uploadRequestEntry.getSize(), quotaAfterUpload,
				"The quota must take in consideration the uploadRequestEntry creation");
		uploadRequestService.updateStatus(john, john, enabledUploadRequest.getUuid(), UploadRequestStatus.CLOSED,
				false);
		uploadRequestService.updateStatus(john, john, enabledUploadRequest.getUuid(), UploadRequestStatus.ARCHIVED,
				false);
		Long quotaAfterURArchiving = quotaService.getRealTimeUsedSpace(owner, owner, johnQuota.getUuid());
		Assertions.assertEquals(quota, quotaAfterURArchiving,
				"The quota must take in consideration the upload requestEntry deletion");
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCheckAccountQuotaUREDeletionByRecipient() throws BusinessException, IOException, ParseException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Account owner = john;
		Account recipient = jane;
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		AccountQuota johnQuota = quotaService.findByRelatedAccount(owner);
		Long quota = quotaService.getRealTimeUsedSpace(owner, owner, johnQuota.getUuid());
		UploadRequest enabledUploadRequest = enabledUploadRequest();
		uploadRequestEntry = uploadRequestEntryService.create(recipient, owner, tempFile, fileName, comment, false,
				null, enabledUploadRequest.getUploadRequestURLs().iterator().next());
		UploadRequestEntry entry = uploadRequestEntryService.find(recipient, recipient, uploadRequestEntry.getUuid());
		Assertions.assertNotNull(entry);
		Long quotaAfterUpload = quotaService.getRealTimeUsedSpace(owner, owner, johnQuota.getUuid());
		Assertions.assertEquals(quota + uploadRequestEntry.getSize(), quotaAfterUpload,
				"The quota must take in consideration the uploadRequestEntry creation");
		uploadRequestEntryService.deleteEntryByRecipients(enabledUploadRequest.getUploadRequestURLs().iterator().next(),
				uploadRequestEntry.getUuid());
		Long quotaAfterDeletion = quotaService.getRealTimeUsedSpace(owner, owner, johnQuota.getUuid());
		Assertions.assertEquals(quota, quotaAfterDeletion,
				"The quota must take in consideration the upload requestEntry deletion");
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindAllEntriesForDeletedUR() throws BusinessException, IOException, ParseException {
		// This test is used to recover the entries for the archived, deleted UR and it
		// will be
		// used in the upgrade task
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Account owner = john;
		Account recipient = jane;
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		UploadRequest enabledUploadRequest = enabledUploadRequest();
		UploadRequestUrl uploadRequestUrl = enabledUploadRequest.getUploadRequestURLs().iterator().next();
		uploadRequestEntry = uploadRequestEntryService.create(recipient, owner, tempFile, fileName, comment, false,
				null, uploadRequestUrl);
		Assertions.assertEquals(1, uploadRequestEntryService.findAllEntries(owner, owner, enabledUploadRequest).size());
		uploadRequestService.updateStatus(john, john, enabledUploadRequest.getUuid(), UploadRequestStatus.CLOSED,
				false);
		uploadRequestBusinessService.updateStatus(enabledUploadRequest, UploadRequestStatus.ARCHIVED);
		Assertions.assertTrue(enabledUploadRequest.isArchived());
		String recoveredUREUuid = uploadRequestEntryRepository.findAllEntriesForArchivedDeletedPurgedUR().iterator()
				.next();
		Assertions.assertEquals(uploadRequestEntry.getUuid(), recoveredUREUuid);
		uploadRequestBusinessService.updateStatus(enabledUploadRequest, UploadRequestStatus.DELETED);
		Assertions.assertTrue(enabledUploadRequest.isDeleted());
		Assertions.assertEquals(uploadRequestEntry.getUuid(), recoveredUREUuid);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testforbidEntryCreationExceedingMaxNumber() throws BusinessException, IOException, ParseException {
		// In this test we will forbid the entry creation if exceeds the max count
		// number
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequest enabledUploadRequest = enabledUploadRequest();
		Assertions.assertNotNull(enabledUploadRequest);
		UploadRequestUrl url = enabledUploadRequest.getUploadRequestURLs().iterator().next();
		Assertions.assertNotNull(url);
		enabledUploadRequest.setMaxFileCount(1);
		enabledUploadRequest = uploadRequestService.update(john, john, enabledUploadRequest.getUuid(),
				enabledUploadRequest, false);
		Assertions.assertEquals(1, enabledUploadRequest.getMaxFileCount());
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		File tempFile2 = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream2, tempFile2);
		uploadRequestEntryService.create(jane, jane, tempFile, fileName, comment, false, null, url);
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			uploadRequestEntryService.create(jane, jane, tempFile2, fileName, comment, false, null, url);
		});
		Assertions.assertEquals(BusinessErrorCode.UPLOAD_REQUEST_TOO_MANY_FILES, exception.getErrorCode());
		enabledUploadRequest.setMaxFileCount(3);
		uploadRequestService.update(john, john, enabledUploadRequest.getUuid(), enabledUploadRequest, false);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testforbidEntryCreationExceedingMaxDepostSize() throws BusinessException, IOException, ParseException {
		// In this test we will forbid the entry creation if exceeds the max deposit size
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequest enabledUploadRequest = enabledUploadRequest();
		Assertions.assertNotNull(enabledUploadRequest);
		UploadRequestUrl url = enabledUploadRequest.getUploadRequestURLs().iterator().next();
		Assertions.assertNotNull(url);
		enabledUploadRequest.setMaxDepositSize((long) 10000);
		uploadRequestService.update(john, john, enabledUploadRequest.getUuid(), enabledUploadRequest, false);
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			uploadRequestEntryService.create(jane, jane, tempFile, fileName, comment, false, null, url);
		});
		Assertions.assertEquals(BusinessErrorCode.UPLOAD_REQUEST_TOTAL_DEPOSIT_SIZE_TOO_LARGE,
				exception.getErrorCode());
		enabledUploadRequest.setMaxDepositSize((long) 100000);
		uploadRequestService.update(john, john, enabledUploadRequest.getUuid(), enabledUploadRequest, false);
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
