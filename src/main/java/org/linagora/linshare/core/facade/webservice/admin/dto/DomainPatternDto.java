/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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
package org.linagora.linshare.core.facade.webservice.admin.dto;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.UserLdapPattern;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "DomainPattern")
@Schema(name = "DomainPattern", description = "A domain pattern is used by domains to search users in an LDAP directory")
public class DomainPatternDto {

	@Schema(description = "Uuid")
	private String uuid;

	@Schema(description = "Label")
	private String label;

	@Schema(description = "Description")
	private String description;

	@Schema(description = "AuthCommand")
	private String authCommand;

	@Schema(description = "SearchUserCommand")
	private String searchUserCommand;

	@Schema(description = "UserMail")
	private String userMail;

	@Schema(description = "UserFirstName")
	private String userFirstName;

	@Schema(description = "UserLastName")
	private String userLastName;

	@Schema(description = "LdapUid")
	private String ldapUid;

	@Schema(description = "AutoCompleteCommandOnAllAttributes")
	private String autoCompleteCommandOnAllAttributes;

	@Schema(description = "AutoCompleteCommandOnFirstAndLastName")
	private String autoCompleteCommandOnFirstAndLastName;

	@Schema(description = "SearchPageSize")
	private Integer searchPageSize;

	@Schema(description = "SearchSizeLimit")
	private Integer searchSizeLimit;

	@Schema(description = "CompletionPageSize")
	private Integer completionPageSize;

	@Schema(description = "CompletionSizeLimit")
	private Integer completionSizeLimit;

	public DomainPatternDto(UserLdapPattern domainPattern) {
		this.uuid = domainPattern.getUuid();
		this.label = domainPattern.getLabel();
		this.description = domainPattern.getDescription();
		this.authCommand = domainPattern.getAuthCommand();
		this.searchUserCommand = domainPattern.getSearchUserCommand();

		Map<String, LdapAttribute> attributes = domainPattern.getAttributes();
		this.userMail = attributes.get(UserLdapPattern.USER_MAIL)
				.getAttribute();
		this.userFirstName = attributes.get(UserLdapPattern.USER_FIRST_NAME)
				.getAttribute();
		this.userLastName = attributes.get(UserLdapPattern.USER_LAST_NAME)
				.getAttribute();
		this.ldapUid = attributes.get(UserLdapPattern.USER_UID).getAttribute();

		this.autoCompleteCommandOnAllAttributes = domainPattern
				.getAutoCompleteCommandOnAllAttributes();
		this.autoCompleteCommandOnFirstAndLastName = domainPattern
				.getAutoCompleteCommandOnFirstAndLastName();
		this.searchPageSize = domainPattern.getSearchPageSize();
		this.searchSizeLimit = domainPattern.getSearchSizeLimit();
		this.completionPageSize = domainPattern.getCompletionPageSize();
		this.completionSizeLimit = domainPattern.getCompletionSizeLimit();
	}

	public DomainPatternDto() {
		super();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
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

	public String getAutoCompleteCommandOnAllAttributes() {
		return autoCompleteCommandOnAllAttributes;
	}

	public void setAutoCompleteCommandOnAllAttributes(
			String autoCompleteCommandOnAllAttributes) {
		this.autoCompleteCommandOnAllAttributes = autoCompleteCommandOnAllAttributes;
	}

	public String getAutoCompleteCommandOnFirstAndLastName() {
		return autoCompleteCommandOnFirstAndLastName;
	}

	public void setAutoCompleteCommandOnFirstAndLastName(
			String autoCompleteCommandOnFirstAndLastName) {
		this.autoCompleteCommandOnFirstAndLastName = autoCompleteCommandOnFirstAndLastName;
	}

}
