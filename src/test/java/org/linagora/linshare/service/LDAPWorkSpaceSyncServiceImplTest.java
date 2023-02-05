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

import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.constants.WorkSpaceProviderType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.LdapWorkSpaceFilter;
import org.linagora.linshare.core.domain.entities.LdapWorkSpaceProvider;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.job.quartz.LdapGroupsBatchResultContext;
import org.linagora.linshare.core.ldap.service.SharedSpaceMemberService;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.WorkSpaceProviderService;
import org.linagora.linshare.core.service.LDAPWorkSpaceSyncService;
import org.linagora.linshare.core.service.LDAPGroupSyncService;
import org.linagora.linshare.core.service.LdapWorkSpaceFilterService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.core.service.impl.LDAPGroupSyncServiceImpl;
import org.linagora.linshare.core.service.impl.LdapConnectionServiceImpl;
import org.linagora.linshare.ldap.LdapWorkSpaceMemberObject;
import org.linagora.linshare.ldap.LdapGroupObject;
import org.linagora.linshare.ldap.Role;
import org.linagora.linshare.mongo.entities.SharedSpaceLDAPDriveMember;
import org.linagora.linshare.mongo.entities.SharedSpaceLDAPGroup;
import org.linagora.linshare.server.embedded.ldap.LdapServerRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.subethamail.wiser.Wiser;

import com.google.common.collect.Lists;


@ExtendWith(SpringExtension.class)
@ExtendWith(LdapServerRule.class)
@Transactional
@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-mongo-init.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-ldap.xml"
})
public class LDAPWorkSpaceSyncServiceImplTest {

	@Autowired
	@Qualifier("ldapGroupSyncService")
	LDAPGroupSyncService syncService;

	@Autowired
	LDAPWorkSpaceSyncService workSpaceSyncService;

	@Autowired
	SharedSpaceNodeService nodeService;

	@Autowired
	SharedSpaceMemberService memberService;

	@Autowired
	private WorkSpaceProviderService workSpaceProviderService;

	@Autowired
	LdapConnectionServiceImpl ldapConnectionService;

	@Autowired
	LdapWorkSpaceFilterService workSpaceFilterService;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	AbstractDomainService abstractDomainService;

	private Wiser wiser;

	private LdapConnection ldapConnection;

	private LdapWorkSpaceFilter workSpaceFilter;

	private Map<String, LdapAttribute> attributes;

	private String baseDn;

	private LoadingServiceTestDatas datas;

	private SystemAccount systemAccount;

	private AbstractDomain domain;

	@Autowired
	@Qualifier("ldapWorkSpaceSyncService")
	LDAPGroupSyncServiceImpl syncServiceImpl;

	private static final Logger logger = LoggerFactory
			.getLogger(LDAPGroupSyncServiceImpl.class);

	public LDAPWorkSpaceSyncServiceImplTest() {
		super();
		wiser = new Wiser(2525);
	}

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		wiser.start();
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		systemAccount = userRepository.getBatchSystemAccount();
		domain = datas.getUser2().getDomain();
		attributes = new HashMap<String, LdapAttribute>();
		attributes.put(GroupLdapPattern.GROUP_NAME, new LdapAttribute(GroupLdapPattern.GROUP_NAME, "cn"));
		attributes.put(GroupLdapPattern.GROUP_MEMBER, new LdapAttribute(GroupLdapPattern.GROUP_MEMBER, "member"));
		attributes.put(GroupLdapPattern.MEMBER_MAIL, new LdapAttribute(GroupLdapPattern.MEMBER_MAIL, "mail"));
		attributes.put(GroupLdapPattern.MEMBER_FIRST_NAME,
				new LdapAttribute(GroupLdapPattern.MEMBER_FIRST_NAME, "givenName"));
		attributes.put(GroupLdapPattern.MEMBER_LAST_NAME, new LdapAttribute(GroupLdapPattern.MEMBER_LAST_NAME, "sn"));

