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
package org.linagora.linshare.batches;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.transaction.Transactional;

import org.apache.cxf.helpers.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.runner.BatchRunner;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.server.embedded.ldap.LdapServerRule;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ExtendWith(LdapServerRule.class)
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
/**
 * Test fails due to injected fake functionalities in 'import-test.sql'
 * TODO: move injected fake Functionalities from 'import-test.sql' to a new script  
 * TODO: Update of functionalities in the the test method will not work 
 * cf: https://ci.linagora.com/linagora/lgs/linshare/products/linshare-core/issues/878 
 */
@Disabled
public class CoumputeNewExpirationUnsharedDocumentBatchImplTest {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private BatchRunner batchRunner;

	@Autowired
	@Qualifier("computeNewExpirationUnsharedDocumentBatch")
	private GenericBatch computeNewExpirationUnsharedDocumentBatch;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;
	
	@Autowired
	private DocumentEntryBusinessService documentEntryBusinessService;
	
	@Autowired
	private FunctionalityReadOnlyService functionalityService;
	
	private User jane;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		jane = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testBatchExecution() throws BusinessException, JobExecutionException, IOException {
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		tempFile.deleteOnExit();
		InputStream stream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("linshare-default.properties");
		IOUtils.copy(stream, new FileOutputStream(tempFile));
		Functionality functionality = functionalityService
				.getDefaultFileExpiryTimeFunctionality(jane.getDomain());
		DocumentEntry documentEntry = documentEntryBusinessService.createDocumentEntry(jane, tempFile,
				tempFile.length(), "file", null, false, null, "text/plain", null, false, null);
		Assertions.assertNotNull(functionality);
		functionality.getActivationPolicy().setStatus(false);
		Assertions.assertNull(documentEntry.getExpirationDate());
		functionality.getActivationPolicy().setStatus(true);
		Assertions.assertTrue(batchRunner.execute(computeNewExpirationUnsharedDocumentBatch), "batch failed");
		Assertions.assertNotNull(documentEntry.getExpirationDate());
	}
}
