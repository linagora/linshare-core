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
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailContentDto;
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
@Sql({ "/import-test-mail-content.sql"})
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
public class MailContentRestServiceImplTest {
    @Autowired
    private MailContentRestServiceImpl testee;

	@Autowired
	private DomainServiceImpl domainService;

	@Autowired
	private UserServiceImpl userService;


	private AbstractDomain rootDomain;
	private AbstractDomain topDomain;
	private AbstractDomain subDomain;

	private User root;

	private final String rootPublicContent = "rootPublicContent";
	private final String topPublicContent =   "topPublicContent";
	private final String subPublicContent =   "subPublicContent";
	private final String rootPrivateContent = "rootPrivateContent";
	private final String topPrivateContent =  "topPrivateContent";
	private final String subPrivateContent =  "subPrivateContent";


	@BeforeEach
	public void setUp() {
		root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
		rootDomain = domainService.find(root, LinShareTestConstants.ROOT_DOMAIN);
		topDomain = domainService.find(root, LinShareTestConstants.TOP_DOMAIN);
		subDomain = domainService.find(root, LinShareTestConstants.SUB_DOMAIN);
	}
	
	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllMailContentReturnDomainInfo() {
		Set<MailContentDto> contents = testee.findAll(LinShareTestConstants.ROOT_DOMAIN, true);

		assertThat(List.copyOf(contents)).allMatch(content ->
				content.getDomainLabel().equals(LinShareTestConstants.ROOT_DOMAIN)
				&& content.getDomain().equals(LinShareTestConstants.ROOT_DOMAIN));
	}


	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllRootCanSeeAllRootConfigs() {
		Set<MailContentDto> configs = testee.findAll(rootDomain.getUuid(), true);

		assertThat(configs.stream().map(MailContentDto::getUuid).collect(Collectors.toList()))
				.contains(rootPublicContent, rootPrivateContent)
				.doesNotContain(topPrivateContent, topPublicContent, subPublicContent, subPrivateContent);
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
		Set<MailContentDto> configs = testee.findAll(topDomain.getUuid(), true);

		assertThat(configs.stream().map(MailContentDto::getUuid).collect(Collectors.toList()))
				.contains(topPublicContent, topPrivateContent)
				.doesNotContain(rootPrivateContent, rootPublicContent, subPrivateContent);
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void getAllAdminCanSeeAllTopConfigs() {
		Set<MailContentDto> configs = testee.findAll(topDomain.getUuid(), true);

		assertThat(configs.stream().map(MailContentDto::getUuid).collect(Collectors.toList()))
				.contains(topPublicContent, topPrivateContent)
				.doesNotContain(rootPrivateContent, rootPublicContent, subPublicContent, subPrivateContent);
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
		Set<MailContentDto> configs = testee.findAll(subDomain.getUuid(), true);

		assertThat(configs.stream().map(MailContentDto::getUuid).collect(Collectors.toList()))
				.contains(subPublicContent, subPrivateContent)
				.doesNotContain(rootPrivateContent, rootPublicContent, topPublicContent, topPrivateContent);
	}

	@Test
	@WithMockUser("d896140a-39c0-11e5-b7f9-080027b8274b") // Jane's uuid (admin on top domain 1)
	public void getAllAdminCanSeeAllSubConfigs() {
		Set<MailContentDto> configs = testee.findAll(subDomain.getUuid(), true);

		assertThat(configs.stream().map(MailContentDto::getUuid).collect(Collectors.toList()))
				.contains(subPublicContent, subPrivateContent)
				.doesNotContain(rootPrivateContent, rootPublicContent, topPublicContent, topPrivateContent);
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
				.stream().map(MailContentDto::getUuid).collect(Collectors.toList());
		List<String> onlyOneDomainResults = testee.findAll(rootDomain.getUuid(), false)
				.stream().map(MailContentDto::getUuid).collect(Collectors.toList());

		assertThat(allDomainResults).containsExactlyInAnyOrderElementsOf(onlyOneDomainResults);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllOnlyCurrentDomainDoesNotReturnParentDomains() {
		Set<MailContentDto> configs = testee.findAll(subDomain.getUuid(), true);

		assertThat(configs.stream().map(MailContentDto::getUuid).collect(Collectors.toList()))
				.contains(subPublicContent, subPrivateContent)
				.doesNotContain(rootPrivateContent, rootPublicContent, topPublicContent, topPrivateContent);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllDoesNotReturnPrivateParentConfigs() {
		Set<MailContentDto> configs = testee.findAll(subDomain.getUuid(), false);

		assertThat(configs.stream().map(MailContentDto::getUuid).collect(Collectors.toList()))
				.contains(rootPublicContent, topPublicContent, subPublicContent, subPrivateContent)
				.doesNotContain(rootPrivateContent, topPrivateContent);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void getAllDoesNotReturnChildsConfigs() {
		Set<MailContentDto> configs = testee.findAll(topDomain.getUuid(), false);

		assertThat(configs.stream().map(MailContentDto::getUuid).collect(Collectors.toList()))
				.contains(rootPublicContent, topPublicContent, topPrivateContent)
				.doesNotContain(rootPrivateContent, subPublicContent, subPrivateContent);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress) // Root
	public void createShouldForbidNullType() {
		MailContentDto newMailContent = testee.find(rootPublicContent);
		newMailContent.setMailContentType(null);

		assertThatThrownBy(() -> testee.create(newMailContent))
				.isInstanceOf(BusinessException.class)
				.hasMessage("Mail content type missing or unknown : null");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress) // Root
	public void createShouldForbidEmptyType() {
		MailContentDto newMailContent = testee.find(rootPublicContent);
		newMailContent.setMailContentType("");

		assertThatThrownBy(() -> testee.create(newMailContent))
				.isInstanceOf(BusinessException.class)
				.hasMessage("Mail content type missing or unknown : ");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress) // Root
	public void createShouldForbidWrongType() {
		MailContentDto newMailContent = testee.find(rootPublicContent);
		newMailContent.setMailContentType("not a type");

		assertThatThrownBy(() -> testee.create(newMailContent))
				.isInstanceOf(BusinessException.class)
				.hasMessage("Mail content type missing or unknown : not a type");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress) // Jane's uuid (admin on top domain 1)
	public void createShouldAssignNewUUid() {
		MailContentDto existingMailContent = testee.find(rootPublicContent);

		MailContentDto createdMailContent = testee.create(existingMailContent);

		assertThat(createdMailContent).isNotNull();
		assertThat(createdMailContent.getUuid()).isNotEqualTo(existingMailContent.getUuid());
	}
}