		workSpaceFilter = new LdapWorkSpaceFilter();
		workSpaceFilter.setLabel("LabelGroup pattern");
		workSpaceFilter.setDescription("description");
		workSpaceFilter.setAttributes(attributes);
		workSpaceFilter.setSearchAllGroupsQuery("ldap.search(baseDn, \"(&(objectClass=groupOfNames)(cn=workspace-*))\");");
		workSpaceFilter.setSearchGroupQuery(
				"ldap.search(baseDn, \"(&(objectClass=groupOfNames)(cn=workspace-\" + pattern + \"))\");");
		workSpaceFilter.setSearchPageSize(100);
		workSpaceFilter.setGroupPrefix("workspace-");
		Account root = datas.getRoot();
		baseDn = "dc=linshare,dc=org";
		workSpaceFilter = workSpaceFilterService.create(root, workSpaceFilter);
		ldapConnection = new LdapConnection("testldap", "ldap://localhost:33389", "anonymous");
		ldapConnection = ldapConnectionService.create(ldapConnection);
		LdapWorkSpaceProvider workSpaceProvider = new LdapWorkSpaceProvider(workSpaceFilter, "ou=groups,dc=linshare,dc=org", ldapConnection, false);
		workSpaceProvider.setType(WorkSpaceProviderType.LDAP_PROVIDER);
		workSpaceProvider = workSpaceProviderService.create(workSpaceProvider);
		domain.setWorkSpaceProvider(workSpaceProvider);
		domain = abstractDomainService.updateDomain(datas.getRoot(), domain);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		systemAccount = null;
		wiser.stop();
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testCreateLDAPWorkSpaceFromLDAPGroupObject() {
		systemAccount = userRepository.getBatchSystemAccount();
		Date syncDate = new Date();
		LdapGroupObject ldapGroupObject = new LdapGroupObject();
		LdapGroupsBatchResultContext resultContext = new LdapGroupsBatchResultContext(domain);
		ldapGroupObject.setName("workspace-1");
		ldapGroupObject.setExternalId("cn=workspace-workspace-1,ou=Groups,dc=linshare,dc=org");
		ldapGroupObject.setMembers(Lists.newArrayList());
		ldapGroupObject.setPrefix("prefix");
		SharedSpaceLDAPGroup workSpace = syncService.createOrUpdateLDAPGroup(systemAccount, domain, ldapGroupObject, syncDate,
				resultContext, NodeType.WORK_SPACE);
		Assertions.assertNotNull(workSpace, "The workSpace has not been found");
		Assertions.assertEquals("cn=workspace-workspace-1,ou=Groups,dc=linshare,dc=org",
				workSpace.getExternalId(), "The externalId do not match");
		Assertions.assertEquals(syncDate, workSpace.getSyncDate(), "The given syncDate is not the same as the found one");
	}

	@Test
	@DirtiesContext
	public void testCreateLDAPMemberFromLDAPWorkSpaceMember() {
		systemAccount = userRepository.getBatchSystemAccount();
		Date syncDate = new Date();
		LdapGroupObject ldapGroupObject = new LdapGroupObject();
		LdapGroupsBatchResultContext resultContext = new LdapGroupsBatchResultContext(domain);
		ldapGroupObject.setName("workspace-1");
		ldapGroupObject.setExternalId("cn=workspace-workspace-1,ou=Groups,dc=linshare,dc=org");
		ldapGroupObject.setMembers(Lists.newArrayList());
		ldapGroupObject.setPrefix("prefix");
		LdapWorkSpaceMemberObject ldapWorkSpaceMemberObject = new LdapWorkSpaceMemberObject("John", "Doe", "user1@linshare.org",
				"uid=user1,ou=People,dc=linshare,dc=org", Role.WORK_SPACE_WRITER, Role.CONTRIBUTOR);
		SharedSpaceLDAPGroup workSpace = syncService.createOrUpdateLDAPGroup(systemAccount, domain, ldapGroupObject, syncDate,
				resultContext, NodeType.WORK_SPACE);
		Assertions.assertNotNull(workSpace, "The workSpace has not been found");
		Assertions.assertEquals("cn=workspace-workspace-1,ou=Groups,dc=linshare,dc=org",
				workSpace.getExternalId(), "The externalId do not match");
		Assertions.assertEquals(syncDate, workSpace.getSyncDate(), "The given syncDate is not the same as the found one");
		SharedSpaceLDAPDriveMember member = workSpaceSyncService.createOrUpdateLDAPWorkSpaceMember(systemAccount,
				domain.getUuid(), workSpace, ldapWorkSpaceMemberObject, syncDate, resultContext,
				domain.getWorkSpaceProvider().getSearchInOtherDomains());
		Assertions.assertNotNull(member, "The member has not been found");
		Assertions.assertEquals("uid=user1,ou=People,dc=linshare,dc=org",
				member.getExternalId(), "The externalId do not match");
		Assertions.assertEquals(Role.CONTRIBUTOR.toString(), member.getNestedRole().getName());
		Assertions.assertEquals(syncDate, member.getSyncDate(), "The given syncDate is not the same as the found one");
	}

