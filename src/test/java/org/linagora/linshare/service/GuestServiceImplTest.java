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

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.PasswordService;
import org.linagora.linshare.core.business.service.impl.PasswordServiceImpl;
import org.linagora.linshare.core.domain.constants.FunctionalityNames;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.Moderator;
import org.linagora.linshare.core.domain.entities.PasswordHistory;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.ModeratorRepository;
import org.linagora.linshare.core.repository.PasswordHistoryRepository;
import org.linagora.linshare.core.repository.RootUserRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.InconsistentUserService;
import org.linagora.linshare.core.service.ModeratorService;
import org.linagora.linshare.core.service.QuotaService;
import org.linagora.linshare.core.service.ResetGuestPasswordService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.ResetGuestPassword;
import org.linagora.linshare.mongo.repository.ResetGuestPasswordMongoRepository;
import org.linagora.linshare.server.embedded.ldap.LdapServerRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class GuestServiceImplTest {

	private static Logger logger = LoggerFactory
			.getLogger(GuestServiceImplTest.class);

	private static String guestDomainName1 = "guestDomainName1";

	@Autowired
	private GuestService guestService;

	@Autowired
	private UserService userService;

	@Autowired
	private FunctionalityService functionalityService;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	@Autowired
	private RootUserRepository rootUserRepository;

	@Autowired
	private InconsistentUserService inconsistentUserService;
	
	@Autowired
	private PasswordService passwordService;

	@Autowired
	private PasswordServiceImpl passwordServiceImpl;

	@Autowired
	private QuotaService quotaService;

	@Autowired
	private PasswordHistoryRepository historyRepository;

	@Autowired
	private ResetGuestPasswordService resetGuestPasswordService;

	@Autowired
	private ResetGuestPasswordMongoRepository guestPasswordMongoRepository;

	@Autowired
	private FunctionalityReadOnlyService functionalityReadOnlyService;

	@Autowired
	private ModeratorService moderatorService;

	@Autowired
	private ModeratorRepository moderatorRepository;

	private User root;

	private User owner1;

	private User owner2;
	
	private User owner3;

	private Guest guest;

	private final static String FIRST_PASSWORD ="Root2017@linshare";
	private final static String SECOND_PASSWORD ="Root2018@linshare";
	private final static String THIRD_PASSWORD ="Root2019@linshare";
	private final static String LAST_PASSWORD ="Root2020@linshare";

	public GuestServiceImplTest() {
		super();
	}

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		root = rootUserRepository
				.findByLsUuid("root@localhost.localdomain@test");

		AbstractDomain subDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlSubDomain);
		owner1 = new Internal("John", "Doe", "user1@linshare.org", null);
		owner1.setDomain(subDomain);
		owner1.setRole(Role.ADMIN);
		owner1.setCanCreateGuest(true);
		owner1 = userService.saveOrUpdateUser(owner1, Optional.empty());

		owner2 = new Internal("Jane", "Smith", "user2@linshare.org", null);
		owner2.setDomain(subDomain);
		owner2.setCanCreateGuest(true);
		owner2.setRole(Role.SIMPLE);
		owner2 = userService.saveOrUpdateUser(owner2, Optional.empty());
		
		owner3 = new Internal("Jane", "Smith", "user4@linshare.org", null);
		owner3.setDomain(subDomain);
		owner3.setCanCreateGuest(true);
		owner3.setRole(Role.SIMPLE);
		owner3 = userService.saveOrUpdateUser(owner3, Optional.empty());

		Functionality functionality = functionalityService.find(
				root, LoadingServiceTestDatas.sqlSubDomain,
				FunctionalityNames.GUESTS.toString());
		functionality.getActivationPolicy().setStatus(true);
		functionalityService.update(root, LoadingServiceTestDatas.sqlSubDomain,
				functionality);
		guest = new Guest("Guest", "Doe", "guest1@linshare.org");
		guest.setCmisLocale("en");
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);

		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testCreateGuest() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Guest guest = new Guest("Guest", "Doe", "guest1@linshare.org");
		guest.setCmisLocale("en");
		guest = guestService.create(owner1, owner1, guest, null);
		Assertions.assertNotNull(guest);
		Assertions.assertEquals(Role.SIMPLE, guest.getRole());
		AccountQuota aq = quotaService.findByRelatedAccount(guest);
		Assertions.assertNotNull(aq.getDomainShared());
		Assertions.assertNotNull(aq.getMaxFileSize());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testForbidRootGuestCreation() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Guest guest = new Guest("Guest", "Doe", "guest1@linshare.org");
		guest.setCmisLocale("en");
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			guestService.create(root, root, guest, null);
		});
		Assertions.assertEquals(BusinessErrorCode.GUEST_FORBIDDEN, exception.getErrorCode());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateGuestSpecialCharacters() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Guest guest = new Guest("EP_TEST_v233<script>alert(document.cookie)</script>",
				"EP_TEST_v233<script>alert(document.cookie)</script>", "guest1@linshare.org");
		guest.setCmisLocale("en");
		guest = guestService.create(owner1, owner1, guest, null);
		Assertions.assertNotNull(guest);
		Assertions.assertEquals(Role.SIMPLE, guest.getRole());
		Assertions.assertEquals(guest.getFirstName(), "EP_TEST_v233");
		Assertions.assertEquals(guest.getLastName(), "EP_TEST_v233");
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateInternalAsGuest() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Guest guest = new Guest("Guest", "Doe", "user4@linshare.org");
		guest.setCmisLocale("en");
		Optional<String> pattern = Optional.empty();
		Optional<ModeratorRole> role = Optional.empty();
		List<Guest> findAll = guestService.findAll(owner1, owner1, Optional.empty(), pattern, role);
		int size = findAll.size();
		try {
			guest = guestService.create(owner1, owner1, guest, null);
		} catch (BusinessException e) {
			logger.debug("Can not create an internal user as guest");
		}
		Assertions.assertEquals(size, guestService.findAll(owner1, owner1, Optional.empty(), pattern, role).size());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testUpdateGuest() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Guest guest = new Guest("Guest", "Doe", "guest1@linshare.org");
		guest.setCmisLocale("en");
		guest = guestService.create(owner1, owner1, guest, null);
		AbstractDomain domain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlSubDomain);
	

		guest.setDomain(domain);
		guest.setFirstName("First");
		guest.setLastName("Last");
		Guest update = guestService.update(owner1, owner1, guest, null);
		Assertions.assertEquals(Role.SIMPLE, update.getRole());
		Assertions.assertEquals("First", update.getFirstName());
		Assertions.assertEquals("Last", update.getLastName());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testUpdateExpirationDateByAdmin() throws BusinessException {
		Guest guest = new Guest("Guest", "Doe", "guest1@linshare.org");
		guest.setCmisLocale("en");
		guest = guestService.create(owner2, owner2, guest, null);
		TimeUnitValueFunctionality func = functionalityReadOnlyService
				.getGuestsExpiration(owner1.getDomain());
		Calendar newExpiryDate = Calendar.getInstance();
		newExpiryDate.setTime(guest.getCreationDate());
		newExpiryDate.add(Calendar.MONTH, func.getMaxValue() + 2); // the new expiration date is over the maximum value setted in the functionality.
		guest.setExpirationDate(newExpiryDate.getTime());
		guest = guestService.update(root, root, guest, null);
		Assertions.assertEquals(newExpiryDate.getTime(), guest.getExpirationDate());
	}

	@Test
	public void testUpdateExpirationDateByUser() throws BusinessException {
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			Guest guest = new Guest("Guest", "Doe", "guest1@linshare.org");
			guest.setCmisLocale("en");
			guest = guestService.create(owner2, owner2, guest, null);
			TimeUnitValueFunctionality func = functionalityReadOnlyService
					.getGuestsExpiration(owner1.getDomain());
			Calendar newExpiryDate = Calendar.getInstance();
			newExpiryDate.setTime(guest.getCreationDate());
			newExpiryDate.add(Calendar.MONTH, func.getMaxValue() + 2); // the new expiration date is over the maximum value setted in the functionality.
			guest.setExpirationDate(newExpiryDate.getTime());
			guestService.update(owner2, owner2, guest, null);
		});
		Assertions.assertEquals(BusinessErrorCode.GUEST_EXPIRY_DATE_INVALID, exception.getErrorCode());
	}

	@Test
	public void testUpdateGuestSpecialCharacters() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Guest guest = new Guest("Guest", "Doe", "guest1@linshare.org");
		guest.setCmisLocale("en");
		guest = guestService.create(owner1, owner1, guest, null);
		AbstractDomain domain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlSubDomain);
		guest.setDomain(domain);
		guest.setFirstName("EP_TEST_v233<script>alert(document.cookie)</script>");
		guest.setLastName("EP_TEST_v233<script>alert(document.cookie)</script>");
		Guest update = guestService.update(owner1, owner1, guest, null);
		Assertions.assertEquals(Role.SIMPLE, update.getRole());
		Assertions.assertEquals(update.getFirstName(), "EP_TEST_v233");
		Assertions.assertEquals(update.getLastName(), "EP_TEST_v233");
		logger.debug(LinShareTestConstants.END_TEST);
	}


	@Test
	public void testChangePassword() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		guest = guestService.create(owner1, owner1, guest, null);
		Assertions.assertNotNull(guest);
		ResetGuestPassword reset = new ResetGuestPassword(guest);
		reset.setPassword(FIRST_PASSWORD);
		guestPasswordMongoRepository.save(reset);
		resetGuestPasswordService.update(guest, guest, reset);
		Assertions.assertTrue(passwordService.matches(FIRST_PASSWORD, guest.getPassword()));
		userService.changePassword(guest, guest, FIRST_PASSWORD, SECOND_PASSWORD);
		Assertions.assertTrue(passwordService.matches(SECOND_PASSWORD, guest.getPassword()));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testStoreOldAndNewPassword() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		guest = guestService.create(owner1, owner1, guest, null);
		Assertions.assertNotNull(guest);
		ResetGuestPassword reset = new ResetGuestPassword(guest);
		reset.setPassword(FIRST_PASSWORD);
		guestPasswordMongoRepository.save(reset);
		resetGuestPasswordService.update(guest, guest, reset);
		List<PasswordHistory> histories = historyRepository.findAllByAccount(guest);
		Assertions.assertTrue(histories.size() == 1);
		Assertions.assertEquals(histories.iterator().next().getPassword(), guest.getPassword());
		Assertions.assertTrue(passwordService.matches(FIRST_PASSWORD, guest.getPassword()));
		userService.changePassword(guest, guest, FIRST_PASSWORD, SECOND_PASSWORD);
		Assertions.assertTrue(passwordService.matches(SECOND_PASSWORD, guest.getPassword()));
		histories = historyRepository.findAllByAccount(guest);
		Assertions.assertTrue(histories.size() == 2);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testStorePasswordHistory() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		passwordServiceImpl.setMaxSavedPasswordNumber(3);
		guest = guestService.create(owner1, owner1, guest, null);
		Assertions.assertNotNull(guest);
		ResetGuestPassword reset = new ResetGuestPassword(guest);
		reset.setPassword(FIRST_PASSWORD);
		guestPasswordMongoRepository.save(reset);
		resetGuestPasswordService.update(guest, guest, reset);
		String firstHashedPassword = guest.getPassword();
		Assertions.assertTrue(passwordService.matches(FIRST_PASSWORD, guest.getPassword()));
		userService.changePassword(guest, guest, FIRST_PASSWORD, SECOND_PASSWORD);
		Assertions.assertTrue(passwordService.matches(SECOND_PASSWORD, guest.getPassword()));
		userService.changePassword(guest, guest, SECOND_PASSWORD, THIRD_PASSWORD);
		userService.changePassword(guest, guest, THIRD_PASSWORD, LAST_PASSWORD);
		List<PasswordHistory> histories = historyRepository.findAllByAccount(guest);
		for (PasswordHistory passwordHistory : histories) {
			Assertions.assertFalse(passwordHistory.getPassword().equals(firstHashedPassword));
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testChangeSamePasswordFail() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		guest = guestService.create(owner1, owner1, guest, null);
		Assertions.assertNotNull(guest);
		ResetGuestPassword reset = new ResetGuestPassword(guest);
		reset.setPassword(FIRST_PASSWORD);
		guestPasswordMongoRepository.save(reset);
		resetGuestPasswordService.update(guest, guest, reset);
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			userService.changePassword(guest, guest, FIRST_PASSWORD, FIRST_PASSWORD);
		});
		Assertions.assertEquals(BusinessErrorCode.RESET_ACCOUNT_PASSWORD_ALREADY_USED, exception.getErrorCode());
		Assertions.assertEquals(
				"The new password you entered is the same as your old passwords, Enter a different password please",
				exception.getMessage());
	}

	@Test
	public void testUpdateInconsistentDomain() {
		
		// create guest
		Guest guest = new Guest("Guest", "Doe", "guest1@linshare.org");
		guest.setCmisLocale("en");
		AbstractDomain guestDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlGuestDomain);
		guest.setDomain(guestDomain);
		guest = guestService.create(owner1, owner1, guest, null);
		Guest find = guestService.find(owner1, owner1, guest.getLsUuid());
		Assertions.assertNotNull(find);
		Assertions.assertEquals(Role.SIMPLE, find.getRole());
		
		// updateGuestDomain
		AbstractDomain domain = abstractDomainRepository.findById(guestDomainName1);
		inconsistentUserService.updateDomain(root, guest.getLsUuid(), domain.getUuid());

	}

	@Test
	public void testResetPassword() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// create guest
		Guest guest = new Guest("Foo", "Bar", "user10@linshare.org");
		String oldPassword = "password222";
		guest.setPassword(passwordService.encode(oldPassword));
		guest.setCmisLocale("en");
		guest = guestService.create(owner1, owner1, guest, null);
		guestService.triggerResetPassword(guest.getLsUuid());
		Assertions.assertFalse(passwordService.matches(oldPassword, guest.getPassword()));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateGuestWithContactRestriction()
			throws IllegalArgumentException, BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		Guest guest = new Guest("Guest", "Doe", "guest1@linshare.org");
		guest.setRestricted(true);
		guest.setCmisLocale("en");
		List<String> restrictedContacts = Lists.newArrayList();
		restrictedContacts.add("user3@linshare.org");
		restrictedContacts.add("user2@linshare.org");
		restrictedContacts.add("user1@linshare.org");
		guest = guestService.create(owner1, owner1, guest, restrictedContacts);
		Assertions.assertTrue(guest.isRestricted());
		Assertions.assertTrue(guest.isGuest());
		List<AllowedContact> ac = guestService.load(owner1, guest);
		Assertions.assertEquals(3, ac.size());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateGuestWithContactRestrictionAndErrors()
			throws IllegalArgumentException, BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		Guest guest = new Guest("Guest", "Doe", "guest1@linshare.org");
		guest.setRestricted(true);
		guest.setCmisLocale("en");
		List<String> restrictedContacts = Lists.newArrayList();
		restrictedContacts.add("user3@linshare.org");
		restrictedContacts.add("user2@linshare.org");
		// This one is not an internal or a guest user.So it will be skip.
		restrictedContacts.add("user-do-not-exist@linshare.org");
		guest = guestService.create(owner1, owner1, guest, restrictedContacts);
		Assertions.assertTrue(guest.isRestricted());
		Assertions.assertTrue(guest.isGuest());
		List<AllowedContact> ac = guestService.load(owner1, guest);
		Assertions.assertEquals(2, ac.size());
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testUpdateGuestWithContactRestriction()
			throws IllegalArgumentException, BusinessException,
			CloneNotSupportedException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		Guest guest = new Guest("Guest", "Doe", "guest1@linshare.org");
		guest.setRestricted(true);
		guest.setCmisLocale("en");
		List<String> restrictedContacts = Lists.newArrayList();
		restrictedContacts.add("user3@linshare.org");
		restrictedContacts.add("user2@linshare.org");
		restrictedContacts.add("user1@linshare.org");
		guest = guestService.create(owner1, owner1, guest, restrictedContacts);

		restrictedContacts = Lists.newArrayList();
		restrictedContacts.add("user1@linshare.org");
		guest = guestService.update(owner1, owner1, guest, restrictedContacts);
		List<AllowedContact> ac = guestService.load(owner1, guest);
		Assertions.assertEquals(1, ac.size());
		logger.debug(LinShareTestConstants.END_TEST);
	}
	@Test
	public void testGuestWithContactRestrictionRemovedRestriction()
			throws IllegalArgumentException, BusinessException,
			CloneNotSupportedException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		Guest guest = new Guest("Guest", "Doe", "guest1@linshare.org");
		guest.setRestricted(true);
		guest.setCmisLocale("en");
		List<String> restrictedContacts = Lists.newArrayList();
		restrictedContacts.add("user3@linshare.org");
		restrictedContacts.add("user2@linshare.org");
		restrictedContacts.add("user1@linshare.org");
		guest = guestService.create(owner1, owner1, guest, restrictedContacts);

		Assertions.assertTrue(guest.isRestricted());
		List<AllowedContact> ac = guestService.load(owner1, guest);
		Assertions.assertEquals(3, ac.size());

		guest.setRestricted(false);
		guest = guestService.update(owner1, owner1, guest, restrictedContacts);
		Assertions.assertFalse(guest.isRestricted());

		ac = guestService.load(owner1, guest);
		Assertions.assertEquals(0, ac.size());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testModeratorUpdateGuest() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Guest guest = new Guest("Guest", "Doe", "guest1@linshare.org");
		guest.setCmisLocale("en");
		guest = guestService.create(owner2, owner2, guest, null);
		Moderator moderator = new Moderator(ModeratorRole.ADMIN, owner3, guest);
		moderator = moderatorService.create(root, guest, moderator, true);
		assertThat(moderator).isNotNull();
		guest.setFirstName("First");
		guest.setLastName("Last");
		Guest update = guestService.update(owner2, owner3, guest, null);
		assertThat("Last").isEqualTo(update.getLastName());
		assertThat("First").isEqualTo(update.getFirstName());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testModeratorDeleteGuest() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Guest guest = new Guest("Guest", "Doe", "guest1@linshare.org");
		guest.setCmisLocale("en");
		guest = guestService.create(owner2, owner2, guest, null);
		Moderator moderator = new Moderator(ModeratorRole.ADMIN, owner3, guest);
		moderator = moderatorService.create(root, guest, moderator, true);
		assertThat(moderator).isNotNull();
		List<Moderator> moderators = moderatorService.findAllByGuest(owner2, owner2, guest.getLsUuid(), null, null);
		assertThat(moderators.size()).isEqualTo(2);
		guest = guestService.delete(owner2, owner2, guest.getLsUuid());
		moderators = moderatorRepository.findAllByGuest(guest, null, null);
		assertThat(moderators.size()).isEqualTo(0);
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
