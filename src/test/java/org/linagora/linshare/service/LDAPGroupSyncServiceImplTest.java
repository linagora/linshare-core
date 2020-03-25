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
package org.linagora.linshare.service;

import java.io.IOException;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.GroupProviderType;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.LdapGroupProvider;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.LdapGroupsBatchResultContext;
import org.linagora.linshare.core.ldap.service.SharedSpaceMemberService;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.GroupLdapPatternService;
import org.linagora.linshare.core.service.GroupProviderService;
import org.linagora.linshare.core.service.InitMongoService;
import org.linagora.linshare.core.service.LDAPGroupSyncService;
import org.linagora.linshare.core.service.LdapConnectionService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.core.service.impl.LDAPGroupSyncServiceImpl;
import org.linagora.linshare.ldap.LdapGroupMemberObject;
import org.linagora.linshare.ldap.LdapGroupObject;
import org.linagora.linshare.ldap.Role;
import org.linagora.linshare.mongo.entities.SharedSpaceLDAPGroup;
import org.linagora.linshare.mongo.entities.SharedSpaceLDAPGroupMember;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.server.embedded.ldap.LdapServerRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.subethamail.wiser.Wiser;

import com.beust.jcommander.internal.Lists;

@ExtendWith(SpringExtension.class)
@ExtendWith(LdapServerRule.class)
@Sql({ "/import-tests-fake-domains.sql" })
@Transactional
@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-mongo-java-server.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-ldap.xml"
})
@DirtiesContext
public class LDAPGroupSyncServiceImplTest {

	@Autowired
	@Qualifier("ldapGroupSyncService")
	LDAPGroupSyncService syncService;

	@Autowired
	@Qualifier("ldapGroupSyncService")
	LDAPGroupSyncServiceImpl syncServiceImpl;

	@Autowired
	SharedSpaceNodeService nodeService;

	@Autowired
	SharedSpaceMemberService memberService;

	@Autowired
	private GroupProviderService groupProviderService;

	@Autowired
	LdapConnectionService ldapConnectionService;

	@Autowired
	GroupLdapPatternService groupPatternService;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	AbstractDomainService abstractDomainService;

	@Autowired
	private InitMongoService init;

	private Wiser wiser;

	private LdapConnection ldapConnection;

	private GroupLdapPattern groupPattern;

	private Map<String, LdapAttribute> attributes;

	private String baseDn;

	private SystemAccount systemAccount;

	private AbstractDomain domain;

	private static final Logger logger = LoggerFactory
			.getLogger(LDAPGroupSyncServiceImpl.class);

	public LDAPGroupSyncServiceImplTest() {
		super();
		wiser = new Wiser(2525);
	}

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		wiser.start();
		init.init();
		systemAccount = userRepository.getBatchSystemAccount();
		Account root = userRepository.findByMailAndDomain(LinShareTestConstants.ROOT_DOMAIN, LinShareTestConstants.ROOT_ACCOUNT);
		domain = abstractDomainService.findById(LinShareTestConstants.TOP_DOMAIN);
		attributes = new HashMap<String, LdapAttribute>();
		attributes.put(GroupLdapPattern.GROUP_NAME, new LdapAttribute(GroupLdapPattern.GROUP_NAME, "cn"));
		attributes.put(GroupLdapPattern.GROUP_MEMBER, new LdapAttribute(GroupLdapPattern.GROUP_MEMBER, "member"));
		attributes.put(GroupLdapPattern.MEMBER_MAIL, new LdapAttribute(GroupLdapPattern.MEMBER_MAIL, "mail"));
		attributes.put(GroupLdapPattern.MEMBER_FIRST_NAME,
				new LdapAttribute(GroupLdapPattern.MEMBER_FIRST_NAME, "givenName"));
		attributes.put(GroupLdapPattern.MEMBER_LAST_NAME, new LdapAttribute(GroupLdapPattern.MEMBER_LAST_NAME, "sn"));

