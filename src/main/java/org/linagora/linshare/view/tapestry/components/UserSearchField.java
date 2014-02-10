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
package org.linagora.linshare.view.tapestry.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.FunctionalityFacade;
import org.linagora.linshare.core.facade.RecipientFavouriteFacade;
import org.linagora.linshare.core.facade.UserAutoCompleteFacade;
import org.linagora.linshare.core.facade.UserFacade;
import org.linagora.linshare.view.tapestry.enums.UserTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class UserSearchField {
	private static final Logger logger = LoggerFactory.getLogger(UserSearchField.class);
	/* ***********************************************************
	 * Parameters***********************************************************
	 */

	@Parameter(required = true, defaultPrefix = BindingConstants.PROP)
	@Property
	private List<UserVo> users;

	/* ***********************************************************
	 * Injected services
	 * ***********************************************************
	 */
	@Inject
	private UserFacade userFacade;

	@Inject
	private Messages messages;

	@Inject
	private RecipientFavouriteFacade recipientFavouriteFacade;
	/* ***********************************************************
	 * Properties & injected symbol, ASO, etc
	 * ***********************************************************
	 */
	@SessionState
	private UserVo userVo;

	@Property
	private String userSearchPattern;

	@Persist("flash")
	@Property
	private String lastName;

	@Persist("flash")
	@Property
	private String firstName;

	@Persist("flash")
	@Property
	private String mail;

	@Property
	@Persist
	private UserTypes userType;

	@Property
	@Persist
	private boolean advancedSearch;

	@Persist
	private boolean reset;

	@Persist
	private boolean resetSimple;

	@Inject
	private ComponentResources componentResources;

	@Property
	private int autocompleteMin;

	@Inject
	private FunctionalityFacade functionalityFacade;

	@Inject
	private UserAutoCompleteFacade userAutoCompleteFacade;

	/* ***********************************************************
	 * Event handlers&processing
	 * ***********************************************************
	 */
	@SetupRender
	public void initValues() {
		autocompleteMin = functionalityFacade.completionThreshold(userVo.getDomainIdentifier());

		if (userType == null)
			userType = UserTypes.ALL;

		if (lastName == null)
			lastName = messages.get("components.userSearch.slidingField.lastName");

		if (firstName == null)
			firstName = messages.get("components.userSearch.slidingField.firstName");

		if (mail == null)
			mail = messages.get("components.userSearch.slidingField.mail");

	}

	public List<String> onProvideCompletionsFromUserSearchPattern(String input) {
		List<UserVo> searchResults;

		List<String> elements = new ArrayList<String>();
		try {
			searchResults = recipientFavouriteFacade.recipientsOrderedByWeightDesc(performSearch(input), userVo);

			for (UserVo user : searchResults) {
				String completeName = user.getFirstName().trim() + " " + user.getLastName().trim();
				if (!elements.contains(completeName)) {
					elements.add(completeName);
				}
			}

			return elements;
		} catch (BusinessException e) {
			logger.error("Error while trying to autocomplete", e);
		} catch (NullPointerException e) {
			logger.error("Error while trying to autocomplete", e);
		}
		return elements;
	}

	public void onActionFromToggleSearch() {
		advancedSearch = !advancedSearch;

	}

	public void onSuccessFromUserSearchForm() {
		if (resetSimple) {
			this.userSearchPattern = null;
			this.resetSimple = false;
			componentResources.triggerEvent("resetListUsers", null, null);
		} else {
			componentResources.triggerEvent("inUserSearch", null, null);
			users = performSearch(userSearchPattern);
		}
	}

	public void onSuccessFromAdvancedSearchForm() {
		if (reset) {
			this.userType = UserTypes.ALL;
			this.lastName = null;
			this.firstName = null;
			this.mail = null;
			this.reset = false;
			componentResources.triggerEvent("resetListUsers", null, null);
		} else {
			componentResources.triggerEvent("inUserSearch", null, null);
			users = performAnyWhereSearch();
		}
	}

	void onSelectedFromReset() {
		reset = true;
	}

	void onSelectedFromResetSimple() {
		resetSimple = true;
	}

	/**
	 * Perform a user search using the user search pattern.
	 * 
	 * @param input
	 *            user search pattern.
	 * @return list of users.
	 */
	private List<UserVo> performSearch(String input) {
		try {
			if (input != null) {
				return userAutoCompleteFacade.autoCompleteUserSortedByFavorites(userVo, input);
			} else {
				logger.debug("empty research ?");
				return userFacade.searchUser(null, null, null, null, userVo);
			}

		} catch (BusinessException e) {
			logger.error("Error while trying to autocomplete", e);
		}
		return new ArrayList<UserVo>();
	}

	public List<UserVo> performAnyWhereSearch() {
		Set<UserVo> userSet = new HashSet<UserVo>();
		AccountType type = null;

		switch (userType) {
		case GUEST:
			type = AccountType.GUEST;
			break;
		case INTERNAL:
			type = AccountType.INTERNAL;
			break;
		default:
			break; // null => ALL
		}
		lastName = (messages.get("components.userSearch.slidingField.lastName").equals(lastName)) ? null : lastName;
		firstName = (messages.get("components.userSearch.slidingField.firstName").equals(firstName)) ? null : firstName;
		mail = (messages.get("components.userSearch.slidingField.mail").equals(mail)) ? null : mail;

		try {
			userSet.addAll(userFacade.searchUser(this.mail, this.firstName, this.lastName, type, userVo));
		} catch (BusinessException e) {
			logger.error("Error while trying to search user", e);
		}
		return new ArrayList<UserVo>(userSet);
	}

	/**
	 * Getter & setters
	 * 
	 */
	/**
	 * internal radio button
	 * 
	 * @return type internal
	 */
	public UserTypes getInternal() {
		return UserTypes.INTERNAL;
	}

	/**
	 * guest radio button
	 * 
	 * @return type guest
	 */
	public UserTypes getGuest() {
		return UserTypes.GUEST;
	}

	/**
	 * both radio button
	 * 
	 * @return type all
	 */
	public UserTypes getAll() {
		return UserTypes.ALL;
	}

	public boolean getNotRestrictedUser() {
		return !userVo.isRestricted();
	}
}
