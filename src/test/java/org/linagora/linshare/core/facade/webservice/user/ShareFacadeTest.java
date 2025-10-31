package org.linagora.linshare.core.facade.webservice.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.EntryType;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AccountContactLists;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.ContactListContact;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.GenericUserDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ShareDto;
import org.linagora.linshare.core.facade.webservice.delegation.dto.ShareCreationDto;
import org.linagora.linshare.core.facade.webservice.user.impl.ShareFacadeImpl;
import org.linagora.linshare.core.repository.MailingListContactRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ContactListService;
import org.linagora.linshare.core.service.ShareService;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class ShareFacadeTest {
	private static final String USER_UUID = "user-uuid";
	private static final String DOMAIN_UUID = "domain-uuid-123";
	private static final String SHARE_ENTRY_UUID = "share-uuid-123";
	private static final String DOCUMENT_ENTRY_UUID_1 = "doc-uuid-1";
	private static final String DOCUMENT_ENTRY_UUID_2 = "doc-uuid-2";
	private static final String LIST_UUID_1 = "list-uuid-1";
	private static final String LIST_UUID_2 = "list-uuid-2";
	private static final String LIST_UUID_SINGLE = "list-uuid";

	private static final String USER_EMAIL_1 = "user1@domain.com";
	private static final String USER_EMAIL_2 = "user2@domain.com";
	private static final String TEST_USER_EMAIL = "user@domain.com";
	private static final String AUTH_USER_EMAIL = "test-user@domain.com";
	private static final String RECIPIENT_EMAIL = "recipient@domain.com";
	private static final String OWNER_EMAIL = "owner@domain.com";

	private static final String FIRST_NAME_USER_1 = "User";
	private static final String LAST_NAME_USER_1 = "One";
	private static final String FIRST_NAME_USER_2 = "User";
	private static final String LAST_NAME_USER_2 = "Two";
	private static final String FIRST_NAME_TEST = "Test";
	private static final String LAST_NAME_TEST = "User";
	private static final String FIRST_NAME_RECIPIENT = "Recipient";
	private static final String LAST_NAME_RECIPIENT = "User";
	private static final String FIRST_NAME_OWNER = "Owner";
	private static final String LAST_NAME_OWNER = "User";

	private static final String TEST_SUBJECT = "Test Subject";
	private static final String TEST_MESSAGE = "Test Message";
	private static final String SIMPLE_SUBJECT = "Simple Subject";
	private static final String SIMPLE_MESSAGE = "Simple Message";
	private static final String SHARING_NOTE = "Sharing Note";
	private static final String MESSAGE_ID = "message-123";
	private static final String REFERENCES = "ref-123";
	private static final String TEST_COMMENT = "Test comment";
	private static final String SHARE_NAME = "Test Share";

	private static final String DOCUMENT_TYPE_PDF = "application/pdf";
	private static final String HUMAN_MIME_TYPE_PDF = "PDF Document";

	private static final long DOCUMENT_SIZE = 1024L;
	private static final long DOWNLOAD_COUNT = 0L;

	private static final boolean SECURED_TRUE = true;
	private static final boolean SECURED_FALSE = false;
	private static final boolean CREATION_ACKNOWLEDGEMENT_TRUE = true;
	private static final boolean CREATION_ACKNOWLEDGEMENT_FALSE = false;
	private static final boolean FORCE_ANONYMOUS_SHARING_TRUE = true;
	private static final boolean FORCE_ANONYMOUS_SHARING_FALSE = false;
	private static final boolean ENABLE_USDA_TRUE = true;
	private static final boolean ENABLE_USDA_FALSE = false;
	private static final boolean CIPHERED_FALSE = false;
	private static final boolean HAS_THUMBNAIL_TRUE = true;

	private static final Language LANGUAGE_FRENCH = Language.FRENCH;
	private static final Language LANGUAGE_ENGLISH = Language.ENGLISH;

	@Mock private UserRepository<User> userRepository;
	@Mock private AccountService accountService;
	@Mock private ContactListService listService;
	@Mock private MailingListContactRepository mailingListContactRepository;
	@Mock private ShareService shareService;

	@InjectMocks
	private ShareFacadeImpl shareFacadeService;

	private User authUser;
	private SecurityContext originalSecurityContext;

	/**
	 * Provides test scenarios for share container configuration tests.
	 *
	 * @return Stream of Arguments containing:
	 * <ul>
	 *   <li>Scenario description</li>
	 *   <li>Mailing list UUIDs</li>
	 *   <li>Recipients list</li>
	 *   <li>Document UUIDs</li>
	 *   <li>Share subject</li>
	 *   <li>Share message</li>
	 *   <li>Secured flag</li>
	 *   <li>Creation acknowledgement flag</li>
	 *   <li>Force anonymous sharing flag</li>
	 *   <li>Expiration date</li>
	 *   <li>Enable USDA flag</li>
	 *   <li>Notification date for USDA</li>
	 *   <li>Sharing note</li>
	 *   <li>In-reply-to reference</li>
	 *   <li>References</li>
	 *   <li>External mail locale</li>
	 *   <li>Expected account contact lists count</li>
	 *   <li>Expected explicit recipient emails</li>
	 *   <li>Expected document count</li>
	 * </ul>
	 */
	private static Stream<Arguments> shareContainerConfigurationScenarios() {
		final Date testExpiryDate = new Date(System.currentTimeMillis() + 86400000);
		final Date testNotificationDate = new Date(System.currentTimeMillis() + 43200000);

		return Stream.of(
				Arguments.of(
						"Complete configuration with mailing lists",
						new HashSet<>(Arrays.asList(LIST_UUID_1, LIST_UUID_2)),
						Arrays.asList(
								createGenericUserDto(USER_EMAIL_1, FIRST_NAME_USER_1, LAST_NAME_USER_1),
								createGenericUserDto(USER_EMAIL_2, FIRST_NAME_USER_2, LAST_NAME_USER_2)
						),
						Arrays.asList(DOCUMENT_ENTRY_UUID_1, DOCUMENT_ENTRY_UUID_2),
						TEST_SUBJECT, TEST_MESSAGE, SECURED_TRUE, CREATION_ACKNOWLEDGEMENT_TRUE, FORCE_ANONYMOUS_SHARING_FALSE,
						testExpiryDate, ENABLE_USDA_TRUE, testNotificationDate, SHARING_NOTE,
						MESSAGE_ID, REFERENCES, LANGUAGE_FRENCH,
						2,
						new HashSet<>(Arrays.asList(USER_EMAIL_1, USER_EMAIL_2)),
						2
				),
				Arguments.of(
						"Minimal configuration without mailing lists",
						null,
						Arrays.asList(createGenericUserDto(USER_EMAIL_1, FIRST_NAME_USER_1, LAST_NAME_USER_1)),
						Arrays.asList(DOCUMENT_ENTRY_UUID_1),
						SIMPLE_SUBJECT, SIMPLE_MESSAGE, SECURED_FALSE, CREATION_ACKNOWLEDGEMENT_FALSE, FORCE_ANONYMOUS_SHARING_TRUE,
						null, ENABLE_USDA_FALSE, null, null, null, null, LANGUAGE_ENGLISH,
						0,
						new HashSet<>(Arrays.asList(USER_EMAIL_1)),
						1
				)
		);
	}

	/**
	 * Creates a GenericUserDto with the specified parameters.
	 *
	 * @param email the user's email address
	 * @param firstName the user's first name
	 * @param lastName the user's last name
	 * @return a new GenericUserDto instance
	 * @throws NullPointerException if any parameter is null
	 */
	private static GenericUserDto createGenericUserDto(@Nonnull final String email, @Nonnull final String firstName, @Nonnull final String lastName) {
		final GenericUserDto dto = new GenericUserDto();
		dto.setMail(email);
		dto.setFirstName(firstName);
		dto.setLastName(lastName);
		return dto;
	}

	/**
	 * Provides test scenarios for mailing list error cases.
	 *
	 * @return Stream of Arguments containing:
	 * <ul>
	 *   <li>Scenario description</li>
	 *   <li>Set of mailing list UUIDs that should trigger not found errors</li>
	 * </ul>
	 */
	private static Stream<Arguments> mailingListErrorScenarios() {
		return Stream.of(
				Arguments.of("Single mailing list not found",
						new HashSet<>(Arrays.asList(LIST_UUID_SINGLE))),
				Arguments.of("Multiple mailing lists not found",
						new HashSet<>(Arrays.asList(LIST_UUID_1, LIST_UUID_2)))
		);
	}

	/**
	 * Provides test scenarios for authentication failure cases.
	 *
	 * @return Stream of Arguments containing:
	 * <ul>
	 *   <li>Scenario description</li>
	 *   <li>Runnable that sets up the authentication failure scenario</li>
	 * </ul>
	 */
	private static Stream<Arguments> authenticationFailureScenarios() {
		return Stream.of(
				Arguments.of("Null principal", (Runnable) () -> {
					final Authentication authentication = mock(Authentication.class);
					final SecurityContext securityContext = mock(SecurityContext.class);
					when(securityContext.getAuthentication()).thenReturn(authentication);
					when(authentication.getName()).thenReturn(null);
					SecurityContextHolder.setContext(securityContext);
				}),
				Arguments.of("User not found in DB", (Runnable) () -> {
					final Authentication authentication = mock(Authentication.class);
					final SecurityContext securityContext = mock(SecurityContext.class);
					when(securityContext.getAuthentication()).thenReturn(authentication);
					when(authentication.getName()).thenReturn(USER_UUID);
					SecurityContextHolder.setContext(securityContext);
				})
		);
	}

	/**
	 * Creates a mocked User instance with the specified attributes.
	 *
	 * @param uuid the user's UUID
	 * @param email the user's email address
	 * @param firstName the user's first name
	 * @param lastName the user's last name
	 * @return a mocked User instance with the specified attributes
	 * @throws NullPointerException if any parameter is null
	 */
	private @Nonnull User createMockUser(@Nonnull final String uuid, @Nonnull final String email, @Nonnull final String firstName, @Nonnull final String lastName) {
		final User user = mock(User.class);
		lenient().when(user.getLsUuid()).thenReturn(uuid);
		lenient().when(user.getMail()).thenReturn(email);
		lenient().when(user.getFirstName()).thenReturn(firstName);
		lenient().when(user.getLastName()).thenReturn(lastName);
		final AbstractDomain domain = mock(AbstractDomain.class);
		lenient().when(domain.getUuid()).thenReturn(DOMAIN_UUID);
		lenient().when(user.getDomain()).thenReturn(domain);
		lenient().when(user.getAccountType()).thenReturn(AccountType.INTERNAL);
		return user;
	}

	/**
	 * Sets up the test environment before each test method execution.
	 * Initializes the authenticated user mock and saves the original security context.
	 */
	@BeforeEach
	void setUp() {
		this.authUser = mock(User.class);
		this.originalSecurityContext = SecurityContextHolder.getContext();
	}

	/**
	 * Cleans up the test environment after each test method execution.
	 * Restores the original security context to avoid test contamination.
	 */
	@AfterEach
	void tearDown() {
		SecurityContextHolder.setContext(originalSecurityContext);
	}

	/**
	 * Mocks a successful authentication scenario by setting up Spring Security context
	 * and configuring the account service to return the authenticated user.
	 */
	private void mockSuccessfulAuthentication() {
		final Authentication authentication = mock(Authentication.class);
		final SecurityContext securityContext = mock(SecurityContext.class);

		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(authentication.getName()).thenReturn(USER_UUID);
		SecurityContextHolder.setContext(securityContext);

		when(this.accountService.findByLsUuid(USER_UUID)).thenReturn(this.authUser);

		when(this.authUser.hasSimpleRole()).thenReturn(true);
		when(this.authUser.isGuest()).thenReturn(false);
		lenient().when(this.authUser.getLsUuid()).thenReturn(USER_UUID);
		when(this.authUser.getAccountRepresentation()).thenReturn(AUTH_USER_EMAIL);
	}

	/**
	 * Tests that the ShareContainer is correctly configured from a ShareCreationDto
	 * with various parameter combinations including mailing lists and recipients.
	 *
	 * @param scenario the test scenario description
	 * @param mailingListUuids the set of mailing list UUIDs (may be null)
	 * @param recipients the list of recipient DTOs
	 * @param documentUuids the list of document UUIDs to share
	 * @param subject the share subject
	 * @param message the share message
	 * @param secured whether the share is secured
	 * @param creationAcknowledgement whether creation acknowledgement is enabled
	 * @param forceAnonymousSharing whether to force anonymous sharing
	 * @param expirationDate the share expiration date
	 * @param enableUSDA whether USDA is enabled
	 * @param notificationDateForUSDA the USDA notification date
	 * @param sharingNote the sharing note
	 * @param inReplyTo the in-reply-to reference
	 * @param references the references
	 * @param externalMailLocale the external mail locale
	 * @param expectedAccountContactListsCount the expected number of account contact lists
	 * @param expectedExplicitRecipientEmails the expected recipient emails
	 * @param expectedDocumentCount the expected document count
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("shareContainerConfigurationScenarios")
	void testCreate_ShareContainerCorrectlyConfiguredFromDto(
			@Nonnull final String scenario,
			@Nullable final Set<String> mailingListUuids,
			@Nonnull final List<GenericUserDto> recipients,
			@Nonnull final List<String> documentUuids,
			@Nonnull final String subject,
			@Nonnull final String message,
			boolean secured,
			boolean creationAcknowledgement,
			boolean forceAnonymousSharing,
			@Nonnull final Date expirationDate,
			boolean enableUSDA,
			@Nonnull final Date notificationDateForUSDA,
			@Nonnull final String sharingNote,
			@Nonnull final String inReplyTo,
			@Nonnull final String references,
			@Nonnull final Language externalMailLocale,
			int expectedAccountContactListsCount,
			@Nonnull final Set<String> expectedExplicitRecipientEmails,
			int expectedDocumentCount) {
		this.mockSuccessfulAuthentication();
		final ShareCreationDto createDto = this.createShareCreationDto(
				mailingListUuids, recipients, documentUuids, subject, message,
				secured, creationAcknowledgement, forceAnonymousSharing, expirationDate,
				enableUSDA, notificationDateForUSDA, sharingNote, inReplyTo, references, externalMailLocale
		);

		if (mailingListUuids != null && !mailingListUuids.isEmpty()) {
			mailingListUuids.forEach(uuid -> {
				final ContactList contactList = mock(ContactList.class);
				final AccountContactLists accountContactList = mock(AccountContactLists.class);
				final List<ContactListContact> contacts = Arrays.asList(
						mock(ContactListContact.class), mock(ContactListContact.class)
				);
				when(contactList.getUuid()).thenReturn(uuid);
				lenient().when(accountContactList.getContactList()).thenReturn(contactList);
				lenient().when(accountContactList.getContactList().getUuid()).thenReturn("account-contact-list-" + uuid);
				contacts.forEach(contact -> {
					lenient().when(contact.getMail()).thenReturn("contact@" + uuid + ".com");
					lenient().when(contact.getUuid()).thenReturn("contact-uuid-" + uuid);
				});

				when(this.listService.findByUuid(eq(USER_UUID), eq(uuid))).thenReturn(contactList);
				when(this.accountService.findAccountContactListByAccountAndContactList(eq(this.authUser), eq(contactList)))
						.thenReturn(Optional.of(accountContactList));
				when(this.mailingListContactRepository.findAllContacts(contactList))
						.thenReturn(contacts);
			});
		}
		final Set<Entry> mockShares = new HashSet<>();
		final Entry shareEntry = this.createMockShareEntry();
		mockShares.add(shareEntry);

		when(this.shareService.create(eq(this.authUser), eq(this.authUser), any(ShareContainer.class)))
				.thenReturn(mockShares);
		final Set<ShareDto> result = shareFacadeService.create(createDto);
		final ArgumentCaptor<ShareContainer> shareContainerCaptor = ArgumentCaptor.forClass(ShareContainer.class);
		verify(this.shareService).create(eq(this.authUser), eq(this.authUser), shareContainerCaptor.capture());
		final ShareContainer actualShareContainer = shareContainerCaptor.getValue();
		assertThat(actualShareContainer.getSubject()).isEqualTo(subject);
		assertThat(actualShareContainer.getMessage()).isEqualTo(message);
		assertThat(actualShareContainer.getSecured()).isEqualTo(secured);
		assertThat(actualShareContainer.getDocumentUuids())
				.containsExactlyInAnyOrderElementsOf(documentUuids);
		assertThat(actualShareContainer.getAccountContactLists())
				.hasSize(expectedAccountContactListsCount);
		assertThat(actualShareContainer.getExplicitRecipientEmails())
				.containsExactlyInAnyOrderElementsOf(expectedExplicitRecipientEmails);
		assertThat(result).isNotEmpty();
	}

	/**
	 * Creates a mocked ShareEntry with all necessary attributes configured
	 * to avoid NullPointerExceptions during ShareDto construction.
	 *
	 * @return a fully configured mocked ShareEntry
	 */
	private @Nonnull Entry createMockShareEntry() {
		final ShareEntry shareEntry = mock(ShareEntry.class);
		final Calendar creationDate = Calendar.getInstance();
		final Calendar modificationDate = Calendar.getInstance();

		when(shareEntry.getUuid()).thenReturn(SHARE_ENTRY_UUID );
		when(shareEntry.getName()).thenReturn(SHARE_NAME);
		when(shareEntry.getCreationDate()).thenReturn(creationDate);
		when(shareEntry.getModificationDate()).thenReturn(modificationDate);
		when(shareEntry.getComment()).thenReturn(TEST_COMMENT);
		when(shareEntry.getExpirationDate()).thenReturn(null);
		when(shareEntry.getEntryType()).thenReturn(EntryType.SHARE);
		when(shareEntry.getDownloaded()).thenReturn(DOWNLOAD_COUNT);

		final DocumentEntry documentEntry = mock(DocumentEntry.class);
		when(documentEntry.getSize()).thenReturn(DOCUMENT_SIZE);
		lenient().when(documentEntry.getType()).thenReturn(DOCUMENT_TYPE_PDF);
		lenient().when(documentEntry.getHumanMimeType()).thenReturn(HUMAN_MIME_TYPE_PDF);
		when(documentEntry.getCiphered()).thenReturn(CIPHERED_FALSE);
		when(documentEntry.isHasThumbnail()).thenReturn(HAS_THUMBNAIL_TRUE);
		when(documentEntry.getCreationDate()).thenReturn(creationDate);
		when(documentEntry.getModificationDate()).thenReturn(modificationDate);
		when(shareEntry.getDocumentEntry()).thenReturn(documentEntry);

		final User recipient = this.createMockUser("recipient-uuid", RECIPIENT_EMAIL, FIRST_NAME_RECIPIENT, LAST_NAME_RECIPIENT);
		when(shareEntry.getRecipient()).thenReturn(recipient);

		final User entryOwner = this.createMockUser("owner-uuid", OWNER_EMAIL, FIRST_NAME_OWNER, LAST_NAME_OWNER);
		lenient().when(shareEntry.getEntryOwner()).thenReturn(entryOwner);
		return shareEntry;
	}

	/**
	 * Tests that appropriate BusinessException is thrown when mailing lists are not found.
	 *
	 * @param scenario the test scenario description
	 * @param mailingListUuids the set of mailing list UUIDs that should trigger not found errors
	 */
	@ParameterizedTest(name = "Mailing list error: {0}")
	@MethodSource("mailingListErrorScenarios")
	void testCreate_MailingListErrorScenarios(
			@Nonnull final String scenario,
			@Nullable final Set<String> mailingListUuids) {
		mockSuccessfulAuthentication();
		final ShareCreationDto createDto = new ShareCreationDto();
		createDto.setMailingListUuid(mailingListUuids);
		createDto.setRecipients(Arrays.asList(
				createGenericUserDto(TEST_USER_EMAIL, FIRST_NAME_TEST, LAST_NAME_TEST)
		));
		createDto.setDocuments(Arrays.asList(DOCUMENT_ENTRY_UUID_1));

		if (mailingListUuids != null && !mailingListUuids.isEmpty()) {
			final String firstUuid = mailingListUuids.iterator().next();
			final ContactList contactList = mock(ContactList.class);

			when(this.listService.findByUuid(eq(USER_UUID), eq(firstUuid))).thenReturn(contactList);
			when(this.accountService.findAccountContactListByAccountAndContactList(eq(this.authUser), eq(contactList)))
					.thenReturn(Optional.empty());
		}

		assertThatThrownBy(() -> this.shareFacadeService.create(createDto))
				.isInstanceOf(BusinessException.class)
				.extracting(ex -> ((BusinessException) ex).getErrorCode())
				.isEqualTo(BusinessErrorCode.ACCOUNT_CONTACT_LIST_NOT_FOUND);
	}

	/**
	 * Tests authentication failure scenarios to ensure proper BusinessException is thrown.
	 *
	 * @param scenario the test scenario description
	 * @param authenticationSetup runnable that sets up the specific authentication failure
	 */
	@ParameterizedTest(name = "Authentication failure: {0}")
	@MethodSource("authenticationFailureScenarios")
	void testCreate_AuthenticationFailure(String scenario, Runnable authenticationSetup) {
		authenticationSetup.run();
		final ShareCreationDto createDto = new ShareCreationDto();
		createDto.setRecipients(Arrays.asList(
				createGenericUserDto(TEST_USER_EMAIL, FIRST_NAME_TEST, LAST_NAME_TEST)
		));
		createDto.setDocuments(Arrays.asList(DOCUMENT_ENTRY_UUID_1));

		assertThatThrownBy(() -> this.shareFacadeService.create(createDto))
				.isInstanceOf(BusinessException.class)
				.extracting(ex -> ((BusinessException) ex).getErrorCode())
				.isEqualTo(BusinessErrorCode.WEBSERVICE_FORBIDDEN);
	}

	/**
	 * Creates a ShareCreationDto with the specified parameters.
	 *
	 * @param mailingListUuids the set of mailing list UUIDs (may be null)
	 * @param recipients the list of recipient DTOs
	 * @param documentUuids the list of document UUIDs to share
	 * @param subject the share subject
	 * @param message the share message
	 * @param secured whether the share is secured
	 * @param creationAcknowledgement whether creation acknowledgement is enabled
	 * @param forceAnonymousSharing whether to force anonymous sharing
	 * @param expirationDate the share expiration date (may be null)
	 * @param enableUSDA whether USDA is enabled
	 * @param notificationDateForUSDA the USDA notification date (may be null)
	 * @param sharingNote the sharing note (may be null)
	 * @param inReplyTo the in-reply-to reference (may be null)
	 * @param references the references (may be null)
	 * @param externalMailLocale the external mail locale
	 * @return a fully configured ShareCreationDto
	 */
	private @Nonnull ShareCreationDto createShareCreationDto(
			@Nullable final Set<String> mailingListUuids,
			@Nonnull final List<GenericUserDto> recipients,
			@Nonnull final List<String> documentUuids,
			@Nonnull final String subject,
			@Nonnull final String message,
			boolean secured,
			boolean creationAcknowledgement,
			boolean forceAnonymousSharing,
			Date expirationDate,
			boolean enableUSDA,
			Date notificationDateForUSDA,
			@Nonnull final String sharingNote,
			@Nonnull final String inReplyTo,
			@Nonnull final String references,
			Language externalMailLocale) {

		final ShareCreationDto dto = new ShareCreationDto();
		dto.setMailingListUuid(mailingListUuids);
		dto.setRecipients(recipients);
		dto.setDocuments(documentUuids);
		dto.setSubject(subject);
		dto.setMessage(message);
		dto.setSecured(secured);
		dto.setCreationAcknowledgement(creationAcknowledgement);
		dto.setForceAnonymousSharing(forceAnonymousSharing);
		dto.setExpirationDate(expirationDate);
		dto.setEnableUSDA(enableUSDA);
		dto.setNotificationDateForUSDA(notificationDateForUSDA);
		dto.setSharingNote(sharingNote);
		dto.setInReplyTo(inReplyTo);
		dto.setReferences(references);
		dto.setExternalMailLocale(externalMailLocale);

		return dto;
	}
}