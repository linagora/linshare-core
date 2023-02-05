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

public enum LogActionV1 {

	FILE_UPLOAD,
	FILE_SHARE,
	FILE_SHARE_WITH_ALERT_FOR_USD,
	FILE_EXPIRE,
	FILE_DELETE,
	FILE_UPDATE,
	FILE_INCONSISTENCY,

	SHARE_RECEIVED,
	SHARE_EXPIRE,
	SHARE_DOWNLOAD,
	SHARE_DOWNLOADED,
	SHARE_WITH_USD_NOT_DOWNLOADED,
	SHARE_WITH_USD_DOWNLOADED,
	SHARE_COPY,
	SHARE_DELETE,
	ANONYMOUS_SHARE_DOWNLOAD,

	THREAD_CREATE,
	THREAD_DELETE,
	THREAD_RENAME,
	THREAD_ADD_MEMBER,
	THREAD_REMOVE_MEMBER,
	THREAD_UPLOAD_ENTRY,
	THREAD_DOWNLOAD_ENTRY,
	THREAD_REMOVE_ENTRY,
	THREAD_REMOVE_INCONSISTENCY_ENTRY,

	USER_CREATE,
	USER_DELETE,
	USER_PURGE,
	USER_EXPIRE,
	USER_AUTH,
	USER_AUTH_FAILED,

	FILE_SIGN,
	USER_UPDATE,
	FILE_ENCRYPT,
	FILE_DECRYPT,
	ANTIVIRUS_SCAN_FAILED,
	FILE_WITH_VIRUS,

	LIST_CREATE,
	LIST_DELETE,
	LIST_UPDATE,
	LIST_ADD_CONTACT,
	LIST_UPDATE_CONTACT,
	LIST_DELETE_CONTACT;

	public static LogActionV1 fromString(String s) {
		try {
			return LogActionV1.valueOf(s.toUpperCase());
		} catch (RuntimeException e) {
			throw new TechnicalException(TechnicalErrorCode.NO_SUCH_LOG_ACTION, StringUtils.isEmpty(s) ? "null or empty" : s);
		}
	}
}
