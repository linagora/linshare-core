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

package org.linagora.linshare.core.business.service;

import java.util.List;

import org.linagora.linshare.core.business.service.impl.GuestBusinessServiceImpl.GuestWithMetadata;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface GuestBusinessService {

	Guest findByLsUuid(String lsUuid) throws BusinessException;

	List<AllowedContact> loadAllowedContacts(User guest) throws BusinessException;

	Guest find(AbstractDomain domain, String mail) throws BusinessException;

	Guest findByMail(String mail) throws BusinessException;

	List<Guest> findAll(List<AbstractDomain> authorizedDomains);

	List<Guest> findAllMyGuests(Account owner);

	List<Guest> findAllOthersGuests(List<AbstractDomain> domains, Account owner);

	List<String> findOutdatedGuestIdentifiers();

	Guest create(Account owner, Guest guest, AbstractDomain domain,
			List<User> allowedContacts)
			throws BusinessException;

	Guest update(Account owner, Guest entity, Guest guestDto,
			AbstractDomain domain, List<User> allowedContacts) throws BusinessException;

	void delete(Guest guest) throws BusinessException;

	boolean exist(String domainId, String mail);

	GuestWithMetadata resetPassword(Guest guest) throws BusinessException;

	Guest resetPassword(Guest guest, String password) throws BusinessException;

	void evict(Guest entity);

	/**
	 * search a guest using firstName and lastName and mail as a pattern. If a pattern is null, it is ignored.
	 * @param authorizedDomains
	 * @param firstName
	 * @param lastName
	 * @param mail
	 * @param owner : if owner is not null, the search will be limited to all guests managed my the owner parameter.
	 * @return
	 * @throws BusinessException
	 */
	List<Guest> search(List<AbstractDomain> authorizedDomains, String firstName, String lastName, String mail, Account owner) throws BusinessException;

	/**
	 * search a guest using input pattern as fragment of firstName or lastName or mail.
	 * @param authorizedDomains
	 * @param pattern
	 * @param owner : if owner is not null, the search will be limited to all guests managed my the owner parameter.
	 * @return
	 * @throws BusinessException
	 */
	List<Guest> search(List<AbstractDomain> authorizedDomains, String pattern) throws BusinessException;

	List<Guest> searchMyGuests(List<AbstractDomain> authorizedDomains, String pattern, Account owner) throws BusinessException;

	List<Guest> searchExceptGuests(List<AbstractDomain> authorizedDomains, String pattern, Account owner) throws BusinessException;

	SystemAccount getGuestSystemAccount();
}
