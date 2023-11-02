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

import java.io.IOException;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ShareEntryGroupDto;
import org.linagora.linshare.core.service.impl.UserServiceImpl;
import org.linagora.linshare.server.embedded.ldap.LdapServerRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ExtendWith(SpringExtension.class)
@ExtendWith(LdapServerRule.class)
@Transactional
@Sql({"/import-tests-make-user2-admin.sql"})
@Sql({"/import-test-technical-users.sql"})
@Sql({"/import-tests-share-entry-group-setup.sql"})
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
        "classpath:springContext-test.xml"})
public class ShareEntryGroupDelegationRestServiceImplTest {

    public static final String TECHNICAL_USER_LIST_SHARE_ENTRY_GROUP = "technical.list.shareEntryGroup@linshare.org";
    public static final String TECHNICAL_USER_GET_SHARE_ENTRY_GROUP = "technical.get.shareEntryGroup@linshare.org";
    public static final String TECHNICAL_USER_UPDATE_SHARE_ENTRY_GROUP = "technical.update.shareEntryGroup@linshare.org";
    public static final String TECHNICAL_USER_DELETE_SHARE_ENTRY_GROUP = "technical.delete.shareEntryGroup@linshare.org";
    public static final String TECHNICAL_USER_CREATE_NODE = "technical.create.node@linshare.org";
    public static final String TECHNICAL_USER_NONE = "technical.none@linshare.org";
    public static final String SHARE_ENTRY_GROUP_UUID = "61eae04b-9496-4cb1-900e-eda8caac6703";

    @Autowired
    private ShareEntryGroupRestServiceImpl testee;

    @Autowired
    private UserServiceImpl userService;
    private User john;


