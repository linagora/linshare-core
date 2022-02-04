/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.repository.hibernate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.PasswordService;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml" })
public class GuestRepositoryImplTest {

	private static Logger logger = LoggerFactory.getLogger(GuestRepositoryImplTest.class);

	private static final String FIRST_NAME = "first name";
	private static final String LAST_NAME = "last name";
	private static final String MAIL = "mail";
	private static final String PASSWORD = "password";

	private static final String FIRST_NAME2 = "jean";
	private static final String LAST_NAME2 = "laporte";
	private static final String MAIL2 = "foo@yopmail.com";

	private static final String FIRST_NAME3 = "robert";
	private static final String LAST_NAME3 = "lepoint";
	private static final String MAIL3 = "foo@lepoint.com";

	private static final String O_FIRST_NAME = "John";
	private static final String O_LAST_NAME = "Doe";
	private static final String O_MAIL = "user1@linshare.org";

	private boolean flag = false;

	// default import.sql
 	private static final String DOMAIN_IDENTIFIER = LinShareConstants.rootDomainIdentifier;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;
	
	@Autowired
	private PasswordService passwordService;

	@Autowired
	@Qualifier("guestRepository")
	private GuestRepository guestRepository;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	private AbstractDomain domain;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);

		domain = abstractDomainRepository.findById(DOMAIN_IDENTIFIER);
		String encpass = passwordService.encode(PASSWORD);
		if (!flag) {
			Guest u1 = new Guest(FIRST_NAME2, LAST_NAME2, MAIL2, encpass, true, "comment");
			u1.setLocale(domain.getDefaultTapestryLocale());
			u1.setCmisLocale(domain.getDefaultTapestryLocale().toString());
			u1.setDomain(domain);
			guestRepository.create(u1);

			Guest u2 = new Guest(FIRST_NAME3, LAST_NAME3, MAIL3, encpass, true, "comment");
			u2.setLocale(domain.getDefaultTapestryLocale());
			u2.setCmisLocale(domain.getDefaultTapestryLocale().toString());
			u2.setDomain(domain);
			guestRepository.create(u2);
			flag = true;
		}

		logger.debug(LinShareTestConstants.END_SETUP);
	}

//	@Test
//	public void testExistGuest() throws BusinessException {
//		logger.info(LinShareTestConstants.BEGIN_TEST);
//		String encpassword = HashUtils.hashSha1withBase64(PASSWORD.getBytes());
//		Guest u = new Guest(FIRST_NAME, LAST_NAME, MAIL, encpassword,
//				true,  "comment");
//		guestRepository.create(u);
//		Assertions.assertTrue(guestRepository.exist(LOGIN, encpassword));
//		Assertions.assertFalse(guestRepository.exist(LOGIN, "pass"));
//		Assertions.assertFalse(guestRepository.exist("login90", encpassword));
//		logger.debug(LinShareTestConstants.END_TEST);
//	}

//	@Test
//	public void testfindGuest() throws BusinessException {
//		logger.info(LinShareTestConstants.BEGIN_TEST);
//
//		Guest u = new Guest(FIRST_NAME, LAST_NAME, MAIL);
//		AbstractDomain domain = abstractDomainRepository
//				.findById(DOMAIN_IDENTIFIER);
//
//		u.setDomain(domain);
//		guestRepository.create(u);
//
//		Guest userFound = null;
//
//		userFound = guestRepository.findByLogin(LOGIN);
//		Assertions.assertNotNull(userFound);
//		Assertions.assertEquals(FIRST_NAME, userFound.getFirstName());
//		userFound = null;
//
//		userFound = guestRepository
//				.findByMailAndDomain(DOMAIN_IDENTIFIER, MAIL);
//		Assertions.assertNotNull(userFound);
//		Assertions.assertEquals(FIRST_NAME, userFound.getFirstName());
//		userFound = null;
//		logger.info(LinShareTestConstants.END_TEST);
//
//	}

