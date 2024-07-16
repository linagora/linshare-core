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
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import javax.annotation.Nonnull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.GenericUserDto;
import org.linagora.linshare.core.facade.webservice.delegation.dto.DocumentDto;
import org.linagora.linshare.core.facade.webservice.delegation.dto.ShareCreationDto;
import org.linagora.linshare.core.facade.webservice.delegation.dto.ShareDto;
import org.linagora.linshare.core.repository.hibernate.DocumentRepositoryImpl;
import org.linagora.linshare.core.service.impl.DocumentEntryServiceImpl;
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
@Sql({"/import-tests-document-entry-setup.sql"})
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
public class ShareDelegationRestServiceImplTest {

    public static final String TECHNICAL_USER_CREATE_SHARE = "technical.create.share@linshare.org";
    public static final String TECHNICAL_USER_CREATE_NODE = "technical.create.node@linshare.org";
    public static final String TECHNICAL_USER_NONE = "technical.none@linshare.org";

    @Autowired
    private ShareRestServiceImpl testee;

    @Autowired
    private UserServiceImpl userService;
    private User john;


    @BeforeEach
    public void setUp() {
        john = userService.findByLsUuid("aebe1b64-39c0-11e5-9fa8-080027b8274b");
    }

        @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void rootCannotCreateDocument() {
        assertThatThrownBy(() -> testee.create(LinShareConstants.defaultRootMailAddress, getShareCreationDto()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

    @Test
    @WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
    public void adminCannotCreateDocument() {
        assertThatThrownBy(() -> testee.create("d896140a-39c0-11e5-b7f9-080027b8274b", getShareCreationDto()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

    @Test
    @WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") // Amy's uuid (simple)
    public void userCannotCreateDocument() {
        assertThatThrownBy(() -> testee.create("aebe1b64-39c0-11e5-9fa8-080027b8254j", getShareCreationDto()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

    @Test
    @WithMockUser(TECHNICAL_USER_CREATE_SHARE)
    public void technicalUserCanCreateDocumentWithPermissions() throws IOException {
        Set<ShareDto> shares = testee.create("aebe1b64-39c0-11e5-9fa8-080027b8254j", getShareCreationDto());
        assertThat(shares).isNotNull();
        assertThat(shares.size()).isEqualTo(2);
    }

    @Test
    @WithMockUser(TECHNICAL_USER_CREATE_NODE)
    public void technicalUserCannotCreateDocumentWithWrongPermissions() {
        assertThatThrownBy(() -> testee.create(TECHNICAL_USER_CREATE_NODE, getShareCreationDto()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to create an entry.");
    }

    @Test
    @WithMockUser(TECHNICAL_USER_NONE)
    public void technicalUserCannotCreateDocumentWithoutPermissions() {
        assertThatThrownBy(() -> testee.create(TECHNICAL_USER_NONE, getShareCreationDto()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to create an entry.");
    }

    private ShareCreationDto getShareCreationDto() throws IOException {
        ShareCreationDto sc = new ShareCreationDto();
        sc.setDocuments(List.of("bfaf3fea-c64a-4ee0-bae8-b1482f1f6401", "fd87394a-41ab-11e5-b191-080027b8274b"));
        sc.setSubject("test subject");
        sc.setMessage("test share");
        sc.setSecured(false);
        sc.setRecipients(List.of(new GenericUserDto(john)));
        return sc;
    }

}