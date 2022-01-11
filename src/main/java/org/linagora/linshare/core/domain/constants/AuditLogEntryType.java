/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2021 LINAGORA
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

public enum AuditLogEntryType {

	SHARE_ENTRY,
	DOCUMENT_ENTRY,
	GUEST,
	WORK_SPACE,
	DRIVE, // compat
	WORK_SPACE_MEMBER,
	DRIVE_MEMBER, // compat
	WORK_GROUP,
	WORKGROUP, // compat
	WORKGROUP_MEMBER,
	WORKGROUP_FOLDER,
	WORKGROUP_DOCUMENT,
	WORKGROUP_DOCUMENT_REVISION,
	DOMAIN,
	USER,
	DOMAIN_PATTERN,
	GROUP_FILTER,
	DRIVE_FILTER,
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
	SHARED_SPACE_MEMBER;

	public static AuditLogEntryType fromString(String s) {
		try {
			return AuditLogEntryType.valueOf(s.toUpperCase());
		} catch (RuntimeException e) {
			throw new TechnicalException(TechnicalErrorCode.NO_SUCH_LOG_ACTION, StringUtils.isEmpty(s) ? "null or empty" : s);
		}
	}
}
