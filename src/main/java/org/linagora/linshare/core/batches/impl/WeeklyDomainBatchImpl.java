package org.linagora.linshare.core.batches.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.linagora.linshare.core.business.service.DomainDailyStatBusinessService;
import org.linagora.linshare.core.business.service.DomainWeeklyStatBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.Context;
import org.linagora.linshare.core.job.quartz.DomainBatchResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.AbstractDomainService;

public class WeeklyDomainBatchImpl extends GenericBatchImpl {

	private final DomainDailyStatBusinessService domainDailyStatBusinessService;
	private final DomainWeeklyStatBusinessService domainWeeklyStatBusinessService;
	private final AbstractDomainService abstractDomainService;

	public WeeklyDomainBatchImpl(final DomainDailyStatBusinessService domainDailyStatBusinessService,
			final DomainWeeklyStatBusinessService domainWeeklyStatBusinessService,
			final AbstractDomainService abstractDomainService, AccountRepository<Account> accountRepository) {
		super(accountRepository);
		this.domainDailyStatBusinessService = domainDailyStatBusinessService;
		this.domainWeeklyStatBusinessService = domainWeeklyStatBusinessService;
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	public List<String> getAll() {
		logger.info("WeeklyDomainBatchImpl job starting");
		List<String> domains = domainDailyStatBusinessService
				.findIdentifierDomainBetweenTwoDates(getFirstDayOfLastWeek(), getLastDayOfLastWeek());
		logger.info(domains.size() + "domain(s) have been found in DomainDailyStat table.");
		return domains;
	}

	@Override
	public Context execute(String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		AbstractDomain resource = abstractDomainService.findById(identifier);
		Context context = new DomainBatchResultContext(resource);
		try {
			logInfo(total, position, "processing domain : " + resource.getDescription());
			domainWeeklyStatBusinessService.create(resource, getFirstDayOfLastWeek(), getLastDayOfLastWeek());
		} catch (BusinessException businessException) {
			logError(total, position, "Error while trying to create a DomainWeeklyStat.");
			logger.info("Error occurred while creating a weekly statistics for a domain", businessException);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to create a DomainWeeklyStat");
			exception.setBusinessException(businessException);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(Context context, long total, long position) {
		DomainBatchResultContext domainContext = (DomainBatchResultContext) context;
		AbstractDomain domain = domainContext.getResource();
		logInfo(total, position,
				"the WeeklyDomainStat for " + domain.getDescription() + "has been successfully created.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position) {
		DomainBatchResultContext domainContext = (DomainBatchResultContext) exception.getContext();
		AbstractDomain domain = domainContext.getResource();
		logError(total, position, "creating WeeklyDomainStatistic has failed : " + domain.getDescription());
		logger.error("Error occured while creating WeeklyDomainStatistic for a domain " + domain.getDescription() + ". BatchBudinessException ", exception);
	}

	@Override
	public void terminate(List<String> all, long errors, long unhandled_errors, long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " WeeklyFomainStatistic for domain(s) have bean created.");
		if (errors > 0) {
			logger.info(errors + "  WeeklyDomainStatistic for domain(s) failed to be created");
		}
		if (unhandled_errors > 0){
			logger.error(unhandled_errors + " WeeklyDomainStatistic failed to be created (unhandled error.)");
		}
		logger.info("WeeklyDomainBatchImpl job terminated");
	}

	private Date getLastDayOfLastWeek() {
		GregorianCalendar dateCalendar = new GregorianCalendar();
		Date firstDate = getFirstDayOfLastWeek();
		dateCalendar.setTime(firstDate);
		int today = dateCalendar.get(GregorianCalendar.DAY_OF_MONTH);
		dateCalendar.set(GregorianCalendar.DAY_OF_MONTH, today + 6);
		dateCalendar.set(GregorianCalendar.HOUR, 0);
		dateCalendar.set(GregorianCalendar.MINUTE, 0);
		dateCalendar.set(GregorianCalendar.SECOND, 0);
		return dateCalendar.getTime();
	}

	private Date getFirstDayOfLastWeek() {
		GregorianCalendar dateCalendar = new GregorianCalendar();
		dateCalendar.add(Calendar.DATE, -7);
		int firstDay = dateCalendar.getFirstDayOfWeek();
		if (firstDay == GregorianCalendar.SUNDAY)
			firstDay++;
		else {
			if (firstDay != GregorianCalendar.MONDAY)
				throw new BusinessException("the used local can not be supported");
		}
		dateCalendar.set(GregorianCalendar.DAY_OF_WEEK, firstDay);
		dateCalendar.set(GregorianCalendar.HOUR, 0);
		dateCalendar.set(GregorianCalendar.MINUTE, 0);
		dateCalendar.set(GregorianCalendar.SECOND, 0);
		return dateCalendar.getTime();
	}
}
