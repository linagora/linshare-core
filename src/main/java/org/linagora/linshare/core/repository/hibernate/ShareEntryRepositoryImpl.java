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
package org.linagora.linshare.core.repository.hibernate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.ShareRecipientStatistic;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.ShareEntryRepository;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class ShareEntryRepositoryImpl extends
		AbstractRepositoryImpl<ShareEntry> implements ShareEntryRepository {

	public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

	public ShareEntryRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(ShareEntry share) {
		DetachedCriteria det = DetachedCriteria.forClass(ShareEntry.class).add(
				Restrictions.eq("uuid", share.getUuid()));
		return det;
	}

	@Override
	public ShareEntry findByUuid(String uuid) {
		List<ShareEntry> entries = findByCriteria(Restrictions.eq("uuid", uuid));
		if (entries == null || entries.isEmpty()) {
			return null;
		} else if (entries.size() == 1) {
			return entries.get(0);
		} else {
			throw new IllegalStateException("Id must be unique");
		}
	}

	@Override
	public ShareEntry create(ShareEntry entity) throws BusinessException {
		entity.setCreationDate(new GregorianCalendar());
		entity.setModificationDate(new GregorianCalendar());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public ShareEntry update(ShareEntry entity) throws BusinessException {
		entity.setModificationDate(new GregorianCalendar());
		return super.update(entity);
	}

	@Override
	public ShareEntry getShareEntry(DocumentEntry documentEntry, User sender,
			User recipient) {
		List<ShareEntry> results = findByCriteria(
				Restrictions.eq("documentEntry", documentEntry),
				Restrictions.eq("entryOwner", sender),
				Restrictions.eq("recipient", recipient));
		if (results == null || results.isEmpty()) {
			return null;
		} else if (results.size() == 1) {
			return results.get(0);
		} else {
			throw new IllegalStateException("Sharing must be unique");
		}
	}

	@Override
	public List<ShareEntry> findAllMyRecievedShareEntries(User owner) {
		List<ShareEntry> entries = findByCriteria(Restrictions.eq("recipient",
				owner));
		if (entries == null) {
			logger.error("the result is null ! this should never happen.");
			return new ArrayList<ShareEntry>();
		}
		return entries;
	}

	@Override
	public List<String> findAllExpiredEntries() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.setProjection(Projections.property("uuid"));
		criteria.add(Restrictions.lt("expirationDate", Calendar.getInstance()));
		@SuppressWarnings("unchecked")
		List<String> list = listByCriteria(criteria);
		return list;
	}

	@Override
	public List<String> findAllSharesExpirationWithoutDownloadEntries(int daysLeftExpiration) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.setProjection(Projections.property("uuid"));

		Calendar dateBeforeExpiration = Calendar.getInstance();
		dateBeforeExpiration.add(Calendar.DATE, daysLeftExpiration);
		dateBeforeExpiration.set(Calendar.HOUR_OF_DAY, 0);
		dateBeforeExpiration.set(Calendar.MINUTE, 0);
		dateBeforeExpiration.set(Calendar.SECOND, 0);
		dateBeforeExpiration.set(Calendar.MILLISECOND, 0);
		Calendar nextDateBeforeExpiration = Calendar.getInstance();
		nextDateBeforeExpiration.set(Calendar.HOUR_OF_DAY, 0);
		nextDateBeforeExpiration.set(Calendar.MINUTE, 0);
		nextDateBeforeExpiration.set(Calendar.SECOND, 0);
		nextDateBeforeExpiration.set(Calendar.MILLISECOND, 0);
		nextDateBeforeExpiration.add(Calendar.DATE, daysLeftExpiration + 1);

		criteria.add(Restrictions.between("expirationDate", dateBeforeExpiration, nextDateBeforeExpiration));
		criteria.add(Restrictions.eq("downloaded", Long.valueOf(0)));
		@SuppressWarnings("unchecked")
		List<String> list = listByCriteria(criteria);
		return list;
	}

	@Override
	public List<String> findUpcomingExpiredEntries(Integer date) {
		Calendar calMin = Calendar.getInstance();
		calMin.add(Calendar.DAY_OF_MONTH, date);
		Calendar calMax = Calendar.getInstance();
		calMax.add(Calendar.DAY_OF_MONTH, date + 1);

		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.setProjection(Projections.distinct(Projections.property("uuid")))
				.add(Restrictions.lt("expirationDate", calMax))
				.add(Restrictions.gt("expirationDate", calMin));
		@SuppressWarnings("unchecked")
		List<String> list = listByCriteria(criteria);
		return list;
	}

	@Override
	public List<ShareEntry> findAllMyShareEntries(User owner,
			DocumentEntry entry) {
		return findByCriteria(Restrictions.conjunction()
				.add(Restrictions.eq("entryOwner", owner))
				.add(Restrictions.eq("documentEntry", entry)));
	}

	@Override
	public List<ShareRecipientStatistic> getTopSharesByFileSize(String domainUuid, String beginDate, String endDate) {
		checkDates(beginDate, endDate);
		List<ShareRecipientStatistic> shares = getInternalShares(domainUuid, beginDate, endDate, "sum");
		if (StringUtils.isBlank(domainUuid)) {
			shares.addAll(getExternalShares(beginDate, endDate, "sum"));
			shares.sort((s1,s2)-> s2.getShareTotalSize().compareTo(s1.getShareTotalSize()));
		}
		return shares;
	}

	@Override
	public List<ShareRecipientStatistic> getTopSharesByFileCount(String domainUuid, String beginDate, String endDate) {
		checkDates(beginDate, endDate);
		List<ShareRecipientStatistic> shares = getInternalShares(domainUuid, beginDate, endDate, "count");
		if (StringUtils.isBlank(domainUuid)) {
			shares.addAll(getExternalShares(beginDate, endDate, "count"));
			shares.sort((s1,s2)-> s2.getShareCount().compareTo(s1.getShareCount()));
		}
		return shares;
	}

	private List<ShareRecipientStatistic> getInternalShares(String domainUuid, String beginDate, String endDate, String orderOperation) {
		List<String> statements = new ArrayList<>();
		if (!StringUtils.isBlank(beginDate)) {
			statements.add("s.creationDate >= '" + beginDate + "'");
		}
		if (!StringUtils.isBlank(endDate)) {
			statements.add("s.creationDate <= '" + endDate + "'");
		}
		if (!StringUtils.isBlank(domainUuid)) {
			statements.add("s.recipient.domain.uuid = '" + domainUuid + "'");
		}
		String whereStatement = statements.isEmpty() ? "" : " WHERE " + String.join(" AND ", statements);

		@SuppressWarnings("unchecked") // we are casting to ShareRecipientStatistic within the query
		Query<ShareRecipientStatistic> query = getCurrentSession().createQuery(
				"SELECT NEW org.linagora.linshare.core.domain.entities.ShareRecipientStatistic" +
						"('internal', s.recipient.lsUuid, s.recipient.mail, s.recipient.domain.uuid, s.recipient.domain.label, count(s.documentEntry.size), sum(s.documentEntry.size))" +
						" FROM org.linagora.linshare.core.domain.entities.ShareEntry s" +
						whereStatement +
						" GROUP BY s.recipient.lsUuid" +
						" ORDER BY " + orderOperation + "(s.documentEntry.size) DESC");
		return query.list();
	}

	private List<ShareRecipientStatistic> getExternalShares(String beginDate, String endDate, String orderOperation) {
		List<String> statements = new ArrayList<>();
		if (!StringUtils.isBlank(beginDate)) {
			statements.add("s.creationDate >= '" + beginDate + "'");
		}
		if (!StringUtils.isBlank(endDate)) {
			statements.add("s.creationDate <= '" + endDate + "'");
		}
		String whereStatement = statements.isEmpty() ? "" : " WHERE " + String.join(" AND ", statements);

		@SuppressWarnings("unchecked") // we are casting to ShareRecipientStatistic within the query
		Query<ShareRecipientStatistic> query = getCurrentSession().createQuery(
				"SELECT NEW org.linagora.linshare.core.domain.entities.ShareRecipientStatistic" +
						"('external', '', s.anonymousUrl.contact.mail, '', '', count(s.documentEntry.size), sum(s.documentEntry.size))" +
						" FROM org.linagora.linshare.core.domain.entities.AnonymousShareEntry s" +
						whereStatement +
						" GROUP BY s.anonymousUrl.contact.mail" +
						" ORDER BY " + orderOperation + "(s.documentEntry.size) DESC");
		return query.list();
	}

	private void checkDates(String beginDate, String endDate) {
		try {
			Date bDate = StringUtils.isBlank(beginDate) ? null : FORMATTER.parse(beginDate);
			Date eDate = StringUtils.isBlank(endDate) ? null : FORMATTER.parse(endDate);
			if (bDate != null && eDate != null && bDate.after(eDate)) {
				throw new BusinessException("End date cannot be before begin date.");
			}
		} catch (ParseException e) {
			throw new BusinessException("Cannot parse the dates.");
		}
	}
}
