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

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "AnonymousUrl")
@Schema(name = "AnonymousUrl", description = "An AnonymousUrl")
public class AnonymousUrlDto {

	@Schema(description = "Uuid")
	private String uuid;

	@Schema(description = "Actor")
	private ContactDto actor;

	@Schema(description = "documents")
	private List<ShareEntryDto> documents;

	@Schema(description = "CreationDate")
	private Date creationDate;

	@Schema(description = "ExpirationDate")
	private Date expirationDate;

	@Schema(description = "Recipient")
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
