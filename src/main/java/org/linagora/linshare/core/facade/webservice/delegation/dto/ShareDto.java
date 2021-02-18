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

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.EntryType;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.facade.webservice.common.dto.GenericUserDto;

import io.swagger.v3.oas.annotations.media.Schema;


@XmlRootElement(name = "Share")
@Schema(name = "Share", description = "A document can be shared between users.")
public class ShareDto {

	@Schema(description = "Uuid")
	protected String uuid;

	@Schema(description = "Name")
	protected String name;

	@Schema(description = "CreationDate")
	protected Date creationDate;

	@Schema(description = "ModificationDate")
	protected Date modificationDate;

	@Schema(description = "ExpirationDate")
	protected Date expirationDate;

	@Schema(description = "Downloaded")
	protected Long downloaded;

	@Schema(description = "DocumentDto")
	protected DocumentDto documentDto;

	@Schema(description = "Recipient")
	protected GenericUserDto recipient;

	@Schema(description = "Description")
	protected String description;

	@Schema(description = "Sender")
	protected GenericUserDto sender;

	@Schema(description = "Size")
	protected Long size;

	@Schema(description = "Type")
	protected String type;

	@Schema(description = "Ciphered")
	protected Boolean ciphered;

	@Schema(description = "Message")
	protected String message;

	protected ShareDto(ShareEntry shareEntry, boolean receivedShare) {
		this.uuid = shareEntry.getUuid();
		this.name = shareEntry.getName();
		this.creationDate = shareEntry.getCreationDate().getTime();
		this.modificationDate = shareEntry.getModificationDate().getTime();
		this.expirationDate = shareEntry.getExpirationDate() != null ? shareEntry.getExpirationDate().getTime() : null;
		if (receivedShare) {
			this.downloaded = shareEntry.getDownloaded();
			this.description = shareEntry.getComment();
			this.sender = new GenericUserDto((User) shareEntry.getEntryOwner());
			this.size = shareEntry.getDocumentEntry().getSize();
			this.type = shareEntry.getDocumentEntry().getType();
			this.ciphered = shareEntry.getDocumentEntry().getCiphered();
		} else {
			// sent share.
			this.documentDto = new DocumentDto(shareEntry.getDocumentEntry());
			this.recipient = new GenericUserDto(
					(User) shareEntry.getRecipient());
		}
	}

	protected ShareDto(Entry entry) {
		this.uuid = entry.getUuid();
		this.name = entry.getName();
		this.creationDate = entry.getCreationDate().getTime();
		this.modificationDate = entry.getModificationDate().getTime();
		this.expirationDate = entry.getExpirationDate() != null ? entry.getExpirationDate().getTime() : null;

		EntryType entryType = entry.getEntryType();
		if (entryType.equals(EntryType.SHARE)) {
			ShareEntry shareEntry = (ShareEntry) entry;
			this.documentDto = new DocumentDto(shareEntry.getDocumentEntry());
			this.recipient = new GenericUserDto((User) shareEntry.getRecipient());
			this.size = shareEntry.getDocumentEntry().getSize();
			this.type = shareEntry.getDocumentEntry().getType();
			this.ciphered = shareEntry.getDocumentEntry().getCiphered();
			this.downloaded = shareEntry.getDownloaded();
			this.sender = new GenericUserDto((User) shareEntry.getEntryOwner());
		} else if (entryType.equals(EntryType.ANONYMOUS_SHARE)) {
			AnonymousShareEntry shareEntry = (AnonymousShareEntry) entry;
			this.documentDto = new DocumentDto(shareEntry.getDocumentEntry());
			this.recipient = new GenericUserDto(shareEntry.getAnonymousUrl().getContact());
			this.size = shareEntry.getDocumentEntry().getSize();
			this.type = shareEntry.getDocumentEntry().getType();
			this.ciphered = shareEntry.getDocumentEntry().getCiphered();
			this.downloaded = shareEntry.getDownloaded();
			this.sender = new GenericUserDto((User) shareEntry.getEntryOwner());
		}
	}

	public ShareDto() {
		super();
	}

	public static ShareDto getReceivedShare(ShareEntry shareEntry) {
		return new ShareDto(shareEntry, true);
	}

	public static ShareDto getSentShare(ShareEntry shareEntry) {
		return new ShareDto(shareEntry, false);
	}

	public static ShareDto getSentShare(Entry entry) {
		return new ShareDto(entry);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Long getDownloaded() {
		return downloaded;
	}

	public void setDownloaded(Long downloaded) {
		this.downloaded = downloaded;
	}

	public DocumentDto getDocumentDto() {
		return documentDto;
	}

	public void setDocumentDto(DocumentDto documentDto) {
		this.documentDto = documentDto;
	}

	public GenericUserDto getRecipient() {
		return recipient;
	}

	public void setRecipient(GenericUserDto recipient) {
		this.recipient = recipient;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public GenericUserDto getSender() {
		return sender;
	}

	public void setSender(GenericUserDto sender) {
		this.sender = sender;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getCiphered() {
		return ciphered;
	}

	public void setCiphered(Boolean ciphered) {
		this.ciphered = ciphered;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