		groupPattern = new GroupLdapPattern();
		groupPattern.setLabel("LabelGroup pattern");
		groupPattern.setDescription("description");
		groupPattern.setAttributes(attributes);
		groupPattern.setSearchAllGroupsQuery("ldap.search(baseDn, \"(&(objectClass=groupOfNames)(cn=workgroup-*))\");");
		groupPattern.setSearchGroupQuery(
				"ldap.search(baseDn, \"(&(objectClass=groupOfNames)(cn=workgroup-\" + pattern + \"))\");");
		groupPattern.setSearchPageSize(100);
		groupPattern.setGroupPrefix("workgroup-");
		baseDn = "dc=linshare,dc=org";
		groupPattern = groupPatternService.create(root, groupPattern);
		ldapConnection = new LdapConnection("testldap", "ldap://localhost:33389", "anonymous");
		ldapConnection = ldapConnectionService.create(ldapConnection);
		LdapGroupProvider groupProvider = new LdapGroupProvider(groupPattern, "ou=groups,dc=linshare,dc=org", ldapConnection, false);
		groupProvider.setType(GroupProviderType.LDAP_PROVIDER);
		groupProvider = groupProviderService.create(groupProvider);
		domain.setGroupProvider(groupProvider);
		domain = abstractDomainService.updateDomain(root, domain);
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
	public void testCreateLDAPGroupFromLDAPGroupObject() {
		systemAccount = userRepository.getBatchSystemAccount();
		Date syncDate = new Date();
		LdapGroupObject ldapGroupObject = new LdapGroupObject();
		LdapGroupsBatchResultContext resultContext = new LdapGroupsBatchResultContext(domain);
		ldapGroupObject.setName("wg-3");
		ldapGroupObject.setExternalId("cn=workgroup-wg-3,ou=Groups3,dc=linshare,dc=org");
		ldapGroupObject.setMembers(Lists.newArrayList());
		ldapGroupObject.setPrefix("prefix");
		SharedSpaceLDAPGroup group = syncService.createOrUpdateLDAPGroup(systemAccount, domain, ldapGroupObject, syncDate,
				resultContext, NodeType.WORK_GROUP);
		Assertions.assertNotNull(group, "The group has not been found");
		Assertions.assertEquals("cn=workgroup-wg-3,ou=Groups3,dc=linshare,dc=org",
				group.getExternalId(), "The externalId do not match");
		Assertions.assertEquals(syncDate, group.getSyncDate(), "The given syncDate is not the same as the found one");
		syncServiceImpl.deleteLDAPGroup(systemAccount, group);
	}

	@Test
	public void testCreateLDAPGroupFromLDAPGroupObjectSpecialCharInName() {
		systemAccount = userRepository.getBatchSystemAccount();
		Date syncDate = new Date();
		LdapGroupObject ldapGroupObject = new LdapGroupObject();
		LdapGroupsBatchResultContext resultContext = new LdapGroupsBatchResultContext(domain);
		ldapGroupObject.setName("EP_TEST_v233<script>alert(document.cookie)</script>");
		ldapGroupObject.setExternalId("cn=workgroup-EP_TEST_v233<script>alert(document.cookie)</script>,ou=Groups3,dc=linshare,dc=org");
		ldapGroupObject.setMembers(Lists.newArrayList());
		ldapGroupObject.setPrefix("prefix");
		SharedSpaceLDAPGroup group = syncService.createOrUpdateLDAPGroup(systemAccount, domain, ldapGroupObject, syncDate,
				resultContext);
		Assertions.assertNotNull(group, "The group has not been found");
		Assertions.assertEquals("cn=workgroup-EP_TEST_v233<script>alert(document.cookie)</script>,ou=Groups3,dc=linshare,dc=org",
				group.getExternalId(), "The externalId do not match");
		Assertions.assertEquals(syncDate, group.getSyncDate(), "The given syncDate is not the same as the found one");
		Assertions.assertEquals(group.getName(), "EP_TEST_v233");
		syncServiceImpl.deleteLDAPGroup(systemAccount, group);
	}

