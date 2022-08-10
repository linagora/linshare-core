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
package org.linagora.linshare.core.facade.webservice.user.dto;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.facade.webservice.common.dto.AsyncTaskDto;
import org.linagora.linshare.core.facade.webservice.common.dto.EntryDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ShareDto;
import org.linagora.linshare.webservice.userv1.task.context.DocumentTaskContext;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "Document")
@Schema(name = "Document", description = "A Document")
public class DocumentDto extends EntryDto {

	@Schema(description = "Description")
	protected String description;

	@Schema(description = "CreationDate")
	protected Date creationDate;

	@Schema(description = "ModificationDate")
	protected Date modificationDate;

	@Schema(description = "ExpirationDate")
	protected Date expirationDate;

	@Schema(description = "Ciphered")
	protected Boolean ciphered;

	@Schema(description = "Type")
	protected String type;

	@Schema(description = "humanMimeType. Only on api v5, since LinShare v6.")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String humanMimeType;

	@Schema(description = "Size")
	protected Long size;

	@Schema(description = "MetaData")
	protected String metaData;

	@Schema(description = "Sha256sum")
	protected String sha256sum;

	@Schema(description = "hasThumbnail")
	protected boolean hasThumbnail;

	@Schema(description = "Shared")
	protected Long shared;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected AsyncTaskDto async;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected List<ShareDto> shares;

	public DocumentDto(AsyncTaskDto asyncTask, DocumentTaskContext documentTaskContext) {
		async = asyncTask;
		this.description = documentTaskContext.getDescription();
		this.name = documentTaskContext.getFileName();
		this.metaData = documentTaskContext.getMetaData();
	}

	public DocumentDto(DocumentEntry de) {
		this(de, 2);
	}

	public DocumentDto(DocumentEntry de, Integer version) {
		if (de == null)
			return;
		this.uuid = de.getUuid();
		this.name = de.getName();
		this.creationDate = de.getCreationDate().getTime();
		this.modificationDate = de.getModificationDate().getTime();
		if (de.getExpirationDate() != null) {
			this.expirationDate = de.getExpirationDate().getTime();
		}
		this.description = de.getComment();
		this.ciphered = de.getCiphered();
		this.type = de.getType();
		if (version >= 5) {
			this.humanMimeType = de.getHumanMimeType();
		}
		this.size = de.getSize();
		this.metaData = de.getMetaData();
		this.sha256sum = de.getSha256sum();
		this.hasThumbnail = de.isHasThumbnail();
		this.shared = de.getShared();
	}

	public DocumentDto() {
		super();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Boolean getCiphered() {
		return ciphered;
	}

	public void setCiphered(Boolean ciphered) {
		this.ciphered = ciphered;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public List<ShareDto> getShares() {
		return shares;
	}

	public void setShares(List<ShareDto> shares) {
		this.shares = shares;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	/**
	 * @return the metaData
	 */
	public String getMetaData() {
		return metaData;
	}

	/**
	 * @param metaData
	 *            the metaData to set
	 */
	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}

	/**
	 * @return the sha256sum
	 */
	public String getSha256sum() {
		return sha256sum;
	}

	/**
	 * @param sha256sum
	 *            the sha256sum to set
	 */
	public void setSha256sum(String sha256sum) {
		this.sha256sum = sha256sum;
	}

	public boolean isHasThumbnail() {
		return hasThumbnail;
	}

	public void setHasThumbnail(boolean hasThumbnail) {
		this.hasThumbnail = hasThumbnail;
	}

	public Long getShared() {
		return shared;
	}

	public void setShared(Long shared) {
		this.shared = shared;
	}

	public AsyncTaskDto getAsync() {
		return async;
	}

	public void setAsync(AsyncTaskDto async) {
		this.async = async;
	}

	public String getHumanMimeType() {
		return humanMimeType;
	}

	public void setHumanMimeType(String humanMimeType) {
		this.humanMimeType = humanMimeType;
	}

	@Override
	public String toString() {
		return "Document [id=" + uuid + ", name=" + name + ", creation="
				+ creationDate + "]";
	}

	/*
	 * Transformers
	 */
	public static Function<DocumentEntry, DocumentDto> toDto(Integer version) {
		return new Function<DocumentEntry, DocumentDto>() {
			@Override
			public DocumentDto apply(DocumentEntry arg0) {
				return new DocumentDto(arg0, version);
			}
		};
	}
}
