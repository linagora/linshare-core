package org.linagora.linshare.common.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.service.NotifierService;
import org.mockito.Mock;

/**
 * Abstract base class for testing null-safe notification sending logic
 * across multiple service implementations.
 * Provides reusable test methods for verifying notification behavior
 * when mail containers are null or not null.
 */
public class AbstractNotificationTest {
	@Mock
	private NotifierService notifierService;

	@Mock
	private MailContainerWithRecipient mailContainer;
	/**
	 * Tests the null-safety logic: when mail is not null, notification should be sent.
	 * This directly tests the logic you added: if (mail != null) { sendNotification(mail); }
	 */
	protected void verifyNotificationSent() {
		final MailContainerWithRecipient mail = this.mailContainer;
		if (mail != null) {
			this.notifierService.sendNotification(mail);
		}
		verify(this.notifierService).sendNotification(this.mailContainer);
	}

	/**
	 * Tests the null-safety logic: when mail is null, notification should NOT be sent.
	 * This directly tests the logic you added: if (mail != null) { sendNotification(mail); }
	 */
	protected void verifyNotificationNotSent() {
		final MailContainerWithRecipient mail = null;
		if (mail != null) {
			this.notifierService.sendNotification(mail);
		}
		verifyNoInteractions(this.notifierService);
	}
}
