/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2021 LINAGORA
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

import com.google.common.collect.Lists;

public class InitMongoServiceImpl implements InitMongoService {

	private final static Logger logger = LoggerFactory.getLogger(InitMongoServiceImpl.class);

	protected final UserService userService;

	protected final SharedSpaceRoleMongoRepository roleMongoRepository;

	protected final SharedSpacePermissionMongoRepository permissionMongoRepository;

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
	private SharedSpaceRole createInitRole(String roleUuid, String roleName, GenericLightEntity domain, NodeType type,
			SharedSpaceAuthor author) {
		SharedSpaceRole role = roleMongoRepository.findByUuid(roleUuid);
		if (role == null) {
			role = new SharedSpaceRole();
			role.setUuid(roleUuid);
			role.setName(roleName);
			role.setEnabled(true);
			role.setDomain(domain);
			role.setAuthor(author);
			role.setType(type);
			role.setModificationDate(new Date());
			role.setCreationDate(new Date());
			roleMongoRepository.insert(role);
		} else if (role.getType() == null ){
			role.setType(type);
			roleMongoRepository.save(role);
		}
		return role;
	}
	
	/**
	 * Create light object that contains minimal information of shared space role
	 * @param roleUuid
	 * @param roleName
	 * @return {@link GenericLightEntity} light role
	 */
	private GenericLightEntity createInitLightRole(String roleUuid, String roleName) {
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
	private SharedSpacePermission createInitPermission(String permissionUuid, String permissionName ,SharedSpaceActionType actionType,
			SharedSpaceResourceType resourceType, GenericLightEntity... roles) {
		SharedSpacePermission permission = permissionMongoRepository.findByUuid(permissionUuid);
		if (permission == null) {
			permission = new SharedSpacePermission();
			permission.setUuid(permissionUuid);
			permission.setAction(actionType);
			permission.setResource(resourceType);
			permission.setCreationDate(new Date());
		}
		permission.setRoles(Lists.newArrayList(roles));
		permission.setModificationDate(new Date());
		permission = permissionMongoRepository.save(permission);
		return permission;
	}

	@Override
	public void init() {
		logger.info("BEGIN -- Initialization with default shared space roles and permissions.");
		User root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
		GenericLightEntity rootDomain = new GenericLightEntity(LinShareConstants.rootDomainIdentifier,
				LinShareConstants.rootDomainIdentifier);
		SharedSpaceAuthor rootAccount = new SharedSpaceAuthor(root.getLsUuid(), root.getFullName());

		createInitRole("234be74d-2966-41c1-9dee-e47c8c63c14e", "ADMIN", rootDomain, NodeType.WORK_GROUP, rootAccount);
		createInitRole("b206c2ba-37de-491e-8e9c-88ed3be70682", "CONTRIBUTOR", rootDomain, NodeType.WORK_GROUP,
				rootAccount);
		createInitRole("8839654d-cb33-4633-bf3f-f9e805f97f84", "WRITER", rootDomain, NodeType.WORK_GROUP, rootAccount);
		createInitRole("4ccbed61-71da-42a0-a513-92211953ac95", "READER", rootDomain, NodeType.WORK_GROUP, rootAccount);

		GenericLightEntity admin = createInitLightRole("234be74d-2966-41c1-9dee-e47c8c63c14e", "ADMIN");
		GenericLightEntity contributor = createInitLightRole("b206c2ba-37de-491e-8e9c-88ed3be70682", "CONTRIBUTOR");
		GenericLightEntity writer = createInitLightRole("8839654d-cb33-4633-bf3f-f9e805f97f84", "WRITER");
		GenericLightEntity reader = createInitLightRole("4ccbed61-71da-42a0-a513-92211953ac95", "READER");

		createInitRole("9e73e962-c233-4b4a-be1c-e8d9547acbdf", "DRIVE_ADMIN", rootDomain, NodeType.WORK_SPACE, rootAccount);
		createInitRole("963025ca-8220-4915-b4fc-dba7b0b56100", "DRIVE_WRITER", rootDomain, NodeType.WORK_SPACE, rootAccount);
		createInitRole("556404b5-09b0-413e-a025-79ee40e043e4", "DRIVE_READER", rootDomain, NodeType.WORK_SPACE, rootAccount);

		GenericLightEntity drive_admin = createInitLightRole("9e73e962-c233-4b4a-be1c-e8d9547acbdf", "DRIVE_ADMIN");
		GenericLightEntity drive_writer = createInitLightRole("963025ca-8220-4915-b4fc-dba7b0b56100", "DRIVE_WRITER");
		GenericLightEntity drive_reader = createInitLightRole("556404b5-09b0-413e-a025-79ee40e043e4", "DRIVE_READER");

		createInitPermission("31cb4d80-c939-40f1-a79e-4d77392e0e0b", "Create a drive ", SharedSpaceActionType.CREATE,
				SharedSpaceResourceType.WORK_SPACE, drive_admin);
		createInitPermission("e432acbb-d72e-4e20-b255-6f1cb7329bbd", "read a drive", SharedSpaceActionType.READ,
				SharedSpaceResourceType.WORK_SPACE, drive_admin, drive_writer, drive_reader);
		createInitPermission("5557fc26-ea2d-4e3b-81af-37a614d8014c", "Update  a drive", SharedSpaceActionType.UPDATE,
				SharedSpaceResourceType.WORK_SPACE, drive_admin);
		createInitPermission("70ecfe55-f388-4e37-91bc-958386e0a865", "Delete  a drive", SharedSpaceActionType.DELETE,
				SharedSpaceResourceType.WORK_SPACE, drive_admin);

		createInitPermission("0457baaf-fd9e-4737-90d9-5a802caf9ff5", "Create a folder", SharedSpaceActionType.CREATE,
				SharedSpaceResourceType.FOLDER, admin, writer, contributor);
		createInitPermission("18a76d34-e19f-45d4-864c-4bb8cadda711", "Read a folder", SharedSpaceActionType.READ,
				SharedSpaceResourceType.FOLDER, admin, writer, contributor, reader);
		createInitPermission("9dedd90c-709b-4c72-a70f-17f8c65f4f2f", "Update a folder", SharedSpaceActionType.UPDATE,
				SharedSpaceResourceType.FOLDER, admin, writer, contributor);
		createInitPermission("dd80afd5-9415-424d-b211-63669934efda", "Download a folder", SharedSpaceActionType.DOWNLOAD,
				SharedSpaceResourceType.FOLDER, admin, writer, contributor, reader);
		createInitPermission("fbe86462-174a-4d14-b6f1-ca4c6e127142", "Delete a folder", SharedSpaceActionType.DELETE,
				SharedSpaceResourceType.FOLDER, admin, writer, drive_admin);
		createInitPermission("3f92f534-44a1-4a78-9be0-368898d61473", "Create a file", SharedSpaceActionType.CREATE,
				SharedSpaceResourceType.FILE, admin, writer, contributor);
		createInitPermission("05e3372f-a78f-490c-9b48-d64dffd231b5", "Read a file", SharedSpaceActionType.READ,
				SharedSpaceResourceType.FILE, admin, writer, contributor, reader);
		createInitPermission("816b30ae-13ed-46a4-9284-fcaa65fc9e84", "Update a file", SharedSpaceActionType.UPDATE,
				SharedSpaceResourceType.FILE, admin, writer, contributor);
		createInitPermission("ea49ea0e-c14e-4f10-95bf-5dae8d01ab91", "Delete a file", SharedSpaceActionType.DELETE,
				SharedSpaceResourceType.FILE, admin, writer, drive_admin);
		createInitPermission("fd95b249-d142-47b4-9d17-3bb039e58f1a", "Download a file", SharedSpaceActionType.DOWNLOAD,
				SharedSpaceResourceType.FILE, admin, writer, contributor, reader);
		createInitPermission("268d7402-91da-4cb9-9a0c-396d0e21c04f", "Download thumbnail a file", SharedSpaceActionType.DOWNLOAD_THUMBNAIL,
				SharedSpaceResourceType.FILE, admin, writer, contributor, reader);
		createInitPermission("f597e8f2-1c3b-4285-a909-62f47528de1e", "Create a member", SharedSpaceActionType.CREATE,
				SharedSpaceResourceType.MEMBER, admin, drive_admin);
		createInitPermission("6b3e52d5-5fa5-4a72-bf62-fc15896b1cfc", "Read a member", SharedSpaceActionType.READ,
				SharedSpaceResourceType.MEMBER, admin, writer, contributor, reader, drive_admin, drive_writer, drive_reader);
		createInitPermission("0f1d6446-d37d-4bc6-a2ed-c391b6866527", "Update a member", SharedSpaceActionType.UPDATE,
				SharedSpaceResourceType.MEMBER, admin, drive_admin);
		createInitPermission("4b29d1f9-dec7-484c-a170-a051e7d9b848", "Delete a member", SharedSpaceActionType.DELETE,
				SharedSpaceResourceType.MEMBER, admin, drive_admin);
		createInitPermission("08a77038-95d0-46be-93de-a602e0315d6e", "Create a workgroup", SharedSpaceActionType.CREATE,
				SharedSpaceResourceType.WORK_GROUP, admin, drive_admin, drive_writer);
		createInitPermission("ce73fa89-04aa-41f2-a94f-cf09b46f810b", "Read a workgroup", SharedSpaceActionType.READ,
				SharedSpaceResourceType.WORK_GROUP, admin, writer, contributor, reader);
		createInitPermission("881dfa55-90c5-460a-9ac2-a38181fd2349", "Update a workgroup", SharedSpaceActionType.UPDATE,
				SharedSpaceResourceType.WORK_GROUP, admin);
		createInitPermission("efd0d533-cb5b-4bf6-a717-81f28ae0a1fe", "Delete a workgroup", SharedSpaceActionType.DELETE,
				SharedSpaceResourceType.WORK_GROUP, admin);
		logger.info("END -- Initialization with default shared space roles and permissions.");
	}

}
