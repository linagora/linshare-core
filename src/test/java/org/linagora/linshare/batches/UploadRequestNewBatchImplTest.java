/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.UploadRequestRepository;
import org.linagora.linshare.core.runner.BatchRunner;
import org.linagora.linshare.utils.LinShareWiser;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.google.common.collect.Lists;

@ContextConfiguration(locations = { "classpath:springContext-datasource.xml", 
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml", 
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-batches.xml",
		"classpath:springContext-test.xml" })
public class UploadRequestNewBatchImplTest extends 
	AbstractTransactionalJUnit4SpringContextTests {

	private static Logger logger = LoggerFactory.getLogger(UploadRequestNewBatchImplTest.class);

	@Autowired
	private BatchRunner batchRunner;

	@Qualifier("closeExpiredUploadRequestBatch")
	@Autowired
	private GenericBatch closeExpiredUploadResquestBatch;

	@Qualifier("enableUploadRequestBatch")
	@Autowired
	private GenericBatch enableUploadResquestBatch;

	@Qualifier("notifyBeforeExpirationUploadRequestBatch")
	@Autowired
	private GenericBatch notifyBeforeExpirationUploadResquestBatch;

	@Autowired
	private UploadRequestRepository uploadRequestRepository;

	private LinShareWiser wiser;

	public UploadRequestNewBatchImplTest() {
		super();
		wiser = new LinShareWiser(2525);
	}

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		this.executeSqlScript("import-tests-close-expired-upload-requests.sql", false);
		wiser.start();
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		wiser.stop();
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testLaunching() throws BusinessException,
		JobExecutionException {
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(closeExpiredUploadResquestBatch);
		batches.add(enableUploadResquestBatch);
		batches.add(notifyBeforeExpirationUploadResquestBatch);
		Assert.assertTrue("At least one batch failed.", batchRunner.execute(batches));
		wiser.checkGeneratedMessages();
	}

	@Test
	public void testBatches() throws BusinessException,
		JobExecutionException {
		BatchRunContext batchRunContext = new BatchRunContext();
		List<String> l = closeExpiredUploadResquestBatch.getAll(batchRunContext);
		Assert.assertEquals(l.size(), 2);
		ResultContext c;
		UploadRequest u;
		int i;
		for (i = 0; i < l.size(); i++) {
			u = uploadRequestRepository.findByUuid(l.get(i));
			Assert.assertEquals(u.getStatus(), UploadRequestStatus.STATUS_ENABLED);
			c = closeExpiredUploadResquestBatch.execute(batchRunContext, l.get(i), l.size(), i);
			Assert.assertEquals(c.getIdentifier(), l.get(i));
			u = uploadRequestRepository.findByUuid(l.get(i));
			Assert.assertEquals(u.getUuid(), l.get(i));
			Assert.assertEquals(u.getStatus(), UploadRequestStatus.STATUS_CLOSED);
		}
		l = enableUploadResquestBatch.getAll(batchRunContext);
		Assert.assertEquals(l.size(), 3);
		for (i = 0; i < l.size(); i++) {
			u = uploadRequestRepository.findByUuid(l.get(i));
			Assert.assertEquals(u.getStatus(), UploadRequestStatus.STATUS_CREATED);
			c = enableUploadResquestBatch.execute(batchRunContext, l.get(i), l.size(), i);
			Assert.assertEquals(c.getIdentifier(), l.get(i));
			u = uploadRequestRepository.findByUuid(l.get(i));
			Assert.assertEquals(u.getUuid(), l.get(i));
			Assert.assertEquals(u.getStatus(), UploadRequestStatus.STATUS_ENABLED);
		}
		l = notifyBeforeExpirationUploadResquestBatch.getAll(batchRunContext);
		Assert.assertEquals(l.size(), 3);
		for (i = 0; i < l.size(); i++) {
			c = notifyBeforeExpirationUploadResquestBatch.execute(batchRunContext, l.get(i), l.size(), i);
			Assert.assertEquals(c.getIdentifier(), l.get(i));
			u = uploadRequestRepository.findByUuid(l.get(i));
			Assert.assertEquals(u.getUuid(), l.get(i));
			Assert.assertEquals(u.getStatus(), UploadRequestStatus.STATUS_ENABLED);
			Assert.assertEquals(u.isNotified(), true);
		}
		wiser.checkGeneratedMessages();
	}
}
