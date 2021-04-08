/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
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
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.cxf.helpers.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.PasswordService;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.SystemAccount;
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
import org.linagora.linshare.core.service.UploadRequestEntryService;
import org.linagora.linshare.core.service.UploadRequestGroupService;
import org.linagora.linshare.core.service.UploadRequestService;
import org.linagora.linshare.core.service.UploadRequestUrlService;
import org.linagora.linshare.mongo.entities.ChangeUploadRequestUrlPassword;
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
@Disabled
public class UploadRequestServiceImplTest {

	private static Logger logger = LoggerFactory.getLogger(UploadRequestServiceImplTest.class);

	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;

	@Autowired
	private UploadRequestGroupService uploadRequestGroupService;
	
	@Autowired
	private ContactRepository repository;

	@Autowired
	private UploadRequestService service;

	@Autowired
	private UploadRequestUrlService requestUrlService;

	@Autowired
	private PasswordService passwordService;

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

	private UploadRequestEntry uploadRequestEntry;

	private UploadRequest ure = new UploadRequest();

	private UploadRequest eJohn;

	private UploadRequest eJane;

	private User john;

	private User jane;

	private final InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");

	private final String fileName = "linshare-default.properties";

	private final String comment = "file description";

	private Contact yoda;

	public UploadRequestServiceImplTest() {
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
//		UPLOAD REQUEST CREATE
		ure.setCanClose(true);
		ure.setMaxDepositSize((long) 100);
		ure.setMaxFileCount(Integer.valueOf(3));
		ure.setMaxFileSize((long) 50);
		ure.setProtectedByPassword(false);
		ure.setCanEditExpiryDate(true);
		ure.setCanDelete(true);
		ure.setLocale(Language.ENGLISH);
		ure.setActivationDate(null);
		ure.setExpiryDate(DateUtils.addMonths(new Date(), 1));
		UploadRequestGroup uploadRequestGroupJohn = uploadRequestGroupService.create(john, john, ure, Lists.newArrayList(yoda), "This is a subject",
				"This is a body", false);
		eJohn = uploadRequestGroupJohn.getUploadRequests().iterator().next();
		ure.setActivationDate(null);
		UploadRequestGroup uploadRequestGroupJane = uploadRequestGroupService.create(jane, jane, ure, Lists.newArrayList(yoda), "This is a subject",
				"This is a body", true);
		eJane = uploadRequestGroupJane.getUploadRequests().iterator().next();
//		END OF UPLOAD REQUEST CREATE
		Assertions.assertEquals(john, (User) eJohn.getUploadRequestGroup().getOwner());
		Assertions.assertEquals(eJohn.getStatus(), UploadRequestStatus.ENABLED);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void findRequest() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequest tmp = service.find(john, john, eJohn.getUuid());
		Assertions.assertEquals(tmp.getStatus(), eJohn.getStatus());
		Assertions.assertEquals(tmp.getUploadRequestGroup().getOwner(), eJohn.getUploadRequestGroup().getOwner());
		Assertions.assertEquals(tmp.getStatus(), eJohn.getStatus());
		Assertions.assertEquals(tmp.getMaxDepositSize(), eJohn.getMaxDepositSize());
		Assertions.assertEquals(tmp.getMaxFileCount(), eJohn.getMaxFileCount());
		Assertions.assertEquals(tmp.getMaxFileSize(), eJohn.getMaxFileSize());
		Assertions.assertEquals(tmp.isProtectedByPassword(), eJohn.isProtectedByPassword());
		Assertions.assertEquals(tmp.isCanClose(), eJohn.isCanClose());
		Assertions.assertEquals(tmp.isCanDelete(), eJohn.isCanDelete());
		Assertions.assertEquals(tmp.getUploadRequestGroup().getAbstractDomain(), eJohn.getUploadRequestGroup().getAbstractDomain());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void update() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequest tmp = service.find(john, john, eJohn.getUuid());
		tmp.setCanClose(false);
		tmp.setCanDelete(false);
		tmp.setCanEditExpiryDate(false);
		tmp.setMaxFileCount(Integer.valueOf(2));
		tmp = service.update(john, john, tmp.getUuid(), tmp, false);
		Assertions.assertEquals(tmp.isCanClose(), false);
		Assertions.assertEquals(tmp.isCanDelete(), false);
		Assertions.assertEquals(tmp.isCanEditExpiryDate(), false);
		Assertions.assertEquals(tmp.getMaxFileCount(), Integer.valueOf(2));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void UpdateUploadRequestStatus() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequest tmp = eJohn.clone();
		tmp = service.updateStatus(john, john, tmp.getUuid(), UploadRequestStatus.CLOSED, false);
		Assertions.assertEquals(tmp.getStatus(), UploadRequestStatus.CLOSED);
		Assertions.assertEquals(john, (User) eJohn.getUploadRequestGroup().getOwner());
		// Status ARCHIVED
		tmp = service.updateStatus(john, john, tmp.getUuid(), UploadRequestStatus.ARCHIVED, true);
		Assertions.assertEquals(tmp.getStatus(), UploadRequestStatus.ARCHIVED);
		Assertions.assertEquals(john, (User) eJohn.getUploadRequestGroup().getOwner());
		// STATUS DELETED
		tmp = service.updateStatus(john, john, tmp.getUuid(), UploadRequestStatus.DELETED, false);
		Assertions.assertEquals(tmp.getStatus(), UploadRequestStatus.DELETED);
		Assertions.assertEquals(john, (User) eJohn.getUploadRequestGroup().getOwner());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void closeUploadRequestByOwner() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequest uploadRequest = eJane.clone();
		uploadRequest = service.updateStatus(jane, jane, uploadRequest.getUuid(), UploadRequestStatus.CLOSED, false);
		Assertions.assertEquals(uploadRequest.getStatus(), UploadRequestStatus.CLOSED);
		Assertions.assertEquals(uploadRequest.getUploadRequestGroup().getStatus(), UploadRequestStatus.CLOSED);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void closeRequestByRecipient() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequest uploadRequest = eJane.clone();
		uploadRequest = service.closeRequestByRecipient(eJane.getUploadRequestURLs().iterator().next());
		Assertions.assertEquals(uploadRequest.getStatus(), UploadRequestStatus.CLOSED);
		Assertions.assertEquals(uploadRequest.getUploadRequestGroup().getStatus(), UploadRequestStatus.CLOSED);
		Assertions.assertEquals(jane, (User) eJane.getUploadRequestGroup().getOwner());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testForbidCloseRequestByRecipient() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequest uploadRequest = eJane.clone();
		uploadRequest.setCanClose(false);
		service.update(jane, jane, uploadRequest.getUuid(), uploadRequest, true);
		Assertions.assertFalse(uploadRequest.isCanClose());
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			service.closeRequestByRecipient(eJane.getUploadRequestURLs().iterator().next());
		});
		Assertions.assertEquals(BusinessErrorCode.UPLOAD_REQUEST_CLOSURE_FORBIDDEN, exception.getErrorCode());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void changeUploadRequestUrlPwd() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		String oldPwd = "Linsh@re2020";
		String newPwd = "Linsh@re2021";
		UploadRequestUrl requestUrl = eJohn.getUploadRequestURLs().iterator().next();
		requestUrl.setPassword(passwordService.encode(oldPwd));
		ChangeUploadRequestUrlPassword resetURUrlPassword = new ChangeUploadRequestUrlPassword(newPwd, oldPwd);
		SystemAccount uploadRequestSysAccount = requestUrlService.getUploadRequestSystemAccount();
		requestUrlService.changePassword(uploadRequestSysAccount, uploadRequestSysAccount, requestUrl.getUuid(),
				resetURUrlPassword);
		Assertions.assertTrue(passwordService.matches(newPwd, requestUrl.getPassword()));
		Assertions.assertFalse(requestUrl.isDefaultPassword());
		Assertions.assertTrue(passwordService.matches(resetURUrlPassword.getOldPassword(), requestUrl.getOriginalPassword()));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void updateStatusWithError() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		try {
			eJohn.updateStatus(UploadRequestStatus.CLOSED);
			UploadRequest tmp = service.updateRequest(john, john, eJohn);
			tmp = service.find(john, john, eJohn.getUuid());
			tmp.updateStatus(UploadRequestStatus.ENABLED);
			tmp = service.updateRequest(john, john, tmp);
		} catch (BusinessException ex) {
			Assertions.assertEquals("Cannot transition from CLOSED to ENABLED.", ex.getMessage());
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testUploadRequestCreateDocumentEntry() throws BusinessException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Account actor = jane;
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		uploadRequestEntry = uploadRequestEntryService.create(actor, actor, tempFile, fileName, comment, false, null,
				eJohn.getUploadRequestURLs().iterator().next());
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
	public void testFindAllUploadRequestEntries()
			throws BusinessException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Account actor = jane;
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		uploadRequestEntry = uploadRequestEntryService.create(actor, actor, tempFile, fileName, comment, false, null,
				eJane.getUploadRequestURLs().iterator().next());
		Assertions.assertTrue(uploadRequestEntryRepository.findByUuid(uploadRequestEntry.getUuid()) != null);

		List<UploadRequestEntry> entries = service.findAllEntries(actor, actor, eJane);
		Assertions.assertNotNull(entries);
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
