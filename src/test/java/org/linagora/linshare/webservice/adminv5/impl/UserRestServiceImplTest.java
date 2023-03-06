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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;


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


	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void createUser() {
		// Given
		assertThat(userService.findUserInDB(topDomain2Dto.getUuid(), "rob.test@linshare.org")).isNull();
		UserDto userDto = new UserDto();
		userDto.setFirstName("Robert");
		userDto.setLastName("Testeur");
		userDto.setMail("rob.test@linshare.org");
		userDto.setRole(Role.SIMPLE);
		userDto.setCanUpload(true);
		userDto.setCanCreateGuest(true);
		userDto.setAccountType(AccountType.INTERNAL);
		userDto.setRestricted(false);
		userDto.setDomain(topDomain2Dto);
		userDto.setExternalMailLocale(Language.ENGLISH);
		userDto.setSecondFAEnabled(true);
		userDto.setSecondFAUuid("ef5c3f4f-db60-431f-a668-d3a82611902f");
		userDto.setLocked(false);

		// When
		UserDto createdUserDto = testee.create(userDto);

		// Then
		assertThat(createdUserDto).isNotNull();
		assertThat(createdUserDto.getUuid()).isNotBlank();
		compareUserDtos(userDto, createdUserDto);
		User userInDB = userService.findUserInDB(topDomain2Dto.getUuid(), "rob.test@linshare.org");
		assertThat(userInDB).isNotNull();
		compareUserToDto(userInDB, createdUserDto);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void createAdminUser() {
		// Given
		assertThat(userService.findUserInDB(topDomain2Dto.getUuid(), "rob.test@linshare.org")).isNull();
		UserDto userDto = new UserDto();
		userDto.setFirstName("Robert");
		userDto.setLastName("Testeur");
		userDto.setMail("rob.test@linshare.org");
		userDto.setRole(Role.ADMIN);
		userDto.setCanUpload(true);
		userDto.setCanCreateGuest(true);
		userDto.setAccountType(AccountType.INTERNAL);
		userDto.setRestricted(false);
		userDto.setDomain(topDomain2Dto);
		userDto.setExternalMailLocale(Language.ENGLISH);
		userDto.setSecondFAEnabled(true);
		userDto.setSecondFAUuid("ef5c3f4f-db60-431f-a668-d3a82611902f");
		userDto.setLocked(false);

		// When
		UserDto createdUserDto = testee.create(userDto);

		// Then
		assertThat(createdUserDto).isNotNull();
		assertThat(createdUserDto.getUuid()).isNotBlank();
		compareUserDtos(userDto, createdUserDto);
		User userInDB = userService.findUserInDB(topDomain2Dto.getUuid(), "rob.test@linshare.org");
		assertThat(userInDB).isNotNull();
		compareUserToDto(userInDB, createdUserDto);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void createUserMinimalInfo() {
		// Given
		assertThat(userService.findUserInDB(topDomain2Dto.getUuid(), "rob.test@linshare.org")).isNull();
		UserDto userDto = new UserDto();
		userDto.setMail("rob.test@linshare.org");
		userDto.setDomain(topDomain2Dto);
		userDto.setRole(Role.SIMPLE);

		// When
		UserDto createdUserDto = testee.create(userDto);

		// Then
		assertThat(createdUserDto.getFirstName()).isNull();
		assertThat(createdUserDto.getLastName()).isNull();
		assertThat(createdUserDto.getMail()).isEqualTo("rob.test@linshare.org");
		assertThat(createdUserDto.getRole()).isEqualTo(Role.SIMPLE);
		assertThat(createdUserDto.getAccountType()).isEqualTo(AccountType.INTERNAL);
		assertThat(createdUserDto.getDomain().getUuid()).isEqualTo(topDomain2Dto.getUuid());
		assertThat(createdUserDto.getDomain().getName()).isEqualTo(topDomain2Dto.getName());
		assertThat(createdUserDto.getExternalMailLocale()).isEqualTo(Language.ENGLISH);
		assertThat(createdUserDto.isCanUpload()).isTrue();
		assertThat(createdUserDto.isCanCreateGuest()).isTrue();
		assertThat(createdUserDto.isRestricted()).isFalse();
		assertThat(createdUserDto.isLocked()).isFalse();
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void createExistingUserDoesNotUpdate() {
		// Given
		User userInDBBefore = userService.findUserInDB(topDomain2Dto.getUuid(), "amy.wolsh@linshare.org");
		assertThat(userInDBBefore).isNotNull();
		UserDto userDto = new UserDto();
		userDto.setLastName("NotWolsh");
		userDto.setMail("amy.wolsh@linshare.org");
		userDto.setDomain(topDomain2Dto);
		userDto.setRole(Role.SIMPLE);

		// When
		UserDto createdUserDto = testee.create(userDto);

		// Then
		assertThat(createdUserDto).isNotNull();
		assertThat(createdUserDto.getUuid()).isNotBlank();
		assertThat(createdUserDto.getLastName()).isEqualTo("Bar2");
		compareUserToDto(userInDBBefore, createdUserDto);
		User userInDBAfter = userService.findUserInDB(topDomain2Dto.getUuid(), "amy.wolsh@linshare.org");
		assertThat(userInDBAfter).isNotNull();
		assertThat(userInDBBefore).isEqualTo(userInDBAfter);
		compareUserToDto(userInDBAfter, createdUserDto);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void createUserExistingInOtherDomain() {
		// Given
		User userInDBBefore = userService.findUserInDB(topDomainDto.getUuid(), "user1@linshare.org");
		assertThat(userInDBBefore).isNotNull();
		UserDto userDto = new UserDto();
		userDto.setLastName("Other John");
		userDto.setMail("user1@linshare.org");
		userDto.setDomain(topDomain2Dto);
		userDto.setRole(Role.SIMPLE);

		// When
		UserDto createdUserDto = testee.create(userDto);

		// Then
		assertThat(createdUserDto).isNotNull();
		assertThat(createdUserDto.getUuid()).isNotBlank();
		assertThat(createdUserDto.getLastName()).isEqualTo("Other John");
		assertThat(createdUserDto.getMail()).isEqualTo("user1@linshare.org");
		assertThat(createdUserDto.getDomain()).isEqualTo(topDomain2Dto);
		assertThat(createdUserDto.getUuid()).isNotEqualTo(userInDBBefore.getLsUuid());
		assertThat(userService.findUserInDB(topDomainDto.getUuid(), "user1@linshare.org")).isNotNull();
		User userInDBAfter = userService.findUserInDB(topDomain2Dto.getUuid(), "user1@linshare.org");
		assertThat(userInDBAfter).isNotNull();
		assertThat(userInDBBefore.getLsUuid()).isNotEqualTo(userInDBAfter.getLsUuid());
		assertThat(userInDBBefore).isNotEqualTo(userInDBAfter);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void createUserSuperadminCanCreateOutsideHisDomain() {
		// Given
		assertThat(userService.findUserInDB(topDomain2Dto.getUuid(), "rob.test@linshare.org")).isNull();
		UserDto userDto = new UserDto();
		userDto.setMail("rob.test@linshare.org");
		userDto.setDomain(topDomain2Dto);
		userDto.setRole(Role.SIMPLE);

		// When
		UserDto createdUserDto = testee.create(userDto);

		// Then
		assertThat(createdUserDto.getDomain().getUuid()).isEqualTo(topDomain2Dto.getUuid());
		assertThat(createdUserDto.getDomain().getName()).isEqualTo(topDomain2Dto.getName());
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") // Amy's uuid (simple)
	public void createRequiresAdminRightsOnRequestedDomain() {
		assertThatThrownBy(() -> testee.create(new UserDto()))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not authorized to use this service");
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void createFailsWithAdminRightsOnOtherDomain() {
		UserDto userDto = new UserDto();
		userDto.setMail("rob.test@linshare.org");
		userDto.setDomain(topDomain2Dto);
		userDto.setRole(Role.SIMPLE);

		assertThatThrownBy(() -> testee.create(userDto))
				.isInstanceOf(BusinessException.class)
				.hasMessage("As admin you can only create on your own domain : MyDomain");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void createUserMailIsMandatory() {
		// Given
		assertThat(userService.findUserInDB(topDomain2Dto.getUuid(), "rob.test@linshare.org")).isNull();
		UserDto userDto = new UserDto();
		userDto.setDomain(topDomain2Dto);
		userDto.setRole(Role.SIMPLE);

		// When / Then
		assertThatThrownBy(() -> testee.create(userDto))
				.isInstanceOf(BusinessException.class)
				.hasMessage("Domain, mail and role are mandatory to create an user.");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void createUserRoleIsMandatory() {
		// Given
		assertThat(userService.findUserInDB(topDomain2Dto.getUuid(), "rob.test@linshare.org")).isNull();
		UserDto userDto = new UserDto();
		userDto.setMail("rob.test@linshare.org");
		userDto.setDomain(topDomain2Dto);

		// When / Then
		assertThatThrownBy(() -> testee.create(userDto))
				.isInstanceOf(BusinessException.class)
				.hasMessage("Domain, mail and role are mandatory to create an user.");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void createUserDomainIsMandatory() {
		// Given
		assertThat(userService.findUserInDB(topDomain2Dto.getUuid(), "rob.test@linshare.org")).isNull();
		UserDto userDto = new UserDto();
		userDto.setMail("rob.test@linshare.org");
		userDto.setRole(Role.SIMPLE);

		// When / Then
		assertThatThrownBy(() -> testee.create(userDto))
				.isInstanceOf(BusinessException.class)
				.hasMessage("Domain, mail and role are mandatory to create an user.");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void deleteUserWithUid() {
		// Given
		User userInDB = userService.findUserInDB(topDomain2Dto.getUuid(), "amy.wolsh@linshare.org");
		assertThat(userInDB).isNotNull();

		// When
		testee.delete(null, userInDB.getLsUuid());

		// Then
		assertThat(userService.findUserInDB(topDomain2Dto.getUuid(), "amy.wolsh@linshare.org")).isNull();
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void deleteUserWithDto() {
		// Given
		User userInDB = userService.findUserInDB(topDomain2Dto.getUuid(), "amy.wolsh@linshare.org");
		assertThat(userInDB).isNotNull();
		UserDto userDto = new UserDto();
		userDto.setUuid(userInDB.getLsUuid());

		// When
		testee.delete(userDto, null);

		// Then
		assertThat(userService.findUserInDB(topDomain2Dto.getUuid(), "amy.wolsh@linshare.org")).isNull();
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void deleteUserAnyUuidIsMandatory() {
		// Given
		UserDto userDto = new UserDto();

		// When / Then
		assertThatThrownBy(() -> testee.delete(userDto, null))
				.isInstanceOf(NullPointerException.class)
				.hasMessage("Missing user's uuid");
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") //amy wolsh
	public void deleteUserForbiddenForSimpleUsers() {
		// Given / When / Then
		assertThatThrownBy(() -> testee.delete(null, null))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not authorized to use this service");
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") //admin uuid
	public void deleteUserAllowedForAdmin() {
		// Given
		User userInDB = userService.findByLsUuid(simpleUser.getLsUuid());
		assertThat(userInDB).isNotNull();

		// When
		testee.delete(null, userInDB.getLsUuid());

		// Then
		assertThatThrownBy(() -> userService.findByLsUuid(simpleUser.getLsUuid()))
				.isInstanceOf(BusinessException.class)
				.hasMessage("User with uuid : aebe1b64-39c0-11e5-9fa8-080027b8274b not found.");
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") //Amy Wolsh
	public void findUserForbiddenForSimpleUsers() {
		assertThatThrownBy(() -> testee.find(simpleUser.getLsUuid()))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not authorized to use this service");
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") //admin uuid
	public void findUserAllowedForAdmin() {
		UserDto userDto = testee.find(adminUser.getLsUuid());

		compareUserToDto(adminUser, userDto);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress) //admin uuid
	public void findUserAllowedForSuperAdmin() {
		UserDto userDto = testee.find(adminUser.getLsUuid());

		compareUserToDto(adminUser, userDto);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress) //admin uuid
	public void findAllUsers() {
		ImmutableList<UserDto> usersDto = testee.findAll(
				Lists.newArrayList(topDomain2Dto.getUuid(), topDomainDto.getUuid()),null,
				"ASC","modificationDate",null,null,null,null,
				null,null,null,null,null,null,
				null,null,null).readEntity(ImmutableList.class);

		assertThat(usersDto.stream().map(UserDto::getMail).collect(Collectors.toList())).containsExactlyInAnyOrder(
				"user1@linshare.org","user2@linshare.org","user3@linshare.org","amy.wolsh@linshare.org"
		);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress) //admin uuid
	public void findAllUsersSearchMail() {
		ImmutableList<UserDto> usersDto = testee.findAll(
				Lists.newArrayList(topDomain2Dto.getUuid(), topDomainDto.getUuid()),null,
				"ASC","modificationDate","amy",null,null,null,
				null,null,null,null,null,null,
				null,null,null).readEntity(ImmutableList.class);

		assertThat(usersDto.stream().map(UserDto::getMail).collect(Collectors.toList()))
				.containsExactlyInAnyOrder("amy.wolsh@linshare.org");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress) //admin uuid
	public void findAllUsersSearchByRole() {
		ImmutableList<UserDto> usersDto = testee.findAll(
				Lists.newArrayList(topDomain2Dto.getUuid(), topDomainDto.getUuid()),null,
				"ASC","modificationDate",null,null,null,null,
				null,null,"ADMIN",null,null,null,
				null,null,null).readEntity(ImmutableList.class);

		assertThat(usersDto.stream().map(UserDto::getMail).collect(Collectors.toList()))
				.containsExactlyInAnyOrder("user2@linshare.org");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress) //admin uuid
	public void findAllUsersAllDomains() {
		ImmutableList<UserDto> usersDto = testee.findAll(
				Lists.newArrayList(),null,
				"ASC","modificationDate",null,null,null,null,
				null,null,null,null,null,null,
				null,null,null).readEntity(ImmutableList.class);

		assertThat(usersDto.stream().map(UserDto::getMail).collect(Collectors.toList()))
				.containsExactlyInAnyOrder("guest@linshare.org",
						"amy.wolsh@linshare.org",
						"user2@linshare.org",
						"user1@linshare.org",
						"user3@linshare.org");
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") //admin uuid
	public void findAllUsersAllDomainsAsAdmin() {
		ImmutableList<UserDto> usersDto = testee.findAll(
				Lists.newArrayList(),null,
				"ASC","modificationDate",null,null,null,null,
				null,null,null,null,null,null,
				null,null,null).readEntity(ImmutableList.class);

		assertThat(usersDto.stream().map(UserDto::getMail).collect(Collectors.toList()))
				.containsExactlyInAnyOrder("guest@linshare.org",
						"user2@linshare.org",
						"user1@linshare.org",
						"user3@linshare.org");
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") //Amy Wolsh
	public void findAllUsersAllDomainsAsUserForbidden() {
		assertThatThrownBy(() -> testee.findAll(
				Lists.newArrayList(), null,
				"ASC", "modificationDate", null, null, null, null,
				null, null, null, null, null, null,
				null, null, null))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not authorized to use this service");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void updateUser() {
		// Given
		UserDto userDto = new UserDto();
		userDto.setUuid(adminUser.getLsUuid());
		userDto.setFirstName("Robert");
		userDto.setLastName("Testeur");
		userDto.setMail("rob.test@linshare.org");
		userDto.setRole(Role.SIMPLE);
		userDto.setCanUpload(true);
		userDto.setCanCreateGuest(true);
		userDto.setAccountType(AccountType.INTERNAL);
		userDto.setRestricted(false);
		userDto.setDomain(topDomain2Dto);
		userDto.setExternalMailLocale(Language.ENGLISH);
		userDto.setSecondFAEnabled(true);
		userDto.setSecondFAUuid("ef5c3f4f-db60-431f-a668-d3a82611902f");
		userDto.setLocked(false);

		// When
		UserDto updatedUserDto = testee.update(userDto, adminUser.getLsUuid());

		// Then
		// Editable Fields
		assertThat(updatedUserDto).isNotNull();
		assertThat(updatedUserDto.getUuid()).isNotBlank();
		assertThat(updatedUserDto.getFirstName()).isEqualTo(userDto.getFirstName());
		assertThat(updatedUserDto.getLastName()).isEqualTo(userDto.getLastName());
		assertThat(updatedUserDto.getRole()).isEqualTo(userDto.getRole());
		assertThat(updatedUserDto.getAccountType()).isEqualTo(userDto.getAccountType());
		assertThat(updatedUserDto.getExternalMailLocale()).isEqualTo(userDto.getExternalMailLocale());
		assertThat(updatedUserDto.isCanUpload()).isEqualTo(userDto.isCanUpload());
		assertThat(updatedUserDto.isCanCreateGuest()).isEqualTo(userDto.isCanCreateGuest());
		assertThat(updatedUserDto.isRestricted()).isEqualTo(userDto.isRestricted());
		assertThat(updatedUserDto.isLocked()).isEqualTo(userDto.isLocked());

		//Uneditable fields
		assertThat(updatedUserDto.getMail()).isNotEqualTo(userDto.getMail());
		assertThat(updatedUserDto.getDomain().getUuid()).isNotEqualTo(userDto.getDomain().getUuid());
		assertThat(updatedUserDto.getDomain().getName()).isNotEqualTo(userDto.getDomain().getName());
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void updateUserUuidIsMandatory() {
		UserDto userDto = UserDto.toDtoV5(simpleUser, null);
		userDto.setUuid(null);

		assertThatThrownBy(() -> testee.update(userDto, null))
				.isInstanceOf(NullPointerException.class)
				.hasMessage("user uuid must be set");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void updateUserUuidCanBeSetFromObject() {
		UserDto userDto = UserDto.toDtoV5(simpleUser, null);

		assertThat(testee.update(userDto, null)).isNotNull();
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void updateUserUuidCanBeSetFromPath() {
		UserDto userDto = UserDto.toDtoV5(simpleUser, null);
		userDto.setUuid(null);

		assertThat(testee.update(userDto, simpleUser.getLsUuid())).isNotNull();
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void updateUserIsLockedIsMandatory() {
		UserDto userDto = new UserDto();

		assertThatThrownBy(() -> testee.update(userDto,  simpleUser.getLsUuid()))
				.isInstanceOf(NullPointerException.class)
				.hasMessage("isLocked parameter should be set");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void updateUserDomainIsMandatory() {
		UserDto userDto = new UserDto();
		userDto.setLocked(false);

		assertThatThrownBy(() -> testee.update(userDto,  simpleUser.getLsUuid()))
				.isInstanceOf(NullPointerException.class)
				.hasMessage("domain parameter should be set");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void updateUserRoleIsMandatory() {
		UserDto userDto = new UserDto();
		userDto.setLocked(false);
		userDto.setDomain(new DomainLightDto(simpleUser.getDomain()));

		assertThatThrownBy(() -> testee.update(userDto,  simpleUser.getLsUuid()))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("The role of user must be set");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void updateUserMailLocaleIsMandatory() {
		UserDto userDto = new UserDto();
		userDto.setLocked(false);
		userDto.setDomain(new DomainLightDto(simpleUser.getDomain()));
		userDto.setRole(Role.SIMPLE);

		assertThatThrownBy(() -> testee.update(userDto,  simpleUser.getLsUuid()))
				.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void updateUserMinimalInfoDoesNotChangeEmptyFields() {
		UserDto userDto = new UserDto();
		userDto.setLocked(false);
		userDto.setDomain(new DomainLightDto(simpleUser.getDomain()));
		userDto.setRole(Role.SIMPLE);
		userDto.setExternalMailLocale(Language.ENGLISH);

		UserDto updatedUser = testee.update(userDto, simpleUser.getLsUuid());

		compareUserToDto(simpleUser, updatedUser);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void updateUserMailCannotBeChanged() {
		UserDto userDto = UserDto.toDtoV5(simpleUser, null);
		userDto.setMail("new.mail@mail.com");

		UserDto updatedUserDto = testee.update(userDto, adminUser.getLsUuid());

		assertThat(updatedUserDto.getMail()).isNotEqualTo(userDto.getMail());
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void updateUserDomainCannotBeChanged() {
		UserDto userDto = UserDto.toDtoV5(simpleUser, null);
		userDto.setDomain(topDomain2Dto);

		UserDto updatedUserDto = testee.update(userDto, adminUser.getLsUuid());

		assertThat(updatedUserDto.getDomain().getUuid()).isNotEqualTo(userDto.getDomain().getUuid());
		assertThat(updatedUserDto.getDomain().getName()).isNotEqualTo(userDto.getDomain().getName());
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void updateUserSecondFACannotBeChanged() {
		UserDto userDto = UserDto.toDtoV5(simpleUser, null);
		userDto.setSecondFAEnabled(true);

		UserDto updatedUserDto = testee.update(userDto, adminUser.getLsUuid());

		assertThat(userDto.isSecondFAEnabled()).isNotEqualTo(updatedUserDto.isSecondFAEnabled());
	}

	private static void compareUserDtos(UserDto expected, UserDto actual) {
		assertThat(expected.getFirstName()).isEqualTo(actual.getFirstName());
		assertThat(expected.getLastName()).isEqualTo(actual.getLastName());
		assertThat(expected.getMail()).isEqualTo(actual.getMail());
		assertThat(expected.getRole()).isEqualTo(actual.getRole());
		assertThat(expected.getAccountType()).isEqualTo(actual.getAccountType());
		assertThat(expected.getDomain().getUuid()).isEqualTo(actual.getDomain().getUuid());
		assertThat(expected.getDomain().getName()).isEqualTo(actual.getDomain().getName());
		assertThat(expected.getExternalMailLocale()).isEqualTo(actual.getExternalMailLocale());
		assertThat(expected.isCanUpload()).isEqualTo(actual.isCanUpload());
		assertThat(expected.isCanCreateGuest()).isEqualTo(actual.isCanCreateGuest());
		assertThat(expected.isRestricted()).isEqualTo(actual.isRestricted());
		assertThat(expected.isLocked()).isEqualTo(actual.isLocked());
	}

	private static void compareUserToDto(User expected, UserDto actual) {
		assertThat(expected.getLsUuid()).isEqualTo(actual.getUuid());
		assertThat(expected.getFirstName()).isEqualTo(actual.getFirstName());
		assertThat(expected.getLastName()).isEqualTo(actual.getLastName());
		assertThat(expected.getMail()).isEqualTo(actual.getMail());
		assertThat(expected.getRole()).isEqualTo(actual.getRole());
		assertThat(expected.getAccountType()).isEqualTo(actual.getAccountType());
		assertThat(expected.getDomain().getUuid()).isEqualTo(actual.getDomain().getUuid());
		assertThat(expected.getDomain().getLabel()).isEqualTo(actual.getDomain().getName());
		assertThat(expected.getExternalMailLocale()).isEqualTo(actual.getExternalMailLocale());
		assertThat(expected.isCanUpload()).isEqualTo(actual.isCanUpload());
		assertThat(expected.isCanCreateGuest()).isEqualTo(actual.isCanCreateGuest());
		assertThat(expected.isRestricted()).isEqualTo(actual.isRestricted());
		assertThat(expected.isLocked()).isEqualTo(actual.isLocked());
	}

}