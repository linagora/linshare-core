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
package org.linagora.linshare.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.cxf.helpers.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.ContactRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.repository.UploadRequestGroupRepository;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.service.UploadRequestEntryService;
import org.linagora.linshare.core.service.UploadRequestGroupService;
import org.linagora.linshare.core.service.UploadRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.collect.Lists;

@ExtendWith(SpringExtension.class)
@Sql({
	"/import-tests-upload-request.sql" })
@Transactional
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml", })
public class UploadRequestGroupServiceImplTest {
	private static Logger logger = LoggerFactory.getLogger(UploadRequestGroupServiceImplTest.class);

	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;

	@Autowired
	private ContactRepository repository;

	@Autowired
	private UploadRequestGroupService uploadRequestGroupService;

	@Autowired
	private UploadRequestService uploadRequestService;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	@Autowired
	private UploadRequestEntryService uploadRequestEntryService;

	@Autowired
	private UploadRequestGroupRepository uploadRequestGroupRepository;

	@Autowired
	private GuestRepository guestRepository;

	private UploadRequest urInit;

	private UploadRequest ure;
	private UploadRequest ureq;

	private User john;

	private Contact yoda, external2;

	private List<Contact> contactList;

	private Guest guest;

	private final InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linshare-default.properties");

	public UploadRequestGroupServiceImplTest() {
		super();
	}

