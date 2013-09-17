/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
package org.linagora.linshare.core.business.service;

import org.linagora.linshare.core.domain.entities.MailingList;
import org.linagora.linshare.core.domain.entities.MailingListContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import java.util.List;

public interface MailingListBusinessService {

	public MailingList createList(MailingList mailingList, User owner) throws BusinessException;

	public MailingList retrieveList(String uuid);

	public List<MailingList> findAllList();

	/**
	 * Find all list of the selected user (private and public)
	 * @param user
	 * @return
	 */
	public List<MailingList> findAllListByUser(User user);

	public void deleteList(String uuid) throws BusinessException;

	public void updateList(MailingList mailingList) throws BusinessException;
	
	/**
	 * Add contact to list
	 * @param mailingList
	 * @param contact
	 * @throws BusinessException
	 */
	public void addContact(MailingList mailingList, MailingListContact contact) throws BusinessException;

	/**
	 * Find all list where user is owner
	 * @param user
	 * @return
	 */
	public List<MailingList> findAllMyList(User user);

	public MailingListContact retrieveContact(MailingList mailingList, String mail) throws BusinessException;

	public void updateContact(MailingList list, MailingListContact contactToUpdate) throws BusinessException;

	public void deleteContact(String listUuid, String mail) throws BusinessException;

	/**
	 * Find all my list according to select visibility
	 * @param owner
	 * @param isPublic
	 * @return
	 */
	public List<MailingList> findAllListByVisibility(User owner, boolean isPublic);

	/**
	 * Find all list for root according to selected visibility
	 * @param isPublic
	 * @return
	 */
	public List<MailingList> findAllListByVisibilityForAdmin(boolean isPublic);

	/**
	 * Find All list according to pattern where user is owner
	 * @param user
	 * @param input
	 * @return
	 */
	public List<MailingList> findAllMyListsForSearch(User user, String input);

	/**
	 * Find all user list according to pattern
	 * @param user
	 * @param input
	 * @return
	 */
	public List<MailingList> findAllListByUserForSearch(User user, String input);

	/**
	 * Find all user list according to selected visibility and input
	 * @param owner
	 * @param isPublic
	 * @param input
	 * @return
	 */
	public List<MailingList> findAllListByVisibilityForSearch(User owner, boolean isPublic, String input);

	/**
	 * Find all list according to pattern for root
	 * @param input
	 * @return
	 */
	public List<MailingList> findAllListForAdminSearch(String input);

	/**
	 * Find all list according to selected visibility and pattern for root
	 * @param isPublic
	 * @param input
	 * @return
	 */
	public List<MailingList> findAllListByVisibilityForAdminSearch(boolean isPublic, String input);

}