//	@Test
//	public void testShares() throws IllegalArgumentException, BusinessException {
//		logger.info(LinShareTestConstants.BEGIN_TEST);
//
//		Guest sender = guestRepository.findByLogin(LOGIN2);
//
//		Guest receiver = guestRepository.findByLogin(LOGIN3);
//
//		/**
//		 * Creation of a document.
//		 */
//		Document document = new Document("document1", "hop.txt", "txt",
//				GregorianCalendar.getInstance(),
//				GregorianCalendar.getInstance(), sender, false, true,
//				new Long(100000));
//
//		documentRepository.create(document);
//
//		/**
//		 * Creation of share
//		 */
//		Share share = new Share(sender, receiver, document, "plop",
//				GregorianCalendar.getInstance(), true, false);
//		shareRepository.create(share);
//
//		sender.addShare(share);
//		receiver.addReceivedShare(share);
//		guestRepository.update(sender);
//		guestRepository.update(receiver);
//		sender = guestRepository.findByLogin(sender.getMail());
//		receiver = guestRepository.findByLogin(receiver.getMail());
//
//		for (Share currentShare : sender.getShares()) {
//			System.out.println("Sender: " + currentShare.getSender().getMail());
//			System.out.println("Receiver: "
//					+ currentShare.getReceiver().getMail());
//		}
//		if (!(sender.getShares().contains(share) && receiver
//				.getReceivedShares().contains(share))) {
//			fail();
//		}
//		logger.info(LinShareTestConstants.END_TEST);
//
//	}

	@Test
	public void testSearchGuest() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		List<Guest> results = null;

		User owner = new Internal(O_FIRST_NAME, O_LAST_NAME, O_MAIL, null);
		owner.setDomain(domain);
		owner.setLocale(domain.getDefaultTapestryLocale());
		owner.setCmisLocale(domain.getDefaultTapestryLocale().toString());
		userRepository.create(owner);

		Guest u = new Guest(FIRST_NAME, LAST_NAME, MAIL);
		u.setOwner(owner);
		u.setDomain(domain);
		u.setLocale(domain.getDefaultTapestryLocale());
		u.setCmisLocale(domain.getDefaultTapestryLocale().toString());

		guestRepository.create(u);

		results = guestRepository.searchGuest(owner, null, null, null);
		assertNotNull(results);
		assertFalse(results.isEmpty());
		assertEquals(results.size(), 1);
		results = null;

		results = guestRepository.searchGuest(null, null, null, LAST_NAME);
		assertNotNull(results);
		assertFalse(results.isEmpty());
		assertEquals(results.size(), 1);
		results = null;

		results = guestRepository.searchGuest(null, null, FIRST_NAME, null);
		assertNotNull(results);
		assertFalse(results.isEmpty());
		assertEquals(results.size(), 1);
		results = null;

		results = guestRepository.searchGuest(null, MAIL, null, null);
		assertNotNull(results);
		assertFalse(results.isEmpty());
		assertEquals(results.size(), 2);
		results = null;

		results = guestRepository.searchGuest(null, "foo@", null, null);
		assertNotNull(results);
		assertFalse(results.isEmpty());
		assertEquals(results.size(), 2);
		results = null;

		results = guestRepository.searchGuestAnyWhere("foo@", null, null);
		assertNotNull(results);
		assertFalse(results.isEmpty());
		assertEquals(results.size(), 2);

		logger.info(LinShareTestConstants.END_TEST);

	}

	@Test
	public void testFindOutdatedGuestIdentifiers() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		Guest u = new Guest(FIRST_NAME, LAST_NAME, MAIL);
		u.setExpirationDate(new Date(0));
		u.setDomain(domain);
		u.setLocale(domain.getDefaultTapestryLocale());
		u.setCmisLocale(domain.getDefaultTapestryLocale().toString());
		guestRepository.create(u);

		List<String> results = guestRepository.findOutdatedGuestIdentifiers();
		logger.debug("results : " + results.toString());
		assertNotNull(results);
		assertFalse(results.isEmpty());
		assertEquals(results.size(), 1);
		assertEquals(u.getLsUuid(), results.get(0));
		logger.info(LinShareTestConstants.END_TEST);
	}

}
