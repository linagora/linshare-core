/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.Role;
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
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DomainPolicyRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.LdapConnectionService;
import org.linagora.linshare.core.service.UserProviderService;
import org.linagora.linshare.core.service.WelcomeMessagesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

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
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml"
		})
public class AbstractDomainServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests{

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

	private LdapConnection ldapconnexion;

	private UserLdapPattern domainPattern;

	private WelcomeMessages current;

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		this.executeSqlScript("import-tests-default-domain-quotas.sql", false);
		this.executeSqlScript("import-tests-quota-other.sql", false);
		ldapconnexion  = new LdapConnection(identifier, providerUrl, securityAuth);
		Account actor = accountService.findByLsUuid("root@localhost.localdomain");
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
			domainPattern = userProviderService.createDomainPattern(actor, domainPattern);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		logger.debug("Current pattern object: " + domainPattern.toString());
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testCreateTopDomain() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		TopDomain topDomain = new TopDomain("label");
		topDomain.setDefaultRole(Role.SIMPLE);
		DomainPolicy policy = domainPolicyRepository.findById(LinShareConstants.defaultDomainPolicyIdentifier);
		topDomain.setPolicy(policy);

		MailConfig mailConfig = new MailConfig();
		mailConfig.setUuid(LinShareConstants.defaultMailConfigIdentifier);
		topDomain.setCurrentMailConfiguration(mailConfig);

		MimePolicy mimePolicy = new MimePolicy();
		mimePolicy.setUuid(LinShareConstants.defaultMimePolicyIdentifier);
		topDomain.setMimePolicy(mimePolicy);
		
		try {
			Account actor = accountService.findByLsUuid("root@localhost.localdomain");
			current = welcomeService.find((User) actor, "4bc57114-c8c9-11e4-a859-37b5db95d856");
			topDomain.setCurrentWelcomeMessages(current);
			abstractDomainService.createTopDomain(actor, topDomain);
			abstractDomainService.deleteDomain(actor, topDomain.getUuid());
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.fail("Can't create top domain.");
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	
	@Test
	public void testCreateTopDomain2() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		TopDomain topDomain = new TopDomain("label");
		topDomain.setDefaultRole(Role.SIMPLE);
		DomainPolicy policy = domainPolicyRepository
				.findById(LinShareConstants.defaultDomainPolicyIdentifier);
		topDomain.setPolicy(policy);
		
		MailConfig mailConfig = new MailConfig();
		mailConfig.setUuid(LinShareConstants.defaultMailConfigIdentifier);
		topDomain.setCurrentMailConfiguration(mailConfig);

		MimePolicy mimePolicy = new MimePolicy();
		mimePolicy.setUuid(LinShareConstants.defaultMimePolicyIdentifier);
		topDomain.setMimePolicy(mimePolicy);
		
		
		
		Account actor = accountService.findByLsUuid("root@localhost.localdomain");
		current = welcomeService.find((User) actor, "4bc57114-c8c9-11e4-a859-37b5db95d856");
		topDomain.setCurrentWelcomeMessages(current);
		try {
			abstractDomainService.createTopDomain(actor, topDomain);
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.fail("Can't create domain.");
		}
		
		try {
			abstractDomainService.deleteDomain(actor, topDomain.getUuid());
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.fail("Can't delete top domain.");
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}
		
}
