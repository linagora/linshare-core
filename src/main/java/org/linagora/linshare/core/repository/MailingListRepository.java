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
package org.linagora.linshare.core.repository;

import java.util.List;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface MailingListRepository extends AbstractRepository<ContactList> {

	ContactList findByUuid(String uuid);

	ContactList findByIdentifier(User owner, String identifier);

	List<ContactList> findAllListWhereOwner(User user);

	List<ContactList> findAllMyList(User user);

	List<ContactList> findAll(User user);

	List<ContactList> findAllMine(User user);

	List<ContactList> findAllOthers(User user);

	List<ContactList> findAllByMemberEmail(User user, String email);

	List<ContactList> findAllMineByMemberEmail(User user, String email);

	List<ContactList> findAllOthersByMemberEmail(User user, String email);

	List<ContactList> searchListByVisibility(User user, boolean isPublic);

	List<ContactList> searchListWithInput(User user, String input);

	List<ContactList> searchMyListWithInput(User user, String input);

	List<ContactList> searchWithInputByVisibility(User user, boolean isPublic, String input);

	List<ContactList> findAllByDomains(List<AbstractDomain> domains);

	ContactList update(ContactList entity) throws BusinessException;

	ContactList create(ContactList entity) throws BusinessException;

}
