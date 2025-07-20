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
package org.linagora.linshare.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.ShareEntryBusinessService;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.TimeUnit;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.ContactListContact;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.GuestDomain;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.Policy;
import org.linagora.linshare.core.domain.entities.RecipientFavourite;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.domain.entities.TimeUnitClass;
import org.linagora.linshare.core.domain.entities.TopDomain;
import org.linagora.linshare.core.domain.entities.UnitValueFunctionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.rac.ShareEntryResourceAccessControl;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.FavouriteRepository;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.impl.ShareEntryServiceImpl;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.logs.ShareEntryAuditLogEntry;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({ SpringExtension.class, MockitoExtension.class })
@ContextConfiguration(locations = {
        "classpath:springContext-datasource.xml",
        "classpath:springContext-repository.xml",
        "classpath:springContext-dao.xml",
        "classpath:springContext-ldap.xml",
        "classpath:springContext-business-service.xml",
        "classpath:springContext-service-miscellaneous.xml",
        "classpath:springContext-service.xml",
        "classpath:springContext-rac.xml",
        "classpath:springContext-mongo.xml",
        "classpath:springContext-mongo-init.xml",
        "classpath:springContext-storage-jcloud.xml",
        "classpath:springContext-test.xml" })
public class ShareEntryServiceImplTest {

    private static final String TOP_DOMAIN = "top-domain";
    private static final String GUEST_DOMAIN = "guest-domain";

    @Mock
    private AbstractDomainRepository abstractDomainRepository;

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private FunctionalityReadOnlyService functionalityService;

    @Mock
    private ShareEntryBusinessService shareEntryBusinessService;

    @Mock
    private LogEntryService logEntryService;

    @Mock
    private DocumentEntryBusinessService documentEntryBusinessService;

    @Mock
    private NotifierService notifierService;

    @Mock
    private MailBuildingService mailBuildingService;

    @Mock
    private FavouriteRepository<String, Internal, RecipientFavourite> recipientFavouriteRepository;

    @Mock
    private ShareEntryResourceAccessControl rac;

    @Mock
    private SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService;

    @InjectMocks
    private ShareEntryServiceImpl shareEntryService;

    private TopDomain topDomain;
    private GuestDomain domainGuest;
    private Internal owner;
    private Internal recipient;
    private Guest guest;
    private DocumentEntry documentEntry;
    private ShareEntryGroup shareEntryGroup;
    private ContactList contactList;
    private ContactListContact contactListContact;

    @BeforeEach
    public void setUp() throws IOException {
        this.topDomain = new TopDomain(TOP_DOMAIN);
        this.topDomain.setUuid(TOP_DOMAIN);
        this.domainGuest = new GuestDomain(GUEST_DOMAIN);
        this.domainGuest.setUuid(GUEST_DOMAIN);
        this.owner = new Internal("Owner", "User", "owner@linshare.org", "owner-uid");
        this.owner.setLsUuid("owner-uuid");
        this.owner.setDomain(this.topDomain);

        this.recipient = new Internal("Recipient", "User", "recipient@linshare.org", "recipient-uid");
        this.recipient.setLsUuid("recipient-uuid");
        this.recipient.setDomain(this.topDomain);

        this.guest = new Guest("guest", "Guest", "guest@linshare.org");
        this.guest.setLsUuid("guest-uuid");
        this.guest.setDomain(this.domainGuest);
        this.documentEntry = createTestDocumentEntry();
        this.shareEntryGroup = new ShareEntryGroup();
        this.shareEntryGroup.setUuid("share-entry-group-uuid");
        this.contactList = new ContactList();
        this.contactList.setUuid("contact-list-uuid");
        this.contactList.setIdentifier("Test Contact List");

        this.contactListContact = new ContactListContact();
        this.contactListContact.setMail("recipient@linshare.org");
        this.contactList.setContactListContacts(Collections.singleton(contactListContact));
        setupCommonMocks();
    }

