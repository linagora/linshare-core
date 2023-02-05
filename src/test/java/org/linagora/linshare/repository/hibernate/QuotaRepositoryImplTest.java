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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.domain.entities.Quota;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.AccountQuotaRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.ContainerQuotaRepository;
import org.linagora.linshare.core.repository.DomainQuotaRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@Transactional
@Sql({
	
	"/import-tests-stat.sql",
	"/import-tests-operationHistory.sql",
	"/import-tests-quota.sql" })
@ContextConfiguration(locations = {
		"classpath:springContext-test.xml",
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})

public class QuotaRepositoryImplTest
		{

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

	@Autowired
	private AccountQuotaRepository accountQuotaRepository;

	@Autowired
	private DomainQuotaRepository domainQuotaRepository;

	@Autowired
	private ContainerQuotaRepository ensembleQuotaRepository;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	private User jane;

	@BeforeEach
	public void setUp(){
		jane = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
	}

	@Test
	public void test() { 
		AbstractDomain domain2 = jane.getDomain();
		AbstractDomain domain1 = domain2.getParentDomain();
		Account account1 = jane;
		Quota result = accountQuotaRepository.find(account1);
		assertNotNull(result);
		DomainQuota domain2Quota = domainQuotaRepository.find(domain2);
		ContainerQuota entity = new ContainerQuota(domain2, domain1, domain2Quota, 100, 90, 10, 40, 20, ContainerQuotaType.WORK_GROUP);
		ensembleQuotaRepository.create(entity);
		Quota result3 = domainQuotaRepository.find(domain2);
		assertEquals(Long.valueOf(1096), result3.getCurrentValue());
		assertEquals(Long.valueOf(1900), result3.getQuota());
		result3 = domainQuotaRepository.find(domain1);
		assertEquals(Long.valueOf(1096), result3.getCurrentValue());
		assertEquals(Long.valueOf(100), result3.getLastValue());
		assertEquals(Long.valueOf(2300), result3.getQuota());
		assertEquals(Long.valueOf(2000), result3.getQuotaWarning());
		List<String> listIdentifier = accountQuotaRepository.findDomainUuidByBatchModificationDate(yesterday());
		assertEquals(2, listIdentifier.size());
		logger.debug(" domain identifier : "+listIdentifier.get(0));
	}

	private Date yesterday() {
		GregorianCalendar dateCalender = new GregorianCalendar();
		dateCalender.add(GregorianCalendar.DATE, -1);
		dateCalender.set(GregorianCalendar.HOUR_OF_DAY, 23);
		dateCalender.set(GregorianCalendar.MINUTE, 59);
		dateCalender.set(GregorianCalendar.SECOND, 59);
		return dateCalender.getTime();
	}

}
