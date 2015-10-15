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

public enum MailActivationType {
	ANONYMOUS_DOWNLOAD,
	REGISTERED_DOWNLOAD,
	NEW_GUEST,
	RESET_PASSWORD,
	SHARED_DOC_UPDATED,
	SHARED_DOC_DELETED,
	SHARED_DOC_UPCOMING_OUTDATED,
	DOC_UPCOMING_OUTDATED,
	NEW_SHARING,
	UPLOAD_PROPOSITION_CREATED,
	UPLOAD_PROPOSITION_REJECTED,
	UPLOAD_REQUEST_UPDATED,
	UPLOAD_REQUEST_ACTIVATED,
	UPLOAD_REQUEST_AUTO_FILTER,
	UPLOAD_REQUEST_CREATED,
	UPLOAD_REQUEST_ACKNOWLEDGEMENT,
	UPLOAD_REQUEST_REMINDER,
	UPLOAD_REQUEST_WARN_OWNER_BEFORE_EXPIRY,
	UPLOAD_REQUEST_WARN_RECIPIENT_BEFORE_EXPIRY,
	UPLOAD_REQUEST_WARN_OWNER_EXPIRY,
	UPLOAD_REQUEST_WARN_RECIPIENT_EXPIRY,
	UPLOAD_REQUEST_CLOSED_BY_RECIPIENT,
	UPLOAD_REQUEST_CLOSED_BY_OWNER,
	UPLOAD_REQUEST_DELETED_BY_OWNER,
	UPLOAD_REQUEST_NO_SPACE_LEFT,
	UPLOAD_REQUEST_FILE_DELETED_BY_SENDER,
	SHARE_CREATION_ACKNOWLEDGEMENT_FOR_OWNER,
	UNDOWNLOADED_SHARED_DOCUMENTS_ALERT;
}
