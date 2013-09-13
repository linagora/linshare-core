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
package org.linagora.linshare.core.facade.impl;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.MailingList;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.MailingListVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.MailingListAdminFacade;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.MailingListService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.view.tapestry.services.impl.MailCompletionService;
import org.linagora.linshare.view.tapestry.services.impl.MailingListCompletionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailingListAdminFacadeImpl implements MailingListAdminFacade {

	Logger logger = LoggerFactory.getLogger(MailingListFacadeImpl.class);
	private final MailingListService mailingListService;
	private final UserService userService;
	private final AbstractDomainService abstractDomainService;

	public MailingListAdminFacadeImpl(MailingListService mailingListService, UserService userService,
			AbstractDomainService abstractDomainService) {
		super();
		this.mailingListService = mailingListService;
		this.userService = userService;
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	public List<String> completionsForAdminSearchList(UserVo actorVo, String input, String criteriaOnSearch)
			throws BusinessException {
		User actor = userService.findByLsUuid(actorVo.getLsUuid());
		List<MailingListVo> searchResults = performSearchForAdmin(actor, input, criteriaOnSearch);
		List<String> elements = new ArrayList<String>();

		for (MailingListVo mailingListVo : searchResults) {
			elements.add(MailingListCompletionService.formatLabel(actorVo, mailingListVo, false));
		}
		return elements;
	}

	private List<MailingListVo> performSearchForAdmin(Account actor, String input, String criteriaOnSearch) throws BusinessException {
		List<MailingList> listByVisibility = mailingListService.findAllListByVisibilityForAdmin(actor, criteriaOnSearch);
		List<MailingListVo> finalList = new ArrayList<MailingListVo>();
		for (MailingList list : listByVisibility) {
			if (list.getIdentifier().toLowerCase().startsWith(input.toLowerCase())) {
				finalList.add(new MailingListVo(list));
			}
		}
		return finalList;
	}

	@Override
	public List<MailingListVo> setListFromAdminSearch(UserVo actorVo, String targetLists, String criteriaOnSearch)
			throws BusinessException {
		User actor = userService.findByLsUuid(actorVo.getLsUuid());
		List<MailingList> lists = new ArrayList<MailingList>();
		if (targetLists.equals("*")) {
			lists = mailingListService.findAllListByVisibilityForAdmin(actor, criteriaOnSearch);
		} else if (targetLists.startsWith("\"") && targetLists.endsWith(">")) {
			MailingList list = mailingListService.retrieveList(MailingListCompletionService.parseFirstElement(targetLists));
			lists.add(list);
		} else {
			return performSearchForAdmin(null, targetLists, criteriaOnSearch);
		}
		return ListToListVo(lists);
	}

	@Override
	public void setNewOwner(MailingListVo mailingListVo, String input) throws BusinessException {

		UserVo selectedUser = MailCompletionService.getUserFromDisplay(input);
		User user = userService.findUnkownUserInDB(selectedUser.getMail());
		mailingListVo.setOwner(new UserVo(user));
		AbstractDomain domain = abstractDomainService.retrieveDomain(user.getDomainId());
		mailingListVo.setDomainId(domain.getIdentifier());

	}

	private List<MailingListVo> ListToListVo(List<MailingList> list) {
		List<MailingListVo> listVo = new ArrayList<MailingListVo>();
		if (list != null) {
			for (MailingList currentList : list) {
				listVo.add(new MailingListVo(currentList));
			}
		}
		return listVo;
	}

}
