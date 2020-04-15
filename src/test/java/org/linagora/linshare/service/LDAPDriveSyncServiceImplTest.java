/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009-2020. Contribute to
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
import org.linagora.linshare.core.domain.constants.DriveProviderType;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.LdapDriveProvider;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.job.quartz.LdapGroupsBatchResultContext;
import org.linagora.linshare.core.ldap.service.SharedSpaceMemberService;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.DriveProviderService;
import org.linagora.linshare.core.service.GroupLdapPatternService;
import org.linagora.linshare.core.service.InitMongoService;
import org.linagora.linshare.core.service.LDAPDriveSyncService;
import org.linagora.linshare.core.service.LDAPGroupSyncService;
import org.linagora.linshare.core.service.LdapConnectionService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.core.service.impl.LDAPGroupSyncServiceImpl;
import org.linagora.linshare.ldap.LdapDriveMemberObject;
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
		"classpath:springContext-fongo.xml",
		"classpath:springContext-storage-jcloud.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-ldap.xml"
})
public class LDAPDriveSyncServiceImplTest {

	@Autowired
	@Qualifier("ldapGroupSyncService")
	LDAPGroupSyncService syncService;

	@Autowired
	LDAPDriveSyncService driveSyncService;

	@Autowired
	SharedSpaceNodeService nodeService;

	@Autowired
	SharedSpaceMemberService memberService;

	@Autowired
	private DriveProviderService driveProviderService;

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

	@Autowired
	@Qualifier("ldapDriveSyncService")
	LDAPGroupSyncServiceImpl syncServiceImpl;

	private static final Logger logger = LoggerFactory
			.getLogger(LDAPGroupSyncServiceImpl.class);

	public LDAPDriveSyncServiceImplTest() {
		super();
		wiser = new Wiser(2525);
	}

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		wiser.start();
		init.init();
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

		groupPattern = new GroupLdapPattern();
		groupPattern.setLabel("LabelGroup pattern");
		groupPattern.setDescription("description");
		groupPattern.setAttributes(attributes);
		groupPattern.setSearchAllGroupsQuery("ldap.search(baseDn, \"(&(objectClass=groupOfNames)(cn=drive-*))\");");
		groupPattern.setSearchGroupQuery(
				"ldap.search(baseDn, \"(&(objectClass=groupOfNames)(cn=drive-\" + pattern + \"))\");");
		groupPattern.setSearchPageSize(100);
		groupPattern.setGroupPrefix("drive-");
		Account root = datas.getRoot();
		baseDn = "dc=linshare,dc=org";
		groupPattern = groupPatternService.create(root, groupPattern);
		ldapConnection = new LdapConnection("testldap", "ldap://localhost:33389", "anonymous");
		ldapConnection = ldapConnectionService.create(ldapConnection);
		LdapDriveProvider driveProvider = new LdapDriveProvider(groupPattern, "ou=groups,dc=linshare,dc=org", ldapConnection, false);
		driveProvider.setType(DriveProviderType.LDAP_PROVIDER);
		driveProvider = driveProviderService.create(driveProvider);
		domain.setDriveProvider(driveProvider);
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
	public void testCreateLDAPDriveFromLDAPGroupObject() {
		systemAccount = userRepository.getBatchSystemAccount();
		Date syncDate = new Date();
		LdapGroupObject ldapGroupObject = new LdapGroupObject();
		LdapGroupsBatchResultContext resultContext = new LdapGroupsBatchResultContext(domain);
		ldapGroupObject.setName("drive-1");
		ldapGroupObject.setExternalId("cn=drive-drive-1,ou=Groups,dc=linshare,dc=org");
		ldapGroupObject.setMembers(Lists.newArrayList());
		ldapGroupObject.setPrefix("prefix");
		SharedSpaceLDAPGroup drive = syncService.createOrUpdateLDAPGroup(systemAccount, domain, ldapGroupObject, syncDate,
				resultContext, NodeType.DRIVE);
		Assertions.assertNotNull(drive, "The drive has not been found");
		Assertions.assertEquals("cn=drive-drive-1,ou=Groups,dc=linshare,dc=org",
				drive.getExternalId(), "The externalId do not match");
		Assertions.assertEquals(syncDate, drive.getSyncDate(), "The given syncDate is not the same as the found one");
	}

