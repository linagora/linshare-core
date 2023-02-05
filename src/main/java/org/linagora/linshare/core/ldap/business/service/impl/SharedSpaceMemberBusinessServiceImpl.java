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
package org.linagora.linshare.core.ldap.business.service.impl;

import java.util.Date;

import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.ldap.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.mongo.entities.SharedSpaceLDAPGroupMember;
import org.linagora.linshare.mongo.repository.SharedSpaceMemberMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceNodeMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceRoleMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;

public class SharedSpaceMemberBusinessServiceImpl extends org.linagora.linshare.core.business.service.impl.SharedSpaceMemberBusinessServiceImpl
		implements SharedSpaceMemberBusinessService {

	public SharedSpaceMemberBusinessServiceImpl(
			SharedSpaceMemberMongoRepository repository,
			SharedSpaceRoleMongoRepository roleRepository,
			SharedSpaceNodeMongoRepository nodeRepository,
			UserRepository<User> userRepository,
			MongoTemplate mongoTemplate
	) {
		super(repository, roleRepository, nodeRepository, userRepository, mongoTemplate);
	}

	@Override
	public SharedSpaceLDAPGroupMember create(SharedSpaceLDAPGroupMember member) {
		return (SharedSpaceLDAPGroupMember) super.create(member);
	}

	@Override
	public SharedSpaceLDAPGroupMember update(SharedSpaceLDAPGroupMember member) {
		SharedSpaceLDAPGroupMember found = (SharedSpaceLDAPGroupMember) findByAccountAndNode(
				member.getAccount().getUuid(), member.getNode().getUuid());
		found.setRole(member.getRole());
		found.setSyncDate(member.getSyncDate());
		found.setModificationDate(new Date());
		return (SharedSpaceLDAPGroupMember) repository.save(member);
	}

}
