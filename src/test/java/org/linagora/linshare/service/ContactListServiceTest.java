/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2019. Contribute to
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
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.ContactListContact;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.FunctionalityRepository;
import org.linagora.linshare.core.repository.MailingListRepository;
import org.linagora.linshare.core.service.ContactListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
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
public class ContactListServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	// default import.sql
	private static final String DOMAIN_IDENTIFIER = LinShareConstants.rootDomainIdentifier;

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
	private MailingListRepository mailingListRepository;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;
	
	@Autowired
	private ContactListService contactListService;

	@Autowired
	private FunctionalityRepository functionalityRepository;

	private AbstractDomain domain;

	private User internal;

	private static String identifier1 = "TestContactList1";

	private static String identifier2 = "TestContactList2";

	private ContactList contactList1, contactList2;

	private ContactListContact contact;

	private ContactListContact contact1;

	private ContactListContact contact2;

	@Before
	public void setUp() throws Exception {
		logger.debug("Begin setUp");

		domain = abstractDomainRepository.findById(DOMAIN_IDENTIFIER);
		internal = new Internal(FIRST_NAME, LAST_NAME, MAIL, UID);
		internal.setLocale(domain.getDefaultTapestryLocale());
		internal.setCmisLocale(domain.getDefaultTapestryLocale().toString());
		internal.setDomain(domain);
		accountRepository.create(internal);

		contactList1 = new ContactList();
		contactList1.setIdentifier(identifier1);
		contactList1.setOwner(internal);
		contactList1.setDomain(domain);
		contactList1.setPublic(true);
		contactList1.setDescription("yoyo");
		contactList1.setMailingListContact(new ArrayList<ContactListContact>());
		mailingListRepository.create(contactList1);

		contactList2 = new ContactList();
		contactList2.setIdentifier(identifier2);
		contactList2.setOwner(internal);
		contactList2.setDomain(domain);
		contactList2.setPublic(false);
		contactList2.setDescription("fofo");
		contactList2.setMailingListContact(new ArrayList<ContactListContact>());
		mailingListRepository.create(contactList2);

		contact = newContact(UID, CONTACT_MAIL);
		contact1 = newContact(UID1, CONTACT_MAIL1);
		contact2 = newContact(UID2, CONTACT_MAIL2);
		List<ContactListContact> contacts = new ArrayList<>();
		contacts.add(contact);
		contacts.add(contact1);
		contacts.add(contact2);
		contactList1.setMailingListContact(contacts);
		mailingListRepository.update(contactList1);

		logger.debug("End setUp");
	}

	@After
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");

		mailingListRepository.delete(contactList1);
		mailingListRepository.delete(contactList2);
		accountRepository.delete(internal);

		logger.debug("End tearDown");
	}

	@Test
	public void testfindMailingList1ByMemberEmail() throws BusinessException {
		List<ContactList> contactLists = contactListService.findAllByMemberEmail(internal, internal, null, CONTACT_MAIL);
		Assert.assertEquals("just one list contains the member who has the mentioned email", contactLists.size(), 1);
		ContactList duplicatedContactList = contactListService.duplicate(internal, internal, contactLists.get(0), "contactList duplicated");
		Assert.assertEquals(3, duplicatedContactList.getMailingListContact().size());
		contactListService.deleteList(internal.getLsUuid(), duplicatedContactList.getUuid());
		ContactList deletedContactList = contactListService.findByIdentifier(internal.getLsUuid(), duplicatedContactList.getUuid());
		Assert.assertNull(deletedContactList);
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
		contactList.setMailingListContact(new ArrayList<ContactListContact>());
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			contactListService.create(internal, internal, contactList);
		});
		Assertions.assertEquals(BusinessErrorCode.FORBIDDEN, exception.getErrorCode());
		Assertions.assertEquals("You are not authorized to create an entry.", exception.getMessage());
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
}
