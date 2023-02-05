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

import java.util.Date;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.UpgradeTaskGroup;
import org.linagora.linshare.core.domain.constants.UpgradeTaskPriority;
import org.linagora.linshare.core.domain.constants.UpgradeTaskStatus;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;

public class UpgradeTask {

	protected Long id;

	protected String uuid;

	protected UpgradeTaskType identifier;

	protected UpgradeTaskGroup taskGroup;

	protected String parentUuid;

	protected UpgradeTaskType parentIdentifier;

	protected Integer taskOrder;

	protected UpgradeTaskStatus status;

	protected UpgradeTaskPriority priority;

	protected Date creationDate;

	protected Date modificationDate;

	protected String extras;

	// last ran task
	protected String asyncTaskUuid;

	protected Set<AsyncTask> upgradeAsyncTask;

	protected boolean hidden;

	public UpgradeTask() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public String getParentUuid() {
		return parentUuid;
	}

	public void setParentUuid(String parentUuid) {
		this.parentUuid = parentUuid;
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

	public String getExtras() {
		return extras;
	}

	public void setExtras(String extras) {
		this.extras = extras;
	}

	public String getAsyncTaskUuid() {
		return asyncTaskUuid;
	}

	public void setAsyncTaskUuid(String asyncTaskUuid) {
		this.asyncTaskUuid = asyncTaskUuid;
	}

	public Set<AsyncTask> getUpgradeAsyncTask() {
		return upgradeAsyncTask;
	}

	public void setUpgradeAsyncTask(Set<AsyncTask> upgradeAsyncTask) {
		this.upgradeAsyncTask = upgradeAsyncTask;
	}

	public UpgradeTaskPriority getPriority() {
		return priority;
	}

	public void setPriority(UpgradeTaskPriority priority) {
		this.priority = priority;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	@Override
	public String toString() {
		return "UpgradeTask [id=" + id + ", uuid=" + uuid + ", identifier=" + identifier + ", taskGroup=" + taskGroup
				+ ", parentUuid=" + parentUuid + ", parentIdentifier=" + parentIdentifier + ", taskOrder=" + taskOrder
				+ ", status=" + status + ", priority=" + priority + ", creationDate=" + creationDate
				+ ", modificationDate=" + modificationDate + ", extras=" + extras + ", asyncTaskUuid=" + asyncTaskUuid
				+ ", upgradeAsyncTask=" + upgradeAsyncTask + ", hidden=" + hidden + "]";
	}

}
