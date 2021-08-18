/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2021. Contribute to
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

import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapAttribute;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;
import com.google.common.collect.Maps;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "LdapGroupFilter")
@Schema(name = "LdapGroupFilter", description = "A LDAP group filter is used by domains to search WorkGroup in an LDAP directory")
public class LDAPWorkGroupFilterDto extends AbstractGroupFilterDto {

	@Schema(description = "The size of the returned page, result of the search query", required = true)
	private Integer searchPageSize;

	@Schema(description = "The query used to search all LDAP groups", required = true)
	private String searchAllGroupsQuery;

	@Schema(description = "The query used to search a LDAP group.", required = true)
	private String searchGroupQuery;

	@Schema(description = "This prefix will be removed from LDAP group name (cn), in order to get the final workgroup name.", required = true)
	private String groupPrefixToRemove;

	@Schema(description = "The LDAP group name attribute in the LDAP group (Ex: for openLdap attribute = 'cn').", required = true)
	private String groupNameAttribute;

	@Schema(description = "The member's attribute in the LDAP group (Ex: for openLdap attribute = 'member').", required = true)
	private String groupMemberAttribute;

	@Schema(description = "The member's mail attribute in the LDAP group (Ex: for openLdap attribute = 'mail')..", required = true)
	private String memberMailAttribute;

	@Schema(description = "The member's firstName attribute in the group LDAP (Ex: for openLdap attribute = 'givenname').", required = true)
	private String memberFirstNameAttribute;

	@Schema(description = "The member's lastName attribute in the group LDAP (Ex: for openLdap attribute = 'sn').", required = true)
	private String memberLastNameAttribute;

	public LDAPWorkGroupFilterDto() {
		super();
	}

	public LDAPWorkGroupFilterDto(GroupLdapPattern groupLdapPattern) {
		this.uuid = groupLdapPattern.getUuid();
		this.name = groupLdapPattern.getLabel();
		this.description = groupLdapPattern.getDescription();
		this.type = groupLdapPattern.getType();
		this.groupNameAttribute = groupLdapPattern.getAttribute(GroupLdapPattern.GROUP_NAME);
		this.groupMemberAttribute = groupLdapPattern.getAttribute(GroupLdapPattern.GROUP_MEMBER);
		this.memberFirstNameAttribute = groupLdapPattern.getAttribute(GroupLdapPattern.MEMBER_FIRST_NAME);
		this.memberLastNameAttribute = groupLdapPattern.getAttribute(GroupLdapPattern.MEMBER_LAST_NAME);
		this.memberMailAttribute = groupLdapPattern.getAttribute(GroupLdapPattern.MEMBER_MAIL);
		this.searchPageSize = groupLdapPattern.getSearchPageSize();
		this.searchAllGroupsQuery = groupLdapPattern.getSearchAllGroupsQuery();
		this.searchGroupQuery = groupLdapPattern.getSearchGroupQuery();
		this.groupPrefixToRemove = groupLdapPattern.getGroupPrefix();
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

	public String getGroupPrefixToRemove() {
		return groupPrefixToRemove;
	}

	public void setGroupPrefixToRemove(String groupPrefixToRemove) {
		this.groupPrefixToRemove = groupPrefixToRemove;
	}

	public String getGroupNameAttribute() {
		return groupNameAttribute;
	}

	public void setGroupNameAttribute(String groupNameAttribute) {
		this.groupNameAttribute = groupNameAttribute;
	}

	public String getGroupMemberAttribute() {
		return groupMemberAttribute;
	}

	public void setGroupMemberAttribute(String groupMemberAttribute) {
		this.groupMemberAttribute = groupMemberAttribute;
	}

	public String getMemberMailAttribute() {
		return memberMailAttribute;
	}

	public void setMemberMailAttribute(String memberMailAttribute) {
		this.memberMailAttribute = memberMailAttribute;
	}

	public String getMemberFirstNameAttribute() {
		return memberFirstNameAttribute;
	}

	public void setMemberFirstNameAttribute(String memberFirstNameAttribute) {
		this.memberFirstNameAttribute = memberFirstNameAttribute;
	}

	public String getMemberLastNameAttribute() {
		return memberLastNameAttribute;
	}

	public void setMemberLastNameAttribute(String memberLastNameAttribute) {
		this.memberLastNameAttribute = memberLastNameAttribute;
	}

	/*
	 * Transformers
	 */
	public static Function<GroupLdapPattern, LDAPWorkGroupFilterDto> toDto() {
		return new Function<GroupLdapPattern, LDAPWorkGroupFilterDto>() {
			@Override
			public LDAPWorkGroupFilterDto apply(GroupLdapPattern arg0) {
				return new LDAPWorkGroupFilterDto(arg0);
			}
		};
	}

	public GroupLdapPattern toLdapGroupFilterObject() {
		GroupLdapPattern groupLdapPattern = new GroupLdapPattern();
		groupLdapPattern.setUuid(getUuid());
		groupLdapPattern.setLabel(getName());
		groupLdapPattern.setDescription(getDescription());
		groupLdapPattern.setGroupPrefix(getGroupPrefixToRemove());
		Map<String, LdapAttribute> attributes = Maps.newHashMap();
		attributes.put(GroupLdapPattern.GROUP_NAME, new LdapAttribute(GroupLdapPattern.GROUP_NAME, getGroupNameAttribute(), true));
		attributes.put(GroupLdapPattern.GROUP_MEMBER, new LdapAttribute(GroupLdapPattern.GROUP_MEMBER, getGroupMemberAttribute(), true));
		attributes.put(GroupLdapPattern.MEMBER_FIRST_NAME, new LdapAttribute(GroupLdapPattern.MEMBER_FIRST_NAME, getMemberLastNameAttribute(), false));
		attributes.put(GroupLdapPattern.MEMBER_LAST_NAME, new LdapAttribute(GroupLdapPattern.MEMBER_LAST_NAME, getMemberFirstNameAttribute(), false));
		attributes.put(GroupLdapPattern.MEMBER_MAIL, new LdapAttribute(GroupLdapPattern.MEMBER_MAIL, getMemberMailAttribute(), false));
		groupLdapPattern.setAttributes(attributes);
		groupLdapPattern.setSearchPageSize(getSearchPageSize());
		groupLdapPattern.setSearchAllGroupsQuery(getSearchAllGroupsQuery());
		groupLdapPattern.setSearchGroupQuery(getSearchGroupQuery());
		return groupLdapPattern;
	}
}
