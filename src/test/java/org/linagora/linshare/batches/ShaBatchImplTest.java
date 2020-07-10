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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.helpers.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.constants.FileSizeUnit;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.constants.TimeUnit;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.FileSizeUnitClass;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Policy;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.TimeUnitClass;
import org.linagora.linshare.core.domain.entities.UnitValueFunctionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.FunctionalityRepository;
import org.linagora.linshare.core.repository.ThreadEntryRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.runner.BatchRunner;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.upgrade.v2_0.Sha256SumUpgradeTaskImpl;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

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
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class ShaBatchImplTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private BatchRunner batchRunner;

	private GenericBatch shaSumBatch;

	/*
	 * beans related to shaSumBatch
	 */
	@Qualifier("accountRepository")
	@Autowired
	private AccountRepository<Account> accountRepository;

	@Autowired
	private ThreadEntryRepository threadEntryRepository;

	@Autowired
	private DocumentEntryBusinessService documentEntryBusinessService;

	@Autowired
	private UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository;

	@Autowired
	private FileDataStore fileDataStore;


	@Autowired
	private DocumentRepository documentRepository;

	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;

	@Autowired
	private DocumentEntryService documentEntryService;

	@Autowired
	private DocumentEntryRepository documentEntryRepository;

	private LoadingServiceTestDatas datas;

	private DocumentEntry aDocumentEntry;

	private DocumentEntry bDocumentEntry;

	@Autowired
	private FunctionalityRepository functionalityRepository;

	private User jane;
	private final InputStream stream2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");
	private final String fileName2 = "linsahre-test.properties";
	private final String comment2 = "file description sample";
	private final InputStream stream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream("OPTIONAL-springContext-jcloud.xml");
	private final String fileName1 = "linshare-default.properties";
	private final String comment1 = "file description default";

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		jane = datas.getUser2();
		shaSumBatch = new Sha256SumUpgradeTaskImpl(accountRepository, documentRepository, fileDataStore, threadEntryRepository, upgradeTaskLogMongoRepository, documentEntryBusinessService);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testLaunch() throws BusinessException, JobExecutionException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(shaSumBatch);
		Assertions.assertTrue(batchRunner.execute(batches), "At least one batch failed.");
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testShaGetAll() throws BusinessException, JobExecutionException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		BatchRunContext batchRunContext = new BatchRunContext();
		Account actor = jane;
		createFunctionalities();
		File tempFile1 = File.createTempFile("linshare-test-", ".tmp");
		File tempFile2 = File.createTempFile("linshare-test-2", ".tmp");
		List<String> l = Lists.newArrayList();
		IOUtils.transferTo(stream1, tempFile1);
		IOUtils.transferTo(stream2, tempFile2);
		aDocumentEntry = documentEntryService.create(actor, actor, tempFile1, fileName1, comment1, false, null);
		bDocumentEntry = documentEntryService.create(actor, actor, tempFile2, fileName2, comment2, false, null);
		Assertions.assertTrue(documentEntryRepository.findById(aDocumentEntry.getUuid()) != null);
		Assertions.assertTrue(documentEntryRepository.findById(bDocumentEntry.getUuid()) != null);
		aDocumentEntry.getDocument().setSha256sum("UNDEFINED");
		bDocumentEntry.getDocument().setSha256sum("UNDEFINED");
		l = shaSumBatch.getAll(batchRunContext);
		Assertions.assertEquals(l.size(), 2);
	}

	@Test
	public void testSha256Batch() throws BusinessException, JobExecutionException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		BatchRunContext batchRunContext = new BatchRunContext();
		Account actor = jane;
		createFunctionalities();
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		File tempFile2 = File.createTempFile("linshare-test-up", ".tmp");
		List<String> l = Lists.newArrayList();
		IOUtils.transferTo(stream2, tempFile);
		IOUtils.transferTo(stream1, tempFile2);
		aDocumentEntry = documentEntryService.create(actor, actor, tempFile, fileName2, comment2, false, null);
		Assertions.assertTrue(documentEntryRepository.findById(aDocumentEntry.getUuid()) != null);
		aDocumentEntry.getDocument().setSha256sum(null);
		documentRepository.update(aDocumentEntry.getDocument());
		l = shaSumBatch.getAll(batchRunContext);
		int i;
		ResultContext c;
		for (i = 0; i < l.size(); i++) {
			c = shaSumBatch.execute(batchRunContext, l.get(i), l.size(), i);
			Assertions.assertEquals(c.getIdentifier(), l.get(i));
			Document doc = documentRepository.findByUuid(l.get(i));
			Assertions.assertEquals("0679aeee7c0c5c4a9a4322326f0243c29025a696a4c2436758470d30ec9488a0", doc.getSha256sum());
			Assertions.assertEquals("b09efecaccbf880c60539f04659489df54698afd", doc.getSha1sum());
		}
		logger.info(LinShareTestConstants.END_TEST);
	}

	private void createFunctionalities() throws IllegalArgumentException, BusinessException {
		Integer value = 1;
		ArrayList<Functionality> functionalities = new ArrayList<Functionality>();
		functionalities.add(
				new UnitValueFunctionality("MIME_TYPE",
					true,
					new Policy(Policies.ALLOWED, false),
					new Policy(Policies.ALLOWED, false),
					jane.getDomain(),
					value,
					new FileSizeUnitClass(FileSizeUnit.GIGA)
				)
		);
		functionalities.add(
				new UnitValueFunctionality("ANTIVIRUS",
					true,
					new Policy(Policies.ALLOWED, false),
					new Policy(Policies.ALLOWED, false),
					jane.getDomain(),
					value,
					new FileSizeUnitClass(FileSizeUnit.GIGA)
				)
		);
		functionalities.add(
				new UnitValueFunctionality("ENCIPHERMENT",
					true,
					new Policy(Policies.ALLOWED, true),
					new Policy(Policies.ALLOWED, true),
					jane.getDomain(),
					value,
					new FileSizeUnitClass(FileSizeUnit.GIGA)
				)
		);
		functionalities.add(
				new StringValueFunctionality("TIME_STAMPING",
					true,
					new Policy(Policies.ALLOWED, false),
					new Policy(Policies.ALLOWED, false),
					jane.getDomain(),
					""
				)
		);
		functionalities.add(
				new UnitValueFunctionality("DOCUMENT_EXPIRATION",
					true,
					new Policy(Policies.ALLOWED, false),
					new Policy(Policies.ALLOWED, false),
					jane.getDomain(),
					value,
					new TimeUnitClass(TimeUnit.DAY)
				)
		);
		for (Functionality functionality : functionalities) {
			functionalityRepository.create(functionality);
			jane.getDomain().addFunctionality(functionality);
		}
	}
}
