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
package org.linagora.linshare.mongo.entities.logs;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.entities.mto.AnonymousShareEntryMto;
import org.linagora.linshare.mongo.entities.mto.CopyMto;
import org.linagora.linshare.mongo.entities.mto.EntryMto;
import org.linagora.linshare.mongo.entities.mto.ShareEntryGroupMto;
import org.linagora.linshare.mongo.entities.mto.ShareEntryMto;

import com.fasterxml.jackson.annotation.JsonInclude;

@XmlRootElement
public class ShareEntryAuditLogEntry extends AuditLogEntryUser {

	protected EntryMto resource;

	protected String recipientMail;

	protected String recipientUuid;

	protected EntryMto resourceUpdated;

	protected ShareEntryGroupMto shareEntryGroup;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected CopyMto copiedTo;

	public ShareEntryAuditLogEntry() {
		super();
	}

	public ShareEntryAuditLogEntry(Account authUser, Account actor, LogAction action, ShareEntry entry,
			AuditLogEntryType type) {
		super(new AccountMto(authUser), new AccountMto(actor), action, type, entry.getUuid());
		this.recipientMail = entry.getRecipient().getMail();
		this.recipientUuid = entry.getRecipient().getLsUuid();
		this.resource = new ShareEntryMto(entry);
		ShareEntryGroup entryGroup = entry.getShareEntryGroup();
		if (null != entryGroup) {
			this.shareEntryGroup = new ShareEntryGroupMto(entryGroup);
		}
		this.addRelatedResources(entry.getDocumentEntry().getUuid());
		if (!actor.getLsUuid().equals(entry.getEntryOwner().getLsUuid())) {
			this.addRelatedAccounts(entry.getEntryOwner().getLsUuid());
		}
		this.addRelatedDomains(
				entry.getEntryOwner().getDomainId(),
				entry.getRecipient().getDomainId()
		);
	}

	public ShareEntryAuditLogEntry(Account authUser, Account actor, LogAction action, AnonymousShareEntry entry,
			AuditLogEntryType type) {
		super(new AccountMto(authUser), new AccountMto(actor), action, type, entry.getUuid());
		this.recipientMail = entry.getAnonymousUrl().getContact().getMail();
		this.resource = new AnonymousShareEntryMto(entry);
		ShareEntryGroup entryGroup = entry.getShareEntryGroup();
		if (null != entryGroup) {
			this.shareEntryGroup = new ShareEntryGroupMto(entryGroup);
		}
		this.addRelatedResources(entry.getDocumentEntry().getUuid());
		this.addRelatedAccounts(entry.getEntryOwner().getLsUuid());
		this.addRelatedDomains(entry.getEntryOwner().getDomainId());
	}

	public ShareEntryAuditLogEntry(Account authUser, Contact actor, AnonymousShareEntry entry,
			AuditLogEntryType type) {
		super(new AccountMto(authUser), new AccountMto(actor), LogAction.DOWNLOAD, type, entry.getUuid());
		this.recipientMail = entry.getAnonymousUrl().getContact().getMail();
		this.resource = new AnonymousShareEntryMto(entry);
		ShareEntryGroup entryGroup = entry.getShareEntryGroup();
		if (null != entryGroup) {
			this.shareEntryGroup = new ShareEntryGroupMto(entryGroup);
		}
		this.addRelatedResources(entry.getDocumentEntry().getUuid());
		this.addRelatedAccounts(entry.getEntryOwner().getLsUuid());
		this.addRelatedDomains(entry.getEntryOwner().getDomainId());
	}

	public EntryMto getResource() {
		return resource;
	}

	public void setResource(EntryMto resource) {
		this.resource = resource;
	}

	public String getRecipientMail() {
		return recipientMail;
	}

	public void setRecipientMail(String recipientMail) {
		this.recipientMail = recipientMail;
	}

	public String getRecipientUuid() {
		return recipientUuid;
	}

	public void setRecipientUuid(String recipientUuid) {
		this.recipientUuid = recipientUuid;
	}

	public EntryMto getResourceUpdated() {
		return resourceUpdated;
	}

	public void setResourceUpdated(EntryMto resourceUpdated) {
		this.resourceUpdated = resourceUpdated;
	}

	public ShareEntryGroupMto getShareEntryGroup() {
		return shareEntryGroup;
	}

	public void setShareEntryGroup(ShareEntryGroupMto shareEntryGroup) {
		this.shareEntryGroup = shareEntryGroup;
	}

	public CopyMto getCopiedTo() {
		return copiedTo;
	}

	public void setCopiedTo(CopyMto copiedTo) {
		this.copiedTo = copiedTo;
	}
}
