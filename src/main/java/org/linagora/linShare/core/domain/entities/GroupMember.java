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
package org.linagora.linShare.core.domain.entities;

import java.io.Serializable;
import java.util.Calendar;

public class GroupMember implements Serializable {
	private static final long serialVersionUID = 8977072714137558071L;

	private User user;
	private GroupMemberType type;
	private Calendar membershipDate;

	public boolean equals(Object obj) {
		return ((GroupMember) obj).getUser().equals(user);
	}

	public int hashCode() {
		return user.hashCode();
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void setType(GroupMemberType type) {
		this.type = type;
	}

	public GroupMemberType getType() {
		return type;
	}

	public void setMembershipDate(Calendar membershipDate) {
		this.membershipDate = membershipDate;
	}

	public Calendar getMembershipDate() {
		return membershipDate;
	}

}
