/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
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
package org.linagora.linshare.core.facade.webservice.admin.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.UpgradeTaskGroup;
import org.linagora.linshare.core.domain.constants.UpgradeTaskPriority;
import org.linagora.linshare.core.domain.constants.UpgradeTaskStatus;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.UpgradeTask;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "UpgradeTask")
@ApiModel(value = "UpgradeTask", description = "each object represents a task to accomplish in order to complete the upgrade process.")
public class UpgradeTaskDto {

	@ApiModelProperty(value = "Identifier")
	protected UpgradeTaskType identifier;

	@ApiModelProperty(value = "TaskGroup")
	protected UpgradeTaskGroup taskGroup;

	@ApiModelProperty(value = "ParentIdentifier")
	protected UpgradeTaskType parentIdentifier;

	@ApiModelProperty(value = "TaskOrder")
	protected Integer taskOrder;

	@ApiModelProperty(value = "Status")
	protected UpgradeTaskStatus status;

	@ApiModelProperty(value = "Priority")
	protected UpgradeTaskPriority priority;

	@ApiModelProperty(value = "CreationDate")
	protected Date creationDate;

	@ApiModelProperty(value = "ModificationDate")
	protected Date modificationDate;

	@ApiModelProperty(value = "Asynchronous task created to manage upgrade task long time processing. (return by the serveur.")
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
