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
package org.linagora.linshare.core.batches.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.transaction.Transactional;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.job.quartz.UploadRequestBatchResultContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Integration test for {@link EnableUploadRequestBatchImpl}.
 */
@ExtendWith(SpringExtension.class)
@Sql(scripts = { "classpath:import-tests-enable-upload-requests.sql" })
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml", "classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml", "classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml", "classpath:springContext-repository.xml",
		"classpath:springContext-business-service.xml", "classpath:springContext-rac.xml",
		"classpath:springContext-service-miscellaneous.xml", "classpath:springContext-service.xml",
		"classpath:springContext-mongo-init.xml", "classpath:springContext-batches.xml",
		"classpath:springContext-test.xml" })
class EnableUploadRequestBatchImplIT {

	/**
	 * Uuid of an upload request, persisted in the db, that has a status {@code CREATED}, and is protected by password.
	 */
	private static final String UPLOAD_REQUEST_WITH_STATUS_CREATED_PROTECTED_BY_PASSWORD_UUID = "f548ac1c-ef45-11e5-a73f-4b811b25f11b";

	/**
	 * Uuid of an upload request, persisted in the db, that has a status {@code CREATED}, and is not protected by
	 * password.
	 */
	private static final String UPLOAD_REQUEST_WITH_STATUS_CREATED_NOT_PROTECTED_BY_PASSWORD_UUID = "42a4a7e4-ef46-11e5-b1d1-e3c21cdc7a0b";

	@Autowired
	private EnableUploadRequestBatchImpl enableUploadRequestBatch;

	/**
	 * <p>
	 * Enable upload request.
	 * </p>
	 * <p>
	 * By <b>enable</b>, we mean: changing its {@code status} from {@link UploadRequestStatus#CREATED} into
	 * {@link UploadRequestStatus#ENABLED}.
	 * </p>
	 * Expected results:
	 * <ul>
	 * <li>no error occurs,</li>
	 * <li>the status is correctly updated from {@link UploadRequestStatus#CREATED} to
	 * {@link UploadRequestStatus#ENABLED},</li>
	 * <li>the {@link ResultContext} indicates that the batch was processed successfully.</li>
	 * <li>If the upload request <strong>is protected</strong> by password:
	 * <ul>
	 * <li>a password is created for each url, associated with the upload request</li>
	 * </ul>
	 * </li>
	 * <li>Else, if the upload request is <strong>NOT protected</strong> with password:
	 * <ul>
	 * <li>no password is created for the urls associated with the upload request</li>
	 * </ul>
	 * </li>
	 * </ul>
	 *
	 * @param isProtectedByPassword A {@code boolean} that indicates whether the upload request is protected by password
	 *                              or not.
	 * @param uploadRequestUuid     the {@code UUID} of the upload request to enable. Not {@code Null}.
	 *
	 */
	@Transactional
	@ParameterizedTest
	@MethodSource("generateUploadRequestPasswordProtectionStatusWithCorrespondingUuid")
	void execute_enableCreatedUploadRequest(final boolean isProtectedByPassword,
			@Nonnull final String uploadRequestUuid) {
		// Execute
		final UploadRequestBatchResultContext resultContext = (UploadRequestBatchResultContext) this.enableUploadRequestBatch.execute(
				new BatchRunContext(), uploadRequestUuid, 1, 0);
		final UploadRequest uploadRequest = resultContext.getResource();

		// Assert
		assertTrue(resultContext.getProcessed());
		assertEquals(UploadRequestStatus.ENABLED, uploadRequest.getStatus());
		if (isProtectedByPassword) {
			uploadRequest.getUploadRequestURLs().forEach(url -> assertNotNull(url.getPassword()));
		} else {
			uploadRequest.getUploadRequestURLs().forEach(url -> assertNull(url.getPassword()));
		}
	}

	/**
	 * <p>
	 * Generate a stream of arguments, containing 2 elements each:
	 * </p>
	 * <ul>
	 * <li>A {@code boolean} representing whether the upload request is protected by password or not,</li>
	 * <li>The corresponding {@code UUID} of that upload request. (The status of the upload request is {@code CREATED})</li>
	 * </ul>
	 *
	 * @return the generated stream of arguments.
	 */
	private static @Nonnull Stream<Arguments> generateUploadRequestPasswordProtectionStatusWithCorrespondingUuid() {
		return Stream.of(Arguments.of(true, UPLOAD_REQUEST_WITH_STATUS_CREATED_PROTECTED_BY_PASSWORD_UUID),
				Arguments.of(false, UPLOAD_REQUEST_WITH_STATUS_CREATED_NOT_PROTECTED_BY_PASSWORD_UUID));
	}
}