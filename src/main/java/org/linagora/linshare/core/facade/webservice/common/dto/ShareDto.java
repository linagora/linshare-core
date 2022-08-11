/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
package org.linagora.linshare.core.facade.webservice.common.dto;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.EntryType;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.linagora.linshare.utils.Version;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "Share")
@Schema(name = "Share", description = "A document can be shared between users.")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShareDto implements Serializable, Comparable<ShareDto> {

	private static final long serialVersionUID = -7270170736406800055L;

	/**
	 * Share
	 */
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

	/**
	 * SentShare
	 */
	@Schema(description = "Document")
	protected DocumentDto document;

	@Schema(description = "Recipient")
	protected GenericUserDto recipient;

	/**
	 * Received Share.
	 */
	@Schema(description = "Description")
	protected String description;

	@Schema(description = "Sender")
	protected UserDto sender;

	@Schema(description = "Size")
	protected Long size;

	@Schema(description = "Type")
	protected String type;

	@Schema(description = "humanMimeType. Only on api v5, since LinShare v6.")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String humanMimeType;

	@Schema(description = "Ciphered")
	protected Boolean ciphered;

	@Schema(description = "hasThumbnail")
	protected Boolean hasThumbnail;

	@Schema(description = "Message")
	protected String message;

	/**
	 * Constructor
	 * 
	 * @param entry
	 */
	protected ShareDto(Version version, Entry entry, boolean receivedShare, boolean withDocument) {
		this.uuid = entry.getUuid();
		this.name = entry.getName();
		this.creationDate = entry.getCreationDate().getTime();
		this.modificationDate = entry.getModificationDate().getTime();
		this.description = entry.getComment();
		if(entry.getExpirationDate() != null)
			this.expirationDate = entry.getExpirationDate().getTime();
		if (entry.getEntryType().equals(EntryType.SHARE)) {
			ShareEntry sa = (ShareEntry) entry;
			this.downloaded = sa.getDownloaded();
			if (receivedShare) {
				this.sender = UserDto.getSimple((User) entry.getEntryOwner());
				if (withDocument) {
					this.size = sa.getDocumentEntry().getSize();
					this.type = sa.getDocumentEntry().getType();
					if (version.isGreaterThanOrEquals(Version.V5)) {
						this.humanMimeType= sa.getDocumentEntry().getHumanMimeType();
					}
					this.ciphered = sa.getDocumentEntry().getCiphered();
					this.hasThumbnail = sa.getDocumentEntry().isHasThumbnail();
				}
			} else {
				// sent share.
				if (withDocument) {
					this.document = new DocumentDto(((ShareEntry) entry).getDocumentEntry(), version);
				}
				this.recipient = new GenericUserDto(sa.getRecipient());
			}
		} else if (entry.getEntryType().equals(EntryType.ANONYMOUS_SHARE)) {
			AnonymousShareEntry a = (AnonymousShareEntry) entry;
			this.downloaded = a.getDownloaded();
			if (withDocument) {
				this.document = new DocumentDto(a.getDocumentEntry(), version);
			}
			this.recipient = new GenericUserDto(a.getAnonymousUrl().getContact());
		}
	}

	public ShareDto() {
		super();
	}

	public static ShareDto getReceivedShare(Version version, Entry entry) {
		return new ShareDto(version, entry, true, true);
	}

	public static ShareDto getSentShare(Version version, Entry entry, boolean withDocument) {
		return new ShareDto(version, entry, false, withDocument);
	}

	public static ShareDto getSentShare(Version version, Entry entry) {
		return new ShareDto(version, entry, false, true);
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

	public DocumentDto getDocument() {
		return document;
	}

	public void setDocument(DocumentDto document) {
		this.document = document;
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

	public Boolean getHasThumbnail() {
		return hasThumbnail;
	}

	public void setHasThumbnail(Boolean hasThumbnail) {
		this.hasThumbnail = hasThumbnail;
	}

	@Override
	public int compareTo(ShareDto o) {
		return this.modificationDate.compareTo(o.getModificationDate());
	}

	public String getHumanMimeType() {
		return humanMimeType;
	}

	public void setHumanMimeType(String humanMimeType) {
		this.humanMimeType = humanMimeType;
	}

	/*
	 * Transformers
	 */
	public static Function<ShareEntry, ShareDto> toDto(Version version) {
		return new Function<ShareEntry, ShareDto>() {
			@Override
			public ShareDto apply(ShareEntry arg0) {
				return ShareDto.getReceivedShare(version, arg0);
			}
		};
	}

	public static Function<Entry, ShareDto> EntrytoDto(Version version) {
		return new Function<Entry, ShareDto>() {
			@Override
			public ShareDto apply(Entry arg0) {
				return ShareDto.getSentShare(version, arg0);
			}
		};
	}
}
