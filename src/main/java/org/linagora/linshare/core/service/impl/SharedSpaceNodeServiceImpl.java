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
package org.linagora.linshare.core.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceNodeBusinessService;
import org.linagora.linshare.core.business.service.WorkSpaceMemberBusinessService;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.entities.fields.SharedSpaceField;
import org.linagora.linshare.core.domain.entities.fields.SharedSpaceMemberField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.PatchDto;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupDto;
import org.linagora.linshare.core.rac.SharedSpaceNodeResourceAccessControl;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.core.service.SharedSpaceRoleService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.core.service.fragment.SharedSpaceFragmentService;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.springframework.data.domain.Sort;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class SharedSpaceNodeServiceImpl extends GenericServiceImpl<Account, SharedSpaceNode>
		implements SharedSpaceNodeService {

	protected final SharedSpaceNodeBusinessService businessService;

	protected final SharedSpaceMemberBusinessService memberBusinessService;

	protected final SharedSpaceMemberService memberService;

	protected final SharedSpaceRoleService ssRoleService;

	protected final LogEntryService logEntryService;

	protected final ThreadService threadService;

	protected final ThreadRepository threadRepository;

	protected final FunctionalityReadOnlyService functionalityService;

	protected final AccountQuotaBusinessService accountQuotaBusinessService;

	protected final WorkGroupNodeService workGroupNodeService;

	protected final WorkSpaceMemberBusinessService workSpaceMemberBusinessService;

	private final Map<NodeType, SharedSpaceFragmentService> serviceBuilders;

	private final DomainPermissionBusinessService domainPermissionBusinessService;

	public SharedSpaceNodeServiceImpl(SharedSpaceNodeBusinessService businessService,
			SharedSpaceNodeResourceAccessControl rac,
			SharedSpaceMemberBusinessService memberBusinessService,
			SharedSpaceMemberService memberService,
			SharedSpaceRoleService ssRoleService,
			LogEntryService logEntryService,
			ThreadService threadService,
			ThreadRepository threadRepository,
			FunctionalityReadOnlyService functionalityService,
			AccountQuotaBusinessService accountQuotaBusinessService,
			WorkGroupNodeService workGroupNodeService,
			WorkSpaceMemberBusinessService workSpaceMemberBusinessService,
			Map<NodeType, SharedSpaceFragmentService> serviceBuilders,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
			DomainPermissionBusinessService domainPermissionBusinessService) {
		super(rac, sanitizerInputHtmlBusinessService);
		this.businessService = businessService;
		this.memberService = memberService;
		this.ssRoleService = ssRoleService;
		this.memberBusinessService = memberBusinessService;
		this.logEntryService = logEntryService;
		this.threadService = threadService;
		this.functionalityService = functionalityService;
		this.accountQuotaBusinessService = accountQuotaBusinessService;
		this.threadRepository = threadRepository;
		this.workGroupNodeService = workGroupNodeService;
		this.workSpaceMemberBusinessService = workSpaceMemberBusinessService;
		this.serviceBuilders = serviceBuilders;
		this.domainPermissionBusinessService = domainPermissionBusinessService;
	}

	private SharedSpaceFragmentService getService(NodeType type) {
		Validate.notNull(type, "Node type must be set");
		SharedSpaceFragmentService nodeService = serviceBuilders.get(type);
		Validate.notNull(nodeService, "Can not find a service that handle your noteType: " + type);
		return nodeService;
	}

	@Override
	public SharedSpaceNode find(Account authUser, Account actor, String uuid) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notEmpty(uuid, "Missing required shared space node uuid.");
		SharedSpaceNode found = businessService.find(uuid);
		if (found == null) {
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_NODE_NOT_FOUND,
					"The shared space node with uuid: " + uuid + " is not found");
		}
		checkReadPermission(authUser, actor, SharedSpaceNode.class, getBusinessErrorCode(found.getNodeType()), found);
		return found;
	}
	
	

	@Override
	public SharedSpaceNode find(Account authUser, Account actor, String uuid, boolean withRole, boolean lastUpdater)
			throws BusinessException {
		SharedSpaceNode node = find(authUser, actor, uuid);
		if (withRole) {
			SharedSpaceMember member = memberService.findMemberByAccountUuid(authUser, actor, actor.getLsUuid(), uuid);
			node.setRole(new GenericLightEntity(member.getRole()));
		} 
		if (lastUpdater) {
			businessService.loadLastUpdaterAuditTrace(node);
		}
		return node;
	}

	@Override
	public SharedSpaceNode create(Account authUser, Account actor, SharedSpaceNode node) throws BusinessException {
		SharedSpaceFragmentService nodeService  = getService(node.getNodeType());
		return nodeService.create(authUser, actor, node);
	}

	/**
	 * Only use to compability with threadFacade
	 *
	 */
	@Deprecated
	public WorkGroupDto createWorkGroupDto(Account authUser, Account actor, SharedSpaceNode node)
			throws BusinessException {
		SharedSpaceFragmentService nodeService = getService(node.getNodeType());
		node.setName(sanitize(node.getName()));
		SharedSpaceNode created = nodeService.create(authUser, actor, node);
		return new WorkGroupDto(new WorkGroup(created), created);
	}

	@Override
	public SharedSpaceNode delete(Account authUser, Account actor, SharedSpaceNode node) throws BusinessException {
		SharedSpaceFragmentService nodeService  = getService(node.getNodeType());
		return nodeService.delete(authUser, actor, node);
	}

	/**
	 * Only use to compability with threadFacade
	 *
	 */
	@Override
	public WorkGroupDto deleteWorkgroupDto(Account authUser, Account actor, SharedSpaceNode node)
			throws BusinessException {
		SharedSpaceFragmentService nodeService = getService(node.getNodeType());
		WorkGroup workGroup = new WorkGroup(node);
		nodeService.delete(authUser, actor, node);
		return new WorkGroupDto(workGroup, node);
	}

	@Override
	public SharedSpaceNode update(Account authUser, Account actor, SharedSpaceNode nodeToUpdate)
			throws BusinessException {
		SharedSpaceFragmentService nodeService  = getService(nodeToUpdate.getNodeType());
		return nodeService.update(authUser, actor, nodeToUpdate);
	}

	@Override
	public SharedSpaceNode updatePartial(Account authUser, Account actor, PatchDto patchNode) throws BusinessException {
		SharedSpaceNode nodeToUpdate = find(authUser, actor, patchNode.getUuid());
		if (patchNode.getName().equals("name")) {
			nodeToUpdate.setName(sanitize(patchNode.getValue()));
		} else {
			throw new BusinessException("Unsupported field name, allowed values: name");
		}
		return update(authUser, actor, nodeToUpdate);
	}

	@Override
	public List<SharedSpaceNode> findAll(Account authUser, Account actor) {
		preChecks(authUser, actor);
		checkListPermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.SHARED_SPACE_NODE_FORBIDDEN, null);
		return businessService.findAll();
	}

	@Override
	public List<SharedSpaceNodeNested> findAllByAccount(Account authUser, Account actor, boolean withRole, String parent) {
		preChecks(authUser, actor);
		Set<NodeType> types = Sets.newHashSet();
		types.add(NodeType.WORK_SPACE);
		types.add(NodeType.WORK_GROUP);
		checkListPermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.WORK_GROUP_FORBIDDEN, null, null, types);
		return memberService.findAllSharedSpacesByAccountAndParentForUsers(authUser, actor, actor.getLsUuid(), withRole, parent, types);
	}

	@Override
	public List<SharedSpaceNodeNested> findAllByAccount(Account authUser, Account actor) {
		preChecks(authUser, actor);
		Set<NodeType> types = Sets.newHashSet();
		types.add(NodeType.WORK_SPACE);
		types.add(NodeType.WORK_GROUP);
		checkListPermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.WORK_GROUP_FORBIDDEN, null, null, types);
		return memberService.findAllSharedSpacesByAccountAndParentForUsers(authUser, actor, actor.getLsUuid(), false, null, types);
	}

	@Override
	public List<SharedSpaceMember> findAllMembers(Account authUser, Account actor, String sharedSpaceNodeUuid,
			String accountUuid) {
		// TODO: No need of rac ?
		List<SharedSpaceMember> members = Lists.newArrayList();
		if (Strings.isNullOrEmpty(accountUuid)) {
			members = memberService.findAll(authUser, actor, sharedSpaceNodeUuid);
		} else {
			members.add(memberService.findMemberByAccountUuid(authUser, actor, accountUuid, find(authUser, actor, sharedSpaceNodeUuid).getUuid()));
		}
		return members;
	}

	@Override
	public List<SharedSpaceNode> searchByName(Account authUser, Account actor, String name) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notEmpty(name, "Missing required shared space node name.");
		List<SharedSpaceNode> founds = businessService.searchByName(name);
		checkListPermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.SHARED_SPACE_NODE_FORBIDDEN, null);
		return founds;
	}

	private BusinessErrorCode getBusinessErrorCode(NodeType nodeType) {
		if (NodeType.WORK_SPACE.equals(nodeType)) {
			return BusinessErrorCode.WORKSPACE_FORBIDDEN;
		} else {
			return BusinessErrorCode.WORK_GROUP_FORBIDDEN;
		}
	}

	@Override
	public PageContainer<SharedSpaceNodeNested> findAll(Account authUser, Account actor, Account account,
			List<String> domains, SortOrder sortOrder, Set<NodeType> nodeTypes, Set<String> sharedSpaceRoles, SharedSpaceField sortField, String name, Integer greaterThanOrEqualTo, Integer lessThanOrEqualTo, PageContainer<SharedSpaceNodeNested> container) {
		preChecks(authUser, actor);
		if (!(authUser.hasSuperAdminRole() || authUser.hasAdminRole())) {
			throw new BusinessException(BusinessErrorCode.USER_FORBIDDEN,
					"You do not have an admin role, you are not authorized use this service");
		}
		checkListPermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.SHARED_SPACE_NODE_FORBIDDEN,
				null);
		Sort sort = Sort.by(SortOrder.getSortDir(sortOrder), sortField.toString());
		if (Objects.nonNull(account)) {
			if (!domainPermissionBusinessService.isAdminForThisDomain(actor, account.getDomain())) {
				throw new BusinessException(BusinessErrorCode.USER_FORBIDDEN,
						"You are not authorized to retieve the sharedSpaces of this account: " + account.getLsUuid());
			}
		}
		List<String> allowedDomainUuids = domainPermissionBusinessService
				.checkDomainAdministrationForListingSharedSpaces(actor, domains);
		Set<String> roleNames = checkRoles(authUser, actor, sharedSpaceRoles);
		if (Objects.isNull(greaterThanOrEqualTo) && Objects.isNull(lessThanOrEqualTo)) {
			if (roleNames.isEmpty()) {
				roleNames.addAll(ssRoleService.findAllSharedSpaceRoleNames(authUser, actor));
			}
			return memberBusinessService.findAllSharedSpaces(account, allowedDomainUuids, nodeTypes, roleNames,
					name, container, sort);
		} else {
			if ((Objects.nonNull(greaterThanOrEqualTo) && greaterThanOrEqualTo < 1) || (Objects.nonNull(lessThanOrEqualTo) && lessThanOrEqualTo < 1)) {
				throw new BusinessException(BusinessErrorCode.SHARED_SPACE_NODE_FORBIDDEN,
						"The greaterThan or lessThan should be greater than 1, please check the entered values.");
			}
			if (Objects.nonNull(lessThanOrEqualTo) && lessThanOrEqualTo == 1) {
				return memberBusinessService.findOrphanSharedSpaces(sort, container);
			}
			return memberBusinessService.findSharedSpacesByMembersNumber(greaterThanOrEqualTo, lessThanOrEqualTo, roleNames, sort,
					container);
		}
	}

	private Set<String> checkRoles(Account authUser, Account actor, Set<String> sharedSpaceRoles) {
		Set<String> roleNames = Sets.newHashSet();
		for (String roleName : sharedSpaceRoles) {
			if (ssRoleService.exist(authUser, actor, roleName)) {
				roleNames.add(roleName);
			}
		}
		return roleNames;
	}

	@Override
	public List<SharedSpaceNode> findAllRootWorkgroups(Account authUser, Account actor) {
		preChecks(authUser, actor);
		checkListPermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.SHARED_SPACE_NODE_FORBIDDEN, null);
		return businessService.findAllRootWorkgroups();
	}

	@Override
	public PageContainer<SharedSpaceMember> findAllMembersWithPagination(Account authUser, Account actor,
			String sharedSpaceNodeUuid, String accountUuid, Set<String> roles, String email, String firstName,
			String lastName, String pattern, String type, SortOrder sortOrder, SharedSpaceMemberField sortField, PageContainer<SharedSpaceMember> container) {
		preChecks(authUser, actor);
		if (!(authUser.hasSuperAdminRole() || authUser.hasAdminRole())) {
			throw new BusinessException(BusinessErrorCode.USER_FORBIDDEN,
					"You do not have an admin role, you are not authorized use this service");
		}
		checkListPermission(authUser, actor, SharedSpaceMember.class, BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
				null);
		Set<String> roleNames = checkRoles(authUser, actor, roles);
		AccountType checkedAccountType = Strings.isNullOrEmpty(type) ? null : AccountType.valueOf(type);
		return memberBusinessService.findAllMembersWithPagination(sharedSpaceNodeUuid, accountUuid, roleNames, email,
				firstName, lastName, pattern, checkedAccountType, sortOrder, sortField, container);
	}

}
