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

public enum MailContentType {
	ANONYMOUS_DOWNLOAD(0),
	REGISTERED_DOWNLOAD(1),
	NEW_GUEST(2),
	RESET_PASSWORD(3),
	SHARED_DOC_UPDATED(4),
	SHARED_DOC_DELETED(5),
	SHARED_DOC_UPCOMING_OUTDATED(6),
	DOC_UPCOMING_OUTDATED(7),
	NEW_SHARING(8),
	NEW_SHARING_PROTECTED(9),
	NEW_SHARING_CYPHERED(10),
	NEW_SHARING_CYPHERED_PROTECTED(11),
	UPLOAD_PROPOSITION_CREATED(12),
	UPLOAD_PROPOSITION_REJECTED(13),
	UPLOAD_REQUEST_UPDATED(14),
	UPLOAD_REQUEST_ACTIVATED(15),
	UPLOAD_REQUEST_AUTO_FILTER(16),
	UPLOAD_REQUEST_CREATED(17),
	UPLOAD_REQUEST_ACKNOWLEDGMENT(18),
	UPLOAD_REQUEST_REMINDER(19),
	UPLOAD_REQUEST_WARN_OWNER_BEFORE_EXPIRY(20),
	UPLOAD_REQUEST_WARN_RECIPIENT_BEFORE_EXPIRY(21),
	UPLOAD_REQUEST_WARN_OWNER_EXPIRY(22),
	UPLOAD_REQUEST_WARN_RECIPIENT_EXPIRY(23),
	UPLOAD_REQUEST_CLOSED_BY_RECIPIENT(24),
	UPLOAD_REQUEST_CLOSED_BY_OWNER(25),
	UPLOAD_REQUEST_DELETED_BY_OWNER(26),
	UPLOAD_REQUEST_NO_SPACE_LEFT(27),
	UPLOAD_REQUEST_ENTRY_URL(28),
	UPLOAD_REQUEST_FILE_DELETED_BY_SENDER(29),
	SHARE_CREATION_ACKNOWLEDGMENT_FOR_OWNER(30),
	SHARE_CREATION_ACKNOWLEDGMENT_WITH_SPECIAL_MESSAGE_FOR_OWNER(31),
	UNDOWNLOADED_SHARED_DOCUMENT_ALERT(32);

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
}
