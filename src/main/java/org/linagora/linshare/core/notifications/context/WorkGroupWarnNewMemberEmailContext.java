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

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.MailActivationType;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;

public class WorkGroupWarnNewMemberEmailContext extends EmailContext {

	protected SharedSpaceMember workgroupMember;

	protected User newMember;

	protected Account owner;

	public WorkGroupWarnNewMemberEmailContext(SharedSpaceMember workgroupMember, Account owner, User newMember) {
		super(newMember.getDomain(), false);
		this.workgroupMember = workgroupMember;
		this.owner = owner;
		this.language = newMember.getMailLocale();
		this.newMember = newMember;
	}

	public SharedSpaceMember getWorkgroupMember() {
		return workgroupMember;
	}

	public void setWorkgroupMember(SharedSpaceMember workgroupMember) {
		this.workgroupMember = workgroupMember;
	}

	public Account getOwner() {
		return owner;
	}

	public void setOwner(Account owner) {
		this.owner = owner;
	}

	@Override
	public MailContentType getType() {
		return MailContentType.WORKGROUP_WARN_NEW_MEMBER;
	}

	@Override
	public MailActivationType getActivation() {
		return MailActivationType.WORKGROUP_WARN_NEW_MEMBER;
	}

	@Override
	public String getMailRcpt() {
		return newMember.getMail();
	}

	@Override
	public String getMailReplyTo() {
		return owner.getMail();
	}

	public User getNewMember() {
		return newMember;
	}

	public void setNewMember(User newMember) {
		this.newMember = newMember;
	}

	@Override
	public void validateRequiredField() {
		Validate.notNull(workgroupMember, "Missing threadMember");
		Validate.notNull(owner, "Missing actor");
	}

}
