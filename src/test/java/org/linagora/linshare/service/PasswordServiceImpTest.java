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

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.business.service.PasswordService;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-test.xml" })
public class PasswordServiceImpTest {

private static Logger logger = LoggerFactory.getLogger(UserServiceImplTest.class);

	@Autowired
	private PasswordService passwordService;

	@Test
	public void testGeneratePassword(){
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Assertions.assertNotNull(passwordService.generatePassword());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testGetPasswordRules() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Assertions.assertFalse(passwordService.getPasswordRules().isEmpty());
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
