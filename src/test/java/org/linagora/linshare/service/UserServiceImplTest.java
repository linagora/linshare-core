/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.FunctionalityNames;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.DenyAllDomain;
import org.linagora.linshare.core.domain.entities.DomainAccessPolicy;
import org.linagora.linshare.core.domain.entities.DomainAccessRule;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.Policy;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AllowedContactRepository;
import org.linagora.linshare.core.repository.DomainAccessPolicyRepository;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.linagora.linshare.core.repository.FunctionalityRepository;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.core.utils.HashUtils;
import org.linagora.linshare.utils.LinShareWiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.google.common.collect.Lists;

@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-start-embedded-ldap.xml",
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml" })
public class UserServiceImplTest extends
		AbstractTransactionalJUnit4SpringContextTests {

	private static Logger logger = LoggerFactory
			.getLogger(UserServiceImplTest.class);

	@Autowired
	private AccountService accountService;

	@Autowired
	private UserService userService;

	@SuppressWarnings("rawtypes")
	@Qualifier("userRepository")
	@Autowired
	private UserRepository userRepository;

	@Qualifier("guestRepository")
	@Autowired
	private GuestRepository guestRepository;

	@Autowired
	private DomainPolicyRepository domainPolicyRepository;

	@Autowired
	private DomainAccessPolicyRepository domainAccessPolicyRepository;

	@Autowired
	private AllowedContactRepository allowedContactRepository;

	@Autowired
	private FunctionalityRepository functionalityRepository;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	@Autowired
	private DomainAccessPolicyRepository domainAccessRepository;

	@Autowired
	private GuestService guestService;

	private LinShareWiser wiser;

	public UserServiceImplTest() {
		super();
		wiser = new LinShareWiser(2525);

	}

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		wiser.start();

		logger.debug(LinShareTestConstants.END_SETUP);
		this.executeSqlScript("import-tests-default-domain-quotas.sql", false);
		this.executeSqlScript("import-tests-quota-other.sql", false);
		this.executeSqlScript("import-mails-hibernate3.sql", false);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		wiser.stop();

		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Ignore
	@Test
	public void testCreateGuest() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain domain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlSubDomain);

		Functionality fonc = new Functionality(FunctionalityNames.GUESTS,
				false, new Policy(Policies.ALLOWED, true), new Policy(
						Policies.ALLOWED, true), domain);

		functionalityRepository.create(fonc);
		domain.addFunctionality(fonc);

		Internal user = new Internal("John", "Doe", "user1@linshare.org", null);
		user.setDomain(domain);
		user.setCanCreateGuest(true);
		user.setCmisLocale("en");
		userService.saveOrUpdateUser(user);

		try {
			List <String> restricted = new ArrayList<>();
			Guest guest = new Guest("Guest", "Doe", "guest1@linshare.org");
			guest.setCmisLocale("en");
			guestService.create(user, user, guest, restricted);
		} catch (TechnicalException e) {
			logger.info("Impossible to send mail, normal in test environment");
		}
		Assert.assertNotNull(userRepository.findByMail("guest1@linshare.org"));
		wiser.checkGeneratedMessages();
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindUserInDB() {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		AbstractDomain domain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlSubDomain);
		Internal user = new Internal("John", "Doe", "user1@linshare.org", null);
		user.setDomain(domain);
		user.setCmisLocale("en");
		logger.info("Save user in DB");
		userService.saveOrUpdateUser(user);

		Assert.assertNotNull(userService.findUserInDB(
				LoadingServiceTestDatas.sqlSubDomain, "user1@linshare.org"));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindUsersInDB() {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		AbstractDomain domain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlSubDomain);
		Internal user = new Internal("John", "Doe", "user1@linshare.org", null);
		user.setDomain(domain);
		user.setCmisLocale("en");
		logger.info("Save user in DB");
		userService.saveOrUpdateUser(user);

		Assert.assertTrue(userService.findUsersInDB(
				LoadingServiceTestDatas.sqlSubDomain).contains(user));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testDeleteUser() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain topDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlDomain);
		AbstractDomain subDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlSubDomain);
		User user1 = new Internal("John", "Doe", "user1@linshare.org", null);
		user1.setDomain(topDomain);
		user1.setRole(Role.ADMIN);
		User user2 = new Internal("Jane", "Smith", "user2@linshare.org", null);
		user2.setDomain(subDomain);
		user1.setCmisLocale("en");
		user2.setCmisLocale("en");
		logger.info("Save users in DB");
		user1 = userService.saveOrUpdateUser(user1);
		// weird
		user1.setRole(Role.ADMIN);
		user1 = (User)userRepository.update(user1);

		user2 = userService.saveOrUpdateUser(user2);

		try {
			logger.info("John Doe trying to delete Jane Smith");
			User u = userService.findUserInDB(
					LoadingServiceTestDatas.sqlSubDomain, "user2@linshare.org");
			userService.deleteUser(user1, u.getLsUuid());
		} catch (BusinessException e) {
			Assert.fail(e.getMessage());
		}

		Assert.assertNull(userService.findUserInDB(
				LoadingServiceTestDatas.sqlSubDomain, "user2@linshare.org"));
		Assert.assertNotNull(userService.findUserInDB(
				LoadingServiceTestDatas.sqlDomain, "user1@linshare.org"));
		wiser.checkGeneratedMessages();
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testDeleteAllUsersFromDomain() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain topDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlDomain);
		AbstractDomain subDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlSubDomain);
		User user1 = new Internal("John", "Doe", "user1@linshare.org", null);
		user1.setDomain(topDomain);
		user1.setRole(Role.ADMIN);
		User user2 = new Internal("Jane", "Smith", "user2@linshare.org", null);
		user2.setDomain(subDomain);
		User user3 = new Internal("Foo", "Bar", "user3@linshare.org", null);
		user3.setDomain(subDomain);
		user1.setCmisLocale("en");
		user2.setCmisLocale("en");
		user3.setCmisLocale("en");
		logger.info("Save users in DB");
		user1 = userService.saveOrUpdateUser(user1);
		user2 = userService.saveOrUpdateUser(user2);
		user3 = userService.saveOrUpdateUser(user3);
		// weird
		user1.setRole(Role.ADMIN);
		user1 = (User)userRepository.update(user1);

		try {
			logger.info("John Doe trying to delete Jane Smith");
			userService.deleteAllUsersFromDomain(user1,
					subDomain.getUuid());
		} catch (BusinessException e) {
			Assert.fail(e.getMessage());
		}

		Assert.assertNull(userService.findUserInDB(
				LoadingServiceTestDatas.sqlSubDomain, "user2@linshare.org"));
		Assert.assertNull(userService.findUserInDB(
				LoadingServiceTestDatas.sqlSubDomain, "user3@linshare.org"));

		Assert.assertNotNull(userService.findUserInDB(
				LoadingServiceTestDatas.sqlDomain, "user1@linshare.org"));
		wiser.checkGeneratedMessages();
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testIsAdminForThisUser() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain rootDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlRootDomain);
		AbstractDomain subDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlSubDomain);
		Internal user1 = new Internal("John", "Doe", "user1@linshare.org", null);
		user1.setDomain(rootDomain);
		user1.setRole(Role.ADMIN);
		Internal user2 = new Internal("Jane", "Smith", "user2@linshare.org", null);
		user2.setDomain(subDomain);
		user1.setCmisLocale("en");
		user2.setCmisLocale("en");
		Assert.assertTrue(userService.isAdminForThisUser(user1, user2));

		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testSearchUser() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		AbstractDomain topDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlDomain);
		AbstractDomain subDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlSubDomain);
		Internal user1 = new Internal("John", "Doe", "user1@linshare.org", null);
		user1.setDomain(topDomain);
		user1.setRole(Role.ADMIN);
		Internal user2 = new Internal("Jane", "Smith", "user2@linshare.org", null);
		user2.setDomain(subDomain);
		user1.setCmisLocale("en");
		user2.setCmisLocale("en");
		userService.saveOrUpdateUser(user1);
		userService.saveOrUpdateUser(user2);
		List<User> searchUser = userService
				.searchUser(user2.getMail(), user2.getFirstName(),
						user2.getLastName(), AccountType.INTERNAL, user1);
		Assert.assertNotEquals(searchUser.size(), 0);
		Assert.assertTrue(searchUser
				.get(0).getMail().equals(user2.getMail()));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testUpdateGuest() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain subDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlSubDomain);
		Functionality fonc = new Functionality(FunctionalityNames.GUESTS,
				false, new Policy(Policies.ALLOWED, true), new Policy(
						Policies.ALLOWED, true), subDomain);

		functionalityRepository.create(fonc);
		subDomain.addFunctionality(fonc);

		User user2 = new Internal("Jane", "Smith", "user2@linshare.org", null);
		user2.setDomain(subDomain);
		user2.setCanCreateGuest(true);
		user2.setRole(Role.SYSTEM);
		user2.setCmisLocale("en");
		user2 = userService.saveOrUpdateUser(user2);

		AbstractDomain guestDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlGuestDomain);
		guestDomain.setDefaultTapestryLocale(SupportedLanguage.fromTapestryLocale("en"));

		// create guest
		Guest guest = new Guest("Foo", "Bar", "user3@linshare.org");
		guest.setDomain(abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlGuestDomain));
		guest.setOwner(user2);
		guest.setExternalMailLocale(SupportedLanguage.toLanguage(guestDomain.getDefaultTapestryLocale()));
		guest.setCmisLocale(guestDomain.getDefaultTapestryLocale().toString());
		guest.setLocale(guestDomain.getDefaultTapestryLocale());
		guest = guestRepository.create(guest);

		guest.setCanCreateGuest(false);
		List <String> restricted = Lists.newArrayList();
		restricted.add("user1@linshare.org");
		guestService.update(user2, user2, guest, restricted);
		Assert.assertFalse(guest.getCanCreateGuest());
		guest.setCanCreateGuest(true);
		guestService.update(user2, user2, guest, restricted);
		Assert.assertTrue(guest.getCanCreateGuest());
		wiser.checkGeneratedMessages();
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testUpdateUserRole() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain topDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlDomain);
		AbstractDomain subDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlSubDomain);
		Internal user1 = new Internal("John", "Doe", "user1@linshare.org", null);
		user1.setDomain(topDomain);
		user1.setRole(Role.SYSTEM);

		User user2 = new Internal("Jane", "Smith", "user2@linshare.org", null);
		user2.setDomain(subDomain);
		user2.setCanCreateGuest(true);
		user2.setRole(Role.SIMPLE);
		
		user1.setCmisLocale("en");
		user2.setCmisLocale("en");
		userService.saveOrUpdateUser(user1);
		userService.saveOrUpdateUser(user2);


		Assert.assertTrue(user2.getRole() == Role.SIMPLE);
		user2.setRole(Role.ADMIN);
		user2 = userService.updateUser(user2, user2, user2.getDomainId());
		Assert.assertTrue(user2.getRole() == Role.ADMIN);
		wiser.checkGeneratedMessages();
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testChangePassword() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain topDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlDomain);
		Guest user1 = new Guest("John", "Doe", "user-unknow@linshare.org");
		user1.setDomain(topDomain);

		String oldPassword = "password";

		user1.setPassword(HashUtils.hashSha1withBase64(oldPassword.getBytes()));
		user1.setCmisLocale("en");
		userService.saveOrUpdateUser(user1);
		String newPassword = "newPassword";
		Assert.assertTrue(user1.getPassword().equals(
				HashUtils.hashSha1withBase64(oldPassword.getBytes())));
		userService.changePassword(user1.getLsUuid(), "user1@linshare.org",
				oldPassword, newPassword);
		Assert.assertTrue(user1.getPassword().equals(
				HashUtils.hashSha1withBase64(newPassword.getBytes())));
		wiser.checkGeneratedMessages();
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testResetPassword() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain topDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlDomain);
		User user1 = new Internal("John", "Doe", "user1@linshare.org", null);
		user1.setDomain(topDomain);
		user1.setCanCreateGuest(true);
		user1.setCmisLocale("en");
		user1 = userService.saveOrUpdateUser(user1);

		AbstractDomain guestDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlGuestDomain);
		guestDomain.setDefaultTapestryLocale(SupportedLanguage.fromTapestryLocale("en"));

		// create guest
		Guest guest = new Guest("Foo", "Bar", "user3@linshare.org");
		
		
		guest.setDomain(abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlGuestDomain));
		guest.setOwner(user1);
		guest.setExternalMailLocale(SupportedLanguage.toLanguage(guestDomain.getDefaultTapestryLocale()));
		guest.setLocale(guestDomain.getDefaultTapestryLocale());
		guest.setCmisLocale("en");
		String oldPassword = "password222";

		guest.setPassword(HashUtils.hashSha1withBase64(oldPassword.getBytes()));

		guest = guestRepository.create(guest);
		// Disable. We don't change guest password anymore, we send him a link to reset it.