    @BeforeEach
    public void setUp() {
        john = userService.findByLsUuid("aebe1b64-39c0-11e5-9fa8-080027b8274b");
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void rootCannotGetAllShareEntries() {
        assertThatThrownBy(() -> testee.findAll(john.getLsUuid(), false))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

    @Test
    @WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
    public void adminCannotGetAllShareEntries() {
        assertThatThrownBy(() -> testee.findAll(john.getLsUuid(), false))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

    @Test
    @WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") // Amy's uuid (simple)
    public void userCannotGetAllShareEntries() {
        assertThatThrownBy(() -> testee.findAll(john.getLsUuid(), false))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

    @Test
    @WithMockUser(TECHNICAL_USER_LIST_SHARE_ENTRY_GROUP)
    public void technicalUserCanGetAllShareEntriesWithPermissions() throws IOException {
        List<ShareEntryGroupDto> shareEntries = testee.findAll(john.getLsUuid(), false);
        assertThat(shareEntries).isNotNull();
        assertThat(shareEntries.size()).isEqualTo(6);
    }

    @Test
    @WithMockUser(TECHNICAL_USER_CREATE_NODE)
    public void technicalUserCannotGetAllShareEntriesWithWrongPermissions() {
        assertThatThrownBy(() -> testee.findAll(john.getLsUuid(), false))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to list all entries.");
    }

    @Test
    @WithMockUser(TECHNICAL_USER_NONE)
    public void technicalUserCannotGetAllShareEntriesWithoutPermissions() {
        assertThatThrownBy(() -> testee.findAll(john.getLsUuid(), false))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to list all entries.");
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void rootCannotGetShareEntries() {
        assertThatThrownBy(() -> testee.find(john.getLsUuid(), SHARE_ENTRY_GROUP_UUID, true))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

    @Test
    @WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
    public void adminCannotGetShareEntries() {
        assertThatThrownBy(() -> testee.find(john.getLsUuid(), SHARE_ENTRY_GROUP_UUID, true))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

    @Test
    @WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") // Amy's uuid (simple)
    public void userCannotGetShareEntries() {
        assertThatThrownBy(() -> testee.find(john.getLsUuid(), SHARE_ENTRY_GROUP_UUID, true))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

    @Test
    @WithMockUser(TECHNICAL_USER_GET_SHARE_ENTRY_GROUP)
    public void technicalUserCanGetShareEntriesWithPermissions() throws IOException {
        ShareEntryGroupDto shareEntryGroup = testee.find(john.getLsUuid(), SHARE_ENTRY_GROUP_UUID, true);
        assertThat(shareEntryGroup).isNotNull();
    }

    @Test
    @WithMockUser(TECHNICAL_USER_CREATE_NODE)
    public void technicalUserCannotGetShareEntriesWithWrongPermissions() {
        assertThatThrownBy(() -> testee.find(john.getLsUuid(), SHARE_ENTRY_GROUP_UUID, true))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to get this entry.");
    }

    @Test
    @WithMockUser(TECHNICAL_USER_NONE)
    public void technicalUserCannotGetShareEntriesWithoutPermissions() {
        assertThatThrownBy(() -> testee.find(john.getLsUuid(), SHARE_ENTRY_GROUP_UUID, true))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to get this entry.");
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void rootCannotUpdateShareEntries() {
        assertThatThrownBy(() -> testee.update(john.getLsUuid(), getShareEntryGroupForUpdate()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

    @Test
    @WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
    public void adminCannotUpdateShareEntries() {
        assertThatThrownBy(() -> testee.update(john.getLsUuid(), getShareEntryGroupForUpdate()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

    @Test
    @WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") // Amy's uuid (simple)
    public void userCannotUpdateShareEntries() {
        assertThatThrownBy(() -> testee.update(john.getLsUuid(), getShareEntryGroupForUpdate()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

    @Test
    @WithMockUser(TECHNICAL_USER_UPDATE_SHARE_ENTRY_GROUP)
    public void technicalUserCanUpdateShareEntriesWithPermissions() throws IOException {
        ShareEntryGroupDto shareEntryGroup = testee.update(john.getLsUuid(), getShareEntryGroupForUpdate());
        assertThat(shareEntryGroup).isNotNull();
        assertThat(shareEntryGroup.getSubject()).isEqualTo("new subject");
    }

    @Test
    @WithMockUser(TECHNICAL_USER_CREATE_NODE)
    public void technicalUserCannotUpdateShareEntriesWithWrongPermissions() {
        assertThatThrownBy(() -> testee.update(john.getLsUuid(), getShareEntryGroupForUpdate()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to get this entry.");
    }

    @Test
    @WithMockUser(TECHNICAL_USER_NONE)
    public void technicalUserCannotUpdateShareEntriesWithoutPermissions() {
        assertThatThrownBy(() -> testee.update(john.getLsUuid(), getShareEntryGroupForUpdate()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to get this entry.");
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void rootCannotDeleteShareEntries() {
        assertThatThrownBy(() -> testee.delete(john.getLsUuid(), SHARE_ENTRY_GROUP_UUID))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

    @Test
    @WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
    public void adminCannotDeleteShareEntries() {
        assertThatThrownBy(() -> testee.delete(john.getLsUuid(), SHARE_ENTRY_GROUP_UUID))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

    @Test
    @WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") // Amy's uuid (simple)
    public void userCannotDeleteShareEntries() {
        assertThatThrownBy(() -> testee.delete(john.getLsUuid(), SHARE_ENTRY_GROUP_UUID))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

    @Test
    @WithMockUser(TECHNICAL_USER_DELETE_SHARE_ENTRY_GROUP)
    public void technicalUserCanDeleteShareEntriesWithPermissions() throws IOException {
        ShareEntryGroupDto shareEntryGroup = testee.delete(john.getLsUuid(), SHARE_ENTRY_GROUP_UUID);
        assertThat(shareEntryGroup).isNotNull();

        assertThatThrownBy(() -> testee.find(john.getLsUuid(), SHARE_ENTRY_GROUP_UUID, true))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Share entry group with uuid :61eae04b-9496-4cb1-900e-eda8caac6703 was not found.");
    }

    @Test
    @WithMockUser(TECHNICAL_USER_CREATE_NODE)
    public void technicalUserCannotDeleteShareEntriesWithWrongPermissions() {
        assertThatThrownBy(() -> testee.delete(john.getLsUuid(), SHARE_ENTRY_GROUP_UUID))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to get this entry.");
    }

    @Test
    @WithMockUser(TECHNICAL_USER_NONE)
    public void technicalUserCannotDeleteShareEntriesWithoutPermissions() {
        assertThatThrownBy(() -> testee.delete(john.getLsUuid(), SHARE_ENTRY_GROUP_UUID))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to get this entry.");
    }

    private ShareEntryGroupDto getShareEntryGroupForUpdate() {
        ShareEntryGroupDto shareEntryGroupDto = new ShareEntryGroupDto();
        shareEntryGroupDto.setUuid(SHARE_ENTRY_GROUP_UUID);
        shareEntryGroupDto.setSubject("new subject");
        return shareEntryGroupDto;
    }

}