/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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
package org.linagora.linshare.core.facade.webservice.common.dto;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.facade.webservice.delegation.dto.DocumentDto;

import com.google.common.base.Function;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement(name = "UploadRequestEntry")
public class UploadRequestEntryDto {

	@ApiModelProperty(value = "Owner")
	protected AccountDto entryOwner;

	@ApiModelProperty(value = "Recipient")
	private ContactDto recipient;

	@ApiModelProperty(value = "CreationDate")
	protected Calendar creationDate;

	@ApiModelProperty(value = "ModificationDate")
	protected Calendar modificationDate;

	@ApiModelProperty(value = "ExpirationDate")
	protected Calendar expirationDate;

	@ApiModelProperty(value = "Name")
	protected String name;

	@ApiModelProperty(value = "Comment")
	protected String comment;

	@ApiModelProperty(value = "Uuid")
	protected String uuid;

	@ApiModelProperty(value = "MetaData")
	protected String metaData;

	@ApiModelProperty(value = "CmisSync")
	protected boolean cmisSync;

	@ApiModelProperty(value = "Document")
	protected DocumentDto documentDto;

	@ApiModelProperty(value = "Size")
	protected Long size;

	@ApiModelProperty(value = "Type")
	protected String type;

	@ApiModelProperty(value = "Sha256sum")
	protected String sha256sum;

	@ApiModelProperty(value = "Copied")
	protected Boolean copied;

	@ApiModelProperty(value = "Ciphered")
	protected Boolean ciphered;

	public UploadRequestEntryDto() {
		super();
	}
	
	public UploadRequestEntryDto(UploadRequestEntry entry) {
		super();
		this.entryOwner = new AccountDto(entry.getEntryOwner(), false);
		this.recipient = new ContactDto(entry.getUploadRequestUrl().getContact());
		this.creationDate = entry.getCreationDate();
		this.modificationDate = entry.getModificationDate();
		this.expirationDate = entry.getExpirationDate();
		this.name = entry.getName();
		this.comment = entry.getComment();
		this.uuid = entry.getUuid();
		this.metaData = entry.getMetaData();
		this.cmisSync = entry.isCmisSync();
		this.documentDto = new DocumentDto(entry);
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

	public Calendar getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Calendar expirationDate) {
		this.expirationDate = expirationDate;
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

	public DocumentDto getDocumentDto() {
		return documentDto;
	}

	public void setDocumentDto(DocumentDto documentDto) {
		this.documentDto = documentDto;
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

	/*
	 * Transformers
	 */
	public static Function<UploadRequestEntry, UploadRequestEntryDto> toDto() {
		return ure -> new UploadRequestEntryDto(ure);
	}
}
