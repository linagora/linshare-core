/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 *
 * Copyright (C) 2022 LINAGORA
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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import javax.transaction.Transactional;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.constants.SharedSpaceActionType;
import org.linagora.linshare.core.domain.constants.SharedSpaceResourceType;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.mongo.entities.SharedSpaceAuthor;
import org.linagora.linshare.mongo.entities.SharedSpacePermission;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;
import org.linagora.linshare.mongo.repository.SharedSpacePermissionMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceRoleMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.collect.Lists;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
	"classpath:springContext-dao.xml",
	"classpath:springContext-ldap.xml",
	"classpath:springContext-repository.xml",
	"classpath:springContext-mongo.xml",
	"classpath:springContext-service.xml",
	"classpath:springContext-service-miscellaneous.xml",
	"classpath:springContext-rac.xml",
	"classpath:springContext-mongo-init.xml",
	"classpath:springContext-storage-jcloud.xml",
	"classpath:springContext-business-service.xml",
	"classpath:springContext-test.xml" })
public class InitMongoServiceImplTest {

	@Autowired
	private InitMongoServiceImpl testee;

	@Autowired
	private SharedSpaceRoleMongoRepository roleMongoRepository;
	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private SharedSpacePermissionMongoRepository permissionMongoRepository;

	@AfterEach
	public void tearDown() {
		roleMongoRepository.findAll()
			.stream()
			.forEach(role -> roleMongoRepository.delete(role));
		permissionMongoRepository.findAll()
			.stream()
			.forEach(permission -> permissionMongoRepository.delete(permission));
	}

	@Test
	public void upsertInitRoleShouldCreateRoleWhenDoesntExists() {
		// Given
		User root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
		GenericLightEntity rootDomain = new GenericLightEntity(
			LinShareConstants.rootDomainIdentifier, LinShareConstants.rootDomainIdentifier);
		SharedSpaceAuthor rootAccount = new SharedSpaceAuthor(root.getLsUuid(), root.getFullName());

		// When
		String roleUuid = "0821d4dc-74fe-4420-aea5-fcd1f08d7910";
		String roleName = "ROLE_NAME";
		NodeType workGroup = NodeType.WORK_GROUP;
		testee.upsertInitRole(roleUuid, roleName, rootDomain, workGroup, rootAccount);

		// Then
		SharedSpaceRole role = roleMongoRepository.findByUuid(roleUuid);

		assertThat(role).isNotNull();
		SoftAssertions softAssertions = new SoftAssertions();
		softAssertions.assertThat(role.getUuid()).isEqualTo(roleUuid);
		softAssertions.assertThat(role.getName()).isEqualTo(roleName);
		softAssertions.assertThat(role.getDomain().getUuid()).isEqualTo(rootDomain.getUuid());
		softAssertions.assertThat(role.getType()).isEqualTo(workGroup);
		softAssertions.assertThat(role.getAuthor().getUuid()).isEqualTo(rootAccount.getUuid());
		softAssertions.assertAll();
	}

	@Test
	public void upsertInitRoleShouldReturnRoleWhenExists() {
		// Given
		User root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
		GenericLightEntity rootDomain = new GenericLightEntity(
			LinShareConstants.rootDomainIdentifier, LinShareConstants.rootDomainIdentifier);
		SharedSpaceAuthor rootAccount = new SharedSpaceAuthor(root.getLsUuid(), root.getFullName());

		String roleUuid = "0821d4dc-74fe-4420-aea5-fcd1f08d7910";
		String roleName = "ROLE_NAME";
		NodeType workGroup = NodeType.WORK_GROUP;
		SharedSpaceRole role = new SharedSpaceRole();
		role.setUuid(roleUuid);
		role.setName(roleName);
		role.setEnabled(true);
		role.setDomain(rootDomain);
		role.setAuthor(rootAccount);
		role.setType(workGroup);
		role.setModificationDate(new Date());
		role.setCreationDate(new Date());
		roleMongoRepository.insert(role);

		// When
		SharedSpaceRole returnedRole = testee.upsertInitRole(roleUuid, roleName, rootDomain, workGroup, rootAccount);

		// Then
		SoftAssertions softAssertions = new SoftAssertions();
		softAssertions.assertThat(returnedRole).isNotNull();
		softAssertions.assertThat(returnedRole.getUuid()).isEqualTo(roleUuid);
		softAssertions.assertThat(returnedRole.getName()).isEqualTo(roleName);
		softAssertions.assertThat(role.getDomain().getUuid()).isEqualTo(rootDomain.getUuid());
		softAssertions.assertThat(returnedRole.getType()).isEqualTo(workGroup);
		softAssertions.assertThat(role.getAuthor().getUuid()).isEqualTo(rootAccount.getUuid());
		softAssertions.assertAll();
	}

