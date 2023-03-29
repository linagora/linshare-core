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
package org.linagora.linshare.webservice.user.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.dto.UserAutoCompleteResultDto;
import org.linagora.linshare.server.embedded.ldap.LdapServerRule;
import org.linagora.linshare.webservice.userv2.impl.AutoCompleteRestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@SuppressWarnings("unchecked") //For lists casting
@ExtendWith(SpringExtension.class)
@ExtendWith(LdapServerRule.class)
@Transactional
@Sql({ "/import-tests-make-user2-admin.sql"})
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
        "classpath:springContext-webservice-userv2.xml",
        "classpath:springContext-test.xml" })
public class AutoCompleteRestServiceImplTest {

    @Autowired
    private AutoCompleteRestServiceImpl testee;

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void autoCompleteEmptyPatternIsForbidden() {
        assertThatThrownBy(() -> testee.autoComplete("", "USERS", null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Pattern must be set.");
    }

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void autoCompleteOnPattern() {
        List<UserAutoCompleteResultDto> userSet = (List<UserAutoCompleteResultDto>)(List<?>)
                testee.autoComplete("son", "USERS", null, null);

        assertThat(userSet).isNotNull();
        assertThat(userSet.stream().map(UserAutoCompleteResultDto::getMail)).containsExactlyInAnyOrder(
                "peter.wilson@linshare.org",
                "dawson.waterfield@linshare.org",
                "anderson.waxman@linshare.org");
    }

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void autoCompleteOnGuest() {
        List<UserAutoCompleteResultDto> userSet = (List<UserAutoCompleteResultDto>)(List<?>)
                testee.autoComplete("linshare", "USERS", "GUEST", null);

        assertThat(userSet).isNotNull();
        assertThat(userSet.stream().map(UserAutoCompleteResultDto::getMail)).containsExactlyInAnyOrder(
				"guest@linshare.org");
    }

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void autoCompleteOnInternals() {
        List<UserAutoCompleteResultDto> userSet = (List<UserAutoCompleteResultDto>)(List<?>)
                testee.autoComplete("linshare", "USERS", "INTERNAL", null);

        assertThat(userSet).isNotNull();
        assertThat(userSet.stream().map(UserAutoCompleteResultDto::getMail)).containsExactlyInAnyOrder(
                "user1@linshare.org",
                "user2@linshare.org",
                "user3@linshare.org",
                "user4@linshare.org",
                "user5@linshare.org",
                "user6@linshare.org",
                "user7@linshare.org",
                "abbey.curry@linshare.org",
                "cornell.able@linshare.org",
                "peter.wilson@linshare.org",
                "anderson.waxman@linshare.org",
                "amy.wolsh@linshare.org",
                "ldap.dude@linshare.org",
                "dawson.waterfield@linshare.org");
    }

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void autoCompleteUnknownAccountType() {
        assertThatThrownBy(() -> testee.autoComplete("linshare", "USERS", "ADMIN", null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Unknown account type");
    }

}