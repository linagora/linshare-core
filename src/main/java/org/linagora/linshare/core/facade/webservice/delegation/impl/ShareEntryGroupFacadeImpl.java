/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
	public List<ShareEntryGroupDto> findAll(String actorUuid, boolean full) throws BusinessException {
		Validate.notEmpty(actorUuid, "actor uuid must be set.");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		List<ShareEntryGroup> list = shareEntryGroupService.findAll(authUser, actor);
		return	ImmutableList.copyOf(Lists.transform(list,
						ShareEntryGroupDto.toDto(full)));
	}

	@Override
	public ShareEntryGroupDto find(String actorUuid, String uuid, boolean full) throws BusinessException {
		Validate.notEmpty(actorUuid, "actor uuid must be set.");
		Validate.notEmpty(uuid, "Shar entry group's uuid must be set.");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		ShareEntryGroup seg = shareEntryGroupService.find(authUser, actor, uuid);
		return new ShareEntryGroupDto(seg, full);
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
		return new ShareEntryGroupDto(seg, false);
	}

	@Override
	public ShareEntryGroupDto delete(String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty(actorUuid, "actor uuid must be set.");
		Validate.notEmpty(uuid, "Shar entry group's uuid must be set.");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		ShareEntryGroup seg = shareEntryGroupService.delete(authUser, actor, uuid);
		return new ShareEntryGroupDto(seg, false);
	}
}
