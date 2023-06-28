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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailFooterDto;
import org.linagora.linshare.core.service.impl.DomainServiceImpl;
import org.linagora.linshare.core.service.impl.UserServiceImpl;
import org.linagora.linshare.server.embedded.ldap.LdapServerRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@ExtendWith(LdapServerRule.class)
@Transactional
@Sql({ "/import-tests-make-user2-admin.sql"})
@Sql({ "/import-test-mail-footers.sql"})
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
public class MailFooterRestServiceImplTest {

    @Autowired
    private DomainServiceImpl domainService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private MailFooterRestServiceImpl testee;

	private AbstractDomain rootDomain;
	private AbstractDomain topDomain;
	private AbstractDomain subDomain;

	private User root;
	private User adminUser;
	private User simpleUser;

	private final String rootPublicFooter = "e85f4a22-8cf2-11e3-8a7a-5404a683a462";
	private final String rootPublicFooter2 = "f871dfec-21f8-4f7f-99f2-eb2d4afb7ad6";
	private final String topPublicFooter = "2bd363a3-6431-41e6-8093-96f71a7f5fc4";
	private final String subPublicFooter = "e585cc07-7fa8-4cb8-87a6-a768ee037a56";
	private final String rootPrivateFooter = "5886053d-b58f-4424-a131-2fc27b2e5d56";
	private final String topPrivateFooter = "20d3f480-117a-4d39-af72-9ef7ea98afb4";
	private final String subPrivateFooter = "c34d689c-7d6a-4552-8d8b-052f06d2c854";

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
	public void getAllShouldReturnDomainName() {
		Set<MailFooterDto> footers = testee.findAll(rootDomain.getUuid(), false);

		assertThat(footers.stream().map(MailFooterDto::getDomainName).collect(Collectors.toList()))
				.allMatch(StringUtils::isNotBlank);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllRootCanSeeAllRootFooters() {
		Set<MailFooterDto> footers = testee.findAll(rootDomain.getUuid(), true);

		assertThat(footers.stream().map(MailFooterDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(rootPublicFooter, rootPublicFooter2, rootPrivateFooter);
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void getAllAdminCannotSeeRootFooters() {
		assertThatThrownBy(() -> testee.findAll(rootDomain.getUuid(), true))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not allowed to manage this domain.");
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") //Amy's uuid (simple user)
	public void getAllUserCannotSeeRootFooters() {
		assertThatThrownBy(() -> testee.findAll(rootDomain.getUuid(), true))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not authorized to use this service");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllRootCanSeeAllTopFooters() {
		Set<MailFooterDto> footers = testee.findAll(topDomain.getUuid(), true);

		assertThat(footers.stream().map(MailFooterDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(topPublicFooter, topPrivateFooter);
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void getAllAdminCanSeeAllTopFooters() {
		Set<MailFooterDto> footers = testee.findAll(topDomain.getUuid(), true);

		assertThat(footers.stream().map(MailFooterDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(topPublicFooter, topPrivateFooter);
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") //Amy's uuid (simple user)
	public void getAllUserCannotSeeTopFooters() {
		assertThatThrownBy(() -> testee.findAll(topDomain.getUuid(), true))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not authorized to use this service");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllRootCanSeeAllSubFooters() {
		Set<MailFooterDto> footers = testee.findAll(subDomain.getUuid(), true);

		assertThat(footers.stream().map(MailFooterDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(subPublicFooter, subPrivateFooter);
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void getAllAdminCanSeeAllSubFooters() {
		Set<MailFooterDto> footers = testee.findAll(subDomain.getUuid(), true);

		assertThat(footers.stream().map(MailFooterDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(subPublicFooter, subPrivateFooter);
	}

	@Test
	@WithMockUser("aebe1b64-39c0-11e5-9fa8-080027b8254j") //Amy's uuid (simple user)
	public void getAllUserCannotSeeSubFooters() {
		assertThatThrownBy(() -> testee.findAll(subDomain.getUuid(), true))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You are not authorized to use this service");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllOnlyCurrentDomainFlagHasNoEffectOnRoot() {
		List<String> allDomainResults = testee.findAll(rootDomain.getUuid(), true)
				.stream().map(MailFooterDto::getUuid).collect(Collectors.toList());
		List<String> onlyOneDomainResults = testee.findAll(rootDomain.getUuid(), false)
				.stream().map(MailFooterDto::getUuid).collect(Collectors.toList());

		assertThat(allDomainResults).containsExactlyInAnyOrderElementsOf(onlyOneDomainResults);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllOnlyCurrentDomainDoesNotReturnParentDomains() {
		Set<MailFooterDto> footers = testee.findAll(subDomain.getUuid(), true);

		assertThat(footers.stream().map(MailFooterDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(subPublicFooter, subPrivateFooter);
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void getAllAdminCanSeePrivateTopFootersWithOnlyCurrentDomainFalse() {
		Set<MailFooterDto> footers = testee.findAll(topDomain.getUuid(), false);

		assertThat(footers.stream().map(MailFooterDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(rootPublicFooter, rootPublicFooter2, topPublicFooter, topPrivateFooter);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllDoesNotReturnPrivateParentFooters() {
		Set<MailFooterDto> footers = testee.findAll(subDomain.getUuid(), false);

		assertThat(footers.stream().map(MailFooterDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(rootPublicFooter, rootPublicFooter2,topPublicFooter, subPublicFooter, subPrivateFooter);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllDoesNotReturnChildsFooters() {
		Set<MailFooterDto> footers = testee.findAll(topDomain.getUuid(), false);

		assertThat(footers.stream().map(MailFooterDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(rootPublicFooter, rootPublicFooter2, topPublicFooter, topPrivateFooter);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllWithoutDomainReturnUsersDomainFooters() {
		Set<MailFooterDto> footers = testee.findAll(null, false);

		assertThat(footers.stream().map(MailFooterDto::getUuid).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(rootPublicFooter, rootPublicFooter2,rootPrivateFooter);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getFooterReturnDomainName() {
		MailFooterDto footer = testee.find(rootPrivateFooter);

		assertThat(footer.getDomainName()).isEqualTo(rootDomain.getLabel());
		assertThat(footer.getDomain()).isEqualTo(rootDomain.getUuid());
	}


}