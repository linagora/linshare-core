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
	GUESTS,
	GUESTS__EXPIRATION,
	GUESTS__RESTRICTED,
	GUESTS__CAN_UPLOAD,
	GUESTS__EXPIRATION_ALLOW_PROLONGATION,
	INTERNAL_CAN_UPLOAD,
	COMPLETION,
	CONTACTS_LIST,
	CONTACTS_LIST__CREATION_RIGHT,
	WORK_GROUP,
	WORK_GROUP__CREATION_RIGHT,
	SHARE_NOTIFICATION_BEFORE_EXPIRATION,
	DOMAIN,
	DOMAIN__MAIL,
	DOMAIN__NOTIFICATION_URL,
	UPLOAD_REQUEST,
	UPLOAD_REQUEST__DELAY_BEFORE_ACTIVATION,
	UPLOAD_REQUEST__DELAY_BEFORE_EXPIRATION,
	UPLOAD_REQUEST__GROUPED_MODE,
	UPLOAD_REQUEST__MAXIMUM_FILE_COUNT,
	UPLOAD_REQUEST__MAXIMUM_FILE_SIZE,
	UPLOAD_REQUEST__MAXIMUM_DEPOSIT_SIZE,
	UPLOAD_REQUEST__NOTIFICATION_LANGUAGE,
	UPLOAD_REQUEST__SECURED_URL,
	UPLOAD_REQUEST__CAN_CLOSE,
	UPLOAD_REQUEST__PROLONGATION,
	UPLOAD_REQUEST__CAN_DELETE,
	UPLOAD_REQUEST__DELAY_BEFORE_NOTIFICATION,
	UPLOAD_REQUEST_ENABLE_TEMPLATE,
	UPLOAD_PROPOSITION,
	SHARE_CREATION_ACKNOWLEDGEMENT_FOR_OWNER,
	UNDOWNLOADED_SHARED_DOCUMENTS_ALERT,
	UNDOWNLOADED_SHARED_DOCUMENTS_ALERT__DURATION,
	CMIS,
	CMIS_DOCUMENTS,
	CMIS_THREADS;

	@Override
	public String toString() {
		return this.name();
	}

}
