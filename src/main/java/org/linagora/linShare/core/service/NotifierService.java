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
package org.linagora.linShare.core.service;

import org.linagora.linShare.core.domain.entities.MailContainer;

/** Provides notification services.
 *
 */
public interface NotifierService {
	
/**
 * Send notification to a recipient.
 * @param fromUser user who send the mail. can be null. if null put generic Linshare account.
 * @param recipient
 * @param subject
 * @param textContent alternative mail with text content
 * @param htmlContent html mail content
 */
    public void sendNotification(String fromUser,String recipient, String subject, String htmlContent,String textContent);
    
    /**
     * @param fromUser user who send the mail. can be null. if null put generic Linshare account.
     * @param recipient the recipient mail
     * @param mailContainer the mail container
     */
    public void sendNotification(String fromUser,String recipient, MailContainer mailContainer);
}
