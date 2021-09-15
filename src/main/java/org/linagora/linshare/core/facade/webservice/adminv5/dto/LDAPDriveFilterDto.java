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

import org.linagora.linshare.core.domain.constants.DriveFilterType;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.LdapDriveFilter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;
import com.google.common.collect.Maps;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "DriveLdapFilter", description = "A drive ldap filter is used by domains to search groups in an LDAP directory and create an associated Drives on LinShare")
public class LDAPDriveFilterDto extends AbstractDriveFilterDto {

	@Schema(description = "The size of the returned page, result of the search query", required = true)
	private Integer searchPageSize;

	@Schema(description = "The query used to search all LDAP groups", required = true)
	private String searchAllGroupsQuery;

	@Schema(description = "The query used to search a LDAP group.", required = true)
	private String searchGroupQuery;

	@Schema(description = "This prefix will be removed from LDAP group name (cn), in order to get the final workgroup name.", required = true)
	private String groupPrefixToRemove;

	@Schema(description = "The LDAP group name attribute in the LDAP group (Ex: for OpenLDAP attribute = 'cn').", required = true)
	private String groupNameAttribute;

	@Schema(description = "The member's attribute in the LDAP group (Ex: for OpenLDAP attribute = 'member').", required = true)
	private String groupMemberAttribute;

	@Schema(description = "The member's mail attribute in the LDAP group (Ex: for OpenLDAP attribute = 'mail')..", required = true)
	private String memberMailAttribute;

	@Schema(description = "The member's firstName attribute in the group LDAP (Ex: for OpenLDAP attribute = 'givenname').", required = true)
	private String memberFirstNameAttribute;

	@Schema(description = "The member's lastName attribute in the group LDAP (Ex: for OpenLDAP attribute = 'sn').", required = true)
	private String memberLastNameAttribute;

	public LDAPDriveFilterDto() {
		super();
	}

	public LDAPDriveFilterDto(LdapDriveFilter driveLdapPattern) {
		this.uuid = driveLdapPattern.getUuid();
		this.name = driveLdapPattern.getLabel();
		this.description = driveLdapPattern.getDescription();
		this.type = driveLdapPattern.getType();
		this.groupNameAttribute = driveLdapPattern.getAttribute(LdapDriveFilter.GROUP_NAME);
		this.groupMemberAttribute = driveLdapPattern.getAttribute(LdapDriveFilter.GROUP_MEMBER);
		this.memberFirstNameAttribute = driveLdapPattern.getAttribute(LdapDriveFilter.MEMBER_FIRST_NAME);
		this.memberLastNameAttribute = driveLdapPattern.getAttribute(LdapDriveFilter.MEMBER_LAST_NAME);
		this.memberMailAttribute = driveLdapPattern.getAttribute(LdapDriveFilter.MEMBER_MAIL);
		this.searchPageSize = driveLdapPattern.getSearchPageSize();
		this.searchAllGroupsQuery = driveLdapPattern.getSearchAllGroupsQuery();
		this.searchGroupQuery = driveLdapPattern.getSearchGroupQuery();
		this.groupPrefixToRemove = driveLdapPattern.getGroupPrefix();
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

	@Schema(defaultValue = "LDAP")
	@Override
	public DriveFilterType getType() {
		return DriveFilterType.LDAP;
	}

	/*
	 * Transformers
	 */
	public static Function<LdapDriveFilter, LDAPDriveFilterDto> toDto() {
		return new Function<LdapDriveFilter, LDAPDriveFilterDto>() {
			@Override
			public LDAPDriveFilterDto apply(LdapDriveFilter ldapDriveFilter) {
				return new LDAPDriveFilterDto(ldapDriveFilter);
			}
		};
	}

	public LdapDriveFilter toLdapDriveFilterObject() {
		LdapDriveFilter driveLdapPattern = new LdapDriveFilter();
		driveLdapPattern.setUuid(getUuid());
		driveLdapPattern.setLabel(getName());
		driveLdapPattern.setDescription(getDescription());
		driveLdapPattern.setGroupPrefix(getGroupPrefixToRemove());
		Map<String, LdapAttribute> attributes = Maps.newHashMap();
		attributes.put(LdapDriveFilter.GROUP_NAME, new LdapAttribute(LdapDriveFilter.GROUP_NAME, getGroupNameAttribute(), true));
		attributes.put(LdapDriveFilter.GROUP_MEMBER, new LdapAttribute(LdapDriveFilter.GROUP_MEMBER, getGroupMemberAttribute(), true));
		attributes.put(LdapDriveFilter.MEMBER_FIRST_NAME, new LdapAttribute(LdapDriveFilter.MEMBER_FIRST_NAME, getMemberFirstNameAttribute(), false));
		attributes.put(LdapDriveFilter.MEMBER_LAST_NAME, new LdapAttribute(LdapDriveFilter.MEMBER_LAST_NAME, getMemberLastNameAttribute(), false));
		attributes.put(LdapDriveFilter.MEMBER_MAIL, new LdapAttribute(LdapDriveFilter.MEMBER_MAIL, getMemberMailAttribute(), false));
		driveLdapPattern.setAttributes(attributes);
		driveLdapPattern.setSearchPageSize(getSearchPageSize());
		driveLdapPattern.setSearchAllGroupsQuery(getSearchAllGroupsQuery());
		driveLdapPattern.setSearchGroupQuery(getSearchGroupQuery());
		return driveLdapPattern;
	}
}
