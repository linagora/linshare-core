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

package org.linagora.linshare.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.business.service.MailingListBusinessService;
import org.linagora.linshare.core.domain.entities.MailingList;
import org.linagora.linshare.core.domain.entities.MailingListContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.MailingListService;
import org.linagora.linshare.core.service.UserService;
import org.springframework.util.Assert;

public class MailingListServiceImpl implements MailingListService {

	private final MailingListBusinessService mailingListBusinessService;
	
	private final UserService userService;

	public MailingListServiceImpl(MailingListBusinessService mailingListBusinessService, UserService userService) {
		super();
		this.mailingListBusinessService = mailingListBusinessService;
		this.userService = userService;
	}

	@Override
	public MailingList createList(User user, MailingList mailingList) throws BusinessException {
		User actor = userService.findByLsUuid(user.getLsUuid());
		if(!actor.isSuperAdmin()){
			mailingList.setOwner(actor);
			mailingList.setDomain(actor.getDomain());
			return mailingListBusinessService.createList(mailingList);
		}
		return null;
	}

	@Override
	public void createContact(MailingListContact contact) throws BusinessException {
		mailingListBusinessService.createContact(contact);
	}

	@Override
	public MailingList retrieveList(String uuid) {
		return mailingListBusinessService.retrieveList(uuid);
	}

	@Override
	public MailingList findListByIdentifier(User owner, String identifier) {
		User actor = userService.findByLsUuid(owner.getLsUuid());
		return mailingListBusinessService.findListByIdentifier(actor, identifier);
	}

	@Override
	public MailingListContact retrieveContact(MailingList mailingList, String mail) throws BusinessException {
		return mailingListBusinessService.retrieveContact(mailingList, mail);
	}

	@Override
	public List<MailingList> findAllList() {
		return mailingListBusinessService.findAllList();
	}

	@Override
	public List<MailingList> findAllListByUser(User user) {
		return mailingListBusinessService.findAllListByUser(user);
	}

	@Override
	public List<MailingList> findAllListByVisibilityForSearch(User user, String criteriaOnSearch, String input) {
		boolean isPublic;
		if (criteriaOnSearch.equals("all")) {
			return mailingListBusinessService.findAllListByUserForSearch(user, input);
		} else if (criteriaOnSearch.equals("allMyLists")) {
			return mailingListBusinessService.findAllMyListsForSearch(user, input);
		} else if (criteriaOnSearch.equals("public")) {
			isPublic = true;
		} else {
			isPublic = false;
		}
		return mailingListBusinessService.findAllListByVisibilityForSearch(user, isPublic, input);
	}
	
	@Override
	public List<MailingList> findAllListByVisibilityForAdminSearch(String criteriaOnSearch, String input) {
		List<MailingList> result = new ArrayList<MailingList>();

			if (criteriaOnSearch.equals("all")) {
				result = mailingListBusinessService.findAllListForAdminSearch(input);
			} else {
				boolean isPublic;
				if (criteriaOnSearch.equals("public")) {
					isPublic = true;
				} else {
					isPublic = false;
				}
				result = mailingListBusinessService.findAllListByVisibilityForAdminSearch(isPublic, input);
			}
		return result;
	}
	
	@Override
	public List<MailingList> findAllListByVisibility(User user, String criteriaOnSearch) {
		boolean isPublic;
		if (criteriaOnSearch.equals("all")) {
			return mailingListBusinessService.findAllListByUser(user);
		} else if (criteriaOnSearch.equals("allMyLists")) {
			return mailingListBusinessService.findAllMyList(user);
		} else if (criteriaOnSearch.equals("public")) {
			isPublic = true;
		} else {
			isPublic = false;
		}
		return mailingListBusinessService.findAllListByVisibility(user, isPublic);
	}

	@Override
	public List<MailingList> findAllListByVisibilityForAdmin(User actor, String criteriaOnSearch) {
		List<MailingList> result = new ArrayList<MailingList>();
		if (actor.isSuperAdmin()) {
			if (criteriaOnSearch.equals("all")) {
				result = mailingListBusinessService.findAllList();
			} else {
				boolean isPublic;
				if (criteriaOnSearch.equals("public")) {
					isPublic = true;
				} else {
					isPublic = false;
				}
				result = mailingListBusinessService.findAllListByVisibilityForAdmin(isPublic);
			}
		}
		return result;
	}

	@Override
	public void deleteList(User actor, String uuid) throws BusinessException {

		MailingList mailingList = mailingListBusinessService.retrieveList(uuid);
		String ownerUuid = mailingList.getOwner().getLsUuid();

		if (actor.isSuperAdmin() || actor.getLsUuid().equals(ownerUuid)) {
			mailingListBusinessService.deleteList(uuid);
		} else {
			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to delete this list.");
		}

	}

	@Override
	public void updateList(User actor, MailingList listToUpdate) throws BusinessException {
		String ownerUuid = listToUpdate.getOwner().getLsUuid();
		if (actor.isSuperAdmin() || actor.getLsUuid().equals(ownerUuid)) {
			mailingListBusinessService.updateList(listToUpdate);
		} else {
			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to update this list.");
		}
		mailingListBusinessService.updateList(listToUpdate);
	}

	
	@Override
	public void addNewContact(User actor, String mailingListUuid, MailingListContact contact)
			throws BusinessException {
		Assert.notNull(actor);
		Assert.notNull(mailingListUuid);
		Assert.notNull(contact);
		
		User actorEntity = userService.findByLsUuid(actor.getLsUuid());
		MailingList mailingList = mailingListBusinessService.retrieveList(mailingListUuid);
		if(mailingList.isOwner(actorEntity)) {
			mailingListBusinessService.addContact(mailingList, contact);
		}
	}

	@Override
	public void updateContact(MailingList list, MailingListContact contactToUpdate) throws BusinessException {
		Assert.notNull(list);
		Assert.notNull(contactToUpdate);
		
		mailingListBusinessService.updateContact(list, contactToUpdate);
	}

	@Override
	public List<MailingList> findAllListByOwner(User user) {
		User actor = userService.findByLsUuid(user.getLsUuid());
		return mailingListBusinessService.findAllMyList(actor);
	}

	@Override
	public void deleteContact(MailingList list, String mail) throws BusinessException {
		mailingListBusinessService.deleteContact(list, mail);
	}
}
