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
package org.linagora.linshare.business.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.light.LightSharedSpaceRole;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.repository.SharedSpaceNodeMongoRepository;
import org.linagora.linshare.mongo.repository.SharedSpaceRoleMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Disabled
@ExtendWith(SpringExtension.class)
@Sql({
	})
@Transactional
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml", 
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-mongo-init.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml" })
public class SharedSpaceMemberBusinessServiceImplTest {

	private static Logger logger = LoggerFactory.getLogger(SharedSpaceMemberBusinessServiceImplTest.class);

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	private SharedSpaceRoleMongoRepository roleRepository;

	@Autowired
	@Qualifier("sharedSpaceMemberBusinessService")
	private SharedSpaceMemberBusinessService sharedSpaceMemberBusinessService;

	@Autowired
	private SharedSpaceNodeMongoRepository nodeRpository;

	private User jane;

	private SharedSpaceAccount accountJane;
	
	private LightSharedSpaceRole lightAdminRoleToPersist, lightReaderRoleToPersist, lightContirbutorRoleToPersist;

	@BeforeEach
	public void setUp() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		jane = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
		lightAdminRoleToPersist = new LightSharedSpaceRole(roleRepository.findByName("ADMIN"));
		lightReaderRoleToPersist = new LightSharedSpaceRole(roleRepository.findByName("READER"));
		lightContirbutorRoleToPersist = new LightSharedSpaceRole(roleRepository.findByName("CONTRIBUTOR"));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}
	
	@Test
	public void testFindAllNestedNodeByAccountUuid() {
		// Create shared spaces (drive, workgroup, nested workgroup into a drive)
		SharedSpaceNode workgroupOnTop = new SharedSpaceNode("workgroup_top_level", null, NodeType.WORK_GROUP);
		workgroupOnTop.setDomainUuid(jane.getDomainId());
		nodeRpository.insert(workgroupOnTop);
		SharedSpaceNode drive = new SharedSpaceNode("drive", null, NodeType.WORK_SPACE);
		drive.setDomainUuid(jane.getDomainId());
		nodeRpository.insert(drive);
		SharedSpaceNode nestedWorkgroup = new SharedSpaceNode("nested_workgroup", drive.getUuid(), NodeType.WORK_GROUP);
		nestedWorkgroup.setDomainUuid(jane.getDomainId());
		nodeRpository.insert(nestedWorkgroup);
		// nested nodes where member Jane will be added
		SharedSpaceNodeNested nodeWorkgroupOnTop = new SharedSpaceNodeNested(workgroupOnTop);
		SharedSpaceNodeNested nodeNestedWorkgroup = new SharedSpaceNodeNested(nestedWorkgroup);
		SharedSpaceNodeNested nodeDrive = new SharedSpaceNodeNested(drive);
		// add jane as member of created nodes
		accountJane = new SharedSpaceAccount(jane);
		sharedSpaceMemberBusinessService
				.create(new SharedSpaceMember(nodeWorkgroupOnTop, lightAdminRoleToPersist, accountJane));
		sharedSpaceMemberBusinessService
				.create(new SharedSpaceMember(nodeNestedWorkgroup, lightReaderRoleToPersist, accountJane));
		sharedSpaceMemberBusinessService
				.create(new SharedSpaceMember(nodeDrive, lightContirbutorRoleToPersist, accountJane));
		List<SharedSpaceNodeNested> nestedNodes = sharedSpaceMemberBusinessService
				.findAllSharedSpacesByAccountAndParentForUsers(accountJane.getUuid(), false, null, null);
		// without role should return 2 nodes , the nested workgroup is not returned
		assertEquals(nestedNodes.size(), 2, "Only nested nodes on top should be returned");
		nestedNodes.forEach(nested -> {
			assertNull(nested.getRole());
		});
		nestedNodes = sharedSpaceMemberBusinessService.findAllSharedSpacesByAccountAndParentForUsers(accountJane.getUuid(), true, null, null);
		// with role should return 2 nodes , the nested workgroup is not returned
		assertEquals(nestedNodes.size(), 2, "Only nested nodes on top should be returned");
		nestedNodes.forEach(nested -> {
			assertNotNull(nested.getRole());
		});

	}

}
