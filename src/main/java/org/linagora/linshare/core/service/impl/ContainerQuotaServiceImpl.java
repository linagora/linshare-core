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
package org.linagora.linshare.core.service.impl;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.ContainerQuotaBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.Quota;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.QuotaResourceAccessControl;
import org.linagora.linshare.core.service.ContainerQuotaService;

public class ContainerQuotaServiceImpl extends GenericServiceImpl<Account, Quota> implements ContainerQuotaService {

	private ContainerQuotaBusinessService business;

	public ContainerQuotaServiceImpl(QuotaResourceAccessControl rac,
			ContainerQuotaBusinessService ensembleQuotaBusinessService,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(rac, sanitizerInputHtmlBusinessService);
		this.business = ensembleQuotaBusinessService;
	}

	@Override
	public List<ContainerQuota> findAll(Account actor) {
		return business.findAll();
	}

	@Override
	public List<ContainerQuota> findAll(Account actor, AbstractDomain domain) {
		return business.findAll(domain);
	}

	@Override
	public ContainerQuota find(Account actor, AbstractDomain domain, ContainerQuotaType containerQuotaType) {
		Validate.notNull(actor, "Acctor must be set.");
		Validate.notNull(domain, "Domain must be set.");
		Validate.notNull(containerQuotaType, "ContainerQuotaType must be set.");
//		checkReadPermission(actor, null, ContainerQuota.class, BusinessErrorCode.QUOTA_UNAUTHORIZED, null, domain);
		ContainerQuota ensembleQuota = business.find(domain, containerQuotaType);
		if(ensembleQuota == null){
			throw new BusinessException(BusinessErrorCode.CONTAINER_QUOTA_NOT_FOUND, "Can not found ensemble " + containerQuotaType.toString() + " quota of the domain "+domain.getUuid());
		}
		return ensembleQuota;
	}

	@Override
	public ContainerQuota find(Account actor, String uuid) {
		ContainerQuota entity = business.find(uuid);
		if(entity == null){
			throw new BusinessException(BusinessErrorCode.CONTAINER_QUOTA_NOT_FOUND, "Can not found quota container : " + uuid);
		}
		return entity;
	}

	@Override
	public ContainerQuota update(Account actor, ContainerQuota cq) {
		Validate.notNull(actor, "Acctor must be set.");
		Validate.notNull(cq, "Entity must be set.");
		// checkUpdatePermission(actor, null, ContainerQuota.class,
		// BusinessErrorCode.QUOTA_UNAUTHORIZED, null, domain);
		ContainerQuota entity = find(actor, cq.getUuid());
		return business.update(entity, cq);
	}

}