	@BeforeEach
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		john = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		AbstractDomain subDomain = abstractDomainRepository.findById(LinShareTestConstants.SUB_DOMAIN);
		yoda = repository.findByMail("yoda@linshare.org");
		external2 = repository.findByMail("external2@linshare.org");
		john.setDomain(subDomain);
		guest = this.guestRepository.findByMail(LinShareTestConstants.GUEST_ACCOUNT);
		urInit = initUploadRequest();
		// UPLOAD REQUEST CREATE
		ure = this.initUploadRequest();
		ureq = this.initUploadRequest();
		contactList = Lists.newArrayList(yoda, external2);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, ure, Lists.newArrayList(yoda), "This is a subject",
				"This is a body", false);
		final UploadRequestGroup uploadRequestGroup1 = uploadRequestGroupService.create(guest, guest, ure, Lists.newArrayList(yoda), "This is a subject",
				"This is a body", false);
		final UploadRequestGroup uploadRequestGroup2 = uploadRequestGroupService.create(guest, guest, ureq, Lists.newArrayList(yoda), "This is a subject",
				"This is a body", false);
		ure = uploadRequestGroup.getUploadRequests().iterator().next();
		ureq = uploadRequestGroup2.getUploadRequests().iterator().next();

		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void createUploadRequestGroupCollective() throws BusinessException {
		// Create URG in collective mode
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, urInit, contactList, "This is a subject",
				"This is a body", true);
		assertNotNull(uploadRequestGroup);
		assertEquals(uploadRequestGroup.getUploadRequests().size(), 1, "URG Contains more than one UR");
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testSanitizeURGInputsOnCreation() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, urInit, contactList,
				"EP_TEST_v233<script>alert(document.cookie)</script>",
				"EP_TEST_v233<script>alert(document.cookie)</script>", true);
		assertNotNull(uploadRequestGroup);
		assertEquals("EP_TEST_v233", uploadRequestGroup.getSubject());
		assertEquals("EP_TEST_v233", uploadRequestGroup.getBody());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void createUploadRequestGroupIndividual() throws BusinessException {
		// Create URG in individual mode
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, urInit,
				contactList, "This is a subject", "This is a body", false);
		assertNotNull(uploadRequestGroup);
		assertEquals(uploadRequestGroup.getUploadRequests().size(), 2, "URG should contains 2 UR");
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void findAll() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		List<UploadRequestGroup> groups = uploadRequestGroupService.findAll(john, john, null);
		assertNotNull(groups.get(0));
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	@Disabled
	public void findFiltred() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date tomorrow = calendar.getTime();
		ure.setActivationDate(tomorrow);
		uploadRequestGroupService.create(john, john, ure, Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		List<UploadRequestGroup> groups = uploadRequestGroupService.findAll(john, john, Lists.newArrayList(UploadRequestStatus.ENABLED));
		assertEquals(uploadRequestGroupService.findAll(john, john, null).size() - 1, groups.size());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void updateStatusCollective() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequestGroup group = uploadRequestGroupService.create(john, john, urInit, contactList,
				"This is a subject", "This is a body", true);
		UploadRequest ur = group.getUploadRequests().iterator().next();
		assertEquals(UploadRequestStatus.ENABLED, group.getStatus());
		assertEquals(UploadRequestStatus.ENABLED, ur.getStatus());
		// Update upload request group status
		uploadRequestGroupService.updateStatus(john, john, group.getUuid(), UploadRequestStatus.CLOSED, false);
		assertEquals(UploadRequestStatus.CLOSED, group.getStatus());
		assertEquals(UploadRequestStatus.CLOSED, ur.getStatus());
		uploadRequestGroupService.updateStatus(john, john, group.getUuid(), UploadRequestStatus.ARCHIVED, true);
		assertEquals(UploadRequestStatus.ARCHIVED, group.getStatus());
		assertEquals(UploadRequestStatus.ARCHIVED, ur.getStatus());
		uploadRequestGroupService.updateStatus(john, john, group.getUuid(), UploadRequestStatus.DELETED, false);
		assertEquals(UploadRequestStatus.DELETED, group.getStatus());
		assertEquals(UploadRequestStatus.DELETED, ur.getStatus());
		BusinessException e = assertThrows(BusinessException.class, () -> {
			uploadRequestGroupService.updateStatus(john, john, group.getUuid(), UploadRequestStatus.CLOSED, false);
		});
		assertEquals(BusinessErrorCode.UPLOAD_REQUEST_STATUS_BAD_TRANSITON, e.getErrorCode());
		final BusinessException e1 = assertThrows(BusinessException.class, () -> {
			uploadRequestGroupService.updateStatus(john, john, group.getUuid(), UploadRequestStatus.DELETED, false);
		});
		assertEquals(BusinessErrorCode.UPLOAD_REQUEST_GROUP_STATUS_NOT_MODIFIED, e1.getErrorCode());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void updateStatusIndividual() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequestGroup group = uploadRequestGroupService.create(john, john, urInit, Lists.newArrayList(yoda, external2),
				"This is a subject", "This is a body", false);
		assertEquals(UploadRequestStatus.ENABLED, group.getStatus());
		Set<UploadRequest> urs = group.getUploadRequests();
		assertStatus(urs, UploadRequestStatus.ENABLED);
		// Update upload request group status
		uploadRequestGroupService.updateStatus(john, john, group.getUuid(), UploadRequestStatus.CLOSED, false);
		assertEquals(UploadRequestStatus.CLOSED, group.getStatus());
		assertStatus(urs, UploadRequestStatus.CLOSED);
		uploadRequestGroupService.updateStatus(john, john, group.getUuid(), UploadRequestStatus.ARCHIVED, true);
		assertEquals(UploadRequestStatus.ARCHIVED, group.getStatus());
		assertStatus(urs, UploadRequestStatus.ARCHIVED);
		uploadRequestGroupService.updateStatus(john, john, group.getUuid(), UploadRequestStatus.DELETED, false);
		assertEquals(UploadRequestStatus.DELETED, group.getStatus());
		assertStatus(urs, UploadRequestStatus.DELETED);
		final BusinessException e = assertThrows(BusinessException.class, () -> {
			uploadRequestGroupService.updateStatus(john, john, group.getUuid(), UploadRequestStatus.CLOSED, false);
		});
		assertEquals(BusinessErrorCode.UPLOAD_REQUEST_STATUS_BAD_TRANSITON, e.getErrorCode());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void updateStatusToCanceled() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date tomorrow = calendar.getTime();
		UploadRequest uploadRequest = initUploadRequest();
		uploadRequest.setActivationDate(tomorrow);
		UploadRequestGroup group = uploadRequestGroupService.create(john, john, uploadRequest, Lists.newArrayList(yoda), "This is a subject", "This is a body", false);
		assertEquals(UploadRequestStatus.CREATED, group.getStatus());
		uploadRequestGroupService.updateStatus(john, john, group.getUuid(), UploadRequestStatus.CANCELED, false);
		assertEquals(UploadRequestStatus.CANCELED, group.getStatus());
		assertTrue(group.getUploadRequests().iterator().next().isCanceled());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void updateStatusToClosedWithAlreadyClosedUploadRequest() throws BusinessException {
		// Update status of URG in collective mode automatically after its related UR has been closed
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequestGroup group = uploadRequestGroupService.create(john, john, urInit, contactList, "This is a subject",
				"This is a body", true);
		assertEquals(UploadRequestStatus.ENABLED, group.getStatus());
		UploadRequest ur = group.getUploadRequests().iterator().next();
		assertEquals(UploadRequestStatus.ENABLED, ur.getStatus());
		uploadRequestService.updateStatus(john, john, ur.getUuid(), UploadRequestStatus.CLOSED, false);
		assertEquals(UploadRequestStatus.CLOSED, ur.getStatus());
		// Update upload request group status
		assertEquals(UploadRequestStatus.CLOSED, group.getStatus());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void avoidUpdateStatusToClosedWithAlreadyClosedUploadRequest() throws BusinessException {
		// Test URG in individual mode not closed after closing one related UR
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequestGroup groupIndiv = uploadRequestGroupService.create(john, john, urInit, contactList, "This is a subject",
				"This is a body", false);
		assertEquals(UploadRequestStatus.ENABLED, groupIndiv.getStatus());
		assertStatus(groupIndiv.getUploadRequests(), UploadRequestStatus.ENABLED);
		UploadRequest ur1 = groupIndiv.getUploadRequests().iterator().next();
		uploadRequestService.updateStatus(john, john, ur1.getUuid(), UploadRequestStatus.CLOSED, false);
		assertEquals(UploadRequestStatus.CLOSED, ur1.getStatus());
		assertEquals(UploadRequestStatus.ENABLED, groupIndiv.getStatus());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	/**
	 * Test ability to close (not automcatical) an URG in individual mode when all
	 * nested UR are closed
	 *
	 * @throws BusinessException
	 */
	@Test
	public void closeGroupStatusIndividualWhenallNestedClosed() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequestGroup group = uploadRequestGroupService.create(john, john, urInit,
				contactList, "This is a subject", "This is a body", false);
		assertEquals(UploadRequestStatus.ENABLED, group.getStatus());
		Set<UploadRequest> urs = group.getUploadRequests();
		assertStatus(urs, UploadRequestStatus.ENABLED);
		// Close all nested URs
		for (UploadRequest uploadRequest : urs) {
			uploadRequestService.updateStatus(john, john, uploadRequest.getUuid(),
					UploadRequestStatus.CLOSED, false);
		}
		assertStatus(urs, UploadRequestStatus.CLOSED);
		uploadRequestGroupService.updateStatus(john, john, group.getUuid(), UploadRequestStatus.CLOSED, false);
		assertEquals(UploadRequestStatus.CLOSED, group.getStatus());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void archiveGroupStatusIndividualWhenAllNestedArchived() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequestGroup group = uploadRequestGroupService.create(john, john, urInit, contactList,
				"This is a subject", "This is a body", false);
		assertEquals(UploadRequestStatus.ENABLED, group.getStatus());
		Set<UploadRequest> urs = group.getUploadRequests();
		assertStatus(urs, UploadRequestStatus.ENABLED);
		// Close all nested URs
		for (UploadRequest uploadRequest : urs) {
			uploadRequestService.updateStatus(john, john, uploadRequest.getUuid(), UploadRequestStatus.CLOSED, false);
		}
		assertStatus(urs, UploadRequestStatus.CLOSED);
		uploadRequestGroupService.updateStatus(john, john, group.getUuid(), UploadRequestStatus.CLOSED, false);
		assertEquals(UploadRequestStatus.CLOSED, group.getStatus());
		// Archive all nested URs
		for (UploadRequest uploadRequest : urs) {
			uploadRequestService.updateStatus(john, john, uploadRequest.getUuid(), UploadRequestStatus.ARCHIVED, false);
		}
		assertStatus(urs, UploadRequestStatus.ARCHIVED);
		uploadRequestGroupService.updateStatus(john, john, group.getUuid(), UploadRequestStatus.ARCHIVED, false);
		assertEquals(UploadRequestStatus.ARCHIVED, group.getStatus());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	/**
	 * Test ability to close URG individual with only one closed UR
	 *
	 * @throws BusinessException
	 */
	@Test
	public void closeGroupStatusIndividualWhenOneNestedClosed() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequestGroup group = uploadRequestGroupService.create(john, john, urInit,
				contactList, "This is a subject", "This is a body", false);
		assertEquals(UploadRequestStatus.ENABLED, group.getStatus());
		Set<UploadRequest> urs = group.getUploadRequests();
		assertStatus(urs, UploadRequestStatus.ENABLED);
		// Close one nested UR
		UploadRequest uploadRequest_1 = group.getUploadRequests().iterator().next();
		uploadRequestService.updateStatus(john, john, uploadRequest_1.getUuid(),UploadRequestStatus.CLOSED,false);
		assertEquals(UploadRequestStatus.CLOSED, uploadRequest_1.getStatus());
		uploadRequestGroupService.updateStatus(john, john, group.getUuid(), UploadRequestStatus.CLOSED, false);
		assertEquals(UploadRequestStatus.CLOSED, group.getStatus());
		assertStatus(urs, UploadRequestStatus.CLOSED);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void update() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequestGroup group = uploadRequestGroupService.find(john, john, ure.getUploadRequestGroup().getUuid());
		List<UploadRequest> uploadRequests = uploadRequestService.findAll(john, john, group, null);
		group.setUploadRequests(uploadRequests.stream().collect(Collectors.toSet()));
		group.setCanClose(false);
		group.setMaxFileCount(Integer.valueOf(5));
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.update(john, john, group, false);
		assertEquals(false, uploadRequestGroup.getCanClose());
		assertEquals(Integer.valueOf(5), uploadRequestGroup.getMaxFileCount());
		UploadRequest uploadRequest = uploadRequests.get(0);
		assertEquals(false, uploadRequest.isCanClose());
		assertEquals(Integer.valueOf(5), uploadRequest.getMaxFileCount());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testSanitizeURGInputsOnUpdate() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequestGroup group = uploadRequestGroupService.find(john, john, ure.getUploadRequestGroup().getUuid());
		group.setSubject("EP_TEST_v233<script>alert(document.cookie)</script>");
		group.setBody("EP_TEST_v233<script>alert(document.cookie)</script>");
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.update(john, john, group, false);
		assertEquals("EP_TEST_v233", uploadRequestGroup.getSubject());
		assertEquals("EP_TEST_v233", uploadRequestGroup.getBody());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testForbidUpdateClosedAndArchivedURG() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequestGroup group = uploadRequestGroupService.find(john, john, ure.getUploadRequestGroup().getUuid());
		uploadRequestGroupService.updateStatus(john, john, group.getUuid(), UploadRequestStatus.CLOSED, false);
		assertEquals(UploadRequestStatus.CLOSED, group.getStatus());
		final BusinessException exception = assertThrows(BusinessException.class, () -> {
			uploadRequestGroupService.update(john, john, group, false);
		});
		assertEquals(exception.getErrorCode(), BusinessErrorCode.UPLOAD_REQUEST_GROUP_UPDATE_FORBIDDEN);
		uploadRequestGroupService.updateStatus(john, john, group.getUuid(), UploadRequestStatus.ARCHIVED, false);
		assertEquals(UploadRequestStatus.ARCHIVED, group.getStatus());
		final BusinessException exception2 = assertThrows(BusinessException.class, () -> {
			uploadRequestGroupService.update(john, john, group, false);
		});
		assertEquals(exception2.getErrorCode(), BusinessErrorCode.UPLOAD_REQUEST_GROUP_UPDATE_FORBIDDEN);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void updateWithMaxDepositSizeFuncDisabled() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequestGroup group = uploadRequestGroupService.find(john, john, ure.getUploadRequestGroup().getUuid());
		group.setMaxDepositSize(Long.valueOf(0));
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.update(john, john, group, false);
		assertEquals(0, uploadRequestGroup.getMaxDepositSize());
		UploadRequest uploadRequest = group.getUploadRequests().iterator().next();
		assertEquals(0, uploadRequest.getMaxDepositSize());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void updateCollectiveGroup() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequestGroup urg = uploadRequestGroupService.create(john, john, urInit, contactList, "This is a subject",
				"This is a body", true);
		UploadRequest uploadRequest = urg.getUploadRequests().iterator().next();
		// in Collective mode the UR is always pristine
		assertTrue(urg.isCollective());
		assertTrue(uploadRequest.isPristine());
		assertEquals(3, urg.getMaxFileCount());
		assertEquals(3, uploadRequest.getMaxFileCount());
		// update from UploadRequestService should raise an exception
		uploadRequest.setMaxFileCount(2);
		final BusinessException e = assertThrows(BusinessException.class, () -> {
			uploadRequestService.update(john, john, uploadRequest.getUuid(), uploadRequest, false);
		});
		assertEquals(e.getErrorCode(), BusinessErrorCode.UPLOAD_REQUEST_NOT_UPDATABLE_GROUP_MODE);
		urg.setMaxFileCount(2);
		// No matter value of force always update the UR
		urg = uploadRequestGroupService.update(john, john, urg, false);
		assertEquals(2, uploadRequest.getMaxFileCount());
		urg.setMaxFileCount(5);
		urg = uploadRequestGroupService.update(john, john, urg, true);
		assertEquals(5, uploadRequest.getMaxFileCount());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void updateWithForceIndividualGroup() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequestGroup urg = uploadRequestGroupService.create(john, john, urInit, contactList, "This is a subject",
				"This is a body", false);
		List<UploadRequest> uploadRequests = uploadRequestService.findAll(john, john, urg, null);
		// assert initial conditions
		assertFalse(urg.isCollective());
		for (UploadRequest uploadRequest : uploadRequests) {
			assertTrue(uploadRequest.isPristine());
			assertEquals(3, uploadRequest.getMaxFileCount());
		}
		// update the group without force | expected pristine URs will be changed
		urg.setMaxFileCount(Integer.valueOf(2));
		urg = uploadRequestGroupService.update(john, john, urg, false);
		for (UploadRequest uploadRequest : uploadRequests) {
			assertTrue(uploadRequest.isPristine());
			assertEquals(2, uploadRequest.getMaxFileCount());
		}
		// update one nested UR
		UploadRequest uploadRequest1 = uploadRequests.get(0);
		UploadRequest uploadRequest2 = uploadRequests.get(1);
		uploadRequest1.setMaxFileCount(5);
		// update via the uploadRequestService endpoint
		uploadRequest1 = uploadRequestService.update(john, john, uploadRequest1.getUuid(), uploadRequest1, false);
		assertEquals(Integer.valueOf(5), uploadRequest1.getMaxFileCount());
		// expected | UR should be dirty (not pristine)
		assertFalse(uploadRequest1.isPristine());
		// update the group without force | expected only pristine URs should be changed
		urg.setMaxFileCount(7);
		urg = uploadRequestGroupService.update(john, john, urg, false);
		assertEquals(Integer.valueOf(5), uploadRequest1.getMaxFileCount());
		assertEquals(Integer.valueOf(7), uploadRequest2.getMaxFileCount());
		// update the group with force | expected all URs should be changed
		urg.setMaxFileCount(6);
		urg = uploadRequestGroupService.update(john, john, urg, true);
		assertEquals(Integer.valueOf(6), uploadRequest1.getMaxFileCount());
		assertEquals(Integer.valueOf(6), uploadRequest2.getMaxFileCount());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void updateStatusToPurged() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, urInit, Lists.newArrayList(yoda), "This is a subject",
				"This is a body", false);
		UploadRequest createdUploadRequest = uploadRequestGroup.getUploadRequests().iterator().next();
		assertEquals(UploadRequestStatus.ENABLED, createdUploadRequest.getUploadRequestGroup().getStatus());
		// Update upload request group status
		uploadRequestGroupService.updateStatus(john, john, createdUploadRequest.getUploadRequestGroup().getUuid(),
				UploadRequestStatus.CLOSED, false);
		assertEquals(UploadRequestStatus.CLOSED, createdUploadRequest.getUploadRequestGroup().getStatus());
		uploadRequestGroupService.updateStatus(john, john, createdUploadRequest.getUploadRequestGroup().getUuid(),
				UploadRequestStatus.ARCHIVED, false);
		assertEquals(UploadRequestStatus.ARCHIVED, createdUploadRequest.getUploadRequestGroup().getStatus());
		// Try with copy false
		uploadRequestGroupService.updateStatus(john, john, createdUploadRequest.getUploadRequestGroup().getUuid(),
				UploadRequestStatus.PURGED, false);
		assertEquals(UploadRequestStatus.PURGED,
				createdUploadRequest.getUploadRequestGroup().getStatus(), "The given upload request has not a PURGED status");
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void updateStatusToPurgedAndCopy() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		UploadRequestGroup uploadRequestGroup = uploadRequestGroupService.create(john, john, urInit, Lists.newArrayList(yoda), "This is a subject",
				"This is a body", false);
		UploadRequest createdUploadRequest = uploadRequestGroup.getUploadRequests().iterator().next();
		assertEquals(UploadRequestStatus.ENABLED, createdUploadRequest.getUploadRequestGroup().getStatus());
		// Update upload request group status
		uploadRequestGroupService.updateStatus(john, john, createdUploadRequest.getUploadRequestGroup().getUuid(),
				UploadRequestStatus.CLOSED, false);
		assertEquals(UploadRequestStatus.CLOSED, createdUploadRequest.getUploadRequestGroup().getStatus());
		uploadRequestGroupService.updateStatus(john, john, createdUploadRequest.getUploadRequestGroup().getUuid(),
				UploadRequestStatus.ARCHIVED, false);
		assertEquals(UploadRequestStatus.ARCHIVED, createdUploadRequest.getUploadRequestGroup().getStatus());
		// Try with copy true
		uploadRequestGroupService.updateStatus(john, john, createdUploadRequest.getUploadRequestGroup().getUuid(),
				UploadRequestStatus.PURGED, true);
		assertEquals(UploadRequestStatus.PURGED, createdUploadRequest.getUploadRequestGroup().getStatus());
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testReturnURGDetails() throws BusinessException, IOException {
		// On this test we will return the nbr of uploaded files and used space on an
		// URG
		logger.info(LinShareTestConstants.BEGIN_TEST);
		assertEquals(UploadRequestStatus.ENABLED, ure.getStatus());
		File tempFile = File.createTempFile("linshare-test-", ".tmp");
		File tempFile2 = File.createTempFile("linshare-test-", ".tmp");
		IOUtils.transferTo(stream, tempFile);
		UploadRequestEntry uploadRequestEntry = uploadRequestEntryService.create(john, john, tempFile,
				"First Upload request entry", "First URE", false, null,
				ure.getUploadRequestURLs().iterator().next());
		UploadRequestEntry uploadRequestEntry2 = uploadRequestEntryService.create(john, john, tempFile2,
				"Second Upload request entry", "Second URE", false, null,
				ure.getUploadRequestURLs().iterator().next());
		UploadRequestGroup uploadRequestGroup = ure.getUploadRequestGroup();
		assertEquals(2, uploadRequestGroupService.countNbrUploadedFiles(uploadRequestGroup));
		assertEquals(uploadRequestEntry.getSize() + uploadRequestEntry2.getSize(),
				uploadRequestGroupService.computeEntriesSize(uploadRequestGroup));
		logger.info(LinShareTestConstants.END_TEST);
	}

	// helpers
	private UploadRequest initUploadRequest() {
		UploadRequest uploadRequest = new UploadRequest();
		uploadRequest.setCanClose(true);
		uploadRequest.setMaxDepositSize((long) 100000);
		uploadRequest.setMaxFileCount(Integer.valueOf(3));
		uploadRequest.setMaxFileSize((long) 100000);
		uploadRequest.setProtectedByPassword(false);
		uploadRequest.setCanEditExpiryDate(true);
		uploadRequest.setCanDelete(true);
		uploadRequest.setLocale(Language.ENGLISH);
		uploadRequest.setActivationDate(null);
		return uploadRequest;
	}
	
	private void assertStatus(Set<UploadRequest> urs, UploadRequestStatus status) {
		for (UploadRequest ur : urs) {
			assertEquals(status, ur.getStatus());
		}
	}

	@Test
	public void testUpdateGuestAccountDomainId() {
		final List<UploadRequestGroup>requestGroups = uploadRequestGroupRepository.findAllByAccountAndDomain(guest, guest.getDomain());
		for (final UploadRequestGroup uploadRequestGroup : requestGroups){
			this.uploadRequestGroupService.transferUploadRequestGroupsFromGuestToInternal(guest, john);
			assertEquals(uploadRequestGroup.getAbstractDomain(), john.getDomain());
		}
	}
}
