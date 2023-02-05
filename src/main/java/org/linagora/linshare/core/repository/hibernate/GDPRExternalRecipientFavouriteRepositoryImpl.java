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

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.GDPRExternalRecipientFavourite;
import org.linagora.linshare.core.repository.GDPRExternalRecipientFavouriteRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class GDPRExternalRecipientFavouriteRepositoryImpl extends AbstractRepositoryImpl<GDPRExternalRecipientFavourite> implements GDPRExternalRecipientFavouriteRepository {

	public GDPRExternalRecipientFavouriteRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(GDPRExternalRecipientFavourite entity) {
		return DetachedCriteria.forClass(getPersistentClass())
			.add(Restrictions.eq("uuid", entity.getUuid()));
	}

	@Override
	public List<String> findUuidByExpirationDateLessThan(Date date) {
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(GDPRExternalRecipientFavourite.class);
		detachedCriteria.setProjection(Projections.property("uuid"));
		detachedCriteria.add(Restrictions.lt("expirationDate", date));
		return listByCriteria(detachedCriteria);
	}

	@Override
	public GDPRExternalRecipientFavourite findByUuid(String uuid) {
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(GDPRExternalRecipientFavourite.class);
		detachedCriteria.add(Restrictions.eq("uuid", uuid));
		return DataAccessUtils.singleResult(findByCriteria(detachedCriteria));
	}
}
