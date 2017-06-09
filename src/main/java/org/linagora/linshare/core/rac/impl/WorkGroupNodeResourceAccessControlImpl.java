/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.rac.impl;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.PermissionType;
import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.WorkGroupNodeResourceAccessControl;
import org.linagora.linshare.core.repository.ThreadMemberRepository;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.mongo.entities.WorkGroupNode;

public class WorkGroupNodeResourceAccessControlImpl
		extends AbstractResourceAccessControlImpl<Account, Account, WorkGroupNode> implements WorkGroupNodeResourceAccessControl<Account, WorkGroupNode> {

	protected final ThreadRepository threadRepository;
	
	protected final ThreadMemberRepository threadMemberRepository;

	public WorkGroupNodeResourceAccessControlImpl(
			FunctionalityReadOnlyService functionalityService,
			ThreadRepository threadRepository,
			ThreadMemberRepository threadMemberRepository) {
		super(functionalityService);
		this.threadRepository = threadRepository;
		this.threadMemberRepository = threadMemberRepository;
	}

	@Override
	protected String getEntryRepresentation(WorkGroupNode entry) {
		return entry.toString();
	}

	@Override
	protected Account getOwner(WorkGroupNode entry, Object... opt) {
		Thread workGroup = threadRepository.findByLsUuid(entry.getWorkGroup());
		return workGroup;
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
	protected boolean isAuthorized(Account actor, Account targetedAccount,
			PermissionType permission, WorkGroupNode entry, Class<?> clazz, Object... opt) {
		Validate.notNull(permission);
		if (actor.hasAllRights())
			return true;
		if (permission.equals(PermissionType.GET)) {
			if (hasReadPermission(actor, targetedAccount, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.LIST)) {
			if (hasListPermission(actor, targetedAccount, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.CREATE)) {
			if (hasCreatePermission(actor, targetedAccount, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.UPDATE)) {
			if (hasUpdatePermission(actor, targetedAccount, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.DELETE)) {
			if (hasDeletePermission(actor, targetedAccount, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.DOWNLOAD)) {
			if (hasDownloadPermission(actor, targetedAccount, entry, opt))
				return true;
		} else if (permission.equals(PermissionType.DOWNLOAD_THUMBNAIL)) {
			if (hasDownloadTumbnailPermission(actor, targetedAccount, entry,
					opt))
				return true;
		}
		if (clazz != null) {
			StringBuilder sb = getActorStringBuilder(actor);
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
	public void checkDownloadPermission(Account actor, Account targetedAccount,
			Class<?> clazz, BusinessErrorCode errCode, WorkGroupNode entry, Object... opt)
			throws BusinessException {
		String logMessage = " is not authorized to download the entry ";
		String exceptionMessage = "You are not authorized to download this entry.";
		checkPermission(actor, targetedAccount, clazz, errCode, entry,
				PermissionType.DOWNLOAD, logMessage, exceptionMessage, opt);
	}

	@Override
	public void checkThumbNailDownloadPermission(Account actor,
			Account targetedAccount, Class<?> clazz, BusinessErrorCode errCode,
			WorkGroupNode entry, Object... opt) throws BusinessException {
		String logMessage = " is not authorized to get the thumbnail of the entry ";
		String exceptionMessage = "You are not authorized to get the thumbnail of this entry.";
		checkPermission(actor, targetedAccount, clazz, errCode, entry,
				PermissionType.DOWNLOAD_THUMBNAIL, logMessage,
				exceptionMessage, opt);
	}

	@Override
	protected boolean hasReadPermission(Account actor, Account account, WorkGroupNode entry, Object... opt) {
		if (actor.hasAllRights()) {
			return true;
		}
		if (actor.hasDelegationRole()) {
			return hasPermission(actor,
					TechnicalAccountPermissionType.THREAD_ENTRIES_GET);
		}
		if (opt.length > 0 && opt[0] instanceof Thread) {
			return threadMemberRepository.findUserThreadMember((Thread) opt[0],
					(User) account) != null;
		}
		return false;
	}

	@Override
	protected boolean hasListPermission(Account actor, Account account, WorkGroupNode entry, Object... opt) {
		if (actor.hasAllRights()) {
			return true;
		}
		if (actor.hasDelegationRole()) {
			return hasPermission(actor,
					TechnicalAccountPermissionType.THREAD_ENTRIES_LIST);
		}
		if (opt.length > 0 && opt[0] instanceof Thread) {
			return threadMemberRepository.findUserThreadMember((Thread) opt[0],
					(User) account) != null;
		}
		return false;
	}

	@Override
	protected boolean hasDeletePermission(Account actor, Account account, WorkGroupNode entry, Object... opt) {
		if (actor.hasAllRights()) {
			return true;
		}
		if (actor.hasDelegationRole()) {
			return hasPermission(actor,
					TechnicalAccountPermissionType.THREAD_ENTRIES_DELETE);
		}
		if (opt.length > 0 && opt[0] instanceof Thread) {
			ThreadMember member = threadMemberRepository.findUserThreadMember((Thread) opt[0],
					(User) account);
			if (member != null && member.getAdmin()) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasCreatePermission(Account actor, Account account, WorkGroupNode entry, Object... opt) {
		if (actor.hasAllRights()) {
			return true;
		}
		if (actor.hasDelegationRole()) {
			return hasPermission(actor,
					TechnicalAccountPermissionType.THREAD_ENTRIES_CREATE);
		}
		if (opt.length > 0 && opt[0] instanceof Thread) {
			ThreadMember member = threadMemberRepository.findUserThreadMember((Thread) opt[0],
					(User) account);
			if (member != null && member.getCanUpload()) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasUpdatePermission(Account actor, Account account, WorkGroupNode entry, Object... opt) {
		if (actor.hasAllRights()) {
			return true;
		}
		if (actor.hasDelegationRole()) {
			return hasPermission(actor,
					TechnicalAccountPermissionType.THREAD_ENTRIES_UPDATE);
		}
		Account workGroup = getOwner(entry, opt);
		ThreadMember member = threadMemberRepository.findUserThreadMember(
				workGroup, (User) account);
		if (member != null && member.getCanUpload()) {
			return true;
		}
		return false;
	}

	protected boolean hasDownloadPermission(Account actor,
			Account account, WorkGroupNode entry, Object... opt) {
		if (actor.hasAllRights()) {
			return true;
		}
		if (actor.hasDelegationRole()) {
			return hasPermission(actor,
					TechnicalAccountPermissionType.THREAD_ENTRIES_DOWNLOAD);
		}
		if (opt.length > 0 && opt[0] instanceof Thread) {
			ThreadMember member = threadMemberRepository.findUserThreadMember((Thread) opt[0],
					(User) account);
			if (member != null) {
				return true;
			}
		}
		return false;
	}

	protected boolean hasDownloadTumbnailPermission(Account actor,
			Account account, WorkGroupNode entry, Object... opt) {
		if (actor.hasAllRights()) {
			return true;
		}
		if (actor.hasDelegationRole()) {
			return hasPermission(actor,
					TechnicalAccountPermissionType.THREAD_ENTRIES_DOWNLOAD_THUMBNAIL);
		}
		if (opt.length > 0 && opt[0] instanceof Thread) {
			ThreadMember member = threadMemberRepository.findUserThreadMember((Thread) opt[0],
					(User) account);
			if (member != null) {
				return true;
			}
		}
		return false;
	}
}
