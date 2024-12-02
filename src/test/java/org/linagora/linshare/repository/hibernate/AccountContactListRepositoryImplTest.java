package org.linagora.linshare.repository.hibernate;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountContactListId;
import org.linagora.linshare.core.domain.entities.AccountContactLists;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountContactListsRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.repository.MailingListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
@Sql(value = { "/import-tests-guests.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DirtiesContext
public class AccountContactListRepositoryImplTest {

	private static final Logger logger = LoggerFactory.getLogger(AccountContactListRepositoryImplTest.class);
	private static final String DOMAIN_IDENTIFIER = LinShareConstants.rootDomainIdentifier;
	private static final String DOMAIN_Guest = LinShareConstants.guestDomainIdentifier;
	@Autowired
	AccountContactListsRepository accountContactListsRepository;

	@Autowired
	GuestRepository guestRepository;

	@Autowired
	AbstractDomainRepository abstractDomainRepository;

	private ContactList restrictedContact1, restrictedContact2;
	private User u;
	private Guest guest;

	private static final String FIRST_NAME = "first name";
	private static final String LAST_NAME = "last name";
	private static final String MAIL = "mail";
	private static final String UID = "uid";
	@Autowired
	MailingListRepository mailingListRepository;

	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;
	@BeforeEach
	public void setUp(){
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		AbstractDomain domain = abstractDomainRepository.findById(DOMAIN_IDENTIFIER);
		AbstractDomain domain_guest = abstractDomainRepository.findById(DOMAIN_Guest);
		User u = new Internal(FIRST_NAME, LAST_NAME, MAIL, UID);
		u.setCmisLocale(domain.getDefaultTapestryLocale().toString());
		u.setDomain(domain);
		accountRepository.create(u);
		restrictedContact1 = createContactList("identifier1", "yoyo", u, domain);
		restrictedContact2 = createContactList("identifier2", "yoyoyy", u, domain);
		List<String> restrictedContactList = Lists.newArrayList();
		restrictedContactList.add(restrictedContact1.getUuid());
		restrictedContactList.add(restrictedContact2.getUuid());
		guest = new Guest("Guest", "Doe", "guest1@linshare.org");
		guest.setCmisLocale("en");
		guest.setDomain(domain_guest);
		this.guestRepository.create(guest);
		createAccountContactList(guest, restrictedContact1);
		createAccountContactList(guest, restrictedContact2);

		logger.debug(LinShareTestConstants.END_SETUP);
	}
	private void createAccountContactList(Guest guest, ContactList contactList) {
		AccountContactListId accountContactListId = new AccountContactListId(guest, contactList);
		AccountContactLists accountContactList = new AccountContactLists();
		accountContactList.setId(accountContactListId);
		accountContactList.setAccount(guest);
		accountContactList.setContactList(contactList);
		this.accountContactListsRepository.create(accountContactList);
	}
	private ContactList createContactList(String identifier, String description, User owner, AbstractDomain domain) {
		ContactList contactList = new ContactList();
		contactList.setIdentifier(identifier);
		contactList.setOwner(owner);
		contactList.setPublic(true);
		contactList.setDomain(domain);
		contactList.setDescription(description);
		contactList.setContactListContacts(new HashSet<>());
		this.mailingListRepository.create(contactList);
		return contactList;
	}

	@Test
	public void findByAccount(){
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<AccountContactLists> accountContactLists1 =accountContactListsRepository.findByAccount(guest);
		assertEquals(2, accountContactLists1.size());
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void findByAccountAndContactListName(){
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Optional<AccountContactLists> accountContactListsList = accountContactListsRepository.findByAccountAndContactList(guest, restrictedContact1);
		assertTrue(accountContactListsList.isPresent());
		logger.info(LinShareTestConstants.END_TEST);

	}

	@Test
	public void findByAccountAndContactList(){
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Optional<AccountContactLists> accountContactListsList = accountContactListsRepository.findByAccountAndContactList(guest,restrictedContact1);
		assertTrue(accountContactListsList.isPresent());
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void findByAccount_NoContactListDefinedForAccount() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain domain_guest = abstractDomainRepository.findById(DOMAIN_Guest);
		Guest guestWithoutContactLists = new Guest("Guest", "Smith", "guest2@linshare.org");
		guestWithoutContactLists.setDomain(domain_guest);
		guestWithoutContactLists.setCmisLocale("en");
        guestRepository.create(guestWithoutContactLists);
		guestWithoutContactLists.setContactLists(null);
		List<AccountContactLists> accountContactLists1 = accountContactListsRepository.findByAccount(guestWithoutContactLists);
		assertEquals(0, accountContactLists1.size());
		logger.info(LinShareTestConstants.END_TEST);
	}

}
