package org.linagora.linshare.core.batches.impl;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.DomainDailyStatBusinessService;
import org.linagora.linshare.core.business.service.DomainQuotaBusinessService;
import org.linagora.linshare.core.business.service.EnsembleQuotaBusinessService;
import org.linagora.linshare.core.domain.constants.EnsembleType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.domain.entities.EnsembleQuota;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.Context;
import org.linagora.linshare.core.job.quartz.DomainBatchResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.AbstractDomainService;

public class DailyDomainBatchImpl extends GenericBatchImpl {

	private final AccountQuotaBusinessService accountQuotaBusinessService;
	private final AbstractDomainService abstractDomainService;
	private final EnsembleQuotaBusinessService ensembleQuotaBusinessService;
	private final DomainQuotaBusinessService domainQuotaBusinessService;
	private final DomainDailyStatBusinessService domainDailyStatBusinessService;

	public DailyDomainBatchImpl(AccountRepository<Account> accountRepository,
			final AccountQuotaBusinessService accountQuotaBusinessService,
			final AbstractDomainService abstractDomainService,
			final EnsembleQuotaBusinessService ensembleQuotaBusinessService,
			final DomainQuotaBusinessService domainQuotaBusinessService,
			final DomainDailyStatBusinessService domainDailyStatBusinessService) {
		super(accountRepository);
		this.accountQuotaBusinessService = accountQuotaBusinessService;
		this.abstractDomainService = abstractDomainService;
		this.ensembleQuotaBusinessService = ensembleQuotaBusinessService;
		this.domainQuotaBusinessService = domainQuotaBusinessService;
		this.domainDailyStatBusinessService = domainDailyStatBusinessService;
	}

	@Override
	public List<String> getAll() {
		logger.info("DailyDomainBatchImpl job starting ...");
		List<String> domains = accountQuotaBusinessService.findDomainByBatchModificationDate(today());
		logger.info(domains.size() + "domain(s) have been found in accountQuota table and modified by batch today");
		return domains;
	}

	@Override
	public Context execute(String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		Date today = today();
		AbstractDomain resource = abstractDomainService.findById(identifier);
		Context context = new DomainBatchResultContext(resource);
		try {
			logInfo(total, position, "processing domain : " + resource.getDescription());
			domainDailyStatBusinessService.create(resource, today);
		} catch (BusinessException businessException) {
			logError(total, position, "Error while trying to create DomainDailyStat");
			logger.info("Error occured while creating daily statistics for domain", businessException);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to create a DomainDailyStat");
			exception.setBusinessException(businessException);
			throw exception;
		}

		try {
			EnsembleQuota userEnsembleQuota = ensembleQuotaBusinessService.find(resource, EnsembleType.USER);
			userEnsembleQuota = ensembleQuotaBusinessService.updateByBatch(userEnsembleQuota, today);
		} catch (BusinessException businessException) {
			logError(total, position, "Error while trying to update userEnsembleQuota");
			logger.info("Error occured while update an user ensemble quota for domain", businessException);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to update a userEnsebleQuota");
			exception.setBusinessException(businessException);
			throw exception;
		}
		try {
			EnsembleQuota ThreadEnsembleQuota = ensembleQuotaBusinessService.find(resource, EnsembleType.THREAD);
			ThreadEnsembleQuota = ensembleQuotaBusinessService.updateByBatch(ThreadEnsembleQuota, today);
		} catch (BusinessException businessException) {
			logError(total, position, "Error while trying to update threadEnsembleQuota");
			logger.info("Error occured while update a thread ensemble quota for domain", businessException);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to update a threadEnsebleQuota");
			exception.setBusinessException(businessException);
			throw exception;
		}

		DomainQuota domainQuota = domainQuotaBusinessService.find(resource);
		try {
			domainQuotaBusinessService.updateByBatch(domainQuota, today);
		} catch (BusinessException businessException) {
			logError(total, position, "Error while trying to update domainQuota");
			logger.info("Error occured while update a domain quota for domain", businessException);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to update a domainQuota");
			exception.setBusinessException(businessException);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(Context context, long total, long position) {
		DomainBatchResultContext domainContext = (DomainBatchResultContext) context;
		AbstractDomain domain = domainContext.getResource();
		logInfo(total, position, "DailyDomainStatistics, EnsembleQuota and DomainQuota of the domain : " + domain.getDescription()
				+ " have been successfully created");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position) {
		DomainBatchResultContext context = (DomainBatchResultContext) exception.getContext();
		AbstractDomain domain = context.getResource();
		logError(total, position, "creating DailyDomainStatistic, EnsembleQuota and DomainQuota have failed for the domain : "
				+ domain.getDescription());
		logger.error("Error occured while creating DailyDomainStatistics, EnsembleQuota and DomainQuota for a domain "
				+ domain.getDescription() + ". BatchBusinessException ", exception);
	}

	@Override
	public void terminate(List<String> all, long errors, long unhandled_errors, long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " DailyDomainStatistic, EnsembleQuota and DomainQuota for domain(s) have bean created.");
		if (errors > 0) {
			logger.info(errors
					+ "  DailyDomainStatistic, EnsembleQuota and DomainQuota for domain(s) failed to be created");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors
					+ "  DailyDomainStatistic, EnsembleQuota and DomainQuota for domain(s) failed to be created (unhandled error.)");
		}
		logger.info("DailyDomainBatchImpl job terminated");
	}

	private Date today() {
		GregorianCalendar dateCalender = new GregorianCalendar();
		dateCalender.set(GregorianCalendar.HOUR, 0);
		dateCalender.set(GregorianCalendar.MINUTE, 0);
		dateCalender.set(GregorianCalendar.SECOND, 0);
		return dateCalender.getTime();
	}
}
