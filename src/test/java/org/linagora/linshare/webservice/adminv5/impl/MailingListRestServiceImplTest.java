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

import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.MailingListDto;
import org.linagora.linshare.server.embedded.ldap.LdapServerRule;
import org.linagora.linshare.webservice.admin.impl.MailingListRestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@ExtendWith(LdapServerRule.class)
@Transactional
@Sql({ "/import-tests-make-user2-admin.sql"})
@Sql({ "/import-test-contact-list.sql"})
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
        "classpath:springContext-batches.xml",
        "classpath:springContext-test.xml" })
public class MailingListRestServiceImplTest {

    @Autowired
    private MailingListRestServiceImpl testee;

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllReturnDomainLabel() {
		Set<MailingListDto> lists = testee.findAll(null, null, null, null);

		assertThat(lists.stream().map(MailingListDto::getDomainLabel).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(LinShareTestConstants.TOP_DOMAIN
						,LinShareTestConstants.TOP_DOMAIN
						,LinShareTestConstants.SUB_DOMAIN
						,LinShareTestConstants.ROOT_DOMAIN);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllRootCanSeeAllLists() {
		assertThat(testee.findAll(null, null, null, null).size()).isEqualTo(4);
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void getAllAdminCanSeeListsInHisDomain() {
		assertThat(testee.findAll(null, null, null, null).size()).isEqualTo(3);
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") //Amy's uuid (simple user)
	public void getAllUserCannotSeeLists() {
		assertThatThrownBy(() -> testee.findAll(null, null, null, null))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not authorized to use this service");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllFilterOnVisibility() {
		Set<MailingListDto> listPublic = testee.findAll(true, null, null, null);
		assertThat(listPublic).isNotEmpty();
		assertThat(listPublic.size()).isEqualTo(3);

		Set<MailingListDto> listPrivate = testee.findAll(false, null, null, null);
		assertThat(listPrivate).isNotEmpty();
		assertThat(listPrivate.size()).isEqualTo(1);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllFilterOnDomain() {
		Set<MailingListDto> topList = testee.findAll(null, LinShareTestConstants.TOP_DOMAIN, null, null);
		assertThat(topList).isNotEmpty();
		assertThat(topList.size()).isEqualTo(2);

		Set<MailingListDto> subList = testee.findAll(null, LinShareTestConstants.SUB_DOMAIN, null, null);
		assertThat(subList).isNotEmpty();
		assertThat(subList.size()).isEqualTo(1);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllFilterOnOwner() {
		String amyUuid = "d896140a-39c0-11e5-b7f9-080027b8274b";
		Set<MailingListDto> amysLists = testee.findAll(null, null, amyUuid, null);
		assertThat(amysLists).isNotEmpty();
		assertThat(amysLists.size()).isEqualTo(3);

		Set<MailingListDto> rootsList = testee.findAll(null, null, LinShareConstants.defaultRootMailAddress, null);
		assertThat(rootsList).isEmpty();
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllFilterOnMember() {
		Set<MailingListDto> ListsWithFelton = testee.findAll(null, null, null, "felton.gumper@linshare.org");
		assertThat(ListsWithFelton).isNotEmpty();
		assertThat(ListsWithFelton.size()).isEqualTo(4);

		Set<MailingListDto> listWithGrant = testee.findAll(null, null, null, "grant.big@linshare.org");
		assertThat(listWithGrant).isNotEmpty();
		assertThat(listWithGrant.size()).isEqualTo(1);

		Set<MailingListDto> listWithNobody = testee.findAll(null, null, null, "nobody");
		assertThat(listWithNobody).isEmpty();
	}
}