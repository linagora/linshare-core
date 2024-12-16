package org.linagora.linshare.business.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.GuestBusinessService;
import org.linagora.linshare.core.business.service.MailingListBusinessService;
import org.linagora.linshare.core.business.service.ModeratorBusinessService;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.Moderator;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.repository.MailingListRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.service.GuestServiceImplTest;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@ExtendWith(SpringExtension.class)
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
@Transactional
public class MailingListBusinessServiceImplTest {

	private static final Logger logger = LoggerFactory.getLogger(GuestServiceImplTest.class);
	@Autowired
	private GuestRepository guestRepository;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	private MailingListRepository mailingListRepository;

	@Autowired
	private AbstractDomainRepository domainRepository;

	@Autowired
	private GuestBusinessService guestBusinessService;

	@Autowired
	private ModeratorBusinessService moderatorBusinessService;

	@Autowired
	private MailingListBusinessService mailingListBusinessService;

	private AbstractDomain guestDomain;
	private AbstractDomain topDomain;
	private AbstractDomain subDomain;
	private User owner;
	private User owner2;
	private User owner3;
	private User owner4;
	private Guest guest;
	private ContactList restrictedContact1 , restrictedContact2,restrictedContact3,restrictedContact4,restrictedContact5,restrictedContact6,restrictedContact7;
	private Moderator moderator;
	private Moderator moderator1;

	@BeforeEach
	public void setUp(){
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		guestDomain = domainRepository.findById(LinShareTestConstants.GUEST_DOMAIN);
		topDomain = domainRepository.findById(LinShareTestConstants.TOP_DOMAIN);
		subDomain = domainRepository.findById(LoadingServiceTestDatas.sqlSubDomain);
		owner = new Internal("John", "Doe", "user1@linshare.org", null);
		owner.setDomain(topDomain);
		owner.setCmisLocale("en");
		owner = userRepository.create(owner);
		owner2 = new Internal("Jane", "Smith", "user2@linshare.org", null);
		owner2.setDomain(topDomain);
		owner2.setCanCreateGuest(true);
		owner2.setRole(Role.SIMPLE);
		owner2.setCmisLocale("en");
		owner2 = userRepository.create(owner2);
		owner3 = new Internal("Jane", "Smith", "user4@linshare.org", null);
		owner3.setDomain(subDomain);
		owner3.setCmisLocale("en");
		owner3 = userRepository.create(owner3);
		owner4 = new Internal("Jane", "Smith", "user5@linshare.org", null);
		owner4.setDomain(topDomain);
		owner4.setCmisLocale("en");
		owner4 = userRepository.create(owner4);
		restrictedContact1 = createContactList("identifier1", "yoyo", owner, topDomain);
		restrictedContact1.setPublic(false);
		restrictedContact2 = createContactList("identifier2", "yoyoyy", owner, topDomain);
		restrictedContact2.setPublic(false);
		restrictedContact3 = createContactList("identifier3", "contact3", owner2, topDomain);
		restrictedContact3.setPublic(true);
		restrictedContact4 = createContactList("identifier4", "contact4", owner2, topDomain);
		restrictedContact4.setPublic(false);
		restrictedContact5 = createContactList("identifier5", "contact5", owner3, subDomain);
		restrictedContact5.setPublic(true);
		restrictedContact6 = createContactList("identifier6", "contact6", owner3, subDomain);
		restrictedContact6.setPublic(false);
		restrictedContact7 = createContactList("identifier7", "contact7", owner4, topDomain);
		restrictedContact7.setPublic(false);
		logger.debug(LinShareTestConstants.END_SETUP);
	}
	private ContactList createContactList(String identifier, String description, User owner, AbstractDomain domain) {
		ContactList contactList = new ContactList();
		contactList.setIdentifier(identifier);
		contactList.setOwner(owner);
		contactList.setDomain(domain);
		contactList.setDescription(description);
		contactList.setContactListContacts(new HashSet<>());
		this.mailingListRepository.create(contactList);
		return contactList;
	}

	@Test
	public void findByAccountAndContactListUuids() {
		guest = new Guest("Guest", "Doe", "guest1@linshare.org");
		guest.setCmisLocale("en");
		guest.setDomain(guestDomain);
		guestRepository.create(guest);
		moderator = new Moderator(ModeratorRole.ADMIN, owner, guest);
		moderator1 = new Moderator(ModeratorRole.SIMPLE, owner2, guest);
		moderator = moderatorBusinessService.create(moderator);
		moderator1 = moderatorBusinessService.create(moderator1);
		guest.addModerator(moderator);
		guest.addModerator(moderator1);
		guestBusinessService.update(owner, guest, guest,null,null);
		List<String> contactUuids = new ArrayList<>();
		contactUuids.add(restrictedContact1.getUuid());
		contactUuids.add(restrictedContact2.getUuid());
		contactUuids.add(restrictedContact3.getUuid());
		contactUuids.add(restrictedContact4.getUuid());
		contactUuids.add(restrictedContact5.getUuid());
		contactUuids.add(restrictedContact6.getUuid());
		contactUuids.add(restrictedContact7.getUuid());
		List<ContactList> result = mailingListBusinessService.findByAccountAndContactListUuids(owner, guest, contactUuids);
		assertTrue(result.contains(restrictedContact1));
		assertTrue(result.contains(restrictedContact2));
		assertTrue(result.contains(restrictedContact3));
		assertTrue(result.contains(restrictedContact4));
		assertFalse(result.contains(restrictedContact5));
		assertFalse(result.contains(restrictedContact6));
		assertFalse(result.contains(restrictedContact7));
		assertEquals(result.size(),4);
		assertEquals(result.size(), new HashSet<>(result).size(), "The result contains duplicates.");

	}

	@Test
	public void findByAccountAndContactListUuidsWithEmptyList() {
		List<String> contactUuids = new ArrayList<>();
		List<ContactList> result = mailingListBusinessService.findByAccountAndContactListUuids(owner, guest, contactUuids);
		assertTrue(result.isEmpty(), "The result should be empty when no UUIDs are provided.");
	}

}
