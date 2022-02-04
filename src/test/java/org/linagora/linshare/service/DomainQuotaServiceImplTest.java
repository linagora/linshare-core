/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2019-2022 LINAGORA
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

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.DomainQuotaBusinessService;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.DomainQuotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Sql({
	
	"/import-tests-domain-quota-updates.sql"})
@Transactional
@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml" })
public class DomainQuotaServiceImplTest {

	@Autowired
	private DomainQuotaService domainQuotaService;

	@Autowired
	private DomainQuotaBusinessService businessService;

	@Autowired
	private AbstractDomainRepository domainRepository;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	private AbstractDomain topDomain;

	private Account jane;

	@BeforeEach
	public void setUp() {
		jane = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
		topDomain = domainRepository.findById(LoadingServiceTestDatas.sqlDomain);
	}

	@Test
	public void testUpdateDefaultQuota() {
		Long quotaValue = 6000L;

		DomainQuota quota = businessService.find(topDomain);
		Assertions.assertNotNull(quota);
		DomainQuota dq = new DomainQuota(quota);
		Assertions.assertNotEquals(quotaValue, quota.getDefaultQuota());
		dq.setUuid(quota.getUuid());
		dq.setQuotaOverride(true);
		dq.setQuota(quotaValue);
		dq.setDefaultQuotaOverride(true);
		dq.setDefaultQuota(quotaValue);
		domainQuotaService.update(jane, dq);
		quota = businessService.find(topDomain);
		Assertions.assertEquals(quotaValue, quota.getDefaultQuota());
	}

	@Test
	public void testExceptionUpdateDefaultQuota() {
		Long quotaValue = 6000L;
		Long defaultQuotaValue = 7000L;

		DomainQuota quota = businessService.find(topDomain);
		Assertions.assertNotNull(quota);
		DomainQuota dq = new DomainQuota(quota);
		dq.setUuid(quota.getUuid());
		dq.setQuotaOverride(true);
		dq.setQuota(quotaValue);
		dq.setDefaultQuotaOverride(true);
		dq.setDefaultQuota(defaultQuotaValue);
		IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
			// An exception is thrown because the default quota can't be over then quota in
			// the same domain
			domainQuotaService.update(jane, dq);
		});
		Assertions.assertEquals("The default_quota filed can't be over quota in the same domain", exception.getMessage());
	}

}
