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

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.business.service.ShareEntryGroupBusinessService;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.runner.BatchRunner;
import org.linagora.linshare.core.service.ShareService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;


@ExtendWith(SpringExtension.class)

@Transactional
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
		"classpath:springContext-test.xml" })
public class WarnSenderAboutShareExpirationBatchImplTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private BatchRunner batchRunner;

	@Autowired
	@Qualifier("warnSenderAboutShareExpirationWithoutDownloadBatch")
	private GenericBatch warnSenderAboutShareExpirationWithoutDownloadBatch;

	@Autowired
	@Qualifier("shareEntryGroupBusinessService")
	private ShareEntryGroupBusinessService shareEntryGroupBusinessService;

	@Autowired
	private DocumentEntryBusinessService documentEntryBusinessService;

	@Autowired
	@Qualifier("shareService")
	private ShareService shareService;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	private User owner;

	private Account actor;

	private User recipient;

	public WarnSenderAboutShareExpirationBatchImplTest() {
		super();
	}

	@BeforeEach
	public void setUp(final @TempDir File tempDir) throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		owner = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		actor = (Account) owner;
		recipient = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
		initShareEntryGroupe(tempDir);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testWarnOwnerGuestExpiration() throws BusinessException, BatchBusinessException {
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(warnSenderAboutShareExpirationWithoutDownloadBatch);
		Assertions.assertTrue(batchRunner.execute(batches), "At least one batch failed.");
	}

	private void initShareEntryGroupe(final File tempDir) throws IOException {
		File tempFile = File.createTempFile("linshare-test-", ".tmp", tempDir);
		Calendar documentEntryExpiration = Calendar.getInstance();
		DocumentEntry documentEntry = documentEntryBusinessService.createDocumentEntry(owner, tempFile,
				tempFile.length(), "shareFile", null, false, null, "text/plain", documentEntryExpiration, false, null);
		ShareContainer shareContainer = new ShareContainer();
		shareContainer.addShareRecipient(recipient);
		shareContainer.addDocumentEntry(documentEntry);
		shareService.create(actor, owner, shareContainer);
	}

}
