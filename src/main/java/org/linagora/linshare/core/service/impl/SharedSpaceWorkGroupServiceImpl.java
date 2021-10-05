/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2019-2021 LINAGORA
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

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceNodeBusinessService;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.BooleanValueFunctionality;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Quota;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.AbstractResourceAccessControl;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.SharedSpaceRoleService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.core.service.fragment.impl.AbstractSharedSpaceFragmentServiceImpl;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberContext;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberDrive;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.VersioningParameters;
import org.linagora.linshare.mongo.entities.WorkGroupNode;

public class SharedSpaceWorkGroupServiceImpl extends AbstractSharedSpaceFragmentServiceImpl {

	public SharedSpaceWorkGroupServiceImpl(
			AbstractResourceAccessControl<Account, Account, SharedSpaceNode> rac,
			SharedSpaceNodeBusinessService businessService,
			SharedSpaceMemberBusinessService memberBusinessService,
			SharedSpaceMemberService memberService,
			SharedSpaceRoleService ssRoleService,
			LogEntryService logEntryService,
			ThreadService threadService,
			ThreadRepository threadRepository,
			FunctionalityReadOnlyService functionalityService,
			AccountQuotaBusinessService accountQuotaBusinessService,
			WorkGroupNodeService workGroupNodeService,
			SharedSpaceMemberBusinessService memberDriveService,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(rac, businessService, memberBusinessService, memberService, ssRoleService, logEntryService, threadService,
				threadRepository, functionalityService, accountQuotaBusinessService, workGroupNodeService, memberDriveService, sanitizerInputHtmlBusinessService);
	}

