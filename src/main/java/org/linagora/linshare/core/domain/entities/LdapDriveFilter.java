/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.domain.entities;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.DriveFilterType;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.LDAPDriveFilterDto;

public class LdapDriveFilter extends LdapPattern {

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

	public LdapDriveFilter() {
		super();
	}

	public LdapDriveFilter(LDAPDriveFilterDto driveLdapPatternDto) {
		this.uuid = driveLdapPatternDto.getUuid();
		this.label = driveLdapPatternDto.getName();
		this.description = driveLdapPatternDto.getDescription();
		this.system = false;
		this.searchPageSize = driveLdapPatternDto.getSearchPageSize();
		this.searchAllGroupsQuery = driveLdapPatternDto.getSearchAllGroupsQuery();
		this.searchGroupQuery = driveLdapPatternDto.getSearchGroupQuery();
		this.groupPrefix = driveLdapPatternDto.getGroupPrefixToRemove();
		this.attributes = new HashMap<String, LdapAttribute>();
		this.attributes.put(GROUP_NAME, new LdapAttribute(GROUP_NAME, driveLdapPatternDto.getGroupNameAttribute(), true));
		this.attributes.put(GROUP_MEMBER, new LdapAttribute(GROUP_MEMBER, driveLdapPatternDto.getGroupMemberAttribute(), true));
		this.attributes.put(MEMBER_LAST_NAME, new LdapAttribute(MEMBER_LAST_NAME, driveLdapPatternDto.getMemberLastNameAttribute(), false));
		this.attributes.put(MEMBER_FIRST_NAME, new LdapAttribute(MEMBER_FIRST_NAME, driveLdapPatternDto.getMemberFirstNameAttribute(), false));
		this.attributes.put(MEMBER_MAIL, new LdapAttribute(MEMBER_MAIL, driveLdapPatternDto.getMemberMailAttribute(), false));
	}

	/**
	 * For tests only.
	 */
	public LdapDriveFilter(String label, String description, String searchAllGroupsQuery,
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


	public DriveFilterType getType() {
		return DriveFilterType.LDAP;
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
		return "DriveLdapPattern [label=" + label + ", uuid=" + uuid + "]";
	}
}
