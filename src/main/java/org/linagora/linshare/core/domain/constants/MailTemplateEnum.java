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
package org.linagora.linshare.core.domain.constants;

/**
 * Enumerate the available mail templates
 * 
 * @author sduprey
 *
 */
public enum MailTemplateEnum {
	GREETINGS(0),
	FOOTER(1),
	CONFIRM_DOWNLOAD_ANONYMOUS(2),
	CONFIRM_DOWNLOAD_REGISTERED(3),
	LINSHARE_URL(4),
	FILE_DOWNLOAD_URL(5),
	DECRYPT_URL(6),
	PERSONAL_MESSAGE(7),
	GUEST_INVITATION(8),
	ACCOUNT_DESCRIPTION(9),
	SHARE_NOTIFICATION(10),
	PASSWORD_GIVING(11),
	FILE_UPDATED(12),
	GROUP_SHARE_NOTIFICATION(13),
	GROUP_NEW_MEMBER(14),
	GROUP_MEMBERSHIP_STATUS(15),
	GROUP_SHARE_DELETED(16),
	SHARED_FILE_DELETED(17),
	SECURED_URL_UPCOMING_OUTDATED(18),
	SHARED_DOC_UPCOMING_OUTDATED(19), 
	DOC_UPCOMING_OUTDATED(20);

	private int value;

	private MailTemplateEnum(final int value) {
		this.value = value;
	}

	public int toInt() {
		return value;
	}

	public static MailTemplateEnum fromInt(final int value) {
		MailTemplateEnum ret = null;
		switch (value) {
		case 0:
			ret = GREETINGS;
			break;
		case 1:
			ret = FOOTER;
			break;
		case 2:
			ret = CONFIRM_DOWNLOAD_ANONYMOUS;
			break;
		case 3:
			ret = CONFIRM_DOWNLOAD_REGISTERED;
			break;
		case 4:
			ret = LINSHARE_URL;
			break;
		case 5:
			ret = FILE_DOWNLOAD_URL;
			break;
		case 6:
			ret = DECRYPT_URL;
			break;
		case 7:
			ret = PERSONAL_MESSAGE;
			break;
		case 8:
			ret = GUEST_INVITATION;
			break;
		case 9:
			ret = ACCOUNT_DESCRIPTION;
			break;
		case 10:
			ret = SHARE_NOTIFICATION;
			break;
		case 11:
			ret = PASSWORD_GIVING;
			break;
		case 12:
			ret = FILE_UPDATED;
			break;
		case 13:
			ret = GROUP_SHARE_NOTIFICATION;
			break;
		case 14:
			ret = GROUP_NEW_MEMBER;
			break;
		case 15:
			ret = GROUP_MEMBERSHIP_STATUS;
			break;
		case 16:
			ret = GROUP_SHARE_DELETED;
			break;
		case 17:
			ret = SHARED_FILE_DELETED;
			break;
		case 18:
			ret = SECURED_URL_UPCOMING_OUTDATED;
			break;
		case 19:
			ret = SHARED_DOC_UPCOMING_OUTDATED;
			break;
		case 20:
			ret = DOC_UPCOMING_OUTDATED;
			break;
		default:
			throw new IllegalArgumentException("Doesn't match an existing MailTemplates");
		}
		return ret;
	}
}
