package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.List;

import org.linagora.linshare.core.domain.entities.AntivirusLogEntry;
import org.linagora.linshare.core.domain.entities.FileLogEntry;
import org.linagora.linshare.core.domain.entities.LogEntry;
import org.linagora.linshare.core.domain.entities.ShareLogEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserLogEntry;
import org.linagora.linshare.core.facade.webservice.user.LogEntryFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.view.tapestry.beans.LogCriteriaBean;
import org.linagora.linshare.webservice.dto.LogCriteriaDto;
import org.linagora.linshare.webservice.dto.LogDto;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class LogEntryFacadeImpl extends GenericFacadeImpl implements
		LogEntryFacade {

	private final LogEntryService logEntryService;

	public LogEntryFacadeImpl(final AccountService accountService,
			final LogEntryService logEntryService) {
		super(accountService);
		this.logEntryService = logEntryService;
	}

	@Override
	public List<LogDto> query(User actor, LogCriteriaDto criteria) {
		LogCriteriaBean crit = new LogCriteriaBean(criteria.getActorMails(),
				criteria.getActorFirstname(), criteria.getActorLastname(),
				criteria.getActorDomain(), criteria.getTargetMails(),
				criteria.getTargetFirstname(), criteria.getTargetLastname(),
				criteria.getTargetDomain(), criteria.getBeforeDate(),
				criteria.getAfterDate(), criteria.getLogActions());

		return Lists.transform(logEntryService.findByCriteria(actor, crit),
				new Function<LogEntry, LogDto>() {
					public LogDto apply(LogEntry input) {
						if (input instanceof ShareLogEntry)
							return new LogDto((ShareLogEntry) input);
						if (input instanceof FileLogEntry)
							return new LogDto((FileLogEntry) input);
						if (input instanceof UserLogEntry)
							return new LogDto((UserLogEntry) input);
						return new LogDto((AntivirusLogEntry) input);
					}
				});
	}

}
