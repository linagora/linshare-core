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
import org.linagora.linShare.core.domain.entities.Group;
import org.linagora.linShare.core.domain.entities.GroupMember;
import org.linagora.linShare.core.domain.entities.GroupMembershipStatus;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.vo.DocumentVo;
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
	 * Notify a user that he received new sharing
	 */
	public MailContainer buildMailNewSharing(MailContainer mailContainer,
			User owner, User recipient, List<DocumentVo> docs,
			String linShareUrl, String linShareUrlParam, String password, 
			boolean hasToDecrypt, String jwsEncryptUrl)
			throws BusinessException;

	/**
	 * Notify a user that he received new sharing
	 */
	public MailContainer buildMailNewSharing(MailContainer mailContainer,
			User owner, String recipientMail, List<DocumentVo> docs, String linShareUrl,
			String linShareUrlParam, String password, 
			boolean hasToDecrypt, String jwsEncryptUrl)
			throws BusinessException;

	/**
	 * Notify a user that some shared files has been updated
	 * by the sender
	 */
	public MailContainer buildMailSharedDocUpdated(MailContainer mailContainer,
			User owner, User recipient, Document document, String oldDocName,
			String fileSizeTxt, String linShareUrl, String linShareUrlParam) 
			throws BusinessException;

	/**
	 * Notify a user that some shared files has been updated
	 * by the sender
	 */
	public MailContainer buildMailSharedDocUpdated(MailContainer mailContainer,
			User owner, String recipientMail, Document document, String oldDocName,
			String fileSizeTxt, String linShareUrl, String linShareUrlParam) 
			throws BusinessException;

	/**
	 * Notify a user that the group received new sharing
	 */
	public MailContainer buildMailNewGroupSharing(MailContainer mailContainer,
			User owner, User recipient, Group group, List<DocumentVo> docs,
			String linShareUrl, String linShareUrlParam)
			throws BusinessException;

	/**
	 * Notify a group (functional email) for a new sharing
	 */
	public MailContainer buildMailNewGroupSharing(MailContainer mailContainer,
			User owner, Group group, List<DocumentVo> docs,
			String linShareUrl, String linShareUrlParam)
			throws BusinessException;

	/**
	 * Notify a group (functional email) that a sharing has been deleted
	 */
	public MailContainer buildMailGroupSharingDeleted(
			MailContainer mailContainer, User manager, Group group, Document doc)
			throws BusinessException;

	/**
	 * Notify user that a group sharing has been deleted
	 */
	public MailContainer buildMailGroupSharingDeleted(
			MailContainer mailContainer, User manager, User user, Group group, Document doc)
			throws BusinessException;

	/**
	 * Notify sbdy who request the membership of another user for a group
	 * of the status of his request
	 */
	public MailContainer buildMailGroupMembershipStatus(MailContainer mailContainer, 
			GroupMember newMember, Group group, GroupMembershipStatus status)
			throws BusinessException;
	
	/**
	 * Notify a user that he is now member of one group
	 */
	public MailContainer buildMailNewGroupMember(
			MailContainer mailContainer, GroupMember newMember, Group group) 
			throws BusinessException;
}