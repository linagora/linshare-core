/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2022. Contribute to
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
package org.linagora.linshare.core.facade.webservice.adminv5.dto;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.UserLdapPattern;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;
import com.google.common.collect.Maps;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "LdapUserFilter")
@Schema(name = "LdapUserFilter", description = "A user filter is used to search users in an LDAP directory")
public class LDAPUserFilterDto extends AbstractUserFilterDto {

	@Schema(description = "The query used for user's authentication.", required = true)
	private String authenticationQuery;

	@Schema(description = "The query used to search users.", required = true)
	private String searchUserQuery;

	@Schema(description = "This query is used by the autocomplete endpoint to search/discover users by all their attributes.", required = true)
	private String autoCompleteCommandOnAllAttributes;

	@Schema(description = "This query is used by the autocomplete endpoint to search/discover users by their first and last name.", required = true)
	private String autoCompleteCommandOnFirstAndLastName;

	@Schema(description = "The user's mail attribute (Ex: for OpenLDAP attribute = 'mail').", required = true)
	private String userMailAttribute;

	@Schema(description = "The user's firstName (Ex: for OpenLDAP attribute = 'givenName')", required = true)
	private String userFirstNameAttribute;

	@Schema(description = "The user's LastName (Ex: for OpenLDAP attribute = 'sn')", required = true)
	private String userLastNameAttribute;

	@Schema(description = "The user's uid Attribute (Ex: for OpenLDAP attribute = 'uid')", required = true)
	private String userUidAttribute;

	@Schema(description = "The size of the returned page, result of the search query", required = true)
	private Integer searchPageSize;

	@Schema(description = "The limit size of the returned entries, result of the search query", required = true)
	private Integer searchSizeLimit;

	@Schema(description = "The size of the completion page size.", required = true)
	private Integer completionPageSize;

	@Schema(description = "The limit size completion", required = true)
	private Integer completionSizeLimit;

	public LDAPUserFilterDto() {
		super();
	}

	public LDAPUserFilterDto(UserLdapPattern userLdapPattern) {
		this.uuid = userLdapPattern.getUuid();
		this.name = userLdapPattern.getLabel();
		this.description = userLdapPattern.getDescription();
		this.type = userLdapPattern.getType();
		this.authenticationQuery = userLdapPattern.getAuthCommand();
		this.searchUserQuery = userLdapPattern.getSearchUserCommand();
		this.autoCompleteCommandOnAllAttributes = userLdapPattern.getAutoCompleteCommandOnAllAttributes();
		this.autoCompleteCommandOnFirstAndLastName = userLdapPattern.getAutoCompleteCommandOnFirstAndLastName();
		this.userFirstNameAttribute = userLdapPattern.getAttribute(UserLdapPattern.USER_FIRST_NAME);
		this.userLastNameAttribute = userLdapPattern.getAttribute(UserLdapPattern.USER_LAST_NAME);
		this.userMailAttribute = userLdapPattern.getAttribute(UserLdapPattern.USER_MAIL);
		this.userUidAttribute = userLdapPattern.getAttribute(UserLdapPattern.USER_UID);
		this.searchPageSize = userLdapPattern.getSearchPageSize();
		this.searchSizeLimit = userLdapPattern.getSearchSizeLimit();
		this.completionPageSize = userLdapPattern.getCompletionPageSize();
		this.completionSizeLimit = userLdapPattern.getCompletionSizeLimit();
		this.creationDate = userLdapPattern.getCreationDate();
		this.modificationDate = userLdapPattern.getModificationDate();
	}

	public String getAuthenticationQuery() {
		return authenticationQuery;
	}

	public void setAuthenticationQuery(String authenticationQuery) {
		this.authenticationQuery = authenticationQuery;
	}

	public String getSearchUserQuery() {
		return searchUserQuery;
	}

	public void setSearchUserQuery(String searchUserQuery) {
		this.searchUserQuery = searchUserQuery;
	}

	public String getAutoCompleteCommandOnAllAttributes() {
		return autoCompleteCommandOnAllAttributes;
	}

	public void setAutoCompleteCommandOnAllAttributes(String autoCompleteCommandOnAllAttributes) {
		this.autoCompleteCommandOnAllAttributes = autoCompleteCommandOnAllAttributes;
	}

