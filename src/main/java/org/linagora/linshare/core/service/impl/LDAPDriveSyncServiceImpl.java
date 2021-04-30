/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2021. Contribute to
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
package org.linagora.linshare.core.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;

import org.linagora.linshare.core.domain.constants.LdapBatchMetaDataType;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.LdapGroupsBatchResultContext;
import org.linagora.linshare.core.ldap.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.LDAPDriveQueryService;
import org.linagora.linshare.core.service.LDAPDriveSyncService;
import org.linagora.linshare.core.service.LDAPGroupQueryService;
import org.linagora.linshare.core.service.SharedSpaceRoleService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.core.service.fragment.SharedSpaceFragmentService;
import org.linagora.linshare.ldap.LdapDriveMemberObject;
import org.linagora.linshare.ldap.LdapGroupObject;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceLDAPDriveMember;
import org.linagora.linshare.mongo.entities.SharedSpaceLDAPGroup;
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.LightSharedSpaceRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

public class LDAPDriveSyncServiceImpl extends LDAPGroupSyncServiceImpl implements LDAPDriveSyncService {

	private static final Logger logger = LoggerFactory.getLogger(LDAPDriveSyncServiceImpl.class);

	private LDAPDriveQueryService ldapDriveQueryService;

	protected LDAPDriveSyncServiceImpl(
			Map<NodeType, SharedSpaceFragmentService> serviceBuilders,
			LDAPGroupQueryService ldapGroupQueryService,
			UserService userService, SharedSpaceRoleService ssRoleService,
			SharedSpaceMemberService memberService,
			MongoTemplate mongoTemplate,
			LDAPDriveQueryService ldapDriveQueryService) {
		super(serviceBuilders, ldapGroupQueryService, userService, ssRoleService, memberService, mongoTemplate);
		this.ldapDriveQueryService = ldapDriveQueryService;
	}

	@Override
	public SharedSpaceLDAPGroup createOrUpdateLDAPGroup(Account actor, AbstractDomain domain, LdapGroupObject group,
			Date syncDate, LdapGroupsBatchResultContext resultContext, NodeType nodeType) {
		return super.createOrUpdateLDAPGroup(actor, domain, group, syncDate, resultContext, nodeType);
	}

	@Override
	public SharedSpaceLDAPDriveMember createOrUpdateLDAPDriveMember(Account actor, String domainUuid,
			SharedSpaceLDAPGroup group, LdapDriveMemberObject memberObject, Date syncDate,
			LdapGroupsBatchResultContext resultContext, Boolean searchInOtherDomains) {
		User user = findOrCreateUser(memberObject.getEmail(), domainUuid, searchInOtherDomains, memberObject);
		SharedSpaceRole role = getRoleFrom(actor, memberObject.getRole(), NodeType.WORK_GROUP);
		SharedSpaceRole nestedRole = getRoleFrom(actor, memberObject.getNestedRole(), NodeType.DRIVE);
		SharedSpaceLDAPDriveMember member = (SharedSpaceLDAPDriveMember) super.findMemberToUpdate(group.getUuid(), memberObject.getExternalId(), syncDate);
		if (member != null) {
			member.setModificationDate(new Date());
			member.setSyncDate(syncDate);
			logger.info(String.format("Ldap group member updated : NAME : %s", member.getAccount().getName()));
			if ((member.getRole().getUuid().equals(role.getUuid()))
					&& (member.getNestedRole().equals(nestedRole.getUuid()))) {
				// Simple update : The member is not notified
				mongoTemplate.save(member);
				return member;
			}
			member.setRole(new LightSharedSpaceRole(role));
			member.setNestedRole(new LightSharedSpaceRole(nestedRole));
			// Complete update : The member is notified his access has been changed
			resultContext.add(LdapBatchMetaDataType.UPDATED_MEMBERS);
			return (SharedSpaceLDAPDriveMember) memberService.update(actor, member);
		}
		SharedSpaceLDAPDriveMember newMember = convertLdapDriveMember(group, role, nestedRole, user, memberObject.getExternalId(),
				syncDate);
		logger.info(String.format("New Ldap group member created : NAME : %s", newMember.getAccount().getName()));
		resultContext.add(LdapBatchMetaDataType.CREATED_MEMBERS);
		return (SharedSpaceLDAPDriveMember) memberService.create(actor, newMember);
	}

	private SharedSpaceLDAPDriveMember convertLdapDriveMember(SharedSpaceLDAPGroup group, SharedSpaceRole role,
			SharedSpaceRole nestedRole, User user, String externalId, Date syncDate) {
		SharedSpaceNodeNested node = new SharedSpaceNodeNested(group);
		LightSharedSpaceRole lightRole = new LightSharedSpaceRole(role);
		LightSharedSpaceRole lightNestedRole = new LightSharedSpaceRole(nestedRole);
		return new SharedSpaceLDAPDriveMember(node, lightRole, new SharedSpaceAccount(user), externalId, syncDate,
				lightNestedRole);
	}

	private void applyDriveTask(Account actor, AbstractDomain domain, LdapGroupObject ldapGroupObject,
			Set<LdapDriveMemberObject> memberObjects, Date syncDate, LdapGroupsBatchResultContext resultContext) {
		SharedSpaceLDAPGroup created = createOrUpdateLDAPGroup(actor, domain, ldapGroupObject, syncDate, resultContext,
				NodeType.DRIVE);
		// Create each member
		for (LdapDriveMemberObject memberObject : memberObjects) {
			createOrUpdateLDAPDriveMember(actor, domain.getUuid(), created, memberObject, syncDate, resultContext,
					domain.getGroupProvider().getSearchInOtherDomains());
		}
		deleteOutDatedMembers(actor, created, syncDate, resultContext);
	}

	@Override
	public void executeBatch(Account actor, AbstractDomain domain, LdapConnection ldapConnection, String baseDn,
			GroupLdapPattern groupPattern, LdapGroupsBatchResultContext resultContext)
			throws BusinessException, NamingException, IOException {
		Date syncDate = new Date();
		Set<LdapGroupObject> groupsObjects = ldapDriveQueryService.listGroups(ldapConnection, baseDn, groupPattern);
		logger.info(String.format("Starting sync at %tc : %d group(s) found remotely", syncDate, groupsObjects.size()));
		for (LdapGroupObject ldapGroupObject : groupsObjects) {
			Set<LdapDriveMemberObject> members = ldapDriveQueryService.listDriveMembers(ldapConnection, baseDn, groupPattern,
					ldapGroupObject);
			applyDriveTask(actor, domain, ldapGroupObject, members, syncDate, resultContext);
		}
		// Delete all outdated drives by deleting all its outdated members
		Query outdatedGroupsQuery = buildFindOutDatedLdapGroupsQuery(syncDate, domain.getUuid());
		List<SharedSpaceLDAPGroup> groupsOutDated = mongoTemplate.find(outdatedGroupsQuery, SharedSpaceLDAPGroup.class);
		logger.info(String.format(
				"Found %d outdated group(s) in domain %s. All the members of these groups will be removed",
				groupsOutDated.size(), domain.toString()));
		for (SharedSpaceLDAPGroup ldapGroup : groupsOutDated) {
			resultContext.add(LdapBatchMetaDataType.DELETED_DRIVES);
			deleteOutDatedMembers(actor, ldapGroup, syncDate, resultContext);
		}
	}

}
