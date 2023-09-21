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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.impl.DomainPolicyServiceImpl;
import org.linagora.linshare.core.service.impl.DomainServiceImpl;
import org.linagora.linshare.core.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;


@ExtendWith(SpringExtension.class)
@Transactional
@Sql({ "/import-tests-make-user2-admin.sql" })
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
public class DomainDomainPolicyRestServiceImplTest {

	@Autowired
	private DomainServiceImpl domainService;

	@Autowired
	private DomainPolicyServiceImpl domainPolicyService;

	@Autowired
	private UserServiceImpl userService;

	@Autowired
	private DomainDomainPolicyRestServiceImpl testee;


	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void assignPolicyOnRootForbidden() {
		String newPolicy = domainPolicyService.create(new DomainPolicy("test policy")).getUuid();

		assertThatThrownBy(() -> testee.assign(LinShareConstants.rootDomainIdentifier, newPolicy))
				.isInstanceOf(BusinessException.class)
				.hasMessage("Policies cannot be assigned to root domain.");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void assignPolicyOnTop() {
		User root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
		String newPolicy = domainPolicyService.create(new DomainPolicy("test policy")).getUuid();

		testee.assign(LinShareTestConstants.TOP_DOMAIN, newPolicy);

		String assignedDomainPolicy = domainService.find(root, LinShareTestConstants.TOP_DOMAIN).getPolicy().getUuid();
		assertThat(assignedDomainPolicy).isEqualTo(newPolicy);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void assignPolicyOnSub() {
		User root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
		String newPolicy = domainPolicyService.create(new DomainPolicy("test policy")).getUuid();

		testee.assign(LinShareTestConstants.SUB_DOMAIN, newPolicy);

		String assignedDomainPolicy = domainService.find(root, LinShareTestConstants.SUB_DOMAIN).getPolicy().getUuid();
		assertThat(assignedDomainPolicy).isEqualTo(newPolicy);
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void assignNewDomainPolicyAsAdminForbidden() {
		assertThatThrownBy(() -> testee.assign(LinShareTestConstants.SUB_DOMAIN, "whatever"))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not authorized to use this service");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void assignUnexistingDomainPolicy() {
		assertThatThrownBy(() -> testee.assign(LinShareTestConstants.TOP_DOMAIN, "wrongUUid"))
				.isInstanceOf(BusinessException.class)
				.hasMessage("Policy not found : wrongUUid");
	}
}
