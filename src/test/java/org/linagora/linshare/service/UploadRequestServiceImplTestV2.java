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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.transaction.Transactional;

import org.apache.cxf.helpers.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.ContactRepository;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.UploadRequestEntryRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.TimeService;
import org.linagora.linshare.core.service.UploadRequestEntryService;
import org.linagora.linshare.core.service.UploadRequestGroupService;
import org.linagora.linshare.core.service.UploadRequestService;
import org.mockito.Mockito;
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
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo-java-server.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml", })
public class UploadRequestServiceImplTestV2 {

	private static Logger logger = LoggerFactory.getLogger(UploadRequestServiceImplTestV2.class);

	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;

	@Autowired
	private ContactRepository repository;

	@Autowired
	private UploadRequestService uploadRequestService;
	
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

	@Autowired
	private TimeService timeService;

	@Autowired
	private DocumentEntryRepository documentEntryRepository;

	@Autowired
	private DocumentEntryService documentEntryService;

	private UploadRequest uploadRequest;

	private UploadRequest ureJohn;

	private UploadRequest ureJane;

	private User john;

	private Contact yoda;

	private User jane;

	private final InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");

	private final String fileName = "linshare-default.properties";

	private final String comment = "file description";

	public UploadRequestServiceImplTestV2() {
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
		// UPLOAD REQUEST CREATE
		uploadRequest = new UploadRequest();
		uploadRequest.setCanClose(true);
		uploadRequest.setMaxDepositSize((long) 100);
		uploadRequest.setMaxFileCount(Integer.valueOf(3));
		uploadRequest.setMaxFileSize((long) 50);
		uploadRequest.setStatus(UploadRequestStatus.CREATED);
		uploadRequest.setExpiryDate(new Date());
		uploadRequest.setProtectedByPassword(false);
		uploadRequest.setCanEditExpiryDate(true);
		uploadRequest.setCanDelete(true);
		uploadRequest.setLocale("en");
		uploadRequest.setActivationDate(new Date());
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, uploadRequest, Lists.newArrayList(yoda), "This is a subject",
				"This is a body", false);
		UploadRequestGroup uploadRequestGroupJane = uploadRequestGroupService.create(jane, jane, uploadRequest, Lists.newArrayList(yoda), "This is a subject",
				"This is a body", false);
		ureJohn = uploadRequestGroup.getUploadRequests().iterator().next();
		ureJane = uploadRequestGroupJane.getUploadRequests().iterator().next();
		timeService = Mockito.mock(TimeService.class);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void createUploadRequest() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequest request = uploadRequest.clone();
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, request, Lists.newArrayList(yoda), "This is a subject",
				"This is a body", false);
		UploadRequest uploadRequest = uploadRequestGroup.getUploadRequests().iterator().next();
		Assertions.assertNotNull(uploadRequest);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void findAll() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, uploadRequest, Lists.newArrayList(yoda), "This is a subject",
				"This is a body", false);
		int size = uploadRequestService.findAll(john, john, uploadRequestGroup, null).size();
		Assertions.assertEquals(1, size);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void findFiltredUploadRequests() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequest request = uploadRequest.clone();
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, request, Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		int initSize = uploadRequestService.findAll(john, john, uploadRequestGroup, Lists.newArrayList(UploadRequestStatus.CREATED)).size();
		int finalSize = uploadRequestService.findAll(john, john, uploadRequestGroup, Lists.newArrayList(UploadRequestStatus.ENABLED)).size();
		Assertions.assertEquals(initSize+1, finalSize);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void updateStatus() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequest request = uploadRequest.clone();
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, request, Lists.newArrayList(yoda), "This is a subject",
				"This is a body", false);
		request = uploadRequestGroup.getUploadRequests().iterator().next();
		Assertions.assertEquals(UploadRequestStatus.ENABLED, request.getUploadRequestGroup().getStatus());
		// Update upload request status
		uploadRequestService.updateStatus(john, john, request.getUuid(), UploadRequestStatus.CLOSED, false);
		Assertions.assertEquals(UploadRequestStatus.CLOSED, request.getStatus());
		uploadRequestService.updateStatus(john, john, request.getUuid(), UploadRequestStatus.ARCHIVED, true);
		Assertions.assertEquals(UploadRequestStatus.ARCHIVED, request.getStatus());
		uploadRequestService.updateStatus(john, john, request.getUuid(), UploadRequestStatus.DELETED, false);
		Assertions.assertEquals(UploadRequestStatus.DELETED, request.getStatus());
	}

	@Test
	public void updateUploadRequest() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequest uRequest = uploadRequestService.find(john, john, ureJohn.getUuid());
		uRequest.setCanClose(false);
		uRequest.setMaxDepositSize((long)150);
		uRequest.setEnableNotification(true);
		uRequest = uploadRequestService.update(john, john, uRequest.getUuid(), uRequest);
		Assertions.assertEquals(uRequest.isCanClose(), false);
		Assertions.assertEquals(uRequest.getMaxDepositSize(), Long.valueOf(150));
		Assertions.assertEquals(uRequest.getEnableNotification(), true);
		Assertions.assertEquals(uRequest.isPristine(), false);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateNewUploadRequestActivatedNow() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequest ureActivated = createSimpleUploadRequest(new Date());
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, ureActivated, Lists.newArrayList(yoda),
				"This is the subject of a new Upload Request",
				"This is a body sent after the creation of the Upload Request", false);
		UploadRequest uploadRequest = uploadRequestGroup.getUploadRequests().iterator().next();
		Assertions.assertEquals(UploadRequestStatus.ENABLED, uploadRequest.getStatus());
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
		uploadRequestGroupService.create(john, john, ureActivatedLater, Lists.newArrayList(yoda),
				"This is the subject of a new Upload Request",
				"This is a body sent after the creation of the Upload Request", false);
		// END OF UPLOAD REQUEST CREATE
		logger.info(LinShareTestConstants.END_TEST);
	}

	private UploadRequest createSimpleUploadRequest(Date activationDate) {
		UploadRequest uploadRequest = new UploadRequest();
		uploadRequest.setCanClose(true);
		uploadRequest.setMaxDepositSize((long) 100);
		uploadRequest.setMaxFileCount(Integer.valueOf(3));
		uploadRequest.setMaxFileSize((long) 50);
		uploadRequest.setStatus(UploadRequestStatus.CREATED);
		uploadRequest.setExpiryDate(new Date());
		uploadRequest.setProtectedByPassword(false);
		uploadRequest.setCanEditExpiryDate(true);
		uploadRequest.setCanDelete(true);
		uploadRequest.setLocale("fr");
		uploadRequest.setActivationDate(activationDate);
		uploadRequest.setCreationDate(new Date());
		uploadRequest.setModificationDate(new Date());
		return uploadRequest;
	}

	@Test
	public void createUploadRequestEntry() throws BusinessException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Account actor = jane;
		Assertions.assertNotNull(actor);
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		UploadRequestUrl requestUrl = ureJohn.getUploadRequestURLs().iterator().next();
		Assertions.assertNotNull(requestUrl);
		UploadRequest uploadRequest = requestUrl.getUploadRequest();
		Assertions.assertNotNull(uploadRequest);
		UploadRequestEntry uploadRequestEntry = uploadRequestEntryService.create(actor, actor, tempFile, fileName, comment, false, null,
				requestUrl);
		Assertions.assertTrue(uploadRequestEntryRepository.findByUuid(uploadRequestEntry.getUuid()) != null);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testUploadRequestEntryCreateSpecialCharacters() throws BusinessException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Account actor = jane;
		Assertions.assertNotNull(actor);
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		UploadRequestUrl requestUrl = ureJohn.getUploadRequestURLs().iterator().next();
		Assertions.assertNotNull(requestUrl);
		UploadRequest uploadRequest = requestUrl.getUploadRequest();
		Assertions.assertNotNull(uploadRequest);
		UploadRequestEntry uploadRequestEntry = uploadRequestEntryService.create(actor, actor, tempFile, "EP_TEST_v233<script>alert(document.cookie)</script>", comment, false, null,
				requestUrl);
		Assertions.assertNotNull(uploadRequestEntryRepository.findByUuid(uploadRequestEntry.getUuid()));
		Assertions.assertEquals(uploadRequestEntry.getName(), "EP_TEST_v233_script_alert(document.cookie)__script_");
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testUploadRequestCopyUploadRequestEntry() throws BusinessException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Account actor = jane;
		Assertions.assertNotNull(actor);
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		UploadRequestUrl requestUrl = ureJane.getUploadRequestURLs().iterator().next();
		Assertions.assertNotNull(requestUrl);
		UploadRequest uploadRequest = requestUrl.getUploadRequest();
		Assertions.assertNotNull(uploadRequest);
		UploadRequestEntry uploadRequestEntry = uploadRequestEntryService.create(actor, actor, tempFile, fileName, comment, false, null,
				requestUrl);
		Assertions.assertTrue(uploadRequestEntryRepository.findByUuid(uploadRequestEntry.getUuid()) != null);

		uploadRequestService.updateStatus(actor, actor, uploadRequest.getUuid(), UploadRequestStatus.CLOSED, false);
		DocumentEntry documentEntry = uploadRequestEntryService.copy(actor, actor, uploadRequestEntry);
		Assertions.assertNotNull(documentEntry);

		Document aDocument = uploadRequestEntry.getDocument();
		userRepository.update(jane);
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.THUMBNAIL_SMALL, aDocument, "image/png");
		metadata.setUuid(aDocument.getUuid());
		fileDataStore.remove(metadata);
		documentRepository.delete(aDocument);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testEnabledUploadRequestCopyUploadRequestEntry() throws BusinessException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		UploadRequestUrl requestUrl = ureJane.getUploadRequestURLs().iterator().next();
		Assertions.assertNotNull(requestUrl);
		UploadRequestEntry uploadRequestEntry = uploadRequestEntryService.create(jane, jane, tempFile, fileName, comment, false, null,
				requestUrl);
		Assertions.assertNotNull(uploadRequestEntryRepository.findByUuid(uploadRequestEntry.getUuid()));
		Assertions.assertTrue(requestUrl.getUploadRequest().getStatus().equals(UploadRequestStatus.ENABLED));
		DocumentEntry documentEntry = uploadRequestEntryService.copy(jane, jane, uploadRequestEntry);
		Assertions.assertNotNull(documentEntry);
		Document aDocument = uploadRequestEntry.getDocument();
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.THUMBNAIL_SMALL, aDocument, "image/png");
		metadata.setUuid(aDocument.getUuid());
		fileDataStore.remove(metadata);
		documentRepository.delete(aDocument);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testUploadRequestCopyUploadRequestEntrySpecialCharacters() throws BusinessException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Account actor = jane;
		Assertions.assertNotNull(actor);
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		UploadRequestUrl requestUrl = ureJane.getUploadRequestURLs().iterator().next();
		Assertions.assertNotNull(requestUrl);
		UploadRequest uploadRequest = requestUrl.getUploadRequest();
		Assertions.assertNotNull(uploadRequest);
		UploadRequestEntry uploadRequestEntry = uploadRequestEntryService.create(actor, actor, tempFile, fileName, comment, false, null,
				requestUrl);
		Assertions.assertNotNull(uploadRequestEntryRepository.findByUuid(uploadRequestEntry.getUuid()));
		uploadRequestEntry.setName("EP_TEST_v233<script>alert(document.cookie)</script>");
		uploadRequestEntryRepository.update(uploadRequestEntry);
		Assertions.assertEquals(uploadRequestEntry.getName(), "EP_TEST_v233<script>alert(document.cookie)</script>");
		uploadRequestService.updateStatus(actor, actor, uploadRequest.getUuid(), UploadRequestStatus.CLOSED, false);
		DocumentEntry documentEntry = uploadRequestEntryService.copy(actor, actor, uploadRequestEntry);
		Assertions.assertNotNull(documentEntry);
		Assertions.assertEquals(documentEntry.getName(), "EP_TEST_v233_script_alert(document.cookie)__script_");
		Document aDocument = uploadRequestEntry.getDocument();
		userRepository.update(jane);
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.THUMBNAIL_SMALL, aDocument, "image/png");
		metadata.setUuid(aDocument.getUuid());
		fileDataStore.remove(metadata);
		documentRepository.delete(aDocument);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testDeleteDocumentEntryAfterCopy() throws BusinessException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Account actor = jane;
		Assertions.assertNotNull(actor);
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		UploadRequestUrl requestUrl = ureJane.getUploadRequestURLs().iterator().next();
		Assertions.assertNotNull(requestUrl);
		UploadRequest uploadRequest = requestUrl.getUploadRequest();
		Assertions.assertNotNull(uploadRequest);
		UploadRequestEntry uploadRequestEntry = uploadRequestEntryService.create(actor, actor, tempFile, fileName, comment, false, null,
				requestUrl);
		Assertions.assertTrue(uploadRequestEntryRepository.findByUuid(uploadRequestEntry.getUuid()) != null);

		uploadRequestService.updateStatus(actor, actor, ureJane.getUuid(), UploadRequestStatus.CLOSED, false);
		DocumentEntry documentEntry = uploadRequestEntryService.copy(actor, actor, uploadRequestEntry);
		Assertions.assertNotNull(documentEntry);
		Assertions.assertNotNull(uploadRequestEntry.getDocumentEntry());

		documentEntryService.delete(actor, actor, documentEntry.getUuid());
		Assertions.assertTrue(documentEntryRepository.findById(documentEntry.getUuid()) == null);

		Document aDocument = uploadRequestEntry.getDocument();
		userRepository.update(jane);
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.THUMBNAIL_SMALL, aDocument, "image/png");
		metadata.setUuid(aDocument.getUuid());
		fileDataStore.remove(metadata);
		documentRepository.delete(aDocument);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	private Date parseDate(String inputDate) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		return formatter.parse(inputDate);
	}

	@Test
	public void activateUploadRequestOnDefaultActivationDate() throws BusinessException, ParseException {
		// In this test when the user does not enter an activation date, the upload
		// request's activation will take the default one defined on the functionality
		// UPLOAD_REQUEST__DELAY_BEFORE_ACTIVATION
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Mockito.when(timeService.dateNow()).thenReturn(parseDate("2021-02-14 01:00:00"));
		UploadRequest uploadRequest = createSimpleUploadRequest(null);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, uploadRequest,
				Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		uploadRequest = uploadRequestGroup.getUploadRequests().iterator().next();
		Assertions.assertNotNull(uploadRequest);
		Assertions.assertEquals(parseDate("2021-02-14 01:00:00"), uploadRequest.getActivationDate());
		Assertions.assertEquals(UploadRequestStatus.ENABLED, uploadRequest.getStatus());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void activateUploadRequestOnMaxDelayofActivation() throws BusinessException, ParseException {
		// In this test the user will choose to activate the UR on the deadline defined
		// on the functionality UPLOAD_REQUEST__DELAY_BEFORE_ACTIVATION
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Mockito.when(timeService.dateNow()).thenReturn(parseDate("2021-08-13 00:00:00"));
		UploadRequest uploadRequest = createSimpleUploadRequest(parseDate("2021-08-13 00:00:00"));
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, uploadRequest,
				Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		uploadRequest = uploadRequestGroup.getUploadRequests().iterator().next();
		Assertions.assertEquals(parseDate("2021-08-13 00:00:00"), uploadRequest.getActivationDate());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void activateUploadRequestAfterMaxDelayofActivation() throws BusinessException, ParseException {
		// In this test the user will choose to activate the UR after the deadline
		// defined on the functionality UPLOAD_REQUEST__DELAY_BEFORE_ACTIVATION
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Mockito.when(timeService.dateNow()).thenReturn(parseDate("2023-08-13 00:00:00"));
		UploadRequest uploadRequest = createSimpleUploadRequest(parseDate("2023-08-13 00:00:00"));
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			uploadRequestGroupService.create(john, john, uploadRequest, Lists.newArrayList(yoda), "This is a subject",
					"This is a body", false);
		});
		Assertions.assertEquals(BusinessErrorCode.UPLOAD_REQUEST_ACTIVATION_DATE_INVALID, exception.getErrorCode());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void activateUploadRequestBeforeDefaultActivationDate() throws BusinessException, ParseException {
		// In this test the user will choose to activate the UR before the default date
		// defined on the functionality UPLOAD_REQUEST__DELAY_BEFORE_ACTIVATION
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Mockito.when(timeService.dateNow()).thenReturn(parseDate("2020-08-13 00:00:00"));
		UploadRequest uploadRequest = createSimpleUploadRequest(parseDate("2020-08-13 00:00:00"));
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			uploadRequestGroupService.create(john, john, uploadRequest, Lists.newArrayList(yoda), "This is a subject",
					"This is a body", false);
		});
		Assertions.assertEquals(BusinessErrorCode.UPLOAD_REQUEST_ACTIVATION_DATE_INVALID, exception.getErrorCode());
		logger.debug(LinShareTestConstants.END_TEST);
	}
}