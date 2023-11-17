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

import javax.transaction.Transactional;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainLightDto;
import org.linagora.linshare.core.facade.webservice.common.dto.GuestDto;
import org.linagora.linshare.core.service.impl.DomainServiceImpl;
import org.linagora.linshare.core.service.impl.GuestServiceImpl;
import org.linagora.linshare.core.service.impl.UserServiceImpl;
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
@Sql({"/import-test-technical-users.sql"})
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
public class GuestDelegationRestServiceImplTest {

	public static final String TECHNICAL_USER_CREATE_GUEST = "technical.create.guest@linshare.org";
	public static final String TECHNICAL_USER_CREATE_NODE = "technical.create.node@linshare.org";
	public static final String TECHNICAL_USER_NONE = "technical.none@linshare.org";
	public static final String GUEST_UUID = "46455499-f703-46a2-9659-24ed0fa0d63c";

	@Autowired
    private DomainServiceImpl domainService;

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private GuestServiceImpl guestService;

    @Autowired
    private GuestRestServiceImpl testee;

    private DomainLightDto topDomain2Dto;

    private DomainLightDto topDomainDto;

    private User root;

    private User adminUser;

    private User simpleUser;
    private Guest guestUser;

    @BeforeEach
    public void setUp() {
        root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
        topDomain2Dto = new DomainLightDto(domainService.find(root, LinShareTestConstants.TOP_DOMAIN2));
        topDomainDto = new DomainLightDto(domainService.find(root, LinShareTestConstants.TOP_DOMAIN));
        adminUser = userService.findUserInDB(topDomainDto.getUuid(), LinShareTestConstants.JANE_ACCOUNT);
        simpleUser = userService.findUserInDB(topDomainDto.getUuid(), LinShareTestConstants.JOHN_ACCOUNT);
        guestUser = guestService.find(root, root, GUEST_UUID);
    }

	@Test
	@WithMockUser(TECHNICAL_USER_CREATE_GUEST)
	public void technicalUserCanCreateGuestWithPermissions() {
		GuestDto guestDto = getGuestDto();

		GuestDto createdGuestDto = testee.create(adminUser.getLsUuid(), guestDto);

		assertThat(createdGuestDto).isNotNull();
		assertThat(createdGuestDto.getUuid()).isNotBlank();
		//mail should be sanitized
		assertThat(createdGuestDto.getMail()).isEqualTo("newguest@linshare.org");
		//names should be capitalized
		assertThat(createdGuestDto.getLastName()).isEqualTo("Test");
		Guest guestInDb = guestService.find(root, root, createdGuestDto.getUuid());
		assertThat(guestInDb).isNotNull();
		compareGuestToDto(guestInDb, createdGuestDto);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void rootCannotCreateGuest() {
		assertThatThrownBy(() -> testee.create(adminUser.getLsUuid(), getGuestDto()))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not authorized to use this service");
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void adminCannotCreateGuest() {
		assertThatThrownBy(() -> testee.create(adminUser.getLsUuid(), getGuestDto()))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not authorized to use this service");
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") // Amy's uuid (simple)
	public void userCannotCreateGuest() {
		assertThatThrownBy(() -> testee.create(adminUser.getLsUuid(), getGuestDto()))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not authorized to use this service");
	}

	@Test
	@WithMockUser(TECHNICAL_USER_CREATE_NODE)
	public void technicalUserCannotCreateGuestWithWrongPermissions() {
		assertThatThrownBy(() -> testee.create(adminUser.getLsUuid(), getGuestDto()))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not authorized to create an entry.");
	}

	@Test
	@WithMockUser(TECHNICAL_USER_NONE)
	public void technicalUserCannotCreateGuestWithoutPermissions() {
		assertThatThrownBy(() -> testee.create(adminUser.getLsUuid(), getGuestDto()))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not authorized to create an entry.");
	}

	@NotNull
	private GuestDto getGuestDto() {
		GuestDto guestDto = GuestDto.toDto().apply(guestUser);
		guestDto.setUuid(null);
		guestDto.setMail("newGuest@linshare.org");
		guestDto.setLastName("test");
		return guestDto;
	}

	private static void compareGuestToDto(Guest expected, GuestDto actual) {
		assertThat(expected.getLsUuid()).isEqualTo(actual.getUuid());
		assertThat(expected.getFirstName()).isEqualTo(actual.getFirstName());
		assertThat(expected.getLastName()).isEqualTo(actual.getLastName());
		assertThat(expected.getMail()).isEqualTo(actual.getMail());
		assertThat(expected.getComment()).isEqualTo(actual.getComment());
		assertThat(expected.getExpirationDate()).isEqualTo(actual.getExpirationDate());
		assertThat(expected.getExternalMailLocale()).isEqualTo(actual.getExternalMailLocale());
		assertThat(expected.isCanUpload()).isEqualTo(actual.isCanUpload());
		assertThat(expected.isRestricted()).isEqualTo(actual.isRestricted());
	}
}