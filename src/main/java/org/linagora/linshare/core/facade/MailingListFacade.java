/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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

import java.util.List;

import org.linagora.linshare.core.domain.vo.MailingListContactVo;
import org.linagora.linshare.core.domain.vo.MailingListVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;

public interface MailingListFacade {

	/**
	 * Basic operations on mailingList
	 */

	MailingListVo createList(UserVo actorVo, MailingListVo mailingListVo) throws BusinessException;

	MailingListVo findByUuid(UserVo actorVo, String uuid);

	List<MailingListVo> getAllMyList(UserVo actorVo, UserVo userVo) throws BusinessException;

	void updateList(UserVo actorVo, MailingListVo mailingListVo) throws BusinessException;

	void deleteList(UserVo actorVo, String uuid) throws BusinessException;

	/**
	 * Basic operations on mailingListContact
	 * @param actorVo
	 * 
	 * @throws BusinessException
	 */

	MailingListContactVo findContactByMail(UserVo actorVo, String listUuid, String mail) throws BusinessException;

	void updateContact(UserVo actorVo, MailingListContactVo contactVo) throws BusinessException;

	public void deleteContact(UserVo actorVo, String contactUuid) throws BusinessException;

	public MailingListContactVo searchContact(UserVo actorVo, String uuid) throws BusinessException;

	/**
	 * Add user to mailing list
	 * @param mailingListVo
	 * @param domainId
	 * @param mail
	 * @param domain
	 * @param mail
	 * 
	 * @throws BusinessException
	 *             list doesn't exist or user doesn't have rights to create
	 *             contact
	 */
	void addUserToList(UserVo actorVo, MailingListVo mailingListVo, String domainId, String mail) throws BusinessException;

	/**
	 * Add contact to mailing list
	 * 
	 * @param actorVo
	 * @param mailingListVo
	 * @param contactVo
	 * @throws BusinessException
	 *             list doesn't exist or user doesn't have rights to create
	 *             contact
	 */
	void addNewContactToList(UserVo actorVo, MailingListVo mailingListVo, MailingListContactVo contactVo)
			throws BusinessException;

	/**
	 * Set list of results from search
	 * 
	 * @param loginUser
	 * @param targetLists
	 * @param criteriaOnSearch
	 * @return
	 * @throws BusinessException
	 *             list doesn't exist
	 */
	List<MailingListVo> setListFromSearch(UserVo loginUser, String targetLists, String criteriaOnSearch)
			throws BusinessException;

	/**
	 * Provide completion for user search
	 * 
	 * @param actorVo
	 * @param pattern
	 * @return
	 * @throws BusinessException
	 *             user not find
	 */
	List<UserVo> completionOnUsers(UserVo actorVo, String pattern) throws BusinessException;

	boolean getListIsDeletable(UserVo actorVo, MailingListVo listVo) throws BusinessException;

	/**
	 * Check if new identifier doesn't exist
	 * 
	 * @param user
	 * @param purposedIdentifier
	 * @return true if and false if not
	 */
	boolean identifierIsAvailable(UserVo user, String purposedIdentifier);

	/**
	 * Purpose an alternative identifier
	 * 
	 * @param user
	 * @param value
	 * @return
	 */
	String findAvailableIdentifier(UserVo user, String value);

	public List<MailingListVo> completionForUploadForm(UserVo userVo, String input) throws BusinessException;

	public List<String> getAllContactMails(UserVo actorVo, MailingListVo ml) throws BusinessException;

}
