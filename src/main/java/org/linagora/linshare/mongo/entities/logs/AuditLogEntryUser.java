/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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

package org.linagora.linshare.mongo.entities.logs;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.mongo.entities.mto.AccountMto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@JsonIgnoreProperties({"relatedAccounts"})
@XmlRootElement(name = "AuditLogEntryUser")
@XmlTransient
public abstract class AuditLogEntryUser extends AuditLogEntry {

	protected AccountMto actor;

	@JsonIgnore
	protected Set<String> relatedAccounts;

	@JsonIgnore
	protected Set<String> relatedResources;

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
		this.addRelatedDomains(actor.getDomain().getUuid());
	}

	protected void initRelatedAccountField() {
		this.relatedAccounts = Sets.newHashSet();
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
	public Set<String> getRelatedAccounts() {
		return relatedAccounts;
	}

	public void addRelatedAccounts(List<String> relatedAccounts) {
		if (this.relatedAccounts == null) {
			this.relatedAccounts = Sets.newHashSet();
		}
		this.relatedAccounts.addAll(relatedAccounts);
	}

	public void addRelatedAccounts(String... relatedAccounts) {
		if (this.relatedAccounts == null) {
			this.relatedAccounts = Sets.newHashSet();
		}
		this.relatedAccounts.addAll(Lists.newArrayList(relatedAccounts));
	}

	@XmlTransient
	public Set<String> getRelatedResources() {
		return relatedResources;
	}

	public void addRelatedResources(List<String> relatedResources) {
		if (this.relatedResources == null) {
			this.relatedResources = Sets.newHashSet();
		}
		this.relatedResources.addAll(relatedResources);
	}

	public void addRelatedResources(String... relatedResources) {
		if (this.relatedResources == null) {
			this.relatedResources = Sets.newHashSet();
		}
		this.relatedResources.addAll(Lists.newArrayList(relatedResources));
	}

}
