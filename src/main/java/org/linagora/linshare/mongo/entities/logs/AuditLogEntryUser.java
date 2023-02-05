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
		this.addRelatedDomains(authUser.getDomain().getUuid());
		if (actor.getDomain() != null) {
			this.addRelatedDomains(actor.getDomain().getUuid());
		}
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

	public void addRelatedAccounts(Set<String> relatedAccounts) {
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
