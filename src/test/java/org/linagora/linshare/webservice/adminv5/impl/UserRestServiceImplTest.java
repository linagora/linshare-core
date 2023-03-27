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

import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainLightDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.UserDto;
import org.linagora.linshare.core.service.impl.DomainServiceImpl;
import org.linagora.linshare.core.service.impl.UserServiceImpl;
import org.linagora.linshare.server.embedded.ldap.LdapServerRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList;


@ExtendWith(SpringExtension.class)
@ExtendWith(LdapServerRule.class)
@Transactional
@Sql({ "/import-tests-make-user2-admin.sql"})
//		"/import-tests-guests.sql" })
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
public class UserRestServiceImplTest {

    @Autowired
    private DomainServiceImpl domainService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRestServiceImpl testee;

    private DomainLightDto topDomain2Dto;

    private DomainLightDto topDomainDto;

    private User root;

    private User adminUser;

    private User simpleUser;

    @BeforeEach
    public void setUp() {
        root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
        topDomain2Dto = new DomainLightDto(domainService.find(root, LinShareTestConstants.TOP_DOMAIN2));
        topDomainDto = new DomainLightDto(domainService.find(root, LinShareTestConstants.TOP_DOMAIN));
        adminUser = userService.findUserInDB(topDomainDto.getUuid(), LinShareTestConstants.JANE_ACCOUNT);
        simpleUser = userService.findUserInDB(topDomainDto.getUuid(), LinShareTestConstants.JOHN_ACCOUNT);
    }

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void autocompleteEmptyPattern() {
        Set<UserDto> userSet = testee.autocomplete("", null, null);

        assertThat(userSet).isNotNull();
        assertThat(userSet.stream().map(UserDto::getMail)).containsExactlyInAnyOrder(
                "user1@linshare.org",
                "user2@linshare.org",
                "user3@linshare.org",
                "user4@linshare.org",
                "user5@linshare.org",
                "user6@linshare.org",
                "user7@linshare.org",
                "guest@linshare.org",
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
	public void autocompleteOnPattern() {
        Set<UserDto> userSet = testee.autocomplete("wa", null, null);

        assertThat(userSet).isNotNull();
        assertThat(userSet.stream().map(UserDto::getMail)).containsExactlyInAnyOrder(
				"user6@linshare.org", //Bruce Wane
                "anderson.waxman@linshare.org",
                "dawson.waterfield@linshare.org");
    }

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void autocompleteOnDomainAndType() {
        Set<UserDto> userSet = testee.autocomplete("linshare", "INTERNAL", simpleUser.getDomainId());

        assertThat(userSet).isNotNull();
        assertThat(userSet.stream().map(UserDto::getMail)).containsExactlyInAnyOrder(
                "user1@linshare.org",
                "user2@linshare.org",
                "user3@linshare.org",
                "user4@linshare.org",
                "user5@linshare.org",
                "user6@linshare.org",
                "user7@linshare.org");
    }

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void autocompleteOnDomain() {
        Set<UserDto> userSet = testee.autocomplete("linshare", null, simpleUser.getDomainId());

        assertThat(userSet).isNotNull();
        assertThat(userSet.stream().map(UserDto::getMail)).containsExactlyInAnyOrder(
                "user1@linshare.org",
                "user2@linshare.org",
                "user3@linshare.org",
                "user4@linshare.org",
                "user5@linshare.org",
                "user6@linshare.org",
                "user7@linshare.org",
                "guest@linshare.org");
    }

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void autocompleteOnGuest() {
        Set<UserDto> userSet = testee.autocomplete("linshare", "GUEST", null);

        assertThat(userSet).isNotNull();
        assertThat(userSet.stream().map(UserDto::getMail)).containsExactlyInAnyOrder(
				"guest@linshare.org");
    }

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void autocompleteOnInternals() {
        Set<UserDto> userSet = testee.autocomplete("linshare", "INTERNAL", null);

        assertThat(userSet).isNotNull();
        assertThat(userSet.stream().map(UserDto::getMail)).containsExactlyInAnyOrder(
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
	public void autocompleteUnknownAccountType() {
        assertThatThrownBy(() -> testee.autocomplete("linshare", "ADMIN", null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Unknown account type");
    }

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void autocompleteUnknownDomain() {
        assertThatThrownBy(() -> testee.autocomplete("linshare", null, "wrong id"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("The current domain does not exist : wrong id");
    }

}