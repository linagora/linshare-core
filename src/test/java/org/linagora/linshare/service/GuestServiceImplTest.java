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

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.FunctionalityNames;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.RootUserRepository;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.core.utils.HashUtils;
import org.linagora.linshare.utils.LinShareWiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class GuestServiceImplTest extends
		AbstractTransactionalJUnit4SpringContextTests {

	private static Logger logger = LoggerFactory
			.getLogger(GuestServiceImplTest.class);

	@Autowired
	private GuestService guestService;

	@Autowired
	private UserService userService;

//	@SuppressWarnings("rawtypes")
//	@Autowired
//	private UserRepository userRepository;

	@Autowired
	private FunctionalityService functionalityService;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	@Autowired
	private RootUserRepository rootUserRepository;

	private User root;

	private LinShareWiser wiser;

	private User owner1;

	private User owner2;

	public GuestServiceImplTest() {
		super();
		wiser = new LinShareWiser(2525);
	}

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		this.executeSqlScript("import-tests-default-domain-quotas.sql", false);
		this.executeSqlScript("import-tests-quota-other.sql", false);
		this.executeSqlScript("import-mails-hibernate3.sql", false);
		wiser.start();
		root = rootUserRepository
				.findByLsUuid("root@localhost.localdomain@test");

		AbstractDomain subDomain = abstractDomainRepository
				.findById(LoadingServiceTestDatas.sqlSubDomain);
		owner1 = new Internal("John", "Doe", "user1@linshare.org", null);
		owner1.setDomain(subDomain);
		owner1.setCanCreateGuest(true);
		owner1 = userService.saveOrUpdateUser(owner1);

		owner2 = new Internal("Jane", "Smith", "user2@linshare.org", null);
		owner2.setDomain(subDomain);
		owner2.setCanCreateGuest(true);
		owner2.setRole(Role.SIMPLE);
		owner2 = userService.saveOrUpdateUser(owner2);

		Functionality functionality = functionalityService.find(
				root, LoadingServiceTestDatas.sqlSubDomain,
				FunctionalityNames.GUESTS.toString());
		functionality.getActivationPolicy().setStatus(true);
		functionalityService.update(root, LoadingServiceTestDatas.sqlSubDomain,
				functionality);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		wiser.stop();

		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testCreateGuest() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Guest guest = new Guest("Guest", "Doe", "guest1@linshare.org");
		guest.setCmisLocale("en");
		guest.setRole(Role.SUPERADMIN);
		guest = guestService.create(owner1, owner1, guest, null);
		Guest find = guestService.find(owner1, owner1, guest.getLsUuid());
		Assert.assertNotNull(find);
		Assert.assertEquals(Role.SIMPLE, find.getRole());
		wiser.checkGeneratedMessages();
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testUpdateGuest() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Guest guest = new Guest("Guest", "Doe", "guest1@linshare.org");
		guest.setCmisLocale("en");
		guest = guestService.create(owner1, owner1, guest, null);

		guest.setFirstName("First");
		guest.setLastName("Last");
		guest.setRole(Role.SUPERADMIN);
		Guest update = guestService.update(owner1, owner1, guest, null);

		Assert.assertEquals(Role.SIMPLE, update.getRole());
		Assert.assertEquals("First", update.getFirstName());
		Assert.assertEquals("Last", update.getLastName());
		wiser.checkGeneratedMessages();
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testResetPassword() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// create guest
		Guest guest = new Guest("Foo", "Bar", "user3@linshare.org");
		String oldPassword = "password222";
		guest.setPassword(HashUtils.hashSha1withBase64(oldPassword.getBytes()));
		guest.setCmisLocale("en");
		guest = guestService.create(owner1, owner1, guest, null);
		guestService.triggerResetPassword(guest.getLsUuid());
		Assert.assertFalse(guest.getPassword().equals(
				HashUtils.hashSha1withBase64(oldPassword.getBytes())));
		wiser.checkGeneratedMessages();
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
		Assert.assertTrue(guest.isRestricted());
		Assert.assertTrue(guest.isGuest());
		List<AllowedContact> ac = guestService.load(owner1, guest);
		Assert.assertEquals(3, ac.size());
		wiser.checkGeneratedMessages();
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Ignore
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
		Assert.assertTrue(guest.isRestricted());
		Assert.assertTrue(guest.isGuest());
		List<AllowedContact> ac = guestService.load(owner1, guest);
		Assert.assertEquals(2, ac.size());
		wiser.checkGeneratedMessages();
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
		Assert.assertEquals(1, ac.size());
		wiser.checkGeneratedMessages();
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

		Assert.assertTrue(guest.isRestricted());
		List<AllowedContact> ac = guestService.load(owner1, guest);
		Assert.assertEquals(3, ac.size());

		guest.setRestricted(false);
		guest = guestService.update(owner1, owner1, guest, restrictedContacts);
		Assert.assertFalse(guest.isRestricted());

		ac = guestService.load(owner1, guest);
		Assert.assertEquals(0, ac.size());
		wiser.checkGeneratedMessages();
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testSearchGuest() throws IllegalArgumentException, BusinessException {
		boolean mine= true;
		List<Guest> search = guestService.search(owner1, owner1, "org", mine);
		logger.info("nb guests : " + search.size());
		Assert.assertEquals(0, search.size());

		search = guestService.search(owner1, owner1, "org", null);
		logger.info("nb guests : " + search.size());
		Assert.assertEquals(1, search.size());

		search = guestService.search(owner1, owner1, "toto", mine);
		logger.info("nb guests : " + search.size());
		Assert.assertEquals(0, search.size());

		search = guestService.search(owner1, owner1, "test", null);
		logger.info("nb guests : " + search.size());
		Assert.assertEquals(1, search.size());

		search = guestService.search(owner1, owner1, null, null, "guest", mine);
		logger.info("nb guests : " + search.size());
		Assert.assertEquals(1, search.size());
	}

}
