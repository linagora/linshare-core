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

package org.linagora.linshare.mongo.entities;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.springframework.data.mongodb.core.mapping.Document;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = ShareAuditLogEntry.class, name = "share_audit"),
		@Type(value = ThreadAuditLogEntry.class, name = "thread_audit"),
		@Type(value = ThreadMemberAuditLogEntry.class, name = "thread_member_audit"),
		@Type(value = UserAuditLogEntry.class, name = "user_audit"),
		@Type(value = GuestAuditLogEntry.class, name = "guest_audit"),
		@Type(value = MailingListAuditLogEntry.class, name = "mailing_list_audit"),
		@Type(value = MailingListContactAuditLogEntry.class, name = "mailing_list_contact_audit"),
		@Type(value = UploadRequestAuditLogEntry.class, name = "upload_request_audit"),
		@Type(value = UploadRequestGroupAuditLogEntry.class, name = "upload_request_group_audit"),
		@Type(value = AnonymousShareAuditLogEntry.class, name = "upload_request_group_audit"),
		@Type(value = UserPreferenceAuditLogEntry.class, name = "upload_request_group_audit")})
@XmlRootElement(name = "AuditLogEntryUser")
@XmlSeeAlso({ ShareAuditLogEntry.class,
	ThreadAuditLogEntry.class,
	ThreadMemberAuditLogEntry.class,
	UserAuditLogEntry.class,
	GuestAuditLogEntry.class,
	MailingListContactAuditLogEntry.class,
	MailingListAuditLogEntry.class,
	UploadRequestAuditLogEntry.class,
	UploadRequestGroupAuditLogEntry.class,
	AnonymousShareAuditLogEntry.class,
	UserPreferenceAuditLogEntry.class})
@Document(collection = "auditLogEntry")
public abstract class AuditLogEntryUser {

	protected AccountMto actor;

	protected AccountMto owner;

	protected LogAction action;

	protected Date creationDate;

	protected AuditLogEntryType type;

	protected String resourceUuid;

	public AuditLogEntryUser() {
		super();
	}

	public AuditLogEntryUser(AuditLogEntryUser log) {
		this.action = log.getAction();
		this.creationDate = log.getCreationDate();
		this.type = log.getType();
		this.actor = log.getActor();
		this.owner = log.getOwner();
		this.resourceUuid = log.getResourceUuid();
	}

	public AuditLogEntryUser(AccountMto actor, AccountMto owner, LogAction action, AuditLogEntryType type,
			String resourceUuid) {
		this.action = action;
		this.creationDate = new Date();
		this.setActor(actor);
		this.type = type;
		this.resourceUuid = resourceUuid;
		this.owner = owner;
	}

	public AuditLogEntryUser(ShareAuditLogEntry log) {
		this.actor = log.getActor();
		this.action = log.getAction();
		this.creationDate = log.getCreationDate();
		this.type = log.getType();
		this.owner = log.getOwner();
		this.resourceUuid = log.getResourceUuid();
	}

	public AuditLogEntryUser(ThreadAuditLogEntry log) {
		this.actor = log.getActor();
		this.action = log.getAction();
		this.creationDate = log.getCreationDate();
		this.type = log.getType();
		this.resourceUuid = log.getResourceUuid();
	}

	public LogAction getAction() {
		return action;
	}

	public void setAction(LogAction action) {
		this.action = action;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public AuditLogEntryType getType() {
		return type;
	}

	public void setType(AuditLogEntryType type) {
		this.type = type;
	}

	public AccountMto getActor() {
		return actor;
	}

	public void setActor(AccountMto actor) {
		this.actor = actor;
	}

	public String getResourceUuid() {
		return resourceUuid;
	}

	public void setResourceUuid(String resourceUuid) {
		this.resourceUuid = resourceUuid;
	}

	public AccountMto getOwner() {
		return owner;
	}

	public void setOwner(AccountMto owner) {
		this.owner = owner;
	}

	public String getRepresentation(AuditLogEntryUser log) {
		return "action : " + log.getAction().name() + ", type : " + log.getType().name();
	}
}
