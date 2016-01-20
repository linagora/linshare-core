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
package org.linagora.linshare.core.repository.hibernate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.ShareEntryRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class ShareEntryRepositoryImpl extends
		AbstractRepositoryImpl<ShareEntry> implements ShareEntryRepository {

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
	public List<ShareEntry> findUpcomingExpiredEntries(Integer date) {
		Calendar calMin = Calendar.getInstance();
		calMin.add(Calendar.DAY_OF_MONTH, date);
		Calendar calMax = Calendar.getInstance();
		calMax.add(Calendar.DAY_OF_MONTH, date + 1);
		return findByCriteria(Restrictions.lt("expirationDate", calMax),
				Restrictions.gt("expirationDate", calMin));
	}

	@Override
	public List<ShareEntry> findAllMyShareEntries(User owner,
			DocumentEntry entry) {
		return findByCriteria(Restrictions.conjunction()
				.add(Restrictions.eq("entryOwner", owner))
				.add(Restrictions.eq("documentEntry", entry)));
	}
}
