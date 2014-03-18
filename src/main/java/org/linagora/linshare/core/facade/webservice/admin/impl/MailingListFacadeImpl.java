/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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

package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.List;

import org.linagora.linshare.core.domain.entities.MailingList;
import org.linagora.linshare.core.domain.entities.MailingListContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MailingListFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.MailingListService;
import org.linagora.linshare.webservice.dto.MailingListContactDto;
import org.linagora.linshare.webservice.dto.MailingListDto;

import com.google.common.collect.Lists;

public class MailingListFacadeImpl extends AdminGenericFacadeImpl implements
		MailingListFacade {

	private MailingListService mailingListService;

	public MailingListFacadeImpl(final AccountService accountService,
			final MailingListService mailingListService) {
		super(accountService);
		this.mailingListService = mailingListService;
	}

	/*
	 * TODO:
	 * 		Handle mailing list update (ownership changes, ...)
	 *  	Problem : cascade on contacts
	 */

	@Override
	public List<MailingListDto> getAll() {
		User actor = super.getAuthentication();
		List<MailingList> lists = mailingListService.findAllListByUser(actor
				.getLsUuid());
		List<MailingListDto> ret = Lists.newArrayList();

		for (MailingList list : lists) {
			ret.add(new MailingListDto(list));
		}
		return ret;
	}

	@Override
	public MailingListDto get(String uuid) throws BusinessException {
		return new MailingListDto(mailingListService.findByUuid(uuid));
	}

	@Override
	public void create(MailingListDto dto) throws BusinessException {
		User actor = super.getAuthentication();
		MailingList list = new MailingList(dto);

		mailingListService.createList(actor.getLsUuid(), actor.getLsUuid(),
				list);
	}

	@Override
	public void delete(String uuid) throws BusinessException {
		User actor = super.getAuthentication();
		mailingListService.deleteList(actor.getLsUuid(), uuid);
	}

	@Override
	public void addContact(String listUuid, MailingListContactDto dto)
			throws BusinessException {
		User actor = super.getAuthentication();
		MailingListContact contact = new MailingListContact(dto);

		mailingListService.addNewContact(actor.getLsUuid(), listUuid, contact);
	}

	@Override
	public void deleteContact(String uuid) throws BusinessException {
		User actor = super.getAuthentication();

		mailingListService.deleteContact(actor.getLsUuid(), uuid);
	}
}
