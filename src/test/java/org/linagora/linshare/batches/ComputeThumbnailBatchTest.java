/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2017-2022 LINAGORA
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
import java.util.List;
import java.util.Set;

import org.apache.cxf.helpers.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.runner.BatchRunner;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml", 
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
		"classpath:springContext-test.xml",
		})
public class ComputeThumbnailBatchTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private BatchRunner batchRunner;

	@Autowired
	@Qualifier("computeThumbnailBatch")
	private GenericBatch computeThumbnailBatch;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	private DocumentEntryBusinessService documentEntryBusinessService;

	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private FileDataStore fileDataStore;

	private LoadingServiceTestDatas datas;

	private User jane;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		jane = datas.getUser2();
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testBatch() throws BusinessException, JobExecutionException {
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(computeThumbnailBatch);
		Assertions.assertTrue(batchRunner.execute(batches), "At least one batch failed.");
	}

	@Disabled //FIXME : Handle issues (related to thumbnail server) and enable the test
	@Test
	public void testBatchExecution() throws BusinessException, JobExecutionException, IOException {
		Document document = createDocument();
		documentRepository.update(document);
		Assertions.assertTrue(document != null);
		Assertions.assertTrue(document.getThmbUuid() != null);
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(computeThumbnailBatch);
		Assertions.assertTrue(batchRunner.execute(batches), "At least one batch failed.");
		Assertions.assertTrue(document.getThmbUuid() == null);
		FileMetaData thmbMetadata = new FileMetaData(FileMetaDataKind.THUMBNAIL, document);
		Assertions.assertTrue(thmbMetadata.getUuid() == null);
	}

	private Document createDocument() throws BusinessException, IOException {
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		tempFile.deleteOnExit();
		InputStream stream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("linshare-default.properties");
		IOUtils.copy(stream, new FileOutputStream(tempFile));
		Calendar cal = Calendar.getInstance();
		DocumentEntry createDocumentEntry = documentEntryBusinessService.createDocumentEntry(jane, tempFile,
				tempFile.length(), "file", null, false, null, "text/plain", cal, false, null);
		Document document = createDocumentEntry.getDocument();
		Set<DocumentEntry> documentEntries = Sets.newHashSet();
		documentEntries.add(createDocumentEntry);
		document.setDocumentEntries(documentEntries);
		File tempThmbFile = File.createTempFile("thumbnail", "png");
		tempThmbFile.deleteOnExit();
		FileMetaData metaDataThmb = new FileMetaData(FileMetaDataKind.THUMBNAIL_SMALL, "image/png",
				tempThmbFile.length(), "thumbnail");
		metaDataThmb = fileDataStore.add(Files.asByteSource(tempThmbFile), metaDataThmb);
		document.setThmbUuid(metaDataThmb.getUuid());
		document.setComputeThumbnail(true);
		return document;
	}
}
