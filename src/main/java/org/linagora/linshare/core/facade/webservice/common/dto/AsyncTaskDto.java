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

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.AsyncTask;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "AsyncTask")
@Schema(name = "AsyncTask", description = "An async task")
public class AsyncTaskDto {

	protected String uuid;

	protected String status;

	protected String errorMsg;

	protected String errorName;

	protected Integer errorCode;

	protected Integer frequency;

	protected String fileName;

	protected String resourceUuid;

	protected Date creationDate;

	protected Date modificationDate;

	protected Long transfertDuration;

	protected Long waitingDuration;

	protected Long processingDuration;

	public AsyncTaskDto() {
		super();
	}

	public AsyncTaskDto(AsyncTask task) {
		this.status = task.getStatus().name();
		this.uuid = task.getUuid();
		this.errorMsg = task.getErrorMsg();
		this.errorName= task.getErrorName();
		this.errorCode = task.getErrorCode();
		this.creationDate = task.getCreationDate();
		this.modificationDate = task.getModificationDate();
		this.frequency = task.getFrequency();
		this.fileName = task.getFileName();
		this.resourceUuid = task.getResourceUuid();
		this.transfertDuration = task.getTransfertDuration();
		this.processingDuration = task.getProcessingDuration();
	}

	@Override
	public String toString() {
		return "AsyncTaskDto [uuid=" + uuid + ", status=" + status
				+ ", errorMsg=" + errorMsg + ", errorCode=" + errorCode
				+ ", resourceUuid=" + resourceUuid + ", creationDate="
				+ creationDate + ", modificationDate=" + modificationDate + "]";
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
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

	public void setModificationDate(Date modiciationDate) {
		this.modificationDate = modiciationDate;
	}

	public Integer getFrequency() {
		return frequency;
	}

	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getResourceUuid() {
		return resourceUuid;
	}

	public void setResourceUuid(String resourceUuid) {
		this.resourceUuid = resourceUuid;
	}

	public Long getTransfertDuration() {
		return transfertDuration;
	}

	public void setTransfertDuration(Long transfertDuration) {
		this.transfertDuration = transfertDuration;
	}

	public Long getProcessingDuration() {
		return processingDuration;
	}

	public void setProcessingDuration(Long processingDuration) {
		this.processingDuration = processingDuration;
	}

	public String getErrorName() {
		return errorName;
	}

	public void setErrorName(String errorName) {
		this.errorName = errorName;
	}

	public Long getWaitingDuration() {
		return waitingDuration;
	}

	public void setWaitingDuration(Long waitingDuration) {
		this.waitingDuration = waitingDuration;
	}

}
