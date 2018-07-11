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

import org.apache.commons.lang.Validate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.business.service.SharedSpaceNodeBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceRoleBusinessService;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.NodeType;
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

	private SharedSpaceRole role;

	private GenericLightEntity roleToPersist;

	private User authUser;

	private User jane;

	private SharedSpaceNode node;

	private GenericLightEntity nodeToPersist;

	private LoadingServiceTestDatas datas;

	@Autowired
	private SharedSpaceRoleBusinessService roleBusinessService;

	private GenericLightEntity accountToPersist;

	@Autowired
	private SharedSpaceNodeBusinessService nodeBusinessService;

	private SharedSpaceAccount account;
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
		authUser = datas.getUser1();
		initService.init();
		role = roleBusinessService.findByName("ADMIN");
		roleToPersist = new GenericLightEntity(role.getUuid(), role.getName());
		Validate.notNull(role, "role must be set");
		SharedSpaceNode node0 = new SharedSpaceNode("nodeTest", "parentuuidTest", NodeType.DRIVE_ROOT);
		node = nodeBusinessService.create(node0);
		nodeToPersist = new GenericLightEntity(node0.getUuid(), node0.getUuid());
		jane = datas.getUser2();
		account = new SharedSpaceAccount(jane);
		accountToPersist = new GenericLightEntity(account.getUuid(), account.getName());
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	public void testFind() {
		SharedSpaceMember toCreate = service.create(authUser, authUser, account.getUuid(), role.getUuid(),
				node.getUuid());
		SharedSpaceMember tofound = service.find(authUser, authUser, toCreate.getUuid());
		Assert.assertEquals(toCreate.getUuid(), tofound.getUuid());
	}

	@Test
	public void testCreate() {
		SharedSpaceMember member = new SharedSpaceMember(nodeToPersist, roleToPersist, accountToPersist);
		SharedSpaceMember memberToCreate = service.create(authUser, authUser, member.getAccount().getUuid(),
				member.getRole().getUuid(), member.getNode().getUuid());
		Assert.assertNotNull(memberToCreate);
	}

	@Test
	public void testAvoidDuplicatesMembers() {
		SharedSpaceMember member = new SharedSpaceMember(nodeToPersist, roleToPersist, accountToPersist);
		SharedSpaceMember memberToCreate = service.create(authUser, authUser, member.getAccount().getUuid(),
				member.getRole().getUuid(), member.getNode().getUuid());
		Assert.assertEquals("The account referenced in this member is not authUser's",
				memberToCreate.getAccount().getUuid(), jane.getLsUuid());
		try {
			service.create(authUser, authUser, member.getAccount().getUuid(), member.getRole().getUuid(),
					member.getNode().getUuid());
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
		SharedSpaceMember member = new SharedSpaceMember(nodeToPersist, roleToPersist, accountToPersist);
		SharedSpaceMember memberToCreate = service.create(authUser, authUser, member.getAccount().getUuid(),
				member.getRole().getUuid(), member.getNode().getUuid());
		Assert.assertEquals("The account referenced in this member is not authUser's",
				memberToCreate.getAccount().getUuid(), jane.getLsUuid());
		SharedSpaceMember deletedMember = service.delete(authUser, authUser, memberToCreate);
		Assert.assertNotNull("No member deleted", deletedMember);
		try {
			service.find(authUser, authUser, memberToCreate.getUuid());
			Assert.assertTrue("An exception should be thrown because the member should not be found", false);
		} catch (BusinessException e) {
			Assert.assertEquals("The member is found in the database. It has not been deleted",
					BusinessErrorCode.SHARED_SPACE_MEMBER_NOT_FOUND, e.getErrorCode());
		}
	}

}
