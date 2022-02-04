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
package org.linagora.linshare.core.repository.hibernate;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AnonymousShareEntryRepository;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class AnonymousShareEntryRepositoryImpl extends AbstractRepositoryImpl<AnonymousShareEntry>implements AnonymousShareEntryRepository {

	public AnonymousShareEntryRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(AnonymousShareEntry entity) {
		DetachedCriteria det = DetachedCriteria.forClass(AnonymousShareEntry.class).add(Restrictions.eq( "uuid", entity.getUuid()) );
		return det;
	}
	
	@Override
	public AnonymousShareEntry findById(String uuid) {
		List<AnonymousShareEntry> entries = findByCriteria(Restrictions.eq("uuid", uuid));
        if (entries == null || entries.isEmpty()) {
            return null;
        } else if (entries.size() == 1) {
            return entries.get(0);
        } else {
            throw new IllegalStateException("Id must be unique");
        }
    }
	
	@Override
	public AnonymousShareEntry create(AnonymousShareEntry entity) throws BusinessException {
		entity.setCreationDate(new GregorianCalendar());
		entity.setModificationDate(new GregorianCalendar());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}
	

	@Override
	public AnonymousShareEntry update(AnonymousShareEntry entity) throws BusinessException {
		entity.setModificationDate(new GregorianCalendar());
		return super.update(entity);
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> findAllExpiredEntries() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.setProjection(Projections.property("uuid"));
		criteria.add(Restrictions.lt("expirationDate", Calendar.getInstance()));
		List<String> list = listByCriteria(criteria);
		return list;
	}

	@SuppressWarnings("unchecked")
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
		List<String> list = listByCriteria(criteria);
		return list;
	}

	@Override
	public List<AnonymousShareEntry> findAllMyAnonymousShareEntries(User owner,
			DocumentEntry entry) {
		 return findByCriteria(
				 Restrictions.conjunction()
				 .add(Restrictions.eq("entryOwner", owner))
				 .add(Restrictions.eq("documentEntry", entry)));
	}
}
