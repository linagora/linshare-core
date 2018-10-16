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
package org.linagora.linshare.service;

import java.io.IOException;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.GroupProviderType;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
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
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.GroupLdapPatternService;
import org.linagora.linshare.core.service.GroupProviderService;
import org.linagora.linshare.core.service.InitMongoService;
import org.linagora.linshare.core.service.LDAPGroupSyncService;
import org.linagora.linshare.core.service.LdapConnectionService;
import org.linagora.linshare.core.service.SharedSpaceLDAPGroupMemberService;
import org.linagora.linshare.core.service.SharedSpaceLDAPGroupService;
import org.linagora.linshare.core.service.impl.LDAPGroupSyncServiceImpl;
import org.linagora.linshare.ldap.LdapGroupMemberObject;
import org.linagora.linshare.ldap.LdapGroupObject;
import org.linagora.linshare.ldap.Role;
import org.linagora.linshare.mongo.entities.SharedSpaceLDAPGroup;
import org.linagora.linshare.mongo.entities.SharedSpaceLDAPGroupMember;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.subethamail.wiser.Wiser;

import com.beust.jcommander.internal.Lists;

@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-ldap.xml", 
		"classpath:springContext-start-embedded-ldap.xml"
})
public class LDAPGroupSyncServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	LDAPGroupSyncService syncService;

	@Autowired
	LDAPGroupSyncServiceImpl syncServiceImpl;

	@Autowired
	@Qualifier("sharedSpaceLdapGroupService")
	SharedSpaceLDAPGroupService nodeService;

	@Autowired
	SharedSpaceLDAPGroupMemberService memberService;

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

	private LoadingServiceTestDatas datas;

	private SystemAccount systemAccount;

	private AbstractDomain domain;

	private static final Logger logger = LoggerFactory
			.getLogger(LDAPGroupSyncServiceImpl.class);

	public LDAPGroupSyncServiceImplTest() {
		super();
		wiser = new Wiser(2525);
	}

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		wiser.start();
		init.init();
		datas = new LoadingServiceTestDatas(userRepository);
		datas.loadUsers();
		systemAccount = userRepository.getBatchSystemAccount();
		domain = datas.getUser1().getDomain();
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
		groupPattern.setSearchAllGroupsQuery("ldap.search(baseDn, \"(&(objectClass=posixGroup)(cn=workgroup-*))\");");
		groupPattern.setSearchGroupQuery(
				"ldap.search(baseDn, \"(&(objectClass=posixGroup)(cn=workgroup-\" + pattern + \"))\");");
		groupPattern.setSearchPageSize(100);
		groupPattern.setGroupPrefix("workgroup-");
		Account root = datas.getRoot();
		baseDn = "dc=linshare,dc=org";
		groupPattern = groupPatternService.create(root, groupPattern);
		ldapConnection = new LdapConnection("testldap", "ldap://localhost:33389", "anonymous");
		ldapConnection = ldapConnectionService.create(ldapConnection);
		LdapGroupProvider groupProvider = new LdapGroupProvider(groupPattern, "ou=groups,dc=linshare,dc=org", ldapConnection, false);
		groupProvider.setType(GroupProviderType.LDAP_PROVIDER);
		groupProvider = groupProviderService.create(groupProvider);
		domain.setGroupProvider(groupProvider);
		domain = abstractDomainService.updateDomain(datas.getRoot(), domain);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		wiser.stop();
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testCreateLDAPGroupFromLDAPGroupObject() {
		Date syncDate = new Date();
		LdapGroupObject ldapGroupObject = new LdapGroupObject();
		LdapGroupsBatchResultContext resultContext = new LdapGroupsBatchResultContext(domain);
		ldapGroupObject.setName("wg-3");
		ldapGroupObject.setExternalId("cn=workgroup-wg-3,ou=Groups3,dc=linshare,dc=org");
		ldapGroupObject.setMembers(Lists.newArrayList());
		ldapGroupObject.setPrefix("prefix");
		SharedSpaceLDAPGroup group = syncService.createOrUpdateLDAPGroup(systemAccount, domain, ldapGroupObject, syncDate,
				resultContext);
		Assert.assertNotNull("The group has not been found", group);
		Assert.assertEquals("The externalId do not match", "cn=workgroup-wg-3,ou=Groups3,dc=linshare,dc=org",
				group.getExternalId());
		Assert.assertEquals("The given syncDate is not the same as the found one", syncDate, group.getSyncDate());
		syncServiceImpl.deleteLDAPGroup(systemAccount, group);
	}

	@Test
	public void testCreateLDAPMemberFromLDAPGroupMember() {
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
				resultContext);
		Assert.assertNotNull("The group has not been found", group);
		Assert.assertEquals("The externalId do not match", "cn=workgroup-wg-3,ou=Groups3,dc=linshare,dc=org",
				group.getExternalId());
		Assert.assertEquals("The given syncDate is not the same as the found one", syncDate, group.getSyncDate());
		SharedSpaceLDAPGroupMember member = syncService.createOrUpdateLDAPGroupMember(systemAccount, domain.getUuid(), group,
				ldapGroupMemberObject, syncDate, resultContext, domain.getGroupProvider().getSearchInOtherDomains());
		Assert.assertNotNull("The member has not been found", member);
		Assert.assertEquals("The externalId do not match", "uid=user1,ou=People,dc=linshare,dc=org",
				member.getExternalId());
		Assert.assertEquals(Role.CONTRIBUTOR.toString(), member.getRole().getName());
		Assert.assertEquals("The given syncDate is not the same as the found one", syncDate, member.getSyncDate());
		syncServiceImpl.deleteLDAPGroup(systemAccount, group);
	}

	@Test
	public void testCreateLDAPMemberInOtherDomains() {
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
				resultContext);
		Assert.assertNotNull("The group has not been found", group);
		Assert.assertEquals("The externalId do not match", "cn=workgroup-wg-3,ou=Groups3,dc=linshare,dc=org",
				group.getExternalId());
		Assert.assertEquals("The given syncDate is not the same as the found one", syncDate, group.getSyncDate());
		SharedSpaceLDAPGroupMember member = syncService.createOrUpdateLDAPGroupMember(systemAccount, domain.getUuid(), group,
				ldapGroupMemberObject, syncDate, resultContext, searchInOtherDomains);
		Assert.assertNotNull("The member has not been found", member);
		Assert.assertEquals("The externalId do not match", "uid=user1,ou=People,dc=linshare,dc=org",
				member.getExternalId());
		Assert.assertEquals(Role.CONTRIBUTOR.toString(), member.getRole().getName());
		Assert.assertEquals("The given syncDate is not the same as the found one", syncDate, member.getSyncDate());
		syncServiceImpl.deleteLDAPGroup(systemAccount, group);
	}

	@Test
	public void testUpdateLDAPMemberFromLDAPGroupMember() {
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
				resultContext);
		// Create a member
		syncService.createOrUpdateLDAPGroupMember(systemAccount, domain.getUuid(), group, ldapGroupMemberObject, syncDate,
				resultContext, domain.getGroupProvider().getSearchInOtherDomains());
		ldapGroupMemberObject.setRole(Role.ADMIN);
		ldapGroupMemberObject.setFirstName("Bob");
		syncDate = calendar.getTime();
		// Update a member
		SharedSpaceLDAPGroupMember updated = syncService.createOrUpdateLDAPGroupMember(systemAccount, domain.getUuid(), group,
				ldapGroupMemberObject, syncDate, resultContext, domain.getGroupProvider().getSearchInOtherDomains());
		Assert.assertEquals(1, memberService.findByNode(systemAccount, systemAccount, group.getUuid()).size());
		Assert.assertNotNull("The member has not been found", updated);
		Assert.assertEquals("The externalId do not match", "uid=user1,ou=People,dc=linshare,dc=org",
				updated.getExternalId());
		Assert.assertEquals(Role.ADMIN.toString(), updated.getRole().getName());
		Assert.assertEquals("The given syncDate is not the same as the found one", syncDate, updated.getSyncDate());
		syncServiceImpl.deleteLDAPGroup(systemAccount, group);
	}

	@Test
	public void testUpdateLDAPGroupFromLDAPGroupObject() {
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
				resultContext);
		Assert.assertNotNull("The group has not been found", group);
		Assert.assertEquals("The externalId do not match", "cn=workgroup-wg-3,ou=Groups3,dc=linshare,dc=org",
				group.getExternalId());
		Assert.assertEquals("The given syncDate is not the same as the found one", syncDate, group.getSyncDate());
		int nbLDAPGroup = nodeService.findAll(systemAccount, systemAccount).size();
		group = syncService.createOrUpdateLDAPGroup(systemAccount, domain, ldapGroupObject, calendar.getTime(), resultContext);
		Assert.assertTrue("The new syncDate is not after the previous syncDate", group.getSyncDate().after(syncDate));
		Assert.assertEquals("A SharedSpaceLDAPGroup has been added and not updated", nbLDAPGroup,
				nodeService.findAll(systemAccount, systemAccount).size());
		syncServiceImpl.deleteLDAPGroup(systemAccount, group);
	}

	@Test
	public void testDeleteNodeFromLDAPGroupSync() {
		Date syncDate = new Date();
		LdapGroupObject ldapGroupObject = new LdapGroupObject();
		LdapGroupsBatchResultContext resultContext = new LdapGroupsBatchResultContext(domain);
		ldapGroupObject.setName("wg-3");
		ldapGroupObject.setExternalId("cn=workgroup-wg-3,ou=Groups3,dc=linshare,dc=org");
		ldapGroupObject.setMembers(Lists.newArrayList());
		ldapGroupObject.setPrefix("prefix");
		SharedSpaceLDAPGroup group = syncService.createOrUpdateLDAPGroup(systemAccount, domain, ldapGroupObject,
				syncDate, resultContext);
		Assert.assertNotNull("The group has not been found", group);
		Assert.assertEquals("The externalId do not match", "cn=workgroup-wg-3,ou=Groups3,dc=linshare,dc=org",
				group.getExternalId());
		Assert.assertEquals("The given syncDate is not the same as the found one", syncDate, group.getSyncDate());
		int nbLDAPGroup = nodeService.findAll(systemAccount, systemAccount).size();
		syncServiceImpl.deleteLDAPGroup(systemAccount, group);
		Assert.assertEquals("A SharedSpaceLDAPGroup has not been deleted", nbLDAPGroup - 1,
				nodeService.findAll(systemAccount, systemAccount).size());
	}

	@Test
	public void testExecuteBatchWithDatas() throws BusinessException, IOException, NamingException {
		LdapGroupsBatchResultContext resultContext = new LdapGroupsBatchResultContext(domain);
		syncService.executeBatch(systemAccount, domain, ldapConnection, baseDn, groupPattern, resultContext);
		List<SharedSpaceNode> foundNodes = nodeService.findAll(systemAccount, systemAccount);
		for (SharedSpaceNode sharedSpaceNode : foundNodes) {
			List<SharedSpaceMember> foundMembers = memberService.findByNode(systemAccount, systemAccount,
					sharedSpaceNode.getUuid());
			Assert.assertTrue("The node has at least a member", foundMembers.size() > 0);
		}
		logger.info(String.format("%d ldap Groups found", foundNodes.size()));
		Assert.assertTrue("At least 1 node found", foundNodes.size() > 0);
		for (SharedSpaceNode group : foundNodes) {
			syncServiceImpl.deleteLDAPGroup(systemAccount, (SharedSpaceLDAPGroup) group);
		}
	}

	@Test
	public void testDeleteGroupByLdapSynchro() throws BusinessException, IOException, NamingException {
		LdapGroupsBatchResultContext resultContext = new LdapGroupsBatchResultContext(domain);
		syncService.executeBatch(systemAccount, domain, ldapConnection, baseDn, groupPattern, resultContext);
		List<SharedSpaceNode> foundNodes = nodeService.findAll(systemAccount, systemAccount);
		Assert.assertTrue("At least 1 node found", foundNodes.size() > 0);
		syncService.executeBatch(systemAccount, domain, ldapConnection, "dc=linshare1,dc=org", groupPattern,
				resultContext);
		List<SharedSpaceNode> afterBatchNodes = nodeService.findAll(systemAccount, systemAccount);
		Assert.assertEquals("Bad number of nodes avec ldap synchro deleting groups", foundNodes.size(),
				afterBatchNodes.size());
		for (SharedSpaceNode sharedSpaceNode : foundNodes) {
			List<SharedSpaceMember> foundMembers = memberService.findByNode(systemAccount, systemAccount,
					sharedSpaceNode.getUuid());
			Assert.assertTrue("The node has at least a member", foundMembers.size() == 0);
		}
		syncService.executeBatch(systemAccount, domain, ldapConnection, baseDn, groupPattern, resultContext);
		foundNodes = nodeService.findAll(systemAccount, systemAccount);
		Assert.assertEquals("Bad number of nodes avec ldap synchro deleting groups", foundNodes.size(),
				afterBatchNodes.size());
		for (SharedSpaceNode sharedSpaceNode : foundNodes) {
			List<SharedSpaceMember> foundMembers = memberService.findByNode(systemAccount, systemAccount,
					sharedSpaceNode.getUuid());
			Assert.assertTrue("The node has no member", foundMembers.size() > 0);
		}
		logger.info(String.format("%d ldap Groups found", foundNodes.size()));
		for (SharedSpaceNode group : foundNodes) {
			syncServiceImpl.deleteLDAPGroup(systemAccount, (SharedSpaceLDAPGroup) group);
		}
	}
}
