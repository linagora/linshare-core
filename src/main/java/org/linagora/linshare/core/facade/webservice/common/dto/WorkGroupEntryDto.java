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

package org.linagora.linshare.core.facade.webservice.common.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.webservice.userv1.task.context.WorkGroupEntryTaskContext;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "ThreadEntry")
@Schema(name = "ThreadEntry", description = "A file in a thread.")
public class WorkGroupEntryDto extends EntryDto {

	@Schema(description = "Description")
	protected String description;

	@Schema(description = "CreationDate")
	protected Date creationDate;

	@Schema(description = "ModificationDate")
	protected Date modificationDate;

	@Schema(description = "Ciphered")
	protected Boolean ciphered;

	@Schema(description = "Type")
	protected String type;

	@Schema(description = "Size")
	protected Long size;

	@Schema(description = "MetaData")
	protected String metaData;

	@Schema(description = "Sha256sum")
	protected String sha256sum;

	@Schema(description = "hasThumbnail")
	protected boolean hasThumbnail;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected AsyncTaskDto async;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(description = "workGroup")
	protected WorkGroupLightDto workGroup;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(description = "workGroupFolder")
	protected WorkGroupNode workGroupFolder;

	public WorkGroupEntryDto(ThreadEntry te) {
		super();
		if (te == null) {
			return;
		}
		this.uuid = te.getUuid();
		this.name = te.getName();
		this.creationDate = te.getCreationDate().getTime();
		this.modificationDate = te.getModificationDate().getTime();
		this.description = te.getComment();
		this.ciphered = te.getCiphered();
		this.type = te.getType();
		this.size = te.getSize();
		this.metaData = te.getMetaData();
		this.sha256sum = te.getSha256sum();
		this.hasThumbnail = te.isHasThumbnail();
	}

	public WorkGroupEntryDto(WorkGroupDocument te) {
		super();
		if (te == null) {
			return;
		}
		this.uuid = te.getUuid();
		this.name = te.getName();
		this.creationDate = te.getCreationDate();
		this.modificationDate = te.getModificationDate();
		this.description = te.getDescription();
		this.ciphered = te.getCiphered();
		this.type = te.getMimeType();
		this.size = te.getSize();
		this.metaData = te.getMetaData();
		this.sha256sum = te.getSha256sum();
		this.hasThumbnail = te.getHasRevision();
	}

	public WorkGroupEntryDto() {
		super();
	}

	public WorkGroupEntryDto(AsyncTaskDto asyncTask,
			WorkGroupEntryTaskContext workGroupEntryTaskContext) {
		async = asyncTask;
		this.name = workGroupEntryTaskContext.getFileName();
	}

	public String getSha256sum() {
		return sha256sum;
	}

	public void setSha256sum(String sha256sum) {
		this.sha256sum = sha256sum;
	}

	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
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

	public Boolean getCiphered() {
		return ciphered;
	}

	public void setCiphered(Boolean ciphered) {
		this.ciphered = ciphered;
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

	public boolean isHasThumbnail() {
		return hasThumbnail;
	}

	public void setHasThumbnail(boolean hasThumbnail) {
		this.hasThumbnail = hasThumbnail;
	}

	public WorkGroupLightDto getWorkGroup() {
		return workGroup;
	}

	public void setWorkGroup(WorkGroupLightDto workGroup) {
		this.workGroup = workGroup;
	}

	public WorkGroupNode getWorkGroupFolder() {
		return workGroupFolder;
	}

	public void setWorkGroupFolder(WorkGroupNode workGroupFolder) {
		this.workGroupFolder = workGroupFolder;
	}

	@Override
	public String toString() {
		return "ThreadEntry [id=" + uuid + ", name=" + name + ", creation="
				+ creationDate + "]";
	}
}
