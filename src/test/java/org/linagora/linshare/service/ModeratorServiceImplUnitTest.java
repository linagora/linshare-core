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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.GuestBusinessService;
import org.linagora.linshare.core.business.service.ModeratorBusinessService;
import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Moderator;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.ModeratorDto;
import org.linagora.linshare.core.notifications.context.GuestModeratorCreationEmailContext;
import org.linagora.linshare.core.notifications.context.GuestModeratorDeletionEmailContext;
import org.linagora.linshare.core.notifications.context.GuestModeratorUpdateEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.rac.ModeratorResourceAccessControl;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.impl.ModeratorServiceImpl;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ModeratorServiceImplUnitTest {

	@Mock
	private ModeratorResourceAccessControl rac;

	@Mock
	private ModeratorBusinessService moderatorBusinessService;

	@Mock
	private GuestBusinessService guestBusinessService;

	@Mock
	private NotifierService notifierService;

	@Mock
	private MailBuildingService mailBuildingService;

	@Mock
	private LogEntryService logEntryService;

	@Mock
	private AccountService accountService;

	@Mock
	private Account authUser;

	@Mock
	private Account actor;

	@Mock
	private User moderatorAccount;

	@Mock
	private Guest guest;

	@Mock
	private AbstractDomain domain;

	@Mock
	private Moderator moderator;

	@InjectMocks
	private ModeratorServiceImpl moderatorService;

	@Mock
	private MailContainerWithRecipient mailContainer;

	@Mock
	private AuditLogEntryUser auditLogEntry;

	@BeforeEach
	void setUp() {
		when(this.domain.getUuid()).thenReturn("domain-uuid");
		when(this.domain.getLabel()).thenReturn("Test Domain");
		when(this.authUser.getLsUuid()).thenReturn("auth-user-uuid");
		when(this.authUser.getDomain()).thenReturn(this.domain);
		when(this.actor.getLsUuid()).thenReturn("actor-uuid");
		when(this.actor.getDomain()).thenReturn(this.domain);
		when(this.moderatorAccount.getLsUuid()).thenReturn("moderator-account-uuid");
		when(this.moderatorAccount.getDomain()).thenReturn(this.domain);
		when(this.moderatorAccount.getMail()).thenReturn("moderator@example.com");
		lenient().when(this.moderatorAccount.getFirstName()).thenReturn("John");
		lenient().when(this.moderatorAccount.getLastName()).thenReturn("Doe");
		when(this.guest.getLsUuid()).thenReturn("guest-uuid");
		when(this.guest.getDomain()).thenReturn(this.domain);
		when(this.guest.getMail()).thenReturn("guest@example.com");
		when(this.guest.getFirstName()).thenReturn("Guest");
		when(this.guest.getLastName()).thenReturn("User");
		when(this.moderator.getUuid()).thenReturn("moderator-uuid");
		when(this.moderator.getRole()).thenReturn(ModeratorRole.ADMIN);
		when(this.moderator.getAccount()).thenReturn(this.moderatorAccount);
		when(this.moderator.getGuest()).thenReturn(this.guest);
		lenient().when(this.guestBusinessService.findByLsUuid(anyString())).thenReturn(this.guest);
		lenient().when(this.moderatorBusinessService.findAllByGuest(any(Guest.class), any(), any()))
				.thenReturn(new ArrayList<>());
		lenient().when(this.moderatorBusinessService.create(any(Moderator.class))).thenReturn(this.moderator);
		lenient().when(this.moderatorBusinessService.find(anyString())).thenReturn(this.moderator);
		lenient().when(this.moderatorBusinessService.update(any(Moderator.class))).thenReturn(this.moderator);
		lenient().when(this.moderatorBusinessService.findByGuestAndAccount(any(Account.class), any(Guest.class)))
				.thenReturn(Optional.empty());
		lenient().when(this.moderatorBusinessService.findAllModeratorUuidsByGuest(any(Guest.class)))
				.thenReturn(Collections.emptyList());
		lenient().when(this.accountService.findAccountContactListsByAccount(any(Guest.class)))
				.thenReturn(Collections.emptyList());
		lenient().doNothing().when(this.rac).checkCreatePermission(any(), any(), any(), any(), any(), any());
		lenient().doNothing().when(this.rac).checkReadPermission(any(), any(), any(), any(), any());
		lenient().doNothing().when(this.rac).checkUpdatePermission(any(), any(), any(), any(), any());
		lenient().doNothing().when(this.rac).checkDeletePermission(any(), any(), any(), any(), any());
		lenient().doNothing().when(this.rac).checkListPermission(any(), any(), any(), any(), any(), any());
		lenient().when(this.guestBusinessService.update(any(), any(), any(), any(), anyList(), anyMap()))
				.thenReturn(this.guest);
		lenient().when(this.logEntryService.insert(any(AuditLogEntryUser.class))).thenReturn(this.auditLogEntry);
	}

	/**
	 * Tests that a notification is sent when creating a moderator
	 * and the mail building service returns a valid mail container.
	 *
	 * <p>Verifies that {@link NotifierService#sendNotification(MailContainerWithRecipient)}
	 * is called when the mail building service successfully builds a mail container.</p>
	 */
	@Test
	void testCreateModerator_WhenMailBuildingServiceReturnsMail_ShouldSendNotification() {
		when(this.actor.getLsUuid()).thenReturn("actor-uuid");
		when(this.moderatorAccount.getLsUuid()).thenReturn("different-moderator-uuid");
		when(this.mailBuildingService.build(any(GuestModeratorCreationEmailContext.class)))
				.thenReturn(this.mailContainer);
		this.moderatorService.create(this.authUser, this.actor, this.moderator, false);
		verify(this.notifierService).sendNotification(this.mailContainer);
	}

	/**
	 * Tests that a notification is sent when creating a moderator with non-null mail.
	 *
	 * <p>This test specifically verifies the notification behavior when mail is not null,
	 * ensuring the notification service is properly invoked.</p>
	 */
	@Test
	void testCreateModerator_WhenMailNotNull_ShouldSendNotification() {
		when(this.actor.getLsUuid()).thenReturn("actor-uuid");
		when(this.moderatorAccount.getLsUuid()).thenReturn("different-moderator-uuid");
		when(this.mailBuildingService.build(any(GuestModeratorCreationEmailContext.class)))
				.thenReturn(this.mailContainer);
		this.moderatorService.create(this.authUser, this.actor, this.moderator, false);
		verify(this.notifierService).sendNotification(this.mailContainer);
	}

	/**
	 * Tests that no notification is sent when creating a moderator
	 * and the mail building service returns null.
	 *
	 * <p>Verifies that {@link NotifierService#sendNotification(MailContainerWithRecipient)}
	 * is never called when the mail building service returns null, demonstrating
	 * proper null-safety in the notification logic.</p>
	 */
	@Test
	void testCreateModerator_WhenMailBuildingServiceReturnsNull_ShouldNotSendNotification() {
		when(this.actor.getLsUuid()).thenReturn("actor-uuid");
		when(this.moderatorAccount.getLsUuid()).thenReturn("different-moderator-uuid");
		when(this.mailBuildingService.build(any(GuestModeratorCreationEmailContext.class)))
				.thenReturn(null);
		this.moderatorService.create(this.authUser, this.actor, this.moderator, false);
		verify(this.notifierService, never()).sendNotification(any(MailContainerWithRecipient.class));
	}

	/**
	 * Tests that a notification is sent when updating a moderator
	 * and the mail building service returns a valid mail container.
	 *
	 * <p>Verifies the notification behavior during moderator updates,
	 * ensuring that role changes or other updates trigger proper notifications.</p>
	 */
	@Test
	void testUpdateModerator_WhenMailBuildingServiceReturnsMail_ShouldSendNotification() {
		when(this.actor.getLsUuid()).thenReturn("actor-uuid");
		when(this.moderatorAccount.getLsUuid()).thenReturn("different-moderator-uuid");
		when(this.mailBuildingService.build(any(GuestModeratorUpdateEmailContext.class)))
				.thenReturn(this.mailContainer);
		final ModeratorDto dto = new ModeratorDto();
		dto.setRole(ModeratorRole.ADMIN);
		this.moderatorService.update(this.authUser, this.actor, this.moderator, dto);
		verify(this.notifierService).sendNotification(this.mailContainer);
	}

	/**
	 * Tests that no notification is sent when updating a moderator
	 * and the mail building service returns null.
	 *
	 * <p>Ensures that the update operation handles null mail containers gracefully
	 * without attempting to send notifications.</p>
	 */
	@Test
	void testUpdateModerator_WhenMailBuildingServiceReturnsNull_ShouldNotSendNotification() {
		when(this.actor.getLsUuid()).thenReturn("actor-uuid");
		when(this.moderatorAccount.getLsUuid()).thenReturn("different-moderator-uuid");
		when(this.mailBuildingService.build(any(GuestModeratorUpdateEmailContext.class)))
				.thenReturn(null);
		final ModeratorDto dto = new ModeratorDto();
		dto.setRole(ModeratorRole.ADMIN);
		this.moderatorService.update(this.authUser, this.actor, this.moderator, dto);
		verify(this.notifierService, never()).sendNotification(any(MailContainerWithRecipient.class));
	}

	/**
	 * Tests that a notification is sent when deleting a moderator
	 * and the mail building service returns a valid mail container.
	 *
	 * <p>Verifies that moderator deletion triggers appropriate notifications
	 * when the mail building service successfully creates a mail container.</p>
	 */
	@Test
	void testDeleteModerator_WhenMailBuildingServiceReturnsMail_ShouldSendNotification() {
		when(this.actor.getLsUuid()).thenReturn("actor-uuid");
		when(this.moderatorAccount.getLsUuid()).thenReturn("different-moderator-uuid");
		when(this.mailBuildingService.build(any(GuestModeratorDeletionEmailContext.class)))
				.thenReturn(this.mailContainer);
		this.moderatorService.delete(this.authUser, this.actor, this.moderator);
		verify(this.notifierService).sendNotification(this.mailContainer);
	}

	/**
	 * Tests that no notification is sent when deleting a moderator
	 * and the mail building service returns null.
	 *
	 * <p>Ensures that deletion operations don't attempt to send notifications
	 * when the mail building service cannot create a mail container.</p>
	 */
	@Test
	void testDeleteModerator_WhenMailBuildingServiceReturnsNull_ShouldNotSendNotification() {
		when(this.actor.getLsUuid()).thenReturn("actor-uuid");
		when(this.moderatorAccount.getLsUuid()).thenReturn("different-moderator-uuid");
		when(this.mailBuildingService.build(any(GuestModeratorDeletionEmailContext.class)))
				.thenReturn(null);
		this.moderatorService.delete(this.authUser, this.actor, this.moderator);
		verify(this.notifierService, never()).sendNotification(any(MailContainerWithRecipient.class));
	}

}