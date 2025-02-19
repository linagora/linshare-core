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

import org.linagora.linshare.core.domain.constants.GroupFilterType;
import org.linagora.linshare.core.facade.webservice.admin.dto.GroupLdapPatternDto;

public class GroupLdapPattern extends LdapPattern {

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

	public GroupLdapPattern() {
		super();
	}

	public GroupLdapPattern(GroupLdapPatternDto groupLdapPatternDto) {
		this.uuid = groupLdapPatternDto.getUuid();
		this.label = groupLdapPatternDto.getLabel();
		this.description = groupLdapPatternDto.getDescription();
		this.system = false;
		this.searchPageSize = groupLdapPatternDto.getSearchPageSize();
		this.searchAllGroupsQuery = groupLdapPatternDto.getSearchAllGroupsQuery();
		this.searchGroupQuery = groupLdapPatternDto.getSearchGroupQuery();
		this.groupPrefix = groupLdapPatternDto.getGroupPrefix();
		this.attributes = new HashMap<String, LdapAttribute>();
		this.attributes.put(GROUP_NAME, new LdapAttribute(GROUP_NAME, groupLdapPatternDto.getGroupName(), true));
		this.attributes.put(GROUP_MEMBER, new LdapAttribute(GROUP_MEMBER, groupLdapPatternDto.getGroupMember(), true));

		this.attributes.put(MEMBER_LAST_NAME,
				new LdapAttribute(MEMBER_LAST_NAME, groupLdapPatternDto.getMemberLastName(), false));
		this.attributes.put(MEMBER_FIRST_NAME,
				new LdapAttribute(MEMBER_FIRST_NAME, groupLdapPatternDto.getMemberFirstName(), false));
		this.attributes.put(MEMBER_MAIL, new LdapAttribute(MEMBER_MAIL, groupLdapPatternDto.getMemberMail(), false));
	}

	/**
	 * For tests only.
	 */
	public GroupLdapPattern(String label, String description, String searchAllGroupsQuery,
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

	@Override
	public String toString() {
		return "GroupLdapPattern [label=" + label + ", uuid=" + uuid + "]";
	}

	public GroupFilterType getType() {
		return GroupFilterType.LDAP;
	}

	@Override
	public String getAttribute(String field) {
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

}
