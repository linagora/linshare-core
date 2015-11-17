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
package org.linagora.linshare.service;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.domain.constants.EnsembleType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.QuotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = { "classpath:springContext-datasource.xml", "classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml", "classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml", "classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml", "classpath:springContext-startopendj.xml",
		"classpath:springContext-jackRabbit-mock.xml", "classpath:springContext-test.xml",
		"classpath:springContext-quota-manager.xml", "classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml" })
public class QuotaServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	QuotaService quotaService;

	@Autowired
	OperationHistoryBusinessService operationHistoryBusinessService;

	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	LoadingServiceTestDatas dates;
	private User jane;

	@Before
	public void setUp() {
		this.executeSqlScript("import-tests-quota.sql", false);
		this.executeSqlScript("import-tests-operationHistory.sql", false);
		dates = new LoadingServiceTestDatas(userRepository);
		dates.loadUsers();
		jane = dates.getUser2();
	}

	@Test
	public void test() {
		Account account = jane;
		Account systemAccount = accountRepository.getBatchSystemAccount();
		AbstractDomain domain = jane.getDomain();

		Long accountConsumptionOfDay = operationHistoryBusinessService.sumOperationValue(account, null, new Date(), null, null);
		Long domainConsumptionOfDay = operationHistoryBusinessService.sumOperationValue(null, domain, new Date(), null, null);
		Long ensembleUserConsumptionOfDay = operationHistoryBusinessService.sumOperationValue(null, domain, new Date(), null, EnsembleType.USER);
		Long platformConsumptionOfDay = operationHistoryBusinessService.sumOperationValue(null, null, new Date(), null, null);
		assertEquals(700, (long) accountConsumptionOfDay);
		assertEquals(800, (long) domainConsumptionOfDay);
		assertEquals(800, (long) ensembleUserConsumptionOfDay);
		assertEquals(1200, (long) platformConsumptionOfDay);
		quotaService.checkIfUserCanAddFile(systemAccount, account, (long) 4, EnsembleType.USER);
	}
}