//		guestService.triggerResetPassword(guest.getLsUuid());
//		Assert.assertFalse(guest.getPassword().equals(
//				HashUtils.hashSha1withBase64(oldPassword.getBytes())));
		wiser.checkGeneratedMessages();
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testRemoveGuestContactRestriction()
			throws IllegalArgumentException, BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain rootDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlSubDomain);
		User user1 = new Internal("John", "Doe", "user1@linshare.org", null);
		user1.setDomain(rootDomain);
		user1.setCanCreateGuest(true);
		user1.setCmisLocale("en");
		user1 = userService.saveOrUpdateUser(user1);

		AbstractDomain guestDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlGuestDomain);
		guestDomain.setDefaultTapestryLocale(SupportedLanguage.fromTapestryLocale("en"));

		// create guest
		Guest guest = new Guest("Foo", "Bar", "user3@linshare.org");

		guest.setDomain(abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlGuestDomain));
		guest.setOwner(user1);
		guest.setRestricted(true);
		guest.setExternalMailLocale(SupportedLanguage.toLanguage(guestDomain.getDefaultTapestryLocale()));
		guest.setLocale(guestDomain.getDefaultTapestryLocale());
		guest.setCmisLocale("en");
		guest = guestRepository.create(guest);

		Assert.assertTrue(guest.isRestricted());
		guest.setRestricted(false);
		guest = guestService.update(user1, user1, guest, null);
		Assert.assertFalse(guest.isRestricted());
		wiser.checkGeneratedMessages();
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testAddGuestContactRestriction() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		AbstractDomain rootDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlSubDomain);
		User user1 = new Internal("John", "Doe", "user1@linpki.org", null);
		user1.setDomain(rootDomain);
		user1.setCanCreateGuest(true);
		user1.setCmisLocale("en");
		user1 = userService.saveOrUpdateUser(user1);

		AbstractDomain guestDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlGuestDomain);
		guestDomain.setDefaultTapestryLocale(SupportedLanguage.fromTapestryLocale("en"));

		// create guest
		Guest guest2 = new Guest("Jane", "Smith", "user2@linpki.org");
		guest2.setDomain(abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlGuestDomain));
		guest2.setOwner(user1);
		guest2.setExternalMailLocale(SupportedLanguage.toLanguage(guestDomain.getDefaultTapestryLocale()));
		guest2.setCmisLocale("en");
		guest2.setLocale(guestDomain.getDefaultTapestryLocale());
		guest2 = guestRepository.create(guest2);

		// create guest
		Guest guest = new Guest("Foo", "Bar", "user3@linpki.org");
		guest.setDomain(abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlGuestDomain));

		guest.setOwner(user1);
		guest.setRestricted(true);
		guest.setExternalMailLocale(SupportedLanguage.toLanguage(guestDomain.getDefaultTapestryLocale()));
		guest.setLocale(guestDomain.getDefaultTapestryLocale());
		guest.setCmisLocale(guestDomain.getDefaultTapestryLocale().toString());
		guest = guestRepository.create(guest);

		List<String> contacts = Lists.newArrayList();
		contacts.add("user1@linshare.org");
		guestService.update(user1, user1, guest, contacts);
		List<AllowedContact> listAllowedContact = allowedContactRepository
				.findByOwner(guest);
		Assert.assertEquals(1, listAllowedContact.size());
		boolean test = false;
		for (AllowedContact allowedContact : listAllowedContact) {
			if (allowedContact.getContact().getMail().equals("user1@linshare.org")) {
				test = true;
			}
		}
		Assert.assertTrue(test);
		wiser.checkGeneratedMessages();
		logger.debug(LinShareTestConstants.END_TEST);
	}

