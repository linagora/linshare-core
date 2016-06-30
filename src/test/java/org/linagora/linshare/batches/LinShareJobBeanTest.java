package org.linagora.linshare.batches;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.LinShareJobBean;
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
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-batches.xml",
		"classpath:springContext-test.xml" })
public class LinShareJobBeanTest extends
		AbstractTransactionalJUnit4SpringContextTests {

	private static Logger logger = LoggerFactory
			.getLogger(LinShareJobBeanTest.class);

	@Qualifier("deleteGuestBatch")
	@Autowired
	private GenericBatch deleteGuestBatch;

	@Qualifier("markUserToPurgeBatch")
	@Autowired
	private GenericBatch markUserToPurgeBatch;

	@Qualifier("purgeUserBatch")
	@Autowired
	private GenericBatch purgeUserBatch;

	private Wiser wiser;

	public LinShareJobBeanTest() {
		super();
		wiser = new Wiser(2525);
	}

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		int countBefore = this.countRowsInTable("users");
		this.executeSqlScript("import-tests-batches-accounts.sql", false);
		int countAfter = this.countRowsInTable("users");
		Assert.assertEquals(3, countAfter - countBefore);
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
	public void testAccountCleaning() throws BusinessException,
			JobExecutionException {
		LinShareJobBean job = new LinShareJobBean();
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(deleteGuestBatch);
		batches.add(markUserToPurgeBatch);
		batches.add(purgeUserBatch);
		job.setBatch(batches);
		Assert.assertTrue("At least one batch failed.", job.executeExternal());
	}
}
