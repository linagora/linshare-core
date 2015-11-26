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

package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.List;

import org.apache.commons.lang.Validate;
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
		User actor = checkAuthentication();
		List<ShareEntryGroup> list = service.findAll(actor, actor);
		return ImmutableList.copyOf(Lists.transform(list, ShareEntryGroupDto.toDto(full)));
	}

	@Override
	public ShareEntryGroupDto find(String uuid, boolean full) {
		Validate.notEmpty(uuid, "Share entry group uuid must be set.");
		User actor = checkAuthentication();
		ShareEntryGroup seg = service.find(actor, actor, uuid);
		return new ShareEntryGroupDto(seg, full);
	}

	@Override
	public ShareEntryGroupDto update(ShareEntryGroupDto shareEntryGroupDto) {
		Validate.notNull(shareEntryGroupDto, "Share entry group must be set.");
		Validate.notNull(shareEntryGroupDto.getUuid(), "Share entry group uuid must be set.");
		User actor = checkAuthentication();
		ShareEntryGroup seg = shareEntryGroupDto.toObject();
		seg = service.update(actor, actor, shareEntryGroupDto.getUuid(), seg);
		return new ShareEntryGroupDto(seg, false);
	}

	@Override
	public ShareEntryGroupDto delete(String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Share entry group uuid must be set.");
		User actor = checkAuthentication();
		ShareEntryGroup seg = service.delete(actor, actor, uuid);
		return new ShareEntryGroupDto(seg, false);
	}
}
