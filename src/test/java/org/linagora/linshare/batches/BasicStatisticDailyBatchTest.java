/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
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
package org.linagora.linshare.batches;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.BasicStatisticType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.RootDomain;
import org.linagora.linshare.core.domain.entities.TopDomain;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.mongo.entities.BasicStatistic;
import org.linagora.linshare.mongo.repository.BasicStatisticMongoRepository;
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

import com.google.common.collect.Lists;

@ExtendWith(SpringExtension.class)
@Transactional
@Sql({ 
	"/import-tests-document-entry-setup.sql" })
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml", 
		"classpath:springContext-mongo-java-server.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-batches-quota-and-statistics.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml" })
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class BasicStatisticDailyBatchTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("basicStatisticDailyBatch")
	private GenericBatch basicStatisticDailyBatch;

	@Autowired
	@Qualifier("basicStatisticMongoRepository")
	private BasicStatisticMongoRepository basicStatisticMongoRepository;

	@Autowired
	private AbstractDomainRepository domainRepository;

	@BeforeEach
	public void setUp() {
		Calendar d = Calendar.getInstance();
		d.add(Calendar.DATE, -1);
		AbstractDomain topDomain = domainRepository
				.create(new TopDomain("topDomain", (RootDomain) domainRepository.findById("LinShareRootDomain")));
		basicStatisticMongoRepository.insert(new BasicStatistic(1L, "LinShareRootDomain", null, LogAction.CREATE,
				d.getTime(), AuditLogEntryType.CONTACTS_LISTS_CONTACTS, BasicStatisticType.ONESHOT));
		basicStatisticMongoRepository.insert(new BasicStatistic(1L, "LinShareRootDomain", null, LogAction.CREATE,
				d.getTime(), AuditLogEntryType.CONTACTS_LISTS_CONTACTS, BasicStatisticType.ONESHOT));
		basicStatisticMongoRepository.insert(new BasicStatistic(1L, "LinShareRootDomain", null, LogAction.CREATE,
				d.getTime(), AuditLogEntryType.CONTACTS_LISTS_CONTACTS, BasicStatisticType.ONESHOT));
		basicStatisticMongoRepository.insert(new BasicStatistic(1L, topDomain.getUuid(), "LinShareRootDomain",
				LogAction.CREATE, d.getTime(), AuditLogEntryType.CONTACTS_LISTS_CONTACTS, BasicStatisticType.ONESHOT));
		basicStatisticMongoRepository.insert(new BasicStatistic(1L, topDomain.getUuid(), "LinShareRootDomain",
				LogAction.CREATE, d.getTime(), AuditLogEntryType.CONTACTS_LISTS_CONTACTS, BasicStatisticType.ONESHOT));
		basicStatisticMongoRepository.insert(new BasicStatistic(1L, topDomain.getUuid(), "LinShareRootDomain",
				LogAction.CREATE, d.getTime(), AuditLogEntryType.WORKGROUP_DOCUMENT, BasicStatisticType.ONESHOT));
		basicStatisticMongoRepository.insert(new BasicStatistic(1L, topDomain.getUuid(), "LinShareRootDomain",
				LogAction.CREATE, d.getTime(), AuditLogEntryType.WORKGROUP_DOCUMENT, BasicStatisticType.ONESHOT));
		basicStatisticMongoRepository.insert(new BasicStatistic(1L, topDomain.getUuid(), "LinShareRootDomain",
				LogAction.CREATE, d.getTime(), AuditLogEntryType.CONTACTS_LISTS_CONTACTS, BasicStatisticType.ONESHOT));
	}

	@Test
	public void test() {
		BatchRunContext batchRunContext = new BatchRunContext();
		List<String> listIdentifier = basicStatisticDailyBatch.getAll(batchRunContext);
		assertEquals(2, listIdentifier.size());

		basicStatisticDailyBatch.execute(batchRunContext, listIdentifier.get(0), listIdentifier.size(), 0);

		Calendar d = Calendar.getInstance();
		d.add(Calendar.DATE, -2);
		Set<BasicStatistic> listDailyBasicStatistics = basicStatisticMongoRepository.findBetweenTwoDates(
				listIdentifier.get(0), Lists.newArrayList(LogAction.class.getEnumConstants()), d.getTime(), new Date(),
				Lists.newArrayList(AuditLogEntryType.class.getEnumConstants()), BasicStatisticType.DAILY);
		assertEquals(1, listDailyBasicStatistics.size());
		assertEquals(3, (long) Lists.newArrayList(listDailyBasicStatistics).get(0).getValue());
		Set<BasicStatistic> DailyBasicStatistic = basicStatisticMongoRepository.findBetweenTwoDates(
				listIdentifier.get(0), Lists.newArrayList(LogAction.CREATE), d.getTime(), new Date(),
				Lists.newArrayList(AuditLogEntryType.CONTACTS_LISTS_CONTACTS), BasicStatisticType.DAILY);
		assertEquals(3, (long) Lists.newArrayList(DailyBasicStatistic).get(0).getValue());
		basicStatisticDailyBatch.execute(batchRunContext, listIdentifier.get(1), listIdentifier.size(), 0);
		Set<BasicStatistic> listBasicStatistics = basicStatisticMongoRepository.findBetweenTwoDates(
				listIdentifier.get(1), Lists.newArrayList(LogAction.class.getEnumConstants()), d.getTime(), new Date(),
				Lists.newArrayList(AuditLogEntryType.class.getEnumConstants()), BasicStatisticType.DAILY);
		assertEquals(2, listBasicStatistics.size());
		Set<BasicStatistic> basicStatisticContactList = basicStatisticMongoRepository.findBetweenTwoDates(
				listIdentifier.get(1), Lists.newArrayList(LogAction.CREATE), d.getTime(), new Date(),
				Lists.newArrayList(AuditLogEntryType.CONTACTS_LISTS_CONTACTS), BasicStatisticType.DAILY);
		assertEquals(3, (long) Lists.newArrayList(basicStatisticContactList).get(0).getValue());
		Set<BasicStatistic> basicStatisticWorkGroup = basicStatisticMongoRepository.findBetweenTwoDates(
				listIdentifier.get(1), Lists.newArrayList(LogAction.CREATE), d.getTime(), new Date(),
				Lists.newArrayList(AuditLogEntryType.WORKGROUP_DOCUMENT), BasicStatisticType.DAILY);
		assertEquals(2, (long) Lists.newArrayList(basicStatisticWorkGroup).get(0).getValue());
	}
}
