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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.FunctionalityNames;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AccountQuota;
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
import org.linagora.linshare.core.service.InconsistentUserService;
import org.linagora.linshare.core.service.QuotaService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.core.utils.HashUtils;
import org.linagora.linshare.server.embedded.ldap.LdapServerRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

@ExtendWith(SpringExtension.class)
@ExtendWith(LdapServerRule.class)
@Transactional
@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml" })
public class GuestServiceImplTest {

	private static Logger logger = LoggerFactory
			.getLogger(GuestServiceImplTest.class);

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
	private QuotaService quotaService;

	private User root;

	private User owner1;

	private User owner2;
	
	private User owner3;

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
		owner1.setRole(Role.SUPERADMIN);
		owner1.setCanCreateGuest(true);
		owner1 = userService.saveOrUpdateUser(owner1);

		owner2 = new Internal("Jane", "Smith", "user2@linshare.org", null);
		owner2.setDomain(subDomain);
		owner2.setCanCreateGuest(true);
		owner2.setRole(Role.SIMPLE);
		owner2 = userService.saveOrUpdateUser(owner2);
		
		owner3 = new Internal("Jane", "Smith", "user4@linshare.org", null);
		owner3.setDomain(subDomain);
		owner3.setCanCreateGuest(true);
		owner3.setRole(Role.SIMPLE);
		owner3 = userService.saveOrUpdateUser(owner2);

		Functionality functionality = functionalityService.find(
				root, LoadingServiceTestDatas.sqlSubDomain,
				FunctionalityNames.GUESTS.toString());
		functionality.getActivationPolicy().setStatus(true);
		functionalityService.update(root, LoadingServiceTestDatas.sqlSubDomain,
				functionality);
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
		int size = guestService.findAll(owner1, owner1, null).size();
		try {
			guest = guestService.create(owner1, owner1, guest, null);
		} catch (BusinessException e) {
			logger.debug("Can not create an internal user as guest");
		}
		Assertions.assertEquals(size, guestService.findAll(owner1, owner1, null).size());
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
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.guestDomainName1);
		inconsistentUserService.updateDomain(owner1, guest.getLsUuid(), domain.getUuid());

	}

	@Test
	public void testResetPassword() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		// create guest
		Guest guest = new Guest("Foo", "Bar", "user10@linshare.org");
		String oldPassword = "password222";
		guest.setPassword(HashUtils.hashBcrypt(oldPassword));
		guest.setCmisLocale("en");
		guest = guestService.create(owner1, owner1, guest, null);
		guestService.triggerResetPassword(guest.getLsUuid());
		Assertions.assertFalse(HashUtils.matches(oldPassword, guest.getPassword()));
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
	public void testSearchGuest() throws IllegalArgumentException, BusinessException {
		boolean mine= true;
		List<Guest> search = guestService.search(owner1, owner1, "org", mine);
		logger.info("nb guests : " + search.size());
		Assertions.assertEquals(0, search.size());

		search = guestService.search(owner1, owner1, "org", null);
		logger.info("nb guests : " + search.size());
		Assertions.assertEquals(1, search.size());

		search = guestService.search(owner1, owner1, "toto", mine);
		logger.info("nb guests : " + search.size());
		Assertions.assertEquals(0, search.size());

		search = guestService.search(owner1, owner1, "test", null);
		logger.info("nb guests : " + search.size());
		Assertions.assertEquals(1, search.size());

		search = guestService.search(owner1, owner1, null, null, "guest", mine);
		logger.info("nb guests : " + search.size());
		Assertions.assertEquals(1, search.size());
	}

}
