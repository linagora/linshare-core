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

package org.linagora.linshare.core.service;

import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface GuestService {

	/**
	 * Find a guest by is lsUuid
	 * @param actor TODO
	 * @param owner
	 *            who trigger the action
	 * @param lsUuid
	 *            guest lsUuid
	 * 
	 * @return guest found otherwise return null
	 * @throws BusinessException
	 */
	Guest find(Account actor, Account owner, String lsUuid) throws BusinessException;

	Guest find(Account actor, Account owner, String domainId, String mail) throws BusinessException;

	List<Guest> findAllMyGuests(Account actor, Account owner) throws BusinessException;

	/**
	 * 
	 * @param lsUuid
	 * @return
	 * @throws BusinessException
	 */
	boolean exist(String lsUuid) throws BusinessException;

	/**
	 * Create a guest
	 * @param actor TODO
	 * @param owner
	 *            who triggered the action
	 * @param guest
	 *            guest to create
	 * @return created guest
	 * @throws BusinessException
	 */
	Guest create(Account actor, Account owner, Guest guest)
			throws BusinessException;

	/**
	 * Update a guest
	 * @param actor TODO
	 * @param owner
	 *            who triggered the action
	 * @param guest
	 *            guest to update
	 * @return updated guest
	 * @throws BusinessException
	 */
	Guest update(Account actor, User owner, Guest guest)
			throws BusinessException;

	/**
	 * 
	 * @param actor TODO
	 * @param owner
	 *            who triggered the action
	 * @param lsUuid
	 *            guest lsUuid
	 * @throws BusinessException
	 */
	void delete(Account actor, User owner, String lsUuid) throws BusinessException;

	/**
	 * Clean outdated guest accounts
	 * 
	 * @param systemAccount
	 */
	void cleanExpiredGuests(SystemAccount systemAccount);

//	/**
//	 * Add a contact for a restricted guest
//	 * 
//	 * @param actor
//	 *            who trigger the action
//	 * @param lsUuid
//	 *            guest lsUuid
//	 * @param contactLsUuid
//	 * @throws BusinessException
//	 */
//	void addRestrictedContact(User actor, String lsUuid, String contactLsUuid)
//			throws BusinessException;
//
//	/**
//	 * Reset all restricted contacts of a guest
//	 * 
//	 * @param actor
//	 *            who trigger the action
//	 * @param lsUuid
//	 *            guest lsUuid
//	 * @param mailContacts
//	 *            new contact mail list
//	 * @throws BusinessException
//	 */
//	void resetContactRestrictions(User actor, String lsUuid,
//			List<String> mailContacts) throws BusinessException;
//
//	/**
//	 * Get all restricted contacts of a guest
//	 * 
//	 * @param actor
//	 *            who trigger the action
//	 * @param lsUuid
//	 *            guest lsUuid
//	 * @return contacts
//	 * @throws BusinessException
//	 */
//	List<AllowedContact> getRestrictedContacts(User actor, String lsUuid)
//			throws BusinessException;

	/**
	 * Reset guest password
	 * 
	 * @param lsUuid
	 *            guest lsUuid
	 * @throws BusinessException
	 */
	void resetPassword(String lsUuid) throws BusinessException;

	List<Guest> findOudatedGuests(Account actor) throws BusinessException;

	void deleteUser(SystemAccount systemAccount, String uuid) throws BusinessException;

//	/**
//	 * Remove restriction on contacts for a guest and delete all his contacts
//	 * 
//	 * @param actor
//	 *            who trigger the action
//	 * @param lsUuid
//	 *            guest lsUuid
//	 * @return updated guest
//	 * @throws BusinessException
//	 */
//	Guest removeContactRestriction(User actor, String lsUuid)
//			throws BusinessException;
}
