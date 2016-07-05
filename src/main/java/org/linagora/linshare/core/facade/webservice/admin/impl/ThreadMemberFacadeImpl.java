/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

package org.linagora.linshare.core.facade.webservice.admin.impl;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.ThreadMemberFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupMemberDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.UserService;

public class ThreadMemberFacadeImpl extends AdminGenericFacadeImpl implements
		ThreadMemberFacade {

	private ThreadService threadService;
	private UserService userService;

	public ThreadMemberFacadeImpl(final AccountService accountService,
			final ThreadService threadService, final UserService userService) {
		super(accountService);
		this.threadService = threadService;
		this.userService = userService;
	}

	@Override
	public WorkGroupMemberDto find(Long id) throws BusinessException {
		@SuppressWarnings("unused")
		User actor = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(id, "id must be set.");
		return new WorkGroupMemberDto(threadService.getThreadMemberById(id));
	}

	@Override
	public WorkGroupMemberDto create(WorkGroupMemberDto dto) throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(dto, "thread member must be set.");
		Validate.notEmpty(dto.getThreadUuid(), "thread member thread id must be set.");
		Validate.notEmpty(dto.getUserDomainId(), "thread member domain id must be set.");
		Validate.notEmpty(dto.getUserMail(), "thread member mail must be set.");

		Thread thread = threadService.find(actor, actor, dto.getThreadUuid());
		User user = (User) accountService.findByLsUuid(dto.getUserUuid());
		if (user == null) {
			user = userService.findOrCreateUser(dto.getUserMail(),
					dto.getUserDomainId());
			if (user == null) {
				throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND,
						"Cannot find user with mail :" + dto.getUserMail()
								+ " in domain :" + dto.getUserDomainId());
			}
		}
		boolean admin = dto.isAdmin();
		boolean canUpload = !dto.isReadonly();

		return new WorkGroupMemberDto(threadService.addMember(actor, actor, thread, user, admin, canUpload));
	}

	@Override
	public WorkGroupMemberDto update(WorkGroupMemberDto dto) throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(dto, "thread member must be set.");
		Validate.notNull(dto.getThreadUuid(), "thread uuid must be set.");
		Validate.notNull(dto.getUserUuid(), "user uuid must be set.");
		boolean admin = dto.isAdmin();
		boolean readonly = dto.isReadonly();
		return new WorkGroupMemberDto(threadService.updateMember(actor, actor, dto.getThreadUuid(), dto.getUserUuid(), admin, !readonly));
	}

	@Override
	public WorkGroupMemberDto delete(WorkGroupMemberDto dto) throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(dto, "thread member must be set.");
		Validate.notNull(dto.getThreadUuid(), "thread uuid must be set.");
		Validate.notNull(dto.getUserUuid(), "user uuid must be set.");
		ThreadMember member = this.threadService.deleteMember(actor, actor, dto.getThreadUuid(), dto.getUserUuid());
		return new WorkGroupMemberDto(member);
	}
}
