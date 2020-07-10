/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
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

package org.linagora.linshare.core.domain.constants;

public enum MailActivationType {

	FILE_WARN_OWNER_BEFORE_FILE_EXPIRY,

	SHARE_NEW_SHARE_FOR_RECIPIENT,
	SHARE_NEW_SHARE_ACKNOWLEDGEMENT_FOR_SENDER,
	SHARE_FILE_DOWNLOAD_ANONYMOUS,
	SHARE_ANONYMOUS_RESET_PASSWORD,
	SHARE_FILE_DOWNLOAD_USERS,
	SHARE_FILE_SHARE_DELETED,
	SHARE_WARN_RECIPIENT_BEFORE_EXPIRY,
	SHARE_WARN_UNDOWNLOADED_FILESHARES,
	SHARE_WARN_SENDER_ABOUT_SHARE_EXPIRATION_WITHOUT_DOWNLOAD,
	SHARE_WARN_RECIPIENT_ABOUT_EXPIRED_SHARE,

	GUEST_ACCOUNT_NEW_CREATION,
	GUEST_ACCOUNT_RESET_PASSWORD_LINK,
	GUEST_WARN_GUEST_ABOUT_HIS_PASSWORD_RESET,
	GUEST_WARN_OWNER_ABOUT_GUEST_EXPIRATION,
	GUEST_ACCOUNT_RESET_PASSWORD_FOR_4_0,

	UPLOAD_REQUEST_UPLOADED_FILE,
	UPLOAD_REQUEST_UNAVAILABLE_SPACE,
	UPLOAD_REQUEST_WARN_BEFORE_EXPIRY,
	UPLOAD_REQUEST_WARN_EXPIRY,
	UPLOAD_REQUEST_CLOSED_BY_RECIPIENT,
	UPLOAD_REQUEST_FILE_DELETED_BY_RECIPIENT,

	UPLOAD_REQUEST_ACTIVATED_FOR_RECIPIENT,
	UPLOAD_REQUEST_ACTIVATED_FOR_OWNER,
	UPLOAD_REQUEST_REMINDER,
	UPLOAD_REQUEST_PASSWORD_RENEWAL,
	UPLOAD_REQUEST_CREATED,
	UPLOAD_REQUEST_CLOSED_BY_OWNER,
	UPLOAD_REQUEST_RECIPIENT_REMOVED,
	UPLOAD_REQUEST_UPDATED_SETTINGS,
	UPLOAD_REQUEST_FILE_DELETED_BY_OWNER,

	WORKGROUP_WARN_NEW_MEMBER,
	WORKGROUP_WARN_UPDATED_MEMBER,
	WORKGROUP_WARN_DELETED_MEMBER,

	ACCOUNT_OWNER_WARN_JWT_PERMANENT_TOKEN_CREATED,
	ACCOUNT_OWNER_WARN_JWT_PERMANENT_TOKEN_DELETED,

	DRIVE_WARN_NEW_MEMBER,
	DRIVE_WARN_UPDATED_MEMBER,
	DRIVE_WARN_DELETED_MEMBER;
	
}
