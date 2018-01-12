/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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
	GUEST_WARN_OWNER_ABOUT_GUEST_EXPIRATION(25),

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

	// Old template identifiers - not used
	UPLOAD_PROPOSITION_CREATED(120),
	UPLOAD_PROPOSITION_REJECTED(130),
	UPLOAD_REQUEST_AUTO_FILTER(160);

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
