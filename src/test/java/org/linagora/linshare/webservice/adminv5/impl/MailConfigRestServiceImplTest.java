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
import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailConfigDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainDto;
import org.linagora.linshare.core.service.impl.DomainServiceImpl;
import org.linagora.linshare.core.service.impl.MailConfigServiceImpl;
import org.linagora.linshare.core.service.impl.UserServiceImpl;
import org.linagora.linshare.server.embedded.ldap.LdapServerRule;
import org.linagora.linshare.webservice.admin.impl.MailConfigRestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@ExtendWith(LdapServerRule.class)
@Transactional
@Sql({ "/import-tests-make-user2-admin.sql"})
@Sql({ "/import-test-mail-configs.sql"})
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
public class MailConfigRestServiceImplTest {

    @Autowired
    private DomainServiceImpl domainService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private MailConfigServiceImpl configService;

    @Autowired
    private MailConfigRestServiceImpl testee;

	private AbstractDomain rootDomain;
	private AbstractDomain topDomain;
	private AbstractDomain subDomain;

	private User root;
	private User adminUser;
	private User simpleUser;

	private final String rootPublicConfig = LinShareConstants.defaultMailConfigIdentifier;
	private final String rootPublicConfig2 = "f8313520-da11-11ed-afa1-0242ac120002";
	private final String topPublicConfig = "a0f1675c-dd32-11ed-b5ea-0242ac120002";
	private final String subPublicConfig = "08c1c3c8-da12-11ed-afa1-0242ac120002";
	private final String rootPrivateConfig = "90553b64-dd2b-11ed-afa1-0242ac120002";
	private final String topPrivateConfig = "98807ba0-dd2b-11ed-afa1-0242ac120002";
	private final String subPrivateConfig = "a059f50e-dd2b-11ed-afa1-0242ac120002";


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
	public void getAllRootCanSeeAllRootConfigs() {
		Set<MailConfigDto> configs = testee.findAll(rootDomain.getUuid(), true);

		assertThat(configs.stream().map(MailConfigDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(rootPublicConfig, rootPublicConfig2,rootPrivateConfig);
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void getAllAdminCannotSeeRootConfigs() {
		assertThatThrownBy(() -> testee.findAll(rootDomain.getUuid(), true))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not allowed to manage this domain.");
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") //Amy's uuid (simple user)
	public void getAllUserCannotSeeRootConfigs() {
		assertThatThrownBy(() -> testee.findAll(rootDomain.getUuid(), true))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not authorized to use this service");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllRootCanSeeAllTopConfigs() {
		Set<MailConfigDto> configs = testee.findAll(topDomain.getUuid(), true);

		assertThat(configs.stream().map(MailConfigDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(topPublicConfig, topPrivateConfig);
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void getAllAdminCanSeeAllTopConfigs() {
		Set<MailConfigDto> configs = testee.findAll(topDomain.getUuid(), true);

		assertThat(configs.stream().map(MailConfigDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(topPublicConfig, topPrivateConfig);
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") //Amy's uuid (simple user)
	public void getAllUserCannotSeeTopConfigs() {
		assertThatThrownBy(() -> testee.findAll(topDomain.getUuid(), true))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not authorized to use this service");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllRootCanSeeAllSubConfigs() {
		Set<MailConfigDto> configs = testee.findAll(subDomain.getUuid(), true);

		assertThat(configs.stream().map(MailConfigDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(subPublicConfig, subPrivateConfig);
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void getAllAdminCanSeeAllSubConfigs() {
		Set<MailConfigDto> configs = testee.findAll(subDomain.getUuid(), true);

		assertThat(configs.stream().map(MailConfigDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(subPublicConfig, subPrivateConfig);
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") //Amy's uuid (simple user)
	public void getAllUserCannotSeeSubConfigs() {
		assertThatThrownBy(() -> testee.findAll(subDomain.getUuid(), true))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not authorized to use this service");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllOnlyCurrentDomainFlagHasNoEffectOnRoot() {
		List<String> allDomainResults = testee.findAll(rootDomain.getUuid(), true)
				.stream().map(MailConfigDto::getUuid).collect(Collectors.toList());
		List<String> onlyOneDomainResults = testee.findAll(rootDomain.getUuid(), false)
				.stream().map(MailConfigDto::getUuid).collect(Collectors.toList());

		assertThat(allDomainResults).containsExactlyInAnyOrderElementsOf(onlyOneDomainResults);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllOnlyCurrentDomainDoesNotReturnParentDomains() {
		Set<MailConfigDto> configs = testee.findAll(subDomain.getUuid(), true);

		assertThat(configs.stream().map(MailConfigDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(subPublicConfig, subPrivateConfig);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllDoesNotReturnPrivateParentConfigs() {
		Set<MailConfigDto> configs = testee.findAll(subDomain.getUuid(), false);

		assertThat(configs.stream().map(MailConfigDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(rootPublicConfig, rootPublicConfig2,topPublicConfig, subPublicConfig, subPrivateConfig);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllDoesNotReturnChildsConfigs() {
		Set<MailConfigDto> configs = testee.findAll(topDomain.getUuid(), false);

		assertThat(configs.stream().map(MailConfigDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(rootPublicConfig, rootPublicConfig2,topPublicConfig, topPrivateConfig);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllWithoutDomainReturnUsersDomainConfigs() {
		Set<MailConfigDto> configs = testee.findAll(null, false);

		assertThat(configs.stream().map(MailConfigDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(rootPublicConfig, rootPublicConfig2,rootPrivateConfig);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllAssociatedDomains() {
		Set<DomainDto> domains = testee.findAllAssociatedDomains(rootPublicConfig);

		assertThat(domains.stream().map(DomainDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder("TopDomain2", "MySubDomain", "LinShareRootDomain", "MyDomain", "GuestDomain");
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void getAllAssociatedDomainsForbiddenForHigherConfig() {
		assertThatThrownBy(() -> testee.findAllAssociatedDomains(rootPublicConfig))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not allowed to manage this mail configuration");
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") //Amy's uuid (simple user)
	public void getAllAssociatedDomainsForbiddenForSimpleUser() {
		assertThatThrownBy(() -> testee.findAllAssociatedDomains(rootPublicConfig))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not authorized to use this service");
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void getAllAssociatedDomainsAdminOnHisDomain() {
		configService.assign(root, topDomain.getUuid(), topPublicConfig);
		configService.assign(root, subDomain.getUuid(), topPublicConfig);
		Set<DomainDto> domains = testee.findAllAssociatedDomains(topPublicConfig);

		assertThat(domains.stream().map(DomainDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder("MySubDomain", "MyDomain");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllAssociatedDomainsReturnValues() {
		configService.assign(root, topDomain.getUuid(), topPublicConfig);
		Set<DomainDto> domains = testee.findAllAssociatedDomains(topPublicConfig);

		assertThat(domains.size()).isEqualTo(1);
		DomainDto domain = domains.stream().findFirst().get();
		assertThat(domain.getName()).isEqualTo("MyDomain");
		assertThat(domain.getUuid()).isEqualTo("MyDomain");
		assertThat(domain.getType()).isEqualTo(DomainType.TOPDOMAIN);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getConfigReturnDomainName() {
		MailConfigDto config = testee.find(rootPublicConfig);

		assertThat(config.getDomainName()).isEqualTo(rootDomain.getLabel());
		assertThat(config.getDomain()).isEqualTo(rootDomain.getUuid());
	}

}