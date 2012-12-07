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

public enum GroupMembershipStatus {

	WAITING_APPROVAL(0), ACCEPTED(1), REJECTED(2);

	private int value;

	private GroupMembershipStatus(int value) {
		this.value = value;
	}

	public int toInt() {
		return value;
	}

	public static GroupMembershipStatus fromInt(int value) {
        switch (value) {
            case 0: return GroupMembershipStatus.WAITING_APPROVAL;
            case 1: return GroupMembershipStatus.ACCEPTED;
            case 2: return GroupMembershipStatus.REJECTED;
            default : throw new IllegalArgumentException("Doesn't match an existing GroupMembershipStatus");
        }
	}

}
