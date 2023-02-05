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
package org.linagora.linshare.repository.hibernate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.QuotaType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Quota;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountQuotaRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.repository.hibernate.DomainQuotaRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@Sql({
	"/import-tests-domain-quota-updates.sql" })
@Transactional
@ContextConfiguration(locations = { "classpath:springContext-test.xml", 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml" })
//Use dirties context to reset the H2 database because of quota alteration 
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class DomainQuotaRepositoryImplTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

	@Autowired
	private AccountQuotaRepository accountQuotaRepository;

	@Autowired
	private DomainQuotaRepositoryImpl domainQuotaRepository;

	@Autowired
	private AbstractDomainRepository domainRepository;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	private User jane;

	private User root;

	private AbstractDomain guestDomain;

	private AbstractDomain topDomain;

	@BeforeEach
	public void setUp() {
		jane = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
		root = userRepository.findByMailAndDomain(LinShareTestConstants.ROOT_DOMAIN, LinShareTestConstants.ROOT_ACCOUNT);
		guestDomain = domainRepository.findById(LinShareTestConstants.GUEST_DOMAIN);
		topDomain = domainRepository.findById(LinShareTestConstants.TOP_DOMAIN);
	}

	@Test
	public void testCascadeMaintenanceMode() {
		Long count = domainQuotaRepository.cascadeMaintenanceMode(root.getDomain(), true);
		// LinShareRootDomain : 1 domain, 2 containers, 1 account
		// MyDomain : 1 domain, 2 containers, 5 accounts (3 users (1 inconsistent from import-tests-domain-quota-updates.sql) & 2 workgroups)
		// subomains: GuestDomain (1 domain and 2 containers), MySubDomain(1 domain, 2
		// containers, 1 account)
		assertEquals(Long.valueOf(23), count);
		Quota quota = accountQuotaRepository.find(jane);
		assertEquals(true, quota.getMaintenance());
		quota = accountQuotaRepository.find(root);
		assertEquals(true, quota.getMaintenance());
	}

	@Test
	public void testCascadeMaintenanceToSubdomains() {
		AbstractDomain myDomain = jane.getDomain();
		Long count = domainQuotaRepository.cascadeMaintenanceMode(myDomain, true);
		// MyDomain : 1 domain, 2 containers, 5 accounts ( 3 users & 2 workgroups)
		// subdomains: GuestDomain 1 domain and 2 containers, MySubDomain 1 domain, 2 containers, 1 account)
		assertEquals(Long.valueOf(15), count);
		Quota quota = accountQuotaRepository.find(jane);
		assertEquals(true, quota.getMaintenance());
		quota = accountQuotaRepository.find(root);
		assertEquals(false, quota.getMaintenance());
	}

	@Test
	public void testCascadeDefaultQuotaToSubDomainsDefaultQuota() {
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
		assertEquals(Long.valueOf(2), count);

		quota = domainQuotaRepository.find(guestDomain);
		assertEquals(newQuotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(quotaValue, quota.getQuota());
	}

	@Test
	public void testCascadeDefaultQuotaToSubDomainsQuota() {
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
		assertEquals(Long.valueOf(2), count);

		quota = domainQuotaRepository.find(guestDomain);
		assertEquals(quotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(newQuotaValue, quota.getQuota());

	}

	@Test
	public void testCascadeDefaultQuotaToDefaultQuotaOfChildrenDomains() {
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
		assertEquals(Long.valueOf(2), count);

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
		Quota quota = null;
		Long count = null;

		Long quotaValue = 1000000000000L;

		quota = domainQuotaRepository.find(guestDomain);
		assertEquals(quotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(quotaValue, quota.getQuota());

		Long newQuotaValue = 8L;
		count = domainQuotaRepository.cascadeDefaultQuotaToQuotaOfChildrenDomains(root.getDomain(), newQuotaValue);
		assertEquals(Long.valueOf(2), count);

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
		Quota quota = null;
		Long count = null;

		Long quotaValue = 1000000000000L;

		quota = domainQuotaRepository.find(guestDomain);
		assertEquals(quotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(quotaValue, quota.getQuota());

		Long newQuotaValue = 8L;
		count = domainQuotaRepository.cascadeDefaultQuotaToQuotaOfChildrenDomains(jane.getDomain(), newQuotaValue);
		assertEquals(Long.valueOf(2), count);

		quota = domainQuotaRepository.find(guestDomain);
		assertEquals(quotaValue, quota.getDefaultQuota());
		assertEquals(false, quota.getDefaultQuotaOverride());
		assertEquals(newQuotaValue, quota.getQuota());
	}

	@Test
	public void testCascadeDefaultQuotaToDefaultQuotaOfChildrenDomains2() {
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
		assertEquals(Long.valueOf(2), count);

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
		assertEquals(Long.valueOf(8), count);

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
