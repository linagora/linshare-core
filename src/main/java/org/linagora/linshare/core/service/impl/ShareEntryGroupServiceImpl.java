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
package org.linagora.linshare.core.service.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
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
			final ShareEntryGroupResourceAccessControl rac,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(rac, sanitizerInputHtmlBusinessService);
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
		for (ShareEntry se : Set.copyOf(seg.getShareEntries())) {
			// AKO : Remove the entry from the list first to avoid hibernate
			// ObjectDeleted exception.
			seg.getShareEntries().remove(se);
			shareEntryService.delete(actor, actor, se.getUuid(), null);
		}
		for (AnonymousShareEntry ase : Set.copyOf(seg.getAnonymousShareEntries())) {
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
