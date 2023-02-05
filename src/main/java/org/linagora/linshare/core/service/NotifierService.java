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
	public void sendNotification(String smtpSender, String replyTo, String recipient, String subject,String htmlContent,
			String inReplyTo, String references, Map<String, DataSource> attachments) throws SendFailedException;

	/**
	 * @param mailContainers
	 *            list of mail containers attached to recipients
	 * @param skipUnreachableAddresses
	 * @throws BusinessException
	 */
	public void sendNotification(List<MailContainerWithRecipient> mailContainers,
			boolean skipUnreachableAddresses) throws BusinessException;

	public void sendNotification(List<MailContainerWithRecipient> mailContainers) throws BusinessException;

	public void sendNotification(MailContainerWithRecipient mailContainer, boolean skipUnreachableAddresses)
			throws BusinessException;

	public void sendNotification(MailContainerWithRecipient mailContainer) throws BusinessException;

	/**
	 * For JMX purpose.
	 */

	String getHost();

	void setHost(String host);

	Integer getPort();

	void setPort(Integer port) throws Exception;

}
