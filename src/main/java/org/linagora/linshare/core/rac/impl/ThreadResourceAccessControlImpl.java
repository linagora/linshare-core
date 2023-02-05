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
package org.linagora.linshare.core.rac.impl;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.rac.ThreadResourceAccessControl;
import org.linagora.linshare.core.repository.ThreadMemberRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;

public class ThreadResourceAccessControlImpl extends
		AbstractResourceAccessControlImpl<Account, Account, WorkGroup> implements
		ThreadResourceAccessControl {

	private final ThreadMemberRepository threadMemberRepository;

	public ThreadResourceAccessControlImpl(
			final FunctionalityReadOnlyService functionalityService,
			final ThreadMemberRepository threadMemberRepository) {
		super(functionalityService);
		this.threadMemberRepository = threadMemberRepository;
	}

	@Override
	protected Account getOwner(WorkGroup entry, Object... opt) {
		Account owner = null;
		if (opt != null && opt.length > 0) {
			if (opt[0] instanceof Account) {
				owner = (Account) opt[0];
			}
		}
		return owner;
	}

	@Override
	protected boolean hasReadPermission(Account authUser, Account actor,
			WorkGroup entry, Object... opt) {
		Validate.notNull(authUser);
		Validate.notNull(actor);
		Validate.notNull(entry);
		if (authUser.hasAllRights()) {
			return true;
		}
		if (actor.hasSafeRole()) {
			return true;
		}
		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser,
					TechnicalAccountPermissionType.THREADS_GET);
		}
		return isUserMember(actor, entry);
	}

	@Override
	protected boolean hasListPermission(Account authUser, Account actor,
			WorkGroup entry, Object... opt) {
		Validate.notNull(authUser);
		// Owner is always null, because threads have not actor.

		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser,
					TechnicalAccountPermissionType.THREADS_LIST);
		}
		if (authUser.hasAllRights()) {
			return true;
		}
		return isUserMember(actor, entry);
	}

	@Override
	protected boolean hasDeletePermission(Account authUser, Account actor,
			WorkGroup entry, Object... opt) {
		Validate.notNull(authUser);

		Validate.notNull(entry);
		// Owner is always null, because threads have not actor.

		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser,
					TechnicalAccountPermissionType.THREADS_DELETE);
		}
		if (authUser.hasAllRights()) {
			return true;
		}
		return isUserAdmin(actor, entry);
	}

	@Override
	protected boolean hasCreatePermission(Account authUser, Account actor,
			WorkGroup entry, Object... opt) {
		Validate.notNull(authUser);
		// Owner is always null, because threads do not have actor.

		if (authUser.hasAllRights()) {
			return true;
		}
		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser,
					TechnicalAccountPermissionType.THREADS_CREATE);
		}
		Functionality creation = functionalityService.getWorkGroupCreationRight(actor.getDomain());
		if (!creation.getActivationPolicy().getStatus()){
			String message = "You can not create thread, you are not authorized.";
			logger.error(message);
			logger.error("The current domain does not allow you to create a thread.");
			return false;
		}
		return true;
	}

	@Override
	protected boolean hasUpdatePermission(Account authUser, Account actor,
			WorkGroup entry, Object... opt) {
		Validate.notNull(authUser);

		Validate.notNull(entry);
		// Owner is always null, because threads have not actor.

		if (authUser.hasAllRights()) {
			return true;
		}
		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser,
					TechnicalAccountPermissionType.THREADS_UPDATE);
		}
		return isUserAdmin(authUser, entry);
	}

	@Override
	protected String getEntryRepresentation(WorkGroup entry) {
		return '(' + entry.getLsUuid() + ')';
	}

	private boolean isUserMember(Account user, WorkGroup workGroup) {
		boolean ret = threadMemberRepository.findUserThreadMember(workGroup,
				(User) user) != null;
		logger.debug(user + " member of " + workGroup + " : " + ret);
		return ret;
	}

	private boolean isUserAdmin(Account user, WorkGroup workGroup) {
		boolean ret = threadMemberRepository.isUserAdmin((User) user, workGroup);
		logger.debug(user + " admin of " + workGroup + " : " + ret);
		return ret;
	}

	@Override
	protected String getTargetedAccountRepresentation(Account targetedAccount) {
		return targetedAccount.getAccountRepresentation();
	}
}
