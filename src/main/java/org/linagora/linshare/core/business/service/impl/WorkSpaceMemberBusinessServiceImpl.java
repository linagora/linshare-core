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
package org.linagora.linshare.core.business.service.impl;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.WorkSpaceMemberBusinessService;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberDrive;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.LightSharedSpaceRole;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.repository.SharedSpaceMemberMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceNodeMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceRoleMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;

public class WorkSpaceMemberBusinessServiceImpl extends SharedSpaceMemberBusinessServiceImpl
		implements WorkSpaceMemberBusinessService {

	public WorkSpaceMemberBusinessServiceImpl(SharedSpaceMemberMongoRepository sharedSpaceMemberMongoRepository,
			SharedSpaceRoleMongoRepository roleRepository,
			SharedSpaceNodeMongoRepository nodeRepository,
			UserRepository<User> userRepository,
			MongoTemplate mongoTemplate) {
		super(sharedSpaceMemberMongoRepository, roleRepository, nodeRepository, userRepository, mongoTemplate);
	}

	@Override
	public SharedSpaceMemberDrive create(SharedSpaceAccount account, SharedSpaceNode node, SharedSpaceRole role,
			SharedSpaceRole nestedRole) {
		checkRole(nestedRole.getUuid());
		SharedSpaceMemberDrive member = new SharedSpaceMemberDrive(new SharedSpaceNodeNested(node),
				new LightSharedSpaceRole(role), account,
				new LightSharedSpaceRole(nestedRole));
		member.setNestedRole(new LightSharedSpaceRole(nestedRole));
		return (SharedSpaceMemberDrive) super.create(member);
	}

	@Override
	public SharedSpaceMemberDrive update(SharedSpaceMemberDrive foundMemberToUpdate, SharedSpaceMemberDrive memberToUpdate) {
		Validate.notNull(memberToUpdate.getNestedRole(), "The workSpace role must be set.");
		LightSharedSpaceRole nestedRole = new LightSharedSpaceRole(checkRole(memberToUpdate.getNestedRole().getUuid()));
		foundMemberToUpdate.setNestedRole(nestedRole);
		return (SharedSpaceMemberDrive) super.update(foundMemberToUpdate, memberToUpdate);
	}

}
