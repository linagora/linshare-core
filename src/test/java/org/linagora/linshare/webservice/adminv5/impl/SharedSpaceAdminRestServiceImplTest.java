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
package org.linagora.linshare.webservice.adminv5.impl;

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
import org.linagora.linshare.core.service.impl.SharedSpaceNodeServiceImpl;;
import org.linagora.linshare.core.service.impl.UserServiceImpl;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.server.embedded.ldap.LdapServerRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ExtendWith(LdapServerRule.class)
@Transactional
@Sql({ "/import-tests-make-user2-admin.sql"})
@ContextConfiguration(locations = {"classpath:springContext-datasource.xml",
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
        "classpath:springContext-webservice-adminv5.xml",
        "classpath:springContext-facade-ws-adminv5.xml",
        "classpath:springContext-facade-ws-user.xml",
        "classpath:springContext-webservice-admin.xml",
        "classpath:springContext-facade-ws-admin.xml",
        "classpath:springContext-webservice.xml",
        "classpath:springContext-upgrade-v2-0.xml",
        "classpath:springContext-facade-ws-async.xml",
        "classpath:springContext-task-executor.xml",
        "classpath:springContext-batches.xml",
        "classpath:springContext-test.xml"})
public class SharedSpaceAdminRestServiceImplTest {

    @Autowired
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
    private User user;
    private User admin;
    private User otherDomainUser;
    private SharedSpaceNode workSpace;
    private SharedSpaceNode workGroup;
    private SharedSpaceNode otherWorkSpace;
    private SharedSpaceNode otherWorkGroup;


    @BeforeEach
    public void setUp() {
        root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
        AbstractDomain topDomain = domainService.find(root, LinShareTestConstants.TOP_DOMAIN);
        user = userService.findUserInDB(topDomain.getUuid(), LinShareTestConstants.JOHN_ACCOUNT);
        admin = userService.findUserInDB(topDomain.getUuid(), LinShareTestConstants.JANE_ACCOUNT);
        otherDomainUser = userService.findByLsUuid("aebe1b64-39c0-11e5-9fa8-080027b8254j");

        SharedSpaceNode tmpWorkSpace = new SharedSpaceNode("John's WorkSpace", NodeType.WORK_SPACE);
        workSpace = nodeService.create(user, user, tmpWorkSpace);

        SharedSpaceNode tmpWorkGroup = new SharedSpaceNode("John's WorkGroup", workSpace.getUuid(), NodeType.WORK_GROUP);
        workGroup = nodeService.create(user, user, tmpWorkGroup);

        SharedSpaceNode tmpWorkSpace2 = new SharedSpaceNode("Amy's WorkSpace", NodeType.WORK_SPACE);
        otherWorkSpace = nodeService.create(otherDomainUser, otherDomainUser, tmpWorkSpace2);

        SharedSpaceNode tmpWorkGroup2 = new SharedSpaceNode("Amy's WorkGroup", otherWorkSpace.getUuid(), NodeType.WORK_GROUP);
        otherWorkGroup = nodeService.create(otherDomainUser, otherDomainUser, tmpWorkGroup2);
    }

    @Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void rootCanReadWorkSpace() {
        SharedSpaceNode sharedSpaceNode = testee.find(workSpace.getUuid());
        assertThat(sharedSpaceNode).isNotNull();
    }

	@Test
    @WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void adminCanReadWorkSpaceInHisDomain() {
        SharedSpaceNode sharedSpaceNode = testee.find(workSpace.getUuid());
        assertThat(sharedSpaceNode).isNotNull();
    }

	@Test
    @WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void adminCannotReadWorkSpaceOutsideHisDomain() {
        assertThatThrownBy(() -> testee.find(otherWorkGroup.getUuid()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to get this entry.");
    }

    @Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void rootCanReadWorkGroup() {
        SharedSpaceNode sharedSpaceNode = testee.find(workGroup.getUuid());
        assertThat(sharedSpaceNode).isNotNull();
    }

	@Test
    @WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void adminCannotReadWorkGroupOutsideHisDomain() {
        assertThatThrownBy(() -> testee.find(otherWorkGroup.getUuid()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to get this entry.");
    }

	@Test
    @WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void adminCanReadWorkGroupInHisDomain() {
        SharedSpaceNode sharedSpaceNode = testee.find(workGroup.getUuid());
        assertThat(sharedSpaceNode).isNotNull();
    }


    @Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void rootCanDeleteWorkSpace() {
        SharedSpaceNode sharedSpaceNode = testee.delete(null, workSpace.getUuid());
        assertThat(sharedSpaceNode).isNotNull();
    }

	@Test
    @WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void adminCanDeleteWorkSpaceInHisDomain() {
        SharedSpaceNode sharedSpaceNode = testee.delete(null, workSpace.getUuid());
        assertThat(sharedSpaceNode).isNotNull();
    }

	@Test
    @WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void adminCannotDeleteWorkSpaceOutsideHisDomain() {
        assertThatThrownBy(() -> testee.delete(null, otherWorkGroup.getUuid()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to get this entry.");
    }

    @Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void rootCanDeleteWorkGroup() {
        SharedSpaceNode sharedSpaceNode = testee.delete(null, workGroup.getUuid());
        assertThat(sharedSpaceNode).isNotNull();
    }

	@Test
    @WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void adminCannotDeleteWorkGroupOutsideHisDomain() {
        assertThatThrownBy(() -> testee.delete(null, otherWorkGroup.getUuid()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to get this entry.");
    }

	@Test
    @WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void adminCanDeleteWorkGroupInHisDomain() {
        SharedSpaceNode sharedSpaceNode = testee.delete(null, workGroup.getUuid());
        assertThat(sharedSpaceNode).isNotNull();
    }

}