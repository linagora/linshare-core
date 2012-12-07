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

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.entities.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

/**
 * Service building each mail content.
 * 
 * @author sduprey
 *
 */
public interface MailContentBuildingService {


	

	
	
	/**
	 * Notify a user that he received new sharing
	 */
	public MailContainerWithRecipient buildMailNewSharingWithRecipient(User actor,MailContainer mailContainer, User recipient, List<String> docNames, String linShareUrl,
			String linShareUrlParam, String password, boolean hasToDecrypt) throws BusinessException;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Notify a user that an outdated document will be soon deleted (secured storage disabled)
	 */
	public MailContainerWithRecipient buildMailUpcomingOutdatedDocument(DocumentEntry document, Integer days) throws BusinessException;
	
	
	/**
	 * notification for expired shares
	 * @param shareEntry
	 * @param days
	 * @return
	 * @throws BusinessException
	 */
	public MailContainerWithRecipient buildMailUpcomingOutdatedShare(ShareEntry shareEntry, Integer days) throws BusinessException;
	
	
	/**
	 * notification for expired anonymous shares
	 * @param shareEntry
	 * @param days
	 * @return
	 * @throws BusinessException
	 */
	public MailContainerWithRecipient buildMailUpcomingOutdatedShare(AnonymousShareEntry shareEntry, Integer days) throws BusinessException;	

	
	/**
	 * notification for document update
	 * @param anonymousShareEntry
	 * @param oldDocName
	 * @param fileSizeTxt
	 * @return
	 * @throws BusinessException
	 */
	public MailContainerWithRecipient buildMailSharedDocumentUpdated(AnonymousShareEntry anonymousShareEntry, String oldDocName, String fileSizeTxt)throws BusinessException;
	
	
	/**
	 * notification for document update
	 * @param shareEntry
	 * @param oldDocName
	 * @param fileSizeTxt
	 * @return
	 * @throws BusinessException
	 */
	public MailContainerWithRecipient buildMailSharedDocumentUpdated(ShareEntry shareEntry, String oldDocName, String fileSizeTxt)throws BusinessException;
	
	
	/**
	 * deletion notification
	 * @param actor 
	 * @param share
	 * @return
	 * @throws BusinessException
	 */
	public MailContainerWithRecipient buildMailSharedFileDeletedWithRecipient(Account actor, ShareEntry shareEntry) throws BusinessException;

	
	/**
	 * anonymous share notification
	 * @param inputMailContainer
	 * @param anonymousUrl
	 * @param sender
	 * @return
	 * @throws BusinessException
	 */
	public MailContainerWithRecipient buildMailNewSharingWithRecipient(MailContainer inputMailContainer, AnonymousUrl anonymousUrl, User sender) throws BusinessException;
	
	
	/**
	 * reset guest password notification
	 * @param recipient
	 * @param password
	 * @return
	 * @throws BusinessException
	 */
	public MailContainerWithRecipient buildMailResetPassword(Guest recipient, String password) throws BusinessException;
	
	
	/**
	 * Notify somebody that his linshare account has been created
	 * @param owner
	 * @param recipient
	 * @param password
	 * @return
	 * @throws BusinessException
	 */
	public MailContainerWithRecipient buildMailNewGuest(User owner,User recipient, String password) throws BusinessException;
	
	
	/**
	 * download notification for guest or internal user
	 * @param shareEntry
	 * @return
	 * @throws BusinessException
	 */
	public MailContainerWithRecipient buildMailRegisteredDownloadWithOneRecipient(ShareEntry shareEntry) throws BusinessException;
	
	
	/**
	 * Notify a user that shared files has been downloaded
	 * by an anonymous user
	 */
	public MailContainerWithRecipient buildMailAnonymousDownload(AnonymousShareEntry shareEntry) throws BusinessException;	
	
}