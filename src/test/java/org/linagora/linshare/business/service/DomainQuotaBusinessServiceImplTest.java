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
package org.linagora.linshare.business.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.DomainQuotaBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo-java-server.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml" })
@Sql({
	"/import-tests-domains-and-accounts.sql",
	"/import-tests-domain-quota-updates.sql"})
@Transactional
public class DomainQuotaBusinessServiceImplTest {
	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

	@Autowired
	private DomainQuotaBusinessService businessService;

	@Autowired
	private AbstractDomainRepository domainRepository;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	private AbstractDomain guestDomain;

	private AbstractDomain topDomain;

	@BeforeEach
	public void setUp() {
		guestDomain = domainRepository.findById(LoadingServiceTestDatas.sqlGuestDomain);
		topDomain = domainRepository.findById(LoadingServiceTestDatas.sqlDomain);
	}

	@Test
	public void testCascadeDefaultQuota() {
		Long quotaValue = 698L;
		DomainQuota quota = businessService.find(topDomain);
		DomainQuota dq = new DomainQuota(quota);
		dq.setQuotaOverride(true);
		dq.setQuota(quotaValue);
		dq.setDefaultQuotaOverride(true);
		dq.setDefaultQuota(quotaValue);

		businessService.update(quota, dq);

		quota = businessService.find(guestDomain);
		Assertions.assertEquals(quotaValue, quota.getDefaultQuota());
		Assertions.assertEquals(false, quota.getDefaultQuotaOverride());
		Assertions.assertEquals(quotaValue, quota.getQuota());

		quota = businessService.find(topDomain);
		Assertions.assertEquals(quotaValue, quota.getDefaultQuota());
		Assertions.assertEquals(true, quota.getDefaultQuotaOverride());
		Assertions.assertEquals(quotaValue, quota.getQuota());
		Assertions.assertEquals(true, quota.getQuotaOverride());
	}
}
