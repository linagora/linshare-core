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
package org.linagora.linshare.repository.hibernate;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.entities.UpgradeTask;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UpgradeTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(SpringExtension.class)
@Transactional
@Sql({
	"/import-upgrade-task-sample.sql",
	"/import-upgrade-task-2_1-sample.sql" })
@ContextConfiguration(locations = {
		"classpath:springContext-test.xml",
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml" })
public class UpgradeTaskRepositoryImplTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UpgradeTaskRepository upgradeTaskRepository;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug("Begin setUp");
		logger.debug("End setUp");
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");
		logger.debug("End tearDown");

	}

	@Test
	public void testFindAllTopDomain() throws BusinessException {
		List<UpgradeTask> list = upgradeTaskRepository.findAll();
		for (UpgradeTask upgradeTask : list) {
			logger.debug(upgradeTask.toString());
		}
		Assertions.assertEquals(17, list.size());
	}

}
