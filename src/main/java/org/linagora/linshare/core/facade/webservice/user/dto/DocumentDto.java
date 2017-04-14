/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.core.facade.webservice.user.dto;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.facade.webservice.common.dto.AsyncTaskDto;
import org.linagora.linshare.core.facade.webservice.common.dto.EntryDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ShareDto;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.webservice.userv1.task.context.DocumentTaskContext;

import com.google.common.base.Function;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "Document")
@ApiModel(value = "Document", description = "A Document")
public class DocumentDto extends EntryDto {

	@ApiModelProperty(value = "Description")
	protected String description;

	@ApiModelProperty(value = "CreationDate")
	protected Date creationDate;

	@ApiModelProperty(value = "ModificationDate")
	protected Date modificationDate;

	@ApiModelProperty(value = "ExpirationDate")
	protected Date expirationDate;

	@ApiModelProperty(value = "Ciphered")
	protected Boolean ciphered;

	@ApiModelProperty(value = "Type")
	protected String type;

	@ApiModelProperty(value = "Size")
	protected Long size;

	@ApiModelProperty(value = "MetaData")
	protected String metaData;

	@ApiModelProperty(value = "Sha256sum")
	protected String sha256sum;

	@ApiModelProperty(value = "hasThumbnail")
	protected boolean hasThumbnail;

	@ApiModelProperty(value = "Shared")
	protected Long shared;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	protected AsyncTaskDto async;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	protected List<ShareDto> shares;

	public DocumentDto(AsyncTaskDto asyncTask, DocumentTaskContext documentTaskContext) {
		async = asyncTask;
		this.description = documentTaskContext.getDescription();
		this.name = documentTaskContext.getFileName();
		this.metaData = documentTaskContext.getMetaData();
	}

	public DocumentDto(DocumentEntry de) {
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
		this.size = de.getSize();
		this.metaData = de.getMetaData();
		this.sha256sum = de.getSha256sum();
		this.hasThumbnail = de.isHasThumbnail();
		this.shared = de.getShared();
	}

	public DocumentDto() {
		super();
	}

	public DocumentDto(WorkGroupDocument de) {
		this.uuid = de.getUuid();
		this.name = de.getName();
		this.creationDate = de.getCreationDate();
		this.modificationDate = de.getModificationDate();
		this.description = de.getDescription();
		this.ciphered = de.getCiphered();
		this.type = de.getMimeType();
		this.size = de.getSize();
		this.metaData = de.getMetaData();
		this.sha256sum = de.getSha256sum();
		this.hasThumbnail = de.getHasThumbnail();
		this.shared = 0L;
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

	@Override
	public String toString() {
		return "Document [id=" + uuid + ", name=" + name + ", creation="
				+ creationDate + "]";
	}

	/*
	 * Transformers
	 */
	public static Function<DocumentEntry, DocumentDto> toDto() {
		return new Function<DocumentEntry, DocumentDto>() {
			@Override
			public DocumentDto apply(DocumentEntry arg0) {
				return new DocumentDto(arg0);
			}
		};
	}
}
