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

import java.util.Set;

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;

public class WorkGroup extends Account {

	protected String name;

	protected Boolean toUpgrade;

	protected Set<WorkgroupMember> myMembers = new java.util.HashSet<WorkgroupMember>();

	public WorkGroup() {
		super();
	}

	public WorkGroup(AbstractDomain domain, Account owner, String name) {
		super();
		this.setName(name);
		this.domain = domain;
		this.enable = true;
		this.toUpgrade = false;
		this.destroyed = 0;
		this.cmisLocale=owner.cmisLocale;
		this.mailLocale = owner.mailLocale;
		this.externalMailLocale = owner.externalMailLocale;
	}

	public WorkGroup(SharedSpaceNode sharedSpaceNode) {
		super();
		this.name = sharedSpaceNode.getName();
		this.lsUuid = sharedSpaceNode.getUuid();
	}

	@Override
	public AccountType getAccountType() {
		return AccountType.THREAD;
	}

	public Set<WorkgroupMember> getMyMembers() {
		return myMembers;
	}

	public void setMyMembers(Set<WorkgroupMember> myMembers) {
		this.myMembers = myMembers;
	}

	@Override
	public String getAccountRepresentation() {
		return "Workgroup : " + name + " (" + lsUuid + ")";
	}

	@Override
	public ContainerQuotaType getContainerQuotaType() {
		return ContainerQuotaType.WORK_GROUP;
	}

	@Override
	public String getFullName() {
		return this.getName();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getToUpgrade() {
		return toUpgrade;
	}

	public void setToUpgrade(Boolean toUpgrade) {
		this.toUpgrade = toUpgrade;
	}
}
