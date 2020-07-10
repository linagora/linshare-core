/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2017-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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

	@Override
	public String toString() {
		return "UpgradeTask [id=" + id + ", uuid=" + uuid + ", identifier=" + identifier + ", taskGroup=" + taskGroup
				+ ", parentUuid=" + parentUuid + ", parentIdentifier=" + parentIdentifier + ", taskOrder=" + taskOrder
				+ ", status=" + status + ", priority=" + priority + ", creationDate=" + creationDate
				+ ", modificationDate=" + modificationDate + ", extras=" + extras + ", asyncTaskUuid=" + asyncTaskUuid
				+ ", upgradeAsyncTask=" + upgradeAsyncTask + "]";
	}

}
