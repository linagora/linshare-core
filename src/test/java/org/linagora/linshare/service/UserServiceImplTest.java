/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.FunctionalityNames;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.Policies;
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
import org.linagora.linshare.core.domain.entities.Role;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.UserVo;
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
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.core.utils.HashUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.subethamail.wiser.Wiser;

@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-startopends.xml",
		"classpath:springContext-jackRabbit.xml",
		"classpath:springContext-test.xml"
		})
public class UserServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests{
	
	private static Logger logger = LoggerFactory.getLogger(UserServiceImplTest.class);
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private UserService userService;
	
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
	
	
	private Wiser wiser;
	
	public UserServiceImplTest() {
		super();
		wiser = new Wiser(2525);

	}
	
	
	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		wiser.start();

		
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		wiser.stop();

		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}
	
	
	
	@Test
	public void testCreateGuest() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlSubDomain);
		
		Functionality fonc = new Functionality(FunctionalityNames.GUESTS,
				false,
				new Policy(Policies.ALLOWED, true),
				new Policy(Policies.ALLOWED, true),
				domain);
		
		functionalityRepository.create(fonc);
		domain.addFunctionality(fonc);
		

		Internal user = new Internal("John","Doe","user1@linpki.org", null);
		user.setDomain(domain);
		user.setCanCreateGuest(true);
		userService.saveOrUpdateUser(user);
		
		try{
			userService.createGuest("guest1@linpki.org", "Guest", "Doe", "guest1@linpki.org", true, false, "", user.getLsUuid(), user.getDomainId());
		}catch(TechnicalException e){
			logger.info("Impossible to send mail, normal in test environment");
		}
		Assert.assertNotNull(userRepository.findByMail("guest1@linpki.org"));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testFindUserInDB(){
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlSubDomain);
		Internal user = new Internal("John","Doe","user1@linpki.org", null);
		user.setDomain(domain);
		logger.info("Save user in DB");
		userService.saveOrUpdateUser(user);
		
		Assert.assertNotNull(userService.findUserInDB(LoadingServiceTestDatas.sqlSubDomain, "user1@linpki.org"));
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testFindUnkownUserInDB(){
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlSubDomain);
		Internal user = new Internal("John","Doe","user1@linpki.org", null);
		user.setDomain(domain);
		logger.info("Save user in DB");
		userService.saveOrUpdateUser(user);
		
		Assert.assertTrue(userService.findUnkownUserInDB("user1@linpki.org").equals(user));

		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testFindUsersInDB(){
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlSubDomain);
		Internal user = new Internal("John","Doe","user1@linpki.org", null);
		user.setDomain(domain);
		logger.info("Save user in DB");
		userService.saveOrUpdateUser(user);
		
		Assert.assertTrue(userService.findUsersInDB(LoadingServiceTestDatas.sqlSubDomain).contains(user));
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testDeleteUser() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain rootDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlRootDomain);
		AbstractDomain subDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlSubDomain);
		Internal user1 = new Internal("John","Doe","user1@linpki.org", null);
		user1.setDomain(rootDomain);
		user1.setRole(Role.ADMIN);
		Internal user2 = new Internal("Jane","Smith","user2@linpki.org", null);
		user2.setDomain(subDomain);
		
		logger.info("Save users in DB");
		userService.saveOrUpdateUser(user1);
		userService.saveOrUpdateUser(user2);
		
		try {
			logger.info("John Doe trying to delete Jane Smith");
			User u = userService.findUserInDB(LoadingServiceTestDatas.sqlSubDomain, "user2@linpki.org");
			userService.deleteUser(user1, u.getLsUuid());
		} catch (BusinessException e) {
			Assert.fail(e.getMessage());
		}
		
		Assert.assertNull(userService.findUserInDB(LoadingServiceTestDatas.sqlSubDomain, "user2@linpki.org"));
		Assert.assertNotNull(userService.findUserInDB(LoadingServiceTestDatas.sqlRootDomain, "user1@linpki.org"));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	
	@Test
	public void testDeleteAllUsersFromDomain() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain rootDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlRootDomain);
		AbstractDomain subDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlSubDomain);
		Internal user1 = new Internal("John","Doe","user1@linpki.org", null);
		user1.setDomain(rootDomain);
		user1.setRole(Role.ADMIN);
		Internal user2 = new Internal("Jane","Smith","user2@linpki.org", null);
		user2.setDomain(subDomain);
		Internal user3 = new Internal("Foo","Bar","user3@linpki.org", null);
		user3.setDomain(subDomain);
		
		logger.info("Save users in DB");
		userService.saveOrUpdateUser(user1);
		userService.saveOrUpdateUser(user2);
		userService.saveOrUpdateUser(user3);
		
		try {
			logger.info("John Doe trying to delete Jane Smith");
			userService.deleteAllUsersFromDomain(user1, subDomain.getIdentifier());
		} catch (BusinessException e) {
			Assert.fail(e.getMessage());
		}
		
		Assert.assertNull(userService.findUserInDB(LoadingServiceTestDatas.sqlSubDomain, "user2@linpki.org"));
		Assert.assertNull(userService.findUserInDB(LoadingServiceTestDatas.sqlSubDomain, "user3@linpki.org"));

		Assert.assertNotNull(userService.findUserInDB(LoadingServiceTestDatas.sqlRootDomain, "user1@linpki.org"));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testIsAdminForThisUser(){
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain rootDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlRootDomain);
		AbstractDomain subDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlSubDomain);
		Internal user1 = new Internal("John","Doe","user1@linpki.org", null);
		user1.setDomain(rootDomain);
		user1.setRole(Role.ADMIN);
		Internal user2 = new Internal("Jane","Smith","user2@linpki.org", null);
		user2.setDomain(subDomain);
		
		Assert.assertTrue(userService.isAdminForThisUser(user1, user2.getDomainId(),user2.getLsUuid()));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	
	@Test
	public void testCleanExpiredGuestAcccounts() throws IllegalArgumentException, BusinessException, ParseException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlSubDomain);
		
		Functionality fonc = new Functionality(FunctionalityNames.GUESTS,
				false,
				new Policy(Policies.ALLOWED, true),
				new Policy(Policies.ALLOWED, true),
				domain);
		
		functionalityRepository.create(fonc);
		domain.addFunctionality(fonc);

		Internal user = new Internal("John","Doe","user1@linpki.org", null);
		user.setDomain(domain);
		user.setCanCreateGuest(true);
		userService.saveOrUpdateUser(user);
		
		Guest guest = userService.createGuest("guest1@linpki.org", "Guest", "Doe", "guest1@linpki.org", true, false, "", user.getLsUuid(), user.getDomainId());
		Assert.assertNotNull(userRepository.findByMail("guest1@linpki.org"));
		
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dfm.setTimeZone(TimeZone.getTimeZone("Europe/Zurich"));
		Date date = dfm.parse("2007-02-26 20:15:00");

		guest.setExpirationDate(date);
		userService.cleanExpiredGuestAcccounts(userRepository.getSystemAccount());
		Assert.assertNull(userRepository.findByMail("guest1@linpki.org"));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testSearchUser() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		AbstractDomain rootDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlRootDomain);
		AbstractDomain subDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlSubDomain);
		Internal user1 = new Internal("John","Doe","user1@linpki.org", null);
		user1.setDomain(rootDomain);
		user1.setRole(Role.ADMIN);
		Internal user2 = new Internal("Jane","Smith","user2@linpki.org", null);
		user2.setDomain(subDomain);
		userService.saveOrUpdateUser(user1);
		userService.saveOrUpdateUser(user2);
		Assert.assertTrue(userService.searchUser(user2.getMail(), user2.getFirstName(), user2.getLastName(), AccountType.INTERNAL, user1).get(0).getMail().equals(user2.getMail()));
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testUpdateGuest()throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain subDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlSubDomain);
		Functionality fonc = new Functionality(FunctionalityNames.GUESTS,
				false,
				new Policy(Policies.ALLOWED, true),
				new Policy(Policies.ALLOWED, true),
				subDomain);
		
		functionalityRepository.create(fonc);
		subDomain.addFunctionality(fonc);
		
		User user2 = new Internal("Jane","Smith","user2@linpki.org", null);
		user2.setDomain(subDomain);
		user2.setCanCreateGuest(true);
		user2.setRole(Role.SYSTEM);
			
		userService.saveOrUpdateUser(user2);
		
		UserVo userVo2 = new UserVo(user2);
		
		AbstractDomain guestDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlGuestDomain);
		guestDomain.setDefaultLocale("en");
		
		//create guest
		Guest guest = new Guest("Foo","Bar","user3@linpki.org");
		guest.setDomain(abstractDomainRepository.findById(LoadingServiceTestDatas.sqlGuestDomain));
		guest.setOwner(user2);
		guest.setExternalMailLocale(guestDomain.getDefaultLocale());
		guest.setLocale(guestDomain.getDefaultLocale());
		guestRepository.create(guest);
		
		userService.updateGuest(guest.getLsUuid(), LoadingServiceTestDatas.sqlGuestDomain,"user3@linpki.org","Foo", "Bar", false, false, userVo2);
		Assert.assertFalse(guest.getCanCreateGuest());
		userService.updateGuest(guest.getLsUuid(), LoadingServiceTestDatas.sqlGuestDomain,"user3@linpki.org","Foo", "Bar", true, true, userVo2);
		Assert.assertTrue(guest.getCanCreateGuest());
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testUpdateUserRole() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain rootDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlRootDomain);
		AbstractDomain subDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlSubDomain);
		Internal user1 = new Internal("John","Doe","user1@linpki.org", null);
		user1.setDomain(rootDomain);
		user1.setRole(Role.SYSTEM);
		
		User user2 = new Internal("Jane","Smith","user2@linpki.org", null);
		user2.setDomain(subDomain);
		user2.setCanCreateGuest(true);
		user2.setRole(Role.SIMPLE);
		
		userService.saveOrUpdateUser(user1);
		userService.saveOrUpdateUser(user2);
		
		UserVo userVo = new UserVo(user1);
		
		Assert.assertTrue(user2.getRole()==Role.SIMPLE);
		userService.updateUserRole(user2.getLsUuid(), LoadingServiceTestDatas.sqlSubDomain, "user2@linpki.org", Role.ADMIN, userVo);
		Assert.assertTrue(user2.getRole()==Role.ADMIN);
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	
	@Test
	public void testChangePassword() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain rootDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlRootDomain);
		Guest user1 = new Guest("John","Doe","user1@linpki.org");
		user1.setDomain(rootDomain);
		
		String oldPassword = "password";
		
		user1.setPassword(
				HashUtils.hashSha1withBase64(oldPassword.getBytes()));
		
		userService.saveOrUpdateUser(user1);
		String newPassword = "newPassword";
		Assert.assertTrue(user1.getPassword().equals(HashUtils.hashSha1withBase64(oldPassword.getBytes())));
		userService.changePassword(user1.getLsUuid(), "user1@linpki.org", oldPassword, newPassword);
		Assert.assertTrue(user1.getPassword().equals(HashUtils.hashSha1withBase64(newPassword.getBytes())));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testResetPassword() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain rootDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlRootDomain);
		Internal user1 = new Internal("John","Doe","user1@linpki.org", null);
		user1.setDomain(rootDomain);
		user1.setCanCreateGuest(true);
		
		userService.saveOrUpdateUser(user1);
		
		AbstractDomain guestDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlGuestDomain);
		guestDomain.setDefaultLocale("en");
		
		//create guest
		Guest guest = new Guest("Foo","Bar","user3@linpki.org");
		guest.setDomain(abstractDomainRepository.findById(LoadingServiceTestDatas.sqlGuestDomain));
		guest.setOwner(user1);
		guest.setExternalMailLocale(guestDomain.getDefaultLocale());
		guest.setLocale(guestDomain.getDefaultLocale());
		String oldPassword = "password222";
		
		guest.setPassword(
				HashUtils.hashSha1withBase64(oldPassword.getBytes()));
		
		guestRepository.create(guest);
		userService.resetPassword(guest.getLsUuid(), "user3@linpki.org");
		Assert.assertFalse(guest.getPassword().equals(HashUtils.hashSha1withBase64(oldPassword.getBytes())));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testRemoveGuestContactRestriction() throws IllegalArgumentException, BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain rootDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlRootDomain);
		Internal user1 = new Internal("John","Doe","user1@linpki.org", null);
		user1.setDomain(rootDomain);
		user1.setCanCreateGuest(true);
		
		userService.saveOrUpdateUser(user1);
		
		AbstractDomain guestDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlGuestDomain);
		guestDomain.setDefaultLocale("en");
		
		//create guest
		Guest guest = new Guest("Foo","Bar","user3@linpki.org");
		guest.setDomain(abstractDomainRepository.findById(LoadingServiceTestDatas.sqlGuestDomain));
		guest.setOwner(user1);
		guest.setRestricted(true);
		guest.setExternalMailLocale(guestDomain.getDefaultLocale());
		guest.setLocale(guestDomain.getDefaultLocale());
		guestRepository.create(guest);
		Assert.assertTrue(guest.isRestricted());
		userService.removeGuestContactRestriction(guest.getLsUuid());
		Assert.assertFalse(guest.isRestricted());
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	
	@Test
	public void testAddGuestContactRestriction() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		AbstractDomain rootDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlRootDomain);
		Internal user1 = new Internal("John","Doe","user1@linpki.org", null);
		user1.setDomain(rootDomain);
		user1.setCanCreateGuest(true);
		
		userService.saveOrUpdateUser(user1);
		
		AbstractDomain guestDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlGuestDomain);
		guestDomain.setDefaultLocale("en");
		
		//create guest
		Guest guest2 = new Guest("Jane","Smith","user2@linpki.org");
		guest2.setDomain(abstractDomainRepository.findById(LoadingServiceTestDatas.sqlGuestDomain));
		guest2.setOwner(user1);
		guest2.setExternalMailLocale(guestDomain.getDefaultLocale());
		guest2.setLocale(guestDomain.getDefaultLocale());
		guestRepository.create(guest2);
		
		//create guest
		Guest guest = new Guest("Foo","Bar","user3@linpki.org");
		guest.setDomain(abstractDomainRepository.findById(LoadingServiceTestDatas.sqlGuestDomain));
		
		guest.setOwner(user1);
		guest.setRestricted(true);
		guest.setExternalMailLocale(guestDomain.getDefaultLocale());
		guest.setLocale(guestDomain.getDefaultLocale());
		guestRepository.create(guest);
		userService.addGuestContactRestriction(guest.getLsUuid(), guest2.getLsUuid());
		List<AllowedContact> listAllowedContact= allowedContactRepository.findByOwner(guest);
		boolean test = false;
		for (AllowedContact allowedContact : listAllowedContact) {
			if(allowedContact.getContact().getLsUuid().equals(guest2.getLsUuid())){
				test=true;
			}	
		}
		Assert.assertTrue(test);
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testSetGuestContactRestriction() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		AbstractDomain rootDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlRootDomain);
		Internal user1 = new Internal("John","Doe","user1@linpki.org", null);
		user1.setDomain(rootDomain);
		user1.setCanCreateGuest(true);
		
		userService.saveOrUpdateUser(user1);
		
		AbstractDomain guestDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlGuestDomain);
		guestDomain.setDefaultLocale("en");
		
		//create guest
		Guest guest2 = new Guest("Jane","Smith","user2@linpki.org");
		guest2.setDomain(abstractDomainRepository.findById(LoadingServiceTestDatas.sqlGuestDomain));
		guest2.setOwner(user1);
		guest2.setExternalMailLocale(guestDomain.getDefaultLocale());
		guest2.setLocale(guestDomain.getDefaultLocale());
		guestRepository.create(guest2);
		
		//create guest
		Guest guest = new Guest("Foo","Bar","user3@linpki.org");
		guest.setDomain(abstractDomainRepository.findById(LoadingServiceTestDatas.sqlGuestDomain));
		guest.setOwner(user1);
		guest.setRestricted(true);
		guest.setExternalMailLocale(guestDomain.getDefaultLocale());
		guest.setLocale(guestDomain.getDefaultLocale());
		
		guestRepository.create(guest);
		
		List<String> mailContacts = new ArrayList<String>();
		mailContacts.add(guest2.getMail());
		userService.setGuestContactRestriction(guest.getLsUuid(), mailContacts);
		List<AllowedContact> listAllowedContact= allowedContactRepository.findByOwner(guest);
		boolean test = false;
		for (AllowedContact allowedContact : listAllowedContact) {
			if(allowedContact.getContact().getLsUuid().equals(guest2.getLsUuid())){
				test=true;
			}	
		}
		Assert.assertTrue(test);
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	
	@Test
	public void testFetchGuestContacts() throws IllegalArgumentException, BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		AbstractDomain rootDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlRootDomain);
		Internal user1 = new Internal("John","Doe","user1@linpki.org", null);
		user1.setDomain(rootDomain);
		user1.setCanCreateGuest(true);
		
		userService.saveOrUpdateUser(user1);
		
		AbstractDomain guestDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlGuestDomain);
		guestDomain.setDefaultLocale("en");
		
		//create guest
		Guest guest2 = new Guest("Jane","Smith","user2@linpki.org");
		guest2.setDomain(abstractDomainRepository.findById(LoadingServiceTestDatas.sqlGuestDomain));
		guest2.setOwner(user1);
		guest2.setExternalMailLocale(guestDomain.getDefaultLocale());
		guest2.setLocale(guestDomain.getDefaultLocale());
		guestRepository.create(guest2);
		
		//create guest
		Guest guest = new Guest("Foo","Bar","user3@linpki.org");
		guest.setDomain(abstractDomainRepository.findById(LoadingServiceTestDatas.sqlGuestDomain));
		guest.setOwner(user1);
		guest.setRestricted(true);
		guest.setExternalMailLocale(guestDomain.getDefaultLocale());
		guest.setLocale(guestDomain.getDefaultLocale());
		guestRepository.create(guest);
		
		userService.addGuestContactRestriction(guest.getLsUuid(), guest2.getLsUuid());
		List<User> guestContacts = userService.fetchGuestContacts(guest.getLsUuid());
		Assert.assertTrue(guestContacts.contains(guest2));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	
	@Test
	public void testGetGuestEmailContacts() throws IllegalArgumentException, BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		AbstractDomain rootDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlRootDomain);
		Internal user1 = new Internal("John","Doe","user1@linpki.org", null);
		user1.setDomain(rootDomain);
		user1.setCanCreateGuest(true);
		
		userService.saveOrUpdateUser(user1);
		
		AbstractDomain guestDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlGuestDomain);
		guestDomain.setDefaultLocale("en");
		
		//create guest
		Guest guest2 = new Guest("Jane","Smith","user2@linpki.org");
		guest2.setDomain(guestDomain);
		guest2.setOwner(user1);
		guest2.setExternalMailLocale(guestDomain.getDefaultLocale());
		guest2.setLocale(guestDomain.getDefaultLocale());
		guestRepository.create(guest2);
		
		//create guest
		Guest guest = new Guest("Foo","Bar","user3@linpki.org");
		guest.setDomain(guestDomain);
		guest.setOwner(user1);
		guest.setRestricted(true);
		guest.setExternalMailLocale(guestDomain.getDefaultLocale());
		guest.setLocale(guestDomain.getDefaultLocale());
		guestRepository.create(guest);
		
		userService.addGuestContactRestriction(guest.getLsUuid(), guest2.getLsUuid());
		List<String> guestEmailContacts = userService.getGuestEmailContacts(guest.getMail());
		Assert.assertTrue(guestEmailContacts.contains(guest2.getMail()));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testUpdateUserDomain() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		AbstractDomain rootDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlRootDomain);
		Internal user1 = new Internal("John","Doe","user1@linpki.org", null);
		user1.setDomain(rootDomain);
		user1.setRole(Role.SUPERADMIN);
		
		userService.saveOrUpdateUser(user1);
		UserVo userVo = new UserVo(user1);
		
		AbstractDomain subDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlSubDomain);
		Internal user2 = new Internal("Jane","Smith","user2@linpki.org", null);
		user2.setDomain(subDomain);
		
		userService.saveOrUpdateUser(user2);
		
		userService.updateUserDomain(user2.getMail(), LoadingServiceTestDatas.sqlGuestDomain, userVo);
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testFindOrCreateUserWithDomainPolicies() throws IllegalArgumentException, BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		AbstractDomain rootDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlRootDomain);
		Internal user1 = new Internal("John","Doe","user1@linpki.org", null);
		user1.setDomain(rootDomain);
		
		userService.saveOrUpdateUser(user1);
		
		AbstractDomain subDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlSubDomain);
		Internal user2 = new Internal("Jane","Smith","user2@linpki.org", null);
		user2.setDomain(subDomain);
		
		userService.saveOrUpdateUser(user2);
		
		Assert.assertEquals(user1, userService.findOrCreateUserWithDomainPolicies(LoadingServiceTestDatas.sqlRootDomain, "user1@linpki.org", LoadingServiceTestDatas.sqlSubDomain));

		
		DomainPolicy domainPolicy = new DomainPolicy("domainPolicy", new DomainAccessPolicy());
		domainPolicyRepository.create(domainPolicy);
		
		DomainAccessRule endRule = new DenyAllDomain();
		domainPolicy.getDomainAccessPolicy().addRule(endRule);
		subDomain.setPolicy(domainPolicy);
		
		domainPolicyRepository.update(domainPolicy);
		
		abstractDomainRepository.update(subDomain);		
		
		try{
			userService.findOrCreateUserWithDomainPolicies(LoadingServiceTestDatas.sqlRootDomain, "user1@linpki.org", LoadingServiceTestDatas.sqlSubDomain);
			
			logger.error("Test shouldn't go here because findOrCreateUserWithDomainPolicies should rise a exception");
			Assert.fail();
		}catch(BusinessException e){
			logger.debug("Test succeed"); 
		}
		
		User foundUser = userService.findOrCreateUserWithDomainPolicies(LoadingServiceTestDatas.sqlRootDomain, "user1@linpki.org", null);
		Assert.assertEquals(foundUser, user1);

		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testFindOrCreateUser() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		long id_user=0;
		
		try {
			logger.debug("Trying to find john doe: should create this user");
			User user = userService.findOrCreateUserWithDomainPolicies("user1@linpki.org", LoadingServiceTestDatas.sqlSubDomain);
			Assert.assertEquals("John", user.getFirstName());
			Assert.assertEquals("Doe", user.getLastName());
			id_user= user.getId();
			logger.debug("the user created was " + user.getFirstName() + " " + user.getLastName() +" (id=" + user.getId() + ")");
		} catch (BusinessException e) {
			logger.error("userService can not create an user ");
			logger.error(e.toString());
		}
		
		try {
			logger.debug("Trying to find john doe: should return this user");
			User user = userService.findOrCreateUser("user1@linpki.org", LoadingServiceTestDatas.sqlSubDomain);
			Assert.assertEquals("John", user.getFirstName());
			Assert.assertEquals("Doe", user.getLastName());
			// Should be the same user, not a new one.
			Assert.assertEquals(id_user, user.getId());
			logger.debug("the user created was " + user.getFirstName() + " " + user.getLastName() +" (id=" + user.getId() + ")");
			
		} catch (BusinessException e) {
			logger.error("userService can not find an user ");
			logger.error(e.toString());
		}
		
		try {
			logger.debug("Trying to find john doe: should return this user");
			User user = userService.findOrCreateUser("user1@linpki.org", LoadingServiceTestDatas.sqlSubDomain);
			Assert.assertEquals("John", user.getFirstName());
			Assert.assertEquals("Doe", user.getLastName());
			// Should be the same user, not a new one.
			Assert.assertEquals(id_user, user.getId());
			logger.debug("the user created was " + user.getFirstName() + " " + user.getLastName() +" (id=" + user.getId() + ")");
			
		} catch (BusinessException e) {
			logger.error("userService can not find an user ");
			logger.error(e.toString());
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test 
	public void testSearchAndCreateUserEntityFromDirectory() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		AbstractDomain rootDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlRootDomain);
		Internal user1 = new Internal("John","Doe","user1@linpki.org", null);
		user1.setDomain(rootDomain);
		
		Assert.assertNull(userService.findUnkownUserInDB("user1@linpki.org"));
		
		userService.searchAndCreateUserEntityFromDirectory(LoadingServiceTestDatas.sqlRootDomain, "user1@linpki.org");

		Assert.assertNotNull(userService.findUnkownUserInDB("user1@linpki.org"));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	@Rollback(true)
	public void testSaveOrUpdateUser() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain domain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlSubDomain);
		
		Internal user = new Internal("John","Doe","user1@linpki.org", null);
		
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
	}
	

		
	
	
	
}
