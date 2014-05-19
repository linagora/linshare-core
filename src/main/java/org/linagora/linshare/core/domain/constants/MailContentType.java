/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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
	ACCOUNT_DESCRIPTION(0),
	ANONYMOUS_DOWNLOAD(1),
	CONFIRM_DOWNLOAD_ANONYMOUS(2),
	CONFIRM_DOWNLOAD_REGISTERED(3),
	DECRYPT_URL(4),
	DOC_UPCOMING_OUTDATED(5),
	FILE_DOWNLOAD_URL(6),
	FILE_UPDATED(7),
	GROUP_MEMBERSHIP_STATUS(8),
	GROUP_NEW_MEMBER(9),
	GROUP_SHARE_DELETED(10),
	GROUP_SHARE_NOTIFICATION(11),
	GUEST_INVITATION(12),
	LINSHARE_URL(13),
	NEW_GUEST(14),
	NEW_SHARING(15),
	NEW_SHARING_WITH_ACTOR(16),
	PASSWORD_GIVING(17),
	PERSONAL_MESSAGE(18),
	REGISTERED_DOWNLOAD(19),
	RESET_PASSWORD(20),
	SECURED_URL_UPCOMING_OUTDATED(21),
	SHARED_DOC_DELETED(22),
	SHARED_DOC_UPCOMING_OUTDATED(23),
	SHARED_DOC_UPDATED(24),
	SHARED_FILE_DELETED(25),
	SHARE_NOTIFICATION(26);

	private int value;

	private MailContentType(int value) {
		this.value = value;
	}

	public int toInt() {
		return this.value;
	}

	public static MailContentType fromInt(int value) {
		for (MailContentType template : values()) {
			if (template.value == value) {
				return template;
			}
		}
		throw new IllegalArgumentException(
				"Doesn't match an existing MailContentType");
	}
}
