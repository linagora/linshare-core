/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009-2018. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.batches;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.domain.constants.GroupProviderType;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
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
import org.linagora.linshare.core.service.InitMongoService;
import org.linagora.linshare.core.service.LdapConnectionService;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.linagora.linshare.utils.LinShareWiser;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.google.common.collect.Lists;

@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml", 
		"classpath:springContext-dao.xml",
		"classpath:springContext-ldap.xml",
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-start-embedded-ldap.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-batches.xml",
		"classpath:springContext-test.xml",
		})
public class SynchronizeLDAPGroupsInWorkgroupsBatchImplTest extends AbstractTransactionalJUnit4SpringContextTests {

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
	LdapConnectionService ldapConnectionService;

	@Autowired
	GroupLdapPatternService groupPatternService;

	@Autowired
	private GroupProviderService groupProviderService;

	@Autowired
	private InitMongoService initService;

	private LinShareWiser wiser;

	private LoadingServiceTestDatas datas;

	public SynchronizeLDAPGroupsInWorkgroupsBatchImplTest() {
		super();
		wiser = new LinShareWiser(2525);
	}

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		initService.init();
		wiser.start();
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		Account root = datas.getRoot();
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
				.setSearchAllGroupsQuery("ldap.search(baseDn, \"(&(objectClass=posixGroup)(cn=workgroup-*))\");");
		groupPatternDto.setSearchGroupQuery(
				"ldap.search(baseDn, \"(&(objectClass=posixGroup)(cn=workgroup-\" + pattern + \"))\");");
		groupPatternDto.setSearchPageSize(100);
		GroupLdapPattern groupPattern = groupPatternService.create(root, new GroupLdapPattern(groupPatternDto));
		LdapConnection connection = ldapConnectionService
				.create(new LdapConnection("Ldap Groups", "ldap://localhost:33389", "anonymous"));
		LdapGroupProvider groupProvider = new LdapGroupProvider(groupPattern, "ou=groups,dc=linshare,dc=org", connection, true,
				true);
		groupProvider.setType(GroupProviderType.LDAP_PROVIDER);
		groupProvider = groupProviderService.create(groupProvider);
//		AbstractDomain topDomain = abstractDomainService.findById(LoadingServiceTestDatas.topDomainName);
//		topDomain.setGroupProvider(groupProvider);
//		topDomain = abstractDomainService.updateDomain(datas.getRoot(), topDomain);
		AbstractDomain domain = abstractDomainService.findById(LoadingServiceTestDatas.sqlDomain);
		domain.setGroupProvider(groupProvider);
		domain = abstractDomainService.updateDomain(datas.getRoot(), domain);
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		wiser.stop();
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testBatch() throws BusinessException, JobExecutionException {
		List<GenericBatch> batches = Lists.newArrayList();
		batches.add(synchroLdapGroupsBatch);
		Assert.assertTrue("At least one batch failed.", batchRunner.execute(batches));
	}

}
