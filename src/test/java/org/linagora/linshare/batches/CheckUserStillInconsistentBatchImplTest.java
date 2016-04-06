package org.linagora.linshare.batches;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.Context;
import org.linagora.linshare.core.job.quartz.LinShareJobBean;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.google.common.collect.Lists;

@ContextConfiguration(locations = { "classpath:springContext-datasource.xml", 
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml", 
		"classpath:springContext-jackRabbit-mock.xml",
		"classpath:springContext-startopendj.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-batches.xml",
		"classpath:springContext-test.xml" })
public class CheckUserStillInconsistentBatchImplTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Qualifier("checkIfUserStillInconsistentBatch")
	@Autowired
	private GenericBatch checkIfUserStillInconsistentBatch;

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		this.executeSqlScript("import-tests-check-inconsistent.sql", false);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testBatch() throws BusinessException, JobExecutionException {
		LinShareJobBean job = new LinShareJobBean();
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(checkIfUserStillInconsistentBatch);
		job.setBatch(batches);
		Assert.assertTrue("At least one batch failed.", job.executeExternal());
	}

	@Test
	public void testBatchExecution() throws BusinessException, JobExecutionException {
		List<String> l = checkIfUserStillInconsistentBatch.getAll();
		Assert.assertEquals(l.size(), 4);
		Context c;
		for (int i = 0; i < l.size(); i++) {
			c = checkIfUserStillInconsistentBatch.execute(l.get(i), l.size(), i);
			Assert.assertEquals(c.getIdentifier(), l.get(i));
		}
		l = checkIfUserStillInconsistentBatch.getAll();
		Assert.assertEquals(l.size(), 1);
	}
}
