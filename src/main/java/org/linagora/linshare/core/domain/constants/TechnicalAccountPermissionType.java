/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.core.domain.constants;

import org.apache.commons.lang.StringUtils;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;

public enum TechnicalAccountPermissionType {
	GUESTS_LIST,
	GUESTS_GET,
	GUESTS_CREATE,
	GUESTS_UPDATE,
	GUESTS_DELETE,
	
	DOCUMENTS_LIST,
	DOCUMENTS_GET,
	DOCUMENTS_CREATE,
	DOCUMENTS_UPDATE,
	DOCUMENTS_DELETE,
	
	SHARES_LIST,
	SHARES_GET,
	SHARES_CREATE,
	SHARES_UPDATE,
	SHARES_DELETE,
	
	RECEIVED_SHARES_LIST,
	RECEIVED_SHARES_GET,
	RECEIVED_SHARES_CREATE,
	RECEIVED_SHARES_UPDATE,
	RECEIVED_SHARES_DELETE,
	
	THREADS_LIST,
	THREADS_GET,
	THREADS_CREATE,
	THREADS_UPDATE,
	THREADS_DELETE,
	
	THREADS_MEMBER_LIST,
	THREADS_MEMBER_GET,
	THREADS_MEMBER_CREATE,
	THREADS_MEMBER_UPDATE,
	THREADS_MEMBER_DELETE,
	
	LISTS_LIST,
	LISTS_GET,
	LISTS_CREATE,
	LISTS_UPDATE,
	LISTS_DELETE;

	public static TechnicalAccountPermissionType fromString(String s) {
		try {
			return TechnicalAccountPermissionType.valueOf(s.toUpperCase());
		} catch (RuntimeException e) {
			throw new TechnicalException(TechnicalErrorCode.NO_SUCH_TECHNICAL_PERMISSION_TYPE, StringUtils.isEmpty(s) ? "null or empty" : s);
		}
	}
}
