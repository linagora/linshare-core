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
package org.linagora.linshare.mongo.entities.mto;

import java.util.Map;

import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.UserLdapPattern;

public class DomainPatternMto {

	private String uuid;

	private String label;

	private String description;

	private String authCommand;

	private String searchUserCommand;

	private String userMail;

	private String userFirstName;

	private String userLastName;

	private String ldapUid;

	private String autoCompleteCommandOnAllAttributes;

	private String autoCompleteCommandOnFirstAndLastName;

	private Integer searchPageSize;

	private Integer searchSizeLimit;

	private Integer completionPageSize;

	private Integer completionSizeLimit;

	public DomainPatternMto() {
	}

	public DomainPatternMto(UserLdapPattern pattern, boolean full) {
		this.uuid = pattern.getUuid();
		this.label = pattern.getLabel();
		if (full) {
			this.description = pattern.getDescription();
			this.authCommand = pattern.getAuthCommand();
			this.searchUserCommand = pattern.getSearchUserCommand();
			Map<String, LdapAttribute> attributes = pattern.getAttributes();
			this.userMail = attributes.get(UserLdapPattern.USER_MAIL)
					.getAttribute();
			this.userFirstName = attributes.get(UserLdapPattern.USER_FIRST_NAME)
					.getAttribute();
			this.userLastName = attributes.get(UserLdapPattern.USER_LAST_NAME)
					.getAttribute();
			this.ldapUid = attributes.get(UserLdapPattern.USER_UID).getAttribute();

			this.autoCompleteCommandOnAllAttributes = pattern
					.getAutoCompleteCommandOnAllAttributes();
			this.autoCompleteCommandOnFirstAndLastName = pattern
					.getAutoCompleteCommandOnFirstAndLastName();
			this.searchPageSize = pattern.getSearchPageSize();
			this.searchSizeLimit = pattern.getSearchSizeLimit();
			this.completionPageSize = pattern.getCompletionPageSize();
			this.completionSizeLimit = pattern.getCompletionSizeLimit();
		}
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getUserMail() {
		return userMail;
	}

	public void setUserMail(String userMail) {
		this.userMail = userMail;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	public String getLdapUid() {
		return ldapUid;
	}

	public void setLdapUid(String ldapUid) {
		this.ldapUid = ldapUid;
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
}
