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
package org.linagora.linshare.core.batches.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.linagora.linshare.core.batches.utils.FakeContext;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.TimeService;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.HibernateTemplate;

import com.google.common.collect.Lists;

public class DeleteOutdatedFavouriteRecipientBatchImpl extends GenericBatchImpl {

	private final HibernateTemplate hibernateTemplate;

	private final TimeService timeService;

	public DeleteOutdatedFavouriteRecipientBatchImpl(
			AccountRepository<Account> accountRepository,
			HibernateTemplate hibernateTemplate,
			TimeService timeService) {
		super(accountRepository);
		this.hibernateTemplate = hibernateTemplate;
		this.timeService = timeService;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		return Lists.newArrayList("fakeUuid");
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		Date currentDate = timeService.dateNow();
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session) throws HibernateException {
				final Query<?> query = session
						.createQuery("DELETE from RecipientFavourite WHERE expirationDate < :currentDate");
				query.setParameter("currentDate", currentDate);
				return (long) query.executeUpdate();
			}
		};
		Long execute = hibernateTemplate.execute(action);
		logger.debug("{} Favourite recipients with expirationDate before {}", execute, currentDate);
		BatchResultContext<FakeContext> res = new BatchResultContext<>(new FakeContext(identifier));
		res.setProcessed(true);
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<FakeContext> res = (BatchResultContext<FakeContext>) context;
		if (res.getProcessed()) {
			logInfo(batchRunContext, total, position, "Expired favourite recipients deleted successfully");
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occured while deleting expired favourite recipients",
				exception);
	}
}