	@Test
	@DirtiesContext
	public void testCreateLDAPMemberFromLDAPGroupMember() {
		systemAccount = userRepository.getBatchSystemAccount();
		Date syncDate = new Date();
		LdapGroupObject ldapGroupObject = new LdapGroupObject();
		LdapGroupsBatchResultContext resultContext = new LdapGroupsBatchResultContext(domain);
		ldapGroupObject.setName("wg-3");
		ldapGroupObject.setExternalId("cn=workgroup-wg-3,ou=Groups3,dc=linshare,dc=org");
		ldapGroupObject.setMembers(Lists.newArrayList());
		ldapGroupObject.setPrefix("prefix");
		LdapGroupMemberObject ldapGroupMemberObject = new LdapGroupMemberObject("John", "Doe", "user1@linshare.org",
				"uid=user1,ou=People,dc=linshare,dc=org");
		ldapGroupMemberObject.setRole(Role.CONTRIBUTOR);
		SharedSpaceLDAPGroup group = syncService.createOrUpdateLDAPGroup(systemAccount, domain, ldapGroupObject, syncDate,
				resultContext, NodeType.WORK_GROUP);
		Assertions.assertNotNull(group, "The group has not been found");
		Assertions.assertEquals("cn=workgroup-wg-3,ou=Groups3,dc=linshare,dc=org",
				group.getExternalId(), "The externalId do not match");
		Assertions.assertEquals(syncDate, group.getSyncDate(), "The given syncDate is not the same as the found one");
		SharedSpaceLDAPGroupMember member = syncService.createOrUpdateLDAPGroupMember(systemAccount, domain.getUuid(), group,
				ldapGroupMemberObject, syncDate, resultContext, domain.getGroupProvider().getSearchInOtherDomains());
		Assertions.assertNotNull(member, "The member has not been found");
		Assertions.assertEquals("uid=user1,ou=People,dc=linshare,dc=org",
				member.getExternalId(), "The externalId do not match");
		Assertions.assertEquals(Role.CONTRIBUTOR.toString(), member.getRole().getName());
		Assertions.assertEquals(syncDate, member.getSyncDate(), "The given syncDate is not the same as the found one");
		syncServiceImpl.deleteLDAPGroup(systemAccount, group);
	}

	@Test
	@DirtiesContext
	public void testCreateLDAPMemberInOtherDomains() {
		systemAccount = userRepository.getBatchSystemAccount();
		Date syncDate = new Date();
		Boolean searchInOtherDomains = true;
		LdapGroupObject ldapGroupObject = new LdapGroupObject();
		LdapGroupsBatchResultContext resultContext = new LdapGroupsBatchResultContext(domain);
		ldapGroupObject.setName("wg-3");
		ldapGroupObject.setExternalId("cn=workgroup-wg-3,ou=Groups3,dc=linshare,dc=org");
		ldapGroupObject.setMembers(Lists.newArrayList());
		ldapGroupObject.setPrefix("prefix");
		LdapGroupMemberObject ldapGroupMemberObject = new LdapGroupMemberObject("John", "Doe", "user1@linshare.org",
				"uid=user1,ou=People,dc=linshare,dc=org");
		ldapGroupMemberObject.setRole(Role.CONTRIBUTOR);
		SharedSpaceLDAPGroup group = syncService.createOrUpdateLDAPGroup(systemAccount, domain, ldapGroupObject, syncDate,
				resultContext, NodeType.WORK_GROUP);
		Assertions.assertNotNull(group, "The group has not been found");
		Assertions.assertEquals("cn=workgroup-wg-3,ou=Groups3,dc=linshare,dc=org",
				group.getExternalId(), "The externalId do not match");
		Assertions.assertEquals(syncDate, group.getSyncDate(), "The given syncDate is not the same as the found one");
		SharedSpaceLDAPGroupMember member = syncService.createOrUpdateLDAPGroupMember(systemAccount, domain.getUuid(), group,
				ldapGroupMemberObject, syncDate, resultContext, searchInOtherDomains);
		Assertions.assertNotNull(member, "The member has not been found");
		Assertions.assertEquals("uid=user1,ou=People,dc=linshare,dc=org",
				member.getExternalId(), "The externalId do not match");
		Assertions.assertEquals(Role.CONTRIBUTOR.toString(), member.getRole().getName());
		Assertions.assertEquals(syncDate, member.getSyncDate(), "The given syncDate is not the same as the found one");
		syncServiceImpl.deleteLDAPGroup(systemAccount, group);
	}

