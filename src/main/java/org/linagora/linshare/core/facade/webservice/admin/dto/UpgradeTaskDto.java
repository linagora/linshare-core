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
package org.linagora.linshare.core.facade.webservice.admin.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.UpgradeTaskGroup;
import org.linagora.linshare.core.domain.constants.UpgradeTaskPriority;
import org.linagora.linshare.core.domain.constants.UpgradeTaskStatus;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.UpgradeTask;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "UpgradeTask")
@Schema(name = "UpgradeTask", description = "each object represents a task to accomplish in order to complete the upgrade process.")
public class UpgradeTaskDto {

	@Schema(description = "Identifier")
	protected UpgradeTaskType identifier;

	@Schema(description = "TaskGroup")
	protected UpgradeTaskGroup taskGroup;

	@Schema(description = "ParentIdentifier")
	protected UpgradeTaskType parentIdentifier;

	@Schema(description = "TaskOrder")
	protected Integer taskOrder;

	@Schema(description = "Status")
	protected UpgradeTaskStatus status;

	@Schema(description = "Priority")
	protected UpgradeTaskPriority priority;

	@Schema(description = "CreationDate")
	protected Date creationDate;

	@Schema(description = "ModificationDate")
	protected Date modificationDate;

	@Schema(description = "Asynchronous task created to manage upgrade task long time processing. (return by the serveur.")
	protected String asyncTaskUuid;

	public UpgradeTaskDto() {
		super();
	}

	public UpgradeTaskDto(UpgradeTask upgradeTask) {
		super();
		this.identifier = upgradeTask.getIdentifier();
		this.taskGroup = upgradeTask.getTaskGroup();
		this.parentIdentifier = upgradeTask.getParentIdentifier();
		this.taskOrder = upgradeTask.getTaskOrder();
		this.status = upgradeTask.getStatus();
		this.priority = upgradeTask.getPriority();
		this.creationDate = upgradeTask.getCreationDate();
		this.modificationDate = upgradeTask.getModificationDate();
		this.asyncTaskUuid = upgradeTask.getAsyncTaskUuid();
	}

	public UpgradeTaskType getIdentifier() {
		return identifier;
	}

	public void setIdentifier(UpgradeTaskType identifier) {
		this.identifier = identifier;
	}

	public UpgradeTaskGroup getTaskGroup() {
		return taskGroup;
	}

	public void setTaskGroup(UpgradeTaskGroup taskGroup) {
		this.taskGroup = taskGroup;
	}

	public UpgradeTaskType getParentIdentifier() {
		return parentIdentifier;
	}

	public void setParentIdentifier(UpgradeTaskType parentIdentifier) {
		this.parentIdentifier = parentIdentifier;
	}

	public Integer getTaskOrder() {
		return taskOrder;
	}

	public void setTaskOrder(Integer taskOrder) {
		this.taskOrder = taskOrder;
	}

	public UpgradeTaskStatus getStatus() {
		return status;
	}

	public void setStatus(UpgradeTaskStatus status) {
		this.status = status;
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

	public String getAsyncTaskUuid() {
		return asyncTaskUuid;
	}

	public void setAsyncTaskUuid(String asyncTaskUuid) {
		this.asyncTaskUuid = asyncTaskUuid;
	}

	public UpgradeTaskPriority getPriority() {
		return priority;
	}

	public void setPriority(UpgradeTaskPriority priority) {
		this.priority = priority;
	}

	@Override
	public String toString() {
		return "UpgradeTaskDto [identifier=" + identifier + ", taskGroup=" + taskGroup
				+ ", parentIdentifier=" + parentIdentifier + ", taskOrder=" + taskOrder
				+ ", status=" + status + ", priority=" + priority + ", creationDate=" + creationDate
				+ ", modificationDate=" + modificationDate + ", asyncTaskUuid=" + asyncTaskUuid + "]";
	}
}
