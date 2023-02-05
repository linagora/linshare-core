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
package org.linagora.linshare.repository.hibernate;

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
public class MailingListRepositoryImplTest {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	// default import.sql
	private static final String DOMAIN_IDENTIFIER = LinShareConstants.rootDomainIdentifier;

	private static final String FIRST_NAME = "first name";
	private static final String LAST_NAME = "last name";
	private static final String MAIL = "mail";
	private static final String UID = "uid";

	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

	@Autowired
	private MailingListRepository mailingListRepository;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	private AbstractDomain domain;

	private User internal;

	private static String identifier = "TestMailingList0";

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug("Begin setUp");

		domain = abstractDomainRepository.findById(DOMAIN_IDENTIFIER);
		internal = new Internal(FIRST_NAME, LAST_NAME, MAIL, UID);
		internal.setCmisLocale(domain.getDefaultTapestryLocale().toString());
		internal.setDomain(domain);
		accountRepository.create(internal);

		logger.debug("End setUp");
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");

		accountRepository.delete(internal);

		logger.debug("End tearDown");
	}

	@Test
	public void testCreateMailingList1() throws BusinessException {

		ContactList current = new ContactList();
		current.setIdentifier(identifier);
		current.setOwner(internal);
		current.setDomain(domain);
		current.setPublic(true);
		current.setDescription("yoyo");

		mailingListRepository.create(current);

		Assertions.assertNotNull(current.getPersistenceId());

		ContactList myList = mailingListRepository.findByIdentifier(internal,
				identifier);
		Assertions.assertTrue(myList != null);

		mailingListRepository.delete(myList);
	}
}
