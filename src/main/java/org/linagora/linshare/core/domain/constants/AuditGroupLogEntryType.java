/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2022 LINAGORA
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;

import com.google.common.collect.Lists;

public enum AuditGroupLogEntryType {

	ACCOUNTS,
	ADMINISTRATION,
	CONTACT_LISTS,
	GUESTS,
	MY_SPACE,
	UPLOAD_REQUESTS,
	SHARED_SPACES;

	public static Map<AuditGroupLogEntryType, List<AuditLogEntryType>> toAuditLogEntryTypes = new HashMap<AuditGroupLogEntryType, List<AuditLogEntryType>>() {
		private static final long serialVersionUID = 5489710996641765350L;
	{
		put(ACCOUNTS, Lists.newArrayList(
				AuditLogEntryType.USER,
				AuditLogEntryType.JWT_PERMANENT_TOKEN,
				AuditLogEntryType.USER_PREFERENCE,
				AuditLogEntryType.RESET_PASSWORD,
				AuditLogEntryType.JWT_PERMANENT_TOKEN,
				AuditLogEntryType.AUTHENTICATION));
		put(ADMINISTRATION, Lists.newArrayList(
				AuditLogEntryType.DOMAIN,
				AuditLogEntryType.DOMAIN_PATTERN,
				AuditLogEntryType.GROUP_FILTER,
				AuditLogEntryType.WORKSPACE_FILTER,
				AuditLogEntryType.FUNCTIONALITY,
				AuditLogEntryType.PUBLIC_KEY,
				AuditLogEntryType.MAIL_ATTACHMENT,
				AuditLogEntryType.MAIL_ATTACHMENT));
		put(CONTACT_LISTS, Lists.newArrayList(
				AuditLogEntryType.CONTACTS_LISTS,
				AuditLogEntryType.CONTACTS_LISTS_CONTACTS));
		put(GUESTS, Lists.newArrayList(
				AuditLogEntryType.GUEST,
				AuditLogEntryType.GUEST_MODERATOR));
		put(MY_SPACE, Lists.newArrayList(
				AuditLogEntryType.SHARE_ENTRY,
				AuditLogEntryType.DOCUMENT_ENTRY,
				AuditLogEntryType.ANONYMOUS_SHARE_ENTRY));
		put(UPLOAD_REQUESTS, Lists.newArrayList(
				AuditLogEntryType.UPLOAD_REQUEST,
				AuditLogEntryType.UPLOAD_REQUEST_URL,
				AuditLogEntryType.UPLOAD_REQUEST_ENTRY,
				AuditLogEntryType.UPLOAD_REQUEST_GROUP));
		put(SHARED_SPACES, Lists.newArrayList(
				AuditLogEntryType.SHARED_SPACE_NODE,
				AuditLogEntryType.WORK_SPACE,
				AuditLogEntryType.WORK_SPACE_MEMBER,
				AuditLogEntryType.WORK_GROUP,
				AuditLogEntryType.WORKGROUP_MEMBER,
				AuditLogEntryType.WORKGROUP_FOLDER,
				AuditLogEntryType.WORKGROUP_DOCUMENT,
				AuditLogEntryType.WORKGROUP_DOCUMENT_REVISION));
	}};

	public static AuditGroupLogEntryType fromString(String s) {
		try {
			return AuditGroupLogEntryType.valueOf(s.toUpperCase());
		} catch (RuntimeException e) {
			throw new TechnicalException(TechnicalErrorCode.NO_SUCH_LOG_ACTION, StringUtils.isEmpty(s) ? "null or empty" : s);
		}
	}
}