    /**
     * Creates a temporary test DocumentEntry instance from a test file.
     * Used to simulate real document upload behavior.
     *
     * @return a prepared DocumentEntry object
     */
    private DocumentEntry createTestDocumentEntry() throws IOException {
        File tempFile = File.createTempFile("linshare-test-", ".tmp");
        tempFile.deleteOnExit();
        InputStream stream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("linshare-default.properties");
        IOUtils.copy(stream, new FileOutputStream(tempFile));

        Document document = new Document("test-file", "test.txt", "text/plain",
                Calendar.getInstance(), Calendar.getInstance(), owner, false, false, 1000L);
        document.setSha256sum("test-sha256");

        DocumentEntry docEntry = new DocumentEntry(owner, "test-file", document);
        docEntry.setUuid("doc-uuid");
        docEntry.setCreationDate(Calendar.getInstance());
        docEntry.setModificationDate(Calendar.getInstance());

        return docEntry;
    }

    /**
     * Prepares common mocks for domain, functionality, permissions, and mail generation
     * used across multiple tests.
     */
    private void setupCommonMocks() {
        UnitValueFunctionality unitValueFunctionality = new UnitValueFunctionality();

        Policy activationPolicy = new Policy();
        activationPolicy.setStatus(true);
        activationPolicy.setParentAllowUpdate(true);
        unitValueFunctionality.setActivationPolicy(activationPolicy);

        Policy configurationPolicy = new Policy();
        configurationPolicy.setStatus(true);
        configurationPolicy.setParentAllowUpdate(true);
        unitValueFunctionality.setConfigurationPolicy(configurationPolicy);

        unitValueFunctionality.setValueUsed(true);
        unitValueFunctionality.setUnit(new TimeUnitClass(TimeUnit.DAY));
        unitValueFunctionality.setValue(7);
        unitValueFunctionality.setMaxValueUsed(false);
        unitValueFunctionality.setUnlimited(false);
        unitValueFunctionality.setUnlimitedUsed(false);

        TimeUnitValueFunctionality timeUnitValueFunctionality = new TimeUnitValueFunctionality(unitValueFunctionality);
        lenient().when(abstractDomainRepository.findById(TOP_DOMAIN)).thenReturn(topDomain);
        lenient().when(abstractDomainRepository.findById(GUEST_DOMAIN)).thenReturn(domainGuest);
        lenient().when(functionalityService.getCollectedEmailsExpirationTimeFunctionality(any(AbstractDomain.class)))
                .thenReturn(timeUnitValueFunctionality);
        lenient().doNothing().when(rac).checkCreatePermission(any(), any(), any(), any(), any());
        lenient().when(mailBuildingService.build(any()))
                .thenReturn(new MailContainerWithRecipient(Language.ENGLISH));
    }

    /**
     * Tests share creation for a regular internal user (non-guest).
     * Verifies that the share is created, persisted, and audit logs are recorded.
     */
    @Test
    public void createShareWithRegularUser() throws BusinessException {
        ShareContainer shareContainer = new ShareContainer();
        shareContainer.addDocumentEntry(documentEntry);
        shareContainer.addShareRecipient(recipient);

        ShareEntry expectedShare = createMockShareEntry("share-uuid", documentEntry, owner, recipient);
        when(shareEntryBusinessService.create(
                eq(documentEntry), eq(owner), eq(recipient),
                isNull(), eq(shareEntryGroup), isNull()))
                .thenReturn(expectedShare);
        Set<ShareEntry> result = shareEntryService.create(owner, owner, shareContainer, shareEntryGroup);
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getUuid()).isEqualTo("share-uuid");

