/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
