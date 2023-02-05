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

import java.util.Calendar;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.RecipientFavourite;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.RecipientFavouriteRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.runner.BatchRunner;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

@ExtendWith({ SpringExtension.class})
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
		"classpath:springContext-batches.xml",
		"classpath:springContext-test.xml" })
public class DeleteOutdatedFavouriteRecipientBatchImplTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private BatchRunner batchRunner;

	@Autowired
	@Qualifier("deleteOutdatedFavouriteRecipientBatch")
	private GenericBatch deleteOutdatedFavouriteRecipientBatch;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	@Qualifier("recipientFavouriteRepository")
	private RecipientFavouriteRepository recipientFavouriteRepository;

	private User authUser;

	private List<RecipientFavourite> recipientFavourites = Lists.newArrayList();

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		authUser = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		Calendar expirationDate = Calendar.getInstance();
		expirationDate.add(Calendar.DATE, -1);
		RecipientFavourite recipient1 = new RecipientFavourite(authUser, LinShareTestConstants.JANE_ACCOUNT, expirationDate.getTime());
		recipient1 = recipientFavouriteRepository.create(recipient1);
		RecipientFavourite recipient2 = new RecipientFavourite(authUser, LinShareTestConstants.FOO_ACCOUNT, null);
		recipient2 = recipientFavouriteRepository.create(recipient2);
		recipientFavourites = recipientFavouriteRepository.findAll();
		assertThat(recipientFavourites.size() == 2);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testBatch() throws BusinessException, JobExecutionException {
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(deleteOutdatedFavouriteRecipientBatch);
		Assertions.assertTrue(batchRunner.execute(batches), "At least one batch failed.");
		recipientFavourites = recipientFavouriteRepository.findAll();
		assertThat(recipientFavourites).hasSize(1);
		assertThat(recipientFavourites.iterator().next().getRecipient()).isEqualTo(LinShareTestConstants.FOO_ACCOUNT);
	}
}
