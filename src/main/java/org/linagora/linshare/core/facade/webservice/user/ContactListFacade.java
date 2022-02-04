/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2022 LINAGORA
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
