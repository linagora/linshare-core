/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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
package org.linagora.linshare.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.constants.SharedSpaceActionType;
import org.linagora.linshare.core.domain.constants.SharedSpaceResourceType;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.service.InitMongoService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.SharedSpaceAuthor;
import org.linagora.linshare.mongo.entities.SharedSpacePermission;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;
import org.linagora.linshare.mongo.repository.SharedSpacePermissionMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceRoleMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class InitMongoServiceImpl implements InitMongoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(InitMongoServiceImpl.class);

	private static final String WORK_GROUP_ADMIN_UUID = "234be74d-2966-41c1-9dee-e47c8c63c14e";
	private static final String WORK_GROUP_CONTRIBUTOR_UUID = "b206c2ba-37de-491e-8e9c-88ed3be70682";
	private static final String WORK_GROUP_WRITER_UUID = "8839654d-cb33-4633-bf3f-f9e805f97f84";
	private static final String WORK_GROUP_READER_UUID = "4ccbed61-71da-42a0-a513-92211953ac95";

	private static final String WORK_SPACE_ADMIN_UUID = "9e73e962-c233-4b4a-be1c-e8d9547acbdf";
	private static final String WORK_SPACE_WRITER_UUID = "963025ca-8220-4915-b4fc-dba7b0b56100";
	private static final String WORK_SPACE_READER_UUID = "556404b5-09b0-413e-a025-79ee40e043e4";

	private static final List<String> DRIVE_RENAMING_OLD_VALUES = ImmutableList.of("DRIVE_ADMIN", "DRIVE_WRITER", "DRIVE_READER");

	private final UserService userService;

	private final SharedSpaceRoleMongoRepository roleMongoRepository;

	private final SharedSpacePermissionMongoRepository permissionMongoRepository;

	public InitMongoServiceImpl(UserService userService,
			SharedSpaceRoleMongoRepository roleMongoRepository,
			SharedSpacePermissionMongoRepository permissionMongoRepository) {
		super();
		this.userService = userService;
		this.roleMongoRepository = roleMongoRepository;
		this.permissionMongoRepository = permissionMongoRepository;
	}

	/**
	 *  This methods allows to insert the default shared space roles in Database
	 * @param roleUuid String static uuid affected to the role
	 * @param roleName String static name affected to the role
	 * @param type {@link NodeType} define the kind of shared space role (WORK_SPACE, Work_GROUP)
	 * @param domain {@link GenericLightEntity} contains LinShare domain minimal informations
	 * @param author {@link SharedSpaceAuthor} The user that create the role
	 * @return role
	 *
	 */
	@VisibleForTesting
	SharedSpaceRole upsertInitRole(String roleUuid, String roleName, GenericLightEntity domain, NodeType type, SharedSpaceAuthor author) {
		SharedSpaceRole role = roleMongoRepository.findByUuid(roleUuid);
		if (role == null) {
			return createInitRole(roleUuid, roleName, domain, type, author);
		}
		// Migrate old role name from DRIVE_* to WORK_SPACE_*
		if (DRIVE_RENAMING_OLD_VALUES.contains(role.getName())) {
			role.setName(roleName);
			role.setModificationDate(new Date());
			roleMongoRepository.save(role);
		}
		if (role.getType() == null) {
			role.setType(type);
			role.setModificationDate(new Date());
			roleMongoRepository.save(role);
		}
		return role;
	}

	private SharedSpaceRole createInitRole(String roleUuid, String roleName, GenericLightEntity domain, NodeType type, SharedSpaceAuthor author) {
		SharedSpaceRole role = new SharedSpaceRole();
		role.setUuid(roleUuid);
		role.setName(roleName);
		role.setEnabled(true);
		role.setDomain(domain);
		role.setAuthor(author);
		role.setType(type);
		role.setModificationDate(new Date());
		role.setCreationDate(new Date());
		roleMongoRepository.insert(role);
		return role;
	}

	/**
	 * Create light object that contains minimal information of shared space role
	 * @param roleUuid
	 * @return {@link GenericLightEntity} light role
	 */
	private GenericLightEntity createInitLightRole(String roleUuid) {
		SharedSpaceRole role = roleMongoRepository.findByUuid(roleUuid);
		GenericLightEntity roleLight = new GenericLightEntity(role.getUuid(), role.getName());
		return roleLight;
	}

	/**
	 * This method allows to insert default shared spaces permissions in Database
	 * @param permissionUuid static uuid of the permission
	 * @param actionType allowed action type of the permission
	 * @param resourceType {@link SharedSpaceResourceType} type of the shared space 
	 * @param roles {@link GenericLightEntity} contains minimal inforamtions
	 *  of the shared space role (it can be single or a list)
	 * @return
	 */
	@VisibleForTesting
	SharedSpacePermission upsertInitPermission(String permissionUuid, SharedSpaceActionType actionType, SharedSpaceResourceType resourceType, GenericLightEntity... roles) {
		SharedSpacePermission permission = permissionMongoRepository.findByUuid(permissionUuid);
		if (permission == null) {
			return createInitPermission(permissionUuid, actionType, resourceType, roles);
		}
		if (rolesHasChanged(permission.getRoles(), roles)) {
			permission.setRoles(Lists.newArrayList(roles));
			permission.setModificationDate(new Date());
			permission = permissionMongoRepository.save(permission);
		}
		return permission;
	}

	private boolean rolesHasChanged(List<GenericLightEntity> currentRoles, GenericLightEntity[] newRoles) {
		List<String> currentRoleUuids = currentRoles
			.stream()
			.map(GenericLightEntity::getUuid)
			.collect(Collectors.toUnmodifiableList());
		List<String> newRoleUuids = Lists.newArrayList(newRoles)
			.stream()
			.map(GenericLightEntity::getUuid)
			.collect(Collectors.toUnmodifiableList());
		return !CollectionUtils.isEqualCollection(currentRoleUuids, newRoleUuids);
	}

	private SharedSpacePermission createInitPermission(String permissionUuid, SharedSpaceActionType actionType, SharedSpaceResourceType resourceType, GenericLightEntity... roles) {
		SharedSpacePermission permission = new SharedSpacePermission();
		permission.setUuid(permissionUuid);
		permission.setAction(actionType);
		permission.setResource(resourceType);
		permission.setCreationDate(new Date());
		permission.setRoles(Lists.newArrayList(roles));
		permission.setModificationDate(new Date());
		permissionMongoRepository.insert(permission);
		return permission;
	}

	@Override
	public void init() {
		LOGGER.info("BEGIN -- Initialization with default shared space roles and permissions.");
		User root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
		GenericLightEntity rootDomain = new GenericLightEntity(LinShareConstants.rootDomainIdentifier,
				LinShareConstants.rootDomainIdentifier);
		SharedSpaceAuthor rootAccount = new SharedSpaceAuthor(root.getLsUuid(), root.getFullName());

		upsertInitRole(WORK_GROUP_ADMIN_UUID, "ADMIN", rootDomain, NodeType.WORK_GROUP, rootAccount);
		upsertInitRole(WORK_GROUP_CONTRIBUTOR_UUID, "CONTRIBUTOR", rootDomain, NodeType.WORK_GROUP, rootAccount);
		upsertInitRole(WORK_GROUP_WRITER_UUID, "WRITER", rootDomain, NodeType.WORK_GROUP, rootAccount);
		upsertInitRole(WORK_GROUP_READER_UUID, "READER", rootDomain, NodeType.WORK_GROUP, rootAccount);

		GenericLightEntity admin = createInitLightRole(WORK_GROUP_ADMIN_UUID);
		GenericLightEntity contributor = createInitLightRole(WORK_GROUP_CONTRIBUTOR_UUID);
		GenericLightEntity writer = createInitLightRole(WORK_GROUP_WRITER_UUID);
		GenericLightEntity reader = createInitLightRole(WORK_GROUP_READER_UUID);

		upsertInitRole(WORK_SPACE_ADMIN_UUID, "WORK_SPACE_ADMIN", rootDomain, NodeType.WORK_SPACE, rootAccount);
		upsertInitRole(WORK_SPACE_WRITER_UUID, "WORK_SPACE_WRITER", rootDomain, NodeType.WORK_SPACE, rootAccount);
		upsertInitRole(WORK_SPACE_READER_UUID, "WORK_SPACE_READER", rootDomain, NodeType.WORK_SPACE, rootAccount);

		GenericLightEntity workSpaceAdmin = createInitLightRole(WORK_SPACE_ADMIN_UUID);
		GenericLightEntity workSpaceWriter = createInitLightRole(WORK_SPACE_WRITER_UUID);
		GenericLightEntity workSpaceReader = createInitLightRole(WORK_SPACE_READER_UUID);

		upsertInitPermission("31cb4d80-c939-40f1-a79e-4d77392e0e0b", SharedSpaceActionType.CREATE, SharedSpaceResourceType.WORK_SPACE, workSpaceAdmin);
		upsertInitPermission("e432acbb-d72e-4e20-b255-6f1cb7329bbd", SharedSpaceActionType.READ, SharedSpaceResourceType.WORK_SPACE, workSpaceAdmin, workSpaceWriter, workSpaceReader);
		upsertInitPermission("5557fc26-ea2d-4e3b-81af-37a614d8014c", SharedSpaceActionType.UPDATE, SharedSpaceResourceType.WORK_SPACE, workSpaceAdmin);
		upsertInitPermission("70ecfe55-f388-4e37-91bc-958386e0a865", SharedSpaceActionType.DELETE, SharedSpaceResourceType.WORK_SPACE, workSpaceAdmin);

		upsertInitPermission("0457baaf-fd9e-4737-90d9-5a802caf9ff5", SharedSpaceActionType.CREATE, SharedSpaceResourceType.FOLDER, admin, writer, contributor);
		upsertInitPermission("18a76d34-e19f-45d4-864c-4bb8cadda711", SharedSpaceActionType.READ, SharedSpaceResourceType.FOLDER, admin, writer, contributor, reader);
		upsertInitPermission("9dedd90c-709b-4c72-a70f-17f8c65f4f2f", SharedSpaceActionType.UPDATE, SharedSpaceResourceType.FOLDER, admin, writer, contributor);
		upsertInitPermission("dd80afd5-9415-424d-b211-63669934efda", SharedSpaceActionType.DOWNLOAD, SharedSpaceResourceType.FOLDER, admin, writer, contributor, reader);
		upsertInitPermission("fbe86462-174a-4d14-b6f1-ca4c6e127142", SharedSpaceActionType.DELETE, SharedSpaceResourceType.FOLDER, admin, writer, workSpaceAdmin);
		upsertInitPermission("3f92f534-44a1-4a78-9be0-368898d61473", SharedSpaceActionType.CREATE, SharedSpaceResourceType.FILE, admin, writer, contributor);
		upsertInitPermission("05e3372f-a78f-490c-9b48-d64dffd231b5", SharedSpaceActionType.READ, SharedSpaceResourceType.FILE, admin, writer, contributor, reader);
		upsertInitPermission("816b30ae-13ed-46a4-9284-fcaa65fc9e84", SharedSpaceActionType.UPDATE, SharedSpaceResourceType.FILE, admin, writer, contributor);
		upsertInitPermission("ea49ea0e-c14e-4f10-95bf-5dae8d01ab91", SharedSpaceActionType.DELETE, SharedSpaceResourceType.FILE, admin, writer, workSpaceAdmin);
		upsertInitPermission("fd95b249-d142-47b4-9d17-3bb039e58f1a", SharedSpaceActionType.DOWNLOAD, SharedSpaceResourceType.FILE, admin, writer, contributor, reader);
		upsertInitPermission("268d7402-91da-4cb9-9a0c-396d0e21c04f", SharedSpaceActionType.DOWNLOAD_THUMBNAIL, SharedSpaceResourceType.FILE, admin, writer, contributor, reader);
		upsertInitPermission("f597e8f2-1c3b-4285-a909-62f47528de1e", SharedSpaceActionType.CREATE, SharedSpaceResourceType.MEMBER, admin, workSpaceAdmin);
		upsertInitPermission("6b3e52d5-5fa5-4a72-bf62-fc15896b1cfc", SharedSpaceActionType.READ, SharedSpaceResourceType.MEMBER, admin, writer, contributor, reader, workSpaceAdmin, workSpaceWriter, workSpaceReader);
		upsertInitPermission("0f1d6446-d37d-4bc6-a2ed-c391b6866527", SharedSpaceActionType.UPDATE, SharedSpaceResourceType.MEMBER, admin, workSpaceAdmin);
		upsertInitPermission("4b29d1f9-dec7-484c-a170-a051e7d9b848", SharedSpaceActionType.DELETE, SharedSpaceResourceType.MEMBER, admin, workSpaceAdmin);
		upsertInitPermission("08a77038-95d0-46be-93de-a602e0315d6e", SharedSpaceActionType.CREATE, SharedSpaceResourceType.WORK_GROUP, admin, workSpaceAdmin, workSpaceWriter);
		upsertInitPermission("ce73fa89-04aa-41f2-a94f-cf09b46f810b", SharedSpaceActionType.READ, SharedSpaceResourceType.WORK_GROUP, admin, writer, contributor, reader);
		upsertInitPermission("881dfa55-90c5-460a-9ac2-a38181fd2349", SharedSpaceActionType.UPDATE, SharedSpaceResourceType.WORK_GROUP, admin);
		upsertInitPermission("efd0d533-cb5b-4bf6-a717-81f28ae0a1fe", SharedSpaceActionType.DELETE, SharedSpaceResourceType.WORK_GROUP, admin);
		LOGGER.info("END -- Initialization with default shared space roles and permissions.");
	}

}
