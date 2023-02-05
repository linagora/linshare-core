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
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;

public class WorkGroupDeletedWarnEmailContext extends EmailContext {

	private SharedSpaceMember sharedSpaceMember;

	private Account actor;

	public WorkGroupDeletedWarnEmailContext(Account actor, SharedSpaceMember workgroupMember, AbstractDomain workgroupMemberDomain) {
		super(workgroupMemberDomain, false);
		this.sharedSpaceMember = workgroupMember;
		this.actor = actor;
	}

	@Override
	public MailContentType getType() {
		return MailContentType.WORKGROUP_WARN_DELETED_WORKGROUP;
	}

	@Override
	public MailActivationType getActivation() {
		return MailActivationType.WORKGROUP_WARN_DELETED_WORKGROUP;
	}

	@Override
	public String getMailRcpt() {
		return sharedSpaceMember.getAccount().getMail();
	}

	@Override
	public String getMailReplyTo() {
		if (actor.canReceiveMail()) {
			return actor.getMail();
		}
		return null;
	}

	@Override
	public void validateRequiredField() {
		Validate.notNull(sharedSpaceMember, "Missing SharedSpaceMember");
		Validate.notNull(actor, "Missing actor");
	}

	public SharedSpaceMember getSharedSpaceMember() {
		return sharedSpaceMember;
	}

	public void setSharedSpaceMember(SharedSpaceMember sharedSpaceMember) {
		this.sharedSpaceMember = sharedSpaceMember;
	}

	public Account getActor() {
		return actor;
	}

	public void setActor(Account actor) {
		this.actor = actor;
	}
}
