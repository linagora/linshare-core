/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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

package org.linagora.linshare.core.facade.webservice.delegation.dto;

import java.util.Date;
import java.util.List;
import java.util.Set;

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
}
