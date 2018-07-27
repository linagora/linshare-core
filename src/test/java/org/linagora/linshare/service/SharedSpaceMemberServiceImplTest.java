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
package org.linagora.linshare.service;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.business.service.SharedSpaceNodeBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceRoleBusinessService;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.InitMongoService;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml", 
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml" })
public class SharedSpaceMemberServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests {
	private static Logger logger = LoggerFactory.getLogger(RecipientFavouriteRepositoryImplTest.class);

	@Autowired
	@Qualifier("userService")
	private UserService userServ;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepo;

	private SharedSpaceRole adminRole;

	private GenericLightEntity lightAdminRoleToPersist;

	private Account root;

	private User john;

	private User jane;

	private GenericLightEntity lightNodePersisted;
	
	private SharedSpaceNode node;

	private LoadingServiceTestDatas datas;

	@Autowired
	private SharedSpaceRoleBusinessService roleBusinessService;

	private GenericLightEntity lightJaneAccount;

	@Autowired
	private SharedSpaceNodeBusinessService nodeBusinessService;

	@Autowired
	InitMongoService initService;

	@Autowired
	private SharedSpaceMemberService service;

	@Before
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		this.executeSqlScript("import-tests-default-domain-quotas.sql", false);
		this.executeSqlScript("import-tests-quota-other.sql", false);
		datas = new LoadingServiceTestDatas(userRepo);
		datas.loadUsers();
		root = datas.getRoot();
		john = datas.getUser1();
		jane = datas.getUser2();
		initService.init();
		adminRole = roleBusinessService.findByName("ADMIN");
		lightAdminRoleToPersist = new GenericLightEntity(adminRole.getUuid(), adminRole.getName());
		Validate.notNull(adminRole, "adminRole must be set");
		node = new SharedSpaceNode("nodeTest", "parentuuidTest", NodeType.DRIVE);
		nodeBusinessService.create(node);
		lightNodePersisted = new GenericLightEntity(node.getUuid(), node.getUuid());
		SharedSpaceAccount account = new SharedSpaceAccount(jane);
		lightJaneAccount = new GenericLightEntity(account.getUuid(), account.getName());
		//John is considered as the admin-creator of the shared space node
		GenericLightEntity johnLightAccount = new GenericLightEntity(john.getLsUuid(), john.getFullName());
		SharedSpaceMember johnMemberShip = service.createWithoutCheckPermission(john, john, node,
				lightAdminRoleToPersist, johnLightAccount);
		Assert.assertNotNull("John has not been added as a member of his shared space", johnMemberShip);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	public void testFind() {
		SharedSpaceMember toCreate = service.create(john, john, node, lightAdminRoleToPersist, lightJaneAccount);
		SharedSpaceMember tofound = service.find(john, john, toCreate.getUuid());
		Assert.assertEquals(toCreate.getUuid(), tofound.getUuid());
	}

	@Test
	public void testCreate() {
		GenericLightEntity janeLightAccount = new GenericLightEntity(jane.getLsUuid(), jane.getFullName());
		//John add Jane as an admin of the shared space node
		SharedSpaceMember janeMembership = service.create(john, john, node, lightAdminRoleToPersist,
				janeLightAccount);
		Assert.assertNotNull("Jane has not been added as a member of John's shared space", janeMembership);
	}

	@Test
	public void testAvoidDuplicatesMembers() {
		SharedSpaceMember memberToCreate = service.create(john, john, node, lightAdminRoleToPersist, lightJaneAccount);
		Assert.assertEquals("The account referenced in this member is not john's",
				memberToCreate.getAccount().getUuid(), jane.getLsUuid());
		try {
			service.create(john, john, node, lightAdminRoleToPersist, lightJaneAccount);
			Assert.assertTrue(
					"An exception should be thrown to prevent the creation of a member with same accountuuid and nodeuuid",
					false);
		} catch (BusinessException e) {
			Assert.assertEquals("The thrown error code is not the one of an already exist ShareSpaceMember",
					BusinessErrorCode.SHARED_SPACE_MEMBER_ALREADY_EXISTS, e.getErrorCode());
		}
	}

	@Test
	public void testDelete() {
		SharedSpaceMember memberToCreate = service.create(john, john, node, lightAdminRoleToPersist, lightJaneAccount);
		Assert.assertEquals("The account referenced in this member is not john's",
				memberToCreate.getAccount().getUuid(), jane.getLsUuid());
		SharedSpaceMember deletedMember = service.delete(john, john, memberToCreate);
		Assert.assertNotNull("No member deleted", deletedMember);
		try {
			service.find(john, john, memberToCreate.getUuid());
			Assert.assertTrue("An exception should be thrown because the member should not be found", false);
		} catch (BusinessException e) {
			Assert.assertEquals("The member is found in the database. It has not been deleted",
					BusinessErrorCode.SHARED_SPACE_MEMBER_NOT_FOUND, e.getErrorCode());
		}
	}

	@Test
	public void testDeleteAll() {
		service.create(john, john, node, lightAdminRoleToPersist, lightJaneAccount);
		List<SharedSpaceMember> foundMembers = service.findAll(root, root, lightNodePersisted.getUuid());
		Assert.assertTrue("No members have been created", foundMembers.size() > 0);
		service.deleteAllMembers(john, john, lightNodePersisted.getUuid());
		foundMembers = service.findAll(root, root, lightNodePersisted.getUuid());
		Assert.assertEquals("There are members left in the shared space node", 0, foundMembers.size());
	}

	@Test
	public void testUpdate() {
		SharedSpaceMember createdMember = service.create(john, john, node, lightAdminRoleToPersist, lightJaneAccount);
		Assert.assertEquals("The account referenced in this member is not john's",
				createdMember.getAccount().getUuid(), jane.getLsUuid());
		GenericLightEntity newAccount = new GenericLightEntity(lightJaneAccount.getUuid(),
				lightJaneAccount.getName() + "DIRTY");
		SharedSpaceMember memberToUpdate = new SharedSpaceMember(new SharedSpaceNodeNested(node), lightAdminRoleToPersist, newAccount);
		memberToUpdate.setUuid(createdMember.getUuid());
		SharedSpaceMember updatedMember = service.update(john, john, memberToUpdate);
		Assert.assertNotEquals("The member has not been updated", lightJaneAccount.getName(),
				updatedMember.getAccount().getName());
		Assert.assertEquals("The member has not been updated", memberToUpdate.getAccount().getName(),
				updatedMember.getAccount().getName());
	}

	@Test
	public void testUpdateRole() {
		SharedSpaceMember createdMember = service.create(john, john, node, lightAdminRoleToPersist, lightJaneAccount);
		Assert.assertEquals("The account referenced in this member is not john's",
				createdMember.getAccount().getUuid(), jane.getLsUuid());
		SharedSpaceRole newRole = roleBusinessService.findByName("CONTRIBUTOR");
		SharedSpaceMember updatedMember = service.updateRole(john, john, createdMember.getUuid(), new GenericLightEntity(newRole.getUuid(), newRole.getName()));
		Assert.assertNotEquals("The adminRole has not been updated", newRole.getUuid(),
				createdMember.getRole().getUuid());
		Assert.assertEquals("The adminRole has not been updated", newRole.getUuid(),
				updatedMember.getRole().getUuid());
	}
}
