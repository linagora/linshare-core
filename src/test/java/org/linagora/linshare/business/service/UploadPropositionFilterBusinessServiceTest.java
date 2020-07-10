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
package org.linagora.linshare.business.service;

import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.UploadPropositionFilterBusinessService;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.UploadPropositionActionType;
import org.linagora.linshare.core.domain.constants.UploadPropositionMatchType;
import org.linagora.linshare.core.domain.constants.UploadPropositionRuleFieldType;
import org.linagora.linshare.core.domain.constants.UploadPropositionRuleOperatorType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.mongo.entities.UploadPropositionFilter;
import org.linagora.linshare.mongo.entities.UploadPropositionRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

@Disabled
//FIXME :  Enable when upload propostion service is used on LinShare
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
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
@Transactional
public class UploadPropositionFilterBusinessServiceTest {

	private static Logger logger = LoggerFactory.getLogger(UploadPropositionFilterBusinessServiceTest.class);

	@Autowired
	private UploadPropositionFilterBusinessService filterBusinessService;

	private UploadPropositionFilter referenceFilter;

	public UploadPropositionFilterBusinessServiceTest() {
		super();
	}

	@BeforeEach
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		UploadPropositionRule rule = new UploadPropositionRule(UploadPropositionRuleOperatorType.CONTAIN,
				UploadPropositionRuleFieldType.RECIPIENT_EMAIL, "gmail.com");
		referenceFilter = new UploadPropositionFilter(UUID.randomUUID().toString(), "LinShareRootDomain", "Upload Proposition Filter",
				UploadPropositionMatchType.ALL, UploadPropositionActionType.ACCEPT, Boolean.TRUE, 0,
				Lists.newArrayList(rule), new Date(), new Date());
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void createUploadPropositionFilter() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadPropositionFilter filterToCreate = initFilter(referenceFilter);
		UploadPropositionFilter persistedFilter = filterBusinessService.create(filterToCreate);
		Assertions.assertNotNull(persistedFilter, "No Filter has been created");
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void createAndFindUploadPropositionFilter() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadPropositionFilter filterToCreate = initFilter(referenceFilter);
		UploadPropositionFilter persistedFilter = filterBusinessService.create(filterToCreate);
		UploadPropositionFilter found = filterBusinessService.find(persistedFilter.getDomainUuid(),
				persistedFilter.getUuid());
		Assertions.assertNotNull(found, "No Filter has been found");
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void updateUploadPropositionFilter() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadPropositionFilter filterToCreate = initFilter(referenceFilter);
		UploadPropositionFilter persistedFilter = filterBusinessService.create(filterToCreate);
		persistedFilter.setMatchType(UploadPropositionMatchType.ANY);
		persistedFilter.setEnabled(Boolean.FALSE);
		UploadPropositionFilter updated = filterBusinessService.update(persistedFilter);
		Assertions.assertEquals( UploadPropositionMatchType.ANY, updated.getMatchType(),
				"Filter field has not been updated : MatchType");
		Assertions.assertEquals(Boolean.FALSE, updated.isEnabled(),
				"Filter field has not been updated : Enabled");
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void deleteUploadPropositionFilter() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadPropositionFilter filterToCreate = initFilter(referenceFilter);
		UploadPropositionFilter persistedFilter = filterBusinessService.create(filterToCreate);
		filterBusinessService.delete(persistedFilter);
		Assertions.assertNull(filterBusinessService.find(persistedFilter.getDomainUuid(), persistedFilter.getUuid()),
				"The filter has not been deleted");
		logger.debug(LinShareTestConstants.END_TEST);
	}

	private UploadPropositionFilter initFilter(UploadPropositionFilter referenceFilter) {
		return new UploadPropositionFilter(UUID.randomUUID().toString(), referenceFilter.getDomainUuid(),
				referenceFilter.getName(), referenceFilter.getMatchType(), referenceFilter.getUploadPropositionAction(),
				referenceFilter.isEnabled(), referenceFilter.getOrder(), referenceFilter.getUploadPropositionRules(),
				new Date(), new Date());
	}
}
