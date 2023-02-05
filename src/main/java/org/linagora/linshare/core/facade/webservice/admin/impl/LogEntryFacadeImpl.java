/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.Calendar;
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
import org.linagora.linshare.core.facade.webservice.admin.dto.LogCriteriaDto;
import org.linagora.linshare.core.facade.webservice.common.dto.LogDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.view.tapestry.beans.LogCriteriaBean;

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
		User authUser = checkAuthentication(Role.ADMIN);
		Calendar before = null;
		Calendar after = null;

		if (criteria.getBeforeDate() != null) {
			before = Calendar.getInstance();
			before.setTime(criteria.getBeforeDate());
		}
		if (criteria.getAfterDate() != null) {
			after= Calendar.getInstance();
			after.setTime(criteria.getAfterDate());
		}

		LogCriteriaBean crit = new LogCriteriaBean(criteria.getActorMails(),
				criteria.getActorFirstName(), criteria.getActorLastName(),
				criteria.getActorDomain(), criteria.getTargetMails(),
				criteria.getTargetFirstName(), criteria.getTargetLastName(),
				criteria.getTargetDomain(), before, after,
				criteria.getLogActions(), criteria.getFileName(),
				criteria.getFileExtension());

		return Lists.transform(logEntryService.findByCriteria(authUser, crit),
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
}
