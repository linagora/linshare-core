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
import org.linagora.linshare.core.facade.webservice.common.dto.ContactListContactDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ContactListDto;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;

public interface ContactListRestService {

	Set<ContactListDto> findAll(Boolean mine, String contactMail) throws BusinessException;

	ContactListDto find(String uuid) throws BusinessException;

	void head(String uuid) throws BusinessException;

	ContactListDto create(ContactListDto dto) throws BusinessException;

	ContactListDto duplicate(String contactsListUuidSource, String contactListName) throws BusinessException;

	ContactListDto update(ContactListDto dto, String uuid) throws BusinessException;

	ContactListDto delete(ContactListDto dto) throws BusinessException;

	ContactListDto delete(String uuid) throws BusinessException;

	Set<ContactListContactDto> findAllContacts(String listUuid) throws BusinessException;

	ContactListContactDto createContact(String uuid, ContactListContactDto dto) throws BusinessException;

	ContactListContactDto updateContact(String uuid, String contactUuuid, ContactListContactDto dto) throws BusinessException;

	void deleteContact(String uuid, ContactListContactDto dto) throws BusinessException;

	void deleteContact(String uuid, String contactUuid) throws BusinessException;

	Set<AuditLogEntryUser> audit(String uuid);

}
