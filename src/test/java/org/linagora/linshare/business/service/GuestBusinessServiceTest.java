/*
 * Copyright (C) 2007-2023 - LINAGORA This program is free software: you can redistribute it and/ uor modify it under
 * the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.linagora.linshare.core.domain.constants.LinShareTestConstants.GUEST_DOMAIN;
import static org.linagora.linshare.core.domain.constants.LinShareTestConstants.TOP_DOMAIN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.linagora.linshare.core.business.service.EntryBusinessService;
import org.linagora.linshare.core.business.service.PasswordService;
import org.linagora.linshare.core.business.service.ShareEntryBusinessService;
import org.linagora.linshare.core.business.service.ShareEntryGroupBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceNodeBusinessService;
import org.linagora.linshare.core.business.service.WorkGroupNodeBusinessService;
import org.linagora.linshare.core.business.service.impl.GuestBusinessServiceImpl;
import org.linagora.linshare.core.business.service.impl.MailingListBusinessServiceImpl;
import org.linagora.linshare.core.business.service.impl.UploadRequestGroupBusinessServiceImpl;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AccountContactLists;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.GuestDomain;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.Policy;
import org.linagora.linshare.core.domain.entities.Root;
import org.linagora.linshare.core.domain.entities.TopDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountContactListsRepository;
import org.linagora.linshare.core.repository.AllowedContactRepository;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.repository.MailingListRepository;
import org.linagora.linshare.core.repository.RecipientFavouriteRepository;
import org.linagora.linshare.core.repository.RootUserRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.FunctionalityService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test suite for the business service implementation about guest accounts (ie. {@link GuestBusinessServiceImpl}),
 * validating operations on guest accounts and their contact lists. Tests include adding, removing, and creating contact
 * lists for guests.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@Sql({ "/import-tests-fake-domains.sql" })
class GuestBusinessServiceTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(GuestBusinessServiceTest.class);
	private static final String CONTACT_LIST_1_ID = "cl1";
	private static final String CONTACT_LIST_2_ID = "cl2";
	private static final String CONTACT_LIST_3_ID = "cl3";

	private static final String CONTACT_1_MAIL = "contact1@test.com";
	private static final String CONTACT_1_FNAME = "Contact1";
	private static final String CONTACT_1_LNAME = "One";
	private static final String CONTACT_1_UID = "cid1";

	private static final String CONTACT_2_MAIL = "contact2@test.com";
	private static final String CONTACT_2_FNAME = "Contact2";
	private static final String CONTACT_2_LNAME = "Two";
	private static final String CONTACT_2_UID = "cid2";

	private static final String CONTACT_3_MAIL = "contact3@test.com";
	private static final String CONTACT_3_FNAME = "Contact3";
	private static final String CONTACT_3_LNAME = "Three";
	private static final String CONTACT_3_UID = "cid3";

	private static final List<String> INITIAL_EMPTY = List.of();
	private static final List<String> INITIAL_SINGLE_CONTACT_LIST = List.of(CONTACT_LIST_1_ID);
	private static final List<String> INITIAL_MULTIPLE_CONTACT_LISTS = List.of(CONTACT_LIST_1_ID, CONTACT_LIST_2_ID);
	private static final List<String> INITIAL_SINGLE_CONTACT = List.of(CONTACT_1_MAIL);
	private static final List<String> INITIAL_MULTIPLE_CONTACTS = List.of(CONTACT_1_MAIL, CONTACT_2_MAIL);

	private static final List<String> NEW_EMPTY = List.of();
	private static final List<String> NEW_SINGLE_CONTACT_LIST = List.of(CONTACT_LIST_1_ID);
	private static final List<String> NEW_MULTIPLE_CONTACT_LISTS = List.of(CONTACT_LIST_1_ID, CONTACT_LIST_2_ID);
	private static final List<String> NEW_DIFFERENT_CONTACT_LISTS = List.of(CONTACT_LIST_2_ID, CONTACT_LIST_3_ID);
	private static final List<String> NEW_SINGLE_CONTACT = List.of(CONTACT_1_MAIL);
	private static final List<String> NEW_MULTIPLE_CONTACTS = List.of(CONTACT_1_MAIL, CONTACT_2_MAIL);
	private static final List<String> NEW_EXTENDED_CONTACTS = List.of(CONTACT_1_MAIL, CONTACT_2_MAIL, CONTACT_3_MAIL);
	private static final List<String> NEW_DIFFERENT_CONTACTS = List.of(CONTACT_3_MAIL);

	private static final List<String> EXPECTED_EMPTY = List.of();
	private static final List<String> EXPECTED_SINGLE_CONTACT_LIST = List.of(CONTACT_LIST_1_ID);
	private static final List<String> EXPECTED_MULTIPLE_CONTACT_LISTS = List.of(CONTACT_LIST_1_ID, CONTACT_LIST_2_ID);
	private static final List<String> EXPECTED_DIFFERENT_CONTACT_LISTS = List.of(CONTACT_LIST_2_ID, CONTACT_LIST_3_ID);
	private static final List<String> EXPECTED_SINGLE_CONTACT = List.of(CONTACT_1_MAIL);
	private static final List<String> EXPECTED_MULTIPLE_CONTACTS = List.of(CONTACT_1_MAIL, CONTACT_2_MAIL);
	private static final List<String> EXPECTED_EXTENDED_CONTACTS = List.of(CONTACT_1_MAIL, CONTACT_2_MAIL, CONTACT_3_MAIL);
	private static final List<String> EXPECTED_DIFFERENT_CONTACTS = List.of(CONTACT_3_MAIL);
	private static final Map<String, User> CONTACT_MAP = Map.of(
			CONTACT_1_MAIL, new Internal(CONTACT_1_FNAME, CONTACT_1_LNAME, CONTACT_1_MAIL, CONTACT_1_UID),
			CONTACT_2_MAIL, new Internal(CONTACT_2_FNAME, CONTACT_2_LNAME, CONTACT_2_MAIL, CONTACT_2_UID),
			CONTACT_3_MAIL, new Internal(CONTACT_3_FNAME, CONTACT_3_LNAME, CONTACT_3_MAIL, CONTACT_3_UID)
	);

	@Mock
	private GuestRepository guestRepository;

	@Mock private UserRepository<User> userRepository;

	@Mock
	private MailingListRepository contactListRepository;

	@Mock
	private AbstractDomainRepository abstractDomainRepository;

	@Mock
	private RootUserRepository rootUserRepository;

	@Mock
	private AllowedContactRepository allowedContactRepository;

	@Mock
	private MailingListBusinessServiceImpl mailingListBusinessServiceImpl;

	@Mock
	private PasswordService passwordService;

	@Mock
	private RecipientFavouriteRepository recipientFavouriteRepository;

	@InjectMocks
	private GuestBusinessServiceImpl guestBusinessService;

	@Mock
	private FunctionalityService functionalityService;

	@Mock private UploadRequestGroupBusinessServiceImpl uploadRequestGroupBusinessService;
	@Mock private EntryBusinessService entryBusinessService;
	@Mock private ShareEntryGroupBusinessService shareEntryGroupBusiness;
	@Mock private ShareEntryBusinessService shareEntryBusinessService;
	@Mock private SharedSpaceNodeBusinessService sharedSpaceNodeBusinessService;
	@Mock private WorkGroupNodeBusinessService workGroupNodeBusinessService;
	@Mock private SharedSpaceMemberBusinessService sharedSpaceMemberBusinessService;
	@Mock private AccountContactListsRepository accountContactListRepository;
	@Mock
	private FunctionalityReadOnlyService functionalityReadOnlyService;
	private User user;
	private Guest guest;
	private User root;
	private AbstractDomain topDomain;
	private AbstractDomain domainGuest;
	private AccountContactLists accountContactLists, accountContactLists2;
	private ContactList contactList1, contactList2;

	@BeforeEach
	void setUp() {
		guestBusinessService = new GuestBusinessServiceImpl(
				guestRepository,
				userRepository,
				allowedContactRepository,
				recipientFavouriteRepository,
				passwordService,
				mailingListBusinessServiceImpl,
				uploadRequestGroupBusinessService,
				entryBusinessService,
				shareEntryGroupBusiness,
				shareEntryBusinessService,
				sharedSpaceNodeBusinessService,
				workGroupNodeBusinessService,
				sharedSpaceMemberBusinessService,
				accountContactListRepository,
				functionalityReadOnlyService
		);
		this.topDomain = new TopDomain(TOP_DOMAIN);
		this.topDomain.setUuid(TOP_DOMAIN);
		when(this.abstractDomainRepository.findById(TOP_DOMAIN)).thenReturn(topDomain);

		this.domainGuest = new GuestDomain(GUEST_DOMAIN);
		this.domainGuest.setUuid(GUEST_DOMAIN);
		when(this.abstractDomainRepository.findById(GUEST_DOMAIN)).thenReturn(domainGuest);

		this.user = new Internal("first name", "last name", "mail", "uid");
		this.user.setDomain(this.topDomain);

		this.guest = new Guest("Guest", "Doe", "guest1@linshare.org");
		this.guest.setDomain(this.domainGuest);
		this.guest.setContactLists(new HashSet<>());

		when(rootUserRepository.findByLsUuid("root@localhost.localdomain@test")).thenReturn((Root) root);
		this.contactList1 = createContactList("identifier1", "yoyo", this.user, this.topDomain);
		this.contactList2 = createContactList("identifier2", "yoyoddd", this.user, this.topDomain);
		this.accountContactLists = new AccountContactLists(this.guest, this.contactList1);
		this.accountContactLists2 = new AccountContactLists(this.guest, this.contactList2);

		when(this.accountContactListRepository.findByAccount(this.guest)).thenReturn(
				List.of(this.accountContactLists, this.accountContactLists2));

		when(this.guestRepository.update(any(Guest.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Functionality assignContactListFunctionality = createFunctionality(true);
		Functionality hideMembersFunctionality = createFunctionality(true);
		Functionality restrictedGuestFunctionality = createFunctionality(true);

		when(functionalityReadOnlyService.getCanAssignContactListToGuest(any(AbstractDomain.class)))
				.thenReturn(assignContactListFunctionality);

		when(functionalityReadOnlyService.getCanHideMembersToGuest(any(AbstractDomain.class)))
				.thenReturn(hideMembersFunctionality);

		when(functionalityReadOnlyService.getRestrictedGuestFunctionality(any(AbstractDomain.class)))
				.thenReturn(restrictedGuestFunctionality);

		when(mailingListBusinessServiceImpl.hasRightToHideMembersToGuest(any(AbstractDomain.class)))
				.thenReturn(true);

		when(mailingListBusinessServiceImpl.hasDelegationPolicy(any(AbstractDomain.class)))
				.thenReturn(true);

		when(mailingListBusinessServiceImpl.determineCanViewPermissionForCreate(
				any(ContactList.class),
				any(Map.class)
		)).thenReturn(true);

		when(passwordService.generatePassword()).thenReturn("pwd");
		when(passwordService.encode(anyString())).thenReturn("hashed");
		when(guestRepository.create(any(Guest.class))).thenReturn(guest);

	}

	/**
	 * Tests the addition of a contact list to a guest's restricted contact lists. Verifies that the contact list is
	 * successfully added and the guest's restricted contact lists are updated.
	 */
	@Test
	void addContactListsToExistingSet() {

		when(this.contactListRepository.findByUuid(this.contactList1.getUuid()))
				.thenReturn(this.contactList1);

		when(this.contactListRepository.findByUuid(this.contactList2.getUuid()))
				.thenReturn(this.contactList2);

		when(this.guestRepository.findByLsUuid(this.guest.getLsUuid()))
				.thenReturn(this.guest);
		when(this.guestRepository.update(any(Guest.class))).thenAnswer(invocation -> invocation.getArgument(0));
		doAnswer(invocation -> {
			final Guest guestTest = invocation.getArgument(0);
			final List<ContactList> contactLists = invocation.getArgument(1);
			guestTest.getRestrictedContactLists().addAll(contactLists.stream()
					.map(contactList -> new AccountContactLists(guestTest, contactList)).collect(Collectors.toSet()));
			return null;
		}).when(this.mailingListBusinessServiceImpl).updateAccountContactLists(any(), any(), any());
		Map<String, Boolean> permissions = new HashMap<>();
		permissions.put(this.contactList1.getUuid(), true);
		permissions.put(this.contactList2.getUuid(), false);
		this.guestBusinessService.update(this.user, this.guest, this.guest, null, Arrays.asList(this.contactList2), permissions );
		final Set<AccountContactLists> updatedContactLists = this.guest.getRestrictedContactLists();
		final Set<ContactList> contactLists = updatedContactLists.stream().map(AccountContactLists::getContactList)
				.collect(Collectors.toSet());
		assertTrue(contactLists.contains(this.contactList2),
				"The contact list should be added to the guest's restricted contact lists");
		verify(this.guestRepository).update(this.guest);
	}

	/**
	 * Tests the addition of a non-existent contact list to a guest's restricted contact lists. Verifies that the
	 * non-existent contact list is not added and the guest's restricted contact lists remain unchanged.
	 */
	@Test
	void addNonExistentContactList() {
		final ContactList nonExistentContactList = new ContactList();
		nonExistentContactList.setUuid(UUID.randomUUID().toString());
		nonExistentContactList.setIdentifier("nonexistent");
		doReturn(this.guest).when(this.guestRepository).findByLsUuid(this.guest.getLsUuid());
		doNothing().when(this.mailingListBusinessServiceImpl).updateAccountContactLists(any(), any(), any());
		this.guestBusinessService.update(this.user, this.guest, this.guest, null, Arrays.asList(nonExistentContactList), null);
		final Set<AccountContactLists> updatedContactLists = this.guest.getRestrictedContactLists();
		final Set<ContactList> contactLists = updatedContactLists.stream().map(AccountContactLists::getContactList)
				.collect(Collectors.toSet());
		assertFalse(contactLists.contains(nonExistentContactList));
		verify(this.guestRepository).update(this.guest);
	}

	/**
	 * Tests the removal of a contact list from a guest's restricted contact lists. Verifies that the contact list is
	 * successfully removed and the guest's restricted contact lists are updated.
	 */
	@Test
	void removeContactListsFromExistingSet() {
		doReturn(this.guest).when(this.guestRepository).findByLsUuid(this.guest.getLsUuid());
		when(this.accountContactListRepository.findByAccount(this.guest)).thenReturn(
				List.of(this.accountContactLists));
		when(this.accountContactListRepository.findByAccount(this.guest)).thenReturn(
				List.of(this.accountContactLists));

		doAnswer(invocation -> {
			final AccountContactLists accountContactList = invocation.getArgument(0);
			this.guest.getRestrictedContactLists().remove(accountContactList);
			return null;
		}).when(this.accountContactListRepository).delete(any(AccountContactLists.class));

		this.guestBusinessService.update(this.user, this.guest, this.guest, null, Collections.emptyList(), null);

		final Set<AccountContactLists> updatedContactLists = this.guest.getRestrictedContactLists();
		final Set<ContactList> contactLists = updatedContactLists.stream().map(AccountContactLists::getContactList)
				.collect(Collectors.toSet());

		LOGGER.info("Updated restricted contact lists: {}", contactLists);
		assertFalse(contactLists.contains(contactList1),
				"The ContactList should be removed from the restricted list of the Guest");
		final Set<ContactList> existingContactLists = accountContactListRepository.findByAccount(this.guest).stream()
				.map(AccountContactLists::getContactList).collect(Collectors.toSet());
		LOGGER.info("Existing contact lists: {}", existingContactLists);
	}

	/**
	 * Tests the removal of a non-existent contact list from a guest's restricted contact lists. Verifies that the
	 * guest's restricted contact lists remain unchanged.
	 */
	@Test
	void removeNonExistentContactList() {
		this.guest.getRestrictedContactLists().add(new AccountContactLists(this.guest, contactList2));
		doReturn(this.guest).when(this.guestRepository).findByLsUuid(this.guest.getLsUuid());
		doNothing().when(this.mailingListBusinessServiceImpl).updateAccountContactLists(any(), any(), any());

		final ContactList nonExistentContactList = new ContactList();
		nonExistentContactList.setIdentifier("nonexistent");
		nonExistentContactList.setUuid(UUID.randomUUID().toString());
		nonExistentContactList.setOwner(this.user);
		nonExistentContactList.setDomain(this.topDomain);
		Map<String, Boolean> permissions = new HashMap<>();
		permissions.put(nonExistentContactList.getUuid(), true);
		this.guestBusinessService.update(this.user, this.guest, this.guest, null, Arrays.asList(nonExistentContactList), permissions);
		final Set<AccountContactLists> updatedContactLists = this.guest.getRestrictedContactLists();
		final Set<ContactList> contactLists = updatedContactLists.stream().map(AccountContactLists::getContactList)
				.collect(Collectors.toSet());
		assertTrue(contactLists.contains(this.contactList2));
		verify(this.guestRepository).update(this.guest);
	}

	/**
	 * Tests the creation of a guest with associated contact lists. Verifies that the guest is successfully created and
	 * the contact lists are correctly associated with the guest.
	 */
	@Test
	void createGuestWithContactLists() {
		this.guest.setMail("guest@example.com");
		this.guest.setRestricted(false);
		when(this.guestRepository.create(any(Guest.class))).thenReturn(this.guest);
		doNothing().when(this.mailingListBusinessServiceImpl).updateAccountContactLists(any(), any(), any());

		when(this.passwordService.generatePassword()).thenReturn("somePassword");
		final Guest createdGuest = this.guestBusinessService.create(this.user, this.guest, this.domainGuest, null,
				Arrays.asList(this.contactList1, this.contactList2), null);
		assertNotNull(createdGuest);
		assertEquals(2, createdGuest.getRestrictedContactLists().size());
		verify(this.guestRepository).create(this.guest);
	}

	/**
	 * Tests the creation of a restricted guest without allowed contacts.
	 * Verifies that a {@link BusinessException} is thrown with the appropriate error code and message
	 * when attempting to create a restricted guest without providing a list of allowed contacts.
	 */
	@Test
	void createRestrictedGuestWithoutAllowedContacts_ThrowsBusinessException() {
		this.guest.setRestricted(true);
		when(this.guestRepository.create(any(Guest.class))).thenReturn(this.guest);
		when(this.passwordService.generatePassword()).thenReturn("password");
		when(this.passwordService.encode(anyString())).thenReturn("hashed");

		final BusinessException  exception = assertThrows(BusinessException.class, () -> {
			this.guestBusinessService.create(this.user, this.guest, this.domainGuest, null, null, null);
		});
		assertEquals(BusinessErrorCode.GUEST_INVALID_INPUT, exception.getErrorCode());
		assertTrue(exception.getMessage().contains("without a list of contacts"));
	}

	/**
	 * Tests that the guest's email is converted to lowercase during creation.
	 * Verifies that the email address is properly normalized to lowercase
	 * regardless of the input format.
	 */
	@Test
	void createGuest_EmailIsLowercased() {
		this.guest.setMail("Guest@Example.com");
		when(this.guestRepository.create(any(Guest.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(this.passwordService.generatePassword()).thenReturn("pwd");
		when(this.passwordService.encode("pwd")).thenReturn("hashed");

		final Guest created = this.guestBusinessService.create(this.user, this.guest, this.domainGuest, null, new ArrayList<>(), null);
		assertEquals("guest@example.com", created.getMail());
	}

	/**
	 * Tests the creation of a restricted guest with allowed contacts.
	 * Verifies that the allowed contacts are correctly associated with the guest
	 * and that the {@link AllowedContactRepository} is invoked to create the necessary records.
	 */
	@Test
	void createRestrictedGuestWithAllowedContacts_CreatesAllowedContacts() {
		this.guest.setRestricted(true);
		final User contact1 = new Internal("c1", "c1", "c1@test.com", "cid1");
		final User contact2 = new Internal("c2", "c2", "c2@test.com", "cid2");
		final List<User> allowedContacts = List.of(contact1, contact2);

		when(this.guestRepository.create(any(Guest.class))).thenReturn(this.guest);
		when(this.passwordService.generatePassword()).thenReturn("pwd");
		when(this.passwordService.encode(anyString())).thenReturn("hashed");

		this.guestBusinessService.create(this.user, this.guest, this.domainGuest, allowedContacts, new ArrayList<>(), null);

		verify(this.allowedContactRepository, times(2)).create(any(AllowedContact.class));
		assertEquals(2, this.guest.getRestrictedContacts().size());
	}

	/**
	 * Tests the creation of a guest with associated contact lists.
	 * Verifies that the contact lists are correctly associated with the guest
	 * and that the {@link AccountContactListsRepository} is invoked to create the necessary records.
	 */
	@Test
	void createGuestWithContactLists_CreatesAccountContactLists() {
		final List<ContactList> contactLists = List.of(this.contactList1, this.contactList2);
		when(this.guestRepository.create(any(Guest.class))).thenReturn(this.guest);
		when(this.passwordService.generatePassword()).thenReturn("pwd");
		when(this.passwordService.encode(anyString())).thenReturn("hashed");

		final Guest created = this.guestBusinessService.create(this.user, this.guest, this.domainGuest, null, contactLists, null);

		verify(this.accountContactListRepository, times(2)).create(any(AccountContactLists.class));
		assertEquals(2, created.getRestrictedContactLists().size());
	}

	/**
	 * Tests the password generation and hashing process during guest creation.
	 * Verifies that a password is generated, hashed, and correctly assigned to the guest.
	 */
	@Test
	void createGuest_PasswordGeneratedAndHashed() {
		final String rawPassword = "rawPassword123";
		final String hashedPassword = "hashedPassword123";
		when(this.passwordService.generatePassword()).thenReturn(rawPassword);
		when(this.passwordService.encode(rawPassword)).thenReturn(hashedPassword);
		when(this.guestRepository.create(any(Guest.class))).thenAnswer(invocation -> invocation.getArgument(0));

		final Guest created = this.guestBusinessService.create(this.user, this.guest, this.domainGuest, null, new ArrayList<>(), null);

		assertEquals(hashedPassword, created.getPassword());
		verify(this.passwordService).generatePassword();
		verify(this.passwordService).encode(rawPassword);
	}
	/**
	 * Tests the scenario where the guest is not found in the repository. Given: - A guest email that does not exist in
	 * the repository. When: - The find method is called with the non-existing email. Then: - The result should be null.
	 * - No interactions should occur with allowedContactRepository and accountContactListsRepository.
	 */
	@Test
	void find_GuestNotFound_ReturnsNull() {
		final String mail = "test@example.com";
		when(this.guestRepository.findByDomainAndMail(this.domainGuest, mail)).thenReturn(null);
		final Guest result = this.guestBusinessService.find(this.domainGuest, mail);
		assertNull(result);
		verifyNoInteractions(this.allowedContactRepository, this.accountContactListRepository);
	}

	/**
	 * Tests the scenario where a non-restricted guest without contact lists is found. Given: - A guest with restricted
	 * set to false and no contact lists. When: - The find method is called with the guest's email. Then: - The result
	 * should be a non-null guest object. - The guest should not be restricted. - The restricted contacts list should be
	 * empty. - No interactions should occur with allowedContactRepository and accountContactListsRepository.
	 */
	@Test
	void find_GuestNotRestrictedNoContactLists_ReturnsBasicGuest() {
		final Guest basicGuest = new Guest("John", "Doe", "john@linshare.org");
		basicGuest.setDomain(this.domainGuest);
		basicGuest.setRestricted(false);
		basicGuest.setContactLists(null);

		when(this.guestRepository.findByDomainAndMail(this.domainGuest, "john@linshare.org")).thenReturn(basicGuest);

		final Guest result = this.guestBusinessService.find(this.domainGuest, "john@linshare.org");

		assertNotNull(result);
		assertFalse(result.isRestricted());
		assertTrue(result.getRestrictedContacts().isEmpty());
		verifyNoInteractions(this.allowedContactRepository, this.accountContactListRepository);
	}

	/**
	 * Tests the scenario where a restricted guest with contacts is found. Given: - A restricted guest with a contact. -
	 * The allowedContactRepository is configured to return an empty list initially. When: - The find method is called
	 * with the guest's email. Then: - The result should be a non-null guest object. - The guest should contain the
	 * expected restricted contact. - The contact's email should be as expected.
	 */
	@Test
	void find_RestrictedGuestWithContacts_AddsContacts() {
		when(this.recipientFavouriteRepository.getElementsOrderByWeight(any())).thenReturn(Collections.emptyList());
		doNothing().when(this.allowedContactRepository).purge(any(Guest.class));

		final Guest basicGuest = new Guest("Restricted", "Guest", "restricted@linshare.org");
		basicGuest.setDomain(this.domainGuest);
		basicGuest.setRestricted(true);
		final Internal contactUser = new Internal("Contact1", "Doe", "c1@linshare.org", "cid1");

		final List<AllowedContact> allowedContacts = new ArrayList<>();
		when(this.allowedContactRepository.findByOwner(basicGuest)).thenReturn(allowedContacts);

		doAnswer(invocation -> {
			AllowedContact ac = invocation.getArgument(0);
			allowedContacts.add(ac);
			return ac;
		}).when(this.allowedContactRepository).create(any(AllowedContact.class));

		this.guestBusinessService.update(null, basicGuest, basicGuest, List.of(contactUser), new ArrayList<>(), null);

		when(this.guestRepository.findByDomainAndMail(this.domainGuest, "restricted@linshare.org")).thenReturn(basicGuest);

		final Guest result = this.guestBusinessService.find(domainGuest, "restricted@linshare.org");

		final Set<AllowedContact> restrictedContacts = result.getRestrictedContacts();
		final List<User> contactUsers = restrictedContacts.stream().map(AllowedContact::getContact)
				.collect(Collectors.toList());

		assertFalse(contactUsers.isEmpty(), "No contact found");
		assertTrue(contactUsers.stream().anyMatch(u -> u.getMail().equals("c1@linshare.org")),
				"The expected contact is not present");
	}

	/**
	 * Tests the scenario where a guest with contact lists is found. Given: - A guest with associated contact lists.
	 * When: - The find method is called with the guest's email. Then: - The result should be a non-null guest object. -
	 * The restricted contact lists should be correctly added to the guest. - The contact lists should contain the
	 * expected lists.
	 */
	@Test
	void find_GuestWithContactLists_AddsContactLists() {
		final String mail = this.guest.getMail();
		when(this.guestRepository.findByDomainAndMail(this.domainGuest, mail)).thenReturn(this.guest);
		final Guest result = this.guestBusinessService.find(this.domainGuest, mail);
		assertNotNull(result);
		final Set<ContactList> contactLists = result.getRestrictedContactLists().stream()
				.map(AccountContactLists::getContactList).collect(Collectors.toSet());
		assertEquals(contactLists.size(), 2);
		assertNotNull(this.guest.getRestrictedContacts());
		assertTrue(contactLists.contains(this.contactList1));
		assertTrue(contactLists.contains(this.contactList2));
	}

	/**
	 * Tests the scenario where the guest is not found by UUID.
	 * Verifies that the method returns null and no interactions occur with the contact repositories.
	 */
	@Test
	void findByLsUuid_GuestNotFound_ReturnsNull() {
		final String uuid = "non-existent-uuid";
		when(this.guestRepository.findByLsUuid(uuid)).thenReturn(null);
		final Guest result = this.guestBusinessService.findByLsUuid(uuid);
		assertNull(result);
		verify(this.guestRepository).findByLsUuid(uuid);
		verifyNoInteractions(this.allowedContactRepository, this.accountContactListRepository);
	}

	/**
	 * Tests finding a non-restricted guest by UUID.
	 * Verifies that the guest is returned as-is without restricted contacts or contact lists.
	 */
	@Test
	void findByLsUuid_GuestNotRestricted_ReturnsBasicGuest() {
		final String uuid = "guest-uuid";
		final Guest guestUuid = new Guest("John", "Doe", "john@linshare.org");
		guestUuid.setRestricted(false);

		guestUuid.setContactLists(null);
		when(this.guestRepository.findByLsUuid(uuid)).thenReturn(guestUuid);
		when(this.accountContactListRepository.findByAccount(guestUuid)).thenReturn(Collections.emptyList());
		final Guest result = this.guestBusinessService.findByLsUuid(uuid);

		assertNotNull(result);
		assertFalse(result.isRestricted());
		assertTrue(result.getRestrictedContacts().isEmpty());

		verify(this.guestRepository).findByLsUuid(uuid);
		verifyNoInteractions(this.allowedContactRepository);
	}

	/**
	 * Tests finding a restricted guest by UUID with associated contacts.
	 * Verifies restricted contacts and repository interactions.
	 */
	@Test
	void findByLsUuid_RestrictedGuestWithContacts_AddsContacts() {
		final String uuid = "restricted-guest";
		final Guest basicGuest = new Guest("Restricted", "Guest", "restricted@linshare.org");
		basicGuest.setRestricted(true);
		basicGuest.setContactLists(new HashSet<>());

		final Internal contactUser = new Internal("Contact", "Doe", "contact@linshare.org", "cid");
		final AllowedContact allowedContact = new AllowedContact(basicGuest, contactUser);

		when(this.guestRepository.findByLsUuid(uuid)).thenReturn(basicGuest);
		when(this.allowedContactRepository.findByOwner(basicGuest)).thenReturn(List.of(allowedContact));

		final Guest result = this.guestBusinessService.findByLsUuid(uuid);

		assertNotNull(result);
		assertEquals(1, result.getRestrictedContacts().size());
		assertTrue(result.getRestrictedContacts().contains(allowedContact));
		verify(this.allowedContactRepository).findByOwner(basicGuest);
		verify(this.accountContactListRepository).findByAccount(basicGuest);
	}

	/**
	 * Tests finding a guest by UUID with associated contact lists.
	 * Verifies contact lists and repository interactions.
	 */
	@Test
	void findByLsUuid_GuestWithContactLists_AddsLists() {
		final String uuid = "guest-with-lists";
		final Guest basicGuest= new Guest("ListGuest", "Doe", "list@linshare.org");
		basicGuest.setContactLists(new HashSet<>());

		final ContactList contactList = createContactList("cl1", "Test List", basicGuest, this.domainGuest);
		final AccountContactLists acl = new AccountContactLists(basicGuest, contactList);

		when(this.guestRepository.findByLsUuid(uuid)).thenReturn(basicGuest);
		when(this.accountContactListRepository.findByAccount(basicGuest)).thenReturn(List.of(acl));

		final Guest result = this.guestBusinessService.findByLsUuid(uuid);

		final Set<ContactList> contactLists = result.getRestrictedContactLists().stream()
				.map(AccountContactLists::getContactList)
				.collect(Collectors.toSet());

		assertEquals(1, contactLists.size());
		assertTrue(contactLists.contains(contactList));
		verify(this.accountContactListRepository).findByAccount(basicGuest);
		verifyNoInteractions(this.allowedContactRepository);
	}

	/**
	 * Tests finding a fully configured guest by UUID.
	 * Verifies restricted contacts, contact lists, and repository interactions.
	 */
	@Test
	void findByLsUuid_FullyConfiguredGuest_AddsBothContactsAndLists()  {
		final String uuid = "full-guest";
		final Guest basicGuest = new Guest("Full", "Guest", "full@linshare.org");
		basicGuest.setRestricted(true);
		basicGuest.setLsUuid(UUID.randomUUID().toString());
		basicGuest.setDomain(domainGuest);
		basicGuest.setContactLists(new HashSet<>());

		final Internal contactUser = new Internal("Contact", "Doe", "contact@linshare.org", "cid");
		final AllowedContact allowedContact = new AllowedContact(basicGuest, contactUser);

		final ContactList contactList = createContactList("cl1", "Test List", this.user, this.domainGuest);
		final AccountContactLists acl = new AccountContactLists(basicGuest, contactList);

		when(this.guestRepository.findByLsUuid(uuid)).thenReturn(basicGuest);
		when(this.allowedContactRepository.findByOwner(basicGuest)).thenReturn(List.of(allowedContact));
		when(this.accountContactListRepository.findByAccount(basicGuest)).thenReturn(List.of(acl));

		final Guest result = guestBusinessService.findByLsUuid(uuid);

		assertEquals(1, result.getRestrictedContacts().size());
		assertTrue(result.getRestrictedContacts().contains(allowedContact));

		final Set<ContactList> contactLists = result.getRestrictedContactLists().stream()
				.map(AccountContactLists::getContactList)
				.collect(Collectors.toSet());

		assertEquals(1, contactLists.size());
		assertTrue(contactLists.contains(contactList));

		verify(this.allowedContactRepository).findByOwner(basicGuest);
		verify(this.accountContactListRepository).findByAccount(basicGuest);
	}

	@Test
	void createGuest_WithContactListViewPermissions_AppliesPermissions() {
		when(this.guestRepository.create(any(Guest.class))).thenReturn(guest);

		final Map<String, Boolean> permissions = new HashMap<>();
		permissions.put(this.contactList1.getUuid(), true);
		permissions.put(this.contactList2.getUuid(), false);

		this.guestBusinessService.create(this.user, this.guest, this.domainGuest, null,
				List.of(this.contactList1, this.contactList2), permissions);

		verify(this.mailingListBusinessServiceImpl, times(2))
				.determineCanViewPermissionForCreate(any(ContactList.class), any(Map.class));
	}

	@Test
	void createRestrictedGuest_WhenFeatureDisabled_ThrowsException() {
		final Functionality restrictedFunctionality = createFunctionality(false);
		when(functionalityReadOnlyService.getRestrictedGuestFunctionality(any(AbstractDomain.class)))
				.thenReturn(restrictedFunctionality);

		guest.setRestricted(true);
		final List<User> allowedContacts = List.of(new Internal("c1", "c1", "c1@test.com", "cid1"));

		final BusinessException exception = assertThrows(BusinessException.class, () -> {
			this.guestBusinessService.create(this.user, this.guest, this.domainGuest, allowedContacts, null, null);
		});

		assertEquals(BusinessErrorCode.FUNCTIONALITY_GUEST_CONTACTS_DISABLED, exception.getErrorCode());
	}

	@Test
	void addContactLists_WhenFeatureDisabled_ThrowsException() {
		final Functionality contactListFunctionality = createFunctionality(false);
		when(this.functionalityReadOnlyService.getCanAssignContactListToGuest(any(AbstractDomain.class)))
				.thenReturn(contactListFunctionality);

		final BusinessException exception = assertThrows(BusinessException.class, () -> {
			this.guestBusinessService.create(user, guest, domainGuest, null, List.of(contactList1), null);
		});

		assertEquals(BusinessErrorCode.FUNCTIONALITY_GUEST_CONTACT_LISTS_DISABLED, exception.getErrorCode());
	}

	/**
	 * Tests that creating a guest with allowed contacts when the restricted guest feature is disabled
	 * throws a BusinessException with the appropriate error code.
	 */
	@Test
	void createGuest_WithAllowedContacts_WhenRestrictedFeatureDisabled_ThrowsException() {
		final Functionality restrictedFunctionality = createFunctionality(false);
		when(functionalityReadOnlyService.getRestrictedGuestFunctionality(any(AbstractDomain.class)))
				.thenReturn(restrictedFunctionality);

		final List<User> allowedContacts = List.of(new Internal("c1", "c1", "c1@test.com", "cid1"));
		final BusinessException exception = assertThrows(BusinessException.class, () -> {
			this.guestBusinessService.create(this.user, this.guest, this.domainGuest, allowedContacts, null, null);
		});

		assertEquals(BusinessErrorCode.FUNCTIONALITY_GUEST_CONTACTS_DISABLED, exception.getErrorCode());
		assertTrue(exception.getMessage().contains("GUESTS__RESTRICTED feature is disabled"));
	}

	/**
	 * Tests that creating a guest with contact lists when the contact list feature is disabled
	 * throws a BusinessException with the appropriate error code.
	 */
	@Test
	void createGuest_WithContactLists_WhenContactListFeatureDisabled_ThrowsException() {
		final Functionality contactListFunctionality = createFunctionality(false);
		when(functionalityReadOnlyService.getCanAssignContactListToGuest(any(AbstractDomain.class)))
				.thenReturn(contactListFunctionality);

		final List<ContactList> contactLists = List.of(this.contactList1);
		final BusinessException exception = assertThrows(BusinessException.class, () -> {
			this.guestBusinessService.create(this.user, this.guest, this.domainGuest, null, contactLists, null);
		});

		assertEquals(BusinessErrorCode.FUNCTIONALITY_GUEST_CONTACT_LISTS_DISABLED, exception.getErrorCode());
		assertTrue(exception.getMessage().contains("GUESTS__CONTACT_LISTS feature is disabled"));
	}

	/**
	 * Tests that creating a guest without any contacts or contact lists when features are disabled
	 * should succeed (no exception thrown).
	 */
	@Test
	void createGuest_WithoutContactsOrLists_WhenFeaturesDisabled_Succeeds() {
		final Functionality restrictedFunctionality = createFunctionality(false);
		final Functionality contactListFunctionality = createFunctionality(false);

		when(functionalityReadOnlyService.getRestrictedGuestFunctionality(any(AbstractDomain.class)))
				.thenReturn(restrictedFunctionality);
		when(functionalityReadOnlyService.getCanAssignContactListToGuest(any(AbstractDomain.class)))
				.thenReturn(contactListFunctionality);

		when(this.guestRepository.create(any(Guest.class))).thenReturn(this.guest);
		assertDoesNotThrow(() -> {
			this.guestBusinessService.create(this.user, this.guest, this.domainGuest, null, null, null);
		});
	}

	/**
	 * Parametrized tests for updating guests with existing contacts
	 */
	@ParameterizedTest
	@MethodSource("provideContactUpdateScenarios")
	void update_contacts(
			final @Nonnull String testName,
			final @Nonnull List<String> initialContactMails,
			final @Nonnull List<String> newContactMails,
			boolean restrictedFunctionalityEnabled,
			final @Nonnull List<String> expectedContactMails,
			final @Nullable BusinessErrorCode expectedErrorCode) {

		final Functionality restrictedFunctionality = restrictedFunctionalityEnabled
				? this.createFunctionality(true)
				: this.createFunctionality(false);

		when(this.functionalityReadOnlyService.getRestrictedGuestFunctionality(any(AbstractDomain.class))).thenReturn(
				restrictedFunctionality);

		final Guest existingGuest = new Guest("Existing", "Guest", "existing@linshare.org");
		existingGuest.setDomain(this.domainGuest);
		boolean hasInitialContacts = !initialContactMails.isEmpty();
		existingGuest.setRestricted(hasInitialContacts);

		final Map<String, User> contactMap = CONTACT_MAP;
		final Set<AllowedContact> initialAllowedContacts = new HashSet<>();
		for (final String mail : initialContactMails) {
			final User contact = contactMap.get(mail);
			if (contact != null) {
				final AllowedContact allowedContact = new AllowedContact(existingGuest, contact);
				initialAllowedContacts.add(allowedContact);
			}
		}
		if (!initialAllowedContacts.isEmpty()) {
			existingGuest.addContacts(initialAllowedContacts);
		}

		final List<User> newContactsToAdd = new ArrayList<>();
		for (final String mail : newContactMails) {
			final User contact = contactMap.get(mail);
			if (contact != null) {
				newContactsToAdd.add(contact);
			}
		}

		this.setupGuestUpdateMocks(existingGuest);

		if (expectedErrorCode != null) {
			final List<User> contactsToPass = newContactsToAdd.isEmpty() &&
					!initialContactMails.isEmpty() ? Collections.emptyList() :
					(newContactsToAdd.isEmpty() ? null : newContactsToAdd);
			final BusinessException exception = assertThrows(BusinessException.class, () -> {
				this.guestBusinessService.update(this.user, existingGuest, existingGuest,
						contactsToPass,
						null,
						null);
			});
			assertEquals(expectedErrorCode, exception.getErrorCode(),
					String.format("Error code mismatch in test '%s'", testName));
		}
		else {
			final List<User> contactsToPass = newContactsToAdd.isEmpty() && !initialContactMails.isEmpty()
					? Collections.emptyList()
					: (newContactsToAdd.isEmpty() ? null : newContactsToAdd);
			this.guestBusinessService.update(this.user, existingGuest, existingGuest, contactsToPass, null, null);
			final Set<AllowedContact> actualContacts = existingGuest.getRestrictedContacts();
			assertThat(actualContacts).extracting(ac -> ac.getContact().getMail())
					.containsExactlyInAnyOrderElementsOf(expectedContactMails);
			verify(this.guestRepository).update(existingGuest);
			if (contactsToPass != null && !contactsToPass.isEmpty()) {
				verify(this.allowedContactRepository).purge(existingGuest);
				verify(this.allowedContactRepository, times(contactsToPass.size())).create(any(AllowedContact.class));
			} else if (contactsToPass != null && contactsToPass.isEmpty() && !initialContactMails.isEmpty()) {
				verify(this.allowedContactRepository).purge(existingGuest);
			}
		}
	}

	private static Stream<Arguments> provideContactUpdateScenarios() {
		return Stream.of(
				Arguments.of("Add contacts to guest without initial contacts",
						INITIAL_EMPTY, NEW_MULTIPLE_CONTACTS, true, EXPECTED_MULTIPLE_CONTACTS, null),

				Arguments.of("Add contacts to guest with existing contacts",
						INITIAL_SINGLE_CONTACT, NEW_EXTENDED_CONTACTS, true, EXPECTED_EXTENDED_CONTACTS, null),

				Arguments.of("Add contacts when restricted functionality disabled - feature disabled",
						INITIAL_EMPTY, NEW_SINGLE_CONTACT, false, EXPECTED_EMPTY, BusinessErrorCode.FUNCTIONALITY_GUEST_CONTACTS_DISABLED),

				Arguments.of("Remove some contacts",
						INITIAL_MULTIPLE_CONTACTS, NEW_SINGLE_CONTACT, true, EXPECTED_SINGLE_CONTACT, null),

				Arguments.of("Remove all contacts from restricted guest",
						INITIAL_MULTIPLE_CONTACTS, NEW_EMPTY, true, EXPECTED_EMPTY, BusinessErrorCode.GUEST_INVALID_INPUT),

				Arguments.of("Remove all contacts from non-restricted guest",
						INITIAL_EMPTY, NEW_EMPTY, true, EXPECTED_EMPTY, null),

				Arguments.of("Replace contacts when restricted functionality disabled - feature disabled",
						INITIAL_MULTIPLE_CONTACTS, NEW_DIFFERENT_CONTACTS, false, EXPECTED_MULTIPLE_CONTACTS, BusinessErrorCode.FUNCTIONALITY_GUEST_CONTACTS_DISABLED),

				Arguments.of("Replace all existing contacts with new ones",
						INITIAL_MULTIPLE_CONTACTS, NEW_DIFFERENT_CONTACTS, true, EXPECTED_DIFFERENT_CONTACTS, null),

				Arguments.of("Add contacts when restricted functionality disabled",
						INITIAL_EMPTY, NEW_SINGLE_CONTACT, false, EXPECTED_EMPTY, BusinessErrorCode.FUNCTIONALITY_GUEST_CONTACTS_DISABLED),

				Arguments.of("Update with null contacts when functionality disabled - should succeed",
						INITIAL_EMPTY, INITIAL_EMPTY, false, EXPECTED_EMPTY, null),

				Arguments.of("Update with empty contacts when functionality disabled - should succeed",
						INITIAL_EMPTY, NEW_EMPTY, false, EXPECTED_EMPTY, null),

				Arguments.of("Update with same contacts",
						INITIAL_MULTIPLE_CONTACTS, INITIAL_MULTIPLE_CONTACTS, true, EXPECTED_MULTIPLE_CONTACTS, null)
		);
	}

	/**
	 * Parametrized tests for updating guests with existing contact lists
	 */
	@ParameterizedTest
	@MethodSource("provideContactListUpdateScenarios")
	void update_contactLists(
			final @Nonnull String testName,
			final @Nonnull List<String> initialContactListIds,
			final @Nonnull List<String> newContactListIds,
			boolean contactListFunctionalityEnabled,
			final @Nonnull List<String> expectedContactListIds,
			final @Nullable BusinessErrorCode expectedErrorCode) {

		final Functionality contactListFunctionality = contactListFunctionalityEnabled ?
				this.createFunctionality(true) : this.createFunctionality(false);

		when(this.functionalityReadOnlyService.getCanAssignContactListToGuest(any(AbstractDomain.class)))
				.thenReturn(contactListFunctionality);

		final User owner = new Internal("Test", "Owner", "owner@test.com", "ownerId");
		final Guest existingGuest = new Guest("Existing", "Guest", "existing@linshare.org");
		existingGuest.setDomain(this.domainGuest);
		existingGuest.setRestricted(!initialContactListIds.isEmpty());

		final Map<String, ContactList> contactListMap = new HashMap<>();
		final Set<AccountContactLists> currentContactLists = new HashSet<>();
		for (final String clId : initialContactListIds) {
			final ContactList cl = createContactList(clId, null, owner, null);
			contactListMap.put(clId, cl);
			final AccountContactLists acl = new AccountContactLists(existingGuest, cl);
			acl.setCanViewContactListMembers(true);
			currentContactLists.add(acl);
		}
		existingGuest.setContactLists(currentContactLists);
		final List<ContactList> newContactListsToAdd = new ArrayList<>();
		for (final String clId : newContactListIds) {
			final ContactList cl = contactListMap.computeIfAbsent(clId, id -> createContactList(id, null, owner, null));
			newContactListsToAdd.add(cl);
			when(this.contactListRepository.findByUuid(cl.getUuid())).thenReturn(cl);
		}
		this.setupGuestUpdateMocks(existingGuest);
		if (expectedErrorCode != null) {
			final Map<String, Boolean> defaultPermissions = this.createDefaultPermissions(newContactListsToAdd);
			final List<ContactList> contactListsToPass = newContactListsToAdd.isEmpty() &&
					!initialContactListIds.isEmpty() ? Collections.emptyList() :
					(newContactListsToAdd.isEmpty() ? null : newContactListsToAdd);
			final BusinessException exception = assertThrows(BusinessException.class, () -> {
				this.guestBusinessService.update(this.user, existingGuest, existingGuest,
						null,
						contactListsToPass,
						defaultPermissions);
			});
			assertEquals(expectedErrorCode, exception.getErrorCode(),
					String.format("Error code mismatch in test '%s'", testName));
		}
		else {
			final Map<String, Boolean> defaultPermissions = this.createDefaultPermissions(newContactListsToAdd);
			final List<ContactList> contactListsToPass = newContactListsToAdd.isEmpty() &&
					!initialContactListIds.isEmpty() ? Collections.emptyList() :
					(newContactListsToAdd.isEmpty() ? null : newContactListsToAdd);
			this.guestBusinessService.update(this.user, existingGuest, existingGuest,
					null,
					contactListsToPass,
					defaultPermissions);
			final Set<AccountContactLists> actualContactLists = existingGuest.getRestrictedContactLists();
			assertThat(actualContactLists)
					.extracting(acl -> acl.getContactList().getIdentifier())
					.containsExactlyInAnyOrderElementsOf(expectedContactListIds);
			verify(this.guestRepository).update(existingGuest);
		}
	}

	private static Stream<Arguments> provideContactListUpdateScenarios() {
		return Stream.of(
				Arguments.of("Add contact lists to guest without initial lists",
						INITIAL_EMPTY, NEW_MULTIPLE_CONTACT_LISTS, true, EXPECTED_MULTIPLE_CONTACT_LISTS, null),

				Arguments.of("Add contact lists when functionality disabled - feature disabled",
						INITIAL_EMPTY, NEW_SINGLE_CONTACT_LIST, false, EXPECTED_EMPTY, BusinessErrorCode.FUNCTIONALITY_GUEST_CONTACT_LISTS_DISABLED),

				Arguments.of("Add contact lists to guest with existing lists",
						INITIAL_SINGLE_CONTACT_LIST, NEW_EXTENDED_CONTACTS, true, EXPECTED_EXTENDED_CONTACTS, null),

				Arguments.of("Remove some contact lists",
						INITIAL_MULTIPLE_CONTACT_LISTS, NEW_SINGLE_CONTACT_LIST, true, EXPECTED_SINGLE_CONTACT_LIST, null),

				Arguments.of("Remove all contact lists",
						INITIAL_MULTIPLE_CONTACT_LISTS, NEW_EMPTY, true, EXPECTED_EMPTY, null),

				Arguments.of("Remove contact lists when functionality disabled - should keep existing",
						INITIAL_MULTIPLE_CONTACT_LISTS, NEW_EMPTY, false, EXPECTED_MULTIPLE_CONTACT_LISTS, null),

				Arguments.of("Replace contact lists with different ones",
						INITIAL_SINGLE_CONTACT_LIST, NEW_DIFFERENT_CONTACT_LISTS, true, EXPECTED_DIFFERENT_CONTACT_LISTS, null),

				Arguments.of("Replace contact lists when functionality disabled - should throw an exception",
						INITIAL_SINGLE_CONTACT_LIST, NEW_DIFFERENT_CONTACT_LISTS, false, EXPECTED_SINGLE_CONTACT_LIST, BusinessErrorCode.FUNCTIONALITY_GUEST_CONTACT_LISTS_DISABLED),

				Arguments.of("Add contact lists when functionality disabled",
						INITIAL_EMPTY, NEW_SINGLE_CONTACT_LIST, false, EXPECTED_EMPTY, BusinessErrorCode.FUNCTIONALITY_GUEST_CONTACT_LISTS_DISABLED),

				Arguments.of("Update with null contact lists when functionality disabled - should succeed",
						INITIAL_EMPTY, INITIAL_EMPTY, false, EXPECTED_EMPTY, null),

				Arguments.of("Update with empty contact lists when functionality disabled - should succeed",
						INITIAL_EMPTY, NEW_EMPTY, false, EXPECTED_EMPTY, null),

				Arguments.of("Update with same contact lists",
						INITIAL_MULTIPLE_CONTACT_LISTS, INITIAL_MULTIPLE_CONTACT_LISTS, true, EXPECTED_MULTIPLE_CONTACT_LISTS, null),

				Arguments.of("Remove all lists from guest without lists",
						INITIAL_EMPTY, NEW_EMPTY, true, EXPECTED_EMPTY, null)
		);
	}

	private Map<String, Boolean> createDefaultPermissions(final @Nullable List<ContactList> contactLists) {
		final Map<String, Boolean> permissions = new HashMap<>();
		if (contactLists != null) {
			for (final ContactList cl : contactLists) {
				permissions.put(cl.getUuid(), true);
			}
		}
		return permissions;
	}

	private void setupGuestUpdateMocks(final @Nonnull Guest existingGuest) {
		when(this.guestRepository.findByLsUuid(existingGuest.getLsUuid())).thenReturn(existingGuest);
		when(this.guestRepository.update(any(Guest.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(this.allowedContactRepository.findByOwner(existingGuest))
				.thenAnswer(invocation -> new ArrayList<>(existingGuest.getRestrictedContacts()));

		doAnswer(invocation -> {
			final Guest guest = invocation.getArgument(0);
			guest.getRestrictedContacts().clear();
			return null;
		}).when(this.allowedContactRepository).purge(any(Guest.class));

		doAnswer(invocation -> {
			final AllowedContact newContact = invocation.getArgument(0);
			final Guest owner = (Guest) newContact.getOwner();
			owner.getRestrictedContacts().add(newContact);
			return newContact;
		}).when(this.allowedContactRepository).create(any(AllowedContact.class));

		when(this.recipientFavouriteRepository.getElementsOrderByWeight(any())).thenReturn(Collections.emptyList());
		doNothing().when(this.recipientFavouriteRepository).delete(any());

		when(this.accountContactListRepository.findByAccount(any(Guest.class)))
				.thenAnswer(invocation -> {
					final Guest guest = invocation.getArgument(0);
					return new ArrayList<>(guest.getRestrictedContactLists());
				});

		when(this.accountContactListRepository.findByAccountAndContactList(any(Guest.class), any(ContactList.class)))
				.thenAnswer(invocation -> {
					final Guest guest = invocation.getArgument(0);
					final ContactList contactList = invocation.getArgument(1);
					return guest.getRestrictedContactLists().stream()
							.filter(acl -> acl.getContactList().getUuid().equals(contactList.getUuid()))
							.findFirst();
				});

		doAnswer(invocation -> {
			final AccountContactLists listToDelete = invocation.getArgument(0);
			final Guest owner = (Guest) listToDelete.getAccount();
			owner.getRestrictedContactLists().remove(listToDelete);
			LOGGER.debug("Deleted contact list: {}", listToDelete.getContactList().getIdentifier());
			return null;
		}).when(this.accountContactListRepository).delete(any(AccountContactLists.class));

		doAnswer(invocation -> {
			final AccountContactLists newList = invocation.getArgument(0);
			final Guest owner = (Guest) newList.getAccount();
			owner.getRestrictedContactLists().add(newList);
			return newList;
		}).when(this.accountContactListRepository).create(any(AccountContactLists.class));

		doAnswer(invocation -> {
			final AccountContactLists updatedList = invocation.getArgument(0);
			return updatedList;
		}).when(this.accountContactListRepository).update(any(AccountContactLists.class));

		when(this.mailingListBusinessServiceImpl.determineCanViewPermissionForCreate(any(ContactList.class), any(Map.class)))
				.thenAnswer(invocation -> {
					final ContactList contactList = invocation.getArgument(0);
					final Map<String, Boolean> permissions = invocation.getArgument(1);
					if (permissions != null && permissions.containsKey(contactList.getUuid())) {
						return permissions.get(contactList.getUuid());
					}
					return true;
				});

		when(this.mailingListBusinessServiceImpl.hasRightToHideMembersToGuest(any(AbstractDomain.class))).thenReturn(true);
		when(this.mailingListBusinessServiceImpl.hasDelegationPolicy(any(AbstractDomain.class))).thenReturn(true);

		doAnswer(invocation -> {
			final Guest account = invocation.getArgument(0);
			final ContactList contactList = invocation.getArgument(1);
			final Optional<AccountContactLists> toDelete = account.getRestrictedContactLists().stream()
					.filter(acl -> acl.getContactList().getUuid().equals(contactList.getUuid()))
					.findFirst();
			toDelete.ifPresent(acl -> {
				account.getRestrictedContactLists().remove(acl);
			});
			return null;
		}).when(this.mailingListBusinessServiceImpl).deleteByAccountAndContactList(any(Guest.class), any(ContactList.class));
		doAnswer(invocation -> {
			final Guest guest = invocation.getArgument(0);
			final List<ContactList> newContactLists = invocation.getArgument(1);
			final Map<String, Boolean> permissions = invocation.getArgument(2);

			LOGGER.debug("updateAccountContactLists called with {} new contact lists",
					newContactLists != null ? newContactLists.size() : 0);
			final List<AccountContactLists> existingACLs = new ArrayList<>(guest.getRestrictedContactLists());
			final Set<String> existingIds = existingACLs.stream()
					.map(acl -> acl.getContactList().getUuid())
					.collect(Collectors.toSet());

			final Set<String> newIds = newContactLists != null
					? newContactLists.stream().map(ContactList::getUuid).collect(Collectors.toSet())
					: Collections.emptySet();
			existingACLs.stream()
					.filter(acl -> !newIds.contains(acl.getContactList().getUuid()))
					.forEach(acl -> {
						guest.getRestrictedContactLists().remove(acl);
						LOGGER.debug("Removed contact list: {}", acl.getContactList().getIdentifier());
					});
			if (newContactLists != null) {
				for (final ContactList cl : newContactLists) {
					if (!existingIds.contains(cl.getUuid())) {
						final AccountContactLists newACL = new AccountContactLists(guest, cl);
						boolean canView = permissions != null && permissions.containsKey(cl.getUuid())
								? permissions.get(cl.getUuid())
								: true;
						newACL.setCanViewContactListMembers(canView);
						guest.getRestrictedContactLists().add(newACL);
						LOGGER.debug("Added contact list: {} with canView={}", cl.getIdentifier(), canView);
					} else {
						guest.getRestrictedContactLists().stream()
								.filter(acl -> acl.getContactList().getUuid().equals(cl.getUuid()))
								.findFirst()
								.ifPresent(acl -> {
									boolean canView = permissions != null && permissions.containsKey(cl.getUuid())
											? permissions.get(cl.getUuid())
											: acl.getCanViewContactListMembers();
									acl.setCanViewContactListMembers(canView);
									LOGGER.debug("Updated contact list: {} with canView={}", cl.getIdentifier(), canView);
								});
					}
				}
			}

			return null;
		}).when(this.mailingListBusinessServiceImpl).updateAccountContactLists(any(Guest.class), any(), any());
	}

	private Functionality createFunctionality(boolean isFunctionalityEnabled) {
		final Functionality functionality = mock(Functionality.class);
		final Policy activationPolicy = createPolicy(isFunctionalityEnabled);
		final Policy delegationPolicy = createPolicy(isFunctionalityEnabled);
		when(functionality.getActivationPolicy()).thenReturn(activationPolicy);
		when(functionality.getDelegationPolicy()).thenReturn(delegationPolicy);
		return functionality;
	}

	private Policy createPolicy(boolean isPolicyEnabled) {
		final Policy policy = mock(Policy.class);
		when(policy.getStatus()).thenReturn(isPolicyEnabled);
		return policy;
	}
	/**
	 * Helper method to create a {@link ContactList} for testing purposes.
	 *
	 * @param identifier  The identifier of the contact list.
	 * @param description The description of the contact list.
	 * @param owner       The owner of the contact list.
	 * @param domain      The domain associated with the contact list.
	 * @return A new {@link ContactList} instance.
	 */
	// TODO: Extract this method to a common utility class to avoid duplication with ContactListBusinessServiceTest.
	private static ContactList createContactList(@Nonnull final String identifier, @Nullable final String description, @Nonnull final User owner,
										  @Nonnull final AbstractDomain domain) {
		final ContactList contactList = new ContactList();
		contactList.setIdentifier(identifier);
		contactList.setOwner(owner);
		contactList.setPublic(true);
		contactList.setDomain(domain);
		contactList.setDescription(description);
		contactList.setContactListContacts(new HashSet<>());
		contactList.setUuid(UUID.randomUUID().toString());
		return contactList;
	}

}