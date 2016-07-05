/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
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

package org.linagora.linshare.core.facade.webservice.delegation.dto;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.facade.webservice.common.dto.GenericUserDto;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class ShareCreationDto {

	@ApiModelProperty(value = "Recipients")
	protected List<GenericUserDto> recipients;

	@ApiModelProperty(value = "Document uuids")
	protected List<String> documents;

	@ApiModelProperty(value = "Secured")
	protected Boolean secured;

	@ApiModelProperty(value = "Share acknowledgement, this boolean allows to choose whether or not the creator of the share wants to receive an acknowledgement.")
	protected Boolean creationAcknowledgement;

	@ApiModelProperty(value = "ExpirationDate")
	protected Date expirationDate;

	@ApiModelProperty(value = "Subject")
	protected String subject;

	@ApiModelProperty(value = "Message")
	protected String message;

	@ApiModelProperty(value = "notificationDateForUSDA")
	protected Date notificationDateForUSDA;

	@ApiModelProperty(value = "enableUSDA")
	protected Boolean enableUSDA;

	@ApiModelProperty(value = "sharingNote")
	protected String sharingNote;

	@ApiModelProperty(value= "mailingListUuid")
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
}
