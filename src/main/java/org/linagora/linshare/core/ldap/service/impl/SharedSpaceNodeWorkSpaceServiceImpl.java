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
package org.linagora.linshare.core.ldap.service.impl;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceNodeBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
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
import org.linagora.linshare.mongo.entities.SharedSpaceLDAPGroup;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;

public class SharedSpaceNodeWorkSpaceServiceImpl extends org.linagora.linshare.core.service.impl.SharedSpaceNodeWorkSpaceServiceImpl {

	public SharedSpaceNodeWorkSpaceServiceImpl(AbstractResourceAccessControl<Account, Account, SharedSpaceNode> rac,
			SharedSpaceNodeBusinessService businessService, SharedSpaceMemberBusinessService memberBusinessService,
			SharedSpaceMemberService memberService, SharedSpaceRoleService ssRoleService,
			LogEntryService logEntryService, ThreadService threadService, ThreadRepository threadRepository,
			FunctionalityReadOnlyService functionalityService, AccountQuotaBusinessService accountQuotaBusinessService,
			WorkGroupNodeService workGroupNodeService, SharedSpaceMemberBusinessService memberWorkSpaceService, SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(rac, businessService, memberBusinessService, memberService, ssRoleService, logEntryService, threadService,
				threadRepository, functionalityService, accountQuotaBusinessService, workGroupNodeService, memberWorkSpaceService, sanitizerInputHtmlBusinessService);
	}

	@Override
	public SharedSpaceNode create(Account authUser, Account actor, SharedSpaceNode node) throws BusinessException {
		SharedSpaceLDAPGroup ldapGroup = (SharedSpaceLDAPGroup) node;
		Validate.notNull(actor, "The actor must be set");
		Validate.notNull(ldapGroup, "The group must be set");
		Validate.notEmpty(ldapGroup.getName(), "The name of the LDAP group must be set");
		Validate.notEmpty(ldapGroup.getExternalId(), "The external ID must be set");
		checkCreatePermission(actor, actor, SharedSpaceLDAPGroup.class, BusinessErrorCode.WORKSPACE_FORBIDDEN, null);
		return super.create(actor, actor, ldapGroup);
	}

	@Override
	public SharedSpaceLDAPGroup update(Account authUser, Account actor, SharedSpaceNode node) {
		SharedSpaceLDAPGroup ldapGroup = (SharedSpaceLDAPGroup) node;
		Validate.notNull(actor, "The actor must be set");
		Validate.notNull(ldapGroup, "The group must be set");
		Validate.notEmpty(ldapGroup.getName(), "The name of the LDAP group must be set");
		Validate.notEmpty(ldapGroup.getExternalId(), "The external ID must be set");
		return (SharedSpaceLDAPGroup) super.update(authUser, actor, node);
	}
}
