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
package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ShareEntryGroupDto;
import org.linagora.linshare.core.facade.webservice.user.ShareEntryGroupFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ShareEntryGroupService;
import org.linagora.linshare.utils.Version;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class ShareEntryGroupFacadeImpl extends UserGenericFacadeImp implements ShareEntryGroupFacade {

	private final ShareEntryGroupService service;

	public ShareEntryGroupFacadeImpl(final AccountService accountService, final ShareEntryGroupService service) {
		super(accountService);
		this.service = service;
	}

	@Override
	public List<ShareEntryGroupDto> findAll(boolean full) {
		User authUser = checkAuthentication();
		List<ShareEntryGroup> list = service.findAll(authUser, authUser);
		return ImmutableList.copyOf(Lists.transform(list, ShareEntryGroupDto.toDto(Version.V2, full)));
	}

	@Override
	public ShareEntryGroupDto find(String uuid, boolean full) {
		Validate.notEmpty(uuid, "Share entry group uuid must be set.");
		User authUser = checkAuthentication();
		ShareEntryGroup seg = service.find(authUser, authUser, uuid);
		return new ShareEntryGroupDto(Version.V2, seg, full);
	}

	@Override
	public ShareEntryGroupDto update(ShareEntryGroupDto shareEntryGroupDto) {
		Validate.notNull(shareEntryGroupDto, "Share entry group must be set.");
		Validate.notNull(shareEntryGroupDto.getUuid(), "Share entry group uuid must be set.");
		User authUser = checkAuthentication();
		ShareEntryGroup seg = shareEntryGroupDto.toObject();
		seg = service.update(authUser, authUser, shareEntryGroupDto.getUuid(), seg);
		return new ShareEntryGroupDto(Version.V2, seg, false);
	}

	@Override
	public ShareEntryGroupDto delete(String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Share entry group uuid must be set.");
		User authUser = checkAuthentication();
		ShareEntryGroup seg = service.delete(authUser, authUser, uuid);
		return new ShareEntryGroupDto(Version.V2, seg, false);
	}
}
