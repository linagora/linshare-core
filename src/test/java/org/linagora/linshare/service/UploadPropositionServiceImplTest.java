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
 * and free version of LinShare™, powered by Linagora © 2009-2018. Contribute to
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

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.domain.constants.UploadPropositionActionType;
import org.linagora.linshare.core.domain.constants.UploadPropositionMatchType;
import org.linagora.linshare.core.domain.constants.UploadPropositionRuleFieldType;
import org.linagora.linshare.core.domain.constants.UploadPropositionRuleOperatorType;
import org.linagora.linshare.core.domain.constants.UploadPropositionStatus;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.TechnicalAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.TechnicalAccountService;
import org.linagora.linshare.core.service.UploadPropositionFilterService;
import org.linagora.linshare.core.service.UploadPropositionService;
import org.linagora.linshare.core.service.UploadRequestGroupService;
import org.linagora.linshare.mongo.entities.UploadProposition;
import org.linagora.linshare.mongo.entities.UploadPropositionContact;
import org.linagora.linshare.mongo.entities.UploadPropositionFilter;
import org.linagora.linshare.mongo.entities.UploadPropositionRule;
import org.linagora.linshare.utils.LinShareWiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.google.common.collect.Lists;

@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml", })
public class UploadPropositionServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests {
	private static Logger logger = LoggerFactory.getLogger(UploadPropositionServiceImplTest.class);

	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	@Autowired
	private UploadPropositionFilterService uploadPropositionfilterService;

	@Autowired
	private UploadPropositionService uploadPropositionService;

	@Autowired
	private UploadRequestGroupService uploadRequestGroupService;

	@Autowired
	private TechnicalAccountService technicalAccountService;

	private LoadingServiceTestDatas datas;

	private AbstractDomain subDomain;

	private Account root;

	private User john;

	private User jane;

	private TechnicalAccount technicalAccount;

	private UploadPropositionFilter acceptationFilter;

	private UploadPropositionFilter rejectionFilter;

	private UploadPropositionFilter noAcceptationFilter;

	private UploadPropositionFilter noRejectionFilter;

	private LinShareWiser wiser;

	private String rootDomainIdentifier = "LinShareDomainRoot";

	private List<UploadPropositionFilter> filters = Lists.newArrayList();

	public UploadPropositionServiceImplTest() {
		super();
		wiser = new LinShareWiser(2525);
	}

