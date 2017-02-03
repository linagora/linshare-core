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
package org.linagora.linshare.repository.hibernate;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.QuotaType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.domain.entities.Quota;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountQuotaRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.ContainerQuotaRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.repository.hibernate.DomainQuotaRepositoryImpl;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = { "classpath:springContext-test.xml", "classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml" })

public class DomainQuotaRepositoryImplTest extends AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

	@Autowired
	private AccountQuotaRepository accountQuotaRepository;

	@Autowired
	private ContainerQuotaRepository containerQuotaRepository;

	@Autowired
	private DomainQuotaRepositoryImpl domainQuotaRepository;

	@Autowired
	private AbstractDomainRepository domainRepository;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	LoadingServiceTestDatas datas;
	private User jane;
	private User root;
	private AbstractDomain guestDomain;
	private AbstractDomain topDomain;

	@Before
	public void setUp() {
		this.executeSqlScript("import-tests-default-domain-quotas.sql", false);
		this.executeSqlScript("import-tests-domain-quota-updates.sql", false);
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		jane = datas.getUser2();
		root = userRepository.findByMailAndDomain(LoadingServiceTestDatas.sqlRootDomain, "root@localhost.localdomain");
		guestDomain = domainRepository.findById(LoadingServiceTestDatas.sqlGuestDomain);
		topDomain = domainRepository.findById(LoadingServiceTestDatas.sqlDomain);
	}

	@Test
	public void testCascadeMaintenanceMode() {
		initialChecks();
		Long count = domainQuotaRepository.cascadeMaintenanceMode(root.getDomain(), true);
		// LinShareRootDomain : 1 domain, 2 containers, 1 account
		// MyDomain : 1 domain, 2 containers, 2 accounts
		// subomains: GuestDomain (1 domain and 2 containers), MySubDomain(1 domain, 2 containers, 1 account)
		assertEquals(new Long(16), count);
		Quota quota = accountQuotaRepository.find(jane);
		assertEquals(true, quota.getMaintenance());
		quota = accountQuotaRepository.find(root);
		assertEquals(true, quota.getMaintenance());
	}

	@Test
	public void testCascadeMaintenanceToSubdomains() {
		initialChecks();
		AbstractDomain myDomain = jane.getDomain();
		Long count = domainQuotaRepository.cascadeMaintenanceMode(myDomain, true);
		// MyDomain : 1 domain, 2 containers, 2 accounts
		// subomains: GuestDomain (1 domain and 2 containers), MySubDomain(1 domain, 2 containers, 1 account)
		assertEquals(new Long(12), count);
		Quota quota = accountQuotaRepository.find(jane);
		assertEquals(true, quota.getMaintenance());
		quota = accountQuotaRepository.find(root);
		assertEquals(false, quota.getMaintenance());
	}

	private void initialChecks() {
		List<DomainQuota> domains = domainQuotaRepository.findAll();
		List<ContainerQuota> containers = containerQuotaRepository.findAll();
		List<AccountQuota> accounts = accountQuotaRepository.findAll();
		// check initial conditions
		/// LinShareRootDomain, MyDomain, MySubDomain and GuestDomain
		assertEquals(4, domains.size());
		// 2 containers by domain
		assertEquals(8, containers.size());
		// root , jane and john
		assertEquals(4, accounts.size());
	}

	@Test
	public void testCascadeDefaultQuotaToSubDomainsDefaultQuota() {
		initialChecks();
		Quota quota = null;
		Long count = null;

		Long quotaValue = 1000000000000L;

		quota = domainQuotaRepository.find(guestDomain);
		assertEquals(quotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(quotaValue, quota.getQuota());

		Long newQuotaValue = 8L;
		List<Long> quotaIds = domainQuotaRepository.getQuotaIdforDefaultQuotaInSubDomains(root.getDomain(),
				newQuotaValue, QuotaType.DOMAIN_QUOTA);
		assertEquals(2, quotaIds.size());
		count = domainQuotaRepository.cascadeDefaultQuotaToSubDomainsDefaultQuota(root.getDomain(), newQuotaValue,
				quotaIds);
		assertEquals(new Long(2), count);

		quota = domainQuotaRepository.find(guestDomain);
		assertEquals(newQuotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(quotaValue, quota.getQuota());
	}

	@Test
	public void testCascadeDefaultQuotaToSubDomainsQuota() {
		initialChecks();
		Quota quota = null;
		Long count = null;

		Long quotaValue = 1000000000000L;

		quota = domainQuotaRepository.find(guestDomain);
		assertEquals(quotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(quotaValue, quota.getQuota());

		Long newQuotaValue = 8L;
		List<Long> quotaIds = domainQuotaRepository.getQuotaIdforQuotaInSubDomains(root.getDomain(), newQuotaValue,
				QuotaType.DOMAIN_QUOTA);
		assertEquals(2, quotaIds.size());
		count = domainQuotaRepository.cascadeDefaultQuotaToSubDomainsQuota(root.getDomain(), newQuotaValue, quotaIds);
		assertEquals(new Long(2), count);

		quota = domainQuotaRepository.find(guestDomain);
		assertEquals(quotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(newQuotaValue, quota.getQuota());

	}

	@Test
	public void testCascadeDefaultQuotaToDefaultQuotaOfChildrenDomains() {
		initialChecks();
		Quota quota = null;
		Long count = null;

		Long quotaValue = 1000000000000L;

		quota = domainQuotaRepository.find(guestDomain);
		assertEquals(quotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(quotaValue, quota.getQuota());

		Long newQuotaValue = 8L;
		count = domainQuotaRepository.cascadeDefaultQuotaToDefaultQuotaOfChildrenDomains(root.getDomain(),
				newQuotaValue);
		assertEquals(new Long(1), count);

		quota = domainQuotaRepository.find(guestDomain);
		assertEquals(quotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(quotaValue, quota.getQuota());

		quota = domainQuotaRepository.find(topDomain);
		assertEquals(newQuotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(quotaValue, quota.getQuota());
	}

	@Test
	public void testCascadeDefaultQuotaToQuotaOfChildrenDomains() {
		initialChecks();
		Quota quota = null;
		Long count = null;

		Long quotaValue = 1000000000000L;

		quota = domainQuotaRepository.find(guestDomain);
		assertEquals(quotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(quotaValue, quota.getQuota());

		Long newQuotaValue = 8L;
		count = domainQuotaRepository.cascadeDefaultQuotaToQuotaOfChildrenDomains(root.getDomain(), newQuotaValue);
		assertEquals(new Long(1), count);

		quota = domainQuotaRepository.find(guestDomain);
		assertEquals(quotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(quotaValue, quota.getQuota());

		quota = domainQuotaRepository.find(topDomain);
		assertEquals(quotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(newQuotaValue, quota.getQuota());
	}

	@Test
	public void testCascadeDefaultQuotaToQuotaOfChildrenDomains2() {
		initialChecks();
		Quota quota = null;
		Long count = null;

		Long quotaValue = 1000000000000L;

		quota = domainQuotaRepository.find(guestDomain);
		assertEquals(quotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(quotaValue, quota.getQuota());

		Long newQuotaValue = 8L;
		count = domainQuotaRepository.cascadeDefaultQuotaToQuotaOfChildrenDomains(jane.getDomain(), newQuotaValue);
		assertEquals(new Long(2), count);

		quota = domainQuotaRepository.find(guestDomain);
		assertEquals(quotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(newQuotaValue, quota.getQuota());
	}

	@Test
	public void testCascadeDefaultQuotaToDefaultQuotaOfChildrenDomains2() {
		initialChecks();
		Quota quota = null;
		Long count = null;

		Long quotaValue = 1000000000000L;

		quota = domainQuotaRepository.find(guestDomain);
		assertEquals(quotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(quotaValue, quota.getQuota());

		Long newQuotaValue = 8L;
		count = domainQuotaRepository.cascadeDefaultQuotaToDefaultQuotaOfChildrenDomains(jane.getDomain(),
				newQuotaValue);
		assertEquals(new Long(2), count);

		quota = domainQuotaRepository.find(topDomain);
		assertEquals(quotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(quotaValue, quota.getQuota());

		quota = domainQuotaRepository.find(guestDomain);
		assertEquals(newQuotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(quotaValue, quota.getQuota());
	}

	@Test
	public void testCascadeDefaultQuota() {
		initialChecks();
		Quota quota = null;
		Long count = null;

		Long quotaValue = 1000000000000L;

		quota = domainQuotaRepository.find(guestDomain);
		assertEquals(quotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(quotaValue, quota.getQuota());

		Long newQuotaValue = 8L;
		count = domainQuotaRepository.cascadeDefaultQuota(root.getDomain(), newQuotaValue);
		// 1 top (2 quotas), 1 sub (2 quotas), 1 sub (2 quotas),
		assertEquals(new Long(6), count);

		quota = domainQuotaRepository.find(topDomain);
		assertEquals(newQuotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(newQuotaValue, quota.getQuota());

		quota = domainQuotaRepository.find(guestDomain);
		assertEquals(newQuotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(newQuotaValue, quota.getQuota());
	}
}
