package org.linagora.linshare.batches;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.DomainDailyStatBusinessService;
import org.linagora.linshare.core.business.service.DomainQuotaBusinessService;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.business.service.ThreadDailyStatBusinessService;
import org.linagora.linshare.core.business.service.UserDailyStatBusinessService;
import org.linagora.linshare.core.domain.entities.DomainDailyStat;
import org.linagora.linshare.core.domain.entities.OperationHistory;
import org.linagora.linshare.core.domain.entities.Quota;
import org.linagora.linshare.core.domain.entities.ThreadDailyStat;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserDailyStat;
import org.linagora.linshare.core.job.quartz.LinShareJobBean;
import org.linagora.linshare.core.repository.AccountQuotaRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.google.common.collect.Lists;

@ContextConfiguration( locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-jackRabbit-mock.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-quota-manager.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml"})
public class DailyBatchJobTest extends AbstractTransactionalJUnit4SpringContextTests {

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
	private OperationHistoryBusinessService operationHistoryBusinessService;

	@Autowired
	private UserDailyStatBusinessService userdailyStatBusinessService;

	@Autowired
	private ThreadDailyStatBusinessService threadDailyStatBusinessService;

	@Autowired
	private AccountQuotaBusinessService accountQuotaBusinessService;

	@Autowired
	private AccountQuotaRepository accountQuotaRepository;

	@Autowired
	private DomainDailyStatBusinessService domainDailyStatBusinessService;

	@Autowired
	private DomainQuotaBusinessService domainQuotaBusinessService;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	LoadingServiceTestDatas dates;
	private User jane;
	private User jando;

	@Before
	public void setUp (){
		this.executeSqlScript("import-tests-operationHistory.sql", false);
		this.executeSqlScript("import-tests-quota.sql", false);
		this.executeSqlScript("import-tests-stat.sql", false);
		dates = new LoadingServiceTestDatas(userRepository);
		dates.loadUsers();
		jane = dates.getUser2();
		jando = dates.getUser1();
	}

	@Test
	public void test() throws JobExecutionException {
		LinShareJobBean job = new LinShareJobBean();
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(dailyUserBatch);
		batches.add(dailyThreadBatch);
		batches.add(dailyDomainBatch);
		batches.add(weeklyUserBatch);
		batches.add(weeklyThreadBatch);
		batches.add(weeklyDomainBatch);
		batches.add(monthlyUserBatch);
		batches.add(monthlyThreadBatch);
		batches.add(monthlyDomainBatch);

		batches.add(dailyUserBatch);
		batches.add(dailyThreadBatch);
		batches.add(dailyDomainBatch);
		batches.add(weeklyUserBatch);
		batches.add(weeklyThreadBatch);
		batches.add(monthlyDomainBatch);
		batches.add(weeklyDomainBatch);
		batches.add(monthlyUserBatch);
		batches.add(monthlyThreadBatch);
		batches.add(monthlyDomainBatch);
		job.setBatch(batches);

		List<OperationHistory> listOperationHistory = operationHistoryBusinessService.find(null, null, null, yesterday());
		assertNotEquals(0, listOperationHistory.size());

		List<UserDailyStat> listUserDailyStat = userdailyStatBusinessService.findBetweenTwoDates(null, new Date(), new Date());
		assertEquals(0, listUserDailyStat.size());

		List<ThreadDailyStat> listThreadDailyStat = threadDailyStatBusinessService.findBetweenTwoDates(null, new Date(), new Date());
		assertEquals(0, listThreadDailyStat.size());

		assertTrue("At least one batch failed.", job.executeExternal());

		listOperationHistory = operationHistoryBusinessService.find(null, null, null, yesterday());
		assertEquals(0, listOperationHistory.size());
		Date newDate = new Date();

		listUserDailyStat = userdailyStatBusinessService.findBetweenTwoDates(null, newDate, newDate);
		assertEquals(2, listUserDailyStat.size());
		UserDailyStat userDailyStat = listUserDailyStat.get(1);
		assertEquals(jane, userDailyStat.getAccount());
		assertEquals(7, (long) userDailyStat.getOperationCount());
		assertEquals(700, (long) userDailyStat.getActualOperationSum());
		assertEquals(5, (long) userDailyStat.getAddOperationCount());
		assertEquals(1200, (long) userDailyStat.getAddOperationSum());
		assertEquals(2, (long) userDailyStat.getDeleteOperationCount());
		assertEquals(-500, (long) userDailyStat.getDeleteOperationSum());
		assertEquals(700, (long) userDailyStat.getDiffOperationSum());

		userDailyStat = listUserDailyStat.get(0);
		assertEquals(jando, userDailyStat.getAccount());
		assertEquals(1, (long) userDailyStat.getOperationCount());
		assertEquals(200, (long) userDailyStat.getActualOperationSum());
		assertEquals(1, (long) userDailyStat.getAddOperationCount());
		assertEquals(200, (long) userDailyStat.getAddOperationSum());
		assertEquals(0, (long) userDailyStat.getDeleteOperationCount());
		assertEquals(0, (long) userDailyStat.getDeleteOperationSum());
		assertEquals(200, (long) userDailyStat.getDiffOperationSum());

		assertEquals(5, accountQuotaRepository.findAll().size());

		Quota quota = accountQuotaBusinessService.find(jane);
		assertNotNull(quota);
		assertEquals(1500, (long) quota.getCurrentValue());
		assertEquals(800, (long) quota.getLastValue());
		assertEquals(1600, (long) quota.getQuota());
		assertEquals(1480, (long) quota.getQuotaWarning());
		assertEquals(5, (long) quota.getFileSizeMax());

		quota = accountQuotaBusinessService.find(jando);
		assertNotNull(quota);
		assertEquals(200, (long) quota.getCurrentValue());
		assertEquals(0, (long) quota.getLastValue());
		assertEquals(1900, (long) quota.getQuota());
		assertEquals(1800, (long) quota.getQuotaWarning());
		assertEquals(5, (long) quota.getFileSizeMax());

		listThreadDailyStat = threadDailyStatBusinessService.findBetweenTwoDates(null, newDate, newDate);
		assertEquals(2, listThreadDailyStat.size());
		ThreadDailyStat threadDailyStat = listThreadDailyStat.get(0);
		assertEquals(2, (long) threadDailyStat.getOperationCount());
		assertEquals(100, (long) threadDailyStat.getActualOperationSum());
		assertEquals(1, (long) threadDailyStat.getAddOperationCount());
		assertEquals(400, (long) threadDailyStat.getAddOperationSum());
		assertEquals(1, (long) threadDailyStat.getDeleteOperationCount());
		assertEquals(-300, (long) threadDailyStat.getDeleteOperationSum());
		assertEquals(100, (long) threadDailyStat.getDiffOperationSum());

		quota = accountQuotaBusinessService.find(threadDailyStat.getAccount());
		assertNotNull(quota);
		assertEquals(800, (long) quota.getCurrentValue());
		assertEquals(700, (long) quota.getLastValue());
		assertEquals(1000, (long) quota.getQuota());
		assertEquals(800, (long) quota.getQuotaWarning());
		assertEquals(5, (long) quota.getFileSizeMax());

		threadDailyStat = listThreadDailyStat.get(1);
		assertEquals(1, (long) threadDailyStat.getOperationCount());
		assertEquals(200, (long) threadDailyStat.getActualOperationSum());
		assertEquals(1, (long) threadDailyStat.getAddOperationCount());
		assertEquals(200, (long) threadDailyStat.getAddOperationSum());
		assertEquals(0, (long) threadDailyStat.getDeleteOperationCount());
		assertEquals(0, (long) threadDailyStat.getDeleteOperationSum());
		assertEquals(200, (long) threadDailyStat.getDiffOperationSum());

		quota = accountQuotaBusinessService.find(threadDailyStat.getAccount());
		assertNotNull(quota);
		assertEquals(700, (long) quota.getCurrentValue());
		assertEquals(500, (long) quota.getLastValue());
		assertEquals(1300, (long) quota.getQuota());
		assertEquals(1000, (long) quota.getQuotaWarning());
		assertEquals(6, (long) quota.getFileSizeMax());

		List<DomainDailyStat> listDomaindailyStat = domainDailyStatBusinessService.findBetweenTwoDates(jane.getDomain(), new Date(), new Date());

		assertEquals(1, listDomaindailyStat.size());
		DomainDailyStat domainDailyStat = listDomaindailyStat.get(0);
		assertEquals(jane.getDomain(), domainDailyStat.getDomain());
		assertEquals(11, (long) domainDailyStat.getOperationCount());
		assertEquals(1200, (long) domainDailyStat.getActualOperationSum());
		assertEquals(8, (long) domainDailyStat.getAddOperationCount());
		assertEquals(2000, (long) domainDailyStat.getAddOperationSum());
		assertEquals(3, (long) domainDailyStat.getDeleteOperationCount());
		assertEquals(-800, (long) domainDailyStat.getDeleteOperationSum());
		assertEquals(1200, (long) domainDailyStat.getDiffOperationSum());

		quota = domainQuotaBusinessService.find(jane.getDomain());
		assertNotNull(quota);
		assertEquals(4100, (long) quota.getCurrentValue());
		assertEquals(1096, (long) quota.getLastValue());
		assertEquals(1900, (long) quota.getQuota());
		assertEquals(1800, (long) quota.getQuotaWarning());
		assertEquals(new Long(5), quota.getFileSizeMax());

		List<String> listDomain = dailyDomainBatch.getAll();
		assertEquals(1, listDomain.size());
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