	@Test
	@DirtiesContext
	public void testCreateLDAPMemberInOtherDomains() {
		systemAccount = userRepository.getBatchSystemAccount();
		Date syncDate = new Date();
		Boolean searchInOtherDomains = true;
		LdapGroupObject ldapGroupObject = new LdapGroupObject();
		LdapGroupsBatchResultContext resultContext = new LdapGroupsBatchResultContext(domain);
		ldapGroupObject.setName("workspace-1");
		ldapGroupObject.setExternalId("cn=workspace-workspace-1,ou=Groups,dc=linshare,dc=org");
		ldapGroupObject.setMembers(Lists.newArrayList());
		ldapGroupObject.setPrefix("prefix");
		LdapWorkSpaceMemberObject ldapWorkSpaceMemberObject = new LdapWorkSpaceMemberObject("John", "Doe", "user1@linshare.org",
				"uid=user1,ou=People,dc=linshare,dc=org", Role.WORK_SPACE_WRITER, Role.CONTRIBUTOR);
		SharedSpaceLDAPGroup workSpace = syncService.createOrUpdateLDAPGroup(systemAccount, domain, ldapGroupObject, syncDate,
				resultContext, NodeType.WORK_SPACE);
		Assertions.assertNotNull(workSpace, "The workSpace has not been found");
		Assertions.assertEquals("cn=workspace-workspace-1,ou=Groups,dc=linshare,dc=org",
				workSpace.getExternalId(), "The externalId do not match");
		Assertions.assertEquals(syncDate, workSpace.getSyncDate(), "The given syncDate is not the same as the found one");
		SharedSpaceLDAPDriveMember member = workSpaceSyncService.createOrUpdateLDAPWorkSpaceMember(systemAccount, domain.getUuid(), workSpace,
				ldapWorkSpaceMemberObject, syncDate, resultContext, searchInOtherDomains);
		Assertions.assertNotNull(member, "The member has not been found");
		Assertions.assertEquals("uid=user1,ou=People,dc=linshare,dc=org",
				member.getExternalId(), "The externalId do not match");
		Assertions.assertEquals(Role.CONTRIBUTOR.toString(), member.getNestedRole().getName());
		Assertions.assertEquals(syncDate, member.getSyncDate(), "The given syncDate is not the same as the found one");
	}

