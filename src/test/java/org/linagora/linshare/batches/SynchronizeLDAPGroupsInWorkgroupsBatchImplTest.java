/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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
package org.linagora.linshare.batches;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.domain.constants.GroupProviderType;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.LdapGroupProvider;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.dto.GroupLdapPatternDto;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.runner.BatchRunner;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.GroupLdapPatternService;
import org.linagora.linshare.core.service.GroupProviderService;
import org.linagora.linshare.core.service.RemoteServerService;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml", 
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-mongo-init.xml",
		"classpath:springContext-batches.xml",
		"classpath:springContext-test.xml",
		})
public class SynchronizeLDAPGroupsInWorkgroupsBatchImplTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private BatchRunner batchRunner;

	@Autowired
	@Qualifier("synchronizeLDAPGroupsInWorkgroupsBatch")
	private GenericBatch synchroLdapGroupsBatch;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	AbstractDomainService abstractDomainService;

	@Autowired
	RemoteServerService ldapConnectionService;

	@Autowired
	GroupLdapPatternService groupPatternService;

	@Autowired
	private GroupProviderService groupProviderService;

	private User root;

	public SynchronizeLDAPGroupsInWorkgroupsBatchImplTest() {
		super();
	}

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		root = userRepository.findByLoginAndDomain(LinShareTestConstants.ROOT_DOMAIN, LinShareTestConstants.ROOT_ACCOUNT);
		GroupLdapPatternDto groupPatternDto = new GroupLdapPatternDto();
		groupPatternDto.setDescription("description");
		groupPatternDto.setLabel("New Pattern");
		groupPatternDto.setGroupMember("member");
		groupPatternDto.setGroupName("cn");
		groupPatternDto.setGroupPrefix("workgroup-");
		groupPatternDto.setMemberFirstName("givenName");
		groupPatternDto.setMemberLastName("sn");
		groupPatternDto.setMemberMail("mail");
		groupPatternDto
				.setSearchAllGroupsQuery("ldap.search(baseDn, \"(&(objectClass=groupOfNames)(cn=workgroup-*))\");");
		groupPatternDto.setSearchGroupQuery(
				"ldap.search(baseDn, \"(&(objectClass=groupOfNames)(cn=workgroup-\" + pattern + \"))\");");
		groupPatternDto.setSearchPageSize(100);
		GroupLdapPattern groupPattern = groupPatternService.create(root, new GroupLdapPattern(groupPatternDto));
		LdapConnection connection = ldapConnectionService
				.create(new LdapConnection("Ldap Groups", "ldap://localhost:33389", "anonymous"));
		LdapGroupProvider groupProvider = new LdapGroupProvider(groupPattern, "ou=groups,dc=linshare,dc=org", connection, false);
		groupProvider.setType(GroupProviderType.LDAP_PROVIDER);
		groupProvider = groupProviderService.create(groupProvider);
//		AbstractDomain topDomain = abstractDomainService.findById(LoadingServiceTestDatas.topDomainName);
//		topDomain.setGroupProvider(groupProvider);
//		topDomain = abstractDomainService.updateDomain(datas.getRoot(), topDomain);
		AbstractDomain domain = abstractDomainService.findById(LinShareTestConstants.ROOT_DOMAIN);
		domain.setGroupProvider(groupProvider);
		domain = abstractDomainService.updateDomain(root, domain);
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testBatch() throws BusinessException, JobExecutionException {
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(synchroLdapGroupsBatch);
		Assertions.assertTrue(batchRunner.execute(batches), "At least one batch failed.");
	}

}
