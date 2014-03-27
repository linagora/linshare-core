/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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
package org.linagora.linshare.webservice.dto;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "Share")
@ApiModel(value = "Share", description = "A document can be shared between users.")
public class ShareDto {

	/**
	 * Share
	 */
    @ApiModelProperty(value = "Uuid")
	protected String uuid;

    @ApiModelProperty(value = "Name")
	protected String name;

    @ApiModelProperty(value = "CreationDate")
	protected Calendar creationDate;

    @ApiModelProperty(value = "ModificationDate")
	protected Calendar modificationDate;

    @ApiModelProperty(value = "ExpirationDate")
	protected Calendar expirationDate;

    @ApiModelProperty(value = "Downloaded")
	protected Long downloaded;

	/**
	 * SentShare
	 */
    @ApiModelProperty(value = "DocumentDto")
	protected DocumentDto documentDto;

    @ApiModelProperty(value = "Recipient")
	protected UserDto recipient;

	/**
	 * Received Share.
	 */
    @ApiModelProperty(value = "Description")
	protected String description;

    @ApiModelProperty(value = "Sender")
	protected UserDto sender;

    @ApiModelProperty(value = "Size")
	protected Long size;

    @ApiModelProperty(value = "Type")
	protected String type;

    @ApiModelProperty(value = "Ciphered")
	protected Boolean ciphered;

	/**
	 * ???
	 */
    @ApiModelProperty(value = "Message")
	protected String message;

	/**
	 * Constructor
	 * 
	 * @param shareEntry
	 */
	protected ShareDto(ShareEntry shareEntry, boolean receivedShare) {
		this.uuid = shareEntry.getUuid();
		this.name = shareEntry.getName();
		this.creationDate = shareEntry.getCreationDate();
		this.modificationDate = shareEntry.getModificationDate();
		this.expirationDate = shareEntry.getExpirationDate();
		if (receivedShare) {
			this.downloaded = shareEntry.getDownloaded();
			this.description = shareEntry.getComment();
			this.sender = UserDto.getSimple((User) shareEntry.getEntryOwner());
			this.size = shareEntry.getDocumentEntry().getSize();
			this.type = shareEntry.getDocumentEntry().getType();
			this.ciphered = shareEntry.getDocumentEntry().getCiphered();
		} else {
			// sent share.
			this.documentDto = new DocumentDto(shareEntry.getDocumentEntry());
			this.recipient = UserDto.getSimple((User) shareEntry.getRecipient());
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

	public Calendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	public Calendar getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Calendar modificationDate) {
		this.modificationDate = modificationDate;
	}

	public Calendar getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Calendar expirationDate) {
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

	public UserDto getRecipient() {
		return recipient;
	}

	public void setRecipient(UserDto recipient) {
		this.recipient = recipient;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public UserDto getSender() {
		return sender;
	}

	public void setSender(UserDto sender) {
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
