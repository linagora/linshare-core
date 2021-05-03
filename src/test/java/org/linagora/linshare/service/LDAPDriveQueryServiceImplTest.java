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
import org.linagora.linshare.core.service.LDAPDriveQueryService;
import org.linagora.linshare.ldap.LdapDriveMemberObject;
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
public class LDAPDriveQueryServiceImplTest {

	protected Logger logger = LoggerFactory.getLogger(LDAPDriveQueryServiceImplTest.class);

	@Autowired
	private LDAPDriveQueryService ldapDriveQueryService;

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
		groupPattern.setSearchAllGroupsQuery("ldap.search(baseDn, \"(&(objectClass=groupOfNames)(cn=drive-*))\");");
		groupPattern.setSearchGroupQuery("ldap.search(baseDn, \"(&(objectClass=groupOfNames)(cn=drive-\" + pattern + \"))\");");
		groupPattern.setSearchPageSize(100);
		groupPattern.setGroupPrefix("drive-");

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
		Set<LdapGroupObject> listGroups = ldapDriveQueryService.listGroups(ldapConnection, baseDn, groupPattern);
		for (LdapGroupObject ldapGroup : listGroups) {
			logger.info("DRIVES: {}", ldapGroup.toString());
			if (!ldapGroup.getMembers().isEmpty()) {
				logger.info(ldapGroup.getMembers().toString());
			}
			Assertions.assertEquals("drive-1", ldapGroup.getName());
			Assertions.assertEquals("drive-drive-1", ldapGroup.getNameWithPrefix());
			Assertions.assertEquals("cn=drive-drive-1,ou=Groups,dc=linshare,dc=org", ldapGroup.getExternalId());
		}
		Assertions.assertEquals(1, listGroups.size());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testMembersWithNoRoles() throws BusinessException, NamingException, IOException {
		String baseDn = "ou=Groups,dc=linshare,dc=org";
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Set<LdapGroupObject> listGroups = ldapDriveQueryService.listGroups(ldapConnection, baseDn, groupPattern);
		for (LdapGroupObject ldapGroup : listGroups) {
			logger.info("DRIVES: {}", ldapGroup.toString());
			Assertions.assertEquals("cn=drive-drive-1,ou=Groups,dc=linshare,dc=org", ldapGroup.getExternalId());
			Set<LdapDriveMemberObject> listMembers = ldapDriveQueryService.listDriveMembers(ldapConnection, baseDn,
					groupPattern, ldapGroup);
			for (LdapDriveMemberObject member : listMembers) {
				logger.info(member.toString());
				Assertions.assertEquals("John", member.getFirstName());
				Assertions.assertEquals("Doe", member.getLastName());
				Assertions.assertEquals("user1@linshare.org", member.getEmail());
				Assertions.assertEquals(Role.READER, member.getNestedRole());
				Assertions.assertEquals(Role.DRIVE_READER, member.getRole());
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
		Set<LdapGroupObject> listGroups = ldapDriveQueryService.listGroups(ldapConnection, baseDn, groupPattern);
		for (LdapGroupObject ldapGroup : listGroups) {
			logger.info("DRIVES: {}", ldapGroup.toString());
			Assertions.assertEquals("cn=drive-drive-2,ou=Groups2,dc=linshare,dc=org", ldapGroup.getExternalId());
			Set<LdapDriveMemberObject> listMembers = ldapDriveQueryService.listDriveMembers(ldapConnection, baseDn,
					groupPattern, ldapGroup);
			for (LdapDriveMemberObject member : listMembers) {
				logger.info(member.toString());
				if (user2_email.equals(member.getEmail())) {
					Assertions.assertEquals("Jane", member.getFirstName());
					Assertions.assertEquals("Smith", member.getLastName());
					Assertions.assertEquals("user2@linshare.org", member.getEmail());
					Assertions.assertEquals(Role.CONTRIBUTOR, member.getNestedRole());
					Assertions.assertEquals(Role.DRIVE_READER, member.getRole());
				}
			}
			Assertions.assertEquals(2, listMembers.size());
		}
		Assertions.assertEquals(1, listGroups.size());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testMemberWithJustDriveRole() throws BusinessException, NamingException, IOException {
		String baseDn = "ou=Groups3,dc=linshare,dc=org";
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Set<LdapGroupObject> listGroups = ldapDriveQueryService.listGroups(ldapConnection, baseDn, groupPattern);
		for (LdapGroupObject ldapGroup : listGroups) {
			logger.info("DRIVES: {}", ldapGroup.toString());
			Assertions.assertEquals("cn=drive-drive-3,ou=Groups3,dc=linshare,dc=org", ldapGroup.getExternalId());
			Set<LdapDriveMemberObject> listMembers = ldapDriveQueryService.listDriveMembers(ldapConnection, baseDn,
					groupPattern, ldapGroup);
			for (LdapDriveMemberObject member : listMembers) {
				logger.info(member.toString());
				if (user2_email.equals(member.getEmail())) {
					Assertions.assertEquals("Jane", member.getFirstName());
					Assertions.assertEquals("Smith", member.getLastName());
					Assertions.assertEquals("user2@linshare.org", member.getEmail());
					Assertions.assertEquals(Role.READER, member.getNestedRole());
					Assertions.assertEquals(Role.DRIVE_WRITER, member.getRole());
				}
			}
			Assertions.assertEquals(2, listMembers.size());
		}
		Assertions.assertEquals(1, listGroups.size());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testMemberWithWgRoleAndDriveRole() throws BusinessException, NamingException, IOException {
		String baseDn = "ou=Groups4,dc=linshare,dc=org";
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Set<LdapGroupObject> listGroups = ldapDriveQueryService.listGroups(ldapConnection, baseDn, groupPattern);
		for (LdapGroupObject ldapGroup : listGroups) {
			logger.info("DRIVES: {}", ldapGroup.toString());
			Assertions.assertEquals("cn=drive-drive-4,ou=Groups4,dc=linshare,dc=org", ldapGroup.getExternalId());
			Set<LdapDriveMemberObject> listMembers = ldapDriveQueryService.listDriveMembers(ldapConnection, baseDn,
					groupPattern, ldapGroup);
			for (LdapDriveMemberObject member : listMembers) {
				logger.info(member.toString());
				if (user2_email.equals(member.getEmail())) {
					Assertions.assertEquals("Jane", member.getFirstName());
					Assertions.assertEquals("Smith", member.getLastName());
					Assertions.assertEquals("user2@linshare.org", member.getEmail());
					Assertions.assertEquals(Role.WRITER, member.getNestedRole());
					Assertions.assertEquals(Role.DRIVE_WRITER, member.getRole());
				}
			}
			Assertions.assertEquals(2, listMembers.size());
		}
		Assertions.assertEquals(1, listGroups.size());
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
