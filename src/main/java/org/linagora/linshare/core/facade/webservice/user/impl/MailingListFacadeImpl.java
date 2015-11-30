/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.MailingList;
import org.linagora.linshare.core.domain.entities.MailingListContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.MailingListContactDto;
import org.linagora.linshare.core.facade.webservice.common.dto.MailingListDto;
import org.linagora.linshare.core.facade.webservice.user.MailingListFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.MailingListService;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class MailingListFacadeImpl extends GenericFacadeImpl implements MailingListFacade {

	private final MailingListService mailingListService;

	public MailingListFacadeImpl(final AccountService accountService, final MailingListService mailingListservice) {
		super(accountService);
		this.mailingListService = mailingListservice;
	}

	@Override
	public Set<MailingListDto> findAll(String ownerUuid) throws BusinessException {
		User actor = checkAuthentication();
		List<MailingList> lists;
		User owner = getOwner(actor, ownerUuid);
		lists = mailingListService.findAllByUser(actor, owner);
		return ImmutableSet.copyOf(Lists.transform(lists, MailingListDto.toDto()));
	}

	@Override
	public MailingListDto find(String ownerUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "List uuid must be set.");

		User actor = checkAuthentication();
		MailingList list;
		User owner = getOwner(actor, ownerUuid);
		list = mailingListService.find(actor, owner, uuid);
		return new MailingListDto(list);
	}

	@Override
	public MailingListDto create(String ownerUuid, MailingListDto dto) throws BusinessException {
		Validate.notNull(dto, "Mailing list must be set.");

		User actor = checkAuthentication();
		MailingList list = dto.toObject();
		User owner = getOwner(actor, ownerUuid);
		list = mailingListService.create(actor, owner, list);
		return new MailingListDto(list);
	}

	@Override
	public MailingListDto update(String ownerUuid, MailingListDto dto) throws BusinessException {
		Validate.notNull(dto, "Mailing list must be set.");
		Validate.notEmpty(dto.getUuid(), "Mailing list uuid must be set.");

		User actor = checkAuthentication();
		MailingList list = dto.toObject();
		User owner = getOwner(actor, ownerUuid);
		list = mailingListService.update(actor, owner, list);
		return new MailingListDto(list);
	}

	@Override
	public MailingListDto delete(String ownerUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Mailing list uuid must be set.");

		User actor = checkAuthentication();
		MailingList list;
		User owner = getOwner(actor, ownerUuid);
		list = mailingListService.delete(actor, owner, uuid);
		return new MailingListDto(list);
	}

	@Override
	public Set<MailingListContactDto> findAllContacts(String ownerUuid, String listUuid) throws BusinessException {
		Validate.notEmpty(listUuid, "Mailing list uuid must be set.");

		User actor = checkAuthentication();
		List<MailingListContact> list;
		User owner = getOwner(actor, ownerUuid);
		list = mailingListService.findAllContacts(actor, owner, listUuid);
		return ImmutableSet.copyOf(Lists.transform(list, MailingListContactDto.toDto()));
	}

	@Override
	public void addContact(String ownerUuid, String listUuid, MailingListContactDto dto) throws BusinessException {
		Validate.notNull(dto, "List contact to add must be set.");
		Validate.notEmpty(listUuid, "Mailing list uuid must be set.");

		User actor = checkAuthentication();
		MailingListContact contact = dto.toObject();
		User owner = getOwner(actor, ownerUuid);
		mailingListService.addContact(actor, owner, listUuid, contact);
	}

	@Override
	public void updateContact(String ownerUuid, MailingListContactDto dto) throws BusinessException {
		Validate.notNull(dto, "List uuid must be set.");
		Validate.notEmpty(dto.getUuid(), "List uuid must be set.");

		User actor = checkAuthentication();
		MailingListContact contact = dto.toObject();
		User owner = getOwner(actor, ownerUuid);
		mailingListService.updateContact(actor, owner, contact);
	}

	@Override
	public void deleteContact(String ownerUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Contact uuid must be set.");

		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		mailingListService.deleteContact(actor, owner, uuid);
	}
}
