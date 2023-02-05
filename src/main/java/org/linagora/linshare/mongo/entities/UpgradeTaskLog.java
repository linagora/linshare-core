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
package org.linagora.linshare.mongo.entities;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.linagora.linshare.core.domain.constants.UpgradeLogCriticity;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlRootElement(name = "UpgradeTaskLog")
@Document(collection = "upgrade_task_log")
public class UpgradeTaskLog {

	@JsonIgnore
	@Id
	@GeneratedValue
	protected String id;

	protected String message;

	protected UpgradeLogCriticity criticity;

	protected String asyncTask;

	@JsonIgnore
	protected String actor;

	protected String upgradeTask;

	protected Date creationDate;

	public UpgradeTaskLog(
			BatchRunContext batchRunContext,
			UpgradeLogCriticity criticity,
			String message) {
		super();
		this.message = message;
		this.criticity = criticity;
		this.asyncTask = batchRunContext.getAsyncTaskUuid();
		this.upgradeTask = batchRunContext.getUpgradeTaskUuid();
		this.actor = batchRunContext.getActorUuid();
		this.creationDate = new Date();
	}

	public UpgradeTaskLog() {
		super();
	}

	@XmlTransient
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public UpgradeLogCriticity getCriticity() {
		return criticity;
	}

	public void setCriticity(UpgradeLogCriticity criticity) {
		this.criticity = criticity;
	}

	public String getAsyncTask() {
		return asyncTask;
	}

	public void setAsyncTask(String asyncTaskUuid) {
		this.asyncTask = asyncTaskUuid;
	}

	public String getUpgradeTask() {
		return upgradeTask;
	}

	public void setUpgradeTask(String upgradeTaskUuid) {
		this.upgradeTask = upgradeTaskUuid;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@XmlTransient
	public String getActor() {
		return actor;
	}

	public void setActor(String actorUuid) {
		this.actor = actorUuid;
	}

}
