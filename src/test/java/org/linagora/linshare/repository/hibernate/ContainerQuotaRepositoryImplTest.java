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
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
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
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.repository.hibernate.ContainerQuotaRepositoryImpl;
import org.linagora.linshare.core.repository.hibernate.DomainQuotaRepositoryImpl;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = { "classpath:springContext-test.xml", "classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml" })

public class ContainerQuotaRepositoryImplTest extends AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

	@Autowired
	private AccountQuotaRepository accountQuotaRepository;

	@Autowired
	private ContainerQuotaRepositoryImpl containerQuotaRepository;

	@Autowired
	private DomainQuotaRepositoryImpl domainQuotaRepository;

	@Autowired
	private AbstractDomainRepository domainRepository;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	LoadingServiceTestDatas datas;
	private User jane;
	private User inconsistent;
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
		inconsistent = userRepository.findByMailAndDomain(LoadingServiceTestDatas.sqlDomain, "inconsistent-user1@linshare.org");
		root = userRepository.findByMailAndDomain(LoadingServiceTestDatas.sqlRootDomain, "root@localhost.localdomain");
		guestDomain = domainRepository.findById(LoadingServiceTestDatas.sqlGuestDomain);
		topDomain = domainRepository.findById(LoadingServiceTestDatas.sqlDomain);
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
	public void testCascadeMaintenanceMode() {
		initialChecks();
		Quota quota = null;
		Long count = null;
		ContainerQuota container = null;

		container = containerQuotaRepository.find(root.getDomain(), ContainerQuotaType.USER);
		assertEquals(false, container.getMaintenance());
		quota = accountQuotaRepository.find(jane);
		assertEquals(false, quota.getMaintenance());
		quota = accountQuotaRepository.find(root);
		assertEquals(false, quota.getMaintenance());
		quota = accountQuotaRepository.find(inconsistent);
		assertEquals(false, quota.getMaintenance());

		count = containerQuotaRepository.cascadeMaintenanceMode(container, true);
		assertEquals(new Long(1), count);

		container = containerQuotaRepository.find(root.getDomain(), ContainerQuotaType.USER);
		assertEquals(false, container.getMaintenance());
		quota = accountQuotaRepository.find(jane);
		assertEquals(false, quota.getMaintenance());
		quota = accountQuotaRepository.find(root);
		assertEquals(true, quota.getMaintenance());
		quota = accountQuotaRepository.find(inconsistent);
		assertEquals(false, quota.getMaintenance());

	}

	@Test
	public void testCascadeDefaultQuotaToSubDomainsDefaultQuota() {
		initialChecks();
		Quota quota = null;
		Long count = null;
		ContainerQuota container = null;

		Long quotaValue = 400000000000L;

		container = containerQuotaRepository.find(root.getDomain(), ContainerQuotaType.USER);
		assertEquals(quotaValue, container.getDefaultQuota());
		assertEquals(null, container.getDefaultQuotaOverride());
		assertEquals(quotaValue, container.getQuota());

		assertEquals(false, container.getMaintenance());
		quota = accountQuotaRepository.find(jane);
		assertEquals(false, quota.getMaintenance());
		quota = accountQuotaRepository.find(root);
		assertEquals(false, quota.getMaintenance());
		quota = accountQuotaRepository.find(inconsistent);
		assertEquals(false, quota.getMaintenance());


		Long newQuotaValue = 8L;
		List<Long> quotaIdList = containerQuotaRepository.getQuotaIdforQuotaInSubDomains(container.getDomain(),
				newQuotaValue, QuotaType.CONTAINER_QUOTA, ContainerQuotaType.USER);
		assertEquals(2, quotaIdList.size());
		count = containerQuotaRepository.cascadeDefaultQuotaToSubDomainsDefaultQuota(container.getDomain(), newQuotaValue, quotaIdList);
		assertEquals(new Long(2), count);

		container = containerQuotaRepository.find(guestDomain, ContainerQuotaType.USER);
		assertEquals(newQuotaValue, container.getDefaultQuota());
		assertEquals(false, container.getDefaultQuotaOverride());
		assertEquals(quotaValue, container.getQuota());
		assertEquals(false, container.getQuotaOverride());
	}

	@Test
	public void testCascadeDefaultQuotaToSubDomainsQuota() {
		initialChecks();
		Quota quota = null;
		Long count = null;
		ContainerQuota container = null;

		Long quotaValue = 400000000000L;

		container = containerQuotaRepository.find(root.getDomain(), ContainerQuotaType.USER);
		assertEquals(quotaValue, container.getDefaultQuota());
		assertEquals(null, container.getDefaultQuotaOverride());
		assertEquals(quotaValue, container.getQuota());

		assertEquals(false, container.getMaintenance());
		quota = accountQuotaRepository.find(jane);
		assertEquals(false, quota.getMaintenance());
		quota = accountQuotaRepository.find(root);
		assertEquals(false, quota.getMaintenance());
		quota = accountQuotaRepository.find(inconsistent);
		assertEquals(false, quota.getMaintenance());


		Long newQuotaValue = 8L;
		List<Long> quotaIdList = containerQuotaRepository.getQuotaIdforQuotaInSubDomains(container.getDomain(),
				newQuotaValue, QuotaType.CONTAINER_QUOTA, ContainerQuotaType.USER);
		assertEquals(2, quotaIdList.size());
		count = containerQuotaRepository.cascadeDefaultQuotaToSubDomainsQuota(container.getDomain(), newQuotaValue, quotaIdList);
		assertEquals(new Long(2), count);

		container = containerQuotaRepository.find(guestDomain, ContainerQuotaType.USER);
		assertEquals(quotaValue, container.getDefaultQuota());
		assertEquals(false, container.getDefaultQuotaOverride());
		assertEquals(newQuotaValue, container.getQuota());
		assertEquals(false, container.getQuotaOverride());
	}

	@Test
	public void testCascadeDefaultQuotaToDefaultQuotaOfChildrenDomains() {
		initialChecks();
		Long count = null;
		ContainerQuota container = null;

		Long quotaValue = 400000000000L;

		container = containerQuotaRepository.find(root.getDomain(), ContainerQuotaType.USER);
		assertEquals(quotaValue, container.getDefaultQuota());
		assertEquals(null, container.getDefaultQuotaOverride());
		assertEquals(quotaValue, container.getQuota());

		container = containerQuotaRepository.find(guestDomain, ContainerQuotaType.USER);
		assertEquals(quotaValue, container.getDefaultQuota());
		assertEquals(false, container.getDefaultQuotaOverride());
		assertEquals(quotaValue, container.getQuota());
		assertEquals(false, container.getQuotaOverride());

		container = containerQuotaRepository.find(topDomain, ContainerQuotaType.USER);
		assertEquals(quotaValue, container.getDefaultQuota());
		assertEquals(false, container.getDefaultQuotaOverride());
		assertEquals(quotaValue, container.getQuota());
		assertEquals(false, container.getQuotaOverride());


		Long newQuotaValue = 8L;
		count = containerQuotaRepository.cascadeDefaultQuotaToDefaultQuotaOfChildrenDomains(root.getDomain(), newQuotaValue, ContainerQuotaType.USER);
		// Only one top domain.
		assertEquals(new Long(1), count);

		container = containerQuotaRepository.find(topDomain, ContainerQuotaType.USER);
		assertEquals(newQuotaValue, container.getDefaultQuota());
		assertEquals(false, container.getDefaultQuotaOverride());
		assertEquals(quotaValue, container.getQuota());
		assertEquals(false, container.getQuotaOverride());

		container = containerQuotaRepository.find(guestDomain, ContainerQuotaType.USER);
		assertEquals(quotaValue, container.getDefaultQuota());
		assertEquals(false, container.getDefaultQuotaOverride());
		assertEquals(quotaValue, container.getQuota());
		assertEquals(false, container.getQuotaOverride());
	}

	@Test
	public void testCascadeDefaultQuotaToQuotaOfChildrenDomains() {
		initialChecks();
		Long count = null;
		ContainerQuota container = null;

		Long quotaValue = 400000000000L;

		container = containerQuotaRepository.find(root.getDomain(), ContainerQuotaType.USER);
		assertEquals(quotaValue, container.getDefaultQuota());
		assertEquals(null, container.getDefaultQuotaOverride());
		assertEquals(quotaValue, container.getQuota());

		container = containerQuotaRepository.find(guestDomain, ContainerQuotaType.USER);
		assertEquals(quotaValue, container.getDefaultQuota());
		assertEquals(false, container.getDefaultQuotaOverride());
		assertEquals(quotaValue, container.getQuota());
		assertEquals(false, container.getQuotaOverride());

		container = containerQuotaRepository.find(topDomain, ContainerQuotaType.USER);
		assertEquals(quotaValue, container.getDefaultQuota());
		assertEquals(false, container.getDefaultQuotaOverride());
		assertEquals(quotaValue, container.getQuota());
		assertEquals(false, container.getQuotaOverride());


		Long newQuotaValue = 8L;
		count = containerQuotaRepository.cascadeDefaultQuotaToQuotaOfChildrenDomains(root.getDomain(), newQuotaValue, ContainerQuotaType.USER);
		// Only one top domain.
		assertEquals(new Long(1), count);

		container = containerQuotaRepository.find(topDomain, ContainerQuotaType.USER);
		assertEquals(quotaValue, container.getDefaultQuota());
		assertEquals(false, container.getDefaultQuotaOverride());
		assertEquals(newQuotaValue, container.getQuota());
		assertEquals(false, container.getQuotaOverride());

		container = containerQuotaRepository.find(guestDomain, ContainerQuotaType.USER);
		assertEquals(quotaValue, container.getDefaultQuota());
		assertEquals(false, container.getDefaultQuotaOverride());
		assertEquals(quotaValue, container.getQuota());
		assertEquals(false, container.getQuotaOverride());
	}

	@Test
	public void testCascadeDefaultQuota() {
		initialChecks();
		Quota quota = null;
		Long count = null;
		ContainerQuota container = null;

		Long quotaValue = 400000000000L;

		container = containerQuotaRepository.find(root.getDomain(), ContainerQuotaType.USER);
		assertEquals(quotaValue, container.getDefaultQuota());
		assertEquals(null, container.getDefaultQuotaOverride());
		assertEquals(quotaValue, container.getQuota());

		assertEquals(false, container.getMaintenance());
		quota = accountQuotaRepository.find(jane);
		assertEquals(false, quota.getMaintenance());
		quota = accountQuotaRepository.find(root);
		assertEquals(false, quota.getMaintenance());
		quota = accountQuotaRepository.find(inconsistent);
		assertEquals(false, quota.getMaintenance());

		Long newQuotaValue = 8L;
		count = containerQuotaRepository.cascadeDefaultQuota(container.getDomain(), newQuotaValue, container.getContainerQuotaType());
		// 2 in topdomain ( 1 quota and 1 defaultquota), 2 in subdomain, 2 in guestdomain 
		assertEquals(new Long(6), count);

		container = containerQuotaRepository.find(guestDomain, ContainerQuotaType.USER);
		assertEquals(newQuotaValue, container.getDefaultQuota());
		assertEquals(false, container.getDefaultQuotaOverride());
		assertEquals(newQuotaValue, container.getQuota());
		assertEquals(false, container.getQuotaOverride());
	}
}
