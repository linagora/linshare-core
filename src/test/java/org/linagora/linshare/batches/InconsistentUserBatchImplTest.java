package org.linagora.linshare.batches;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.batches.InconsistentUserBatch;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.runner.BatchRunner;
import org.linagora.linshare.core.service.InconsistentUserService;
import org.linagora.linshare.service.LoadingServiceTestDatas;
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
		"classpath:springContext-start-embedded-ldap.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-batches.xml",
		"classpath:springContext-test.xml" })
public class InconsistentUserBatchImplTest extends AbstractTransactionalJUnit4SpringContextTests{

	private static Logger logger = LoggerFactory.getLogger(UploadRequestNewBatchImplTest.class);

	@Autowired
	private BatchRunner batchRunner;

	@Qualifier("inconsistentUserBatch")
	@Autowired
	private InconsistentUserBatch inconsistentUserBatch;

	@Qualifier("userRepository")
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private InconsistentUserService inService;

	private LoadingServiceTestDatas datas;

	private Account root;

	public InconsistentUserBatchImplTest() {
		super();
	}

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		this.executeSqlScript("import-tests-inconsistent.sql", false);
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		root = datas.getRoot();
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testLaunching() throws JobExecutionException {
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(inconsistentUserBatch);
		Assert.assertTrue("At least one batch failed.", batchRunner.execute(batches));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testInconsistentBatch() {
		BatchRunContext batchRunContext = new BatchRunContext();
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		List<String> list = inconsistentUserBatch.getAll(batchRunContext);
		int i = 0;
		for (String s : list) {
			inconsistentUserBatch.execute(batchRunContext, s, list.size(), i);
		}
		List<Internal> l = inService.findAllInconsistent((User)root);
		Assert.assertEquals(l.size(), 5);
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
