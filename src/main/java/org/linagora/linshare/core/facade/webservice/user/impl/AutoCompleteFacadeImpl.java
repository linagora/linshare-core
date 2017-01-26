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
 * and free version of LinShare™, powered by Linagora © 2009–2016. Contribute to
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

package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.SearchType;
import org.linagora.linshare.core.domain.constants.VisibilityType;
import org.linagora.linshare.core.domain.entities.MailingList;
import org.linagora.linshare.core.domain.entities.RecipientFavourite;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;
import org.linagora.linshare.core.facade.webservice.user.AutoCompleteFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.AutoCompleteResultDto;
import org.linagora.linshare.core.facade.webservice.user.dto.ListAutoCompleteResultDto;
import org.linagora.linshare.core.facade.webservice.user.dto.ThreadMemberAutoCompleteResultDto;
import org.linagora.linshare.core.facade.webservice.user.dto.UserAutoCompleteResultDto;
import org.linagora.linshare.core.repository.RecipientFavouriteRepository;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.MailingListService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.UserService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class AutoCompleteFacadeImpl extends UserGenericFacadeImp implements AutoCompleteFacade {

	final private static int AUTO_COMPLETE_LIMIT = 20;

	final private static int FAVOURTITE_RECIPIENT_LIMIT = 100;

	private final UserService userService;

	private final ThreadService threadService;

	private final MailingListService mailingListSerice;

	private final RecipientFavouriteRepository favourite;

	public AutoCompleteFacadeImpl(final AccountService accountService,
			final UserService userService,
			final MailingListService mailingListSerice,
			final ThreadService threadService,
			RecipientFavouriteRepository favourite
			) {
		super(accountService);
		this.userService = userService;
		this.mailingListSerice = mailingListSerice;
		this.threadService = threadService;
		this.favourite = favourite;
	}

	@Override
	public Set<UserDto> findUser(String pattern) throws BusinessException {
		User actor = checkAuthentication();
		List<User> users = userService.autoCompleteUser(actor, pattern);
		logger.debug("nb result for completion : " + users.size());
		// TODO : FMA : Use database configuration for auto complete limit
		return getUserDtoList(users);
	}

	@Override
	public Set<String> getMail(String pattern) throws BusinessException {
		User actor = checkAuthentication();
		Validate.notEmpty(pattern, "pattern must be set.");
		List<User> users = userService.autoCompleteUser(actor, pattern);
		logger.debug("nb result for completion : " + users.size());
		// TODO : FMA : Use database configuration for auto complete limit
		return getMailList(users, AUTO_COMPLETE_LIMIT);
	}

	private Set<UserDto> getUserDtoList(List<User> users) {
		HashSet<UserDto> hashSet = new HashSet<UserDto>();
		int range = (users.size() < AUTO_COMPLETE_LIMIT ? users.size() : AUTO_COMPLETE_LIMIT);
		for (User user : users.subList(0, range)) {
			hashSet.add(UserDto.getCompletionUser(user));
		}
		return hashSet;
	}

	private Set<String> getMailList(List<User> users, int limit) {
		Set<String> res = new HashSet<String>();
		for (User user : users) {
			res.add(user.getMail());
		}
		return res;
	}

	@Override
	public List<AutoCompleteResultDto> search(String pattern, String type, String threadUuid) throws BusinessException {
		User actor = checkAuthentication();
		if (pattern.length() > 2) {
			List<AutoCompleteResultDto> result = Lists.newArrayList();
			SearchType enumType = SearchType.fromString(type);
			if (enumType.equals(SearchType.SHARING)) {
				List<MailingList> mailingListsList = mailingListSerice.searchListByVisibility(actor.getLsUuid(), VisibilityType.All.name(), pattern);
				int range = (mailingListsList.size() < AUTO_COMPLETE_LIMIT ? mailingListsList.size() : AUTO_COMPLETE_LIMIT);
				Set<UserDto> userList = findUser(pattern);
				result.addAll(ImmutableList.copyOf(Lists.transform(Lists.newArrayList(userList), UserAutoCompleteResultDto.toDto())));
				result.addAll(ImmutableList.copyOf(Lists.transform(mailingListsList.subList(0, range), ListAutoCompleteResultDto.toDto())));
				// TODO : Fix this dirty hack ! :(
				List<RecipientFavourite> favouriteRecipeints = favourite.findMatchElementsOrderByWeight(pattern, actor, FAVOURTITE_RECIPIENT_LIMIT);
				int range2 = (favouriteRecipeints.size() < AUTO_COMPLETE_LIMIT ? favouriteRecipeints.size() : AUTO_COMPLETE_LIMIT);
				result.addAll(ImmutableList.copyOf(Lists.transform(favouriteRecipeints.subList(0, range2), AutoCompleteResultDto.toRFDto())));
			} else if (enumType.equals(SearchType.USERS)) {
				Set<UserDto> userList = findUser(pattern);
				result.addAll(ImmutableList.copyOf(Lists.transform(Lists.newArrayList(userList), UserAutoCompleteResultDto.toDto())));
			} else if (enumType.equals(SearchType.THREAD_MEMBERS)) {
				Validate.notEmpty(threadUuid, "You must fill threadUuid query parameter.");
				Thread thread = threadService.find(actor, actor, threadUuid);
				List<User> users = userService.autoCompleteUser(actor, pattern);
				int range = (users.size() < AUTO_COMPLETE_LIMIT ? users.size() : AUTO_COMPLETE_LIMIT);
				for (User user : users.subList(0, range)) {
					User account = userService.findOrCreateUser(user.getMail(), user.getDomainId());
					ThreadMember member = threadService.getMemberFromUser(thread, account);
					if (member == null) {
						result.add(new ThreadMemberAutoCompleteResultDto(account));
					} else {
						result.add(new ThreadMemberAutoCompleteResultDto(member));
					}
				}
			} else {
				throw new BusinessException(BusinessErrorCode.WEBSERVICE_BAD_REQUEST, "Unexpected search type.");
			}
			return result;
		} else {
			throw new BusinessException("Pattern size must be at least tree.");
		}
	}
}
