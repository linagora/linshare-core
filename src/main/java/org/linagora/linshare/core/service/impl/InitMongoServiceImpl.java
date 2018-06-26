/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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
package org.linagora.linshare.core.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.SharedSpaceActionType;
import org.linagora.linshare.core.domain.constants.SharedSpaceResourceType;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.service.InitMongoService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.SharedSpaceAuthor;
import org.linagora.linshare.mongo.entities.SharedSpaceDomain;
import org.linagora.linshare.mongo.entities.SharedSpacePermission;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.repository.SharedSpacePermissionMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceRoleMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitMongoServiceImpl implements InitMongoService {

	private final static Logger logger = LoggerFactory.getLogger(InitMongoServiceImpl.class);

	protected final UserService userService;

	protected final SharedSpaceRoleMongoRepository sharedSpaceRoleMongoRepository;

	protected final SharedSpacePermissionMongoRepository sharedSpacePermissionMongoRepository;

	public InitMongoServiceImpl(UserService userService, SharedSpaceRoleMongoRepository sharedSpaceRoleMongoRepository,
			SharedSpacePermissionMongoRepository sharedSpacePermissionMongoRepository) {
		super();
		this.userService = userService;
		this.sharedSpaceRoleMongoRepository = sharedSpaceRoleMongoRepository;
		this.sharedSpacePermissionMongoRepository = sharedSpacePermissionMongoRepository;

	}

	private SharedSpaceRole createInitRole(String roleUuid, String roleName, SharedSpaceDomain domain,
			SharedSpaceAuthor author) {
		SharedSpaceRole role = sharedSpaceRoleMongoRepository.findByUuid(roleUuid);
		if (role == null) {
			role = new SharedSpaceRole();
			role.setUuid(roleUuid);
			role.setName(roleName);
			role.setEnabled(true);
			role.setSharedSpaceDomain(domain);
			role.setSharedSpaceAuthor(author);
			role.setModificationDate(new Date());
			role.setCreationDate(new Date());
			sharedSpaceRoleMongoRepository.insert(role);
		}
		return role;
	}

	private SharedSpacePermission createInitPermission(String permissionUuid, String permissionName,
			SharedSpaceActionType actionType, SharedSpaceResourceType resourceType,
			List<SharedSpaceRole> sharedSpaceRoles) {
		SharedSpacePermission permission = sharedSpacePermissionMongoRepository.findByUuid(permissionUuid);
		if (permission == null) {
			permission = new SharedSpacePermission();
			permission.setUuid(permissionUuid);
			permission.setAction(actionType);
			permission.setResource(resourceType);
			permission.setSharedSpaceRole(sharedSpaceRoles);
			permission.setCreationDate(new Date());
			permission.setModificationDate(new Date());
			sharedSpacePermissionMongoRepository.insert(permission);
		}
		return permission;
	}

	@Override
	public void init() {
		logger.info("Initialization");
		User root = userService.findByLsUuid("root@localhost.localdomain");
		SharedSpaceDomain rootDomain = new SharedSpaceDomain(LinShareConstants.rootDomainIdentifier,
				LinShareConstants.rootDomainIdentifier);
		SharedSpaceAuthor rootAccount = new SharedSpaceAuthor(root.getFullName(), root.getMail());
		SharedSpaceRole admin = createInitRole("77a699fe-faca-46a5-97c0-1b46a0f5cd05", "ADMIN", rootDomain,
				rootAccount);
		SharedSpaceRole contributor = createInitRole("77a699fe-faca-46a5-97c0-1b46a0f5cd19", "CONTRIBUTOR", rootDomain,
				rootAccount);
		SharedSpaceRole writer = createInitRole("77a699fe-faca-46a5-97c0-1b46a0f5cd14", "WRITER", rootDomain,
				rootAccount);
		SharedSpaceRole reader = createInitRole("77a699fe-faca-46a5-97c0-1b46a0f5cd13", "READER", rootDomain,
				rootAccount);
		List<SharedSpaceRole> roles = new ArrayList<SharedSpaceRole>();
		List<SharedSpaceRole> roleAdmin = Collections.singletonList(admin);
		roles.add(admin);
		roles.add(writer);
		roles.add(contributor);
		roles.add(reader);
		SharedSpacePermission createDrive = createInitPermission("77a699fe-faca-46a5-97c0-1b46a0452d05",
				"Create a drive ", SharedSpaceActionType.CREATE, SharedSpaceResourceType.DRIVE, roleAdmin);
		SharedSpacePermission readDrive = createInitPermission("77a699fe-faca-46a5-97c0-1b46a074dd05", "read a drive",
				SharedSpaceActionType.READ, SharedSpaceResourceType.DRIVE, roles);
		SharedSpacePermission updateDrive = createInitPermission("77a699fe-faca-46a5-97c0-1b461dm23d05",
				"Update  a drive", SharedSpaceActionType.UPDATE, SharedSpaceResourceType.DRIVE, roles.subList(0, 3));
		SharedSpacePermission deleteDrive = createInitPermission("77a699fe-faca-46a5-97c0-1b46akq23d05",
				"Delete  a drive", SharedSpaceActionType.DELETE, SharedSpaceResourceType.DRIVE, roleAdmin);
		SharedSpacePermission createFolder = createInitPermission("77a6d9fe-41ca-46a5-97c0-1b46a0452d05",
				"Create a folder", SharedSpaceActionType.CREATE, SharedSpaceResourceType.FOLDER, roles.subList(0, 3));
		SharedSpacePermission readFolder = createInitPermission("77a699fe-fKLa-46a5-97c0-1jq6a074dd05", "Read a folder",
				SharedSpaceActionType.READ, SharedSpaceResourceType.FOLDER, roles);
		SharedSpacePermission updateFolder = createInitPermission("77a699fe-fmla-46a5-97c0-1b461dm23d05",
				"Update a folder", SharedSpaceActionType.UPDATE, SharedSpaceResourceType.FOLDER, roles.subList(0, 3));
		SharedSpacePermission deleteFolder = createInitPermission("77a699fe-faca-46a5-4ac0-1b44akq23d05",
				"Delete a folder", SharedSpaceActionType.DELETE, SharedSpaceResourceType.FOLDER, roles.subList(0, 2));
		SharedSpacePermission createFile = createInitPermission("77a699fe-faca-46a5-97c0-1b46a2452d05", "Create a file",
				SharedSpaceActionType.CREATE, SharedSpaceResourceType.FILE, roles.subList(0, 3));
		SharedSpacePermission readFile = createInitPermission("77a699fe-faca-46a5-97c0-1b46a073dd05", "Read a file",
				SharedSpaceActionType.READ, SharedSpaceResourceType.FILE, roles);
		SharedSpacePermission updateFile = createInitPermission("77a699fe-faca-46a5-97c0-1b461gm23d05", "Update a file",
				SharedSpaceActionType.UPDATE, SharedSpaceResourceType.FILE, roles.subList(0, 3));
		SharedSpacePermission deleteFile = createInitPermission("77a699fe-faca-46a5-97c0-1b46amq23d05", "Delete a file",
				SharedSpaceActionType.DELETE, SharedSpaceResourceType.FILE, roles.subList(0, 2));
		SharedSpacePermission createMember = createInitPermission("77a6d9fe-faca-46a5-97c0-1b40a0452d05",
				"Create a member", SharedSpaceActionType.CREATE, SharedSpaceResourceType.MEMBER, roleAdmin);
		SharedSpacePermission readMember = createInitPermission("77a6d9fe-faca-46a5-97c0-1b15a0452d05", "Read a member",
				SharedSpaceActionType.READ, SharedSpaceResourceType.MEMBER, roles.subList(0, 3));
		SharedSpacePermission updateMember = createInitPermission("77a699fe-fmla-46a5-97c0-1b441dm23d05",
				"Update a member", SharedSpaceActionType.UPDATE, SharedSpaceResourceType.MEMBER, roleAdmin);
		SharedSpacePermission deleteMember = createInitPermission("77a699fe-faca-46a5-4ac0-1b47akq23d05",
				"Delete a member", SharedSpaceActionType.DELETE, SharedSpaceResourceType.MEMBER, roleAdmin);
		SharedSpacePermission createworkGroup = createInitPermission("77a659fe-faca-46a5-4ac0-3b46akq23d05",
				"Create a workgroup", SharedSpaceActionType.CREATE, SharedSpaceResourceType.WORKGROUP, roleAdmin);
		SharedSpacePermission readWorkGroup = createInitPermission("77a699fe-faca-46a5-67c0-1b46a074dd05",
				"Read a workgroup", SharedSpaceActionType.READ, SharedSpaceResourceType.WORKGROUP, roles);
		SharedSpacePermission updateWorkGroup = createInitPermission("77a699fe-faca-46a5-87c0-1b461dm23d05",
				"Update a workgroup", SharedSpaceActionType.UPDATE, SharedSpaceResourceType.WORKGROUP,
				roles.subList(0, 3));
		SharedSpacePermission deleteWorkGroup = createInitPermission("77a699fe-faca-46a2-97c0-1b46akq23d05",
				"Delete a workgroup", SharedSpaceActionType.DELETE, SharedSpaceResourceType.WORKGROUP, roleAdmin);
	}

}
