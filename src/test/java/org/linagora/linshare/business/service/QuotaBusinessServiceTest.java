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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.ContainerQuotaBusinessService;
import org.linagora.linshare.core.business.service.DomainQuotaBusinessService;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml" })
@Sql({"/import-tests-stat.sql","/import-tests-operationHistory.sql","/import-tests-quota.sql"})
@Transactional
public class QuotaBusinessServiceTest {

	private static Logger logger = LoggerFactory.getLogger(DocumentEntryBusinessServiceImplTest.class);

	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

	@Autowired
	private DomainQuotaBusinessService domainQuotaBusinessService;

	@Autowired
	private AccountQuotaBusinessService accountQuotaBusinessService;

	@Autowired
	private ContainerQuotaBusinessService ensembleQuotaBusinessService;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	LoadingServiceTestDatas datas;
	private User jane;

	@BeforeEach
	public void setUp() {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);

		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		jane = datas.getUser2();
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@Test
	public void test() {
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		Account account = jane;
		AbstractDomain domain = jane.getDomain();

		DomainQuota domainQuota = domainQuotaBusinessService.find(domain);
		Assertions.assertNotNull(domainQuota);
		Assertions.assertEquals(1096, (long) domainQuota.getCurrentValue());
		Assertions.assertEquals(500, (long) domainQuota.getLastValue());
		Assertions.assertEquals(1900, (long) domainQuota.getQuota());
		Assertions.assertEquals(1800, (long) domainQuota.getQuotaWarning());
		domainQuotaBusinessService.sumOfCurrentValue(domainQuota);
		domainQuota = domainQuotaBusinessService.find(domain);
		Assertions.assertEquals(1396, (long) domainQuota.getCurrentValue());
		Assertions.assertEquals(1096, (long) domainQuota.getLastValue());

		ContainerQuota ensembleQuota = ensembleQuotaBusinessService.find(domain, ContainerQuotaType.USER);
		Assertions.assertNotNull(ensembleQuota);
		Assertions.assertEquals(496, (long) ensembleQuota.getCurrentValue());
		Assertions.assertEquals(0, (long) ensembleQuota.getLastValue());
		Assertions.assertEquals(1900, (long) ensembleQuota.getQuota());
		Assertions.assertEquals(1300, (long) ensembleQuota.getQuotaWarning());
		Assertions.assertEquals(5, (long) ensembleQuota.getDefaultMaxFileSize());
		ensembleQuotaBusinessService.sumOfCurrentValue(ensembleQuota);
		ensembleQuota = ensembleQuotaBusinessService.find(domain, ContainerQuotaType.USER);
		Assertions.assertEquals(1700, (long) ensembleQuota.getCurrentValue());
		Assertions.assertEquals(496, (long) ensembleQuota.getLastValue());

		ContainerQuota threadEnsembleQuota = ensembleQuotaBusinessService.find(domain, ContainerQuotaType.WORK_GROUP);
		Assertions.assertNotNull(threadEnsembleQuota);
		Assertions.assertEquals(900, (long) threadEnsembleQuota.getCurrentValue());
		Assertions.assertEquals(200, (long) threadEnsembleQuota.getLastValue());
		Assertions.assertEquals(2000, (long) threadEnsembleQuota.getQuota());
		Assertions.assertEquals(1500, (long) threadEnsembleQuota.getQuotaWarning());
		Assertions.assertEquals(5, (long) threadEnsembleQuota.getDefaultMaxFileSize());
		ensembleQuotaBusinessService.sumOfCurrentValue(threadEnsembleQuota);
		threadEnsembleQuota = ensembleQuotaBusinessService.find(domain, ContainerQuotaType.WORK_GROUP);
		Assertions.assertEquals(1200, (long) threadEnsembleQuota.getCurrentValue());
		Assertions.assertEquals(900, (long) threadEnsembleQuota.getLastValue());

		AccountQuota qo = accountQuotaBusinessService.find(account);
		Assertions.assertNotNull(qo);
		Assertions.assertEquals(800, (long) qo.getCurrentValue());
		Assertions.assertEquals(0, (long) qo.getLastValue());
		Assertions.assertEquals(1600, (long) qo.getQuota());
		Assertions.assertEquals(1480, (long) qo.getQuotaWarning());
		Assertions.assertEquals(5, (long) qo.getMaxFileSize());
		accountQuotaBusinessService.createOrUpdate(account, new GregorianCalendar(2042, 10, 11, 00, 00).getTime());
		qo = accountQuotaBusinessService.find(account);
		Assertions.assertNotNull(qo);
		Assertions.assertEquals(1700, (long) qo.getCurrentValue());
		Assertions.assertEquals(800, (long) qo.getLastValue());

		List<String> listDomain = accountQuotaBusinessService.findDomainUuidByBatchModificationDate(yesterday());
		Assertions.assertEquals(1, listDomain.size());
		logger.debug(LinShareTestConstants.END_TEST);
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
