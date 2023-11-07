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
import static org.linagora.linshare.core.domain.constants.LinShareConstants.defaultDomainPolicyIdentifier;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.facade.webservice.admin.dto.DomainPolicyDto;
import org.linagora.linshare.core.service.impl.DomainPolicyServiceImpl;
import org.linagora.linshare.core.service.impl.DomainServiceImpl;
import org.linagora.linshare.core.service.impl.UserServiceImpl;
import org.linagora.linshare.webservice.admin.impl.DomainPolicyRestServiceImpl;
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
public class DomainPolicyRestServiceImplTest {

	@Autowired
	private DomainServiceImpl domainService;

	@Autowired
	private DomainPolicyServiceImpl domainPolicyService;

	@Autowired
	private UserServiceImpl userService;

	@Autowired
	private DomainPolicyRestServiceImpl testee;


	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void policiesShouldReturnDates() {
		Set<DomainPolicyDto> all = testee.findAll();
		assertThat(all).isNotNull();
		assertThat(all).isNotEmpty();
		DomainPolicyDto policy = all.iterator().next();
		assertThat(policy.getCreationDate()).isNotNull();
		assertThat(policy.getModificationDate()).isNotNull();
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void policiesShouldPersistDates() {
		DomainPolicyDto defaultPolicy = testee.find(defaultDomainPolicyIdentifier);
		defaultPolicy.setLabel("test policy");
		defaultPolicy.setCreationDate(null);
		defaultPolicy.setModificationDate(null);

		DomainPolicyDto newPolicy = testee.create(defaultPolicy);

		assertThat(newPolicy).isNotNull();
		assertThat(newPolicy.getCreationDate()).isNotNull();
		assertThat(newPolicy.getModificationDate()).isNotNull();
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void policiesShouldPersistDescription() {
		DomainPolicyDto defaultPolicy = testee.find(defaultDomainPolicyIdentifier);
		defaultPolicy.setLabel("test policy");
		defaultPolicy.setDescription("test policy description");
		defaultPolicy.setCreationDate(null);
		defaultPolicy.setModificationDate(null);

		DomainPolicyDto newPolicy = testee.create(defaultPolicy);

		assertThat(newPolicy).isNotNull();
		assertThat(newPolicy.getDescription()).isEqualTo("test policy description");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void updatePoliciesShouldSetLabelAndDescription() {
		DomainPolicyDto defaultPolicy = testee.find(defaultDomainPolicyIdentifier);
		defaultPolicy.setLabel("test policy");
		defaultPolicy.setDescription("test policy description");
		defaultPolicy.setCreationDate(null);
		defaultPolicy.setModificationDate(null);

		DomainPolicyDto newPolicy = testee.update(defaultPolicy);

		assertThat(newPolicy).isNotNull();
		assertThat(newPolicy.getDescription()).isEqualTo("test policy description");
		assertThat(newPolicy.getLabel()).isEqualTo("test policy");
	}
}
