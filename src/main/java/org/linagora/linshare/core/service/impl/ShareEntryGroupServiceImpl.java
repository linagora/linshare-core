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

package org.linagora.linshare.core.service.impl;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.ShareEntryGroupBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.ShareEntryGroupResourceAccessControl;
import org.linagora.linshare.core.service.AnonymousShareEntryService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.ShareEntryGroupService;
import org.linagora.linshare.core.service.ShareEntryService;

public class ShareEntryGroupServiceImpl extends GenericServiceImpl<Account, ShareEntryGroup>
		implements ShareEntryGroupService {

	private final ShareEntryGroupBusinessService businessService;
	private final ShareEntryService shareEntryService;
	private final AnonymousShareEntryService anonymousShareEntryService;

	@SuppressWarnings("unused")
	private LogEntryService logEntryService;

	public ShareEntryGroupServiceImpl(
			final ShareEntryGroupBusinessService shareEntryGroupBusinessService,
			final ShareEntryService shareEntryService,
			final LogEntryService logEntryService,
			final AnonymousShareEntryService anonymousShareEntryService,
			final ShareEntryGroupResourceAccessControl rac) {
		super(rac);
		this.businessService = shareEntryGroupBusinessService;
		this.logEntryService = logEntryService;
		this.anonymousShareEntryService = anonymousShareEntryService;
		this.shareEntryService = shareEntryService;
	}

	@Override
	public ShareEntryGroup create(Account actor, ShareEntryGroup entity) {
		Validate.notNull(actor, "Actor must be set.");
		Validate.notNull(entity, "Entity must be set.");

		return businessService.create(entity);
	}

	@Override
	public ShareEntryGroup delete(Account actor, Account owner, String uuid) {
		preChecks(actor, owner);
		Validate.notEmpty(uuid, "Uuid must be set.");

		ShareEntryGroup seg = find(actor, owner, uuid);

		if (seg == null) {
			throw new BusinessException(BusinessErrorCode.SHARE_ENTRY_GROUP_NOT_FOUND,
					"Share entry group with uuid :" + uuid + " was not found.");
		}
		checkDeletePermission(actor, seg.getOwner(), ShareEntryGroup.class,
				BusinessErrorCode.SHARE_ENTRY_GROUP_FORBIDDEN, seg);
		for (ShareEntry se : seg.getShareEntries()) {
			// AKO : Remove the entry from the list first to avoid hibernate
			// ObjectDeleted exception.
			seg.getShareEntries().remove(se);
			shareEntryService.delete(actor, actor, se.getUuid(), null);
		}
		for (AnonymousShareEntry ase : seg.getAnonymousShareEntries()) {
//			AKO : Remove the entry from the list first to avoid hibernate ObjectDeleted exception.
			seg.getAnonymousShareEntries().remove(ase);
			anonymousShareEntryService.delete(actor, actor, ase.getUuid());
		}
		businessService.delete(seg);

		return seg;
	}

	@Override
	public ShareEntryGroup delete(Account actor, Account owner, ShareEntryGroup shareEntryGroup) {
		return delete(actor, owner, shareEntryGroup.getUuid());
	}

	@Override
	public ShareEntryGroup find(Account actor, Account owner, String uuid) {
		preChecks(actor, owner);
		Validate.notEmpty(uuid, "Uuid must be set.");

		ShareEntryGroup seg = businessService.findByUuid(uuid);
		if (seg == null) {
			throw new BusinessException(BusinessErrorCode.SHARE_ENTRY_GROUP_NOT_FOUND,
					"Share entry group with uuid :" + uuid + " was not found.");
		}
		checkReadPermission(actor, seg.getOwner(), ShareEntryGroup.class, BusinessErrorCode.SHARE_ENTRY_GROUP_FORBIDDEN,
				seg);
		return seg;
	}

	/**
	 * This method is used to communicate with the DTO facade
	 */
	@Override
	public ShareEntryGroup update(Account actor, Account owner, String uuid, ShareEntryGroup shareEntryGroupObject) {
		preChecks(actor, owner);
		Validate.notEmpty(uuid, "Share entry group uuid must be set.");
		Validate.notNull(shareEntryGroupObject, "Share entry object must be set.");

		ShareEntryGroup seg = find(actor, owner, shareEntryGroupObject.getUuid());
		checkUpdatePermission(actor, seg.getOwner(), ShareEntryGroup.class,
				BusinessErrorCode.SHARE_ENTRY_GROUP_FORBIDDEN, seg);
		return businessService.update(seg, shareEntryGroupObject);
	}

	/**
	 * This method is only used by the batch.
	 */
	@Override
	public ShareEntryGroup update(Account actor, Account owner, ShareEntryGroup shareEntryGroup) {
		preChecks(actor, owner);
		Validate.notEmpty(shareEntryGroup.getUuid(), "Share entry group uuid must be set.");

		checkUpdatePermission(actor, shareEntryGroup.getOwner(), ShareEntryGroup.class,
				BusinessErrorCode.SHARE_ENTRY_GROUP_FORBIDDEN, shareEntryGroup);
		return businessService.update(shareEntryGroup);
	}

	@Override
	public List<String> findAllAboutToBeNotified(Account actor, Account owner) {
		preChecks(actor, owner);

		checkListPermission(actor, owner, ShareEntryGroup.class, BusinessErrorCode.SHARE_ENTRY_GROUP_FORBIDDEN, null);
		return businessService
				.findAllAboutToBeNotified();
	}

	@Override
	public List<String> findAllToPurge(Account actor, Account owner) {
		preChecks(actor, owner);

		checkListPermission(actor, owner, ShareEntryGroup.class, BusinessErrorCode.SHARE_ENTRY_GROUP_FORBIDDEN, null);
		return businessService.findAllToPurge();
	}

	@Override
	public List<ShareEntryGroup> findAll(Account actor, Account owner) {
		preChecks(actor, owner);

		checkListPermission(actor, owner, ShareEntryGroup.class, BusinessErrorCode.SHARE_ENTRY_GROUP_FORBIDDEN, null);
		return businessService.findAll(owner);
	}
}
