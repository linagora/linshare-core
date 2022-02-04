/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
import org.linagora.linshare.mongo.repository.AuditUserMongoRepository;
import org.linagora.linshare.mongo.repository.BasicStatisticMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

@DirtiesContext
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
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-mongo-init.xml",
		"classpath:springContext-batches-quota-and-statistics.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml" })
// TODO: Revamp the whole test. Dirty test :(
public class BasicStatisticDailyBatchTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("basicStatisticDailyBatch")
	private GenericBatch basicStatisticDailyBatch;

	@Autowired
	@Qualifier("basicStatisticMongoRepository")
	private BasicStatisticMongoRepository basicStatisticMongoRepository;

	@Autowired
	@Qualifier("auditUserMongoRepository")
	private AuditUserMongoRepository auditUserMongoRepository;

	@Autowired
	private AbstractDomainRepository domainRepository;

	private String topDomainUuid;

	private String rootDomainUuid = "LinShareRootDomain";

	@BeforeEach
	public void setUp() {
		basicStatisticMongoRepository.deleteAll();
		auditUserMongoRepository.deleteAll();
		Calendar d = Calendar.getInstance();
		d.add(Calendar.DATE, -1);
		// is it a good idea to create a topDomain like that ?
		AbstractDomain topDomain = domainRepository
				.create(new TopDomain("topDomain", (RootDomain) domainRepository.findById("LinShareRootDomain")));
		topDomainUuid = topDomain.getUuid();

		// Adding 3 traces for CONTACTS_LISTS_CONTACTS.CREATE for LinShareRootDomain
		basicStatisticMongoRepository.insert(new BasicStatistic(1L, rootDomainUuid, null, LogAction.CREATE,
				d.getTime(), AuditLogEntryType.CONTACTS_LISTS_CONTACTS, BasicStatisticType.ONESHOT));
		basicStatisticMongoRepository.insert(new BasicStatistic(1L, rootDomainUuid, null, LogAction.CREATE,
				d.getTime(), AuditLogEntryType.CONTACTS_LISTS_CONTACTS, BasicStatisticType.ONESHOT));
		basicStatisticMongoRepository.insert(new BasicStatistic(1L, rootDomainUuid, null, LogAction.CREATE,
				d.getTime(), AuditLogEntryType.CONTACTS_LISTS_CONTACTS, BasicStatisticType.ONESHOT));

		// Adding 3 traces for CONTACTS_LISTS_CONTACTS.CREATE for topDomain
		basicStatisticMongoRepository.insert(new BasicStatistic(1L, topDomainUuid, rootDomainUuid,
				LogAction.CREATE, d.getTime(), AuditLogEntryType.CONTACTS_LISTS_CONTACTS, BasicStatisticType.ONESHOT));
		basicStatisticMongoRepository.insert(new BasicStatistic(1L, topDomainUuid, rootDomainUuid,
				LogAction.CREATE, d.getTime(), AuditLogEntryType.CONTACTS_LISTS_CONTACTS, BasicStatisticType.ONESHOT));
		basicStatisticMongoRepository.insert(new BasicStatistic(1L, topDomainUuid, rootDomainUuid,
				LogAction.CREATE, d.getTime(), AuditLogEntryType.CONTACTS_LISTS_CONTACTS, BasicStatisticType.ONESHOT));

		// Adding 2 traces for WORKGROUP_DOCUMENT.CREATE for topDomain
		basicStatisticMongoRepository.insert(new BasicStatistic(1L, topDomainUuid, rootDomainUuid,
				LogAction.CREATE, d.getTime(), AuditLogEntryType.WORKGROUP_DOCUMENT, BasicStatisticType.ONESHOT));
		basicStatisticMongoRepository.insert(new BasicStatistic(1L, topDomainUuid, rootDomainUuid,
				LogAction.CREATE, d.getTime(), AuditLogEntryType.WORKGROUP_DOCUMENT, BasicStatisticType.ONESHOT));
	}

	@Test
	public void test() {
		// Expected result:
		// - 1 Daily trace  for LinShareRootDomain : CONTACTS_LISTS_CONTACTS (3 hits)
		// - 2 Daily traces for topDomain : CONTACTS_LISTS_CONTACTS (3 hits) and WORKGROUP_DOCUMENT (2 hits)
		//
		String currentDomain = "LinShareRootDomain";
		BatchRunContext batchRunContext = new BatchRunContext();
		List<String> listIdentifier = basicStatisticDailyBatch.getAll(batchRunContext);
		for (String identifier : listIdentifier) {
			logger.debug("listIdentifier: {}", identifier);
		}
		// LinShareRootDomain and topDomain
		int domainsCount = listIdentifier.size();
		assertEquals(2, domainsCount);

		basicStatisticDailyBatch.execute(batchRunContext, currentDomain, domainsCount, 0);

		Calendar d = Calendar.getInstance();
		d.add(Calendar.DATE, -2);
		Set<BasicStatistic> listDailyBasicStatistics = basicStatisticMongoRepository.findBetweenTwoDates(
				currentDomain, Lists.newArrayList(LogAction.class.getEnumConstants()), d.getTime(), new Date(),
				Lists.newArrayList(AuditLogEntryType.class.getEnumConstants()), BasicStatisticType.DAILY);
		assertEquals(1, listDailyBasicStatistics.size());
		BasicStatistic firstAndUnique = listDailyBasicStatistics.iterator().next();
		assertEquals(3, (long) firstAndUnique.getValue());

		Set<BasicStatistic> DailyBasicStatistic = basicStatisticMongoRepository.findBetweenTwoDates(
				currentDomain, Lists.newArrayList(LogAction.CREATE), d.getTime(), new Date(),
				Lists.newArrayList(AuditLogEntryType.CONTACTS_LISTS_CONTACTS), BasicStatisticType.DAILY);
		assertEquals(3, (long) Lists.newArrayList(DailyBasicStatistic).get(0).getValue());

		basicStatisticDailyBatch.execute(batchRunContext, topDomainUuid, domainsCount, 0);
		Set<BasicStatistic> listBasicStatistics = basicStatisticMongoRepository.findBetweenTwoDates(
				topDomainUuid, Lists.newArrayList(LogAction.class.getEnumConstants()), d.getTime(), new Date(),
				Lists.newArrayList(AuditLogEntryType.class.getEnumConstants()), BasicStatisticType.DAILY);
		assertEquals(2, listBasicStatistics.size());

		Set<BasicStatistic> basicStatisticContactList = basicStatisticMongoRepository.findBetweenTwoDates(
				topDomainUuid, Lists.newArrayList(LogAction.CREATE), d.getTime(), new Date(),
				Lists.newArrayList(AuditLogEntryType.CONTACTS_LISTS_CONTACTS), BasicStatisticType.DAILY);
		firstAndUnique = basicStatisticContactList.iterator().next();
		assertEquals(3, (long) firstAndUnique.getValue());

		Set<BasicStatistic> basicStatisticWorkGroup = basicStatisticMongoRepository.findBetweenTwoDates(
				topDomainUuid, Lists.newArrayList(LogAction.CREATE), d.getTime(), new Date(),
				Lists.newArrayList(AuditLogEntryType.WORKGROUP_DOCUMENT), BasicStatisticType.DAILY);
		firstAndUnique = basicStatisticWorkGroup.iterator().next();
		assertEquals(2, (long) firstAndUnique.getValue());
	}
}
