/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml" })
@Sql({
	
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
