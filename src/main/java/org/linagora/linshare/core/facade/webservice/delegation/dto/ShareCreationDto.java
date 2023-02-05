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
package org.linagora.linshare.core.facade.webservice.delegation.dto;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.facade.webservice.common.dto.GenericUserDto;

import io.swagger.v3.oas.annotations.media.Schema;

public class ShareCreationDto {

	@Schema(description = "Recipients")
	protected List<GenericUserDto> recipients;

	@Schema(description = "Document uuids")
	protected List<String> documents;

	@Schema(description = "Secured")
	protected Boolean secured;

	@Schema(description = "Share acknowledgement, this boolean allows to choose whether or not the creator of the share wants to receive an acknowledgement.")
	protected Boolean creationAcknowledgement;

	@Schema(description = "Force anonymous sharing even for internal or guest users.")
	protected Boolean forceAnonymousSharing;

	@Schema(description = "ExpirationDate")
	protected Date expirationDate;

	@Schema(description = "Subject")
	protected String subject;

	@Schema(description = "Message")
	protected String message;

	@Schema(description = "notificationDateForUSDA")
	protected Date notificationDateForUSDA;

	@Schema(description = "enableUSDA")
	protected Boolean enableUSDA;

	@Schema(description = "sharingNote")
	protected String sharingNote;

	@Schema(description= "mailingListUuid")
	private Set<String> mailingListUuid;

	@Schema(description = "You can provide the ID of the email you are about to send in your MUA client"
			+ "(ex Thunderbird plugin). LinShare will send its emails with this value.")
	protected String inReplyTo;

	@Schema(description = "You can provide the IDs of all emails related the email you are about to send in your MUA client"
			+ "(ex Thunderbird plugin). LinShare will send its emails with this value.")
	protected String references;

	@Schema(description= "Language used for mail notification to external recipients.")
	protected Language externalMailLocale;

	public ShareCreationDto() {
		super();
	}

	public List<GenericUserDto> getRecipients() {
		return recipients;
	}

	public void setRecipients(List<GenericUserDto> recipients) {
		this.recipients = recipients;
	}

	public List<String> getDocuments() {
		return documents;
	}

	public void setDocuments(List<String> documents) {
		this.documents = documents;
	}

	public Boolean getSecured() {
		return secured;
	}

	public void setSecured(Boolean secured) {
		this.secured = secured;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getNotificationDateForUSDA() {
		return notificationDateForUSDA;
	}

	public void setNotificationDateForUSDA(Date notificationDateForUSDA) {
		this.notificationDateForUSDA = notificationDateForUSDA;
	}

	public Boolean isEnableUSDA() {
		return enableUSDA;
	}

	public void setEnableUSDA(Boolean enableUSDA) {
		this.enableUSDA = enableUSDA;
	}

	public String getSharingNote() {
		return sharingNote;
	}

	public void setSharingNote(String sharingNote) {
		this.sharingNote = sharingNote;
	}

	public void setCreationAcknowledgement(Boolean creationAcknowkedgement) {
		this.creationAcknowledgement = creationAcknowkedgement;
	}

	public Boolean isCreationAcknowledgement() {
		return creationAcknowledgement;
	}

	public Set<String> getMailingListUuid() {
		return mailingListUuid;
	}

	public void setMailingListUuid(Set<String> mailingListUuid) {
		this.mailingListUuid = mailingListUuid;
	}

	public Boolean getForceAnonymousSharing() {
		return forceAnonymousSharing;
	}

	public void setForceAnonymousSharing(Boolean forceAnonymousSharing) {
		this.forceAnonymousSharing = forceAnonymousSharing;
	}

	public String getInReplyTo() {
		return inReplyTo;
	}

	public void setInReplyTo(String inReplyTo) {
		this.inReplyTo = inReplyTo;
	}

	public String getReferences() {
		return references;
	}

	public void setReferences(String references) {
		this.references = references;
	}

	public Language getExternalMailLocale() {
		return externalMailLocale;
	}

	public void setExternalMailLocale(Language externalMailLocale) {
		this.externalMailLocale = externalMailLocale;
	}
}