	@Test
	@DirtiesContext
	public void testUpdateLDAPMemberFromLDAPWorkSpaceMember() {
		systemAccount = userRepository.getBatchSystemAccount();
		Date syncDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Year.now().getValue() + 1, 1, 1);
		LdapGroupObject ldapGroupObject = new LdapGroupObject();
		LdapGroupsBatchResultContext resultContext = new LdapGroupsBatchResultContext(domain);
		ldapGroupObject.setName("workspace-1");
		ldapGroupObject.setExternalId("cn=workspace-workspace-1,ou=Groups,dc=linshare,dc=org");
		ldapGroupObject.setMembers(Lists.newArrayList());
		ldapGroupObject.setPrefix("prefix");
		LdapWorkSpaceMemberObject ldapWorkSpaceMemberObject = new LdapWorkSpaceMemberObject("John", "Doe", "user1@linshare.org",
				"uid=user1,ou=People,dc=linshare,dc=org", Role.WORK_SPACE_WRITER, Role.CONTRIBUTOR);
		// Create a workSpace
		SharedSpaceLDAPGroup workSpace = syncService.createOrUpdateLDAPGroup(systemAccount, domain, ldapGroupObject, syncDate,
				resultContext, NodeType.WORK_SPACE);
		// Create a member
		workSpaceSyncService.createOrUpdateLDAPWorkSpaceMember(systemAccount, domain.getUuid(), workSpace,
				ldapWorkSpaceMemberObject, syncDate, resultContext, domain.getWorkSpaceProvider().getSearchInOtherDomains());
		ldapWorkSpaceMemberObject.setNestedRole(Role.ADMIN);
		ldapWorkSpaceMemberObject.setFirstName("Bob");
		syncDate = calendar.getTime();
		// Update a member
		SharedSpaceLDAPDriveMember updated = workSpaceSyncService.createOrUpdateLDAPWorkSpaceMember(systemAccount, domain.getUuid(), workSpace,
				ldapWorkSpaceMemberObject, syncDate, resultContext, domain.getWorkSpaceProvider().getSearchInOtherDomains());
		Assertions.assertEquals(1, memberService.findByNode(systemAccount, systemAccount, workSpace.getUuid()).size());
		Assertions.assertNotNull(updated, "The member has not been found");
		Assertions.assertEquals("uid=user1,ou=People,dc=linshare,dc=org",
				updated.getExternalId(), "The externalId do not match");
		Assertions.assertEquals(Role.ADMIN.toString(), updated.getNestedRole().getName());
		Assertions.assertEquals(syncDate, updated.getSyncDate(), "The given syncDate is not the same as the found one");
	}

	@Test
	@DirtiesContext
	public void testUpdateLDAPWorkSpaceFromLDAPGroupObject() {
		systemAccount = userRepository.getBatchSystemAccount();
		Date syncDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Year.now().getValue() + 1, 1, 1);
		LdapGroupObject ldapGroupObject = new LdapGroupObject();
		LdapGroupsBatchResultContext resultContext = new LdapGroupsBatchResultContext(domain);
		ldapGroupObject.setName("workspace-1");
		ldapGroupObject.setExternalId("cn=workspace-workspace-1,ou=Groups,dc=linshare,dc=org");
		ldapGroupObject.setMembers(Lists.newArrayList());
		ldapGroupObject.setPrefix("prefix");
		SharedSpaceLDAPGroup workSpace = syncService.createOrUpdateLDAPGroup(systemAccount, domain, ldapGroupObject, syncDate,
				resultContext, NodeType.WORK_SPACE);
		Assertions.assertNotNull(workSpace, "The workSpace has not been found");
		Assertions.assertEquals("cn=workspace-workspace-1,ou=Groups,dc=linshare,dc=org",
				workSpace.getExternalId(), "The externalId do not match");
		Assertions.assertEquals(syncDate, workSpace.getSyncDate(), "The given syncDate is not the same as the found one");
		int nbLDAPGroup = nodeService.findAll(systemAccount, systemAccount).size();
		workSpace = syncService.createOrUpdateLDAPGroup(systemAccount, domain, ldapGroupObject, calendar.getTime(), resultContext, NodeType.WORK_SPACE);
		Assertions.assertTrue(workSpace.getSyncDate().after(syncDate), "The new syncDate is not after the previous syncDate");
		Assertions.assertEquals(nbLDAPGroup,
				nodeService.findAll(systemAccount, systemAccount).size(), "A SharedSpaceLDAPGroup has been added and not updated");
	}

	@Test
	@DirtiesContext
	public void testDeleteNodeFromLDAPWorkSpaceSync() {
		systemAccount = userRepository.getBatchSystemAccount();
		Date syncDate = new Date();
		LdapGroupObject ldapGroupObject = new LdapGroupObject();
		LdapGroupsBatchResultContext resultContext = new LdapGroupsBatchResultContext(domain);
		ldapGroupObject.setName("workspace-1");
		ldapGroupObject.setExternalId("cn=workspace-workspace-1,ou=Groups,dc=linshare,dc=org");
		ldapGroupObject.setMembers(Lists.newArrayList());
		ldapGroupObject.setPrefix("prefix");
		LdapWorkSpaceMemberObject ldapWorkSpaceMemberObject = new LdapWorkSpaceMemberObject("John", "Doe", "user1@linshare.org",
				"uid=user1,ou=People,dc=linshare,dc=org", Role.WORK_SPACE_WRITER, Role.CONTRIBUTOR);
		SharedSpaceLDAPGroup workSpace = syncService.createOrUpdateLDAPGroup(systemAccount, domain, ldapGroupObject, syncDate,
				resultContext, NodeType.WORK_SPACE);
		Assertions.assertNotNull(workSpace, "The workSpace has not been found");
		Assertions.assertEquals("cn=workspace-workspace-1,ou=Groups,dc=linshare,dc=org",
				workSpace.getExternalId(), "The externalId do not match");
		Assertions.assertEquals(syncDate, workSpace.getSyncDate(), "The given syncDate is not the same as the found one");
		SharedSpaceLDAPDriveMember member = workSpaceSyncService.createOrUpdateLDAPWorkSpaceMember(systemAccount,
				domain.getUuid(), workSpace, ldapWorkSpaceMemberObject, syncDate, resultContext,
				domain.getWorkSpaceProvider().getSearchInOtherDomains());
		Assertions.assertNotNull(member, "The member has not been found");
		Assertions.assertEquals("uid=user1,ou=People,dc=linshare,dc=org",
				member.getExternalId(), "The externalId do not match");
		int nbLDAPMember = memberService.findAll(systemAccount, systemAccount, workSpace.getUuid()).size();
		Date syncDate2 = new Date();
		syncServiceImpl.deleteOutDatedMembers(systemAccount, workSpace, syncDate2, resultContext);
		Assertions.assertEquals(nbLDAPMember - 1,
				memberService.findAll(systemAccount, systemAccount, workSpace.getUuid()).size(), "A SharedSpaceLDAPDrive member has not been deleted");
	}
}
