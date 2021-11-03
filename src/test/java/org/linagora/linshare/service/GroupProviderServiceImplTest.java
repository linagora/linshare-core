/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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

import java.util.HashMap;
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
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.LdapGroupProvider;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.GroupLdapPatternService;
import org.linagora.linshare.core.service.GroupProviderService;
import org.linagora.linshare.core.service.RemoteServerService;
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
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-test.xml" })
public class GroupProviderServiceImplTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private GroupProviderService groupProviderService;

	@Autowired
	private GroupLdapPatternService groupLdapPatternService;

	@Autowired
	private RemoteServerService ldapConnectionService;

	@Autowired
	private AccountService accountService;

	private GroupLdapPattern groupPattern;

	private LdapConnection ldapconnexion;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		ldapconnexion  = new LdapConnection("label", "ldap://10.75.113.53:389", "simple");
		ldapconnexion = ldapConnectionService.create(ldapconnexion);
		LdapAttribute attribute = new LdapAttribute("field", "attribute", false);
		Map<String, LdapAttribute> attributeList = new HashMap<>();
		attributeList.put("first", attribute);
		groupPattern = new GroupLdapPattern("lable", "description", "searchAllGroupsQuery",
				"searchGroupQuery", "groupPrefix", false);
		Account actor = accountService.findByLsUuid(LinShareTestConstants.ROOT_ACCOUNT);
		groupPattern = groupLdapPatternService.create(actor, groupPattern);
		Assertions.assertNotNull(groupPattern);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testCreateGroupProvider () {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		LdapGroupProvider groupProvider = new LdapGroupProvider(groupPattern, "dc=nodomain,dc=com", ldapconnexion, false);
		groupProvider = groupProviderService.create(groupProvider);
		Assertions.assertNotNull(groupProvider);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateDeleteGroupProvider() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			LdapGroupProvider groupProvider = new LdapGroupProvider(groupPattern, "dc=nodomain,dc=com", ldapconnexion,
					false);
			groupProvider = groupProviderService.create(groupProvider);
			Assertions.assertNotNull(groupProvider);
			groupProviderService.delete(groupProvider);
			groupProviderService.find(groupProvider.getUuid());
		});
		Assertions.assertEquals(BusinessErrorCode.GROUP_PROVIDER_NOT_FOUND, exception.getErrorCode());
		logger.debug(LinShareTestConstants.END_TEST);
	}

}
