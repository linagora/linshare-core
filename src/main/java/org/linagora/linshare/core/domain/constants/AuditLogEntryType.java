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
package org.linagora.linshare.core.domain.constants;

import org.apache.commons.lang3.StringUtils;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;

public enum AuditLogEntryType {

	SHARE_ENTRY,
	DOCUMENT_ENTRY,
	GUEST,
	WORK_SPACE,
	WORK_SPACE_MEMBER,
	WORK_GROUP,
	WORKGROUP_MEMBER,
	WORKGROUP_FOLDER,
	WORKGROUP_DOCUMENT,
	WORKGROUP_DOCUMENT_REVISION,
	DOMAIN,
	USER,
	DOMAIN_PATTERN,
	GROUP_FILTER,
	WORKSPACE_FILTER,
	FUNCTIONALITY,
	CONTACTS_LISTS,
	CONTACTS_LISTS_CONTACTS,
	UPLOAD_REQUEST_GROUP,
	UPLOAD_REQUEST,
	UPLOAD_REQUEST_URL,
	UPLOAD_REQUEST_ENTRY,
	UPLOAD_PROPOSITION,
	ANONYMOUS_SHARE_ENTRY,
	AUTHENTICATION,
	USER_PREFERENCE,
	RESET_PASSWORD,
	SAFE_DETAIL,
	PUBLIC_KEY,
	JWT_PERMANENT_TOKEN,
	SHARED_SPACE_NODE,
	MAIL_ATTACHMENT,
	SHARED_SPACE_MEMBER, // compat: SHARED_SPACE_MEMBER was split into DRIVE_MEMBER and WORKGROUP_MEMBER
	DRIVE_MEMBER, // compat: DRIVE_MEMBER became WORK_SPACE_MEMBER
	DRIVE, // compat: DRIVE became WORK_SPACE
	WORKGROUP, // compat: WORKGROUP became WORK_SPACE
	GUEST_MODERATOR;

	public static AuditLogEntryType fromString(String s) {
		try {
			return AuditLogEntryType.valueOf(s.toUpperCase());
		} catch (RuntimeException e) {
			throw new TechnicalException(TechnicalErrorCode.NO_SUCH_LOG_ACTION, StringUtils.isEmpty(s) ? "null or empty" : s);
		}
	}
}
