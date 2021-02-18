/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2017-2021 LINAGORA
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
package org.linagora.linshare.core.rac.impl;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.PermissionType;
import org.linagora.linshare.core.domain.constants.SharedSpaceActionType;
import org.linagora.linshare.core.domain.constants.SharedSpaceResourceType;
import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.WorkGroupNodeResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.repository.SharedSpaceMemberMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpacePermissionMongoRepository;

public class WorkGroupNodeResourceAccessControlImpl
		extends AbstractSharedSpaceResourceAccessControlImpl<Account, WorkGroupNode>
		implements WorkGroupNodeResourceAccessControl<Account, WorkGroupNode> {

	public WorkGroupNodeResourceAccessControlImpl(
			FunctionalityReadOnlyService functionalityService,
			SharedSpaceMemberMongoRepository sharedSpaceMemberMongoRepository,
			SharedSpacePermissionMongoRepository sharedSpacePermissionMongoRepository) {
		super(functionalityService, sharedSpaceMemberMongoRepository, sharedSpacePermissionMongoRepository);
	}

	@Override
	protected String getEntryRepresentation(WorkGroupNode entry) {
		return entry.toString();
	}

	@Override
	protected String getOwnerRepresentation(Account owner) {
		return owner.getAccountRepresentation();
	}

	@Override
	protected String getTargetedAccountRepresentation(Account targetedAccount) {
		return targetedAccount.getAccountRepresentation();
	}

	@Override
	protected boolean isAuthorized(Account authUser, Account targetedAccount, PermissionType permission,
			WorkGroupNode entry, Class<?> clazz, Object... opt) {
		Validate.notNull(permission);
		if (authUser.hasAllRights())
			return true;
		if (permission.equals(PermissionType.GET)) {
			if (hasReadPermission(authUser, targetedAccount, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.LIST)) {
			if (hasListPermission(authUser, targetedAccount, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.CREATE)) {
			if (hasCreatePermission(authUser, targetedAccount, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.UPDATE)) {
			if (hasUpdatePermission(authUser, targetedAccount, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.DELETE)) {
			if (hasDeletePermission(authUser, targetedAccount, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.DOWNLOAD)) {
			if (hasDownloadPermission(authUser, targetedAccount, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.DOWNLOAD_THUMBNAIL)) {
			if (hasDownloadTumbnailPermission(authUser, targetedAccount, entry, opt))
				return true;
		}
		if (clazz != null) {
			StringBuilder sb = getAuthUserStringBuilder(authUser);
			sb.append(" is trying to access to unauthorized resource named ");
			sb.append(clazz.toString());
			if (entry != null) {
				appendOwner(sb, entry, opt);
			}
			logger.error(sb.toString());
		}
		return false;
	}

	@Override
	public void checkDownloadPermission(Account authUser, Account targetedAccount, Class<?> clazz,
			BusinessErrorCode errCode, WorkGroupNode entry, Object... opt) throws BusinessException {
		String logMessage = " is not authorized to download the entry ";
		String exceptionMessage = "You are not authorized to download this entry.";
		checkPermission(authUser, targetedAccount, clazz, errCode, entry, PermissionType.DOWNLOAD, logMessage,
				exceptionMessage, opt);
	}

	@Override
	public void checkThumbNailDownloadPermission(Account authUser, Account targetedAccount, Class<?> clazz,
			BusinessErrorCode errCode, WorkGroupNode entry, Object... opt) throws BusinessException {
		String logMessage = " is not authorized to get the thumbnail of the entry ";
		String exceptionMessage = "You are not authorized to get the thumbnail of this entry.";
		checkPermission(authUser, targetedAccount, clazz, errCode, entry, PermissionType.DOWNLOAD_THUMBNAIL, logMessage,
				exceptionMessage, opt);
	}

	@Override
	protected boolean hasReadPermission(Account authUser, Account actor, WorkGroupNode entry, Object... opt) {
		return defaultSharedSpacePermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.THREAD_ENTRIES_GET, SharedSpaceActionType.READ);
	}

	@Override
	protected boolean hasListPermission(Account authUser, Account actor, WorkGroupNode entry, Object... opt) {
		WorkGroup workGroup = (WorkGroup) opt[0];
		return defaultSharedSpacePermissionCheck(authUser, actor, workGroup.getLsUuid(),
				TechnicalAccountPermissionType.THREAD_ENTRIES_LIST, SharedSpaceActionType.READ);
	}

	@Override
	protected boolean hasDeletePermission(Account authUser, Account actor, WorkGroupNode entry, Object... opt) {
		return defaultSharedSpacePermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.THREAD_ENTRIES_DELETE, SharedSpaceActionType.DELETE);
	}

	@Override
	protected boolean hasCreatePermission(Account authUser, Account actor, WorkGroupNode entry, Object... opt) {
		WorkGroup workGroup = (WorkGroup) opt[0];
		if (authUser.hasSafeRole()) {
			return true;
		}
		return defaultSharedSpacePermissionCheck(authUser, actor, workGroup.getLsUuid(),
				TechnicalAccountPermissionType.THREAD_ENTRIES_CREATE, SharedSpaceActionType.CREATE);
	}

	@Override
	protected boolean hasUpdatePermission(Account authUser, Account actor, WorkGroupNode entry, Object... opt) {
		return defaultSharedSpacePermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.THREAD_ENTRIES_UPDATE, SharedSpaceActionType.UPDATE);
	}

	protected boolean hasDownloadPermission(Account authUser, Account actor, WorkGroupNode entry, Object... opt) {
		return defaultSharedSpacePermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.THREAD_ENTRIES_DOWNLOAD, SharedSpaceActionType.DOWNLOAD);
	}

	protected boolean hasDownloadTumbnailPermission(Account authUser, Account actor, WorkGroupNode entry,
			Object... opt) {
		return defaultSharedSpacePermissionCheck(authUser, actor, entry,
				TechnicalAccountPermissionType.THREAD_ENTRIES_DOWNLOAD_THUMBNAIL,
				SharedSpaceActionType.DOWNLOAD_THUMBNAIL);
	}

	protected SharedSpaceResourceType getSharedSpaceResourceType(WorkGroupNode entry) {
		switch (entry.getNodeType()) {
			case ROOT_FOLDER:
			case FOLDER:	
				return SharedSpaceResourceType.FOLDER;
			case DOCUMENT:
			case DOCUMENT_REVISION:
				return SharedSpaceResourceType.FILE;
			default:
				throw new BusinessException(BusinessErrorCode.INVALID_WORK_GROUP_NODE_TYPE, "Bad workgroup node type mapping");
			
		}
	}

	protected boolean defaultSharedSpacePermissionCheck(Account authUser, Account actor, WorkGroupNode entry,
			TechnicalAccountPermissionType permission, SharedSpaceActionType action) {
		if (authUser.hasSuperAdminRole()) {
			return true;
		}
		if (authUser.hasDelegationRole()) {
			return hasPermission(authUser, permission);
		}
		if (authUser.isInternal() || authUser.isGuest()) {
			if (actor != null && authUser.equals(actor)) {
				SharedSpaceMember foundMember = sharedSpaceMemberMongoRepository.findByAccountAndNode(actor.getLsUuid(),
						entry.getWorkGroup());
				if (foundMember == null) {
					return false;
				}
				return hasPermission(foundMember.getRole().getUuid(), action, getSharedSpaceResourceType(entry));
			}
		}
		return false;
	}

	@Override
	protected String getSharedSpaceNodeUuid(WorkGroupNode entry) {
		return entry.getWorkGroup();
	}

	@Override
	protected Account getOwner(WorkGroupNode entry, Object... opt) {
		return null;
	}

	@Override
	protected SharedSpaceResourceType getSharedSpaceResourceType() {
		return SharedSpaceResourceType.FOLDER;
	}
}
