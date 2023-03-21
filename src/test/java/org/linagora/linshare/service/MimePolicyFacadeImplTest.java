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
package org.linagora.linshare.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MimePolicyFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.MimePolicyDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-facade-ws-admin.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-test.xml" })
public class MimePolicyFacadeImplTest {

	private static Logger mimePolicyDto = LoggerFactory.getLogger(MimePolicyFacadeImplTest.class);

	@Autowired
	MimePolicyFacade mimePolicyFacade;

	@BeforeEach
	public void setUp() throws BusinessException {
		mimePolicyDto.debug(LinShareTestConstants.BEGIN_SETUP);
		mimePolicyDto.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws BusinessException {
		mimePolicyDto.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		mimePolicyDto.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	@WithMockUser(LinShareTestConstants.ROOT_ACCOUNT)
	public void findAll() {
		Set<MimePolicyDto> mimePolicies = mimePolicyFacade.findAll("LinShareRootDomain", false);

		assertThat(mimePolicies).isNotNull();
		assertThat(mimePolicies.size()).isEqualTo(1);
		Optional<MimePolicyDto> mimePolicyDto = mimePolicies.stream().findFirst();
		assertThat(mimePolicyDto.isPresent()).isTrue();
		MimePolicyDto dto = mimePolicyDto.get();
		assertThat(dto).isNotNull();
		assertThat(dto.getUuid()).isEqualTo("3d6d8800-e0f7-11e3-8ec0-080027c0eef0");
		assertThat(dto.getName()).isEqualTo("Default Mime Policy");
		assertThat(dto.getDomainId()).isEqualTo("LinShareRootDomain");
		assertThat(dto.getDomainName()).isEqualTo("LinShareRootDomain");
		assertThat(dto.getCreationDate()).isNotNull();
		assertThat(dto.getModificationDate()).isNotNull();
		assertThat(dto.getMimeTypes()).isNull();
	}
	@Test
	@WithMockUser(LinShareTestConstants.ROOT_ACCOUNT)
	public void findAllOnlyDomain() {
		Set<MimePolicyDto> mimePolicies = mimePolicyFacade.findAll("LinShareRootDomain", true);

		assertThat(mimePolicies).isNotNull();
		assertThat(mimePolicies.size()).isEqualTo(1);
		Optional<MimePolicyDto> mimePolicyDto = mimePolicies.stream().findFirst();
		assertThat(mimePolicyDto.isPresent()).isTrue();
		MimePolicyDto dto = mimePolicyDto.get();
		assertThat(dto).isNotNull();
		assertThat(dto.getUuid()).isEqualTo("3d6d8800-e0f7-11e3-8ec0-080027c0eef0");
		assertThat(dto.getName()).isEqualTo("Default Mime Policy");
		assertThat(dto.getDomainId()).isEqualTo("LinShareRootDomain");
		assertThat(dto.getDomainName()).isEqualTo("LinShareRootDomain");
		assertThat(dto.getCreationDate()).isNotNull();
		assertThat(dto.getModificationDate()).isNotNull();
		assertThat(dto.getMimeTypes()).isNull();
	}
	@Test
	@WithMockUser(LinShareTestConstants.ROOT_ACCOUNT)
	public void findOneFull() {
		MimePolicyDto mimePolicyDto = mimePolicyFacade.find("3d6d8800-e0f7-11e3-8ec0-080027c0eef0", true);

		assertThat(mimePolicyDto).isNotNull();
		assertThat(mimePolicyDto.getUuid()).isEqualTo("3d6d8800-e0f7-11e3-8ec0-080027c0eef0");
		assertThat(mimePolicyDto.getName()).isEqualTo("Default Mime Policy");
		assertThat(mimePolicyDto.getDomainId()).isEqualTo("LinShareRootDomain");
		assertThat(mimePolicyDto.getDomainName()).isEqualTo("LinShareRootDomain");
		assertThat(mimePolicyDto.getCreationDate()).isNotNull();
		assertThat(mimePolicyDto.getModificationDate()).isNotNull();
		assertThat(mimePolicyDto.getMimeTypes()).isNotNull();
		assertThat(mimePolicyDto.getMimeTypes().size()).isEqualTo(0);
	}

	@Test
	@WithMockUser(LinShareTestConstants.ROOT_ACCOUNT)
	public void findOne() {
		MimePolicyDto mimePolicyDto = mimePolicyFacade.find("3d6d8800-e0f7-11e3-8ec0-080027c0eef0", false);

		assertThat(mimePolicyDto).isNotNull();
		assertThat(mimePolicyDto.getUuid()).isEqualTo("3d6d8800-e0f7-11e3-8ec0-080027c0eef0");
		assertThat(mimePolicyDto.getName()).isEqualTo("Default Mime Policy");
		assertThat(mimePolicyDto.getDomainId()).isEqualTo("LinShareRootDomain");
		assertThat(mimePolicyDto.getDomainName()).isEqualTo("LinShareRootDomain");
		assertThat(mimePolicyDto.getCreationDate()).isNotNull();
		assertThat(mimePolicyDto.getModificationDate()).isNotNull();
		assertThat(mimePolicyDto.getMimeTypes()).isNull();
	}
}
