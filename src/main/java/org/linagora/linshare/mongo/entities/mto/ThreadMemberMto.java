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
package org.linagora.linshare.mongo.entities.mto;

import org.linagora.linshare.core.domain.entities.WorkgroupMember;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupLightDto;

@Deprecated(forRemoval = false)
//DEPRECATED: We keep this object in order to be able to read old data still store in the database.
public class ThreadMemberMto {

	protected WorkGroupLightDto workGroup;

	protected AccountMto user;

	protected boolean canUpload;

	protected boolean admin;

	public ThreadMemberMto() {
	}

	public ThreadMemberMto(WorkgroupMember member) {
		this.workGroup = new WorkGroupLightDto(member.getThread());
		this.user = new AccountMto(member.getUser(), true);
		this.canUpload = member.getCanUpload();
		this.admin = member.getAdmin();
	}

	public WorkGroupLightDto getWorkGroup() {
		return workGroup;
	}

	public void setWorkGroup(WorkGroupLightDto workGroup) {
		this.workGroup = workGroup;
	}

	public AccountMto getUser() {
		return user;
	}

	public void setUser(AccountMto user) {
		this.user = user;
	}

	public boolean isCanUpload() {
		return canUpload;
	}

	public void setCanUpload(boolean canUpload) {
		this.canUpload = canUpload;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

}
