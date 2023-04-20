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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailLayoutDto;
import org.linagora.linshare.core.service.impl.DomainServiceImpl;
import org.linagora.linshare.core.service.impl.UserServiceImpl;
import org.linagora.linshare.server.embedded.ldap.LdapServerRule;
import org.linagora.linshare.webservice.admin.impl.MailLayoutRestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@ExtendWith(LdapServerRule.class)
@Transactional
@Sql({ "/import-tests-make-user2-admin.sql"})
@Sql({ "/import-test-mail-layouts.sql"})
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
public class MailLayoutRestServiceImplTest {

    @Autowired
    private DomainServiceImpl domainService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private MailLayoutRestServiceImpl testee;

	private AbstractDomain rootDomain;
	private AbstractDomain topDomain;
	private AbstractDomain subDomain;

	private User root;
	private User adminUser;
	private User simpleUser;

	private final String rootPublicLayout = "15044750-89d1-11e3-8d50-5404a683a462";
	private final String rootPublicLayout2 = "b7b787ab-6305-458d-99fb-b84885178bd2";
	private final String topPublicLayout = "fe8d86d9-ce27-4355-a539-26fad2b12621";
	private final String subPublicLayout = "782a6b5c-3991-442d-bb11-a5e74149e62a";
	private final String rootPrivateLayout = "36481d51-442a-485f-b6c2-3674a0d2ebc0";
	private final String topPrivateLayout = "8e025cf3-d1fc-4fb4-bf01-6ca2ad800919";
	private final String subPrivateLayout = "1385f33f-cb63-4426-a73e-224c1468363e";

	@BeforeEach
    public void setUp() {
        root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
		rootDomain = domainService.find(root, LinShareTestConstants.ROOT_DOMAIN);
		topDomain = domainService.find(root, LinShareTestConstants.TOP_DOMAIN);
		subDomain = domainService.find(root, LinShareTestConstants.SUB_DOMAIN);
        adminUser = userService.findUserInDB(topDomain.getUuid(), LinShareTestConstants.JANE_ACCOUNT);
        simpleUser = userService.findUserInDB(topDomain.getUuid(), LinShareTestConstants.JOHN_ACCOUNT);
    }

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllRootCanSeeAllRootLayouts() {
		Set<MailLayoutDto> layouts = testee.findAll(rootDomain.getUuid(), true);

		assertThat(layouts.stream().map(MailLayoutDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(rootPublicLayout, rootPublicLayout2,rootPrivateLayout);
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void getAllAdminCannotSeeRootLayouts() {
		assertThatThrownBy(() -> testee.findAll(rootDomain.getUuid(), true))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not allowed to manage this domain.");
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") //Amy's uuid (simple user)
	public void getAllUserCannotSeeRootLayouts() {
		assertThatThrownBy(() -> testee.findAll(rootDomain.getUuid(), true))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not authorized to use this service");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllRootCanSeeAllTopLayouts() {
		Set<MailLayoutDto> layouts = testee.findAll(topDomain.getUuid(), true);

		assertThat(layouts.stream().map(MailLayoutDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(topPublicLayout, topPrivateLayout);
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void getAllAdminCanSeeAllTopLayouts() {
		Set<MailLayoutDto> layouts = testee.findAll(topDomain.getUuid(), true);

		assertThat(layouts.stream().map(MailLayoutDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(topPublicLayout, topPrivateLayout);
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") //Amy's uuid (simple user)
	public void getAllUserCannotSeeTopLayouts() {
		assertThatThrownBy(() -> testee.findAll(topDomain.getUuid(), true))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not authorized to use this service");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllRootCanSeeAllSubLayouts() {
		Set<MailLayoutDto> layouts = testee.findAll(subDomain.getUuid(), true);

		assertThat(layouts.stream().map(MailLayoutDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(subPublicLayout, subPrivateLayout);
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void getAllAdminCanSeeAllSubLayouts() {
		Set<MailLayoutDto> layouts = testee.findAll(subDomain.getUuid(), true);

		assertThat(layouts.stream().map(MailLayoutDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(subPublicLayout, subPrivateLayout);
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") //Amy's uuid (simple user)
	public void getAllUserCannotSeeSubLayouts() {
		assertThatThrownBy(() -> testee.findAll(subDomain.getUuid(), true))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not authorized to use this service");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllOnlyCurrentDomainFlagHasNoEffectOnRoot() {
		List<String> allDomainResults = testee.findAll(rootDomain.getUuid(), true)
				.stream().map(MailLayoutDto::getUuid).collect(Collectors.toList());
		List<String> onlyOneDomainResults = testee.findAll(rootDomain.getUuid(), false)
				.stream().map(MailLayoutDto::getUuid).collect(Collectors.toList());

		assertThat(allDomainResults).containsExactlyInAnyOrderElementsOf(onlyOneDomainResults);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllOnlyCurrentDomainDoesNotReturnParentDomains() {
		Set<MailLayoutDto> layouts = testee.findAll(subDomain.getUuid(), true);

		assertThat(layouts.stream().map(MailLayoutDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(subPublicLayout, subPrivateLayout);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllDoesNotReturnPrivateParentLayouts() {
		Set<MailLayoutDto> layouts = testee.findAll(subDomain.getUuid(), false);

		assertThat(layouts.stream().map(MailLayoutDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(rootPublicLayout, rootPublicLayout2,topPublicLayout, subPublicLayout, subPrivateLayout);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllDoesNotReturnChildsLayouts() {
		Set<MailLayoutDto> layouts = testee.findAll(topDomain.getUuid(), false);

		assertThat(layouts.stream().map(MailLayoutDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(rootPublicLayout, rootPublicLayout2,topPublicLayout, topPrivateLayout);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllWithoutDomainReturnUsersDomainLayouts() {
		Set<MailLayoutDto> layouts = testee.findAll(null, false);

		assertThat(layouts.stream().map(MailLayoutDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(rootPublicLayout, rootPublicLayout2,rootPrivateLayout);
	}
}