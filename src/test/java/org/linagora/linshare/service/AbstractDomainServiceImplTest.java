/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
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
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.DomainPurgeStepEnum;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MimePolicy;
import org.linagora.linshare.core.domain.entities.TopDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserLdapPattern;
import org.linagora.linshare.core.domain.entities.WelcomeMessages;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.LdapConnectionService;
import org.linagora.linshare.core.service.UserProviderService;
import org.linagora.linshare.core.service.WelcomeMessagesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Sql({ "/import-tests.sql" })
@Transactional
@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo-java-server.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml"
		})
public class AbstractDomainServiceImplTest {

	private static Logger logger = LoggerFactory.getLogger(AbstractDomainServiceImplTest.class);

	public static String topDomaineName = "TEST_ADST_Domain_0_1";

	@SuppressWarnings("unused")
	private static String baseDn = "dc=nodomain,dc=com";
	private static String identifier= "ID_ADST_LDAP_DE_TEST";
	private static String identifierP = "ID_ADST_PARAM_DE_TEST";
	private static String providerUrl = "ldap://10.75.113.53:389";
	private static String securityAuth = "simple";

	@Autowired
	private AbstractDomainService abstractDomainService;

	@Autowired
	private UserProviderService userProviderService;

	@Autowired
	private LdapConnectionService ldapConnectionService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private WelcomeMessagesService welcomeService;

	@Autowired
	private DomainPolicyRepository domainPolicyRepository;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	private LdapConnection ldapconnexion;

	private UserLdapPattern domainPattern;

	private WelcomeMessages current;

	private Account root;

