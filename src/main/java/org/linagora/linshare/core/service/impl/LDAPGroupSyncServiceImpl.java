/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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
package org.linagora.linshare.core.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;

import org.apache.commons.lang3.Validate;
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
import org.linagora.linshare.core.service.LDAPGroupQueryService;
import org.linagora.linshare.core.service.LDAPGroupSyncService;
import org.linagora.linshare.core.service.SharedSpaceRoleService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.core.service.fragment.SharedSpaceFragmentService;
import org.linagora.linshare.ldap.LdapGroupMemberObject;
import org.linagora.linshare.ldap.LdapGroupObject;
import org.linagora.linshare.ldap.Role;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceLDAPGroup;
import org.linagora.linshare.mongo.entities.SharedSpaceLDAPGroupMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;
import org.linagora.linshare.mongo.entities.light.LightSharedSpaceRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class LDAPGroupSyncServiceImpl implements LDAPGroupSyncService {

	private static final Logger logger = LoggerFactory.getLogger(LDAPGroupSyncServiceImpl.class);

	protected final Map<NodeType, SharedSpaceFragmentService> serviceBuilders;

	protected LDAPGroupQueryService ldapGroupQueryService;

	protected UserService userService;

	protected SharedSpaceRoleService ssRoleService;

	protected SharedSpaceMemberService memberService;

	protected final MongoTemplate mongoTemplate;

	public LDAPGroupSyncServiceImpl(
			Map<NodeType, SharedSpaceFragmentService> serviceBuilders,
			LDAPGroupQueryService ldapGroupQueryService,
			UserService userService,
			SharedSpaceRoleService ssRoleService,
			SharedSpaceMemberService memberService,
			MongoTemplate mongoTemplate) {
		super();
		this.serviceBuilders = serviceBuilders;
		this.ldapGroupQueryService = ldapGroupQueryService;
		this.userService = userService;
		this.ssRoleService = ssRoleService;
		this.mongoTemplate = mongoTemplate;
		this.memberService = memberService;
	}

	private SharedSpaceFragmentService getService(NodeType type) {
		Validate.notNull(type, "Node type must be set");
		SharedSpaceFragmentService nodeService = serviceBuilders.get(type);
		Validate.notNull(nodeService, "Can not find a service that handle your noteType: " + type);
		return nodeService;
	}

	@Override
	public SharedSpaceLDAPGroup createOrUpdateLDAPGroup(Account actor, AbstractDomain domain, LdapGroupObject group,
			Date syncDate, LdapGroupsBatchResultContext resultContext) {
		SharedSpaceLDAPGroup node = convertLdapGroup(group, syncDate, domain);
		SharedSpaceLDAPGroup ldapGroup = findGroupToUpdate(node.getExternalId(), syncDate);
		SharedSpaceFragmentService nodeService  = getService(node.getNodeType());
		if (ldapGroup != null) {
			ldapGroup.setModificationDate(new Date());
			ldapGroup.setSyncDate(syncDate);
			logger.info(String.format("Ldap group updated : NAME : %s", ldapGroup.getName()));
			if (node.getName().equals(ldapGroup.getName())) {
				// Simple update : The member is not notified
				mongoTemplate.save(ldapGroup);
				return ldapGroup;
			}
			ldapGroup.setName(node.getName());
			// Complete update : The member is notified his access has been changed
			resultContext.add(LdapBatchMetaDataType.UPDATED_GROUPS);
			return (SharedSpaceLDAPGroup) nodeService.update(actor, actor, ldapGroup);
		}
		logger.info(String.format("New Ldap group created : NAME : %s", node.getName()));
		resultContext.add(LdapBatchMetaDataType.CREATED_GROUPS);
		return (SharedSpaceLDAPGroup) nodeService.create(actor, actor, node);
	}

	@Override
	public SharedSpaceLDAPGroupMember createOrUpdateLDAPGroupMember(Account actor, String domainUuid,
			SharedSpaceLDAPGroup group, LdapGroupMemberObject memberObject, Date syncDate,
			LdapGroupsBatchResultContext resultContext, Boolean searchInOtherDomains) {
		User user;
		try {
			if (null == searchInOtherDomains || searchInOtherDomains) {
				user = userService.findOrCreateUserWithDomainPolicies(memberObject.getEmail(), domainUuid);
			} else {
				user = userService.findOrCreateUser(memberObject.getEmail(), domainUuid);
			}
		} catch (BusinessException e) {
			logger.warn("The user [email=" + memberObject.getEmail() + ", domainUuid=" + domainUuid
					+ "] has not been found", e);
			return null;
		}
		SharedSpaceRole role = getRoleFrom(actor, memberObject.getRole());
		SharedSpaceLDAPGroupMember member = findMemberToUpdate(group.getUuid(), memberObject.getExternalId(), syncDate);
		if (member != null) {
			member.setModificationDate(new Date());
			member.setSyncDate(syncDate);
			logger.info(String.format("Ldap group member updated : NAME : %s", member.getAccount().getName()));
			if (member.getRole().getUuid().equals(role.getUuid())) {
				// Simple update : The member is not notified
				mongoTemplate.save(member);
				return member;
			}
			member.setRole(new LightSharedSpaceRole(role));
			// Complete update : The member is notified his access has been changed
			resultContext.add(LdapBatchMetaDataType.UPDATED_MEMBERS);
			return memberService.update(actor, member);
		}
		SharedSpaceLDAPGroupMember newMember = convertLdapGroupMember(group, role, user, memberObject.getExternalId(),
				syncDate);
		logger.info(String.format("New Ldap group member created : NAME : %s", newMember.getAccount().getName()));
		resultContext.add(LdapBatchMetaDataType.CREATED_MEMBERS);
		return memberService.create(actor, newMember);
	}

	private SharedSpaceRole getRoleFrom(Account actor, Role role) {
		return ssRoleService.findByName(actor, actor, role.toString());
	}

	private SharedSpaceLDAPGroup convertLdapGroup(LdapGroupObject group, Date syncDate, AbstractDomain domain) {
		return new SharedSpaceLDAPGroup(group.getName(), null, NodeType.WORK_GROUP, group.getExternalId(),
				group.getPrefix(), syncDate, new GenericLightEntity(domain));
	}

	private SharedSpaceLDAPGroupMember convertLdapGroupMember(SharedSpaceLDAPGroup group, SharedSpaceRole role,
			User user, String externalId, Date syncDate) {
		SharedSpaceNodeNested node = new SharedSpaceNodeNested(group);
		LightSharedSpaceRole lightRole = new LightSharedSpaceRole(role);
		return new SharedSpaceLDAPGroupMember(node, lightRole, new SharedSpaceAccount(user), externalId, syncDate);
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
			if (null != syncDate) {
				query.addCriteria(Criteria.where("syncDate").lt(syncDate));
			}
		} else {
			query.addCriteria(new Criteria().orOperator(Criteria.where("syncDate").lt(syncDate),
					Criteria.where("syncDate").is(null)));
		}
		return query;
	}

	private Query buildFindLdapGroupQuery(String externalId, Date syncDate) {
		return buildFindLdapGroupQuery(externalId, syncDate, null);
	}

	private Query buildFindLdapGroupQuery(String externalId, Date syncDate, String domainUuid) {
		Query query = new Query();
		if (null != externalId) {
			query.addCriteria(Criteria.where("externalId").is(externalId));
		}
		if (null != syncDate) {
			query.addCriteria(Criteria.where("syncDate").lt(syncDate));
		}
		if (null != domainUuid) {
			query.addCriteria(Criteria.where("domain.uuid").is(domainUuid));
		}
		return query;
	}

	private Query buildFindOutDatedLdapGroupsQuery(Date syncDate, String domainUuid) {
		return buildFindLdapGroupQuery(null, syncDate, domainUuid);
	}

	private Query buildFindOutDatedLdapMembersQuery(String nodeUuid, Date syncDate) {
		return buildFindLdapGroupMemberQuery(nodeUuid, null, syncDate);
	}

	public void deleteLDAPGroup(Account actor, SharedSpaceLDAPGroup group) {
		SharedSpaceFragmentService nodeService = getService(group.getNodeType());
		nodeService.delete(actor, actor, group);
	}

	private void deleteOutDatedMembers(Account actor, SharedSpaceLDAPGroup group, Date syncDate,
			LdapGroupsBatchResultContext resultContext) {
		Query outdatedGroupMembersQuery = buildFindOutDatedLdapMembersQuery(group.getUuid(), syncDate);
		List<SharedSpaceLDAPGroupMember> outDatedMembers = mongoTemplate.find(outdatedGroupMembersQuery,
				SharedSpaceLDAPGroupMember.class);
		// Delete all outdated members
		logger.info(
				String.format("Found %d outdated members in local group %s", outDatedMembers.size(), group.toString()));
		for (SharedSpaceLDAPGroupMember outDatedMember : outDatedMembers) {
			resultContext.add(LdapBatchMetaDataType.DELETED_MEMBERS);
			memberService.delete(actor, actor, outDatedMember.getUuid());
			logger.info(String.format("Member successfully removed from node with externalID '%s' : %s",
					group.getExternalId(), outDatedMember.toString()));
		}
	}

	@Override
	public void applyTask(Account actor, AbstractDomain domain, LdapGroupObject ldapGroupObject,
			Set<LdapGroupMemberObject> memberObjects, Date syncDate, LdapGroupsBatchResultContext resultContext) {
		SharedSpaceLDAPGroup created = createOrUpdateLDAPGroup(actor, domain, ldapGroupObject, syncDate, resultContext);
		// Create each member
		for (LdapGroupMemberObject memberObject : memberObjects) {
			createOrUpdateLDAPGroupMember(actor, domain.getUuid(), created, memberObject, syncDate, resultContext,
					domain.getGroupProvider().getSearchInOtherDomains());
		}
		deleteOutDatedMembers(actor, created, syncDate, resultContext);
	}

	@Override
	public void executeBatch(Account actor, AbstractDomain domain, LdapConnection ldapConnection, String baseDn,
			GroupLdapPattern groupPattern, LdapGroupsBatchResultContext resultContext)
			throws BusinessException, NamingException, IOException {
		Date syncDate = new Date();
		Set<LdapGroupObject> groupsObjects = ldapGroupQueryService.listGroups(ldapConnection, baseDn, groupPattern);
		logger.info(String.format("Starting sync at %tc : %d group(s) found remotely", syncDate, groupsObjects.size()));
		for (LdapGroupObject ldapGroupObject : groupsObjects) {
			Set<LdapGroupMemberObject> members = ldapGroupQueryService.listMembers(ldapConnection, baseDn, groupPattern,
					ldapGroupObject);
			applyTask(actor, domain, ldapGroupObject, members, syncDate, resultContext);
		}
		// Delete all outdated groups by deleting all its outdated members
		Query outdatedGroupsQuery = buildFindOutDatedLdapGroupsQuery(syncDate, domain.getUuid());
		List<SharedSpaceLDAPGroup> groupsOutDated = mongoTemplate.find(outdatedGroupsQuery, SharedSpaceLDAPGroup.class);
		logger.info(String.format(
				"Found %d outdated group(s) in domain %s. All the members of these groups will be removed",
				groupsOutDated.size(), domain.toString()));
		for (SharedSpaceLDAPGroup ldapGroup : groupsOutDated) {
			resultContext.add(LdapBatchMetaDataType.DELETED_GROUPS);
			deleteOutDatedMembers(actor, ldapGroup, syncDate, resultContext);
		}
	}

}
