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
import org.linagora.linshare.webservice.adminv5.DomainMimePolicyRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;


@ExtendWith(SpringExtension.class)
@Transactional
@Sql({ "/import-tests-make-user2-admin.sql" })
@Sql({ "/import-test-mime-policies.sql" })
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
public class DomainMimePolicyRestServiceImplTest {

	@Autowired
	private DomainServiceImpl domainService;

	@Autowired
	private UserServiceImpl userService;

	@Autowired
	private DomainMimePolicyRestService testee;

	private User root;

	private AbstractDomain topDomain;

	private AbstractDomain rootDomain;

	private AbstractDomain subDomain;

	private final String newMimePolicy = "0d3ff074-d22d-11ed-afa1-0242ac120002";
	private final String newSubMimePolicy = "7bd723c4-d23a-11ed-afa1-0242ac120002";

	@BeforeEach
	public void setUp() {
		root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
		rootDomain = domainService.find(root, LinShareConstants.rootDomainIdentifier);
		topDomain = domainService.find(root, LinShareTestConstants.TOP_DOMAIN);
		subDomain = domainService.find(root, LinShareTestConstants.SUB_DOMAIN);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void assignExistingMimePolicy() {
		User root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
		String originalMimePolicy = rootDomain.getMimePolicy().getUuid();

		testee.assign(rootDomain.getUuid(), originalMimePolicy);

		String assignedMimePolicy = domainService.find(root, LinShareConstants.rootDomainIdentifier).getMimePolicy().getUuid();
		assertThat(assignedMimePolicy).isEqualTo(originalMimePolicy);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void assignNewMimePolicy() {
		String originalMimePolicy = topDomain.getMimePolicy().getUuid();
		assertThat(originalMimePolicy).isNotEqualTo(newMimePolicy);

		testee.assign(topDomain.getUuid(), newMimePolicy);

		String assignedMimePolicy = domainService.find(root, topDomain.getUuid()).getMimePolicy().getUuid();
		assertThat(assignedMimePolicy).isEqualTo(newMimePolicy);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void assignUnexistingMimePolicy() {
		assertThatThrownBy(() -> testee.assign(rootDomain.getUuid(), "wrongUUid"))
				.isInstanceOf(BusinessException.class)
				.hasMessage("Can not find mimePolicy with uuid : wrongUUid.");
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void assignNewMimePolicyAsAdmin() {
		String originalMimePolicy = topDomain.getMimePolicy().getUuid();
		assertThat(originalMimePolicy).isNotEqualTo(newMimePolicy);

		testee.assign(topDomain.getUuid(), newMimePolicy);

		String assignedMimePolicy = domainService.find(root, topDomain.getUuid()).getMimePolicy().getUuid();
		assertThat(assignedMimePolicy).isEqualTo(newMimePolicy);
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void assignNewMimePolicyAsAdminToSubDomain() {
		String originalMimePolicy = topDomain.getMimePolicy().getUuid();
		assertThat(originalMimePolicy).isNotEqualTo(newMimePolicy);

		testee.assign(subDomain.getUuid(), newMimePolicy);

		String assignedMimePolicy = domainService.find(root, subDomain.getUuid()).getMimePolicy().getUuid();
		assertThat(assignedMimePolicy).isEqualTo(newMimePolicy);
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void assignNewMimePolicyAsAdminOnParentForbidden() {
		assertThatThrownBy(() -> testee.assign(rootDomain.getUuid(), newMimePolicy))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not allowed to manage domain LinShareRootDomain");
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void assignNewMimePolicyFromSubdomainForbidden() {
		assertThatThrownBy(() -> testee.assign(topDomain.getUuid(), newSubMimePolicy))
				.isInstanceOf(BusinessException.class)
				.hasMessage("Mime policy 7bd723c4-d23a-11ed-afa1-0242ac120002 cannot be added to domain MyDomain");
	}

}
