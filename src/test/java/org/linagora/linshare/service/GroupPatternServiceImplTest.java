/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.GroupLdapPatternService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-mongo-java-server.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-test.xml" })
public class GroupPatternServiceImplTest {

	private static final Logger logger = LoggerFactory.getLogger(GroupPatternServiceImplTest.class);

	@Autowired
	private GroupLdapPatternService groupLdapPatternService;

	@Autowired
	private AccountService accountService;

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
	public void testCreateGroupPattern() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		GroupLdapPattern groupPattern = new GroupLdapPattern("lable", "description", "searchAllGroupsQuery", "searchGroupQuery", "groupPrefix", false);
		try {
			Account actor = accountService.findByLsUuid("root@localhost.localdomain");
			groupLdapPatternService.create(actor, groupPattern);
		} catch (BusinessException e) {
			e.printStackTrace();
			Assertions.fail("Can't create domain pattern.");
		}
		logger.debug("Current pattern object: " + groupPattern.toString());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateGroupPatternSpecialChar() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		GroupLdapPattern groupPattern = new GroupLdapPattern("EP_TEST_v233<script>alert(document.cookie)</script>",
				"EP_TEST_v233<script>alert(document.cookie)</script>", "searchAllGroupsQuery", "searchGroupQuery",
				"groupPrefix", false);
		Account actor = accountService.findByLsUuid("root@localhost.localdomain");
		groupLdapPatternService.create(actor, groupPattern);
		Assertions.assertNotNull(groupPattern);
		Assertions.assertEquals(groupPattern.getLabel(), "EP_TEST_v233");
		Assertions.assertEquals(groupPattern.getDescription(), "EP_TEST_v233");
		logger.debug("Current pattern object: " + groupPattern.toString());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testUpdateGroupPatternSpecialChar() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		GroupLdapPattern groupPattern = new GroupLdapPattern("Label",
				"Description", "searchAllGroupsQuery", "searchGroupQuery",
				"groupPrefix", false);
		Account actor = accountService.findByLsUuid("root@localhost.localdomain");
		groupLdapPatternService.create(actor, groupPattern);
		Assertions.assertNotNull(groupPattern);
		groupPattern.setLabel("EP_TEST_v233<script>alert(document.cookie)</script>");
		groupPattern.setDescription("EP_TEST_v233<script>alert(document.cookie)</script>");
		groupLdapPatternService.update(actor, groupPattern);
		Assertions.assertEquals(groupPattern.getLabel(), "EP_TEST_v233");
		Assertions.assertEquals(groupPattern.getDescription(), "EP_TEST_v233");
		logger.debug("Current pattern object: " + groupPattern.toString());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateFindDomainPattern() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		GroupLdapPattern groupPattern = new GroupLdapPattern("label", "description", "searchAllGroupsQuery", "searchGroupQuery", "groupPrefix", false);
		try {
			Account actor = accountService.findByLsUuid("root@localhost.localdomain");
			groupPattern = groupLdapPatternService.create(actor, groupPattern);
			GroupLdapPattern found = groupLdapPatternService.find(groupPattern.getUuid());
			Assertions.assertEquals("searchGroupQuery", found.getSearchGroupQuery());
		} catch (BusinessException e) {
			e.printStackTrace();
			Assertions.fail("Can't create domain pattern.");
		}
		logger.debug("Current pattern object: " + groupPattern.toString());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateDeleteDomainPattern() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		GroupLdapPattern groupPattern = new GroupLdapPattern("label", "description", "searchAllGroupsQuery", "searchGroupQuery", "groupPrefix", false);
		Account actor = accountService.findByLsUuid("root@localhost.localdomain");
		try {
			groupPattern = groupLdapPatternService.create(actor, groupPattern);
		} catch (BusinessException e) {
			e.printStackTrace();
			Assertions.fail("Can't create pattern.");
		}
		try {
			groupLdapPatternService.delete(actor, groupPattern);
		} catch (BusinessException e) {
			logger.error(e.toString());
			e.printStackTrace();
			Assertions.fail("Can't delete pattern.");
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindAllUpdateDomainPattern() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		GroupLdapPattern groupPattern = new GroupLdapPattern("label", "description", "searchAllGroupsQuery", "searchGroupQuery", "groupPrefix", false);
		Account actor = accountService.findByLsUuid("root@localhost.localdomain");
		try {
			List<GroupLdapPattern> list = groupLdapPatternService.findAll();
			groupPattern = groupLdapPatternService.create(actor, groupPattern);
			Assertions.assertNotNull(groupPattern);
			Assertions.assertTrue(groupLdapPatternService.findAll().size() == list.size() + 1);
			groupPattern = groupLdapPatternService.find(groupPattern.getUuid());
		} catch (BusinessException e) {
			e.printStackTrace();
			Assertions.fail("Can't retrieve pattern.");
		}
		Map<String, LdapAttribute> attributes = groupPattern.getAttributes();
		attributes.get(GroupLdapPattern.GROUP_NAME).setAttribute("Name");
		groupPattern.setDescription("new Description");
		try {
			groupLdapPatternService.update(actor, groupPattern);
			Assertions.assertEquals("new Description", groupPattern.getDescription());
		} catch (BusinessException e) {
			e.printStackTrace();
			Assertions.fail("Can't update pattern.");
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
