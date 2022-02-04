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
package org.linagora.linshare.batches;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.cxf.helpers.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.ContactRepository;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.runner.BatchRunner;
import org.linagora.linshare.core.service.UploadRequestEntryService;
import org.linagora.linshare.core.service.UploadRequestGroupService;
import org.linagora.linshare.mongo.entities.DocumentGarbageCollecteur;
import org.linagora.linshare.mongo.repository.DocumentGarbageCollectorMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

@DirtiesContext
@ExtendWith(SpringExtension.class)
@Sql({
"/import-tests-upload-request.sql" })
@Transactional
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-mongo-init.xml",
		"classpath:springContext-batches.xml",
		"classpath:springContext-test.xml" })
public class DocumentGarbageCollectorBatchImplTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

	@Autowired
	@Qualifier("documentGarbageCollectorBatch")
	private GenericBatch documentGarbageCollectorBatchImpl;

	@Autowired
	private DocumentGarbageCollectorMongoRepository documentGarbageRepository;

	@Autowired
	private DocumentEntryRepository documentEntryRepository;

	@Autowired
	private DocumentEntryBusinessService documentEntryBusinessService;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private UploadRequestGroupService uploadRequestGroupService;

	@Autowired
	private UploadRequestEntryService uploadRequestEntryService;

	@Autowired
	private BatchRunner batchRunner;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	private Account john;
	
	private Contact yoda;

	private File tempFile, tempFile2, tempFile3, tempFile4;

	private InputStream stream;
	
	private UploadRequest ur, urEntity;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		tempFile = File.createTempFile("linshare-test-1", ".tmp");
		tempFile2 = File.createTempFile("linshare-test-2", ".tmp");
		tempFile3 = File.createTempFile("linshare-test-3", ".tmp");
		tempFile4 = File.createTempFile("linshare-test-4", ".tmp");
		stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");
		john = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		yoda = contactRepository.findByMail("yoda@linshare.org");
		ur = new UploadRequest();
		ur.setCanClose(true);
		ur.setMaxDepositSize((long) 100000);
		ur.setMaxFileCount(Integer.valueOf(3));
		ur.setMaxFileSize((long) 100000);
		ur.setStatus(UploadRequestStatus.CREATED);
		ur.setExpiryDate(new Date());
		ur.setProtectedByPassword(false);
		ur.setCanEditExpiryDate(true);
		ur.setCanDelete(true);
		ur.setLocale(Language.ENGLISH);
		ur.setActivationDate(null);
		UploadRequestGroup uploadRequestGroupIndiv = uploadRequestGroupService.create(john, (User) john, ur, Lists.newArrayList(yoda), "This is a subject",
				"This is a body", false);
		urEntity = uploadRequestGroupIndiv.getUploadRequests().iterator().next();
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		stream.close();
		documentGarbageRepository.deleteAll();
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testBatchExecution1() throws IOException {
		IOUtils.copy(stream, new FileOutputStream(tempFile));
		Calendar cal = Calendar.getInstance();
		DocumentEntry createDocumentEntry = documentEntryBusinessService.createDocumentEntry(john, tempFile,
				tempFile.length(), "file.txt", null, false, null, "text/plain", cal, false, null);
		IOUtils.copy(stream, new FileOutputStream(tempFile2));
		DocumentEntry createDocumentEntry2 = documentEntryBusinessService.createDocumentEntry(john, tempFile2,
				tempFile2.length(), "file2.txt", null, false, null, "text/plain", cal, false, null);
		documentEntryBusinessService.deleteDocumentEntry(createDocumentEntry);
		// Create 2 upload request entries
		IOUtils.copy(stream, new FileOutputStream(tempFile3));
		UploadRequestEntry uploadRequestEntry = uploadRequestEntryService.create(john, john, tempFile3,
				tempFile3.getName(), "comment", false, null, urEntity.getUploadRequestURLs().iterator().next());
		InputStream stream1 = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("linshare-default.properties");
		IOUtils.copy(stream1, new FileOutputStream(tempFile4));
		UploadRequestEntry uploadRequestEntry2 = uploadRequestEntryService.create(john, john, tempFile4,
				tempFile4.getName(), "comment", false, null, urEntity.getUploadRequestURLs().iterator().next());
		// delete one upload request entry
		uploadRequestEntryService.delete((User) john, (User) john, uploadRequestEntry.getUuid());
		List<DocumentGarbageCollecteur> garbageCollectors = documentGarbageRepository.findAll();
		Assertions.assertEquals(2, garbageCollectors.size());
		for (DocumentGarbageCollecteur documentGarbageCollector : garbageCollectors) {
			Integer hourInterval = 0 - cal.getMinimum(Calendar.HOUR_OF_DAY);
			cal.setTime(documentGarbageCollector.getCreationDate());
			cal.add(Calendar.HOUR_OF_DAY, hourInterval);
			documentGarbageCollector.setCreationDate(cal.getTime());
			documentGarbageRepository.save(documentGarbageCollector);
		}
		documentEntryBusinessService.deleteDocumentEntry(createDocumentEntry2);
		uploadRequestEntryService.delete((User) john, (User) john, uploadRequestEntry2.getUuid());
		Assertions.assertEquals(4, documentGarbageRepository.findAll().size());
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(documentGarbageCollectorBatchImpl);
		Assertions.assertTrue(batchRunner.execute(batches), "At least one batch failed.");
		Assertions.assertEquals(0, documentEntryRepository.findAll().size());
		// The garbage will not pick up this document because it's deleted in the same
		// day with garbage execution time.
		Assertions.assertEquals(4, documentGarbageRepository.findAll().size());
	}

	@Test
	public void testBatchExecution2() throws IOException {
		InputStream stream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("linshare-default.properties");
		IOUtils.copy(stream, new FileOutputStream(tempFile));
		Calendar cal = Calendar.getInstance();
		DocumentEntry createDocumentEntry = documentEntryBusinessService.createDocumentEntry(john, tempFile,
				tempFile.length(), "file.txt", null, false, null, "text/plain", cal, false, null);
		IOUtils.copy(stream, new FileOutputStream(tempFile2));
		DocumentEntry createDocumentEntry2 = documentEntryBusinessService.createDocumentEntry(john, tempFile2,
				tempFile2.length(), "file2.txt", null, false, null, "text/plain", cal, false, null);
		documentEntryBusinessService.deleteDocumentEntry(createDocumentEntry);
		// Create 2 upload request entries
		IOUtils.copy(stream, new FileOutputStream(tempFile3));
		UploadRequestEntry uploadRequestEntry = uploadRequestEntryService.create(john, john, tempFile3,
				tempFile3.getName(), "comment", false, null, urEntity.getUploadRequestURLs().iterator().next());
		InputStream stream1 = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("linshare-default.properties");
		IOUtils.copy(stream1, new FileOutputStream(tempFile4));
		UploadRequestEntry uploadRequestEntry2 = uploadRequestEntryService.create(john, john, tempFile4,
				tempFile4.getName(), "comment", false, null, urEntity.getUploadRequestURLs().iterator().next());
		uploadRequestEntryService.delete((User) john, (User) john, uploadRequestEntry.getUuid());
		List<DocumentGarbageCollecteur> garbageCollectors = documentGarbageRepository.findAll();
		Assertions.assertEquals(2, garbageCollectors.size());
		for (DocumentGarbageCollecteur documentGarbageCollector : garbageCollectors) {
			Integer hourInterval = 0 - cal.getMaximum(Calendar.HOUR_OF_DAY);
			cal.setTime(documentGarbageCollector.getCreationDate());
			cal.add(Calendar.HOUR_OF_DAY, hourInterval);
			documentGarbageCollector.setCreationDate(cal.getTime());
			documentGarbageRepository.save(documentGarbageCollector);
		}
		documentEntryBusinessService.deleteDocumentEntry(createDocumentEntry2);
		uploadRequestEntryService.delete((User) john, (User) john, uploadRequestEntry2.getUuid());
		Assertions.assertEquals(4, documentGarbageRepository.findAll().size());
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(documentGarbageCollectorBatchImpl);
		Assertions.assertTrue(batchRunner.execute(batches), "At least one batch failed.");
		Assertions.assertEquals(0, documentEntryRepository.findAll().size());
		// The garbage will pick up this document because it's deleted at the day before
		// with garbage execution time.
		Assertions.assertEquals(2, documentGarbageRepository.findAll().size());
	}

}