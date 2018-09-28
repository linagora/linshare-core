/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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

import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapAttribute;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "GroupLdapPattern")
@ApiModel(value = "GroupLdapPattern", description = "A group ldap pattern is used by domains to search WorkGroup in an LDAP directory")
public class GroupLdapPatternDto {

	@ApiModelProperty(value = "Uuid")
	private String uuid;

	@ApiModelProperty(value = "Label")
	private String label;

	@ApiModelProperty(value = "Description")
	private String description;

	@ApiModelProperty(value = "SearchPageSize")
	private Integer searchPageSize;

	@ApiModelProperty(value = "searchAllGroupsQuery")
	private String searchAllGroupsQuery;

	@ApiModelProperty(value = "searchGroupQuery")
	private String searchGroupQuery;

	@ApiModelProperty(value = "groupPrefix")
	private String groupPrefix;

	@ApiModelProperty(value = "searchInOtherDomains")
	private Boolean searchInOtherDomains;

	@ApiModelProperty(value = "groupName")
	private String groupName;

	@ApiModelProperty(value = "groupMember")
	private String groupMember;

	@ApiModelProperty(value = "memberMail")
	private String memberMail;

	@ApiModelProperty(value = "memberFirstName")
	private String memberFirstName;

	@ApiModelProperty(value = "memberLastName")
	private String memberLastName;

	public GroupLdapPatternDto(GroupLdapPattern groupLdapPattern) {
		this.uuid = groupLdapPattern.getUuid();
		this.label = groupLdapPattern.getLabel();
		this.description = groupLdapPattern.getDescription();
		Map<String, LdapAttribute> attributes = groupLdapPattern.getAttributes();

		this.groupName = attributes.get(GroupLdapPattern.GROUP_NAME).getAttribute();
		this.groupMember = attributes.get(GroupLdapPattern.GROUP_MEMBER).getAttribute();
		this.memberFirstName = attributes.get(GroupLdapPattern.MEMBER_FIRST_NAME).getAttribute();
		this.memberLastName = attributes.get(GroupLdapPattern.MEMBER_LAST_NAME).getAttribute();
		this.memberMail = attributes.get(GroupLdapPattern.MEMBER_MAIL).getAttribute();

		this.searchPageSize = groupLdapPattern.getSearchPageSize();
		this.searchAllGroupsQuery = groupLdapPattern.getSearchAllGroupsQuery();
		this.searchGroupQuery = groupLdapPattern.getSearchGroupQuery();
		this.groupPrefix = groupLdapPattern.getGroupPrefix();
		this.searchInOtherDomains = groupLdapPattern.getSearchInOtherDomains();
	}

	public GroupLdapPatternDto() {
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

	public Integer getSearchPageSize() {
		return searchPageSize;
	}

	public void setSearchPageSize(Integer searchPageSize) {
		this.searchPageSize = searchPageSize;
	}

	public String getSearchAllGroupsQuery() {
		return searchAllGroupsQuery;
	}

	public void setSearchAllGroupsQuery(String searchAllGroupsQuery) {
		this.searchAllGroupsQuery = searchAllGroupsQuery;
	}

	public String getSearchGroupQuery() {
		return searchGroupQuery;
	}

	public void setSearchGroupQuery(String searchGroupQuery) {
		this.searchGroupQuery = searchGroupQuery;
	}

	public String getGroupPrefix() {
		return groupPrefix;
	}

	public void setGroupPrefix(String groupPrefix) {
		this.groupPrefix = groupPrefix;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupMember() {
		return groupMember;
	}

	public void setGroupMember(String groupMember) {
		this.groupMember = groupMember;
	}

	public String getMemberMail() {
		return memberMail;
	}

	public void setMemberMail(String memberMail) {
		this.memberMail = memberMail;
	}

	public String getMemberFirstName() {
		return memberFirstName;
	}

	public void setMemberFirstName(String memberFirstName) {
		this.memberFirstName = memberFirstName;
	}

	public String getMemberLastName() {
		return memberLastName;
	}

	public void setMemberLastName(String memberLastName) {
		this.memberLastName = memberLastName;
	}

	public Boolean getSearchInOtherDomains() {
		return searchInOtherDomains;
	}

	public void setSearchInOtherDomains(Boolean searchInOtherDomains) {
		this.searchInOtherDomains = searchInOtherDomains;
	}

}
