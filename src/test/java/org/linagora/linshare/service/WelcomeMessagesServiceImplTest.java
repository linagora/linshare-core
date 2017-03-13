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

package org.linagora.linshare.service;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WelcomeMessages;
import org.linagora.linshare.core.domain.entities.WelcomeMessagesEntry;
import org.linagora.linshare.core.repository.RootUserRepository;
import org.linagora.linshare.core.service.WelcomeMessagesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-test.xml" })
public class WelcomeMessagesServiceImplTest extends
		AbstractTransactionalJUnit4SpringContextTests {

	private static Logger logger = LoggerFactory
			.getLogger(WelcomeMessagesServiceImplTest.class);

	@Autowired
	private WelcomeMessagesService welcomeService;

	private User actor;

	@Autowired
	private RootUserRepository rootUserRepository;

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		try {
			actor = rootUserRepository
					.findByLsUuid("root@localhost.localdomain");
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	@DirtiesContext
	public void listAll() {
		logger.debug("List All the welcome messages with and with the query param.");

		try {
			List<WelcomeMessages> wlcms = welcomeService.findAll(actor, null, false);
			Assert.assertEquals(1, wlcms.size());
			wlcms = welcomeService.findAll(actor, null, true);
			Assert.assertEquals(1, wlcms.size());
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
			logger.debug(e.getMessage());
		}
		logger.debug("End of the creation tests.");
	}

	@Test
	@DirtiesContext
	public void find() {
		logger.debug(LinShareTestConstants.BEGIN_TEST);

		try {
			WelcomeMessages wlcm = welcomeService.find(actor,
					"4bc57114-c8c9-11e4-a859-37b5db95d856");
			logger.debug("NAME : -------------->" + wlcm.getName());
			logger.debug("DESCRIPTION : -------------->"
					+ wlcm.getDescription());
			logger.debug("UUID : -------------->" + wlcm.getUuid());
			logger.debug("MAP SIZE : -------------->"
					+ wlcm.getWelcomeMessagesEntries().size());
			for (WelcomeMessagesEntry entry : wlcm.getWelcomeMessagesEntries()
					.values()) {
				logger.debug("CUSTOMISATION ENTRY LANG: -------------->"
						+ entry.getLang().toString());
				logger.debug("CUSTOMISATION ENTRY VALUE : -------------->"
						+ entry.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
			logger.debug(e.getMessage());
			Assert.assertTrue(false);
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void createAndDeleteWelcomeMessage() {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);

		try {
			WelcomeMessages welcm = welcomeService.find(actor,
					"4bc57114-c8c9-11e4-a859-37b5db95d856");
			WelcomeMessages welcm_create = welcomeService.create(actor, welcm,
					LoadingServiceTestDatas.rootDomainName);
			logger.debug("Object created.");
			Assert.assertNotNull(welcm_create);
			Assert.assertEquals(3, welcm_create.getWelcomeMessagesEntries()
					.size());

			logger.debug("Deleting the welcome message we just created.");

			WelcomeMessages wlcm_tmp = welcomeService.find(actor,
					welcm_create.getUuid());
			WelcomeMessages wlcm_delete = welcomeService.delete(actor,
					wlcm_tmp.getUuid());
			logger.debug("Object deleted.");

			Assert.assertEquals(wlcm_delete, welcm_create);
		} catch (Exception e) {
			logger.debug("FAIL");
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void updateWelcomeMessages() {
		logger.debug(LinShareTestConstants.BEGIN_TEST);

		try {
			WelcomeMessages wlcm = welcomeService.find(actor,
					"4bc57114-c8c9-11e4-a859-37b5db95d856");

			logger.debug("Updating the object.");

			WelcomeMessages tmp = (WelcomeMessages) wlcm.clone();
			tmp.setName("A new name for tests");
			tmp.setDescription("a new welcome descreption");
			String text1 = "This is epic!!!";
			String text2 = "Ceci est un exploit!!!";
			// TODO: to improve when delete is ready.
			for (WelcomeMessagesEntry wEntry : tmp.getWelcomeMessagesEntries()
					.values()) {
				if (wEntry.getLang().toString().equals("ENGLISH"))
					wEntry.setValue(text1);
				if (wEntry.getLang().toString().equals("FRENCH"))
					wEntry.setValue(text2);
			}
			WelcomeMessages wlcm_updated = welcomeService.update(actor, tmp,
					null);
			Assert.assertEquals(wlcm_updated.getDescription(),
					"a new welcome descreption");
			Assert.assertEquals(wlcm_updated.getName(), "A new name for tests");
			for (WelcomeMessagesEntry wEntry : wlcm_updated
					.getWelcomeMessagesEntries().values()) {
				if (wEntry.getLang().toString().equals("ENGLISH"))
					Assert.assertEquals(wEntry.getValue(), "This is epic!!!");
				if (wEntry.getLang().toString().equals("FRENCH"))
					Assert.assertEquals(wEntry.getValue(),
							"Ceci est un exploit!!!");
			}
		} catch (Exception e) {
			logger.debug("FAIL!");
			e.printStackTrace();
			logger.debug(e.getMessage());
			Assert.assertTrue(false);
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}
}