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
package org.linagora.linshare.core.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;

import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.LDAPGroupQueryService;
import org.linagora.linshare.core.service.LDAPGroupSyncService;
import org.linagora.linshare.core.service.SharedSpaceLDAPGroupMemberService;
import org.linagora.linshare.core.service.SharedSpaceLDAPGroupService;
import org.linagora.linshare.core.service.SharedSpaceRoleService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.ldap.LdapGroupMemberObject;
import org.linagora.linshare.ldap.LdapGroupObject;
import org.linagora.linshare.ldap.Role;
import org.linagora.linshare.mongo.entities.SharedSpaceLDAPGroup;
import org.linagora.linshare.mongo.entities.SharedSpaceLDAPGroupMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.google.common.collect.Maps;

public class LDAPGroupSyncServiceImpl implements LDAPGroupSyncService {

	private static final Logger logger = LoggerFactory
			.getLogger(LDAPGroupSyncServiceImpl.class);

	protected SharedSpaceLDAPGroupService groupService;

	protected LDAPGroupQueryService ldapGroupQueryService;

	protected UserService userService;

	protected SharedSpaceRoleService ssRoleService;

	protected SharedSpaceLDAPGroupMemberService memberService;

	protected final MongoTemplate mongoTemplate;

	private Map<String, Integer> resultStats;

	public LDAPGroupSyncServiceImpl(SharedSpaceLDAPGroupService groupService,
			LDAPGroupQueryService ldapGroupQueryService,
			UserService userService,
			SharedSpaceRoleService ssRoleService,
			SharedSpaceLDAPGroupMemberService memberService,
			MongoTemplate mongoTemplate) {
		super();
		this.groupService = groupService;
		this.ldapGroupQueryService = ldapGroupQueryService;
		this.userService = userService;
		this.ssRoleService = ssRoleService;
		this.mongoTemplate = mongoTemplate;
		this.memberService = memberService;
		this.resultStats = initResultStats();
	}

	public Map<String, Integer> getResultStats() {
		return resultStats;
	}

	public void setResultStats(Map<String, Integer> resultStats) {
		this.resultStats = resultStats;
	}

	private Map<String, Integer> initResultStats() {
		Map<String, Integer> resultStats = Maps.newHashMap();
		resultStats.put("deletedGroups", 0);
		resultStats.put("updatedGroups", 0);
		resultStats.put("createdGroups", 0);
		resultStats.put("deletedMembers", 0);
		resultStats.put("updatedMembers", 0);
		resultStats.put("createdMembers", 0);
		return resultStats;
	}

	@Override
	public SharedSpaceLDAPGroup createOrUpdateLDAPGroup(Account actor, LdapGroupObject group, Date syncDate) {
		SharedSpaceLDAPGroup node = convertLdapGroup(group, syncDate);
		SharedSpaceLDAPGroup ldapGroup = findGroupToUpdate(node.getExternalId(), syncDate);
		if (ldapGroup != null) {
			ldapGroup.setModificationDate(new Date());
			ldapGroup.setSyncDate(syncDate);
			logger.info(String.format("Ldap group updated : NAME -> %s", ldapGroup.getName()));
			if (node.getName().equals(ldapGroup.getName())) {
				// Simple update : The member is not notified
				mongoTemplate.save(ldapGroup);
				return ldapGroup;
			}
			ldapGroup.setName(node.getName());
			// Complete update : The member is notified his access has been changed
			return groupService.update(actor, ldapGroup);
		}
		logger.info(String.format("New Ldap group created : NAME -> %s", node.getName()));
		return groupService.create(actor, node);
	}

	@Override
	public SharedSpaceLDAPGroupMember createOrUpdateLDAPGroupMember(Account actor, AbstractDomain domain, SharedSpaceLDAPGroup group,
			LdapGroupMemberObject memberObject, Date syncDate) {
		User user = userService.findOrCreateUser(memberObject.getEmail(), domain.getUuid());
		SharedSpaceRole role = getRoleFrom(actor, memberObject.getRole());
		SharedSpaceLDAPGroupMember member = findMemberToUpdate(group.getUuid(), memberObject.getExternalId(), syncDate);
		if (member != null) {
			member.setModificationDate(new Date());
			member.setSyncDate(syncDate);
			logger.info(String.format("Ldap group member updated : NAME -> %s", member.getAccount().getName()));
			if (member.getRole().getUuid().equals(role.getUuid())) {
				// Simple update : The member is not notified
				mongoTemplate.save(member);
				return member;
			}
			member.setRole(new GenericLightEntity(role.getUuid(), role.getName()));
			// Complete update : The member is notified his access has been changed
			return memberService.update(actor, member);
		}
		SharedSpaceLDAPGroupMember newMember = convertLdapGroupMember(group, role, user, memberObject.getExternalId(),
				syncDate);
		logger.info(String.format("New Ldap group member created : NAME -> %s", newMember.getAccount().getName()));
		return memberService.create(actor, newMember);
	}

	private SharedSpaceRole getRoleFrom(Account actor, Role role) {
		return ssRoleService.findByName(actor, actor, role.toString());
	}