//	@Test
//	public void testSetGuestContactRestriction() throws BusinessException {
//		logger.info(LinShareTestConstants.BEGIN_TEST);
//
//		AbstractDomain rootDomain = abstractDomainRepository
//				.findById(LoadingServiceTestDatas.sqlRootDomain);
//		User user1 = new Internal("John", "Doe", "user1@linshare.org", null);
//		user1.setDomain(rootDomain);
//		user1.setCanCreateGuest(true);
//		user1.setCmisLocale("en");
//		user1 = userService.saveOrUpdateUser(user1);
//
//		AbstractDomain guestDomain = abstractDomainRepository
//				.findById(LoadingServiceTestDatas.sqlGuestDomain);
//		guestDomain.setDefaultTapestryLocale("en");
//
//		// create guest
//		Guest guest2 = new Guest("Jane", "Smith", "user2@linshare.org");
//		guest2.setDomain(abstractDomainRepository
//				.findById(LoadingServiceTestDatas.sqlGuestDomain));
//		guest2.setOwner(user1);
//		guest2.setExternalMailLocale(guestDomain.getDefaultTapestryLocale());
//		guest2.setLocale(guestDomain.getDefaultTapestryLocale());
//		guest2.setCmisLocale(guestDomain.getDefaultTapestryLocale());
//		guest2 = guestRepository.create(guest2);
//
//		// create guest
//		Guest guest = new Guest("Foo", "Bar", "user3@linshare.org");
//		guest.setDomain(abstractDomainRepository
//				.findById(LoadingServiceTestDatas.sqlGuestDomain));
//		guest.setOwner(user1);
//		guest.setRestricted(true);
//		guest.setExternalMailLocale(guestDomain.getDefaultTapestryLocale());
//		guest.setLocale(guestDomain.getDefaultTapestryLocale());
//		guest.setCmisLocale(guestDomain.getDefaultTapestryLocale());
//
//		guest = guestRepository.create(guest);
//
//		guest.addContacts(new AllowedContact(guest, guest2));
//		guest = guestService.update(user1, guest, null);
//		List<AllowedContact> listAllowedContact = allowedContactRepository
//				.findByOwner(guest);
//		boolean test = false;
//		for (AllowedContact allowedContact : listAllowedContact) {
//			if (allowedContact.getContact().getLsUuid()
//					.equals(guest2.getLsUuid())) {
//				test = true;
//			}
//		}
//		Assert.assertTrue(test);
//
//		logger.debug(LinShareTestConstants.END_TEST);
//	}
//
//	@Test
//	public void testFetchGuestContacts() throws IllegalArgumentException,
//			BusinessException {
//		logger.info(LinShareTestConstants.BEGIN_TEST);
//
//		AbstractDomain rootDomain = abstractDomainRepository
//				.findById(LoadingServiceTestDatas.sqlRootDomain);
//		User user1 = new Internal("John", "Doe", "user1@linshare.org", null);
//		user1.setDomain(rootDomain);
//		user1.setCanCreateGuest(true);
//		user1.setCmisLocale("en");
//		user1 = userService.saveOrUpdateUser(user1);
//
//		AbstractDomain guestDomain = abstractDomainRepository
//				.findById(LoadingServiceTestDatas.sqlGuestDomain);
//		guestDomain.setDefaultTapestryLocale("en");
//
//		// create guest
//		Guest guest2 = new Guest("Jane", "Smith", "user2@linshare.org");
//		guest2.setDomain(guestDomain);
//		guest2.setOwner(user1);
//		guest2.setExternalMailLocale(guestDomain.getDefaultTapestryLocale());
//		guest2.setLocale(guestDomain.getDefaultTapestryLocale());
//		guest2.setCmisLocale(guestDomain.getDefaultTapestryLocale());
//		guest2 = guestRepository.create(guest2);
//
//		// create guest
//		Guest guest = new Guest("Foo", "Bar", "user3@linshare.org");
//		guest.setDomain(guestDomain);
//		guest.setOwner(user1);
//		guest.setRestricted(true);
//		guest.setExternalMailLocale(guestDomain.getDefaultTapestryLocale());
//		guest.setLocale(guestDomain.getDefaultTapestryLocale());
//		guest.setCmisLocale(guestDomain.getDefaultTapestryLocale());
//		guest = guestRepository.create(guest);
//
//		guest.addContacts(new AllowedContact(guest, guest2));
//		guest = guestService.update(user1, guest, null);
//		Set<AllowedContact> guestContacts = guest.getRestrictedContacts();
//		boolean contain = false;
//		for (AllowedContact contact : guestContacts) {
//			if (contact.getContact().equals(guest2)) {
//				contain = true;
//			}
//		}
//		Assert.assertTrue(contain);
//
//		logger.debug(LinShareTestConstants.END_TEST);
//	}

