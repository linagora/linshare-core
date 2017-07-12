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
package org.linagora.linshare.mongo.entities;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.linagora.linshare.core.domain.constants.UpgradeLogCriticity;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
