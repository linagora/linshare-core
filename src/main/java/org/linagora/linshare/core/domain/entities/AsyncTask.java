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
package org.linagora.linshare.core.domain.entities;

import java.util.Calendar;
import java.util.Date;

import org.linagora.linshare.core.domain.constants.AsyncTaskStatus;
import org.linagora.linshare.core.domain.constants.AsyncTaskType;

/**
 * @author fred
 *
 */
public class AsyncTask {

	protected Long id;

	protected Account actor;

	protected Account owner;

	protected AbstractDomain domain;

	protected String uuid;

	protected AsyncTaskStatus status;

	protected AsyncTaskType taskType;

	protected Date creationDate;

	protected Date startProcessingDate;

	protected Date endProcessingDate;

	protected Long processingDuration;

	protected Date modificationDate;

	protected Integer errorCode;

	protected String errorMsg;

	protected String errorName;

	protected Long size;

	protected Long transfertDuration;

	protected Long waitingDuration;

	protected String fileName;

	protected String resourceUuid;

	protected Integer frequency;

	protected String metaData;

	protected UpgradeTask upgradeTask;

	public AsyncTask(Long size, Long transfertDuration, String fileName,
			Integer frequency, AsyncTaskType taskType) {
		super();
		this.status = AsyncTaskStatus.PENDING;
		this.size = size;
		this.fileName = fileName;
		this.frequency = frequency;
		this.taskType = taskType;
		this.transfertDuration = transfertDuration;
	}

	public AsyncTask() {
		super();
	}

	public AsyncTask(String fileName, Integer frequency, AsyncTaskType taskType) {
		this.status = AsyncTaskStatus.PENDING;
		this.fileName = fileName;
		this.frequency = frequency;
		this.taskType = taskType;
	}

	public AsyncTask(UpgradeTask upgradeTask, AsyncTaskType taskType) {
		this.status = AsyncTaskStatus.PENDING;
		this.fileName = upgradeTask.getIdentifier().name();
		this.taskType = taskType;
		this.upgradeTask = upgradeTask;
	}

	@Override
	public String toString() {
		return "AsyncTask [owner=" + owner + ", uuid=" + uuid + ", status="
				+ status + ", taskType=" + taskType + ", processingDuration="
				+ processingDuration + ", size=" + size
				+ ", transfertDuration=" + transfertDuration
				+ ", waitingDuration=" + waitingDuration + ", fileName="
				+ fileName + ", resourceUuid=" + resourceUuid + "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Account getActor() {
		return actor;
	}

	public void setActor(Account actor) {
		this.actor = actor;
	}

	public Account getOwner() {
		return owner;
	}

	public void setOwner(Account owner) {
		this.owner = owner;
	}

	public AbstractDomain getDomain() {
		return domain;
	}

	public void setDomain(AbstractDomain domain) {
		this.domain = domain;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public AsyncTaskStatus getStatus() {
		return status;
	}

	public void setStatus(AsyncTaskStatus status) {
		this.status = status;
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

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Integer getFrequency() {
		return frequency;
	}

	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}

	public AsyncTaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(AsyncTaskType taskType) {
		this.taskType = taskType;
	}

	public String getResourceUuid() {
		return resourceUuid;
	}

	public void setResourceUuid(String resourceUuid) {
		this.resourceUuid = resourceUuid;
	}

	public Date getStartProcessingDate() {
		return startProcessingDate;
	}

	public void setStartProcessingDate(Date startProcessingDate) {
		this.startProcessingDate = startProcessingDate;
	}

	public Date getEndProcessingDate() {
		return endProcessingDate;
	}

	public void setEndProcessingDate(Date endProcessingDateDate) {
		this.endProcessingDate = endProcessingDateDate;
	}

	public void computeProcessingDuration() {
		if (startProcessingDate != null && endProcessingDate != null) {
			Calendar start = Calendar.getInstance();
			start.setTime(startProcessingDate);
			Calendar end = Calendar.getInstance();
			end.setTime(endProcessingDate);
			this.processingDuration = end.getTimeInMillis()
					- start.getTimeInMillis();
		}
	}

	public void computeWaitingDuration() {
		if (creationDate != null && startProcessingDate != null) {
			Calendar start = Calendar.getInstance();
			start.setTime(creationDate);
			Calendar end = Calendar.getInstance();
			end.setTime(startProcessingDate);
			this.waitingDuration = end.getTimeInMillis() - start.getTimeInMillis();
		}
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Long getProcessingDuration() {
		return processingDuration;
	}

	public void setProcessingDuration(Long processingDuration) {
		this.processingDuration = processingDuration;
	}

	public Long getTransfertDuration() {
		return transfertDuration;
	}

	public void setTransfertDuration(Long transfertDuration) {
		this.transfertDuration = transfertDuration;
	}

	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
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

	public UpgradeTask getUpgradeTask() {
		return upgradeTask;
	}

	public void setUpgradeTask(UpgradeTask upgradeTask) {
		this.upgradeTask = upgradeTask;
	}
}
