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
	THREAD_UPLOAD_FAILED;
}
