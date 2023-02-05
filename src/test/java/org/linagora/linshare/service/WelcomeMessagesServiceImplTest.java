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

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-test.xml" })
public class WelcomeMessagesServiceImplTest {

	private static Logger logger = LoggerFactory
			.getLogger(WelcomeMessagesServiceImplTest.class);

	@Autowired
	private WelcomeMessagesService welcomeService;

	private User actor;

	@Autowired
	private RootUserRepository rootUserRepository;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		try {
			actor = rootUserRepository
					.findByLsUuid(LinShareTestConstants.ROOT_ACCOUNT);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
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
			Assertions.assertEquals(1, wlcms.size());
			wlcms = welcomeService.findAll(actor, null, true);
			Assertions.assertEquals(1, wlcms.size());
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
			Assertions.assertTrue(false);
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void createAndDeleteWelcomeMessage() {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);

		try {
			WelcomeMessages welcm = welcomeService.find(actor,
					"4bc57114-c8c9-11e4-a859-37b5db95d856");
			WelcomeMessages welcm_create = welcomeService.createByCopy(actor, welcm,
					LinShareTestConstants.ROOT_DOMAIN);
			logger.debug("Object created.");
			Assertions.assertNotNull(welcm_create);
			Assertions.assertEquals(4, welcm_create.getWelcomeMessagesEntries()
					.size());

			logger.debug("Deleting the welcome message we just created.");

			WelcomeMessages wlcm_tmp = welcomeService.find(actor,
					welcm_create.getUuid());
			WelcomeMessages wlcm_delete = welcomeService.delete(actor,
					wlcm_tmp.getUuid());
			logger.debug("Object deleted.");

			Assertions.assertEquals(wlcm_delete, welcm_create);
		} catch (Exception e) {
			logger.debug("FAIL");
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void createWelcomeMessageSpecialChar() {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		WelcomeMessages welcomeMessage = welcomeService.find(actor, "4bc57114-c8c9-11e4-a859-37b5db95d856");
		welcomeMessage.setName("EP_TEST_v233<script>alert(document.cookie)</script>");
		welcomeMessage.setDescription("EP_TEST_v233<script>alert(document.cookie)</script>");
		WelcomeMessages welcomeMessage_create = welcomeService.createByCopy(actor, welcomeMessage,
				LoadingServiceTestDatas.sqlRootDomain);
		Assertions.assertNotNull(welcomeMessage_create);
		Assertions.assertEquals(welcomeMessage_create.getName(), "EP_TEST_v233");
		Assertions.assertEquals(welcomeMessage_create.getDescription(), "EP_TEST_v233");
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void createAndUpdateWelcomeMessageSpecialChar() {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		WelcomeMessages welcomeMessage = welcomeService.find(actor, "4bc57114-c8c9-11e4-a859-37b5db95d856");
		WelcomeMessages welcomeMessage_create = welcomeService.createByCopy(actor, welcomeMessage,
				LoadingServiceTestDatas.sqlRootDomain);
		Assertions.assertNotNull(welcomeMessage_create);
		welcomeMessage_create.setName("EP_TEST_v233<script>alert(document.cookie)</script>");
		welcomeMessage_create.setDescription("EP_TEST_v233<script>alert(document.cookie)</script>");
		welcomeService.update(actor, welcomeMessage_create, null);
		Assertions.assertEquals(welcomeMessage_create.getName(), "EP_TEST_v233");
		Assertions.assertEquals(welcomeMessage_create.getDescription(), "EP_TEST_v233");
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
			Assertions.assertEquals(wlcm_updated.getDescription(),
					"a new welcome descreption");
			Assertions.assertEquals(wlcm_updated.getName(), "A new name for tests");
			for (WelcomeMessagesEntry wEntry : wlcm_updated
					.getWelcomeMessagesEntries().values()) {
				if (wEntry.getLang().toString().equals("ENGLISH"))
					Assertions.assertEquals(wEntry.getValue(), "This is epic!!!");
				if (wEntry.getLang().toString().equals("FRENCH"))
					Assertions.assertEquals(wEntry.getValue(),
							"Ceci est un exploit!!!");
			}
		} catch (Exception e) {
			logger.debug("FAIL!");
			e.printStackTrace();
			logger.debug(e.getMessage());
			Assertions.assertTrue(false);
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
