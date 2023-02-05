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

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.batches.impl.gdpr.GDPRConstants;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UploadRequestRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.runner.BatchRunner;
import org.linagora.linshare.core.service.TimeService;
import org.linagora.linshare.core.service.UploadRequestService;
import org.linagora.linshare.mongo.entities.logs.UploadRequestAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.UploadRequestGroupAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.UploadRequestUrlAuditLogEntry;
import org.linagora.linshare.utils.TestingTimeService;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;


@ExtendWith({ SpringExtension.class})
@Transactional
@Sql({ "/import-tests-upload-request-delete-recipients.sql" })
@ContextConfiguration(locations = {
	"classpath:springContext-datasource.xml",
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
	"classpath:springContext-test.xml",
	"classpath:springContext-overriding.xml" })
public class GDPRUploadRequestBatchImplTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private static final String UPLOAD_REQUEST_UUID = "f447ac1c-ef45-11e5-a73f-4b811b25f11b";

	@Autowired
	@Qualifier("GDPRUploadRequestBatch")
	private GenericBatch testee;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	private BatchRunner batchRunner;

	@Autowired
	@Qualifier("timeService")
	private TimeService timeService;

	@Autowired
	@Qualifier("uploadRequestRepository")
	private UploadRequestRepository uploadRequestRepository;

	@Autowired
	@Qualifier("uploadRequestService")
	private UploadRequestService uploadRequestService;

	@Autowired
	@Qualifier("mongoTemplate")
	private MongoTemplate mongoTemplate;

	private User john;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		john = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		((TestingTimeService) timeService).setReference(new Date());
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void gdprBatchShouldNotFailWhenNoData() throws BusinessException, JobExecutionException {
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(testee);
		batchRunner.execute(ImmutableList.of(testee));
	}

	@Test
	public void gdprBatchShouldNotAnonymizeWhenEnabled() throws BusinessException, JobExecutionException {
		// Set now to now + 2 years
		((TestingTimeService) timeService).setReference(Date.from(new Date()
			.toInstant()
			.atZone(ZoneId.systemDefault())
			.toLocalDate()
			.plus(Period.ofYears(2))
			.atStartOfDay()
			.atZone(ZoneId.systemDefault())
			.toInstant()));

		batchRunner.execute(ImmutableList.of(testee));

		UploadRequest uploadRequest = uploadRequestRepository.findByUuid(UPLOAD_REQUEST_UUID);
		uploadRequest.getUploadRequestURLs()
			.stream()
			.forEach(url -> assertThat(url.getContact().getMail()).isNotEqualTo(GDPRConstants.MAIL_ANONYMIZATION));
	}

	@Test
	public void gdprBatchShouldAnonymizeUploadRequest() throws BusinessException, JobExecutionException {
		// Set now to now + 2 years
		((TestingTimeService) timeService).setReference(Date.from(new Date()
			.toInstant()
			.atZone(ZoneId.systemDefault())
			.toLocalDate()
			.plus(Period.ofYears(2))
			.atStartOfDay()
			.atZone(ZoneId.systemDefault())
			.toInstant()));

		UploadRequest uploadRequest = uploadRequestRepository.findByUuid(UPLOAD_REQUEST_UUID);
		uploadRequest.setStatus(UploadRequestStatus.PURGED);
		uploadRequestRepository.update(uploadRequest);

		batchRunner.execute(ImmutableList.of(testee));

		uploadRequest.getUploadRequestURLs()
			.stream()
			.forEach(url -> assertThat(url.getContact().getMail()).isEqualTo(GDPRConstants.MAIL_ANONYMIZATION));
	}

	@Test
	public void gdprBatchShouldAnonymizeUploadRequestAudit() throws BusinessException, JobExecutionException {
		// Set now to now + 2 years
		((TestingTimeService) timeService).setReference(Date.from(new Date()
			.toInstant()
			.atZone(ZoneId.systemDefault())
			.toLocalDate()
			.plus(Period.ofYears(2))
			.atStartOfDay()
			.atZone(ZoneId.systemDefault())
			.toInstant()));

		uploadRequestService.updateStatus(john, john, UPLOAD_REQUEST_UUID, UploadRequestStatus.CLOSED, false);
		uploadRequestService.updateStatus(john, john, UPLOAD_REQUEST_UUID, UploadRequestStatus.PURGED, false);

		batchRunner.execute(ImmutableList.of(testee));

		Query query = Query.query(Criteria.where("resource.uuid").is(UPLOAD_REQUEST_UUID));
		List<UploadRequestAuditLogEntry> uploadRequestAuditLogEntries = mongoTemplate.find(query, UploadRequestAuditLogEntry.class);
		assertThat(uploadRequestAuditLogEntries).isNotEmpty();
		uploadRequestAuditLogEntries.stream()
			.forEach(entry -> {
				assertThat(entry.getResource().getOwner().getMail()).isEqualTo(GDPRConstants.MAIL_ANONYMIZATION);
				assertThat(entry.getResource().getOwner().getFirstName()).isEqualTo(GDPRConstants.FIRST_NAME_ANONYMIZATION);
				assertThat(entry.getResource().getOwner().getLastName()).isEqualTo(GDPRConstants.LAST_NAME_ANONYMIZATION);
			});
	}

	@Test
	public void gdprBatchShouldAnonymizeUploadRequestGroupAudit() throws BusinessException, JobExecutionException {
		// Set now to now + 2 years
		((TestingTimeService) timeService).setReference(Date.from(new Date()
			.toInstant()
			.atZone(ZoneId.systemDefault())
			.toLocalDate()
			.plus(Period.ofYears(2))
			.atStartOfDay()
			.atZone(ZoneId.systemDefault())
			.toInstant()));

		uploadRequestService.updateStatus(john, john, UPLOAD_REQUEST_UUID, UploadRequestStatus.CLOSED, false);
		uploadRequestService.updateStatus(john, john, UPLOAD_REQUEST_UUID, UploadRequestStatus.PURGED, false);

		batchRunner.execute(ImmutableList.of(testee));

		Query query = Query.query(Criteria.where("resource.uuid").is(UPLOAD_REQUEST_UUID));
		List<UploadRequestGroupAuditLogEntry> uploadRequestGroupAuditLogEntries = mongoTemplate.find(query, UploadRequestGroupAuditLogEntry.class);
		assertThat(uploadRequestGroupAuditLogEntries).isNotEmpty();
		uploadRequestGroupAuditLogEntries.stream()
			.forEach(entry -> {
				assertThat(entry.getResource().getOwner().getMail()).isEqualTo(GDPRConstants.MAIL_ANONYMIZATION);
				assertThat(entry.getResource().getOwner().getFirstName()).isEqualTo(GDPRConstants.FIRST_NAME_ANONYMIZATION);
				assertThat(entry.getResource().getOwner().getLastName()).isEqualTo(GDPRConstants.LAST_NAME_ANONYMIZATION);
			});
	}

	@Test
	public void gdprBatchShouldAnonymizeUploadRequestUrlAudit() throws BusinessException, JobExecutionException {
		// Set now to now + 2 years
		((TestingTimeService) timeService).setReference(Date.from(new Date()
			.toInstant()
			.atZone(ZoneId.systemDefault())
			.toLocalDate()
			.plus(Period.ofYears(2))
			.atStartOfDay()
			.atZone(ZoneId.systemDefault())
			.toInstant()));

		uploadRequestService.updateStatus(john, john, UPLOAD_REQUEST_UUID, UploadRequestStatus.CLOSED, false);
		uploadRequestService.updateStatus(john, john, UPLOAD_REQUEST_UUID, UploadRequestStatus.PURGED, false);

		batchRunner.execute(ImmutableList.of(testee));

		Query query = Query.query(Criteria.where("resource.uuid").is(UPLOAD_REQUEST_UUID));
		List<UploadRequestUrlAuditLogEntry> uploadRequestUrlAuditLogEntries = mongoTemplate.find(query, UploadRequestUrlAuditLogEntry.class);
		assertThat(uploadRequestUrlAuditLogEntries).isNotEmpty();
		uploadRequestUrlAuditLogEntries.stream()
			.forEach(entry -> {
				assertThat(entry.getResource().getContactMail()).isEqualTo(GDPRConstants.MAIL_ANONYMIZATION);
			});
	}
}
