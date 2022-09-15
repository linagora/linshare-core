/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