	@Test
	@DirtiesContext
	public void testUpdateLDAPMemberFromLDAPGroupMember() {
		systemAccount = userRepository.getBatchSystemAccount();
		Date syncDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Year.now().getValue() + 1, 1, 1);
		LdapGroupObject ldapGroupObject = new LdapGroupObject();
		LdapGroupsBatchResultContext resultContext = new LdapGroupsBatchResultContext(domain);
		ldapGroupObject.setName("wg-3");
		ldapGroupObject.setExternalId("cn=workgroup-wg-3,ou=Groups3,dc=linshare,dc=org");
		ldapGroupObject.setMembers(Lists.newArrayList());
		ldapGroupObject.setPrefix("prefix");
		LdapGroupMemberObject ldapGroupMemberObject = new LdapGroupMemberObject("John", "Doe", "user1@linshare.org",
				"uid=user1,ou=People,dc=linshare,dc=org");
		ldapGroupMemberObject.setRole(Role.CONTRIBUTOR);
		// Create a group
		SharedSpaceLDAPGroup group = syncService.createOrUpdateLDAPGroup(systemAccount, domain, ldapGroupObject, syncDate,
				resultContext, NodeType.WORK_GROUP);
		// Create a member
		syncService.createOrUpdateLDAPGroupMember(systemAccount, domain.getUuid(), group, ldapGroupMemberObject, syncDate,
				resultContext, domain.getGroupProvider().getSearchInOtherDomains());
		ldapGroupMemberObject.setRole(Role.ADMIN);
		ldapGroupMemberObject.setFirstName("Bob");
		syncDate = calendar.getTime();
		// Update a member
		SharedSpaceLDAPGroupMember updated = syncService.createOrUpdateLDAPGroupMember(systemAccount, domain.getUuid(), group,
				ldapGroupMemberObject, syncDate, resultContext, domain.getGroupProvider().getSearchInOtherDomains());
		Assertions.assertEquals(1, memberService.findByNode(systemAccount, systemAccount, group.getUuid()).size());
		Assertions.assertNotNull(updated, "The member has not been found");
		Assertions.assertEquals("uid=user1,ou=People,dc=linshare,dc=org",
				updated.getExternalId(), "The externalId do not match");
		Assertions.assertEquals(Role.ADMIN.toString(), updated.getRole().getName());
		Assertions.assertEquals(syncDate, updated.getSyncDate(), "The given syncDate is not the same as the found one");
		syncServiceImpl.deleteLDAPGroup(systemAccount, group);
	}

	@Test
	@DirtiesContext
	public void testUpdateLDAPGroupFromLDAPGroupObject() {
		systemAccount = userRepository.getBatchSystemAccount();
		Date syncDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Year.now().getValue() + 1, 1, 1);
		LdapGroupObject ldapGroupObject = new LdapGroupObject();
		LdapGroupsBatchResultContext resultContext = new LdapGroupsBatchResultContext(domain);
		ldapGroupObject.setName("wg-3");
		ldapGroupObject.setExternalId("cn=workgroup-wg-3,ou=Groups3,dc=linshare,dc=org");
		ldapGroupObject.setMembers(Lists.newArrayList());
		ldapGroupObject.setPrefix("prefix");
		SharedSpaceLDAPGroup group = syncService.createOrUpdateLDAPGroup(systemAccount, domain, ldapGroupObject, syncDate,
				resultContext, NodeType.WORK_GROUP);
		Assertions.assertNotNull(group, "The group has not been found");
		Assertions.assertEquals("cn=workgroup-wg-3,ou=Groups3,dc=linshare,dc=org",
				group.getExternalId(), "The externalId do not match");
		Assertions.assertEquals(syncDate, group.getSyncDate(), "The given syncDate is not the same as the found one");
		int nbLDAPGroup = nodeService.findAll(systemAccount, systemAccount).size();
		group = syncService.createOrUpdateLDAPGroup(systemAccount, domain, ldapGroupObject, calendar.getTime(), resultContext, NodeType.WORK_GROUP);
		Assertions.assertTrue(group.getSyncDate().after(syncDate), "The new syncDate is not after the previous syncDate");
		Assertions.assertEquals(nbLDAPGroup,
				nodeService.findAll(systemAccount, systemAccount).size(), "A SharedSpaceLDAPGroup has been added and not updated");
		syncServiceImpl.deleteLDAPGroup(systemAccount, group);
	}

	@Test
	@DirtiesContext
	public void testDeleteNodeFromLDAPGroupSync() {
		systemAccount = userRepository.getBatchSystemAccount();
		Date syncDate = new Date();
		LdapGroupObject ldapGroupObject = new LdapGroupObject();
		LdapGroupsBatchResultContext resultContext = new LdapGroupsBatchResultContext(domain);
		ldapGroupObject.setName("wg-3");
		ldapGroupObject.setExternalId("cn=workgroup-wg-3,ou=Groups3,dc=linshare,dc=org");
		ldapGroupObject.setMembers(Lists.newArrayList());
		ldapGroupObject.setPrefix("prefix");
		SharedSpaceLDAPGroup group = syncService.createOrUpdateLDAPGroup(systemAccount, domain, ldapGroupObject,
				syncDate, resultContext, NodeType.WORK_GROUP);
		Assertions.assertNotNull(group, "The group has not been found");
		Assertions.assertEquals("cn=workgroup-wg-3,ou=Groups3,dc=linshare,dc=org",
				group.getExternalId(), "The externalId do not match");
		Assertions.assertEquals(syncDate, group.getSyncDate(), "The given syncDate is not the same as the found one");
		int nbLDAPGroup = nodeService.findAll(systemAccount, systemAccount).size();
		syncServiceImpl.deleteLDAPGroup(systemAccount, group);
		Assertions.assertEquals(nbLDAPGroup - 1,
				nodeService.findAll(systemAccount, systemAccount).size(), "A SharedSpaceLDAPGroup has not been deleted");
	}

	@Test
	@DirtiesContext
	public void testExecuteBatchWithDatas() throws BusinessException, IOException, NamingException {
		systemAccount = userRepository.getBatchSystemAccount();
		LdapGroupsBatchResultContext resultContext = new LdapGroupsBatchResultContext(domain);
		syncService.executeBatch(systemAccount, domain, ldapConnection, baseDn, groupPattern, resultContext);
		List<SharedSpaceNode> foundNodes = nodeService.findAll(systemAccount, systemAccount);
		for (SharedSpaceNode sharedSpaceNode : foundNodes) {
			List<SharedSpaceMember> foundMembers = memberService.findByNode(systemAccount, systemAccount,
					sharedSpaceNode.getUuid());
			Assertions.assertTrue(foundMembers.size() > 0, "The node has at least a member");
		}
		logger.info(String.format("%d ldap Groups found", foundNodes.size()));
		Assertions.assertTrue(foundNodes.size() > 0, "At least 1 node found");
		for (SharedSpaceNode group : foundNodes) {
			syncServiceImpl.deleteLDAPGroup(systemAccount, (SharedSpaceLDAPGroup) group);
		}
	}

	@Test
	@DirtiesContext
	public void testDeleteGroupByLdapSynchro() throws BusinessException, IOException, NamingException {
		systemAccount = userRepository.getBatchSystemAccount();
		LdapGroupsBatchResultContext resultContext = new LdapGroupsBatchResultContext(domain);
		syncService.executeBatch(systemAccount, domain, ldapConnection, baseDn, groupPattern, resultContext);
		List<SharedSpaceNode> foundNodes = nodeService.findAll(systemAccount, systemAccount);
		Assertions.assertTrue(foundNodes.size() > 0, "At least 1 node found");
		syncService.executeBatch(systemAccount, domain, ldapConnection, "dc=linshare1,dc=org", groupPattern,
				resultContext);
		List<SharedSpaceNode> afterBatchNodes = nodeService.findAll(systemAccount, systemAccount);
		Assertions.assertEquals(foundNodes.size(),
				afterBatchNodes.size(), "Bad number of nodes avec ldap synchro deleting groups");
		for (SharedSpaceNode sharedSpaceNode : foundNodes) {
			List<SharedSpaceMember> foundMembers = memberService.findByNode(systemAccount, systemAccount,
					sharedSpaceNode.getUuid());
			Assertions.assertTrue(foundMembers.size() == 0, "The node has at least a member");
		}
		syncService.executeBatch(systemAccount, domain, ldapConnection, baseDn, groupPattern, resultContext);
		foundNodes = nodeService.findAll(systemAccount, systemAccount);
		Assertions.assertEquals(foundNodes.size(),
				afterBatchNodes.size(), "Bad number of nodes avec ldap synchro deleting groups");
		for (SharedSpaceNode sharedSpaceNode : foundNodes) {
			List<SharedSpaceMember> foundMembers = memberService.findByNode(systemAccount, systemAccount,
					sharedSpaceNode.getUuid());
			Assertions.assertTrue(foundMembers.size() > 0, "The node has no member");
		}
		logger.info(String.format("%d ldap Groups found", foundNodes.size()));
		for (SharedSpaceNode group : foundNodes) {
			syncServiceImpl.deleteLDAPGroup(systemAccount, (SharedSpaceLDAPGroup) group);
		}
	}
}
