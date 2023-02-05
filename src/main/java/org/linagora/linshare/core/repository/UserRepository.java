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
import java.util.Optional;
import java.util.Set;

import org.hibernate.criterion.Order;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.view.tapestry.beans.AccountOccupationCriteriaBean;
import org.linagora.linshare.webservice.utils.PageContainer;

public interface UserRepository<T extends User> extends AccountRepository<T> {

    /** Find a user using its mail.
     * @param mail
     * @return  user, null if not found.
     */
    T findByMail(String mail);
    
    /**
     * Return a list of mails beginning with the text
     * @param beginWith
     * @return List<String>
     */
    List<String> findMails(String beginWith);
    
	List<T> findByCriteria(AccountOccupationCriteriaBean criteria);

	/** Find a user using its login.
	 * @param login : ie mail or ldap uid.
     * @return  user, null if not found.
     */
	T findByLogin(String login);

	/** Find a user using its domain and login.
     * @param domain : domain identifier
     * @param login : ie mail or ldap uid.
     * @return  user, null if not found.
     */
	T findByLoginAndDomain(String domain, String login);

	PageContainer<T> findAll(List<AbstractDomain> domains, Order sortOrder, String mail, String firstName, String lastName,
							 Boolean restricted, Boolean canCreateGuest, Boolean canUpload, Role role, AccountType type,
							 Set<Long> subset, PageContainer<T> container);

	List<T> autoCompleteUser(List<AbstractDomain> domains, String mail);

	List<T> autoCompleteUser(List<AbstractDomain> domains, String firstName, String lastName);

	Set<Long> findGuestWithModerators(Optional<Integer> greaterThan, Optional<Integer> lessThan, ModeratorRole role);
} 
