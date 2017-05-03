package org.linagora.linshare.batches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.DomainDailyStatBusinessService;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.business.service.ThreadDailyStatBusinessService;
import org.linagora.linshare.core.business.service.UserDailyStatBusinessService;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.DomainDailyStat;
import org.linagora.linshare.core.domain.entities.OperationHistory;
import org.linagora.linshare.core.domain.entities.ThreadDailyStat;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserDailyStat;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.runner.BatchRunner;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.google.common.collect.Lists;

@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-batches.xml",
		"classpath:springContext-batches-quota-and-statistics.xml",
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml" })
public class DailyBatchJobTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private BatchRunner batchRunner;

	@Autowired
	@Qualifier("statisticMonthlyUserBatch")
	private GenericBatch monthlyUserBatch;

	@Autowired
	@Qualifier("statisticMonthlyThreadBatch")
	private GenericBatch monthlyThreadBatch;

	@Autowired
	@Qualifier("statisticMonthlyDomainBatch")
	private GenericBatch monthlyDomainBatch;

	@Autowired
	@Qualifier("statisticWeeklyUserBatch")
	private GenericBatch weeklyUserBatch;

	@Autowired
	@Qualifier("statisticWeeklyThreadBatch")
	private GenericBatch weeklyThreadBatch;

	@Autowired
	@Qualifier("statisticWeeklyDomainBatch")
	private GenericBatch weeklyDomainBatch;

	@Autowired
	@Qualifier("statisticDailyUserBatch")
	private GenericBatch dailyUserBatch;

	@Autowired
	@Qualifier("statisticDailyThreadBatch")
	private GenericBatch dailyThreadBatch;

	@Autowired
	@Qualifier("statisticDailyDomainBatch")
	private GenericBatch dailyDomainBatch;

	@Autowired
	@Qualifier("computeSubDomainQuotaBatch")
	private GenericBatch computeSubDomainQuotaBatch;

	@Autowired
	private OperationHistoryBusinessService operationHistoryBusinessService;

	@Autowired
	private UserDailyStatBusinessService userdailyStatBusinessService;

	@Autowired
	private ThreadDailyStatBusinessService threadDailyStatBusinessService;

	@Autowired
	private AccountQuotaBusinessService accountQuotaBusinessService;

	@Autowired
	private DomainDailyStatBusinessService domainDailyStatBusinessService;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	LoadingServiceTestDatas dates;
	private User jane;

	@Before
	public void setUp() {
		this.executeSqlScript("import-tests-stat.sql", false);
		this.executeSqlScript("import-tests-operationHistory.sql", false);
		this.executeSqlScript("import-tests-quota.sql", false);
		this.executeSqlScript("import-mails-hibernate3.sql", false);
		dates = new LoadingServiceTestDatas(userRepository);
		dates.loadUsers();
		jane = dates.getUser2();
	}

	@Test
	public void test() throws JobExecutionException {
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(dailyUserBatch);
		batches.add(dailyThreadBatch);
		batches.add(dailyDomainBatch);
		batches.add(computeSubDomainQuotaBatch);
		batches.add(weeklyUserBatch);
		batches.add(weeklyThreadBatch);
		batches.add(weeklyDomainBatch);
		batches.add(monthlyUserBatch);
		batches.add(monthlyThreadBatch);
		batches.add(monthlyDomainBatch);

		List<OperationHistory> listOperationHistory = operationHistoryBusinessService.find(null, null, null,
				yesterday());
		// assertNotEquals(0, listOperationHistory.size());
		List<UserDailyStat> listUserDailyStat = userdailyStatBusinessService.findBetweenTwoDates(null, new Date(),
				new Date());
		assertEquals(0, listUserDailyStat.size());
		List<ThreadDailyStat> listThreadDailyStat = threadDailyStatBusinessService.findBetweenTwoDates(null, new Date(),
				new Date());
		assertEquals(0, listThreadDailyStat.size());

		// running all batches.
		assertTrue("At least one batch failed.", batchRunner.execute(batches));

		listOperationHistory = operationHistoryBusinessService.find(null, null, null, yesterday());
		assertEquals(0, listOperationHistory.size());
		listUserDailyStat = userdailyStatBusinessService.findBetweenTwoDates(null,
				new GregorianCalendar(2042, 9, 01, 00, 00).getTime(),
				new GregorianCalendar(2042, 9, 16, 00, 00).getTime());
		assertEquals(1, listUserDailyStat.size());
		UserDailyStat userDailyStat = listUserDailyStat.get(0);
		assertEquals(jane, userDailyStat.getAccount());
		assertEquals(5, (long) userDailyStat.getOperationCount());
		assertEquals(50, (long) userDailyStat.getActualOperationSum());
		assertEquals(3, (long) userDailyStat.getCreateOperationCount());
		assertEquals(100, (long) userDailyStat.getCreateOperationSum());
		assertEquals(2, (long) userDailyStat.getDeleteOperationCount());
		assertEquals(-50, (long) userDailyStat.getDeleteOperationSum());
		assertEquals(50, (long) userDailyStat.getDiffOperationSum());
		AccountQuota quota = accountQuotaBusinessService.find(jane);
		assertNotNull(quota);
		assertEquals(1100, (long) quota.getCurrentValue());
		assertEquals(800, (long) quota.getLastValue());
		assertEquals(1600, (long) quota.getQuota());
		assertEquals(1480, (long) quota.getQuotaWarning());
		assertEquals(5, (long) quota.getMaxFileSize());
		listThreadDailyStat = threadDailyStatBusinessService.findBetweenTwoDates(null,
				new GregorianCalendar(2042, 10, 01, 00, 00).getTime(),
				new GregorianCalendar(2042, 10, 16, 00, 00).getTime());
		assertEquals(5, listThreadDailyStat.size());
		ThreadDailyStat threadDailyStat = listThreadDailyStat.get(0);
		assertEquals(5, (long) threadDailyStat.getOperationCount());
		assertEquals(50, (long) threadDailyStat.getActualOperationSum());
		assertEquals(3, (long) threadDailyStat.getCreateOperationCount());
		assertEquals(100, (long) threadDailyStat.getCreateOperationSum());
		assertEquals(2, (long) threadDailyStat.getDeleteOperationCount());
		assertEquals(-50, (long) threadDailyStat.getDeleteOperationSum());
		assertEquals(50, (long) threadDailyStat.getDiffOperationSum());
		accountQuotaBusinessService.createOrUpdate(threadDailyStat.getAccount(), new Date());
		quota = accountQuotaBusinessService.find(threadDailyStat.getAccount());
		assertNotNull(quota);
		assertEquals(0, (long) quota.getCurrentValue());
		assertEquals(0, (long) quota.getLastValue());
		assertEquals(2000, (long) quota.getQuota());
		assertEquals(1500, (long) quota.getQuotaWarning());
		assertEquals(5, (long) quota.getMaxFileSize());
		List<DomainDailyStat> listDomaindailyStat = domainDailyStatBusinessService.findBetweenTwoDates(jane.getDomain(),
				null, null);
		assertEquals(5, listDomaindailyStat.size());
		listDomaindailyStat = domainDailyStatBusinessService.findBetweenTwoDates(jane.getDomain(),
				new GregorianCalendar(2042, 8, 8, 00, 00).getTime(), new GregorianCalendar(2042, 8, 11, 00, 00).getTime());
		assertEquals(1, listDomaindailyStat.size());
		DomainDailyStat domainDailyStat = listDomaindailyStat.get(0);
		assertEquals(jane.getDomain(), domainDailyStat.getDomain());
		assertEquals(5, (long) domainDailyStat.getOperationCount());
		assertEquals(50, (long) domainDailyStat.getActualOperationSum());
		assertEquals(3, (long) domainDailyStat.getCreateOperationCount());
		assertEquals(100, (long) domainDailyStat.getCreateOperationSum());
		assertEquals(2, (long) domainDailyStat.getDeleteOperationCount());
		assertEquals(-50, (long) domainDailyStat.getDeleteOperationSum());
		assertEquals(50, (long) domainDailyStat.getDiffOperationSum());
	}

	private Date yesterday() {
		GregorianCalendar dateCalender = new GregorianCalendar();
		dateCalender.add(GregorianCalendar.DATE, -1);
		dateCalender.set(GregorianCalendar.HOUR_OF_DAY, 23);
		dateCalender.set(GregorianCalendar.MINUTE, 59);
		dateCalender.set(GregorianCalendar.SECOND, 59);
		return dateCalender.getTime();
	}
}
