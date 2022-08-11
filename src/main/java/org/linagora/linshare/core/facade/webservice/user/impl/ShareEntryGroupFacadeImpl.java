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
		return ImmutableList.copyOf(Lists.transform(list, ShareEntryGroupDto.toDto(2, full)));
	}

	@Override
	public ShareEntryGroupDto find(String uuid, boolean full) {
		Validate.notEmpty(uuid, "Share entry group uuid must be set.");
		User authUser = checkAuthentication();
		ShareEntryGroup seg = service.find(authUser, authUser, uuid);
		return new ShareEntryGroupDto(2, seg, full);
	}

	@Override
	public ShareEntryGroupDto update(ShareEntryGroupDto shareEntryGroupDto) {
		Validate.notNull(shareEntryGroupDto, "Share entry group must be set.");
		Validate.notNull(shareEntryGroupDto.getUuid(), "Share entry group uuid must be set.");
		User authUser = checkAuthentication();
		ShareEntryGroup seg = shareEntryGroupDto.toObject();
		seg = service.update(authUser, authUser, shareEntryGroupDto.getUuid(), seg);
		return new ShareEntryGroupDto(2, seg, false);
	}

	@Override
	public ShareEntryGroupDto delete(String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Share entry group uuid must be set.");
		User authUser = checkAuthentication();
		ShareEntryGroup seg = service.delete(authUser, authUser, uuid);
		return new ShareEntryGroupDto(2, seg, false);
	}
}
