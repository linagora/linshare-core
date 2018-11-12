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
package org.linagora.linshare.core.ldap.service.impl;

import org.jsoup.helper.Validate;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.ldap.business.service.SharedSpaceNodeBusinessService;
import org.linagora.linshare.core.ldap.service.SharedSpaceMemberService;
import org.linagora.linshare.core.ldap.service.SharedSpaceNodeService;
import org.linagora.linshare.core.rac.SharedSpaceNodeResourceAccessControl;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.SharedSpaceMemberDriveService;
import org.linagora.linshare.core.service.SharedSpaceRoleService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.mongo.entities.SharedSpaceLDAPGroup;
import org.linagora.linshare.mongo.entities.WorkGroupNode;

public class SharedSpaceNodeServiceImpl extends org.linagora.linshare.core.service.impl.SharedSpaceNodeServiceImpl implements SharedSpaceNodeService {

	SharedSpaceNodeBusinessService businessService;

	public SharedSpaceNodeServiceImpl(
			SharedSpaceNodeBusinessService businessService,
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
			SharedSpaceMemberDriveService memberDriveService) {
		super(businessService, rac, memberBusinessService, memberService, ssRoleService, logEntryService, threadService,
				threadRepository, functionalityService, accountQuotaBusinessService, workGroupNodeService, memberDriveService);
		this.businessService = businessService;
	}

	@Override
	public SharedSpaceLDAPGroup create(Account actor, SharedSpaceLDAPGroup ldapGroup) {
		Validate.notNull(actor, "The actor must be set");
		Validate.notNull(ldapGroup, "The group must be set");
		Validate.notEmpty(ldapGroup.getName(), "The name of the LDAP group must be set");
		Validate.notEmpty(ldapGroup.getExternalId(), "The external ID must be set");
		super.checkVersioningParameter(actor.getDomain(), ldapGroup);
		checkCreatePermission(actor, actor, SharedSpaceLDAPGroup.class, BusinessErrorCode.WORK_GROUP_FORBIDDEN, null);
		return (SharedSpaceLDAPGroup) super.simpleCreate(actor, actor, ldapGroup);
	}

	@Override
	public SharedSpaceLDAPGroup update(Account actor, SharedSpaceLDAPGroup ldapGroup) {
		Validate.notNull(actor, "The actor must be set");
		Validate.notNull(ldapGroup, "The group must be set");
		Validate.notEmpty(ldapGroup.getName(), "The name of the LDAP group must be set");
		Validate.notEmpty(ldapGroup.getExternalId(), "The external ID must be set");
		checkUpdatePermission(actor, actor, SharedSpaceLDAPGroup.class, BusinessErrorCode.WORK_GROUP_FORBIDDEN,
				ldapGroup);
		SharedSpaceLDAPGroup updated = businessService.update(ldapGroup);
		//For compatibility with deprecated Thread API, should be removed when this api taken off
		if (updated.getNodeType().equals(NodeType.WORK_GROUP)) {
			WorkGroup wg = threadRepository.findByLsUuid(updated.getUuid());
			wg.setName(updated.getName());
			threadRepository.update(wg);
			WorkGroupNode rootFolder = workGroupNodeService.getRootFolder(actor, actor, wg);
			rootFolder.setName(updated.getName());
			workGroupNodeService.update(actor, actor, wg, rootFolder);
		}
		saveUpdateLog(actor, actor, ldapGroup, updated);
		return updated;
	}

}
