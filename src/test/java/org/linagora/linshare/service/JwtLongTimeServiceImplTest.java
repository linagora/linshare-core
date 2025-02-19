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
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.JwtLongTimeService;
import org.linagora.linshare.core.service.impl.JwtServiceImpl;
import org.linagora.linshare.mongo.entities.PermanentToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.jsonwebtoken.Claims;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-ldap.xml" })
public class JwtLongTimeServiceImplTest {

	private static final Logger logger = LoggerFactory.getLogger(JwtLongTimeServiceImplTest.class);

	private final String TOKEN_LABEL = "token label";

	private final String TOKEN_DESC = "token description";

	@Autowired
	private JwtServiceImpl jwtService;

	@Autowired
	private JwtLongTimeService jwtLongTimeService;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	private User john;

	private User jane;

	private User guest;

	private Account root;

	public JwtLongTimeServiceImplTest() {
		super();
	}

	@BeforeEach
	public void setUp() {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		john = userRepository.findByMail(LinShareTestConstants.JOHN_ACCOUNT);
		jane = userRepository.findByMail(LinShareTestConstants.JANE_ACCOUNT);
		guest = userRepository.findByMail(LinShareTestConstants.GUEST_ACCOUNT);
		root = userRepository.findByDomainAndMail(LinShareTestConstants.ROOT_DOMAIN, LinShareTestConstants.ROOT_ACCOUNT);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void createTokenTest() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		PermanentToken johnToken = new PermanentToken(TOKEN_LABEL, TOKEN_DESC);
		PermanentToken token = jwtLongTimeService.create(john, john, johnToken);
		Claims decode = jwtService.decode(token.getToken());
		logger.debug("Token:" + decode.toString());
		Assertions.assertEquals(john.getMail(), decode.getSubject());
		Assertions.assertEquals(null, decode.getExpiration());
		Assertions.assertEquals(token.getLabel(), TOKEN_LABEL);
		Assertions.assertEquals(token.getDescription(), TOKEN_DESC);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void createTokenByGuestTest() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		PermanentToken token = jwtLongTimeService.create(guest, guest, new PermanentToken(TOKEN_LABEL, TOKEN_DESC));
		Claims decode = jwtService.decode(token.getToken());
		logger.debug("Token:" + decode.toString());
		Assertions.assertEquals(guest.getMail(), decode.getSubject());
		Assertions.assertEquals(null, decode.getExpiration());
		Assertions.assertEquals(token.getLabel(), TOKEN_LABEL);
		Assertions.assertEquals(token.getDescription(), TOKEN_DESC);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testUpdateTokenByGuest() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		PermanentToken token = jwtLongTimeService.create(guest, guest, new PermanentToken(TOKEN_LABEL, TOKEN_DESC));
		Claims decode = jwtService.decode(token.getToken());
		logger.debug("Token:" + decode.toString());
		Assertions.assertEquals(guest.getMail(), decode.getSubject());
		Assertions.assertEquals(null, decode.getExpiration());
		Assertions.assertEquals(token.getLabel(), TOKEN_LABEL);
		Assertions.assertEquals(token.getDescription(), TOKEN_DESC);
		token.setLabel("updated label");
		token.setDescription("updated description");
		jwtLongTimeService.update(guest, guest, token.getUuid(), token);
		Assertions.assertEquals(token.getLabel(), "updated label");
		Assertions.assertEquals(token.getDescription(), "updated description");
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testDeleteTokenByGuest() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		PermanentToken token = jwtLongTimeService.create(guest, guest, new PermanentToken(TOKEN_LABEL, TOKEN_DESC));
		Claims decode = jwtService.decode(token.getToken());
		logger.debug("Token:" + decode.toString());
		Assertions.assertEquals(guest.getMail(), decode.getSubject());
		Assertions.assertEquals(null, decode.getExpiration());
		Assertions.assertEquals(token.getLabel(), TOKEN_LABEL);
		Assertions.assertEquals(token.getDescription(), TOKEN_DESC);
		PermanentToken deletedToken = jwtLongTimeService.delete(guest, guest, token);
		Assertions.assertEquals(token.getUuid(), deletedToken.getUuid());
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testForbidAdminTokencreationForUser() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		jane.setRole(Role.ADMIN);
		userRepository.update(jane);
		Assertions.assertTrue(jane.hasAdminRole());
		PermanentToken token = new PermanentToken(TOKEN_LABEL, TOKEN_DESC);
		// Forbid the token creation by admin for a user
		BusinessException e = Assertions.assertThrows(BusinessException.class, () -> {
			jwtLongTimeService.create(jane, john, token);
		});
		Assertions.assertEquals(e.getErrorCode(), BusinessErrorCode.JWT_PERMANENT_TOKEN_CAN_NOT_CREATE);
		// Admin can create a token for himself
		PermanentToken createdToken = jwtLongTimeService.create(jane, jane, token);
		Claims decode = jwtService.decode(createdToken.getToken());
		logger.debug("Token:" + decode.toString());
		Assertions.assertEquals(jane.getMail(), decode.getSubject());
		Assertions.assertNull(decode.getExpiration());
		Assertions.assertEquals(createdToken.getLabel(), TOKEN_LABEL);
		Assertions.assertEquals(createdToken.getDescription(), TOKEN_DESC);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void findAllByActorTest() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		PermanentToken janeToken = new PermanentToken(TOKEN_LABEL, null);
		PermanentToken johnToken = new PermanentToken(TOKEN_LABEL, null);
		for (int i = 0; i < 5; i++) {
			jwtLongTimeService.create(jane, jane, janeToken);
			jwtLongTimeService.create(john, john, johnToken);
		}
		List<PermanentToken> mongoEntities = jwtLongTimeService.findAll(jane, jane);
		Assertions.assertEquals(5, mongoEntities.size());
		for (PermanentToken entity : mongoEntities) {
			Assertions.assertEquals(jane.getMail(), entity.getSubject());
			Assertions.assertEquals(entity.getDescription(), null);
		}
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void findTest() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		PermanentToken janeToken = new PermanentToken(TOKEN_LABEL, null);
		janeToken = jwtLongTimeService.create(jane, jane, janeToken);
		PermanentToken found = jwtLongTimeService.find(jane, jane, janeToken.getUuid());
		Assertions.assertEquals(jane.getMail(), found.getSubject());
		jwtLongTimeService.delete(jane, jane, found);
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void findAllByDomainTest() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		PermanentToken johnToken = new PermanentToken(TOKEN_LABEL, TOKEN_DESC);
		int initSize = jwtLongTimeService.findAllByDomain(root, john.getDomain(), false).size();
		for (int i = 0; i < 5; i++) {
			jwtLongTimeService.create(john, john, johnToken);
		}
		List<PermanentToken> mongoEntities = jwtLongTimeService.findAllByDomain(root, john.getDomain(), false);
		Assertions.assertEquals(initSize + 5, mongoEntities.size());
		PermanentToken guestToken = new PermanentToken(TOKEN_LABEL, TOKEN_DESC);
		int initSizeGuest = jwtLongTimeService.findAllByDomain(root, guest.getDomain(), true).size();
		for (int i = 0; i < 5; i++) {
			jwtLongTimeService.create(root, guest, guestToken);
		}
		List<PermanentToken> mongoEntitiesRecursive = jwtLongTimeService.findAllByDomain(root, john.getDomain(), true);
		Assertions.assertEquals(initSize + initSizeGuest + 10, mongoEntitiesRecursive.size());
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void updateTokenTest() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		PermanentToken johnToken = new PermanentToken(TOKEN_LABEL, TOKEN_DESC);
		PermanentToken token = jwtLongTimeService.create(john, john, johnToken);
		Assertions.assertEquals(token.getLabel(), TOKEN_LABEL);
		Assertions.assertEquals(token.getDescription(), TOKEN_DESC);
		token.setLabel("New label");
		token.setDescription("New description");
		PermanentToken updated = jwtLongTimeService.update(john, john, token.getUuid(), token);
		Assertions.assertEquals(updated.getUuid(), token.getUuid());
		Assertions.assertEquals(updated.getLabel(), "New label");
		Assertions.assertEquals(updated.getDescription(), "New description");
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void deleteTokenTest() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		PermanentToken johnToken = new PermanentToken(TOKEN_LABEL, TOKEN_DESC);
		PermanentToken token = jwtLongTimeService.create(john, john, johnToken);
		PermanentToken deleted = jwtLongTimeService.delete(john, john, token);
		Assertions.assertEquals(token.getUuid(), deleted.getUuid());
		Assertions.assertEquals(token.getLabel(), deleted.getLabel());
		Assertions.assertEquals(deleted.getActor().getUuid(), john.getLsUuid());
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void deleteTokenByAdminTest() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		PermanentToken johnToken = new PermanentToken(TOKEN_LABEL, null);
		PermanentToken token = jwtLongTimeService.create(john, john, johnToken);
		PermanentToken deleted = jwtLongTimeService.delete(root, john, token);
		Assertions.assertEquals(token.getUuid(), deleted.getUuid());
		Assertions.assertEquals(token.getLabel(), deleted.getLabel());
		Assertions.assertEquals(deleted.getActor().getUuid(), john.getLsUuid());
		logger.info(LinShareTestConstants.END_TEST);
	}

	@Test
	public void sendMailOnCreateByAdminTest() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		PermanentToken johnToken = new PermanentToken(TOKEN_LABEL, TOKEN_DESC);
		jwtLongTimeService.create(root, john, johnToken);
		logger.info(LinShareTestConstants.END_TEST);
	}

}
