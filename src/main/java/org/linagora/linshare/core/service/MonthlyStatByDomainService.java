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
package org.linagora.linshare.core.service;

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DomainMonthlyStat;
import org.linagora.linshare.core.exception.BusinessException;

public interface MonthlyStatByDomainService {

	DomainMonthlyStat create(Account actor, AbstractDomain domain, Date beginDate, Date endDate)
			throws BusinessException;

	List<AbstractDomain> findDomainBetweenTwoDates(Account actor, Date beginDate, Date endDate)
			throws BusinessException;

	void deleteBeforeDate(Account actor, Date creationDate) throws BusinessException;
}
