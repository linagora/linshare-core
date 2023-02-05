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

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.runner.BatchRunner;
import org.linagora.linshare.server.embedded.ldap.LdapServerRule;
import org.quartz.JobExecutionException;
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
@ExtendWith(LdapServerRule.class)
@Sql({
	
	"/import-tests-check-inconsistent.sql" })
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
		"classpath:springContext-test.xml" })
public class CheckUserStillInconsistentBatchImplTest {

	private static Logger logger = LoggerFactory.getLogger(CheckUserStillInconsistentBatchImplTest.class);

	@Autowired
	private BatchRunner batchRunner;

	@Qualifier("checkIfUserStillInconsistentBatch")
	@Autowired
	private GenericBatch checkIfUserStillInconsistentBatch;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	/**
	 * Test that checkIfUserStillInconsistentBatch is executed 
	 * @throws BusinessException
	 * @throws JobExecutionException
	 */
	@Test
	public void testBatch() throws BusinessException, JobExecutionException {
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(checkIfUserStillInconsistentBatch);
		Assertions.assertTrue(batchRunner.execute(batches), "At least one batch failed.");
	}
	
	/**
	 * Test that after batch execution only one 
	 * account (Clark  refer to /import-tests-check-inconsistent.sql) still inconsistent 
	 * @throws BusinessException
	 * @throws JobExecutionException
	 */
	@Test
	public void testBatchExecution() throws BusinessException, JobExecutionException {
		BatchRunContext batchRunContext = new BatchRunContext();
		List<String> inconsistentUuids = checkIfUserStillInconsistentBatch.getAll(batchRunContext);
		Assertions.assertEquals(inconsistentUuids.size(), 4);
		ResultContext c;
		for (int i = 0; i < inconsistentUuids.size(); i++) {
			c = checkIfUserStillInconsistentBatch.execute(batchRunContext, inconsistentUuids.get(i), inconsistentUuids.size(), i);
			Assertions.assertEquals(c.getIdentifier(), inconsistentUuids.get(i));
		}
		inconsistentUuids = checkIfUserStillInconsistentBatch.getAll(batchRunContext);
		Assertions.assertEquals(inconsistentUuids.size(), 1);
	}
}
