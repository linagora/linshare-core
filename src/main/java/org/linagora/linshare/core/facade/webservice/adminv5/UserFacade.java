/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2022. Contribute to
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
package org.linagora.linshare.core.facade.webservice.adminv5;

import java.util.List;

import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.domain.entities.fields.UserFields;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.AdminGenericFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.RestrictedContactDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.UserDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.UserDtoQuotaDto;
import org.linagora.linshare.core.facade.webservice.common.dto.GuestDto;
import org.linagora.linshare.core.facade.webservice.common.dto.PasswordDto;
import org.linagora.linshare.core.facade.webservice.user.dto.SecondFactorDto;
import org.linagora.linshare.webservice.utils.PageContainer;

public interface UserFacade extends AdminGenericFacade {

	PageContainer<UserDto> findAll(String actorUuid, String domainUuid, SortOrder sortOrder, UserFields sortField,
			String mail, String firstName, String lastName, Boolean restricted, Boolean canCreateGuest,
			Boolean canUpload, String role, String type, Integer pageNumber, Integer pageSize);

	UserDto find(String actorUuid, String uuid);

	UserDto update(String actorUuid, UserDto userDto, String uuid) throws BusinessException;

	UserDto delete(String actorUuid, UserDto userDto, String uuid) throws BusinessException;

	List<RestrictedContactDto> findAllRestrictedContacts(String actorUuid, String userUuid, String mail,
			String firstName, String lastName);

	RestrictedContactDto findRestrictedContact(String actorUuid, String ownerUuid, String restrictedContactUuid);

	RestrictedContactDto createRestrictedContact(String actorUuid, String ownerUuid, RestrictedContactDto restrictedContactDto);

	RestrictedContactDto deleteRestrictedContact(String actorUuid, String ownerUuid,
			RestrictedContactDto restrictedContactDto, String restrictedContactUuid);

	UserDto isAuthorized();

	void changePassword(PasswordDto password);

	UserDtoQuotaDto findUserQuota(String actorUuid, String accountUuid, String quotaUuid);

	SecondFactorDto find2FA(String userUuid, String secondFactorUuid) throws BusinessException;

	SecondFactorDto delete2FA(String userUuid, String secondFactorUuid, SecondFactorDto dto) throws BusinessException;

	UserDtoQuotaDto updateUserQuota(String actorUuid, String userUuid, String quotaUuid, UserDtoQuotaDto dto);

	List<GuestDto> findAllUserGuests(String actorUuid, String uuid, ModeratorRole role, String pattern);

}
