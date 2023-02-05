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
package org.linagora.linshare.core.facade.webservice.user;

import java.util.Set;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ContactListContactDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ContactListDto;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;

public interface ContactListFacade {

	Set<ContactListDto> findAll(String actorUuid, Boolean mine) throws BusinessException;

	ContactListDto find(String actorUuid, String uuid) throws BusinessException;

	ContactListDto create(String actorUuid, ContactListDto dto) throws BusinessException;

	ContactListDto duplicate(String actorUuid, String contactsListUuidSource, String contactListName) throws BusinessException;

	ContactListDto update(String actorUuid, ContactListDto dto, String uuid) throws BusinessException;

	ContactListDto delete(String actorUuid, String uuid) throws BusinessException;

	Set<ContactListContactDto> findAllContacts(String actorUuid, String listUuid) throws BusinessException;

	ContactListContactDto addContact(String actorUuid, String listUuid, ContactListContactDto dto)
			throws BusinessException;

	ContactListContactDto updateContact(String actorUuid, ContactListContactDto dto, String contactUuid) throws BusinessException;

	void deleteContact(String actorUuid, String uuid) throws BusinessException;

	Set<AuditLogEntryUser> audit(String actorUuid, String uuid);

	Set<ContactListDto> findAllByMemberEmail(String actorUuid, Boolean mine, String mail) throws BusinessException;
}
