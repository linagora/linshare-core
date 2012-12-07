/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linshare.view.tapestry.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

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
	 *                         Parameters
	 ************************************************************ */

	@SuppressWarnings("unused")
	@Parameter(required = true, defaultPrefix = BindingConstants.PROP)
	@Property
	private List<UserVo> users;

	/* ***********************************************************
	 *                      Injected services
	 ************************************************************ */
	@Inject
	private UserFacade userFacade;

	@Inject
	private Messages messages;
	
	@Inject
	private RecipientFavouriteFacade recipientFavouriteFacade;
	/* ***********************************************************
	 *                Properties & injected symbol, ASO, etc
	 ************************************************************ */
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
	
	

	/* ***********************************************************
	 *                   Event handlers&processing
	 ************************************************************ */
	@SetupRender
	public void initValues(){
		autocompleteMin = functionalityFacade.completionThreshold(userVo.getDomainIdentifier());
		
		if(userType==null) userType=UserTypes.ALL;
		
		if(lastName==null) lastName=messages.get("components.userSearch.slidingField.lastName");
		
		if(firstName==null) firstName=messages.get("components.userSearch.slidingField.firstName");
		
		if(mail==null) mail=messages.get("components.userSearch.slidingField.mail");
		
	}
	
	public List<String> onProvideCompletionsFromUserSearchPattern(String input) {
		List<UserVo> searchResults;

		List<String> elements = new ArrayList<String>();
		try {
			searchResults = recipientFavouriteFacade.recipientsOrderedByWeightDesc(performSearch(input),userVo);

			for (UserVo user : searchResults) {
	            String completeName = user.getFirstName().trim() + " " + user.getLastName().trim();
	            if (!elements.contains(completeName)) {
	                elements.add(completeName);
	            }
			}

			return elements;
		} catch (BusinessException e) {
			logger.error("Error while trying to autocomplete", e);
		}
		return elements;
	}


	public void onActionFromToggleSearch(){
		advancedSearch=!advancedSearch;
	
	}
	
	
	public void onSuccessFromUserSearchForm() {
		if (resetSimple) {
			this.userSearchPattern = null;
			this.resetSimple = false;
			componentResources.triggerEvent("resetListUsers", null, null);
		}
		else {
			componentResources.triggerEvent("inUserSearch", null, null);
			users = performSearch(userSearchPattern);
		}
	}

	public void onSuccessFromAdvancedSearchForm() {
		if (reset) {
			this.userType=UserTypes.ALL;
			this.lastName = null;
			this.firstName = null;
			this.mail = null;
			this.reset = false;
			componentResources.triggerEvent("resetListUsers", null, null);
		}
		else {
			componentResources.triggerEvent("inUserSearch", null, null);
			users=performAnyWhereSearch();
		}
	}
	
	void onSelectedFromReset() {
		reset = true;
	}
	
	void onSelectedFromResetSimple() {
		resetSimple = true;
	}
	
	/** Perform a user search using the user search pattern.
	 * @param input user search pattern.
	 * @return list of users.
	 */
	private List<UserVo> performSearch(String input) {
		Set<UserVo> userSet = new HashSet<UserVo>();

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
		
		try {
	        if (input != null) {
	            userSet.addAll(userFacade.searchUser(input.trim(), null, null, null, userVo));
	            userSet.addAll(userFacade.searchUser(null, firstName_, lastName_, null, userVo));
	            userSet.addAll(userFacade.searchUser(null, lastName_, firstName_, null, userVo));
	        } else {
	        	userSet.addAll(userFacade.searchUser(null, null, null, null, userVo));
	        }
		} catch (BusinessException e) {
			logger.error("Error while trying to autocomplete", e);
		}

		return new ArrayList<UserVo>(userSet);
	}


	public List<UserVo> performAnyWhereSearch() {
		Set<UserVo> userSet = new HashSet<UserVo>();
		AccountType type=null;
		
		switch (userType) {
		case GUEST:
			type=AccountType.GUEST;
			break;
		case INTERNAL:
			type=AccountType.INTERNAL;
			break;
		default:
			break; //null => ALL
		}
		lastName=(messages.get("components.userSearch.slidingField.lastName").equals(lastName))?null:lastName;
		firstName=(messages.get("components.userSearch.slidingField.firstName").equals(firstName))?null:firstName;
		mail=(messages.get("components.userSearch.slidingField.mail").equals(mail))?null:mail;	
		
		try {
			userSet.addAll(userFacade.searchUser(this.mail, this.firstName,this.lastName,type,userVo));
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
	 * @return type internal
	 */
	public UserTypes getInternal() { return UserTypes.INTERNAL; }

	/**
	 * guest radio button
	 * @return type guest
	 */
	public UserTypes getGuest() { return UserTypes.GUEST; }

	/**
	 * both radio button
	 * @return type all
	 */
	public UserTypes getAll() { return UserTypes.ALL; }
	
	public boolean getNotRestrictedUser() {
		return !userVo.isRestricted();
	}
}
