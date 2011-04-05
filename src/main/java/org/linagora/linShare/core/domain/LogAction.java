/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.core.domain;

import org.apache.commons.lang.StringUtils;
import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;


public enum LogAction {

	FILE_UPLOAD,
	FILE_SHARE,
	FILE_EXPIRE,
	FILE_DELETE,
	FILE_UPDATE,
    FILE_INCONSISTENCY,
	
	SHARE_EXPIRE,
	SHARE_DOWNLOAD,
	SHARE_COPY,
	SHARE_DELETE,
	ANONYMOUS_SHARE_DOWNLOAD,
	
	USER_CREATE,
	USER_DELETE,
	USER_EXPIRE, 
	
	FILE_SIGN, 
	USER_UPDATE, 
	FILE_ENCRYPT,
	FILE_DECRYPT,
	
	ANTIVIRUS_SCAN_FAILED,
	FILE_WITH_VIRUS;
	
	
	
	public static LogAction fromString(String s) {
		try {
			return LogAction.valueOf(s.toUpperCase());
		} catch (RuntimeException e) {
			throw new TechnicalException(TechnicalErrorCode.NO_SUCH_LOG_ACTION, StringUtils.isEmpty(s) ? "null or empty" : s);
		}
	}
}
