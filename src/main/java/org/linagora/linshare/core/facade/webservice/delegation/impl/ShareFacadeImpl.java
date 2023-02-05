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

import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.delegation.ShareFacade;
import org.linagora.linshare.core.facade.webservice.delegation.dto.ShareCreationDto;
import org.linagora.linshare.core.facade.webservice.delegation.dto.ShareDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ShareService;
import org.linagora.linshare.core.service.UserService;

import com.google.common.collect.Sets;

public class ShareFacadeImpl extends DelegationGenericFacadeImpl implements
		ShareFacade {

	private final ShareService shareService;

	public ShareFacadeImpl(
			final AccountService accountService,
			final ShareService shareService,
			final UserService userService) {
		super(accountService, userService);
		this.shareService = shareService;
	}

	@Override
	public Set<ShareDto> create(String actorUuid, ShareCreationDto createDto) {
		Validate.notEmpty(actorUuid, "Missing required actor uuid");
		User authUser = checkAuthentication();
		User actor = getActor(actorUuid);
		if ((authUser.isGuest() && !authUser.isCanUpload()))
			throw new BusinessException(
					BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		ShareContainer sc = new ShareContainer();
		sc.addDocumentUuid(createDto.getDocuments());
		sc.setSubject(createDto.getSubject());
		sc.setMessage(createDto.getMessage());
		sc.setSecured(createDto.getSecured());
		sc.setExpiryDate(createDto.getExpirationDate());
		sc.addGenericUserDto(createDto.getRecipients());
		sc.setAcknowledgement(createDto.isCreationAcknowledgement());
		sc.setSharingNote(createDto.getSharingNote());
		Set<Entry> shares = shareService.create(authUser, actor, sc);
		Set<ShareDto> sharesDto = Sets.newHashSet();
		for (Entry entry : shares) {
			sharesDto.add(ShareDto.getSentShare(entry));
		}
		return sharesDto;
	}
}
