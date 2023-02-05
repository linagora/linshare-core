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
package org.linagora.linshare.core.business.service;

import java.util.List;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainAccessRule;
import org.linagora.linshare.core.exception.BusinessException;

public interface DomainAccessPolicyBusinessService {

	public DomainAccessRule retrieveDomainAccessRule(long id);

	public void deleteDomainAccessRule(long persistenceID)
			throws BusinessException;

	public DomainAccessRule find(long id) throws BusinessException;;

	public DomainAccessRule create(DomainAccessRule domainAccessRule) throws BusinessException;

	public DomainAccessRule update(DomainAccessRule domainAccessRule)throws BusinessException;

	public void delete(DomainAccessRule domainAccessRule) throws BusinessException;

	public List<DomainAccessRule> findByDomain(AbstractDomain domain) throws BusinessException;

	public boolean domainHasPolicyRules(AbstractDomain domain) throws BusinessException;
}
