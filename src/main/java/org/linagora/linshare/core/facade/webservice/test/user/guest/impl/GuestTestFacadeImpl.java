/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
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

package org.linagora.linshare.core.facade.webservice.test.user.guest.impl;

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.GuestDto;
import org.linagora.linshare.core.facade.webservice.test.user.guest.GuestTestFacade;
import org.linagora.linshare.core.facade.webservice.user.impl.GenericFacadeImpl;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.ResetGuestPasswordService;
import org.linagora.linshare.mongo.entities.ResetGuestPassword;
import org.linagora.linshare.mongo.repository.ResetGuestPasswordMongoRepository;
import org.linagora.linshare.core.facade.webservice.test.user.guest.dto.GuestPasswordDto;

import com.google.common.collect.ImmutableList;

public class GuestTestFacadeImpl extends GenericFacadeImpl implements GuestTestFacade {

	private final GuestService guestService;
	private final ResetGuestPasswordService resetGuestPasswordService;
	private final ResetGuestPasswordMongoRepository resetGuestPasswordMongoRepository;

	public GuestTestFacadeImpl(AccountService accountService,
	                           GuestService guestService,
	                           ResetGuestPasswordService resetGuestPasswordService,
	                           ResetGuestPasswordMongoRepository resetGuestPasswordMongoRepository) {
		super(accountService);
		this.guestService = guestService;
		this.resetGuestPasswordService = resetGuestPasswordService;
		this.resetGuestPasswordMongoRepository = resetGuestPasswordMongoRepository;
	}

	@Override
	public GuestDto create(GuestDto guestDto) {
		User authUser = checkAuthentication();
		ImmutableList<String> restrictedMails = ImmutableList.of();
		Guest guest = guestDto.toUserObject();
		return GuestDto.getSimple(guestService.create(authUser, authUser, guest, restrictedMails));
	}

	@Override
	public GuestDto setPassword(String uuid, GuestPasswordDto dto) {
		User authUser = checkAuthentication();
		Guest guest = guestService.find(authUser, authUser, uuid);
		List<ResetGuestPassword> tokensNotUsed = resetGuestPasswordMongoRepository.findByGuestNotUsed(uuid, new Date());
		ResetGuestPassword resetGuestPassword = tokensNotUsed.stream()
			.findFirst()
			.orElseThrow(() -> new BusinessException(BusinessErrorCode.RESET_GUEST_PASSWORD_NOT_FOUND, "The reset token was not found."));
		resetGuestPassword.setPassword(dto.getPassword());
		resetGuestPasswordService.update(authUser, authUser, resetGuestPassword);
		return GuestDto.getSimple(guest);
	}

	@Override
	public GuestDto delete(String uuid) {
		User authUser = checkAuthentication();
		Guest guest = guestService.delete(authUser, authUser, uuid);
		return GuestDto.getSimple(guest);
	}
}
