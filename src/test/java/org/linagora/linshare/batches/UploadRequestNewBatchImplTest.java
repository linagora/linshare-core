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
import org.linagora.linshare.core.job.quartz.Context;
import org.linagora.linshare.core.job.quartz.LinShareJobBean;
import org.linagora.linshare.core.repository.UploadRequestRepository;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.subethamail.wiser.Wiser;

import com.google.common.collect.Lists;

@ContextConfiguration(locations = { "classpath:springContext-datasource.xml", 
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml", 
		"classpath:springContext-jackRabbit-mock.xml",
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

	private Wiser wiser;

	public UploadRequestNewBatchImplTest() {
		super();
		wiser = new Wiser(2525);
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
		LinShareJobBean job = new LinShareJobBean();
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(closeExpiredUploadResquestBatch);
		batches.add(enableUploadResquestBatch);
		batches.add(notifyBeforeExpirationUploadResquestBatch);
		job.setBatch(batches);
		Assert.assertTrue("At least one batch failed.", job.executeExternal());
	}

	@Test
	public void testBatches() throws BusinessException,
		JobExecutionException {
		List<String> l = closeExpiredUploadResquestBatch.getAll();
		Assert.assertEquals(l.size(), 2);
		Context c;
		UploadRequest u;
		int i;
		for (i = 0; i < l.size(); i++) {
			u = uploadRequestRepository.findByUuid(l.get(i));
			Assert.assertEquals(u.getStatus(), UploadRequestStatus.STATUS_ENABLED);
			c = closeExpiredUploadResquestBatch.execute(l.get(i), l.size(), i);
			Assert.assertEquals(c.getIdentifier(), l.get(i));
			u = uploadRequestRepository.findByUuid(l.get(i));
			Assert.assertEquals(u.getUuid(), l.get(i));
			Assert.assertEquals(u.getStatus(), UploadRequestStatus.STATUS_CLOSED);
		}
		l = enableUploadResquestBatch.getAll();
		Assert.assertEquals(l.size(), 3);
		for (i = 0; i < l.size(); i++) {
			u = uploadRequestRepository.findByUuid(l.get(i));
			Assert.assertEquals(u.getStatus(), UploadRequestStatus.STATUS_CREATED);
			c = enableUploadResquestBatch.execute(l.get(i), l.size(), i);
			Assert.assertEquals(c.getIdentifier(), l.get(i));
			u = uploadRequestRepository.findByUuid(l.get(i));
			Assert.assertEquals(u.getUuid(), l.get(i));
			Assert.assertEquals(u.getStatus(), UploadRequestStatus.STATUS_ENABLED);
		}
		l = notifyBeforeExpirationUploadResquestBatch.getAll();
		Assert.assertEquals(l.size(), 3);
		for (i = 0; i < l.size(); i++) {
			c = notifyBeforeExpirationUploadResquestBatch.execute(l.get(i), l.size(), i);
			Assert.assertEquals(c.getIdentifier(), l.get(i));
			u = uploadRequestRepository.findByUuid(l.get(i));
			Assert.assertEquals(u.getUuid(), l.get(i));
			Assert.assertEquals(u.getStatus(), UploadRequestStatus.STATUS_ENABLED);
			Assert.assertEquals(u.isNotified(), true);
		}
	}
}
