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

import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.GDPRExternalRecipientFavourite;
import org.linagora.linshare.core.domain.entities.RecipientFavourite;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.GDPRExternalRecipientFavouriteRepository;
import org.linagora.linshare.core.repository.RecipientFavouriteRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.runner.BatchRunner;
import org.linagora.linshare.core.service.TimeService;
import org.linagora.linshare.utils.TestingTimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableList;
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
	"classpath:springContext-mongo-init.xml",
	"classpath:springContext-batches.xml",
	"classpath:springContext-test.xml",
	"classpath:springContext-overriding.xml" })
public class GDPRFavouriteRecipientBatchImplTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("GDPRFavouriteRecipientBatch")
	private GenericBatch testee;

	@Autowired
	private BatchRunner batchRunner;

	@Autowired
	@Qualifier("timeService")
	private TimeService timeService;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	@Qualifier("recipientFavouriteRepository")
	private RecipientFavouriteRepository recipientFavouriteRepository;

	@Autowired
	@Qualifier("gdprExternalRecipientFavouriteRepository")
	private GDPRExternalRecipientFavouriteRepository GDPRExternalRecipientFavouriteRepository;

	private User john;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		((TestingTimeService) timeService).setReference(new Date());
		john = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void gdprBatchShouldNotFailWhenNoData() {
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(testee);
		batchRunner.execute(ImmutableList.of(testee));
	}

	@Test
	public void gdprBatchShouldAnonymizeFavouriteRecipient() {
		recipientFavouriteRepository.incAndCreate(john, "recipient@linshare.org", timeService.dateNow(), true);
		List<RecipientFavourite> recipientFavourites = recipientFavouriteRepository.findAll();
		assertThat(recipientFavourites).isNotEmpty();
		List<GDPRExternalRecipientFavourite> GDPRExternalRecipientFavourites = GDPRExternalRecipientFavouriteRepository.findAll();
		assertThat(GDPRExternalRecipientFavourites).isNotEmpty();

		// Set now to now + 2 years
		((TestingTimeService) timeService).setReference(Date.from(new Date()
			.toInstant()
			.atZone(ZoneId.systemDefault())
			.toLocalDate()
			.plus(Period.ofYears(2))
			.atStartOfDay()
			.atZone(ZoneId.systemDefault())
			.toInstant()));

		batchRunner.execute(ImmutableList.of(testee));

		recipientFavourites = recipientFavouriteRepository.findAll();
		assertThat(recipientFavourites).isEmpty();

		GDPRExternalRecipientFavourites = GDPRExternalRecipientFavouriteRepository.findAll();
		assertThat(GDPRExternalRecipientFavourites).isEmpty();
	}
}

