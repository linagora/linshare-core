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

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.mongo.entities.light.LightSharedSpaceRole;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;

@XmlRootElement(name = "SharedSpaceMemberWorkSpace")
public class SharedSpaceMemberDrive extends SharedSpaceMember {

	protected LightSharedSpaceRole nestedRole;

	public SharedSpaceMemberDrive() {
		super();
		this.type = NodeType.WORK_SPACE;
	}

	public SharedSpaceMemberDrive(SharedSpaceNodeNested node, LightSharedSpaceRole role, SharedSpaceAccount account,
			LightSharedSpaceRole nestedRole) {
		super(node, role, account);
		this.nestedRole = nestedRole;
		this.type = NodeType.WORK_SPACE;
	}

	public SharedSpaceMemberDrive(SharedSpaceMember member) {
		super(member.getNode(), member.getRole(), member.getAccount());
		this.type = NodeType.WORK_SPACE;
	}

	public LightSharedSpaceRole getNestedRole() {
		return nestedRole;
	}

	public void setNestedRole(LightSharedSpaceRole nestedRole) {
		this.nestedRole = nestedRole;
	}
}