	private SharedSpaceLDAPGroup convertLdapGroup(LdapGroupObject group, Date syncDate) {
		return new SharedSpaceLDAPGroup(group.getName(), null, NodeType.WORK_GROUP, group.getExternalId(),
				group.getPrefix(), syncDate);
	}

	private SharedSpaceLDAPGroupMember convertLdapGroupMember(SharedSpaceLDAPGroup group, SharedSpaceRole role,
			User user, String externalId, Date syncDate) {
		SharedSpaceNodeNested node = new SharedSpaceNodeNested(group);
		GenericLightEntity lightRole = new GenericLightEntity(role.getUuid(), role.getName());
		GenericLightEntity account = new GenericLightEntity(user.getLsUuid(), user.getFullName());
		return new SharedSpaceLDAPGroupMember(node, lightRole, account, externalId, syncDate);
	}

	private SharedSpaceLDAPGroup findGroupToUpdate(String externalId, Date syncDate) {
		Query query = buildFindLdapGroupQuery(externalId, syncDate);
		return mongoTemplate.findOne(query, SharedSpaceLDAPGroup.class);
	}

	private SharedSpaceLDAPGroupMember findMemberToUpdate(String nodeUuid, String externalId, Date syncDate) {
		Query query = buildFindLdapGroupMemberQuery(nodeUuid, externalId, syncDate);
		return mongoTemplate.findOne(query, SharedSpaceLDAPGroupMember.class);
	}

	private Query buildFindLdapGroupMemberQuery(String nodeUuid, String externalId, Date syncDate) {
		Query query = new Query();
		query.addCriteria(Criteria.where("node.uuid").is(nodeUuid));
		if (null != externalId) {
			query.addCriteria(Criteria.where("externalId").is(externalId));
		}
		if (null != syncDate) {
			query.addCriteria(Criteria.where("syncDate").lt(syncDate));
		}
		return query;
	}

	private Query buildFindLdapGroupQuery(String externalId, Date syncDate) {
		Query query = new Query();
		if (null != syncDate) {
			query.addCriteria(Criteria.where("externalId").is(externalId));
		}
		if (null != syncDate) {
			query.addCriteria(Criteria.where("syncDate").lt(syncDate));
		}
		return query;
	}

	private Query buildFindOutDatedLdapGroupsQuery(Date syncDate) {
		return buildFindLdapGroupQuery(null, syncDate);
	}

	private Query buildFindOutDatedLdapMembersQuery(String nodeUuid, Date syncDate) {
		return buildFindLdapGroupMemberQuery(nodeUuid, null, syncDate);
	}

	public void deleteLDAPGroup(Account actor, SharedSpaceLDAPGroup group) {
		groupService.delete(actor, actor, group);
	}

	@Override
	public void applyTask(Account actor, AbstractDomain domain, LdapGroupObject ldapGroupObject, Set<LdapGroupMemberObject> memberObjects,
			Date syncDate) {
		SharedSpaceLDAPGroup created = createOrUpdateLDAPGroup(actor, ldapGroupObject, syncDate);
		// TODO Create each member
		for (LdapGroupMemberObject memberObject : memberObjects) {
			createOrUpdateLDAPGroupMember(actor, domain, created, memberObject, syncDate);
		}
		Query outdatedGroupMembersQuery = buildFindOutDatedLdapMembersQuery(created.getUuid(), syncDate);
		List<SharedSpaceLDAPGroupMember> outDatedMembers = mongoTemplate.find(outdatedGroupMembersQuery,
				SharedSpaceLDAPGroupMember.class);
		// Delete all outdated members
		for (SharedSpaceLDAPGroupMember outDatedMember : outDatedMembers) {
			memberService.delete(actor, actor, outDatedMember.getUuid(),
					userService.findByLsUuid(outDatedMember.getAccount().getUuid()));
		}
	}

	@Override
	public void executeBatch(Account actor, AbstractDomain domain, LdapConnection ldapConnection, String baseDn,
			GroupLdapPattern groupPattern) throws BusinessException, NamingException, IOException {
		Date syncDate = new Date();
		Set<LdapGroupObject> groupsObjects = ldapGroupQueryService.listGroups(ldapConnection, baseDn, groupPattern);
		for (LdapGroupObject ldapGroupObject : groupsObjects) {
			Set<LdapGroupMemberObject> members = ldapGroupQueryService.listMembers(ldapConnection, baseDn, groupPattern,
					ldapGroupObject);
			applyTask(actor, domain, ldapGroupObject, members, syncDate);
		}
		// Delete all outdated groups
		Query outdatedGroupsQuery = buildFindOutDatedLdapGroupsQuery(syncDate);
		List<SharedSpaceLDAPGroup> groupsOutDated = mongoTemplate.find(outdatedGroupsQuery, SharedSpaceLDAPGroup.class);
		for (SharedSpaceLDAPGroup ldapGroup : groupsOutDated) {
			deleteLDAPGroup(actor, ldapGroup);
		}
	}

}