        verify(shareEntryBusinessService).create(
                eq(documentEntry), eq(owner), eq(recipient),
                isNull(), eq(shareEntryGroup), isNull());
        verify(logEntryService).insert(anyList());
    }

    /**
     * Tests share creation for a guest user.
     * Verifies guest expiration logic, share creation, guest update, and audit logging.
     */
    @Test
    public void createShareWithGuest() throws BusinessException {
        ShareContainer shareContainer = new ShareContainer();
        shareContainer.addDocumentEntry(documentEntry);
        shareContainer.addShareRecipient(guest);

        ShareEntry expectedShare = createMockShareEntry("share-uuid", documentEntry, owner, guest);
        TimeUnitValueFunctionality guestFunctionality = mock(TimeUnitValueFunctionality.class);
        when(guestFunctionality.toCalendarValue()).thenReturn(Calendar.DAY_OF_MONTH);
        when(guestFunctionality.getMaxValue()).thenReturn(30);

        when(functionalityService.getGuestsExpiration(any())).thenReturn(guestFunctionality);
        when(guestRepository.findByMail(anyString())).thenReturn(guest);
        when(shareEntryBusinessService.create(any(), any(), any(), any(), any(), any()))
                .thenReturn(expectedShare);
        Set<ShareEntry> result = shareEntryService.create(owner, owner, shareContainer, shareEntryGroup);
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);

        verify(shareEntryBusinessService).create(
                eq(documentEntry), eq(owner), eq(guest),
                eq(null), eq(shareEntryGroup), eq(null));
        verify(guestRepository).update(guest);
        verify(logEntryService).insert(anyList());
    }

    /**
     * Tests share creation for multiple recipients and multiple documents.
     * Verifies that all combinations of document and recipient produce distinct shares.
     */
    @Test
    public void createShareWithMultipleRecipientsAndDocuments() throws BusinessException {
        DocumentEntry documentEntry2 = createSecondTestDocumentEntry();
        Internal recipient2 = new Internal("recipient2", "user2", "recipient2@linshare.org", "recipient2-uid");
        recipient2.setLsUuid("recipient2-uuid");
        recipient2.setDomain(this.topDomain);

        ShareContainer shareContainer = new ShareContainer();
        shareContainer.addDocumentEntry(documentEntry);
        shareContainer.addDocumentEntry(documentEntry2);
        shareContainer.addShareRecipient(recipient);
        shareContainer.addShareRecipient(recipient2);
        ShareEntry share1 = createMockShareEntry("share-uuid-1", documentEntry, owner, recipient);
        ShareEntry share2 = createMockShareEntry("share-uuid-2", documentEntry2, owner, recipient);
        ShareEntry share3 = createMockShareEntry("share-uuid-3", documentEntry, owner, recipient2);
        ShareEntry share4 = createMockShareEntry("share-uuid-4", documentEntry2, owner, recipient2);

        when(shareEntryBusinessService.create(any(), any(), any(), any(), any(), any()))
                .thenReturn(share1, share2, share3, share4);
        Set<ShareEntry> result = shareEntryService.create(owner, owner, shareContainer, shareEntryGroup);
        assertThat(result).hasSize(4);

        verify(shareEntryBusinessService, times(4)).create(any(), any(), any(), any(), any(), any());
        verify(logEntryService).insert(anyList());
        verify(mailBuildingService, times(2)).build(any());
    }

    /**
     * Creates a second DocumentEntry for use in multi-document sharing tests.
     *
     * @return a new DocumentEntry instance with unique attributes
     */
    private DocumentEntry createSecondTestDocumentEntry() {
        Document document2 = new Document("test-file-2", "test2.txt", "text/plain",
                Calendar.getInstance(), Calendar.getInstance(), owner, false, false, 2000L);
        document2.setSha256sum("test-sha256-2");
        DocumentEntry docEntry2 = new DocumentEntry(owner, "test-file-2", document2);
        docEntry2.setUuid("doc-uuid-2");
        docEntry2.setCreationDate(Calendar.getInstance());
        docEntry2.setModificationDate(Calendar.getInstance());

        return docEntry2;
    }

    /**
     * Creates a mocked ShareEntry with basic metadata and associations.
     *
     * @param uuid       the UUID of the share
     * @param doc        the document being shared
     * @param owner      the account initiating the share
     * @param recipient  the recipient of the share
     * @return a mocked ShareEntry
     */
    private ShareEntry createMockShareEntry(String uuid, DocumentEntry doc, Account owner, User recipient) {
        ShareEntry share = mock(ShareEntry.class);
        when(share.getUuid()).thenReturn(uuid);
        when(share.getDocumentEntry()).thenReturn(doc);
        when(share.getEntryOwner()).thenReturn(owner);
        when(share.getRecipient()).thenReturn(recipient);
        when(share.getCreationDate()).thenReturn(Calendar.getInstance());
        when(share.getModificationDate()).thenReturn(Calendar.getInstance());
        when(share.getDownloaded()).thenReturn(0L);
        return share;
    }

    /**
     * Tests share creation when the recipient is part of a contact list.
     * Verifies that the contact list UUID is stored in the share and logged in the audit log.
     */
    @Test
    public void createShareWithContactList() throws BusinessException {
        ShareContainer shareContainer = new ShareContainer();
        shareContainer.addDocumentEntry(documentEntry);
        shareContainer.addShareRecipient(recipient);
        Set<ContactList> contactLists = Sets.newHashSet();
        contactLists.add(contactList);
        shareContainer.setContactLists(contactLists);

        ShareEntry expectedShare = createMockShareEntry("share-uuid", documentEntry, owner, recipient);
        when(shareEntryBusinessService.create(
                eq(documentEntry), eq(owner), eq(recipient),
                isNull(), eq(shareEntryGroup), isNull()))
                .thenReturn(expectedShare);
        ArgumentCaptor<List<AuditLogEntryUser>> logCaptor = ArgumentCaptor.forClass(List.class);
        ShareEntryAuditLogEntry auditLog = new ShareEntryAuditLogEntry();
        auditLog.setContactListUuid("contact-list-uuid");
        auditLog.setContactListName("Test Contact List");
        List<AuditLogEntryUser> mockedReturn = Collections.singletonList(auditLog);
        when(logEntryService.insert(logCaptor.capture())).thenReturn(mockedReturn);
        Set<ShareEntry> result = shareEntryService.create(owner, owner, shareContainer, shareEntryGroup);
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);

        ShareEntry createdShare = result.iterator().next();
        assertThat(createdShare.getUuid()).isEqualTo("share-uuid");
        verify(expectedShare).setContactListUuid("contact-list-uuid");
        verify(shareEntryBusinessService).create(
                eq(documentEntry), eq(owner), eq(recipient),
                isNull(), eq(shareEntryGroup), isNull());
        verify(logEntryService, times(1)).insert(anyList());
    }

    /**
     * Similar to the previous test, this validates diagnostic flow for contact list-based sharing.
     * Ensures the contact list UUID is assigned and persisted correctly with full logging.
     */
    @Test
    public void testCreateShareWithContactListDiagnostic() throws BusinessException {
        ShareContainer shareContainer = new ShareContainer();
        shareContainer.addDocumentEntry(documentEntry);
        shareContainer.addShareRecipient(recipient);
        Set<ContactList> contactLists = Sets.newHashSet();
        contactLists.add(contactList);
        shareContainer.setContactLists(contactLists);

        ShareEntry expectedShare = createMockShareEntry("share-uuid", documentEntry, owner, recipient);
        when(shareEntryBusinessService.create(any(), any(), any(), any(), any(), any()))
                .thenReturn(expectedShare);
        Set<ShareEntry> result = shareEntryService.create(owner, owner, shareContainer, shareEntryGroup);
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        verify(logEntryService, times(1)).insert(ArgumentMatchers.<List<AuditLogEntryUser>>any());
        verify(expectedShare).setContactListUuid("contact-list-uuid");

        verify(shareEntryBusinessService).create(
                eq(documentEntry), eq(owner), eq(recipient),
                isNull(), eq(shareEntryGroup), isNull());
    }
}