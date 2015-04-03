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
package org.linagora.linshare.core.facade.webservice.admin.dto;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.UserLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapAttribute;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "DomainPattern")
@ApiModel(value = "DomainPattern", description = "A domain pattern is used by domains to search users in an LDAP directory")
public class DomainPatternDto {

	@ApiModelProperty(value = "Uuid")
	private String uuid;

	@ApiModelProperty(value = "Label")
	private String label;

	@ApiModelProperty(value = "Description")
	private String description;

	@ApiModelProperty(value = "AuthCommand")
	private String authCommand;

	@ApiModelProperty(value = "SearchUserCommand")
	private String searchUserCommand;

	@ApiModelProperty(value = "UserMail")
	private String userMail;

	@ApiModelProperty(value = "UserFirstName")
	private String userFirstName;

	@ApiModelProperty(value = "UserLastName")
	private String userLastName;

	@ApiModelProperty(value = "LdapUid")
	private String ldapUid;

	@ApiModelProperty(value = "AutoCompleteCommandOnAllAttributes")
	private String autoCompleteCommandOnAllAttributes;

	@ApiModelProperty(value = "AutoCompleteCommandOnFirstAndLastName")
	private String autoCompleteCommandOnFirstAndLastName;

	@ApiModelProperty(value = "SearchPageSize")
	private Integer searchPageSize;

	@ApiModelProperty(value = "SearchSizeLimit")
	private Integer searchSizeLimit;

	@ApiModelProperty(value = "CompletionPageSize")
	private Integer completionPageSize;

	@ApiModelProperty(value = "CompletionSizeLimit")
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
