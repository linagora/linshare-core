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
import org.linagora.linshare.core.business.service.DomainQuotaBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.domain.entities.Quota;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.QuotaResourceAccessControl;
import org.linagora.linshare.core.service.DomainQuotaService;

public class DomainQuotaServiceImpl extends GenericServiceImpl<Account, Quota> implements DomainQuotaService {

	DomainQuotaBusinessService business;

	public DomainQuotaServiceImpl(QuotaResourceAccessControl rac,
			DomainQuotaBusinessService domainQuotaBusinessService,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(rac, sanitizerInputHtmlBusinessService);
		this.business = domainQuotaBusinessService;
	}

	@Override
	public List<DomainQuota> findAll(Account actor) {
		return business.findAll();
	}

	@Override
	public List<DomainQuota> findAll(Account actor, AbstractDomain parentDomain) {
		return business.findAll(parentDomain);
	}

	@Override
	public List<DomainQuota> findAllByDomain(Account actor, AbstractDomain domain) {
		return business.findAllByDomain(domain);
	}

	@Override
	public DomainQuota find(Account actor, String uuid) {
		Validate.notNull(actor, "Actor must be set.");
		Validate.notNull(uuid, "Domain quota uuid must be set.");
		// checkReadPermission(actor, null, DomainQuota.class,
		// BusinessErrorCode.QUOTA_UNAUTHORIZED, null);
		DomainQuota domainQuota = business.find(uuid);
		if (domainQuota == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_QUOTA_NOT_FOUND, "Can not found domain quota " + uuid);
		}
		return domainQuota;
	}

	@Override
	public DomainQuota update(Account actor, DomainQuota dq) {
		Validate.notNull(actor, "Actor must be set.");
		Validate.notNull(dq, "Entity must be set.");
		Validate.notNull(dq.getQuota(), "Quota must be set");
		// checkCreatePermission(actor, null, DomainQuota.class,
		// BusinessErrorCode.QUOTA_UNAUTHORIZED, null, domain);
		// TODO FMA Quota manage override and maintenance flags.
		DomainQuota entity = find(actor, dq.getUuid());
		if (dq.getDefaultQuota() != null) {
			boolean validateDefaultQuota = dq.getDefaultQuota() <= dq.getQuota();
			Validate.isTrue(validateDefaultQuota, "The default_quota field can't be over quota in the same domain");
		}
		return business.update(entity, dq);
	}

}
