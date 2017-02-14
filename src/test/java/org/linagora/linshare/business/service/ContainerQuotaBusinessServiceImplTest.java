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
package org.linagora.linshare.business.service;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.ContainerQuotaBusinessService;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml" })
public class ContainerQuotaBusinessServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

	@Autowired
	private ContainerQuotaBusinessService businessService;

	@Autowired
	private AccountQuotaBusinessService accountBusinessService;

	@Autowired
	private AbstractDomainRepository domainRepository;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	LoadingServiceTestDatas datas;

	private AbstractDomain guestDomain;

	private AbstractDomain topDomain;

	private AbstractDomain rootDomain;

	private User jane;

	public ContainerQuotaBusinessServiceImplTest() {
		super();
	}

	@Before
	public void setUp() {
		this.executeSqlScript("import-tests-default-domain-quotas.sql", false);
		this.executeSqlScript("import-tests-domain-quota-updates.sql", false);
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		jane = datas.getUser2();
//		root = userRepository.findByMailAndDomain(LoadingServiceTestDatas.sqlRootDomain, "root@localhost.localdomain");
		guestDomain = domainRepository.findById(LoadingServiceTestDatas.sqlGuestDomain);
		topDomain = domainRepository.findById(LoadingServiceTestDatas.sqlDomain);
		rootDomain = domainRepository.findById(LoadingServiceTestDatas.sqlRootDomain);
	}

	@Test
	public void testCascadeDefaultQuota() {
		Long quotaValue = 698L;

		ContainerQuota quota = businessService.find(topDomain, ContainerQuotaType.USER);
		ContainerQuota dto = new ContainerQuota(quota);

		dto.setQuotaOverride(true);
		dto.setQuota(quotaValue);
		dto.setDefaultQuotaOverride(true);
		dto.setDefaultQuota(quotaValue);
		dto.setDefaultMaxFileSizeOverride(true);
		dto.setDefaultMaxFileSize(quotaValue);
		dto.setMaxFileSizeOverride(true);
		dto.setMaxFileSize(quotaValue);

		businessService.update(quota, dto);

		quota = businessService.find(guestDomain, ContainerQuotaType.USER);
		assertEquals(quotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(quotaValue, quota.getQuota());

		quota = businessService.find(topDomain, ContainerQuotaType.USER);
		assertEquals(quotaValue, quota.getDefaultQuota());
		assertEquals(true, quota.getDefaultQuotaOverride());
		assertEquals(quotaValue, quota.getQuota());
		assertEquals(true, quota.getQuotaOverride());
		assertEquals(quotaValue, quota.getDefaultMaxFileSize());
		assertEquals(true, quota.getDefaultMaxFileSizeOverride());
		assertEquals(quotaValue, quota.getMaxFileSize());
		assertEquals(true, quota.getMaxFileSizeOverride());
	}

	@Test
	public void testCascadeMaxFileSize() {
		Long quotaValue = 698L;
		Long originalQuotaValue = 10000000000L;

		ContainerQuota quota = businessService.find(topDomain, ContainerQuotaType.USER);
		ContainerQuota dto = new ContainerQuota(quota);

		dto.setMaxFileSizeOverride(true);
		dto.setMaxFileSize(quotaValue);

		businessService.update(quota, dto);

		quota = businessService.find(topDomain, ContainerQuotaType.USER);
		assertEquals(quotaValue, quota.getMaxFileSize());
		assertEquals(true, quota.getMaxFileSizeOverride());

		quota = businessService.find(guestDomain, ContainerQuotaType.USER);
		assertEquals(originalQuotaValue, quota.getMaxFileSize());
		assertEquals(false, quota.getMaxFileSizeOverride());

		AccountQuota aq = accountBusinessService.find(jane);
		assertEquals(quotaValue, aq.getMaxFileSize());
		assertEquals(false, aq.getMaxFileSizeOverride());
	}

	@Test
	public void testCascadeDefaultMaxFileSize() {
		Long quotaValue = 698L;
		Long originalQuotaValue = 1000000000L;

		ContainerQuota quota = businessService.find(topDomain, ContainerQuotaType.USER);
		ContainerQuota dto = new ContainerQuota(quota);

		dto.setDefaultMaxFileSizeOverride(true);
		dto.setDefaultMaxFileSize(quotaValue);

		businessService.update(quota, dto);

		quota = businessService.find(topDomain, ContainerQuotaType.USER);
		assertEquals(quotaValue, quota.getDefaultMaxFileSize());
		assertEquals(true, quota.getDefaultMaxFileSizeOverride());

		quota = businessService.find(guestDomain, ContainerQuotaType.USER);
		assertEquals(quotaValue, quota.getDefaultMaxFileSize());
		assertEquals(false, quota.getDefaultMaxFileSizeOverride());

		// Jane is in topdomain, so she is not impacted by defaultMaxFileSize modification.
		AccountQuota aq = accountBusinessService.find(jane);
		assertEquals(originalQuotaValue, aq.getMaxFileSize());
		assertEquals(false, aq.getMaxFileSizeOverride());
	}

	@Test
	public void testCascadeAccountQuota() {
		Long quotaValue = 698L;

		ContainerQuota quota = businessService.find(topDomain, ContainerQuotaType.USER);
		ContainerQuota dto = new ContainerQuota(quota);

		dto.setAccountQuotaOverride(true);
		dto.setAccountQuota(quotaValue);

		businessService.update(quota, dto);

		quota = businessService.find(topDomain, ContainerQuotaType.USER);
		assertEquals(quotaValue, quota.getAccountQuota());
		assertEquals(true, quota.getAccountQuotaOverride());

		AccountQuota aq = accountBusinessService.find(jane);
		assertEquals(quotaValue, aq.getQuota());
		assertEquals(false, aq.getQuotaOverride());

	}

	@Test
	public void testCascadeDefaultAccountQuota() {
		Long quotaValue = 698L;

		ContainerQuota quota = businessService.find(rootDomain, ContainerQuotaType.USER);
		ContainerQuota dto = new ContainerQuota(quota);

		dto.setDefaultAccountQuotaOverride(true);
		dto.setDefaultAccountQuota(quotaValue);

		businessService.update(quota, dto);

		quota = businessService.find(topDomain, ContainerQuotaType.USER);
		assertEquals(quotaValue, quota.getDefaultAccountQuota());
		assertEquals(false, quota.getDefaultAccountQuotaOverride());

		AccountQuota aq = accountBusinessService.find(jane);
		assertEquals(quotaValue, aq.getQuota());
		assertEquals(false, aq.getQuotaOverride());

	}

	@Test
	public void testCascadeDefaultQuotaForRoot() {
		Long quotaValue = 698L;

		ContainerQuota quota = businessService.find(topDomain.getParentDomain(), ContainerQuotaType.USER);

		assertEquals(null, quota.getQuotaOverride());
		assertEquals(null, quota.getDefaultQuotaOverride());
		assertEquals(null, quota.getDefaultMaxFileSizeOverride());
		assertEquals(null, quota.getDefaultAccountQuotaOverride());

		ContainerQuota dto = new ContainerQuota(quota);
		dto.setQuota(quotaValue);
		dto.setDefaultQuota(quotaValue);
		// dto.setDefaultMaxFileSize(quotaValue);

		businessService.update(quota, dto);

		quota = businessService.find(guestDomain, ContainerQuotaType.USER);
		assertEquals(quotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(quotaValue, quota.getQuota());

		quota = businessService.find(topDomain, ContainerQuotaType.USER);
		assertEquals(quotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(quotaValue, quota.getQuota());
		assertEquals(false, quota.getQuotaOverride());
		// assertEquals(quotaValue, quota.getDefaultMaxFileSize());
		// assertEquals(true, quota.getDefaultMaxFileSizeOverride());
	}
}
