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
package org.linagora.linshare.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.LDAPWorkSpaceQueryService;
import org.linagora.linshare.ldap.LdapWorkSpaceMemberObject;
import org.linagora.linshare.ldap.LdapGroupObject;
import org.linagora.linshare.ldap.Role;
import org.linagora.linshare.server.embedded.ldap.LdapServerRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ExtendWith(LdapServerRule.class)
@ContextConfiguration(locations = {
		"classpath:springContext-test.xml",
		"classpath:springContext-ldap.xml"
})
public class LDAPWorkSpaceQueryServiceImplTest {

	protected Logger logger = LoggerFactory.getLogger(LDAPWorkSpaceQueryServiceImplTest.class);

	@Autowired
	private LDAPWorkSpaceQueryService ldapWorkSpaceQueryService;

	private LdapConnection ldapConnection;

	private GroupLdapPattern groupPattern;

	private Map<String, LdapAttribute> attributes;

	private String baseDn;

	private static final String user2_email = "user2@linshare.org";

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		attributes = new HashMap<String, LdapAttribute>();
		attributes.put(GroupLdapPattern.GROUP_NAME, new LdapAttribute(GroupLdapPattern.GROUP_NAME, "cn"));
		attributes.put(GroupLdapPattern.GROUP_MEMBER, new LdapAttribute(GroupLdapPattern.GROUP_MEMBER, "member"));
		attributes.put(GroupLdapPattern.MEMBER_MAIL, new LdapAttribute(GroupLdapPattern.MEMBER_MAIL, "mail"));
		attributes.put(GroupLdapPattern.MEMBER_FIRST_NAME, new LdapAttribute(GroupLdapPattern.MEMBER_FIRST_NAME, "givenName"));
		attributes.put(GroupLdapPattern.MEMBER_LAST_NAME, new LdapAttribute(GroupLdapPattern.MEMBER_LAST_NAME, "sn"));

		groupPattern = new GroupLdapPattern();
		groupPattern.setAttributes(attributes);
		groupPattern.setSearchAllGroupsQuery("ldap.search(baseDn, \"(&(objectClass=groupOfNames)(cn=workspace-*))\");");
		groupPattern.setSearchGroupQuery("ldap.search(baseDn, \"(&(objectClass=groupOfNames)(cn=workspace-\" + pattern + \"))\");");
		groupPattern.setSearchPageSize(100);
		groupPattern.setGroupPrefix("workspace-");

