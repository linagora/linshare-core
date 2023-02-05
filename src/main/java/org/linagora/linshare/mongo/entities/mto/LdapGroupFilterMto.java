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

import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.LdapWorkSpaceFilter;

public class LdapGroupFilterMto extends AbstractGroupFilterMto {

	private String groupName;

	private String groupMember;

	private String memberMail;

	private String memberFirstName;

	private String memberLastName;

	private String searchAllGroupsQuery;

	private String searchGroupQuery;

	private String groupPrefix;

	private Integer searchPageSize;

	public LdapGroupFilterMto() {
		super();
	}

	public LdapGroupFilterMto(GroupLdapPattern groupFilter) {
		super(groupFilter);
		this.searchAllGroupsQuery = groupFilter.getSearchAllGroupsQuery();
		this.searchGroupQuery = groupFilter.getSearchGroupQuery();
		this.groupPrefix = groupFilter.getGroupPrefix();
		this.searchPageSize = groupFilter.getSearchPageSize();
		Map<String, LdapAttribute> attributes = groupFilter.getAttributes();
		this.groupName = attributes.get(LdapWorkSpaceFilter.GROUP_NAME).getAttribute();
		this.groupMember = attributes.get(LdapWorkSpaceFilter.GROUP_MEMBER).getAttribute();
		this.memberFirstName = attributes.get(LdapWorkSpaceFilter.MEMBER_FIRST_NAME).getAttribute();
		this.memberLastName = attributes.get(LdapWorkSpaceFilter.MEMBER_LAST_NAME).getAttribute();
		this.memberMail = attributes.get(LdapWorkSpaceFilter.MEMBER_MAIL).getAttribute();
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

	public Integer getSearchPageSize() {
		return searchPageSize;
	}

	public void setSearchPageSize(Integer searchPageSize) {
		this.searchPageSize = searchPageSize;
	}

	@Override
	public String toString() {
		return "LdapGroupFilterMto [uuid=" + uuid + ", label=" + label + ", type=" + type + "]";
	}
}
