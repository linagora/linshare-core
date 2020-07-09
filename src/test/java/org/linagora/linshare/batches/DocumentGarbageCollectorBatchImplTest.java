/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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
package org.linagora.linshare.batches;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import org.apache.cxf.helpers.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.runner.BatchRunner;
import org.linagora.linshare.mongo.entities.DocumentGarbageCollecteur;
import org.linagora.linshare.mongo.repository.DocumentGarbageCollectorMongoRepository;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-mongo-java-server.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
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
	private BatchRunner batchRunner;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	private LoadingServiceTestDatas datas;

	private Account john;

	private File tempFile, tempFile2;

	private InputStream stream;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		tempFile = File.createTempFile("linshare-test-1", ".tmp");
		tempFile2 = File.createTempFile("linshare-test-2", ".tmp");
		stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		john = datas.getUser1();
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
		List<DocumentGarbageCollecteur> garbageCollectors = documentGarbageRepository.findAll();
		Assertions.assertEquals(1, garbageCollectors.size());
		for (DocumentGarbageCollecteur documentGarbageCollector : garbageCollectors) {
			Integer hourInterval = 0 - cal.getMinimum(Calendar.HOUR_OF_DAY);
			cal.setTime(documentGarbageCollector.getCreationDate());
			cal.add(Calendar.HOUR_OF_DAY, hourInterval);
			documentGarbageCollector.setCreationDate(cal.getTime());
			documentGarbageRepository.save(documentGarbageCollector);
		}
		documentEntryBusinessService.deleteDocumentEntry(createDocumentEntry2);
		Assertions.assertEquals(2, documentGarbageRepository.findAll().size());
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(documentGarbageCollectorBatchImpl);
		Assertions.assertTrue(batchRunner.execute(batches), "At least one batch failed.");
		Assertions.assertEquals(0, documentEntryRepository.findAll().size());
		// The garbage will not pick up this document because it's deleted in the same
		// day with garbage execution time.
		Assertions.assertEquals(2, documentGarbageRepository.findAll().size());
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
		List<DocumentGarbageCollecteur> garbageCollectors = documentGarbageRepository.findAll();
		Assertions.assertEquals(1, garbageCollectors.size());
		for (DocumentGarbageCollecteur documentGarbageCollector : garbageCollectors) {
			Integer hourInterval = 0 - cal.getMaximum(Calendar.HOUR_OF_DAY);
			cal.setTime(documentGarbageCollector.getCreationDate());
			cal.add(Calendar.HOUR_OF_DAY, hourInterval);
			documentGarbageCollector.setCreationDate(cal.getTime());
			documentGarbageRepository.save(documentGarbageCollector);
		}
		documentEntryBusinessService.deleteDocumentEntry(createDocumentEntry2);
		Assertions.assertEquals(2, documentGarbageRepository.findAll().size());
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(documentGarbageCollectorBatchImpl);
		Assertions.assertTrue(batchRunner.execute(batches), "At least one batch failed.");
		Assertions.assertEquals(0, documentEntryRepository.findAll().size());
		// The garbage will pick up this document because it's deleted at the day before
		// with garbage execution time.
		Assertions.assertEquals(1, documentGarbageRepository.findAll().size());
	}

}