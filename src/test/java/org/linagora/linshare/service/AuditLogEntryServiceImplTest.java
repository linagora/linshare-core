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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Sets;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.MailingListBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountContactLists;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.GuestDomain;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.TopDomain;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.impl.AuditLogEntryServiceImpl;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.logs.ShareEntryAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.entities.mto.ShareEntryMto;
import org.linagora.linshare.mongo.repository.AuditUserMongoRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class AuditLogEntryServiceImplTest {

    private static final String TOP_DOMAIN = "top-domain";
    private static final String GUEST_DOMAIN = "guest-domain";

    @Mock
    private AuditUserMongoRepository userMongoRepository;

    @Mock
    private MailingListBusinessService mailingListBusinessService;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AuditLogEntryServiceImpl auditLogEntryService;

    private Account regularUser;
    private Account guest;
    private Account owner;
    private String entryUuid;
    private String contactListUuid;
    private TopDomain topDomain;
    private GuestDomain domainGuest;

    @BeforeEach
    public void setUp() {
        this.topDomain = new TopDomain(TOP_DOMAIN);
        this.topDomain.setUuid(TOP_DOMAIN);

        this.domainGuest = new GuestDomain(GUEST_DOMAIN);
        this.domainGuest.setUuid(GUEST_DOMAIN);

        owner = new Internal("Owner", "User", "owner@linshare.org", "owner-uid");
        owner.setLsUuid("owner-uuid");
        owner.setDomain(this.topDomain);

        this.guest = new Guest("guest", "Guest", "guest@linshare.org");
        this.guest.setLsUuid("guest-uuid");
        this.guest.setDomain(this.domainGuest);

        this.regularUser = new Internal("Recipient", "User", "recipient@linshare.org", "recipient-uid");
        this.regularUser.setLsUuid("recipient-uuid");
        this.regularUser.setDomain(this.topDomain);

        entryUuid = "document-entry-uuid";
        contactListUuid = "contact-list-uuid";
    }

    /**
     * Tests that a regular (non-guest) user can retrieve all audit logs
     * for a document entry without triggering contact list visibility rules.
     */
    @Test
    void findAllForRegularUser() {
        List<LogAction> actions = List.of(LogAction.CREATE, LogAction.DELETE);
        List<AuditLogEntryType> types = List.of(AuditLogEntryType.DOCUMENT_ENTRY);
        String beginDate = "2023-01-01T00:00:00.000Z";
        String endDate = "2023-01-31T23:59:59.999Z";
        ShareEntryAuditLogEntry log1 = createShareEntryAuditLog("log1", owner, entryUuid, null);
        ShareEntryAuditLogEntry log2 = createShareEntryAuditLog("log2", owner, entryUuid, contactListUuid);

        when(userMongoRepository.findDocumentHistoryForUser(
                eq(owner.getLsUuid()),
                eq(entryUuid),
                anyList(),
                anyList(),
                any(Sort.class)))
                .thenReturn(Sets.newHashSet(log1, log2));

        Set<AuditLogEntryUser> result = auditLogEntryService.findAll(
                regularUser, owner, entryUuid, actions, types, beginDate, endDate);

        assertThat(result).hasSize(2);
        verify(userMongoRepository).findDocumentHistoryForUser(
                eq(owner.getLsUuid()),
                eq(entryUuid),
                eq(actions),
                eq(types),
                eq(Sort.by(Sort.Direction.DESC, "creationDate")));
        verifyNoInteractions(mailingListBusinessService, accountService);
    }

    /**
     * Tests that a guest user retrieving audit logs will receive filtered share entries
     * when they do not have permission to view contact list members.
     * Ensures recipient info is hidden and contact list name is preserved.
     */
    @Test
    void findAllForGuestUserWithContactList() throws BusinessException {
        List<LogAction> actions = List.of(LogAction.CREATE);
        List<AuditLogEntryType> types = List.of(AuditLogEntryType.SHARE_ENTRY);

        ContactList contactList = new ContactList();
        contactList.setUuid(contactListUuid);
        contactList.setIdentifier("Test Contact List");
        AccountContactLists acl = new AccountContactLists();
        acl.setCanViewContactListMembers(false);

        ShareEntryAuditLogEntry logWithContactList = createShareEntryAuditLog(
                "log1", owner, entryUuid, contactListUuid);

        when(userMongoRepository.findDocumentHistoryForUser(
                eq(owner.getLsUuid()),
                eq(entryUuid),
                anyList(),
                anyList(),
                any(Sort.class)))
                .thenReturn(Sets.newHashSet(logWithContactList));

        when(mailingListBusinessService.findByUuid(contactListUuid))
                .thenReturn(contactList);
        when(accountService.findAccountContactListByAccountAndContactList(eq(guest), eq(contactList)))
                .thenReturn(Optional.of(acl));

        Set<AuditLogEntryUser> result = auditLogEntryService.findAll(
                guest, owner, entryUuid, actions, types, null, null);

        assertThat(result).hasSize(1);
        ShareEntryAuditLogEntry processedLog = (ShareEntryAuditLogEntry) result.iterator().next();
        assertThat(processedLog.getContactListUuid()).isEqualTo(contactListUuid);
        assertThat(processedLog.getContactListName()).isEqualTo("Test Contact List");

        ShareEntryMto resource = (ShareEntryMto) processedLog.getResource();
        assertThat(resource.getRecipient()).isNull();

        verify(mailingListBusinessService).findByUuid(contactListUuid);
        verify(accountService).findAccountContactListByAccountAndContactList(guest, contactList);
    }

    /**
     * Tests that the last deleted contact list name is correctly retrieved
     * from the audit log documents when available.
     */
    @Test
    void findLastDeletedContactListNameWhenFound() {
        String expectedName = "Deleted Contact List";
        Document doc = new Document();
        Document resource = new Document("name", expectedName);
        doc.put("resource", resource);

        when(userMongoRepository.findLastDeletedContactLists(contactListUuid))
                .thenReturn(List.of(doc));

        Optional<String> result = auditLogEntryService.findLastDeletedContactListName(contactListUuid);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedName);
    }

    /**
     * Tests that an empty result is returned when no deleted contact list
     * is found in the audit logs.
     */
    @Test
    void findLastDeletedContactListNameWhenNotFound() {
        when(userMongoRepository.findLastDeletedContactLists(contactListUuid))
                .thenReturn(Collections.emptyList());

        Optional<String> result = auditLogEntryService.findLastDeletedContactListName(contactListUuid);
        assertThat(result).isEmpty();
    }

    /**
     * Helper method to create a mock {@link ShareEntryAuditLogEntry} with
     * optional contact list UUID and recipient.
     *
     * @param uuid             the UUID of the audit log
     * @param actor            the actor performing the action
     * @param entryUuid        the UUID of the shared entry
     * @param contactListUuid  optional contact list UUID associated with the share
     * @return a prepared {@link ShareEntryAuditLogEntry} object
     */
    private ShareEntryAuditLogEntry createShareEntryAuditLog(String uuid, Account actor, String entryUuid, String contactListUuid) {
        ShareEntryAuditLogEntry log = new ShareEntryAuditLogEntry();
        log.setUuid(uuid);
        log.setCreationDate(new Date());
        AccountMto actorMto = new AccountMto(actor);
        log.setActor(actorMto);

        log.setType(AuditLogEntryType.SHARE_ENTRY);
        log.setAction(LogAction.CREATE);

        ShareEntryMto resource = new ShareEntryMto();
        resource.setUuid(entryUuid);
        AccountMto recipientMto = new AccountMto(actor);
        resource.setRecipient(recipientMto);

        log.setResource(resource);

        if (contactListUuid != null) {
            log.setContactListUuid(contactListUuid);
        }

        return log;
    }
}