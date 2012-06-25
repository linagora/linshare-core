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

import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.entities.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.entities.SecuredUrl;
import org.linagora.linshare.core.domain.entities.Share;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.exception.BusinessException;

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
	public MailContainer buildMailAnonymousDownload(User actor,
			MailContainer mailContainer, List<Document> docs, String email,
			User recipient) throws BusinessException;
	
	/**
	 * Notify a user that shared files has been downloaded
	 * by a registered user
	 */
	public MailContainer buildMailRegisteredDownload(User actor,
			MailContainer mailContainer, List<Document> docs,
			User downloadingUser, User recipient) throws BusinessException;
	

	/**
	 * Notify somebody that his linshare account has been created
	 */
	public MailContainer buildMailNewGuest(User actor,MailContainer mailContainer,
			User owner, User recipient, String password)
			throws BusinessException;


	/**
	 * Notify a guest user of his new password
	 */
	public MailContainer buildMailResetPassword(User actor,MailContainer mailContainer,
			User recipient, String password) throws BusinessException;

	/**
	 * Notify a user that he received new sharing
	 */
	public MailContainer buildMailNewSharing(User actor,MailContainer mailContainer,
			User owner, User recipient, List<DocumentVo> docs,
			String linShareUrl, String linShareUrlParam, String password, 
			boolean hasToDecrypt, String jwsEncryptUrl)
			throws BusinessException;

	/**
	 * Notify a user that he received new sharing
	 */
	public MailContainer buildMailNewSharing(User actor,MailContainer mailContainer,
			User owner, String recipientMail, List<DocumentVo> docs, String linShareUrl,
			String linShareUrlParam, String password, 
			boolean hasToDecrypt, String jwsEncryptUrl)
			throws BusinessException;

	/**
	 * Notify a user that some shared files has been updated
	 * by the sender
	 */
	public MailContainer buildMailSharedDocUpdated(User actor,MailContainer mailContainer,
			User owner, User recipient, Document document, String oldDocName,
			String fileSizeTxt, String linShareUrl, String linShareUrlParam) 
			throws BusinessException;

	/**
	 * Notify a user that some shared files has been updated
	 * by the sender
	 */
	public MailContainer buildMailSharedDocUpdated(User actor,MailContainer mailContainer,
			User owner, String recipientMail, Document document, String oldDocName,
			String fileSizeTxt, String linShareUrl, String linShareUrlParam) 
			throws BusinessException;


	/**
	 * Notify a user that a received shared file is about to be deleted by its owner.
	 */
	public MailContainer buildMailSharedFileDeleted(User actor,
			MailContainer mailContainer, Document doc, User owner, User receiver)
			throws BusinessException;

	public MailContainer buildMailSharedFileDeleted(User actor,
			MailContainer mailContainer, Document doc, User owner, Contact receiver)
			throws BusinessException;
	

	/**
	 * Notify a user that received a share that the share will soon be deleted
	 */
	public MailContainer buildMailUpcomingOutdatedSecuredUrl(User actor,
			MailContainer mailContainer, SecuredUrl securedUrl,
			Contact recipient, Integer days, String securedUrlWithParam) throws BusinessException;

	public MailContainer buildMailUpcomingOutdatedShare(User actor,
			MailContainer mailContainer, Share share, Integer days) 
			throws BusinessException;
	

	/**
	 * Notify a user that an outdated document will be soon deleted (secured storage disabled)
	 */
	public MailContainer buildMailUpcomingOutdatedDocument(User actor,
			MailContainer mailContainer, Document document, Integer days) throws BusinessException;
	
	
	
	
	
	
	
	
	/**
	 * WithRecipicient Functions
	 */
	
	
	
	
	/**
	 * Notify a user that shared files has been downloaded
	 * by an anonymous user
	 */
	public MailContainerWithRecipient buildMailAnonymousDownloadWithRecipient(User actor,
			MailContainer mailContainer, List<Document> docs, String email,
			User recipient) throws BusinessException;

	public List<MailContainerWithRecipient> buildMailAnonymousDownloadWithOneRecipient(User actor,
			MailContainer mailContainer, List<Document> docs, String email,
			User recipient) throws BusinessException;	
	
	/**
	 * Notify a user that shared files has been downloaded
	 * by a registered user
	 */
	public MailContainerWithRecipient buildMailRegisteredDownloadWithRecipient(User actor,
			MailContainer mailContainer, List<Document> docs,
			User downloadingUser, User recipient) throws BusinessException;
	

	public List<MailContainerWithRecipient> buildMailRegisteredDownloadWithOneRecipient(User actor,
			MailContainer mailContainer, List<Document> docs,
			User downloadingUser, User recipient) throws BusinessException;
	
	/**
	 * Notify somebody that his linshare account has been created
	 */
	public MailContainerWithRecipient buildMailNewGuestWithRecipient(User actor,MailContainer mailContainer,
			User owner, User recipient, String password)
			throws BusinessException;

	public List<MailContainerWithRecipient> buildMailNewGuestWithOneRecipient(User actor,MailContainer mailContainer,
			User owner, User recipient, String password)
			throws BusinessException;

	/**
	 * Notify a guest user of his new password
	 */
	public MailContainerWithRecipient buildMailResetPasswordWithRecipient(User actor,MailContainer mailContainer,
			User recipient, String password) throws BusinessException;

	public List<MailContainerWithRecipient> buildMailResetPasswordWithOneRecipient(User actor,MailContainer mailContainer,
			User recipient, String password) throws BusinessException;

	/**
	 * Notify a user that he received new sharing
	 */
	public MailContainerWithRecipient buildMailNewSharingWithRecipient(User actor,MailContainer mailContainer,
			User owner, User recipient, List<DocumentVo> docs,
			String linShareUrl, String linShareUrlParam, String password, 
			boolean hasToDecrypt, String jwsEncryptUrl)
			throws BusinessException;

	/**
	 * Notify a user that he received new sharing
	 */
	public MailContainerWithRecipient buildMailNewSharingWithRecipient(User actor,MailContainer mailContainer,
			User owner, String recipientMail, List<DocumentVo> docs, String linShareUrl,
			String linShareUrlParam, String password, 
			boolean hasToDecrypt, String jwsEncryptUrl)
			throws BusinessException;

	/**
	 * Notify a user that some shared files has been updated
	 * by the sender
	 */
	public MailContainerWithRecipient buildMailSharedDocUpdatedWithRecipient(User actor,MailContainer mailContainer,
			User owner, User recipient, Document document, String oldDocName,
			String fileSizeTxt, String linShareUrl, String linShareUrlParam) 
			throws BusinessException;

	/**
	 * Notify a user that some shared files has been updated
	 * by the sender
	 */
	public MailContainerWithRecipient buildMailSharedDocUpdatedWithRecipient(User actor,MailContainer mailContainer,
			User owner, String recipientMail, Document document, String oldDocName,
			String fileSizeTxt, String linShareUrl, String linShareUrlParam) 
			throws BusinessException;

	/**
	 * Notify a user that a received shared file is about to be deleted by its owner.
	 */
	public MailContainerWithRecipient buildMailSharedFileDeletedWithRecipient(User actor,
			MailContainer mailContainer, Document doc, User owner, User receiver)
			throws BusinessException;

	public MailContainerWithRecipient buildMailSharedFileDeletedWithRecipient(User actor,
			MailContainer mailContainer, Document doc, User owner, Contact receiver)
			throws BusinessException;
	

	/**
	 * Notify a user that received a share that the share will soon be deleted
	 */
	public MailContainerWithRecipient buildMailUpcomingOutdatedSecuredUrlWithRecipient(User actor,
			MailContainer mailContainer, SecuredUrl securedUrl,
			Contact recipient, Integer days, String securedUrlWithParam) throws BusinessException;

	public MailContainerWithRecipient buildMailUpcomingOutdatedShareWithRecipient(User actor,
			MailContainer mailContainer, Share share, Integer days) 
			throws BusinessException;
	
	public List<MailContainerWithRecipient> buildMailUpcomingOutdatedShareWithOneRecipient(User actor,
			MailContainer mailContainer, Share share, Integer days) 
			throws BusinessException;	

	/**
	 * Notify a user that an outdated document will be soon deleted (secured storage disabled)
	 */
	public MailContainerWithRecipient buildMailUpcomingOutdatedDocumentWithRecipient(User actor,
			MailContainer mailContainer, Document document, Integer days) throws BusinessException;

	public List<MailContainerWithRecipient> buildMailUpcomingOutdatedDocumentWithOneRecipient(User actor, 
			MailContainer mailContainer, Document document, Integer days)
			throws BusinessException;
	
	
	
}