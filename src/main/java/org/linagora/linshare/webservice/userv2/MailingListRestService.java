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
package org.linagora.linshare.webservice.userv2;

import java.util.Set;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.MailingListContactDto;
import org.linagora.linshare.core.facade.webservice.common.dto.MailingListDto;

public interface MailingListRestService {

	Set<MailingListDto> findAll() throws BusinessException;

	MailingListDto find(String uuid) throws BusinessException;

	void head(String uuid) throws BusinessException;

	MailingListDto create(MailingListDto dto) throws BusinessException;

	MailingListDto update(MailingListDto dto) throws BusinessException;

	MailingListDto delete(MailingListDto dto) throws BusinessException;

	MailingListDto delete(String uuid) throws BusinessException;

	Set<MailingListContactDto> findAllContacts(String listUuid) throws BusinessException;

	MailingListContactDto createContact(String uuid, MailingListContactDto dto) throws BusinessException;

	void updateContact(String uuid, MailingListContactDto dto) throws BusinessException;

	void deleteContact(String uuid, MailingListContactDto dto) throws BusinessException;

	void deleteContact(String uuid, String contactUuid) throws BusinessException;
}
