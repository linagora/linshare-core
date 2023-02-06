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
package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.SearchType;
import org.linagora.linshare.core.domain.constants.VisibilityType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.RecipientFavourite;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;
import org.linagora.linshare.core.facade.webservice.user.AutoCompleteFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.AutoCompleteResultDto;
import org.linagora.linshare.core.facade.webservice.user.dto.ListAutoCompleteResultDto;
import org.linagora.linshare.core.facade.webservice.user.dto.ThreadMemberAutoCompleteResultDto;
import org.linagora.linshare.core.facade.webservice.user.dto.UserAutoCompleteResultDto;
import org.linagora.linshare.core.facade.webservice.user.dto.WorkgroupMemberAutoCompleteResultDto;
import org.linagora.linshare.core.repository.RecipientFavouriteRepository;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ContactListService;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.core.service.UserService2;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class AutoCompleteFacadeImpl extends UserGenericFacadeImp implements AutoCompleteFacade {

	final private static int AUTO_COMPLETE_LIMIT = 20;

	final private static int FAVOURTITE_RECIPIENT_LIMIT = 100;

	private final UserService userService;

	private final UserService2 userService2;

	private final ContactListService contactListService;

	private final RecipientFavouriteRepository favourite;

	private final SharedSpaceMemberService ssMemberService;

	public AutoCompleteFacadeImpl(final AccountService accountService,
			final UserService userService,
			final ContactListService contactListService,
			final RecipientFavouriteRepository favourite,
			final UserService2 userService2,
			final SharedSpaceMemberService ssMemberService
			) {
		super(accountService);
		this.userService = userService;
		this.userService2 = userService2;
		this.contactListService = contactListService;
		this.favourite = favourite;
		this.ssMemberService = ssMemberService;
	}

	@Override
	public Set<UserDto> findUser(String pattern) throws BusinessException {
		User authUser = checkAuthentication();
		List<User> users = userService.autoCompleteUser(authUser, pattern);
		logger.debug("nb result for completion : " + users.size());
		// TODO : FMA : Use database configuration for auto complete limit
		return getUserDtoList(users);
	}

	@Override
	public Set<String> getMail(String pattern) throws BusinessException {
		User authUser = checkAuthentication();
		Validate.notEmpty(pattern, "pattern must be set.");
		List<User> users = userService.autoCompleteUser(authUser, pattern);
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
		User authUser = checkAuthentication();
		User actor = this.getActor(authUser, null);
		if (pattern.length() < 3) {
			throw new BusinessException("Pattern size must be at least tree.");
		}
		SearchType enumType = SearchType.fromString(type);
		switch (enumType) {
			case SHARING:
				return searchForSharing(pattern, authUser);
			case USERS:
				return searchUsers(authUser, actor, pattern);
			case THREAD_MEMBERS:
				return searchForThreadMembers(pattern, threadUuid, authUser);
			case UPLOAD_REQUESTS:
				return searchForUploadRequests(pattern, authUser);
			case WORKGROUP_MEMBERS:
				return searchForWorkgroupMembers(pattern, threadUuid, authUser);
			case WORKGROUP_AUTHORS:
				return searchForWorkgroupAuthors(pattern, threadUuid, authUser);
		default:
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_BAD_REQUEST, "Unexpected search type.");
		}
	}

	private List<AutoCompleteResultDto> searchForWorkgroupAuthors(String pattern, String threadUuid, User authUser) {
		List<WorkgroupMemberAutoCompleteResultDto> autocomplete = ssMemberService.autocompleteOnAssetAuthor(authUser, authUser, threadUuid, pattern);
		int range = (autocomplete.size() < AUTO_COMPLETE_LIMIT ? autocomplete.size() : AUTO_COMPLETE_LIMIT);
		// TODO: How to get author of assets in a workgroup that does not belong to the workgroup anymore ?
		return Lists.newArrayList(autocomplete.subList(0, range));
	}

	private List<AutoCompleteResultDto> searchForWorkgroupMembers(String pattern, String threadUuid, User authUser) {
		List<WorkgroupMemberAutoCompleteResultDto> autocomplete = ssMemberService.autocompleteOnActiveMembers(authUser, authUser, threadUuid, pattern);
		int range = (autocomplete.size() < AUTO_COMPLETE_LIMIT ? autocomplete.size() : AUTO_COMPLETE_LIMIT);
		return Lists.newArrayList(autocomplete.subList(0, range));
	}

	private List<AutoCompleteResultDto> searchForUploadRequests(String pattern, User authUser) {
		List<AutoCompleteResultDto> result = Lists.newArrayList();
		List<ContactList> mailingListsList = contactListService.searchListByVisibility(authUser.getLsUuid(), VisibilityType.All.name(), pattern);
		int range = (mailingListsList.size() < AUTO_COMPLETE_LIMIT ? mailingListsList.size() : AUTO_COMPLETE_LIMIT);
		Set<UserDto> userList = findUser(pattern);
		result.addAll(ImmutableList.copyOf(Lists.transform(Lists.newArrayList(userList), UserAutoCompleteResultDto.toDto())));
		result.addAll(ImmutableList.copyOf(Lists.transform(mailingListsList.subList(0, range), ListAutoCompleteResultDto.toDto())));
		return result;
	}

	private List<AutoCompleteResultDto> searchForThreadMembers(String pattern, String threadUuid, User authUser) {
		List<AutoCompleteResultDto> result = Lists.newArrayList();
		Validate.notEmpty(threadUuid, "You must fill threadUuid query parameter.");
		List<ContactList> mailingListsList = contactListService.searchListByVisibility(authUser.getLsUuid(), VisibilityType.All.name(), pattern);
		int rangeContactList = (mailingListsList.size() < AUTO_COMPLETE_LIMIT ? mailingListsList.size() : AUTO_COMPLETE_LIMIT);
		result.addAll(ImmutableList.copyOf(Lists.transform(mailingListsList.subList(0, rangeContactList), ListAutoCompleteResultDto.toDto())));
		List<User> users = userService.autoCompleteUser(authUser, pattern);
		int range = (users.size() < AUTO_COMPLETE_LIMIT ? users.size() : AUTO_COMPLETE_LIMIT);
		for (User user : users.subList(0, range)) {
			User account = userService.findOrCreateUser(user.getMail(), user.getDomainId());
			SharedSpaceMember member = null;
			try {
				member = ssMemberService.findMemberByAccountUuid(authUser, authUser, account.getLsUuid(), threadUuid);
			} catch (BusinessException e) {
				logger.debug(String.format("Thes account %s is not yet a member of this SharedSpace %s",
						account.getAccountRepresentation(), threadUuid));
			}
			if (member == null) {
				result.add(new ThreadMemberAutoCompleteResultDto(account));
			} else {
				result.add(new ThreadMemberAutoCompleteResultDto(member, account));
			}
		}
		return result;
	}

	private List<AutoCompleteResultDto> searchUsers(Account authUser, Account actor, String pattern) {
		return findUser(pattern).stream().map(UserAutoCompleteResultDto::new).collect(Collectors.toList());
	}

	private List<AutoCompleteResultDto> searchForSharing(String pattern, User authUser) {
		List<AutoCompleteResultDto> result = Lists.newArrayList();
		List<ContactList> mailingListsList = contactListService.searchListByVisibility(authUser.getLsUuid(), VisibilityType.All.name(), pattern);
		int range = (mailingListsList.size() < AUTO_COMPLETE_LIMIT ? mailingListsList.size() : AUTO_COMPLETE_LIMIT);
		Set<UserDto> userList = findUser(pattern);
		result.addAll(ImmutableList.copyOf(Lists.transform(Lists.newArrayList(userList), UserAutoCompleteResultDto.toDto())));
		result.addAll(ImmutableList.copyOf(Lists.transform(mailingListsList.subList(0, range), ListAutoCompleteResultDto.toDto())));
		// TODO : Fix this dirty hack ! :(
		List<RecipientFavourite> favouriteRecipeints = favourite.findMatchElementsOrderByWeight(pattern, authUser, FAVOURTITE_RECIPIENT_LIMIT);
		int range2 = (favouriteRecipeints.size() < AUTO_COMPLETE_LIMIT ? favouriteRecipeints.size() : AUTO_COMPLETE_LIMIT);
		result.addAll(ImmutableList.copyOf(Lists.transform(favouriteRecipeints.subList(0, range2), AutoCompleteResultDto.toRFDto())));
		return result;
	}
}
