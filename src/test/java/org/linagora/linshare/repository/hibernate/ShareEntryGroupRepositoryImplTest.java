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
package org.linagora.linshare.repository.hibernate;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.ShareEntryGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@Transactional
@Sql({ 
	"/import-tests-share-entry-group-setup.sql" })
@ContextConfiguration(locations = { 
		"classpath:springContext-test.xml",
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml" })
public class ShareEntryGroupRepositoryImplTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String IMPORT_LOCAL_TEST_SEG_UUID_1 = "c96d778e-b09b-4557-b785-ff5124bd2b8d";
	private static final String IMPORT_LOCAL_TEST_SEG_UUID_2 = "61eae04b-9496-4cb1-900e-eda8caac6703";
	private static final String IMPORT_LOCAL_TEST_SEG_UUID_4 = "421a2bc5-d41c-4b83-8e94-cd87aa2964c3";
	private static final String IMPORT_LOCAL_TEST_SEG_UUID_5 = "6588844c-3891-44bf-af14-b2b85ca47de4";
	private static final String IMPORT_LOCAL_TEST_SEG_UUID_6 = "027599d8-3433-4e07-9b7c-e8be82fed4a9";
	private static int countShareEntryGroup = 6;

	@Autowired
	private ShareEntryGroupRepository repository;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testFindAllShareEntriesAboutToBeNotified()
			throws BusinessException {
		logger.debug("Begin testFindAllShareEntriesAboutToBeNotified");

		List<String> list = null;
		try {
			list = repository.findAllShareEntriesAboutToBeNotified(new Date());
		} catch (Exception e) {
			e.printStackTrace();
			Assertions.fail("Should not happend");
		}
		logger.debug("findAllShareEntriesAboutToBeNotified : " + list);
		Assertions.assertEquals(2, list.size());
		Assertions.assertTrue(list.contains(IMPORT_LOCAL_TEST_SEG_UUID_1));
		Assertions.assertTrue(list.contains(IMPORT_LOCAL_TEST_SEG_UUID_2));
		logger.debug("End testFindAllShareEntriesAboutToBeNotified");
	}

	@Test
	public void testFindAllAnonymousShareEntriesAboutToBeNotified()
			throws BusinessException {
		logger.debug("Begin testFindAllAnonymousShareEntriesAboutToBeNotified");
		List<String> list = null;
		try {
			list = repository
					.findAllAnonymousShareEntriesAboutToBeNotified(new Date());
		} catch (Exception e) {
			e.printStackTrace();
			Assertions.fail("Should not happend");
		}
		logger.debug("findAllAnonymousShareEntriesAboutToBeNotified : " + list);
		Assertions.assertEquals(2, list.size());
		Assertions.assertTrue(list.contains(IMPORT_LOCAL_TEST_SEG_UUID_1));
		Assertions.assertTrue(list.contains(IMPORT_LOCAL_TEST_SEG_UUID_5));
		logger.debug("End testFindAllAnonymousShareEntriesAboutToBeNotified");
	}

	@Test
	public void testFindAllAboutToBeNotified() throws BusinessException {
		logger.debug("Begin testFindAllAboutToBeNotified");
		List<ShareEntryGroup> findAll = repository.findAll();
		Assertions.assertEquals(countShareEntryGroup, findAll.size());

		Set<String> findAllToNotify = null;
		try {
			findAllToNotify = repository.findAllAboutToBeNotified();
		} catch (Exception e) {
			e.printStackTrace();
			Assertions.fail("Should not happend");
		}
		logger.debug("findAllToNotify : " + findAllToNotify);
		Assertions.assertEquals(countShareEntryGroup - 3, findAllToNotify.size());
		Assertions.assertTrue(findAllToNotify
				.contains(IMPORT_LOCAL_TEST_SEG_UUID_1));
		Assertions.assertTrue(findAllToNotify
				.contains(IMPORT_LOCAL_TEST_SEG_UUID_2));
		Assertions.assertTrue(findAllToNotify
				.contains(IMPORT_LOCAL_TEST_SEG_UUID_5));
		logger.debug("End testFindAllAboutToBeNotified");
	}

	@Test
	public void testFindAllToPurge() throws BusinessException {
		logger.debug("Begin testFindAllToPurge");
		List<String> findAllToPurge = null;
		try {
			findAllToPurge = repository.findAllToPurge();
			logger.debug("findAllToPurge size : " + findAllToPurge.size());
			for (String string : findAllToPurge) {
				logger.debug("ToPurge : " + string);
			}
			Assertions.assertEquals(2, findAllToPurge.size());
			Assertions.assertTrue(findAllToPurge
					.contains(IMPORT_LOCAL_TEST_SEG_UUID_4));
			Assertions.assertTrue(findAllToPurge
					.contains(IMPORT_LOCAL_TEST_SEG_UUID_6));
		} catch (Exception e) {
			e.printStackTrace();
			Assertions.fail("Should not happend");
		}
		logger.debug("End testFindAllToPurge");
	}

	@Test
	public void testNeedNotification() throws BusinessException,
			JobExecutionException {
		Set<String> allUuids = repository.findAllAboutToBeNotified();
		Assertions.assertEquals(3, allUuids.size());

		ShareEntryGroup shareEntryGroup = null;

		shareEntryGroup = repository
				.findByUuid(IMPORT_LOCAL_TEST_SEG_UUID_5);
		Assertions.assertFalse(shareEntryGroup.needNotification());

		shareEntryGroup = repository
				.findByUuid(IMPORT_LOCAL_TEST_SEG_UUID_1);
		Assertions.assertTrue(shareEntryGroup.needNotification());

		shareEntryGroup = repository
				.findByUuid(IMPORT_LOCAL_TEST_SEG_UUID_2);
		Assertions.assertTrue(shareEntryGroup.needNotification());
	}
}
