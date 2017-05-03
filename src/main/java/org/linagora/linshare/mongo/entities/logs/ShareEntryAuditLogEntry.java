/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */

package org.linagora.linshare.mongo.entities.logs;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.entities.mto.AnonymousShareEntryMto;
import org.linagora.linshare.mongo.entities.mto.EntryMto;
import org.linagora.linshare.mongo.entities.mto.ShareEntryGroupMto;
import org.linagora.linshare.mongo.entities.mto.ShareEntryMto;

@XmlRootElement
public class ShareEntryAuditLogEntry extends AuditLogEntryUser {

	protected EntryMto resource;

	protected String recipientMail;

	protected String recipientUuid;

	protected EntryMto resourceUpdated;

	protected ShareEntryGroupMto shareEntryGroup;

	public ShareEntryAuditLogEntry() {
		super();
	}

	public ShareEntryAuditLogEntry(Account authUser, Account actor, LogAction action, ShareEntry entry,
			AuditLogEntryType type) {
		super(new AccountMto(authUser), new AccountMto(actor), action, type, entry.getUuid());
		this.recipientMail = entry.getRecipient().getMail();
		this.recipientUuid = entry.getRecipient().getLsUuid();
		this.resource = new ShareEntryMto(entry);
		this.shareEntryGroup = new ShareEntryGroupMto(entry.getShareEntryGroup());
		this.addRelatedResources(entry.getDocumentEntry().getUuid());
		if (!actor.getLsUuid().equals(entry.getEntryOwner().getLsUuid())) {
			this.addRelatedAccounts(entry.getEntryOwner().getLsUuid());
		}
	}

	public ShareEntryAuditLogEntry(Account authUser, Account actor, LogAction action, AnonymousShareEntry entry,
			AuditLogEntryType type) {
		super(new AccountMto(authUser), new AccountMto(actor), action, type, entry.getUuid());
		this.recipientMail = entry.getAnonymousUrl().getContact().getMail();
		this.resource = new AnonymousShareEntryMto(entry);
		this.shareEntryGroup = new ShareEntryGroupMto(entry.getShareEntryGroup());
		this.addRelatedResources(entry.getDocumentEntry().getUuid());
	}

	public ShareEntryAuditLogEntry(Account authUser, Contact actor, AnonymousShareEntry entry,
			AuditLogEntryType type) {
		super(new AccountMto(authUser), new AccountMto(actor), LogAction.DOWNLOAD, type, entry.getUuid());
		this.recipientMail = entry.getAnonymousUrl().getContact().getMail();
		this.resource = new AnonymousShareEntryMto(entry);
		this.shareEntryGroup = new ShareEntryGroupMto(entry.getShareEntryGroup());
		this.addRelatedResources(entry.getDocumentEntry().getUuid());
		this.addRelatedAccounts(entry.getEntryOwner().getLsUuid());
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
}
