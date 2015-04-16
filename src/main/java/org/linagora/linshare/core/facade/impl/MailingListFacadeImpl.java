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
package org.linagora.linshare.core.facade.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.VisibilityType;
import org.linagora.linshare.core.domain.entities.MailingList;
import org.linagora.linshare.core.domain.entities.MailingListContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.MailingListContactVo;
import org.linagora.linshare.core.domain.vo.MailingListVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.MailingListFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.MailingListService;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailingListFacadeImpl implements MailingListFacade {

	Logger logger = LoggerFactory.getLogger(MailingListFacadeImpl.class);

	private final MailingListService mailingListService;
	private final UserService userService;
	private final AccountService accountService;

	public MailingListFacadeImpl(MailingListService mailingListService, UserService userService,
			AccountService accountService) {
		super();
		this.mailingListService = mailingListService;
		this.userService = userService;
		this.accountService = accountService;
	}

	/**
	 * Basic operations on mailingList
	 */

	@Override
	public MailingListVo createList(UserVo actorVo, MailingListVo mailingListVo) throws BusinessException {
		MailingList mailingList = new MailingList(mailingListVo);
		// actor and owner are the same person.
		MailingList createdList = mailingListService.createList(actorVo.getLsUuid(), actorVo.getLsUuid(), mailingList);
		return new MailingListVo(createdList);
	}

	@Override
	public MailingListVo findByUuid(UserVo actorVo, String uuid) {
		try {
			return new MailingListVo(mailingListService.findByUuid(actorVo.getLsUuid(), uuid));
		} catch (BusinessException e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public List<MailingListVo> getAllMyList(UserVo actorVo, UserVo userVo) throws BusinessException {
		return ListToListVo(mailingListService.findAllListByOwner(actorVo.getLsUuid(), userVo.getLsUuid()));
	}
	
	@Override
	public List<String> getAllContactMails(UserVo actorVo, MailingListVo ml) throws BusinessException {
		return mailingListService.getAllContactMails(actorVo.getLsUuid(), ml.getUuid());
	}

	@Override
	public void updateList(UserVo actorVo, MailingListVo mailingListVo) throws BusinessException {
		MailingList mailingList = new MailingList(mailingListVo);
		UserVo ownerVo = mailingListVo.getOwner();
		if(ownerVo != null) {
			User owner = userService.findByLsUuid(ownerVo.getLsUuid());
			mailingList.setOwner(owner);
		}
		mailingListService.updateList(actorVo.getLsUuid(), mailingList);
	}

	@Override
	public void deleteList(UserVo actorVo, String uuid) throws BusinessException {
		mailingListService.deleteList(actorVo.getLsUuid(), uuid);
	}

	/**
	 * Basic operations on mailingListContact
	 */

	@Override
	public void updateContact(UserVo actorVo, MailingListContactVo contactVo) throws BusinessException {
		MailingListContact contact = new MailingListContact(contactVo);
		mailingListService.updateContact(actorVo.getLsUuid(), contact);
	}

	@Override
	public void deleteContact(UserVo actorVo, String contactUuid) throws BusinessException {
		mailingListService.deleteContact(actorVo.getLsUuid(), contactUuid);
	}

	@Override
	public MailingListContactVo searchContact(UserVo actorVo, String uuid) throws BusinessException {
		return new MailingListContactVo(mailingListService.searchContact(actorVo.getLsUuid(), uuid));
	}

	@Override
	public MailingListContactVo findContactByMail(UserVo actorVo, String listUuid, String mail) throws BusinessException {
		return new MailingListContactVo(mailingListService.findContactWithMail(actorVo.getLsUuid(), listUuid, mail));
	}

	@Override
	public void addUserToList(UserVo actorVo, MailingListVo mailingListVo, String domainId, String mail) throws BusinessException {
		User selectedUser = userService.findOrCreateUserWithDomainPolicies(mail, domainId);
		if (selectedUser != null) {
			MailingListContact contact = new MailingListContact(selectedUser.getMail(), selectedUser.getFirstName(),
					selectedUser.getLastName());
			mailingListService.addNewContact(actorVo.getLsUuid(), mailingListVo.getUuid(), contact);
		} else {
			logger.error("User not found !");
		}
	}

	@Override
	public void addNewContactToList(UserVo actorVo, MailingListVo mailingListVo, MailingListContactVo contactVo)
			throws BusinessException {
		MailingListContact contact = new MailingListContact(contactVo);
		mailingListService.addNewContact(actorVo.getLsUuid(), mailingListVo.getUuid(), contact);
	}

	/**
	 * Helpers
	 */

	@Override
	public boolean identifierIsAvailable(UserVo user, String purposedIdentifier) {
		Validate.notNull(user);
		return mailingListService.findByIdentifier(user.getLsUuid(),
				purposedIdentifier) == null;
	}

	@Override
	public String findAvailableIdentifier(UserVo user, String value) {
		int i = 1;
		String copy = value;
		while (identifierIsAvailable(user, copy) == false) {
			copy = value + i;
			i++;
		}
		return copy;
	}

	private List<MailingListVo> performSearchList(UserVo loginUser, String input, String criteriaOnSearch)
			throws BusinessException {
		List<MailingList> listByVisibility = mailingListService.searchListByVisibility(loginUser.getLsUuid(),
				criteriaOnSearch, input);
		return ListToListVo(listByVisibility);
	}

	@Override
	public List<MailingListVo> completionForUploadForm(UserVo userVo, String input) throws BusinessException {
		return performSearchList(userVo, input, VisibilityType.All.toString());
	}

	@Override
	public List<MailingListVo> setListFromSearch(UserVo loginUser, String targetLists, String criteriaOnSearch)
			throws BusinessException {
		if (targetLists.equals("*")) {
			return ListToListVo(mailingListService.findAllListByVisibility(loginUser.getLsUuid(), criteriaOnSearch));
		} else {
			return performSearchList(loginUser, targetLists, criteriaOnSearch);
		}
	}

	@Override
	public List<UserVo> completionOnUsers(UserVo actorVo, String pattern) throws BusinessException {
		User actor = userService.findByLsUuid(actorVo.getLogin());
		List<UserVo> ret = new ArrayList<UserVo>();
		List<User> userSet = performSearchUser(actor, pattern);

		for (User user : userSet)
			ret.add(new UserVo(user));
		return ret;
	}

	private List<User> performSearchUser(User loginUser, String input) throws BusinessException {
		String firstName_ = null;
		String lastName_ = null;

		if (input != null && input.length() > 0) {
			StringTokenizer stringTokenizer = new StringTokenizer(input, " ");
			if (stringTokenizer.hasMoreTokens()) {
				firstName_ = stringTokenizer.nextToken();
				if (stringTokenizer.hasMoreTokens()) {
					lastName_ = stringTokenizer.nextToken();
				}
			}
		}

		Set<User> userSet = new HashSet<User>();

		if (input != null) {
			userSet.addAll(userService.searchUser(input.trim(), null, null, null, loginUser));
		} else {
			userSet.addAll(userService.searchUser(null, firstName_, lastName_, null, loginUser));
		}
		return new ArrayList<User>(userSet);
	}

	private List<MailingListVo> ListToListVo(List<MailingList> list) {
		List<MailingListVo> listVo = new ArrayList<MailingListVo>();

		for (MailingList currentList : list) {
			listVo.add(new MailingListVo(currentList));
		}
		return listVo;
	}

	@Override
	public boolean getListIsDeletable(UserVo actorVo, MailingListVo listVo) throws BusinessException {
		MailingList list = mailingListService.findByUuid(actorVo.getLsUuid(), listVo.getUuid());
		User actor = (User) userService.findOrCreateUser(actorVo.getMail(), actorVo.getDomainIdentifier());

		return list.getOwner().equals(actor) || actorVo.isSuperAdmin();
	}
}
