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
package org.linagora.linshare.core.facade.webservice.admin.dto;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapAttribute;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "GroupLdapPattern")
@Schema(name = "GroupLdapPattern", description = "A group ldap pattern is used by domains to search WorkGroup in an LDAP directory")
public class GroupLdapPatternDto {

	@Schema(description = "Uuid")
	private String uuid;

	@Schema(description = "Label")
	private String label;

	@Schema(description = "Description")
	private String description;

	@Schema(description = "SearchPageSize")
	private Integer searchPageSize;

	@Schema(description = "searchAllGroupsQuery")
	private String searchAllGroupsQuery;

	@Schema(description = "searchGroupQuery")
	private String searchGroupQuery;

	@Schema(description = "groupPrefix")
	private String groupPrefix;

	@Schema(description = "groupName")
	private String groupName;

	@Schema(description = "groupMember")
	private String groupMember;

	@Schema(description = "memberMail")
	private String memberMail;

	@Schema(description = "memberFirstName")
	private String memberFirstName;

	@Schema(description = "memberLastName")
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

}
