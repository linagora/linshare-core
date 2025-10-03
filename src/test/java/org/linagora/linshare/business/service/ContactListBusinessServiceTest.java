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
package org.linagora.linshare.business.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.linagora.linshare.core.domain.constants.LinShareTestConstants.GUEST_DOMAIN;
import static org.linagora.linshare.core.domain.constants.LinShareTestConstants.SUB_DOMAIN;
import static org.linagora.linshare.core.domain.constants.LinShareTestConstants.TOP_DOMAIN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.linagora.linshare.core.business.service.ModeratorBusinessService;
import org.linagora.linshare.core.business.service.impl.MailingListBusinessServiceImpl;
import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.AccountContactLists;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Moderator;
import org.linagora.linshare.core.domain.entities.TopDomain;
import org.linagora.linshare.core.domain.entities.GuestDomain;
import org.linagora.linshare.core.domain.entities.SubDomain;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Policy;

import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountContactListsRepository;
import org.linagora.linshare.core.repository.MailingListRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class contains unit tests for the {@link MailingListBusinessServiceImpl} class.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ContactListBusinessServiceTest {

    @Mock
    private MailingListRepository mailingListRepository;

    @Mock
    private AbstractDomainRepository abstractDomainRepository;

    @Mock
    private ModeratorBusinessService moderatorBusinessService;

    @Mock
    private AccountContactListsRepository accountContactListsRepository;

    @InjectMocks
    private MailingListBusinessServiceImpl mailingListBusinessService;

    @Mock
    private FunctionalityReadOnlyService functionalityReadOnlyService;

    private static User owner;
    private static User owner2;
    private static User owner3;
    private static Guest guest;
    private static AbstractDomain guestDomain;
    private static AbstractDomain topDomain;
    private static AbstractDomain subDomain;
    private static Moderator moderator;
    private static Moderator moderator1;

    /**
     * These contact lists are used in the parameterized unit test {@link #findByAccountAndContactListUuids} as static
     * contact lists to improve performance by avoiding their recreation for each parameter combination.
     */
    private static ContactList restrictedContact1, restrictedContact2, restrictedContact3;

    static {
        topDomain = new TopDomain(TOP_DOMAIN);
        topDomain.setUuid(TOP_DOMAIN);

        guestDomain = new GuestDomain(GUEST_DOMAIN);
        guestDomain.setUuid(GUEST_DOMAIN);

        subDomain = new SubDomain(SUB_DOMAIN);
        subDomain.setUuid(SUB_DOMAIN);

        owner = new Internal("John", "Doe", "user1@linshare.org", null);
        owner.setDomain(topDomain);

        owner2 = new Internal("Jane", "Smith", "user2@linshare.org", null);
        owner2.setDomain(topDomain);

        owner3 = new Internal("John", "Smith", "user3@linshare.org", null);
        owner3.setDomain(subDomain);

        guest = new Guest("Guest", "Doe", "guest1@linshare.org");
        guest.setModerators(new HashSet<>());
        guest.setDomain(guestDomain);

        moderator = new Moderator(ModeratorRole.ADMIN, owner, guest);
        moderator1 = new Moderator(ModeratorRole.SIMPLE, owner2, guest);

        restrictedContact1 = createDefaultContactList();
        restrictedContact2 = createDefaultContactList();
        restrictedContact3 = createDefaultContactList();
    }

    /**
     * Initializes the test environment before each test method is executed. This method sets up the necessary mock
     * objects and initializes the test data.
     */
    @BeforeEach
    public void setUp() {
        when(this.abstractDomainRepository.findById(TOP_DOMAIN)).thenReturn(topDomain);
        when(this.abstractDomainRepository.findById(GUEST_DOMAIN)).thenReturn(guestDomain);
        when(this.abstractDomainRepository.findById(SUB_DOMAIN)).thenReturn(subDomain);

        lenient().when(this.moderatorBusinessService.create(any(Moderator.class))).thenReturn(moderator);
        lenient().when(this.moderatorBusinessService.create(any(Moderator.class))).thenReturn(moderator1);

        Objects.requireNonNull(restrictedContact1, "restrictedContact1 must not be null");
        Objects.requireNonNull(restrictedContact2, "restrictedContact2 must not be null");
        Objects.requireNonNull(restrictedContact3, "restrictedContact3 must not be null");

        final Functionality functionality = mock(Functionality.class);
        final Policy activationPolicy = mock(Policy.class);
        final Policy delegationPolicy = mock(Policy.class);

        when(activationPolicy.getStatus()).thenReturn(true);
        when(delegationPolicy.getStatus()).thenReturn(true);

        when(functionality.getActivationPolicy()).thenReturn(activationPolicy);
        when(functionality.getDelegationPolicy()).thenReturn(delegationPolicy);

        when(functionalityReadOnlyService.getCanHideMembersToGuest(any(AbstractDomain.class)))
                .thenReturn(functionality);
    }

    /**
     * Helper method to invoke the private method {@link MailingListBusinessServiceImpl#isPrivateListValid}.
     */
    private boolean invokeIsPrivateListValid(@Nonnull final ContactList contactList,
                                             @Nonnull final Account actor,
                                             @Nonnull final Account guest,
                                             final boolean hasOtherModerator)
            throws Exception {
        final Method method = MailingListBusinessServiceImpl.class.getDeclaredMethod(
                "isPrivateListValid",
                ContactList.class,
                Account.class,
                Account.class,
                boolean.class
        );
        method.setAccessible(true);
        return (Boolean) method.invoke(this.mailingListBusinessService, contactList, actor, guest, hasOtherModerator);
    }

    /**
     * Validates creation of new account-contact list associations.
     * Verifies:
     * <ul>
     *   <li>New links are created for all items in input list</li>
     *   <li>No deletions occur when updating from empty state</li>
     * </ul>
     */
    @Test
    void updateAccountContactLists_ShouldAddNewContacts() {
        final ContactList contact1 = this.createContactList("identifier1", "cl1", owner3, subDomain);
        contact1.setUuid(UUID.randomUUID().toString());
        final ContactList contact2 = createContactList("identifier2", "cl2", owner3, subDomain);
        contact2.setUuid(UUID.randomUUID().toString());
        final List<ContactList> newContacts = List.of(contact1, contact2);

        Map<String, Boolean> permissions = new HashMap<>();
        permissions.put(contact1.getUuid(), true);
        permissions.put(contact2.getUuid(), true);

        when(this.accountContactListsRepository.findByAccount(guest)).thenReturn(Collections.emptyList());

        this.mailingListBusinessService.updateAccountContactLists(guest, newContacts, permissions);

        verify(this.accountContactListsRepository, times(2)).create(any(AccountContactLists.class));
        verify(this.accountContactListsRepository, never()).delete(any(AccountContactLists.class));
    }

    /**
     * Tests update scenario with partial list changes.
     * Verifies:
     * <ul>
     *   <li>Obsolete associations are removed</li>
     *   <li>New associations are created</li>
     *   <li>Repository delete/create methods are called appropriately</li>
     * </ul>
     */
    @Test
    void updateAccountContactLists_ShouldRemoveOldContacts() {
        final ContactList contact1 = createContactList("identifier1", "cl1", owner3, topDomain);
        contact1.setUuid(UUID.randomUUID().toString());
        final ContactList contact2 = createContactList("identifier2", "cl2", owner3, topDomain);
        contact2.setUuid(UUID.randomUUID().toString());
        final List<ContactList> newContacts = List.of(contact2);

        final AccountContactLists existingLink = new AccountContactLists(guest, contact1);
        final Set<AccountContactLists> accountContactLists = new HashSet<>();
        accountContactLists.add(existingLink);
        guest.setContactLists(accountContactLists);

        when(this.accountContactListsRepository.findByAccount(guest)).thenReturn(List.of(existingLink));
        when(this.accountContactListsRepository.findByAccountAndContactList(guest, contact1))
                .thenReturn(Optional.of(existingLink));
        Map<String, Boolean> permissions = new HashMap<>();
        permissions.put(contact1.getUuid(), true);
        permissions.put(contact2.getUuid(), true);
        this.mailingListBusinessService.updateAccountContactLists(guest, newContacts, permissions);

        verify(this.accountContactListsRepository).delete(existingLink);
        verify(this.accountContactListsRepository).create(any(AccountContactLists.class));
    }

    /**
     * Validates idempotent behavior for empty input.
     * Ensures no operations are performed when both existing and new lists are empty.
     */
    @Test
    void updateAccountContactLists_WithEmptyLists_ShouldDoNothing() {
        when(this.accountContactListsRepository.findByAccount(guest)).thenReturn(Collections.emptyList());

        this.mailingListBusinessService.updateAccountContactLists(guest, Collections.emptyList(), null);

        verify(this.accountContactListsRepository, never()).create(any());
        verify(this.accountContactListsRepository, never()).delete(any());
    }

    /**
     * Tests handling of null contact lists input.
     */
    @Test
    void updateAccountContactLists_WhenNullContactLists_ShouldProcessEmptyList() {
        final List<ContactList> emptyList = Collections.emptyList();
        this.mailingListBusinessService.updateAccountContactLists(guest, emptyList, null);
        verify(this.accountContactListsRepository, never()).create(any());
        verify(this.accountContactListsRepository, never()).delete(any());
    }

    /**
     * Tests no-op scenario when input matches existing associations.
     * Verifies repository isn't called when no changes are detected.
     */
    @Test
    void updateAccountContactLists_WithIdenticalLists_ShouldDoNothing() {
        final ContactList contact = this.createContactList("cl1", "Liste 1", owner, topDomain);
        final AccountContactLists link = new AccountContactLists(guest, contact);

        when(this.accountContactListsRepository.findByAccount(guest)).thenReturn(List.of(link));
        final Map<String, Boolean> permissions = new HashMap<>();
        permissions.put(contact.getUuid(), true);

        this.mailingListBusinessService.updateAccountContactLists(guest, List.of(contact), permissions);

        verify(this.accountContactListsRepository, never()).create(any());
        verify(this.accountContactListsRepository, never()).delete(any());
    }

    /**
     * Tests error propagation from persistence layer.
     * Verifies repository exceptions are wrapped in business exceptions with context.
     */
    @Test
    void deleteByAccountAndContactList_WhenRepositoryThrowsException_ShouldPropagate() {
        final ContactList contact = this.createContactList("cl1", "Liste 1", owner, topDomain);
        final AccountContactLists link = new AccountContactLists(guest, contact);

        when(this.accountContactListsRepository.findByAccountAndContactList(guest, contact)).thenReturn(Optional.of(link));
        doThrow(new IllegalArgumentException("DB error")).when(accountContactListsRepository).delete(link);

        final BusinessException exception = assertThrows(BusinessException.class, () ->
                this.mailingListBusinessService.deleteByAccountAndContactList(guest, contact)
        );

        assertEquals(BusinessErrorCode.FAILED_DELETE_ACCOUNT_CONTACT_LISTS, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("BUG !!!"));

		assertInstanceOf(IllegalArgumentException.class, exception.getCause());
        assertEquals("DB error", exception.getCause().getMessage());
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
    // TODO: Extract this method to a common utility class to avoid duplication with GuestBusinessServiceTest.
    private ContactList createContactList(@Nonnull final String identifier, @Nullable final String description, @Nonnull final User owner,
                                          @Nonnull final AbstractDomain domain) {
        final ContactList contactList = new ContactList();
        contactList.setIdentifier(identifier);
        contactList.setOwner(owner);
        contactList.setPublic(true);
        contactList.setDomain(domain);
        contactList.setDescription(description);
        contactList.setContactListContacts(new HashSet<>());
        contactList.setUuid(UUID.randomUUID().toString());
        this.mailingListRepository.create(contactList);
        return contactList;
    }

    /**
     * Helper method to create a default {@link ContactList} for testing purposes.
     *
     * @return A new {@link ContactList} instance with default values.
     */
    private static ContactList createDefaultContactList() {
        final ContactList contactList = new ContactList();
        contactList.setUuid(UUID.randomUUID().toString());
        contactList.setIdentifier("default-identifier");
        contactList.setDescription("Default Description");
        contactList.setPublic(true);
        contactList.setOwner(owner);
        contactList.setDomain(topDomain);
        contactList.setContactListContacts(new HashSet<>());
        return contactList;
    }

    /**
     * Test that ensures the correct {@link ContactList} instances are returned when searching by account and contact list UUIDs.
     *
     * <p>This test verifies that when provided with a list of {@link ContactList}s and their corresponding UUIDs, the
     * method {@link MailingListBusinessService#findByAccountAndContactListUuids(User, Guest, List)} correctly returns
     * the expected contact lists, considering the moderation roles of the {@link Guest} and the association with the
     * {@link User} actor.</p>
     *
     * @param description    A description for the test case.
     * @param actor          The {@link User} acting on the request.
     * @param guest          The {@link Guest} associated with the contact lists.
     * @param contactLists   The list of {@link ContactList} instances to be searched.
     * @param contactUuids   The list of UUIDs corresponding to the {@link ContactList}s.
     * @param expectedResult The expected result of the search, containing the correct {@link ContactList}s.
     */
    @ParameterizedTest
    @MethodSource("provideTestCasesForFindByAccountAndContactListUuids")
    void findByAccountAndContactListUuids(
            final String description,
            @Nonnull final User actor,
            @Nonnull final Guest guest,
            final List<ContactList> contactLists,
            final List<String> contactUuids,
            final List<ContactList> expectedResult
    ) {
        for (final ContactList contactList : contactLists) {
            when(this.mailingListRepository.findByUuid(contactList.getUuid())).thenReturn(contactList);
        }

        if (guest.getModerators() != null && !guest.getModerators().isEmpty()) {
            for (final Moderator mod : guest.getModerators()) {
                when(this.moderatorBusinessService.findByGuestAndAccount(mod.getAccount(), guest))
                        .thenReturn(Optional.of(mod));
            }
        }

        final List<ContactList> result = this.mailingListBusinessService.findByAccountAndContactListUuids(actor, guest, contactUuids);

        assertEquals(expectedResult.size(), result.size(), description + ": Size mismatch");
        assertTrue(result.containsAll(expectedResult), description + ": Result does not contain expected contact lists");
    }

    /**
     * Provides a stream of test cases for the {@link MailingListBusinessServiceImpl#findByAccountAndContactListUuids} method.
     */
    private static Stream<Arguments> provideTestCasesForFindByAccountAndContactListUuids() {
        final ContactList publicList1 = createDefaultContactList();
        publicList1.setIdentifier("public-list-1");
        publicList1.setDescription("Public List 1");

        final ContactList publicList2 = createDefaultContactList();
        publicList2.setIdentifier("public-list-2");
        publicList2.setDescription("Public List 2");
        publicList2.setOwner(owner2);

        final ContactList privateList1 = createDefaultContactList();
        privateList1.setIdentifier("private-list-1");
        privateList1.setDescription("Private List 1");
        privateList1.setPublic(false);

        final ContactList privateList2 = createDefaultContactList();
        privateList2.setIdentifier("private-list-2");
        privateList2.setDescription("Private List 2");
        privateList2.setPublic(false);
        privateList2.setOwner(owner2);

        final ContactList publicList3 = createDefaultContactList();
        publicList3.setIdentifier("public-list.-3");
        publicList3.setDescription("Public List 3");
        publicList3.setDomain(subDomain);
        publicList3.setOwner(owner3);

        final Guest guestWithModerators = new Guest();
        guestWithModerators.setModerators(new HashSet<>(Arrays.asList(moderator, moderator1)));

        return Stream.of(
                Arguments.of(
                        "Public lists in the same domain",
                        owner,
                        guest,
                        Arrays.asList(publicList1, publicList2),
                        Arrays.asList(publicList1.getUuid(), publicList2.getUuid()),
                        Arrays.asList(publicList1, publicList2)
                ),
                Arguments.of(
                        "Private lists owned by the actor",
                        owner,
                        guest,
                        Arrays.asList(privateList1, privateList2),
                        Arrays.asList(privateList1.getUuid(), privateList2.getUuid()),
                        Arrays.asList(privateList1)
                ),
                Arguments.of(
                        "Actor is a moderator",
                        owner,
                        guestWithModerators,
                        Arrays.asList(privateList1, privateList2),
                        Arrays.asList(privateList1.getUuid(), privateList2.getUuid()),
                        Arrays.asList(privateList1, privateList2)
                ),
                Arguments.of(
                        "Mixed public and private lists",
                        owner,
                        guest,
                        Arrays.asList(publicList1, privateList1, publicList3),
                        Arrays.asList(publicList1.getUuid(), privateList1.getUuid(), publicList3.getUuid()),
                        Arrays.asList(publicList1, privateList1)
                )
        );
    }

    /**
     * Provides a stream of test cases for the {@link MailingListBusinessServiceImpl#isPrivateListValid} method.
     */
    private static Stream<Arguments> privateListValidationProvider() {
        return Stream.of(
                arguments(
                        "Owner is actor",
                        owner,
                        owner,
                        Set.of(),
                        false,
                        Optional.empty(),
                        true
                ),
                arguments(
                        "Not owner, no moderators",
                        owner2,
                        owner,
                        Set.of(),
                        false,
                        Optional.empty(),
                        false
                ),

                arguments(
                        "Actor is moderator",
                        owner2,
                        owner2,
                        Set.of(moderator1),
                        true,
                        Optional.of(moderator1),
                        true
                ),

                arguments(
                        "Not owner, not moderator",
                        owner3,
                        owner2,
                        Set.of(),
                        false,
                        Optional.empty(),
                        false
                ),

                arguments(
                        "Guest has no moderators",
                        owner2,
                        owner,
                        Set.of(),
                        false,
                        Optional.empty(),
                        false
                ),

                arguments(
                        "Actor moderator but no others",
                        owner2,
                        owner,
                        Set.of(moderator),
                        false,
                        Optional.empty(),
                        false
                ),

                arguments(
                        "Other moderators exist but actor not moderator",
                        owner2,
                        owner,
                        Set.of(moderator1),
                        true,
                        Optional.empty(),
                        false
                )
        );
    }

    /**
     * Tests the {@link MailingListBusinessServiceImpl#isPrivateListValid} method with various scenarios.
     * <p>
     * This parameterized test verifies the behavior of the private list validation logic under different conditions,
     * including cases where:
     * <ul>
     *   <li>The actor is the owner of the contact list</li>
     *   <li>The actor is not the owner and there are no moderators</li>
     *   <li>The actor is a moderator for the guest</li>
     *   <li>The actor is neither the owner nor a moderator</li>
     * </ul>
     * </p>
     *
     * @param description          A description of the test scenario.
     * @param contactListOwner     The owner of the contact list.
     * @param actor                The actor attempting to access the list.
     * @param guestModerators      The set of moderators for the guest.
     * @param hasOtherModerator    A flag indicating if there are other moderators.
     * @param mockModeratorResult  The mocked result of the moderator check.
     * @param expectedResult       The expected result of the validation.
     * @throws Exception If an error occurs during test execution.
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("privateListValidationProvider")
    void isPrivateListValid(
            final String description,
            @Nonnull final User contactListOwner,
            @Nonnull final Account actor,
            final Set<Moderator> guestModerators,
            final boolean hasOtherModerator,
            final Optional<Moderator> mockModeratorResult,
            final boolean expectedResult
    ) throws Exception {
        final ContactList contactList = new ContactList();
        contactList.setOwner(contactListOwner);
        contactList.setPublic(false);

        final Guest newGuest = new Guest("Test", "Guest", "test@linshare.org");
        newGuest.setModerators(new HashSet<>(guestModerators));
        newGuest.setDomain(guestDomain);

        if (!contactListOwner.equals(actor)) {
            when(this.moderatorBusinessService.findByGuestAndAccount(contactListOwner, guest)).thenReturn(
                    mockModeratorResult);
        }

        assertEquals(expectedResult, this.invokeIsPrivateListValid(contactList, actor, guest, hasOtherModerator), description);

        if (!contactListOwner.equals(actor)) {
            verify(this.moderatorBusinessService).findByGuestAndAccount(contactListOwner, guest);
        }
    }

    /**
     * Tests handling of non-existent contact lists during retrieval.
     */
    @Test
    void findByAccountAndContactListUuids_WhenContactListNotFound_ShouldThrowException() {
        final String uuid = UUID.randomUUID().toString();
        when(this.mailingListRepository.findByUuid(uuid)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            this.mailingListBusinessService.findByAccountAndContactListUuids(this.owner, this.guest, List.of(uuid));
        });
        assertTrue(exception.getCause() instanceof BusinessException);
        assertEquals("The current mailing list do not exist : " + uuid, exception.getCause().getMessage());
    }

    /**
     * Parameterized test for view permission determination during creation.
     */
    @ParameterizedTest
    @MethodSource("provideTestCasesForDetermineCanViewPermission")
    void determineCanViewPermissionForCreate_TestCases(
             final String description,
             final Boolean delegationPolicyStatus,
             final Boolean permissionValue,
             final Class<? extends Exception> expectedException,
             final Boolean expectedResult
    ) {
        final ContactList contactList = new ContactList();
        contactList.setUuid(UUID.randomUUID().toString());
        contactList.setIdentifier("test-list");
        contactList.setOwner(owner);

        final Map<String, Boolean> permissions = new HashMap<>();
        permissions.put(contactList.getUuid(), permissionValue);

        final Functionality functionality = mock(Functionality.class);
        final Policy activationPolicy = mock(Policy.class);
        final Policy delegationPolicy = mock(Policy.class);

        when(activationPolicy.getStatus()).thenReturn(true);
        when(functionality.getActivationPolicy()).thenReturn(activationPolicy);
        when(functionality.getDelegationPolicy()).thenReturn(delegationPolicy);
        when(delegationPolicy.getStatus()).thenReturn(delegationPolicyStatus);
        when(this.functionalityReadOnlyService.getCanHideMembersToGuest(any(AbstractDomain.class))).thenReturn(functionality);

        if (expectedException != null) {
            assertThrows(expectedException, () -> {
                this.mailingListBusinessService.determineCanViewPermissionForCreate(contactList, permissions);
            });
        } else {
             Boolean result = this.mailingListBusinessService.determineCanViewPermissionForCreate(contactList, permissions);
            assertEquals(expectedResult, result);
        }
    }

    /**
     * Provides test cases for view permission determination.
     */
    private static Stream<Arguments> provideTestCasesForDetermineCanViewPermission() {
        return Stream.of(
                Arguments.of("Delegation enabled, permission true", true, true, null, true),
                Arguments.of("Delegation enabled, permission false", true, false, null, false),
                Arguments.of("Delegation enabled, permission null", true, true, null, true),
                Arguments.of("Delegation disabled, permission true", false, true, BusinessException.class, null),
                Arguments.of("Delegation disabled, permission false", false, false, null, false),
                Arguments.of("Delegation disabled, permission null", false, null, BusinessException.class, null)
        );
    }


    /**
     * Tests exception when hide members feature is disabled during update.
     */
    @Test
    void updateAccountContactLists_WhenHideMembersFeatureDisabled_ShouldThrowException() {
        final ContactList contact = createContactList("test", "Test", owner, topDomain);
        final Map<String, Boolean> permissions = new HashMap<>();
        permissions.put(contact.getUuid(), true);
        final Functionality functionality = mock(Functionality.class);
        final Policy activationPolicy = mock(Policy.class);
        when(activationPolicy.getStatus()).thenReturn(false);
        when(functionality.getActivationPolicy()).thenReturn(activationPolicy);
        when(this.functionalityReadOnlyService.getCanHideMembersToGuest(any(AbstractDomain.class))).thenReturn(functionality);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            this.mailingListBusinessService.updateAccountContactLists(guest, List.of(contact), permissions);
        });

        assertEquals(BusinessErrorCode.FUNCTIONALITY_GUESTS__HIDE_MEMBERS_DISABLED, exception.getErrorCode());
    }

    /**
     * Tests exception for contact lists without owner during permission check.
     */
    @Test
    void determineCanViewPermissionForCreate_WhenContactListHasNoOwner_ShouldThrowException() {
        final ContactList contactList = new ContactList();
        contactList.setUuid(UUID.randomUUID().toString());
        contactList.setOwner(null);

        final Map<String, Boolean> permissions = new HashMap<>();
        permissions.put(contactList.getUuid(), true);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            this.mailingListBusinessService.determineCanViewPermissionForCreate(contactList, permissions);
        });

        assertEquals(BusinessErrorCode.INVALID_CONTACT_LIST, exception.getErrorCode());
    }

    /**
     * Tests exception for missing permissions during view permission check.
     */
    @Test
    void determineCanViewPermissionForCreate_WhenPermissionsMissing_ShouldThrowException() {
        final ContactList contactList = new ContactList();
        contactList.setUuid(UUID.randomUUID().toString());
        contactList.setOwner(this.owner);

        final Map<String, Boolean> permissions = new HashMap<>();

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            this.mailingListBusinessService.determineCanViewPermissionForCreate(contactList, permissions);
        });

        assertEquals(BusinessErrorCode.GUEST_INVALID_INPUT, exception.getErrorCode());
    }

    /**
     * Tests that an IllegalStateException is thrown when the GUESTS__HIDE_MEMBERS functionality is disabled.
     */
    @Test
    void determineCanViewPermissionForCreate_WhenHideMembersFunctionalityDisabled_ShouldThrowIllegalStateException() {
        final ContactList contactList = new ContactList();
        contactList.setUuid(UUID.randomUUID().toString());
        contactList.setIdentifier("test-list");
        contactList.setOwner(owner);

        final Map<String, Boolean> permissions = new HashMap<>();
        permissions.put(contactList.getUuid(), true);
        final Functionality functionality = mock(Functionality.class);
        final Policy activationPolicy = mock(Policy.class);
        when(activationPolicy.getStatus()).thenReturn(false);
        when(functionality.getActivationPolicy()).thenReturn(activationPolicy);
        when(functionalityReadOnlyService.getCanHideMembersToGuest(any(AbstractDomain.class))).thenReturn(functionality);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            mailingListBusinessService.determineCanViewPermissionForCreate(contactList, permissions);
        });

        assertEquals("This method should not be called when GUESTS__HIDE_MEMBERS is disabled", exception.getMessage());
    }
}