/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009-2018. Contribute to
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
import java.util.UUID;

import javax.persistence.GeneratedValue;
import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.UploadPropositionExceptionRuleType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "UploadPropositionExceptionRule")
@Document(collection = "upload_proposition_exception_rule")
public class UploadPropositionExceptionRule {

	@JsonIgnore
	@Id
	@GeneratedValue
	protected String id;

	@Schema(description = "Uuid")
	protected String uuid;

	@Schema(description = "DomainUuid")
	protected String domainUuid;

	@Schema(description = "Mail")
	protected String mail;

	@Schema(description = "AccountUuid")
	protected String accountUuid;

	@Schema(description = "ExceptionRule")
	protected UploadPropositionExceptionRuleType exceptionRuleType;

	@Schema(description = "CreationDate")
	protected Date creationDate;

	@Schema(description = "ModificationDate")
	protected Date modificationDate;

	public UploadPropositionExceptionRule() {
		super();
		this.uuid = UUID.randomUUID().toString();
	}

	public UploadPropositionExceptionRule(String uuid, String domainUuid, String mail, String accountUuid,
			UploadPropositionExceptionRuleType exceptionRuleType, Date creationDate, Date modificationDate) {
		super();
		this.uuid = uuid;
		this.domainUuid = domainUuid;
		this.mail = mail;
		this.accountUuid = accountUuid;
		this.exceptionRuleType = exceptionRuleType;
		this.creationDate = creationDate;
		this.modificationDate = modificationDate;
	}

	public UploadPropositionExceptionRule(UploadPropositionExceptionRule uploadPropositionExceptionRule) {
		super();
		this.uuid = uploadPropositionExceptionRule.getUuid();
		this.domainUuid = uploadPropositionExceptionRule.getDomainUuid();
		this.mail = uploadPropositionExceptionRule.getMail();
		this.accountUuid = uploadPropositionExceptionRule.getAccountUuid();
		this.exceptionRuleType = uploadPropositionExceptionRule.getExceptionRuleType();
		this.creationDate = uploadPropositionExceptionRule.getCreationDate();
		this.modificationDate = new Date();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getDomainUuid() {
		return domainUuid;
	}

	public void setDomainUuid(String domainUuid) {
		this.domainUuid = domainUuid;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getAccountUuid() {
		return accountUuid;
	}

	public void setAccountUuid(String accountUuid) {
		this.accountUuid = accountUuid;
	}

	public UploadPropositionExceptionRuleType getExceptionRuleType() {
		return exceptionRuleType;
	}

	public void setExceptionRuleType(UploadPropositionExceptionRuleType exceptionRuleType) {
		this.exceptionRuleType = exceptionRuleType;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}
}
