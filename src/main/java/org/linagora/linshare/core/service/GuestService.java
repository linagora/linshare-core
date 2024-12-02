/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.AccountContactLists;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

import javax.annotation.Nonnull;

public interface GuestService {

	/**
	 * Find a guest by is lsUuid
	 * @param authUser who trigger the action
	 * @param actor
	 *            for who trigger the action
	 * @param lsUuid
	 *            guest lsUuid
	 * 
	 * @return guest found otherwise throws business exception
	 * @throws BusinessException
	 */
	Guest find(Account authUser, Account actor, String lsUuid) throws BusinessException;

	List<AllowedContact> load(Account authUser, User actor) throws BusinessException;

	Guest find(Account authUser, Account actor, String domainUuid, String mail) throws BusinessException;

	/**
	 * Test if a guest exists
	 * @param lsUuid
	 * @return boolean
	 * @throws BusinessException
	 */
	boolean exist(String lsUuid) throws BusinessException;

	/**
	 * Create a guest
	 * @param authUser who triggered the action
	 * @param actor
	 *            for who triggered the action
	 * @param guest
	 *            guest to create
	 * @param restrictedMails TODO
	 * @return created guest
	 * @throws BusinessException
	 */
	Guest create(Account authUser, Account actor, Guest guest, List<String> restrictedMails, List<String> restrictedContactUuid)
			throws BusinessException;

	/**
	 * Update a guest
	 * @param authUser
	 * @param actor
	 * @param guest
	 * @param restrictedMails : if null, it won't be updated.
	 * @param restrictedContactUuid : if null, it won't be updated.
	 * @return Guest
	 * @throws BusinessException
	 */
	Guest update(Account authUser, User actor, Guest guest, List<String> restrictedMails, List<String> restrictedContactUuid)
			throws BusinessException;

	/**
	 * 
	 * @param actor who triggered the action
	 * @param authUser
	 *            for who triggered the action
	 * @param lsUuid
	 *            guest lsUuid
	 * @return the deleted object
	 * @throws BusinessException
	 */
	Guest delete(Account authUser, User actor, String lsUuid) throws BusinessException;

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

	Date getGuestExpirationDate(Account authUser, Date currentGuestExpirationDate) throws BusinessException;

	SystemAccount getGuestSystemAccount();

	/**
	 * This method is used to findAll guests where a user is moderator of it is used on:
	 *	- ADMIN API: the 'userToFilterBy' should be set
	 *	- USER API: the 'userToFilterBy' will be the actor
	 * @param authUser
	 * @param actor
	 * @param userToFilterBy
	 * @param pattern
	 * @param moderatorRole
	 * @return List<Guest>
	 */
	List<Guest> findAll(Account authUser, User actor, Optional<User> userToFilterBy, Optional<String> pattern,
			Optional<ModeratorRole> moderatorRole);

	void convertGuestToInternalUser(@Nonnull final SystemAccount systemAccount,@Nonnull final Account authUser,@Nonnull final User guestUser);

}
