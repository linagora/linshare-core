/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 *
 * Copyright (C) 2022 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.webservice.adminv5.impl;

import static org.assertj.core.api.Assertions.assertThat;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.DomainPurgeStepEnum;
import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Policy;
import org.linagora.linshare.core.domain.entities.User;
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
}
