/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linshare.core.service;

import java.util.List;

import javax.mail.SendFailedException;

import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.entities.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;

/** Provides notification services.
 *
 */
public interface NotifierService {
	
	/**
	 * Send notification to a recipient.
	 * @param replyTo user who send the mail. can be null. if null put generic Linshare account.
	 * @param recipient
	 * @param subject
	 * @param textContent alternative mail with text content
	 * @param htmlContent html mail content
	 * @throws SendFailedException 
	 */
    public void sendNotification(String replyTo,String recipient, String subject, String htmlContent,String textContent) throws SendFailedException;
    
    /**
     * @param replyTo user who send the mail. can be null. if null put generic Linshare account.
     * @param recipient the recipient mail
     * @param mailContainer the mail container
     * @throws SendFailedException 
     */
    public void sendNotification(String replyTo,String recipient, MailContainer mailContainer) throws SendFailedException;

    
    /**
     * @param mailContainerWithRecipient list of mail containers attached to recipients
     * @throws BusinessException 
     */
    public void sendAllNotifications(List<MailContainerWithRecipient> mailContainerWithRecipient) throws BusinessException;
  
}
