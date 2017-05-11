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
 * and free version of LinShare™, powered by Linagora © 2009–2016. Contribute to
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
package org.linagora.linshare.core.facade.webservice.external.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.facade.webservice.common.dto.ContactDto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "AnonymousUrl")
@ApiModel(value = "AnonymousUrl", description = "An AnonymousUrl")
public class AnonymousUrlDto {

	@ApiModelProperty(value = "Uuid")
	private String uuid;

	@ApiModelProperty(value = "Actor")
	private ContactDto actor;

	@ApiModelProperty(value = "documents")
	private List<ShareEntryDto> documents;

	@ApiModelProperty(value = "CreationDate")
	private Date creationDate;

	@ApiModelProperty(value = "ExpirationDate")
	private Date expirationDate;

	@ApiModelProperty(value = "Recipient")
	private ContactDto recipient;

	public AnonymousUrlDto() {
		super();
	}

	public AnonymousUrlDto(Account owner, AnonymousUrl url) {
		super();
		this.uuid = url.getUuid();
		this.actor = new ContactDto(url.getOwner());
		this.recipient = new ContactDto(url.getContact());
		this.documents = transformListShareEntriesToDto(url.getAnonymousShareEntries());
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public ContactDto getActor() {
		return actor;
	}

	public void setActor(ContactDto actor) {
		this.actor = actor;
	}

	public List<ShareEntryDto> getDocuments() {
		return documents;
	}

	public void setDocuments(List<ShareEntryDto> documents) {
		this.documents = documents;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public ContactDto getRecipient() {
		return recipient;
	}

	public void setRecipient(ContactDto recipient) {
		this.recipient = recipient;
	}

	private List<ShareEntryDto> transformListShareEntriesToDto(
			Set<AnonymousShareEntry> anonymousShareEntries) {
		boolean checkIt = true;
		List<ShareEntryDto> shareEntry = new ArrayList<ShareEntryDto>();
		for (AnonymousShareEntry anonymousShareEntry : anonymousShareEntries) {
			ShareEntryDto shareEntrydto = new ShareEntryDto(anonymousShareEntry.getUuid(),
					anonymousShareEntry.getDocumentEntry());
			shareEntry.add(shareEntrydto);
			if (checkIt) {
				ShareEntryGroup entryGroup = anonymousShareEntry.getShareEntryGroup();
				if (entryGroup != null) {
					this.creationDate = entryGroup.getCreationDate();
					this.expirationDate = entryGroup.getExpirationDate();
				}
			}
			checkIt = false;
		}
		return shareEntry;
	}
}
