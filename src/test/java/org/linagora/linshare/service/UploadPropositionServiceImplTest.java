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

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.domain.constants.UploadPropositionActionType;
import org.linagora.linshare.core.domain.constants.UploadPropositionExceptionRuleType;
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
import org.linagora.linshare.core.service.UploadPropositionExceptionRuleService;
import org.linagora.linshare.core.service.UploadPropositionFilterService;
import org.linagora.linshare.core.service.UploadPropositionService;
import org.linagora.linshare.core.service.UploadRequestGroupService;
import org.linagora.linshare.mongo.entities.UploadProposition;
import org.linagora.linshare.mongo.entities.UploadPropositionContact;
import org.linagora.linshare.mongo.entities.UploadPropositionExceptionRule;
import org.linagora.linshare.mongo.entities.UploadPropositionFilter;
import org.linagora.linshare.mongo.entities.UploadPropositionRule;
import org.linagora.linshare.mongo.repository.UploadPropositionExceptionRuleMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

@Disabled
@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo-java-server.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml", })
public class UploadPropositionServiceImplTest {
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

	@Autowired
	private UploadPropositionExceptionRuleService uploadPropositionExceptionRuleService;

	@Autowired
	private UploadPropositionExceptionRuleMongoRepository uploadPropositionExceptionRuleMongoRepository;

	private LoadingServiceTestDatas datas;

	private AbstractDomain rootDomain;

	private Account root;

	private User john;

	private User jane;

	private TechnicalAccount technicalAccount;

	private UploadPropositionFilter acceptationFilter;

	private UploadPropositionFilter rejectionFilter;

	private UploadPropositionFilter noAcceptationFilter;

	private UploadPropositionFilter noRejectionFilter;

	private String rootDomainIdentifier = "TEST_Domain-0";

	public UploadPropositionServiceImplTest() {
		super();
	}

