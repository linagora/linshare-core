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

package org.linagora.linshare.core.facade.webservice.uploadproposition.dto;

import org.linagora.linshare.core.domain.constants.UploadPropositionActionType;
import org.linagora.linshare.core.domain.constants.UploadPropositionStatus;
import org.linagora.linshare.core.domain.entities.UploadProposition;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class UploadPropositionDto {

	@ApiModelProperty(value = "FirstName")
	protected String firstName;

	@ApiModelProperty(value = "LastName")
	protected String lastName;

	@ApiModelProperty(value = "Mail")
	protected String mail;

	@ApiModelProperty(value = "Subject")
	protected String subject;

	@ApiModelProperty(value = "Body")
	protected String body;

	@ApiModelProperty(value = "recipientMail")
	protected String recipientMail;

	@ApiModelProperty(value = "recipientDomain")
	protected String recipientDomain;

	@ApiModelProperty(value = "action")
	protected String action;

	public UploadPropositionDto() {
		super();
	}

	public UploadPropositionDto(String firstName, String lastName, String mail,
			String subject, String body, String recipientMail,
			String recipientDomain) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.mail = mail;
		this.subject = subject;
		this.body = body;
		this.recipientMail = recipientMail;
		this.recipientDomain = recipientDomain;
	}


	public UploadProposition toEntity(UploadPropositionDto dto) {
		UploadProposition entity = new UploadProposition();
		entity.setBody(dto.getBody());
		entity.setDomainSource(dto.getRecipientDomain());
		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
		entity.setSubject(dto.getSubject());
		entity.setMail(dto.getMail());
		entity.setRecipientMail(dto.getRecipientMail());
		entity.setStatus(UploadPropositionStatus.SYSTEM_PENDING);
		if (dto.getAction() != null) {
			UploadPropositionActionType actionType = UploadPropositionActionType.fromString(dto.getAction());
			if (UploadPropositionActionType.ACCEPT.equals(actionType)) {
				entity.setStatus(UploadPropositionStatus.SYSTEM_ACCEPTED);
			} else if (UploadPropositionActionType.REJECT.equals(actionType)) {
				entity.setStatus(UploadPropositionStatus.SYSTEM_REJECTED);
			}
		}
		return entity;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getRecipientMail() {
		return recipientMail;
	}

	public void setRecipientMail(String recipientMail) {
		this.recipientMail = recipientMail;
	}

	public String getRecipientDomain() {
		return recipientDomain;
	}

	public void setRecipientDomain(String recipientDomain) {
		this.recipientDomain = recipientDomain;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Override
	public String toString() {
		return "UploadPropositionDto [firstName=" + firstName + ", lastName="
				+ lastName + ", mail=" + mail + ", subject=" + subject
				+ ", body=" + body + ", recipientMail=" + recipientMail
				+ ", recipientDomain=" + recipientDomain + ", action=" + action
				+ "]";
	}
}
