/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
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

package org.linagora.linshare.mongo.entities.logs;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.mongo.entities.mto.AccountMto;

import com.google.common.collect.Lists;

@JsonIgnoreProperties({"relatedAccounts"})
@XmlRootElement(name = "AuditLogEntryUser")
public abstract class AuditLogEntryUser extends AuditLogEntry {

	protected AccountMto actor;

	@JsonIgnore
	protected List<String> relatedAccounts;

	@JsonIgnore
	protected List<String> relatedResources;

	public AuditLogEntryUser() {
		super();
	}

	public AuditLogEntryUser(AccountMto authUser, AccountMto actor, LogAction action, AuditLogEntryType type,
			String resourceUuid) {
		super();
		this.authUser = authUser;
		this.actor = actor;
		this.action = action;
		this.creationDate = new Date();
		this.type = type;
		this.resourceUuid = resourceUuid;
		initRelatedAccountField();
	}

	public AuditLogEntryUser(ShareEntryAuditLogEntry log) {
		super();
		this.authUser = log.getAuthUser();
		this.actor = log.getActor();
		this.action = log.getAction();
		this.creationDate = log.getCreationDate();
		this.type = log.getType();
		this.resourceUuid = log.getResourceUuid();
		initRelatedAccountField();
	}

	public AuditLogEntryUser(ThreadAuditLogEntry log) {
		super();
		this.authUser = log.getAuthUser();
		this.actor = log.getActor();
		this.action = log.getAction();
		this.creationDate = log.getCreationDate();
		this.type = log.getType();
		this.resourceUuid = log.getResourceUuid();
		initRelatedAccountField();
	}

	protected void initRelatedAccountField() {
		this.relatedAccounts = Lists.newArrayList();
		String authUserUuid = authUser.getUuid();
		String actorUuid = actor.getUuid();
		this.relatedAccounts.add(authUserUuid);
		if (actorUuid != null) {
			if (!authUserUuid.equals(actorUuid)) {
				this.relatedAccounts.add(actorUuid);
			}
		}
	}

	public AccountMto getActor() {
		return actor;
	}

	public void setActor(AccountMto actor) {
		this.actor = actor;
	}

	@XmlTransient
	public List<String> getRelatedAccounts() {
		return relatedAccounts;
	}

	public void setRelatedAccounts(List<String> relatedAccounts) {
		this.relatedAccounts = relatedAccounts;
	}

	public void addRelatedAccounts(List<String> relatedAccounts) {
		if (this.relatedAccounts == null) {
			this.relatedAccounts = Lists.newArrayList();
		}
		this.relatedAccounts.addAll(relatedAccounts);
	}

	public void addRelatedAccounts(String... relatedAccounts) {
		if (this.relatedAccounts == null) {
			this.relatedAccounts = Lists.newArrayList();
		}
		this.relatedAccounts.addAll(Lists.newArrayList(relatedAccounts));
	}

	public void setRelatedResources(List<String> relatedResources) {
		this.relatedResources = relatedResources;
	}

	public void addRelatedResources(List<String> relatedResources) {
		if (this.relatedResources == null) {
			this.relatedResources = Lists.newArrayList();
		}
		this.relatedResources.addAll(relatedResources);
	}

	public void addRelatedResources(String... relatedResources) {
		if (this.relatedResources == null) {
			this.relatedResources = Lists.newArrayList();
		}
		this.relatedResources.addAll(Lists.newArrayList(relatedResources));
	}
}
