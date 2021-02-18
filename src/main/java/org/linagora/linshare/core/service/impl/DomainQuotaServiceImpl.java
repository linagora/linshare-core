/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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
			Validate.isTrue(validateDefaultQuota, "The default_quota filed can't be over quota in the same domain");
		}
		return business.update(entity, dq);
	}

}