	@BeforeEach
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		john = datas.getUser1();
		jane = datas.getUser2();
		root = datas.getRoot();
		rootDomain = abstractDomainRepository.findById(rootDomainIdentifier);
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
		uploadPropositionfilterService.findAllEnabledFilters(find);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void createUploadPropositionAcceptedBySystem() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadPropositionFilter createdAcceptationFilter = uploadPropositionfilterService.create((User) root,
				acceptationFilter, rootDomain);
		UploadPropositionFilter createdRejectionFilter = uploadPropositionfilterService.create((User) root,
				rejectionFilter, rootDomain);
		UploadProposition uploadPropositionToCreate = initProposition(jane);
		List<UploadPropositionFilter> enabledFilters = uploadPropositionfilterService.findAll(root, root.getDomain());
		int countUploadRequests = uploadRequestGroupService
				.findAll(jane, jane, Lists.newArrayList(UploadRequestStatus.ENABLED)).size();
		int countUploadPropositions = uploadPropositionService.findAllByAccount(jane, jane).size();
		//Simulating the payload received from the external portal
		uploadPropositionToCreate.setStatus(portalCheckAndApply(enabledFilters, uploadPropositionToCreate, jane));
		UploadProposition created = uploadPropositionService.create(technicalAccount, jane.getMail(),
				uploadPropositionToCreate);
		Assertions.assertNotNull(created,"No upload Proposition created");
		Assertions.assertEquals(UploadPropositionStatus.SYSTEM_ACCEPTED, created.getStatus(),"Wrong status");
		Assertions.assertEquals(countUploadPropositions,
				uploadPropositionService.findAllByAccount(jane, jane).size(), "An uploadProposition has been created");
		Assertions.assertEquals(countUploadRequests + 1,
				uploadRequestGroupService.findAll(jane, jane, Lists.newArrayList(UploadRequestStatus.ENABLED)).size());
		uploadPropositionfilterService.delete(root, rootDomain, createdAcceptationFilter);
		uploadPropositionfilterService.delete(root, rootDomain, createdRejectionFilter);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void createUploadPropositionRejectedBySystem() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadPropositionFilter createdAcceptationFilter = uploadPropositionfilterService.create((User) root,
				acceptationFilter, rootDomain);
		UploadPropositionFilter createdRejectionFilter = uploadPropositionfilterService.create((User) root,
				rejectionFilter, rootDomain);
		List<UploadPropositionFilter> enabledFilters = uploadPropositionfilterService.findAll(root, root.getDomain());
		UploadProposition uploadPropositionToCreate = initProposition(john);
		int countUploadRequests = uploadRequestGroupService
				.findAll(john, john, Lists.newArrayList(UploadRequestStatus.ENABLED)).size();
		int countUploadPropositions = uploadPropositionService.findAllByAccount(john, john).size();
		//Simulating the payload received from the external portal
		uploadPropositionToCreate.setStatus(portalCheckAndApply(enabledFilters, uploadPropositionToCreate, john));
		Assertions.assertEquals(UploadPropositionStatus.SYSTEM_REJECTED,
				uploadPropositionToCreate.getStatus(), "Wrong status");
		UploadProposition created = uploadPropositionService.create(technicalAccount, john.getMail(),
				uploadPropositionToCreate);
		Assertions.assertNotNull(created, "No upload Proposition created");
		Assertions.assertEquals(UploadPropositionStatus.SYSTEM_REJECTED, created.getStatus(), "Wrong status");
		Assertions.assertEquals(countUploadPropositions,
				uploadPropositionService.findAllByAccount(john, john).size(), "An uploadProposition has been created");
		Assertions.assertEquals(countUploadRequests,
				uploadRequestGroupService.findAll(john, john, Lists.newArrayList(UploadRequestStatus.ENABLED)).size());
		uploadPropositionfilterService.delete(root, rootDomain, createdAcceptationFilter);
		uploadPropositionfilterService.delete(root, rootDomain, createdRejectionFilter);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void createUploadPropositionNotDealtBySystem() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadPropositionFilter createdNoAcceptationFilter = uploadPropositionfilterService.create((User) root,
				noAcceptationFilter, rootDomain);
		UploadPropositionFilter createdNoRejectionFilter = uploadPropositionfilterService.create((User) root,
				noRejectionFilter, rootDomain);
		List<UploadPropositionFilter> enabledFilters = uploadPropositionfilterService.findAll(root, root.getDomain());
		UploadProposition uploadPropositionToCreate = initProposition(john);
		int countUploadRequests = uploadRequestGroupService
				.findAll(john, john, Lists.newArrayList(UploadRequestStatus.ENABLED)).size();
		int countUploadPropositions = uploadPropositionService.findAllByAccount(john, john).size();
		//Simulating the payload received from the external portal
		uploadPropositionToCreate.setStatus(portalCheckAndApply(enabledFilters, uploadPropositionToCreate, john));
		Assertions.assertEquals(UploadPropositionStatus.SYSTEM_PENDING,
				uploadPropositionToCreate.getStatus(), "Wrong status");
		UploadProposition created = uploadPropositionService.create(technicalAccount, john.getMail(),
				uploadPropositionToCreate);
		Assertions.assertNotNull(created, "No upload Proposition created");
		Assertions.assertEquals(UploadPropositionStatus.USER_PENDING, created.getStatus(), "Wrong status");
		Assertions.assertEquals(countUploadPropositions + 1,
				uploadPropositionService.findAllByAccount(john, john).size(), "No uploadProposition has been created");
		Assertions.assertEquals(countUploadRequests,
				uploadRequestGroupService.findAll(john, john, Lists.newArrayList(UploadRequestStatus.ENABLED)).size());
		uploadPropositionfilterService.delete(root, rootDomain, createdNoAcceptationFilter);
		uploadPropositionfilterService.delete(root, rootDomain, createdNoRejectionFilter);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void acceptUploadPropositionByUser() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadPropositionFilter createdNoAcceptationFilter = uploadPropositionfilterService.create((User) root,
				noAcceptationFilter, rootDomain);
		UploadPropositionFilter createdNoRejectionFilter = uploadPropositionfilterService.create((User) root,
				noRejectionFilter, rootDomain);
		List<UploadPropositionFilter> enabledFilters = uploadPropositionfilterService.findAll(root, root.getDomain());
		UploadProposition uploadPropositionToCreate = initProposition(jane);
		int countUploadRequests = uploadRequestGroupService
				.findAll(jane, jane, Lists.newArrayList(UploadRequestStatus.ENABLED)).size();
		int countUploadPropositions = uploadPropositionService.findAllByAccount(jane, jane).size();
		//Simulating the payload received from the external portal
		uploadPropositionToCreate.setStatus(portalCheckAndApply(enabledFilters, uploadPropositionToCreate, jane));
		Assertions.assertEquals(UploadPropositionStatus.SYSTEM_PENDING,
				uploadPropositionToCreate.getStatus(), "Wrong status");
		UploadProposition created = uploadPropositionService.create(technicalAccount, jane.getMail(),
				uploadPropositionToCreate);
		Assertions.assertNotNull(created, "No upload Proposition created");
		Assertions.assertEquals(UploadPropositionStatus.USER_PENDING, created.getStatus(), "Wrong status");
		Assertions.assertNotNull(uploadPropositionService.find(jane, jane, created.getUuid()), "No uploadProposition has been created");
		Assertions.assertEquals(countUploadRequests,
				uploadRequestGroupService.findAll(jane, jane, Lists.newArrayList(UploadRequestStatus.ENABLED)).size());
		UploadProposition acceptedUploadProposition = uploadPropositionService.accept(jane, jane, created.getUuid());
		Assertions.assertEquals(UploadPropositionStatus.USER_ACCEPTED, acceptedUploadProposition.getStatus());
		Assertions.assertEquals(countUploadPropositions,
				uploadPropositionService.findAllByAccount(jane, jane).size(), "No uploadProposition has been created");
		Assertions.assertEquals(countUploadRequests + 1,
				uploadRequestGroupService.findAll(jane, jane, Lists.newArrayList(UploadRequestStatus.ENABLED)).size());
		uploadPropositionfilterService.delete(root, rootDomain, createdNoAcceptationFilter);
		uploadPropositionfilterService.delete(root, rootDomain, createdNoRejectionFilter);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	@Disabled //FIXME :  Handle issues and enable the test
	public void rejectUploadPropositionByUser() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadPropositionFilter createdNoAcceptationFilter = uploadPropositionfilterService.create((User) root,
				noAcceptationFilter, rootDomain);
		UploadPropositionFilter createdNoRejectionFilter = uploadPropositionfilterService.create((User) root,
				noRejectionFilter, rootDomain);
		List<UploadPropositionFilter> enabledFilters = uploadPropositionfilterService.findAll(root, root.getDomain());
		UploadProposition uploadPropositionToCreate = initProposition(jane);
		int countUploadRequests = uploadRequestGroupService
				.findAll(jane, jane, Lists.newArrayList(UploadRequestStatus.ENABLED)).size();
		int countUploadPropositions = uploadPropositionService.findAllByAccount(jane, jane).size();
		//Simulating the payload received from the external portal
		uploadPropositionToCreate.setStatus(portalCheckAndApply(enabledFilters, uploadPropositionToCreate, jane));
		Assertions.assertEquals(UploadPropositionStatus.SYSTEM_PENDING,
				uploadPropositionToCreate.getStatus(), "Wrong status");
		UploadProposition created = uploadPropositionService.create(technicalAccount, jane.getMail(),
				uploadPropositionToCreate);
		Assertions.assertNotNull(created, "No upload Proposition created");
		Assertions.assertEquals(UploadPropositionStatus.USER_PENDING, created.getStatus(), "Wrong status");
		Assertions.assertNotNull(uploadPropositionService.find(jane, jane, created.getUuid()), "No uploadProposition has been created");
		Assertions.assertEquals(countUploadRequests,
				uploadRequestGroupService.findAll(jane, jane, Lists.newArrayList(UploadRequestStatus.ENABLED)).size());
		UploadProposition acceptedUploadProposition = uploadPropositionService.reject(jane, jane, created.getUuid());
		Assertions.assertEquals(UploadPropositionStatus.USER_REJECTED, acceptedUploadProposition.getStatus());
		Assertions.assertEquals(countUploadPropositions,
				uploadPropositionService.findAllByAccount(jane, jane).size(), "No uploadProposition has been deleted");
		Assertions.assertEquals(countUploadRequests,
				uploadRequestGroupService.findAll(jane, jane, Lists.newArrayList(UploadRequestStatus.ENABLED)).size());
		uploadPropositionfilterService.delete(root, rootDomain, createdNoAcceptationFilter);
		uploadPropositionfilterService.delete(root, rootDomain, createdNoRejectionFilter);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void rejectUploadPropositionByBlackList() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadPropositionFilter createdNoAcceptationFilter = uploadPropositionfilterService.create((User) root,
				noAcceptationFilter, rootDomain);
		UploadPropositionFilter createdNoRejectionFilter = uploadPropositionfilterService.create((User) root,
				noRejectionFilter, rootDomain);
		//Add yoda@linshare.com to jane's blacklist
		UploadPropositionExceptionRule createdExceptionRule = addMailToList(jane, "yoda@linshare.com", UploadPropositionExceptionRuleType.DENY);
		List<UploadPropositionFilter> enabledFilters = uploadPropositionfilterService.findAll(root, root.getDomain());
		UploadProposition uploadPropositionToCreate = initProposition(jane);
		int countUploadRequests = uploadRequestGroupService
				.findAll(jane, jane, Lists.newArrayList(UploadRequestStatus.ENABLED)).size();
		int countUploadPropositions = uploadPropositionService.findAllByAccount(jane, jane).size();
		//Simulating the payload received from the external portal
		uploadPropositionToCreate.setStatus(portalCheckAndApply(enabledFilters, uploadPropositionToCreate, jane));
		Assertions.assertEquals(UploadPropositionStatus.SYSTEM_PENDING,
				uploadPropositionToCreate.getStatus(), "Wrong status");
		UploadProposition created = uploadPropositionService.create(technicalAccount, jane.getMail(),
				uploadPropositionToCreate);
		Assertions.assertNotNull(created, "No upload Proposition created");
		Assertions.assertEquals(UploadPropositionStatus.USER_REJECTED, created.getStatus(), "Wrong status");
		Assertions.assertEquals(countUploadPropositions,
				uploadPropositionService.findAllByAccount(jane, jane).size(), "An uploadProposition has been created");
		Assertions.assertEquals(countUploadRequests,
				uploadRequestGroupService.findAll(jane, jane, Lists.newArrayList(UploadRequestStatus.ENABLED)).size());
		uploadPropositionExceptionRuleMongoRepository.delete(createdExceptionRule);
		uploadPropositionfilterService.delete(root, rootDomain, createdNoAcceptationFilter);
		uploadPropositionfilterService.delete(root, rootDomain, createdNoRejectionFilter);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	@Disabled //FIXME :  Handle issues and enable the test
	public void acceptUploadPropositionByWhiteList() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadPropositionFilter createdNoAcceptationFilter = uploadPropositionfilterService.create((User) root,
				noAcceptationFilter, rootDomain);
		UploadPropositionFilter createdNoRejectionFilter = uploadPropositionfilterService.create((User) root,
				noRejectionFilter, rootDomain);
		//Add yoda@linshare.com to jane's white list
		UploadPropositionExceptionRule createdExceptionRule = addMailToList(jane, "yoda@linshare.com", UploadPropositionExceptionRuleType.ALLOW);
		List<UploadPropositionFilter> enabledFilters = uploadPropositionfilterService.findAll(root, root.getDomain());
		UploadProposition uploadPropositionToCreate = initProposition(jane);
		int countUploadRequests = uploadRequestGroupService
				.findAll(jane, jane, Lists.newArrayList(UploadRequestStatus.ENABLED)).size();
		int countUploadPropositions = uploadPropositionService.findAllByAccount(jane, jane).size();
		//Simulating the payload received from the external portal
		uploadPropositionToCreate.setStatus(portalCheckAndApply(enabledFilters, uploadPropositionToCreate, jane));
		Assertions.assertEquals(UploadPropositionStatus.SYSTEM_PENDING,
				uploadPropositionToCreate.getStatus(), "Wrong status");
		UploadProposition created = uploadPropositionService.create(technicalAccount, jane.getMail(),
				uploadPropositionToCreate);
		Assertions.assertNotNull(created, "No upload Proposition created");
		Assertions.assertEquals(UploadPropositionStatus.USER_ACCEPTED, created.getStatus(), "Wrong status");
		Assertions.assertEquals(countUploadPropositions,
				uploadPropositionService.findAllByAccount(jane, jane).size(), "An uploadProposition has been created");
		Assertions.assertEquals(countUploadRequests + 1,
				uploadRequestGroupService.findAll(jane, jane, Lists.newArrayList(UploadRequestStatus.ENABLED)).size());
		uploadPropositionExceptionRuleMongoRepository.delete(createdExceptionRule);
		uploadPropositionfilterService.delete(root, rootDomain, createdNoAcceptationFilter);
		uploadPropositionfilterService.delete(root, rootDomain, createdNoRejectionFilter);
		logger.info(LinShareTestConstants.END_TEST);
	}

	private UploadProposition initProposition(User actor) {
		return new UploadProposition(UUID.randomUUID().toString(), john.getDomainId(), null, "Label",
				"Body Upload Proposition", new UploadPropositionContact("First", "Last", "yoda@linshare.com"),
				actor.getLsUuid(), new Date(), new Date());
	}

	private UploadPropositionFilter initFilter(User root, UploadPropositionMatchType matchType,
			UploadPropositionActionType actionType, Integer order, String containedValue) {
		List<UploadPropositionRule> rules = Lists.newArrayList();
		rules.add(new UploadPropositionRule(UploadPropositionRuleOperatorType.CONTAIN,
				UploadPropositionRuleFieldType.RECIPIENT_EMAIL, containedValue));
		return new UploadPropositionFilter(UUID.randomUUID().toString(), rootDomainIdentifier,
				"FILTER " + actionType + " contained value " + containedValue, matchType, actionType, Boolean.TRUE,
				order, rules, new Date(), new Date());
	}

	private UploadPropositionStatus portalCheckAndApply(List<UploadPropositionFilter> enabledFilters, UploadProposition proposition,
			Account linShareUser) {
			for (UploadPropositionFilter filter : enabledFilters) {
				if (portalMatch(filter, proposition, linShareUser)) {
					if (UploadPropositionActionType.REJECT.equals(filter.getUploadPropositionAction())) {
						return UploadPropositionStatus.SYSTEM_REJECTED;
					}
					return UploadPropositionStatus.SYSTEM_ACCEPTED;
				}
			}
		return UploadPropositionStatus.SYSTEM_PENDING;
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

	private UploadPropositionExceptionRule addMailToList(Account actor, String mail, UploadPropositionExceptionRuleType exceptionRuleType) {
		UploadPropositionExceptionRule rule = new UploadPropositionExceptionRule(UUID.randomUUID().toString(), actor.getDomainId(), mail, actor.getLsUuid(), exceptionRuleType, new Date(), new Date());
		return uploadPropositionExceptionRuleService.create(actor, actor, rule);
	}
}
