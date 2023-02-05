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

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.PasswordHistory;
import org.linagora.linshare.core.repository.PasswordHistoryRepository;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class PasswordHistoryRepositoryImpl extends AbstractRepositoryImpl<PasswordHistory> implements PasswordHistoryRepository{

	public PasswordHistoryRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(PasswordHistory entity) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass())
				.add(Restrictions.eq("id", entity.getId()));
		return det;
	}

	@Override
	public List<PasswordHistory> findAllByAccount(Account account) {
		return findByCriteria(DetachedCriteria.forClass(getPersistentClass())
				.add(Restrictions.eq("account", account)));
	}

	@Override
	public PasswordHistory findOldestByAccount(Account account) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("account", account));
		criteria.addOrder(Order.asc("creationDate"));
		List<PasswordHistory> histories = findByCriteria(criteria);
		if (histories.isEmpty()) {
			return null;
		}
		return histories.get(0);
	}
}
