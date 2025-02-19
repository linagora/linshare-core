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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.ContactListContact;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.FunctionalityRepository;
import org.linagora.linshare.core.repository.MailingListRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.service.ContactListService;
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
		"classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml" })
public class ContactListServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(ContactListServiceTest.class);

	// default import.sql
	private static final String DOMAIN_IDENTIFIER = LinShareConstants.rootDomainIdentifier;
	private static final String DOMAIN_GUEST_IDENTIFIER = LinShareConstants.guestDomainIdentifier;

	private static final String FIRST_NAME = "first name";
	private static final String LAST_NAME = "last name";
	private static final String MAIL = "mail";
	private static final String UID = "uid";
	private static final String UID1 = "uid1";
	private static final String UID2 = "uid2";
	private static final String CONTACT_MAIL = "c@mail";
	private static final String CONTACT_MAIL1 = "c1@mail";
	private static final String CONTACT_MAIL2 = "c2@mail";
	private static final int CONTACT_CAN_CREATE_FUNCTIONALITY_ID = 27;
	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	private MailingListRepository mailingListRepository;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	@Autowired
	private ContactListService contactListService;

	@Autowired
	private FunctionalityRepository functionalityRepository;

	@Autowired
	private GuestRepository guestRepository;

	private AbstractDomain domain;

	private User internal, john;

	private static String identifier1 = "TestContactList1";

	private static String identifier2 = "TestContactList2";

	private ContactList contactList1, contactList2;

	private ContactListContact contact;

	private ContactListContact contact1;

	private ContactListContact contact2;

	private Guest guest;

	private AbstractDomain guest_domain;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug("Begin setUp");

		john = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		domain = abstractDomainRepository.findById(DOMAIN_IDENTIFIER);
		guest_domain = abstractDomainRepository.findById(DOMAIN_GUEST_IDENTIFIER);

		internal = new Internal(FIRST_NAME, LAST_NAME, MAIL, UID);
		internal.setCmisLocale(domain.getDefaultTapestryLocale().toString());
		internal.setDomain(domain);
		accountRepository.create(internal);

		this.guest = guestRepository.findByMail(LinShareTestConstants.GUEST_ACCOUNT);
		this.guest.setDomain(guest_domain);

		contactList1 = new ContactList();
		contactList1.setIdentifier(identifier1);
		contactList1.setOwner(internal);
		contactList1.setDomain(domain);
		contactList1.setPublic(true);
		contactList1.setDescription("yoyo");
		contactList1.setContactListContacts(new HashSet<ContactListContact>());
		mailingListRepository.create(contactList1);

		contactList2 = new ContactList();
		contactList2.setIdentifier(identifier2);
		contactList2.setOwner(internal);
		contactList2.setDomain(domain);
		contactList2.setPublic(false);
		contactList2.setDescription("fofo");
		contactList2.setContactListContacts(new HashSet<ContactListContact>());
		mailingListRepository.create(contactList2);

		contact = newContact(UID, CONTACT_MAIL);
		contact1 = newContact(UID1, CONTACT_MAIL1);
		contact2 = newContact(UID2, CONTACT_MAIL2);
		final Set<ContactListContact> contacts = new HashSet<>();
		contacts.add(contact);
		contacts.add(contact1);
		contacts.add(contact2);
		contactList1.setContactListContacts(contacts);
		mailingListRepository.update(contactList1);

		logger.debug("End setUp");
	}


	@AfterEach
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");

		mailingListRepository.delete(contactList1);
		mailingListRepository.delete(contactList2);
		accountRepository.delete(internal);

		logger.debug("End tearDown");
	}

	@Test
	public void testCreateContactListSpecialCharacter() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		ContactList contactList = new ContactList();
		contactList.setIdentifier("EP_TEST_v233<script>alert(document.cookie)</script>");
		contactList.setOwner(internal);
		contactList.setDomain(domain);
		contactList.setPublic(false);
		contactList.setDescription("EP_TEST_v233<script>alert(document.cookie)</script>");
		contactList.setContactListContacts(new HashSet<ContactListContact>());
		contactListService.create(internal, internal, contactList);
		assertEquals(contactList.getIdentifier(), "EP_TEST_v233");
		assertEquals(contactList.getDescription(), "EP_TEST_v233");
		ContactList deletedContactList = contactListService.findByIdentifier(internal.getLsUuid(),
				contactList.getUuid());
		assertNull(deletedContactList);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testUpdateContactListSpecialCharacter() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		ContactList contactList = new ContactList();
		contactList.setIdentifier("contactList");
		contactList.setOwner(internal);
		contactList.setDomain(domain);
		contactList.setPublic(false);
		contactList.setDescription("EP_TEST_v233<script>alert(document.cookie)</script>");
		contactList.setContactListContacts(new HashSet<ContactListContact>());
		contactListService.create(internal, internal, contactList);
		contactList.setIdentifier("EP_TEST_v233<script>alert(document.cookie)</script>");
		contactListService.update(internal, internal, contactList);
		assertEquals(contactList.getIdentifier(), "EP_TEST_v233");
		assertEquals(contactList.getDescription(), "EP_TEST_v233");
		ContactList deletedContactList = contactListService.findByIdentifier(internal.getLsUuid(),
				contactList.getUuid());
		assertNull(deletedContactList);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testfindMailingList1ByMemberEmail() throws BusinessException {
		List<ContactList> contactLists = contactListService.findAllByMemberEmail(internal, internal, null, CONTACT_MAIL);
		assertEquals(contactLists.size(), 1, "just one list contains the member who has the mentioned email");
		ContactList duplicatedContactList = contactListService.duplicate(internal, internal, contactLists.get(0), "contactList duplicated");
		assertEquals(3, duplicatedContactList.getContactListContacts().size());
		contactListService.deleteList(internal.getLsUuid(), duplicatedContactList.getUuid());
		ContactList deletedContactList = contactListService.findByIdentifier(internal.getLsUuid(), duplicatedContactList.getUuid());
		assertNull(deletedContactList);
	}

	@Test
	public void testCreateWithfunctionalityDisabled() throws BusinessException {
		Functionality canCreateContactListFunctionality = functionalityRepository
				.findById(CONTACT_CAN_CREATE_FUNCTIONALITY_ID);
		canCreateContactListFunctionality.getActivationPolicy().setStatus(false);
		functionalityRepository.update(canCreateContactListFunctionality);
		ContactList contactList = new ContactList();
		contactList.setIdentifier(identifier2);
		contactList.setOwner(internal);
		contactList.setDomain(domain);
		contactList.setPublic(false);
		contactList.setDescription("fofo");
		contactList.setContactListContacts(new HashSet<ContactListContact>());
		final BusinessException exception = assertThrows(BusinessException.class, () -> {
			contactListService.create(internal, internal, contactList);
		});
		assertEquals(BusinessErrorCode.FORBIDDEN, exception.getErrorCode());
		assertEquals("You are not authorized to create an entry.", exception.getMessage());
	}

	/**
	 * Test a duplication of public contact_list 
	 */
	@Test
	public void testDuplicate() {
		Functionality canCreateContactListFunctionality = functionalityRepository
				.findById(CONTACT_CAN_CREATE_FUNCTIONALITY_ID);
		canCreateContactListFunctionality.getActivationPolicy().setStatus(false);
		functionalityRepository.update(canCreateContactListFunctionality);
		ContactList duplicatedContactList = contactListService.duplicate(internal, internal, contactList1, "Copy");
		assertAll("Duplication fails", () -> {
			assertEquals(duplicatedContactList.getDomain(), contactList1.getDomain());
			assertEquals(duplicatedContactList.getDescription(), contactList1.getDescription());
			assertEquals(duplicatedContactList.getOwner(), contactList1.getOwner());
			assertEquals(duplicatedContactList.isPublic(), contactList1.isPublic());
		});
		contactList1.setPublic(false);
		final BusinessException exception = assertThrows(BusinessException.class, () -> {
			contactListService.duplicate(john, john, contactList1, "Copy");
		});
		assertEquals(BusinessErrorCode.CONTACT_LIST_DUPLICATION_FORBIDDEN, exception.getErrorCode());
	}

	// helpers
	public ContactListContact newContact(String uuid, String mail) {
		ContactListContact newContact = new ContactListContact();
		newContact.setFirstName(FIRST_NAME);
		newContact.setLastName(LAST_NAME);
		newContact.setMail(mail);
		newContact.setUuid(uuid);
		newContact.setCreationDate(new Date());
		newContact.setModificationDate(new Date());
		return newContact;
	}

	@Test
	public void testUpdateGuestAccountDomainId() throws BusinessException {

		final List<ContactList> contacts = contactListService.findAllListManagedByUser(guest.getLsUuid());
		for (final ContactList contactList : contacts) {
			this.contactListService.transferContactListFromGuestToInternal(guest, internal);
			assertEquals(internal.getDomain().getPersistenceId(), contactList.getDomain().getPersistenceId());
		}
	}
}