	@Test
	public void upsertInitRoleShouldUpdateTypeWhenRoleExistsAndNoType() {
		// Given
		User root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
		GenericLightEntity rootDomain = new GenericLightEntity(
			LinShareConstants.rootDomainIdentifier, LinShareConstants.rootDomainIdentifier);
		SharedSpaceAuthor rootAccount = new SharedSpaceAuthor(root.getLsUuid(), root.getFullName());

		String roleUuid = "0821d4dc-74fe-4420-aea5-fcd1f08d7910";
		String roleName = "ROLE_NAME";
		SharedSpaceRole role = new SharedSpaceRole();
		role.setUuid(roleUuid);
		role.setName(roleName);
		role.setEnabled(true);
		role.setDomain(rootDomain);
		role.setAuthor(rootAccount);
		role.setModificationDate(new Date());
		role.setCreationDate(new Date());
		roleMongoRepository.insert(role);

		// When
		NodeType workGroup = NodeType.WORK_GROUP;
		SharedSpaceRole returnedRole = testee.upsertInitRole(roleUuid, roleName, rootDomain, workGroup, rootAccount);

		// Then
		assertThat(returnedRole.getType()).isEqualTo(workGroup);
	}

	@Test
	public void upsertInitRoleShouldMigrateWhenOldDriveName() {
		// Given
		User root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
		GenericLightEntity rootDomain = new GenericLightEntity(
			LinShareConstants.rootDomainIdentifier, LinShareConstants.rootDomainIdentifier);
		SharedSpaceAuthor rootAccount = new SharedSpaceAuthor(root.getLsUuid(), root.getFullName());

		String roleUuid = "0821d4dc-74fe-4420-aea5-fcd1f08d7910";
		SharedSpaceRole role = new SharedSpaceRole();
		role.setUuid(roleUuid);
		role.setName("DRIVE_ADMIN");
		role.setEnabled(true);
		role.setDomain(rootDomain);
		role.setAuthor(rootAccount);
		role.setModificationDate(new Date());
		role.setCreationDate(new Date());
		roleMongoRepository.insert(role);

		// When
		String newRoleName = "WORK_SPACE_ADMIN";
		SharedSpaceRole returnedRole = testee.upsertInitRole(roleUuid, newRoleName, rootDomain, NodeType.WORK_GROUP, rootAccount);

		// Then
		assertThat(returnedRole.getName()).isEqualTo(newRoleName);
	}

	@Test
	public void upsertInitPermissionShouldCreatePermissionWhenDoesntExists() {
		// Given
		String permissionUuid = "3cd75eec-cacc-42e6-ad59-23d521a1d8d1";
		SharedSpaceActionType actionType = SharedSpaceActionType.CREATE;
		SharedSpaceResourceType resourceType = SharedSpaceResourceType.MEMBER;
		GenericLightEntity entity = new GenericLightEntity("30b6a3be-e171-4829-8b54-a28c846ed411", "RoleName");

		// When
		testee.upsertInitPermission(permissionUuid, actionType, resourceType, entity);

		// Then
		SharedSpacePermission permission = permissionMongoRepository.findByUuid(permissionUuid);

		assertThat(permission).isNotNull();
		SoftAssertions softAssertions = new SoftAssertions();
		softAssertions.assertThat(permission.getUuid()).isEqualTo(permissionUuid);
		softAssertions.assertThat(permission.getAction()).isEqualTo(actionType);
		softAssertions.assertThat(permission.getResource()).isEqualTo(resourceType);
		softAssertions.assertThat(permission.getRoles())
			.extracting("uuid")
			.containsOnly(entity.getUuid());
		softAssertions.assertAll();
	}

	@Test
	public void upsertInitPermissionShouldReturnPermissionWhenExists() {
		// Given
		String permissionUuid = "3cd75eec-cacc-42e6-ad59-23d521a1d8d1";
		SharedSpaceActionType actionType = SharedSpaceActionType.CREATE;
		SharedSpaceResourceType resourceType = SharedSpaceResourceType.MEMBER;
		GenericLightEntity entity = new GenericLightEntity("30b6a3be-e171-4829-8b54-a28c846ed411", "RoleName");

		SharedSpacePermission permission = new SharedSpacePermission();
		permission.setUuid(permissionUuid);
		permission.setAction(actionType);
		permission.setResource(resourceType);
		permission.setCreationDate(new Date());
		permission.setRoles(Lists.newArrayList(entity));
		permission.setModificationDate(new Date());
		permissionMongoRepository.insert(permission);

		// When
		SharedSpacePermission returnedPermission = testee.upsertInitPermission(permissionUuid, actionType, resourceType, entity);

		// Then
		assertThat(returnedPermission).isNotNull();
		SoftAssertions softAssertions = new SoftAssertions();
		softAssertions.assertThat(returnedPermission.getUuid()).isEqualTo(permissionUuid);
		softAssertions.assertThat(returnedPermission.getAction()).isEqualTo(actionType);
		softAssertions.assertThat(returnedPermission.getResource()).isEqualTo(resourceType);
		softAssertions.assertThat(returnedPermission.getRoles())
			.extracting("uuid")
			.containsOnly(entity.getUuid());
		softAssertions.assertAll();
	}

