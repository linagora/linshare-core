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
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupNode;

public class WorkGroupWarnNewWorkgroupDocumentContext extends EmailContext {

	protected Account owner;

	protected SharedSpaceMember workgroupMember;

	protected WorkGroupNode folder;

	protected WorkGroupDocument document;

	public WorkGroupWarnNewWorkgroupDocumentContext(AbstractDomain domain, boolean needToRetrieveGuestDomain,
			Account owner, SharedSpaceMember workgroupMember, WorkGroupNode folder, WorkGroupDocument document) {
		super(domain, needToRetrieveGuestDomain);
		this.owner = owner;
		this.workgroupMember = workgroupMember;
		this.folder = folder;
		this.document = document;
		this.language = owner.getMailLocale();
	}

	public Account getOwner() {
		return owner;
	}

	public void setOwner(Account owner) {
		this.owner = owner;
	}

	public SharedSpaceMember getWorkgroupMember() {
		return workgroupMember;
	}

	public void setWorkgroupMember(SharedSpaceMember workgroupMember) {
		this.workgroupMember = workgroupMember;
	}

	public WorkGroupDocument getDocument() {
		return document;
	}

	public void setDocument(WorkGroupDocument document) {
		this.document = document;
	}

	public WorkGroupNode getFolder() {
		return folder;
	}

	public void setFolder(WorkGroupNode folder) {
		this.folder = folder;
	}

	@Override
	public MailContentType getType() {
		return MailContentType.WORKGROUP_WARN_NEW_WORKGROUP_DOCUMENT;
	}

	@Override
	public MailActivationType getActivation() {
		return MailActivationType.WORKGROUP_WARN_NEW_WORKGROUP_DOCUMENT;
	}

	@Override
	public String getMailRcpt() {
		return workgroupMember.getAccount().getMail();
	}

	@Override
	public String getMailReplyTo() {
		return owner.getMail();
	}

	@Override
	public void validateRequiredField() {
		Validate.notNull(workgroupMember, "Missing threadMember");
		Validate.notNull(owner, "Missing actor");
	}

}
