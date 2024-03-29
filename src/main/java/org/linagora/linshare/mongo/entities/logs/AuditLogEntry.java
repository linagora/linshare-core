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

import java.util.Date;
import java.util.List;
import java.util.Set;
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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@Type(value = AuthenticationAuditLogEntryUser.class, name = "authentication_audit"),
		@Type(value = DomainAuditLogEntry.class, name = "domain_audit"),
		@Type(value = DomainPatternAuditLogEntry.class, name = "domain_pattern_audit"),
		@Type(value = FunctionalityAuditLogEntry.class, name = "ldap_connection_audit"),
		@Type(value = GroupFilterAuditLogEntry.class, name = "group_filter_audit"),
		@Type(value = GuestAuditLogEntry.class, name = "guest_audit"),
		@Type(value = JwtLongTimeAuditLogEntry.class, name = "jwt_longtime"),
		@Type(value = LdapConnectionAuditLogEntry.class, name = "ldap_connection_audit"),
		@Type(value = MailAttachmentAuditLogEntry.class, name = "mail_attchment_audit"),
		@Type(value = MailingListAuditLogEntry.class, name = "mailing_list_audit"),
		@Type(value = MailingListContactAuditLogEntry.class, name = "mailing_list_contact_audit"),
		@Type(value = ModeratorAuditLogEntry.class, name = "moderator_audit"),
		@Type(value = PublicKeyAuditLogEntry.class, name = "public_key_audit"),
		@Type(value = SafeDetailAuditLogEntry.class, name = "safe_detali_audit"),
		@Type(value = SharedSpaceMemberAuditLogEntry.class, name = "shared_space_member_audit"),
		@Type(value = SharedSpaceNodeAuditLogEntry.class, name = "shared_space_node_audit"),
		@Type(value = ShareEntryAuditLogEntry.class, name = "share_audit"),
		@Type(value = UploadRequestAuditLogEntry.class, name = "upload_request_audit"),
		@Type(value = UploadRequestEntryAuditLogEntry.class, name = "upload_request_entry_audit"),
		@Type(value = UploadRequestGroupAuditLogEntry.class, name = "upload_request_group_audit"),
		@Type(value = UploadRequestUrlAuditLogEntry.class, name = "upload_request_url_audit"),
		@Type(value = UserAuditLogEntry.class, name = "user_audit"),
		@Type(value = UserPreferenceAuditLogEntry.class, name = "user_preference_audit"),
		@Type(value = WorkGroupNodeAuditLogEntry.class, name = "workgroup_node_audit"),
		@Type(value = WorkSpaceFilterAuditLogEntry.class, name = "workspace_node_audit"),
	})
@XmlSeeAlso({
	AuthenticationAuditLogEntryUser.class,
	DocumentEntryAuditLogEntry.class,
	DomainAuditLogEntry.class,
	DomainPatternAuditLogEntry.class,
	FunctionalityAuditLogEntry.class,
	GuestAuditLogEntry.class,
	JwtLongTimeAuditLogEntry.class,
	LdapConnectionAuditLogEntry.class,
	MailAttachmentAuditLogEntry.class,
	MailingListAuditLogEntry.class,
	MailingListContactAuditLogEntry.class,
	PublicKeyAuditLogEntry.class,
	SafeDetailAuditLogEntry.class,
	SharedSpaceMemberAuditLogEntry.class,
	SharedSpaceNodeAuditLogEntry.class,
	ShareEntryAuditLogEntry.class,
	UploadRequestAuditLogEntry.class,
	UploadRequestEntryAuditLogEntry.class,
	UploadRequestGroupAuditLogEntry.class,
	UploadRequestUrlAuditLogEntry.class,
	UserAuditLogEntry.class,
	UserPreferenceAuditLogEntry.class,
	WorkGroupNodeAuditLogEntry.class,
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
	protected Set<String> relatedDomains;

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

	@XmlTransient
	public Set<String> getRelatedDomains() {
		return relatedDomains;
	}

	public void addRelatedDomains(String... relatedDomains) {
		if (this.relatedDomains == null) {
			this.relatedDomains = Sets.newHashSet();
		}
		this.relatedDomains.addAll(Lists.newArrayList(relatedDomains));
	}

	public void addRelatedDomains(List<String> relatedDomains) {
		if (this.relatedDomains == null) {
			this.relatedDomains = Sets.newHashSet();
		}
		this.relatedDomains.addAll(relatedDomains);
	}

	public void addRelatedDomains(Set<String> relatedDomains) {
		if (this.relatedDomains == null) {
			this.relatedDomains = Sets.newHashSet();
		}
		this.relatedDomains.addAll(relatedDomains);
	}

	@Override
	public String toString() {
		return "AuditLogEntry [AuthUser=" + authUser + ", resourceUuid=" + resourceUuid + ", action=" + action + ", type="
				+ type + ", creationDate=" + creationDate + "]";
	}
}