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
package org.linagora.linshare.core.facade.webservice.common.dto;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.UploadRequestEntry;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "UploadRequestEntry")
public class UploadRequestEntryDto {

	@Schema(description = "Owner")
	protected AccountDto entryOwner;

	@Schema(description = "Recipient")
	private ContactDto recipient;

	@Schema(description = "CreationDate")
	protected Calendar creationDate;

	@Schema(description = "ModificationDate")
	protected Calendar modificationDate;

	@Schema(description = "Name")
	protected String name;

	@Schema(description = "Comment")
	protected String comment;

	@Schema(description = "Uuid")
	protected String uuid;

	@Schema(description = "MetaData")
	protected String metaData;

	@Schema(description = "CmisSync")
	protected boolean cmisSync;

	@Schema(description = "Size")
	protected Long size;

	@Schema(description = "Type")
	protected String type;

	@Schema(description = "humanMimeType. Only on api v5, since LinShare v6.")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String humanMimeType;

	@Schema(description = "Sha256sum")
	protected String sha256sum;

	@Schema(description = "Copied")
	protected Boolean copied;

	@Schema(description = "Ciphered")
	protected Boolean ciphered;

	public UploadRequestEntryDto() {
		super();
	}

	public UploadRequestEntryDto(UploadRequestEntry entry) {
		this(entry, 2);
	}

	public UploadRequestEntryDto(UploadRequestEntry entry, Integer version) {
		super();
		this.entryOwner = new AccountDto(entry.getEntryOwner(), false);
		this.recipient = new ContactDto(entry.getUploadRequestUrl().getContact());
		this.creationDate = entry.getCreationDate();
		this.modificationDate = entry.getModificationDate();
		this.name = entry.getName();
		this.comment = entry.getComment();
		this.uuid = entry.getUuid();
		this.metaData = entry.getMetaData();
		this.cmisSync = entry.isCmisSync();
		this.size = entry.getSize();
		this.type = entry.getType();
		if (version >= 5) {
			this.humanMimeType = entry.getHumanMimeType();
		}
		this.copied = entry.getCopied();
	}

	public AccountDto getEntryOwner() {
		return entryOwner;
	}

	public void setEntryOwner(AccountDto entryOwner) {
		this.entryOwner = entryOwner;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}

	public boolean isCmisSync() {
		return cmisSync;
	}

	public void setCmisSync(boolean cmisSync) {
		this.cmisSync = cmisSync;
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

	public String getSha256sum() {
		return sha256sum;
	}

	public void setSha256sum(String sha256sum) {
		this.sha256sum = sha256sum;
	}

	public Boolean getCopied() {
		return copied;
	}

	public void setCopied(Boolean copied) {
		this.copied = copied;
	}

	public Boolean getCiphered() {
		return ciphered;
	}

	public void setCiphered(Boolean ciphered) {
		this.ciphered = ciphered;
	}

	public ContactDto getRecipient() {
		return recipient;
	}

	public void setRecipient(ContactDto recipient) {
		this.recipient = recipient;
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
	public static Function<UploadRequestEntry, UploadRequestEntryDto> toDto(Integer version) {
		return ure -> new UploadRequestEntryDto(ure, version);
	}
}
