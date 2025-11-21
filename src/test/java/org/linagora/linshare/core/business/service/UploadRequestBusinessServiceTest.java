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
package org.linagora.linshare.core.business.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.linagora.linshare.core.business.service.impl.UploadRequestBusinessServiceImpl;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.repository.UploadRequestRepository;
import org.linagora.linshare.core.repository.UploadRequestUrlRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Unit test for {@link UploadRequestBusinessServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UploadRequestBusinessServiceTest {

	@Mock
	private UploadRequestRepository uploadRequestRepository;

	@Mock
	private UploadRequestUrlRepository uploadRequestUrlRepository;

	@Mock
	private PasswordService passwordService;

	@InjectMocks
	private UploadRequestBusinessServiceImpl uploadRequestBusinessService;

	/**
	 * <p>
	 * Verify the status update of an upload request from different initial statuses to different target statuses.
	 * </p>
	 * <p>
	 * The upload request can be either protected by password or not.
	 * </p>
	 * Expected results:
	 * <ul>
	 * <li>no error occurs,</li>
	 * <li>the status is correctly updated from {@code initialStatus} to {@code targetStatus},</li>
	 * <li>and, if the initial status is {@code CREATED} and the target status is {@code ENABLED}, additional processing
	 * is done:
	 * <ul>
	 * <li>if the upload request <b>is protected</b> by password: a password is created for each URL, associated with the
	 * upload request</li>
	 * <li>otherwise: no password is created for the URLs that are associated with the upload request.</li>
	 * </ul>
	 * </li>
	 * </ul>
	 *
	 * @param initialStatus
	 *                              The initial status of the upload request, from which to update the status. Not
	 *                              {@code null}.
	 * @param targetStatus
	 *                              The target status of the upload request, to which to update the status. Not
	 *                              {@code null}.
	 * @param isProtectedByPassword
	 *                              A flag that indicates whether the upload request is protected by password or not.
	 */
	@ParameterizedTest
	@MethodSource("generateUploadRequestInitialAndTargetStatusWithPasswordProtectionStatus")
	void updateStatus(@Nonnull final UploadRequestStatus initialStatus,
			@Nonnull final UploadRequestStatus targetStatus, final boolean isProtectedByPassword) {
		// Prepare
		final UploadRequest uploadRequest = new UploadRequest();
		uploadRequest.setStatus(initialStatus);
		uploadRequest.setProtectedByPassword(isProtectedByPassword);

		final UploadRequestUrl url1 = new UploadRequestUrl();
		final UploadRequestUrl url2 = new UploadRequestUrl();
		url1.setUuid("test-uuid-1");
		url2.setUuid("test-uuid-2");
		url1.setUploadRequest(uploadRequest);
		url2.setUploadRequest(uploadRequest);

		uploadRequest.setUploadRequestURLs(Set.of(url1, url2));

		// Mock
		when(this.passwordService.generatePassword()).thenReturn("some-random-password");
		when(this.passwordService.encode("some-random-password")).thenReturn(
				"{bcrypt}$2a$10$a49P5d6GXUIg5JYr09CPCOl/K0ktgF0LI/w5GtYYCzpPeRLMhVXBm");
		when(this.uploadRequestUrlRepository.update(any(UploadRequestUrl.class))).thenAnswer(
				invocation -> invocation.getArgument(0));
		when(this.uploadRequestRepository.update(any(UploadRequest.class))).thenAnswer(
				invocation -> invocation.getArgument(0));

		// Execute logic
		final UploadRequest updatedUploadRequest = this.uploadRequestBusinessService.updateStatus(uploadRequest, targetStatus);

		// Assert
		assertEquals(targetStatus, updatedUploadRequest.getStatus());
		if (UploadRequestStatus.CREATED.equals(initialStatus) && UploadRequestStatus.ENABLED.equals(targetStatus)
				&& isProtectedByPassword) {
			updatedUploadRequest.getUploadRequestURLs().forEach(url -> assertNotNull(url.getPassword()));
		} else {
			updatedUploadRequest.getUploadRequestURLs().forEach(url -> assertNull(url.getPassword()));
		}
	}

	/**
	 * <p>
	 * Generate a stream of arguments, containing 3 elements each:
	 * </p>
	 * <ul>
	 * <li>Initial status of type {@link UploadRequestStatus}</li>
	 * <li>Target status of type {@link UploadRequestStatus}</li>
	 * <li>{@code Boolean} representing whether the upload request is protected by password or not.</li>
	 * </ul>
	 *
	 * <p>
	 * Note that the generated transitions (initial status --> target status) are allowed.
	 * </p>
	 *
	 * @return the generated stream of arguments.
	 */
	private static @Nonnull Stream<Arguments> generateUploadRequestInitialAndTargetStatusWithPasswordProtectionStatus() {
		return Stream.of(
				// Protected by password
				Arguments.of(UploadRequestStatus.CREATED, UploadRequestStatus.CANCELED, true),
				Arguments.of(UploadRequestStatus.CREATED, UploadRequestStatus.ENABLED, true),
				Arguments.of(UploadRequestStatus.PURGED, UploadRequestStatus.DELETED, true),
				Arguments.of(UploadRequestStatus.ARCHIVED, UploadRequestStatus.DELETED, true),
				Arguments.of(UploadRequestStatus.ARCHIVED, UploadRequestStatus.PURGED, true),
				Arguments.of(UploadRequestStatus.CLOSED, UploadRequestStatus.ARCHIVED, true),
				Arguments.of(UploadRequestStatus.CLOSED, UploadRequestStatus.PURGED, true),
				Arguments.of(UploadRequestStatus.ENABLED, UploadRequestStatus.CLOSED, true),

				// Not protected by password
				Arguments.of(UploadRequestStatus.CREATED, UploadRequestStatus.CANCELED, false),
				Arguments.of(UploadRequestStatus.CREATED, UploadRequestStatus.ENABLED, false),
				Arguments.of(UploadRequestStatus.PURGED, UploadRequestStatus.DELETED, false),
				Arguments.of(UploadRequestStatus.ARCHIVED, UploadRequestStatus.DELETED, false),
				Arguments.of(UploadRequestStatus.ARCHIVED, UploadRequestStatus.PURGED, false),
				Arguments.of(UploadRequestStatus.CLOSED, UploadRequestStatus.ARCHIVED, false),
				Arguments.of(UploadRequestStatus.CLOSED, UploadRequestStatus.PURGED, false),
				Arguments.of(UploadRequestStatus.ENABLED, UploadRequestStatus.CLOSED, false));
	}

}