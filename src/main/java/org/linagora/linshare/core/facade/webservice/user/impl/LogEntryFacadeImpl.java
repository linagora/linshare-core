/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */

package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.domain.entities.AntivirusLogEntry;
import org.linagora.linshare.core.domain.entities.FileLogEntry;
import org.linagora.linshare.core.domain.entities.LogEntry;
import org.linagora.linshare.core.domain.entities.ShareLogEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserLogEntry;
import org.linagora.linshare.core.facade.webservice.common.dto.LogCriteriaDto;
import org.linagora.linshare.core.facade.webservice.common.dto.LogDto;
import org.linagora.linshare.core.facade.webservice.user.LogEntryFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.view.tapestry.beans.LogCriteriaBean;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class LogEntryFacadeImpl extends UserGenericFacadeImp implements
		LogEntryFacade {

	private final LogEntryService logEntryService;

	public LogEntryFacadeImpl(final AccountService accountService,
			final LogEntryService logEntryService) {
		super(accountService);
		this.logEntryService = logEntryService;
	}

	@Override
	public List<LogDto> query(LogCriteriaDto criteria) {
		User authUser = checkAuthentication();
		Calendar before = Calendar.getInstance();
		Calendar after = Calendar.getInstance();
		before.setTime(criteria.getBeforeDate());
		after.setTime(criteria.getAfterDate());
		LogCriteriaBean crit = new LogCriteriaBean(Lists.newArrayList(authUser
				.getMail()), authUser.getFirstName(),
				authUser.getLastName(), authUser.getDomain().getLabel(),
				criteria.getTargetMails(), criteria.getTargetFirstName(),
				criteria.getTargetLastName(), criteria.getTargetDomain(),
				before, after, criteria.getLogActions(),
				criteria.getFileName(), criteria.getFileExtension());
		return Lists.transform(logEntryService.findByCriteria(authUser, crit),
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
