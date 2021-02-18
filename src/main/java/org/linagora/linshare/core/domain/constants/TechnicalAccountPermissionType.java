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
package org.linagora.linshare.core.domain.constants;

import org.apache.commons.lang3.StringUtils;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;

public enum TechnicalAccountPermissionType {
	GUESTS_LIST,
	GUESTS_GET,
	GUESTS_CREATE,
	GUESTS_UPDATE,
	GUESTS_DELETE,

	DOCUMENT_ENTRIES_LIST,
	DOCUMENT_ENTRIES_GET,
	DOCUMENT_ENTRIES_CREATE,
	DOCUMENT_ENTRIES_UPDATE,
	DOCUMENT_ENTRIES_DELETE,
	DOCUMENT_ENTRIES_DOWNLOAD,
	DOCUMENT_ENTRIES_DOWNLOAD_THUMBNAIL,

	SHARE_ENTRIES_LIST,
	SHARE_ENTRIES_GET,
	SHARE_ENTRIES_CREATE,
	SHARE_ENTRIES_UPDATE,
	SHARE_ENTRIES_DELETE,
	SHARE_ENTRIES_DOWNLOAD,
	SHARE_ENTRIES_DOWNLOAD_THUMBNAIL,

	ANONYMOUS_SHARE_ENTRIES_LIST,
	ANONYMOUS_SHARE_ENTRIES_GET,
	ANONYMOUS_SHARE_ENTRIES_CREATE,
	ANONYMOUS_SHARE_ENTRIES_UPDATE,
	ANONYMOUS_SHARE_ENTRIES_DELETE,
	ANONYMOUS_SHARE_ENTRIES_DOWNLOAD,
	ANONYMOUS_SHARE_ENTRIES_DOWNLOAD_THUMBNAIL,

	THREADS_LIST,
	THREADS_GET,
	THREADS_CREATE,
	THREADS_UPDATE,
	THREADS_DELETE,

	THREAD_MEMBERS_LIST,
	THREAD_MEMBERS_GET,
	THREAD_MEMBERS_CREATE,
	THREAD_MEMBERS_UPDATE,
	THREAD_MEMBERS_DELETE,

	THREAD_ENTRIES_LIST,
	THREAD_ENTRIES_GET,
	THREAD_ENTRIES_CREATE,
	THREAD_ENTRIES_UPDATE,
	THREAD_ENTRIES_DELETE,
	THREAD_ENTRIES_DOWNLOAD,
	THREAD_ENTRIES_DOWNLOAD_THUMBNAIL,

	LISTS_LIST,
	LISTS_GET,
	LISTS_CREATE,
	LISTS_UPDATE,
	LISTS_DELETE,

	ASYNC_TASK_LIST,
	ASYNC_TASK_GET,
	ASYNC_TASK_CREATE,
	ASYNC_TASK_UPDATE,
	ASYNC_TASK_DELETE,

	SHARE_ENTRY_GROUPS_GET,
	SHARE_ENTRY_GROUPS_LIST,
	SHARE_ENTRY_GROUPS_UPDATE,
	SHARE_ENTRY_GROUPS_DELETE,

	UPLOAD_REQUEST_LIST,
	UPLOAD_REQUEST_GET,
	UPLOAD_REQUEST_CREATE,
	UPLOAD_REQUEST_UPDATE,
	UPLOAD_REQUEST_DELETE,

	UPLOAD_REQUEST_URL_LIST,
	UPLOAD_REQUEST_URL_GET,
	UPLOAD_REQUEST_URL_CREATE,
	UPLOAD_REQUEST_URL_UPDATE,
	UPLOAD_REQUEST_URL_DELETE,

	UPLOAD_REQUEST_TEMPLATE_DELETE,
	UPLOAD_REQUEST_TEMPLATE_GET,
	UPLOAD_REQUEST_TEMPLATE_CREATE,
	UPLOAD_REQUEST_TEMPLATE_UPDATE,
	UPLOAD_REQUEST_TEMPLATE_LIST,

	UPLOAD_REQUEST_ENTRIES_LIST,
	UPLOAD_REQUEST_ENTRIES_GET,
	UPLOAD_REQUEST_ENTRIES_CREATE,
	UPLOAD_REQUEST_ENTRIES_UPDATE,
	UPLOAD_REQUEST_ENTRIES_DELETE,
	UPLOAD_REQUEST_ENTRIES_DOWNLOAD,
	UPLOAD_REQUEST_ENTRIES_DOWNLOAD_THUMBNAIL,

	UPLOAD_PROPOSITION_DELETE,
	UPLOAD_PROPOSITION_GET,
	UPLOAD_PROPOSITION_CREATE,
	UPLOAD_PROPOSITION_UPDATE,
	UPLOAD_PROPOSITION_LIST,

	UPLOAD_PROPOSITION_EXCEPTION_RULE_DELETE,
	UPLOAD_PROPOSITION_EXCEPTION_RULE_GET,
	UPLOAD_PROPOSITION_EXCEPTION_RULE_CREATE,
	UPLOAD_PROPOSITION_EXCEPTION_RULE_UPDATE,
	UPLOAD_PROPOSITION_EXCEPTION_RULE_LIST,

	SHARED_SPACE_NODE_CREATE,
	SHARED_SPACE_NODE_GET,
	SHARED_SPACE_NODE_UPDATE,
	SHARED_SPACE_NODE_DELETE,
	SHARED_SPACE_NODE_LIST,

	DRIVE_CREATE,
	DRIVE_GET,
	DRIVE_UPDATE,
	DRIVE_DELETE,
	DRIVE_LIST,

	SHARED_SPACE_PERMISSION_CREATE,
	SHARED_SPACE_PERMISSION_GET,
	SHARED_SPACE_PERMISSION_UPDATE,
	SHARED_SPACE_PERMISSION_DELETE,
	SHARED_SPACE_PERMISSION_LIST,

	SHARED_SPACE_ROLE_CREATE,
	SHARED_SPACE_ROLE_GET,
	SHARED_SPACE_ROLE_UPDATE,
	SHARED_SPACE_ROLE_DELETE,
	SHARED_SPACE_ROLE_LIST,

	AUDIT_LIST,

	// TODO FIXME Quota & Statistics
	QUOTA_GET,
	QUOTA_LIST,
	QUOTA_CREATE,
	QUOTA_UPDATE,

	STATISTIC_LIST,
	STATISTIC_GET,

	SAFE_DETAIL_CREATE,
	SAFE_DETAIL_DELETE,
	SAFE_DETAIL_GET,
	SAFE_DETAIL_LIST;

	public static TechnicalAccountPermissionType fromString(String s) {
		try {
			return TechnicalAccountPermissionType.valueOf(s.toUpperCase());
		} catch (RuntimeException e) {
			throw new TechnicalException(TechnicalErrorCode.NO_SUCH_TECHNICAL_PERMISSION_TYPE,
					StringUtils.isEmpty(s) ? "null or empty" : s);
		}
	}
}
