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
package org.linagora.linshare.core.business.service;

import java.util.List;
import java.util.Optional;

import org.linagora.linshare.core.business.service.impl.GuestBusinessServiceImpl.GuestWithMetadata;
import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

import javax.annotation.Nonnull;

public interface GuestBusinessService {

	Guest findByLsUuid2(String lsUuid) throws BusinessException;

	Guest findByLsUuid(String lsUuid) throws BusinessException;

	List<AllowedContact> loadAllowedContacts(User guest) throws BusinessException;

	Guest find(AbstractDomain domain, String mail) throws BusinessException;

	Guest findByMail(String mail) throws BusinessException;

	List<String> findOutdatedGuestIdentifiers();
	List<String> findAllGuests();

	Guest create(Account actor, Guest guest, AbstractDomain domain,
			List<User> allowedContacts)
			throws BusinessException;

	Guest update(Account actor, Guest entity, Guest guestDto,
			List<User> allowedContacts) throws BusinessException;

	void delete(Guest guest) throws BusinessException;

	boolean exist(String domainId, String mail);

	GuestWithMetadata resetPassword(Guest guest) throws BusinessException;

	void evict(Guest entity);

	SystemAccount getGuestSystemAccount();

	List<Guest> findAll(List<AbstractDomain> authorizedDomains,
			Optional<ModeratorRole> moderatorRole, Optional<User> moderatorAccount,
			Optional<String> pattern);

	Account convertGuestToInternalUser(@Nonnull final Account internalAccount, @Nonnull final Guest guestAccount) ;
}
