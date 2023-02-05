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

import java.util.List;
import java.util.Optional;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.domain.entities.fields.UserFields;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.utils.PageContainer;

public interface UserService2 {

	PageContainer<User> findAll(Account authUser, Account actor, List<String> domainsUuids, SortOrder sortOrder,
			UserFields sortField, String mail, String firstName, String lastName, Boolean restricted,
			Boolean canCreateGuest, Boolean canUpload, String role, String type, String moderatorRole, Optional<Integer> greaterThan,
			Optional<Integer> lowerThan, PageContainer<User> container);

	PageContainer<User> findAll(Account authUser, Account actor, List<String> domainsUuids, SortOrder sortOrder,
								UserFields sortField, String mail, String firstName, String lastName, Boolean restricted,
								Boolean canCreateGuest, Boolean canUpload, String role, String type, PageContainer<User> container);

	List<User> autoCompleteUser(Account authUser, Account actor, String pattern) throws BusinessException;

	public User find(Account authUser, Account actor, String lsUuid);

	public User update(Account authUser, Account actor, User userToUpdate, String domainId) throws BusinessException;

	public User delete(Account authUser, Account actor, String uuid);

	public User unlock(Account authUser, Account actor, User accountToUnlock) throws BusinessException;

	public List<AllowedContact> findAllRestrictedContacts(Account authUser, Account actor, User user,
			String mail, String firstName, String lastName);

	public AllowedContact findRestrictedContact(Account authUser, Account actor, User owner, String restrictedContactUuid);

	public AllowedContact deleteRestrictedContact(Account authUser, Account actor, User owner, String restrictedContactUuid);

	public AllowedContact createRestrictedContact(Account authUser, Account actor, AllowedContact allowedContactToCreate);
}
