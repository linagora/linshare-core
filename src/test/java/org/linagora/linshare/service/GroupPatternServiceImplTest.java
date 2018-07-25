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

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.GroupLdapPatternService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-test.xml" })
public class GroupPatternServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private GroupLdapPatternService groupLdapPatternService;

	@Autowired
	private AccountService accountService;

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
	public void testCreateGroupPattern() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		GroupLdapPattern groupPattern = new GroupLdapPattern("lable", "description", "searchAllGroupsQuery", "memberQuery", "searchGroupQuery", "findMemberQuery", "groupPrefix");
		try {
			Account actor = accountService.findByLsUuid("root@localhost.localdomain");
			groupLdapPatternService.create(actor, groupPattern);
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.fail("Can't create domain pattern.");
		}
		logger.debug("Current pattern object: " + groupPattern.toString());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateFindDomainPattern() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		GroupLdapPattern groupPattern = new GroupLdapPattern("label", "description", "searchAllGroupsQuery", "memberQuery", "searchGroupQuery", "findMemberQuery", "groupPrefix");
		try {
			Account actor = accountService.findByLsUuid("root@localhost.localdomain");
			groupPattern = groupLdapPatternService.create(actor, groupPattern);
			GroupLdapPattern found = groupLdapPatternService.find(groupPattern.getUuid());
			Assert.assertEquals("searchGroupQuery", found.getSearchGroupQuery());
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.fail("Can't create domain pattern.");
		}
		logger.debug("Current pattern object: " + groupPattern.toString());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateDeleteDomainPattern() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		GroupLdapPattern groupPattern = new GroupLdapPattern("label", "description", "searchAllGroupsQuery", "memberQuery", "searchGroupQuery", "findMemberQuery", "groupPrefix");
		Account actor = accountService.findByLsUuid("root@localhost.localdomain");
		try {
			groupPattern = groupLdapPatternService.create(actor, groupPattern);
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.fail("Can't create pattern.");
		}
		try {
			groupLdapPatternService.delete(actor, groupPattern, groupPattern.getUuid());
		} catch (BusinessException e) {
			logger.error(e.toString());
			e.printStackTrace();
			Assert.fail("Can't delete pattern.");
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindAllUpdateDomainPattern() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		GroupLdapPattern groupPattern = new GroupLdapPattern("label", "description", "searchAllGroupsQuery", "memberQuery", "searchGroupQuery", "findMemberQuery", "groupPrefix");
		Account actor = accountService.findByLsUuid("root@localhost.localdomain");
		try {
			groupPattern = groupLdapPatternService.create(actor, groupPattern);
			Assert.assertNotNull(groupPattern);
			List<GroupLdapPattern> list = groupLdapPatternService.findAll();
			groupPattern = list.get(0);
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.fail("Can't retrieve pattern.");
		}
		Map<String, LdapAttribute> attributes = groupPattern.getAttributes();
		attributes.get(GroupLdapPattern.GROUP_NAME).setAttribute("Name");
		groupPattern.setDescription("new Description");
		try {
			groupLdapPatternService.update(actor, groupPattern);
			Assert.assertEquals("new Description", groupPattern.getDescription());
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.fail("Can't update pattern.");
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
