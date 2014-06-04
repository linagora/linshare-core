package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AntivirusLogEntry;
import org.linagora.linshare.core.domain.entities.FileLogEntry;
import org.linagora.linshare.core.domain.entities.LogEntry;
import org.linagora.linshare.core.domain.entities.ShareLogEntry;
import org.linagora.linshare.core.domain.entities.ThreadLogEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserLogEntry;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.LogEntryFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.view.tapestry.beans.LogCriteriaBean;
import org.linagora.linshare.webservice.dto.LogCriteriaDto;
import org.linagora.linshare.webservice.dto.LogDto;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class LogEntryFacadeImpl extends AdminGenericFacadeImpl implements
		LogEntryFacade {

	private final LogEntryService logEntryService;

	public LogEntryFacadeImpl(final AccountService accountService,
			final LogEntryService logEntryService) {
		super(accountService);
		this.logEntryService = logEntryService;
	}

	@Override
	public List<LogDto> query(LogCriteriaDto criteria) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		Calendar before = null;
		Calendar after = null;

		setTime(before, criteria.getBeforeDate());
		setTime(after, criteria.getAfterDate());

		LogCriteriaBean crit = new LogCriteriaBean(criteria.getActorMails(),
				criteria.getActorFirstName(), criteria.getActorLastName(),
				criteria.getActorDomain(), criteria.getTargetMails(),
				criteria.getTargetFirstName(), criteria.getTargetLastName(),
				criteria.getTargetDomain(), before, after,
				criteria.getLogActions(), criteria.getFileName(),
				criteria.getFileExtension());

		return Lists.transform(logEntryService.findByCriteria(actor, crit),
				new Function<LogEntry, LogDto>() {
					public LogDto apply(LogEntry input) {
						if (input instanceof ShareLogEntry)
							return new LogDto((ShareLogEntry) input);
						if (input instanceof FileLogEntry)
							return new LogDto((FileLogEntry) input);
						if (input instanceof UserLogEntry)
							return new LogDto((UserLogEntry) input);
						if (input instanceof ThreadLogEntry)
							return new LogDto((ThreadLogEntry) input);
						return new LogDto((AntivirusLogEntry) input);
					}
				});
	}

	private void setTime(Calendar cal, Date date) {
		if (date == null)
			return;
		cal = Calendar.getInstance();
		cal.setTime(date);
	}
}
