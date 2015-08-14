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
package org.linagora.linshare.repository.hibernate;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.ShareEntryGroupRepository;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = { "classpath:springContext-test.xml",
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml" })
public class ShareEntryGroupRepositoryImplTest extends
		AbstractTransactionalJUnit4SpringContextTests {

	private static final String IMPORT_LOCAL_TEST_SEG_UUID_1 = "c96d778e-b09b-4557-b785-ff5124bd2b8d";
	private static final String IMPORT_LOCAL_TEST_SEG_UUID_2 = "61eae04b-9496-4cb1-900e-eda8caac6703";
	private static final String IMPORT_LOCAL_TEST_SEG_UUID_4 = "421a2bc5-d41c-4b83-8e94-cd87aa2964c3";
	private static final String IMPORT_LOCAL_TEST_SEG_UUID_5 = "6588844c-3891-44bf-af14-b2b85ca47de4";
	private static final String IMPORT_LOCAL_TEST_SEG_UUID_6 = "027599d8-3433-4e07-9b7c-e8be82fed4a9";
	private static int countdocs = 4;
	private static int countEntries = 14;
	private static int countdocEntries = 4;
	private static int countShareEntries = 7;
	private static int countAnonymousShareEntries = 3;
	private static int countShareEntryGroup = 6;

	@Autowired
	private ShareEntryGroupRepository repository;

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		int entries = this.countRowsInTable("entry");
		int shares = this.countRowsInTable("share_entry");
		int ashares = this.countRowsInTable("anonymous_share_entry");
		int docEntries = this.countRowsInTable("document_entry");
		int docs = this.countRowsInTable("document");
		this.executeSqlScript("import-tests-share-entry-group-setup.sql", false);
		Assert.assertEquals(countEntries + entries,
				this.countRowsInTable("entry"));
		Assert.assertEquals(countShareEntries + shares,
				this.countRowsInTable("share_entry"));
		Assert.assertEquals(countAnonymousShareEntries + ashares,
				this.countRowsInTable("anonymous_share_entry"));
		Assert.assertEquals(countdocEntries + docEntries,
				this.countRowsInTable("document_entry"));
		Assert.assertEquals(countdocs + docs, this.countRowsInTable("document"));
		Assert.assertEquals(countShareEntryGroup,
				this.countRowsInTable("share_entry_group"));
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
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
			Assert.fail("Should not happend");
		}
		logger.debug("findAllShareEntriesAboutToBeNotified : " + list);
		Assert.assertEquals(2, list.size());
		Assert.assertTrue(list.contains(IMPORT_LOCAL_TEST_SEG_UUID_1));
		Assert.assertTrue(list.contains(IMPORT_LOCAL_TEST_SEG_UUID_2));
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
			Assert.fail("Should not happend");
		}
		logger.debug("findAllAnonymousShareEntriesAboutToBeNotified : " + list);
		Assert.assertEquals(2, list.size());
		Assert.assertTrue(list.contains(IMPORT_LOCAL_TEST_SEG_UUID_1));
		Assert.assertTrue(list.contains(IMPORT_LOCAL_TEST_SEG_UUID_5));
		logger.debug("End testFindAllAnonymousShareEntriesAboutToBeNotified");
	}

	@Test
	public void testFindAllAboutToBeNotified() throws BusinessException {
		logger.debug("Begin testFindAllAboutToBeNotified");
		List<ShareEntryGroup> findAll = repository.findAll();
		Assert.assertEquals(countShareEntryGroup, findAll.size());

		Set<String> findAllToNotify = null;
		try {
			findAllToNotify = repository.findAllAboutToBeNotified();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Should not happend");
		}
		logger.debug("findAllToNotify : " + findAllToNotify);
		Assert.assertEquals(countShareEntryGroup - 3, findAllToNotify.size());
		Assert.assertTrue(findAllToNotify
				.contains(IMPORT_LOCAL_TEST_SEG_UUID_1));
		Assert.assertTrue(findAllToNotify
				.contains(IMPORT_LOCAL_TEST_SEG_UUID_2));
		Assert.assertTrue(findAllToNotify
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
			Assert.assertEquals(2, findAllToPurge.size());
			Assert.assertTrue(findAllToPurge
					.contains(IMPORT_LOCAL_TEST_SEG_UUID_4));
			Assert.assertTrue(findAllToPurge
					.contains(IMPORT_LOCAL_TEST_SEG_UUID_6));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Should not happend");
		}
		logger.debug("End testFindAllToPurge");
	}

	@Test
	public void testNeedNotification() throws BusinessException,
			JobExecutionException {
		Set<String> allUuids = repository.findAllAboutToBeNotified();
		Assert.assertEquals(3, allUuids.size());

		ShareEntryGroup shareEntryGroup = null;

		shareEntryGroup = repository
				.findByUuid(IMPORT_LOCAL_TEST_SEG_UUID_5);
		Assert.assertFalse(shareEntryGroup.needNotification());

		shareEntryGroup = repository
				.findByUuid(IMPORT_LOCAL_TEST_SEG_UUID_1);
		Assert.assertTrue(shareEntryGroup.needNotification());

		shareEntryGroup = repository
				.findByUuid(IMPORT_LOCAL_TEST_SEG_UUID_2);
		Assert.assertTrue(shareEntryGroup.needNotification());
	}
}