	@Test
	public void upsertInitPermissionShouldUpdateRolesWhenModified() {
		// Given
		String permissionUuid = "3cd75eec-cacc-42e6-ad59-23d521a1d8d1";
		SharedSpaceActionType actionType = SharedSpaceActionType.CREATE;
		SharedSpaceResourceType resourceType = SharedSpaceResourceType.MEMBER;
		GenericLightEntity entity = new GenericLightEntity("30b6a3be-e171-4829-8b54-a28c846ed411", "RoleName");

		SharedSpacePermission permission = new SharedSpacePermission();
		permission.setUuid(permissionUuid);
		permission.setAction(actionType);
		permission.setResource(resourceType);
		permission.setCreationDate(new Date());
		permission.setRoles(Lists.newArrayList(entity));
		Date modificationDate = new Date();
		permission.setModificationDate(modificationDate);
		permissionMongoRepository.insert(permission);

		// When
		GenericLightEntity other = new GenericLightEntity("558234e6-421a-4d2e-b18e-30402794ccea", "OtherRoleName");
		SharedSpacePermission returnedPermission = testee.upsertInitPermission(permissionUuid, actionType, resourceType, entity, other);

		// Then
		assertThat(returnedPermission).isNotNull();
		SoftAssertions softAssertions = new SoftAssertions();
		softAssertions.assertThat(returnedPermission.getUuid()).isEqualTo(permissionUuid);
		softAssertions.assertThat(returnedPermission.getAction()).isEqualTo(actionType);
		softAssertions.assertThat(returnedPermission.getResource()).isEqualTo(resourceType);
		softAssertions.assertThat(returnedPermission.getRoles())
			.extracting("uuid")
			.containsOnly(entity.getUuid(), other.getUuid());
		softAssertions.assertThat(returnedPermission.getModificationDate()).isNotEqualTo(modificationDate);
		softAssertions.assertAll();
	}

	@Test
	public void upsertInitPermissionShouldNotUpdateRolesWhenSame() {
		// Given
		String permissionUuid = "3cd75eec-cacc-42e6-ad59-23d521a1d8d1";
		SharedSpaceActionType actionType = SharedSpaceActionType.CREATE;
		SharedSpaceResourceType resourceType = SharedSpaceResourceType.MEMBER;
		GenericLightEntity entity = new GenericLightEntity("30b6a3be-e171-4829-8b54-a28c846ed411", "RoleName");

		SharedSpacePermission permission = new SharedSpacePermission();
		permission.setUuid(permissionUuid);
		permission.setAction(actionType);
		permission.setResource(resourceType);
		permission.setCreationDate(new Date());
		permission.setRoles(Lists.newArrayList(entity));
		Date modificationDate = new Date();
		permission.setModificationDate(modificationDate);
		permissionMongoRepository.insert(permission);

		// When
		SharedSpacePermission returnedPermission = testee.upsertInitPermission(permissionUuid, actionType, resourceType, entity);

		// Then
		assertThat(returnedPermission).isNotNull();
		assertThat(returnedPermission.getModificationDate()).isEqualTo(modificationDate);
	}

	@Test
	public void upsertInitPermissionShouldMigrateWhenOldDriveName() {
		// Given
		String permissionUuid = "3cd75eec-cacc-42e6-ad59-23d521a1d8d1";
		SharedSpaceActionType actionType = SharedSpaceActionType.CREATE;
		SharedSpaceResourceType resourceType = SharedSpaceResourceType.MEMBER;
		GenericLightEntity entity = new GenericLightEntity("30b6a3be-e171-4829-8b54-a28c846ed411", "DRIVE_ADMIN");

		SharedSpacePermission permission = new SharedSpacePermission();
		permission.setUuid(permissionUuid);
		permission.setAction(actionType);
		permission.setResource(resourceType);
		permission.setCreationDate(new Date());
		permission.setRoles(Lists.newArrayList(entity));
		permission.setModificationDate(new Date());
		permissionMongoRepository.insert(permission);

		// When
		String newRoleName = "WORK_SPACE_ADMIN";
		SharedSpacePermission returnedPermission = testee.upsertInitPermission(permissionUuid, actionType, resourceType, entity);

		// Then
		assertThat(returnedPermission.getRoles()).hasSize(1);
		assertThat(returnedPermission.getRoles().get(0).getName()).isEqualTo("WORK_SPACE_ADMIN");
	}
}