	@Test
	@DirtiesContext
	public void testCreateLDAPMemberFromLDAPDriveMember() {
		systemAccount = userRepository.getBatchSystemAccount();
		Date syncDate = new Date();
		LdapGroupObject ldapGroupObject = new LdapGroupObject();
		LdapGroupsBatchResultContext resultContext = new LdapGroupsBatchResultContext(domain);
		ldapGroupObject.setName("drive-1");
		ldapGroupObject.setExternalId("cn=drive-drive-1,ou=Groups,dc=linshare,dc=org");
		ldapGroupObject.setMembers(Lists.newArrayList());
		ldapGroupObject.setPrefix("prefix");
		LdapDriveMemberObject ldapDriveMemberObject = new LdapDriveMemberObject("John", "Doe", "user1@linshare.org",
				"uid=user1,ou=People,dc=linshare,dc=org", Role.DRIVE_WRITER);
		ldapDriveMemberObject.setRole(Role.CONTRIBUTOR);
		SharedSpaceLDAPGroup drive = syncService.createOrUpdateLDAPGroup(systemAccount, domain, ldapGroupObject, syncDate,
				resultContext, NodeType.DRIVE);
		Assertions.assertNotNull(drive, "The drive has not been found");
		Assertions.assertEquals("cn=drive-drive-1,ou=Groups,dc=linshare,dc=org",
				drive.getExternalId(), "The externalId do not match");
		Assertions.assertEquals(syncDate, drive.getSyncDate(), "The given syncDate is not the same as the found one");
		SharedSpaceLDAPDriveMember member = driveSyncService.createOrUpdateLDAPDriveMember(systemAccount,
				domain.getUuid(), drive, ldapDriveMemberObject, syncDate, resultContext,
				domain.getDriveProvider().getSearchInOtherDomains());
		Assertions.assertNotNull(member, "The member has not been found");
		Assertions.assertEquals("uid=user1,ou=People,dc=linshare,dc=org",
				member.getExternalId(), "The externalId do not match");
		Assertions.assertEquals(Role.CONTRIBUTOR.toString(), member.getRole().getName());
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
		ldapGroupObject.setName("drive-1");
		ldapGroupObject.setExternalId("cn=drive-drive-1,ou=Groups,dc=linshare,dc=org");
		ldapGroupObject.setMembers(Lists.newArrayList());
		ldapGroupObject.setPrefix("prefix");
		LdapDriveMemberObject ldapDriveMemberObject = new LdapDriveMemberObject("John", "Doe", "user1@linshare.org",
				"uid=user1,ou=People,dc=linshare,dc=org", Role.DRIVE_WRITER);
		ldapDriveMemberObject.setRole(Role.CONTRIBUTOR);
		SharedSpaceLDAPGroup drive = syncService.createOrUpdateLDAPGroup(systemAccount, domain, ldapGroupObject, syncDate,
				resultContext, NodeType.DRIVE);
		Assertions.assertNotNull(drive, "The drive has not been found");
		Assertions.assertEquals("cn=drive-drive-1,ou=Groups,dc=linshare,dc=org",
				drive.getExternalId(), "The externalId do not match");
		Assertions.assertEquals(syncDate, drive.getSyncDate(), "The given syncDate is not the same as the found one");
		SharedSpaceLDAPDriveMember member = driveSyncService.createOrUpdateLDAPDriveMember(systemAccount, domain.getUuid(), drive,
				ldapDriveMemberObject, syncDate, resultContext, searchInOtherDomains);
		Assertions.assertNotNull(member, "The member has not been found");
		Assertions.assertEquals("uid=user1,ou=People,dc=linshare,dc=org",
				member.getExternalId(), "The externalId do not match");
		Assertions.assertEquals(Role.CONTRIBUTOR.toString(), member.getRole().getName());
		Assertions.assertEquals(syncDate, member.getSyncDate(), "The given syncDate is not the same as the found one");
	}

