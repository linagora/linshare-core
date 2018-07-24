/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.LDAPGroupQueryService;
import org.linagora.linshare.ldap.LdapGroupMemberObject;
import org.linagora.linshare.ldap.LdapGroupObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = { "classpath:springContext-test.xml",
		"classpath:springContext-ldap.xml", 
		"classpath:springContext-start-embedded-ldap.xml"
})
public class LDAPGroupQueryServiceImplTest extends AbstractJUnit4SpringContextTests {

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
		attributes.put(GroupLdapPattern.GROUP_DN, new LdapAttribute(GroupLdapPattern.GROUP_DN, "cn"));
		attributes.put(GroupLdapPattern.GROUP_MEMBER, new LdapAttribute(GroupLdapPattern.GROUP_MEMBER, "member"));
		attributes.put(GroupLdapPattern.MEMBER_MAIL, new LdapAttribute(GroupLdapPattern.MEMBER_MAIL, "mail"));
		attributes.put(GroupLdapPattern.MEMBER_FIRST_NAME, new LdapAttribute(GroupLdapPattern.MEMBER_FIRST_NAME, "givenName"));
		attributes.put(GroupLdapPattern.MEMBER_LAST_NAME, new LdapAttribute(GroupLdapPattern.MEMBER_LAST_NAME, "sn"));

		groupPattern = new GroupLdapPattern();
		groupPattern.setAttributes(attributes);
		groupPattern.setSearchAllGroupsQuery("ldap.search(baseDn, \"(&(objectClass=posixGroup)(cn=workgroup-*))\");");
		groupPattern.setFindMemberQuery("");
		groupPattern.setSearchGroupQuery("");
		groupPattern.setSearchPageSize(100);
		groupPattern.setGroupPrefix("workgroup-");

		ldapConnection = new LdapConnection("testldap", "ldap://localhost:33389", "anonymous");
		baseDn = "ou=Groups,dc=linshare,dc=org";
	}

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testGroups() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date date_before = new Date();
		List<LdapGroupObject> listGroups = ldapGroupQueryService.listGroups(ldapConnection, baseDn, groupPattern);
		for (LdapGroupObject ldapGroup : listGroups) {
			logger.info(ldapGroup.toString());
			ldapGroup.setPrefix(groupPattern.getGroupPrefix());
			ldapGroup.removePrefix();
			logger.info(ldapGroup.toString());
			if (ldapGroup.getMembers() != null) logger.info(ldapGroup.getMembers().toString());
			Assert.assertEquals("wg-1", ldapGroup.getName());
			Assert.assertEquals("workgroup-wg-1", ldapGroup.getNameWithPrefix());
			Assert.assertEquals("workgroup-wg-1", ldapGroup.getExternalId());
		}
		Date date_after = new Date();
		Assert.assertEquals(1, listGroups.size());
		logger.info("fin test : " + String.valueOf(date_after.getTime() - date_before.getTime()));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testMembers() throws BusinessException, NamingException, IOException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Date date_before = new Date();
		List<LdapGroupObject> listGroups = ldapGroupQueryService.listGroups(ldapConnection, baseDn, groupPattern);
		for (LdapGroupObject ldapGroup : listGroups) {
			logger.info(ldapGroup.toString());
			ldapGroup.setPrefix(groupPattern.getGroupPrefix());
			ldapGroup.removePrefix();
			logger.info(ldapGroup.toString());
			if (ldapGroup.getMembers() != null) logger.info(ldapGroup.getMembers().toString());
			Assert.assertEquals("wg-1", ldapGroup.getName());
			Assert.assertEquals("workgroup-wg-1", ldapGroup.getNameWithPrefix());
			Assert.assertEquals("workgroup-wg-1", ldapGroup.getExternalId());

			List<LdapGroupMemberObject> listMembers = ldapGroupQueryService.listMembers(ldapConnection, baseDn, groupPattern, ldapGroup);
			for (LdapGroupMemberObject ldapGroupMemberObject : listMembers) {
				logger.info(ldapGroupMemberObject.toString());
			}
			Assert.assertEquals(2, listMembers.size());
		}
		Date date_after = new Date();
		Assert.assertEquals(1, listGroups.size());
		logger.info("fin test : " + String.valueOf(date_after.getTime() - date_before.getTime()));
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
