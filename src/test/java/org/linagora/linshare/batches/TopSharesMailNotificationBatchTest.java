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

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.runner.BatchRunner;
import org.linagora.linshare.core.service.NotifierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.subethamail.wiser.Wiser;

import com.google.common.collect.Lists;

@ExtendWith(SpringExtension.class)
@Sql({"/import-tests-share-entry-group-setup-yesterday.sql" })
@Transactional
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
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
		"classpath:springContext-test.xml" })
public class TopSharesMailNotificationBatchTest {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private BatchRunner batchRunner;

	@Qualifier("topSharesMailNotificationBatch")
	@Autowired
	private GenericBatch topShareBatch;

	@Autowired
	private NotifierService mailNotifierService;

	private Wiser wiser;

	@BeforeEach
	public void init() {
		wiser = new Wiser(2525);
		mailNotifierService.setHost("localhost");
		wiser.start();
	}

	@AfterEach
	public void tearDown() {
		wiser.stop();
	}


	@Test
	public void testTopSharesNotification() throws BusinessException, MessagingException, IOException {
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(topShareBatch);
		Assertions.assertTrue(batchRunner.execute(batches), "Batch failed.");
		Assertions.assertTrue(wiser.getMessages().size() > 0);
		Assertions.assertEquals("linshare-noreply@linagora.com", wiser.getMessages().get(0).getEnvelopeSender());
		Assertions.assertEquals("external1@linshare.org", wiser.getMessages().get(0).getEnvelopeReceiver());
		Assertions.assertEquals("external2@linshare.org", wiser.getMessages().get(1).getEnvelopeReceiver());

		MimeMultipart mailContent = (MimeMultipart) ((MimeMultipart) wiser.getMessages().get(0).getMimeMessage().getContent()).getBodyPart(0).getContent();
		Assertions.assertTrue(((String) mailContent.getBodyPart(0).getContent()).startsWith("<!DOCTYPE html>"));
		Assertions.assertTrue(mailContent.getBodyPart(1).getFileName().startsWith("All_shares_"));
		Assertions.assertTrue(mailContent.getBodyPart(2).getFileName().startsWith("Top_shares_by_file_count_"));
		Assertions.assertTrue(mailContent.getBodyPart(3).getFileName().startsWith("Top_shares_by_file_size_"));
	}
}
