/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2021 LINAGORA
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
package org.linagora.linshare.webservice.adminv5;

import java.util.List;

import javax.ws.rs.core.Response;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.RestrictedContactDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.UserDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.UserDtoQuotaDto;
import org.linagora.linshare.core.facade.webservice.user.dto.SecondFactorDto;

public interface UserRestService {

	Response findAll(String domainUuid, String sortOrder, String sortField, String mail, String firstName,
			String lastName, Boolean restricted, Boolean canCreateGuest, Boolean canUpload, String role, String type,
			Integer pageNumber, Integer pageSize) throws BusinessException;

	UserDto find(String uuid) throws BusinessException;

	UserDto update(UserDto userDto, String uuid) throws BusinessException;

	UserDto delete(UserDto userDto, String uuid) throws BusinessException;

	List<RestrictedContactDto> findAllRestrictedContacts(String userUuid, String mail, String firstName,
			String lastName) throws BusinessException;

	RestrictedContactDto findRestrictedContact(String ownerUuid, String userUuid) throws BusinessException;

	RestrictedContactDto createRestrictedContact(String ownerUuid, RestrictedContactDto restrictedContactDto) throws BusinessException;

	RestrictedContactDto deleteRestrictedContact(String ownerUuid, RestrictedContactDto restrictedContactDto,
			String restrictedContactUuid) throws BusinessException;

	UserDtoQuotaDto findUserQuota(String accountUuid, String quotaUuid) throws BusinessException;

	SecondFactorDto find2FA(String uuid, String secondfaUuid) throws BusinessException;

	SecondFactorDto delete2FA(String uuid, String secondfaUuid, SecondFactorDto dto) throws BusinessException;

	UserDtoQuotaDto updateUserQuota(String userUuid, String quotaUuid, UserDtoQuotaDto dto) throws BusinessException;
}
