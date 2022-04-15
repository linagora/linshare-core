/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2017-2022 LINAGORA
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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.ContactListContact;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.MailingListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations = { 
		"classpath:springContext-test.xml",
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml" })
public class ConatctListSearchRepositoryImplTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

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

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug("Begin setUp");

		domain = abstractDomainRepository.findById(DOMAIN_IDENTIFIER);
		internal = new Internal(FIRST_NAME, LAST_NAME, MAIL, UID);
		internal.setCmisLocale(domain.getDefaultTapestryLocale().toString());
		internal.setDomain(domain);
		accountRepository.create(internal);

		mailingList1 = new ContactList();
		mailingList1.setIdentifier(identifier1);
		mailingList1.setOwner(internal);
		mailingList1.setDomain(domain);
		mailingList1.setPublic(true);
		mailingList1.setDescription("yoyo");
		mailingList1.setContactListContacts(new HashSet<ContactListContact>());
		mailingListRepository.create(mailingList1);

		mailingList2 = new ContactList();
		mailingList2.setIdentifier(identifier2);
		mailingList2.setOwner(internal);
		mailingList2.setDomain(domain);
		mailingList2.setPublic(false);
		mailingList2.setDescription("fofo");
		mailingList2.setContactListContacts(new HashSet<ContactListContact>());
		mailingListRepository.create(mailingList2);

		contact = new ContactListContact();
		contact.setFirstName(FIRST_NAME);
		contact.setLastName(LAST_NAME);
		contact.setMail(CONTACT_MAIL);
		contact.setUuid(UID);
		contact.setCreationDate(new Date());
		contact.setModificationDate(new Date());
		Set<ContactListContact> contacts = new HashSet<>();
		contacts.add(contact);
		mailingList1.setContactListContacts(contacts);
		mailingListRepository.update(mailingList1);

		logger.debug("End setUp");
	}

	@AfterEach
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
		Assertions.assertEquals(mailingLists.size(), 1,
				"just one list contains the member who has the mentioned email");
	}
}
