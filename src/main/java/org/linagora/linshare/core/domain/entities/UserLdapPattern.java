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
package org.linagora.linshare.core.domain.entities;

import java.util.HashMap;
import java.util.Map;

import org.linagora.linshare.core.domain.constants.UserFilterType;
import org.linagora.linshare.core.facade.webservice.admin.dto.DomainPatternDto;

public class UserLdapPattern extends LdapPattern {

	private String authCommand;

	private String searchUserCommand;

	private Integer searchPageSize;

	private Integer searchSizeLimit;

	private String autoCompleteCommandOnFirstAndLastName;

	private String autoCompleteCommandOnAllAttributes;

	private Integer completionPageSize;

	private Integer completionSizeLimit;

	public UserLdapPattern() {
	}
	/**
	 * For tests only.
	 *
	 * @param label
	 * @param description
	 * @param getUserCommand
	 * @param getAllDomainUsersCommand
	 * @param authCommand
	 * @param searchUserCommand
	 * @param attributes
	 */
	public UserLdapPattern(String label, String description,
			String getUserCommand, String getAllDomainUsersCommand,
			String authCommand, String searchUserCommand,
			Map<String, LdapAttribute> attributes) {
		this.uuid = label;
		this.label = label;
		this.description = description;
		this.authCommand = authCommand;
		this.searchUserCommand = searchUserCommand;
		this.attributes = attributes;
		this.autoCompleteCommandOnAllAttributes = "";
		this.autoCompleteCommandOnFirstAndLastName = "";
		this.searchPageSize = 0;
		this.searchSizeLimit = 0;
		this.completionPageSize = 0;
		this.completionSizeLimit = 0;
	}

	public UserLdapPattern(DomainPatternDto domainPatternDto) {
		this.uuid = domainPatternDto.getUuid();
		this.label= domainPatternDto.getLabel();
		this.description = domainPatternDto.getDescription();
		this.authCommand = domainPatternDto.getAuthCommand();
		this.searchUserCommand = domainPatternDto.getSearchUserCommand();
		this.system = false;

		this.autoCompleteCommandOnAllAttributes = domainPatternDto
				.getAutoCompleteCommandOnAllAttributes();
		this.autoCompleteCommandOnFirstAndLastName = domainPatternDto
				.getAutoCompleteCommandOnFirstAndLastName();
		this.searchPageSize = domainPatternDto.getSearchPageSize();
		this.searchSizeLimit = domainPatternDto.getSearchSizeLimit();
		this.completionPageSize = domainPatternDto.getCompletionPageSize();
		this.completionSizeLimit = domainPatternDto.getCompletionSizeLimit();

		this.attributes = new HashMap<String, LdapAttribute>();
		this.attributes.put(USER_MAIL, new LdapAttribute(USER_MAIL,
				domainPatternDto.getUserMail(), true));
		this.attributes.put(USER_FIRST_NAME, new LdapAttribute(USER_FIRST_NAME,
				domainPatternDto.getUserFirstName(), true));
		this.attributes.put(USER_LAST_NAME, new LdapAttribute(USER_LAST_NAME,
				domainPatternDto.getUserLastName(), true));
		this.attributes.put(USER_UID, new LdapAttribute(USER_UID,
				domainPatternDto.getLdapUid(), false));
	}

	/**
	 * For tests only.
	 *
	 * @param label
	 * @param description
	 * @param authCommand
	 * @param searchUserCommand
	 * @param searchPageSize
	 * @param searchSizeLimit
	 * @param attributes
	 * @param autoCompleteCommandOnAllAttributes
	 * @param autoCompleteCommandOnFirstAndLastName
	 * @param completionPageSize
	 * @param completionSizeLimit
	 * @param system
	 */
	public UserLdapPattern(String label, String description,
			String authCommand, String searchUserCommand,
			Integer searchPageSize, Integer searchSizeLimit,
			Map<String, LdapAttribute> attributes,
			String autoCompleteCommandOnAllAttributes,
			String autoCompleteCommandOnFirstAndLastName,
			Integer completionPageSize, Integer completionSizeLimit,
			boolean system) {
		super();
		this.uuid = label;
		this.label = label;
		this.description = description;
		this.authCommand = authCommand;
		this.searchUserCommand = searchUserCommand;
		this.attributes = attributes;
		this.autoCompleteCommandOnAllAttributes = autoCompleteCommandOnAllAttributes;
		this.autoCompleteCommandOnFirstAndLastName = autoCompleteCommandOnFirstAndLastName;
		this.system = system;
		this.searchSizeLimit = searchSizeLimit;
		this.searchPageSize = searchPageSize;
		this.completionSizeLimit = completionSizeLimit;
		this.completionPageSize = completionPageSize;
	}

	public String getAuthCommand() {
		return authCommand;
	}

	public void setAuthCommand(String authCommand) {
		this.authCommand = authCommand;
	}

	public String getSearchUserCommand() {
		return searchUserCommand;
	}

	public void setSearchUserCommand(String searchUserCommand) {
		this.searchUserCommand = searchUserCommand;
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

	public String getAutoCompleteCommandOnFirstAndLastName() {
		return autoCompleteCommandOnFirstAndLastName;
	}

	public void setAutoCompleteCommandOnFirstAndLastName(
			String autoCompleteCommandOnFirstAndLastName) {
		this.autoCompleteCommandOnFirstAndLastName = autoCompleteCommandOnFirstAndLastName;
	}

	public String getAutoCompleteCommandOnAllAttributes() {
		return autoCompleteCommandOnAllAttributes;
	}

	public void setAutoCompleteCommandOnAllAttributes(
			String autoCompleteCommandOnAllAttributes) {
		this.autoCompleteCommandOnAllAttributes = autoCompleteCommandOnAllAttributes;
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

	/**
	 * Helpers
	 */
	@Override
	public String getAttribute(String field) {
		return attributes.get(field).getAttribute().trim().toLowerCase();
	}

	public UserFilterType getType() {
		return UserFilterType.LDAP;
	}
}
