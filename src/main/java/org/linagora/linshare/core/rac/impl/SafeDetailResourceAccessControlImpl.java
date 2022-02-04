/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2017-2022 LINAGORA
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

package org.linagora.linshare.core.rac.impl;

import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.rac.SafeDetailResourceAccessControl;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.mongo.entities.SafeDetail;

public class SafeDetailResourceAccessControlImpl
		extends AbstractResourceAccessControlImpl<Account, Account, SafeDetail>
		implements SafeDetailResourceAccessControl {

	private AccountRepository<Account> accountRepository;

	public SafeDetailResourceAccessControlImpl(AccountRepository<Account> accountRepository,
			FunctionalityReadOnlyService functionalityService) {
		super(functionalityService);
		this.accountRepository = accountRepository;
	}

	@Override
	protected boolean hasReadPermission(Account actor, Account account, SafeDetail entry, Object... opt) {
		if (actor.hasSafeRole()) {
			return true;
		} else if (actor.hasDelegationRole()) {
			return hasPermission(actor, TechnicalAccountPermissionType.SAFE_DETAIL_GET);
		} else if (actor.isInternal() || actor.isGuest()) {
			return (account != null && actor.equals(account)
					&& actor.getLsUuid().equals(entry.getAccountUuid()));
		}
		return false;
	}

	@Override
	protected boolean hasListPermission(Account actor, Account account, SafeDetail entry, Object... opt) {
		return defaultPermissionCheck(actor, account, entry, TechnicalAccountPermissionType.SAFE_DETAIL_LIST, false);
	}

	@Override
	protected boolean hasDeletePermission(Account actor, Account account, SafeDetail entry, Object... opt) {
		return defaultPermissionCheck(actor, account, entry, TechnicalAccountPermissionType.SAFE_DETAIL_DELETE);
	}

	@Override
	protected boolean hasCreatePermission(Account actor, Account account, SafeDetail entry, Object... opt) {
		return defaultPermissionCheck(actor, account, entry, TechnicalAccountPermissionType.SAFE_DETAIL_CREATE, false);
	}

	@Override
	protected boolean hasUpdatePermission(Account actor, Account account, SafeDetail entry, Object... opt) {
		return false;
	}

	@Override
	protected String getTargetedAccountRepresentation(Account targetedAccount) {
		return targetedAccount.getAccountRepresentation();
	}

	@Override
	protected String getEntryRepresentation(SafeDetail entry) {
		return entry.getUuid();
	}

	@Override
	protected Account getOwner(SafeDetail entry, Object... opt) {
		return accountRepository.findByLsUuid(entry.getAccountUuid());
	}
}
