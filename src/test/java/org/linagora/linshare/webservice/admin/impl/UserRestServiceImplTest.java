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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;
import org.linagora.linshare.server.embedded.ldap.LdapServerRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@ExtendWith(LdapServerRule.class)
@Transactional
@Sql({ "/import-tests-check-inconsistent.sql"})
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
        "classpath:springContext-webservice-admin.xml",
        "classpath:springContext-facade-ws-admin.xml",
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
    public static final String GUEST_UUID = "46455499-f703-46a2-9659-24ed0fa0d63c";
    public static final String INTERNAL_UUID = "aebe1b64-39c0-11e5-9fa8-080027b8274b";
    @Autowired
    private UserRestServiceImpl testee;

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void cannotMoveInconsistentGuestOutsideGuestDomain() {
		UserDto userDto = new UserDto();
		userDto.setDomain(LinShareTestConstants.SUB_DOMAIN);
        userDto.setUuid(GUEST_UUID);

		assertThatThrownBy(() -> testee.updateInconsistent(userDto))
				.isInstanceOf(BusinessException.class)
				.hasMessage("Cannot move guest outside guest domain");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void cannotMoveInconsistentInternalToGuestDomain() {
		UserDto userDto = new UserDto();
		userDto.setDomain(LinShareTestConstants.GUEST_DOMAIN);
        userDto.setUuid(INTERNAL_UUID);

		assertThatThrownBy(() -> testee.updateInconsistent(userDto))
				.isInstanceOf(BusinessException.class)
				.hasMessage("Cannot move internal to guest domain");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getInconsistentUserShouldReturnDomainName() {
		Set<UserDto> allInconsistent = testee.findAllInconsistent();
		assertThat(allInconsistent).isNotNull();
		assertThat(allInconsistent).isNotEmpty();
		assertThat(allInconsistent)
				.withFailMessage("Incorrect or absent domain name")
				.allMatch(user -> StringUtils.isNotBlank(user.getDomainName()) && user.getDomainName().equals("MySubDomain"));
	}
}