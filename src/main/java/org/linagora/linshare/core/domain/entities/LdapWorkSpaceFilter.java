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

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.WorkSpaceFilterType;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.LDAPWorkSpaceFilterDto;

public class LdapWorkSpaceFilter extends LdapPattern {

	// ldap: group
	public static final String GROUP_NAME = "group_name_attr";
	public static final String GROUP_MEMBER = "extended_group_member_attr";

	// ldap: group member
	public static final String MEMBER_MAIL = "member_mail";
	public static final String MEMBER_FIRST_NAME = "member_firstname";
	public static final String MEMBER_LAST_NAME = "member_lastname";

	protected String searchAllGroupsQuery;

	protected String searchGroupQuery;

	protected String groupPrefix;

	protected Integer searchPageSize;

	public LdapWorkSpaceFilter() {
		super();
	}

	public LdapWorkSpaceFilter(LDAPWorkSpaceFilterDto workSpaceLdapFilterDto) {
		this.uuid = workSpaceLdapFilterDto.getUuid();
		this.label = workSpaceLdapFilterDto.getName();
		this.description = workSpaceLdapFilterDto.getDescription();
		this.system = false;
		this.searchPageSize = workSpaceLdapFilterDto.getSearchPageSize();
		this.searchAllGroupsQuery = workSpaceLdapFilterDto.getSearchAllGroupsQuery();
		this.searchGroupQuery = workSpaceLdapFilterDto.getSearchGroupQuery();
		this.groupPrefix = workSpaceLdapFilterDto.getGroupPrefixToRemove();
		this.attributes = new HashMap<String, LdapAttribute>();
		this.attributes.put(GROUP_NAME, new LdapAttribute(GROUP_NAME, workSpaceLdapFilterDto.getGroupNameAttribute(), true));
		this.attributes.put(GROUP_MEMBER, new LdapAttribute(GROUP_MEMBER, workSpaceLdapFilterDto.getGroupMemberAttribute(), true));
		this.attributes.put(MEMBER_LAST_NAME, new LdapAttribute(MEMBER_LAST_NAME, workSpaceLdapFilterDto.getMemberLastNameAttribute(), false));
		this.attributes.put(MEMBER_FIRST_NAME, new LdapAttribute(MEMBER_FIRST_NAME, workSpaceLdapFilterDto.getMemberFirstNameAttribute(), false));
		this.attributes.put(MEMBER_MAIL, new LdapAttribute(MEMBER_MAIL, workSpaceLdapFilterDto.getMemberMailAttribute(), false));
	}

	/**
	 * For tests only.
	 */
	public LdapWorkSpaceFilter(String label, String description, String searchAllGroupsQuery,
			String searchGroupQuery, String groupPrefix, Boolean searchInOtherDomains) {
		this.label = label;
		this.description = description;
		this.system = false;
		this.searchAllGroupsQuery = searchAllGroupsQuery;
		this.searchGroupQuery = searchGroupQuery;
		this.groupPrefix = groupPrefix;
		this.searchPageSize = 0;
		this.attributes = new HashMap<String, LdapAttribute>();
		this.attributes.put(GROUP_NAME, new LdapAttribute(GROUP_NAME, "attribute", true));
		this.attributes.put(GROUP_MEMBER, new LdapAttribute(GROUP_MEMBER, "attribute", true));
		this.attributes.put(MEMBER_LAST_NAME, new LdapAttribute(MEMBER_LAST_NAME, "attribute", false));
		this.attributes.put(MEMBER_FIRST_NAME, new LdapAttribute(MEMBER_FIRST_NAME, "attribute", false));
		this.attributes.put(MEMBER_MAIL, new LdapAttribute(MEMBER_MAIL, "attribute", false));
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

	public Integer getSearchPageSize() {
		return searchPageSize;
	}

	public void setSearchPageSize(Integer searchPageSize) {
		this.searchPageSize = searchPageSize;
	}

	public String getGroupPrefix() {
		return groupPrefix;
	}

	public void setGroupPrefix(String groupPrefix) {
		this.groupPrefix = groupPrefix;
	}


	public WorkSpaceFilterType getType() {
		return WorkSpaceFilterType.LDAP;
	}

	public String getAttribute(String field) {
		Validate.notEmpty(field, "Field must be set.");
		return attributes.get(field).getAttribute().trim().toLowerCase();
	}

	@Override
	public Map<String, String> getMethodsMapping() {
		Map<String, String> methodsMapping = super.getMethodsMapping();
		methodsMapping.put(DN, "setExternalId");
		methodsMapping.put(GROUP_NAME, "setName");
		methodsMapping.put(GROUP_MEMBER, "addMember");
		methodsMapping.put(MEMBER_LAST_NAME, "setLastName");
		methodsMapping.put(MEMBER_FIRST_NAME, "setFirstName");
		methodsMapping.put(MEMBER_MAIL, "setEmail");
		return methodsMapping;
	}

	@Override
	public String toString() {
		return "WorkSpaceLdapPattern [label=" + label + ", uuid=" + uuid + "]";
	}
}
