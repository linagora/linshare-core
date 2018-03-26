/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
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
import java.util.List;
import java.util.UUID;

import javax.persistence.GeneratedValue;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.linagora.linshare.core.domain.constants.UploadPropositionStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "UploadProposition")
@Document(collection = "upload_proposition")
public class UploadProposition {

	@JsonIgnore
	@Id
	@GeneratedValue
	protected String id;

	@ApiModelProperty(value = "Uuid")
	protected String uuid;

	@ApiModelProperty(value = "DomainUuid")
	protected String domainUuid;

	@ApiModelProperty(value = "Status")
	protected UploadPropositionStatus status;

	@ApiModelProperty(value = "Label")
	protected String label;

	@ApiModelProperty(value = "Body")
	protected String body;

	@ApiModelProperty(value = "Mail")
	protected String mail;

	@ApiModelProperty(value = "FirstName")
	protected String firstName;

	@ApiModelProperty(value = "LastName")
	protected String lastName;

	@ApiModelProperty(value = "AccountUuid")
	protected String accountUuid;

	@ApiModelProperty(value = "Filters")
	protected List<UploadPropositionFilter> filters;

	@ApiModelProperty(value = "CreationDate")
	protected Date creationDate;

	@ApiModelProperty(value = "ModificationDate")
	protected Date modificationDate;

	public UploadProposition() {
		super();
	}

	public UploadProposition(String uuid, String domainUuid, UploadPropositionStatus status, String label, String body,
			String mail, String firstName, String lastName, String accountUuid, List<UploadPropositionFilter> filters,
			Date creationDate, Date modificationDate) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.domainUuid = domainUuid;
		this.status = status;
		this.label = label;
		this.body = body;
		this.mail = mail;
		this.firstName = firstName;
		this.lastName = lastName;
		this.accountUuid = accountUuid;
		this.filters = filters;
		this.creationDate = new Date();
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

	public UploadPropositionStatus getStatus() {
		return status;
	}

	public void setStatus(UploadPropositionStatus status) {
		this.status = status;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
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

	public String getAccountUuid() {
		return accountUuid;
	}

	public void setAccountUuid(String accountUuid) {
		this.accountUuid = accountUuid;
	}

	public List<UploadPropositionFilter> getFilters() {
		return filters;
	}

	public void setFilters(List<UploadPropositionFilter> filters) {
		this.filters = filters;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
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