		ldapConnection = new LdapConnection("testldap", "ldap://localhost:33389", "anonymous");
		baseDn = "ou=Groups,dc=linshare,dc=org";
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testSearchAllGroups() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Set<LdapGroupObject> listGroups = ldapWorkSpaceQueryService.listGroups(ldapConnection, baseDn, groupPattern);
		for (LdapGroupObject ldapGroup : listGroups) {
			logger.info("WORKSPACES: {}", ldapGroup.toString());
			if (!ldapGroup.getMembers().isEmpty()) {
				logger.info(ldapGroup.getMembers().toString());
			}
			Assertions.assertEquals("workspace-1", ldapGroup.getName());
			Assertions.assertEquals("workspace-workspace-1", ldapGroup.getNameWithPrefix());
			Assertions.assertEquals("cn=workspace-workspace-1,ou=Groups,dc=linshare,dc=org", ldapGroup.getExternalId());
		}
		Assertions.assertEquals(1, listGroups.size());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testMembersWithNoRoles() throws BusinessException, NamingException, IOException {
		String baseDn = "ou=Groups,dc=linshare,dc=org";
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Set<LdapGroupObject> listGroups = ldapWorkSpaceQueryService.listGroups(ldapConnection, baseDn, groupPattern);
		for (LdapGroupObject ldapGroup : listGroups) {
			logger.info("WORKSPACES: {}", ldapGroup.toString());
			Assertions.assertEquals("cn=workspace-workspace-1,ou=Groups,dc=linshare,dc=org", ldapGroup.getExternalId());
			Set<LdapWorkSpaceMemberObject> listMembers = ldapWorkSpaceQueryService.listWorkSpaceMembers(ldapConnection, baseDn,
					groupPattern, ldapGroup);
			for (LdapWorkSpaceMemberObject member : listMembers) {
				logger.info(member.toString());
				Assertions.assertEquals("John", member.getFirstName());
				Assertions.assertEquals("Doe", member.getLastName());
				Assertions.assertEquals("user1@linshare.org", member.getEmail());
				Assertions.assertEquals(Role.READER, member.getNestedRole());
				Assertions.assertEquals(Role.WORK_SPACE_READER, member.getRole());
			}
			Assertions.assertEquals(1, listMembers.size());
		}
		Assertions.assertEquals(1, listGroups.size());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testMemberWithJustWGRole() throws BusinessException, NamingException, IOException {
		String baseDn = "ou=Groups2,dc=linshare,dc=org";
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Set<LdapGroupObject> listGroups = ldapWorkSpaceQueryService.listGroups(ldapConnection, baseDn, groupPattern);
		for (LdapGroupObject ldapGroup : listGroups) {
			logger.info("WORKSPACES: {}", ldapGroup.toString());
			Assertions.assertEquals("cn=workspace-workspace-2,ou=Groups2,dc=linshare,dc=org", ldapGroup.getExternalId());
			Set<LdapWorkSpaceMemberObject> listMembers = ldapWorkSpaceQueryService.listWorkSpaceMembers(ldapConnection, baseDn,
					groupPattern, ldapGroup);
			for (LdapWorkSpaceMemberObject member : listMembers) {
				logger.info(member.toString());
				if (user2_email.equals(member.getEmail())) {
					Assertions.assertEquals("Jane", member.getFirstName());
					Assertions.assertEquals("Smith", member.getLastName());
					Assertions.assertEquals("user2@linshare.org", member.getEmail());
					Assertions.assertEquals(Role.CONTRIBUTOR, member.getNestedRole());
					Assertions.assertEquals(Role.WORK_SPACE_READER, member.getRole());
				}
			}
			Assertions.assertEquals(2, listMembers.size());
		}
		Assertions.assertEquals(1, listGroups.size());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testMemberWithJustWorkSpaceRole() throws BusinessException, NamingException, IOException {
		String baseDn = "ou=Groups3,dc=linshare,dc=org";
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Set<LdapGroupObject> listGroups = ldapWorkSpaceQueryService.listGroups(ldapConnection, baseDn, groupPattern);
		for (LdapGroupObject ldapGroup : listGroups) {
			logger.info("WORKSPACES: {}", ldapGroup.toString());
			Assertions.assertEquals("cn=workspace-workspace-3,ou=Groups3,dc=linshare,dc=org", ldapGroup.getExternalId());
			Set<LdapWorkSpaceMemberObject> listMembers = ldapWorkSpaceQueryService.listWorkSpaceMembers(ldapConnection, baseDn,
					groupPattern, ldapGroup);
			for (LdapWorkSpaceMemberObject member : listMembers) {
				logger.info(member.toString());
				if (user2_email.equals(member.getEmail())) {
					Assertions.assertEquals("Jane", member.getFirstName());
					Assertions.assertEquals("Smith", member.getLastName());
					Assertions.assertEquals("user2@linshare.org", member.getEmail());
					Assertions.assertEquals(Role.READER, member.getNestedRole());
					Assertions.assertEquals(Role.WORK_SPACE_WRITER, member.getRole());
				}
			}
			Assertions.assertEquals(2, listMembers.size());
		}
		Assertions.assertEquals(1, listGroups.size());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testMemberWithWgRoleAndWorkSpaceRole() throws BusinessException, NamingException, IOException {
		String baseDn = "ou=Groups4,dc=linshare,dc=org";
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Set<LdapGroupObject> listGroups = ldapWorkSpaceQueryService.listGroups(ldapConnection, baseDn, groupPattern);
		for (LdapGroupObject ldapGroup : listGroups) {
			logger.info("WORKSPACES: {}", ldapGroup.toString());
			Assertions.assertEquals("cn=workspace-workspace-4,ou=Groups4,dc=linshare,dc=org", ldapGroup.getExternalId());
			Set<LdapWorkSpaceMemberObject> listMembers = ldapWorkSpaceQueryService.listWorkSpaceMembers(ldapConnection, baseDn,
					groupPattern, ldapGroup);
			for (LdapWorkSpaceMemberObject member : listMembers) {
				logger.info(member.toString());
				if (user2_email.equals(member.getEmail())) {
					Assertions.assertEquals("Jane", member.getFirstName());
					Assertions.assertEquals("Smith", member.getLastName());
					Assertions.assertEquals("user2@linshare.org", member.getEmail());
					Assertions.assertEquals(Role.WRITER, member.getNestedRole());
					Assertions.assertEquals(Role.WORK_SPACE_WRITER, member.getRole());
				}
			}
			Assertions.assertEquals(2, listMembers.size());
		}
		Assertions.assertEquals(1, listGroups.size());
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
