/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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

import java.util.Set;

import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.UploadRequestUrlResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;

public class UploadRequestUrlResourceAccessControlImpl
		extends AbstractUploadRequestResourceAccessControlImpl<Account, UploadRequestUrl>
		implements UploadRequestUrlResourceAccessControl {

	public UploadRequestUrlResourceAccessControlImpl(FunctionalityReadOnlyService functionalityService) {
		super(functionalityService);
	}

	@Override
	protected boolean hasReadPermission(Account authUser, Account actor, UploadRequestUrl entry, Object... opt) {
		return defaultUploadRequestPermissionCheck(authUser, actor, entry, TechnicalAccountPermissionType.UPLOAD_REQUEST_URL_GET);
	}

	@Override
	protected boolean hasListPermission(Account authUser, Account actor, UploadRequestUrl entry, Object... opt) {
		return defaultPermissionCheck(authUser, actor, entry, TechnicalAccountPermissionType.UPLOAD_REQUEST_URL_LIST, false);
	}

	@Override
	protected boolean hasDeletePermission(Account authUser, Account actor, UploadRequestUrl uploadRequestUrl, Object... opt) {
		UploadRequest uploadRequest = uploadRequestUrl.getUploadRequest();
		Set<UploadRequestUrl> uploadRequestURLs = uploadRequest.getUploadRequestURLs();
		UploadRequestGroup uploadRequestGroup = uploadRequest.getUploadRequestGroup();
		if (uploadRequestGroup.getRestricted()) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_DELETE_RECIPIENT_FROM_RESTRICTED_REQUEST,
					"Cannot delete a recipient of an upload request in mode restricted");
		}
		if (uploadRequestURLs.size() < 2) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_DELETE_LAST_RECIPIENT,
					"Cannot delete the last recipient of an upload request in shared mode");
		} else if (!uploadRequestUrl.getUploadRequestEntries().isEmpty()) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_ENTRY_URL_EXISTS,
					"Cannot delete upload request url with existed entries");
		}
		return defaultPermissionCheck(authUser, actor, uploadRequestUrl, TechnicalAccountPermissionType.UPLOAD_REQUEST_URL_DELETE);
	}

	@Override
	protected boolean hasCreatePermission(Account authUser, Account actor, UploadRequestUrl entry, Object... opt) {
		return defaultPermissionCheck(authUser, actor, entry, TechnicalAccountPermissionType.UPLOAD_REQUEST_URL_CREATE, false);
	}

	@Override
	protected boolean hasUpdatePermission(Account authUser, Account actor, UploadRequestUrl entry, Object... opt) {
		return defaultPermissionCheck(authUser, actor, entry, TechnicalAccountPermissionType.UPLOAD_REQUEST_URL_UPDATE);
	}

	@Override
	protected String getTargetedAccountRepresentation(Account targetedAccount) {
		return targetedAccount.getAccountRepresentation();
	}

	@Override
	protected String getEntryRepresentation(UploadRequestUrl entry) {
		return entry.getUuid();
	}

	@Override
	protected Account getOwner(UploadRequestUrl entry, Object... opt) {
		return entry.getUploadRequest().getUploadRequestGroup().getOwner();
	}

}
