/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 *
 * Copyright (C) 2015 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.facade;

import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.linagora.linshare.core.domain.objects.MailContainer;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.domain.vo.SignatureVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;

public interface ShareFacade {

	/**
	 * Retrieve all the sharing received by a user
	 * @param recipient the user
	 * @return
	 * @throws BusinessException
	 */
	public List<ShareDocumentVo> getAllSharingReceivedByUser(UserVo recipient) throws BusinessException;

	/**
	 * Retrieve all the sharings of a file by a user
	 * @param sender the user
	 * @param document
	 * @return
	 */
	public List<ShareDocumentVo> getSharingsByUserAndFile(UserVo sender, DocumentVo document);

	/**
	 * Retrieve all the sharing urls of a file by a user (email)
	 * @param sender the user
	 * @param document
	 * @return a list of couples : the mail of the recipient and the expiration of the url
	 */
	public Map<String, Calendar> getAnonymousSharingsByUserAndFile(UserVo senderVo, DocumentVo documentVo);

	/**
	 * Delete a sharing
	 * @param share
	 * @param actor
	 * @throws BusinessException
	 */
	void deleteSharing(ShareDocumentVo share, UserVo actor) throws BusinessException;

    /** Create a local copy of a shared document.
     * @param shareDocumentVo shared document.
     * @param actorVo user that the document belongs to.
     * @return the DocumentVo corresponding to the local copy
     * @throws BusinessException if document is too large for user account or forbidden mime type.
     */
    public DocumentVo createLocalCopy(ShareDocumentVo shareDocumentVo, UserVo actorVo) throws BusinessException;

	/**
	 * This method returns true if we can enable or disable manually on the IHM the checkbox 'Secured Anonymous URL'.
	 * @param user domain identifier
	 * @return
	 */
	public boolean isVisibleSecuredAnonymousUrlCheckBox(String domainIdentifier);
	/**
	 * This method returns true if we the functionality is enabled or not.
	 * @param domainIdentifier
	 * @return
	 */
	public boolean getDefaultSecuredAnonymousUrlCheckBoxValue(String domainIdentifier);

	/**
	 * Get a ShareDocumentVo by the share persistenceId
	 * @param persistenceId
	 * @return
	 */
	public ShareDocumentVo getShareDocumentVoByUuid(UserVo actorVo, String uuid ) throws BusinessException;

	/**
	 * This method is desinged to update only share comment.
	 * @param actorVo
	 * @param uuid : share uuid
	 * @param comment
	 * @throws IllegalArgumentException
	 * @throws BusinessException
	 */
	public void updateShareComment(UserVo actorVo, String uuid, String comment) throws IllegalArgumentException, BusinessException ;

	public InputStream getShareThumbnailStream(UserVo actorVo, String shareEntryUuid) throws BusinessException;

	public InputStream getShareStream(UserVo actorVo, String shareEntryUuid) throws BusinessException;

	public boolean isSignedShare(UserVo actorVo, ShareDocumentVo shareVo);

	public boolean isSignedShare(UserVo actorVo, String shareVoIdentifier);

	public SignatureVo getSignature(UserVo actorVo, ShareDocumentVo documentVo);

	public List<SignatureVo> getAllSignatures(UserVo actorVo, ShareDocumentVo documentVo);

	public void share(UserVo actorVo, List<DocumentVo> documentVos,
			List<String> recipientsEmail,
			boolean secured, MailContainer mailContainer)
			throws BusinessException;
}

