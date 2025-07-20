package org.linagora.linshare.core.facade.webservice.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.EntryBusinessService;
import org.linagora.linshare.core.business.service.MailingListBusinessService;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.EntryType;
import org.linagora.linshare.core.domain.entities.*;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ShareDto;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.linagora.linshare.core.facade.webservice.user.impl.DocumentFacadeImpl;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.MimePolicyService;
import org.linagora.linshare.core.service.ShareService;
import org.linagora.linshare.core.service.SignatureService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.UploadRequestEntryService;
import org.linagora.linshare.core.service.WorkGroupDocumentRevisionService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.utils.Version;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.annotation.Nonnull;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class DocumentFacadeImplTest {

    @Mock
    private DocumentEntryService documentEntryService;

    @Mock
    private AccountService accountService;

    @Mock
    private MimePolicyService mimePolicyService;

    @Mock
    private ShareService shareService;

    @Mock
    private EntryBusinessService entryBusinessService;

    @Mock
    private AuditLogEntryService auditLogEntryService;

    @Mock
    private ThreadService threadService;

    @Mock
    private WorkGroupNodeService workGroupNodeService;

    @Mock
    private SignatureService signatureService;

    @Mock
    private WorkGroupDocumentRevisionService revisionService;

    @Mock
    private UploadRequestEntryService requestEntryService;

    @Mock
    private MailingListBusinessService mailingListBusinessService;
    private TestDocumentFacade documentFacade;

    private User authUser;
    private DocumentEntry documentEntry;
    private String documentUuid;
    private Version version;

    @BeforeEach
    void setUp() {
        documentUuid = "test-document-uuid";
        version = Version.V2;
        authUser = mock(User.class);
        lenient().when(authUser.isGuest()).thenReturn(false);
        lenient().when(authUser.getLsUuid()).thenReturn("auth-user-uuid");
        documentEntry = mock(DocumentEntry.class);
        when(documentEntry.getUuid()).thenReturn(documentUuid);
        when(documentEntry.getName()).thenReturn("test-document.pdf");
        when(documentEntry.getType()).thenReturn("document");
        when(documentEntry.getSize()).thenReturn(1024L);
        when(documentEntry.getCreationDate()).thenReturn(Calendar.getInstance());
        when(documentEntry.getModificationDate()).thenReturn(Calendar.getInstance());
        when(documentEntry.getCiphered()).thenReturn(false);
        when(documentEntry.getSha256sum()).thenReturn("test-sha256");
        documentFacade = new TestDocumentFacade(
                accountService,
                documentEntryService,
                entryBusinessService,
                auditLogEntryService,
                shareService,
                mimePolicyService,
                threadService,
                workGroupNodeService,
                signatureService,
                revisionService,
                requestEntryService,
                mailingListBusinessService,
                authUser
        );
        when(documentEntryService.find(authUser, authUser, documentUuid)).thenReturn(documentEntry);
    }

    /**
     * Test retrieving a document without requesting shares.
     * Verifies that the returned DocumentDto is valid and contains an empty share list.
     */
    @Test
    void findWithoutShares_ShouldReturnDocumentDtoWithoutShares() throws BusinessException {
        when(documentEntry.getType()).thenReturn("document");
        when(documentEntry.getSize()).thenReturn(1024L);
        when(documentEntry.getCreationDate()).thenReturn(Calendar.getInstance());
        when(documentEntry.getModificationDate()).thenReturn(Calendar.getInstance());
        when(documentEntry.getCiphered()).thenReturn(false);
        when(documentEntry.getSha256sum()).thenReturn("test-sha256");
        DocumentDto result = documentFacade.find(version, documentUuid, false);
        assertNotNull(result);
        assertNotNull(result.getShares());
        assertTrue(result.getShares().isEmpty());
        verify(documentEntryService).find(authUser, authUser, documentUuid);
        verifyNoInteractions(entryBusinessService);
    }

    /**
     * Test retrieving a document with shares enabled but no existing shares.
     * Ensures the shares list is empty but properly initialized.
     */
    @Test
    void findWithShares_NoShares_ShouldReturnDocumentDtoWithEmptyShares() throws BusinessException {
        when(entryBusinessService.findAllMyAnonymousShareEntries(authUser, documentEntry))
                .thenReturn(new ArrayList<>());
        when(entryBusinessService.findAllMyShareEntries(authUser, documentEntry))
                .thenReturn(new ArrayList<>());
        DocumentDto result = documentFacade.find(version, documentUuid, true);
        assertNotNull(result);
        assertNotNull(result.getShares());
        assertTrue(result.getShares().isEmpty());

        verify(entryBusinessService).findAllMyAnonymousShareEntries(authUser, documentEntry);
        verify(entryBusinessService).findAllMyShareEntries(authUser, documentEntry);
    }

    private User createMockUser(String uuid, String mail, String firstName, String lastName, AccountType accountType) {
        User user = mock(User.class);
        AbstractDomain domain = mock(AbstractDomain.class);
        when(user.getLsUuid()).thenReturn(uuid);
        when(user.getMail()).thenReturn(mail);
        when(user.getFirstName()).thenReturn(firstName);
        when(user.getLastName()).thenReturn(lastName);
        when(user.getAccountType()).thenReturn(accountType);
        when(user.getDomain()).thenReturn(domain);
        when(domain.getUuid()).thenReturn("domain-" + uuid);
        return user;
    }

    /**
     * Test sharing behavior for a regular user who is allowed to view contact list members.
     * Verifies that contact list resolution works and no visibility filtering is applied.
     */
    @Test
    void findWithShares_RegularUserWithContactList_CanViewTrue_ShouldShowContactListMembers() throws BusinessException {
        String contactListUuid = "contact-list-uuid";
        String contactListName = "Test Contact List";
        ShareEntry shareEntry = createMockShareEntry(contactListUuid);
        ContactList contactList = mock(ContactList.class);
        when(contactList.getIdentifier()).thenReturn(contactListName);
        when(entryBusinessService.findAllMyAnonymousShareEntries(authUser, documentEntry))
                .thenReturn(new ArrayList<>());
        when(entryBusinessService.findAllMyShareEntries(authUser, documentEntry))
                .thenReturn(Arrays.asList(shareEntry));
        when(mailingListBusinessService.findByUuid(contactListUuid)).thenReturn(contactList);
        when(authUser.isGuest()).thenReturn(false);
        DocumentDto result = documentFacade.find(version, documentUuid, true);
        assertNotNull(result);
        assertNotNull(result.getShares());
        assertEquals(1, result.getShares().size());
        verify(mailingListBusinessService).findByUuid(contactListUuid);
        verify(accountService, never()).findAccountContactListByAccountAndContactList(any(), any());
    }

    /**
     * Test sharing behavior for a guest user who has permission to view contact list members.
     * Ensures that recipient details are fully shown in the share.
     */
    @Test
    void findWithShares_GuestUserWithContactList_CanViewTrue_ShouldShowContactListMembers() throws BusinessException {
        String contactListUuid = "contact-list-uuid";
        String contactListName = "Test Contact List";
        ContactList contactList = mock(ContactList.class);
        when(contactList.getUuid()).thenReturn(contactListUuid);
        when(contactList.getIdentifier()).thenReturn(contactListName);
        ShareEntry shareEntry = createMockShareEntry(contactListUuid);
        AccountContactLists accountContactLists = mock(AccountContactLists.class);
        when(accountContactLists.getCanViewContactListMembers()).thenReturn(true);
        when(entryBusinessService.findAllMyAnonymousShareEntries(authUser, documentEntry))
                .thenReturn(new ArrayList<>());
        when(entryBusinessService.findAllMyShareEntries(authUser, documentEntry))
                .thenReturn(Arrays.asList(shareEntry));
        when(mailingListBusinessService.findByUuid(contactListUuid)).thenReturn(contactList);
        when(authUser.isGuest()).thenReturn(true);
        when(accountService.findAccountContactListByAccountAndContactList(authUser, contactList))
                .thenReturn(Optional.of(accountContactLists));
        DocumentDto result = documentFacade.find(version, documentUuid, true);
        assertNotNull(result);
        assertNotNull(result.getShares());
        assertEquals(1, result.getShares().size());

        ShareDto shareDto = result.getShares().get(0);
        assertNotNull(shareDto);

        assertNotNull(shareDto.getRecipient());
        assertEquals("John", shareDto.getRecipient().getFirstName());
        assertEquals("Doe", shareDto.getRecipient().getLastName());

        verify(mailingListBusinessService).findByUuid(contactListUuid);
        verify(accountService).findAccountContactListByAccountAndContactList(authUser, contactList);
    }

    /**
     * Test guest user behavior when they are not allowed to view contact list members.
     * Ensures that recipient details are hidden and the contact list name is displayed instead.
     */
    @Test
    void findWithShares_GuestUserWithContactList_CanViewFalse_ShouldHideRecipientDetails() throws BusinessException {
        String contactListUuid = "contact-list-uuid";
        String contactListName = "Test Contact List";

        ShareEntry shareEntry = createMockShareEntry(contactListUuid);
        ContactList contactList = mock(ContactList.class);
        when(contactList.getIdentifier()).thenReturn(contactListName);

        AccountContactLists accountContactLists = mock(AccountContactLists.class);
        when(accountContactLists.getCanViewContactListMembers()).thenReturn(false);

        when(entryBusinessService.findAllMyAnonymousShareEntries(authUser, documentEntry))
                .thenReturn(new ArrayList<>());
        when(entryBusinessService.findAllMyShareEntries(authUser, documentEntry))
                .thenReturn(Arrays.asList(shareEntry));
        when(mailingListBusinessService.findByUuid(contactListUuid)).thenReturn(contactList);
        when(authUser.isGuest()).thenReturn(true);
        when(accountService.findAccountContactListByAccountAndContactList(authUser, contactList))
                .thenReturn(Optional.of(accountContactLists));

        DocumentDto result = documentFacade.find(version, documentUuid, true);
        assertNotNull(result);
        assertNotNull(result.getShares());
        assertEquals(1, result.getShares().size());

        ShareDto shareDto = result.getShares().get(0);
        assertTrue(shareDto.getHideRecipientDetails());
        assertEquals(contactListName, shareDto.getContactListName());
        assertEquals("", shareDto.getRecipient().getFirstName());
        assertEquals("", shareDto.getRecipient().getLastName());
        assertEquals("", shareDto.getRecipient().getMail());

        verify(mailingListBusinessService).findByUuid(contactListUuid);
        verify(accountService).findAccountContactListByAccountAndContactList(authUser, contactList);
    }

    /**
     * Test guest user behavior when no AccountContactLists record is found.
     * Ensures recipient information is hidden by default due to unknown visibility.
     */
    @Test
    void findWithShares_GuestUserWithContactList_NoAccountContactList_ShouldHideRecipientDetails() throws BusinessException {
        String contactListUuid = "contact-list-uuid";
        String contactListName = "Test Contact List";

        ShareEntry shareEntry = createMockShareEntry(contactListUuid);
        ContactList contactList = mock(ContactList.class);
        when(contactList.getIdentifier()).thenReturn(contactListName);

        when(entryBusinessService.findAllMyAnonymousShareEntries(authUser, documentEntry))
                .thenReturn(new ArrayList<>());
        when(entryBusinessService.findAllMyShareEntries(authUser, documentEntry))
                .thenReturn(Arrays.asList(shareEntry));
        when(mailingListBusinessService.findByUuid(contactListUuid)).thenReturn(contactList);
        when(authUser.isGuest()).thenReturn(true);
        when(accountService.findAccountContactListByAccountAndContactList(authUser, contactList))
                .thenReturn(Optional.empty());
        DocumentDto result = documentFacade.find(version, documentUuid, true);
        assertNotNull(result);
        assertNotNull(result.getShares());
        assertEquals(1, result.getShares().size());

        ShareDto shareDto = result.getShares().get(0);
        assertTrue(shareDto.getHideRecipientDetails());
        assertEquals(contactListName, shareDto.getContactListName());

        verify(mailingListBusinessService).findByUuid(contactListUuid);
        verify(accountService).findAccountContactListByAccountAndContactList(authUser, contactList);
    }

    /**
     * Test behavior when the contact list used in a share has been deleted.
     * Verifies that the contact list name is retrieved from audit logs and recipient details are hidden.
     */
    @Test
    void findWithShares_DeletedContactList_ShouldUseAuditLogName() throws BusinessException {
        String contactListUuid = "deleted-contact-list-uuid";
        String auditLogName = "Deleted Contact List Name";

        ShareEntry shareEntry = createMockShareEntry(contactListUuid);
        BusinessException deletedException = new BusinessException(BusinessErrorCode.LIST_DO_NOT_EXIST, "List not found");

        when(entryBusinessService.findAllMyAnonymousShareEntries(authUser, documentEntry))
                .thenReturn(new ArrayList<>());
        when(entryBusinessService.findAllMyShareEntries(authUser, documentEntry))
                .thenReturn(Arrays.asList(shareEntry));
        when(mailingListBusinessService.findByUuid(contactListUuid)).thenThrow(deletedException);
        when(auditLogEntryService.findLastDeletedContactListName(contactListUuid))
                .thenReturn(Optional.of(auditLogName));

        DocumentDto result = documentFacade.find(version, documentUuid, true);
        assertNotNull(result);
        assertNotNull(result.getShares());
        assertEquals(1, result.getShares().size());

        ShareDto shareDto = result.getShares().get(0);
        assertTrue(shareDto.getHideRecipientDetails());
        assertEquals(auditLogName, shareDto.getContactListName());
        assertEquals("", shareDto.getRecipient().getFirstName());
        assertEquals("", shareDto.getRecipient().getLastName());
        assertEquals("", shareDto.getRecipient().getMail());

        verify(mailingListBusinessService).findByUuid(contactListUuid);
        verify(auditLogEntryService).findLastDeletedContactListName(contactListUuid);
    }

    /**
     * Tests that a default name is used when the contact list is deleted
     * and no audit log entry is available for its name.
     */
    @Test
    void findWithShares_DeletedContactList_NoAuditLogName_ShouldUseDefaultName() throws BusinessException {
        String contactListUuid = "deleted-contact-list-uuid";

        ShareEntry shareEntry = createMockShareEntry(contactListUuid);

        BusinessException deletedException = new BusinessException(BusinessErrorCode.LIST_DO_NOT_EXIST, "List not found");

        when(entryBusinessService.findAllMyAnonymousShareEntries(authUser, documentEntry))
                .thenReturn(new ArrayList<>());
        when(entryBusinessService.findAllMyShareEntries(authUser, documentEntry))
                .thenReturn(Arrays.asList(shareEntry));
        when(mailingListBusinessService.findByUuid(contactListUuid)).thenThrow(deletedException);
        when(auditLogEntryService.findLastDeletedContactListName(contactListUuid))
                .thenReturn(Optional.empty());

        DocumentDto result = documentFacade.find(version, documentUuid, true);
        assertNotNull(result);
        assertNotNull(result.getShares());
        assertEquals(1, result.getShares().size());

        ShareDto shareDto = result.getShares().get(0);
        assertTrue(shareDto.getHideRecipientDetails());
        assertEquals("Deleted List", shareDto.getContactListName());

        verify(mailingListBusinessService).findByUuid(contactListUuid);
        verify(auditLogEntryService).findLastDeletedContactListName(contactListUuid);
    }

    /**
     * Tests that an unexpected BusinessException during contact list lookup
     * does not crash the method and is logged as a warning.
     */
    @Test
    void findWithShares_OtherBusinessException_ShouldLogWarningAndContinue() throws BusinessException {
        String contactListUuid = "contact-list-uuid";

        ShareEntry shareEntry = createMockShareEntry(contactListUuid);

        BusinessException otherException = new BusinessException(BusinessErrorCode.WEBSERVICE_FORBIDDEN, "Other error");

        when(entryBusinessService.findAllMyAnonymousShareEntries(authUser, documentEntry))
                .thenReturn(new ArrayList<>());
        when(entryBusinessService.findAllMyShareEntries(authUser, documentEntry))
                .thenReturn(Arrays.asList(shareEntry));
        when(mailingListBusinessService.findByUuid(contactListUuid)).thenThrow(otherException);
        DocumentDto result = documentFacade.find(version, documentUuid, true);
        assertNotNull(result);
        assertNotNull(result.getShares());
        assertEquals(1, result.getShares().size());

        verify(mailingListBusinessService).findByUuid(contactListUuid);
        verify(auditLogEntryService, never()).findLastDeletedContactListName(any());
    }

    /**
     * Tests that a ShareDto is correctly returned without contact list
     * when the share has no associated contactListUuid.
     */
    @Test
    void findWithShares_ShareWithoutContactList_ShouldUseRegularShareDto() throws BusinessException {
        ShareEntry shareEntry = createMockShareEntry(null);
        when(entryBusinessService.findAllMyAnonymousShareEntries(authUser, documentEntry))
                .thenReturn(new ArrayList<>());
        when(entryBusinessService.findAllMyShareEntries(authUser, documentEntry))
                .thenReturn(Arrays.asList(shareEntry));
        DocumentDto result = documentFacade.find(version, documentUuid, true);
        assertNotNull(result);
        assertNotNull(result.getShares());
        assertEquals(1, result.getShares().size());

        verify(mailingListBusinessService, never()).findByUuid(any());
        verify(auditLogEntryService, never()).findLastDeletedContactListName(any());
    }

    private ShareEntry createMockShareEntry(String contactListUuid) {
        ShareEntry shareEntry = mock(ShareEntry.class);
        User recipientUser = createMockUser(
                "recipient-uuid",
                "recipient@example.com",
                "John",
                "Doe",
                AccountType.INTERNAL
        );
        when(shareEntry.getUuid()).thenReturn("share-uuid");
        when(shareEntry.getName()).thenReturn("share-name");
        when(shareEntry.getRecipient()).thenReturn(recipientUser);
        when(shareEntry.getComment()).thenReturn("share-comment");
        when(shareEntry.getDownloaded()).thenReturn(0L);
        when(shareEntry.getEntryType()).thenReturn(EntryType.SHARE);
        Calendar expirationCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        when(shareEntry.getCreationDate()).thenReturn(calendar);
        when(shareEntry.getModificationDate()).thenReturn(calendar);

        expirationCalendar.add(Calendar.DAY_OF_MONTH, 30);
        when(shareEntry.getExpirationDate()).thenReturn(expirationCalendar);
        when(shareEntry.getDocumentEntry()).thenReturn(documentEntry);
        when(shareEntry.getContactListUuid()).thenReturn(contactListUuid);
        return shareEntry;
    }

    private static class TestDocumentFacade extends DocumentFacadeImpl {

        private final User testAuthUser;

        public TestDocumentFacade(
                AccountService accountService,
                DocumentEntryService documentEntryService,
                EntryBusinessService entryBusinessService,
                AuditLogEntryService auditLogEntryService,
                ShareService shareService,
                MimePolicyService mimePolicyService,
                ThreadService threadService,
                WorkGroupNodeService workGroupNodeService,
                SignatureService signatureService,
                WorkGroupDocumentRevisionService revisionService,
                UploadRequestEntryService requestEntryService,
                MailingListBusinessService mailingListBusinessService,
                User testAuthUser) {

            super(documentEntryService, accountService, mimePolicyService, shareService,entryBusinessService, auditLogEntryService,
                    threadService, workGroupNodeService,
                    signatureService, revisionService, requestEntryService, mailingListBusinessService);

            this.testAuthUser = testAuthUser;
        }

        @Override
        @Nonnull
        protected User checkAuthentication() {
            return testAuthUser;
        }

        @Override
        @Nonnull
        protected User getActor(@Nonnull Account authUser, @Nonnull String actorUuid) {
            return (User) authUser;
        }
    }
}