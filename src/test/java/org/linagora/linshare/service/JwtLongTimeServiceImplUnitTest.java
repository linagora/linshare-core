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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.business.service.JwtLongTimeBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.rac.JwtLongTimeResourceAccessControl;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.JwtService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.impl.JwtLongTimeServiceImpl;
import org.linagora.linshare.mongo.entities.PermanentToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtLongTimeServiceImplUnitTest {

	@InjectMocks
	private JwtLongTimeServiceImpl jwtLongTimeService;

	@Mock
	private JwtLongTimeBusinessService jwtLongTimeBusinessService;

	@Mock
	private NotifierService notifierService;

	@Mock
	private MailBuildingService mailBuildingService;

	@Mock
	private JwtService jwtService;

	@Mock
	private JwtLongTimeResourceAccessControl jwtLongTimeResourceAccessControl;

	@Mock
	private DomainPermissionBusinessService domainPermissionBusinessService;

	@Mock
	private AbstractDomainService abstractDomainService;

	@Mock
	private AccountRepository<Account> accountRepository;

	@Mock
	private LogEntryService logEntryService;

	@Mock
	private AuditLogEntryService auditLogEntryService;

	@Mock
	private FunctionalityReadOnlyService functionalityReadOnlyService;

	@Mock
	private DomainBusinessService domainBusinessService;

	@Mock
	private SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService;

	private User regularUser;
	private User adminUser;
	private PermanentToken permanentToken;

	@BeforeEach
	void setUp() {
		this.jwtLongTimeService = new JwtLongTimeServiceImpl(
				"test-issuer",
				this.jwtLongTimeBusinessService,
				this.notifierService,
				this.mailBuildingService,
				this.jwtService,
				this.jwtLongTimeResourceAccessControl,
				this.domainPermissionBusinessService,
				this.abstractDomainService,
				this.accountRepository,
				this.logEntryService,
				this.auditLogEntryService,
				this.functionalityReadOnlyService,
				this.domainBusinessService,
				this.sanitizerInputHtmlBusinessService
		);
		this.regularUser = mock(User.class);
		lenient().when(this.regularUser.getLsUuid()).thenReturn("user-uuid");
		lenient().when(this.regularUser.getMail()).thenReturn("user@test.com");
		lenient().when(this.regularUser.getFullName()).thenReturn("Regular User");
		lenient().when(this.regularUser.hasSuperAdminRole()).thenReturn(false);
		lenient().when(this.regularUser.getDomain()).thenReturn(mock(org.linagora.linshare.core.domain.entities.AbstractDomain.class));
		this.adminUser = mock(User.class);
		lenient().when(this.adminUser.getLsUuid()).thenReturn("admin-uuid");
		lenient().when(this.adminUser.getMail()).thenReturn("admin@test.com");
		lenient().when(this.adminUser.getFullName()).thenReturn("Admin User");
		lenient().when(this.adminUser.hasSuperAdminRole()).thenReturn(true);
		lenient().when(this.adminUser.getDomain()).thenReturn(mock(org.linagora.linshare.core.domain.entities.AbstractDomain.class));
		this.permanentToken = new PermanentToken("Test Token", "Test Description");
	}

	/**
	 * Unit test that verifies the business logic of the create method for regular users:
	 * When mailBuildingService.build() returns a non-null mail container for non-admin users,
	 * then notifierService.sendNotification() MUST be called
	 */
	@Test
	void testCreate_WhenRegularUserAndMailNotNull_ShouldSendNotification() throws BusinessException {
		final MailContainerWithRecipient mailContainer = mock(MailContainerWithRecipient.class);
		when(this.jwtService.generateToken(any(User.class), any(String.class), any(Date.class))).thenReturn("jwt-token");
		when(this.mailBuildingService.build(any(EmailContext.class))).thenReturn(mailContainer);
		this.jwtLongTimeService.create(this.regularUser, this.regularUser, this.permanentToken);
		verify(this.mailBuildingService).build(any(EmailContext.class));
		verify(this.notifierService).sendNotification(mailContainer);
	}

	/**
	 * Unit test that verifies the business logic of the create method for regular users:
	 * When mailBuildingService.build() returns null for non-admin users,
	 * then notifierService.sendNotification() MUST NOT be called
	 */
	@Test
	void testCreate_WhenRegularUserAndMailNull_ShouldNotSendNotification() throws BusinessException {
		when(this.jwtService.generateToken(any(User.class), any(String.class), any(Date.class))).thenReturn("jwt-token");
		when(this.mailBuildingService.build(any(EmailContext.class))).thenReturn(null);
		this.jwtLongTimeService.create(this.regularUser, this.regularUser, this.permanentToken);
		verify(this.mailBuildingService).build(any(EmailContext.class));
		verify(this.notifierService, never()).sendNotification(any(MailContainerWithRecipient.class));
	}

	/**
	 * Unit test that verifies the business logic of the create method for admin users:
	 * When user is admin, mailBuildingService.build() and notifierService.sendNotification() MUST NOT be called
	 * even if mailBuildingService.build() returns a non-null mail container
	 */
	@Test
	void testCreate_WhenAdminUser_ShouldNotSendNotification() throws BusinessException {
		final MailContainerWithRecipient mailContainer = mock(MailContainerWithRecipient.class);
		when(this.jwtService.generateToken(any(User.class), any(String.class), any(Date.class))).thenReturn("jwt-token");
		lenient().when(this.mailBuildingService.build(any(EmailContext.class))).thenReturn(mailContainer);
		this.jwtLongTimeService.create(this.adminUser, this.adminUser, this.permanentToken);
		verify(this.mailBuildingService, never()).build(any(EmailContext.class));
		verify(this.notifierService, never()).sendNotification(any(MailContainerWithRecipient.class));
	}

	/**
	 * Unit test that verifies the business logic of the delete method for regular users:
	 * When mailBuildingService.build() returns a non-null mail container for non-admin users,
	 * then notifierService.sendNotification() MUST be called
	 */
	@Test
	void testDelete_WhenRegularUserAndMailNotNull_ShouldSendNotification() throws BusinessException {
		final MailContainerWithRecipient mailContainer = mock(MailContainerWithRecipient.class);
		final PermanentToken existingToken = this.createMockPermanentToken();
		when(this.jwtLongTimeBusinessService.find(existingToken.getUuid())).thenReturn(existingToken);
		when(this.mailBuildingService.build(any(EmailContext.class))).thenReturn(mailContainer);
		this.jwtLongTimeService.delete(this.regularUser, this.regularUser, existingToken);
		verify(this.mailBuildingService).build(any(EmailContext.class));
		verify(this.notifierService).sendNotification(mailContainer);
	}

	/**
	 * Unit test that verifies the business logic of the delete method for regular users:
	 * When mailBuildingService.build() returns null for non-admin users,
	 * then notifierService.sendNotification() MUST NOT be called
	 */
	@Test
	void testDelete_WhenRegularUserAndMailNull_ShouldNotSendNotification() throws BusinessException {
		final PermanentToken existingToken = this.createMockPermanentToken();
		when(this.jwtLongTimeBusinessService.find(existingToken.getUuid())).thenReturn(existingToken);
		when(this.mailBuildingService.build(any(EmailContext.class))).thenReturn(null);
		this.jwtLongTimeService.delete(this.regularUser, this.regularUser, existingToken);
		verify(this.mailBuildingService).build(any(EmailContext.class));
		verify(this.notifierService, never()).sendNotification(any(MailContainerWithRecipient.class));
	}

	/**
	 * Unit test that verifies the business logic of the delete method for admin users:
	 * When user is admin, mailBuildingService.build() and notifierService.sendNotification() MUST NOT be called
	 * even if mailBuildingService.build() returns a non-null mail container
	 */
	@Test
	void testDelete_WhenAdminUser_ShouldNotSendNotification() throws BusinessException {
		final PermanentToken existingToken = this.createMockPermanentToken();
		final MailContainerWithRecipient mailContainer = mock(MailContainerWithRecipient.class);
		when(this.jwtLongTimeBusinessService.find(existingToken.getUuid())).thenReturn(existingToken);
		lenient().when(this.mailBuildingService.build(any(EmailContext.class))).thenReturn(mailContainer);
		this.jwtLongTimeService.delete(this.adminUser, this.adminUser, existingToken);
		verify(this.mailBuildingService, never()).build(any(EmailContext.class));
		verify(this.notifierService, never()).sendNotification(any(MailContainerWithRecipient.class));
	}

	private PermanentToken createMockPermanentToken() {
		final PermanentToken token = new PermanentToken();
		token.setUuid("token-uuid");
		token.setLabel("Test Token");
		token.setDescription("Test Description");
		token.setSubject("user@test.com");
		return token;
	}
}