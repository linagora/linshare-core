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
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.ContactListContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ContactListContactDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ContactListDto;
import org.linagora.linshare.core.facade.webservice.user.ContactListFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.ContactListService;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class ContactListFacadeImpl extends GenericFacadeImpl implements ContactListFacade {

	private final ContactListService contactListService;

	private final AuditLogEntryService auditLogEntryService;

	public ContactListFacadeImpl(final AccountService accountService,
			final ContactListService contactListservice,
			final AuditLogEntryService auditLogEntryService) {
		super(accountService);
		this.contactListService = contactListservice;
		this.auditLogEntryService = auditLogEntryService;
	}

	@Override
	public Set<ContactListDto> findAll(String actorUuid, Boolean mine) throws BusinessException {
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		List<ContactList> lists = contactListService.findAll(authUser, actor, mine);
		return ImmutableSet.copyOf(Lists.transform(lists, ContactListDto.toDto()));
	}

	@Override
	public Set<ContactListDto> findAllByMemberEmail(String actorUuid, Boolean mine, String email)
			throws BusinessException {
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		List<ContactList> lists = contactListService.findAllByMemberEmail(authUser, actor, mine, email);
		return ImmutableSet.copyOf(Lists.transform(lists, ContactListDto.toDto()));
	}

	@Override
	public Set<ContactListDto> findContactListByPattern(String actorUuid, String pattern) throws BusinessException {
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		List<ContactList> lists = contactListService.searchContactLists(authUser, pattern);
		return ImmutableSet.copyOf(
				Lists.transform(lists.stream()
								.filter(contact -> contact.getIdentifier().contains(pattern))
								.collect(Collectors.toList()),
						ContactListDto.toDto()
				)
		);
	}

	@Override
	public ContactListDto find(String actorUuid, String uuid) throws BusinessException {
		User authUser = checkAuthentication();
		Validate.notEmpty(uuid, "List uuid must be set.");
		ContactList list;
		User actor = getActor(authUser, actorUuid);
		list = contactListService.find(authUser, actor, uuid);
		return new ContactListDto(list);
	}

	@Override
	public ContactListDto create(String actorUuid, ContactListDto dto) throws BusinessException {
		User authUser = checkAuthentication();
		Validate.notNull(dto, "Mailing list must be set.");
		Validate.notEmpty(dto.getName(),"The contact list name must be set.");
		ContactList list = dto.toObject();
		User actor = getActor(authUser, actorUuid);
		if (list.getOwner() == null) {
			list.setOwner(actor);
		}
		list = contactListService.create(authUser, actor, list);
		return new ContactListDto(list);
	}

	@Override
	public ContactListDto duplicate(String actorUuid, String contactsListUuidSource, String contactListName)
			throws BusinessException {
		Validate.notNull(contactsListUuidSource, "Mailing list uuid must be set.");
		Validate.notNull(contactListName, "Mailing list name must be set.");

		User authUser = checkAuthentication();
		ContactList list = contactListService.findByUuid(authUser.getLsUuid(), contactsListUuidSource);
		User actor = getActor(authUser, actorUuid);
		list = contactListService.duplicate(authUser, actor, list, contactListName);
		return new ContactListDto(list);
	}

	@Override
	public ContactListDto update(String actorUuid, ContactListDto dto, String uuid) throws BusinessException {
		Validate.notNull(dto, "Mailing list must be set.");
		if (!Strings.isNullOrEmpty(uuid)) {
			dto.setUuid(uuid);
		}
		Validate.notEmpty(dto.getUuid(), "Mailing list uuid must be set.");
		User authUser = checkAuthentication();
		ContactList list = dto.toObject();
		User actor = getActor(authUser, actorUuid);
		list = contactListService.update(authUser, actor, list);
		return new ContactListDto(list);
	}

	@Override
	public ContactListDto delete(String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Mailing list uuid must be set.");
		User authUser = checkAuthentication();
		ContactList list;
		User actor = getActor(authUser, actorUuid);
		list = contactListService.delete(authUser, actor, uuid);
		return new ContactListDto(list);
	}

	@Override
	public Set<ContactListContactDto> findAllContacts(String actorUuid, String listUuid) throws BusinessException {
		Validate.notEmpty(listUuid, "Mailing list uuid must be set.");
		User authUser = checkAuthentication();
		List<ContactListContact> list;
		User actor = getActor(authUser, actorUuid);
		list = contactListService.findAllContacts(authUser, actor, listUuid);
		return ImmutableSet.copyOf(Lists.transform(list, ContactListContactDto.toDto()));
	}

	@Override
	public ContactListContactDto addContact(String actorUuid, String listUuid, ContactListContactDto dto) throws BusinessException {
		Validate.notNull(dto, "List contact to add must be set.");
		Validate.notEmpty(listUuid, "Mailing list uuid must be set.");
		User authUser = checkAuthentication();
		ContactListContact contact = dto.toObject();
		User actor = getActor(authUser, actorUuid);
		ContactListContact contact2 = contactListService.addContact(authUser, actor, listUuid, contact);
		return new ContactListContactDto(contact2);
	}

	@Override
	public ContactListContactDto updateContact(String actorUuid, ContactListContactDto dto, String contactUuid) throws BusinessException {
		Validate.notNull(dto, "List uuid must be set.");
		if (contactUuid != null) {
			dto.setUuid(contactUuid);
		}
		Validate.notEmpty(dto.getUuid(), "List uuid must be set.");
		User authUser = checkAuthentication();
		ContactListContact contact = dto.toObject();
		User actor = getActor(authUser, actorUuid);
		ContactListContact updateContact = contactListService.updateContact(authUser, actor, contact);
		return new ContactListContactDto(updateContact);
	}

	@Override
	public void deleteContact(String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Contact uuid must be set.");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		contactListService.deleteContact(authUser, actor, uuid);
	}

	@Override
	public Set<AuditLogEntryUser> audit(String actorUuid, String uuid) {
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		contactListService.find(authUser, actor, uuid);
		return auditLogEntryService.findAllContactLists(authUser, actor, uuid);
	}
}
