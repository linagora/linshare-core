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
package org.linagora.linshare.core.service;

import java.util.List;
import java.util.Map;

import javax.activation.DataSource;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.mail.SendFailedException;

import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;

/** Provides notification services.
 *
 */
public interface NotifierService {
	
    /**
     * 
     * Send notification to a recipient.
	 * @param smtpSender the smtp sender mail, for the "From" field of the mail.
     * @param replyTo user who send the mail. can be null. if null put generic Linshare account.
     * @param recipient
     * @param subject
     * @param htmlContent html mail content
     * @param inReplyTo : message ID
     * @param references : message ID list
	 * @throws SendFailedException
     */
	public void sendNotification(@Nonnull final String smtpSender, @Nullable final String replyTo, @Nonnull final String recipient, @Nonnull final String subject, @Nonnull final String htmlContent,
			@Nullable final String inReplyTo, @Nullable final String references, @Nullable final Map<String, DataSource> attachments) throws SendFailedException;

	/**
	 * Sends email notifications to recipients from the provided mail containers.
	 *
	 * @param mailContainers List of mail containers with recipients and content (non-null, no null elements)
	 * @return List of failed recipient addresses (empty if all succeeded)
	 */
	public @Nonnull List<String> sendNotification(@Nonnull final List<MailContainerWithRecipient> mailContainers) throws BusinessException;

	/**
	 * Sends a single email notification.
	 * @param mailContainer email to send (non-null)
	 * @return {@code true} if sent successfully, {@code false} if an error about unreachable recipient ({@link SendFailedException}) occurs.
	 * @throws BusinessException an error different from {@link SendFailedException} occurs.
	 */
	public default boolean sendNotification(@Nonnull MailContainerWithRecipient mailContainer) throws BusinessException {
			return this.sendNotification(List.of(mailContainer)).isEmpty();
	}

	/**
	 * For JMX purpose.
	 */

	String getHost();

	void setHost(String host);

	int getPort();

	void setPort(int port) throws Exception;

}
