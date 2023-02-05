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


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.ContainerQuotaBusinessService;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
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
@Sql({"/import-tests-domain-quota-updates.sql"})
@Transactional
//Use dirties context to reset the H2 database because of quota alteration 
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class ContainerQuotaBusinessServiceImplTest {
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

	private AbstractDomain guestDomain;

	private AbstractDomain topDomain;

	private AbstractDomain rootDomain;

	private User jane;

	public ContainerQuotaBusinessServiceImplTest() {
		super();
	}

	@BeforeEach
	public void setUp() {
		jane = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
		guestDomain = domainRepository.findById(LinShareTestConstants.GUEST_DOMAIN);
		topDomain = domainRepository.findById(LinShareTestConstants.TOP_DOMAIN);
		rootDomain = domainRepository.findById(LinShareTestConstants.ROOT_DOMAIN);
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
		Assertions.assertEquals(quotaValue, quota.getDefaultQuota());
		Assertions.assertEquals(false, quota.getDefaultQuotaOverride());
		Assertions.assertEquals(quotaValue, quota.getQuota());

		quota = businessService.find(topDomain, ContainerQuotaType.USER);
		Assertions.assertEquals(quotaValue, quota.getDefaultQuota());
		Assertions.assertEquals(true, quota.getDefaultQuotaOverride());
		Assertions.assertEquals(quotaValue, quota.getQuota());
		Assertions.assertEquals(true, quota.getQuotaOverride());
		Assertions.assertEquals(quotaValue, quota.getDefaultMaxFileSize());
		Assertions.assertEquals(true, quota.getDefaultMaxFileSizeOverride());
		Assertions.assertEquals(quotaValue, quota.getMaxFileSize());
		Assertions.assertEquals(true, quota.getMaxFileSizeOverride());
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
		Assertions.assertEquals(quotaValue, quota.getMaxFileSize());
		Assertions.assertEquals(true, quota.getMaxFileSizeOverride());

		quota = businessService.find(guestDomain, ContainerQuotaType.USER);
		Assertions.assertEquals(originalQuotaValue, quota.getMaxFileSize());
		Assertions.assertEquals(false, quota.getMaxFileSizeOverride());

		AccountQuota aq = accountBusinessService.find(jane);
		Assertions.assertEquals(quotaValue, aq.getMaxFileSize());
		Assertions.assertEquals(false, aq.getMaxFileSizeOverride());
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
		Assertions.assertEquals(quotaValue, quota.getDefaultMaxFileSize());
		Assertions.assertEquals(true, quota.getDefaultMaxFileSizeOverride());

		quota = businessService.find(guestDomain, ContainerQuotaType.USER);
		Assertions.assertEquals(quotaValue, quota.getDefaultMaxFileSize());
		Assertions.assertEquals(false, quota.getDefaultMaxFileSizeOverride());

		// Jane is in topdomain, so she is not impacted by defaultMaxFileSize modification.
		AccountQuota aq = accountBusinessService.find(jane);
		Assertions.assertEquals(originalQuotaValue, aq.getMaxFileSize());
		Assertions.assertEquals(false, aq.getMaxFileSizeOverride());
	}

	@Test
	public void testCascadeAccountQuota() {
		Long expectedQuotaValue = Long.valueOf(698);

		ContainerQuota containerQuota = businessService.find(topDomain, ContainerQuotaType.USER);
		ContainerQuota containerQuotaDto = new ContainerQuota(containerQuota);

		containerQuotaDto.setAccountQuotaOverride(true);
		containerQuotaDto.setAccountQuota(expectedQuotaValue);

		businessService.update(containerQuota, containerQuotaDto);

		containerQuota = businessService.find(topDomain, ContainerQuotaType.USER);
		Assertions.assertEquals(expectedQuotaValue, containerQuota.getAccountQuota());
		Assertions.assertEquals(true, containerQuota.getAccountQuotaOverride());

		AccountQuota accountQuotaJane = accountBusinessService.find(jane);
		Assertions.assertEquals(expectedQuotaValue, accountQuotaJane.getQuota());
		Assertions.assertEquals(false, accountQuotaJane.getQuotaOverride());

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
		Assertions.assertEquals(quotaValue, quota.getDefaultAccountQuota());
		Assertions.assertEquals(false, quota.getDefaultAccountQuotaOverride());

		AccountQuota aq = accountBusinessService.find(jane);
		Assertions.assertEquals(quotaValue, aq.getQuota());
		Assertions.assertEquals(false, aq.getQuotaOverride());

	}

	@Test
	public void testCascadeDefaultQuotaForRoot() {
		Long quotaValue = 698L;

		ContainerQuota quota = businessService.find(topDomain.getParentDomain(), ContainerQuotaType.USER);

		Assertions.assertEquals(null, quota.getQuotaOverride());
		Assertions.assertEquals(null, quota.getDefaultQuotaOverride());
		Assertions.assertEquals(null, quota.getDefaultMaxFileSizeOverride());
		Assertions.assertEquals(null, quota.getDefaultAccountQuotaOverride());

		ContainerQuota dto = new ContainerQuota(quota);
		dto.setQuota(quotaValue);
		dto.setDefaultQuota(quotaValue);
		// dto.setDefaultMaxFileSize(quotaValue);

		businessService.update(quota, dto);

		quota = businessService.find(guestDomain, ContainerQuotaType.USER);
		Assertions.assertEquals(quotaValue, quota.getDefaultQuota());
		Assertions.assertEquals(false, quota.getDefaultQuotaOverride());
		Assertions.assertEquals(quotaValue, quota.getQuota());

		quota = businessService.find(topDomain, ContainerQuotaType.USER);
		Assertions.assertEquals(quotaValue, quota.getDefaultQuota());
		Assertions.assertEquals(false, quota.getDefaultQuotaOverride());
		Assertions.assertEquals(quotaValue, quota.getQuota());
		Assertions.assertEquals(false, quota.getQuotaOverride());
		// assertEquals(quotaValue, quota.getDefaultMaxFileSize());
		// assertEquals(true, quota.getDefaultMaxFileSizeOverride());
	}
}
