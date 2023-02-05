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
package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.ContactListContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MailingListFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.MailingListContactDto;
import org.linagora.linshare.core.facade.webservice.common.dto.MailingListDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ContactListService;

public class MailingListFacadeImpl extends AdminGenericFacadeImpl implements
		MailingListFacade {

	private ContactListService contactListService;

	public MailingListFacadeImpl(final AccountService accountService,
			final ContactListService contactListService) {
		super(accountService);
		this.contactListService = contactListService;
	}

	/*
	 * TODO: Handle mailing list update (ownership changes, ...) Problem :
	 * cascade on contacts
	 */

	@Override
	public Set<MailingListDto> findAll() throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		List<ContactList> lists = contactListService.findAllListByUser(
				authUser.getLsUuid(), authUser.getLsUuid());
		Set<MailingListDto> ret = new HashSet<MailingListDto>();
		for (ContactList list : lists) {
			ret.add(new MailingListDto(list));
		}
		return ret;
	}

	@Override
	public MailingListDto find(String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		ContactList list = contactListService.findByUuid(authUser.getLsUuid(),
				uuid);
		if (list == null) {
			throw new BusinessException(BusinessErrorCode.NO_SUCH_ELEMENT,
					"Cannot found mailling list : " + uuid);
		}
		return new MailingListDto(list);
	}

	@Override
	public MailingListDto create(MailingListDto dto) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		ContactList list = new ContactList(dto);
		return new MailingListDto(contactListService.createList(authUser.getLsUuid(), authUser.getLsUuid(), list));
	}

	@Override
	public MailingListDto update(MailingListDto dto) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notNull(dto.getUuid(), "uuid dto must be set.");
		Validate.notNull(dto.getOwner(), "Owner of contact list must be set");
		ContactList list = new ContactList(dto);
		return new MailingListDto(contactListService.updateList(authUser.getLsUuid(), list));
	}

	@Override
	public MailingListDto delete(String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		ContactList list = contactListService.deleteList(authUser.getLsUuid(), uuid);
		return new MailingListDto(list);
	}

	@Override
	public void addContact(String listUuid, MailingListContactDto dto)
			throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		ContactListContact contact = new ContactListContact(dto);
		contactListService.addNewContact(authUser.getLsUuid(), listUuid, contact);
	}

	@Override
	public void deleteContact(String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		contactListService.deleteContact(authUser.getLsUuid(), uuid);
	}
}
