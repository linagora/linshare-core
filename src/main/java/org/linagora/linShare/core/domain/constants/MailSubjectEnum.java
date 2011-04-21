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
package org.linagora.linShare.core.domain.constants;

/**
 * Enumerate the available mail subjects.
 * 
 * @author sduprey
 *
 */
public enum MailSubjectEnum {
	ANONYMOUS_DOWNLOAD(0), 
	REGISTERED_DOWNLOAD(1), 
	NEW_GUEST(2), 
	RESET_PASSWORD(3), 
	NEW_SHARING(4), 
	SHARED_DOC_UPDATED(5),
	NEW_GROUP_SHARING(6),
	MEMBERSHIP_REQUEST_STATUS(7),
	NEW_GROUP_MEMBER(8),
	GROUP_SHARING_DELETED(9),
	SHARED_DOC_DELETED(10),
	SHARED_DOC_UPCOMING_OUTDATED(11),
	DOC_UPCOMING_OUTDATED(12);

	private int value;

	private MailSubjectEnum(final int value) {
		this.value = value;
	}

	public int toInt() {
		return value;
	}

	public static MailSubjectEnum fromInt(final int value) {
		MailSubjectEnum ret = null;
		switch (value) {
		case 0:
			ret = ANONYMOUS_DOWNLOAD;
			break;
		case 1:
			ret = REGISTERED_DOWNLOAD;
			break;
		case 2:
			ret = NEW_GUEST;
			break;
		case 3:
			ret = RESET_PASSWORD;
			break;
		case 4:
			ret = NEW_SHARING;
			break;
		case 5:
			ret = SHARED_DOC_UPDATED;
			break;
		case 6:
			ret = NEW_GROUP_SHARING;
			break;
		case 7:
			ret = MEMBERSHIP_REQUEST_STATUS;
			break;
		case 8:
			ret = NEW_GROUP_MEMBER;
			break;
		case 9:
			ret = GROUP_SHARING_DELETED;
			break;
		case 10:
			ret = SHARED_DOC_DELETED;
			break;
		case 11:
			ret = SHARED_DOC_UPCOMING_OUTDATED;
			break;
		case 12:
			ret = DOC_UPCOMING_OUTDATED;
			break;
		default:
			throw new IllegalArgumentException("Doesn't match an existing MailSubjects");
		}
		return ret;
	}
}
