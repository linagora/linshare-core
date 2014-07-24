/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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

package org.linagora.linshare.core.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.UploadPropositionBusinessService;
import org.linagora.linshare.core.domain.constants.UploadPropositionActionType;
import org.linagora.linshare.core.domain.constants.UploadPropositionStatus;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.UploadProposition;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.UploadPropositionService;
import org.linagora.linshare.core.service.UploadRequestService;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadPropositionServiceImpl implements UploadPropositionService {

	private static final Logger logger = LoggerFactory
			.getLogger(UploadPropositionServiceImpl.class);


	final private UploadPropositionBusinessService uploadPropositionBusinessService;

	final private DomainBusinessService domainBusinessService;

	final private UploadRequestService uploadRequestService;

	final private AccountRepository<Account> accountRepository;

	final private UserService userService;

	public UploadPropositionServiceImpl(
			final UploadPropositionBusinessService uploadPropositionBusinessService,
			final DomainBusinessService domainBusinessService,
			final UploadRequestService uploadRequestService,
			final AccountRepository<Account> accountRepository,
			final UserService userService) {
		super();
		this.uploadPropositionBusinessService = uploadPropositionBusinessService;
		this.domainBusinessService = domainBusinessService;
		this.uploadRequestService = uploadRequestService;
		this.accountRepository = accountRepository;
		this.userService = userService;
	}

	@Override
	public UploadProposition create(UploadProposition proposition,
			UploadPropositionActionType action) throws BusinessException {
		Validate.notNull(proposition, "UploadProposition must be set.");

		AbstractDomain rootDomain = domainBusinessService.getUniqueRootDomain();
		proposition.setDomain(rootDomain);

		UploadProposition created;
		if (action.equals(UploadPropositionActionType.ACCEPT)) {
			proposition.setStatus(UploadPropositionStatus.SYSTEM_ACCEPTED);
			created = uploadPropositionBusinessService.create(proposition);
			User owner = null;
			try {
				if (proposition.getDomaineSource() != null) {
					owner = userService.findOrCreateUser(proposition.getRecipientMail(), proposition.getDomaineSource());
				} else {
					owner = userService.findOrCreateUser(proposition.getRecipientMail(), rootDomain.getIdentifier());
				}
			} catch (BusinessException e) {
				logger.error("The recipient of the upload proposition can't be found : " + created.getUuid() + ": " + proposition.getRecipientMail());
				return null;
			}

			// TODO functionalityFacade
			UploadRequestGroup grp = new UploadRequestGroup(created);
//			Account actor = accountRepository.getUploadRequestSystemAccount();
			grp = uploadRequestService.createRequestGroup(owner, grp);


			UploadRequest e = new UploadRequest();
			e.setOwner(owner);
			e.setAbstractDomain(owner.getDomain());
			e.setNotificationDate(e.getExpiryDate()); // FIXME functionalityFacade
			e.setUploadRequestGroup(grp);

			e.setUploadPropositionRequestUuid(created.getUuid());
			e.setMaxFileCount(3);
			e.setMaxDepositSize(new Long(30*1024*1024));
			e.setMaxFileSize(new Long(3*1024*1024));
			e.setActivationDate(new Date());
			e.setNotificationDate(new Date());

			Calendar a = Calendar.getInstance();
			a.add(Calendar.MONTH, 3);
			e.setExpiryDate(a.getTime());

			e.setCanDelete(true);
			e.setCanClose(true);
			e.setCanEditExpiryDate(true);
			e.setLocale("fr");
			e.setSecured(true);

			Contact contact = new Contact(proposition.getRecipientMail());
			e = uploadRequestService.createRequest(owner, e, contact);

			e.updateStatus(UploadRequestStatus.STATUS_ENABLED); // TODO
			uploadRequestService.updateRequest(owner, e);
		} else {
			created = uploadPropositionBusinessService.create(proposition);
		}
		return created;
	}

	@Override
	public void delete(Account actor, UploadProposition proposition)
			throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		uploadPropositionBusinessService.delete(proposition);
	}

	@Override
	public UploadProposition find(Account actor, String uuid)
			throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		return uploadPropositionBusinessService.findByUuid(uuid);
	}

	@Override
	public List<UploadProposition> findAll(Account actor)
			throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		return uploadPropositionBusinessService.findAll(null);
	}

	@Override
	public UploadProposition update(Account actor,
			UploadProposition propositionDto) throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		Validate.notNull(propositionDto, "UploadProposition must be set.");
		Validate.notEmpty(propositionDto.getUuid(),
				"UploadProposition identifier must be set.");
		return uploadPropositionBusinessService.update(propositionDto);
	}
}
