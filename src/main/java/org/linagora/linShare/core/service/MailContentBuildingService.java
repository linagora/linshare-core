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

import java.util.List;

import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.exception.BusinessException;

/**
 * Service building each mail content.
 * 
 * @author sduprey
 *
 */
public interface MailContentBuildingService {

	/**
	 * Notify a user that shared files has been downloaded
	 * by an anonymous user
	 */
	public MailContainer buildMailAnonymousDownload(
			MailContainer mailContainer, List<Document> docs, String email,
			User recipient) throws BusinessException;

	/**
	 * Notify a user that shared files has been downloaded
	 * by a registered user
	 */
	public MailContainer buildMailRegisteredDownload(
			MailContainer mailContainer, List<Document> docs,
			User downloadingUser, User recipient) throws BusinessException;

	/**
	 * Notify somebody that his linshare account has been created
	 */
	public MailContainer buildMailNewGuest(MailContainer mailContainer,
			User owner, User recipient, String password)
			throws BusinessException;

	/**
	 * Notify a guest user of his new password
	 */
	public MailContainer buildMailResetPassword(MailContainer mailContainer,
			User recipient, String password) throws BusinessException;

	/**
	 * Notify a user that he received new sharings
	 */
	public MailContainer buildMailNewSharing(MailContainer mailContainer,
			User owner, User recipient, List<Document> docs,
			String personalMessage, String linShareUrl,
			String linShareUrlParam, String password, boolean hasToDecrypt)
			throws BusinessException;

	/**
	 * Notify a user that some shared files has been updated
	 * by the sender
	 */
	public MailContainer buildMailSharedDocUpdated(MailContainer mailContainer,
			User owner, User recipient, Document document, String oldDocName,
			String fileSizeTxt, String linShareUrl, String linShareUrlParam,
			boolean hasToDecrypt) throws BusinessException;

}