/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
package org.linagora.linshare.view.tapestry.enums;

/**
 * Defines different kind of business error messages and the associated label.
 * 
 */
public enum BusinessUserMessageType {
	LOCAL_COPY_OK,
	UPLOAD_OK,
	UPLOAD_UPDATE_FILE_CONTENT_OK,
	UPLOAD_WITH_WARNING,
	UPLOAD_WITH_VIRUS,
	UPLOAD_NOT_ALLOWED,
	UPLOAD_WITH_FILE_TOO_LARGE,
	UPLOAD_NOT_ENOUGH_SPACE,
	QUICKSHARE_BADMAIL,
	QUICKSHARE_NOMAIL,
	QUICKSHARE_NO_FILE_TO_SHARE,
	QUICKSHARE_FAILED,
	QUICKSHARE_SUCCESS,
	SHARE_WARNING_MAIL_ADDRESS,
	PASSWORD_CHANGE_SUCCESS,
	PASSWORD_RESET_SUCCESS,
	NOFILE_SELECTED,
	DECRYPTION_OK,
	DECRYPTION_FAILED,
	UNREACHABLE_MAIL_ADDRESS,
	WARNING_TAGS_FOUND,
	THREAD_CREATION_FAIL,
	THREAD_CREATION_SUCCESS,
	THREAD_UPLOAD_NO_FILE,
	THREAD_UPLOAD_FAILED,
	MIME_TYPE_WARNING;
}