	@Before
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		wiser.start();
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		john = datas.getUser1();
		jane = datas.getUser2();
		root = datas.getRoot();
		subDomain = abstractDomainRepository.findById(LoadingServiceTestDatas.sqlSubDomain);
		acceptationFilter = initFilter((User)root, UploadPropositionMatchType.ALL, UploadPropositionActionType.ACCEPT, 0, "user2");
		rejectionFilter = initFilter((User)root, UploadPropositionMatchType.ALL, UploadPropositionActionType.REJECT, 1, "user1");
		noAcceptationFilter = initFilter((User)root, UploadPropositionMatchType.ALL, UploadPropositionActionType.ACCEPT, 1, "NO_MATCH_ACCEPTED");
		noRejectionFilter = initFilter((User)root, UploadPropositionMatchType.ALL, UploadPropositionActionType.REJECT, 1, "NO_MATCH_REJECT");
		technicalAccount = new TechnicalAccount();
		technicalAccount.setMail("technicalAccount@linshare.org");
		technicalAccount.setLastName("technicalAccount");
		technicalAccount.setEnable(true);
		technicalAccount.setRole(Role.SUPERADMIN);
		technicalAccount.setPassword("secret");
		technicalAccount.setLocale(SupportedLanguage.ENGLISH);
		technicalAccount.setOwner(john);
		technicalAccount = technicalAccountService.create(john, technicalAccount);
		TechnicalAccount find = technicalAccountService.find(technicalAccount, technicalAccount.getLsUuid());
		filters = uploadPropositionfilterService.findAllEnabledFilters(find);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		wiser.stop();
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void createUploadPropositionAcceptedBySystem() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		uploadPropositionfilterService.create((User)root, acceptationFilter, subDomain);
		uploadPropositionfilterService.create((User)root, rejectionFilter, subDomain);
		UploadProposition uploadPropositionToCreate = initProposition();
		int countUploadRequests = uploadRequestGroupService
				.findAll(jane, jane, Lists.newArrayList(UploadRequestStatus.ENABLED)).size();
		int countUploadPropositions = uploadPropositionService.findAllByAccount(jane, jane).size();
		UploadProposition created = null;
		if (portalCheckAndApply(filters, uploadPropositionToCreate, jane)
				&& portalCheckRecipient(jane.getMail(), jane.getDomainId())) {
			uploadPropositionToCreate.setStatus(UploadPropositionStatus.SYSTEM_ACCEPTED);
			created = uploadPropositionService.create(technicalAccount, jane.getMail(), uploadPropositionToCreate);
		}
		Assert.assertEquals("An uploadProposition has been created", countUploadPropositions,
				uploadPropositionService.findAllByAccount(jane, jane).size());
		Assert.assertNotNull("No upload Proposition created", created);
		Assert.assertEquals(countUploadRequests + 1,
				uploadRequestGroupService.findAll(jane, jane, Lists.newArrayList(UploadRequestStatus.ENABLED)).size());
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void createUploadPropositionRejectedBySystem() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		uploadPropositionfilterService.create((User)root, acceptationFilter, subDomain);
		uploadPropositionfilterService.create((User)root, rejectionFilter, subDomain);
		UploadProposition uploadPropositionToCreate = initProposition();
		int countUploadRequests = uploadRequestGroupService
				.findAll(john, john, Lists.newArrayList(UploadRequestStatus.ENABLED)).size();
		int countUploadPropositions = uploadPropositionService.findAllByAccount(john, john).size();
		UploadProposition created = null;
		if (portalCheckAndApply(filters, uploadPropositionToCreate, john)
				&& portalCheckRecipient(john.getMail(), john.getDomainId())) {
			uploadPropositionToCreate.setStatus(UploadPropositionStatus.SYSTEM_ACCEPTED);
			created = uploadPropositionService.create(technicalAccount, john.getMail(), uploadPropositionToCreate);
		}
		Assert.assertEquals("An uploadProposition has been created", countUploadPropositions,
				uploadPropositionService.findAllByAccount(john, john).size());
		Assert.assertNull("An upload Proposition created", created);
		Assert.assertEquals(countUploadRequests,
				uploadRequestGroupService.findAll(john, john, Lists.newArrayList(UploadRequestStatus.ENABLED)).size());
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void createUploadPropositionNotDealtBySystem() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		uploadPropositionfilterService.create((User)root, noAcceptationFilter, subDomain);
		uploadPropositionfilterService.create((User)root, noRejectionFilter, subDomain);
		UploadProposition uploadPropositionToCreate = initProposition();
		int countUploadRequests = uploadRequestGroupService
				.findAll(john, john, Lists.newArrayList(UploadRequestStatus.ENABLED)).size();
		int countUploadPropositions = uploadPropositionService.findAllByAccount(john, john).size();
		UploadProposition created = null;
		if (portalCheckAndApply(filters, uploadPropositionToCreate, john)
				&& portalCheckRecipient(john.getMail(), john.getDomainId())) {
			uploadPropositionToCreate.setStatus(UploadPropositionStatus.SYSTEM_PENDING);
			created = uploadPropositionService.create(technicalAccount, john.getMail(), uploadPropositionToCreate);
		}
		Assert.assertEquals("No uploadProposition has been created", countUploadPropositions + 1,
				uploadPropositionService.findAllByAccount(john, john).size());
		Assert.assertNotNull("No upload Proposition created", created);
		Assert.assertEquals(countUploadRequests,
				uploadRequestGroupService.findAll(john, john, Lists.newArrayList(UploadRequestStatus.ENABLED)).size());
		logger.info(LinShareTestConstants.END_TEST);
	}

	private UploadProposition initProposition() {
		return new UploadProposition(UUID.randomUUID().toString(), john.getDomainId(), null, "Label",
				"Body Upload Proposition", new UploadPropositionContact("First", "Last", "yoda@linshare.com"),
				john.getLsUuid(), new Date(), new Date());
	}

	private UploadPropositionFilter initFilter(User root, UploadPropositionMatchType matchType,
			UploadPropositionActionType actionType, Integer order, String containedValue) {
		List<UploadPropositionRule> rules = Lists.newArrayList();
		rules.add(new UploadPropositionRule(UploadPropositionRuleOperatorType.CONTAIN,
				UploadPropositionRuleFieldType.RECIPIENT_EMAIL, containedValue));
		return new UploadPropositionFilter(UUID.randomUUID().toString(), rootDomainIdentifier, "GMAIL out", matchType,
				actionType, Boolean.TRUE, order, rules, new Date(), new Date());
	}

	private boolean portalCheckAndApply(List<UploadPropositionFilter> enabledFilters, UploadProposition proposition,
			Account linShareUser) {
			for (UploadPropositionFilter filter : filters) {
				if (portalMatch(filter, proposition, linShareUser)) {
					return !UploadPropositionActionType.REJECT.equals(filter.getUploadPropositionAction());
				}
			}
		return true;
	}

	private boolean portalCheckRecipient(String mail, String domainId) {
		//uploadPropositionService.checkIfValidRecipient(technicalAccount, mail, domainId);
		return true;
	}

	private boolean portalMatch(UploadPropositionFilter filter, UploadProposition uploadProposition,
			Account linShareUser) {
		int successCpt = 0;
		UploadPropositionMatchType matchType = filter.getMatchType();
		if (UploadPropositionMatchType.TRUE.equals(matchType)) {
			return true;
		}
		for (UploadPropositionRule rule : filter.getUploadPropositionRules()) {
			if (portalRuleMatch(rule, uploadProposition, linShareUser)) {
				if (matchType.equals(UploadPropositionMatchType.ANY)) {
					// only one match is enough
					return true;
				}
				successCpt += 1;
			}
		}
		// at least one rule matched
		if (successCpt >= 1) {
			// all rules should match.
			if (filter.getUploadPropositionRules().size() == successCpt) {
				return true;
			}
		}
		return false;
	}

	private boolean portalRuleMatch(UploadPropositionRule rule, UploadProposition proposition, Account linShareUser) {
		String compare = null;
		switch (rule.getField()) {
		case SENDER_EMAIL:
			compare = proposition.getContact().getMail();
			break;
		case RECIPIENT_EMAIL:
			compare = linShareUser.getMail();
			break;
		case SUBJECT:
			compare = proposition.getLabel();
			break;
		default:
			logger.error("Unknown field " + rule.getField() + " on rule : " + rule.toString());
			return false;
		}
		if (compare == null) {
			return false;
		}
		if (rule.getValue() == null) {
			return false;
		}
		UploadPropositionRuleOperatorType op = rule.getOperator();
		return op.check(compare, rule.getValue());
	}
}
