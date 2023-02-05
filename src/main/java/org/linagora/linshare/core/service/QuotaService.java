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

import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.exception.BusinessException;

public interface QuotaService {

	void checkIfUserCanAddFile(Account account, Long fileSize, ContainerQuotaType containerQuotaType)
			throws BusinessException;

	AccountQuota findByRelatedAccount(Account account) throws BusinessException;

	DomainQuota find(AbstractDomain domain) throws BusinessException;

	AccountQuota find(Account actor, Account owner, String uuid) throws BusinessException;

	Long getRealTimeUsedSpace(Account actor, Account owner, String uuid) throws BusinessException;

	Long getRealTimeUsedSpace(Account actor, Account owner, ContainerQuota cq) throws BusinessException;

	Long getRealTimeUsedSpace(Account actor, Account owner, DomainQuota cq) throws BusinessException;

	Long getTodayUsedSpace(Account actor, Account owner) throws BusinessException;

}
