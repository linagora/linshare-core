/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.TargetKind;
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
import org.linagora.linshare.core.domain.objects.CopyResource;
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
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.TimeService;
import org.linagora.linshare.core.service.UploadRequestEntryService;
import org.linagora.linshare.core.service.UploadRequestGroupService;
import org.linagora.linshare.core.service.UploadRequestService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.collect.Lists;

@ExtendWith({ SpringExtension.class, MockitoExtension.class })
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
public class UploadRequestServiceImplV2Test {

	private static Logger logger = LoggerFactory.getLogger(UploadRequestServiceImplV2Test.class);

	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;

	@Autowired
	private ContactRepository repository;

	@Autowired
	private UploadRequestService uploadRequestService;
	
	@Autowired
	@InjectMocks
	private UploadRequestGroupService uploadRequestGroupService;
	
	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	@Autowired
	private UploadRequestEntryService uploadRequestEntryService;
	
	@Autowired
	@InjectMocks
	private FunctionalityReadOnlyService functionalityReadOnlyService;

	@Autowired
	private UploadRequestEntryRepository uploadRequestEntryRepository;

	@Qualifier("jcloudFileDataStore")
	@Autowired
	private FileDataStore fileDataStore;

	@Autowired
	private DocumentRepository documentRepository;

	@Mock
	private TimeService timeService;

	@Autowired
	private DocumentEntryRepository documentEntryRepository;

	@Autowired
	private DocumentEntryService documentEntryService;

	private UploadRequest globaleUploadRequest;

	private UploadRequest ureJohn;

	private User john;

	private Contact yoda;

	private User jane;

	private final InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");

	private final String fileName = "linshare-default.properties";

	private final String comment = "file description";

	private Date globalNow;
	
	public UploadRequestServiceImplV2Test() {
		super();
	}

