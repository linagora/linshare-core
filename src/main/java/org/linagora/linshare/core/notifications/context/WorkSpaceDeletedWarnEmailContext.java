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

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.MailActivationType;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;

public class WorkSpaceDeletedWarnEmailContext extends EmailContext {

	private Account actor;

	private SharedSpaceMember sharedSpaceMember;

	private List<SharedSpaceNodeNested> nestedNodes;

	public WorkSpaceDeletedWarnEmailContext(Account actor, AbstractDomain domain,
	                                        SharedSpaceMember sharedSpaceMember, List<SharedSpaceNodeNested> nestedNodes) {
		super(domain, false);
		this.actor = actor;
		this.sharedSpaceMember = sharedSpaceMember;
		this.nestedNodes = nestedNodes;
	}

	@Override
	public MailContentType getType() {
		return MailContentType.WORK_SPACE_WARN_DELETED;
	}

	@Override
	public MailActivationType getActivation() {
		return MailActivationType.WORK_SPACE_WARN_DELETED;
	}

	public List<SharedSpaceNodeNested> getNestedNodes() {
		return nestedNodes;
	}

	public void setNestedNodes(List<SharedSpaceNodeNested> nestedNodes) {
		this.nestedNodes = nestedNodes;
	}

	public Account getActor() {
		return actor;
	}

	public void setActor(Account actor) {
		this.actor = actor;
	}

	public SharedSpaceMember getSharedSpaceMember() {
		return sharedSpaceMember;
	}

	public void setSharedSpaceMember(SharedSpaceMember sharedSpaceMember) {
		this.sharedSpaceMember = sharedSpaceMember;
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
}
