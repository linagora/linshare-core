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
import static org.linagora.linshare.core.domain.constants.LinShareTestConstants.GUEST_DOMAIN;
import static org.linagora.linshare.core.domain.constants.LinShareTestConstants.SUB_DOMAIN;
import static org.linagora.linshare.core.domain.constants.LinShareTestConstants.TOP_DOMAIN;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.DomainPurgeStepEnum;
import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Policy;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainDto;
import org.linagora.linshare.core.repository.hibernate.FunctionalityRepositoryImpl;
import org.linagora.linshare.core.repository.hibernate.PolicyRepositoryImpl;
import org.linagora.linshare.core.service.impl.DomainServiceImpl;
import org.linagora.linshare.core.service.impl.FunctionalityServiceImpl;
import org.linagora.linshare.core.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.collect.ImmutableSet;


@ExtendWith(SpringExtension.class)
@Transactional
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
public class DomainRestServiceImplTest {

	@Autowired
	private DomainServiceImpl domainService;
	@Autowired
	private FunctionalityServiceImpl functionalityService;
	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private FunctionalityRepositoryImpl functionalityRepository;
	@Autowired
	private PolicyRepositoryImpl policyRepository;

	@Autowired
	private DomainRestServiceImpl testee;

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void deleteShouldDeleteFunctionalities() {
		// Given
		User root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
		AbstractDomain rootDomain = domainService.find(root, LinShareConstants.rootDomainIdentifier);
		AbstractDomain domain = domainService.create(root, "MYTOPDOMAIN", "description", DomainType.TOPDOMAIN, rootDomain);
		String domainUuid = domain.getUuid();
		Functionality rootFunctionality = functionalityService.find(root, domainUuid, "WORK_GROUP__CREATION_RIGHT");

		int initialNumberOfFunctionalities = functionalityRepository.findAll().size();
		Functionality functionality = new Functionality(rootFunctionality.getIdentifier(), rootFunctionality.isSystem(), rootFunctionality.getActivationPolicy(), rootFunctionality.getConfigurationPolicy(), domain);
		Policy activationPolicy = functionality.getActivationPolicy();
		activationPolicy.setStatus(true);
		functionalityService.update(root, domainUuid, functionality);
		domain.setFunctionalities(ImmutableSet.of(functionality));
		domainService.update(root, domainUuid, domain);
		int currentNumberOfFunctionalities = functionalityRepository.findAll().size();
		assertThat(currentNumberOfFunctionalities).isEqualTo(initialNumberOfFunctionalities + 1);

		// When
		testee.delete(domainUuid, DomainDto.getLight(domain));

		// Then
		AbstractDomain deletedDomain = domainService.find(root, domainUuid);
		assertThat(deletedDomain.getPurgeStep()).isEqualTo(DomainPurgeStepEnum.WAIT_FOR_PURGE);

		assertThat(functionalityRepository.findAll()).hasSize(initialNumberOfFunctionalities);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void deleteShouldDeletePolicies() {
		// Given
		User root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
		AbstractDomain rootDomain = domainService.find(root, LinShareConstants.rootDomainIdentifier);
		AbstractDomain domain = domainService.create(root, "MYTOPDOMAIN", "description", DomainType.TOPDOMAIN, rootDomain);
		String domainUuid = domain.getUuid();
		Functionality rootFunctionality = functionalityService.find(root, domainUuid, "WORK_GROUP__CREATION_RIGHT");

		int initialNumberOfPolicies = policyRepository.findAll().size();
		Functionality functionality = new Functionality(rootFunctionality.getIdentifier(), rootFunctionality.isSystem(), rootFunctionality.getActivationPolicy(), rootFunctionality.getConfigurationPolicy(), domain);
		Policy activationPolicy = functionality.getActivationPolicy();
		activationPolicy.setStatus(true);
		functionalityService.update(root, domainUuid, functionality);
		domain.setFunctionalities(ImmutableSet.of(functionality));
		domainService.update(root, domainUuid, domain);
		int currentNumberOfPolicies = policyRepository.findAll().size();
		assertThat(currentNumberOfPolicies).isEqualTo(initialNumberOfPolicies + 2);

		// When
		testee.delete(domainUuid, DomainDto.getLight(domain));

		// Then
		AbstractDomain deletedDomain = domainService.find(root, domainUuid);
		assertThat(deletedDomain.getPurgeStep()).isEqualTo(DomainPurgeStepEnum.WAIT_FOR_PURGE);

		assertThat(policyRepository.findAll()).hasSize(initialNumberOfPolicies);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void createShouldCreateLanguage() {
		// Given
		User root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
		AbstractDomain rootDomain = domainService.find(root, LinShareConstants.rootDomainIdentifier);
		DomainDto domainDto = DomainDto.getUltraLight(rootDomain);
		domainDto.setUuid(null);
		domainDto.setName("new domain");
		domainDto.setType(DomainType.TOPDOMAIN);
		domainDto.setParent(DomainDto.getLight(rootDomain));
		domainDto.setDefaultEmailLanguage(Language.FRENCH);

		DomainDto newDomainDto = testee.create(false, null, domainDto);

		assertThat(newDomainDto).isNotNull();
		assertThat(newDomainDto.getDefaultEmailLanguage()).isEqualTo(Language.FRENCH);
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void createSecondGuestDomainShouldBeForbidden() {
		User root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
		AbstractDomain guestDomain = domainService.find(root, GUEST_DOMAIN);
		AbstractDomain topDomain = domainService.find(root, TOP_DOMAIN);
		DomainDto newGuestDomain = DomainDto.getFull(guestDomain);
		newGuestDomain.setUuid(null);
		newGuestDomain.setName("new guest domain");
		newGuestDomain.setParent(DomainDto.getLight(topDomain));

		assertThatThrownBy(() -> testee.create(false, null, newGuestDomain))
				.isInstanceOf(BusinessException.class)
				.hasMessage("Another guest domain already exist : GuestDomain");
	}
	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void createGuestDomainShouldBeForbiddenOnRoot() {
		User root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
		AbstractDomain rootDomain = domainService.find(root, LinShareConstants.rootDomainIdentifier);
		AbstractDomain guestDomain = domainService.find(root, GUEST_DOMAIN);
		DomainDto newGuestDomain = DomainDto.getFull(guestDomain);
		newGuestDomain.setUuid(null);
		newGuestDomain.setName("new guest domain");
		newGuestDomain.setParent(DomainDto.getLight(rootDomain));

		assertThatThrownBy(() -> testee.create(false, null, newGuestDomain))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You must create a guest domain inside a TopDomain.");
	}

	@Test
	@WithMockUser(LinShareConstants.defaultRootMailAddress)
	public void createGuestDomainShouldBeForbiddenOnSubDomain() {
		User root = userService.findByLsUuid(LinShareConstants.defaultRootMailAddress);
		AbstractDomain subDomain = domainService.find(root, SUB_DOMAIN);
		AbstractDomain guestDomain = domainService.find(root, GUEST_DOMAIN);
		DomainDto newGuestDomain = DomainDto.getFull(guestDomain);
		newGuestDomain.setUuid(null);
		newGuestDomain.setName("new guest domain");
		newGuestDomain.setParent(DomainDto.getLight(subDomain));

		assertThatThrownBy(() -> testee.create(false, null, newGuestDomain))
				.isInstanceOf(BusinessException.class)
				.hasMessage("You must create a guest domain inside a TopDomain.");
	}
}
