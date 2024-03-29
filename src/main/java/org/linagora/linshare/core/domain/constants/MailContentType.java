/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.domain.constants;

public enum MailContentType {

	FILE_WARN_OWNER_BEFORE_FILE_EXPIRY(1),

	SHARE_NEW_SHARE_FOR_RECIPIENT(2),
	SHARE_NEW_SHARE_ACKNOWLEDGEMENT_FOR_SENDER(3),
	SHARE_FILE_DOWNLOAD(4),
	SHARE_FILE_SHARE_DELETED(5),
	SHARE_WARN_RECIPIENT_BEFORE_EXPIRY(6),
	SHARE_WARN_UNDOWNLOADED_FILESHARES(7),
	SHARE_WARN_SENDER_ABOUT_SHARE_EXPIRATION_WITHOUT_DOWNLOAD(26),
	SHARE_WARN_RECIPIENT_ABOUT_EXPIRED_SHARE(27),

	GUEST_ACCOUNT_NEW_CREATION(8),
	GUEST_ACCOUNT_RESET_PASSWORD_LINK(9),
	GUEST_WARN_GUEST_ABOUT_HIS_PASSWORD_RESET(31),
	GUEST_WARN_MODERATOR_ABOUT_GUEST_EXPIRATION(25),

	GUEST_MODERATOR_CREATION(41),
	GUEST_MODERATOR_UPDATE(42),
	GUEST_MODERATOR_DELETION(43),

	UPLOAD_REQUEST_UPLOADED_FILE(10),
	UPLOAD_REQUEST_UNAVAILABLE_SPACE(11),
	UPLOAD_REQUEST_WARN_BEFORE_EXPIRY(12),
	UPLOAD_REQUEST_WARN_EXPIRY(13),
	UPLOAD_REQUEST_CLOSED_BY_RECIPIENT(14),
	UPLOAD_REQUEST_FILE_DELETED_BY_RECIPIENT(15),

	UPLOAD_REQUEST_ACTIVATED_FOR_RECIPIENT(16),
	UPLOAD_REQUEST_ACTIVATED_FOR_OWNER(17),
	UPLOAD_REQUEST_REMINDER(18),
	UPLOAD_REQUEST_PASSWORD_RENEWAL(19),
	UPLOAD_REQUEST_CREATED(20),
	UPLOAD_REQUEST_CLOSED_BY_OWNER(21),
	UPLOAD_REQUEST_RECIPIENT_REMOVED(22),
	UPLOAD_REQUEST_UPDATED_SETTINGS(23),
	UPLOAD_REQUEST_FILE_DELETED_BY_OWNER(24),

	WORKGROUP_WARN_NEW_MEMBER(28),
	WORKGROUP_WARN_UPDATED_MEMBER(29),
	WORKGROUP_WARN_DELETED_MEMBER(30),
	WORKGROUP_WARN_DELETED_WORKGROUP(39),
	WORKGROUP_WARN_NEW_WORKGROUP_DOCUMENT(44),
	WORKGROUP_WARN_WORKGROUP_DOCUMENT_UPDATED(45),

	ACCOUNT_OWNER_WARN_JWT_PERMANENT_TOKEN_CREATED(32),
	ACCOUNT_OWNER_WARN_JWT_PERMANENT_TOKEN_DELETED(33),

	WORK_SPACE_WARN_NEW_MEMBER(34),
	WORK_SPACE_WARN_UPDATED_MEMBER(35),
	WORK_SPACE_WARN_DELETED_MEMBER(36),
	WORK_SPACE_WARN_DELETED(40),

	GUEST_ACCOUNT_RESET_PASSWORD_FOR_4_0(37),
	SHARE_ANONYMOUS_RESET_PASSWORD(38);

	private int value;

	private MailContentType(int value) {
		this.value = value;
	}

	public int toInt() {
		return this.value;
	}

	public static MailContentType fromInt(int value) {
		for (MailContentType type : values()) {
			if (type.value == value) {
				return type;
			}
		}
		throw new IllegalArgumentException(
				"Value : " + value + " doesn't match an existing MailContentType");
	}

	public static boolean contains(String test) {
		String str= test.toUpperCase();
		for (MailContentType c : MailContentType.values()) {
			if (c.name().equals(str)) {
				return true;
			}
		}
		return false;
	}
}
