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
package org.linagora.linshare.core.notifications.context;

import java.util.List;

import org.linagora.linshare.core.domain.constants.MailActivationType;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberDrive;

public class WorkSpaceWarnUpdatedMemberEmailContext extends WorkGroupWarnUpdatedMemberEmailContext {

	protected List<SharedSpaceMember> nestedMembers;

	public WorkSpaceWarnUpdatedMemberEmailContext(SharedSpaceMemberDrive member, User userMember, Account actor,
	                                              List<SharedSpaceMember> nestedMembers) {
		super(member, userMember, actor);
		this.nestedMembers = nestedMembers;
	}

	@Override
	public MailContentType getType() {
		return MailContentType.WORK_SPACE_WARN_UPDATED_MEMBER;
	}

	@Override
	public MailActivationType getActivation() {
		return MailActivationType.WORK_SPACE_WARN_UPDATED_MEMBER;
	}

	public List<SharedSpaceMember> getNestedMembers() {
		return nestedMembers;
	}

	public void setNestedMembers(List<SharedSpaceMember> nestedMembers) {
		this.nestedMembers = nestedMembers;
	}
}