//	@Test
//	public void testGetGuestEmailContacts() throws IllegalArgumentException,
//			BusinessException {
//		logger.info(LinShareTestConstants.BEGIN_TEST);
//
//		AbstractDomain rootDomain = abstractDomainRepository
//				.findById(LoadingServiceTestDatas.sqlRootDomain);
//		User user1 = new Internal("John", "Doe", "user1@linshare.org", null);
//		user1.setDomain(rootDomain);
//		user1.setCanCreateGuest(true);
//		user1.setCmisLocale("en");
//		user1 = userService.saveOrUpdateUser(user1);
//
//		AbstractDomain guestDomain = abstractDomainRepository
//				.findById(LoadingServiceTestDatas.sqlGuestDomain);
//		guestDomain.setDefaultTapestryLocale("en");
//
//		// create guest
//		Guest guest2 = new Guest("Jane", "Smith", "user2@linshare.org");
//		guest2.setDomain(guestDomain);
//		guest2.setOwner(user1);
//		guest2.setExternalMailLocale(guestDomain.getDefaultTapestryLocale());
//		guest2.setLocale(guestDomain.getDefaultTapestryLocale());
//		guest2.setCmisLocale(guestDomain.getDefaultTapestryLocale());
//		Guest createGuest2 = guestRepository.create(guest2);
//
//		// create guest
//		Guest guest = new Guest("Foo", "Bar", "user3@linshare.org");
//		guest.setDomain(guestDomain);
//		guest.setOwner(user1);
//		guest.setRestricted(true);
//		guest.setExternalMailLocale(guestDomain.getDefaultTapestryLocale());
//		guest.setLocale(guestDomain.getDefaultTapestryLocale());
//		guest.setCmisLocale(guestDomain.getDefaultTapestryLocale());
//		Guest createGuest = guestRepository.create(guest);
//
//		createGuest.addContacts(new AllowedContact(createGuest, createGuest2));
//		guest = guestService.update(user1, createGuest, null);
//		Set<AllowedContact> guestContacts = guest.getRestrictedContacts();
//		boolean contain = false;
//		for (AllowedContact contact : guestContacts) {
//			if (contact.getContact().getMail().equals(createGuest2.getMail())) {
//				contain = true;
//			}
//		}
//		Assert.assertTrue(contain);
//
//		logger.debug(LinShareTestConstants.END_TEST);
//	}

	@Test
	public void testUpdateUserDomain() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		AbstractDomain subDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlDomain);
		Internal user1 = new Internal("John", "Doe", "user1@linshare.org", null);
		user1.setDomain(subDomain);
		user1.setRole(Role.SUPERADMIN);
		user1.setCmisLocale("en");
		userService.saveOrUpdateUser(user1);

		Internal user2 = new Internal("Jane", "Smith", "user2@linshare.org", null);
		user2.setDomain(subDomain);
		user2.setCmisLocale("en");
		userService.saveOrUpdateUser(user2);
		wiser.checkGeneratedMessages();
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindOrCreateUserWithDomainPolicies()
			throws IllegalArgumentException, BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		AbstractDomain topDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlDomain);
		Internal user1 = new Internal("John", "Doe", "user1@linshare.org", null);
		user1.setDomain(topDomain);
		user1.setCmisLocale("en");
		userService.saveOrUpdateUser(user1);

		AbstractDomain subDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlSubDomain);
		Internal user2 = new Internal("Jane", "Smith", "user2@linshare.org", null);
		user2.setDomain(subDomain);
		user2.setCmisLocale("en");
		userService.saveOrUpdateUser(user2);

		DomainAccessPolicy accessPolicy = new DomainAccessPolicy();
		domainAccessRepository.create(accessPolicy);
		DomainPolicy domainPolicy = new DomainPolicy("domainPolicy",
				accessPolicy);
		domainPolicyRepository.create(domainPolicy);

		DomainAccessRule endRule = new DenyAllDomain();
		domainPolicy.getDomainAccessPolicy().addRule(endRule);
		subDomain.setPolicy(domainPolicy);

		domainPolicyRepository.update(domainPolicy);

		abstractDomainRepository.update(subDomain);

		try {
			userService.findOrCreateUserWithDomainPolicies(
					LoadingServiceTestDatas.sqlDomain, "user1@linshare.org",
					LoadingServiceTestDatas.sqlSubDomain);

			logger.error("Test shouldn't go here because findOrCreateUserWithDomainPolicies should rise a exception");
			Assert.fail();
		} catch (BusinessException e) {
			logger.debug("Test succeed");
		}
		wiser.checkGeneratedMessages();
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindOrCreateUser() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		long id_user = 0;

		try {
			logger.debug("Trying to find john doe: should create this user");
			User user = userService.findOrCreateUserWithDomainPolicies(
					"user1@linshare.org", LoadingServiceTestDatas.sqlSubDomain);
			Assert.assertEquals("John", user.getFirstName());
			Assert.assertEquals("Doe", user.getLastName());
			id_user = user.getId();
			logger.debug("the user created was " + user.getFirstName() + " "
					+ user.getLastName() + " (id=" + user.getId() + ")");
		} catch (BusinessException e) {
			logger.error("userService can not create an user ");
			logger.error(e.toString());
		}

		try {
			logger.debug("Trying to find john doe: should return this user");
			User user = userService.findOrCreateUser("user1@linshare.org",
					LoadingServiceTestDatas.sqlSubDomain);
			Assert.assertEquals("John", user.getFirstName());
			Assert.assertEquals("Doe", user.getLastName());
			// Should be the same user, not a new one.
			Assert.assertEquals(id_user, user.getId());
			logger.debug("the user created was " + user.getFirstName() + " "
					+ user.getLastName() + " (id=" + user.getId() + ")");

		} catch (BusinessException e) {
			logger.error("userService can not find an user ");
			logger.error(e.toString());
		}

		try {
			logger.debug("Trying to find john doe: should return this user");
			User user = userService.findOrCreateUser("user1@linshare.org",
					LoadingServiceTestDatas.sqlSubDomain);
			Assert.assertEquals("John", user.getFirstName());
			Assert.assertEquals("Doe", user.getLastName());
			// Should be the same user, not a new one.
			Assert.assertEquals(id_user, user.getId());
			logger.debug("the user created was " + user.getFirstName() + " "
					+ user.getLastName() + " (id=" + user.getId() + ")");

		} catch (BusinessException e) {
			logger.error("userService can not find an user ");
			logger.error(e.toString());
		}
		wiser.checkGeneratedMessages();
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@Rollback(true)
	public void testSaveOrUpdateUser() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain domain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlSubDomain);

		Internal user = new Internal("John", "Doe", "user1@linshare.org", null);
		user.setCmisLocale("en");
		logger.info("Trying to create a user without domain : should fail.");
		try {
			userService.saveOrUpdateUser(user);
			Assert.fail("It should fail before this message.");
		} catch (TechnicalException e) {
			logger.debug("TechnicalException raise as planned.");
		}
		logger.info("Trying to create a user : .");
		logger.debug("user id : " + user.getId());
		user.setDomain(domain);
		userService.saveOrUpdateUser(user);
		logger.debug("user id : " + user.getId());
		Assert.assertTrue(user.getCanUpload());
		user.setCanUpload(false);
		userService.saveOrUpdateUser(user);
		Assert.assertFalse(user.getCanUpload());
		logger.debug(LinShareTestConstants.END_TEST);
		wiser.checkGeneratedMessages();
	}
}
