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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.assertj.core.groups.Tuple;
import org.bson.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.linagora.linshare.core.business.service.MailingListBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountContactLists;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.GuestDomain;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.TopDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.AuditLogEntryResourceAccessControl;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.impl.AuditLogEntryServiceImpl;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.logs.ShareEntryAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.entities.mto.ShareEntryMto;
import org.linagora.linshare.mongo.repository.AuditUserMongoRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Sort;

import com.google.common.collect.Sets;

/**
 * Unit tests for {@link AuditLogEntryService}.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuditLogEntryServiceImplTest {

    private static final String TOP_DOMAIN = "top-domain";
    private static final String GUEST_DOMAIN = "guest-domain";
	private static Account regularUser;
	private static Account guest;
	private static TopDomain topDomain;

    @Mock
    private AuditUserMongoRepository userMongoRepository;

    @Mock
    private MailingListBusinessService mailingListBusinessService;

    @Mock
    private AccountService accountService;

	@Mock
	private AuditLogEntryResourceAccessControl rac;

    @InjectMocks
    private AuditLogEntryServiceImpl auditLogEntryService;


    private Account owner;
    private String entryUuid;
    private String contactListUuid;

	@BeforeAll
	static void init() {
		topDomain = new TopDomain(TOP_DOMAIN);
		topDomain.setUuid(TOP_DOMAIN);
		final GuestDomain domainGuest = new GuestDomain(GUEST_DOMAIN);
		domainGuest.setUuid(GUEST_DOMAIN);

		guest = new Guest("guest", "Guest", "guest@linshare.org");
		guest.setLsUuid("guest-uuid");
		guest.setDomain(domainGuest);

		regularUser = new Internal("Recipient", "User", "recipient@linshare.org", "recipient-uid");
		regularUser.setLsUuid("recipient-uuid");
		regularUser.setDomain(topDomain);
	}

    @BeforeEach
    void setUp() {
        owner = new Internal("Owner", "User", "owner@linshare.org", "owner-uid");
        owner.setLsUuid("owner-uuid");
        owner.setDomain(topDomain);

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
	 * Returns whether the given {@link AccountMto} is null or contains no data.
	 */
	private static Boolean isEmptyAccountMtoOrNull(final AccountMto accountMto) {
		if (accountMto == null) {
			return true;
		}
		return accountMto.getUuid() == null &&
				accountMto.getMail() == null &&
				accountMto.getName() == null;
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
        assertNotNull(resource.getRecipient());
		assertThat(resource.getRecipient()).isInstanceOf(AccountMto.class);
		assertThat(resource.getRecipient().getUuid()).isNull();
		assertThat(resource.getRecipient().getMail()).isNull();
        verify(mailingListBusinessService).findByUuid(contactListUuid);
        verify(accountService).findAccountContactListByAccountAndContactList(guest, contactList);
    }


	private static String normalizeString(String value) {
		return (value == null || value.isEmpty() || value.trim().isEmpty()) ? null : value;
	}

	private static AccountMto normalizeAccountMto(AccountMto accountMto) {
		if (accountMto == null) {
			return null;
		}
		boolean hasUuid = accountMto.getUuid() != null && !accountMto.getUuid().isEmpty();
		boolean hasMail = accountMto.getMail() != null && !accountMto.getMail().isEmpty();
		boolean hasName = accountMto.getName() != null && !accountMto.getName().isEmpty();

		if (!hasUuid && !hasMail && !hasName) {
			return null;
		}
		return accountMto;
	}

	/**
	 * <p>
	 * Verify the finding of all {@link ShareEntryAuditLogEntry}ies for a given user.
	 * </p>
	 * Expected results:
	 * <ul>
	 * <li>No errors occur,</li>
	 * <li>if the {@code authUser} is a <b>guest</b>:
	 * <ul>
	 * <li>if the share log has a contact list:
	 * <ul>
	 * <li>if the guest can view the contact list members:
	 * <ul>
	 * <li>The logs are kept unchanged. (Nothing is hidden)</li>
	 * </ul>
	 * </li>
	 * <li>Else
	 * <ul>
	 * <li>The {@code authUser}, {@code actor} and {@code recipient} information are hidden in the log.</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </li>
	 * <li>Else,
	 * <ul>
	 * <li>The logs are kept unchanged. (Nothing is hidden)</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </li>
	 * <li>
	 * Else
	 * <ul>
	 * <li>The logs are kept unchanged. (Nothing is hidden)</li>
	 * </ul>
	 * </li>
	 * </ul>
	 *
	 * @param authUser
	 *                                              The authenticated user that is requesting the audit logs. Not
	 *                                              {@code null}.
	 * @param actor
	 *                                              The actor that is requesting the logs. Not {@code null}.
	 * @param shareEntryAuditLog
	 *                                              A share entry log created by a previous action on a share entry. Not
	 *                                              {@code null}.
	 * @param shareEntryCreatedForContactListMember
	 *                                              If {@code true}, the provided share entry log was created for a user
	 *                                              associated to a contact list member.
	 * @param canViewContactListMembers
	 *                                              If {@code true}, the authenticated user acting as guest can view the
	 *                                              members of the potential contact list associated to the share entry,
	 *                                              otherwise he can't.
	 */
	@ParameterizedTest
	@MethodSource("generateSampleShareEntryAuditLog")
	void findAllForUsers_shareLogEntries(@Nonnull final Account authUser, @Nonnull final Account actor,
			@Nonnull final ShareEntryAuditLogEntry shareEntryAuditLog,
			final boolean shareEntryCreatedForContactListMember, final boolean canViewContactListMembers) {
		// Prepare
		final ContactList contactList = new ContactList();
		final AccountContactLists accountContactLists = new AccountContactLists();
		accountContactLists.setCanViewContactListMembers(canViewContactListMembers);
		contactList.setUuid(shareEntryAuditLog.getContactListUuid());
		contactList.setOwner((User) regularUser);

		// Mock
		when(this.userMongoRepository.findForUser(eq(actor.getLsUuid()), anyList(), anyList())).thenReturn(
				Set.of(shareEntryAuditLog));
		if (shareEntryCreatedForContactListMember && shareEntryAuditLog.getContactListUuid() != null) {
			when(this.mailingListBusinessService.findByUuid(shareEntryAuditLog.getContactListUuid())).thenReturn(
					contactList);
			when(this.accountService.findAccountContactListByAccountAndContactList(authUser, contactList)).thenReturn(
					Optional.of(accountContactLists));
		}

		// Execute
		final Set<AuditLogEntryUser> auditLogs = this.auditLogEntryService.findAllForUsers(authUser, actor, null,
				null, true, null, null);

		// Assert
		if (authUser.isGuest() && shareEntryAuditLog.getContactListUuid() != null) {
			verify(this.mailingListBusinessService).findByUuid(shareEntryAuditLog.getContactListUuid());
		}
		auditLogs.forEach(log -> assertInstanceOf(ShareEntryAuditLogEntry.class, log));
		if (authUser.isGuest() &&
				shareEntryCreatedForContactListMember &&
				!canViewContactListMembers &&
				!Objects.equals(shareEntryAuditLog.getActor().getUuid(), authUser.getLsUuid())) {
			// Audit log entries are requested by a guest having not the right to view contact list member, and the
			// current audit log entry is relative to share for a contact list member and was not generated by the guest
			// (so by the contact list member) --> contact list member data are hidden
			assertThat(auditLogs)
					.extracting(
							auditLog -> normalizeAccountMto(((ShareEntryAuditLogEntry) auditLog).getAuthUser()),
							auditLog -> normalizeAccountMto(((ShareEntryAuditLogEntry) auditLog).getActor()),
							auditLog -> normalizeString(((ShareEntryAuditLogEntry) auditLog).getRecipientMail()),
							auditLog -> normalizeString(((ShareEntryAuditLogEntry) auditLog).getRecipientUuid()),
							auditLog -> ((ShareEntryAuditLogEntry) auditLog).getContactListUuid(),
							auditLog -> normalizeAccountMto(
									((ShareEntryMto) ((ShareEntryAuditLogEntry) auditLog).getResource()).getRecipient()),
							AuditLogEntryUser::getAction,
							AuditLogEntryUser::getCause)
					.containsOnly(
							Tuple.tuple(
									null,
									null,
									null,
									null,
									shareEntryAuditLog.getContactListUuid(),
									null,
									shareEntryAuditLog.getAction(),
									shareEntryAuditLog.getCause()));
		} else if (authUser.isGuest() &&
				shareEntryCreatedForContactListMember &&
				!canViewContactListMembers &&
				Objects.equals(shareEntryAuditLog.getActor().getUuid(), authUser.getLsUuid()) &&
				!shareEntryAuditLog.getRecipientUuid().equals(contactList.getOwner().getLsUuid())) {
			// Audit log entries are requested by a guest having not the right to view contact list member, and the
			// current audit log entry is relative to share for a contact list member and was generated by the guest based
			// on a CREATE action --> recipient data are hidden (because the recipient is a member of contact list for
			// which members are hidden)
			assertThat(auditLogs)
					.extracting(
							AuditLogEntryUser::getAuthUser,
							AuditLogEntryUser::getActor,
							auditLog -> normalizeString(((ShareEntryAuditLogEntry) auditLog).getRecipientMail()),
							auditLog -> normalizeString(((ShareEntryAuditLogEntry) auditLog).getRecipientUuid()),
							auditLog -> ((ShareEntryAuditLogEntry) auditLog).getContactListUuid(),
							auditLog -> normalizeAccountMto(
									((ShareEntryMto) ((ShareEntryAuditLogEntry) auditLog).getResource()).getRecipient()),
							AuditLogEntryUser::getAction,
							AuditLogEntryUser::getCause)
					.containsOnly(
							Tuple.tuple(
									shareEntryAuditLog.getAuthUser(),
									shareEntryAuditLog.getActor(),
									null,
									null,
									shareEntryAuditLog.getContactListUuid(),
									null,
									shareEntryAuditLog.getAction(),
									shareEntryAuditLog.getCause()));
		} else {
			// Otherwise --> No data is hidden
			assertThat(auditLogs)
					.extracting(
							AuditLogEntryUser::getAuthUser,
							AuditLogEntryUser::getActor,
							auditLog -> ((ShareEntryAuditLogEntry) auditLog).getRecipientMail(),
							auditLog -> ((ShareEntryAuditLogEntry) auditLog).getRecipientUuid(),
							auditLog -> ((ShareEntryAuditLogEntry) auditLog).getContactListUuid(),
							auditLog -> ((ShareEntryMto) ((ShareEntryAuditLogEntry) auditLog).getResource()).getRecipient(),
							AuditLogEntryUser::getAction,
							AuditLogEntryUser::getCause)
					.containsOnly(
							Tuple.tuple(
									shareEntryAuditLog.getAuthUser(),
									shareEntryAuditLog.getActor(),
									shareEntryAuditLog.getRecipientMail(),
									shareEntryAuditLog.getRecipientUuid(),
									shareEntryAuditLog.getContactListUuid(),
									((ShareEntryMto) shareEntryAuditLog.getResource()).getRecipient(),
									shareEntryAuditLog.getAction(),
									shareEntryAuditLog.getCause()));
		}
	}

	/**
	 * Generate a stream of arguments with 5 elements each:
	 * <ul>
	 * <li>{@code authUser}: the authenticated user requesting the logs.</li>
	 * <li>{@code actor}: the actor requesting the logs.</li>
	 * <li>A {@link ShareEntryAuditLogEntry} with different combination of
	 * <ul>
	 * <li>Has {@code contactList} or not</li>
	 * <li>{@code authUser}: the authenticated user when the log was saved.</li>
	 * <li>{@code actor}: of the action when the log was saved.</li>
	 * <li>{@code Log action}, eg: DOWNLOAD, UPDATE, etc...</li>
	 * <li>{@code Log action cause}, eg: COPY, UNDEFINED, etc...</li>
	 * </ul>
	 * </li>
	 * <li>A {@code boolean} representing whether the share log has a contact list or not</li>
	 * <li>A {@code boolean} representing whether the guest member associated with that contact list can view the
	 * member's info of that contact list.</li>
	 * </ul>
	 *
	 * @return the generated stream of arguments. Not {@code null}.
	 */
	private static @Nonnull Stream<Arguments> generateSampleShareEntryAuditLog() {
		final ContactListInfo contactListInfo = new ContactListInfo("contact-list-uuid", "my-contact-list");

		return Stream.of(
				//--> authUser/actor (requesting the log): guest
				//----> withContactList:yes
				//------> canViewContactListMembers:true
				//--------> authUser/actor (in the log):guest
				//----------> action:DOWNLOAD, cause:COPY, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.DOWNLOAD, LogActionCause.COPY),
						true, true),
				//----------> action:DOWNLOAD, cause:null, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.DOWNLOAD, null),
						true, true),
				//----------> action:DELETE, cause:UNDEFINED, authUser:guest, actor:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.DELETE, LogActionCause.UNDEFINED),
						true, true),
				//----------> action:CREATE, cause:null, authUser:guest, actor:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.CREATE, null),
						true, true),
				//------> authUser/actor (in the log):regularUser
				//----------> action:DOWNLOAD, cause:COPY, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, regularUser, regularUser, LogAction.DOWNLOAD,
								LogActionCause.COPY), true, true),
				//----------> action:DOWNLOAD, cause:null, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.DOWNLOAD, null),
						true, true),
				//----------> action:DELETE, cause:UNDEFINED, authUser:guest, actor:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, regularUser, regularUser, LogAction.DELETE,
								LogActionCause.UNDEFINED), true, true),
				//----------> action:CREATE, cause:null, authUser:guest, actor:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, regularUser, regularUser, LogAction.CREATE,
								null), true, true),
				//------> canViewContactListMembers:false
				//--------> authUser/actor (in the log):guest
				//----------> action:DOWNLOAD, cause:COPY, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.DOWNLOAD, LogActionCause.COPY),
						true, false),
				//----------> action:DOWNLOAD, cause:null, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.DOWNLOAD, null),
						true, false),
				//----------> action:DELETE, cause:UNDEFINED, authUser:guest, actor:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.DELETE, LogActionCause.UNDEFINED),
						true, false),
				//----------> action:CREATE, cause:null, authUser:guest, actor:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.CREATE, null),
						true, false),
				//--------> authUser/actor (in the log):regularUser
				//----------> action:DOWNLOAD, cause:COPY, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, regularUser, regularUser, LogAction.DOWNLOAD,
								LogActionCause.COPY), true, false),
				//----------> action:DOWNLOAD, cause:null, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.DOWNLOAD, null),
						true, false),
				//----------> action:DELETE, cause:UNDEFINED, authUser:guest, actor:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, regularUser, regularUser, LogAction.DELETE,
								LogActionCause.UNDEFINED), true, false),
				//----------> action:CREATE, cause:null, authUser:guest, actor:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, regularUser, regularUser, LogAction.CREATE,
								null), true, false),

				//----> withContactList:no
				//------> authUser/actor (in the log):guest
				//--------> action:DOWNLOAD, cause:COPY, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(null, guest, guest, LogAction.DOWNLOAD, LogActionCause.COPY), false, false),
				//----------> action:DOWNLOAD, cause:null, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.DOWNLOAD, null),
						false, false),
				//--------> action:DELETE, cause:UNDEFINED, authUser:guest, actor:guest
				Arguments.of(guest, guest,
						createShareEntryLog(null, guest, guest, LogAction.DELETE, LogActionCause.UNDEFINED), false, false),
				//--------> action:CREATE, cause:null, authUser:guest, actor:guest
				Arguments.of(guest, guest,
						createShareEntryLog(null, guest, guest, LogAction.CREATE, null), false, false),
				//------> authUser/actor (in the log):regularUser
				//--------> action:DOWNLOAD, cause:COPY, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(null, regularUser, regularUser, LogAction.DOWNLOAD, LogActionCause.COPY),
						false, false),
				//----------> action:DOWNLOAD, cause:null, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.DOWNLOAD, null),
						false, false),
				//--------> action:DELETE, cause:UNDEFINED, authUser:guest, actor:guest
				Arguments.of(guest, guest,
						createShareEntryLog(null, regularUser, regularUser, LogAction.DELETE, LogActionCause.UNDEFINED),
						false, false),
				//--------> action:CREATE, cause:null, authUser:guest, actor:guest
				Arguments.of(guest, guest,
						createShareEntryLog(null, regularUser, regularUser, LogAction.CREATE, null),
						false, false),
				//--------> authUser/actor (in the log):guest
				//----------> action:DOWNLOAD, cause:COPY, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(null, guest, guest, LogAction.DOWNLOAD, LogActionCause.COPY), false, false),
				//----------> action:DOWNLOAD, cause:null, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.DOWNLOAD, null),
						false, false),
				//--------> action:DELETE, cause:UNDEFINED, authUser:guest, actor:guest
				Arguments.of(guest, guest,
						createShareEntryLog(null, guest, guest, LogAction.DELETE, LogActionCause.UNDEFINED), false, false),
				//--------> action:CREATE, cause:null, authUser:guest, actor:guest
				Arguments.of(guest, guest,
						createShareEntryLog(null, guest, guest, LogAction.CREATE, null), false, false),
				//------> authUser/actor (in the log):regularUser
				//----------> action:DOWNLOAD, cause:COPY, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(null, regularUser, regularUser, LogAction.DOWNLOAD, LogActionCause.COPY),
						false, false),
				//----------> action:DOWNLOAD, cause:null, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.DOWNLOAD, null),
						false, false),
				//--------> action:DELETE, cause:UNDEFINED, authUser:guest, actor:guest
				Arguments.of(guest, guest,
						createShareEntryLog(null, regularUser, regularUser, LogAction.DELETE, LogActionCause.UNDEFINED),
						false, false),
				//--------> action:CREATE, cause:null, authUser:guest, actor:guest
				Arguments.of(guest, guest,
						createShareEntryLog(null, regularUser, regularUser, LogAction.CREATE, null),
						false, false),

				//--> authUser/actor (requesting the log): regularUser
				//----> withContactList:yes
				//------> canViewContactListMembers:true
				//--------> authUser/actor (in the log):guest
				//----------> action:DOWNLOAD, cause:COPY, authUser:guest
				Arguments.of(regularUser, regularUser,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.DOWNLOAD, LogActionCause.COPY),
						true, true),
				//----------> action:DOWNLOAD, cause:null, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.DOWNLOAD, null),
						true, true),
				//----------> action:DELETE, cause:UNDEFINED, authUser:guest, actor:guest
				Arguments.of(regularUser, regularUser,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.DELETE, LogActionCause.UNDEFINED),
						true, true),
				//----------> action:CREATE, cause:null, authUser:guest, actor:guest
				Arguments.of(regularUser, regularUser,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.CREATE, null),
						true, true),
				//------> authUser/actor (in the log):regularUser
				//----------> action:DOWNLOAD, cause:COPY, authUser:guest
				Arguments.of(regularUser, regularUser,
						createShareEntryLog(contactListInfo, regularUser, regularUser, LogAction.DOWNLOAD,
								LogActionCause.COPY), true, true),
				//----------> action:DOWNLOAD, cause:null, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.DOWNLOAD, null),
						true, true),
				//----------> action:DELETE, cause:UNDEFINED, authUser:guest, actor:guest
				Arguments.of(regularUser, regularUser,
						createShareEntryLog(contactListInfo, regularUser, regularUser, LogAction.DELETE,
								LogActionCause.UNDEFINED), true, true),
				//----------> action:CREATE, cause:null, authUser:guest, actor:guest
				Arguments.of(regularUser, regularUser,
						createShareEntryLog(contactListInfo, regularUser, regularUser, LogAction.CREATE,
								null), true, true),
				//------> canViewContactListMembers:false
				//--------> authUser/actor (in the log):guest
				//----------> action:DOWNLOAD, cause:COPY, authUser:guest
				Arguments.of(regularUser, regularUser,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.DOWNLOAD, LogActionCause.COPY),
						true, false),
				//----------> action:DOWNLOAD, cause:null, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.DOWNLOAD, null),
						true, true),
				//----------> action:DELETE, cause:UNDEFINED, authUser:guest, actor:guest
				Arguments.of(regularUser, regularUser,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.DELETE, LogActionCause.UNDEFINED),
						true, false),
				//----------> action:CREATE, cause:null, authUser:guest, actor:guest
				Arguments.of(regularUser, regularUser,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.CREATE, null),
						true, false),
				//--------> authUser/actor (in the log):regularUser
				//----------> action:DOWNLOAD, cause:COPY, authUser:guest
				Arguments.of(regularUser, regularUser,
						createShareEntryLog(contactListInfo, regularUser, regularUser, LogAction.DOWNLOAD,
								LogActionCause.COPY), true, false),
				//----------> action:DOWNLOAD, cause:null, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.DOWNLOAD, null),
						true, false),
				//----------> action:DELETE, cause:UNDEFINED, authUser:guest, actor:guest
				Arguments.of(regularUser, regularUser,
						createShareEntryLog(contactListInfo, regularUser, regularUser, LogAction.DELETE,
								LogActionCause.UNDEFINED), true, false),
				//----------> action:CREATE, cause:null, authUser:guest, actor:guest
				Arguments.of(regularUser, regularUser,
						createShareEntryLog(contactListInfo, regularUser, regularUser, LogAction.CREATE,
								null), true, false),

				//----> withContactList:no
				//------> authUser/actor (in the log):guest
				//--------> action:DOWNLOAD, cause:COPY, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(null, guest, guest, LogAction.DOWNLOAD, LogActionCause.COPY), false, false),
				//----------> action:DOWNLOAD, cause:null, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(null, guest, guest, LogAction.DOWNLOAD, null),
						false, false),
				//--------> action:DELETE, cause:UNDEFINED, authUser:guest, actor:guest
				Arguments.of(guest, guest,
						createShareEntryLog(null, guest, guest, LogAction.DELETE, LogActionCause.UNDEFINED), false, false),
				//--------> action:CREATE, cause:null, authUser:guest, actor:guest
				Arguments.of(guest, guest,
						createShareEntryLog(null, guest, guest, LogAction.CREATE, null), false, false),
				//------> authUser/actor (in the log):regularUser
				//--------> action:DOWNLOAD, cause:COPY, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(null, regularUser, regularUser, LogAction.DOWNLOAD, LogActionCause.COPY),
						false, false),
				//----------> action:DOWNLOAD, cause:null, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.DOWNLOAD, null),
						false, false),
				//--------> action:DELETE, cause:UNDEFINED, authUser:guest, actor:guest
				Arguments.of(guest, guest,
						createShareEntryLog(null, regularUser, regularUser, LogAction.DELETE, LogActionCause.UNDEFINED),
						false, false),
				//--------> action:CREATE, cause:null, authUser:guest, actor:guest
				Arguments.of(guest, guest,
						createShareEntryLog(null, regularUser, regularUser, LogAction.CREATE, null),
						false, false),
				//--------> authUser/actor (in the log):guest
				//----------> action:DOWNLOAD, cause:COPY, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(null, guest, guest, LogAction.DOWNLOAD, LogActionCause.COPY), false, false),
				//----------> action:DOWNLOAD, cause:null, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.DOWNLOAD, null),
						false, false),
				//--------> action:DELETE, cause:UNDEFINED, authUser:guest, actor:guest
				Arguments.of(guest, guest,
						createShareEntryLog(null, guest, guest, LogAction.DELETE, LogActionCause.UNDEFINED), false, false),
				//--------> action:CREATE, cause:null, authUser:guest, actor:guest
				Arguments.of(guest, guest,
						createShareEntryLog(null, guest, guest, LogAction.CREATE, null), false, false),
				//------> authUser/actor (in the log):regularUser
				//----------> action:DOWNLOAD, cause:COPY, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(null, regularUser, regularUser, LogAction.DOWNLOAD, LogActionCause.COPY),
						false, false),
				//----------> action:DOWNLOAD, cause:null, authUser:guest
				Arguments.of(guest, guest,
						createShareEntryLog(contactListInfo, guest, guest, LogAction.DOWNLOAD, null),
						false, false),
				//--------> action:DELETE, cause:UNDEFINED, authUser:guest, actor:guest
				Arguments.of(guest, guest,
						createShareEntryLog(null, regularUser, regularUser, LogAction.DELETE, LogActionCause.UNDEFINED),
						false, false),
				//--------> action:CREATE, cause:null, authUser:guest, actor:guest
				Arguments.of(guest, guest,
						createShareEntryLog(null, regularUser, regularUser, LogAction.CREATE, null),
						false, false));
	}

	private static class ContactListInfo {
		String contactListUuid;
		String contactListName;

		ContactListInfo(@Nonnull String contactListUuid, @Nonnull String contactListName) {
			this.contactListUuid = contactListUuid;
			this.contactListName = contactListName;
		}
	}

	/**
	 * Create a {@link ShareEntryAuditLogEntry} with the provided combination.
	 */
	private static @Nonnull ShareEntryAuditLogEntry createShareEntryLog(@Nullable final ContactListInfo contactListInfo,
			@Nonnull final Account authUser, @Nonnull final Account actor, @Nonnull final LogAction action,
			@Nullable final LogActionCause cause) {
		final ShareEntryAuditLogEntry shareEntry = new ShareEntryAuditLogEntry();
		final AccountMto recipientMto = new AccountMto(regularUser);
		if (contactListInfo != null) {
			final ContactList contactList = new ContactList();
			contactList.setUuid(contactListInfo.contactListUuid);
			shareEntry.setContactListUuid(contactListInfo.contactListUuid);
			shareEntry.setContactListName(contactListInfo.contactListName);
		}
		shareEntry.setAuthUser(new AccountMto(authUser));
		shareEntry.setActor(new AccountMto(actor));
		shareEntry.setAction(action);
		shareEntry.setCause(cause);
		shareEntry.setRecipientUuid("some-random-uuid-1");
		shareEntry.setRecipientMail("random1@linshare.org");
		ShareEntryMto shareEntryMto = new ShareEntryMto();
		shareEntryMto.setRecipient(recipientMto);
		shareEntry.setResource(shareEntryMto);

		return shareEntry;
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