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

package org.linagora.linshare.repository.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.ContactListContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.MailingListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = { "classpath:springContext-test.xml",
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml" })
public class ConatctListSearchRepositoryImplTest extends AbstractJUnit4SpringContextTests {

	// default import.sql
	private static final String DOMAIN_IDENTIFIER = LinShareConstants.rootDomainIdentifier;

	private static final String FIRST_NAME = "first name";
	private static final String LAST_NAME = "last name";
	private static final String MAIL = "mail";
	private static final String UID = "uid";
	private static final String CONTACT_MAIL = "c@mail";

	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

	@Autowired
	private MailingListRepository mailingListRepository;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	private AbstractDomain domain;

	private User internal;

	private static String identifier1 = "TestMailingList1";

	private static String identifier2 = "TestMailingList2";

	private ContactList mailingList1, mailingList2;

	private ContactListContact contact;

	@Before
	public void setUp() throws Exception {
		logger.debug("Begin setUp");

		domain = abstractDomainRepository.findById(DOMAIN_IDENTIFIER);
		internal = new Internal(FIRST_NAME, LAST_NAME, MAIL, UID);
		internal.setLocale(domain.getDefaultTapestryLocale());
		internal.setCmisLocale(domain.getDefaultTapestryLocale().toString());
		internal.setDomain(domain);
		accountRepository.create(internal);

		mailingList1 = new ContactList();
		mailingList1.setIdentifier(identifier1);
		mailingList1.setOwner(internal);
		mailingList1.setDomain(domain);
		mailingList1.setPublic(true);
		mailingList1.setDescription("yoyo");
		mailingList1.setMailingListContact(new ArrayList<ContactListContact>());
		mailingListRepository.create(mailingList1);

		mailingList2 = new ContactList();
		mailingList2.setIdentifier(identifier2);
		mailingList2.setOwner(internal);
		mailingList2.setDomain(domain);
		mailingList2.setPublic(false);
		mailingList2.setDescription("fofo");
		mailingList2.setMailingListContact(new ArrayList<ContactListContact>());
		mailingListRepository.create(mailingList2);

		contact = new ContactListContact();
		contact.setFirstName(FIRST_NAME);
		contact.setLastName(LAST_NAME);
		contact.setMail(CONTACT_MAIL);
		contact.setUuid(UID);
		contact.setCreationDate(new Date());
		contact.setModificationDate(new Date());
		List<ContactListContact> contacts = new ArrayList<>();
		contacts.add(contact);
		mailingList1.setMailingListContact(contacts);
		mailingListRepository.update(mailingList1);

		logger.debug("End setUp");
	}

	@After
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");

		mailingListRepository.delete(mailingList1);
		mailingListRepository.delete(mailingList2);
		accountRepository.delete(internal);

		logger.debug("End tearDown");
	}

	@Test
	public void testfindMailingList1ByMemberEmail() throws BusinessException {
		List<ContactList> mailingLists = mailingListRepository.findAllByMemberEmail(internal, CONTACT_MAIL);
		Assert.assertEquals("just one list contains the member who has the mentioned email", mailingLists.size(), 1);
	}
}
