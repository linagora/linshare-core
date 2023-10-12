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
package org.linagora.linshare.webservice.admin.impl;

import static org.assertj.core.api.Assertions.assertThat;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.facade.webservice.common.dto.TechnicalAccountDto;
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
        "classpath:springContext-test.xml" })
public class TechnicalAccountRestServiceImplTest {

    public static final String TECHNICAL_LOCKED = "technical.locked@linshare.org";
    public static final String TECHNICAL_NONE = "technical.none@linshare.org";
    @Autowired
    private TechnicalAccountRestServiceImpl testee;

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void updateCanUnlockLockedUsers() {
        TechnicalAccountDto account = testee.find(TECHNICAL_LOCKED);
        assertThat(account.isLocked()).isTrue();
        account.setLocked(false);
        TechnicalAccountDto updatedAccount = testee.update(account);

        assertThat(updatedAccount.isLocked()).isNotNull();
        assertThat(updatedAccount.isLocked()).isFalse();
    }

    @Test
    @WithMockUser(LinShareConstants.defaultRootMailAddress)
    public void updateCannotLockUnlockedUsers() {
        TechnicalAccountDto account = testee.find(TECHNICAL_NONE);
        assertThat(account.isLocked()).isFalse();
        account.setLocked(true);
        TechnicalAccountDto updatedAccount = testee.update(account);

        assertThat(updatedAccount.isLocked()).isNotNull();
        assertThat(updatedAccount.isLocked()).isFalse();
    }

}

