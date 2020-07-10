/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.UploadRequestTemplate;
import org.linagora.linshare.core.rac.UploadRequestTemplateResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;

public class UploadRequestTemplateResourceAccessControlImpl
		extends AbstractUploadRequestResourceAccessControlImpl<Account, UploadRequestTemplate>
		implements UploadRequestTemplateResourceAccessControl {

	public UploadRequestTemplateResourceAccessControlImpl(FunctionalityReadOnlyService functionalityService) {
		super(functionalityService);
	}

	@Override
	protected boolean hasReadPermission(Account authUser, Account actor, UploadRequestTemplate entry, Object... opt) {
		if (isEnable(authUser)) {
			return defaultPermissionCheck(authUser, actor, entry, TechnicalAccountPermissionType.UPLOAD_REQUEST_TEMPLATE_GET);
		}
		return false;
	}

	@Override
	protected boolean hasListPermission(Account authUser, Account actor, UploadRequestTemplate entry, Object... opt) {
		if (isEnable(authUser)) {
			return defaultPermissionCheck(authUser, actor, entry, TechnicalAccountPermissionType.UPLOAD_REQUEST_TEMPLATE_LIST, false);
		}
		return false;
	}

	@Override
	protected boolean hasDeletePermission(Account authUser, Account actor, UploadRequestTemplate entry, Object... opt) {
		if (isEnable(authUser)) {
			return defaultPermissionCheck(authUser, actor, entry, TechnicalAccountPermissionType.UPLOAD_REQUEST_TEMPLATE_DELETE);
		}
		return false;
	}

	@Override
	protected boolean hasCreatePermission(Account authUser, Account actor, UploadRequestTemplate entry, Object... opt) {
		if (isEnable(authUser)) {
			return defaultPermissionCheck(authUser, actor, entry, TechnicalAccountPermissionType.UPLOAD_REQUEST_TEMPLATE_CREATE, false);
		}
		return false;
	}

	@Override
	protected boolean hasUpdatePermission(Account authUser, Account actor, UploadRequestTemplate entry, Object... opt) {
		if (isEnable(authUser)) {
			return defaultPermissionCheck(authUser, actor, entry, TechnicalAccountPermissionType.UPLOAD_REQUEST_TEMPLATE_UPDATE);
		}
		return false;
	}

	@Override
	protected String getTargetedAccountRepresentation(Account targetedAccount) {
		return targetedAccount.getAccountRepresentation();
	}

	@Override
	protected String getEntryRepresentation(UploadRequestTemplate entry) {
		return entry.getUuid();
	}

	private boolean isEnable(Account authUser) {
		Functionality func = functionalityService.getUploadRequestFunctionality(authUser.getDomain());
		if (func.getActivationPolicy().getStatus()) {
			Functionality templateFunc = functionalityService.getUploadRequestEnableTemplateFunctionality(authUser.getDomain());
			return templateFunc.getActivationPolicy().getStatus();
		}
		return false;
	}

	@Override
	protected Account getOwner(UploadRequestTemplate entry, Object... opt) {
		return entry.getOwner();
	}
}
