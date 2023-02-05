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

import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.rac.AbstractUploadRequestResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;

public abstract class AbstractUploadRequestResourceAccessControlImpl<R, E>
		extends AbstractResourceAccessControlImpl<Account, R, E> implements AbstractUploadRequestResourceAccessControl<R, E> {

	public AbstractUploadRequestResourceAccessControlImpl(FunctionalityReadOnlyService functionalityService) {
		super(functionalityService);
	}

	/**
	 * Checking if the functionality Upload Request is enabled before checking the
	 * default permissions
	 * 
	 * @param authUser
	 * @param actor
	 * @param entry
	 * @param permission
	 * @param checkActorIsEntryOwner
	 * @return
	 */
	protected boolean defaultUploadRequestPermissionCheck(Account authUser, Account actor, E entry,
			TechnicalAccountPermissionType permission, boolean checkActorIsEntryOwner) {
		if (isEnabled(authUser)) {
			if (authUser.hasUploadRequestRole()) {
				logger.trace("authUser has UploadRequest Role");
				return true;
			}
			return defaultPermissionCheck(authUser, actor, entry, permission, checkActorIsEntryOwner);
		}
		return false;
	}

	protected boolean defaultUploadRequestPermissionCheck(Account authUser, Account actor, E entry,
			TechnicalAccountPermissionType permission) {
		return defaultUploadRequestPermissionCheck(authUser, actor, entry, permission, true);
	}

	private boolean isEnabled(Account authUser) {
		Functionality func = functionalityService.getUploadRequestFunctionality(authUser.getDomain());
		return func.getActivationPolicy().getStatus();
	}
}
