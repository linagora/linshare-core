/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.mongo.entities.logs;

import java.util.Date;
import java.util.UUID;

import javax.persistence.GeneratedValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@Type(value = AuthenticationAuditLogEntryUser.class, name = "authentication_audit"),
		@Type(value = MailAttachmentAuditLogEntry.class, name = "mail_attchment_audit"),
		@Type(value = SafeDetailAuditLogEntry.class, name = "safe_detali_audit"),
		@Type(value = WorkGroupNodeAuditLogEntry.class, name = "workgroup_node_audit"),
		@Type(value = ShareEntryAuditLogEntry.class, name = "share_audit"),
		@Type(value = ThreadAuditLogEntry.class, name = "thread_audit"),
		@Type(value = ThreadMemberAuditLogEntry.class, name = "thread_member_audit"),
		@Type(value = UserAuditLogEntry.class, name = "user_audit"),
		@Type(value = GuestAuditLogEntry.class, name = "guest_audit"),
		@Type(value = MailingListAuditLogEntry.class, name = "mailing_list_audit"),
		@Type(value = MailingListContactAuditLogEntry.class, name = "mailing_list_contact_audit"),
		@Type(value = UploadRequestAuditLogEntry.class, name = "upload_request_audit"),
		@Type(value = UploadRequestGroupAuditLogEntry.class, name = "upload_request_group_audit"),
		@Type(value = UploadRequestUrlAuditLogEntry.class, name = "upload_request_url_audit"),
		@Type(value = UploadRequestEntryAuditLogEntry.class, name = "upload_request_entry_audit"),
		@Type(value = UserPreferenceAuditLogEntry.class, name = "user_preference_audit"),
		@Type(value = DomainAuditLogEntry.class, name = "domain_audit"),
		@Type(value = DomainPatternAuditLogEntry.class, name = "domain_pattern_audit"),
		@Type(value = LdapConnectionAuditLogEntry.class, name = "ldap_connection_audit"),
		@Type(value = FunctionalityAuditLogEntry.class, name = "ldap_connection_audit"),
		@Type(value = PublicKeyAuditLogEntry.class, name = "public_key_audit"),
		@Type(value = JwtLongTimeAuditLogEntry.class, name = "jwt_longtime"),
		@Type(value = SharedSpaceNodeAuditLogEntry.class, name = "shared_space_node_audit"),
		@Type(value = SharedSpaceMemberAuditLogEntry.class, name = "shared_space_member_audit")
	})
@XmlSeeAlso({
	AuthenticationAuditLogEntryUser.class,
	MailAttachmentAuditLogEntry.class,
	SafeDetailAuditLogEntry.class,
	WorkGroupNodeAuditLogEntry.class,
	ShareEntryAuditLogEntry.class,
	DocumentEntryAuditLogEntry.class,
	ThreadAuditLogEntry.class,
	ThreadMemberAuditLogEntry.class,
	UserAuditLogEntry.class,
	GuestAuditLogEntry.class,
	MailingListContactAuditLogEntry.class,
	MailingListAuditLogEntry.class,
	UploadRequestAuditLogEntry.class,
	UploadRequestGroupAuditLogEntry.class,
	UploadRequestUrlAuditLogEntry.class,
	UploadRequestEntryAuditLogEntry.class,
	UserPreferenceAuditLogEntry.class,
	DomainAuditLogEntry.class,
	DomainPatternAuditLogEntry.class,
	LdapConnectionAuditLogEntry.class,
	FunctionalityAuditLogEntry.class,
	PublicKeyAuditLogEntry.class,
	JwtLongTimeAuditLogEntry.class,
	SharedSpaceNodeAuditLogEntry.class,
	SharedSpaceMemberAuditLogEntry.class
	})
@XmlRootElement(name = "AuditLogEntry")
@Document(collection="audit_log_entries")
public class AuditLogEntry {

	@JsonIgnore
	@Id @GeneratedValue
	protected String id;

	protected String uuid;

	protected AccountMto authUser;

	protected String resourceUuid;

	protected LogAction action;

	protected LogActionCause cause;

	protected String fromResourceUuid;

	protected AuditLogEntryType type;

	protected Date creationDate;

	@JsonIgnore
	protected String technicalComment;

	public AuditLogEntry() {
		super();
		this.uuid = UUID.randomUUID().toString();
	}

	@XmlTransient
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public AccountMto getAuthUser() {
		return authUser;
	}

	public void setAuthUser(AccountMto authUser) {
		this.authUser = authUser;
	}

	public String getResourceUuid() {
		return resourceUuid;
	}

	public void setResourceUuid(String resourceUuid) {
		this.resourceUuid = resourceUuid;
	}

	public LogAction getAction() {
		return action;
	}

	public void setAction(LogAction action) {
		this.action = action;
	}

	public AuditLogEntryType getType() {
		return type;
	}

	public void setType(AuditLogEntryType type) {
		this.type = type;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getRepresentation(AuditLogEntryUser log) {
		return "action : " + log.getAction().name() + ", type : " + log.getType().name();
	}

	@XmlTransient
	public String getTechnicalComment() {
		return technicalComment;
	}

	public void setTechnicalComment(String technicalComment) {
		this.technicalComment = technicalComment;
	}

	public LogActionCause getCause() {
		return cause;
	}

	public void setCause(LogActionCause cause) {
		this.cause = cause;
	}

	public String getFromResourceUuid() {
		return fromResourceUuid;
	}

	public void setFromResourceUuid(String fromResourceUuid) {
		this.fromResourceUuid = fromResourceUuid;
	}

	@Override
	public String toString() {
		return "AuditLogEntry [AuthUser=" + authUser + ", resourceUuid=" + resourceUuid + ", action=" + action + ", type="
				+ type + ", creationDate=" + creationDate + "]";
	}
}