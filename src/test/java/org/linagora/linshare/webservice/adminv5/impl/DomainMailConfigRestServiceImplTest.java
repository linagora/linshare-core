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
package org.linagora.linshare.webservice.adminv5.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.impl.DomainServiceImpl;
import org.linagora.linshare.core.service.impl.UserServiceImpl;
import org.linagora.linshare.webservice.adminv5.DomainMailConfigRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;


@ExtendWith(SpringExtension.class)
@Transactional
@Sql({ "/import-tests-make-user2-admin.sql" })
@Sql({ "/import-test-mail-configs.sql" })
@ContextConfiguration(locations = { "classpath:springContext-datasource.xml",
	"classpath:springContext-dao.xml",
	"classpath:springContext-ldap.xml",
	"classpath:springContext-repository.xml",
	"classpath:springContext-mongo.xml",
	"classpath:springContext-service.xml",
	"classpath:springContext-service-miscellaneous.xml",
	"classpath:springContext-rac.xml",
	"classpath:springContext-mongo-init.xml",
	"classpath:springContext-storage-jcloud.xml",
	"classpath:springContext-business-service.xml",
	"classpath:springContext-webservice-adminv5.xml",
	"classpath:springContext-facade-ws-adminv5.xml",
	"classpath:springContext-facade-ws-user.xml",
	"classpath:springContext-webservice-admin.xml",
	"classpath:springContext-facade-ws-admin.xml",
	"classpath:springContext-webservice.xml",
	"classpath:springContext-upgrade-v2-0.xml",
	"classpath:springContext-facade-ws-async.xml",
	"classpath:springContext-task-executor.xml",
	"classpath:springContext-webservice-adminv5.xml",
	"classpath:springContext-batches.xml",
	"classpath:springContext-test.xml" })
public class DomainMailConfigRestServiceImplTest {

	@Autowired
	private DomainServiceImpl domainService;

	@Autowired
	private UserServiceImpl userService;

	@Autowired
	private DomainMailConfigRestService testee;

	private User root;

	private AbstractDomain topDomain;

	private AbstractDomain rootDomain;

	private AbstractDomain subDomain;

	private final String newMailConfig = "f8313520-da11-11ed-afa1-0242ac120002";
	private final String newSubMailConfig = "08c1c3c8-da12-11ed-afa1-0242ac120002";

	@BeforeEach
	public void setUp() {
		root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
		rootDomain = domainService.find(root, LinShareConstants.rootDomainIdentifier);
		topDomain = domainService.find(root, LinShareTestConstants.TOP_DOMAIN);
		subDomain = domainService.find(root, LinShareTestConstants.SUB_DOMAIN);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void assignExistingMailConfig() {
		User root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
		String originalMailConfig = rootDomain.getCurrentMailConfiguration().getUuid();

		testee.assign(rootDomain.getUuid(), originalMailConfig);

		String assignedMailConfig = domainService.find(root, LinShareConstants.rootDomainIdentifier).getCurrentMailConfiguration().getUuid();
		assertThat(assignedMailConfig).isEqualTo(originalMailConfig);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void assignNewMailConfig() {
		String originalMailConfig = topDomain.getCurrentMailConfiguration().getUuid();
		assertThat(originalMailConfig).isNotEqualTo(newMailConfig);

		testee.assign(topDomain.getUuid(), newMailConfig);

		String assignedMailConfig = domainService.find(root, topDomain.getUuid()).getCurrentMailConfiguration().getUuid();
		assertThat(assignedMailConfig).isEqualTo(newMailConfig);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void assignUnexistingMailConfig() {
		assertThatThrownBy(() -> testee.assign(rootDomain.getUuid(), "wrongUUid"))
				.isInstanceOf(BusinessException.class)
				.hasMessage("Can not find mailConfig wrongUUid");
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void assignNewMailConfigAsAdmin() {
		String originalMailConfig = topDomain.getCurrentMailConfiguration().getUuid();
		assertThat(originalMailConfig).isNotEqualTo(newMailConfig);

		testee.assign(topDomain.getUuid(), newMailConfig);

		String assignedMailConfig = domainService.find(root, topDomain.getUuid()).getCurrentMailConfiguration().getUuid();
		assertThat(assignedMailConfig).isEqualTo(newMailConfig);
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void assignNewMailConfigAsAdminToSubDomain() {
		String originalMailConfig = topDomain.getCurrentMailConfiguration().getUuid();
		assertThat(originalMailConfig).isNotEqualTo(newMailConfig);

		testee.assign(subDomain.getUuid(), newMailConfig);

		String assignedMailConfig = domainService.find(root, subDomain.getUuid()).getCurrentMailConfiguration().getUuid();
		assertThat(assignedMailConfig).isEqualTo(newMailConfig);
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void assignNewMailConfigAsAdminOnParentForbidden() {
		assertThatThrownBy(() -> testee.assign(rootDomain.getUuid(), newMailConfig))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not allowed to manage domain LinShareRootDomain");
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void assignNewMailConfigFromSubdomainForbidden() {
		assertThatThrownBy(() -> testee.assign(topDomain.getUuid(), newSubMailConfig))
				.isInstanceOf(BusinessException.class)
				.hasMessage("Mail config 08c1c3c8-da12-11ed-afa1-0242ac120002 cannot be added to domain MyDomain");
	}

}