	public String getAutoCompleteCommandOnFirstAndLastName() {
		return autoCompleteCommandOnFirstAndLastName;
	}

	public void setAutoCompleteCommandOnFirstAndLastName(String autoCompleteCommandOnFirstAndLastName) {
		this.autoCompleteCommandOnFirstAndLastName = autoCompleteCommandOnFirstAndLastName;
	}

	public String getUserMailAttribute() {
		return userMailAttribute;
	}

	public void setUserMailAttribute(String userMailAttribute) {
		this.userMailAttribute = userMailAttribute;
	}

	public String getUserFirstNameAttribute() {
		return userFirstNameAttribute;
	}

	public void setUserFirstNameAttribute(String userFirstNameAttribute) {
		this.userFirstNameAttribute = userFirstNameAttribute;
	}

	public String getUserLastNameAttribute() {
		return userLastNameAttribute;
	}

	public void setUserLastNameAttribute(String userLastNameAttribute) {
		this.userLastNameAttribute = userLastNameAttribute;
	}

	public String getUserUidAttribute() {
		return userUidAttribute;
	}

	public void setUserUidAttribute(String userUidAttribute) {
		this.userUidAttribute = userUidAttribute;
	}

	public Integer getSearchPageSize() {
		return searchPageSize;
	}

	public void setSearchPageSize(Integer searchPageSize) {
		this.searchPageSize = searchPageSize;
	}

	public Integer getSearchSizeLimit() {
		return searchSizeLimit;
	}

	public void setSearchSizeLimit(Integer searchSizeLimit) {
		this.searchSizeLimit = searchSizeLimit;
	}

	public Integer getCompletionPageSize() {
		return completionPageSize;
	}

	public void setCompletionPageSize(Integer completionPageSize) {
		this.completionPageSize = completionPageSize;
	}

	public Integer getCompletionSizeLimit() {
		return completionSizeLimit;
	}

	public void setCompletionSizeLimit(Integer completionSizeLimit) {
		this.completionSizeLimit = completionSizeLimit;
	}

	/*
	 * Transformers
	 */
	public static Function<UserLdapPattern, LDAPUserFilterDto> toDto() {
		return new Function<UserLdapPattern, LDAPUserFilterDto>() {
			@Override
			public LDAPUserFilterDto apply(UserLdapPattern arg0) {
				return new LDAPUserFilterDto(arg0);
			}
		};
	}

	public UserLdapPattern toLdapUserFilterObject() {
		UserLdapPattern ldapPattern = new UserLdapPattern();
		ldapPattern.setUuid(getUuid());
		ldapPattern.setLabel(getName());
		ldapPattern.setDescription(getDescription());
		ldapPattern.setAuthCommand(getAuthenticationQuery());
		ldapPattern.setSearchUserCommand(getSearchUserQuery());
		ldapPattern.setAutoCompleteCommandOnAllAttributes(getAutoCompleteCommandOnAllAttributes());
		ldapPattern.setAutoCompleteCommandOnFirstAndLastName(getAutoCompleteCommandOnFirstAndLastName());
		Map<String, LdapAttribute> attributes = Maps.newHashMap();
		attributes.put(UserLdapPattern.USER_FIRST_NAME, new LdapAttribute(UserLdapPattern.USER_FIRST_NAME, getUserFirstNameAttribute(), true));
		attributes.put(UserLdapPattern.USER_LAST_NAME, new LdapAttribute(UserLdapPattern.USER_LAST_NAME, getUserLastNameAttribute(), true));
		attributes.put(UserLdapPattern.USER_MAIL, new LdapAttribute(UserLdapPattern.USER_MAIL, getUserMailAttribute(), true));
		attributes.put(UserLdapPattern.USER_UID, new LdapAttribute(UserLdapPattern.USER_UID, getUserUidAttribute(), false));
		ldapPattern.setAttributes(attributes);
		ldapPattern.setSearchPageSize(getSearchPageSize());
		ldapPattern.setSearchSizeLimit(getSearchSizeLimit());
		ldapPattern.setCompletionPageSize(getCompletionPageSize());
		ldapPattern.setCompletionSizeLimit(getCompletionSizeLimit());
		return ldapPattern;
	}
}
