/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */

package org.linagora.linshare.core.service;

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface GuestService {

	/**
	 * Find a guest by is lsUuid
	 * @param actor who trigger the action
	 * @param owner
	 *            for who trigger the action
	 * @param lsUuid
	 *            guest lsUuid
	 * 
	 * @return guest found otherwise throws business exception
	 * @throws BusinessException
	 */
	Guest find(Account actor, Account owner, String lsUuid) throws BusinessException;

	List<AllowedContact> load(Account actor, User owner) throws BusinessException;

	Guest find(Account actor, Account owner, String domainUuid, String mail) throws BusinessException;

	/**
	 * find all guests according to domain access policies
	 * @param actor
	 * @param owner
	 * @return List<Guest>
	 * @throws BusinessException
	 */
	List<Guest> findAll(Account actor, Account owner, Boolean mine) throws BusinessException;

	/**
	 * find all guests according to domain access policies, using firstName or lastName or mail, or both.
	 * fragment pattern for this three parameter are supported.
	 * @param actor
	 * @param owner
	 * @param firstName
	 * @param lastName
	 * @param mail
	 * @return List<Guest>
	 * @throws BusinessException
	 */
	List<Guest> search(Account actor, Account owner, String firstName, String lastName, String mail, boolean all) throws BusinessException;

	/**
	 * find all guests according to domain access policies, using pattern as a fragment of firstName or lastName or mail.
	 * @param actor
	 * @param owner
	 * @param pattern
	 * @param mine TODO
	 * @return List<Guest>
	 * @throws BusinessException
	 */
	List<Guest> search(Account actor, Account owner, String pattern, Boolean mine) throws BusinessException;

	/**
	 * Test if a guest exists
	 * @param lsUuid
	 * @return boolean
	 * @throws BusinessException
	 */
	boolean exist(String lsUuid) throws BusinessException;

	/**
	 * Create a guest
	 * @param actor who triggered the action
	 * @param owner
	 *            for who triggered the action
	 * @param guest
	 *            guest to create
	 * @param restrictedMails TODO
	 * @return created guest
	 * @throws BusinessException
	 */
	Guest create(Account actor, Account owner, Guest guest, List<String> restrictedMails)
			throws BusinessException;

	/**
	 * Update a guest
	 * @param actor
	 * @param owner
	 * @param guest
	 * @param restrictedMails : if null, it won't be updated.
	 * @return Guest
	 * @throws BusinessException
	 */
	Guest update(Account actor, User owner, Guest guest, List<String> restrictedMails)
			throws BusinessException;

	/**
	 * 
	 * @param actor who triggered the action
	 * @param owner
	 *            for who triggered the action
	 * @param lsUuid
	 *            guest lsUuid
	 * @return the deleted object
	 * @throws BusinessException
	 */
	Guest delete(Account actor, User owner, String lsUuid) throws BusinessException;

	/**
	 * Reset guest password
	 * 
	 * @param lsUuid
	 *            guest lsUuid
	 * @throws BusinessException
	 */
	void triggerResetPassword(String lsUuid) throws BusinessException;

	/**
	 * Create a new token to reset guest password.
	 * 
	 * @param mail
	 *            : mail of the guest you want to reset
	 * @param domainUuid
	 *            : optional domain uuid (multidomain purpose)
	 * @throws BusinessException
	 */
	void triggerResetPassword(SystemAccount systemAccount, String mail, String domainUuid) throws BusinessException;

	List<String> findOudatedGuests(SystemAccount systemAccount) throws BusinessException;

	Guest findOudatedGuest(SystemAccount systemAccount, String uuid) throws BusinessException;

	void deleteUser(SystemAccount systemAccount, String uuid) throws BusinessException;

	Date getGuestExpirationDate(Account actor, Date currentGuestExpirationDate) throws BusinessException;

	SystemAccount getGuestSystemAccount();
}
