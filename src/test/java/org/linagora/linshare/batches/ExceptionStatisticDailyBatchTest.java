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
package org.linagora.linshare.batches;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.domain.constants.ExceptionStatisticType;
import org.linagora.linshare.core.domain.constants.ExceptionType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.runner.BatchRunner;
import org.linagora.linshare.mongo.entities.ExceptionStatistic;
import org.linagora.linshare.mongo.repository.ExceptionStatisticMongoRepository;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-mongo-init.xml",
		"classpath:springContext-batches.xml",
		"classpath:springContext-batches-quota-and-statistics.xml",
		"classpath:springContext-test.xml" })
public class ExceptionStatisticDailyBatchTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private BatchRunner batchRunner;

	@Autowired
	@Qualifier("exceptionStatisticDailyBatch")
	private GenericBatch exceptionStatisticDailyBatch;

	@Autowired
	@Qualifier("exceptionStatisticMongoRepository")
	private ExceptionStatisticMongoRepository exceptionStatisticMongoRepository;

	@Autowired
	private AbstractDomainRepository domainRepository;

	private AbstractDomain topDomain;

	private AbstractDomain rootDomain;

	@BeforeEach
	public void setUp() {
		Calendar d = Calendar.getInstance();
		d.add(Calendar.DATE, -1);
		topDomain = domainRepository.findById(LoadingServiceTestDatas.sqlDomain);
		rootDomain = domainRepository.findById(LoadingServiceTestDatas.sqlRootDomain);
		exceptionStatisticMongoRepository.insert(newExceptionStatistic(topDomain, d.getTime()));
		exceptionStatisticMongoRepository.insert(newExceptionStatistic(rootDomain, d.getTime()));
		Calendar date = Calendar.getInstance();
		date.add(Calendar.DATE, -2);
		exceptionStatisticMongoRepository.insert(newExceptionStatistic(topDomain, date.getTime()));
	}

	@Test
	public void test() {
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(exceptionStatisticDailyBatch);
		Assertions.assertTrue(batchRunner.execute(batches), "At least one batch failed.");
	}

	private ExceptionStatistic newExceptionStatistic(AbstractDomain domain, Date time) {
		ExceptionStatistic exceptionStatistic = new ExceptionStatistic();
		exceptionStatistic.setValue(1L);
		exceptionStatistic.setCreationDate(time);
		exceptionStatistic.setDomainUuid(domain.getUuid());
		exceptionStatistic.setParentDomainUuid(domain.getParentDomain() != null ? domain.getParentDomain().getUuid() : null);
		exceptionStatistic.setErrorCode(null);
		exceptionStatistic.setStackTrace(null);
		exceptionStatistic.setExceptionType(ExceptionType.BUSINESS_EXCEPTION);
		exceptionStatistic.setType(ExceptionStatisticType.ONESHOT);
		exceptionStatistic.setUuid(UUID.randomUUID().toString());
		return exceptionStatistic;
	}
}
