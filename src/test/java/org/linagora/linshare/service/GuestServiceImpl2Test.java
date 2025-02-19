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

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.FunctionalityNames;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Moderator;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.ModeratorService;
import org.linagora.linshare.server.embedded.ldap.LdapServerRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;


@ExtendWith(SpringExtension.class)
@ExtendWith(LdapServerRule.class)
@Sql({ "/import-tests-fake-domains.sql" })
@Transactional
@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml" })
@DirtiesContext
public class GuestServiceImpl2Test {

	private static Logger logger = LoggerFactory
			.getLogger(GuestServiceImpl2Test.class);

	@Autowired
	private GuestService guestService;

	@Autowired
	private FunctionalityService functionalityService;

	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;

	@Autowired
	private ModeratorService moderatorService;

	private User root;

	private User john;

	private User jane;
	
	private User foo;

	private Guest johnGuest;

	private Guest janeGuest;

	private Guest fooGuest;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		root = userRepository.findByDomainAndMail(LinShareTestConstants.ROOT_DOMAIN, LinShareTestConstants.ROOT_ACCOUNT);
		john = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		jane = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
		foo = userRepository.findByMail(LinShareTestConstants.FOO_ACCOUNT);
		Functionality functionality = functionalityService.find(
				root, LoadingServiceTestDatas.sqlSubDomain,
				FunctionalityNames.GUESTS.toString());
		functionality.getActivationPolicy().setStatus(true);
		functionalityService.update(root, LinShareTestConstants.TOP_DOMAIN,
				functionality);
		johnGuest = new Guest("Guest", "Doe", "guest1@linshare.org");
		johnGuest.setCmisLocale("en");
		johnGuest = guestService.create(john, john, johnGuest, null);
		assertThat(johnGuest).isNotNull();
		List<Moderator> johnGuestModerators = moderatorService.findAllByGuest(root, root, johnGuest.getLsUuid(), null, null);
		assertThat(john).isEqualTo(johnGuestModerators.get(0).getAccount());
		janeGuest = new Guest("Guest", "Smith", "guest2@linshare.org");
		janeGuest.setCmisLocale("en");
		janeGuest = guestService.create(jane, jane, janeGuest, null);
		assertThat(janeGuest).isNotNull();
		Moderator johnModerator =new Moderator(ModeratorRole.SIMPLE, john, janeGuest);
		johnModerator = moderatorService.create(jane, jane, johnModerator, false);
		List<Moderator> janeGuestModerators = moderatorService.findAllByGuest(root, root, janeGuest.getLsUuid(), null, null);
		assertThat(janeGuestModerators.size()).isEqualTo(2);
		fooGuest = new Guest("Guest", "Bar", "guest3@linshare.org");
		fooGuest.setCmisLocale("en");
		fooGuest = guestService.create(foo, foo, fooGuest, null);
		assertThat(fooGuest).isNotNull();
		List<Moderator> fooGuestModerators = moderatorService.findAllByGuest(root, root, fooGuest.getLsUuid(), null, null);
		assertThat(foo).isEqualTo(fooGuestModerators.get(0).getAccount());
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		guestService.delete(root, root, johnGuest.getLsUuid());
		guestService.delete(root, root, janeGuest.getLsUuid());
		guestService.delete(root, root, fooGuest.getLsUuid());
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testFindAllGuests() throws BusinessException {
		// We will return all guests
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Optional<String> pattern = Optional.empty();
		Optional<ModeratorRole> role = Optional.empty();
		Optional<User> moderator = Optional.empty();
		List<Guest> guests = guestService.findAll(root, root, moderator, pattern, role);
		assertThat(guests.containsAll(Lists.newArrayList(johnGuest, janeGuest, fooGuest)));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindAllGuestsWhereModeratorOf() throws BusinessException {
		// We will return all guests where john is moderator of with ADMIN and SIMPLE role
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Optional<String> pattern = Optional.empty();
		Optional<ModeratorRole> role = Optional.empty();
		Optional<User> moderator = Optional.ofNullable(john);
		List<Guest> johnGuests = guestService.findAll(root, root, moderator, pattern, role);
		assertThat(johnGuests.size()).isEqualTo(2);
		assertThat(johnGuests.containsAll(Lists.newArrayList(johnGuest, janeGuest)));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindAllGuestsByAdminRole() throws BusinessException {
		// We will return guests where John is moderator of with ADMIN role
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Optional<String> pattern = Optional.empty();
		Optional<ModeratorRole> role = Optional.ofNullable(ModeratorRole.ADMIN);
		Optional<User> moderator = Optional.ofNullable(john);
		List<Guest> johnGuests = guestService.findAll(root, root, moderator, pattern, role);
		assertThat(johnGuests.size()).isEqualTo(1);
		assertThat(johnGuests.get(0)).isEqualTo(johnGuest);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindAllGuestsBySimpleRole() throws BusinessException {
		// We will return guests where John is moderator of with SIMPLE role
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Optional<String> pattern = Optional.empty();
		Optional<ModeratorRole> role = Optional.ofNullable(ModeratorRole.SIMPLE);
		Optional<User> moderator = Optional.ofNullable(john);
		List<Guest> johnGuests = guestService.findAll(root, root, moderator, pattern, role);
		assertThat(johnGuests.size()).isEqualTo(1);
		assertThat(johnGuests.get(0)).isEqualTo(janeGuest);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testSearchAllGuestsByPattern() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Optional<String> pattern = Optional.empty();
		Optional<ModeratorRole> role = Optional.empty();
		Optional<User> moderator = Optional.empty();
		// We will return all guests
		List<Guest> guests = guestService.findAll(root, root, moderator, pattern, role);
		assertThat(guests.containsAll(Lists.newArrayList(johnGuest, janeGuest, fooGuest)));
		// Filter returned guests by pattern
		pattern = Optional.of("Bar");
		List<Guest> johnGuests = guestService.findAll(root, root, moderator, pattern, role);
		assertThat(johnGuests.size()).isEqualTo(1);
		assertThat(johnGuests.get(0)).isEqualTo(fooGuest);
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