	@Override
	public SharedSpaceNode create(Account authUser, Account actor, SharedSpaceNode node) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notNull(node, "Missing required input shared space node.");
		Validate.notNull(node.getNodeType(), "you must set the node type");
		checkVersioningParameter(actor.getDomain(), node);
		checkCreatePermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.WORK_GROUP_FORBIDDEN, node);
		if (!(NodeType.WORK_GROUP.equals(node.getNodeType()))) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_OPERATION_UNSUPPORTED,
					"Can not create this kind of sharedSpace with this method.");
		}
		SharedSpaceNode toCreate = new SharedSpaceNode(actor.getDomainId(), sanitize(node.getName()), node.getParentUuid(), node.getNodeType(),
				node.getVersioningParameters(), sanitize(node.getDescription()), new SharedSpaceAccount((User) actor));
		SharedSpaceNode created = simpleCreate(authUser, actor, toCreate);
		SharedSpaceNode parent = null;
		if (node.getParentUuid() != null) {
			parent = businessService.find(node.getParentUuid());
		}
		checkCreatePermission(authUser, actor, SharedSpaceNode.class, BusinessErrorCode.WORK_GROUP_FORBIDDEN, node,
				parent);
		SharedSpaceRole workGroupRole = ssRoleService.getAdmin(authUser, actor);
		SharedSpaceMemberContext context = new SharedSpaceMemberContext(workGroupRole);
		SharedSpaceMember firstMember = memberService.create(authUser, actor, created, context,
				new SharedSpaceAccount((User) actor));
		// Adding all drive members to the workgroup
		if (parent != null && firstMember.isNested()) {
			List<SharedSpaceMember> driveMembers = memberService.findAll(authUser, actor, parent.getUuid());
			for (SharedSpaceMember sharedSpaceMember : driveMembers) {
				if (sharedSpaceMember.getAccount().getUuid().equals(firstMember.getAccount().getUuid())) {
					continue;
				}
				SharedSpaceMemberDrive driveMember = (SharedSpaceMemberDrive) sharedSpaceMember;
				SharedSpaceRole nestedRole = ssRoleService.find(authUser, actor, driveMember.getNestedRole().getUuid());
				memberService.create(authUser, actor, created, nestedRole, driveMember.getAccount());
			}
		}
		return created;
	}

	protected SharedSpaceNode simpleCreate(Account authUser, Account actor, SharedSpaceNode node)
			throws BusinessException {
		// Hack to create thread into shared space node
		WorkGroup workGroup = threadService.create(authUser, actor, node.getName());
		Quota workgroupQuota = accountQuotaBusinessService.find(workGroup);
		node.setUuid(workGroup.getLsUuid());
		node.setQuotaUuid(workgroupQuota.getUuid());
		return super.create(authUser, actor, node);
	}

	protected void checkVersioningParameter(AbstractDomain domain, SharedSpaceNode node) {
		BooleanValueFunctionality versioningFunctionality = functionalityService.getWorkGroupFileVersioning(domain);
		Boolean userValue = node.getVersioningParameters() == null ? null : node.getVersioningParameters().getEnable();
		node.setVersioningParameters(new VersioningParameters(versioningFunctionality.getFinalValue(userValue)));
	}

	@Override
	public SharedSpaceNode update(Account authUser, Account actor, SharedSpaceNode nodeToUpdate)
			throws BusinessException {
		Validate.notNull(nodeToUpdate.getVersioningParameters());
		SharedSpaceNode updated = super.update(authUser, actor, nodeToUpdate);
		checkUpdateVersioningParameters(updated.getVersioningParameters(), nodeToUpdate.getVersioningParameters(),
				actor.getDomain());
		simpleUpdate(authUser, actor, updated);
		return updated;
	}

	protected void simpleUpdate(Account authUser, Account actor, SharedSpaceNode node) throws BusinessException {
		// For compatibility with deprecated Thread API, should be removed when this api taken off
		WorkGroup wg = threadRepository.findByLsUuid(node.getUuid());
		wg.setName(node.getName());
		threadRepository.update(wg);
		WorkGroupNode rootFolder = workGroupNodeService.getRootFolder(authUser, actor, wg);
		rootFolder.setName(node.getName());
		workGroupNodeService.update(authUser, actor, wg, rootFolder);
	}

	protected void checkUpdateVersioningParameters(VersioningParameters newParam, VersioningParameters parameter,
			AbstractDomain domain) {
		if (!parameter.equals(newParam)) {
			Functionality versioning = functionalityService.getWorkGroupFileVersioning(domain);
			if (!versioning.getDelegationPolicy().getStatus()) {
				logger.error(
						"The current domain does not allow you to update the versioning parameters on the shared space node.");
				throw new BusinessException(BusinessErrorCode.SHARED_SPACE_MEMBER_FORBIDDEN,
						"can not update shared space versioning parameters, you are not authorized.");
			}
		}
	}

	@Override
	public SharedSpaceNode delete(Account authUser, Account actor, SharedSpaceNode node) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notNull(node, "missing required node to delete.");
		Validate.notEmpty(node.getUuid(), "missing required node uuid to delete");
		SharedSpaceNode foundedNodeTodel = businessService.find(node.getUuid());
		if (foundedNodeTodel == null) {
			throw new BusinessException(BusinessErrorCode.SHARED_SPACE_NODE_NOT_FOUND,
					"The shared space node with uuid: " + node.getUuid() + " is not found");
		}
		checkReadPermission(authUser, actor, SharedSpaceNode.class,
				BusinessErrorCode.WORK_GROUP_FORBIDDEN, foundedNodeTodel);
		checkDeletePermission(authUser, actor, SharedSpaceNode.class,
				BusinessErrorCode.WORK_GROUP_FORBIDDEN, foundedNodeTodel);
		//Delete the Thread
		WorkGroup workGroup = threadService.find(authUser, authUser, foundedNodeTodel.getUuid());
		threadService.deleteThread(authUser, authUser, workGroup);
		memberService.deleteAllMembers(authUser, actor, foundedNodeTodel, LogActionCause.WORKGROUP_DELETION, null);
		businessService.delete(foundedNodeTodel);
		saveLog(authUser, actor, LogAction.DELETE, foundedNodeTodel);
		return foundedNodeTodel;
	}
}
