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
package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.ContactListContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.MailingListContactDto;
import org.linagora.linshare.core.facade.webservice.common.dto.MailingListDto;
import org.linagora.linshare.core.facade.webservice.user.MailingListFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ContactListService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class MailingListFacadeImpl extends GenericFacadeImpl implements MailingListFacade {

	private final ContactListService contactListService;

	public MailingListFacadeImpl(final AccountService accountService, final ContactListService contactListservice) {
		super(accountService);
		this.contactListService = contactListservice;
	}

	@Override
	public Set<MailingListDto> findAll(String actorUuid, Boolean mine) throws BusinessException {
		User authUser = checkAuthentication();
		List<ContactList> lists;
		User actor = getActor(authUser, actorUuid);
		lists = contactListService.findAllByUser(authUser, actor);
		return ImmutableSet.copyOf(Lists.transform(lists, MailingListDto.toDto()));
	}

	@Override
	public MailingListDto find(String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "List uuid must be set.");
		User authUser = checkAuthentication();
		ContactList list;
		User actor = getActor(authUser, actorUuid);
		list = contactListService.find(authUser, actor, uuid);
		return new MailingListDto(list);
	}

	@Override
	public MailingListDto create(String actorUuid, MailingListDto dto) throws BusinessException {
		Validate.notNull(dto, "Mailing list must be set.");
		User authUser = checkAuthentication();
		ContactList list = dto.toObject();
		User actor = getActor(authUser, actorUuid);
		list = contactListService.create(authUser, actor, list);
		return new MailingListDto(list);
	}

	@Override
	public MailingListDto update(String actorUuid, MailingListDto dto, String uuid) throws BusinessException {
		Validate.notNull(dto, "Mailing list must be set.");
		if (!Strings.isNullOrEmpty(uuid)) {
			dto.setUuid(uuid);
		}
		Validate.notEmpty(dto.getUuid(), "Mailing list uuid must be set.");
		User authUser = checkAuthentication();
		ContactList list = dto.toObject();
		User actor = getActor(authUser, actorUuid);
		list = contactListService.update(authUser, actor, list);
		return new MailingListDto(list);
	}

	@Override
	public MailingListDto delete(String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Mailing list uuid must be set.");
		User authUser = checkAuthentication();
		ContactList list;
		User actor = getActor(authUser, actorUuid);
		list = contactListService.delete(authUser, actor, uuid);
		return new MailingListDto(list);
	}

	@Override
	public Set<MailingListContactDto> findAllContacts(String actorUuid, String listUuid) throws BusinessException {
		Validate.notEmpty(listUuid, "Mailing list uuid must be set.");
		User authUser = checkAuthentication();
		List<ContactListContact> list;
		User actor = getActor(authUser, actorUuid);
		list = contactListService.findAllContacts(authUser, actor, listUuid);
		return ImmutableSet.copyOf(Lists.transform(list, MailingListContactDto.toDto()));
	}

	@Override
	public MailingListContactDto addContact(String actorUuid, String listUuid, MailingListContactDto dto) throws BusinessException {
		Validate.notNull(dto, "List contact to add must be set.");
		Validate.notEmpty(listUuid, "Mailing list uuid must be set.");
		User authUser = checkAuthentication();
		ContactListContact contact = dto.toObject();
		User actor = getActor(authUser, actorUuid);
		ContactListContact contact2 = contactListService.addContact(authUser, actor, listUuid, contact);
		return new MailingListContactDto(contact2);
	}

	@Override
	public MailingListContactDto updateContact(String actorUuid, MailingListContactDto dto) throws BusinessException {
		Validate.notNull(dto, "List uuid must be set.");
		Validate.notEmpty(dto.getUuid(), "List uuid must be set.");
		User authUser = checkAuthentication();
		ContactListContact contact = dto.toObject();
		User actor = getActor(authUser, actorUuid);
		ContactListContact updateContact = contactListService.updateContact(authUser, actor, contact);
		return new MailingListContactDto(updateContact);
	}

	@Override
	public void deleteContact(String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Contact uuid must be set.");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		contactListService.deleteContact(authUser, actor, uuid);
	}
}
