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
package org.linagora.linshare.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import javax.annotation.Nonnull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.AccountLightDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainLightDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.ModeratorDto;
import org.linagora.linshare.core.facade.webservice.user.ModeratorFacade;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.server.embedded.ldap.LdapServerRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ExtendWith(LdapServerRule.class)
@Transactional
@Sql({ "/import-tests-make-user2-admin.sql" })
@ContextConfiguration(locations = {  "classpath:springContext-datasource.xml",
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
public class ModeratorFacadeImplTest {

	private static Logger logger = LoggerFactory.getLogger(ModeratorFacadeImplTest.class);

	@Autowired
	private ModeratorFacade testee;

	@Autowired
	private GuestRepository guestRepository;

	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;

	private Account root;

	private Account simpleUser;

	private Account adminUser;

	private Guest guest;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		simpleUser = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		adminUser = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
		guest = guestRepository.findByMail(LinShareTestConstants.GUEST_ACCOUNT);
		root = userRepository.findByLsUuid(LinShareTestConstants.ROOT_ACCOUNT);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	@WithMockUser(LinShareTestConstants.ROOT_ACCOUNT)
	public void testEitherModeratorUuidOrEmailAndDomainAreRequired() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		ModeratorDto moderatorDto = getTestSimpleModeratorDto(new AccountLightDto());

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			testee.create(simpleUser.getLsUuid(), guest.getLsUuid(), moderatorDto);
		});

		assertThat("Either moderator's account uuid or moderator's email and domain uuid pair should be set")
				.isEqualTo(exception.getMessage());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@WithMockUser(LinShareTestConstants.ROOT_ACCOUNT)
	public void testCreateModeratorFromUuidOnly() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		assertThat(guest.getModerators()).size().isEqualTo(0);
		AccountLightDto johnDto = new AccountLightDto();
		johnDto.setUuid(simpleUser.getLsUuid());
		ModeratorDto moderatorDto = getTestSimpleModeratorDto(johnDto);

		ModeratorDto moderator = testee.create(root.getLsUuid(), guest.getLsUuid(), moderatorDto);

		assertThat(moderator).isNotNull();
		assertThat(moderator.getAccount()).isEqualTo(new AccountLightDto(simpleUser));
		assertThat(moderator.getGuest()).isEqualTo(new AccountLightDto(guest));
		assertThat(guestRepository.findByLsUuid(moderator.getGuest().getUuid()).getModerators()).size()
				.isEqualTo(1);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@WithMockUser(LinShareTestConstants.ROOT_ACCOUNT)
	public void testCreateModeratorFromMailAndDomainOnExistingDbUser() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		assertThat(guest.getModerators()).size().isEqualTo(0);
		AccountLightDto johnDto = new AccountLightDto(simpleUser);
		johnDto.setUuid(null);
		ModeratorDto moderatorDto = getTestSimpleModeratorDto(johnDto);

		ModeratorDto moderator = testee.create(root.getLsUuid(), guest.getLsUuid(), moderatorDto);

		assertThat(moderator).isNotNull();
		assertThat(moderator.getAccount()).isEqualTo(new AccountLightDto(simpleUser));
		assertThat(moderator.getGuest()).isEqualTo(new AccountLightDto(guest));
		assertThat(guestRepository.findByLsUuid(moderator.getGuest().getUuid()).getModerators()).size()
				.isEqualTo(1);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@WithMockUser(LinShareTestConstants.ROOT_ACCOUNT)
	public void testCreateModeratorFromMailAndDomain() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		assertThat(userRepository.findByMail(LinShareTestConstants.FOO2_LDAP_ACCOUNT)).isNull();
		assertThat(guest.getModerators()).size().isEqualTo(0);
		AccountLightDto foo2AccountDto = new AccountLightDto();
		foo2AccountDto.setEmail(LinShareTestConstants.FOO2_LDAP_ACCOUNT);
		foo2AccountDto.setDomain(new DomainLightDto(simpleUser.getDomain()));
		ModeratorDto moderatorDto = getTestSimpleModeratorDto(foo2AccountDto);

		ModeratorDto moderator = testee.create(root.getLsUuid(), guest.getLsUuid(), moderatorDto);

		User createdUser = userRepository.findByMail(LinShareTestConstants.FOO2_LDAP_ACCOUNT);
		assertThat(createdUser).isNotNull();
		assertThat(moderator).isNotNull();
		assertThat(moderator.getAccount()).isEqualTo(new AccountLightDto(createdUser));
		assertThat(moderator.getGuest()).isEqualTo(new AccountLightDto(guest));
		assertThat(guestRepository.findByLsUuid(moderator.getGuest().getUuid()).getModerators()).size()
				.isEqualTo(1);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@WithMockUser(LinShareTestConstants.ROOT_ACCOUNT)
	public void testSuperAdminCreateSimpleModerator() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		assertThat(guest.getModerators()).size().isEqualTo(0);
		AccountLightDto johnDto = new AccountLightDto(simpleUser);
		ModeratorDto moderatorDto = getTestSimpleModeratorDto(johnDto);

		ModeratorDto moderator = testee.create(root.getLsUuid(), guest.getLsUuid(), moderatorDto);

		assertThat(moderator).isNotNull();
		assertThat(moderator.getAccount()).isEqualTo(johnDto);
		assertThat(moderator.getGuest()).isEqualTo(new AccountLightDto(guest));
		assertThat(guestRepository.findByLsUuid(moderator.getGuest().getUuid()).getModerators()).size()
				.isEqualTo(1);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@WithMockUser(LinShareTestConstants.ROOT_ACCOUNT)
	public void testSuperAdminCreateSimpleModeratorAbsentFromDb() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		assertThat(userRepository.findByMail(LinShareTestConstants.FOO2_LDAP_ACCOUNT)).isNull();
		assertThat(guest.getModerators()).size().isEqualTo(0);
		AccountLightDto foo2AccountDto = new AccountLightDto();
		foo2AccountDto.setUuid("dummy_uuid");
		foo2AccountDto.setEmail(LinShareTestConstants.FOO2_LDAP_ACCOUNT);
		foo2AccountDto.setDomain(new DomainLightDto(simpleUser.getDomain()));
		ModeratorDto moderatorDto = getTestSimpleModeratorDto(foo2AccountDto);

		ModeratorDto moderator = testee.create(root.getLsUuid(), guest.getLsUuid(), moderatorDto);

		User createdUser = userRepository.findByMail(LinShareTestConstants.FOO2_LDAP_ACCOUNT);
		assertThat(createdUser).isNotNull();
		assertThat(moderator).isNotNull();
		assertThat(moderator.getAccount()).isEqualTo(new AccountLightDto(createdUser));
		assertThat(moderator.getGuest()).isEqualTo(new AccountLightDto(guest));
		assertThat(guestRepository.findByLsUuid(moderator.getGuest().getUuid()).getModerators()).size()
				.isEqualTo(1);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8274b") // John's uuid (simple)
	public void testSimpleUserCreateSimpleModeratorAbsentFromDbIsForbidden() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		assertThat(userRepository.findByMail(LinShareTestConstants.FOO2_LDAP_ACCOUNT)).isNull();
		assertThat(guest.getModerators()).size().isEqualTo(0);
		AccountLightDto foo2AccountDto = new AccountLightDto();
		foo2AccountDto.setUuid("dummy_uuid");
		foo2AccountDto.setEmail(LinShareTestConstants.FOO2_LDAP_ACCOUNT);
		foo2AccountDto.setDomain(new DomainLightDto(simpleUser.getDomain()));
		ModeratorDto moderatorDto = getTestSimpleModeratorDto(foo2AccountDto);

		BusinessException exception = assertThrows(BusinessException.class, () -> {
			testee.create(simpleUser.getLsUuid(), guest.getLsUuid(), moderatorDto);
		});

		assertThat("You are not authorized to create an entry.").isEqualTo(exception.getMessage());
		assertThat(guestRepository.findByLsUuid(guest.getLsUuid()).getModerators()).size()
				.isEqualTo(0);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8274b") // John's uuid (simple)
	public void testSimpleUserCreateSimpleModeratorIsForbidden() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		assertThat(guest.getModerators()).size().isEqualTo(0);
		AccountLightDto johnDto = new AccountLightDto(simpleUser);
		ModeratorDto moderatorDto = getTestSimpleModeratorDto(johnDto);

		BusinessException exception = assertThrows(BusinessException.class, () -> {
			testee.create(simpleUser.getLsUuid(), guest.getLsUuid(), moderatorDto);
		});

		assertThat("You are not authorized to create an entry.").isEqualTo(exception.getMessage());
		assertThat(guestRepository.findByLsUuid(guest.getLsUuid()).getModerators()).size()
				.isEqualTo(0);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin)
	public void testAdminCreateSimpleModeratorAbsentFromDb() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		assertThat(userRepository.findByMail(LinShareTestConstants.FOO2_LDAP_ACCOUNT)).isNull();
		assertThat(guest.getModerators()).size().isEqualTo(0);
		AccountLightDto foo2AccountDto = new AccountLightDto();
		foo2AccountDto.setUuid("dummy_uuid");
		foo2AccountDto.setEmail(LinShareTestConstants.FOO2_LDAP_ACCOUNT);
		foo2AccountDto.setDomain(new DomainLightDto(adminUser.getDomain()));
		ModeratorDto moderatorDto = getTestSimpleModeratorDto(foo2AccountDto);

		ModeratorDto moderator = testee.create(adminUser.getLsUuid(), guest.getLsUuid(), moderatorDto);

		User createdUser = userRepository.findByMail(LinShareTestConstants.FOO2_LDAP_ACCOUNT);
		assertThat(createdUser).isNotNull();
		assertThat(moderator).isNotNull();
		assertThat(moderator.getAccount()).isEqualTo(new AccountLightDto(createdUser));
		assertThat(moderator.getGuest()).isEqualTo(new AccountLightDto(guest));
		assertThat(guestRepository.findByLsUuid(moderator.getGuest().getUuid()).getModerators()).size()
				.isEqualTo(1);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin)
	public void testAdminCreateSimpleModerator() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		assertThat(guest.getModerators()).size().isEqualTo(0);
		AccountLightDto johnDto = new AccountLightDto(simpleUser);
		ModeratorDto moderatorDto = getTestSimpleModeratorDto(johnDto);

		ModeratorDto moderator = testee.create(adminUser.getLsUuid(), guest.getLsUuid(), moderatorDto);

		assertThat(moderator).isNotNull();
		assertThat(moderator.getAccount()).isEqualTo(johnDto);
		assertThat(moderator.getGuest()).isEqualTo(new AccountLightDto(guest));
		assertThat(guestRepository.findByLsUuid(moderator.getGuest().getUuid()).getModerators()).size()
				.isEqualTo(1);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8274b") // John's uuid (simple)
	public void testAdminModeratorCreateSimpleModeratorAbsentFromDb() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
        // make john guest moderator
        assertThat(testee.create(adminUser.getLsUuid(), guest.getLsUuid(),
                getTestModeratorDto(new AccountLightDto(simpleUser), ModeratorRole.ADMIN)))
                .isNotNull();
		assertThat(userRepository.findByMail(LinShareTestConstants.FOO2_LDAP_ACCOUNT)).isNull();
		assertThat(guest.getModerators()).size().isEqualTo(1);
		AccountLightDto foo2AccountDto = new AccountLightDto();
		foo2AccountDto.setUuid("dummy_uuid");
		foo2AccountDto.setEmail(LinShareTestConstants.FOO2_LDAP_ACCOUNT);
		foo2AccountDto.setDomain(new DomainLightDto(adminUser.getDomain()));
		ModeratorDto moderatorDto = getTestSimpleModeratorDto(foo2AccountDto);

		ModeratorDto moderator = testee.create(simpleUser.getLsUuid(), guest.getLsUuid(), moderatorDto);

		User createdUser = userRepository.findByMail(LinShareTestConstants.FOO2_LDAP_ACCOUNT);
		assertThat(createdUser).isNotNull();
		assertThat(moderator).isNotNull();
		assertThat(moderator.getAccount()).isEqualTo(new AccountLightDto(createdUser));
		assertThat(moderator.getGuest()).isEqualTo(new AccountLightDto(guest));
		assertThat(guestRepository.findByLsUuid(moderator.getGuest().getUuid()).getModerators()).size()
				.isEqualTo(2);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8274b") // John's uuid (simple)
	public void testAdminModeratorCreateSimpleModerator() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
        // make john guest moderator
        assertThat(testee.create(adminUser.getLsUuid(), guest.getLsUuid(),
                getTestModeratorDto(new AccountLightDto(simpleUser), ModeratorRole.ADMIN)))
                .isNotNull();
		assertThat(guest.getModerators()).size().isEqualTo(1);
		AccountLightDto janeDto = new AccountLightDto(adminUser);
		ModeratorDto moderatorDto = getTestSimpleModeratorDto(janeDto);

		ModeratorDto moderator = testee.create(simpleUser.getLsUuid(), guest.getLsUuid(), moderatorDto);

		assertThat(moderator).isNotNull();
		assertThat(moderator.getAccount()).isEqualTo(janeDto);
		assertThat(moderator.getGuest()).isEqualTo(new AccountLightDto(guest));
		assertThat(guestRepository.findByLsUuid(moderator.getGuest().getUuid()).getModerators()).size()
				.isEqualTo(2);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8274b") // John's uuid (simple)
	public void testSimpleModeratorCreateSimpleModeratorAbsentFromDbIsForbidden() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
        // make john guest moderator
        assertThat(testee.create(adminUser.getLsUuid(), guest.getLsUuid(),
                getTestModeratorDto(new AccountLightDto(simpleUser), ModeratorRole.SIMPLE)))
                .isNotNull();
		assertThat(userRepository.findByMail(LinShareTestConstants.FOO2_LDAP_ACCOUNT)).isNull();
		assertThat(guest.getModerators()).size().isEqualTo(1);
		AccountLightDto foo2AccountDto = new AccountLightDto();
		foo2AccountDto.setUuid("dummy_uuid");
		foo2AccountDto.setEmail(LinShareTestConstants.FOO2_LDAP_ACCOUNT);
		foo2AccountDto.setDomain(new DomainLightDto(adminUser.getDomain()));
		ModeratorDto moderatorDto = getTestSimpleModeratorDto(foo2AccountDto);

		BusinessException exception = assertThrows(BusinessException.class, () -> {
			testee.create(simpleUser.getLsUuid(), guest.getLsUuid(), moderatorDto);
		});

		assertThat("You are not authorized to create an entry.").isEqualTo(exception.getMessage());
		assertThat(guestRepository.findByLsUuid(guest.getLsUuid()).getModerators()).size()
				.isEqualTo(1);
		logger.debug(LinShareTestConstants.END_TEST);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8274b") // John's uuid (simple)
	public void testSimpleModeratorCreateSimpleModeratorIsForbidden() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
        // make john guest moderator
        assertThat(testee.create(adminUser.getLsUuid(), guest.getLsUuid(),
                getTestModeratorDto(new AccountLightDto(simpleUser), ModeratorRole.SIMPLE)))
                .isNotNull();
		assertThat(guest.getModerators()).size().isEqualTo(1);
		AccountLightDto janeDto = new AccountLightDto(adminUser);
		ModeratorDto moderatorDto = getTestSimpleModeratorDto(janeDto);

		BusinessException exception = assertThrows(BusinessException.class, () -> {
			testee.create(simpleUser.getLsUuid(), guest.getLsUuid(), moderatorDto);
		});

		assertThat("You are not authorized to create an entry.").isEqualTo(exception.getMessage());
		assertThat(guestRepository.findByLsUuid(guest.getLsUuid()).getModerators()).size()
				.isEqualTo(1);
		logger.debug(LinShareTestConstants.END_TEST);
	}


	@Test
	@WithMockUser(LinShareTestConstants.ROOT_ACCOUNT)
	public void testSuperAdminCreateAdminModerator() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		assertThat(guest.getModerators()).size().isEqualTo(0);
		AccountLightDto johnDto = new AccountLightDto(simpleUser);
		ModeratorDto moderatorDto = getTestAdminModeratorDto(johnDto);

		ModeratorDto moderator = testee.create(root.getLsUuid(), guest.getLsUuid(), moderatorDto);

		assertThat(moderator).isNotNull();
		assertThat(moderator.getAccount()).isEqualTo(johnDto);
		assertThat(moderator.getGuest()).isEqualTo(new AccountLightDto(guest));
		assertThat(guestRepository.findByLsUuid(moderator.getGuest().getUuid()).getModerators()).size()
				.isEqualTo(1);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@WithMockUser(LinShareTestConstants.ROOT_ACCOUNT)
	public void testSuperAdminCreateAdminModeratorAbsentFromDb() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		assertThat(userRepository.findByMail(LinShareTestConstants.FOO2_LDAP_ACCOUNT)).isNull();
		assertThat(guest.getModerators()).size().isEqualTo(0);
		AccountLightDto foo2AccountDto = new AccountLightDto();
		foo2AccountDto.setUuid("dummy_uuid");
		foo2AccountDto.setEmail(LinShareTestConstants.FOO2_LDAP_ACCOUNT);
		foo2AccountDto.setDomain(new DomainLightDto(simpleUser.getDomain()));
		ModeratorDto moderatorDto = getTestAdminModeratorDto(foo2AccountDto);

		ModeratorDto moderator = testee.create(root.getLsUuid(), guest.getLsUuid(), moderatorDto);

		User createdUser = userRepository.findByMail(LinShareTestConstants.FOO2_LDAP_ACCOUNT);
		assertThat(createdUser).isNotNull();
		assertThat(moderator).isNotNull();
		assertThat(moderator.getAccount()).isEqualTo(new AccountLightDto(createdUser));
		assertThat(moderator.getGuest()).isEqualTo(new AccountLightDto(guest));
		assertThat(guestRepository.findByLsUuid(moderator.getGuest().getUuid()).getModerators()).size()
				.isEqualTo(1);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8274b") // John's uuid (simple)
	public void testSimpleUserCreateAdminModeratorAbsentFromDbIsForbidden() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		assertThat(userRepository.findByMail(LinShareTestConstants.FOO2_LDAP_ACCOUNT)).isNull();
		assertThat(guest.getModerators()).size().isEqualTo(0);
		AccountLightDto foo2AccountDto = new AccountLightDto();
		foo2AccountDto.setUuid("dummy_uuid");
		foo2AccountDto.setEmail(LinShareTestConstants.FOO2_LDAP_ACCOUNT);
		foo2AccountDto.setDomain(new DomainLightDto(simpleUser.getDomain()));
		ModeratorDto moderatorDto = getTestAdminModeratorDto(foo2AccountDto);

		BusinessException exception = assertThrows(BusinessException.class, () -> {
			testee.create(simpleUser.getLsUuid(), guest.getLsUuid(), moderatorDto);
		});

		assertThat("You are not authorized to create an entry.").isEqualTo(exception.getMessage());
		assertThat(guestRepository.findByLsUuid(guest.getLsUuid()).getModerators()).size()
				.isEqualTo(0);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8274b") // John's uuid (simple)
	public void testSimpleUserCreateAdminModeratorIsForbidden() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		assertThat(guest.getModerators()).size().isEqualTo(0);
		AccountLightDto johnDto = new AccountLightDto(simpleUser);
		ModeratorDto moderatorDto = getTestAdminModeratorDto(johnDto);

		BusinessException exception = assertThrows(BusinessException.class, () -> {
			testee.create(simpleUser.getLsUuid(), guest.getLsUuid(), moderatorDto);
		});

		assertThat("You are not authorized to create an entry.").isEqualTo(exception.getMessage());
		assertThat(guestRepository.findByLsUuid(guest.getLsUuid()).getModerators()).size()
				.isEqualTo(0);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin)
	public void testAdminCreateAdminModeratorAbsentFromDb() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		assertThat(userRepository.findByMail(LinShareTestConstants.FOO2_LDAP_ACCOUNT)).isNull();
		assertThat(guest.getModerators()).size().isEqualTo(0);
		AccountLightDto foo2AccountDto = new AccountLightDto();
		foo2AccountDto.setUuid("dummy_uuid");
		foo2AccountDto.setEmail(LinShareTestConstants.FOO2_LDAP_ACCOUNT);
		foo2AccountDto.setDomain(new DomainLightDto(adminUser.getDomain()));
		ModeratorDto moderatorDto = getTestAdminModeratorDto(foo2AccountDto);

		ModeratorDto moderator = testee.create(adminUser.getLsUuid(), guest.getLsUuid(), moderatorDto);

		User createdUser = userRepository.findByMail(LinShareTestConstants.FOO2_LDAP_ACCOUNT);
		assertThat(createdUser).isNotNull();
		assertThat(moderator).isNotNull();
		assertThat(moderator.getAccount()).isEqualTo(new AccountLightDto(createdUser));
		assertThat(moderator.getGuest()).isEqualTo(new AccountLightDto(guest));
		assertThat(guestRepository.findByLsUuid(moderator.getGuest().getUuid()).getModerators()).size()
				.isEqualTo(1);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin)
	public void testAdminCreateAdminModerator() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		assertThat(guest.getModerators()).size().isEqualTo(0);
		AccountLightDto johnDto = new AccountLightDto(simpleUser);
		ModeratorDto moderatorDto = getTestAdminModeratorDto(johnDto);

		ModeratorDto moderator = testee.create(adminUser.getLsUuid(), guest.getLsUuid(), moderatorDto);

		assertThat(moderator).isNotNull();
		assertThat(moderator.getAccount()).isEqualTo(johnDto);
		assertThat(moderator.getGuest()).isEqualTo(new AccountLightDto(guest));
		assertThat(guestRepository.findByLsUuid(moderator.getGuest().getUuid()).getModerators()).size()
				.isEqualTo(1);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8274b") // John's uuid (simple)
	public void testAdminModeratorCreateAdminModeratorAbsentFromDb() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
        // make john guest moderator
        assertThat(testee.create(adminUser.getLsUuid(), guest.getLsUuid(),
                getTestModeratorDto(new AccountLightDto(simpleUser), ModeratorRole.ADMIN)))
                .isNotNull();
		assertThat(userRepository.findByMail(LinShareTestConstants.FOO2_LDAP_ACCOUNT)).isNull();
		assertThat(guest.getModerators()).size().isEqualTo(1);
		AccountLightDto foo2AccountDto = new AccountLightDto();
		foo2AccountDto.setUuid("dummy_uuid");
		foo2AccountDto.setEmail(LinShareTestConstants.FOO2_LDAP_ACCOUNT);
		foo2AccountDto.setDomain(new DomainLightDto(adminUser.getDomain()));
		ModeratorDto moderatorDto = getTestAdminModeratorDto(foo2AccountDto);

		ModeratorDto moderator = testee.create(simpleUser.getLsUuid(), guest.getLsUuid(), moderatorDto);

		User createdUser = userRepository.findByMail(LinShareTestConstants.FOO2_LDAP_ACCOUNT);
		assertThat(createdUser).isNotNull();
		assertThat(moderator).isNotNull();
		assertThat(moderator.getAccount()).isEqualTo(new AccountLightDto(createdUser));
		assertThat(moderator.getGuest()).isEqualTo(new AccountLightDto(guest));
		assertThat(guestRepository.findByLsUuid(moderator.getGuest().getUuid()).getModerators()).size()
				.isEqualTo(2);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8274b") // John's uuid (simple)
	public void testAdminModeratorCreateAdminModerator() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
        // make john guest moderator
        assertThat(testee.create(adminUser.getLsUuid(), guest.getLsUuid(),
                getTestModeratorDto(new AccountLightDto(simpleUser), ModeratorRole.ADMIN)))
                .isNotNull();
		assertThat(guest.getModerators()).size().isEqualTo(1);
		AccountLightDto janeDto = new AccountLightDto(adminUser);
		ModeratorDto moderatorDto = getTestAdminModeratorDto(janeDto);

		ModeratorDto moderator = testee.create(simpleUser.getLsUuid(), guest.getLsUuid(), moderatorDto);

		assertThat(moderator).isNotNull();
		assertThat(moderator.getAccount()).isEqualTo(janeDto);
		assertThat(moderator.getGuest()).isEqualTo(new AccountLightDto(guest));
		assertThat(guestRepository.findByLsUuid(moderator.getGuest().getUuid()).getModerators()).size()
				.isEqualTo(2);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8274b") // John's uuid (simple)
	public void testSimpleModeratorCreateAdminModeratorAbsentFromDbIsForbidden() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
        // make john guest moderator
        assertThat(testee.create(adminUser.getLsUuid(), guest.getLsUuid(),
                getTestModeratorDto(new AccountLightDto(simpleUser), ModeratorRole.SIMPLE)))
                .isNotNull();
		assertThat(userRepository.findByMail(LinShareTestConstants.FOO2_LDAP_ACCOUNT)).isNull();
		assertThat(guest.getModerators()).size().isEqualTo(1);
		AccountLightDto foo2AccountDto = new AccountLightDto();
		foo2AccountDto.setUuid("dummy_uuid");
		foo2AccountDto.setEmail(LinShareTestConstants.FOO2_LDAP_ACCOUNT);
		foo2AccountDto.setDomain(new DomainLightDto(adminUser.getDomain()));
		ModeratorDto moderatorDto = getTestAdminModeratorDto(foo2AccountDto);

		BusinessException exception = assertThrows(BusinessException.class, () -> {
			testee.create(simpleUser.getLsUuid(), guest.getLsUuid(), moderatorDto);
		});

		assertThat("You are not authorized to create an entry.").isEqualTo(exception.getMessage());
		assertThat(guestRepository.findByLsUuid(guest.getLsUuid()).getModerators()).size()
				.isEqualTo(1);
		logger.debug(LinShareTestConstants.END_TEST);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8274b") // John's uuid (simple)
	public void testSimpleModeratorCreateAdminModeratorIsForbidden() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
        // make john guest moderator
        assertThat(testee.create(adminUser.getLsUuid(), guest.getLsUuid(),
                getTestModeratorDto(new AccountLightDto(simpleUser), ModeratorRole.SIMPLE)))
                .isNotNull();
		assertThat(guest.getModerators()).size().isEqualTo(1);
		AccountLightDto janeDto = new AccountLightDto(adminUser);
		ModeratorDto moderatorDto = getTestAdminModeratorDto(janeDto);

		BusinessException exception = assertThrows(BusinessException.class, () -> {
			testee.create(simpleUser.getLsUuid(), guest.getLsUuid(), moderatorDto);
		});

		assertThat("You are not authorized to create an entry.").isEqualTo(exception.getMessage());
		assertThat(guestRepository.findByLsUuid(guest.getLsUuid()).getModerators()).size()
				.isEqualTo(1);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Nonnull
	private ModeratorDto getTestSimpleModeratorDto(AccountLightDto modAccount) {
		return getTestModeratorDto(modAccount, ModeratorRole.SIMPLE);
	}
	
	@Nonnull
	private ModeratorDto getTestAdminModeratorDto(AccountLightDto modAccount) {
		return getTestModeratorDto(modAccount, ModeratorRole.ADMIN);
	}

	@Nonnull
	private ModeratorDto getTestModeratorDto(AccountLightDto modAccount, ModeratorRole role) {
		return new ModeratorDto(
				"dummy_uuid",
				role,
				new Date(),
				new Date(),
				modAccount,
				new AccountLightDto(guest)
		);
	}

}
