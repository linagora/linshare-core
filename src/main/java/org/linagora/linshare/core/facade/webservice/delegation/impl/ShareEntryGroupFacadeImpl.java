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
package org.linagora.linshare.core.facade.webservice.delegation.impl;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ShareEntryGroupDto;
import org.linagora.linshare.core.facade.webservice.delegation.ShareEntryGroupFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ShareEntryGroupService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.utils.Version;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class ShareEntryGroupFacadeImpl extends DelegationGenericFacadeImpl implements ShareEntryGroupFacade {

	private final ShareEntryGroupService shareEntryGroupService;

	public ShareEntryGroupFacadeImpl(final AccountService accountService, final UserService userService,
			final ShareEntryGroupService shareEntryGroupService) {
		super(accountService, userService);
		this.shareEntryGroupService = shareEntryGroupService;
	}

	@Override
	public List<ShareEntryGroupDto> findAll(Version version, String actorUuid, boolean full) throws BusinessException {
		Validate.notEmpty(actorUuid, "actor uuid must be set.");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		List<ShareEntryGroup> list = shareEntryGroupService.findAll(authUser, actor);
		return	ImmutableList.copyOf(Lists.transform(list,
						ShareEntryGroupDto.toDto(version, full)));
	}

	@Override
	public ShareEntryGroupDto find(Version version, String actorUuid, String uuid, boolean full) throws BusinessException {
		Validate.notEmpty(actorUuid, "actor uuid must be set.");
		Validate.notEmpty(uuid, "Shar entry group's uuid must be set.");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		ShareEntryGroup seg = shareEntryGroupService.find(authUser, actor, uuid);
		return new ShareEntryGroupDto(version, seg, full);
	}

	@Override
	public ShareEntryGroupDto update(String actorUuid, ShareEntryGroupDto shareEntryGroupDto) throws BusinessException {
		Validate.notEmpty(actorUuid, "actor uuid must be set.");
		Validate.notNull(shareEntryGroupDto, "Share entry group must be set.");
		Validate.notEmpty(shareEntryGroupDto.getUuid(), "Uuid must be set.");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		ShareEntryGroup seg = shareEntryGroupDto.toObject();
		seg = shareEntryGroupService.update(authUser, actor, shareEntryGroupDto.getUuid(), seg);
		return new ShareEntryGroupDto(Version.V2, seg, false);
	}

	@Override
	public ShareEntryGroupDto delete(String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty(actorUuid, "actor uuid must be set.");
		Validate.notEmpty(uuid, "Shar entry group's uuid must be set.");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		ShareEntryGroup seg = shareEntryGroupService.delete(authUser, actor, uuid);
		return new ShareEntryGroupDto(Version.V2, seg, false);
	}
}
