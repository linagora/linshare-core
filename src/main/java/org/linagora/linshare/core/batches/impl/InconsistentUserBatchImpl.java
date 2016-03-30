package org.linagora.linshare.core.batches.impl;

import java.util.List;

import org.linagora.linshare.core.batches.InconsistentUserBatch;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.Context;
import org.linagora.linshare.core.job.quartz.InconsistentUserBatchResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.InconsistentUserService;
import org.linagora.linshare.core.service.UserService;

public class InconsistentUserBatchImpl extends GenericBatchImpl implements InconsistentUserBatch {

	InconsistentUserService service;
	UserService userService;
	AbstractDomainService abstractDomainService;

	public InconsistentUserBatchImpl(AccountRepository<Account> accountRepository,
			InconsistentUserService service,
			UserService userService,
			AbstractDomainService abstractDomainService) {
		super(accountRepository);
		this.service = service;
		this.userService = userService;
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	public List<String> getAll() {
		logger.info(getClass().toString() + " job is starting ...");
		SystemAccount account = getSystemAccount();
		List<String> entries = service.findAllUserUuids(account);
		logger.info(entries.size() + " inconsistent users have found to be flaged.");
		return entries;
	}

	@Override
	public Context execute(String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		SystemAccount account = getSystemAccount();
		User u = userService.findByLsUuid(identifier);
		Context context = new InconsistentUserBatchResultContext(u);
		context.setProcessed(false);
		logInfo(total,
				position,
				"processing internal : "
						+ u.getAccountRepresentation());
		if (!abstractDomainService.isUserExist(u.getDomain(),
				u.getMail())) {
			logInfo(total, position, "Flagging as inconsistent internal : " + u.getAccountRepresentation());
			u.setInconsistent(true);
			userService.updateUser(account, u, u.getDomainId());
			context.setProcessed(true);
		}
		return context;
	}

	@Override
	public void notify(Context context, long total, long position) {
		InconsistentUserBatchResultContext c = (InconsistentUserBatchResultContext) context;
		User u = c.getResource();
		logInfo(total, position, "The inconsistent user " + u.getLsUuid() + " has been successfully flaged.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position) {
		InconsistentUserBatchResultContext c = (InconsistentUserBatchResultContext) exception.getContext();
		User u = c.getResource();
		logError(total, position, "Flaging user has failed " + u.getLsUuid() + ". BatchBusinessException ", exception);
	}

	@Override
	public void terminate(List<String> all, long errors, long unhandled_errors, long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " user(s) have been checked.");
		logger.info(processed + " user(s) have been flaged as inconsistent.");
		if (errors > 0) {
			logger.error(errors + " user(s) failed to be flaged as inconsistent.");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors + " user(s) failed to be flaged as inconsistent (unhandled error)");
		}
		logger.info(getClass().toString() + " job terminated.");
	}
}