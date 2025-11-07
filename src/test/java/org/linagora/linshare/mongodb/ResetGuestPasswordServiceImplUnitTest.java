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
package org.linagora.linshare.mongodb;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.PasswordService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.GuestWarnGuestAboutHisPasswordResetEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.impl.ResetGuestPasswordServiceImpl;
import org.linagora.linshare.mongo.entities.ResetGuestPassword;
import org.linagora.linshare.mongo.repository.ResetGuestPasswordMongoRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResetGuestPasswordServiceImplUnitTest {

	@InjectMocks
	private ResetGuestPasswordServiceImpl resetGuestPasswordService;

	@Mock
	private ResetGuestPasswordMongoRepository repository;

	@Mock
	private GuestService guestService;

	@Mock
	private LogEntryService logEntryService;

	@Mock
	private NotifierService notifierService;

	@Mock
	private MailBuildingService mailBuildingService;

	@Mock
	private PasswordService passwordService;

	/**
	 * Unit test that verifies the business logic of the update method:
	 * When mailBuildingService.build() returns a non-null mail container,
	 * then notifierService.sendNotification() MUST be called
	 */
	@Test
	void testUpdate_WhenMailBuildingServiceReturnsMail_ShouldSendNotification() throws BusinessException {
		final Account actor = this.createMockActor();
		final Account owner = this.createMockActor();
		final ResetGuestPassword resetGuestPassword = this.createMockResetGuestPassword();
		final Guest guest = this.createMockGuest();
		final MailContainerWithRecipient mailContainer = mock(MailContainerWithRecipient.class);
		when(this.repository.findByUuid("reset-uuid")).thenReturn(resetGuestPassword);
		when(this.guestService.find(actor, owner, "guest-uuid")).thenReturn(guest);
		when(this.repository.save(any(ResetGuestPassword.class))).thenReturn(resetGuestPassword);
		doNothing().when(this.passwordService).validateAndStorePassword(any(Guest.class), any(String.class));
		when(this.mailBuildingService.build(any(GuestWarnGuestAboutHisPasswordResetEmailContext.class)))
				.thenReturn(mailContainer);
		this.resetGuestPasswordService.update(actor, owner, resetGuestPassword);
		verify(this.notifierService).sendNotification(mailContainer);
	}

	/**
	 * Unit test that verifies the business logic of the update method:
	 * When mailBuildingService.build() returns null,
	 * then notifierService.sendNotification() MUST NOT be called
	 */
	@Test
	void testUpdate_WhenMailBuildingServiceReturnsNull_ShouldNotSendNotification() throws BusinessException {
		final Account actor = this.createMockActor();
		final Account owner = this.createMockActor();
		final ResetGuestPassword resetGuestPassword = this.createMockResetGuestPassword();
		final Guest guest = this.createMockGuest();
		when(this.repository.findByUuid("reset-uuid")).thenReturn(resetGuestPassword);
		when(this.guestService.find(actor, owner, "guest-uuid")).thenReturn(guest);
		when(this.repository.save(any(ResetGuestPassword.class))).thenReturn(resetGuestPassword);
		doNothing().when(this.passwordService).validateAndStorePassword(any(Guest.class), any(String.class));
		when(this.mailBuildingService.build(any(GuestWarnGuestAboutHisPasswordResetEmailContext.class)))
				.thenReturn(null);
		this.resetGuestPasswordService.update(actor, owner, resetGuestPassword);
		verify(this.notifierService, never()).sendNotification(any(MailContainerWithRecipient.class));
	}

	private ResetGuestPassword createMockResetGuestPassword() {
		final ResetGuestPassword resetGuestPassword = mock(ResetGuestPassword.class);
		when(resetGuestPassword.getUuid()).thenReturn("reset-uuid");
		when(resetGuestPassword.getPassword()).thenReturn("ValidPassword123!");
		when(resetGuestPassword.getGuestUuid()).thenReturn("guest-uuid");
		when(resetGuestPassword.getAlreadyUsed()).thenReturn(false);
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, 1);
		final Date futureDate = calendar.getTime();
		when(resetGuestPassword.getExpirationDate()).thenReturn(futureDate);

		return resetGuestPassword;
	}

	private Guest createMockGuest() {
		final Guest guest = mock(Guest.class);
		final AbstractDomain domain = mock(AbstractDomain.class);
		when(domain.getUuid()).thenReturn("domain-uuid");
		when(domain.getLabel()).thenReturn("Test Domain");
		when(guest.getLsUuid()).thenReturn("guest-uuid");
		when(guest.getMail()).thenReturn("guest@example.com");
		when(guest.getFirstName()).thenReturn("John");
		when(guest.getLastName()).thenReturn("Doe");
		when(guest.getDomain()).thenReturn(domain);
		when(guest.getAccountRepresentation()).thenReturn("John Doe <guest@example.com>");
		return guest;
	}

	private Account createMockActor() {
		final Account actor = mock(Account.class);
		final AbstractDomain domain = mock(AbstractDomain.class);
		lenient().when(domain.getUuid()).thenReturn("actor-domain-uuid");
		lenient().when(domain.getLabel()).thenReturn("Actor Domain");
		lenient().when(actor.getLsUuid()).thenReturn("test-actor-uuid");
		lenient().when(actor.getDomain()).thenReturn(domain);
		return actor;
	}
}