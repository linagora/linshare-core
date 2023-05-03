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

import java.util.Set;

import javax.transaction.Transactional;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.TechnicalAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.impl.DomainServiceImpl;
import org.linagora.linshare.core.service.impl.LogEntryServiceImpl;
import org.linagora.linshare.core.service.impl.UserServiceImpl;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.logs.UserAuditLogEntry;
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
public class AuditLogEntryDelegationRestServiceImplTest {

    public static final String TECHNICAL_USER_AUDIT = "technical.audit@linshare.org";

    @Autowired
    private AuditLogEntryDelegationRestServiceImpl testee;

    @Autowired
    private DomainServiceImpl domainService;

    @Autowired
    private UserServiceImpl userService;

    private User root;

    @Autowired
    LogEntryServiceImpl logEntryService;

    @BeforeEach
    public void setUp() {
        root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
        AbstractDomain topDomain = domainService.find(root, LinShareTestConstants.TOP_DOMAIN);
        User john = userService.findUserInDB(topDomain.getUuid(), LinShareTestConstants.JOHN_ACCOUNT);

        logEntryService.insert(new UserAuditLogEntry(root, john, LogAction.CREATE, AuditLogEntryType.USER, john));
        logEntryService.insert(new UserAuditLogEntry(john, john, LogAction.UPDATE, AuditLogEntryType.USER, john));
    }

    @Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void rootCannotGetAudits() {
        assertThatThrownBy(() -> testee.findAll(root.getLsUuid(), null,null,true,null,null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

	@Test
    @WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void adminCannotGetAudits() {
        assertThatThrownBy(() -> testee.findAll("d896140a-39c0-11e5-b7f9-080027b8274b", null,null,true,null,null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

	@Test
    @WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") // Amy's uuid (simple)
	public void userCannotGetAudits() {
        assertThatThrownBy(() -> testee.findAll("aebe1b64-39c0-11e5-9fa8-080027b8254j", null,null,true,null,null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You are not authorized to use this service");
    }

	@Test
    @WithMockUser(TECHNICAL_USER_AUDIT)
	public void technicalUserCanGetAudits() {
        Set<AuditLogEntryUser> rootLogs = testee.findAll(root.getLsUuid(), null, null, true, null, null);
        assertThat(rootLogs).isNotNull();
        assertThat(rootLogs.size()).isEqualTo(1);
    }
}