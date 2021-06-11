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
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.Quota;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.QuotaResourceAccessControl;
import org.linagora.linshare.core.service.AccountQuotaService;

public class AccountQuotaServiceImpl extends GenericServiceImpl<Account, Quota> implements AccountQuotaService {

	private AccountQuotaBusinessService business;

	public AccountQuotaServiceImpl(QuotaResourceAccessControl rac,
			AccountQuotaBusinessService accountQuotaBusinessService,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService
			) {
		super(rac, sanitizerInputHtmlBusinessService);
		this.business = accountQuotaBusinessService;
	}

	@Deprecated
	@Override
	public AccountQuota find(Account actor, String uuid) {
		Validate.notNull(actor, "Actor must be set.");
		Validate.notEmpty(uuid, "Uuid must be set.");
//		checkReadPermission(actor, owner, AccountQuota.class, BusinessErrorCode.QUOTA_UNAUTHORIZED, null);
		AccountQuota accountQuota = business.find(uuid);
		if (accountQuota == null) {
			throw new BusinessException(BusinessErrorCode.ACCOUNT_QUOTA_NOT_FOUND,
					"Can not found account quota with uuid : " + uuid);
		}
		return accountQuota;
	}

	@Override
	public AccountQuota find(Account authUser, Account actor, String userUuid, String quotaUuid) {
		Validate.notNull(authUser, "authUser must be set.");
		Validate.notNull(actor, "Actor must be set.");
		Validate.notEmpty(quotaUuid, "Uuid must be set.");
		AccountQuota accountQuota = business.find(quotaUuid);
		if (accountQuota == null) {
			throw new BusinessException(BusinessErrorCode.ACCOUNT_QUOTA_NOT_FOUND,
					"Can not found account quota with uuid : " + quotaUuid);
		}
		if (!userUuid.equals(accountQuota.getAccount().getLsUuid())) {
			throw new BusinessException(BusinessErrorCode.ACCOUNT_QUOTA_CANNOT_GET,
					"The chosen quota is not related to the requested account, please check the entered accountUuid and quotaUuid");
		}
		return accountQuota;
	}

	@Override
	public List<AccountQuota> findAll(Account actor) {
		Validate.notNull(actor, "Actor must be set.");
		return business.findAll();
	}

	@Deprecated
	@Override
	public AccountQuota update(Account actor, AccountQuota aq) {
		Validate.notNull(actor, "Actor must be set.");
		Validate.notNull(aq, "Account quota must be set.");
		Validate.notEmpty(aq.getUuid(), "Account quota uuid must be set.");
		AccountQuota entity = find(actor, aq.getUuid());
		// checkUpdatePermission(actor, owner, AccountQuota.class,
		// BusinessErrorCode.QUOTA_UNAUTHORIZED, null);
		return business.update(entity, aq);
	}

	@Override
	public AccountQuota update(Account authUser, Account actor, String userUuid, AccountQuota accountQuota) {
		Validate.notNull(actor, "Actor must be set.");
		Validate.notNull(accountQuota, "Account quota must be set.");
		Validate.notEmpty(accountQuota.getUuid(), "Account quota uuid must be set.");
		AccountQuota entity = find(authUser, actor, userUuid, accountQuota.getUuid());
		return business.update(entity, accountQuota);
	}

}
