/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
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
		this.locale = owner.locale;
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
