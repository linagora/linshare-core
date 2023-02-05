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
package org.linagora.linshare.mongodb;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.SharedSpaceActionType;
import org.linagora.linshare.core.domain.constants.SharedSpaceResourceType;
import org.linagora.linshare.mongo.repository.SharedSpacePermissionMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-mongo-init.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml" })
public class SharedSpacePermissionNumberTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private SharedSpacePermissionMongoRepository permissionRepo;

	@BeforeEach
	public void init() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testNumberOfPermissions() {
		Long resourceNumber = (long) SharedSpaceResourceType.values().length - 2;
		// we remove DRIVE and WORKGROUP from the resource count because we only kept it for compatibility.
		Long actionNumber = (long) SharedSpaceActionType.values().length;
		Long actualPermissionNumber = resourceNumber * actionNumber - 7;
		/*
		 * The DOWNLOAD and DOWNLOAD_THUMBNAIL actions are not applicable to all
		 * resources, DOWNLOAD ---> FOLDER + FILE and DOWNLOAD_THUMBNAIL ----> FILE, so
		 * we substract 7 wich is the product of this new added perms.
		 */
		Long number = permissionRepo.count();
		Assertions.assertEquals(number, actualPermissionNumber);
	}
}
