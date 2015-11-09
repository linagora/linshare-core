/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.batches.impl;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.linagora.linshare.core.batches.DailyBatch;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.DomainDailyStatBusinessService;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.business.service.PlatformQuotaBusinessService;
import org.linagora.linshare.core.business.service.DomainQuotaBusinessService;
import org.linagora.linshare.core.business.service.EnsembleQuotaBusinessService;
import org.linagora.linshare.core.business.service.ThreadDailyStatBusinessService;
import org.linagora.linshare.core.business.service.UserDailyStatBusinessService;
import org.linagora.linshare.core.domain.constants.EnsembleType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.User;

public class DailyBatchImpl implements DailyBatch {

	private OperationHistoryBusinessService operationHistoryBusinessService;
	private UserDailyStatBusinessService userDailyStatBusinessService;
	private ThreadDailyStatBusinessService threadDailyStatBusinessService;
	private DomainDailyStatBusinessService domainDailyStatBusinessService;
	private DomainQuotaBusinessService domainQuotaBusinessService;
	private AccountQuotaBusinessService accountQuotaBusinessService;
	private EnsembleQuotaBusinessService ensembleQuotaBusinessService;
	private PlatformQuotaBusinessService platformQuotaBusinessService;

	public DailyBatchImpl(OperationHistoryBusinessService operationHistoryBusinessService,
			UserDailyStatBusinessService userDailyStatBusinessService,
			ThreadDailyStatBusinessService threadDailyStatBusinessService,
			DomainDailyStatBusinessService domainDailyStatBusinessService,
			DomainQuotaBusinessService domainQuotaBusinessService,
			AccountQuotaBusinessService accountQuotaBusinessService,
			EnsembleQuotaBusinessService ensembleQuotaBusinessService,
			PlatformQuotaBusinessService platformQuotaBusinessService) {
		this.userDailyStatBusinessService = userDailyStatBusinessService;
		this.threadDailyStatBusinessService = threadDailyStatBusinessService;
		this.domainDailyStatBusinessService = domainDailyStatBusinessService;
		this.operationHistoryBusinessService = operationHistoryBusinessService;
		this.domainQuotaBusinessService = domainQuotaBusinessService;
		this.accountQuotaBusinessService = accountQuotaBusinessService;
		this.ensembleQuotaBusinessService = ensembleQuotaBusinessService;
		this.platformQuotaBusinessService = platformQuotaBusinessService;
	}

	@Override
	public void executeBatch() {
		Date today = getToday();
		List<Account> listUser = operationHistoryBusinessService.findAccountBeforeDate(today, EnsembleType.USER);
		List<Account> listThread = operationHistoryBusinessService.findAccountBeforeDate(today, EnsembleType.THREAD);
		List<AbstractDomain> listDomain = operationHistoryBusinessService.findDomainBeforeDate(today);

		for (Account user : listUser) {
			userDailyStatBusinessService.create((User) user, today);
			accountQuotaBusinessService.createOrUpdate(user, today);
		}

		for (Account thread : listThread) {
			threadDailyStatBusinessService.create((Thread) thread, today);
			accountQuotaBusinessService.createOrUpdate(thread, today);
		}

		for (AbstractDomain domain : listDomain) {
			domainDailyStatBusinessService.create(domain, today);
			domainQuotaBusinessService.createOrUpdate(domain, today);
			ensembleQuotaBusinessService.createOrUpdate(domain, EnsembleType.USER, today);
			ensembleQuotaBusinessService.createOrUpdate(domain, EnsembleType.THREAD, today);
		}
		platformQuotaBusinessService.createOrUpdate(today);
		operationHistoryBusinessService.deleteBeforeDate(today);
	}

	private Date getToday() {
		GregorianCalendar dateCalender = new GregorianCalendar();
		dateCalender.set(GregorianCalendar.HOUR, 0);
		dateCalender.set(GregorianCalendar.MINUTE, 0);
		dateCalender.set(GregorianCalendar.SECOND, 0);
		return dateCalender.getTime();
	}
}