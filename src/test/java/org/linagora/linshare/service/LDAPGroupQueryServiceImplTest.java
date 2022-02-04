/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
package org.linagora.linshare.service;

import java.io.IOException;
import java.util.Date;
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
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.LDAPGroupQueryService;
import org.linagora.linshare.ldap.LdapGroupMemberObject;
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
public class LDAPGroupQueryServiceImplTest {

	protected Logger logger = LoggerFactory.getLogger(LDAPGroupQueryServiceImplTest.class);

	@Autowired
	private LDAPGroupQueryService ldapGroupQueryService;

	private LdapConnection ldapConnection;

	private GroupLdapPattern groupPattern;

	private Map<String, LdapAttribute> attributes;

	private String baseDn;

	public LDAPGroupQueryServiceImplTest() {
		super();
		attributes = new HashMap<String, LdapAttribute>();
		attributes.put(GroupLdapPattern.GROUP_NAME, new LdapAttribute(GroupLdapPattern.GROUP_NAME, "cn"));
		attributes.put(GroupLdapPattern.GROUP_MEMBER, new LdapAttribute(GroupLdapPattern.GROUP_MEMBER, "member"));
		attributes.put(GroupLdapPattern.MEMBER_MAIL, new LdapAttribute(GroupLdapPattern.MEMBER_MAIL, "mail"));
		attributes.put(GroupLdapPattern.MEMBER_FIRST_NAME, new LdapAttribute(GroupLdapPattern.MEMBER_FIRST_NAME, "givenName"));
		attributes.put(GroupLdapPattern.MEMBER_LAST_NAME, new LdapAttribute(GroupLdapPattern.MEMBER_LAST_NAME, "sn"));

		groupPattern = new GroupLdapPattern();
		groupPattern.setAttributes(attributes);
		groupPattern.setSearchAllGroupsQuery("ldap.search(baseDn, \"(&(objectClass=groupOfNames)(cn=workgroup-*))\");");
		groupPattern.setSearchGroupQuery("ldap.search(baseDn, \"(&(objectClass=groupOfNames)(cn=workgroup-\" + pattern + \"))\");");
		groupPattern.setSearchPageSize(100);
		groupPattern.setGroupPrefix("workgroup-");

		ldapConnection = new LdapConnection("testldap", "ldap://localhost:33389", "anonymous");
		baseDn = "ou=Groups,dc=linshare,dc=org";
	}

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
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
		Set<LdapGroupObject> listGroups = ldapGroupQueryService.listGroups(ldapConnection, baseDn, groupPattern);
		for (LdapGroupObject ldapGroup : listGroups) {
			logger.info("GROUPS:" + ldapGroup.toString());
			if (!ldapGroup.getMembers().isEmpty()) {
				logger.info(ldapGroup.getMembers().toString());
			}
			Assertions.assertEquals("wg-1", ldapGroup.getName());
			Assertions.assertEquals("workgroup-wg-1", ldapGroup.getNameWithPrefix());
			Assertions.assertEquals("cn=workgroup-wg-1,ou=Groups,dc=linshare,dc=org", ldapGroup.getExternalId());
			Assertions.assertEquals(Role.READER, ldapGroup.getRoles().get(NodeType.WORK_GROUP));
		}
		Assertions.assertEquals(1, listGroups.size());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testSearchGroups() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		baseDn = "dc=linshare,dc=org";
		Set<LdapGroupObject> listGroups = ldapGroupQueryService.searchGroups(ldapConnection, baseDn, groupPattern, "3");
		for (LdapGroupObject ldapGroup : listGroups) {
			logger.info("GROUPS:" + ldapGroup.toString());
			if (!ldapGroup.getMembers().isEmpty()) {
				logger.info(ldapGroup.getMembers().toString());
			}
			Assertions.assertEquals("wg-3", ldapGroup.getName());
			Assertions.assertEquals("workgroup-wg-3", ldapGroup.getNameWithPrefix());
			Assertions.assertEquals("cn=workgroup-wg-3,ou=Groups3,dc=linshare,dc=org", ldapGroup.getExternalId());
			Assertions.assertEquals(Role.READER, ldapGroup.getRoles().get(NodeType.WORK_GROUP));
		}
		Assertions.assertEquals(1, listGroups.size());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testMembers() throws BusinessException, NamingException, IOException {
		String baseDn = "ou=Groups2,dc=linshare,dc=org";
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Set<LdapGroupObject> listGroups = ldapGroupQueryService.listGroups(ldapConnection, baseDn, groupPattern);
		for (LdapGroupObject ldapGroup : listGroups) {
			logger.info("GROUPS:" + ldapGroup.toString());
			Assertions.assertEquals("cn=workgroup-wg-2,ou=Groups2,dc=linshare,dc=org", ldapGroup.getExternalId());
			Set<LdapGroupMemberObject> listMembers = ldapGroupQueryService.listMembers(ldapConnection, baseDn, groupPattern, ldapGroup);
			for (LdapGroupMemberObject member : listMembers) {
				logger.info(member.toString());
				Assertions.assertEquals("John", member.getFirstName());
				Assertions.assertEquals("Doe", member.getLastName());
				Assertions.assertEquals("user1@linshare.org", member.getEmail());
				Assertions.assertEquals(Role.READER, member.getRole());
			}
			Assertions.assertEquals(1, listMembers.size());
		}
		Assertions.assertEquals(1, listGroups.size());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testNestedMembers() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		String baseDn = "ou=Groups3,dc=linshare,dc=org";
		Date date_before = new Date();
		Set<LdapGroupObject> listGroups = ldapGroupQueryService.listGroups(ldapConnection, baseDn, groupPattern);
		Assertions.assertEquals(1, listGroups.size());
		LdapGroupObject ldapGroup = listGroups.iterator().next();
		Assertions.assertEquals("cn=workgroup-wg-3,ou=Groups3,dc=linshare,dc=org", ldapGroup.getExternalId());
		Set<LdapGroupMemberObject> listMembers = ldapGroupQueryService.listMembers(ldapConnection, baseDn, groupPattern, ldapGroup);
		Assertions.assertEquals(3, listMembers.size());
		for (LdapGroupMemberObject member : listMembers) {
			logger.info(member.toString());
		}
		Date date_after = new Date();
		logger.info("End test : " + String.valueOf(date_after.getTime() - date_before.getTime()));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testNoReaders() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		String baseDn = "ou=Groups4,dc=linshare,dc=org";
		Date date_before = new Date();
		Set<LdapGroupObject> listGroups = ldapGroupQueryService.listGroups(ldapConnection, baseDn, groupPattern);
		Assertions.assertEquals(1, listGroups.size());
		LdapGroupObject ldapGroup = listGroups.iterator().next();
		Assertions.assertEquals("cn=workgroup-wg-4,ou=Groups4,dc=linshare,dc=org", ldapGroup.getExternalId());
		Set<LdapGroupMemberObject> listMembers = ldapGroupQueryService.listMembers(ldapConnection, baseDn, groupPattern, ldapGroup);
		Assertions.assertEquals(2, listMembers.size());
		for (LdapGroupMemberObject member : listMembers) {
			logger.info(member.toString());
			Assertions.assertNotEquals(Role.READER, member.getRole());
		}
		Date date_after = new Date();
		logger.info("End test : " + String.valueOf(date_after.getTime() - date_before.getTime()));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testUserReaderAndWriter() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		String baseDn = "ou=Groups5,dc=linshare,dc=org";
		Date date_before = new Date();
		Set<LdapGroupObject> listGroups = ldapGroupQueryService.listGroups(ldapConnection, baseDn, groupPattern);
		Assertions.assertEquals(1, listGroups.size());
		LdapGroupObject ldapGroup = listGroups.iterator().next();
		Assertions.assertEquals("cn=workgroup-wg-5,ou=Groups5,dc=linshare,dc=org", ldapGroup.getExternalId());
		Set<LdapGroupMemberObject> listMembers = ldapGroupQueryService.listMembers(ldapConnection, baseDn, groupPattern, ldapGroup);
		Assertions.assertEquals(3, listMembers.size());
		for (LdapGroupMemberObject member : listMembers) {
			logger.info(member.toString());
			Assertions.assertNotEquals(Role.READER, member.getRole());
			if ("user2@linshare.org".equals(member.getEmail())) {
				Assertions.assertEquals(Role.CONTRIBUTOR, member.getRole());
			}
			else{
				Assertions.assertEquals(Role.WRITER, member.getRole());
			}
		}
		Date date_after = new Date();
		logger.info("End test : " + String.valueOf(date_after.getTime() - date_before.getTime()));
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
