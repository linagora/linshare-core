/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */

package org.linagora.linshare.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.ExceptionStatisticType;
import org.linagora.linshare.core.domain.constants.ExceptionType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.mongo.entities.ExceptionStatistic;
import org.linagora.linshare.mongo.repository.ExceptionStatisticMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.collect.Lists;

@ExtendWith(SpringExtension.class)
@Sql({
	"/import-tests-account.sql"})
@Transactional
@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-mongo-java-server.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-test.xml" })
public class ExceptionStatisticserviceImplTest {

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	@Qualifier("exceptionStatisticMongoRepository")
	private ExceptionStatisticMongoRepository exceptionStatisticMongoRepository;

	@Autowired
	private AbstractDomainRepository domainRepository;

	private AbstractDomain topDomain;

	private AbstractDomain rootDomain;

	@Test
	public void findBetweenTwoDates() {
		Calendar d = Calendar.getInstance();
		d.add(Calendar.DATE, -1);
		topDomain = domainRepository.findById(LoadingServiceTestDatas.sqlDomain);
		rootDomain = domainRepository.findById(LoadingServiceTestDatas.sqlRootDomain);
		exceptionStatisticMongoRepository.insert(newExceptionStatistic(topDomain, d.getTime()));
		exceptionStatisticMongoRepository.insert(newExceptionStatistic(rootDomain, d.getTime()));
		Calendar date = Calendar.getInstance();
		date.add(Calendar.DATE, -2);
		exceptionStatisticMongoRepository.insert(newExceptionStatistic(topDomain, date.getTime()));
		List<ExceptionType> exceptionTypes = Lists.newArrayList();
		exceptionTypes.add(ExceptionType.BUSINESS_EXCEPTION);
		// findBetween two dates
		Calendar beginDate = Calendar.getInstance();
		beginDate.add(Calendar.DATE, -3);
		Set<ExceptionStatistic> exceptionStatistics = exceptionStatisticMongoRepository.findBetweenTwoDates(topDomain.getUuid(), exceptionTypes, beginDate.getTime(),
				new Date(), ExceptionStatisticType.DAILY);
		Assertions.assertEquals(2, exceptionStatistics.size());
	}

	private ExceptionStatistic newExceptionStatistic(AbstractDomain domain, Date time) {
		ExceptionStatistic exceptionStatistic = new ExceptionStatistic();
		exceptionStatistic.setValue(2L);
		exceptionStatistic.setCreationDate(time);
		exceptionStatistic.setDomainUuid(domain.getUuid());
		exceptionStatistic.setParentDomainUuid(domain.getParentDomain() != null ? domain.getParentDomain().getUuid() : null);
		exceptionStatistic.setErrorCode(null);
		exceptionStatistic.setStackTrace(null);
		exceptionStatistic.setExceptionType(ExceptionType.BUSINESS_EXCEPTION);
		exceptionStatistic.setType(ExceptionStatisticType.DAILY);
		exceptionStatistic.setUuid(UUID.randomUUID().toString());
		return exceptionStatistic;
	}

}
