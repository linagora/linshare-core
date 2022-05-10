/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2022. Contribute to
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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Moderator;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.ModeratorService;
import org.linagora.linshare.server.embedded.ldap.LdapServerRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ExtendWith(LdapServerRule.class)
@Transactional
@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml" })
public class ModeratorServiceImplForRootUserTest {

	private static Logger logger = LoggerFactory.getLogger(ModeratorServiceImplForRootUserTest.class);

	@Autowired
	private ModeratorService moderatorService;

	@Autowired
	private GuestRepository guestRepository;

	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;

	private Account root;

	private Account john;

	private Account jane;

	private Guest guest;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		root = userRepository.findByLsUuid(LinShareTestConstants.ROOT_ACCOUNT);
		john = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		jane = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
		guest = guestRepository.findByMail(LinShareTestConstants.GUEST_ACCOUNT);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);

		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testCreateModerator() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Moderator moderator = moderatorService.create(root, root, new Moderator(ModeratorRole.ADMIN, john, guest));
		assertThat(moderator).isNotNull();
		assertThat(moderator.getAccount()).isEqualTo(john);
		assertThat(moderator.getGuest()).isEqualTo(guest);
		Guest guest = guestRepository.findByLsUuid(moderator.getGuest().getLsUuid());
		assertThat(guest.getModerators()).size().isEqualTo(1);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindModerator() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Moderator moderatorToCreate = moderatorService.create(root, root, new Moderator(ModeratorRole.ADMIN, john, guest));
		assertThat(moderatorToCreate).isNotNull();
		assertThat(moderatorToCreate.getAccount()).isEqualTo(john);
		assertThat(moderatorToCreate.getGuest()).isEqualTo(guest);
		Guest guest = guestRepository.findByLsUuid(moderatorToCreate.getGuest().getLsUuid());
		assertThat(guest.getModerators()).size().isEqualTo(1);
		Moderator moderator = moderatorService.find(root, root, moderatorToCreate.getUuid());
		assertThat(moderator).isNotNull();
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testDeleteModerator() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Moderator moderator = moderatorService.create(root, root, new Moderator(ModeratorRole.ADMIN, john, guest));
		assertThat(moderator).isNotNull();
		assertThat(moderator.getAccount()).isEqualTo(john);
		assertThat(moderator.getGuest()).isEqualTo(guest);
		Guest guest = guestRepository.findByLsUuid(moderator.getGuest().getLsUuid());
		assertThat(guest.getModerators()).size().isEqualTo(1);
		moderator = moderatorService.delete(root, root, moderator);
		String modearotUuid = moderator.getUuid();
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			moderatorService.find(root, root, modearotUuid);
		});
		Assertions.assertEquals(BusinessErrorCode.GUEST_MODERATOR_CANNOT_FIND, exception.getErrorCode());
		guest = guestRepository.findByLsUuid(guest.getLsUuid());
		assertThat(guest.getModerators()).size().isEqualTo(0);
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testFindModeratorsByGuest() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Moderator moderator1 = moderatorService.create(root, root, new Moderator(ModeratorRole.ADMIN, john, guest));
		assertThat(moderator1).isNotNull();
		assertThat(moderator1.getAccount()).isEqualTo(john);
		assertThat(moderator1.getGuest()).isEqualTo(guest);
		Moderator moderator2 = moderatorService.create(root, root, new Moderator(ModeratorRole.ADMIN, jane, guest));
		assertThat(moderator2).isNotNull();
		assertThat(moderator2.getAccount()).isEqualTo(jane);
		assertThat(moderator2.getGuest()).isEqualTo(guest);
		Guest guest = guestRepository.findByLsUuid(moderator1.getGuest().getLsUuid());
		assertThat(guest.getModerators()).size().isEqualTo(2);
		List<Moderator> moderators = moderatorService.findAllByGuest(root, root, guest.getLsUuid(), null, null);
		assertThat(moderators.size()).isEqualTo(2);
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
