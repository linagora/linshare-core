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

import java.util.List;

import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.entities.WorkgroupMember;

import com.google.common.collect.Lists;

@Deprecated(forRemoval = false)
// DEPRECATED: We keep this object in order to be able to read old data still store in the database.
public class ThreadMto {

	protected String name;

	protected String uuid;

	private List<ThreadMemberMto> members;

	public ThreadMto() {
	}

	public ThreadMto(WorkGroup thread, boolean light) {
		this.setName(thread.getName());
		this.setUuid(thread.getLsUuid());
		if (!light) {
			this.members = Lists.newArrayList();
			for (WorkgroupMember m : thread.getMyMembers()) {
				members.add(new ThreadMemberMto(m));
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public List<ThreadMemberMto> getMembers() {
		return members;
	}

	public void setMembers(List<ThreadMemberMto> members) {
		this.members = members;
	}
}
