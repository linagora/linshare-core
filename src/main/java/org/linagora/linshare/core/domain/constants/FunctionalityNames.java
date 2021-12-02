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

public enum FunctionalityNames {

	MIME_TYPE,
	ENCIPHERMENT,
	TIME_STAMPING,
	ANTIVIRUS,
	DOCUMENT_EXPIRATION,
	SHARE_EXPIRATION,
	SHARE_EXPIRATION__DELETE_FILE_ON_EXPIRATION,
	ANONYMOUS_URL,
	ANONYMOUS_URL__NOTIFICATION,
	ANONYMOUS_URL__NOTIFICATION_URL,
	ANONYMOUS_URL__FORCE_ANONYMOUS_SHARING,
	ANONYMOUS_URL__HIDE_RECEIVED_SHARE_MENU,
	GUESTS,
	GUESTS__EXPIRATION,
	GUESTS__RESTRICTED,
	GUESTS__CAN_UPLOAD,
	GUESTS__EXPIRATION_ALLOW_PROLONGATION,
	INTERNAL_ENABLE_PERSONAL_SPACE,
	COMPLETION,
	CONTACTS_LIST,
	CONTACTS_LIST__CREATION_RIGHT,
	WORK_GROUP__CREATION_RIGHT,
	WORK_GROUP__FILE_VERSIONING,
	WORK_GROUP__FILE_EDITION,
	WORK_GROUP__DOWNLOAD_ARCHIVE,
	SHARE_NOTIFICATION_BEFORE_EXPIRATION,
	DOMAIN,
	DOMAIN__MAIL,
	DOMAIN__NOTIFICATION_URL,
	UPLOAD_REQUEST,
	UPLOAD_REQUEST__DELAY_BEFORE_ACTIVATION,
	UPLOAD_REQUEST__DELAY_BEFORE_EXPIRATION,
	UPLOAD_REQUEST__MAXIMUM_FILE_COUNT,
	UPLOAD_REQUEST__MAXIMUM_FILE_SIZE,
	UPLOAD_REQUEST__MAXIMUM_DEPOSIT_SIZE,
	UPLOAD_REQUEST__NOTIFICATION_LANGUAGE,
	UPLOAD_REQUEST__PROTECTED_BY_PASSWORD,
	UPLOAD_REQUEST__CAN_CLOSE,
	UPLOAD_REQUEST__CAN_DELETE,
	UPLOAD_REQUEST__DELAY_BEFORE_NOTIFICATION,
	UPLOAD_REQUEST__REMINDER_NOTIFICATION,
	UPLOAD_REQUEST_ENABLE_TEMPLATE,
	UPLOAD_PROPOSITION,
	SHARE_CREATION_ACKNOWLEDGEMENT_FOR_OWNER,
	UNDOWNLOADED_SHARED_DOCUMENTS_ALERT,
	UNDOWNLOADED_SHARED_DOCUMENTS_ALERT__DURATION,
	JWT_PERMANENT_TOKEN,
	SHARED_SPACE,
	DRIVE__CREATION_RIGHT,
	SECOND_FACTOR_AUTHENTICATION,
	COLLECTED_EMAILS_EXPIRATION;

	@Override
	public String toString() {
		return this.name();
	}

}
