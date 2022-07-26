/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.Quota;
import org.linagora.linshare.core.domain.entities.fields.AccountQuotaDtoField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.AbstractResourceAccessControl;
import org.linagora.linshare.core.service.AccountQuotaService;
import org.linagora.linshare.webservice.utils.PageContainer;

public class AccountQuotaServiceImpl extends GenericServiceImpl<Account, Quota> implements AccountQuotaService {

	private final AccountQuotaBusinessService business;
	private final DomainPermissionBusinessService permissionService;

	public AccountQuotaServiceImpl(AbstractResourceAccessControl<Account, Account, Quota> rac,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService, AccountQuotaBusinessService business,
			DomainPermissionBusinessService permissionService) {
		super(rac, sanitizerInputHtmlBusinessService);
		this.business = business;
		this.permissionService = permissionService;
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
		preChecks(authUser, actor);
		Validate.notNull(accountQuota, "Account quota must be set.");
		Validate.notEmpty(accountQuota.getUuid(), "Account quota uuid must be set.");
		AccountQuota entity = find(authUser, actor, userUuid, accountQuota.getUuid());
		return business.update(entity, accountQuota);
	}

	@Override
	public PageContainer<AccountQuota> findAll(
			Account authUser, AbstractDomain domain,
			boolean includeNestedDomains,
			SortOrder sortOrder, AccountQuotaDtoField sortField,
			Optional<Long> greaterThanOrEqualTo, Optional<Long> lessThanOrEqualTo,
			Optional<ContainerQuotaType> containerQuotaType,
			Optional<String> beginDate, Optional<String> endDate,
			PageContainer<AccountQuota> container) {
		Validate.notNull(authUser, "authUser must be set.");
		if (!permissionService.isAdminforThisDomain(authUser, domain)) {
			throw new BusinessException(
					BusinessErrorCode.ACCOUNT_QUOTA_CANNOT_GET,
					"You are not allowed to query this domain");
		}
		Optional<LocalDate> begin = Optional.empty();
		Optional<LocalDate> end = Optional.empty();
		try {
			if (beginDate.isPresent()) {
				begin = Optional.of(LocalDate.parse(beginDate.get()));
			}
			if (endDate.isPresent()) {
				end = Optional.of(LocalDate.parse(endDate.get()));
			}
			if (begin.isPresent() && end.isPresent()) {
				if (end.get().isBefore(begin.get())) {
					throw new BusinessException(
							BusinessErrorCode.STATISTIC_DATE_RANGE_ERROR,
							String.format("begin date (%s) must be before end date (%s)", begin.get(), end.get())
					);
				}
			}
		} catch (DateTimeParseException e) {
			throw new BusinessException(BusinessErrorCode.STATISTIC_DATE_PARSING_ERROR, e.getMessage());
		}
		return business.findAll(authUser, domain, includeNestedDomains, sortOrder, sortField, greaterThanOrEqualTo, lessThanOrEqualTo, containerQuotaType, begin, end, container);
	}
}
