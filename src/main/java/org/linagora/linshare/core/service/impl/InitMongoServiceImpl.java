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

	public InitMongoServiceImpl(UserService userService, SharedSpaceRoleMongoRepository roleMongoRepository,
			SharedSpacePermissionMongoRepository permissionMongoRepository) {
		super();
		this.userService = userService;
		this.roleMongoRepository = roleMongoRepository;
		this.permissionMongoRepository = permissionMongoRepository;
	}

	private SharedSpaceRole createInitRole(String roleUuid, String roleName, GenericLightEntity domain,
			SharedSpaceAuthor author) {
		SharedSpaceRole role = roleMongoRepository.findByUuid(roleUuid);
		if (role == null) {
			role = new SharedSpaceRole();
			role.setUuid(roleUuid);
			role.setName(roleName);
			role.setEnabled(true);
			role.setDomain(domain);
			role.setAuthor(author);
			role.setModificationDate(new Date());
			role.setCreationDate(new Date());
			roleMongoRepository.insert(role);
		}
		return role;
	}

	private GenericLightEntity createInitLightRole(String roleUuid, String roleName) {
		SharedSpaceRole role = roleMongoRepository.findByUuid(roleUuid);
		GenericLightEntity roleLight = new GenericLightEntity(role.getUuid(), role.getName());
		return roleLight;
	}

	private SharedSpacePermission createInitPermission(String permissionUuid, String permissionName,
			SharedSpaceActionType actionType, SharedSpaceResourceType resourceType,
			GenericLightEntity... roles) {
		SharedSpacePermission permission = permissionMongoRepository.findByUuid(permissionUuid);
		if (permission == null) {
			permission = new SharedSpacePermission();
			permission.setUuid(permissionUuid);
			permission.setAction(actionType);
			permission.setResource(resourceType);
			permission.setRoles(Lists.newArrayList(roles));
			permission.setCreationDate(new Date());
			permission.setModificationDate(new Date());
			permission = permissionMongoRepository.insert(permission);
		}
		return permission;
	}

	@Override
	public void init() {
		logger.info("Initialization");
		User root = userService.findByLsUuid("root@localhost.localdomain");
		GenericLightEntity rootDomain = new GenericLightEntity(LinShareConstants.rootDomainIdentifier,
				LinShareConstants.rootDomainIdentifier);
		SharedSpaceAuthor rootAccount = new SharedSpaceAuthor(root.getLsUuid(), root.getFullName());

		createInitRole("234be74d-2966-41c1-9dee-e47c8c63c14e", "ADMIN", rootDomain, rootAccount);
		createInitRole("b206c2ba-37de-491e-8e9c-88ed3be70682", "CONTRIBUTOR", rootDomain, rootAccount);
		createInitRole("8839654d-cb33-4633-bf3f-f9e805f97f84", "WRITER", rootDomain, rootAccount);
		createInitRole("4ccbed61-71da-42a0-a513-92211953ac95", "READER", rootDomain, rootAccount);

		GenericLightEntity admin = createInitLightRole("234be74d-2966-41c1-9dee-e47c8c63c14e", "ADMIN");
		GenericLightEntity contributor = createInitLightRole("b206c2ba-37de-491e-8e9c-88ed3be70682", "CONTRIBUTOR");
		GenericLightEntity writer = createInitLightRole("8839654d-cb33-4633-bf3f-f9e805f97f84", "WRITER");
		GenericLightEntity reader = createInitLightRole("4ccbed61-71da-42a0-a513-92211953ac95", "READER");

		createInitPermission("31cb4d80-c939-40f1-a79e-4d77392e0e0b", "Create a drive ", SharedSpaceActionType.CREATE,
				SharedSpaceResourceType.DRIVE, admin);
		createInitPermission("e432acbb-d72e-4e20-b255-6f1cb7329bbd", "read a drive", SharedSpaceActionType.READ,
				SharedSpaceResourceType.DRIVE, admin, writer, contributor, reader);
		createInitPermission("5557fc26-ea2d-4e3b-81af-37a614d8014c", "Update  a drive", SharedSpaceActionType.UPDATE,
				SharedSpaceResourceType.DRIVE, admin, writer, contributor);
		createInitPermission("70ecfe55-f388-4e37-91bc-958386e0a865", "Delete  a drive", SharedSpaceActionType.DELETE,
				SharedSpaceResourceType.DRIVE, admin);
		createInitPermission("0457baaf-fd9e-4737-90d9-5a802caf9ff5", "Create a folder", SharedSpaceActionType.CREATE,
				SharedSpaceResourceType.FOLDER, admin, writer, contributor);
		createInitPermission("18a76d34-e19f-45d4-864c-4bb8cadda711", "Read a folder", SharedSpaceActionType.READ,
				SharedSpaceResourceType.FOLDER, admin, writer, contributor, reader);
		createInitPermission("9dedd90c-709b-4c72-a70f-17f8c65f4f2f", "Update a folder", SharedSpaceActionType.UPDATE,
				SharedSpaceResourceType.FOLDER, admin, writer, contributor);
		createInitPermission("dd80afd5-9415-424d-b211-63669934efda", "Download a folder", SharedSpaceActionType.DOWNLOAD,
				SharedSpaceResourceType.FOLDER, admin, writer, contributor, reader);
		createInitPermission("fbe86462-174a-4d14-b6f1-ca4c6e127142", "Delete a folder", SharedSpaceActionType.DELETE,
				SharedSpaceResourceType.FOLDER, admin, writer);
		createInitPermission("3f92f534-44a1-4a78-9be0-368898d61473", "Create a file", SharedSpaceActionType.CREATE,
				SharedSpaceResourceType.FILE, admin, writer, contributor);
		createInitPermission("05e3372f-a78f-490c-9b48-d64dffd231b5", "Read a file", SharedSpaceActionType.READ,
				SharedSpaceResourceType.FILE, admin, writer, contributor, reader);
		createInitPermission("816b30ae-13ed-46a4-9284-fcaa65fc9e84", "Update a file", SharedSpaceActionType.UPDATE,
				SharedSpaceResourceType.FILE, admin, writer, contributor);
		createInitPermission("ea49ea0e-c14e-4f10-95bf-5dae8d01ab91", "Delete a file", SharedSpaceActionType.DELETE,
				SharedSpaceResourceType.FILE, admin, writer);
		createInitPermission("fd95b249-d142-47b4-9d17-3bb039e58f1a", "Download a file", SharedSpaceActionType.DOWNLOAD,
				SharedSpaceResourceType.FILE, admin, writer, contributor, reader);
		createInitPermission("268d7402-91da-4cb9-9a0c-396d0e21c04f", "Download thumbnail a file", SharedSpaceActionType.DOWNLOAD_THUMBNAIL,
				SharedSpaceResourceType.FILE, admin, writer, contributor, reader);
		createInitPermission("f597e8f2-1c3b-4285-a909-62f47528de1e", "Create a member", SharedSpaceActionType.CREATE,
				SharedSpaceResourceType.MEMBER, admin);
		createInitPermission("6b3e52d5-5fa5-4a72-bf62-fc15896b1cfc", "Read a member", SharedSpaceActionType.READ,
				SharedSpaceResourceType.MEMBER, admin, writer, contributor, reader);
		createInitPermission("0f1d6446-d37d-4bc6-a2ed-c391b6866527", "Update a member", SharedSpaceActionType.UPDATE,
				SharedSpaceResourceType.MEMBER, admin);
		createInitPermission("4b29d1f9-dec7-484c-a170-a051e7d9b848", "Delete a member", SharedSpaceActionType.DELETE,
				SharedSpaceResourceType.MEMBER, admin);
		createInitPermission("08a77038-95d0-46be-93de-a602e0315d6e", "Create a workgroup", SharedSpaceActionType.CREATE,
				SharedSpaceResourceType.WORKGROUP, admin);
		createInitPermission("ce73fa89-04aa-41f2-a94f-cf09b46f810b", "Read a workgroup", SharedSpaceActionType.READ,
				SharedSpaceResourceType.WORKGROUP, admin, writer, contributor, reader);
		createInitPermission("881dfa55-90c5-460a-9ac2-a38181fd2349", "Update a workgroup", SharedSpaceActionType.UPDATE,
				SharedSpaceResourceType.WORKGROUP, admin, writer, contributor);
		createInitPermission("efd0d533-cb5b-4bf6-a717-81f28ae0a1fe", "Delete a workgroup", SharedSpaceActionType.DELETE,
				SharedSpaceResourceType.WORKGROUP, admin);
	}

}
