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

import org.linagora.linshare.core.domain.entities.TechnicalAccount;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.TechnicalAccountRepository;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class TechnicalAccountRepositoryImpl extends GenericUserRepositoryImpl<TechnicalAccount>
		implements TechnicalAccountRepository {

	public TechnicalAccountRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public TechnicalAccount create(TechnicalAccount entity) throws BusinessException {
		return super.create(entity);
	}

	@Override
	public TechnicalAccount findByLogin(String login) {
		return super.findByLsUuid(login);
	}

	@Override
	public TechnicalAccount findByLoginAndDomain(String domain, String login) {
		return super.findByLsUuid(login);
	}
}
