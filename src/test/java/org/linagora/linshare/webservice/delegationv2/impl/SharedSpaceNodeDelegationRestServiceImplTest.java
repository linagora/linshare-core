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
package org.linagora.linshare.webservice.delegationv2.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.impl.DomainServiceImpl;
import org.linagora.linshare.core.service.impl.LogEntryServiceImpl;
import org.linagora.linshare.core.service.impl.SharedSpaceNodeServiceImpl;
import org.linagora.linshare.core.service.impl.UserServiceImpl;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.server.embedded.ldap.LdapServerRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ExtendWith(LdapServerRule.class)
@Transactional
@Sql({ "/import-tests-make-user2-admin.sql"})
@Sql({ "/import-test-technical-users.sql"})
@ContextConfiguration(locations = {
        "classpath:springContext-datasource.xml",
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
        "classpath:springContext-webservice.xml",
        "classpath:springContext-upgrade-v2-0.xml",
        "classpath:springContext-facade-ws-async.xml",
        "classpath:springContext-facade-ws-admin.xml",
        "classpath:springContext-facade-ws-user.xml",
        "classpath:springContext-facade-ws-delegation.xml",
        "classpath:springContext-task-executor.xml",
        "classpath:springContext-batches.xml",
        "classpath:springContext-webservice-delegationv2.xml",
        "classpath:springContext-test.xml" })
public class SharedSpaceNodeDelegationRestServiceImplTest {

    public static final String TECHNICAL_USER_CREATE_NODE = "technical.create.node@linshare.org";
    public static final String TECHNICAL_USER_NONE = "technical.none@linshare.org";

    @Autowired
    @Qualifier("delegationV2SharedSpaceRestService")
    private SharedSpaceRestServiceImpl testee;
    @Autowired
    private SharedSpaceNodeServiceImpl nodeService;
    @Autowired
    private DomainServiceImpl domainService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    LogEntryServiceImpl logEntryService;

    private User root;
    private User john;


    @BeforeEach
    public void setUp() {
        root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
        AbstractDomain topDomain = domainService.find(root, LinShareTestConstants.TOP_DOMAIN);
        john = userService.findUserInDB(topDomain.getUuid(), LinShareTestConstants.JOHN_ACCOUNT);
    }

    @Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void rootCannotCreateWorkSpace() {
        assertThatThrownBy(() -> testee.create(john.getLsUuid(), getSampleWorkSpace()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

	@Test
    @WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void adminCannotCreateWorkSpace() {
        assertThatThrownBy(() -> testee.create(john.getLsUuid(), getSampleWorkSpace()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

	@Test
    @WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") // Amy's uuid (simple)
	public void userCannotCreateWorkSpace() {
        assertThatThrownBy(() -> testee.create(john.getLsUuid(), getSampleWorkSpace()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

	@Test
    @WithMockUser(TECHNICAL_USER_CREATE_NODE)
	public void technicalUserCanCreateWorkSpaceWithPermissions() {
        SharedSpaceNode sharedSpaceNode = testee.create(john.getLsUuid(), getSampleWorkSpace());
        assertThat(sharedSpaceNode).isNotNull();
        assertThat(sharedSpaceNode.isWorkSpace()).isTrue();
    }

	@Test
    @WithMockUser(TECHNICAL_USER_NONE)
	public void technicalUserCannotCreateWorkSpaceWithoutPermissions() {
        assertThatThrownBy(() -> testee.create(john.getLsUuid(), getSampleWorkSpace()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to create an entry.");
    }
    @Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void rootCannotCreateWorkGroup() {
        assertThatThrownBy(() -> testee.create(john.getLsUuid(), getSampleWorkGroup()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

	@Test
    @WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void adminCannotCreateWorkGroup() {
        assertThatThrownBy(() -> testee.create(john.getLsUuid(), getSampleWorkGroup()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

	@Test
    @WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") // Amy's uuid (simple)
	public void userCannotCreateWorkGroup() {
        assertThatThrownBy(() -> testee.create(john.getLsUuid(), getSampleWorkGroup()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

	@Test
    @WithMockUser(TECHNICAL_USER_CREATE_NODE)
	public void technicalUserCanCreateWorkGroupWithPermissions() {
        SharedSpaceNode sharedSpaceNode = testee.create(john.getLsUuid(), getSampleWorkGroup());
        assertThat(sharedSpaceNode).isNotNull();
        assertThat(sharedSpaceNode.isWorkGroup()).isTrue();
    }

	@Test
    @WithMockUser(TECHNICAL_USER_NONE)
	public void technicalUserCannotCreateWorkGroupWithoutPermissions() {
        assertThatThrownBy(() -> testee.create(john.getLsUuid(), getSampleWorkGroup()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to create an entry.");
    }

    private SharedSpaceNode getSampleWorkSpace() {
        return new SharedSpaceNode("sample work space", NodeType.WORK_SPACE);
    }

    private SharedSpaceNode getSampleWorkGroup() {
        SharedSpaceNode workSpace = nodeService.create(root, john, getSampleWorkSpace());
        return new SharedSpaceNode("sample work group", workSpace.getUuid(), NodeType.WORK_GROUP);
    }

}