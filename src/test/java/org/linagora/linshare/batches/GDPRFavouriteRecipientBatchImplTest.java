/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 *
 * Copyright (C) 2022 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
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
