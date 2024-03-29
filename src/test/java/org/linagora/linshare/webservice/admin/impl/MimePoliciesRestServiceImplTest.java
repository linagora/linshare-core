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
package org.linagora.linshare.webservice.admin.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.facade.webservice.common.dto.MimePolicyDto;
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
	"classpath:springContext-webservice-adminv4.xml",
	"classpath:springContext-facade-ws-admin.xml",
	"classpath:springContext-facade-ws-user.xml",
	"classpath:springContext-webservice-adminv4.xml",
	"classpath:springContext-facade-ws-admin.xml",
	"classpath:springContext-webservice.xml",
	"classpath:springContext-upgrade-v2-0.xml",
	"classpath:springContext-facade-ws-async.xml",
	"classpath:springContext-task-executor.xml",
	"classpath:springContext-webservice-admin.xml",
	"classpath:springContext-batches.xml",
	"classpath:springContext-test.xml" })
public class MimePoliciesRestServiceImplTest {

	@Autowired
	private MimePolicyRestServiceImpl testee;

	private final String rootMimePolicy = "0d3ff074-d22d-11ed-afa1-0242ac120002";
	private final String subMimePolicy = "7bd723c4-d23a-11ed-afa1-0242ac120002";

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") //admin
	public void getRootMimePolicyAllowedForAdminOfChildDomain() {
		MimePolicyDto mimePolicyDto = testee.find(rootMimePolicy, true);

		assertThat(mimePolicyDto).isNotNull();
		assertThat(mimePolicyDto.getName()).isEqualTo("Second Mime Policy");
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") //admin
	public void adminCanSetUnknownTypeAllowed() {
		MimePolicyDto mimePolicyDto = testee.find(subMimePolicy, true);
		mimePolicyDto.setUnknownTypeAllowed(true);

		MimePolicyDto newMimePolicyDto = testee.update(mimePolicyDto);
		assertThat(newMimePolicyDto).isNotNull();
		assertThat(newMimePolicyDto.isUnknownTypeAllowed()).isTrue();

		MimePolicyDto storedMimePolicyDto = testee.find(subMimePolicy, true);
		assertThat(storedMimePolicyDto).isNotNull();
		assertThat(storedMimePolicyDto.isUnknownTypeAllowed()).isTrue();
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") //admin
	public void adminCreateMimePolicyWithUnknownTypeAllowed() {
		MimePolicyDto mimePolicyDto = new MimePolicyDto();
		mimePolicyDto.setUnknownTypeAllowed(true);
		mimePolicyDto.setName("blacklist mime policy for subdomain");
		mimePolicyDto.setDomainId("MySubDomain");

		MimePolicyDto newMimePolicyDto = testee.create(mimePolicyDto);
		assertThat(newMimePolicyDto).isNotNull();
		assertThat(newMimePolicyDto.isUnknownTypeAllowed()).isTrue();

		MimePolicyDto storedMimePolicyDto = testee.find(newMimePolicyDto.getUuid(), true);
		assertThat(storedMimePolicyDto).isNotNull();
		assertThat(storedMimePolicyDto.isUnknownTypeAllowed()).isTrue();
	}

}