	@BeforeEach
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		john = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		jane = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
		AbstractDomain subDomain = abstractDomainRepository.findById(LinShareTestConstants.SUB_DOMAIN);
		globalNow = parseDate("2020-08-12 15:25:00");
		Mockito.when(timeService.dateNow()).thenReturn(globalNow);
		yoda = repository.findByMail("yoda@linshare.org");
		john.setDomain(subDomain);
		// UPLOAD REQUEST CREATE
		globaleUploadRequest = new UploadRequest();
		globaleUploadRequest.setCanClose(true);
		globaleUploadRequest.setMaxDepositSize((long) 100000);
		globaleUploadRequest.setMaxFileCount(Integer.valueOf(3));
		globaleUploadRequest.setMaxFileSize((long) 100000);
		globaleUploadRequest.setNotificationDate(parseDate("2020-09-01 09:00:00"));
		globaleUploadRequest.setExpiryDate(parseDate("2020-09-01 15:00:00"));
		globaleUploadRequest.setProtectedByPassword(false);
		globaleUploadRequest.setCanEditExpiryDate(true);
		globaleUploadRequest.setCanDelete(true);
		globaleUploadRequest.setLocale(Language.ENGLISH);
		globaleUploadRequest.setActivationDate(null);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, globaleUploadRequest, Lists.newArrayList(yoda), "This is a subject",
				"This is a body", false);
		ureJohn = uploadRequestGroup.getUploadRequests().iterator().next();
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
		UploadRequest request = globaleUploadRequest.clone();
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, request, Lists.newArrayList(yoda), "This is a subject",
				"This is a body", false);
		UploadRequest uploadRequest = uploadRequestGroup.getUploadRequests().iterator().next();
		Assertions.assertNotNull(uploadRequest);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void createUploadRequestWithSaasLimitation() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequest request = globaleUploadRequest.clone();

		// by default we only have the permissions to create 5 upload requests
		uploadRequestGroupService.create(john, john, request, Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		uploadRequestGroupService.create(john, john, request, Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		uploadRequestGroupService.create(john, john, request, Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		uploadRequestGroupService.create(john, john, request, Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		uploadRequestGroupService.create(john, john, request, Lists.newArrayList(yoda), "This is a subject", "This is a body", false);

		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			uploadRequestGroupService.create(john, john, request, Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		});
		Assertions.assertEquals(BusinessErrorCode.UPLOAD_REQUEST_LIMIT_REACHED, exception.getErrorCode());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void findAll() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, globaleUploadRequest, Lists.newArrayList(yoda), "This is a subject",
				"This is a body", false);
		int size = uploadRequestService.findAll(john, john, uploadRequestGroup, null).size();
		Assertions.assertEquals(1, size);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void findFiltredUploadRequests() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequest request = globaleUploadRequest.clone();
		request.setActivationDate(null);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, request, Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		int initSize = uploadRequestService.findAll(john, john, uploadRequestGroup, Lists.newArrayList(UploadRequestStatus.CREATED)).size();
		int finalSize = uploadRequestService.findAll(john, john, uploadRequestGroup, Lists.newArrayList(UploadRequestStatus.ENABLED)).size();
		Assertions.assertEquals(initSize+1, finalSize);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void updateStatus() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequest request = globaleUploadRequest.clone();
		request.setActivationDate(null);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, request, Lists.newArrayList(yoda), "This is a subject",
				"This is a body", false);
		request = uploadRequestGroup.getUploadRequests().iterator().next();
		Assertions.assertTrue(request.isPristine());
		Assertions.assertEquals(UploadRequestStatus.ENABLED, request.getUploadRequestGroup().getStatus());
		// Update upload request status
		uploadRequestService.updateStatus(john, john, request.getUuid(), UploadRequestStatus.CLOSED, false);
		Assertions.assertEquals(UploadRequestStatus.CLOSED, request.getStatus());
		uploadRequestService.updateStatus(john, john, request.getUuid(), UploadRequestStatus.ARCHIVED, true);
		Assertions.assertEquals(UploadRequestStatus.ARCHIVED, request.getStatus());
		uploadRequestService.updateStatus(john, john, request.getUuid(), UploadRequestStatus.DELETED, false);
		Assertions.assertEquals(UploadRequestStatus.DELETED, request.getStatus());
		Assertions.assertTrue(request.isPristine());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void updateUploadRequest() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequest uRequest = uploadRequestService.find(john, john, ureJohn.getUuid());
		uRequest.setCanClose(false);
		uRequest.setMaxDepositSize((long)150);
		uRequest.setEnableNotification(true);
		uRequest = uploadRequestService.update(john, john, uRequest.getUuid(), uRequest, false);
		Assertions.assertEquals(uRequest.isCanClose(), false);
		Assertions.assertEquals(uRequest.getMaxDepositSize(), Long.valueOf(150));
		Assertions.assertEquals(uRequest.getEnableNotification(), true);
		Assertions.assertEquals(uRequest.isPristine(), false);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void updateUploadRequestCorrectLocale() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequest uRequest = uploadRequestService.find(john, john, ureJohn.getUuid());
		Language rightLocale = Language.FRENCH;
		uRequest.setLocale(rightLocale);
		uRequest = uploadRequestService.update(john, john, uRequest.getUuid(), uRequest, false);
		Assertions.assertEquals(rightLocale, uRequest.getLocale());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateNewUploadRequestActivatedNow() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequest ureActivated = createSimpleUploadRequest(null);
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
		calendar.setTime(timeService.dateNow());
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
		uploadRequest.setProtectedByPassword(false);
		uploadRequest.setCanEditExpiryDate(true);
		uploadRequest.setCanDelete(true);
		uploadRequest.setLocale(Language.FRENCH);
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
		UploadRequest request = globaleUploadRequest.clone();
		request.setActivationDate(null);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(jane, jane, request, Lists.newArrayList(yoda), "This is a subject",
				"This is a body", false);
		request = uploadRequestGroup.getUploadRequests().iterator().next();
		UploadRequestUrl requestUrl = request.getUploadRequestURLs().iterator().next();
		Assertions.assertNotNull(requestUrl);
		UploadRequest uploadRequest = requestUrl.getUploadRequest();
		Assertions.assertNotNull(uploadRequest);
		UploadRequestEntry uploadRequestEntry = uploadRequestEntryService.create(actor, actor, tempFile, fileName, comment, false, null,
				requestUrl);
		Assertions.assertTrue(uploadRequestEntryRepository.findByUuid(uploadRequestEntry.getUuid()) != null);

		uploadRequestService.updateStatus(actor, actor, uploadRequest.getUuid(), UploadRequestStatus.CLOSED, false);
		DocumentEntry documentEntry = uploadRequestEntryService.copy(actor, actor, new CopyResource(TargetKind.UPLOAD_REQUEST, uploadRequestEntry));
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
	public void testCollectiveUploadRequestCopyUploadRequestEntry() throws BusinessException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Account actor = jane;
		Assertions.assertNotNull(actor);
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		UploadRequest request = globaleUploadRequest.clone();
		request.setActivationDate(null);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(jane, jane, request, Lists.newArrayList(yoda), "This is a subject",
				"This is a body", true);
		request = uploadRequestGroup.getUploadRequests().iterator().next();
		UploadRequestUrl requestUrl = request.getUploadRequestURLs().iterator().next();
		Assertions.assertNotNull(requestUrl);
		UploadRequest uploadRequest = requestUrl.getUploadRequest();
		Assertions.assertNotNull(uploadRequest);
		UploadRequestEntry uploadRequestEntry = uploadRequestEntryService.create(actor, actor, tempFile, fileName, comment, false, null,
				requestUrl);
		Assertions.assertTrue(uploadRequestEntryRepository.findByUuid(uploadRequestEntry.getUuid()) != null);

		uploadRequestService.updateStatus(actor, actor, uploadRequest.getUuid(), UploadRequestStatus.CLOSED, false);
		DocumentEntry documentEntry = uploadRequestEntryService.copy(actor, actor, new CopyResource(TargetKind.UPLOAD_REQUEST, uploadRequestEntry));
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
		UploadRequest request = globaleUploadRequest.clone();
		request.setActivationDate(null);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(jane, jane, request, Lists.newArrayList(yoda), "This is a subject",
				"This is a body", false);
		request = uploadRequestGroup.getUploadRequests().iterator().next();
		UploadRequestUrl requestUrl = request.getUploadRequestURLs().iterator().next();
		Assertions.assertNotNull(requestUrl);
		UploadRequestEntry uploadRequestEntry = uploadRequestEntryService.create(jane, jane, tempFile, fileName, comment, false, null,
				requestUrl);
		Assertions.assertNotNull(uploadRequestEntryRepository.findByUuid(uploadRequestEntry.getUuid()));
		Assertions.assertTrue(requestUrl.getUploadRequest().getStatus().equals(UploadRequestStatus.ENABLED));
		DocumentEntry documentEntry = uploadRequestEntryService.copy(jane, jane, new CopyResource(TargetKind.UPLOAD_REQUEST, uploadRequestEntry));
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
		UploadRequest request = globaleUploadRequest.clone();
		request.setActivationDate(null);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(jane, jane, request, Lists.newArrayList(yoda), "This is a subject",
				"This is a body", false);
		request = uploadRequestGroup.getUploadRequests().iterator().next();
		UploadRequestUrl requestUrl = request.getUploadRequestURLs().iterator().next();
		Assertions.assertNotNull(requestUrl);
		Assertions.assertNotNull(request);
		UploadRequestEntry uploadRequestEntry = uploadRequestEntryService.create(actor, actor, tempFile, fileName, comment, false, null,
				requestUrl);
		Assertions.assertNotNull(uploadRequestEntryRepository.findByUuid(uploadRequestEntry.getUuid()));
		uploadRequestEntry.setName("EP_TEST_v233<script>alert(document.cookie)</script>");
		uploadRequestEntry.setComment("EP_TEST_v233<script>alert(document.cookie)</script>");
		uploadRequestEntryRepository.update(uploadRequestEntry);
		Assertions.assertEquals(uploadRequestEntry.getName(), "EP_TEST_v233<script>alert(document.cookie)</script>");
		uploadRequestService.updateStatus(actor, actor, request.getUuid(), UploadRequestStatus.CLOSED, false);
		DocumentEntry documentEntry = uploadRequestEntryService.copy(actor, actor, new CopyResource(TargetKind.UPLOAD_REQUEST, uploadRequestEntry));
		Assertions.assertNotNull(documentEntry);
		Assertions.assertEquals(documentEntry.getName(), "EP_TEST_v233_script_alert(document.cookie)__script_");
		Assertions.assertEquals(documentEntry.getComment(), "EP_TEST_v233_script_alert(document.cookie)__script_");
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
		UploadRequest request = globaleUploadRequest.clone();
		request.setActivationDate(null);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(jane, jane, request, Lists.newArrayList(yoda), "This is a subject",
				"This is a body", false);
		request = uploadRequestGroup.getUploadRequests().iterator().next();
		UploadRequestUrl requestUrl = request.getUploadRequestURLs().iterator().next();
		Assertions.assertNotNull(requestUrl);
		UploadRequestEntry uploadRequestEntry = uploadRequestEntryService.create(actor, actor, tempFile, fileName, comment, false, null,
				requestUrl);
		Assertions.assertTrue(uploadRequestEntryRepository.findByUuid(uploadRequestEntry.getUuid()) != null);
		uploadRequestService.updateStatus(actor, actor, request.getUuid(), UploadRequestStatus.CLOSED, false);
		DocumentEntry documentEntry = uploadRequestEntryService.copy(actor, actor, new CopyResource(TargetKind.UPLOAD_REQUEST, uploadRequestEntry));
		Assertions.assertNotNull(documentEntry);

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
		return formatter.parse(inputDate);
	}

	@Test
	public void testActivateUploadRequestOnDefaultActivationDate() throws BusinessException, ParseException {
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
		Assertions.assertEquals(UploadRequestStatus.ENABLED, uploadRequestGroup.getStatus(),
				"No activation date has been set meaning the upload request is directly enabled");
		Assertions.assertEquals(UploadRequestStatus.ENABLED, uploadRequest.getStatus(),
				"No activation date has been set meaning the upload request is directly enabled");
		Assertions.assertEquals(parseDate("2021-02-14 01:00:00"), uploadRequest.getActivationDate());
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
	public void testActivateUploadRequestAfterMaxDelayofActivation() throws BusinessException, ParseException {
		// In this test the user will choose to activate the UR after the deadline
		// defined on the functionality UPLOAD_REQUEST__DELAY_BEFORE_ACTIVATION
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Mockito.when(timeService.dateNow()).thenReturn(parseDate("2023-08-13 00:00:00"));
		// the max_date will be [mockTodayDate + maxDate from functionality]
		// set an activation date out of the range [mockTodayDate, max_date] | Expected raise an exception
		Date activationDate = parseDate("2023-08-12 11:12:00");
		UploadRequest uploadRequest = createSimpleUploadRequest(activationDate);
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			uploadRequestGroupService.create(john, john, uploadRequest, Lists.newArrayList(yoda), "This is a subject",
					"This is a body", false);
		});
		Assertions.assertEquals(BusinessErrorCode.UPLOAD_REQUEST_ACTIVATION_DATE_INVALID, exception.getErrorCode());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testActivateUploadRequestBeforeDefaultActivationDate() throws BusinessException, ParseException {
		// In this test the user will choose to activate the UR before the default date
		// defined on the functionality UPLOAD_REQUEST__DELAY_BEFORE_ACTIVATION
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Mockito.when(timeService.dateNow()).thenReturn(parseDate("2020-08-13 00:00:00"));
		Date activatioDateBeforeMockedDate = parseDate("2020-08-12 00:00:00");
		UploadRequest uploadRequest = createSimpleUploadRequest(activatioDateBeforeMockedDate);
		// delegation enabled and the given activation date is not null so the activation date should be before now (mockedTodayDate)
		// expected raise an exception
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			uploadRequestGroupService.create(john, john, uploadRequest, Lists.newArrayList(yoda), "This is a subject",
					"This is a body", false);
		});
		Assertions.assertEquals(BusinessErrorCode.UPLOAD_REQUEST_ACTIVATION_DATE_INVALID, exception.getErrorCode());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testRoundActivateDateWhenCreateUploadRequest() throws BusinessException, ParseException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Mockito.when(timeService.dateNow()).thenReturn(parseDate("2020-08-12 15:25:00"));
		Date activationDateAfterMockedDate = parseDate("2020-08-13 16:34:00");
		UploadRequest uploadRequest = createSimpleUploadRequest(activationDateAfterMockedDate);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, uploadRequest,
				Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		Date expectedDate = parseDate("2020-08-13 17:00:00");
		Assertions.assertEquals(expectedDate, uploadRequestGroup.getActivationDate(),
				"The activation date has not been rounded");
		uploadRequest = uploadRequestGroup.getUploadRequests().iterator().next();
		Assertions.assertEquals(expectedDate, uploadRequest.getActivationDate(),
				"The activation date has not been rounded");
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testNotRoundActivateDateWhenCreateUploadRequest() throws BusinessException, ParseException {
		// validate activation date not rounded if enabled now (activation date is set to null)
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date now = parseDate("2020-08-12 15:25:00");
		Mockito.when(timeService.dateNow()).thenReturn(now);
		UploadRequest uploadRequest = createSimpleUploadRequest(null);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, uploadRequest,
				Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		Assertions.assertEquals(UploadRequestStatus.ENABLED, uploadRequestGroup.getStatus());
		Assertions.assertEquals(now, uploadRequestGroup.getActivationDate(),
				"The activation date should have now date value");
		uploadRequest = uploadRequestGroup.getUploadRequests().iterator().next();
		Assertions.assertEquals(now, uploadRequest.getActivationDate(),
				"The activation date should have now date value");
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testRoundActivateDateWhenCreatePostponedUploadRequest() throws BusinessException, ParseException {
		// validate activation date not rounded if not enabled now (activation date has a value)
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date now = parseDate("2020-08-12 15:25:00");
		Mockito.when(timeService.dateNow()).thenReturn(now);
		UploadRequest uploadRequest = createSimpleUploadRequest(now);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, uploadRequest,
				Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		Date expectedActivationDate = parseDate("2020-08-12 16:00:00");
		Assertions.assertEquals(UploadRequestStatus.CREATED, uploadRequestGroup.getStatus());
		Assertions.assertEquals(expectedActivationDate, uploadRequestGroup.getActivationDate(),
				"The activation date has not been rounded");
		uploadRequest = uploadRequestGroup.getUploadRequests().iterator().next();
		Assertions.assertEquals(expectedActivationDate, uploadRequest.getActivationDate(),
				"The activation date has not been rounded");
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testRoundExpiryDateWhenCreateEnabledUploadRequest() throws BusinessException, ParseException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date now = parseDate("2020-03-12 15:25:00");
		Date expiryDate = parseDate("2020-04-01 9:15:45");
		Date notificationDate = parseDate("2020-03-30 11:15:45");
		Mockito.when(timeService.dateNow()).thenReturn(now);
		UploadRequest request = globaleUploadRequest.clone();
		request.setActivationDate(null);
		request.setExpiryDate(expiryDate);
		request.setNotificationDate(notificationDate);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(jane, jane, request, Lists.newArrayList(yoda), "This is a subject",
				"This is a body", false);
		request = uploadRequestGroup.getUploadRequests().iterator().next();
		Date expectedExpiryDate = parseDate("2020-04-01 10:00:00");
		Assertions.assertEquals(expectedExpiryDate, uploadRequestGroup.getExpiryDate(),
				"The expiry date of the group has not been rounded");
		Assertions.assertEquals(expectedExpiryDate, request.getExpiryDate(),
				"The expiry date of the upload request has not been rounded");
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testRoundExpiryDateDefaultWhenCreateEnabledUploadRequest() throws BusinessException, ParseException {
		// test that expiration date is rounded if the upload request is created without
		// setting expiration date
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date now = parseDate("2020-03-12 15:25:00");
		Mockito.when(timeService.dateNow()).thenReturn(now);
		UploadRequest request = globaleUploadRequest.clone();
		request.setActivationDate(null);
		request.setExpiryDate(null);
		request.setNotificationDate(null);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(jane, jane, request,
				Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		Assertions.assertEquals(UploadRequestStatus.ENABLED, uploadRequestGroup.getStatus());
		request = uploadRequestGroup.getUploadRequests().iterator().next();
		// default from the functionality expiration ==> 3 months after activation date
		Date expectedExpiryDate = parseDate("2020-06-12 16:00:00");
		// default from the functionality notification ==> 7 days before expiration
		Date expectedNotificationDate = parseDate("2020-06-05 16:00:00");
		// by default expiration and notification date will have a rounded default value
		// computed from functionality
		Assertions.assertEquals(expectedExpiryDate, uploadRequestGroup.getExpiryDate(),
				"The expiry date of the group has not been rounded");
		Assertions.assertEquals(expectedNotificationDate, uploadRequestGroup.getNotificationDate(),
				"The notification date of the group has not been rounded");
		Assertions.assertEquals(expectedExpiryDate, request.getExpiryDate(),
				"The expiry date of the upload request has not been rounded");
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testRoundExpiryDateWhenCreateUploadRequest() throws BusinessException, ParseException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date now = parseDate("2020-03-12 15:25:00");
		Date expiryDate = parseDate("2020-04-01 9:15:45");
		Date notificationDate = parseDate("2020-03-30 11:15:45");
		Mockito.when(timeService.dateNow()).thenReturn(now);
		UploadRequest request = globaleUploadRequest.clone();
		request.setActivationDate(now);
		request.setExpiryDate(expiryDate);
		request.setNotificationDate(notificationDate);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, request,
				Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		request = uploadRequestGroup.getUploadRequests().iterator().next();
		Date expectedExpiryDate = parseDate("2020-04-01 10:00:00");
		Assertions.assertEquals(expectedExpiryDate, uploadRequestGroup.getExpiryDate(),
				"The expiry date of the group has not been rounded");
		Assertions.assertEquals(expectedExpiryDate, request.getExpiryDate(),
				"The expiry date of the upload request has not been rounded");
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testRoundNotifDateWhenCreateEnabledUploadRequest() throws BusinessException, ParseException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date now = parseDate("2020-03-12 15:25:00");
		Date expiryDate = parseDate("2020-04-01 9:15:45");
		Date notificationDate = parseDate("2020-03-30 11:15:45");
		Mockito.when(timeService.dateNow()).thenReturn(now);
		UploadRequest request = globaleUploadRequest.clone();
		request.setActivationDate(null);
		request.setExpiryDate(expiryDate);
		request.setNotificationDate(notificationDate);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, request,
				Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		request = uploadRequestGroup.getUploadRequests().iterator().next();
		Date expectedNotifDate = parseDate("2020-03-30 12:00:00");
		Assertions.assertEquals(expectedNotifDate, uploadRequestGroup.getNotificationDate(),
				"The notification date of the group has not been rounded");
		Assertions.assertEquals(expectedNotifDate, request.getNotificationDate(),
				"The notification date of the upload request has not been rounded");
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testRoundNotifDateWhenCreateUploadRequest() throws BusinessException, ParseException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date now = parseDate("2020-03-12 15:25:00");
		Date expiryDate = parseDate("2020-04-01 9:15:45");
		Date notificationDate = parseDate("2020-03-30 11:15:45");
		Mockito.when(timeService.dateNow()).thenReturn(now);
		UploadRequest request = globaleUploadRequest.clone();
		request.setActivationDate(now);
		request.setExpiryDate(expiryDate);
		request.setNotificationDate(notificationDate);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, request,
				Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		request = uploadRequestGroup.getUploadRequests().iterator().next();
		Date expectedNotifDate = parseDate("2020-03-30 12:00:00");
		Assertions.assertEquals(expectedNotifDate, uploadRequestGroup.getNotificationDate(),
				"The notification date of the group has not been rounded");
		request = uploadRequestGroup.getUploadRequests().iterator().next();
		Assertions.assertEquals(expectedNotifDate, request.getNotificationDate(),
				"The notification date of the upload request has not been rounded");
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFailToCreatePastUploadRequest() throws BusinessException, ParseException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
		calendar.setTime(parseDate("2020-04-05 11:05:00"));
		Date past = calendar.getTime();
		calendar.setTime(parseDate("2020-05-12 15:25:00"));
		Date now = calendar.getTime();
		Mockito.when(timeService.dateNow()).thenReturn(now);
		Date expiryDate = parseDate("2020-10-12 15:25:00");
		Date notifDate = parseDate("2020-10-10 10:25:00");
		UploadRequest uploadRequest = createSimpleUploadRequest(past);
		uploadRequest.setExpiryDate(expiryDate);
		uploadRequest.setNotificationDate(notifDate);
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			uploadRequestGroupService.create(john, john, uploadRequest, Lists.newArrayList(yoda), "This is a subject",
					"This is a body", false);
		});
		Assertions.assertEquals(BusinessErrorCode.UPLOAD_REQUEST_ACTIVATION_DATE_INVALID, exception.getErrorCode());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateFailedUploadRequestFRTimezone() throws BusinessException, ParseException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
		calendar.setTime(parseDate("2021-02-22 03:58:00"));
		Date now = calendar.getTime();
		calendar.setTime(parseDate("2021-02-23 05:00:00"));
		Date future = calendar.getTime();
		Mockito.when(timeService.dateNow()).thenReturn(now);
		Date expiryDate = parseDate("2021-03-22 02:00:00");
		Date notifDate = parseDate("2021-03-21 09:00:00");
		UploadRequest uploadRequest = createSimpleUploadRequest(future);
		uploadRequest.setExpiryDate(expiryDate);
		uploadRequest.setNotificationDate(notifDate);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, uploadRequest,
				Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		uploadRequest = uploadRequestGroup.getUploadRequests().iterator().next();
		Date expectedActivationDate = parseDate("2021-02-23 05:00:00");
		Assertions.assertEquals(expectedActivationDate, uploadRequestGroup.getActivationDate(),
				"The activation date of the group is incorrect");
		Assertions.assertEquals(expectedActivationDate, uploadRequest.getActivationDate(),
				"The activation date of the upload request is incorrect");
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateFailedUploadRequestVNTimezone() throws BusinessException, ParseException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
		calendar.setTime(parseDate("2021-02-22 03:58:00"));
		Date now = calendar.getTime();
		calendar.setTime(parseDate("2021-02-23 05:00:00"));
		Date future = calendar.getTime();
		Mockito.when(timeService.dateNow()).thenReturn(now);
		Date expiryDate = parseDate("2021-03-22 02:00:00");
		Date notifDate = parseDate("2021-03-21 09:00:00");
		UploadRequest uploadRequest = createSimpleUploadRequest(future);
		uploadRequest.setExpiryDate(expiryDate);
		uploadRequest.setNotificationDate(notifDate);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, uploadRequest,
				Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		uploadRequest = uploadRequestGroup.getUploadRequests().iterator().next();
		Date expectedActivationDate = parseDate("2021-02-23 05:00:00");
		Assertions.assertEquals(expectedActivationDate, uploadRequestGroup.getActivationDate(),
				"The activation date of the group is incorrect");
		Assertions.assertEquals(expectedActivationDate, uploadRequest.getActivationDate(),
				"The activation date of the upload request is incorrect");
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreatePastUploadRequest() throws BusinessException, ParseException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date now = parseDate("2021-02-24 15:00:00");
		Date future = parseDate("2021-02-24 13:00:00");
		Mockito.when(timeService.dateNow()).thenReturn(now);
		Date expiryDate = parseDate("2021-03-22 02:00:00");
		Date notifDate = parseDate("2021-03-21 09:00:00");
		UploadRequest uploadRequest = createSimpleUploadRequest(future);
		uploadRequest.setExpiryDate(expiryDate);
		uploadRequest.setNotificationDate(notifDate);
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			uploadRequestGroupService.create(john, john, uploadRequest, Lists.newArrayList(yoda), "This is a subject",
					"This is a body", false);
		});
		Assertions.assertEquals(BusinessErrorCode.UPLOAD_REQUEST_ACTIVATION_DATE_INVALID, exception.getErrorCode());
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testUploadRequestActivationPostponed() throws BusinessException, ParseException {
		// validate expiration date with a postponed activation date
		// expiration should have a minimum value activation date
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date now = parseDate("2021-02-24 15:00:00");
		Date future = parseDate("2021-03-24 13:00:00");
		Mockito.when(timeService.dateNow()).thenReturn(now);
		UploadRequest uploadRequest = createSimpleUploadRequest(future);
		uploadRequest.setExpiryDate(null);
		uploadRequest.setNotificationDate(null);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(jane, jane, uploadRequest,
				Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		Assertions.assertEquals(UploadRequestStatus.CREATED, uploadRequestGroup.getStatus());
		uploadRequest = uploadRequestGroup.getUploadRequests().iterator().next();
		// default from the functionality expiration ==> 3 months after activation date
		Date expectedExpiryDate = parseDate("2021-06-24 13:00:00");
		// default from the functionality notification ==> 7 days before expiration
		Date expectedNotificationDate = parseDate("2021-06-17 13:00:00");
		// by default expiration and notification date will have a rounded default value
		// computed from functionality
		Assertions.assertEquals(expectedExpiryDate, uploadRequestGroup.getExpiryDate(),
				"The expiry date should be activation date + 3 months ");
		Assertions.assertEquals(expectedNotificationDate, uploadRequestGroup.getNotificationDate(),
				"The notification date should be 7 days before expiration date");
		Assertions.assertEquals(expectedExpiryDate, uploadRequest.getExpiryDate(),
				"The expiry date should be activation date + 3 months");
		Assertions.assertEquals(expectedNotificationDate, uploadRequest.getNotificationDate(),
				"The notification date should be 7 days before expiration date");

	}
	
	@Test
	public void testUploadRequestNotificationDate() throws BusinessException, ParseException {
		// test to validate that notification date should have a minimum value expiration date
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date now = parseDate("2021-02-24 15:05:00");
		Date expiryDate = parseDate("2021-03-24 13:00:00");
		Date notifyDate = parseDate("2021-03-25 13:00:00");
		Mockito.when(timeService.dateNow()).thenReturn(now);
		UploadRequest uploadRequest = createSimpleUploadRequest(null);
		uploadRequest.setExpiryDate(expiryDate);
		uploadRequest.setNotificationDate(notifyDate);
		// when notification is after expiration an exception is thrown
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			uploadRequestGroupService.create(jane, jane, uploadRequest,
					Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		});
		Assertions.assertEquals(BusinessErrorCode.UPLOAD_REQUEST_NOTIFICATION_DATE_INVALID, exception.getErrorCode());
		notifyDate = parseDate("2021-03-02 14:00:00");
		uploadRequest.setNotificationDate(notifyDate);
		BusinessException exception2 = Assertions.assertThrows(BusinessException.class, () -> {
			uploadRequestGroupService.create(jane, jane, uploadRequest,
					Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		});
		Assertions.assertEquals(BusinessErrorCode.UPLOAD_REQUEST_NOTIFICATION_DATE_INVALID, exception2.getErrorCode());
		// notification date can be between [expiration and expiration - notification functionality max value]
		notifyDate = parseDate("2021-03-22 13:00:00");
		uploadRequest.setNotificationDate(notifyDate);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(jane, jane, uploadRequest,
				Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		Assertions.assertEquals(expiryDate, uploadRequestGroup.getExpiryDate());
		Assertions.assertEquals(notifyDate, uploadRequestGroup.getNotificationDate());
		Assertions.assertEquals(expiryDate, uploadRequest.getExpiryDate());
		Assertions.assertEquals(notifyDate, uploadRequest.getNotificationDate());
	}
	
}