	@Test
	@DirtiesContext
	public void testUpdateLDAPMemberFromLDAPDriveMember() {
		systemAccount = userRepository.getBatchSystemAccount();
		Date syncDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Year.now().getValue() + 1, 1, 1);
		LdapGroupObject ldapGroupObject = new LdapGroupObject();
		LdapGroupsBatchResultContext resultContext = new LdapGroupsBatchResultContext(domain);
		ldapGroupObject.setName("drive-1");
		ldapGroupObject.setExternalId("cn=drive-drive-1,ou=Groups,dc=linshare,dc=org");
		ldapGroupObject.setMembers(Lists.newArrayList());
		ldapGroupObject.setPrefix("prefix");
		LdapDriveMemberObject ldapDriveMemberObject = new LdapDriveMemberObject("John", "Doe", "user1@linshare.org",
				"uid=user1,ou=People,dc=linshare,dc=org", Role.DRIVE_WRITER);
		ldapDriveMemberObject.setRole(Role.CONTRIBUTOR);
		// Create a drive
		SharedSpaceLDAPGroup drive = syncService.createOrUpdateLDAPGroup(systemAccount, domain, ldapGroupObject, syncDate,
				resultContext, NodeType.DRIVE);
		// Create a member
		driveSyncService.createOrUpdateLDAPDriveMember(systemAccount, domain.getUuid(), drive,
				ldapDriveMemberObject, syncDate, resultContext, domain.getDriveProvider().getSearchInOtherDomains());
		ldapDriveMemberObject.setRole(Role.ADMIN);
		ldapDriveMemberObject.setFirstName("Bob");
		syncDate = calendar.getTime();
		// Update a member
		SharedSpaceLDAPDriveMember updated = driveSyncService.createOrUpdateLDAPDriveMember(systemAccount, domain.getUuid(), drive,
				ldapDriveMemberObject, syncDate, resultContext, domain.getDriveProvider().getSearchInOtherDomains());
		Assertions.assertEquals(1, memberService.findByNode(systemAccount, systemAccount, drive.getUuid()).size());
		Assertions.assertNotNull(updated, "The member has not been found");
		Assertions.assertEquals("uid=user1,ou=People,dc=linshare,dc=org",
				updated.getExternalId(), "The externalId do not match");
		Assertions.assertEquals(Role.ADMIN.toString(), updated.getRole().getName());
		Assertions.assertEquals(syncDate, updated.getSyncDate(), "The given syncDate is not the same as the found one");
	}

	@Test
	@DirtiesContext
	public void testUpdateLDAPDriveFromLDAPGroupObject() {
		systemAccount = userRepository.getBatchSystemAccount();
		Date syncDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Year.now().getValue() + 1, 1, 1);
		LdapGroupObject ldapGroupObject = new LdapGroupObject();
		LdapGroupsBatchResultContext resultContext = new LdapGroupsBatchResultContext(domain);
		ldapGroupObject.setName("drive-1");
		ldapGroupObject.setExternalId("cn=drive-drive-1,ou=Groups,dc=linshare,dc=org");
		ldapGroupObject.setMembers(Lists.newArrayList());
		ldapGroupObject.setPrefix("prefix");
		SharedSpaceLDAPGroup drive = syncService.createOrUpdateLDAPGroup(systemAccount, domain, ldapGroupObject, syncDate,
				resultContext, NodeType.DRIVE);
		Assertions.assertNotNull(drive, "The drive has not been found");
		Assertions.assertEquals("cn=drive-drive-1,ou=Groups,dc=linshare,dc=org",
				drive.getExternalId(), "The externalId do not match");
		Assertions.assertEquals(syncDate, drive.getSyncDate(), "The given syncDate is not the same as the found one");
		int nbLDAPGroup = nodeService.findAll(systemAccount, systemAccount).size();
		drive = syncService.createOrUpdateLDAPGroup(systemAccount, domain, ldapGroupObject, calendar.getTime(), resultContext, NodeType.DRIVE);
		Assertions.assertTrue(drive.getSyncDate().after(syncDate), "The new syncDate is not after the previous syncDate");
		Assertions.assertEquals(nbLDAPGroup,
				nodeService.findAll(systemAccount, systemAccount).size(), "A SharedSpaceLDAPGroup has been added and not updated");
	}

	@Test
	@DirtiesContext
	public void testDeleteNodeFromLDAPDriveSync() {
		systemAccount = userRepository.getBatchSystemAccount();
		Date syncDate = new Date();
		LdapGroupObject ldapGroupObject = new LdapGroupObject();
		LdapGroupsBatchResultContext resultContext = new LdapGroupsBatchResultContext(domain);
		ldapGroupObject.setName("drive-1");
		ldapGroupObject.setExternalId("cn=drive-drive-1,ou=Groups,dc=linshare,dc=org");
		ldapGroupObject.setMembers(Lists.newArrayList());
		ldapGroupObject.setPrefix("prefix");
		LdapDriveMemberObject ldapDriveMemberObject = new LdapDriveMemberObject("John", "Doe", "user1@linshare.org",
				"uid=user1,ou=People,dc=linshare,dc=org", Role.DRIVE_WRITER);
		ldapDriveMemberObject.setRole(Role.CONTRIBUTOR);
		SharedSpaceLDAPGroup drive = syncService.createOrUpdateLDAPGroup(systemAccount, domain, ldapGroupObject, syncDate,
				resultContext, NodeType.DRIVE);
		Assertions.assertNotNull(drive, "The drive has not been found");
		Assertions.assertEquals("cn=drive-drive-1,ou=Groups,dc=linshare,dc=org",
				drive.getExternalId(), "The externalId do not match");
		Assertions.assertEquals(syncDate, drive.getSyncDate(), "The given syncDate is not the same as the found one");
		SharedSpaceLDAPDriveMember member = driveSyncService.createOrUpdateLDAPDriveMember(systemAccount,
				domain.getUuid(), drive, ldapDriveMemberObject, syncDate, resultContext,
				domain.getDriveProvider().getSearchInOtherDomains());
		Assertions.assertNotNull(member, "The member has not been found");
		Assertions.assertEquals("uid=user1,ou=People,dc=linshare,dc=org",
				member.getExternalId(), "The externalId do not match");
		int nbLDAPMember = memberService.findAll(systemAccount, systemAccount, drive.getUuid()).size();
		Date syncDate2 = new Date();
		syncServiceImpl.deleteOutDatedMembers(systemAccount, drive, syncDate2, resultContext);
		Assertions.assertEquals(nbLDAPMember - 1,
				memberService.findAll(systemAccount, systemAccount, drive.getUuid()).size(), "A SharedSpaceLDAPDrive member has not been deleted");
	}
}