	private AbstractDomain domain;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		domain = userRepository.findByMail("user1@linshare.org").getDomain();
		ldapconnexion  = new LdapConnection(identifier, providerUrl, securityAuth);
		root = userRepository.findByMailAndDomain(LoadingServiceTestDatas.sqlRootDomain, "root@localhost.localdomain");
		current = welcomeService.find((User) root, "4bc57114-c8c9-11e4-a859-37b5db95d856");
		LdapAttribute attribute = new LdapAttribute("field", "attribute", false);
		Map<String, LdapAttribute> attributeList = new HashMap<>();
			attributeList.put("first", attribute);
		try {
			ldapconnexion = ldapConnectionService.create(ldapconnexion);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		logger.debug("Current ldapconnexion object: " + ldapconnexion.toString());
		
		domainPattern = new UserLdapPattern(identifierP, "blabla", "getUserCommand", "getAllDomainUsersCommand", "authCommand", "searchUserCommand", attributeList);
		domainPattern.setAutoCompleteCommandOnAllAttributes("auto complete command 1");
		domainPattern.setAutoCompleteCommandOnFirstAndLastName("auto complete command 2");
		try {
			domainPattern = userProviderService.createDomainPattern(root, domainPattern);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		logger.debug("Current pattern object: " + domainPattern.toString());
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testCreateTopDomain() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		TopDomain topDomain = new TopDomain("label");
		topDomain.setDescription("description");
		topDomain.setDefaultRole(Role.SIMPLE);
		topDomain.setPurgeStep(DomainPurgeStepEnum.IN_USE);
		DomainPolicy policy = domainPolicyRepository.findById(LinShareConstants.defaultDomainPolicyIdentifier);
		topDomain.setPolicy(policy);

		MailConfig mailConfig = new MailConfig();
		mailConfig.setUuid(LinShareConstants.defaultMailConfigIdentifier);
		topDomain.setCurrentMailConfiguration(mailConfig);

		MimePolicy mimePolicy = new MimePolicy();
		mimePolicy.setUuid(LinShareConstants.defaultMimePolicyIdentifier);
		topDomain.setMimePolicy(mimePolicy);
		
		try {
			topDomain.setCurrentWelcomeMessages(current);
			abstractDomainService.createTopDomain(root, topDomain);
			abstractDomainService.markToPurge(root, topDomain.getUuid());
		} catch (BusinessException e) {
			e.printStackTrace();
			Assertions.fail("Can't create top domain.");
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateTopDomainSpecialCharWithoutDescription() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		TopDomain topDomain = new TopDomain("EP_TEST_v233<script>alert(document.cookie)</script>");
		topDomain.setDefaultRole(Role.SIMPLE);
		topDomain.setPurgeStep(DomainPurgeStepEnum.IN_USE);
		DomainPolicy policy = domainPolicyRepository.findById(LinShareConstants.defaultDomainPolicyIdentifier);
		topDomain.setPolicy(policy);

		MailConfig mailConfig = new MailConfig();
		mailConfig.setUuid(LinShareConstants.defaultMailConfigIdentifier);
		topDomain.setCurrentMailConfiguration(mailConfig);

		MimePolicy mimePolicy = new MimePolicy();
		mimePolicy.setUuid(LinShareConstants.defaultMimePolicyIdentifier);
		topDomain.setMimePolicy(mimePolicy);
		topDomain.setCurrentWelcomeMessages(current);
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			abstractDomainService.createTopDomain(root, topDomain);
		});
		Assertions.assertEquals(BusinessErrorCode.INVALID_FILENAME, exception.getErrorCode());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateTopDomainSpecialChar() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		TopDomain topDomain = new TopDomain("EP_TEST_v233<script>alert(document.cookie)</script>");
		topDomain.setDescription("EP_TEST_v233<script>alert(document.cookie)</script>");
		topDomain.setDefaultRole(Role.SIMPLE);
		topDomain.setPurgeStep(DomainPurgeStepEnum.IN_USE);
		DomainPolicy policy = domainPolicyRepository.findById(LinShareConstants.defaultDomainPolicyIdentifier);
		topDomain.setPolicy(policy);

		MailConfig mailConfig = new MailConfig();
		mailConfig.setUuid(LinShareConstants.defaultMailConfigIdentifier);
		topDomain.setCurrentMailConfiguration(mailConfig);

		MimePolicy mimePolicy = new MimePolicy();
		mimePolicy.setUuid(LinShareConstants.defaultMimePolicyIdentifier);
		topDomain.setMimePolicy(mimePolicy);
		topDomain.setCurrentWelcomeMessages(current);
		abstractDomainService.createTopDomain(root, topDomain);
		Assertions.assertNotNull(topDomain);
		Assertions.assertEquals(topDomain.getLabel(), "EP_TEST_v233");
		Assertions.assertEquals(topDomain.getDescription(), "EP_TEST_v233");
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testUpdateDomainSpecialChar() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		domain.setLabel("EP_TEST_v233<script>alert(document.cookie)</script>");
		domain.setDescription("EP_TEST_v233<script>alert(document.cookie)</script>");
		abstractDomainService.updateDomain(root, domain);
		Assertions.assertEquals(domain.getLabel(), "EP_TEST_v233");
		Assertions.assertEquals(domain.getDescription(), "EP_TEST_v233");
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateTopDomain2() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		TopDomain topDomain = new TopDomain("label");
		topDomain.setDescription("description");
		topDomain.setDefaultRole(Role.SIMPLE);
		topDomain.setPurgeStep(DomainPurgeStepEnum.IN_USE);
		DomainPolicy policy = domainPolicyRepository
				.findById(LinShareConstants.defaultDomainPolicyIdentifier);
		topDomain.setPolicy(policy);
		
		MailConfig mailConfig = new MailConfig();
		mailConfig.setUuid(LinShareConstants.defaultMailConfigIdentifier);
		topDomain.setCurrentMailConfiguration(mailConfig);

		MimePolicy mimePolicy = new MimePolicy();
		mimePolicy.setUuid(LinShareConstants.defaultMimePolicyIdentifier);
		topDomain.setMimePolicy(mimePolicy);
		
		topDomain.setCurrentWelcomeMessages(current);
		try {
			abstractDomainService.createTopDomain(root, topDomain);
		} catch (BusinessException e) {
			e.printStackTrace();
			Assertions.fail("Can't create domain.");
		}
		
		try {
			abstractDomainService.markToPurge(root, topDomain.getUuid());
		} catch (BusinessException e) {
			e.printStackTrace();
			Assertions.fail("Can't delete top domain.");
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testCreateFindAndPurgeDomain() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		TopDomain topDomain = new TopDomain("topDomainToPurge");
		topDomain.setDescription("description");
		topDomain.setDefaultRole(Role.SIMPLE);
		topDomain.setPurgeStep(DomainPurgeStepEnum.IN_USE);
		DomainPolicy policy = domainPolicyRepository.findById(LinShareConstants.defaultDomainPolicyIdentifier);
		topDomain.setPolicy(policy);

		MailConfig mailConfig = new MailConfig();
		mailConfig.setUuid(LinShareConstants.defaultMailConfigIdentifier);
		topDomain.setCurrentMailConfiguration(mailConfig);

		MimePolicy mimePolicy = new MimePolicy();
		mimePolicy.setUuid(LinShareConstants.defaultMimePolicyIdentifier);
		topDomain.setMimePolicy(mimePolicy);

		topDomain.setCurrentWelcomeMessages(current);
		int initSize = abstractDomainService.findAll(root).size();
		try {
			abstractDomainService.createTopDomain(root, topDomain);
		} catch (BusinessException e) {
			e.printStackTrace();
			Assertions.fail("Can't create domain.");
		}
		List<AbstractDomain> abstractDomainsService = abstractDomainService.findAll(root);
		Assertions.assertEquals(initSize + 1, abstractDomainsService.size());
		try {
			abstractDomainService.markToPurge(root, topDomain.getUuid());
		} catch (BusinessException e) {
			e.printStackTrace();
			Assertions.fail("Can't delete top domain.");
		}
		Assertions.assertEquals(initSize, abstractDomainService.findAll(root).size());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testPurgeDomainWithFunctionalities() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		AbstractDomain subDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlSubDomain);
		Assertions.assertNotNull(subDomain);
		Assertions.assertNotNull(subDomain.getFunctionalities());
		Account actor = accountService.findByLsUuid("root@localhost.localdomain");
		int initSize = abstractDomainService.findAll(actor).size();
		try {
			abstractDomainService.markToPurge(actor, subDomain.getUuid());
		} catch (Exception e) {
			e.printStackTrace();
			Assertions.fail("Can't delete top domain.");
		}
		Assertions.assertEquals(initSize - 1, abstractDomainService.findAll(actor).size());
		logger.debug(LinShareTestConstants.END_TEST);
	}

}
