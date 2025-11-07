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
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.ContainerQuotaBusinessService;
import org.linagora.linshare.core.business.service.GuestBusinessService;
import org.linagora.linshare.core.business.service.MailingListBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Moderator;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.rac.GuestResourceAccessControl;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.ModeratorService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.core.service.impl.GuestServiceImpl;
import org.linagora.linshare.mongo.entities.ResetGuestPassword;
import org.linagora.linshare.mongo.repository.ResetGuestPasswordMongoRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GuestServiceImplUnitTest {

	@InjectMocks
	private GuestServiceImpl guestService;

	@Mock
	private GuestBusinessService guestBusinessService;

	@Mock
	private AbstractDomainService abstractDomainService;

	@Mock
	private FunctionalityReadOnlyService functionalityReadOnlyService;

	@Mock
	private UserService userService;

	@Mock
	private NotifierService notifierService;

	@Mock
	private MailBuildingService mailBuildingService;

	@Mock
	private LogEntryService logEntryService;

	@Mock
	private ContainerQuotaBusinessService containerQuotaBusinessService;

	@Mock
	private AccountQuotaBusinessService accountQuotaBusinessService;

	@Mock
	private ResetGuestPasswordMongoRepository resetGuestPasswordMongoRepository;

	@Mock
	private ModeratorService moderatorService;

	@Mock
	private AccountRepository<Account> accountRepository;

	@Mock
	private MailingListBusinessService mailingListBusinessService;

	@Mock
	private GuestResourceAccessControl guestResourceAccessControl;

	@Mock
	private SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService;

	private User actor;
	private Guest guest;
	private AbstractDomain guestDomain;
	private AbstractDomain actorDomain;
	private ContainerQuota containerQuota;

	@BeforeEach
	void setUp() {
		this.actor = mock(User.class);
		this.actorDomain = mock(AbstractDomain.class);
		lenient().when(this.actor.getLsUuid()).thenReturn("test-actor-uuid");
		lenient().when(this.actor.getDomain()).thenReturn(this.actorDomain);
		lenient().when(this.actor.getDomainId()).thenReturn("actor-domain-id");
		lenient().when(this.actor.isRoot()).thenReturn(false);
		lenient().when(this.actor.getAccountRepresentation()).thenReturn("actor@test.com");
		this.guestDomain = mock(AbstractDomain.class);
		lenient().when(this.guestDomain.getUuid()).thenReturn("guest-domain-uuid");
		lenient().when(this.guestDomain.getParentDomain()).thenReturn(this.actorDomain);
		this.guest = new Guest("Guest", "Doe", "guest@test.com");
		this.guest.setRole(Role.SIMPLE);
		this.guest.setDomain(this.guestDomain);
		this.guest.setLsUuid("guest-uuid");
		this.guest.setCreationDate(new Date());
		this.containerQuota = mock(ContainerQuota.class);
		lenient().when(this.containerQuota.getDomainQuota()).thenReturn(mock(DomainQuota.class));
		lenient().when(this.abstractDomainService.findGuestDomain(anyString())).thenReturn(this.guestDomain);
		lenient().when(this.guestBusinessService.exist(anyString(), anyString())).thenReturn(true);
		lenient().when(this.sanitizerInputHtmlBusinessService.strictClean(anyString())).thenAnswer(i -> i.getArgument(0));
		lenient().when(this.userService.findOrCreateUser(anyString(), anyString())).thenReturn(null);
		lenient().when(this.containerQuotaBusinessService.find(any(AbstractDomain.class), eq(ContainerQuotaType.USER)))
				.thenReturn(this.containerQuota);
		lenient().when(this.accountRepository.findAllModeratorUuidsByGuest(any(Guest.class))).thenReturn(Collections.emptyList());
		lenient().when(this.accountQuotaBusinessService.create(any(AccountQuota.class))).thenReturn(mock(AccountQuota.class));
	}

	/**
	 * Unit test that verifies the business logic of the create method:
	 * When guest is created and mailBuildingService.build() returns a non-null mail container,
	 * then notifierService.sendNotification() MUST be called
	 */
	@Test
	void testCreate_WhenMailIsNotNull_ShouldSendNotification() throws BusinessException {
		final MailContainerWithRecipient mailContainer = mock(MailContainerWithRecipient.class);
		final ResetGuestPassword resetPassword = mock(ResetGuestPassword.class);
		final Moderator moderator = mock(Moderator.class);
		lenient().when(this.guestBusinessService.create(
				any(User.class),
				any(Guest.class),
				any(AbstractDomain.class),
				nullable(List.class),
				nullable(List.class),
				nullable(Map.class)
		)).thenReturn(this.guest);
		lenient().when(this.resetGuestPasswordMongoRepository.insert(any(ResetGuestPassword.class))).thenReturn(resetPassword);
		lenient().when(resetPassword.getUuid()).thenReturn("reset-uuid");
		lenient().when(this.mailBuildingService.build(any(EmailContext.class))).thenReturn(mailContainer);
		lenient().when(this.moderatorService.create(
				any(Account.class),
				any(Account.class),
				any(Moderator.class),
				anyBoolean()
		)).thenReturn(moderator);
		this.guestService.create(this.actor, this.actor, this.guest, null, null, null);
		verify(this.mailBuildingService).build(any(EmailContext.class));
		verify(this.notifierService).sendNotification(mailContainer);
	}

	/**
	 * Unit test that verifies the business logic of the create method:
	 * When guest is created but mailBuildingService.build() returns null,
	 * then notifierService.sendNotification() MUST NOT be called
	 */
	@Test
	void testCreate_WhenMailIsNull_ShouldNotSendNotification() throws BusinessException {
		final ResetGuestPassword resetPassword = mock(ResetGuestPassword.class);
		final Moderator moderator = mock(Moderator.class);
		lenient().when(this.guestBusinessService.create(
				any(User.class),
				any(Guest.class),
				any(AbstractDomain.class),
				nullable(List.class),
				nullable(List.class),
				nullable(Map.class)
		)).thenReturn(this.guest);
		lenient().when(this.resetGuestPasswordMongoRepository.insert(any(ResetGuestPassword.class))).thenReturn(resetPassword);
		lenient().when(resetPassword.getUuid()).thenReturn("reset-uuid");
		lenient().when(this.mailBuildingService.build(any(EmailContext.class))).thenReturn(null);
		lenient().when(this.moderatorService.create(
				any(Account.class),
				any(Account.class),
				any(Moderator.class),
				anyBoolean()
		)).thenReturn(moderator);
		this.guestService.create(this.actor, this.actor, this.guest, null, null, null);
		verify(this.mailBuildingService).build(any(EmailContext.class));
		verify(this.notifierService, never()).sendNotification(any(MailContainerWithRecipient.class));
	}

	/**
	 * Unit test that verifies the business logic of the triggerResetPassword method:
	 * When password reset is triggered and mailBuildingService.build() returns a non-null mail container,
	 * then notifierService.sendNotification() MUST be called
	 */
	@Test
	void testTriggerResetPassword_WhenMailIsNotNull_ShouldSendNotification() throws BusinessException {
		final String guestUuid = "guest-uuid";
		final MailContainerWithRecipient mailContainer = mock(MailContainerWithRecipient.class);
		final ResetGuestPassword resetPassword = mock(ResetGuestPassword.class);
		when(this.guestBusinessService.findByLsUuid(guestUuid)).thenReturn(this.guest);
		when(this.resetGuestPasswordMongoRepository.findByGuestNotUsed(anyString(), any(Date.class)))
				.thenReturn(Collections.emptyList());
		when(this.resetGuestPasswordMongoRepository.insert(any(ResetGuestPassword.class))).thenReturn(resetPassword);
		when(resetPassword.getUuid()).thenReturn("reset-uuid");
		when(this.mailBuildingService.build(any(EmailContext.class))).thenReturn(mailContainer);
		this.guestService.triggerResetPassword(guestUuid);
		verify(this.mailBuildingService).build(any(EmailContext.class));
		verify(this.notifierService).sendNotification(mailContainer);
	}

	/**
	 * Unit test that verifies the business logic of the triggerResetPassword method:
	 * When password reset is triggered but mailBuildingService.build() returns null,
	 * then notifierService.sendNotification() MUST NOT be called
	 */
	@Test
	void testTriggerResetPassword_WhenMailIsNull_ShouldNotSendNotification() throws BusinessException {
		final String guestUuid = "guest-uuid";
		final ResetGuestPassword resetPassword = mock(ResetGuestPassword.class);
		when(this.guestBusinessService.findByLsUuid(guestUuid)).thenReturn(this.guest);
		when(this.resetGuestPasswordMongoRepository.findByGuestNotUsed(anyString(), any(Date.class)))
				.thenReturn(Collections.emptyList());
		when(this.resetGuestPasswordMongoRepository.insert(any(ResetGuestPassword.class))).thenReturn(resetPassword);
		when(resetPassword.getUuid()).thenReturn("reset-uuid");
		when(this.mailBuildingService.build(any(EmailContext.class))).thenReturn(null);
		this.guestService.triggerResetPassword(guestUuid);
		verify(this.mailBuildingService).build(any(EmailContext.class));
		verify(this.notifierService, never()).sendNotification(any(MailContainerWithRecipient.class));
	}

	/**
	 * Unit test that verifies the business logic of the triggerResetPassword (with email) method:
	 * When password reset is triggered by email and mailBuildingService.build() returns a non-null mail container,
	 * then notifierService.sendNotification() MUST be called
	 */
	@Test
	void testTriggerResetPasswordByEmail_WhenMailIsNotNull_ShouldSendNotification() throws BusinessException {
		final String email = "guest@test.com";
		final String domainUuid = "domain-uuid";
		final SystemAccount systemAccount = mock(SystemAccount.class);
		final MailContainerWithRecipient mailContainer = mock(MailContainerWithRecipient.class);
		final ResetGuestPassword resetPassword = mock(ResetGuestPassword.class);
		final AbstractDomain domain = mock(AbstractDomain.class);
		when(this.abstractDomainService.findById(domainUuid)).thenReturn(domain);
		when(this.guestBusinessService.find(any(AbstractDomain.class), eq(email))).thenReturn(this.guest);
		when(this.resetGuestPasswordMongoRepository.findByGuestNotUsed(anyString(), any(Date.class)))
				.thenReturn(Collections.emptyList());
		when(this.resetGuestPasswordMongoRepository.insert(any(ResetGuestPassword.class))).thenReturn(resetPassword);
		when(resetPassword.getUuid()).thenReturn("reset-uuid");
		when(this.mailBuildingService.build(any(EmailContext.class))).thenReturn(mailContainer);
		this.guestService.triggerResetPassword(systemAccount, email, domainUuid);
		verify(this.mailBuildingService).build(any(EmailContext.class));
		verify(this.notifierService).sendNotification(mailContainer);
	}

	/**
	 * Unit test that verifies the business logic of the triggerResetPassword (with email) method:
	 * When password reset is triggered by email but mailBuildingService.build() returns null,
	 * then notifierService.sendNotification() MUST NOT be called
	 */
	@Test
	void testTriggerResetPasswordByEmail_WhenMailIsNull_ShouldNotSendNotification() throws BusinessException {
		final String email = "guest@test.com";
		final String domainUuid = "domain-uuid";
		final SystemAccount systemAccount = mock(SystemAccount.class);
		final ResetGuestPassword resetPassword = mock(ResetGuestPassword.class);
		final AbstractDomain domain = mock(AbstractDomain.class);
		when(this.abstractDomainService.findById(domainUuid)).thenReturn(domain);
		when(this.guestBusinessService.find(any(AbstractDomain.class), eq(email))).thenReturn(this.guest);
		when(this.resetGuestPasswordMongoRepository.findByGuestNotUsed(anyString(), any(Date.class)))
				.thenReturn(Collections.emptyList());
		when(this.resetGuestPasswordMongoRepository.insert(any(ResetGuestPassword.class))).thenReturn(resetPassword);
		when(resetPassword.getUuid()).thenReturn("reset-uuid");
		when(this.mailBuildingService.build(any(EmailContext.class))).thenReturn(null);
		this.guestService.triggerResetPassword(systemAccount, email, domainUuid);
		verify(this.mailBuildingService).build(any(EmailContext.class));
		verify(this.notifierService, never()).sendNotification(any(MailContainerWithRecipient.class));
	}
}