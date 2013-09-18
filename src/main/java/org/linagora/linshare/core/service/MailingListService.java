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

package org.linagora.linshare.core.service;

import java.util.List;

import org.linagora.linshare.core.domain.entities.MailingList;
import org.linagora.linshare.core.domain.entities.MailingListContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface MailingListService {

	/**
	 * 
	 * @param actorUuid : actor
	 * @param ownerUuid : list owner 
	 * @param mailingList : mailing list to be created
	 * @return Mailing list created
	 * @throws BusinessException : could be raised if list already exists or you are not authorized. 
	 */
	MailingList createList(String actorUuid, String ownerUuid, MailingList mailingList) throws BusinessException;

	void deleteList(User actor, String mailingListUuid) throws BusinessException;

	void updateList(User actor, MailingList listToUpdate) throws BusinessException;
	
	MailingList retrieveList(String uuid) throws BusinessException;

	/**
	 * Find all list (private and public) of the selected user 
	 * @param user
	 * @return
	 */
	public List<MailingList> findAllListByUser(User user);

	

	/**
	 * Add contact to list
	 * @param actor
	 * @param mailingListUuid
	 * @param contact
	 * @throws BusinessException
	 */
	public void addNewContact(User actor, String mailingListUuid, MailingListContact contact) throws BusinessException;

	/**
	 * Find all list of the user 
	 * @param user
	 * @return
	 */
	public List<MailingList> findAllListByOwner(User user);

	public void deleteContact(String listUuid, String mail) throws BusinessException;

	public MailingListContact retrieveContact(MailingList mailingList, String mail) throws BusinessException;

	public void updateContact(MailingList list, MailingListContact contactToUpdate) throws BusinessException;

	/**
	 * retrieving all list from repository. Method allowed only for Root.
	 * 
	 * @param actor
	 * @param criteriaOnSearch
	 *            : could be "public", "private" or "all"
	 * @return list of MailingList objects.
	 */
	public List<MailingList> findAllListByVisibilityForAdmin(User user, String criteriaOnSearch);

	/**
	 * find list of result according to visibility selected and pattern
	 * @param actor
	 * @param criteriaOnSearch
	 * @param pattern
	 * @return
	 */
	public List<MailingList> findAllListByVisibilityForSearch(User actor, String criteriaOnSearch, String pattern);

	/**
	 * Find all user list of the selected visibility
	 * @param user
	 * @param criteriaOnSearch
	 * @return
	 */
	public List<MailingList> findAllListByVisibility(User user, String criteriaOnSearch);

	/**
	 * Find all list of the selected visibility
	 * @param criteriaOnSearch
	 * @param input
	 * @return
	 */
	public List<MailingList> findAllListByVisibilityForAdminSearch(String criteriaOnSearch, String input);
}
